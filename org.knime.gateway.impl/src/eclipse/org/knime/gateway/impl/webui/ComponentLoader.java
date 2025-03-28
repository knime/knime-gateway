/*
 * ------------------------------------------------------------------------
 *
 *  Copyright by KNIME AG, Zurich, Switzerland
 *  Website: http://www.knime.org; Email: contact@knime.org
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License, Version 3, as
 *  published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but
 *  WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, see <http://www.gnu.org/licenses>.
 *
 *  Additional permission under GNU GPL version 3 section 7:
 *
 *  KNIME interoperates with ECLIPSE solely via ECLIPSE's plug-in APIs.
 *  Hence, KNIME and ECLIPSE are both independent programs and are not
 *  derived from each other. Should, however, the interpretation of the
 *  GNU GPL Version 3 ("License") under any applicable laws result in
 *  KNIME and ECLIPSE being a combined program, KNIME AG herewith grants
 *  you the additional permission to use and propagate KNIME together with
 *  ECLIPSE with only the license terms in place for ECLIPSE applying to
 *  ECLIPSE and the GNU GPL Version 3 applying for KNIME, provided the
 *  license terms of ECLIPSE themselves allow for the respective use and
 *  propagation of ECLIPSE together with KNIME.
 *
 *  Additional permission relating to nodes for KNIME that extend the Node
 *  Extension (and in particular that are based on subclasses of NodeModel,
 *  NodeDialog, and NodeView) and that only interoperate with KNIME through
 *  standard APIs ("Nodes"):
 *  Nodes are deemed to be separate and independent programs and to not be
 *  covered works.  Notwithstanding anything to the contrary in the
 *  License, the License does not apply to Nodes, you are not required to
 *  license Nodes under the License, and you are granted a license to
 *  prepare and propagate Nodes, in each case even if such Nodes are
 *  propagated with or for interoperation with KNIME.  The owner of a Node
 *  may freely choose the license terms applicable to such Node, including
 *  when such Node is propagated with or for interoperation with KNIME.
 * ---------------------------------------------------------------------
 *
 * History
 *   Mar 13, 2025 (hornm): created
 */
package org.knime.gateway.impl.webui;

import static org.knime.gateway.api.entity.EntityBuilderManager.builder;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

import org.apache.commons.lang3.function.FailableFunction;
import org.knime.core.node.CanceledExecutionException;
import org.knime.core.node.ExecutionMonitor;
import org.knime.core.node.workflow.NodeID;
import org.knime.core.node.workflow.NodeProgressListener;
import org.knime.gateway.api.entity.NodeIDEnt;
import org.knime.gateway.api.webui.entity.ComponentPlaceholderEnt;
import org.knime.gateway.api.webui.entity.ComponentPlaceholderEnt.ComponentPlaceholderEntBuilder;
import org.knime.gateway.api.webui.entity.ComponentPlaceholderEnt.StateEnum;
import org.knime.gateway.impl.service.util.WorkflowChangesListener;
import org.knime.gateway.impl.service.util.WorkflowChangesTracker.WorkflowChange;
import org.knime.gateway.impl.webui.service.commands.util.Geometry;

/**
 * Manages component-loading jobs for a single workflow.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
public final class ComponentLoader {

    private final Map<String, Loader> m_loaders = Collections.synchronizedMap(new LinkedHashMap<>());

    private final WorkflowChangesListener m_workflowChangesListener;

    ComponentLoader(final WorkflowChangesListener workflowChangesListener) {
        m_workflowChangesListener = workflowChangesListener;
    }

    /**
     * Creates a new component loading job.
     *
     * @param position coordinate to show the placeholder and the successfully loaded component
     * @param loadComponent the actual component loading logic
     *
     * @return the job/placeholder id
     */
    public LoadJob createComponentLoadJob(final Geometry.Point position,
        final FailableFunction<ExecutionMonitor, LoadResult, CanceledExecutionException> loadComponent) {
        var placeholderId = UUID.randomUUID().toString();
        var exec = new ExecutionMonitor();
        NodeProgressListener progressListener = progressEvent -> updatePlaceholderWithMessageAndProgress(placeholderId,
            progressEvent.getNodeProgress().getMessage(), progressEvent.getNodeProgress().getProgress());
        exec.getProgressMonitor().addProgressListener(progressListener);
        Supplier<LoadResult> componentLoader = () -> {
            try {
                return loadComponent.apply(exec);
            } catch (CanceledExecutionException ex) { // NOSONAR
                throw new CancellationException(ex.getMessage());
            }
        };
        var future = CompletableFuture //
            .supplyAsync(componentLoader) //
            .handle((result, ex) -> {
                exec.getProgressMonitor().removeAllProgressListener();
                if (ex == null) {
                    updatePlaceholderWithSuccess(placeholderId, new NodeIDEnt(result.componentId()).toString(),
                        result.message(), result.details());
                } else {
                    updatePlaceholderWithError(placeholderId, "Component could not be loaded", ex.getMessage());
                }
                return result;
            });
        final var progress = exec.getProgressMonitor().getProgress();
        var placeholder = builder(ComponentPlaceholderEntBuilder.class) //
            .setId(placeholderId) //
            .setState(StateEnum.LOADING) //
            .setPosition(position.toEnt()) //
            .setComponentId(null) //
            .setMessage(exec.getProgressMonitor().getMessage()) //
            .setProgress(progress == null ? null : BigDecimal.valueOf(progress)) //
            .build();

        var loader = new Loader(new LoadJob(placeholderId, future), placeholder, exec);
        m_loaders.put(placeholderId, loader);
        m_workflowChangesListener.trigger(WorkflowChange.COMPONENT_PLACEHOLDER_ADDED);
        return loader.loadJob();
    }

    private void updatePlaceholderWithMessageAndProgress(final String id, final String message, final Double progress) {
        replacePlaceholderEnt(id,
            previousEnt -> builder(ComponentPlaceholderEntBuilder.class).setId(previousEnt.getId()) //
                .setState(previousEnt.getState()) //
                .setPosition(previousEnt.getPosition()) //
                .setComponentId(previousEnt.getComponentId()) //
                .setMessage(message) //
                .setProgress(progress == null ? null : BigDecimal.valueOf(progress)) //
                .build() //
        );
    }

    private void updatePlaceholderWithSuccess(final String id, final String replacementId, final String warningMessage,
        final String details) {
        replacePlaceholderEnt(id,
            previousEnt -> builder(ComponentPlaceholderEntBuilder.class).setId(previousEnt.getId()) //
                .setState(warningMessage == null ? StateEnum.SUCCESS : StateEnum.SUCCESS_WITH_WARNING) //
                .setPosition(previousEnt.getPosition()) //
                .setComponentId(replacementId) //
                .setMessage(warningMessage) //
                .setDetails(details) //
                .build() //
        );
    }

    private void updatePlaceholderWithError(final String id, final String message, final String details) {
        replacePlaceholderEnt(id,
            previousEnt -> builder(ComponentPlaceholderEntBuilder.class).setId(previousEnt.getId()) //
                .setState(StateEnum.ERROR) //
                .setPosition(previousEnt.getPosition()) //
                .setComponentId(null) //
                .setMessage(message) //
                .setDetails(details) //
                .build() //
        );
    }

    private void replacePlaceholderEnt(final String id, final UnaryOperator<ComponentPlaceholderEnt> replacer) {
        var loader = m_loaders.get(id);
        if (loader == null) {
            return;
        }
        var placeholder = loader.placeholder();
        var newPlaceholder = replacer.apply(placeholder);
        m_loaders.put(id, new Loader(loader.loadJob(), newPlaceholder, loader.exec()));
        m_workflowChangesListener.trigger(null);
    }

    /**
     * Cancels the load operation for the given id and removes it.
     *
     * @param id the id of the load job to cancel and remove
     */
    public void cancelAndRemoveLoadJob(final String id) {
        var loader = m_loaders.remove(id);
        if (loader != null && !loader.loadJob().future().isDone()) {
            loader.exec().getProgressMonitor().setExecuteCanceled();
            loader.loadJob().future().cancel(true);
        }
        // send workflow changed event to update placeholders
        m_workflowChangesListener.trigger(null);
    }

    /**
     * Returns the currently present placeholders and removes all the ones that aren't visible anymore (e.g. the ones
     * with state {@link StateEnum#SUCCESS}).
     *
     * @return the currently present placeholders or an empty list if none
     */
    public Collection<ComponentPlaceholderEnt> getComponentPlaceholdersAndCleanUp() {
        var placeholders = m_loaders.values().stream().map(Loader::placeholder).toList();
        m_loaders.values().removeIf(loader -> !isVisiblePlaceholder(loader.placeholder()));
        return placeholders;
    }

    private static boolean isVisiblePlaceholder(final ComponentPlaceholderEnt placeholder) {
        var state = placeholder.getState();
        return state == StateEnum.LOADING || state == StateEnum.ERROR;
    }

    private record Loader(LoadJob loadJob, ComponentPlaceholderEnt placeholder, ExecutionMonitor exec) {

    }

    /**
     * Represents a load job.
     *
     * @param id the identifier of the job
     * @param future the future that represents the loading operation; returns a {@link LoadResult} if it completes
     *            successfully
     */
    public record LoadJob(String id, CompletableFuture<LoadResult> future) {

    }

    /**
     * Represents the load result once the {@link LoadJob} finished successfully.
     *
     * @param componentId the id of the successfully added component
     * @param message a message on warnings/problems during load; can be {@code null}
     * @param details some more details regarding the loading problems; can be {@code null}
     */
    public record LoadResult(NodeID componentId, String message, String details) {

    }

}

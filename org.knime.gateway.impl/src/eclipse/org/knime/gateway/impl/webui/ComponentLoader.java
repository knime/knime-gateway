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
import org.knime.gateway.api.webui.entity.PlaceholderEnt;
import org.knime.gateway.api.webui.entity.PlaceholderEnt.PlaceholderEntBuilder;
import org.knime.gateway.api.webui.entity.PlaceholderEnt.StateEnum;
import org.knime.gateway.api.webui.entity.XYEnt.XYEntBuilder;
import org.knime.gateway.impl.service.util.WorkflowChangesListener;
import org.knime.gateway.impl.service.util.WorkflowChangesTracker.WorkflowChange;

/**
 * TODO
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
// TODO thread safety?
public final class ComponentLoader {

    private final Map<String, Loader> m_loaders = new LinkedHashMap<>();

    private final WorkflowChangesListener m_workflowChangesListener;

    ComponentLoader(final WorkflowChangesListener workflowChangesListener) {
        // TODO 'inverse' dependency? I.e. WorkflowChangesListener  listens to WorkflowElementLoader?
        m_workflowChangesListener = workflowChangesListener;
    }

    /**
     * Creates a new component loading job.
     *
     * @param x x coordinate to show the placeholder and the successfully loaded component
     * @param y y coordinate to show the placeholder and the successfully loaded component
     * @param loadComponent the actual component loading logic
     *
     * @return the job/placeholder id
     */
    public LoadJob addComponentLoadJob(final int x, final int y,
        final FailableFunction<ExecutionMonitor, LoadResult, CanceledExecutionException> loadComponent) {
        var id = UUID.randomUUID().toString();
        var exec = new ExecutionMonitor();
        NodeProgressListener progressListener = progressEvent -> updatePlaceholderWithMessageAndProgress(id,
            progressEvent.getNodeProgress().getMessage(), progressEvent.getNodeProgress().getProgress());
        exec.getProgressMonitor().addProgressListener(progressListener);
        Supplier<LoadResult> supplier = () -> {
            try {
                return loadComponent.apply(exec);
            } catch (CanceledExecutionException ex) {
                throw new CancellationException(ex.getMessage());
            }
        };
        // TODO use custom thread pool?
        var future = CompletableFuture.<LoadResult> supplyAsync(supplier).handle((result, ex) -> {
            if (ex == null) {
                updatePlaceholderWithSuccess(id, new NodeIDEnt(result.componentId).toString(), result.warningMessage);
            } else {
                updatePlaceholderWithError(id, ex.getMessage());
            }
            return result;
        });
        var loaderInternal = new Loader(new LoadJob(id, future),
            buildPlaceholderEnt(id, StateEnum.LOADING, x, y, null,
                exec.getProgressMonitor().getMessage() /* TODO progress messages?*/,
                exec.getProgressMonitor().getProgress()),
            exec);
        m_loaders.put(id, loaderInternal);
        m_workflowChangesListener.trigger(WorkflowChange.PLACERHOLDER_ADDED);
        return loaderInternal.loader();
    }

    private void updatePlaceholderWithMessageAndProgress(final String id, final String message, final Double progress) {
        replacePlaceholderEnt(id,
            placeholder -> buildPlaceholderEnt(id, placeholder.getState(), placeholder.getPosition().getX(),
                placeholder.getPosition().getY(), placeholder.getReplacementId(), message, progress));
    }

    private void updatePlaceholderWithSuccess(final String id, final String replacementId,
        final String warningMessage) {
        replacePlaceholderEnt(id, placeholder -> buildPlaceholderEnt(id,
            warningMessage == null ? StateEnum.SUCCESS : StateEnum.SUCCESS_WITH_WARNING,
            placeholder.getPosition().getX(), placeholder.getPosition().getY(), replacementId, warningMessage, null));
    }

    private void updatePlaceholderWithError(final String id, final String message) {
        replacePlaceholderEnt(id, placeholder -> buildPlaceholderEnt(id, StateEnum.ERROR,
            placeholder.getPosition().getX(), placeholder.getPosition().getY(), null, message, null));
    }

    private void replacePlaceholderEnt(final String id, final UnaryOperator<PlaceholderEnt> replacer) {
        var loader = m_loaders.get(id);
        if (loader == null) {
            return;
        }
        var placeholder = loader.placeholder();
        var newPlaceholder = replacer.apply(placeholder);
        m_loaders.put(id, new Loader(loader.loader(), newPlaceholder, loader.exec()));
        m_workflowChangesListener.trigger(null);
    }

    private static PlaceholderEnt buildPlaceholderEnt(final String id, final StateEnum state, final int x, final int y,
        final String replacementId, final String message, final Double progress) {
        return builder(PlaceholderEntBuilder.class) //
            .setId(id) //
            .setState(state) //
            .setPosition(builder(XYEntBuilder.class) //
                .setX(x) //
                .setY(y) //
                .build()) //
            .setReplacementId(replacementId) //
            .setMessage(message) //
            .setProgress(progress == null ? null : BigDecimal.valueOf(progress)) //
            .build();
    }

    /**
     * Cancels the load operation for the given id and removes it.
     *
     * @param id
     */
    public void cancelAndRemoveLoadOperation(final String id) {
        var loader = m_loaders.remove(id);
        if (loader != null && !loader.loader().future().isDone()) {
            loader.exec().getProgressMonitor().setExecuteCanceled();
            loader.loader().future().cancel(true);
        }
        // send workflow changed event to update placeholders
        m_workflowChangesListener.trigger(null);
    }

    /**
     * Returns the currently present placeholders and removes all the nones that aren't visible anymore (e.g. the ones
     * with state {@link StateEnum#SUCCESS}).
     *
     * @return the currently present placeholders or an empty list if none
     */
    public Collection<PlaceholderEnt> getPlaceholdersAndCleanUp() {
        var placerholders = m_loaders.values().stream().map(loader -> loader.placeholder()).toList();
        m_loaders.values().removeIf(loader -> !isVisiblePlaceholder(loader.placeholder()));
        return placerholders;
    }

    private static boolean isVisiblePlaceholder(final PlaceholderEnt placeholder) {
        var state = placeholder.getState();
        return state == StateEnum.LOADING;
    }

    private static record Loader(LoadJob loader, PlaceholderEnt placeholder, ExecutionMonitor exec) {

    }

    /**
     * Represents a load job.
     *
     * @param id the identifier of the job
     * @param future the future that represents the loading operation; returns a {@link LoadResult} if it completes
     *            successfully
     */
    public static record LoadJob(String id, CompletableFuture<LoadResult> future) {

    }

    /**
     * @param componentId the id of the successfully added component
     * @param warningMessage a message on warnings/problems during load; can be {@code null}
     */
    public static record LoadResult(NodeID componentId, String warningMessage) {

    }

}

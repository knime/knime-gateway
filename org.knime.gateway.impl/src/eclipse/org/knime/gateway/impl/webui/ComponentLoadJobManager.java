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
import java.util.concurrent.CompletionException;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

import org.knime.core.node.CanceledExecutionException;
import org.knime.core.node.ExecutionMonitor;
import org.knime.core.node.workflow.NodeProgressListener;
import org.knime.core.node.workflow.WorkflowManager;
import org.knime.gateway.api.entity.NodeIDEnt;
import org.knime.gateway.api.webui.entity.AddComponentCommandEnt;
import org.knime.gateway.api.webui.entity.ComponentPlaceholderEnt;
import org.knime.gateway.api.webui.entity.ComponentPlaceholderEnt.ComponentPlaceholderEntBuilder;
import org.knime.gateway.api.webui.entity.ComponentPlaceholderEnt.StateEnum;
import org.knime.gateway.impl.service.util.WorkflowChangesListener;
import org.knime.gateway.impl.service.util.WorkflowChangesTracker.WorkflowChange;
import org.knime.gateway.impl.webui.service.commands.util.ComponentLoader;
import org.knime.gateway.impl.webui.service.commands.util.ComponentLoader.LoadResult;
import org.knime.gateway.impl.webui.spaces.SpaceProviders;

/**
 * Manages component-loading jobs for a single workflow.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
public final class ComponentLoadJobManager {

    private final Map<String, LoadJobInternal> m_loadJobs = Collections.synchronizedMap(new LinkedHashMap<>());

    private final WorkflowManager m_wfm;

    private final WorkflowChangesListener m_workflowChangesListener;

    private final SpaceProviders m_spaceProviders;

    ComponentLoadJobManager(final WorkflowManager wfm, final WorkflowChangesListener workflowChangesListener,
        final SpaceProviders spaceProviders) {
        m_wfm = wfm;
        m_workflowChangesListener = workflowChangesListener;
        m_spaceProviders = spaceProviders;
    }

    /**
     * Creates a new component loading job.
     *
     * @param commandEnt the command-entity carrying all the infos required to load the component
     *
     * @return the job/placeholder id
     */
    public LoadJob startLoadJob(final AddComponentCommandEnt commandEnt) {
        var placeholderId = UUID.randomUUID().toString();
        var exec = new ExecutionMonitor();
        var loadJob = createLoadJob(placeholderId, exec, commandEnt);
        final var progress = exec.getProgressMonitor().getProgress();
        var placeholder = builder(ComponentPlaceholderEntBuilder.class) //
            .setId(placeholderId) //
            .setState(StateEnum.LOADING) //
            .setName(commandEnt.getName()) //
            .setPosition(commandEnt.getPosition()) //
            .setComponentId(null) //
            .setMessage(exec.getProgressMonitor().getMessage()) //
            .setProgress(progress == null ? null : BigDecimal.valueOf(progress)) //
            .build();

        var loadJobInternal = new LoadJobInternal(loadJob, placeholder, exec, commandEnt);
        m_loadJobs.put(placeholderId, loadJobInternal);
        m_workflowChangesListener.trigger(WorkflowChange.COMPONENT_PLACEHOLDER_ADDED);
        return loadJobInternal.loadJob();
    }

    private LoadJob createLoadJob(final String placeholderId, final ExecutionMonitor exec,
        final AddComponentCommandEnt commandEnt) {
        Supplier<LoadResult> componentLoader = () -> {
            NodeProgressListener progressListener =
                progressEvent -> updatePlaceholderWithMessageAndProgress(placeholderId,
                    progressEvent.getNodeProgress().getMessage(), progressEvent.getNodeProgress().getProgress());
            exec.getProgressMonitor().addProgressListener(progressListener);
            try {
                return ComponentLoader.loadComponent(commandEnt, m_wfm, m_spaceProviders, exec);
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
                } else if (ex instanceof CompletionException ce && ce.getCause() instanceof CancellationException) {
                    updatePlaceholderWithError(placeholderId, "Component loading cancelled", null);
                } else {
                    updatePlaceholderWithError(placeholderId, "Component could not be loaded", ex.getMessage());
                }
                return result;
            });
        return new LoadJob(placeholderId, future);
    }

    private void updatePlaceholderWithMessageAndProgress(final String id, final String message, final Double progress) {
        replacePlaceholderEnt(id,
            previousEnt -> builder(ComponentPlaceholderEntBuilder.class).setId(previousEnt.getId()) //
                .setState(StateEnum.LOADING) //
                .setName(previousEnt.getName()) //
                .setPosition(previousEnt.getPosition()) //
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
                .setName(previousEnt.getName()) //
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
                .setName(previousEnt.getName()) //
                .setPosition(previousEnt.getPosition()) //
                .setMessage(message) //
                .setDetails(details) //
                .build() //
        );
    }

    private void replacePlaceholderEnt(final String id, final UnaryOperator<ComponentPlaceholderEnt> replacer) {
        var originalJob = m_loadJobs.get(id);
        if (originalJob == null) {
            return;
        }
        m_loadJobs.put(id, new LoadJobInternal( //
            originalJob.loadJob(), //
            replacer.apply(originalJob.placeholder()), //
            originalJob.exec(), //
            originalJob.commandEnt()) //
        );
        m_workflowChangesListener.trigger(null);
    }

    /**
     * Cancels the load operation for the given id and removes it.
     *
     * @param id the id of the load job to cancel and remove
     * @return the command entity that was used to create the load job or null if no such job was found
     */
    public AddComponentCommandEnt cancelAndRemoveLoadJob(final String id) {
        var loadJob = m_loadJobs.remove(id);
        if (loadJob == null) {
            return null;
        }
        cancelLoadJob(loadJob);
        m_workflowChangesListener.trigger(null);
        return loadJob.commandEnt();
    }

    /**
     * Cancels all load jobs and removes them.
     */
    public void cancelAndRemoveAllLoadJobs() {
        m_loadJobs.values().stream().forEach(ComponentLoadJobManager::cancelLoadJob);
        m_loadJobs.clear();
    }

    /**
     * Cancels the load operation for the given id (provided the job isn't done, yet).
     *
     * @param id the id of the load job to cancel
     */
    public void cancelLoadJob(final String id) {
        cancelLoadJob(m_loadJobs.get(id));
    }

    private static void cancelLoadJob(final LoadJobInternal loadJobInternal) {
        if (loadJobInternal != null && !loadJobInternal.loadJob().future().isDone()) {
            loadJobInternal.exec().getProgressMonitor().setExecuteCanceled();
        }
    }

    /**
     * Reruns the load job for the given id (provided the job is done).
     *
     * @param id -
     */
    public void rerunLoadJob(final String id) {
        var originalJob = m_loadJobs.get(id);
        if (originalJob == null || !originalJob.loadJob().future().isDone()) {
            return;
        }
        var exec = new ExecutionMonitor();
        var loadJob = createLoadJob(id, exec, originalJob.commandEnt());
        m_loadJobs.put(id,
            new LoadJobInternal(loadJob, originalJob.placeholder(), exec, originalJob.commandEnt()));

    }

    /**
     * Returns the currently present placeholders and removes all the ones that aren't visible anymore (e.g. the ones
     * with state {@link StateEnum#SUCCESS}).
     *
     * @return the currently present placeholders or an empty list if none
     */
    public Collection<ComponentPlaceholderEnt> getComponentPlaceholdersAndCleanUp() {
        var placeholders = m_loadJobs.values().stream().map(LoadJobInternal::placeholder).toList();
        m_loadJobs.values().removeIf(loadJobInternal -> !isVisiblePlaceholder(loadJobInternal.placeholder()));
        return placeholders;
    }

    private static boolean isVisiblePlaceholder(final ComponentPlaceholderEnt placeholder) {
        var state = placeholder.getState();
        return state == StateEnum.LOADING || state == StateEnum.ERROR;
    }

    private record LoadJobInternal(LoadJob loadJob, ComponentPlaceholderEnt placeholder, ExecutionMonitor exec,
            AddComponentCommandEnt commandEnt) {

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

}

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
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.Future;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

import org.knime.core.node.ExecutionMonitor;
import org.knime.core.node.workflow.NodeID;
import org.knime.core.node.workflow.NodeProgressListener;
import org.knime.core.node.workflow.WorkflowManager;
import org.knime.core.node.workflow.WorkflowPersistor.LoadResult;
import org.knime.gateway.api.entity.NodeIDEnt;
import org.knime.gateway.api.webui.entity.AddComponentCommandEnt;
import org.knime.gateway.api.webui.entity.ComponentPlaceholderEnt;
import org.knime.gateway.api.webui.entity.ComponentPlaceholderEnt.ComponentPlaceholderEntBuilder;
import org.knime.gateway.api.webui.entity.ComponentPlaceholderEnt.StateEnum;
import org.knime.gateway.api.webui.entity.XYEnt;
import org.knime.gateway.impl.service.util.WorkflowChangesListener;
import org.knime.gateway.impl.service.util.WorkflowChangesTracker.WorkflowChange;
import org.knime.gateway.impl.webui.service.commands.WorkflowCommand;
import org.knime.gateway.impl.webui.service.commands.util.ComponentLoader;
import org.knime.gateway.impl.webui.spaces.SpaceProviders;

/**
 * Manages component-loading jobs for a single workflow.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 * @since 5.11
 */
public final class ComponentLoadJobManager {

    private final Map<String, LoadJobInternal> m_trackedLoadJobs = Collections.synchronizedMap(new LinkedHashMap<>());

    private final WorkflowManager m_wfm;

    private final WorkflowChangesListener m_workflowChangesListener;

    private final SpaceProviders m_spaceProviders;

    private final IComponentLoader m_componentLoader;

    ComponentLoadJobManager(final WorkflowManager wfm, final WorkflowChangesListener workflowChangesListener,
        final SpaceProviders spaceProviders, final IComponentLoader componentLoader) {
        m_wfm = wfm;
        m_workflowChangesListener = workflowChangesListener;
        m_spaceProviders = spaceProviders;
        m_componentLoader = componentLoader;
    }

    ComponentLoadJobManager(final WorkflowManager wfm, final WorkflowChangesListener workflowChangesListener,
        final SpaceProviders spaceProviders) {
        this( //
            wfm, //
            workflowChangesListener, //
            spaceProviders, //
            ComponentLoader::loadComponent //
        );
    }

    /**
     * Creates a new component loading job.
     *
     * @param commandEnt the command-entity carrying all the infos required to load the component
     * @param postLoadAction optional follow-up action executed after component load completes
     * @return the job/placeholder id string
     * @since 5.11
     */
    public LoadJob startLoadJob(final AddComponentCommandEnt commandEnt, final PostLoadAction postLoadAction) {
        var placeholderId = UUID.randomUUID().toString();

        // TODO fix position

        var loadJobRunner = createLoadJobRunner(placeholderId, commandEnt, postLoadAction);
        loadJobRunner.run();

        var loadJob = new LoadJob(placeholderId, loadJobRunner);
        var placeholder = builder(ComponentPlaceholderEntBuilder.class) //
            .setId(placeholderId) //
            .setState(StateEnum.LOADING) //
            .setName(commandEnt.getName()) //
            .setPosition(getPosition(commandEnt, m_wfm)) //
            .build();
        var loadJobInternal = new LoadJobInternal(loadJob, placeholder, commandEnt);
        m_trackedLoadJobs.put(placeholderId, loadJobInternal);
        m_workflowChangesListener.trigger(WorkflowChange.COMPONENT_PLACEHOLDER_ADDED);
        return loadJobInternal.loadJob();
    }

    private static XYEnt getPosition(final AddComponentCommandEnt commandEnt, final WorkflowManager wfm) {
        var replacementOptions = commandEnt.getReplacementOptions();
        if (replacementOptions != null) {
            // In API for native nodes, a position is not required for replacement. We follow the pattern here.
            return Optional.ofNullable(wfm.getNodeContainer(replacementOptions.getTargetNodeId().toNodeID(wfm))) //
                .map(nc -> nc.getUIInformation().getBounds()) //
                .map(bounds -> builder(XYEnt.XYEntBuilder.class) //
                    .setX(bounds[0]).setY(bounds[1]).build() //
                ).orElseThrow(() -> new IllegalStateException("Cannot determine placeholder position from target node") //
                ); //
        }
        var givenPosition = commandEnt.getPosition();
        if (givenPosition == null) {
            throw new IllegalArgumentException("Expected to receive position for placeholder from frontend");
        }
        return givenPosition;
    }

    /**
     * @since 5.11
     */
    private LoadJobRunner createLoadJobRunner(final String placeholderId, final AddComponentCommandEnt commandEnt,
        final PostLoadAction postLoadAction) {
        return new LoadJobRunner(placeholderId, commandEnt, postLoadAction);
    }

    /**
     * Creates a new component loading job.
     *
     * @param commandEnt the command-entity carrying all the infos required to load the component
     * @return the job/placeholder id string
     * @since 5.11
     */
    public LoadJob startLoadJob(final AddComponentCommandEnt commandEnt) {
        return startLoadJob(commandEnt, null);
    }

    private static <E extends Throwable> Optional<E> unwrappedInstanceOf(final Throwable ex, final Class<E> clazz) {
        // Depending on which CompletableFuture composition is used (supplyAsync, thenApply, handle, ...) exceptions
        // come either raw or wrapped in CompletionException. For simplicity, we cover both cases and unwrap when
        // possible.
        if (ex == null) {
            return Optional.empty();
        }
        var unwrapped = (ex instanceof CompletionException ce && ce.getCause() != null) //
            ? ce.getCause() //
            : ex;
        if (clazz.isInstance(unwrapped)) {
            return Optional.of(clazz.cast(unwrapped));
        } else {
            return Optional.empty();
        }
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
        var originalJob = m_trackedLoadJobs.get(id);
        if (originalJob == null) {
            return;
        }
        m_trackedLoadJobs.put(id, new LoadJobInternal( //
            originalJob.loadJob(), //
            replacer.apply(originalJob.placeholder()), //
            originalJob.commandEnt()) //
        );
        m_workflowChangesListener.trigger(null);
    }

    /**
     * Cancels the load operation for the given id and removes it.
     *
     * @param id the id of the load job to cancel and remove
     * @return the command entity that was used to create the load job or null if no such job was found
     * @since 5.11
     */
    public AddComponentCommandEnt cancelAndRemoveLoadJob(final String id) {
        var loadJob = m_trackedLoadJobs.remove(id);
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
        m_trackedLoadJobs.values().stream().forEach(ComponentLoadJobManager::cancelLoadJob);
        m_trackedLoadJobs.clear();
    }

    /**
     * Cancels the load operation for the given id (provided the job isn't done, yet).
     *
     * @param id the id of the load job to cancel
     */
    public void cancelLoadJob(final String id) {
        cancelLoadJob(m_trackedLoadJobs.get(id));
    }

    private static void cancelLoadJob(final LoadJobInternal loadJobInternal) {
        if (loadJobInternal == null) {
            return;
        }
        if (loadJobInternal.loadJob().runner().isLoadDone()) {
            return;
        }
        loadJobInternal.loadJob().runner().cancel();
    }

    /**
     * Wires together the component load, optional post-load action, and callbacks.
     *
     * <p>
     * A single instance manages one load execution at a time. It exposes helpers to check completion and access
     * immediate results if available.
     *
     * @since 5.11
     */
    public final class LoadJobRunner {
        private final Function<ExecutionMonitor, NodeID> m_loadComponent;

        private final PostLoadAction m_postLoadAction;

        private final Supplier<NodeProgressListener> m_progressListener;

        private final Consumer<NodeID> m_onLoadSuccess;

        private final Consumer<ComponentLoader.ComponentLoadedWithWarningsException> m_onLoadWithWarnings;

        private final Consumer<CancellationException> m_onCancelled;

        private final Consumer<Throwable> m_onError;

        private CompletableFuture<NodeID> m_currentLoadFuture;

        private CompletableFuture<WorkflowCommand> m_currentPostLoadFuture;

        private volatile ExecutionMonitor m_monitor;

        /**
         * Creates a load job runner with default wiring for production usage.
         *
         * @param placeholderId the placeholder id used for progress and completion updates
         * @param commandEnt the command entity used to load the component
         * @param postLoadAction optional follow-up action executed after component load completes
         */
        public LoadJobRunner( //
            final String placeholderId, //
            final AddComponentCommandEnt commandEnt, //
            final PostLoadAction postLoadAction //
        ) {
            this( //
                monitor -> m_componentLoader.loadComponent(commandEnt, m_wfm, m_spaceProviders, monitor), //
                postLoadAction, //
                () -> progressEvent -> updatePlaceholderWithMessageAndProgress( //
                    placeholderId, //
                    progressEvent.getNodeProgress().getMessage(), //
                    progressEvent.getNodeProgress().getProgress() //
                ), //
                componentId -> updatePlaceholderWithSuccess( //
                    placeholderId, //
                    new NodeIDEnt(componentId).toString(), //
                    null, //
                    null //
                ), //
                ex -> updatePlaceholderWithSuccess( //
                    placeholderId, //
                    new NodeIDEnt(ex.getComponentId()).toString(), //
                    ex.getTitle(), //
                    ex.getMessage() //
                ), //
                ex -> updatePlaceholderWithError( //
                    placeholderId, //
                    "Component loading cancelled", //
                    null //
                ), //
                ex -> updatePlaceholderWithError( //
                    placeholderId, //
                    "Component could not be loaded", //
                    ex != null ? ex.getMessage() : null //
                ) //
            );
        }

        /**
         * Creates a fully configurable load job runner, primarily intended for tests.
         *
         * @param loadComponent callback that performs the component loading
         * @param postLoadAction optional follow-up action executed after component load completes
         * @param progressListener supplier for progress listener instances
         * @param onLoadSuccess callback invoked on successful load
         * @param onLoadWithWarnings callback invoked when loading completes with warnings
         * @param onCancelled callback invoked when loading is cancelled
         * @param onError callback invoked on load failure
         */
        LoadJobRunner( //
            final Function<ExecutionMonitor, NodeID> loadComponent, //
            final PostLoadAction postLoadAction, //
            final Supplier<NodeProgressListener> progressListener, //
            final Consumer<NodeID> onLoadSuccess, //
            final Consumer<ComponentLoader.ComponentLoadedWithWarningsException> onLoadWithWarnings, //
            final Consumer<CancellationException> onCancelled, //
            final Consumer<Throwable> onError //
        ) {
            m_loadComponent = loadComponent;
            m_postLoadAction = postLoadAction;
            m_progressListener = progressListener;
            m_onLoadSuccess = onLoadSuccess;
            m_onLoadWithWarnings = onLoadWithWarnings;
            m_onCancelled = onCancelled;
            m_onError = onError;
        }

        void run() {
            var monitor = new ExecutionMonitor();
            m_monitor = monitor; // volatile to make cancellation visibility explicit across threads
            var listener = m_progressListener.get();
            monitor.getProgressMonitor().addProgressListener(listener);

            // Keep the original future so we can attach listener cleanup without mixing in the handled
            // error-mapping below.
            var rawLoadFuture = CompletableFuture.supplyAsync(() -> {
                monitor.setProgress(0.0); // emit a first progress event to make placeholder appear
                return m_loadComponent.apply(monitor);
            });
            rawLoadFuture.whenComplete((res, ex) -> monitor.getProgressMonitor().removeProgressListener(listener));

            var handledLoadFuture = rawLoadFuture.handle(this::delegateComponentLoadResult);
            var postLoadFuture = composePostLoadFuture(handledLoadFuture, m_postLoadAction);
            // keep futures in fields to access results
            m_currentLoadFuture = handledLoadFuture;
            m_currentPostLoadFuture = postLoadFuture;
        }

        private NodeID delegateComponentLoadResult(final NodeID result, final Throwable exception) {
            if (result != null) { // previous stage completed successfully
                Optional.ofNullable(m_onLoadSuccess).ifPresent(f -> f.accept(result));
                return result;
            }
            // reaching this implies exception != null
            var loadedWithWarnings =
                unwrappedInstanceOf(exception, ComponentLoader.ComponentLoadedWithWarningsException.class).orElse(null);
            if (loadedWithWarnings != null) {
                m_onLoadWithWarnings.accept(loadedWithWarnings);
                // still have this future chain complete successfully
                return loadedWithWarnings.getComponentId();
            }
            var cancelled = unwrappedInstanceOf(exception, CancellationException.class).orElse(null);
            if (cancelled != null) {
                m_onCancelled.accept(cancelled);
                throw new CompletionException(cancelled);
            }
            var other = unwrappedInstanceOf(exception, Throwable.class).orElse(null);
            m_onError.accept(other);
            throw new CompletionException(other != null ? other : null);
        }

        private CompletableFuture<WorkflowCommand> composePostLoadFuture(final CompletableFuture<NodeID> loadFuture,
            final PostLoadAction postLoadAction) {
            if (postLoadAction == null) {
                return null;
            }
            return loadFuture.thenApplyAsync(componentId -> postLoadAction.apply(m_wfm, componentId));
        }

        void cancel() {
            if (m_monitor == null) {
                return;
            }
            // Cancellation is cooperative: we signal the execution monitor, and the loader will observe the flag and
            // terminate. Futures are not cancelled directly to avoid interrupting workflow operations mid-flight.
            m_monitor.getProgressMonitor().setExecuteCanceled();
        }

        /**
         * @return whether component loading is done, either successfully, with failure, or cancellation
         */
        public boolean isLoadDone() {
            return Optional.ofNullable(m_currentLoadFuture).map(Future::isDone).orElse(true);
        }

        /**
         * @return the id of the loaded component if loading completed successfully, or an empty optional if loading is
         *         still in progress or failed
         */
        public Optional<NodeID> loadGetNow() {
            return getNow(m_currentLoadFuture);
        }

        /**
         * @return the post-load workflow command if the post-load action completed successfully, or an empty optional
         *         if the post-load action is still in progress, failed, or was not defined
         */
        public Optional<WorkflowCommand> postLoadGetNow() {
            return getNow(m_currentPostLoadFuture);
        }

        private static <T> Optional<T> getNow(final CompletableFuture<T> future) {
            return Optional.ofNullable(future).map(f -> {
                try {
                    return f.getNow(null); // value if already completed, null else
                } catch (CompletionException | CancellationException e) {
                    return null;
                }
            });
        }
    }

    /**
     * Reruns the load job for the given id (provided the job is done).
     *
     * @param id the id of the load job to restart
     */
    public void rerunLoadJob(final String id) {
        var job = m_trackedLoadJobs.get(id);
        if (job == null || !job.loadJob().runner().isLoadDone()) {
            return;
        }
        job.loadJob().runner().run();
        m_trackedLoadJobs.put( //
            id, //
            new LoadJobInternal( //
                job.loadJob(), //
                job.placeholder(), //
                job.commandEnt() //
            )//
        ); //
    }

    /**
     * Returns the currently present placeholders and removes all the ones that aren't visible anymore (e.g. the ones
     * with state {@link StateEnum#SUCCESS}).
     *
     * @return the currently present placeholders or an empty list if none
     */
    public Collection<ComponentPlaceholderEnt> getComponentPlaceholdersAndCleanUp() {
        var placeholders = m_trackedLoadJobs.values().stream().map(LoadJobInternal::placeholder).toList();
        m_trackedLoadJobs.values().removeIf(loadJobInternal -> !isVisiblePlaceholder(loadJobInternal.placeholder()));
        return placeholders;
    }

    private static boolean isVisiblePlaceholder(final ComponentPlaceholderEnt placeholder) {
        var state = placeholder.getState();
        return state == StateEnum.LOADING || state == StateEnum.ERROR;
    }

    /**
     * Follow-up action executed after a component has been loaded.
     *
     * @since 5.11
     */
    @FunctionalInterface
    public interface PostLoadAction {

        /**
         * Applies the post-load action.
         *
         * @param wfm the workflow manager owning the loaded component
         * @param componentId the id of the loaded component
         * @return the post-load workflow command, or null for no command
         */
        WorkflowCommand apply(WorkflowManager wfm, NodeID componentId);
    }

    /**
     * Adapter interface to load a component with a given execution monitor.
     */
    @FunctionalInterface
    interface IComponentLoader {
        /**
         * Loads the component represented by the given command entity.
         *
         * @param ent the command entity with component metadata and position
         * @param wfm the workflow manager receiving the component
         * @param spaceProviders space providers used for component resolution
         * @param executionMonitor execution monitor for progress and cancellation
         * @return the id of the loaded component
         */
        NodeID loadComponent(AddComponentCommandEnt ent, WorkflowManager wfm, SpaceProviders spaceProviders,
            ExecutionMonitor executionMonitor);
    }

    private record LoadJobInternal( //
            LoadJob loadJob, //
            ComponentPlaceholderEnt placeholder, //
            AddComponentCommandEnt commandEnt //
    ) {

    }

    /**
     * Represents a load job.
     *
     * @param id the identifier of the job
     * @param runner the future that represents the loading operation; returns a {@link LoadResult} if it completes
     *            successfully
     * @since 5.11
     */
    public record LoadJob(String id, LoadJobRunner runner) {

    }

}

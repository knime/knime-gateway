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
 *   Mar 11, 2021 (hornm): created
 */
package org.knime.gateway.impl.webui;

import static org.knime.gateway.api.entity.EntityBuilderManager.builder;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

import org.knime.core.node.workflow.NodeContainer;
import org.knime.core.node.workflow.SubNodeContainer;
import org.knime.core.node.workflow.WorkflowEvent;
import org.knime.core.node.workflow.WorkflowListener;
import org.knime.core.node.workflow.WorkflowLock;
import org.knime.core.node.workflow.WorkflowManager;
import org.knime.core.util.Pair;
import org.knime.gateway.api.util.DependentNodeProperties;
import org.knime.gateway.api.webui.entity.PatchEnt;
import org.knime.gateway.api.webui.entity.WorkflowChangedEventEnt;
import org.knime.gateway.api.webui.entity.WorkflowChangedEventEnt.WorkflowChangedEventEntBuilder;
import org.knime.gateway.api.webui.entity.WorkflowEnt;
import org.knime.gateway.api.webui.entity.WorkflowMonitorStateChangeEventEnt;
import org.knime.gateway.api.webui.entity.WorkflowMonitorStateChangeEventEnt.WorkflowMonitorStateChangeEventEntBuilder;
import org.knime.gateway.api.webui.entity.WorkflowMonitorStateEnt;
import org.knime.gateway.api.webui.entity.WorkflowMonitorStateSnapshotEnt;
import org.knime.gateway.api.webui.entity.WorkflowMonitorStateSnapshotEnt.WorkflowMonitorStateSnapshotEntBuilder;
import org.knime.gateway.api.webui.entity.WorkflowSnapshotEnt;
import org.knime.gateway.api.webui.entity.WorkflowSnapshotEnt.WorkflowSnapshotEntBuilder;
import org.knime.gateway.api.webui.util.EntityFactory;
import org.knime.gateway.api.webui.util.WorkflowBuildContext;
import org.knime.gateway.api.webui.util.WorkflowBuildContext.WorkflowBuildContextBuilder;
import org.knime.gateway.impl.project.ProjectManager;
import org.knime.gateway.impl.service.util.EntityRepository;
import org.knime.gateway.impl.service.util.PatchCreator;
import org.knime.gateway.impl.service.util.PatchEntCreator;
import org.knime.gateway.impl.service.util.SimpleRepository;
import org.knime.gateway.impl.service.util.WorkflowChangesListener;
import org.knime.gateway.impl.service.util.WorkflowChangesListener.Scope;
import org.knime.gateway.impl.service.util.WorkflowChangesTracker;
import org.knime.gateway.impl.service.util.WorkflowChangesTracker.WorkflowChange;
import org.knime.gateway.impl.service.util.WorkflowManagerResolver;
import org.knime.gateway.impl.webui.service.commands.WorkflowCommands;
import org.knime.gateway.impl.webui.spaces.SpaceProviders;
import org.knime.gateway.impl.webui.spaces.SpaceProvidersManager;
import org.knime.gateway.impl.webui.spaces.SpaceProvidersManager.Key;

/**
 * Provides utility methods to operate on a workflow represented by a {@link WorkflowKey} where the methods require to
 * keep a state per workflow (e.g. cached objects, undo & redo stack, entity history). The state per workflow is cleared
 * as soon as it's removed from memory (the actual {@link WorkflowManager}-instances are accessed via the
 * {@link ProjectManager}).
 *
 * The purpose is to remove this complexity from the default service implementations.
 *
 * Note: this class not 100% thread-safe, yet (e.g. {@link SimpleRepository})!
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 * @author Kai Franze, KNIME GmbH
 */
public final class WorkflowMiddleware {

    /**
     * Determines the number of commands per workflow kept in the undo and redo stacks.
     */
    private static final int UNDO_AND_REDO_STACK_SIZE_PER_WORKFLOW = 50;

    private final EntityRepository<WorkflowKey, WorkflowEnt> m_workflowEntRepo =
        new SimpleRepository<>(1, new SnapshotIdGenerator());

    private final EntityRepository<WorkflowKey, WorkflowMonitorStateEnt> m_workflowMonitorStateEntRepo =
        new SimpleRepository<>(1, new SnapshotIdGenerator());

    /**
     * Needs to be a {@link ConcurrentHashMap}!
     *
     * Operations on this synchronized map (e.g. {@link Map#computeIfAbsent(Object, Function)} might in turn require
     * other locks (e.g. {@link WorkflowLock}). Such an operation is performed while the lock on the map is being held
     * which might lead to a deadlock (NXT-2667).
     *
     * <p>
     * Example:
     * <ul>
     * <li>Thread B is in mapping function of {@code computeIfAbsent}. The mapping function (second parameter) is called
     * <i>while</i> the lock on the map is being held. The mapping function, in turn, requires a WorkflowLock for
     * {@link WorkflowState#WorkflowState(WorkflowKey)}.</li>
     * <li>Thread A might come into this method already holding a WorkflowLock.</li>
     * </ul>
     * B is blocked by A on its WorkflowLock. A is blocked by B on the map's lock.
     *
     * Consequently, we do need to synchronize the map but only on 'key-level'. If synchronized on 'access-level' in
     * general, deadlocks can happen for different keys. Concurrent hash map prevents that by synchronizing on ~keys.
     * But deadlock could theoretically still happen on concurrent operations for the same key.
     */
    final Map<WorkflowKey, WorkflowState> m_workflowStateCache = new ConcurrentHashMap<>();

    private final WorkflowCommands m_commands = new WorkflowCommands(UNDO_AND_REDO_STACK_SIZE_PER_WORKFLOW);

    private final SpaceProvidersManager m_spaceProvidersManager;

    /**
     * @param projectManager
     * @since 5.5
     */
    @SuppressWarnings("java:S1176") // javadoc
    public WorkflowMiddleware(final ProjectManager projectManager) {
        this(projectManager, null);
    }

    /**
     * @param projectManager
     * @param spaceProvidersManager
     */
    @SuppressWarnings("java:S1176") // javadoc
    public WorkflowMiddleware(final ProjectManager projectManager, final SpaceProvidersManager spaceProvidersManager) {
        m_spaceProvidersManager = spaceProvidersManager;
        projectManager.addProjectRemovedListener(
            projectId -> clearWorkflowState(wfKey -> wfKey.getProjectId().equals(projectId)));
    }

    /**
     * TODO NXT-3637 re-visit - might not need to be public anymore
     *
     * @param keyFilter -
     * @since 5.5
     */
    public synchronized void clearWorkflowState(final Predicate<WorkflowKey> keyFilter) {
        m_workflowEntRepo.disposeHistory(keyFilter);
        m_workflowMonitorStateEntRepo.disposeHistory(keyFilter);
        m_commands.disposeUndoAndRedoStacks(keyFilter);
        m_workflowStateCache.entrySet().removeIf(e -> {
            if (keyFilter.test(e.getKey())) {
                e.getValue().dispose();
                return true;
            } else {
                return false;
            }
        });
    }

    /**
     * Creates a new workflow snapshot entity. If there are any changes to the workflow, a new workflow entity is
     * committed to the {@link EntityRepository} and the respective snapshot id used. Otherwise, the snapshot id of the
     * last commit will be used.
     *
     * @param wfKey the workflow to get/create the snapshot for
     * @param buildContextSupplier the information required to build a workflow entity - provided in a lazy manner
     *            because if won't be used if the entity is retrieved from cache
     * @return a new entity instance
     */
    public WorkflowSnapshotEnt buildWorkflowSnapshotEnt(final WorkflowKey wfKey,
        final Supplier<WorkflowBuildContextBuilder> buildContextSupplier) {
        var workflowState = getWorkflowState(wfKey);
        var workflowEntity = EntityFactory.Workflow.buildWorkflowEnt(workflowState.m_wfm, buildContextSupplier.get());

        // try to commit the workflow entity and return the (existing or new) snapshot
        final var snapshotId = m_workflowEntRepo.commit(wfKey, workflowEntity);
        return builder(WorkflowSnapshotEntBuilder.class).setSnapshotId(snapshotId).setWorkflow(workflowEntity).build();
    }

    /**
     * Builds a new {@link WorkflowMonitorStateChangeEventEnt} instance. If there are any changes to the workflow, a new
     * workflow entity is committed to the {@link EntityRepository} and the respective snapshot id used. Otherwise, the
     * snapshot id of the last commit will be used.
     *
     * @param wfKey the workflow to get/create the snapshot for
     * @return the entity instance
     */
    public WorkflowMonitorStateSnapshotEnt buildWorkflowMonitorStateSnapshotEnt(final WorkflowKey wfKey) {
        var ws = getWorkflowState(wfKey);
        var state = EntityFactory.WorkflowMonitorState.buildWorkflowMonitorStateEnt(ws.m_wfm);
        return builder(WorkflowMonitorStateSnapshotEntBuilder.class).setState(state)
            .setSnapshotId(m_workflowMonitorStateEntRepo.commit(wfKey, state)).build();
    }

    /**
     * Get the latest snapshot ID
     *
     * @param wfKey The workflow to query
     * @return The snapshot-id of the most recent commit, or an empty optional if there are no commits yet.
     */
    public Optional<String> getLatestSnapshotId(final WorkflowKey wfKey) {
        var latestCommit = m_workflowEntRepo.getLastCommit(wfKey);
        return latestCommit.map(Pair::getFirst);
    }

    /**
     * Gives access to {@link WorkflowCommands} to in order to execute, undo or redo commands.
     *
     * @return the workflow commands instance
     */
    public WorkflowCommands getCommands() {
        return m_commands;
    }

    /**
     * Returns the {@link WorkflowChangesListener} associated with the workflow represented by the given
     * {@link WorkflowKey}.
     *
     * If called for the first time, the {@link WorkflowChangesListener} will be created. Subsequent calls will always
     * return the very same instance (per workflow).
     *
     * @param wfKey -
     * @return the changes listener, never <code>null</code>
     */
    public WorkflowChangesListener getWorkflowChangesListener(final WorkflowKey wfKey) {
        var ws = getWorkflowState(wfKey);
        return ws.changesListener();
    }

    /**
     * Returns the {@link WorkflowChangesListener} associated with the workflow represented by the given
     * {@link WorkflowKey} as required by the workflow monitor (it only listen for a subset of workflow changes, but
     * does it recursively).
     *
     * If called for the first time, the {@link WorkflowChangesListener} will be created. Subsequent calls will always
     * return the very same instance (per workflow).
     *
     * @param wfKey -
     * @return the listener instance
     */
    public WorkflowChangesListener getWorkflowChangesListenerForWorkflowMonitor(final WorkflowKey wfKey) {
        return getWorkflowState(wfKey).changesListenerForWorkflowMonitor();
    }

    /**
     * Helper to create a {@link WorkflowChangedEventEnt}-instance.
     *
     * When calling this, it is assumed that {@link #buildWorkflowSnapshotEnt(WorkflowKey, Supplier)} has been called at
     * least once before for the given workflow key.
     *
     * State information (maintained by this {@link WorkflowMiddleware}-instance) is used to <br>
     * - avoid unnecessary calculations of {@link DependentNodeProperties} on workflow changes tracked by the associated
     * {@link WorkflowChangesListener} (only if interaction info is to be included)<br>
     * - include the can-undo and can-redo flags determined via {@link WorkflowCommands} (only if interaction info is to
     * be included)<br>
     * - compare the current state against an older state (i.e. entity history managed via {@link EntityRepository})
     *
     * @param wfKey the workflow to create the changed event for
     * @param patchEntCreator creator for the patch which is supplied with the {@link WorkflowChangedEventEnt}
     * @param snapshotId the latest snapshot id
     * @param includeInteractionInfo see {@link WorkflowBuildContextBuilder#includeInteractionInfo(boolean)}
     * @return <code>null</code> if there are no changes, otherwise the {@link WorkflowChangedEventEnt}
     */
    public WorkflowChangedEventEnt buildWorkflowChangedEvent(final WorkflowKey wfKey,
        final PatchEntCreator patchEntCreator, final String snapshotId, final boolean includeInteractionInfo) {
        var buildContextBuilder = WorkflowBuildContext.builder()//
            .includeInteractionInfo(includeInteractionInfo);
        final var ws = getWorkflowState(wfKey);
        if (includeInteractionInfo) {
            buildContextBuilder.canUndo(m_commands.canUndo(wfKey))//
                .canRedo(m_commands.canRedo(wfKey))//
                .setDependentNodeProperties(() -> getDependentNodeProperties(wfKey))//
                .setComponentPlaceholders(getComponentLoadJobManager(wfKey).getComponentPlaceholdersAndCleanUp());
        }
        if (m_spaceProvidersManager != null) {
            buildContextBuilder.setSpaceProviderTypes(
                m_spaceProvidersManager.getSpaceProviders(Key.of(wfKey.getProjectId())).getProviderTypes());
        }
        final var wfEnt = EntityFactory.Workflow.buildWorkflowEnt(ws.m_wfm, buildContextBuilder);
        if (wfEnt == null) {
            // no change
            return null;
        } else {
            var patch = m_workflowEntRepo.getChangesAndCommit(snapshotId, wfEnt, patchEntCreator).orElse(null);
            return patch == null ? null : builder(WorkflowChangedEventEntBuilder.class).setPatch(patch)
                .setSnapshotId(patchEntCreator.getLastSnapshotId()).build();
        }
    }

    /**
     * Helper to create a {@link WorkflowMonitorStateChangeEventEnt}-instance.
     *
     * When calling this, it is assumed that {@link #buildWorkflowMonitorStateSnapshotEnt(WorkflowKey)} has been called
     * at least once before with the given workflow key.
     *
     * State information (maintained by this {@link WorkflowMiddleware}-instance) is used to <br>
     * - compare the current state against an older state (i.e. entity history managed via {@link EntityRepository})
     *
     * @param wfKey the workflow to create the changed event for
     * @param snapshotId the latest snapshot id
     * @param patchCreator helper to create the patch (diff)
     * @return {@code null} if there are not changes, other the event-entity instance
     */
    public WorkflowMonitorStateChangeEventEnt buildWorkflowMonitorStateChangeEventEnt(final WorkflowKey wfKey,
        final String snapshotId, final PatchCreator<PatchEnt> patchCreator) {
        final var ws = getWorkflowState(wfKey);
        var monitorState = EntityFactory.WorkflowMonitorState.buildWorkflowMonitorStateEnt(ws.m_wfm);
        var patch =
            m_workflowMonitorStateEntRepo.getChangesAndCommit(snapshotId, monitorState, patchCreator).orElse(null);
        if (patch != null) {
            return builder(WorkflowMonitorStateChangeEventEntBuilder.class).setPatch(patch).build();
        } else {
            return null;
        }
    }

    /**
     * -
     *
     * @param wfKey -
     * @return <code>true</code> if there is state cached for the workflow represented by the given workflow key
     */
    public boolean hasStateFor(final WorkflowKey wfKey) {
        return m_workflowStateCache.containsKey(wfKey);
    }

    /**
     * Clears the cached {@link DependentNodeProperties} to make sure those are re-computed when calling
     * {@link #buildWorkflowChangedEvent(WorkflowKey, PatchEntCreator, String, boolean)} the next time.
     *
     * This is just a temporary solution and will be clean-up with NXT-3469.
     *
     * @param wfKey the workflow to clear the cache for
     */
    public void clearCachedDependentNodeProperties(final WorkflowKey wfKey) {
        var state = m_workflowStateCache.get(wfKey);
        if (state != null) {
            state.m_depNodeProperties.clearCache();
        }
    }

    /**
     * Obtain the dependent node properties for the given workflow. Recalculate if the the workflow has pending changes
     * or use cached data otherwise.
     *
     * @param wfKey The workflow key characterising the workflow
     * @return recent {@code DependentNodeProperties}
     */
    private DependentNodeProperties getDependentNodeProperties(final WorkflowKey wfKey) {
        return getWorkflowState(wfKey).getDependentNodeProperties();
    }

    /**
     * -
     *
     * @param wfKey -
     * @return the {@link ComponentLoadJobManager} instance for the given workflow
     * @since 5.5
     */
    public ComponentLoadJobManager getComponentLoadJobManager(final WorkflowKey wfKey) {
        return getWorkflowState(wfKey).componentLoadJobManager();
    }

    /**
     * @see WorkflowMiddleware#m_workflowStateCache
     */
    private WorkflowState getWorkflowState(final WorkflowKey wfKey) {
        return m_workflowStateCache.computeIfAbsent(wfKey, this::createWorkflowStateAndEventuallyClearCache);
    }

    private WorkflowState createWorkflowStateAndEventuallyClearCache(final WorkflowKey wfKey) {
        final var state = new WorkflowState( //
            wfKey, //
            m_spaceProvidersManager == null //
                ? null //
                : m_spaceProvidersManager.getSpaceProviders(Key.of(wfKey.getProjectId())) //
        );
        final var wfm = state.m_wfm;
        if (!wfm.isProject()) {
            var nc = getNodeContainerOf(wfm); // component or metanode
            nc.getParent().addListener(new NodeRemovedListenerToClearWorkflowState(wfKey, nc));
        }
        return state;
    }

    private static NodeContainer getNodeContainerOf(final WorkflowManager wfm) {
        final var ncParent = (NodeContainer)wfm.getDirectNCParent();
        if (ncParent instanceof SubNodeContainer) {
            return ncParent;
        } else {
            return wfm;
        }
    }

    private static class SnapshotIdGenerator implements Supplier<String> {

        private final AtomicLong m_count = new AtomicLong();

        @Override
        public String get() {
            return Long.toString(m_count.getAndIncrement());
        }

    }

    /**
     * Helper class that summarizes all the things that are associated with a single workflow.
     */
    private static final class WorkflowState {

        private final WorkflowManager m_wfm;

        private final SpaceProviders m_spaceProviders;

        private CachedDependentNodeProperties m_depNodeProperties;

        private WorkflowChangesListener m_changesListener;

        private WorkflowChangesListener m_changesListenerForWorkflowMonitor;

        private ComponentLoadJobManager m_componentLoadJobManager;

        private WorkflowState(final WorkflowKey wfKey, final SpaceProviders spaceProviders) {
            m_spaceProviders = spaceProviders;
            m_wfm = WorkflowManagerResolver.load(wfKey.getProjectId(), wfKey.getWorkflowId(), wfKey.getVersionId());
        }

        DependentNodeProperties getDependentNodeProperties() {
            if (m_depNodeProperties == null) {
                m_depNodeProperties = new CachedDependentNodeProperties(m_wfm, changesListener());
            }
            return m_depNodeProperties.get();
        }

        WorkflowChangesListener changesListener() {
            if (m_changesListener == null) {
                m_changesListener = new WorkflowChangesListener(m_wfm);
            }
            return m_changesListener;
        }

        WorkflowChangesListener changesListenerForWorkflowMonitor() {
            if (m_changesListenerForWorkflowMonitor == null) {
                m_changesListenerForWorkflowMonitor =
                    new WorkflowChangesListener(m_wfm, Set.of(Scope.NODE_MESSAGES), true);
            }
            return m_changesListenerForWorkflowMonitor;
        }

        ComponentLoadJobManager componentLoadJobManager() {
            if (m_componentLoadJobManager == null) {
                m_componentLoadJobManager = new ComponentLoadJobManager(m_wfm, changesListener(), m_spaceProviders);
            }
            return m_componentLoadJobManager;
        }

        void dispose() {
            if (m_depNodeProperties != null) {
                m_depNodeProperties.dispose();
            }
            if (m_changesListener != null) {
                m_changesListener.close();
            }
            if (m_changesListenerForWorkflowMonitor != null) {
                m_changesListenerForWorkflowMonitor.close();
            }
            if (m_componentLoadJobManager != null) {
                m_componentLoadJobManager.cancelAndRemoveAllLoadJobs();
            }
        }

    }

    /**
     * Wrapper around {@link DependentNodeProperties} that tracks the given workflow for changes affecting the dependent
     * node properties and recomputes them if needed.
     */
    private static final class CachedDependentNodeProperties {

        private DependentNodeProperties m_dependentNodeProperties;

        private final WorkflowChangesTracker m_tracker;

        private final WorkflowChangesListener m_wfChangesListener;

        private final WorkflowManager m_wfm;

        CachedDependentNodeProperties(final WorkflowManager wfm, final WorkflowChangesListener wfChangesListener) {
            m_wfm = wfm;
            m_wfChangesListener = wfChangesListener;
            m_tracker = m_wfChangesListener.createWorkflowChangeTracker();
        }

        @SuppressWarnings("java:S1176") // javadoc
        public DependentNodeProperties get() {
            var recompute = m_dependentNodeProperties == null || m_tracker.invoke(t -> {
                var nodeStateChanges = t.hasOccurredAtLeastOne(WorkflowChange.NODE_STATE_UPDATED);
                var nodeOrConnectionAddedOrRemoved = t.hasOccurredAtLeastOne(WorkflowChange.NODE_ADDED,
                    WorkflowChange.NODE_REMOVED, WorkflowChange.CONNECTION_ADDED, WorkflowChange.CONNECTION_REMOVED);
                t.reset();
                return nodeStateChanges || nodeOrConnectionAddedOrRemoved;
            });
            if (Boolean.TRUE.equals(recompute)) {
                m_dependentNodeProperties = DependentNodeProperties.determineDependentNodeProperties(m_wfm);
            }
            return m_dependentNodeProperties;
        }

        void clearCache() {
            m_dependentNodeProperties = null;
        }

        void dispose() {
            m_wfChangesListener.removeWorkflowChangesTracker(m_tracker);
        }

    }

    /**
     * A workflow listener that clears the workflow state of a removed sub-workflow (component or metanode) within the
     * workflow it is attached to. It listens for the 'node_removed' event of this particular node (i.e. component or
     * metanode the given wfKey refers to). It also removes the workflow state for all sub-workflows contained in the
     * respective component/metanode (if there is anything cached). And it finally removes itself from the workflow it
     * is attached to.
     */
    private class NodeRemovedListenerToClearWorkflowState implements WorkflowListener {

        private final WorkflowKey m_wfKey;

        private final NodeContainer m_metanodeOrComponent;

        NodeRemovedListenerToClearWorkflowState(final WorkflowKey wfKey, final NodeContainer metanodeOrComponent) {
            m_wfKey = wfKey;
            m_metanodeOrComponent = metanodeOrComponent;
        }

        @Override
        public void workflowChanged(final WorkflowEvent e) {
            if (e.getType() == WorkflowEvent.Type.NODE_REMOVED && m_metanodeOrComponent == e.getOldValue()) {
                clearWorkflowState(k -> m_wfKey.getProjectId().equals(k.getProjectId())
                    && m_wfKey.getWorkflowId().isEqualOrParentOf(k.getWorkflowId()));
                m_metanodeOrComponent.getParent().removeListener(this);
            }

        }
    }

}

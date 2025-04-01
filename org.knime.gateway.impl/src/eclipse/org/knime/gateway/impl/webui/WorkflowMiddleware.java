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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Predicate;
import java.util.function.Supplier;

import org.knime.core.node.workflow.WorkflowAnnotation;
import org.knime.core.node.workflow.WorkflowManager;
import org.knime.core.node.workflow.WorkflowResourceCache.WorkflowResource;
import org.knime.core.util.Pair;
import org.knime.gateway.api.entity.AnnotationIDEnt;
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
import org.knime.gateway.impl.service.util.DefaultServiceUtil;
import org.knime.gateway.impl.service.util.EntityRepository;
import org.knime.gateway.impl.service.util.PatchCreator;
import org.knime.gateway.impl.service.util.PatchEntCreator;
import org.knime.gateway.impl.service.util.SimpleRepository;
import org.knime.gateway.impl.service.util.WorkflowChangesListener;
import org.knime.gateway.impl.service.util.WorkflowChangesListener.Scope;
import org.knime.gateway.impl.service.util.WorkflowChangesTracker;
import org.knime.gateway.impl.service.util.WorkflowChangesTracker.WorkflowChange;
import org.knime.gateway.impl.webui.service.commands.WorkflowCommands;
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

    private final SpaceProvidersManager m_spaceProvidersManager;

    /**
     * @param projectManager
     * @param spaceProvidersManager
     */
    public WorkflowMiddleware(final ProjectManager projectManager, final SpaceProvidersManager spaceProvidersManager) {
        m_spaceProvidersManager = spaceProvidersManager;
    }

    /**
     * Creates a new workflow snapshot entity. If there are any changes to the workflow, a new workflow entity is
     * committed to the {@link EntityRepository} and the respective snapshot id used. Otherwise the snapshot id of the
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
        return buildWorkflowSnapshotEnt(workflowEntity, m_workflowEntRepo.commit(wfKey, workflowEntity));
    }

    private static WorkflowSnapshotEnt buildWorkflowSnapshotEnt(final WorkflowEnt workflow, final String snapshotId) {
        return builder(WorkflowSnapshotEntBuilder.class).setSnapshotId(snapshotId).setWorkflow(workflow).build();
    }

    /**
     * Builds a new {@link WorkflowMonitorStateChangeEventEnt} instance. If there are any changes to the workflow, a new
     * workflow entity is committed to the {@link EntityRepository} and the respective snapshot id used. Otherwise the
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
     * @param wfKey the workflow to get the commands for
     *
     * @return the workflow commands instance
     */
    public WorkflowCommands getCommands(final WorkflowKey wfKey) {
        assert wfKey.getVersionId().isCurrentState();
        return getWorkflowState(wfKey).commands();
    }

    /**
     * Returns the {@link WorkflowChangesListener} associated with the workflow represented by the given
     * {@link WorkflowKey}.
     *
     * If called for the first time, the {@link WorkflowChangesListener} will be created. Subsequent calls will always
     * return the very same instance (per workflow).
     *
     * @param wfKey
     * @return the changes listener, never <code>null</code>
     */
    public WorkflowChangesListener getWorkflowChangesListener(final WorkflowKey wfKey) {
        return getWorkflowState(wfKey).changesListener();
    }

    /**
     * TODO
     *
     * @param wfKey
     * @return
     */
    public WorkflowChangedEventBuilder createWorkflowChangedEventBuilder(final WorkflowKey wfKey) {
        var builder = new WorkflowChangedEventBuilder(wfKey);
        getWorkflowState(wfKey).onDispose(builder::dispose);
        return builder;
    }

    /**
     * Returns the {@link WorkflowChangesListener} associated with the workflow represented by the given
     * {@link WorkflowKey} as required by the workflow monitor (it only listen for a subset of workflow changes, but
     * does it recursively).
     *
     * If called for the first time, the {@link WorkflowChangesListener} will be created. Subsequent calls will always
     * return the very same instance (per workflow).
     *
     * @param wfKey
     * @return the listener instance
     */
    // TODO event builder class?
    public WorkflowChangesListener getWorkflowChangesListenerForWorkflowMonitor(final WorkflowKey wfKey) {
        return getWorkflowState(wfKey).changesListenerForWorkflowMonitor();
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
     * Builds an {@code AnnotationIDEnt} considering the {@code WorkflowBuildContext}.
     *
     * @param wa
     * @param wfm
     * @return The annotation ID entity
     */
    public static AnnotationIDEnt buildAnnotationIDEnt(final WorkflowAnnotation wa, final WorkflowManager wfm) {
        final var buildContextBuilder = WorkflowBuildContext.builder().includeInteractionInfo(false);
        return EntityFactory.Workflow.buildAnnotationIDEnt(wa, buildContextBuilder, wfm);
    }

    /**
     * @param wfKey TODO
     * @return <code>true</code> if there is state cached for the workflow represented by the given workflow key
     */
    public boolean hasStateFor(final WorkflowKey wfKey) {
        var wfm =
            DefaultServiceUtil.getWorkflowManager(wfKey.getProjectId(), wfKey.getVersionId(), wfKey.getWorkflowId());
        return Boolean.valueOf(wfm.getWorkflowResourceCache().get2(WorkflowState.class).isPresent());
    }

    /**
     * TODO
     *
     * @param wfKey
     * @param onDispose
     */
    public void runOnWorkflowDisposal(final WorkflowKey wfKey, final Runnable onDispose) {
        getWorkflowState(wfKey).onDispose(onDispose);
    }

    /**
     * @see WorkflowMiddleware#m_workflowStateCache
     */
    private WorkflowState getWorkflowState(final WorkflowKey wfKey) {
        // TODO optimize access?
        var wfm =
            DefaultServiceUtil.getWorkflowManager(wfKey.getProjectId(), wfKey.getVersionId(), wfKey.getWorkflowId());
        return wfm.getWorkflowResourceCache().computeIfAbsent2(WorkflowState.class, () -> {
            var state = new WorkflowState(wfKey, wfm);
            state.onDispose(() -> {
                Predicate<WorkflowKey> keyFilter = k -> k.equals(wfKey);
                m_workflowEntRepo.disposeHistory(keyFilter);
                m_workflowMonitorStateEntRepo.disposeHistory(keyFilter);
            });
            return state;
        });
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
    private static final class WorkflowState implements WorkflowResource {

        private final WorkflowManager m_wfm;

        private WorkflowChangesListener m_changesListener;

        private WorkflowChangesListener m_changesListenerForWorkflowMonitor;

        private WorkflowCommands m_commands;

        private List<Runnable> m_disposeRunnables = new ArrayList<>();

        private final WorkflowKey m_wfKey;

        private WorkflowState(final WorkflowKey wfKey, final WorkflowManager wfm) {
            m_wfKey = wfKey;
            m_wfm = wfm;
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

        WorkflowCommands commands() {
            if (m_commands == null) {
                m_commands = new WorkflowCommands(m_wfKey, UNDO_AND_REDO_STACK_SIZE_PER_WORKFLOW);
            }
            return m_commands;
        }

        void onDispose(final Runnable dispose) {
            m_disposeRunnables.add(dispose);
        }

        @Override
        public void dispose() {
            if (m_changesListener != null) {
                m_changesListener.close();
            }
            if (m_changesListenerForWorkflowMonitor != null) {
                m_changesListenerForWorkflowMonitor.close();
            }
            m_disposeRunnables.forEach(Runnable::run);
        }

    }

    /**
     * TODO
     */
    public final class WorkflowChangedEventBuilder {

        private final WorkflowChangesTracker m_tracker;

        private final WorkflowChangesListener m_workflowChangesListener;

        private final WorkflowKey m_wfKey;

        private DependentNodeProperties m_dependentNodeProperties;

        private WorkflowChangedEventBuilder(final WorkflowKey wfKey) {
            m_wfKey = wfKey;
            m_workflowChangesListener = getWorkflowChangesListener(wfKey);
            m_tracker = m_workflowChangesListener.createWorkflowChangeTracker();
        }

        /**
         * Helper to create a {@link WorkflowChangedEventEnt}-instance.
         *
         * When calling this, it is assumed that {@link #buildWorkflowSnapshotEnt(WorkflowKey, Supplier)} has been
         * called at least once before for the given workflow key.
         *
         * State information (maintained by this {@link WorkflowMiddleware}-instance) is used to <br>
         * - avoid unnecessary calculations of {@link DependentNodeProperties} on workflow changes tracked by the
         * associated {@link WorkflowChangesListener} (only if interaction info is to be included)<br>
         * - include the can-undo and can-redo flags determined via {@link WorkflowCommands} (only if interaction info
         * is to be included)<br>
         * - compare the current state against an older state (i.e. entity history managed via {@link EntityRepository})
         *
         * @param patchEntCreator creator for the patch which is supplied with the {@link WorkflowChangedEventEnt}
         * @param snapshotId the latest snapshot id
         * @param includeInteractionInfo see {@link WorkflowBuildContextBuilder#includeInteractionInfo(boolean)}
         * @return <code>null</code> if there are no changes, otherwise the {@link WorkflowChangedEventEnt}
         */
        public WorkflowChangedEventEnt buildWorkflowChangedEvent(final PatchEntCreator patchEntCreator,
            final String snapshotId, final boolean includeInteractionInfo) {
            WorkflowBuildContextBuilder buildContextBuilder = WorkflowBuildContext.builder()//
                .includeInteractionInfo(includeInteractionInfo);
            final var ws = getWorkflowState(m_wfKey);
            if (includeInteractionInfo) {
                buildContextBuilder.canUndo(getCommands(m_wfKey).canUndo())//
                    .canRedo(getCommands(m_wfKey).canRedo())//
                    .setDependentNodeProperties(this::getDependentNodeProperties);
            }
            if (m_spaceProvidersManager != null) {
                buildContextBuilder.setSpaceProviderTypes(
                    m_spaceProvidersManager.getSpaceProviders(Key.of(m_wfKey.getProjectId())).getProviderTypes());
            }
            final var wfEnt = EntityFactory.Workflow.buildWorkflowEnt(ws.m_wfm, buildContextBuilder);
            var patch = m_workflowEntRepo.getChangesAndCommit(snapshotId, wfEnt, patchEntCreator).orElse(null);
            return patch == null ? null : builder(WorkflowChangedEventEntBuilder.class).setPatch(patch)
                .setSnapshotId(patchEntCreator.getLastSnapshotId()).build();
        }

        private DependentNodeProperties getDependentNodeProperties() {
            var recompute = m_dependentNodeProperties == null || m_tracker.invoke(t -> {
                var nodeStateChanges = t.hasOccurredAtLeastOne(WorkflowChange.NODE_STATE_UPDATED);
                var nodeOrConnectionAddedOrRemoved = t.hasOccurredAtLeastOne(WorkflowChange.NODE_ADDED,
                    WorkflowChange.NODE_REMOVED, WorkflowChange.CONNECTION_ADDED, WorkflowChange.CONNECTION_REMOVED);
                t.reset();
                return nodeStateChanges || nodeOrConnectionAddedOrRemoved;
            });
            if (Boolean.TRUE.equals(recompute)) {
                m_dependentNodeProperties =
                    DependentNodeProperties.determineDependentNodeProperties(getWorkflowState(m_wfKey).m_wfm);
            }
            return m_dependentNodeProperties;
        }

        /**
         * TODO
         */
        public void dispose() {
            m_workflowChangesListener.removeWorkflowChangesTracker(m_tracker);
            m_dependentNodeProperties = null;
        }

    }

}

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

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Predicate;
import java.util.function.Supplier;

import org.knime.core.node.workflow.WorkflowLock;
import org.knime.core.node.workflow.WorkflowManager;
import org.knime.core.util.Pair;
import org.knime.gateway.api.util.DependentNodeProperties;
import org.knime.gateway.api.webui.entity.WorkflowChangedEventEnt;
import org.knime.gateway.api.webui.entity.WorkflowEnt;
import org.knime.gateway.api.webui.entity.WorkflowSnapshotEnt;
import org.knime.gateway.api.webui.entity.WorkflowSnapshotEnt.WorkflowSnapshotEntBuilder;
import org.knime.gateway.api.webui.util.EntityFactory;
import org.knime.gateway.api.webui.util.WorkflowBuildContext;
import org.knime.gateway.api.webui.util.WorkflowBuildContext.WorkflowBuildContextBuilder;
import org.knime.gateway.impl.project.WorkflowProjectManager;
import org.knime.gateway.impl.service.util.DefaultServiceUtil;
import org.knime.gateway.impl.service.util.EntityRepository;
import org.knime.gateway.impl.service.util.PatchCreator;
import org.knime.gateway.impl.service.util.SimpleRepository;
import org.knime.gateway.impl.service.util.WorkflowChangesListener;
import org.knime.gateway.impl.service.util.WorkflowChangesTracker;
import org.knime.gateway.impl.service.util.WorkflowChangesTracker.WorkflowChange;
import org.knime.gateway.impl.webui.service.commands.WorkflowCommands;

/**
 * Provides utility methods to operate on a workflow represented by a {@link WorkflowKey} where the methods require to
 * keep a state per workflow (e.g. cached objects, undo & redo stack, entity history). The state per workflow is cleared
 * as soon as it's removed from memory (the actual {@link WorkflowManager}-instances are accessed via the
 * {@link WorkflowProjectManager}).
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

    private final EntityRepository<WorkflowKey, WorkflowEnt> m_entityRepo;

    private final Map<WorkflowKey, WorkflowState> m_workflowStateCache;

    private final WorkflowCommands m_commands;

    /**
     * @param workflowProjectManager
     */
    public WorkflowMiddleware(final WorkflowProjectManager workflowProjectManager) {
        m_entityRepo = new SimpleRepository<>(1, new SnapshotIdGenerator());
        m_commands = new WorkflowCommands(UNDO_AND_REDO_STACK_SIZE_PER_WORKFLOW);
        m_workflowStateCache = Collections.synchronizedMap(new HashMap<>());
        workflowProjectManager.addWorkflowProjectRemovedListener(
            projectId -> clearWorkflowState(k -> k.getProjectId().equals(projectId)));
    }

    private void clearWorkflowState(final Predicate<WorkflowKey> keyFilter) {
        m_entityRepo.disposeHistory(keyFilter);
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

<<<<<<< Updated upstream
=======
    // TODO: This does more harm than good. Do we really need it?
//    public void clearSubtreeWorkflowStates(final WorkflowKey rootKey) {
//        clearWorkflowState(key -> key.getProjectId().equals(rootKey.getProjectId())
//            && key.getWorkflowId().isInSubtreeOf(rootKey.getWorkflowId()));
//    }

>>>>>>> Stashed changes
    /**
     * Notify this class that a workflow has been disposed.
     *
     * @param wfKey the keys to clear the workflow state for
     */
    public void clearWorkflowState(final WorkflowKey wfKey) {
        clearWorkflowState(k -> k.equals(wfKey));
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
        return buildWorkflowSnapshotEnt(workflowEntity, m_entityRepo.commit(wfKey, workflowEntity));
    }

    private static WorkflowEnt buildWorkflowEntIfWorkflowHasChanged(final WorkflowManager wfm,
        final WorkflowBuildContextBuilder buildContextBuilder, final WorkflowChangesTracker tracker) {
        try (WorkflowLock lock = wfm.lock()) {
            var workflowChanged = tracker.invoke(t -> {
                if (t.hasOccurredAtLeastOne(WorkflowChange.ANY)) {
                    t.reset();
                    return true;
                } else {
                    return false;
                }
            });
            return workflowChanged ? EntityFactory.Workflow.buildWorkflowEnt(wfm, buildContextBuilder) : null;
        }
    }

    private static WorkflowSnapshotEnt buildWorkflowSnapshotEnt(final WorkflowEnt workflow, final String snapshotId) {
        return builder(WorkflowSnapshotEntBuilder.class).setSnapshotId(snapshotId).setWorkflow(workflow).build();
    }

    /**
     * @param wfKey The workflow to query
     * @return The snapshot-id of the most recent commit, or an empty optional if there are no commits yet.
     */
    public Optional<String> getLatestSnapshotId(final WorkflowKey wfKey) {
        var latestCommit = m_entityRepo.getLastCommit(wfKey);
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
     * @param wfKey
     * @return the changes listener, never <code>null</code>
     */
    public WorkflowChangesListener getWorkflowChangesListener(final WorkflowKey wfKey) {
        WorkflowState ws = getWorkflowState(wfKey);
        return ws.changesListener();
    }

    /**
     * Helper to create a {@link WorkflowChangedEventEnt}-instance.
     *
     * When calling this, it is assumed that {@link #buildWorkflowSnapshotEnt(WorkflowKey, Supplier)} has been called at
     * least once before for the given workflow key.
     *
     * State information is used to <br>
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
     * @param changes A workflow changes tracker to determine if and how to build the event
     * @param componentPropertiesProvider Get properties from components
     * @return <code>null</code> if there are no changes, otherwise the {@link WorkflowChangedEventEnt}
     */
    public WorkflowChangedEventEnt buildWorkflowChangedEvent(final WorkflowKey wfKey,
        final PatchCreator<WorkflowChangedEventEnt> patchEntCreator, final String snapshotId,
        final boolean includeInteractionInfo, final WorkflowChangesTracker changes,
        final ComponentPropertiesProvider componentPropertiesProvider) {
        WorkflowBuildContextBuilder buildContextBuilder = WorkflowBuildContext.builder()//
            .includeInteractionInfo(includeInteractionInfo)//
            .setIsLinkTypePredicate(componentPropertiesProvider::isNodeLinkTypeChangable);
        WorkflowState ws = getWorkflowState(wfKey);
        if (includeInteractionInfo) {
            buildContextBuilder.canUndo(m_commands.canUndo(wfKey))//
                .canRedo(m_commands.canRedo(wfKey))//
                .setDependentNodeProperties(() -> getDependentNodeProperties(wfKey));
        }
        WorkflowEnt wfEnt = buildWorkflowEntIfWorkflowHasChanged(ws.m_wfm, buildContextBuilder, changes);
        if (wfEnt == null) {
            // no change
            return null;
        } else {
            return m_entityRepo.getChangesAndCommit(snapshotId, wfEnt, patchEntCreator).orElse(null);
        }
    }

    /**
     * @param wfKey
     * @return <code>true</code> if there is state cached for the workflow represented by the given workflow key
     */
    public boolean hasStateFor(final WorkflowKey wfKey) {
        return m_workflowStateCache.containsKey(wfKey);
    }

    /**
     * Obtain the dependent node properties for the given workflow. Recalculate if the the workflow has pending
     * changes or use cached data otherwise.
     * @param wfKey The workflow key characterising the workflow
     * @return recent {@code DependentNodeProperties}
     */
    private DependentNodeProperties getDependentNodeProperties(final WorkflowKey wfKey) {
        return getWorkflowState(wfKey).getDependentNodeProperties();
    }

    private WorkflowState getWorkflowState(final WorkflowKey wfKey) {
        return m_workflowStateCache.computeIfAbsent(wfKey, WorkflowState::new);
    }

    private static class SnapshotIdGenerator implements Supplier<String> {

        private AtomicLong m_count = new AtomicLong();

        @Override
        public String get() {
            return Long.toString(m_count.getAndIncrement());
        }

    }

    /**
     * Helper class that summarizes all the things that are associated with a single workflow.
     */
    private static final class WorkflowState {

        private final WorkflowKey m_wfKey;

        private final WorkflowManager m_wfm;

        private CachedDependentNodeProperties m_depNodeProperties;

        private WorkflowChangesListener m_changesListener;

        private WorkflowState(final WorkflowKey wfKey)  {
            m_wfKey = wfKey;
            m_wfm = DefaultServiceUtil.getWorkflowManager(m_wfKey.getProjectId(), m_wfKey.getWorkflowId());
        }

        DependentNodeProperties getDependentNodeProperties() {
            if (m_depNodeProperties == null) {
                m_depNodeProperties = new CachedDependentNodeProperties(m_wfm, m_changesListener);
            }
            return m_depNodeProperties.get();
        }

        WorkflowChangesListener changesListener() {
            if (m_changesListener == null) {
                m_changesListener = new WorkflowChangesListener(m_wfm);
            }
            return m_changesListener;
        }

        void dispose() {
            if (m_depNodeProperties != null) {
                m_depNodeProperties.dispose();
            }
            if (m_changesListener != null) {
                m_changesListener.close();
            }
        }

    }

    /**
     * Wrapper around {@link DependentNodeProperties} that tracks the given workflow for changes affecting
     * the dependent node properties and recomputes them if needed.
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

        public DependentNodeProperties get() {
            var recompute = m_tracker.invoke(t -> {
                var nodeStateChanges = t.hasOccurredAtLeastOne(WorkflowChange.NODE_STATE_UPDATED);
                var nodeOrConnectionAddedOrRemoved = t.hasOccurredAtLeastOne(WorkflowChange.NODE_ADDED,
                    WorkflowChange.NODE_REMOVED, WorkflowChange.CONNECTION_ADDED, WorkflowChange.CONNECTION_REMOVED);
                t.reset();
                return m_dependentNodeProperties == null || nodeStateChanges || nodeOrConnectionAddedOrRemoved;
            });
            if (Boolean.TRUE.equals(recompute)) {
                m_dependentNodeProperties = DependentNodeProperties.determineDependentNodeProperties(m_wfm);
            }
            return m_dependentNodeProperties;
        }

        void dispose() {
            m_wfChangesListener.removeWorkflowChangesTracker(m_tracker);
        }
    }

}

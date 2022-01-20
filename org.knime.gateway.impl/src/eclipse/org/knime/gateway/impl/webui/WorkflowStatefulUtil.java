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
import static org.knime.gateway.api.webui.util.EntityBuilderUtil.buildWorkflowEnt;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Supplier;

import org.knime.core.node.workflow.WorkflowLock;
import org.knime.core.node.workflow.WorkflowManager;
import org.knime.core.util.Pair;
import org.knime.gateway.api.util.DependentNodeProperties;
import org.knime.gateway.api.webui.entity.WorkflowChangedEventEnt;
import org.knime.gateway.api.webui.entity.WorkflowCommandEnt;
import org.knime.gateway.api.webui.entity.WorkflowEnt;
import org.knime.gateway.api.webui.entity.WorkflowSnapshotEnt;
import org.knime.gateway.api.webui.entity.WorkflowSnapshotEnt.WorkflowSnapshotEntBuilder;
import org.knime.gateway.api.webui.service.util.ServiceExceptions.NodeNotFoundException;
import org.knime.gateway.api.webui.service.util.ServiceExceptions.NotASubWorkflowException;
import org.knime.gateway.api.webui.service.util.ServiceExceptions.OperationNotAllowedException;
import org.knime.gateway.api.webui.util.WorkflowBuildContext;
import org.knime.gateway.api.webui.util.WorkflowBuildContext.WorkflowBuildContextBuilder;
import org.knime.gateway.impl.project.WorkflowProjectManager;
import org.knime.gateway.impl.service.util.EntityRepository;
import org.knime.gateway.impl.service.util.PatchCreator;
import org.knime.gateway.impl.service.util.SimpleRepository;
import org.knime.gateway.impl.service.util.WorkflowChangesListener;
import org.knime.gateway.impl.service.util.WorkflowChangesListener.WorkflowChanges;
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
 */
public final class WorkflowStatefulUtil {

    private static final WorkflowStatefulUtil INSTANCE = new WorkflowStatefulUtil();

    /**
     * Determines the number of commands per workflow kept in the undo and redo stacks.
     */
    private static final int UNDO_AND_REDO_STACK_SIZE_PER_WORKFLOW = 50;

    private final EntityRepository<WorkflowKey, WorkflowEnt> m_entityRepo;

    private final Map<WorkflowKey, WorkflowState> m_workflowCache;

    private final WorkflowCommands m_commands;

    /**
     * @return the singleton instance
     */
    public static WorkflowStatefulUtil getInstance() {
        return INSTANCE;
    }

    private WorkflowStatefulUtil() {
        m_entityRepo = new SimpleRepository<>(1, new SnapshotIdGenerator());
        m_commands = new WorkflowCommands(UNDO_AND_REDO_STACK_SIZE_PER_WORKFLOW);
        m_workflowCache = Collections.synchronizedMap(new HashMap<>());
        WorkflowProjectManager.addWorkflowProjectRemovedListener(this::clearWorkflowState);
    }

    private void clearWorkflowState(final String projectId) {
        m_entityRepo.disposeHistory(k -> k.getProjectId().equals(projectId));
        m_commands.disposeUndoAndRedoStacks(projectId);
        m_workflowCache.entrySet().removeIf(e -> {
            if (e.getKey().getProjectId().equals(projectId)) {
                if (e.getValue().m_changesListener != null) {
                    e.getValue().m_changesListener.close();
                }
                return true;
            } else {
                return false;
            }
        });
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
        var workflowState = workflowState(wfKey);
        var workflowEntity = buildWorkflowEnt(workflowState.m_wfm, buildContextSupplier.get());
        if (workflowEntity == null) {
            // no workflow change -> check most recent commit
            Pair<String, WorkflowEnt> last = m_entityRepo.getLastCommit(wfKey).orElse(null);
            if (last != null) {
                return buildWorkflowSnapshotEnt(last.getSecond(), last.getFirst());
            }

            // no commit, yet
            workflowEntity = buildWorkflowEnt(workflowState.m_wfm, buildContextSupplier.get());
        }

        // commit the new workflow entity and return the snapshot
        return buildWorkflowSnapshotEnt(workflowEntity, m_entityRepo.commit(wfKey, workflowEntity));
    }

    private static WorkflowEnt buildWorkflowEntIfWorkflowHasChanged(final WorkflowManager wfm,
        final Supplier<WorkflowBuildContextBuilder> buildContextSupplier, final WorkflowChanges c) {
        try (WorkflowLock lock = wfm.lock()) {
            if (c.anyChange()) {
                var ent = buildWorkflowEnt(wfm, buildContextSupplier.get());
                c.reset();
                return ent;
            }
            return null;
        }
    }

    private static WorkflowSnapshotEnt buildWorkflowSnapshotEnt(final WorkflowEnt workflow, final String snapshotId) {
        return builder(WorkflowSnapshotEntBuilder.class).setSnapshotId(snapshotId).setWorkflow(workflow).build();
    }

    /**
     * See {@link WorkflowCommands#execute(WorkflowKey, WorkflowCommandEnt)}.
     *
     * @param wfKey
     * @param command
     * @throws OperationNotAllowedException
     * @throws NotASubWorkflowException
     * @throws NodeNotFoundException
     */
    public void executeCommand(final WorkflowKey wfKey, final WorkflowCommandEnt command)
        throws OperationNotAllowedException, NotASubWorkflowException, NodeNotFoundException {
        m_commands.execute(wfKey, command);
    }

    /**
     * See {@link WorkflowCommands#undo(WorkflowKey)}.
     *
     * @param wfKey
     * @throws OperationNotAllowedException
     */
    public void undoCommand(final WorkflowKey wfKey) throws OperationNotAllowedException {
        m_commands.undo(wfKey);
    }

    /**
     * See {@link WorkflowCommands#redo(WorkflowKey)}.
     *
     * @param wfKey
     * @throws OperationNotAllowedException
     */
    public void redoCommand(final WorkflowKey wfKey) throws OperationNotAllowedException {
        m_commands.redo(wfKey);
    }

    /**
     * See {@link WorkflowCommands#canUndo(WorkflowKey)}.
     *
     * @param wfKey
     * @return
     */
    public boolean canUndoCommand(final WorkflowKey wfKey) {
        return m_commands.canUndo(wfKey);
    }

    /**
     * See {@link WorkflowCommands#canRedo(WorkflowKey)}.
     *
     * @param wfKey
     * @return
     */
    public boolean canRedoCommand(final WorkflowKey wfKey) {
        return m_commands.canRedo(wfKey);
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
        WorkflowState ws = workflowState(wfKey);
        return ws.changesListener();
    }

    /**
     * Helper to create a {@link WorkflowChangedEventEnt}-instance.
     *
     * When calling this, it is assumed that {@link #buildWorkflowSnapshotEntOrGetFromCache(WorkflowKey, Supplier)} has
     * been called at least once before for the given workflow key.
     *
     * State information is used to <br>
     * - avoid unnecessary calculations of {@link NodeSuccessors} and {@link DependentNodeProperties} on workflow
     * changes tracked by the associated {@link WorkflowChangesListener} (only if interaction info is to be
     * included)<br>
     * - include the can-undo and can-redo flags determined via {@link WorkflowCommands} (only if interaction info is to
     * be included)<br>
     * - compare the current state against an older state (i.e. entity history managed via {@link EntityRepository})
     *
     * @param wfKey the workflow to create the changed event for
     * @param patchEntCreator creator for the patch which is supplied with the {@link WorkflowChangedEventEnt}
     * @param snapshotId the latest snapshot id
     * @param includeInteractioInfo see {@link WorkflowBuildContextBuilder#includeInteractionInfo(boolean)}
     * @return <code>null</code> if there are no changes, otherwise the {@link WorkflowChangedEventEnt}
     */
    public WorkflowChangedEventEnt buildWorkflowChangedEvent(final WorkflowKey wfKey,
        final PatchCreator<WorkflowChangedEventEnt> patchEntCreator, final String snapshotId,
        final boolean includeInteractioInfo) {
        WorkflowBuildContextBuilder buildContextBuilder = WorkflowBuildContext.builder()//
            .includeInteractionInfo(includeInteractioInfo);
        WorkflowState ws = workflowState(wfKey);
        WorkflowChanges changes = ws.changesListener().getChanges();
        if (includeInteractioInfo) {
            buildContextBuilder.canUndo(canUndoCommand(wfKey))//
                .canRedo(canRedoCommand(wfKey))//
                .setDependentNodeProperties(() -> getDependentNodeProperties(wfKey, changes));
        }
        WorkflowEnt wfEnt = buildWorkflowEntIfWorkflowHasChanged(ws.m_wfm, () -> buildContextBuilder, changes);
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
        return m_workflowCache.containsKey(wfKey);
    }

    private DependentNodeProperties getDependentNodeProperties(final WorkflowKey wfKey, final WorkflowChanges changes) {
        // dependent node properties are only re-calculated if there are respective changes
        // otherwise a cached instance is used
        WorkflowState ws = workflowState(wfKey);
        if (ws.m_depNodeProperties == null || changes.nodeStateChanges() || changes.nodeOrConnectionAddedOrRemoved()) {
            ws.m_depNodeProperties = DependentNodeProperties.determineDependentNodeProperties(ws.m_wfm);
        }
        return ws.m_depNodeProperties;
    }

    private WorkflowState workflowState(final WorkflowKey wfKey) {
        return m_workflowCache.computeIfAbsent(wfKey, k -> {
            try {
                return new WorkflowState(WorkflowUtil.getWorkflowManager(wfKey));
            } catch (NodeNotFoundException | NotASubWorkflowException ex) {
                throw new IllegalStateException(ex);
            }
        });
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

        private DependentNodeProperties m_depNodeProperties;

        private WorkflowChangesListener m_changesListener;

        private final WorkflowManager m_wfm;

        private WorkflowState(final WorkflowManager wfm) {
            m_wfm = wfm;
        }

        WorkflowChangesListener changesListener() {
            if (m_changesListener == null) {
                m_changesListener = new WorkflowChangesListener(m_wfm);
            }
            return m_changesListener;
        }

    }

}

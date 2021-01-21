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
 *   Jan 20, 2021 (hornm): created
 */
package org.knime.gateway.impl.webui.service.operations;

import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedDeque;

import org.knime.gateway.api.webui.entity.TranslateOperationEnt;
import org.knime.gateway.api.webui.entity.WorkflowOperationEnt;
import org.knime.gateway.api.webui.service.util.ServiceExceptions.NodeNotFoundException;
import org.knime.gateway.api.webui.service.util.ServiceExceptions.NotASubWorkflowException;
import org.knime.gateway.api.webui.service.util.ServiceExceptions.OperationNotAllowedException;
import org.knime.gateway.impl.webui.service.WorkflowKey;

/**
 * Allows one to apply, undo and redo workflow operations for workflows. It accordingly keeps undo- and redo-stacks for
 * each workflow an operation is been applied on. Individual types of workflow operations are represented by the
 * implementations of {@link WorkflowOperationEnt}, i.e. different kind of entities of workflow operations.
 *
 * This is API that might/should be moved closer to the core eventually.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
public final class WorkflowOperations {

    private final Map<WorkflowKey, Deque<WorkflowOperation<? extends WorkflowOperationEnt>>> m_redoStacks;

    private final Map<WorkflowKey, Deque<WorkflowOperation<? extends WorkflowOperationEnt>>> m_undoStacks;

    private final int m_maxNumUndoAndRedoOperationsPerWorkflow;

    /**
     * Creates a new instance with initially empty undo- and redo-stacks.
     *
     * @param maxNumUndoAndRedoOperationsPerWorkflow the maximum size of undo- and redo-stack for each workflow
     */
    public WorkflowOperations(final int maxNumUndoAndRedoOperationsPerWorkflow) {
        m_maxNumUndoAndRedoOperationsPerWorkflow = maxNumUndoAndRedoOperationsPerWorkflow;
        m_redoStacks = Collections.synchronizedMap(new HashMap<>());
        m_undoStacks = Collections.synchronizedMap(new HashMap<>());
    }

    /**
     * Applies the given workflow operation, represented by the operation entity, to a workflow referenced by the given
     * {@link WorkflowKey}.
     *
     * @param <E> the type of workflow operation
     * @param wfKey reference to the workflow to apply the operation on
     * @param operation the workflow operation entity to apply
     *
     * @throws OperationNotAllowedException if the operation couldn't be applied
     * @throws NotASubWorkflowException if no sub-workflow (component, metanode) is referenced
     * @throws NodeNotFoundException if the reference doesn't point to a workflow
     */
    @SuppressWarnings("unchecked")
    public <E extends WorkflowOperationEnt> void apply(final WorkflowKey wfKey, final E operation)
        throws OperationNotAllowedException, NotASubWorkflowException, NodeNotFoundException {
        WorkflowOperation<E> op;
        if (operation instanceof TranslateOperationEnt) {
            op = (WorkflowOperation<E>)new Translate();
        } else {
            throw new OperationNotAllowedException(
                "Operation of type " + operation.getClass().getSimpleName() + " cannot be applied. Unknown operation.");
        }
        op.apply(wfKey, operation);
        addOperationToUndoStack(wfKey, op);
        clearRedoStack(wfKey);
    }

    /**
     * @param wfKey reference to the workflow to check the undo-state for
     * @return whether there is at least one operation on the undo-stack
     */
    public boolean canUndo(final WorkflowKey wfKey) {
        Deque<WorkflowOperation<? extends WorkflowOperationEnt>> undoStack = m_undoStacks.get(wfKey);
        return undoStack != null && !undoStack.isEmpty();
    }

    /**
     * @param wfKey reference to the workflow to undo the last operation for
     * @throws OperationNotAllowedException if there is no operation to be undone
     */
    public void undo(final WorkflowKey wfKey) throws OperationNotAllowedException {
        WorkflowOperation<? extends WorkflowOperationEnt> op = moveOperationFromUndoToRedoStack(wfKey);
        if (op != null) {
            op.undo();
        } else {
            throw new OperationNotAllowedException("No operation to undo");
        }
    }

    /**
     * @param wfKey reference to the workflow to check the redo-state for
     * @return whether there is at least one operation on the redo-stack
     */
    public boolean canRedo(final WorkflowKey wfKey) {
        Deque<WorkflowOperation<? extends WorkflowOperationEnt>> redoStack = m_redoStacks.get(wfKey);
        return redoStack != null && !redoStack.isEmpty();
    }

    /**
     * @param wfKey reference to the workflow to redo the last operation for
     * @throws OperationNotAllowedException if there is no operation to be redone
     */
    public void redo(final WorkflowKey wfKey) throws OperationNotAllowedException {
        WorkflowOperation<? extends WorkflowOperationEnt> op = moveOperationFromRedoToUndoStack(wfKey);
        if (op != null) {
            op.redo();
        } else {
            throw new OperationNotAllowedException("No operation to redo");
        }
    }

    /**
     * Removes all operations from the undo- and redo-stacks for all workflows of a workflow project referenced by its
     * project-id.
     *
     * @param projectId the project-id of the workflow to clear all stacks for
     */
    public void disposeUndoAndRedoStacks(final String projectId) {
        m_undoStacks.entrySet().removeIf(e -> e.getKey().getProjectId().equals(projectId));
        m_redoStacks.entrySet().removeIf(e -> e.getKey().getProjectId().equals(projectId));
    }

    private void addOperationToUndoStack(final WorkflowKey wfKey,
        final WorkflowOperation<? extends WorkflowOperationEnt> op) {
        if (op == null) {
            return;
        }
        Deque<WorkflowOperation<? extends WorkflowOperationEnt>> stack =
            m_undoStacks.computeIfAbsent(wfKey, p -> new ConcurrentLinkedDeque<>());
        addAndEnsureMaxSize(stack, op);
    }

    private void addOperationToRedoStack(final WorkflowKey wfKey,
        final WorkflowOperation<? extends WorkflowOperationEnt> op) {
        if (op == null) {
            return;
        }
        Deque<WorkflowOperation<? extends WorkflowOperationEnt>> stack =
            m_redoStacks.computeIfAbsent(wfKey, p -> new ConcurrentLinkedDeque<>());
        addAndEnsureMaxSize(stack, op);
    }

    private <T> void addAndEnsureMaxSize(final Deque<T> stack, final T obj) {
        stack.addFirst(obj);
        if (stack.size() > m_maxNumUndoAndRedoOperationsPerWorkflow) {
            stack.removeLast();
        }
    }

    private void clearRedoStack(final WorkflowKey wfKey) {
        Deque<WorkflowOperation<? extends WorkflowOperationEnt>> stack = m_redoStacks.get(wfKey);
        if (stack != null) {
            stack.clear();
        }
    }

    private WorkflowOperation<? extends WorkflowOperationEnt>
        moveOperationFromRedoToUndoStack(final WorkflowKey wfKey) {
        Deque<WorkflowOperation<? extends WorkflowOperationEnt>> redoStack = m_redoStacks.get(wfKey);
        WorkflowOperation<? extends WorkflowOperationEnt> op = redoStack != null ? redoStack.poll() : null;
        addOperationToUndoStack(wfKey, op);
        return op;
    }

    private WorkflowOperation<? extends WorkflowOperationEnt>
        moveOperationFromUndoToRedoStack(final WorkflowKey wfKey) {
        Deque<WorkflowOperation<? extends WorkflowOperationEnt>> undoStack = m_undoStacks.get(wfKey);
        WorkflowOperation<? extends WorkflowOperationEnt> op = undoStack != null ? undoStack.poll() : null;
        addOperationToRedoStack(wfKey, op);
        return op;
    }

    /**
     * For testing purposes only!
     *
     * @return
     */
    int getUndoStackSize(final WorkflowKey wfKey) {
        Deque<WorkflowOperation<? extends WorkflowOperationEnt>> undoStack = m_undoStacks.get(wfKey);
        return undoStack == null ? -1 : undoStack.size();
    }

    /**
     * For testing purposes only!
     *
     * @return
     */
    int getRedoStackSize(final WorkflowKey wfKey) {
        Deque<WorkflowOperation<? extends WorkflowOperationEnt>> redoStack = m_redoStacks.get(wfKey);
        return redoStack == null ? -1 : redoStack.size();
    }

}

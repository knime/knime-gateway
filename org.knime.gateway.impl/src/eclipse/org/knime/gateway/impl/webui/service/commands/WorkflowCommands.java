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
package org.knime.gateway.impl.webui.service.commands;

import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentLinkedDeque;

import org.knime.core.util.Pair;
import org.knime.gateway.api.webui.entity.AddNodeCommandEnt;
import org.knime.gateway.api.webui.entity.CollapseCommandEnt;
import org.knime.gateway.api.webui.entity.CommandResultEnt;
import org.knime.gateway.api.webui.entity.ConnectCommandEnt;
import org.knime.gateway.api.webui.entity.DeleteCommandEnt;
import org.knime.gateway.api.webui.entity.ExpandCommandEnt;
import org.knime.gateway.api.webui.entity.TranslateCommandEnt;
import org.knime.gateway.api.webui.entity.UpdateComponentOrMetanodeNameCommandEnt;
import org.knime.gateway.api.webui.entity.WorkflowCommandEnt;
import org.knime.gateway.api.webui.service.util.ServiceExceptions.NodeNotFoundException;
import org.knime.gateway.api.webui.service.util.ServiceExceptions.NotASubWorkflowException;
import org.knime.gateway.api.webui.service.util.ServiceExceptions.OperationNotAllowedException;
import org.knime.gateway.impl.service.util.TrackingSemaphore;
import org.knime.gateway.impl.webui.WorkflowKey;
import org.knime.gateway.impl.webui.WorkflowStatefulUtil;

/**
 * Allows one to execute, undo and redo workflow commands for workflows. Individual commands are assumed to be executed
 * sequentially. It accordingly keeps undo- and redo-stacks for each workflow a command has been executed on.
 * Individual types of workflow commands are represented by the implementations of {@link WorkflowCommandEnt},
 * i.e. different kind of entities of workflow commands.
 *
 * This is API that might/should be moved closer to the core eventually.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
public final class WorkflowCommands {

    private final Map<WorkflowKey, Deque<WorkflowCommand>> m_redoStacks;

    private final Map<WorkflowKey, Deque<WorkflowCommand>> m_undoStacks;

    private final int m_maxNumUndoAndRedoCommandsPerWorkflow;

    /**
     * Creates a new instance with initially empty undo- and redo-stacks.
     *
     * @param maxNumUndoAndRedoCommandsPerWorkflow the maximum size of undo- and redo-stack for each workflow
     */
    public WorkflowCommands(final int maxNumUndoAndRedoCommandsPerWorkflow) {
        m_maxNumUndoAndRedoCommandsPerWorkflow = maxNumUndoAndRedoCommandsPerWorkflow;
        m_redoStacks = Collections.synchronizedMap(new HashMap<>());
        m_undoStacks = Collections.synchronizedMap(new HashMap<>());
    }

    /**
     * Executes the given workflow command, represented by the command entity, to a workflow referenced by the given
     * {@link WorkflowKey}.
     *
     * @param <E> the type of workflow command
     * @param wfKey reference to the workflow to execute the command for
     * @param commandEnt the workflow command entity to execute
     *
     * @return The instance of the executed command
     *
     * @throws OperationNotAllowedException if the command couldn't be executed
     * @throws NotASubWorkflowException if no sub-workflow (component, metanode) is referenced
     * @throws NodeNotFoundException if the reference doesn't point to a workflow
     */
    @SuppressWarnings("unchecked")
    public <E extends WorkflowCommandEnt> CommandResultEnt execute(final WorkflowKey wfKey, final E commandEnt)
        throws OperationNotAllowedException, NotASubWorkflowException, NodeNotFoundException {
        WorkflowCommand command;
        if (commandEnt instanceof TranslateCommandEnt) {
            command = new Translate(wfKey, (TranslateCommandEnt)commandEnt);
        } else if (commandEnt instanceof DeleteCommandEnt) {
            command = new Delete(wfKey, (DeleteCommandEnt)commandEnt);
        } else if (commandEnt instanceof ConnectCommandEnt) {
            command = new Connect(wfKey, (ConnectCommandEnt)commandEnt);
        } else if (commandEnt instanceof AddNodeCommandEnt) {
            command = new AddNode(wfKey, (AddNodeCommandEnt)commandEnt);
        } else if(commandEnt instanceof UpdateComponentOrMetanodeNameCommandEnt) {
            command = new UpdateComponentOrMetanodeNameCommand(wfKey, (UpdateComponentOrMetanodeNameCommandEnt)commandEnt);
        } else if (commandEnt instanceof CollapseCommandEnt) {
            command = new CollapseTeeing(wfKey, (CollapseCommandEnt)commandEnt);
        } else if (commandEnt instanceof ExpandCommandEnt) {
            command = new ExpandTeeing(wfKey, (ExpandCommandEnt)commandEnt);
        } else {
            throw new OperationNotAllowedException(
                "Command of type " + commandEnt.getClass().getSimpleName() + " cannot be executed. Unknown command.");
        }

        Optional<TrackingSemaphore> eventSyncSemaphore = Optional.empty();
        if (command.providesResult()) {
            eventSyncSemaphore = Optional.of(new TrackingSemaphore(command.getTrackedEvent().orElseThrow(), wfKey));
        }
        if (command.execute()) {
            addCommandToUndoStack(wfKey, command);
            clearRedoStack(wfKey);
        }
        if (eventSyncSemaphore.isPresent()) {
            eventSyncSemaphore.get().acquire();
        }

        return command.getResult().map(r -> {
            var latestCommit = WorkflowStatefulUtil.getInstance().getEntityRepository().getLastCommit(wfKey);
            var latestSnapshotId = latestCommit.map(Pair::getFirst).orElse(null);
            return r.buildEntity(latestSnapshotId);
        }).orElse(null);
    }

    /**
     * @param wfKey reference to the workflow to check the undo-state for
     * @return whether there is at least one command on the undo-stack
     */
    public boolean canUndo(final WorkflowKey wfKey) {
        var undoStack = m_undoStacks.get(wfKey);
        return undoStack != null && !undoStack.isEmpty() && undoStack.peek().canUndo();
    }

    /**
     * @param wfKey reference to the workflow to undo the last command for
     * @throws OperationNotAllowedException if there is no command to be undone
     */
    public void undo(final WorkflowKey wfKey) throws OperationNotAllowedException {
        WorkflowCommand op = moveCommandFromUndoToRedoStack(wfKey);
        if (op != null) {
            op.undo();
        } else {
            throw new OperationNotAllowedException("No command to undo");
        }
    }

    /**
     * @param wfKey reference to the workflow to check the redo-state for
     * @return whether there is at least one command on the redo-stack
     */
    public boolean canRedo(final WorkflowKey wfKey) {
        var redoStack = m_redoStacks.get(wfKey);
        return redoStack != null && !redoStack.isEmpty() && redoStack.peek().canRedo();
    }

    /**
     * @param wfKey reference to the workflow to redo the last command for
     * @throws OperationNotAllowedException if there is no command to be redone
     */
    public void redo(final WorkflowKey wfKey) throws OperationNotAllowedException {
        WorkflowCommand op = moveCommandFromRedoToUndoStack(wfKey);
        if (op != null) {
            op.redo();
        } else {
            throw new OperationNotAllowedException("No command to redo");
        }
    }

    /**
     * Removes all commands from the undo- and redo-stacks for all workflows of a workflow project referenced by its
     * project-id.
     *
     * @param projectId the project-id of the workflow to clear all stacks for
     */
    public void disposeUndoAndRedoStacks(final String projectId) {
        m_undoStacks.entrySet().removeIf(e -> e.getKey().getProjectId().equals(projectId));
        m_redoStacks.entrySet().removeIf(e -> e.getKey().getProjectId().equals(projectId));
    }

    private void addCommandToUndoStack(final WorkflowKey wfKey,
        final WorkflowCommand op) {
        if (op == null) {
            return;
        }
        var stack = m_undoStacks.computeIfAbsent(wfKey, p -> new ConcurrentLinkedDeque<>());
        addAndEnsureMaxSize(stack, op);
    }

    private void addCommandToRedoStack(final WorkflowKey wfKey,
        final WorkflowCommand op) {
        if (op == null) {
            return;
        }
        var stack = m_redoStacks.computeIfAbsent(wfKey, p -> new ConcurrentLinkedDeque<>());
        addAndEnsureMaxSize(stack, op);
    }

    private <T> void addAndEnsureMaxSize(final Deque<T> stack, final T obj) {
        stack.addFirst(obj);
        if (stack.size() > m_maxNumUndoAndRedoCommandsPerWorkflow) {
            stack.removeLast();
        }
    }

    private void clearRedoStack(final WorkflowKey wfKey) {
        var stack = m_redoStacks.get(wfKey);
        if (stack != null) {
            stack.clear();
        }
    }

    private WorkflowCommand moveCommandFromRedoToUndoStack(final WorkflowKey wfKey) {
        var redoStack = m_redoStacks.get(wfKey);
        var op = redoStack != null ? redoStack.poll() : null;
        addCommandToUndoStack(wfKey, op);
        return op;
    }

    private WorkflowCommand moveCommandFromUndoToRedoStack(final WorkflowKey wfKey) {
        var undoStack = m_undoStacks.get(wfKey);
        var op = undoStack != null ? undoStack.poll() : null;
        addCommandToRedoStack(wfKey, op);
        return op;
    }

    /**
     * For testing purposes only!
     *
     * @return
     */
    int getUndoStackSize(final WorkflowKey wfKey) {
        var undoStack = m_undoStacks.get(wfKey);
        return undoStack == null ? -1 : undoStack.size();
    }

    /**
     * For testing purposes only!
     *
     * @return
     */
    int getRedoStackSize(final WorkflowKey wfKey) {
        var redoStack = m_redoStacks.get(wfKey);
        return redoStack == null ? -1 : redoStack.size();
    }

}

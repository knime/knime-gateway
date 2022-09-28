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

import java.util.Deque;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Predicate;

import org.knime.gateway.api.webui.entity.AddNodeCommandEnt;
import org.knime.gateway.api.webui.entity.AddPortCommandEnt;
import org.knime.gateway.api.webui.entity.CollapseCommandEnt;
import org.knime.gateway.api.webui.entity.CommandResultEnt;
import org.knime.gateway.api.webui.entity.ConnectCommandEnt;
import org.knime.gateway.api.webui.entity.CopyCommandEnt;
import org.knime.gateway.api.webui.entity.CutCommandEnt;
import org.knime.gateway.api.webui.entity.DeleteCommandEnt;
import org.knime.gateway.api.webui.entity.ExpandCommandEnt;
import org.knime.gateway.api.webui.entity.PasteCommandEnt;
import org.knime.gateway.api.webui.entity.RemovePortCommandEnt;
import org.knime.gateway.api.webui.entity.TranslateCommandEnt;
import org.knime.gateway.api.webui.entity.UpdateComponentOrMetanodeNameCommandEnt;
import org.knime.gateway.api.webui.entity.WorkflowCommandEnt;
import org.knime.gateway.api.webui.service.util.ServiceExceptions.NodeNotFoundException;
import org.knime.gateway.api.webui.service.util.ServiceExceptions.NotASubWorkflowException;
import org.knime.gateway.api.webui.service.util.ServiceExceptions.OperationNotAllowedException;
import org.knime.gateway.impl.service.util.WorkflowChangeWaiter;
import org.knime.gateway.impl.service.util.WorkflowChangesTracker.WorkflowChange;
import org.knime.gateway.impl.webui.WorkflowKey;
import org.knime.gateway.impl.webui.WorkflowMiddleware;

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

    private final Map<WorkflowKey, CommandStack> m_redoStacks;

    private final Map<WorkflowKey, CommandStack> m_undoStacks;

    private final int m_maxNumUndoAndRedoCommandsPerWorkflow;

    private final WorkflowMiddleware m_workflowMiddleware;

    private WorkflowCommand m_workflowCommandForTesting;

    /**
     * Creates a new instance with initially empty undo- and redo-stacks.
     *
     * @param maxNumUndoAndRedoCommandsPerWorkflow the maximum size of undo- and redo-stack for each workflow
     * @param workflowMiddleware
     */
    public WorkflowCommands(final int maxNumUndoAndRedoCommandsPerWorkflow,
        final WorkflowMiddleware workflowMiddleware) {
        m_maxNumUndoAndRedoCommandsPerWorkflow = maxNumUndoAndRedoCommandsPerWorkflow;
        m_workflowMiddleware = workflowMiddleware;
        m_redoStacks = new HashMap<>();
        m_undoStacks = new HashMap<>();
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
    public <E extends WorkflowCommandEnt> CommandResultEnt execute(final WorkflowKey wfKey,
        final E commandEnt) throws OperationNotAllowedException, NotASubWorkflowException, NodeNotFoundException {
        var command = createWorkflowCommand(commandEnt);

        var hasResult = hasCommandResult(wfKey, command);
        WorkflowChangeWaiter wfChangeWaiter = null;
        if (hasResult) {
            wfChangeWaiter = prepareCommandResult(wfKey, command).orElse(null);
        }
        executeCommandAndModifyCommandStacks(wfKey, command);
        return hasResult ? waitForCommandResult(wfKey, command, wfChangeWaiter) : null;
    }

    @SuppressWarnings("java:S1541")
    private <E extends WorkflowCommandEnt> WorkflowCommand createWorkflowCommand(final E commandEnt)
        throws OperationNotAllowedException {
        WorkflowCommand command;
        if (commandEnt instanceof TranslateCommandEnt) {
            command = new Translate((TranslateCommandEnt)commandEnt);
        } else if (commandEnt instanceof DeleteCommandEnt) {
            command = new Delete((DeleteCommandEnt)commandEnt, m_workflowMiddleware);
        } else if (commandEnt instanceof ConnectCommandEnt) {
            command = new Connect((ConnectCommandEnt)commandEnt);
        } else if (commandEnt instanceof AddNodeCommandEnt) {
            command = new AddNode((AddNodeCommandEnt)commandEnt);
        } else if (commandEnt instanceof UpdateComponentOrMetanodeNameCommandEnt) {
            command = new UpdateComponentOrMetanodeNameCommand((UpdateComponentOrMetanodeNameCommandEnt)commandEnt);
        } else if (commandEnt instanceof CollapseCommandEnt) {
            command = new Collapse((CollapseCommandEnt)commandEnt, m_workflowMiddleware);
        } else if (commandEnt instanceof ExpandCommandEnt) {
            command = new Expand((ExpandCommandEnt)commandEnt, m_workflowMiddleware);
        } else if (commandEnt instanceof AddPortCommandEnt) {
          command = new AddPort((AddPortCommandEnt)commandEnt);
        } else if (commandEnt instanceof RemovePortCommandEnt) {
            command = new RemovePort((RemovePortCommandEnt)commandEnt);
        } else if (commandEnt instanceof CopyCommandEnt) {
            command = new Copy((CopyCommandEnt)commandEnt);
        } else if (commandEnt instanceof CutCommandEnt) {
            command = new Cut((CutCommandEnt)commandEnt, m_workflowMiddleware);
        } else if (commandEnt instanceof PasteCommandEnt) {
            command = new Paste((PasteCommandEnt)commandEnt);
        } else {
            if (m_workflowCommandForTesting != null) {
                command = m_workflowCommandForTesting;
            } else {
                throw new OperationNotAllowedException("Command of type " + commandEnt.getClass().getSimpleName()
                    + " cannot be executed. Unknown command.");
            }
        }
        return command;
    }

    private static boolean hasCommandResult(final WorkflowKey wfKey, final WorkflowCommand command)
        throws NodeNotFoundException, NotASubWorkflowException {
        if (command instanceof WithResult) {
            var hasResult = true;
            if (command instanceof HigherOrderCommand) {
                hasResult = ((HigherOrderCommand)command).preExecuteToDetermineWhetherProvidesResult(wfKey);
            }
            return hasResult;
        }
        return false;
    }

    private Optional<WorkflowChangeWaiter> prepareCommandResult(final WorkflowKey wfKey, final WorkflowCommand command) {
        // Only commands with results that trigger a real workflow change need a waiter
        if (!((WithResult)command).getChangesToWaitFor().isEmpty()) {
            var wfChangesListener = m_workflowMiddleware.getWorkflowChangesListener(wfKey);
            return Optional.of(wfChangesListener.createWorkflowChangeWaiter(
                ((WithResult)command).getChangesToWaitFor().toArray(WorkflowChange[]::new)));
        }
        return Optional.empty();
    }

    private synchronized void executeCommandAndModifyCommandStacks(final WorkflowKey wfKey,
        final WorkflowCommand command)
        throws NodeNotFoundException, NotASubWorkflowException, OperationNotAllowedException {
        var undoStack = getOrCreateCommandStackFor(wfKey, m_undoStacks);
        var redoStack = getOrCreateCommandStackFor(wfKey, m_redoStacks);

        try (var lock1 = undoStack.lock(); var lock2 = redoStack.lock()) {
            // The undo- and redo-stacks need to be updated before the command is being executed.
            // That's because during the command-execution events are fired which in turn access the
            // canUndo- and canRedo-methods in here. If the stacks would be updated after the command-execution
            // there is a chance, that canUndo or canRedo don't return the correct values, yet (race-condition).
            // (see NXT-965)
            undoStack.add(command);
            redoStack.clear();
            var workflowModified = command.execute(wfKey);
            if (workflowModified) {
                // acknowledge the changes made to the stacks
                undoStack.commitPendingChange();
                redoStack.commitPendingChange();
            }
        }
    }

    private CommandStack getOrCreateCommandStackFor(final WorkflowKey wfKey,
        final Map<WorkflowKey, CommandStack> stacks) {
        return stacks.computeIfAbsent(wfKey, k -> new CommandStack(m_maxNumUndoAndRedoCommandsPerWorkflow));
    }

    private CommandResultEnt waitForCommandResult(final WorkflowKey wfKey, final WorkflowCommand command,
        final WorkflowChangeWaiter wfChangeWaiter) throws OperationNotAllowedException {
        String latestSnapshotId = null;
        if (wfChangeWaiter != null) {
            try {
                wfChangeWaiter.blockUntilOccurred();
                // Only set a snapshot id if there is a workflow change to wait for
                latestSnapshotId = m_workflowMiddleware.getLatestSnapshotId(wfKey).orElse(null);
            } catch (InterruptedException e) { // NOSONAR: Exception re-thrown
                throw new OperationNotAllowedException("Interrupted while waiting corresponding workflow change", e);
            }
        }
        return ((WithResult)command).buildEntity(latestSnapshotId);
    }

    /**
     * @param wfKey reference to the workflow to check the undo-state for
     * @return whether there is at least one command on the undo-stack
     */
    public synchronized boolean canUndo(final WorkflowKey wfKey) {
        var stack = m_undoStacks.get(wfKey);
        return stack != null && stack.peek().map(WorkflowCommand::canUndo).orElse(Boolean.FALSE);
    }

    /**
     * @param wfKey reference to the workflow to undo the last command for
     * @throws OperationNotAllowedException if there is no command to be undone
     */
    public synchronized void undo(final WorkflowKey wfKey) throws OperationNotAllowedException {
        var undoStack = m_undoStacks.get(wfKey);
        if (undoStack != null && !undoStack.isEmpty()) {
            undoStack.getHeadAndTransferTo(getOrCreateCommandStackFor(wfKey, m_redoStacks)).undo();
        } else {
            throw new OperationNotAllowedException("No command to undo");
        }
    }

    /**
     * @param wfKey reference to the workflow to check the redo-state for
     * @return whether there is at least one command on the redo-stack
     */
    public synchronized boolean canRedo(final WorkflowKey wfKey) {
        var stack = m_redoStacks.get(wfKey);
        return stack != null && stack.peek().map(WorkflowCommand::canRedo).orElse(Boolean.FALSE);
    }

    /**
     * @param wfKey reference to the workflow to redo the last command for
     * @throws OperationNotAllowedException if there is no command to be redone
     */
    public synchronized void redo(final WorkflowKey wfKey) throws OperationNotAllowedException {
        var redoStack = m_redoStacks.get(wfKey);
        if (redoStack != null && !redoStack.isEmpty()) {
            redoStack.getHeadAndTransferTo(getOrCreateCommandStackFor(wfKey, m_undoStacks)).redo();
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
    void disposeUndoAndRedoStacks(final String projectId) {
        disposeUndoAndRedoStacks(k -> k.getProjectId().equals(projectId));
    }

    /**
     * Removes all commands from the undo- and redo-stacks for all workflows of a workflow project referenced by its
     * project-id.
     *
     * @param keyFilter
     */
    public synchronized void disposeUndoAndRedoStacks(final Predicate<WorkflowKey> keyFilter) {
        m_undoStacks.entrySet().removeIf(e -> keyFilter.test(e.getKey()));
        m_redoStacks.entrySet().removeIf(e -> keyFilter.test(e.getKey()));
    }

    /**
     * For testing purposes only!
     *
     * @return
     */
    int getUndoStackSize(final WorkflowKey wfKey) {
        CommandStack undoStack = m_undoStacks.get(wfKey);
        return undoStack == null ? 0 : undoStack.size();
    }

    /**
     * For testing purposes only!
     *
     * @return
     */
    int getRedoStackSize(final WorkflowKey wfKey) {
        CommandStack redoStack = m_redoStacks.get(wfKey);
        return redoStack == null ? 0 : redoStack.size();
    }

    private static final class CommandStack {

        private final Deque<WorkflowCommand> m_stack;

        private WorkflowCommand m_pendingCommand;

        private boolean m_hasPendingCommit;

        private final int m_maxStackSize;

        private final ReentrantLock m_stackModifyLock = new ReentrantLock();

        CommandStack(final int maxStackSize) {
            m_stack = new ConcurrentLinkedDeque<>();
            m_maxStackSize = maxStackSize;
        }

        /**
         * Adds a command to the stack. Must be acknowledged via {@link #commitPendingChange()} before any other change
         * can be made to the stack.
         *
         * The stack always needs to be locked before it can be modified, see {@link #lock()}.
         */
        void add(final WorkflowCommand command) {
            assertLocked();
            assertNoPendingCommit();
            m_pendingCommand = command;
            m_hasPendingCommit = true;
        }

        /**
         * Clears all the commands from the stack. Must be acknowledged via {@link #commitPendingChange()} before any
         * other change can be made to the stack.
         *
         * The stack always needs to be locked before it can be modified, see {@link #lock()}.
         */
        void clear() {
            assertLocked();
            assertNoPendingCommit();
            assert m_pendingCommand == null;
            m_hasPendingCommit = true;
        }

        private void assertNoPendingCommit() {
            if (m_hasPendingCommit) {
                throw new IllegalStateException(
                    "Any change to the command stack must be committed first before another change can be made");
            }
        }

        private void assertLocked() {
            if (!m_stackModifyLock.isLocked()) {
                throw new IllegalStateException(
                    "Stack modificiations must be carried out while the stack is locked from modifications");
            }
        }

        /**
         * Acknowledges a modification ({@link #add(WorkflowCommand)} or {@link #clear()}) done to the stack.
         *
         * The stack always needs to be locked before it can be modified or pending changes can be commited, see
         * {@link #lock()}.
         */
        void commitPendingChange() {
            assertLocked();
            if (!m_hasPendingCommit) {
                throw new IllegalStateException("Nothing to commit");
            }
            if (m_pendingCommand == null) {
                m_stack.clear();
            } else {
                m_stack.addFirst(m_pendingCommand);
                ensureMaxStackSize();
                m_pendingCommand = null;
            }
            m_hasPendingCommit = false;
        }

        private void ensureMaxStackSize() {
            if (m_stack.size() > m_maxStackSize) { // NOSONAR - stack size is small enough to not have a performance impact
                m_stack.removeLast();
            }
        }

        @SuppressWarnings("java:S1452")
        Optional<WorkflowCommand> peek() {
            return Optional.ofNullable(m_hasPendingCommit ? m_pendingCommand : m_stack.peek());
        }

        @SuppressWarnings("java:S1452")
        WorkflowCommand getHeadAndTransferTo(final CommandStack otherCommandStack) {
            var c = m_stack.poll();
            if (c == null) {
                throw new NoSuchElementException("Stack is empty");
            }
            otherCommandStack.m_stack.addFirst(c);
            return c;
        }

        int size() {
            return m_stack.size(); // NOSONAR - stack size is small enough to not have a performance impact
        }

        boolean isEmpty() {
            return (m_hasPendingCommit && m_pendingCommand != null) || m_stack.isEmpty();
        }

        /**
         * Locks the stack from being modified by other threads.
         *
         * When the lock is being released (through the returned {@link AutoCloseable}), it will also revert any pending
         * changes (if there are any).
         *
         * @return an {@link AutoCloseable} which allows one to release the lock in a try-block
         */
        CommandStackModifyLock lock() {
            m_stackModifyLock.lock();
            return () -> {
                if (m_hasPendingCommit) {
                    m_pendingCommand = null;
                    m_hasPendingCommit = false;
                }
                m_stackModifyLock.unlock();
            };
        }

    }

    private interface CommandStackModifyLock extends AutoCloseable {

        @Override
        void close();

    }

    void setCommandToExecuteForTesting(final WorkflowCommand wc) {
        m_workflowCommandForTesting = wc;
    }

}

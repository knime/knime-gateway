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
 */
package org.knime.gateway.impl.webui.service.commands;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Optional;
import java.util.function.BiFunction;

import org.knime.core.node.workflow.WorkflowManager;
import org.knime.core.util.Pair;
import org.knime.gateway.api.webui.service.util.ServiceExceptions;
import org.knime.gateway.impl.webui.WorkflowKey;

/**
 * A higher-order workflow command that is composed of a sequence of other workflow commands of which any next command
 * is configured based on the result of the previous command.
 *
 * @author Benjamin Moser, KNIME GmbH, Konstanz, Germany
 */
abstract class CommandSequence extends AbstractWorkflowCommand {

    private WorkflowCommand m_initialCommand;

    private Pair<WorkflowCommand, WorkflowCommandConfigurator>[] m_otherCommands;

    private final LinkedList<WorkflowCommand> m_executedCommands = new LinkedList<>();

    @SafeVarargs
    final void configure(final WorkflowKey wfKey, final WorkflowManager wfm,
            final WorkflowCommand initialCommand,
            final Pair<WorkflowCommand, WorkflowCommandConfigurator>... otherCommands) {
        super.configure(wfKey, wfm);
        m_initialCommand = initialCommand;
        m_otherCommands = otherCommands;
    }

    @Override
    protected boolean execute() throws ServiceExceptions.OperationNotAllowedException {
        try {
            boolean wfModified = m_initialCommand.executeWithWorkflowLock();
            m_executedCommands.add(m_initialCommand);
            WorkflowCommand prevCommand = m_initialCommand;
            for (Pair<WorkflowCommand, WorkflowCommandConfigurator> nextCommandAndConfigurator : m_otherCommands) {
                WorkflowCommand nextCommand = nextCommandAndConfigurator.getFirst();
                WorkflowCommandConfigurator configurator = nextCommandAndConfigurator.getSecond();
                var configuredCommand = configurator.apply(nextCommand, prevCommand);
                wfModified = configuredCommand.executeWithWorkflowLock();
                prevCommand = nextCommand;
                m_executedCommands.add(nextCommand);
            }
            return wfModified;
        } catch (ServiceExceptions.NodeNotFoundException | ServiceExceptions.NotASubWorkflowException | ServiceExceptions.OperationNotAllowedException e) {
            undoAllExecuted();
            throw new ServiceExceptions.OperationNotAllowedException("Error executing command", e);
        }
    }

    @Override
    public Optional<CommandResultBuilder> getResultBuilder() {
        if (m_otherCommands.length == 0) {
            return m_initialCommand.getResultBuilder();
        } else {
            return m_otherCommands[m_otherCommands.length-1].getFirst().getResultBuilder();
        }
    }

    private void undoAllExecuted() throws ServiceExceptions.OperationNotAllowedException {
        for (Iterator<WorkflowCommand> it = m_executedCommands.descendingIterator(); it.hasNext(); ) {
            WorkflowCommand cmd = it.next();
            cmd.undo();  // may also throw exception
        }
        m_executedCommands.clear();
    }

    @Override
    public void undo() throws ServiceExceptions.OperationNotAllowedException {
        undoAllExecuted();
    }

    @Override
    public boolean canUndo() {
        return m_executedCommands.stream().allMatch(WorkflowCommand::canUndo);
    }

    @Override
    public boolean canRedo() {
        return m_executedCommands.stream().allMatch(WorkflowCommand::canRedo);
    }

    interface WorkflowCommandConfigurator extends BiFunction<WorkflowCommand, WorkflowCommand, WorkflowCommand> {

    }

}

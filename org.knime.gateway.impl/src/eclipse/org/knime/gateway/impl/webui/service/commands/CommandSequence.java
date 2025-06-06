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

import java.util.List;
import java.util.Optional;

import org.knime.core.node.workflow.WorkflowLock;
import org.knime.core.node.workflow.WorkflowManager;
import org.knime.gateway.api.webui.service.util.ServiceExceptions;
import org.knime.gateway.api.webui.service.util.ServiceExceptions.NodeNotFoundException;
import org.knime.gateway.api.webui.service.util.ServiceExceptions.NotASubWorkflowException;
import org.knime.gateway.api.webui.service.util.ServiceExceptions.ServiceCallException;
import org.knime.gateway.impl.webui.WorkflowKey;
import org.knime.gateway.impl.webui.WorkflowUtil;

/**
 * A higher-order workflow command that is composed of a sequence of other workflow commands of which any next command
 * is configured based on the result of the previous command.
 *
 * @author Benjamin Moser, KNIME GmbH, Konstanz, Germany
 */
abstract class CommandSequence extends HigherOrderCommand {

    /** Not private so {@link Cut} command can access it */
    protected final List<WorkflowCommand> m_commands;

    private WorkflowKey m_wfKey;

    protected CommandSequence(final List<WorkflowCommand> commands) {
        if (commands.isEmpty()) {
            throw new IllegalStateException("No commands given");
        }
        m_commands = commands;
    }

    @Override
    protected Optional<WithResult> preExecuteToGetResultProvidingCommand(final WorkflowKey wfKey)
        throws ServiceCallException {
        var lastCommand = m_commands.get(m_commands.size() - 1);
        if (lastCommand instanceof WithResult withResult) {
            return Optional.of(withResult);
        } else {
            return Optional.empty();
        }
    }

    @Override
    protected boolean executeWithWorkflowLockAndContext() throws ServiceExceptions.ServiceCallException {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean execute(final WorkflowKey wfKey) throws ServiceExceptions.ServiceCallException {
        m_wfKey = wfKey;
        WorkflowManager wfm;
        try {
            wfm = WorkflowUtil.getWorkflowManager(wfKey);
        } catch (NodeNotFoundException | NotASubWorkflowException ex) {
            throw new ServiceCallException(ex.getMessage(), ex);
        }
        var isWorkflowModified = false;
        try (WorkflowLock lock = wfm.lock()) {
            for (var command : m_commands) {
                if (command.execute(wfKey)) {
                    isWorkflowModified = true;
                }
            }
        }
        return isWorkflowModified;
    }

    @Override
    public void undo() throws ServiceExceptions.ServiceCallException {
        var iterator = m_commands.listIterator(m_commands.size());
        while (iterator.hasPrevious()) {
            iterator.previous().undo();
        }
    }

    @Override
    public void redo() throws ServiceCallException {
        execute(m_wfKey);
    }

    @Override
    public boolean canUndo() {
        return m_commands.stream().allMatch(WorkflowCommand::canUndo);
    }

    @Override
    public boolean canRedo() {
        return m_commands.stream().allMatch(WorkflowCommand::canRedo);
    }

}

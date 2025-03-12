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

import java.util.Optional;
import java.util.function.Function;

import org.knime.core.node.workflow.WorkflowManager;
import org.knime.gateway.api.webui.service.util.ServiceExceptions;
import org.knime.gateway.api.webui.service.util.ServiceExceptions.NodeNotFoundException;
import org.knime.gateway.api.webui.service.util.ServiceExceptions.NotASubWorkflowException;
import org.knime.gateway.impl.webui.WorkflowKey;
import org.knime.gateway.impl.webui.WorkflowUtil;

/**
 * Higher-order command that executes either one of the child commands based on a given predicate. Other properties are
 * based on the active child command as well.
 *
 * @author Benjamin Moser, KNIME GmbH, Konstanz, Germany
 */
abstract class CommandIfElse extends HigherOrderCommand {

    private final Function<WorkflowManager, Boolean> m_predicate;

    private final WorkflowCommand m_leftCommand;

    private final WorkflowCommand m_rightCommand;

    private WorkflowCommand m_activeCommand;

    /**
     * Initialise the command by providing two commands and a predicate to decide which command to execute.
     *
     * @param wfKey The key of the workflow
     * @param predicate a function which receives the {@link WorkflowManager} this command operates on and returns
     *            {@code true} to execute {@code leftCommand}, {@code false} to execute {@code rightCommand}
     * @param leftCommand the command to be executed if the given predicate returns {@code true}
     * @param rightCommand the command to be executed if the given predicate returns {@code false}
     */
    CommandIfElse(final Function<WorkflowManager, Boolean> predicate, final WorkflowCommand leftCommand,
        final WorkflowCommand rightCommand) {
        m_predicate = predicate;
        m_leftCommand = leftCommand;
        m_rightCommand = rightCommand;
    }

    @Override
    public Optional<WithResult> preExecuteToGetResultProvidingCommand(final WorkflowKey wfKey)
        throws ServiceExceptions.ServiceCallException {
        WorkflowManager wfm;
        try {
            wfm = WorkflowUtil.getWorkflowManager(wfKey);
        } catch (NodeNotFoundException | NotASubWorkflowException ex) {
            throw new ServiceExceptions.ServiceCallException(ex.getMessage(), ex);
        }
        var takeLeft = m_predicate.apply(wfm);
        m_activeCommand = Boolean.TRUE.equals(takeLeft) ? m_leftCommand : m_rightCommand;
        if (m_activeCommand instanceof WithResult withResult) {
            return Optional.of(withResult);
        } else {
            return Optional.empty();
        }
    }

    @Override
    protected boolean executeWithLockedWorkflow() throws ServiceExceptions.ServiceCallException {
        return m_activeCommand.execute(getWorkflowKey());
    }

    @Override
    public void undo() throws ServiceExceptions.ServiceCallException {
        m_activeCommand.undo();
    }

    @Override
    public boolean canUndo() {
        return m_activeCommand.canUndo();
    }

    @Override
    public boolean canRedo() {
        return m_activeCommand.canRedo();
    }

}

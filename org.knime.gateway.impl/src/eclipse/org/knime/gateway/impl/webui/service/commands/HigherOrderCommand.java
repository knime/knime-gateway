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
 *   Apr 19, 2022 (hornm): created
 */
package org.knime.gateway.impl.webui.service.commands;

import java.util.Optional;
import java.util.Set;

import org.knime.gateway.api.webui.entity.CommandResultEnt;
import org.knime.gateway.api.webui.service.util.ServiceExceptions.NodeNotFoundException;
import org.knime.gateway.api.webui.service.util.ServiceExceptions.NotASubWorkflowException;
import org.knime.gateway.api.webui.service.util.ServiceExceptions.ServiceCallException;
import org.knime.gateway.impl.service.util.WorkflowChangesTracker.WorkflowChange;
import org.knime.gateway.impl.webui.WorkflowKey;

/**
 * Implemented by commands that receive other commands as parameters.
 *
 * It can also dynamically determine whether the resulting higher-order command produces a command result or not.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
abstract class HigherOrderCommand extends AbstractWorkflowCommand implements WithResult {

    private WithResult m_resultProvidingCommand;

    /**
     * Optionally returns a {@link WorkflowCommand} that produces a result (i.e. implements {@link WithResult}) which
     * will also be the result of this higher-order command.
     *
     * <ul>
     *     <li>Guaranteed to be called before {@link #getChangesToWaitFor()} and {@link #buildEntity(String)}</li>
     *     <li>
     *         Guaranteed to be called before {@link #execute(WorkflowKey)}, i.e. some initialisation of the higher-
     *         order command may happen in this method.
     *     </li>
     * </ul>
     *
     * @param wfKey represents the workflow this command operates on
     *
     * @return the command that returns a result or an empty optional if this higher order command doesn't return any
     *         result
     * @throws ServiceCallException
     */
    protected abstract Optional<WithResult> preExecuteToGetResultProvidingCommand(WorkflowKey wfKey)
        throws ServiceCallException;

    /**
     * @param wfKey represents the workflow this command operates on
     * @return {@code true} if this higher-order command returns a result, otherwise {@code false}
     * @throws NodeNotFoundException
     * @throws NotASubWorkflowException
     */
    final boolean preExecuteToDetermineWhetherProvidesResult(final WorkflowKey wfKey) throws ServiceCallException {
        m_resultProvidingCommand = preExecuteToGetResultProvidingCommand(wfKey).orElse(null);
        if (m_resultProvidingCommand instanceof HigherOrderCommand) {
            m_resultProvidingCommand = ((HigherOrderCommand)m_resultProvidingCommand)
                .preExecuteToGetResultProvidingCommand(wfKey).orElse(null);
        }
        return m_resultProvidingCommand != null;
    }

    @Override
    public Set<WorkflowChange> getChangesToWaitFor() {
        if (m_resultProvidingCommand == null) {
            throw new IllegalStateException("Implementation problem. No command with result given.");
        }
        return m_resultProvidingCommand.getChangesToWaitFor();
    }

    @Override
    public CommandResultEnt buildEntity(final String snapshotId) {
        if (m_resultProvidingCommand == null) {
            throw new IllegalStateException("Implementation problem. No command with result given.");
        }
        return m_resultProvidingCommand.buildEntity(snapshotId);
    }

}

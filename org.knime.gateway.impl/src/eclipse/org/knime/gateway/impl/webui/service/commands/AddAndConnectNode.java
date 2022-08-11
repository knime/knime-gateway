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
 *   Aug 10, 2022 (Kai Franze, KNIME GmbH): created
 */
package org.knime.gateway.impl.webui.service.commands;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

import org.knime.gateway.api.entity.NodeIDEnt;
import org.knime.gateway.api.webui.entity.AddNodeCommandEnt;
import org.knime.gateway.api.webui.service.util.ServiceExceptions.NodeNotFoundException;
import org.knime.gateway.api.webui.service.util.ServiceExceptions.NotASubWorkflowException;
import org.knime.gateway.impl.webui.WorkflowKey;

/**
 * Workflow command to add a node to the workflow and connect it to an existing node.
 * This operation is a {@link CommandSequence} of {@link SimplyAddNode} and {@link Connect}.
 *
 * @author Kai Franze, KNIME GmbH
 */
public class AddAndConnectNode extends CommandSequence {

    AddAndConnectNode(final AddNodeCommandEnt commandEnt) {
        super(getCommands(commandEnt));
    }

    /**
     * Override the method in {@link CommandSequence} to get result providing command which isn't the last command in
     * the sequence
     *
     * TODO: This is the second time we override this, see {@link Cut}. Do we need a more general solution here?
     */
    @Override
    protected Optional<WithResult> preExecuteToGetResultProvidingCommand(final WorkflowKey wfKey)
        throws NodeNotFoundException, NotASubWorkflowException {
        return Optional.of((WithResult)m_commands.get(0));
    }

    private static List<WorkflowCommand> getCommands(final AddNodeCommandEnt commandEnt) {
        var simplyAddNodeCommand = new SimplyAddNode(commandEnt);
        var sourceNodeId = commandEnt.getSourceNodeId();
        var sourcePortIdx = commandEnt.getSourcePortIdx();
        Supplier<NodeIDEnt> destNodeIdSupplier = () -> simplyAddNodeCommand.buildEntity(null).getNewNodeId();
        var connectCommand = new Connect(sourceNodeId, sourcePortIdx, destNodeIdSupplier);
        return List.of(simplyAddNodeCommand, connectCommand);
    }

}

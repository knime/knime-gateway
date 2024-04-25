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
 *   Apr 4, 2024 (kai): created
 */
package org.knime.gateway.impl.webui.service.commands;

import java.util.List;

import org.knime.core.node.workflow.ConnectionContainer;
import org.knime.gateway.api.webui.entity.AutoConnectCommandEnt;
import org.knime.gateway.api.webui.service.util.ServiceExceptions.OperationNotAllowedException;
import org.knime.gateway.impl.webui.service.commands.util.NodesAutoConnector;

/**
 * ...
 *
 * @author Kai Franze, KNIME GmbH, Germany
 */
final class AutoConnect extends AbstractWorkflowCommand {

    private final AutoConnectCommandEnt m_commandEnt;

    private List<ConnectionContainer> m_oldConnections;

    private List<ConnectionContainer> m_newConnections;

    AutoConnect(final AutoConnectCommandEnt commandEnt) {
        m_commandEnt = commandEnt;
    }

    @Override
    protected boolean executeWithLockedWorkflow() throws OperationNotAllowedException {
        final var isFlowVariablesOnly = Boolean.TRUE.equals(m_commandEnt.isFlowVariablesOnly());
        if (isFlowVariablesOnly) {
            throw new OperationNotAllowedException("Automatically connecting all flow variables is not supported yet");
        }

        final var connectableEnts = m_commandEnt.getConnectables();
        if (connectableEnts.stream().anyMatch(
            c -> Boolean.TRUE.equals(c.isMetanodeInPortsBar()) && Boolean.TRUE.equals(c.isMetanodeOutPortsBar()))) {
            throw new OperationNotAllowedException("A metanode ports bar can either be INPUT or OUTPUT, never both");
        }

        final var wfm = getWorkflowManager();
        final var nodesAutoConnector = new NodesAutoConnector(wfm, connectableEnts, isFlowVariablesOnly);

        final var addedAndRemovedConnections = nodesAutoConnector.connect();
        m_newConnections = addedAndRemovedConnections.getFirst();
        m_oldConnections = addedAndRemovedConnections.getSecond();

        return !m_newConnections.isEmpty();
    }

    @Override
    public boolean canUndo() {
        final var wfm = getWorkflowManager();
        return m_newConnections.stream().allMatch(wfm::canRemoveConnection);
    }

    @Override
    public void undo() throws OperationNotAllowedException {
        final var wfm = getWorkflowManager();
        m_newConnections.forEach(wfm::removeConnection);
        m_oldConnections
            .forEach(cc -> wfm.addConnection(cc.getSource(), cc.getSourcePort(), cc.getDest(), cc.getDestPort()));
        m_newConnections = null;
        m_oldConnections = null;
    }

}

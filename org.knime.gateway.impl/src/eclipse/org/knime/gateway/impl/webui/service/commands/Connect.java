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

import java.util.function.Supplier;

import org.knime.core.node.workflow.ConnectionContainer;
import org.knime.core.node.workflow.ConnectionID;
import org.knime.core.node.workflow.NodeID;
import org.knime.core.node.workflow.WorkflowManager;
import org.knime.gateway.api.entity.NodeIDEnt;
import org.knime.gateway.api.webui.entity.ConnectCommandEnt;
import org.knime.gateway.api.webui.service.util.ServiceExceptions.OperationNotAllowedException;

/**
 * Workflow command to connect two nodes.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
final class Connect extends AbstractWorkflowCommand {

    private ConnectCommandEnt m_commandEnt;

    private ConnectionContainer m_newConnection;

    private ConnectionContainer m_oldConnection;

    private NodeIDEnt m_sourceNodeId;

    private Integer m_sourcePortIdx;

    private Supplier<NodeIDEnt> m_destNodeIdSupplier;

    Connect(final ConnectCommandEnt commandEnt) {
        m_commandEnt = commandEnt;
    }

    Connect(final NodeIDEnt sourceNodeId, final Integer sourcePortIdx, final Supplier<NodeIDEnt> destNodeIdSupplier) {
        m_sourceNodeId = sourceNodeId;
        m_sourcePortIdx = sourcePortIdx;
        m_destNodeIdSupplier = destNodeIdSupplier;
    }

    @Override
    public boolean executeWithLockedWorkflow() throws OperationNotAllowedException {
        var wfm = getWorkflowManager();
        var projectWfm = wfm.getProjectWFM();
        NodeID sourceNodeId;
        Integer sourcePortIdx;
        NodeID destNodeId;
        Integer destPortIdx;
        if (m_commandEnt != null) { // In case the command entity was provided
            sourceNodeId = m_commandEnt.getSourceNodeId().toNodeID(projectWfm.getID());
            sourcePortIdx = m_commandEnt.getSourcePortIdx();
            destNodeId = m_commandEnt.getDestinationNodeId().toNodeID(projectWfm.getID());
            destPortIdx = m_commandEnt.getDestinationPortIdx();
        } else { // In case we need to get the destination node id and infer port index first
            sourceNodeId = m_sourceNodeId.toNodeID(projectWfm.getID());
            sourcePortIdx = m_sourcePortIdx;
            destNodeId = m_destNodeIdSupplier.get().toNodeID(projectWfm.getID());
            destPortIdx = inferDestPortIdx(getWorkflowManager(), sourceNodeId, sourcePortIdx, destNodeId);
        }
        return connectNodesAndReturnIfWorkflowHasChanged(wfm, sourceNodeId, sourcePortIdx, destNodeId, destPortIdx);
    }

    private static Integer inferDestPortIdx(final WorkflowManager wfm, final NodeID sourceNodeId,
        final Integer sourcePortIdx, final NodeID destNodeId) throws OperationNotAllowedException {
        var sourceNode = wfm.getNodeContainer(sourceNodeId);
        var sourcePortType = sourceNode.getOutPort(sourcePortIdx).getPortType();
        var destNode = wfm.getNodeContainer(destNodeId);
        for (var i = 0; i < destNode.getNrInPorts(); i++) {
            if (destNode.getOutPort(i).getPortType().equals(sourcePortType)) {
                return i;
            }
        }
        throw new OperationNotAllowedException("Destination port index could not be infered");
    }

    private boolean connectNodesAndReturnIfWorkflowHasChanged(final WorkflowManager wfm, final NodeID sourceNodeId,
        final Integer sourcePortIdx, final NodeID destNodeId, final Integer destPortIdx)
        throws OperationNotAllowedException {
        try {
            m_oldConnection = wfm.getConnection(new ConnectionID(destNodeId, destPortIdx));
        } catch (IllegalArgumentException e) {
            throw new OperationNotAllowedException(e.getMessage(), e);
        }
        if (m_oldConnection != null && m_oldConnection.getSource().equals(sourceNodeId)
            && m_oldConnection.getSourcePort() == sourcePortIdx) {
            // it's the very same connection -> no change
            return false;
        }

        if (!wfm.canAddConnection(sourceNodeId, sourcePortIdx, destNodeId, destPortIdx)) {
            throw new OperationNotAllowedException("Connection can't be added");
        }

        m_newConnection = wfm.addConnection(sourceNodeId, sourcePortIdx, destNodeId, destPortIdx);
        return true;
    }

    @Override
    public boolean canUndo() {
        return getWorkflowManager().canRemoveConnection(m_newConnection);
    }

    @Override
    public void undo() throws OperationNotAllowedException {
        var wfm = getWorkflowManager();
        wfm.removeConnection(m_newConnection);
        if (m_oldConnection != null) {
            wfm.addConnection(m_oldConnection.getSource(), m_oldConnection.getSourcePort(), m_oldConnection.getDest(),
                m_oldConnection.getDestPort());
        }
        m_newConnection = null;
        m_oldConnection = null;
    }

}

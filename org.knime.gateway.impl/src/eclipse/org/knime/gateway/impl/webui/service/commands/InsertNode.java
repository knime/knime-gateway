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
 *   Jun 29, 2022 (Kai Franze, KNIME GmbH): created
 */
package org.knime.gateway.impl.webui.service.commands;

import java.io.IOException;

import org.knime.core.node.workflow.NodeID;
import org.knime.core.node.workflow.WorkflowManager;
import org.knime.gateway.api.webui.entity.InsertNodeCommandEnt;
import org.knime.gateway.api.webui.service.util.ServiceExceptions.OperationNotAllowedException;
import org.knime.gateway.impl.service.util.DefaultServiceUtil;

/**
 * Workflow command to insert a node on t workflow parts into the active workflow
 *
 * @author Juan Baquero, KNIME GmbH
 */
public class InsertNode extends AbstractWorkflowCommand {

    private final InsertNodeCommandEnt m_commandEnt;

    InsertNode(final InsertNodeCommandEnt commandEnt) {
        m_commandEnt = commandEnt;
    }

    @Override
    protected boolean executeWithLockedWorkflow() throws OperationNotAllowedException {
        var wfm = getWorkflowManager();
        var connectionID = DefaultServiceUtil.entityToConnectionID(getWorkflowKey().getProjectId(), m_commandEnt.getConnectionId());
        // Save original source and destination
        var connectionContainer = wfm.getConnection(connectionID);
        var destNode = connectionContainer.getDest();
        var destPort = connectionContainer.getDestPort();
        var srcNode = connectionContainer.getSource();
        var srcPort = connectionContainer.getSourcePort();

        // Remove Connection
        wfm.removeConnection(connectionContainer);

        // Create new node / move previous node
        var nodeFactoryEnt = m_commandEnt.getNodeFactory();
        var position = m_commandEnt.getPosition();
        NodeID newNodeId;
        try {
            newNodeId = DefaultServiceUtil.createAndAddNode(nodeFactoryEnt.getClassName(), nodeFactoryEnt.getSettings(), position.getX(), position.getY(), wfm, true);
        } catch (IOException ex) {
            throw new OperationNotAllowedException(ex.getMessage());
        }

        // TODO implement existing node

        // Incoming connections
        var incomingPortMapping = AddNode.getMatchingPorts(srcNode, newNodeId, srcPort, wfm);
        var outgoingPortMapping = AddNode.getMatchingPorts(newNodeId, destNode, null, wfm);

        incomingPortMapping.forEach((entrySrcPort, entryDestPort) -> {
            addConnection(srcNode, entrySrcPort, newNodeId, entryDestPort, wfm);
        });
        outgoingPortMapping.forEach((entrySrcPort, entryDestPort) -> {
            addConnection(newNodeId, entrySrcPort, destNode, destPort, wfm);
        });

        return true;
    }

    private static void addConnection(final NodeID src, final int srcPort, final NodeID dest, final int destPort, final WorkflowManager wfm) {
        if(wfm.canAddConnection(src, srcPort, dest, destPort)) {
            wfm.addConnection(src, srcPort, dest, destPort);
        }
    }

    @Override
    public void undo() throws OperationNotAllowedException {
        var wfm = getWorkflowManager();
    }

}

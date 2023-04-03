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
 *   Mar 27, 2023 (Juan Baquero, KNIME GmbH): created
 */
package org.knime.gateway.impl.webui.service.commands;

import java.io.IOException;
import java.util.Set;

import org.knime.core.node.NodeLogger;
import org.knime.core.node.workflow.ConnectionID;
import org.knime.core.node.workflow.NodeID;
import org.knime.core.node.workflow.WorkflowCopyContent;
import org.knime.core.node.workflow.WorkflowManager;
import org.knime.core.node.workflow.WorkflowPersistor;
import org.knime.gateway.api.webui.entity.InsertNodeCommandEnt;
import org.knime.gateway.api.webui.service.util.ServiceExceptions.OperationNotAllowedException;
import org.knime.gateway.impl.service.util.DefaultServiceUtil;
import org.knime.gateway.impl.webui.NodeRepository;
import org.knime.gateway.impl.webui.service.commands.util.MatchingPortsUtil;

/**
 * Workflow command to insert a node on top of an active connection
 *
 * @author Juan Baquero, KNIME GmbH
 */
final class InsertNode extends AbstractWorkflowCommand {

    private static final NodeLogger LOGGER = NodeLogger.getLogger(NodeRepository.class);

    private final InsertNodeCommandEnt m_commandEnt;

    private NodeID m_destNode;

    private int m_destPort;

    private NodeID m_srcNode;

    private int m_srcPort;

    private NodeID m_insertedNode;

    private ConnectionID m_connection;

    private WorkflowPersistor m_copy;

    InsertNode(final InsertNodeCommandEnt commandEnt) {
        m_commandEnt = commandEnt;

    }

    @Override
    protected boolean executeWithLockedWorkflow() throws OperationNotAllowedException {
        var wfm = getWorkflowManager();
        m_connection =
            DefaultServiceUtil.entityToConnectionID(getWorkflowKey().getProjectId(), m_commandEnt.getConnectionId());

        // Save original source and destination
        var connectionContainer = wfm.getConnection(m_connection);
        m_destNode = connectionContainer.getDest();
        m_destPort = connectionContainer.getDestPort();
        m_srcNode = connectionContainer.getSource();
        m_srcPort = connectionContainer.getSourcePort();

        // Remove Connection
        try {
            wfm.removeConnection(connectionContainer);
        } catch (Exception ex) {
            throw new OperationNotAllowedException("Could not remove connection");
        }

        // Move previous node / Create new node
        var position = m_commandEnt.getPosition();
        var nodeEnt = m_commandEnt.getNodeId();
        var nodeFactoryEnt = m_commandEnt.getNodeFactory();
        if (nodeEnt != null) { // Move node
            m_insertedNode = DefaultServiceUtil.entityToNodeID(getWorkflowKey().getProjectId(), nodeEnt);
            WorkflowCopyContent content = WorkflowCopyContent.builder().setNodeIDs(m_insertedNode).build();
            m_copy = wfm.copy(true, content);

            var nodeContainer = wfm.getNodeContainer(m_insertedNode);
            var oldPosition = nodeContainer.getUIInformation().getBounds();
            Translate.performTranslation(wfm, Set.of(nodeContainer), Set.of(),
                new int[]{position.getX() - oldPosition[0], position.getY() - oldPosition[1]});
        } else if (nodeFactoryEnt != null) { // New node
            try {
                m_insertedNode = DefaultServiceUtil.createAndAddNode(nodeFactoryEnt.getClassName(),
                    nodeFactoryEnt.getSettings(), position.getX(), position.getY(), wfm, true);
            } catch (IOException ex) {
                throw new OperationNotAllowedException(ex.getMessage());
            }
        } else {
            throw new OperationNotAllowedException(
                "Both nodeId and nodeFactoryId are not defined. Provide one of them.");
        }

        // Reconnect
        try {
            var incomingPortMapping = MatchingPortsUtil.getMatchingPorts(m_srcNode, m_insertedNode, m_srcPort, wfm);
            incomingPortMapping.forEach((entrySrcPort, entryDestPort) -> {
                addConnection(m_srcNode, entrySrcPort, m_insertedNode, entryDestPort, wfm);
            });
        } catch (Exception ex) { // NOSONAR
            LOGGER.warn("Could not find a suitable destination port for incomming connection.");
        }

        try {
            var outgoingPortMapping = MatchingPortsUtil.getMatchingPorts(m_insertedNode, m_destNode, null, wfm);
            outgoingPortMapping.forEach((entrySrcPort, entryDestPort) -> {
                addConnection(m_insertedNode, entrySrcPort, m_destNode, m_destPort, wfm);
            });
        } catch (Exception ex) { // NOSONAR
            LOGGER.warn("Could not find a suitable destination port for outgoing connection.");
        }

        return true;
    }

    private static void addConnection(final NodeID src, final int srcPort, final NodeID dest, final int destPort,
        final WorkflowManager wfm) {
        if (wfm.canAddConnection(src, srcPort, dest, destPort)) {
            wfm.addConnection(src, srcPort, dest, destPort);
        }
    }

    @Override
    public boolean canUndo() {
        var wfm = getWorkflowManager();
        return wfm.canRemoveNode(m_insertedNode) && wfm.canAddConnection(m_srcNode, m_srcPort, m_destNode, m_destPort);
    }

    @Override
    public boolean canRedo() {
        var wfm = getWorkflowManager();
        return wfm.canRemoveConnection(wfm.getConnection(m_connection));
    }

    @Override
    public void undo() throws OperationNotAllowedException {
        var wfm = getWorkflowManager();
        wfm.removeNode(m_insertedNode);
        if (m_copy != null) {
            wfm.paste(m_copy);
        }
        wfm.addConnection(m_srcNode, m_srcPort, m_destNode, m_destPort);
        m_insertedNode = null;
        m_srcNode = null;
        m_srcPort = 0;
        m_destNode = null;
        m_destPort = 0;
    }

}

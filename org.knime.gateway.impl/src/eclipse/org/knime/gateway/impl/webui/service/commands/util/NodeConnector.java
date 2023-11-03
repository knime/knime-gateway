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
 *   Apr 12, 2023 (hornm): created
 */
package org.knime.gateway.impl.webui.service.commands.util;

import org.knime.core.node.workflow.ConnectionContainer;
import org.knime.core.node.workflow.NodeContainer;
import org.knime.core.node.workflow.NodeID;
import org.knime.core.node.workflow.NodeTimer;
import org.knime.core.node.workflow.WorkflowManager;
import org.knime.gateway.api.entity.NodeIDEnt;
import org.knime.gateway.api.util.CoreUtil;

/**
 * Helper to connect a node to a upstream and/or downstream node.
 *
 * Mandatory parameters to connect the node are passed via the constructor, optional ones via individual methods. The
 * node is finally connected with the {@link #connect()}-method.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
public final class NodeConnector {

    private final WorkflowManager m_wfm;

    private final NodeID m_nodeId;

    private NodeID m_sourceNodeId;

    private Integer m_sourcePortIdx;

    private NodeID m_destNodeId;

    private Integer m_destPortIdx;

    private boolean m_track;

    NodeConnector(final WorkflowManager wfm) {
        m_wfm = wfm;
        m_nodeId = null;
    }

    /**
     * @param wfm the workflow manager that contains the node to connect
     * @param nodeId the node to connect
     */
    public NodeConnector(final WorkflowManager wfm, final NodeID nodeId) {
        m_wfm = wfm;
        m_nodeId = nodeId;
    }

    /**
     * Connects an existing downstream node to this node.
     *
     * @param sourceNodeId
     * @param sourcePortIdx if {@code null} it will be automatically determined
     * @return this connector
     */
    public NodeConnector connectFrom(final NodeIDEnt sourceNodeId, final Integer sourcePortIdx) {
        return connectFrom(
            sourceNodeId == null ? null : sourceNodeId.toNodeID(CoreUtil.getProjectWorkflowNodeID(m_wfm)),
            sourcePortIdx);
    }

    /**
     * Connects an existing downstream node to this node.
     *
     * @param sourceNodeId
     * @param sourcePortIdx if {@code null} it will be automatically determined
     * @return this connector
     */
    public NodeConnector connectFrom(final NodeID sourceNodeId, final Integer sourcePortIdx) {
        m_sourceNodeId = sourceNodeId;
        m_sourcePortIdx = sourcePortIdx;
        return this;
    }

    /**
     * Connects an existing upstream node to this node.
     *
     * @param destNodeId
     * @return this connector
     */
    public NodeConnector connectTo(final NodeID destNodeId) {
        m_destNodeId = destNodeId;
        return this;
    }

    /**
     * Connects an existing upstream node to this node from a set port.
     *
     * @param destNodeId
     * @param destPortIdx
     * @return this connector
     */
    public NodeConnector connectTo(final NodeID destNodeId, final Integer destPortIdx) {
        m_destNodeId = destNodeId;
        m_destPortIdx = destPortIdx;
        return this;
    }

    /**
     * Turns on to track connection creation.
     *
     * @return this connector
     */
    public NodeConnector trackCreation() {
        m_track = true;
        return this;
    }

    /**
     * Actually carries out the connect-operation.
     *
     * @return {@code true} if the connections were created successfully, {@code false} if at least one connection
     *         couldn't be added
     */
    public boolean connect() {
        if (m_nodeId == null) {
            throw new IllegalStateException("Can't connect node. No node id given.");
        }
        return connect(m_nodeId);
    }

    /**
     * Actually carries out the connect-operation for the given node-id.
     *
     * @param nodeId
     * @return {@code true} if the connections were created successfully, {@code false} if at least one connection
     *         couldn't be added
     */
    boolean connect(final NodeID nodeId) {
        boolean allConnectionsCreated = true;
        if (m_sourceNodeId != null
            && !findMatchingPortAndConnect(m_wfm, m_sourceNodeId, m_sourcePortIdx, nodeId, null, m_track)) {
            allConnectionsCreated = false;
        }
        if (m_destNodeId != null
            && !findMatchingPortAndConnect(m_wfm, nodeId, null, m_destNodeId, m_destPortIdx, m_track)) { // NOSONAR
            allConnectionsCreated = false;
        }
        return allConnectionsCreated;
    }

    private static boolean findMatchingPortAndConnect(final WorkflowManager wfm, final NodeID sourceNodeId,
        final Integer sourcePortIdxParam, final NodeID destNodeId, final Integer destPortIdxParam,
        final boolean track) {
        var sourceNode = getNodeContainerOrSelf(sourceNodeId, wfm);
        var destNode = getNodeContainerOrSelf(destNodeId, wfm);
        var matchingPorts =
            MatchingPortsUtil.getMatchingPorts(sourceNode, destNode, sourcePortIdxParam, destPortIdxParam, wfm);
        for (var entry : matchingPorts.entrySet()) {
            Integer sourcePortIdx = entry.getKey();
            Integer destPortIdx = entry.getValue();
            if (destPortIdx == -1) {
                return false;
            }
            if (connect(wfm, sourceNodeId, sourcePortIdx, destNodeId, destPortIdx, false) == null) {
                return false;
            }
        }
        if (track) {
            trackConnectionCreation(wfm, sourceNodeId, destNodeId);
        }
        return true;
    }

    private static void trackConnectionCreation(final WorkflowManager wfm, final NodeID sourceNodeId,
        final NodeID destNodeId) {
        NodeTimer.GLOBAL_TIMER.addConnectionCreation(getNodeContainerOrSelf(sourceNodeId, wfm),
            getNodeContainerOrSelf(destNodeId, wfm));
    }

    private static NodeContainer getNodeContainerOrSelf(final NodeID nodeId, final WorkflowManager wfm) {
        if (nodeId.equals(wfm.getID())) {
            return wfm;
        }
        return wfm.getNodeContainer(nodeId);
    }

    /**
     * Creates a connection for two ports.
     *
     * @param wfm
     * @param sourceNodeId
     * @param sourcePortIdx
     * @param destNodeId
     * @param destPortIdx
     * @param track whether to track the connection creation (via the {@link NodeTimer})
     * @return the new connection or {@code null} if the connection couldn't be created
     */
    public static ConnectionContainer connect(final WorkflowManager wfm, final NodeID sourceNodeId,
        final int sourcePortIdx, final NodeID destNodeId, final int destPortIdx, final boolean track) {
        if (wfm.canAddConnection(sourceNodeId, sourcePortIdx, destNodeId, destPortIdx)) {
            var cc = wfm.addConnection(sourceNodeId, sourcePortIdx, destNodeId, destPortIdx);
            if (track) {
                trackConnectionCreation(wfm, sourceNodeId, destNodeId);
            }
            return cc;
        } else {
            return null;
        }
    }

}

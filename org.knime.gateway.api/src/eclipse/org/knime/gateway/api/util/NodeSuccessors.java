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
 *   Mar 1, 2021 (hornm): created
 */
package org.knime.gateway.api.util;

import java.util.BitSet;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.stream.Collectors;

import org.knime.core.node.workflow.ConnectionContainer;
import org.knime.core.node.workflow.NodeContainer;
import org.knime.core.node.workflow.NodeID;
import org.knime.core.node.workflow.WorkflowLock;
import org.knime.core.node.workflow.WorkflowManager;

/**
 * Logic and data-structure to calculate and provide all node successors of all nodes in a workflow in one go.
 *
 * @noreference This class is not intended to be referenced by clients.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
public final class NodeSuccessors {

    /**
     * Determines the node successors for all nodes contained in this workflow in one run go.
     *
     * @param wfm the workflow manager to determine the node successors for
     * @return the node successors
     */
    public static NodeSuccessors determineNodeSuccessors(final WorkflowManager wfm) {
        try (WorkflowLock lock = wfm.lock()) {
            return new NodeSuccessors(wfm);
        }
    }

    private final Map<NodeID, Node> m_successors = new HashMap<>();

    private NodeSuccessors(final WorkflowManager wfm) {
        calc(wfm);
    }

    /**
     * Directly gives the successors for a node (without any calculation).
     *
     * @param id the node to get the successors for
     * @return all the successors for the node of the given id (including itself) represented as a {@link BitSet}; each
     *         bit represents a node in the workflow, in the order as returned by
     *         {@link WorkflowManager#getNodeContainers()}
     */
    public BitSet getSuccessors(final NodeID id) {
        Node n = m_successors.get(id);
        if (n == null) {
            throw new IllegalArgumentException("No successors for node with id '" + id + "' found");
        }
        return n.m_bitSet;
    }

    private void calc(final WorkflowManager wfm) {
        assert wfm.isLockedByCurrentThread();
        Collection<NodeContainer> ncs = wfm.getNodeContainers();
        Queue<NodeID> ids = new LinkedList<>();

        // initialize
        int idx = 0;
        for (NodeContainer nc : ncs) {
            NodeID id = nc.getID();
            m_successors.put(id, new Node(idx, id, ncs.size()));
            ids.add(id);
            idx++;
        }

        // determine successors
        while (!ids.isEmpty()) {
            NodeID id = ids.poll();
            Node n = m_successors.get(id);
            determineSuccessors(n, null, wfm, m_successors);
        }
    }

    private static void determineSuccessors(final Node node, final Node predecessor, final WorkflowManager wfm,
        final Map<NodeID, Node> nodes) {
        if (!node.m_visited) {
            // node not visited, yet -> recursively determine its successors
            Set<NodeID> successors = successors(node, wfm);
            for (NodeID successorId : successors) {
                Node successor = nodes.get(successorId);
                determineSuccessors(successor, node, wfm, nodes);
                node.m_bitSet.or(successor.m_bitSet);
            }
            node.m_visited = true;
        } else if (predecessor != null) {
            // if this node has already been visited (all its successors are known) and it's not a source node
            // -> all the successors of this node are also the successors of the predecessor
            predecessor.m_bitSet.or(node.m_bitSet);
        } else {
            //
        }
    }

    private static Set<NodeID> successors(final Node n, final WorkflowManager wfm) {
        return wfm.getOutgoingConnectionsFor(n.m_id).stream().filter(cc -> !cc.getDest().equals(wfm.getID()))
            .map(ConnectionContainer::getDest).collect(Collectors.toSet());
    }

    private static class Node {

        private boolean m_visited = false;

        private BitSet m_bitSet;

        private NodeID m_id;

        Node(final int idx, final NodeID id, final int numNodes) {
            m_bitSet = new BitSet(numNodes);
            m_bitSet.set(idx);
            this.m_id = id;

        }

    }

}

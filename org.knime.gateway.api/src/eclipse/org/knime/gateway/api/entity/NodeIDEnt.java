/*
 * ------------------------------------------------------------------------
 *  Copyright by KNIME AG, Zurich, Switzerland
 *  Website: http://www.knime.com; Email: contact@knime.com
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
package org.knime.gateway.api.entity;

import java.util.Arrays;

import org.knime.core.node.workflow.NodeContainer;
import org.knime.core.node.workflow.NodeID;
import org.knime.core.node.workflow.NodeID.NodeIDSuffix;
import org.knime.gateway.api.util.CoreUtil;

/**
 * Represents a node id as used by gateway entities and services (and the UI in general). Equivalent to the core's
 * {@link org.knime.core.node.workflow.NodeID}.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
public final class NodeIDEnt {

    private static final String ROOT_MARKER = "root";

    private final int[] m_ids;

    /**
     * Creates the node id from a list of hierarchical node ids.
     *
     * @param ids the ids of the single nodes that form the node hierarchy
     */
    public NodeIDEnt(final int... ids) {
        m_ids = ids.clone();
    }

    /**
     * @param nodeID the node id to create the entity from
     */
    public NodeIDEnt(final NodeID nodeID) {
        this(extractNodeIDs(nodeID, false));
    }

    /**
     * @param nodeID the node id to create the entity from
     * @param hasComponentProjectParent if the node with the given id is part of a component project; if so, an
     *            unnecessary '0' is removed; e.g. instead of 'root:0:4:5', 'root:4:5' is used
     */
    public NodeIDEnt(final NodeID nodeID, final boolean hasComponentProjectParent) {
        this(extractNodeIDs(nodeID, hasComponentProjectParent));
    }

    /**
     * Deserialization constructor.
     *
     * @param s string representation as returned by {@link #toString()}
     */
    public NodeIDEnt(final String s) {
        this(extractNodeIDs(s, false));
    }

    private static int[] extractNodeIDs(final NodeID nodeID, final boolean hasComponentProjectParent) {
        final var s = nodeID.toString();
        return extractNodeIDs(s, hasComponentProjectParent);
    }

    private static int[] extractNodeIDs(final String s, final boolean hasComponentProjectParent) {
        final int index = s.indexOf(':');
        final int start = hasComponentProjectParent ? 1 : 0;
        if (index >= start) {
            final var split = s.substring(index + 1).split(":");
            final var ids = new int[split.length - start];
            for (int i = start; i < split.length; i++) {
                ids[i - start] = Integer.parseInt(split[i]);
            }
            return ids;
        } else {
            return new int[0];
        }
    }

    /**
     * The id representing the root node (i.e. the node that 'contains' the root workflow).
     *
     * @return the node id entity
     */
    public static NodeIDEnt getRootID() {
        return new NodeIDEnt();
    }

    /**
     * Appends the given single node id to this id and returns a copy.
     *
     * @param id the id to append
     * @return a new node id entity
     */
    public NodeIDEnt appendNodeID(final int id) {
        final var ids = new int[m_ids.length + 1];
        System.arraycopy(m_ids, 0, ids, 0, m_ids.length);
        ids[m_ids.length] = id;
        return new NodeIDEnt(ids);
    }

    /**
     * Converts a node id entity (as provided by gateway entities) to a {@link NodeID} instance.
     *
     * @param nc the workflow/component project itself or any node/sub-workflow within the workflow/component project
     *
     * @return the {@link NodeID} instance
     */
    public NodeID toNodeID(final NodeContainer nc) {
        var project = CoreUtil.getProjectWorkflow(nc);
        return toNodeID(project.getID());
    }

    /**
     * @param rootID
     * @return
     * @deprecated use {@link #toNodeID(NodeContainer)} instead
     */
    @Deprecated(since = "5.3")
    public NodeID toNodeID(final NodeID rootID) {
        if (m_ids.length == 0) {
            return rootID;
        } else {
            return NodeIDSuffix.fromString(toStringWithoutRoot()).prependParent(rootID);
        }
    }

    /**
     * @return the individual nested node ids
     */
    public int[] getNodeIDs() {
        return m_ids.clone();
    }

    /**
     * @return the node id as string
     */
    @Override
    public String toString() {
        final var sb = new StringBuilder(ROOT_MARKER);
        for (int i : m_ids) {
            sb.append(":" + i);
        }
        return sb.toString();
    }

    /**
     * @param id the id to check agains
     * @return {@code true} if this node id is equal or a parent node id of the passed id; otherwise {@code false}
     */
    public boolean isEqualOrParentOf(final NodeIDEnt id) {
        if (this.equals(id)) {
            return true;
        }
        if (m_ids.length >= id.m_ids.length) {
            // a parent node id (where we already know it's unequal) must be strictly shorter
            return false;
        }
        for (int i = 0; i < m_ids.length; i++) {
            if (m_ids[i] != id.m_ids[i]) {
                return false;
            }
        }
        return true;
    }

    private String toStringWithoutRoot() {
        return toString().substring(ROOT_MARKER.length() + 1);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(m_ids);
    }

    @Override
    public boolean equals(final Object obj) {
        return obj instanceof NodeIDEnt other && Arrays.equals(m_ids, other.m_ids);
    }
}

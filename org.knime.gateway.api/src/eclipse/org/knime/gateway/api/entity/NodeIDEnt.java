/* ------------------------------------------------------------------
 * This source code, its documentation and all appendant files
 * are protected by copyright law. All rights reserved.
 *
 * Copyright by KNIME AG, Zurich, Switzerland
 *
 * You may not modify, publish, transmit, transfer or sell, reproduce,
 * create derivative works from, distribute, perform, display, or in
 * any way exploit any of the content, in whole or in part, except as
 * otherwise expressly permitted in writing by the copyright owner or
 * as specified in the license file distributed with this product.
 *
 * If you have any questions please contact the copyright holder:
 * website: www.knime.com
 * email: contact@knime.com
 * ---------------------------------------------------------------------
 *
 */
package org.knime.gateway.api.entity;

import java.util.Arrays;

import org.knime.core.node.workflow.NodeID;
import org.knime.core.node.workflow.NodeID.NodeIDSuffix;

/**
 * Represents a node id as used by gateway entities and services. Equivalent to the core's
 * {@link org.knime.core.node.workflow.NodeID}.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
public final class NodeIDEnt {

    /* an empty string marks the root*/
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
        this(extractNodeIDs(nodeID));
    }

    /**
     * Deserialization constructor.
     *
     * @param s string representation as returned by {@link #toString()}
     */
    public NodeIDEnt(final String s) {
        this(extractNodeIDs(s));
    }

    private static int[] extractNodeIDs(final NodeID nodeID) {
        String s = nodeID.toString();
        return extractNodeIDs(s);
    }

    private static int[] extractNodeIDs(final String s) {
        int index = s.indexOf(":");
        if (index >= 0) {
            String[] split = s.substring(index + 1).split(":");
            int[] ids = new int[split.length];
            for (int i = 0; i < split.length; i++) {
                ids[i] = Integer.valueOf(split[i]);
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
        return new NodeIDEnt(new int[0]);
    }

    /**
     * Appends the given single node id to this id and returns a copy.
     *
     * @param id the id to append
     * @return a new node id entity
     */
    public NodeIDEnt appendNodeID(final int id) {
        int[] ids = new int[m_ids.length + 1];
        System.arraycopy(m_ids, 0, ids, 0, m_ids.length);
        ids[m_ids.length] = id;
        return new NodeIDEnt(ids);
    }

    /**
     * Converts the entity into a {@link NodeID} object.
     *
     * @param rootID the root node id to be prepended
     * @return the node id object
     */
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
        String id = ROOT_MARKER;
        for (int i : m_ids) {
            id += ":" + i;
        }
        return id;
    }

    private String toStringWithoutRoot() {
        return toString().substring(ROOT_MARKER.length() + 1);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return Arrays.hashCode(m_ids);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(final Object obj) {
        if (obj instanceof NodeIDEnt) {
            return Arrays.equals(m_ids, ((NodeIDEnt)obj).m_ids);
        } else {
            return false;
        }
    }
}

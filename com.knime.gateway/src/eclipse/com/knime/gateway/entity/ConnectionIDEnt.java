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
package com.knime.gateway.entity;

import java.util.Objects;

import org.knime.core.node.workflow.ConnectionID;

/**
 * Represents a connection id as used by gateway entities and services. Equivalent to the core's
 * {@link org.knime.core.node.workflow.ConnectionID}.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
public final class ConnectionIDEnt {

    private NodeIDEnt m_destNodeID;
    private int m_destPortIdx;

    /**
     * @param destNodeID the destination node id of the connection
     * @param destPortIdx the destination port index
     */
    public ConnectionIDEnt(final NodeIDEnt destNodeID, final int destPortIdx) {
        m_destNodeID = destNodeID;
        m_destPortIdx = destPortIdx;
    }

    /**
     * Creates a new connection id entity from a {@link ConnectionID}.
     *
     * @param connectionId
     */
    public ConnectionIDEnt(final ConnectionID connectionId) {
        this(new NodeIDEnt(connectionId.getDestinationNode()), connectionId.getDestinationPort());
    }

    /**
     * Deserialization constructor.
     *
     * @param s string representation as returned by {@link #toString()}
     */
    public ConnectionIDEnt(final String s) {
        String[] split = s.split("_");
        m_destNodeID = new NodeIDEnt(split[0]);
        m_destPortIdx = Integer.valueOf(split[1]);
    }

    /**
     * @return the destination node id entity
     */
    public NodeIDEnt getDestNodeIDEnt() {
        return m_destNodeID;
    }

    /**
     * @return the destination port index
     */
    public int getDestPortIdx() {
        return m_destPortIdx;
    }

    @Override
    public String toString() {
        return m_destNodeID.toString() + "_" + m_destPortIdx;
    }

    @Override
    public int hashCode() {
        return Objects.hash(m_destNodeID.hashCode(), m_destNodeID);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null) {
            return false;
        }
        if (getClass() != o.getClass()) {
            return false;
        }
        ConnectionIDEnt ent = (ConnectionIDEnt)o;
        return Objects.equals(m_destNodeID, ent.m_destNodeID) && m_destPortIdx == ent.m_destPortIdx;
    }
}

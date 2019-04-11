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

import org.knime.core.node.workflow.WorkflowAnnotationID;

/**
 * Represents a (workflow) annotation id as used by gateway entities and services. Equivalent to the core's
 * {@link org.knime.core.node.workflow.WorkflowAnnotationID}.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
public final class AnnotationIDEnt {

    private NodeIDEnt m_nodeId;

    private int m_index;

    /**
     * Creates a new annotation id entity from a node id entity and an index.
     *
     * @param nodeId the node id of the workflow annotation is part of
     * @param index
     */
    public AnnotationIDEnt(final NodeIDEnt nodeId, final int index) {
        m_nodeId = nodeId;
        m_index = index;
    }

    /**
     * Creates a new annotation id entity from a {@link WorkflowAnnotationID}.
     *
     * @param id
     */
    public AnnotationIDEnt(final WorkflowAnnotationID id) {
        this(new NodeIDEnt(id.getNodeID()), id.getIndex());
    }

    /**
     * Deserialization constructor.
     *
     * @param s string representation as returned by {@link #toString()}
     */
    public AnnotationIDEnt(final String s) {
        String[] split = s.split("_");
        m_nodeId = new NodeIDEnt(split[0]);
        m_index = Integer.valueOf(split[1]);
    }

    @Override
    public String toString() {
        return m_nodeId.toString() + "_" + m_index;
    }

    /**
     * @return the id of the node the workflow annotation is part of
     */
    public NodeIDEnt getNodeIDEnt() {
        return m_nodeId;
    }

    /**
     * @return its index in that workflow
     */
    public int getIndex() {
        return m_index;
    }

    @Override
    public int hashCode() {
        return Objects.hash(m_nodeId.hashCode(), m_index);
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
        AnnotationIDEnt ent = (AnnotationIDEnt)o;
        return Objects.equals(m_nodeId, ent.m_nodeId) && m_index == ent.m_index;
    }

}

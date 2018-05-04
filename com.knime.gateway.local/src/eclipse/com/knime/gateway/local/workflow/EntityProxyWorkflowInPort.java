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
package com.knime.gateway.local.workflow;

import org.knime.core.ui.node.workflow.NodeOutPortUI;
import org.knime.core.ui.node.workflow.WorkflowInPortUI;

import com.knime.gateway.v0.entity.NodeEnt;
import com.knime.gateway.v0.entity.NodeInPortEnt;
import com.knime.gateway.v0.entity.NodeOutPortEnt;

/**
 * Entity-proxy class that proxies {@link NodeInPortEnt} and implements {@link WorkflowInPortUI}.
 *
 * @author Martin Horn, University of Konstanz
 */
class EntityProxyWorkflowInPort extends EntityProxyNodeInPort implements WorkflowInPortUI {

    private NodeOutPortEnt m_underlyingPort;

    private NodeEnt m_node;

    /**
     * See {@link AbstractEntityProxy#AbstractEntityProxy(com.knime.gateway.entity.GatewayEntity, EntityProxyAccess)}.
     *
     * @param inPort
     * @param underlyingPort the underlying port that is wrapped with this port
     * @param node the node the underlying port belongs to
     * @param access
     */
    EntityProxyWorkflowInPort(final NodeInPortEnt inPort, final NodeOutPortEnt underlyingPort,
        final NodeEnt node, final EntityProxyAccess access) {
        super(inPort, access);
        m_underlyingPort = underlyingPort;
        m_node = node;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setPortIndex(final int portIndex) {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public NodeOutPortUI getUnderlyingPort() {
        return getAccess().getNodeOutPort(m_underlyingPort, m_node);
    }

}

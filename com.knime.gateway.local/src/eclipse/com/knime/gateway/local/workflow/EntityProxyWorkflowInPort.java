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

import com.knime.gateway.v0.entity.NodeInPortEnt;

/**
 * Entity-proxy class that proxies {@link NodeInPortEnt} and implements {@link WorkflowInPortUI}.
 *
 * @author Martin Horn, University of Konstanz
 */
class EntityProxyWorkflowInPort extends EntityProxyNodeInPort implements WorkflowInPortUI {

    /**
     * See {@link AbstractEntityProxy#AbstractEntityProxy(com.knime.gateway.entity.GatewayEntity, EntityProxyAccess)}.
     *
     * @param inPort
     * @param node the node the underlying port belongs to
     * @param access
     */
    EntityProxyWorkflowInPort(final NodeInPortEnt inPort, final EntityProxyAccess access) {
        super(inPort, access);
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
        //TODO no underlying port available so far in this implementation
        return null;
    }

}

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

import org.knime.core.node.port.PortType;
import org.knime.core.node.port.PortTypeRegistry;
import org.knime.core.ui.node.workflow.NodeInPortUI;

import com.knime.gateway.entity.NodeInPortEnt;
import com.knime.gateway.entity.PortTypeEnt;
import com.knime.gateway.local.util.missing.MissingPortObject;

/**
 * Entity-proxy class that proxies {@link NodeInPortEnt} and implements {@link NodeInPortUI}.
 *
 * @author Martin Horn, University of Konstanz
 */
class EntityProxyNodeInPort extends AbstractEntityProxy<NodeInPortEnt> implements NodeInPortUI {


    /**
     * See {@link AbstractEntityProxy#AbstractEntityProxy(com.knime.gateway.entity.GatewayEntity, EntityProxyAccess)}.
     *
     * @param inPort
     * @param access
     */
    EntityProxyNodeInPort(final NodeInPortEnt inPort, final EntityProxyAccess access) {
        super(inPort, access);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getPortIndex() {
        return getEntity().getPortIndex();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PortType getPortType() {
        PortTypeEnt pte = getEntity().getPortType();
        PortTypeRegistry ptr = PortTypeRegistry.getInstance();
        return ptr.getPortType(ptr.getObjectClass(pte.getPortObjectClassName()).orElse(MissingPortObject.class),
            pte.isOptional());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getPortName() {
        return getEntity().getPortName();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setPortName(final String portName) {
        throw new UnsupportedOperationException();
    }

}
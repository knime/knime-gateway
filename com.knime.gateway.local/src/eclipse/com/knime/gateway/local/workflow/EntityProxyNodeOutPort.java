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

import org.knime.core.node.port.PortObject;
import org.knime.core.node.port.PortType;
import org.knime.core.node.port.PortTypeRegistry;
import org.knime.core.node.workflow.NodeContainerState;
import org.knime.core.node.workflow.NodeStateChangeListener;
import org.knime.core.node.workflow.NodeStateEvent;
import org.knime.core.ui.node.workflow.NodeOutPortUI;

import com.knime.gateway.local.util.missing.MissingPortObject;
import com.knime.gateway.v0.entity.NodeEnt;
import com.knime.gateway.v0.entity.NodeOutPortEnt;
import com.knime.gateway.v0.entity.PortTypeEnt;

/**
 * Entity-proxy class that proxies {@link NodeOutPortEnt} and implements {@link NodeOutPortUI}.
 *
 * @author Martin Horn, University of Konstanz
 */
public class EntityProxyNodeOutPort extends AbstractEntityProxy<NodeOutPortEnt> implements NodeOutPortUI {

    private NodeEnt m_node;

    /**
     * @param outPort
     * @param node the node this port belongs to
     *
     */
    public EntityProxyNodeOutPort(final NodeOutPortEnt outPort, final NodeEnt node, final EntityProxyAccess access) {
        super(outPort, access);
        m_node = node;
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
        Class<? extends PortObject> portObjectClass =
            ptr.getObjectClass(pte.getPortObjectClassName()).orElseGet(() -> MissingPortObject.class);
        return ptr.getPortType(portObjectClass, pte.isOptional());
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

    /**
     * {@inheritDoc}
     */
    @Override
    public void stateChanged(final NodeStateEvent state) {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public NodeContainerState getNodeContainerState() {
        return getNodeState();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean addNodeStateChangeListener(final NodeStateChangeListener listener) {
        // TODO
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean removeNodeStateChangeListener(final NodeStateChangeListener listener) {
        //TODO
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getPortSummary() {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isInactive() {
        return getEntity().isInactive();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public NodeContainerState getNodeState() {
        return EntityProxyNodeContainer.getNodeContainerState(m_node);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void notifyNodeStateChangeListener(final NodeStateEvent e) {
        throw new UnsupportedOperationException();
    }

}

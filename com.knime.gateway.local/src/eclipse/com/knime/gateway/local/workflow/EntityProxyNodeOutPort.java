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

import java.util.LinkedHashSet;
import java.util.Set;

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
 * @param <N> type of the node the port belongs to
 */
public class EntityProxyNodeOutPort<N extends NodeEnt> extends AbstractEntityProxy<NodeOutPortEnt> implements NodeOutPortUI {

    private N m_node;

    private final Set<NodeStateChangeListener> m_listener;

    /**
     * @param outPort
     * @param node the node this port belongs to
     * @param access
     *
     */
    public EntityProxyNodeOutPort(final NodeOutPortEnt outPort, final N node, final EntityProxyAccess access) {
        super(outPort, access);
        m_node = node;
        m_listener = new LinkedHashSet<>();
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
        notifyNodeStateChangeListener(state);
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
        return m_listener.add(listener);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean removeNodeStateChangeListener(final NodeStateChangeListener listener) {
        return m_listener.remove(listener);
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
        m_listener.stream().forEach(l -> l.stateChanged(e));
    }

    /**
     * @return the node entity representing the node this port belongs to
     */
    protected N getNodeEnt() {
        return m_node;
    }

    void updateNodeEnt(final N newNodeEnt) {
        m_node = newNodeEnt;
    }

}

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

import static java.util.concurrent.CompletableFuture.completedFuture;
import static java.util.concurrent.CompletableFuture.supplyAsync;

import java.awt.Rectangle;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

import javax.swing.JComponent;
import javax.swing.SwingUtilities;

import org.eclipse.swt.widgets.Display;
import org.knime.core.data.DataTableSpec;
import org.knime.core.node.BufferedDataTable;
import org.knime.core.node.NodeLogger;
import org.knime.core.node.port.PortObject;
import org.knime.core.node.port.PortObjectSpec;
import org.knime.core.node.port.PortType;
import org.knime.core.node.util.ViewUtils;
import org.knime.core.node.workflow.CredentialsProvider;
import org.knime.core.node.workflow.FlowObjectStack;
import org.knime.core.node.workflow.NodeContainerState;
import org.knime.core.node.workflow.NodeStateChangeListener;
import org.knime.core.node.workflow.NodeStateEvent;
import org.knime.core.node.workflow.OutPortView;
import org.knime.core.ui.node.workflow.NodeOutPortUI;
import org.knime.core.ui.node.workflow.async.AsyncNodeOutPortUI;
import org.knime.workbench.ui.async.AsyncUtil;

import com.knime.gateway.v0.entity.NodeEnt;
import com.knime.gateway.v0.entity.NodeOutPortEnt;
import com.knime.gateway.v0.service.util.ServiceExceptions.InvalidRequestException;
import com.knime.gateway.v0.service.util.ServiceExceptions.NodeNotFoundException;

/**
 * Entity-proxy class that proxies {@link NodeOutPortEnt} and implements {@link NodeOutPortUI}.
 *
 * @author Martin Horn, University of Konstanz
 * @param <N> type of the node the port belongs to
 */
class EntityProxyNodeOutPort<N extends NodeEnt> extends AbstractEntityProxy<NodeOutPortEnt>
    implements AsyncNodeOutPortUI {

    private N m_node;

    private final Set<NodeStateChangeListener> m_listener;

    private OutPortView m_portView;

    private boolean m_portViewUpdateRequired = false;

    /**
     * See {@link AbstractEntityProxy#AbstractEntityProxy(com.knime.gateway.entity.GatewayEntity, EntityProxyAccess)}.
     *
     * @param outPort
     * @param node the node this port belongs to
     * @param access
     */
    EntityProxyNodeOutPort(final NodeOutPortEnt outPort, final N node, final EntityProxyAccess access) {
        super(outPort, access);
        m_node = node;
        m_listener = new LinkedHashSet<>();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CompletableFuture<PortObjectSpec> getPortObjectSpecAsync() {
        return supplyAsync(() -> {
            return getPortObjectSpecInternal();
        });
    }

    private PortObjectSpec getPortObjectSpecInternal() {
        try {
            return getAccess().getOutputPortObjectSpecs(m_node)[getEntity().getPortIndex()];
        } catch (InvalidRequestException | NodeNotFoundException ex) {
            //should never happen
            throw new RuntimeException(ex);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CompletableFuture<PortObject> getPortObjectAsync() {
        if (!getNodeState().isExecuted()) {
            return completedFuture(null);
        }
        if (getEntity().getPortType().getPortObjectClassName().equals(BufferedDataTable.class.getCanonicalName())) {
            return supplyAsync(() -> {
                return getAccess().getOutputDataTable(getEntity(), getNodeEnt(),
                    (DataTableSpec)getPortObjectSpecInternal());
            });
        } else {
            return completedFuture(new UnsupportedPortObject(getPortType()));
        }
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
        return EntityProxyAccess.getPortType(getEntity().getPortType());
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
        if (!getNodeContainerState().isExecutionInProgress()) {
            notifyNodeStateChangeListener(state);
            if (m_portView != null) {
                if (m_portView.isVisible()) {
                    try {
                        updatePortView();
                    } catch (Exception e) {
                        NodeLogger.getLogger(getClass()).error("Failed to update port view.", e);
                    }
                } else {
                    //port view not visible
                    //don't update port view but remember to update later
                    m_portViewUpdateRequired = true;
                }
            }
        }
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
        return getEntity().getSummary();
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
        return AbstractEntityProxyNodeContainer.getNodeContainerState(m_node);
    }

    @Override
    public FlowObjectStack getFlowObjectStack() {
        return getAccess().getOutputFlowVariableStack(getNodeEnt(), getAccess().getNodeID(getNodeEnt()));
    }

    @Override
    public void openPortView(final String name, final Rectangle knimeWindowBounds) {
        ViewUtils.invokeLaterInEDT(new Runnable() {
            @Override
            public void run() {
                openPortViewInEDT(name, knimeWindowBounds);
            }
        });
    }

    private void openPortViewInEDT(final String name, final Rectangle knimeWindowBounds) {
        assert SwingUtilities.isEventDispatchThread();
        if (m_portView == null) {
            String label = getAccess().getNodeID(getNodeEnt()).toString() + " - " + getNodeEnt().getName() + " (job)";
            m_portView = new OutPortView(label, name);
            updatePortView();
        } else {
            if (m_portViewUpdateRequired) {
                m_portViewUpdateRequired = false;
                updatePortView();
            }
        }
        m_portView.openView(knimeWindowBounds);
    }

    private void updatePortView() {
        CompletableFuture<PortObjectSpec> futurePortObjectSpec = getPortObjectSpecAsync();
        CompletableFuture<PortObject> futurePortObject = getPortObjectAsync();
        Display.getDefault().syncExec(() -> {
            AsyncUtil.waitForTermination(CompletableFuture.allOf(futurePortObject, futurePortObjectSpec),
                "Loading port object ...");
        });
        m_portView.update(futurePortObject.getNow(null), futurePortObjectSpec.getNow(null), getFlowObjectStack(),
            CredentialsProvider.EMPTY_CREDENTIALS_PROVIDER, null);
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

    private static class UnsupportedPortObject implements PortObject {
        private final PortType m_type;

        UnsupportedPortObject(final PortType type) {
            m_type = type;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public String getSummary() {
            return "Port object of type " + m_type.getName() + " not yet supported by remote view.";
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public PortObjectSpec getSpec() {
            return null;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public JComponent[] getViews() {
            return new JComponent[0];
        }
    }
}

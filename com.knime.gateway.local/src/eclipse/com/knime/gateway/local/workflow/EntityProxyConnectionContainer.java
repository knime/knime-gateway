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

import java.util.List;
import java.util.UUID;

import org.knime.core.node.workflow.ConnectionContainer;
import org.knime.core.node.workflow.ConnectionContainer.ConnectionType;
import org.knime.core.node.workflow.ConnectionID;
import org.knime.core.node.workflow.ConnectionProgressEvent;
import org.knime.core.node.workflow.ConnectionProgressListener;
import org.knime.core.node.workflow.ConnectionUIInformation;
import org.knime.core.node.workflow.ConnectionUIInformationListener;
import org.knime.core.node.workflow.NodeID;
import org.knime.core.ui.node.workflow.ConnectionContainerUI;
import org.knime.workbench.editor2.commands.DeleteCommand;

import com.knime.gateway.v0.entity.ConnectionEnt;
import com.knime.gateway.v0.entity.XYEnt;

/**
 * Entity-proxy class that proxies {@link ConnectionEnt} and implements {@link ConnectionContainerUI}.
 *
 * @author Martin Horn, University of Konstanz
 */
class EntityProxyConnectionContainer extends AbstractEntityProxy<ConnectionEnt>
    implements ConnectionContainerUI {

    private UUID m_rootWorkflowID;

    /**
     * See {@link AbstractEntityProxy#AbstractEntityProxy(com.knime.gateway.entity.GatewayEntity, EntityProxyAccess)}.
     *
     * @param conn
     * @param access
     */
    EntityProxyConnectionContainer(final ConnectionEnt conn, final UUID rootWorkflowID, final EntityProxyAccess access) {
        super(conn, access);
        m_rootWorkflowID = rootWorkflowID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void progressChanged(final ConnectionProgressEvent pe) {
        //nothing to do so far
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ConnectionUIInformation getUIInfo() {
        List<? extends XYEnt> bendPoints = getEntity().getBendPoints();
        ConnectionUIInformation.Builder builder = ConnectionUIInformation.builder();
        for (int i = 0; i < bendPoints.size(); i++) {
            XYEnt xy = bendPoints.get(i);
            builder.addBendpoint(xy.getX(), xy.getY(), i);
        }
        return builder.build();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public NodeID getDest() {
        return getAccess().getNodeID(m_rootWorkflowID, getEntity().getDest());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getDestPort() {
        return getEntity().getDestPort();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public NodeID getSource() {
        return getAccess().getNodeID(m_rootWorkflowID, getEntity().getSource());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getSourcePort() {
        return getEntity().getSourcePort();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isDeletable() {
        return getEntity().isDeletable();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isFlowVariablePortConnection() {
        return getEntity().isFlowVariablePortConnection();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ConnectionType getType() {
        return ConnectionType.valueOf(getEntity().getType().toString());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ConnectionID getID() {
        return new ConnectionID(getDest(), getDestPort());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setUIInfo(final ConnectionUIInformation uiInfo) {

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addUIInformationListener(final ConnectionUIInformationListener l) {
        // TODO Auto-generated method stub

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeUIInformationListener(final ConnectionUIInformationListener l) {
        // TODO Auto-generated method stub

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addProgressListener(final ConnectionProgressListener listener) {
        // TODO Auto-generated method stub

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeProgressListener(final ConnectionProgressListener listener) {
        // TODO Auto-generated method stub

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void cleanup() {
        // TODO Auto-generated method stub

    }

    /**
     * {@inheritDoc}
     *
     * Needs to be implemented since, e.g., used in line 155 of {@link DeleteCommand}.
     *
     * Implementation taken from {@link ConnectionContainer#equals(Object)}.
     */
    @Override
    public boolean equals(final Object obj) {
        if (!(obj instanceof EntityProxyConnectionContainer)) {
            return false;
        }
        EntityProxyConnectionContainer cc = (EntityProxyConnectionContainer)obj;
        return getDest().equals(cc.getDest()) && (getDestPort() == cc.getDestPort())
            && getSource().equals(cc.getSource()) && (getSourcePort() == cc.getSourcePort())
            && getType().equals(cc.getType());
    }

    /**
     * {@inheritDoc}
     *
     * Needs to be implemented since, e.g., used in line 155 of {@link DeleteCommand}.
     *
     * Implementation taken from {@link ConnectionContainer#hashCode()}.
     */
    @Override
    public int hashCode() {
        return getDest().hashCode() + getSource().hashCode() + getDestPort() + getSourcePort() + getType().hashCode();
    }

}

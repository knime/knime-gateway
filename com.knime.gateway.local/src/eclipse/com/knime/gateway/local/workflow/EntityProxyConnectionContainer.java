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

import org.knime.core.node.workflow.ConnectionContainer.ConnectionType;
import org.knime.core.node.workflow.ConnectionID;
import org.knime.core.node.workflow.ConnectionProgressEvent;
import org.knime.core.node.workflow.ConnectionProgressListener;
import org.knime.core.node.workflow.ConnectionUIInformation;
import org.knime.core.node.workflow.ConnectionUIInformationListener;
import org.knime.core.node.workflow.NodeID;
import org.knime.core.ui.node.workflow.ConnectionContainerUI;

import com.knime.gateway.v0.entity.ConnectionEnt;
import com.knime.gateway.v0.entity.XYEnt;

/**
 * Entity-proxy class that proxies {@link ConnectionEnt} and implements {@link ConnectionContainerUI}.
 *
 * @author Martin Horn, University of Konstanz
 */
public class EntityProxyConnectionContainer extends AbstractEntityProxy<ConnectionEnt>
    implements ConnectionContainerUI {

    /**
     * @param conn
     * @param access
     */
    public EntityProxyConnectionContainer(final ConnectionEnt conn, final EntityProxyAccess access) {
        super(conn, access);
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
        return NodeID.fromString(getEntity().getDest());
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
        return NodeID.fromString(getEntity().getSource());
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
        throw new UnsupportedOperationException();
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

}

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

import org.knime.core.ui.node.workflow.WorkflowOutPortUI;

import com.knime.gateway.v0.entity.NodeEnt;
import com.knime.gateway.v0.entity.NodeOutPortEnt;

/**
 * Entity-proxy class that proxies {@link NodeOutPortEnt} and implements {@link WorkflowOutPortUI}.
 *
 * @author Martin Horn, University of Konstanz
 */
public class EntityProxyWorkflowOutPort extends EntityProxyNodeOutPort implements WorkflowOutPortUI {

    /**
     *
     */
    public EntityProxyWorkflowOutPort(final NodeOutPortEnt outPort, final NodeEnt node,
        final EntityProxyAccess access) {
        super(outPort, node, access);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setPortIndex(final int portIndex) {
        throw new UnsupportedOperationException();
    }

}

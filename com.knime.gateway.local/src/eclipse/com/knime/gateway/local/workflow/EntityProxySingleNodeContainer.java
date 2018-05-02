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

import org.knime.core.ui.node.workflow.SingleNodeContainerUI;

import com.knime.gateway.v0.entity.NodeEnt;

/**
 * Entity-proxy class that proxies {@link NodeEnt} and implements {@link SingleNodeContainerUI}.
 *
 * @author Martin Horn, University of Konstanz
 *
 * @param <E> the type of the node entity
 */
public abstract class EntityProxySingleNodeContainer<E extends NodeEnt> extends EntityProxyNodeContainer<E>
    implements SingleNodeContainerUI {

    /**
     * @param node
     * @param access
     */
    public EntityProxySingleNodeContainer(final E node, final EntityProxyAccess access) {
        super(node, access);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isMemberOfScope() {
        // TODO
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @Override
    public void update(final NodeEnt entity) {
        //update port entities, too -> makes sure that still the very same entity proxy classes are used
        getAccess().updateNodeInPorts(getEntity(), entity);
        getAccess().updateNodeOutPorts(getEntity(), entity);

        //TODO update all other entity proxies referenced from the node container,
        //e.g. node annotation, node connections, etc.

        super.update((E)entity);
    }

}

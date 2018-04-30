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

import com.knime.gateway.entity.GatewayEntity;

/**
 * Implementations translate/proxy certain calls to the entities they use as the main source of information.
 *
 * @author Martin Horn, Univerity of Konstanz
 * @param <E> the underlying entity
 */
public interface EntityProxy<E extends GatewayEntity> {

    /**
     * @return the proxy's entity
     */
    E getEntity();

    /**
     * @return the entity proxy store to create/get (new) entity proxy instances
     */
    EntityProxyAccess getAccess();

    /**
     * Updates the proxy's entity. This method is really only meant to propagate the entity updates! No events should be triggered here or other method of entity proxy-classes accessed.
     * The whole object tree might be in a 'inconsistent' state while updating.
     *
     * Use {@link #postUpdate()}, e.g., in order trigger events necessary after the update.
     *
     * @param entity the new entity
     */
    void update(final E entity);

    /**
     * Called after the underlying entity has been updated via {@link #update(GatewayEntity)}.
     */
    void postUpdate();
}

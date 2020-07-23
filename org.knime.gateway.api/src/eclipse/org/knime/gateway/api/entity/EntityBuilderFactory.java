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
package org.knime.gateway.api.entity;

/**
 * Interface to be implemented by plugins that make use of the entity builder factory extension point.
 * Delivers concrete implementations for given entity builder interfaces (see also {@link EntityBuilderManager}).
 *
 * @author Martin Horn, University of Konstanz
 */
public interface EntityBuilderFactory {

    static final String EXT_POINT_ID = "com.knime.gateway.entity.EntityBuilderFactory";

    static final String EXT_POINT_ATTR = "EntityBuilderFactory";

    /**
     * Normal priority, <code>0</code>.
     */
    public static final int NORMAL_PRIORITY = 0;

    /**
     * @return the priority with what that entity builder will be used in case of multiple registered entity builders in
     *         the {@link EntityBuilderManager}
     */
    default int getPriority() {
        return NORMAL_PRIORITY;
    }

    /**
     * Creates an instance for the demanded entity builder interface.

     * @param builderInterface
     * @return an instance of the requested builder interface
     */
    <E extends GatewayEntity, B extends GatewayEntityBuilder<E>> B createEntityBuilder(Class<B> builderInterface);

}

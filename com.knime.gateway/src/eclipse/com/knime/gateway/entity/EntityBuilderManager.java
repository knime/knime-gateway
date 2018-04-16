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
package com.knime.gateway.entity;

import java.util.Collections;
import java.util.List;

import org.knime.core.node.NodeLogger;

import com.knime.gateway.util.ExtPointUtil;

/**
 * Manages entity builders (i.e. {@link GatewayEntityBuilder}s) and gives access to they implementations (that are
 * injected via the {@link EntityBuilderFactory} extension point).
 *
 * @author Martin Horn, University of Konstanz
 */
public class EntityBuilderManager {

    private static final NodeLogger LOGGER = NodeLogger.getLogger(EntityBuilderManager.class);

    private static EntityBuilderFactory BUILDER_FACTORY;

    private EntityBuilderManager() {
        //utility class
    }

    /**
     * Delivers implementations for entity builder interfaces (see {@link GatewayEntityBuilder}). Implementations are
     * injected via {@link EntityBuilderFactory} extension point.
     *
     * @param builderInterface the builder interface the implementation is requested for
     * @return an implementation of the requested builder interface (it returns a new instance with every method call)
     */
    public static <E extends GatewayEntity, B extends GatewayEntityBuilder<E>> B
        builder(final Class<B> builderInterface) {
        if (BUILDER_FACTORY == null) {
            BUILDER_FACTORY = createBuilderFactory();
        }
        return BUILDER_FACTORY.createEntityBuilder(builderInterface);
    }

    private static EntityBuilderFactory createBuilderFactory() {

        List<EntityBuilderFactory> instances = ExtPointUtil
            .collectExecutableExtensions(EntityBuilderFactory.EXT_POINT_ID, EntityBuilderFactory.EXT_POINT_ATTR);
        if (instances.size() == 0) {
            LOGGER.warn("No entity builder factory registered. Default factory used.");
            return new DefaultEntityBuilderFactory();

        } else if (instances.size() > 1) {
            LOGGER.warn("Multiple entity builder factories registered. The one with the highest priority used.");
            Collections.sort(instances, (o1, o2) -> Integer.compare(o2.getPriority(), o1.getPriority()));
        }
        return instances.get(0);
    }

}

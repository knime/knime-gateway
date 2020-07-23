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
package org.knime.gateway.impl.entity;

import org.knime.gateway.api.entity.EntityBuilderFactory;
import org.knime.gateway.api.entity.GatewayEntity;
import org.knime.gateway.api.entity.GatewayEntityBuilder;
import org.knime.gateway.impl.entity.util.Interface2ImplMap;

/**
 * Default implementation of the {@link EntityBuilderFactory}. It returns the default implementation of the respective
 * entity builder interfaces.
 *
 * @author Martin Horn, University of Konstanz
 */
public class DefaultEntityBuilderFactory implements EntityBuilderFactory {

    /**
     * {@inheritDoc}
     */
    @Override
    public <E extends GatewayEntity, B extends GatewayEntityBuilder<E>> B
        createEntityBuilder(final Class<B> builderInterface) {
        return (B)Interface2ImplMap.get(builderInterface);
    }

}

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
package com.knime.gateway.rest.client;

import java.io.IOException;

import com.knime.gateway.local.service.ServerServiceConfig;
import com.knime.gateway.local.service.ServiceConfig;
import com.knime.gateway.local.service.ServiceFactory;
import com.knime.gateway.service.GatewayService;

/**
 * Service factories whose returned services talk to the KNIME server's gateway rest interface.
 *
 * @author Martin Horn, University of Konstanz
 */
public class RestClientServiceFactory implements ServiceFactory {

    /**
     * {@inheritDoc}
     */
    @Override
    public int getPriority() {
        return NORMAL_PRIORITY + 1;
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @Override
    public <S extends GatewayService> S createService(final Class<S> serviceInterface,
        final ServiceConfig serviceConfig) {
        if (serviceConfig instanceof ServerServiceConfig) {
            ServerServiceConfig serverServiceConfig = (ServerServiceConfig)serviceConfig;
            try {
                return (S)ServiceInterface2RestClientMap.get(serviceInterface, serverServiceConfig.getURI(),
                    serverServiceConfig.getJWT().orElse(null), serverServiceConfig.getServerVersion().orElse(null));
            } catch (InstantiationException | IllegalAccessException | IOException ex) {
                throw new RuntimeException(ex);
            }
        } else {
            throw new IllegalStateException("No server service config given!");
        }
    }
}

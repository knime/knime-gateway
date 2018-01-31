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

import org.knime.gateway.local.service.ServerServiceConfig;
import org.knime.gateway.local.service.ServiceConfig;
import org.knime.gateway.local.service.ServiceFactory;
import org.knime.gateway.service.GatewayService;
import org.knime.gateway.v0.service.NodeService;
import org.knime.gateway.v0.service.WorkflowService;

import com.knime.gateway.rest.client.service.NodeClient;
import com.knime.gateway.rest.client.service.WorkflowClient;

/**
 * Service factories whose returned services talk to a http(s) server at "v4/gateway/jsonrpc" by 'posting' json-rpc
 * messages.
 *
 * @author Martin Horn, University of Konstanz
 */
public class JaxRsClientServiceFactory implements ServiceFactory {

    private static final String GATEWAY_PATH = "/v4/jobs/{uuid}/gateway/jsonrpc";

    /**
     * {@inheritDoc}
     */
    @Override
    public <S extends GatewayService> S createService(final Class<S> serviceInterface,
        final ServiceConfig serviceConfig) {
        if (serviceConfig instanceof ServerServiceConfig) {
            ServerServiceConfig serverServiceConfig = (ServerServiceConfig)serviceConfig;
            if (serviceInterface == WorkflowService.class) {
                try {
                    return (S)new WorkflowClient(serverServiceConfig.getURI(),
                        serverServiceConfig.getJWT().orElse(null));
                } catch (InstantiationException | IllegalAccessException | IOException ex) {
                    // TODO exception handling
                    throw new RuntimeException(ex);
                }
            } else if(serviceInterface == NodeService.class){
                try {
                    return (S)new NodeClient(serverServiceConfig.getURI(),
                        serverServiceConfig.getJWT().orElse(null));
                } catch (InstantiationException | IllegalAccessException | IOException ex) {
                    // TODO exception handling
                    throw new RuntimeException(ex);
                }
            } else {
                throw new IllegalArgumentException("Unsupported service.");
            }
        } else {
            throw new IllegalStateException("No server service config given!");
        }
    }
}

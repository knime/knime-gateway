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
package com.knime.gateway.local.service;

import com.knime.gateway.service.GatewayService;

/**
 * Interface to be implemented by plugins that make use of the service factory extension point. Delivers concrete
 * implementations for given service interfaces (see also {@link ServiceManager}).
 *
 * Service factories usually communicate to a specific gateway endpoint (see com.knime.gateway.remote.GatewayEndpoint),
 * possibly mediated by the KNME-server.
 *
 * @author Martin Horn, University of Konstanz
 */
public interface ServiceFactory {

    static final String EXT_POINT_ID = "com.knime.gateway.local.service.ServiceFactory";

    static final String EXT_POINT_ATTR = "ServiceFactory";

    /**
     * Normal priority, <code>0</code>.
     */
    public static final int NORMAL_PRIORITY = 0;

    /**
     * @return the priority with what that service will be used in case of multiple registered services in the
     *         {@link ServiceManager}
     */
    default int getPriority() {
        return NORMAL_PRIORITY;
    }

    /**
     * Creates an instance for the demanded server interface. The returned implementation very likely represents a
     * client that communicates with the respective server.
     *
     * @param serviceInterface the service to create
     * @param serviceConfig a configuration object with more parameters required for service creation
     * @return a new instance of the service interface or <code>null</code> if the service cannot be created (e.g.
     *         because a service config object that cannot be handled)
     */
    <S extends GatewayService> S createService(Class<S> serviceInterface, ServiceConfig serviceConfig);
}

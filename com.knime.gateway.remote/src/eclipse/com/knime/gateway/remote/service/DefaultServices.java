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
package com.knime.gateway.remote.service;

import java.util.NoSuchElementException;

import com.knime.gateway.service.GatewayService;
import com.knime.gateway.v0.service.AnnotationService;
import com.knime.gateway.v0.service.ExecEnvService;
import com.knime.gateway.v0.service.NodeService;
import com.knime.gateway.v0.service.StaticNodeService;
import com.knime.gateway.v0.service.WorkflowService;

/**
 * Provides the default service implementations for gateway services.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
public class DefaultServices {

    private DefaultServices() {
        //utility class
    }

    /**
     * Maps a service interface to its default service implementation.
     *
     * @param serviceInterface the service interface the default implementation is requested for
     * @return the default implementation for the provided service interface
     * @throws NoSuchElementException if no default service implementation has been found
     */
    @SuppressWarnings("unchecked")
    public static <S extends GatewayService> S getDefaultService(final Class<S> serviceInterface)
        throws NoSuchElementException {
        if(serviceInterface.equals(WorkflowService.class)) {
            return (S)DefaultWorkflowService.getInstance();
        } else if(serviceInterface.equals(NodeService.class)) {
            return (S)DefaultNodeService.getInstance();
        } else if (serviceInterface.equals(AnnotationService.class)) {
            return (S)DefaultAnnotationService.getInstance();
        } else if (serviceInterface.equals(StaticNodeService.class)) {
            return (S)DefaultStaticNodeService.getInstance();
        } else if (serviceInterface.equals(ExecEnvService.class)) {
            return (S)DefaultExecEnvService.getInstance();
        } else {
            throw new NoSuchElementException(
                "No default service implementation found for " + serviceInterface.getSimpleName());
        }
    }
}

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

import com.knime.gateway.rest.client.service.NodeClient;
import com.knime.gateway.v0.service.NodeService;
import com.knime.gateway.rest.client.service.WorkflowClient;
import com.knime.gateway.v0.service.WorkflowService;

import java.io.IOException;
import java.net.URI;

import com.knime.gateway.service.GatewayService;

/**
 * Maps a service interface (i.e. it's class) to the respective rest client implementation.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
@javax.annotation.Generated(value = "org.knime.gateway.codegen.GatewayCodegen")
public class ServiceInterface2RestClientMap {

    private ServiceInterface2RestClientMap() {
        //utility class
    }

    /**
     * Maps a gateway service (i.e. its interface class) to an instance of the corresponding rest client implementation.
     *
     * @param clazz the service class to be mapped to the corresponding rest client implementation
     * @param restAddress the rest adress
     * @param jwt an optional json web token for authentication
     * @return a new instance of the respective rest client
     * @throws InstantiationException
     * @throws IllegalAccessException
     * @throws IOException
     */
    public static AbstractGatewayClient<?> get(Class<? extends GatewayService> clazz, final URI restAddress,
        final String jwt)  throws InstantiationException, IllegalAccessException, IOException {
        if(clazz == NodeService.class) {
            return new NodeClient(restAddress, jwt);
        }        
        if(clazz == WorkflowService.class) {
            return new WorkflowClient(restAddress, jwt);
        }        
        else {
            throw new IllegalArgumentException("No service mapping.");
        }    
    }
}
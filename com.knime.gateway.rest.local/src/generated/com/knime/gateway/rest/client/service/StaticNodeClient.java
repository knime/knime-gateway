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
package com.knime.gateway.rest.client.service;

import com.knime.gateway.v0.entity.NodeCategoryEnt;


import java.io.IOException;
import java.net.URI;
import java.util.concurrent.TimeoutException;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.GenericType;

import com.knime.gateway.service.ServiceException;
import com.knime.gateway.v0.service.StaticNodeService;
import com.knime.gateway.v0.service.util.ServiceExceptions;

import com.knime.enterprise.gateway.rest.api.StaticNode;
import com.knime.enterprise.utility.ExecutorException;
import com.knime.enterprise.utility.PermissionException;
import com.knime.gateway.rest.client.AbstractGatewayClient;

/**
 * Client that provides access to a KNIME Gateway's <tt>StaticNode</tt> resource. 
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
@javax.annotation.Generated(value = "org.knime.gateway.codegen.GatewayCodegen")
public class StaticNodeClient extends AbstractGatewayClient<StaticNode> implements StaticNodeService {

    /**
     * See {@link AbstractGatewayClient#AbstractGatewayClient(URI, String)}.
     *
     * @param restAddress
     * @param jwt
     * @throws InstantiationException
     * @throws IllegalAccessException
     * @throws IOException
     */
    public StaticNodeClient(final URI restAddress, final String jwt)
        throws InstantiationException, IllegalAccessException, IOException {
        super(restAddress, jwt);
    }
    
    @Override
    public NodeCategoryEnt getAllNodes(java.util.UUID jobId, String nodeType)  {
        try{
            return doRequest(c -> {
                try {
                    return c.getAllNodes(jobId, nodeType);
                } catch (PermissionException | ExecutorException | IOException | TimeoutException ex) {
                    //server errors
                    // TODO exception handling
                    throw new RuntimeException(ex);
                }
            }, NodeCategoryEnt.class);
        } catch (WebApplicationException ex) {
            //executor errors
            throw new ServiceException(
                "Error response with status code '" + ex.getResponse().getStatus() + "' and message: " + readExceptionMessage(ex));
        }
    }
    
}

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

import org.knime.gateway.v0.entity.WorkflowEnt;


import java.io.IOException;
import java.net.URI;
import java.util.concurrent.TimeoutException;

import javax.ws.rs.WebApplicationException;

import org.knime.gateway.service.ServiceException;
import org.knime.gateway.v0.service.WorkflowService;
import org.knime.gateway.v0.service.util.ServiceExceptions;

import com.knime.enterprise.gateway.rest.api.Workflow;
import com.knime.enterprise.utility.ExecutorException;
import com.knime.enterprise.utility.PermissionException;
import com.knime.gateway.rest.client.AbstractGatewayClient;

/**
 * Client that provides access to a KNIME Gateway's <tt>Workflow</tt> resource. 
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
@javax.annotation.Generated(value = "org.knime.gateway.codegen.GatewayCodegen")
public class WorkflowClient extends AbstractGatewayClient<Workflow> implements WorkflowService {

    /**
     * See {@link AbstractGatewayClient#AbstractGatewayClient(URI, String)}.
     *
     * @param restAddress
     * @param jwt
     * @throws InstantiationException
     * @throws IllegalAccessException
     * @throws IOException
     */
    public WorkflowClient(final URI restAddress, final String jwt)
        throws InstantiationException, IllegalAccessException, IOException {
        super(restAddress, jwt);
    }
    
    @Override
    public WorkflowEnt getSubWorkflow(java.util.UUID jobId, String nodeId)  throws ServiceExceptions.NotASubWorkflowException, ServiceExceptions.NodeNotFoundException {
        try{
            return doRequest(c -> {
                try {
                    return c.getSubWorkflow(jobId, nodeId);
                } catch (PermissionException | ExecutorException | IOException | TimeoutException ex) {
                    //server errors
                    // TODO exception handling
                    throw new RuntimeException(ex);
                }
            }, WorkflowEnt.class);
        } catch (WebApplicationException ex) {
            //executor errors
            if (ex.getResponse().getStatus() == 400) {
                throw new ServiceExceptions.NotASubWorkflowException(readExceptionMessage(ex.getResponse()));
            }
            if (ex.getResponse().getStatus() == 404) {
                throw new ServiceExceptions.NodeNotFoundException(readExceptionMessage(ex.getResponse()));
            }
            throw new ServiceException(
                "Error response with status code '" + ex.getResponse().getStatus() + "' and message: " + readExceptionMessage(ex.getResponse()));
        }
    }
    
    @Override
    public WorkflowEnt getWorkflow(java.util.UUID jobId)  {
        try{
            return doRequest(c -> {
                try {
                    return c.getWorkflow(jobId);
                } catch (PermissionException | ExecutorException | IOException | TimeoutException ex) {
                    //server errors
                    // TODO exception handling
                    throw new RuntimeException(ex);
                }
            }, WorkflowEnt.class);
        } catch (WebApplicationException ex) {
            //executor errors
            throw new ServiceException(
                "Error response with status code '" + ex.getResponse().getStatus() + "' and message: " + readExceptionMessage(ex.getResponse()));
        }
    }
    
}

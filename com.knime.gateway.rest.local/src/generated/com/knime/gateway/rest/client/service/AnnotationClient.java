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

import com.knime.gateway.v0.entity.BoundsEnt;


import java.io.IOException;
import java.net.URI;
import java.util.concurrent.TimeoutException;

import javax.ws.rs.ProcessingException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.GenericType;

import com.knime.gateway.service.ServiceException;
import com.knime.gateway.v0.service.AnnotationService;
import com.knime.gateway.v0.service.util.ServiceExceptions;

import com.knime.enterprise.gateway.rest.api.Annotation;
import com.knime.enterprise.utility.ExecutorException;
import com.knime.enterprise.utility.PermissionException;
import com.knime.gateway.rest.client.AbstractGatewayClient;

/**
 * Client that provides access to a KNIME Gateway's <tt>Annotation</tt> resource. 
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
@javax.annotation.Generated(value = "org.knime.gateway.codegen.GatewayCodegen")
public class AnnotationClient extends AbstractGatewayClient<Annotation> implements AnnotationService {

    /**
     * See {@link AbstractGatewayClient#AbstractGatewayClient(URI, String)}.
     *
     * @param restAddress
     * @param jwt
     * @throws InstantiationException
     * @throws IllegalAccessException
     * @throws IOException
     */
    public AnnotationClient(final URI restAddress, final String jwt)
        throws InstantiationException, IllegalAccessException, IOException {
        super(restAddress, jwt);
    }
    
    @Override
    public void setAnnotationBounds(java.util.UUID jobId, String annoId, BoundsEnt boundsEnt)  throws ServiceExceptions.NotFoundException {
        try{
            doRequest(c -> {
                try {
                    return c.setAnnotationBounds(jobId, annoId, toByteArray(boundsEnt));
                } catch (PermissionException | ExecutorException | IOException | TimeoutException ex) {
                    //server errors
                    throw new ServiceException("Internal server error.", ex);
                } catch (ProcessingException e) {
                    //in case the server cannot be reached (timeout, connection refused)
                    throw new ServiceException("Server doesn't seem to be reachable.",
                        e.getCause());
                }
            });
        } catch (WebApplicationException ex) {
            //executor errors
            com.knime.gateway.v0.entity.GatewayExceptionEnt gatewayException = readAndParseGatewayExceptionResponse(ex);
            if (gatewayException.getExceptionName().equals("NotFoundException")) {
                throw new ServiceExceptions.NotFoundException(gatewayException.getExceptionMessage());
            }
            throw new ServiceException("Undefined service exception '" + gatewayException.getExceptionName()
                + "' with message: " + gatewayException.getExceptionMessage());
        }
    }
    
    @Override
    public void setAnnotationBoundsInSubWorkflow(java.util.UUID jobId, String nodeId, String annoId, BoundsEnt boundsEnt)  throws ServiceExceptions.NotASubWorkflowException, ServiceExceptions.NotFoundException {
        try{
            doRequest(c -> {
                try {
                    return c.setAnnotationBoundsInSubWorkflow(jobId, nodeId, annoId, toByteArray(boundsEnt));
                } catch (PermissionException | ExecutorException | IOException | TimeoutException ex) {
                    //server errors
                    throw new ServiceException("Internal server error.", ex);
                } catch (ProcessingException e) {
                    //in case the server cannot be reached (timeout, connection refused)
                    throw new ServiceException("Server doesn't seem to be reachable.",
                        e.getCause());
                }
            });
        } catch (WebApplicationException ex) {
            //executor errors
            com.knime.gateway.v0.entity.GatewayExceptionEnt gatewayException = readAndParseGatewayExceptionResponse(ex);
            if (gatewayException.getExceptionName().equals("NotASubWorkflowException")) {
                throw new ServiceExceptions.NotASubWorkflowException(gatewayException.getExceptionMessage());
            }
            if (gatewayException.getExceptionName().equals("NotFoundException")) {
                throw new ServiceExceptions.NotFoundException(gatewayException.getExceptionMessage());
            }
            throw new ServiceException("Undefined service exception '" + gatewayException.getExceptionName()
                + "' with message: " + gatewayException.getExceptionMessage());
        }
    }
    
}

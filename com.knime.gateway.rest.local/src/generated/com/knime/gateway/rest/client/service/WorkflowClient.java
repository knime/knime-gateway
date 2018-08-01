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

import com.knime.gateway.v0.entity.ConnectionEnt;
import com.knime.gateway.v0.entity.PatchEnt;
import com.knime.gateway.v0.entity.WorkflowPartsEnt;
import com.knime.gateway.v0.entity.WorkflowSnapshotEnt;


import java.io.IOException;
import java.net.URI;
import java.util.concurrent.TimeoutException;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.GenericType;

import com.knime.gateway.service.ServiceException;
import com.knime.gateway.v0.service.WorkflowService;
import com.knime.gateway.v0.service.util.ServiceExceptions;

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
    public String createConnection(java.util.UUID jobId, ConnectionEnt connection)  throws ServiceExceptions.ActionNotAllowedException {
        try{
            return doRequest(c -> {
                try {
                    return c.createConnection(jobId, toByteArray(connection));
                } catch (PermissionException | ExecutorException | IOException | TimeoutException ex) {
                    //server errors
                    // TODO exception handling
                    throw new RuntimeException(ex);
                }
            }, String.class);
        } catch (WebApplicationException ex) {
            //executor errors
            if (ex.getResponse().getStatus() == 405) {
                throw new ServiceExceptions.ActionNotAllowedException(readExceptionMessage(ex));
            }
            throw new ServiceException(
                "Error response with status code '" + ex.getResponse().getStatus() + "' and message: " + readExceptionMessage(ex));
        }
    }
    
    @Override
    public java.util.UUID createWorkflowCopy(java.util.UUID jobId, WorkflowPartsEnt parts)  {
        try{
            return doRequest(c -> {
                try {
                    return c.createWorkflowCopy(jobId, toByteArray(parts));
                } catch (PermissionException | ExecutorException | IOException | TimeoutException ex) {
                    //server errors
                    // TODO exception handling
                    throw new RuntimeException(ex);
                }
            }, java.util.UUID.class);
        } catch (WebApplicationException ex) {
            //executor errors
            throw new ServiceException(
                "Error response with status code '" + ex.getResponse().getStatus() + "' and message: " + readExceptionMessage(ex));
        }
    }
    
    @Override
    public java.util.UUID deleteWorkflowParts(java.util.UUID jobId, WorkflowPartsEnt parts, Boolean copy)  throws ServiceExceptions.NotASubWorkflowException, ServiceExceptions.NodeNotFoundException, ServiceExceptions.ActionNotAllowedException {
        try{
            return doRequest(c -> {
                try {
                    return c.deleteWorkflowParts(jobId, toByteArray(parts), copy);
                } catch (PermissionException | ExecutorException | IOException | TimeoutException ex) {
                    //server errors
                    // TODO exception handling
                    throw new RuntimeException(ex);
                }
            }, java.util.UUID.class);
        } catch (WebApplicationException ex) {
            //executor errors
            if (ex.getResponse().getStatus() == 400) {
                throw new ServiceExceptions.NotASubWorkflowException(readExceptionMessage(ex));
            }
            if (ex.getResponse().getStatus() == 404) {
                throw new ServiceExceptions.NodeNotFoundException(readExceptionMessage(ex));
            }
            if (ex.getResponse().getStatus() == 405) {
                throw new ServiceExceptions.ActionNotAllowedException(readExceptionMessage(ex));
            }
            throw new ServiceException(
                "Error response with status code '" + ex.getResponse().getStatus() + "' and message: " + readExceptionMessage(ex));
        }
    }
    
    @Override
    public WorkflowSnapshotEnt getSubWorkflow(java.util.UUID jobId, String nodeId)  throws ServiceExceptions.NotASubWorkflowException, ServiceExceptions.NodeNotFoundException {
        try{
            return doRequest(c -> {
                try {
                    return c.getSubWorkflow(jobId, nodeId);
                } catch (PermissionException | ExecutorException | IOException | TimeoutException ex) {
                    //server errors
                    // TODO exception handling
                    throw new RuntimeException(ex);
                }
            }, WorkflowSnapshotEnt.class);
        } catch (WebApplicationException ex) {
            //executor errors
            if (ex.getResponse().getStatus() == 400) {
                throw new ServiceExceptions.NotASubWorkflowException(readExceptionMessage(ex));
            }
            if (ex.getResponse().getStatus() == 404) {
                throw new ServiceExceptions.NodeNotFoundException(readExceptionMessage(ex));
            }
            throw new ServiceException(
                "Error response with status code '" + ex.getResponse().getStatus() + "' and message: " + readExceptionMessage(ex));
        }
    }
    
    @Override
    public PatchEnt getSubWorkflowDiff(java.util.UUID jobId, String nodeId, java.util.UUID snapshotId)  throws ServiceExceptions.NotASubWorkflowException, ServiceExceptions.NotFoundException {
        try{
            return doRequest(c -> {
                try {
                    return c.getSubWorkflowDiff(jobId, nodeId, snapshotId);
                } catch (PermissionException | ExecutorException | IOException | TimeoutException ex) {
                    //server errors
                    // TODO exception handling
                    throw new RuntimeException(ex);
                }
            }, PatchEnt.class);
        } catch (WebApplicationException ex) {
            //executor errors
            if (ex.getResponse().getStatus() == 400) {
                throw new ServiceExceptions.NotASubWorkflowException(readExceptionMessage(ex));
            }
            if (ex.getResponse().getStatus() == 404) {
                throw new ServiceExceptions.NotFoundException(readExceptionMessage(ex));
            }
            throw new ServiceException(
                "Error response with status code '" + ex.getResponse().getStatus() + "' and message: " + readExceptionMessage(ex));
        }
    }
    
    @Override
    public WorkflowSnapshotEnt getWorkflow(java.util.UUID jobId)  {
        try{
            return doRequest(c -> {
                try {
                    return c.getWorkflow(jobId);
                } catch (PermissionException | ExecutorException | IOException | TimeoutException ex) {
                    //server errors
                    // TODO exception handling
                    throw new RuntimeException(ex);
                }
            }, WorkflowSnapshotEnt.class);
        } catch (WebApplicationException ex) {
            //executor errors
            throw new ServiceException(
                "Error response with status code '" + ex.getResponse().getStatus() + "' and message: " + readExceptionMessage(ex));
        }
    }
    
    @Override
    public PatchEnt getWorkflowDiff(java.util.UUID jobId, java.util.UUID snapshotId)  throws ServiceExceptions.NotFoundException {
        try{
            return doRequest(c -> {
                try {
                    return c.getWorkflowDiff(jobId, snapshotId);
                } catch (PermissionException | ExecutorException | IOException | TimeoutException ex) {
                    //server errors
                    // TODO exception handling
                    throw new RuntimeException(ex);
                }
            }, PatchEnt.class);
        } catch (WebApplicationException ex) {
            //executor errors
            if (ex.getResponse().getStatus() == 404) {
                throw new ServiceExceptions.NotFoundException(readExceptionMessage(ex));
            }
            throw new ServiceException(
                "Error response with status code '" + ex.getResponse().getStatus() + "' and message: " + readExceptionMessage(ex));
        }
    }
    
    @Override
    public WorkflowPartsEnt pasteWorkflowParts(java.util.UUID jobId, java.util.UUID partsId, Integer x, Integer y, String nodeId)  throws ServiceExceptions.NotASubWorkflowException, ServiceExceptions.NotFoundException {
        try{
            return doRequest(c -> {
                try {
                    return c.pasteWorkflowParts(jobId, partsId, x, y, nodeId);
                } catch (PermissionException | ExecutorException | IOException | TimeoutException ex) {
                    //server errors
                    // TODO exception handling
                    throw new RuntimeException(ex);
                }
            }, WorkflowPartsEnt.class);
        } catch (WebApplicationException ex) {
            //executor errors
            if (ex.getResponse().getStatus() == 400) {
                throw new ServiceExceptions.NotASubWorkflowException(readExceptionMessage(ex));
            }
            if (ex.getResponse().getStatus() == 404) {
                throw new ServiceExceptions.NotFoundException(readExceptionMessage(ex));
            }
            throw new ServiceException(
                "Error response with status code '" + ex.getResponse().getStatus() + "' and message: " + readExceptionMessage(ex));
        }
    }
    
}

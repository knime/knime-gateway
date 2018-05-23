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

import com.knime.gateway.v0.entity.FlowVariableEnt;
import com.knime.gateway.v0.entity.NodeEnt;
import com.knime.gateway.v0.entity.NodeSettingsEnt;
import com.knime.gateway.v0.entity.PortObjectSpecEnt;


import java.io.IOException;
import java.net.URI;
import java.util.concurrent.TimeoutException;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.GenericType;

import com.knime.gateway.service.ServiceException;
import com.knime.gateway.v0.service.NodeService;
import com.knime.gateway.v0.service.util.ServiceExceptions;

import com.knime.enterprise.gateway.rest.api.Node;
import com.knime.enterprise.utility.ExecutorException;
import com.knime.enterprise.utility.PermissionException;
import com.knime.gateway.rest.client.AbstractGatewayClient;

/**
 * Client that provides access to a KNIME Gateway's <tt>Node</tt> resource. 
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
@javax.annotation.Generated(value = "org.knime.gateway.codegen.GatewayCodegen")
public class NodeClient extends AbstractGatewayClient<Node> implements NodeService {

    /**
     * See {@link AbstractGatewayClient#AbstractGatewayClient(URI, String)}.
     *
     * @param restAddress
     * @param jwt
     * @throws InstantiationException
     * @throws IllegalAccessException
     * @throws IOException
     */
    public NodeClient(final URI restAddress, final String jwt)
        throws InstantiationException, IllegalAccessException, IOException {
        super(restAddress, jwt);
    }
    
    @Override
    public String changeAndGetNodeState(java.util.UUID jobId, String nodeId, String action)  throws ServiceExceptions.NodeNotFoundException, ServiceExceptions.ActionNotAllowedException {
        try{
            return doRequest(c -> {
                try {
                    return c.changeAndGetNodeState(jobId, nodeId, action);
                } catch (PermissionException | ExecutorException | IOException | TimeoutException ex) {
                    //server errors
                    // TODO exception handling
                    throw new RuntimeException(ex);
                }
            }, String.class);
        } catch (WebApplicationException ex) {
            //executor errors
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
    public java.util.List<FlowVariableEnt> getInputFlowVariables(java.util.UUID jobId, String nodeId)  throws ServiceExceptions.NodeNotFoundException {
        try{
            return doRequest(c -> {
                try {
                    return c.getInputFlowVariables(jobId, nodeId);
                } catch (PermissionException | ExecutorException | IOException | TimeoutException ex) {
                    //server errors
                    // TODO exception handling
                    throw new RuntimeException(ex);
                }
            }, new GenericType<java.util.List<FlowVariableEnt>>(){});
        } catch (WebApplicationException ex) {
            //executor errors
            if (ex.getResponse().getStatus() == 404) {
                throw new ServiceExceptions.NodeNotFoundException(readExceptionMessage(ex));
            }
            throw new ServiceException(
                "Error response with status code '" + ex.getResponse().getStatus() + "' and message: " + readExceptionMessage(ex));
        }
    }
    
    @Override
    public java.util.List<PortObjectSpecEnt> getInputPortSpecs(java.util.UUID jobId, String nodeId)  throws ServiceExceptions.NodeNotFoundException, ServiceExceptions.NotSupportedException {
        try{
            return doRequest(c -> {
                try {
                    return c.getInputPortSpecs(jobId, nodeId);
                } catch (PermissionException | ExecutorException | IOException | TimeoutException ex) {
                    //server errors
                    // TODO exception handling
                    throw new RuntimeException(ex);
                }
            }, new GenericType<java.util.List<PortObjectSpecEnt>>(){});
        } catch (WebApplicationException ex) {
            //executor errors
            if (ex.getResponse().getStatus() == 404) {
                throw new ServiceExceptions.NodeNotFoundException(readExceptionMessage(ex));
            }
            if (ex.getResponse().getStatus() == 405) {
                throw new ServiceExceptions.NotSupportedException(readExceptionMessage(ex));
            }
            throw new ServiceException(
                "Error response with status code '" + ex.getResponse().getStatus() + "' and message: " + readExceptionMessage(ex));
        }
    }
    
    @Override
    public NodeEnt getNode(java.util.UUID jobId, String nodeId)  throws ServiceExceptions.NodeNotFoundException {
        try{
            return doRequest(c -> {
                try {
                    return c.getNode(jobId, nodeId);
                } catch (PermissionException | ExecutorException | IOException | TimeoutException ex) {
                    //server errors
                    // TODO exception handling
                    throw new RuntimeException(ex);
                }
            }, NodeEnt.class);
        } catch (WebApplicationException ex) {
            //executor errors
            if (ex.getResponse().getStatus() == 404) {
                throw new ServiceExceptions.NodeNotFoundException(readExceptionMessage(ex));
            }
            throw new ServiceException(
                "Error response with status code '" + ex.getResponse().getStatus() + "' and message: " + readExceptionMessage(ex));
        }
    }
    
    @Override
    public NodeSettingsEnt getNodeSettings(java.util.UUID jobId, String nodeId)  throws ServiceExceptions.NodeNotFoundException {
        try{
            return doRequest(c -> {
                try {
                    return c.getNodeSettings(jobId, nodeId);
                } catch (PermissionException | ExecutorException | IOException | TimeoutException ex) {
                    //server errors
                    // TODO exception handling
                    throw new RuntimeException(ex);
                }
            }, NodeSettingsEnt.class);
        } catch (WebApplicationException ex) {
            //executor errors
            if (ex.getResponse().getStatus() == 404) {
                throw new ServiceExceptions.NodeNotFoundException(readExceptionMessage(ex));
            }
            throw new ServiceException(
                "Error response with status code '" + ex.getResponse().getStatus() + "' and message: " + readExceptionMessage(ex));
        }
    }
    
    @Override
    public java.util.List<PortObjectSpecEnt> getOutputPortSpecs(java.util.UUID jobId, String nodeId)  throws ServiceExceptions.NodeNotFoundException, ServiceExceptions.NotSupportedException {
        try{
            return doRequest(c -> {
                try {
                    return c.getOutputPortSpecs(jobId, nodeId);
                } catch (PermissionException | ExecutorException | IOException | TimeoutException ex) {
                    //server errors
                    // TODO exception handling
                    throw new RuntimeException(ex);
                }
            }, new GenericType<java.util.List<PortObjectSpecEnt>>(){});
        } catch (WebApplicationException ex) {
            //executor errors
            if (ex.getResponse().getStatus() == 404) {
                throw new ServiceExceptions.NodeNotFoundException(readExceptionMessage(ex));
            }
            if (ex.getResponse().getStatus() == 405) {
                throw new ServiceExceptions.NotSupportedException(readExceptionMessage(ex));
            }
            throw new ServiceException(
                "Error response with status code '" + ex.getResponse().getStatus() + "' and message: " + readExceptionMessage(ex));
        }
    }
    
    @Override
    public NodeEnt getRootNode(java.util.UUID jobId)  {
        try{
            return doRequest(c -> {
                try {
                    return c.getRootNode(jobId);
                } catch (PermissionException | ExecutorException | IOException | TimeoutException ex) {
                    //server errors
                    // TODO exception handling
                    throw new RuntimeException(ex);
                }
            }, NodeEnt.class);
        } catch (WebApplicationException ex) {
            //executor errors
            throw new ServiceException(
                "Error response with status code '" + ex.getResponse().getStatus() + "' and message: " + readExceptionMessage(ex));
        }
    }
    
    @Override
    public void setNodeSettings(java.util.UUID jobId, String nodeId, NodeSettingsEnt nodeSettings)  throws ServiceExceptions.NodeNotFoundException, ServiceExceptions.InvalidSettingsException {
        try{
            doRequest(c -> {
                try {
                    return c.setNodeSettings(jobId, nodeId, toByteArray(nodeSettings));
                } catch (PermissionException | ExecutorException | IOException | TimeoutException ex) {
                    //server errors
                    // TODO exception handling
                    throw new RuntimeException(ex);
                }
            });
        } catch (WebApplicationException ex) {
            //executor errors
            if (ex.getResponse().getStatus() == 404) {
                throw new ServiceExceptions.NodeNotFoundException(readExceptionMessage(ex));
            }
            if (ex.getResponse().getStatus() == 405) {
                throw new ServiceExceptions.InvalidSettingsException(readExceptionMessage(ex));
            }
            throw new ServiceException(
                "Error response with status code '" + ex.getResponse().getStatus() + "' and message: " + readExceptionMessage(ex));
        }
    }
    
}

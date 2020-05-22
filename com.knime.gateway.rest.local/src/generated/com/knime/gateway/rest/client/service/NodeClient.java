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

import com.knime.gateway.entity.BoundsEnt;
import com.knime.gateway.entity.DataTableEnt;
import com.knime.gateway.entity.FlowVariableEnt;
import com.knime.gateway.entity.JavaObjectEnt;
import com.knime.gateway.entity.MetaNodeDialogEnt;
import com.knime.gateway.entity.NodeEnt;
import com.knime.gateway.entity.NodeFactoryKeyEnt;
import com.knime.gateway.entity.NodeSettingsEnt;
import com.knime.gateway.entity.PortObjectSpecEnt;
import com.knime.gateway.entity.ViewDataEnt;


import java.io.IOException;
import java.net.URI;
import java.util.concurrent.TimeoutException;

import javax.ws.rs.ProcessingException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.GenericType;

import com.knime.gateway.service.ServiceException;
import com.knime.gateway.service.NodeService;
import com.knime.gateway.service.util.ServiceExceptions;

import com.knime.enterprise.gateway.rest.api.Node;
import com.knime.enterprise.utility.ExecutorException;
import com.knime.enterprise.utility.PermissionException;
import com.knime.gateway.rest.client.AbstractGatewayClient;

/**
 * Client that provides access to a KNIME Gateway's <tt>Node</tt> resource. 
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
@javax.annotation.Generated(value = {"org.knime.gateway.codegen.GatewayCodegen", "src-gen/com.knime.gateway.rest.local-config.json"})
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
    public String changeAndGetNodeState(java.util.UUID jobId, com.knime.gateway.entity.NodeIDEnt nodeId, String action)  throws ServiceExceptions.NodeNotFoundException, ServiceExceptions.ActionNotAllowedException {
        try{
            return doRequest(c -> {
                try {
                    return c.changeAndGetNodeState(jobId, nodeId.toString(), action);
                } catch (PermissionException | ExecutorException | IOException | TimeoutException ex) {
                    //server errors
                    throw new ServiceException("Internal server error.", ex);
                } catch (ProcessingException e) {
                    //in case the server cannot be reached (timeout, connection refused)
                    throw new ServiceException("Server doesn't seem to be reachable.",
                        e.getCause());
                }
            }, String.class);
        } catch (WebApplicationException ex) {
            //executor errors
            com.knime.gateway.entity.GatewayExceptionEnt gatewayException = readAndParseGatewayExceptionResponse(ex, "Node", "changeAndGetNodeState");
            if (gatewayException.getExceptionName().equals("NodeNotFoundException")) {
                throw new ServiceExceptions.NodeNotFoundException(gatewayException.getExceptionMessage());
            }
            if (gatewayException.getExceptionName().equals("ActionNotAllowedException")) {
                throw new ServiceExceptions.ActionNotAllowedException(gatewayException.getExceptionMessage());
            }
            throw new ServiceException("Undefined service exception '" + gatewayException.getExceptionName()
                + "' with message: " + gatewayException.getExceptionMessage());
        }
    }
    
    @Override
    public com.knime.gateway.entity.NodeIDEnt createNode(java.util.UUID jobId, com.knime.gateway.entity.NodeIDEnt workflowId, Integer x, Integer y, NodeFactoryKeyEnt nodeFactoryKeyEnt)  throws ServiceExceptions.NotASubWorkflowException, ServiceExceptions.NodeNotFoundException, ServiceExceptions.InvalidRequestException {
        try{
            return doRequest(c -> {
                try {
                    return c.createNode(jobId, workflowId.toString(), x, y, toByteArray(nodeFactoryKeyEnt));
                } catch (PermissionException | ExecutorException | IOException | TimeoutException ex) {
                    //server errors
                    throw new ServiceException("Internal server error.", ex);
                } catch (ProcessingException e) {
                    //in case the server cannot be reached (timeout, connection refused)
                    throw new ServiceException("Server doesn't seem to be reachable.",
                        e.getCause());
                }
            }, com.knime.gateway.entity.NodeIDEnt.class);
        } catch (WebApplicationException ex) {
            //executor errors
            com.knime.gateway.entity.GatewayExceptionEnt gatewayException = readAndParseGatewayExceptionResponse(ex, "Node", "createNode");
            if (gatewayException.getExceptionName().equals("NotASubWorkflowException")) {
                throw new ServiceExceptions.NotASubWorkflowException(gatewayException.getExceptionMessage());
            }
            if (gatewayException.getExceptionName().equals("NodeNotFoundException")) {
                throw new ServiceExceptions.NodeNotFoundException(gatewayException.getExceptionMessage());
            }
            if (gatewayException.getExceptionName().equals("InvalidRequestException")) {
                throw new ServiceExceptions.InvalidRequestException(gatewayException.getExceptionMessage());
            }
            throw new ServiceException("Undefined service exception '" + gatewayException.getExceptionName()
                + "' with message: " + gatewayException.getExceptionMessage());
        }
    }
    
    @Override
    public java.util.List<FlowVariableEnt> getInputFlowVariables(java.util.UUID jobId, com.knime.gateway.entity.NodeIDEnt nodeId)  throws ServiceExceptions.NodeNotFoundException {
        try{
            return doRequest(c -> {
                try {
                    return c.getInputFlowVariables(jobId, nodeId.toString());
                } catch (PermissionException | ExecutorException | IOException | TimeoutException ex) {
                    //server errors
                    throw new ServiceException("Internal server error.", ex);
                } catch (ProcessingException e) {
                    //in case the server cannot be reached (timeout, connection refused)
                    throw new ServiceException("Server doesn't seem to be reachable.",
                        e.getCause());
                }
            }, new GenericType<java.util.List<FlowVariableEnt>>(){});
        } catch (WebApplicationException ex) {
            //executor errors
            com.knime.gateway.entity.GatewayExceptionEnt gatewayException = readAndParseGatewayExceptionResponse(ex, "Node", "getInputFlowVariables");
            if (gatewayException.getExceptionName().equals("NodeNotFoundException")) {
                throw new ServiceExceptions.NodeNotFoundException(gatewayException.getExceptionMessage());
            }
            throw new ServiceException("Undefined service exception '" + gatewayException.getExceptionName()
                + "' with message: " + gatewayException.getExceptionMessage());
        }
    }
    
    @Override
    public java.util.List<PortObjectSpecEnt> getInputPortSpecs(java.util.UUID jobId, com.knime.gateway.entity.NodeIDEnt nodeId)  throws ServiceExceptions.NodeNotFoundException {
        try{
            return doRequest(c -> {
                try {
                    return c.getInputPortSpecs(jobId, nodeId.toString());
                } catch (PermissionException | ExecutorException | IOException | TimeoutException ex) {
                    //server errors
                    throw new ServiceException("Internal server error.", ex);
                } catch (ProcessingException e) {
                    //in case the server cannot be reached (timeout, connection refused)
                    throw new ServiceException("Server doesn't seem to be reachable.",
                        e.getCause());
                }
            }, new GenericType<java.util.List<PortObjectSpecEnt>>(){});
        } catch (WebApplicationException ex) {
            //executor errors
            com.knime.gateway.entity.GatewayExceptionEnt gatewayException = readAndParseGatewayExceptionResponse(ex, "Node", "getInputPortSpecs");
            if (gatewayException.getExceptionName().equals("NodeNotFoundException")) {
                throw new ServiceExceptions.NodeNotFoundException(gatewayException.getExceptionMessage());
            }
            throw new ServiceException("Undefined service exception '" + gatewayException.getExceptionName()
                + "' with message: " + gatewayException.getExceptionMessage());
        }
    }
    
    @Override
    public NodeEnt getNode(java.util.UUID jobId, com.knime.gateway.entity.NodeIDEnt nodeId)  throws ServiceExceptions.NodeNotFoundException {
        try{
            return doRequest(c -> {
                try {
                    return c.getNode(jobId, nodeId.toString());
                } catch (PermissionException | ExecutorException | IOException | TimeoutException ex) {
                    //server errors
                    throw new ServiceException("Internal server error.", ex);
                } catch (ProcessingException e) {
                    //in case the server cannot be reached (timeout, connection refused)
                    throw new ServiceException("Server doesn't seem to be reachable.",
                        e.getCause());
                }
            }, NodeEnt.class);
        } catch (WebApplicationException ex) {
            //executor errors
            com.knime.gateway.entity.GatewayExceptionEnt gatewayException = readAndParseGatewayExceptionResponse(ex, "Node", "getNode");
            if (gatewayException.getExceptionName().equals("NodeNotFoundException")) {
                throw new ServiceExceptions.NodeNotFoundException(gatewayException.getExceptionMessage());
            }
            throw new ServiceException("Undefined service exception '" + gatewayException.getExceptionName()
                + "' with message: " + gatewayException.getExceptionMessage());
        }
    }
    
    @Override
    public NodeSettingsEnt getNodeSettings(java.util.UUID jobId, com.knime.gateway.entity.NodeIDEnt nodeId)  throws ServiceExceptions.NodeNotFoundException {
        try{
            return doRequest(c -> {
                try {
                    return c.getNodeSettings(jobId, nodeId.toString());
                } catch (PermissionException | ExecutorException | IOException | TimeoutException ex) {
                    //server errors
                    throw new ServiceException("Internal server error.", ex);
                } catch (ProcessingException e) {
                    //in case the server cannot be reached (timeout, connection refused)
                    throw new ServiceException("Server doesn't seem to be reachable.",
                        e.getCause());
                }
            }, NodeSettingsEnt.class);
        } catch (WebApplicationException ex) {
            //executor errors
            com.knime.gateway.entity.GatewayExceptionEnt gatewayException = readAndParseGatewayExceptionResponse(ex, "Node", "getNodeSettings");
            if (gatewayException.getExceptionName().equals("NodeNotFoundException")) {
                throw new ServiceExceptions.NodeNotFoundException(gatewayException.getExceptionMessage());
            }
            throw new ServiceException("Undefined service exception '" + gatewayException.getExceptionName()
                + "' with message: " + gatewayException.getExceptionMessage());
        }
    }
    
    @Override
    public DataTableEnt getOutputDataTable(java.util.UUID jobId, com.knime.gateway.entity.NodeIDEnt nodeId, Integer portIdx, Long from, Integer size)  throws ServiceExceptions.NodeNotFoundException, ServiceExceptions.InvalidRequestException {
        try{
            return doRequest(c -> {
                try {
                    return c.getOutputDataTable(jobId, nodeId.toString(), portIdx, from, size);
                } catch (PermissionException | ExecutorException | IOException | TimeoutException ex) {
                    //server errors
                    throw new ServiceException("Internal server error.", ex);
                } catch (ProcessingException e) {
                    //in case the server cannot be reached (timeout, connection refused)
                    throw new ServiceException("Server doesn't seem to be reachable.",
                        e.getCause());
                }
            }, DataTableEnt.class);
        } catch (WebApplicationException ex) {
            //executor errors
            com.knime.gateway.entity.GatewayExceptionEnt gatewayException = readAndParseGatewayExceptionResponse(ex, "Node", "getOutputDataTable");
            if (gatewayException.getExceptionName().equals("NodeNotFoundException")) {
                throw new ServiceExceptions.NodeNotFoundException(gatewayException.getExceptionMessage());
            }
            if (gatewayException.getExceptionName().equals("InvalidRequestException")) {
                throw new ServiceExceptions.InvalidRequestException(gatewayException.getExceptionMessage());
            }
            throw new ServiceException("Undefined service exception '" + gatewayException.getExceptionName()
                + "' with message: " + gatewayException.getExceptionMessage());
        }
    }
    
    @Override
    public java.util.List<FlowVariableEnt> getOutputFlowVariables(java.util.UUID jobId, com.knime.gateway.entity.NodeIDEnt nodeId)  throws ServiceExceptions.NodeNotFoundException {
        try{
            return doRequest(c -> {
                try {
                    return c.getOutputFlowVariables(jobId, nodeId.toString());
                } catch (PermissionException | ExecutorException | IOException | TimeoutException ex) {
                    //server errors
                    throw new ServiceException("Internal server error.", ex);
                } catch (ProcessingException e) {
                    //in case the server cannot be reached (timeout, connection refused)
                    throw new ServiceException("Server doesn't seem to be reachable.",
                        e.getCause());
                }
            }, new GenericType<java.util.List<FlowVariableEnt>>(){});
        } catch (WebApplicationException ex) {
            //executor errors
            com.knime.gateway.entity.GatewayExceptionEnt gatewayException = readAndParseGatewayExceptionResponse(ex, "Node", "getOutputFlowVariables");
            if (gatewayException.getExceptionName().equals("NodeNotFoundException")) {
                throw new ServiceExceptions.NodeNotFoundException(gatewayException.getExceptionMessage());
            }
            throw new ServiceException("Undefined service exception '" + gatewayException.getExceptionName()
                + "' with message: " + gatewayException.getExceptionMessage());
        }
    }
    
    @Override
    public java.util.List<PortObjectSpecEnt> getOutputPortSpecs(java.util.UUID jobId, com.knime.gateway.entity.NodeIDEnt nodeId)  throws ServiceExceptions.NodeNotFoundException {
        try{
            return doRequest(c -> {
                try {
                    return c.getOutputPortSpecs(jobId, nodeId.toString());
                } catch (PermissionException | ExecutorException | IOException | TimeoutException ex) {
                    //server errors
                    throw new ServiceException("Internal server error.", ex);
                } catch (ProcessingException e) {
                    //in case the server cannot be reached (timeout, connection refused)
                    throw new ServiceException("Server doesn't seem to be reachable.",
                        e.getCause());
                }
            }, new GenericType<java.util.List<PortObjectSpecEnt>>(){});
        } catch (WebApplicationException ex) {
            //executor errors
            com.knime.gateway.entity.GatewayExceptionEnt gatewayException = readAndParseGatewayExceptionResponse(ex, "Node", "getOutputPortSpecs");
            if (gatewayException.getExceptionName().equals("NodeNotFoundException")) {
                throw new ServiceExceptions.NodeNotFoundException(gatewayException.getExceptionMessage());
            }
            throw new ServiceException("Undefined service exception '" + gatewayException.getExceptionName()
                + "' with message: " + gatewayException.getExceptionMessage());
        }
    }
    
    @Override
    public ViewDataEnt getViewData(java.util.UUID jobId, com.knime.gateway.entity.NodeIDEnt nodeId)  throws ServiceExceptions.NodeNotFoundException, ServiceExceptions.InvalidRequestException {
        try{
            return doRequest(c -> {
                try {
                    return c.getViewData(jobId, nodeId.toString());
                } catch (PermissionException | ExecutorException | IOException | TimeoutException ex) {
                    //server errors
                    throw new ServiceException("Internal server error.", ex);
                } catch (ProcessingException e) {
                    //in case the server cannot be reached (timeout, connection refused)
                    throw new ServiceException("Server doesn't seem to be reachable.",
                        e.getCause());
                }
            }, ViewDataEnt.class);
        } catch (WebApplicationException ex) {
            //executor errors
            com.knime.gateway.entity.GatewayExceptionEnt gatewayException = readAndParseGatewayExceptionResponse(ex, "Node", "getViewData");
            if (gatewayException.getExceptionName().equals("NodeNotFoundException")) {
                throw new ServiceExceptions.NodeNotFoundException(gatewayException.getExceptionMessage());
            }
            if (gatewayException.getExceptionName().equals("InvalidRequestException")) {
                throw new ServiceExceptions.InvalidRequestException(gatewayException.getExceptionMessage());
            }
            throw new ServiceException("Undefined service exception '" + gatewayException.getExceptionName()
                + "' with message: " + gatewayException.getExceptionMessage());
        }
    }
    
    @Override
    public MetaNodeDialogEnt getWMetaNodeDialog(java.util.UUID jobId, com.knime.gateway.entity.NodeIDEnt nodeId)  throws ServiceExceptions.NodeNotFoundException, ServiceExceptions.InvalidRequestException {
        try{
            return doRequest(c -> {
                try {
                    return c.getWMetaNodeDialog(jobId, nodeId.toString());
                } catch (PermissionException | ExecutorException | IOException | TimeoutException ex) {
                    //server errors
                    throw new ServiceException("Internal server error.", ex);
                } catch (ProcessingException e) {
                    //in case the server cannot be reached (timeout, connection refused)
                    throw new ServiceException("Server doesn't seem to be reachable.",
                        e.getCause());
                }
            }, MetaNodeDialogEnt.class);
        } catch (WebApplicationException ex) {
            //executor errors
            com.knime.gateway.entity.GatewayExceptionEnt gatewayException = readAndParseGatewayExceptionResponse(ex, "Node", "getWMetaNodeDialog");
            if (gatewayException.getExceptionName().equals("NodeNotFoundException")) {
                throw new ServiceExceptions.NodeNotFoundException(gatewayException.getExceptionMessage());
            }
            if (gatewayException.getExceptionName().equals("InvalidRequestException")) {
                throw new ServiceExceptions.InvalidRequestException(gatewayException.getExceptionMessage());
            }
            throw new ServiceException("Undefined service exception '" + gatewayException.getExceptionName()
                + "' with message: " + gatewayException.getExceptionMessage());
        }
    }
    
    @Override
    public NodeEnt replaceNode(java.util.UUID jobId, com.knime.gateway.entity.NodeIDEnt nodeId, NodeFactoryKeyEnt nodeFactoryKeyEnt)  throws ServiceExceptions.NodeNotFoundException, ServiceExceptions.ActionNotAllowedException {
        try{
            return doRequest(c -> {
                try {
                    return c.replaceNode(jobId, nodeId.toString(), toByteArray(nodeFactoryKeyEnt));
                } catch (PermissionException | ExecutorException | IOException | TimeoutException ex) {
                    //server errors
                    throw new ServiceException("Internal server error.", ex);
                } catch (ProcessingException e) {
                    //in case the server cannot be reached (timeout, connection refused)
                    throw new ServiceException("Server doesn't seem to be reachable.",
                        e.getCause());
                }
            }, NodeEnt.class);
        } catch (WebApplicationException ex) {
            //executor errors
            com.knime.gateway.entity.GatewayExceptionEnt gatewayException = readAndParseGatewayExceptionResponse(ex, "Node", "replaceNode");
            if (gatewayException.getExceptionName().equals("NodeNotFoundException")) {
                throw new ServiceExceptions.NodeNotFoundException(gatewayException.getExceptionMessage());
            }
            if (gatewayException.getExceptionName().equals("ActionNotAllowedException")) {
                throw new ServiceExceptions.ActionNotAllowedException(gatewayException.getExceptionMessage());
            }
            throw new ServiceException("Undefined service exception '" + gatewayException.getExceptionName()
                + "' with message: " + gatewayException.getExceptionMessage());
        }
    }
    
    @Override
    public void setNodeBounds(java.util.UUID jobId, com.knime.gateway.entity.NodeIDEnt nodeId, BoundsEnt boundsEnt)  throws ServiceExceptions.NodeNotFoundException {
        try{
            doRequest(c -> {
                try {
                    return c.setNodeBounds(jobId, nodeId.toString(), toByteArray(boundsEnt));
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
            com.knime.gateway.entity.GatewayExceptionEnt gatewayException = readAndParseGatewayExceptionResponse(ex, "Node", "setNodeBounds");
            if (gatewayException.getExceptionName().equals("NodeNotFoundException")) {
                throw new ServiceExceptions.NodeNotFoundException(gatewayException.getExceptionMessage());
            }
            throw new ServiceException("Undefined service exception '" + gatewayException.getExceptionName()
                + "' with message: " + gatewayException.getExceptionMessage());
        }
    }
    
    @Override
    public void setNodeSettings(java.util.UUID jobId, com.knime.gateway.entity.NodeIDEnt nodeId, NodeSettingsEnt nodeSettingsEnt)  throws ServiceExceptions.NodeNotFoundException, ServiceExceptions.InvalidSettingsException, ServiceExceptions.IllegalStateException {
        try{
            doRequest(c -> {
                try {
                    return c.setNodeSettings(jobId, nodeId.toString(), toByteArray(nodeSettingsEnt));
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
            com.knime.gateway.entity.GatewayExceptionEnt gatewayException = readAndParseGatewayExceptionResponse(ex, "Node", "setNodeSettings");
            if (gatewayException.getExceptionName().equals("NodeNotFoundException")) {
                throw new ServiceExceptions.NodeNotFoundException(gatewayException.getExceptionMessage());
            }
            if (gatewayException.getExceptionName().equals("InvalidSettingsException")) {
                throw new ServiceExceptions.InvalidSettingsException(gatewayException.getExceptionMessage());
            }
            if (gatewayException.getExceptionName().equals("IllegalStateException")) {
                throw new ServiceExceptions.IllegalStateException(gatewayException.getExceptionMessage());
            }
            throw new ServiceException("Undefined service exception '" + gatewayException.getExceptionName()
                + "' with message: " + gatewayException.getExceptionMessage());
        }
    }
    
    @Override
    public void setViewValue(java.util.UUID jobId, com.knime.gateway.entity.NodeIDEnt nodeId, Boolean useAsDefault, JavaObjectEnt javaObjectEnt)  throws ServiceExceptions.NodeNotFoundException, ServiceExceptions.InvalidRequestException {
        try{
            doRequest(c -> {
                try {
                    return c.setViewValue(jobId, nodeId.toString(), useAsDefault, toByteArray(javaObjectEnt));
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
            com.knime.gateway.entity.GatewayExceptionEnt gatewayException = readAndParseGatewayExceptionResponse(ex, "Node", "setViewValue");
            if (gatewayException.getExceptionName().equals("NodeNotFoundException")) {
                throw new ServiceExceptions.NodeNotFoundException(gatewayException.getExceptionMessage());
            }
            if (gatewayException.getExceptionName().equals("InvalidRequestException")) {
                throw new ServiceExceptions.InvalidRequestException(gatewayException.getExceptionMessage());
            }
            throw new ServiceException("Undefined service exception '" + gatewayException.getExceptionName()
                + "' with message: " + gatewayException.getExceptionMessage());
        }
    }
    
}

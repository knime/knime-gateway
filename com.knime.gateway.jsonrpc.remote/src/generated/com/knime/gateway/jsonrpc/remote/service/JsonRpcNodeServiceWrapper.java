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
package com.knime.gateway.jsonrpc.remote.service;

import com.knime.gateway.v0.entity.NodeEnt;

import com.googlecode.jsonrpc4j.JsonRpcError;
import com.googlecode.jsonrpc4j.JsonRpcErrors;
import com.googlecode.jsonrpc4j.JsonRpcMethod;
import com.googlecode.jsonrpc4j.JsonRpcParam;
import com.googlecode.jsonrpc4j.JsonRpcService;

import com.knime.gateway.v0.service.util.ServiceExceptions;

import com.knime.gateway.v0.service.NodeService;

/**
 * Json rpc annotated class that wraps another service and delegates the method calls. 
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
@JsonRpcService(value = "NodeService")
@javax.annotation.Generated(value = "org.knime.gateway.codegen.GatewayCodegen")
public class JsonRpcNodeServiceWrapper implements NodeService {

    private final NodeService m_service;
    
    public JsonRpcNodeServiceWrapper(NodeService service) {
        m_service = service;
    }

	/**
     * {@inheritDoc}
     */
    @Override
    @JsonRpcMethod(value = "changeAndGetNodeState")
    @JsonRpcErrors(value = {
        @JsonRpcError(exception = ServiceExceptions.NodeNotFoundException.class, code = -32600,
            data = "404" /*per convention the data property contains the status code*/),
        @JsonRpcError(exception = ServiceExceptions.ActionNotAllowedException.class, code = -32600,
            data = "405" /*per convention the data property contains the status code*/)
    })
    public String changeAndGetNodeState(@JsonRpcParam(value="jobId") java.util.UUID jobId, @JsonRpcParam(value="nodeId") String nodeId, String action)  throws ServiceExceptions.NodeNotFoundException, ServiceExceptions.ActionNotAllowedException {
        return m_service.changeAndGetNodeState(jobId, nodeId, action);    
    }

	/**
     * {@inheritDoc}
     */
    @Override
    @JsonRpcMethod(value = "getNode")
    @JsonRpcErrors(value = {
        @JsonRpcError(exception = ServiceExceptions.NodeNotFoundException.class, code = -32600,
            data = "404" /*per convention the data property contains the status code*/)
    })
    public NodeEnt getNode(@JsonRpcParam(value="jobId") java.util.UUID jobId, @JsonRpcParam(value="nodeId") String nodeId)  throws ServiceExceptions.NodeNotFoundException {
        return m_service.getNode(jobId, nodeId);    
    }

	/**
     * {@inheritDoc}
     */
    @Override
    @JsonRpcMethod(value = "getNodeSettings")
    @JsonRpcErrors(value = {
        @JsonRpcError(exception = ServiceExceptions.NodeNotFoundException.class, code = -32600,
            data = "404" /*per convention the data property contains the status code*/)
    })
    public String getNodeSettings(@JsonRpcParam(value="jobId") java.util.UUID jobId, @JsonRpcParam(value="nodeId") String nodeId)  throws ServiceExceptions.NodeNotFoundException {
        return m_service.getNodeSettings(jobId, nodeId);    
    }

	/**
     * {@inheritDoc}
     */
    @Override
    @JsonRpcMethod(value = "getRootNode")
    public NodeEnt getRootNode(@JsonRpcParam(value="jobId") java.util.UUID jobId)  {
        return m_service.getRootNode(jobId);    
    }

}

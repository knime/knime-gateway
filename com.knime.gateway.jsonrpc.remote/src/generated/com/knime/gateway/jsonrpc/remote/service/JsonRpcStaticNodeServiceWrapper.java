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

import com.knime.gateway.v0.entity.NodeCategoryEnt;

import com.googlecode.jsonrpc4j.JsonRpcError;
import com.googlecode.jsonrpc4j.JsonRpcErrors;
import com.googlecode.jsonrpc4j.JsonRpcMethod;
import com.googlecode.jsonrpc4j.JsonRpcParam;
import com.googlecode.jsonrpc4j.JsonRpcService;

import com.knime.gateway.v0.service.util.ServiceExceptions;

import com.knime.gateway.v0.service.StaticNodeService;

/**
 * Json rpc annotated class that wraps another service and delegates the method calls. 
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
@JsonRpcService(value = "StaticNodeService")
@javax.annotation.Generated(value = "org.knime.gateway.codegen.GatewayCodegen")
public class JsonRpcStaticNodeServiceWrapper implements StaticNodeService {

    private final StaticNodeService m_service;
    
    public JsonRpcStaticNodeServiceWrapper(StaticNodeService service) {
        m_service = service;
    }

	/**
     * {@inheritDoc}
     */
    @Override
    @JsonRpcMethod(value = "getAllNodes")
    public NodeCategoryEnt getAllNodes(@JsonRpcParam(value="jobId") java.util.UUID jobId, String nodeType)  {
        return m_service.getAllNodes(jobId, nodeType);    
    }

}

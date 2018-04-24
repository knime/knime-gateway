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
package com.knime.gateway.jsonrpc.local.service;

import com.knime.gateway.v0.entity.NodeEnt;

import com.knime.gateway.v0.service.util.ServiceExceptions;

import com.googlecode.jsonrpc4j.JsonRpcMethod;

import com.knime.gateway.v0.service.NodeService;

/**
 * Interface that adds json rpc annotations. 
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
@javax.annotation.Generated(value = "org.knime.gateway.codegen.GatewayCodegen")
public interface JsonRpcNodeService extends NodeService {

	/**
     * {@inheritDoc}
     */
    @Override
    @JsonRpcMethod(value = "NodeService.changeAndGetNodeState")
    String changeAndGetNodeState(java.util.UUID jobId, String nodeId, String action)  throws ServiceExceptions.NodeNotFoundException, ServiceExceptions.ActionNotAllowedException;

	/**
     * {@inheritDoc}
     */
    @Override
    @JsonRpcMethod(value = "NodeService.getNode")
    NodeEnt getNode(java.util.UUID jobId, String nodeId)  throws ServiceExceptions.NodeNotFoundException;

	/**
     * {@inheritDoc}
     */
    @Override
    @JsonRpcMethod(value = "NodeService.getNodeSettings")
    String getNodeSettings(java.util.UUID jobId, String nodeId)  throws ServiceExceptions.NodeNotFoundException;

	/**
     * {@inheritDoc}
     */
    @Override
    @JsonRpcMethod(value = "NodeService.getRootNode")
    NodeEnt getRootNode(java.util.UUID jobId) ;

}

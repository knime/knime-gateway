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

import com.knime.gateway.service.util.ServiceExceptions;

import com.googlecode.jsonrpc4j.JsonRpcMethod;

import com.knime.gateway.service.NodeService;

/**
 * Interface that adds json rpc annotations. 
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
@javax.annotation.Generated(value = {"org.knime.gateway.codegen.GatewayCodegen", "src-gen/com.knime.gateway.jsonrpc.local-config.json"})
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
    @JsonRpcMethod(value = "NodeService.createNode")
    String createNode(java.util.UUID jobId, Integer x, Integer y, NodeFactoryKeyEnt nodeFactoryKeyEnt, String parentNodeId)  throws ServiceExceptions.NotASubWorkflowException, ServiceExceptions.NodeNotFoundException, ServiceExceptions.InvalidRequestException;

	/**
     * {@inheritDoc}
     */
    @Override
    @JsonRpcMethod(value = "NodeService.getInputFlowVariables")
    java.util.List<FlowVariableEnt> getInputFlowVariables(java.util.UUID jobId, String nodeId)  throws ServiceExceptions.NodeNotFoundException;

	/**
     * {@inheritDoc}
     */
    @Override
    @JsonRpcMethod(value = "NodeService.getInputPortSpecs")
    java.util.List<PortObjectSpecEnt> getInputPortSpecs(java.util.UUID jobId, String nodeId)  throws ServiceExceptions.NodeNotFoundException;

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
    NodeSettingsEnt getNodeSettings(java.util.UUID jobId, String nodeId)  throws ServiceExceptions.NodeNotFoundException;

	/**
     * {@inheritDoc}
     */
    @Override
    @JsonRpcMethod(value = "NodeService.getOutputDataTable")
    DataTableEnt getOutputDataTable(java.util.UUID jobId, String nodeId, Integer portIdx, Long from, Integer size)  throws ServiceExceptions.NodeNotFoundException, ServiceExceptions.InvalidRequestException;

	/**
     * {@inheritDoc}
     */
    @Override
    @JsonRpcMethod(value = "NodeService.getOutputFlowVariables")
    java.util.List<FlowVariableEnt> getOutputFlowVariables(java.util.UUID jobId, String nodeId)  throws ServiceExceptions.NodeNotFoundException;

	/**
     * {@inheritDoc}
     */
    @Override
    @JsonRpcMethod(value = "NodeService.getOutputPortSpecs")
    java.util.List<PortObjectSpecEnt> getOutputPortSpecs(java.util.UUID jobId, String nodeId)  throws ServiceExceptions.NodeNotFoundException;

	/**
     * {@inheritDoc}
     */
    @Override
    @JsonRpcMethod(value = "NodeService.getRootNode")
    NodeEnt getRootNode(java.util.UUID jobId) ;

	/**
     * {@inheritDoc}
     */
    @Override
    @JsonRpcMethod(value = "NodeService.getViewData")
    ViewDataEnt getViewData(java.util.UUID jobId, String nodeId)  throws ServiceExceptions.NodeNotFoundException, ServiceExceptions.InvalidRequestException;

	/**
     * {@inheritDoc}
     */
    @Override
    @JsonRpcMethod(value = "NodeService.getWMetaNodeDialog")
    MetaNodeDialogEnt getWMetaNodeDialog(java.util.UUID jobId, String nodeId)  throws ServiceExceptions.NodeNotFoundException, ServiceExceptions.InvalidRequestException;

	/**
     * {@inheritDoc}
     */
    @Override
    @JsonRpcMethod(value = "NodeService.setNodeBounds")
    void setNodeBounds(java.util.UUID jobId, String nodeId, BoundsEnt boundsEnt)  throws ServiceExceptions.NodeNotFoundException;

	/**
     * {@inheritDoc}
     */
    @Override
    @JsonRpcMethod(value = "NodeService.setNodeSettings")
    void setNodeSettings(java.util.UUID jobId, String nodeId, NodeSettingsEnt nodeSettingsEnt)  throws ServiceExceptions.NodeNotFoundException, ServiceExceptions.InvalidSettingsException, ServiceExceptions.IllegalStateException;

	/**
     * {@inheritDoc}
     */
    @Override
    @JsonRpcMethod(value = "NodeService.setViewValue")
    void setViewValue(java.util.UUID jobId, String nodeId, Boolean useAsDefault, JavaObjectEnt javaObjectEnt)  throws ServiceExceptions.NodeNotFoundException, ServiceExceptions.InvalidRequestException;

}

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
package com.knime.gateway.v0.service;

import com.knime.gateway.service.GatewayService;
import com.knime.gateway.v0.service.util.ServiceExceptions;

import com.knime.gateway.v0.entity.FlowVariableEnt;
import com.knime.gateway.v0.entity.NodeEnt;
import com.knime.gateway.v0.entity.NodeSettingsEnt;
import com.knime.gateway.v0.entity.PortObjectSpecEnt;

/**
 * Operations on single nodes.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
@javax.annotation.Generated(value = "org.knime.gateway.codegen.GatewayCodegen")
public interface NodeService extends GatewayService {

    /**
     * Retrieves and manipulates the node&#39;s state for the given node-id.
     *
     * @param jobId ID the job the workflow is requested for
     * @param nodeId The ID of the node the information is requested for. For nested nodes the node ids are concatenated with an &#39;:&#39;, e.g. 3:6:4
     * @param action The action (reset, cancel, execute) to be performed in order to change the node&#39;s state.
     *
     * @return the result
     * @throws ServiceExceptions.NodeNotFoundException The requested node was not found.
     * @throws ServiceExceptions.ActionNotAllowedException If an action is not allowed because it&#39;s not applicable or it doesn&#39;t exist. Please refer to the exception message for more details.
     */
    String changeAndGetNodeState(java.util.UUID jobId, String nodeId, String action)  throws ServiceExceptions.NodeNotFoundException, ServiceExceptions.ActionNotAllowedException;
        
    /**
     * Provides the node&#39;s flow variables available for the node with the given node-id.
     *
     * @param jobId ID the job the workflow is requested for
     * @param nodeId The ID of the node the information is requested for. For nested nodes the node ids are concatenated with an &#39;:&#39;, e.g. 3:6:4
     *
     * @return the result
     * @throws ServiceExceptions.NodeNotFoundException The requested node was not found.
     */
    java.util.List<FlowVariableEnt> getInputFlowVariables(java.util.UUID jobId, String nodeId)  throws ServiceExceptions.NodeNotFoundException;
        
    /**
     * Provides the node&#39;s input port specifications for the given node-id. I.e. all output port specs of the output ports connected to the input ports of this node.
     *
     * @param jobId ID the job the workflow is requested for
     * @param nodeId The ID of the node the information is requested for. For nested nodes the node ids are concatenated with an &#39;:&#39;, e.g. 3:6:4
     *
     * @return the result
     * @throws ServiceExceptions.NodeNotFoundException The requested node was not found.
     * @throws ServiceExceptions.NotSupportedException If the request is not supported for a reason. Please refer to the exception message for more details.
     */
    java.util.List<PortObjectSpecEnt> getInputPortSpecs(java.util.UUID jobId, String nodeId)  throws ServiceExceptions.NodeNotFoundException, ServiceExceptions.NotSupportedException;
        
    /**
     * Retrieves the node for the given node-id.
     *
     * @param jobId ID the job the workflow is requested for
     * @param nodeId The ID of the node the information is requested for.
     *
     * @return the result
     * @throws ServiceExceptions.NodeNotFoundException The requested node was not found.
     */
    NodeEnt getNode(java.util.UUID jobId, String nodeId)  throws ServiceExceptions.NodeNotFoundException;
        
    /**
     * Retrieves the node&#39;s settings for the given node-id.
     *
     * @param jobId ID the job the workflow is requested for
     * @param nodeId The ID of the node the information is requested for. For nested nodes the node ids are concatenated with an &#39;:&#39;, e.g. 3:6:4
     *
     * @return the result
     * @throws ServiceExceptions.NodeNotFoundException The requested node was not found.
     */
    NodeSettingsEnt getNodeSettings(java.util.UUID jobId, String nodeId)  throws ServiceExceptions.NodeNotFoundException;
        
    /**
     * Provides the node&#39;s output port specifications for the given node-id.
     *
     * @param jobId ID the job the workflow is requested for
     * @param nodeId The ID of the node the information is requested for. For nested nodes the node ids are concatenated with an &#39;:&#39;, e.g. 3:6:4
     *
     * @return the result
     * @throws ServiceExceptions.NodeNotFoundException The requested node was not found.
     * @throws ServiceExceptions.NotSupportedException If the request is not supported for a reason. Please refer to the exception message for more details.
     */
    java.util.List<PortObjectSpecEnt> getOutputPortSpecs(java.util.UUID jobId, String nodeId)  throws ServiceExceptions.NodeNotFoundException, ServiceExceptions.NotSupportedException;
        
    /**
     * Retrieves the root node referencing the workflow
     *
     * @param jobId ID the job the workflow is requested for
     *
     * @return the result
     */
    NodeEnt getRootNode(java.util.UUID jobId) ;
        
    /**
     * Sets the node&#39;s settings for the given node-id.
     *
     * @param jobId ID the job the workflow is requested for
     * @param nodeId The ID of the node the information is requested for. For nested nodes the node ids are concatenated with an &#39;:&#39;, e.g. 3:6:4
     * @param nodeSettings The node settings to set.
     *
     * 
     * @throws ServiceExceptions.NodeNotFoundException The requested node was not found.
     * @throws ServiceExceptions.InvalidSettingsException If settings couldn&#39;t be applied.
     */
    void setNodeSettings(java.util.UUID jobId, String nodeId, NodeSettingsEnt nodeSettings)  throws ServiceExceptions.NodeNotFoundException, ServiceExceptions.InvalidSettingsException;
        
}

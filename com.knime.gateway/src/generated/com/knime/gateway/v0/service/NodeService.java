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

import com.knime.gateway.v0.entity.BoundsEnt;
import com.knime.gateway.v0.entity.DataTableEnt;
import com.knime.gateway.v0.entity.FlowVariableEnt;
import com.knime.gateway.v0.entity.JavaObjectEnt;
import com.knime.gateway.v0.entity.MetaNodeDialogEnt;
import com.knime.gateway.v0.entity.NodeEnt;
import com.knime.gateway.v0.entity.NodeFactoryKeyEnt;
import com.knime.gateway.v0.entity.NodeSettingsEnt;
import com.knime.gateway.v0.entity.PortObjectSpecEnt;
import com.knime.gateway.v0.entity.ViewDataEnt;

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
     * @param jobId ID of the job the workflow is requested for.
     * @param nodeId The ID of a node. The node-id format: For nested nodes the node ids are concatenated with an &#39;:&#39;, e.g. 3:6:4. Nodes within wrapped metanodes require an additional trailing &#39;0&#39;, e.g. 3:6:0:4 (if 3:6 is a wrapped metanode).
     * @param action The action (reset, cancel, execute) to be performed in order to change the node&#39;s state.
     *
     * @return the result
     * @throws ServiceExceptions.NodeNotFoundException The requested node was not found.
     * @throws ServiceExceptions.ActionNotAllowedException If an action is not allowed because it&#39;s not applicable or it doesn&#39;t exist. Please refer to the exception message for more details.
     */
    String changeAndGetNodeState(java.util.UUID jobId, String nodeId, String action)  throws ServiceExceptions.NodeNotFoundException, ServiceExceptions.ActionNotAllowedException;
        
    /**
     * Creates and adds a new native node to the workflow.
     *
     * @param jobId ID of the job the workflow is requested for.
     * @param x the x coordinate to place the new node
     * @param y the y coordinate to place the new node
     * @param nodeFactoryKeyEnt The key representing the native node to be added to the workflow.
     * @param parentNodeId Optional id of the parent node if the new node should be added to a sub-workflow. The node-id format: For nested nodes the node ids are concatenated with an &#39;:&#39;, e.g. 3:6:4. Nodes within wrapped metanodes required an additional trailing &#39;0&#39;, e.g. 3:6:0:4 (if 3:6 is a wrapped metanode).
     *
     * @return the result
     * @throws ServiceExceptions.NotASubWorkflowException The requested node is not a sub-workflow (i.e. a meta- or sub-node), but is required to be.
     * @throws ServiceExceptions.NodeNotFoundException The requested node was not found.
     * @throws ServiceExceptions.InvalidRequestException If the request is invalid for a reason. Please refer to the exception message for more details.
     */
    String createNode(java.util.UUID jobId, Integer x, Integer y, NodeFactoryKeyEnt nodeFactoryKeyEnt, String parentNodeId)  throws ServiceExceptions.NotASubWorkflowException, ServiceExceptions.NodeNotFoundException, ServiceExceptions.InvalidRequestException;
        
    /**
     * Provides the node&#39;s flow variables available for the node with the given node-id.
     *
     * @param jobId ID of the job the workflow is requested for.
     * @param nodeId The ID of a node. The node-id format: For nested nodes the node ids are concatenated with an &#39;:&#39;, e.g. 3:6:4. Nodes within wrapped metanodes require an additional trailing &#39;0&#39;, e.g. 3:6:0:4 (if 3:6 is a wrapped metanode).
     *
     * @return the result
     * @throws ServiceExceptions.NodeNotFoundException The requested node was not found.
     */
    java.util.List<FlowVariableEnt> getInputFlowVariables(java.util.UUID jobId, String nodeId)  throws ServiceExceptions.NodeNotFoundException;
        
    /**
     * Provides the node&#39;s input port specifications for the given node-id. I.e. all output port specs of the output ports connected to the input ports of this node.
     *
     * @param jobId ID of the job the workflow is requested for.
     * @param nodeId The ID of a node. The node-id format: For nested nodes the node ids are concatenated with an &#39;:&#39;, e.g. 3:6:4. Nodes within wrapped metanodes require an additional trailing &#39;0&#39;, e.g. 3:6:0:4 (if 3:6 is a wrapped metanode).
     *
     * @return the result
     * @throws ServiceExceptions.NodeNotFoundException The requested node was not found.
     */
    java.util.List<PortObjectSpecEnt> getInputPortSpecs(java.util.UUID jobId, String nodeId)  throws ServiceExceptions.NodeNotFoundException;
        
    /**
     * Retrieves the node for the given node-id.
     *
     * @param jobId ID of the job the workflow is requested for.
     * @param nodeId The ID of a node. The node-id format: For nested nodes the node ids are concatenated with an &#39;:&#39;, e.g. 3:6:4. Nodes within wrapped metanodes require an additional trailing &#39;0&#39;, e.g. 3:6:0:4 (if 3:6 is a wrapped metanode).
     *
     * @return the result
     * @throws ServiceExceptions.NodeNotFoundException The requested node was not found.
     */
    NodeEnt getNode(java.util.UUID jobId, String nodeId)  throws ServiceExceptions.NodeNotFoundException;
        
    /**
     * Retrieves the node&#39;s settings for the given node-id.
     *
     * @param jobId ID of the job the workflow is requested for.
     * @param nodeId The ID of a node. The node-id format: For nested nodes the node ids are concatenated with an &#39;:&#39;, e.g. 3:6:4. Nodes within wrapped metanodes require an additional trailing &#39;0&#39;, e.g. 3:6:0:4 (if 3:6 is a wrapped metanode).
     *
     * @return the result
     * @throws ServiceExceptions.NodeNotFoundException The requested node was not found.
     */
    NodeSettingsEnt getNodeSettings(java.util.UUID jobId, String nodeId)  throws ServiceExceptions.NodeNotFoundException;
        
    /**
     * Gives access to the table at the a certain port index (if it&#39;s a table). Otherwise will return &#39;not supported&#39;.
     *
     * @param jobId ID of the job the workflow is requested for.
     * @param nodeId The ID of a node. The node-id format: For nested nodes the node ids are concatenated with an &#39;:&#39;, e.g. 3:6:4. Nodes within wrapped metanodes require an additional trailing &#39;0&#39;, e.g. 3:6:0:4 (if 3:6 is a wrapped metanode).
     * @param portIdx The port index to get the table for.
     * @param from Row index to start returning the rows. Rows from the beginning (i.e. index 0) will be returned.
     * @param size Number of rows to retrieve. If not given, all rows to the end of the table are returned.
     *
     * @return the result
     * @throws ServiceExceptions.NodeNotFoundException The requested node was not found.
     * @throws ServiceExceptions.InvalidRequestException If the request is invalid for a reason. Please refer to the exception message for more details.
     */
    DataTableEnt getOutputDataTable(java.util.UUID jobId, String nodeId, Integer portIdx, Long from, Integer size)  throws ServiceExceptions.NodeNotFoundException, ServiceExceptions.InvalidRequestException;
        
    /**
     * Provides the node&#39;s flow variables available for the node&#39;s output with the given node-id. For metanodes (not wrapped metanodes) the same variables are returned as with &#39;... For metanodes (not wrapped metanodes) the same variables are returned as with &#39;.../input/flowvariables&#39;.
     *
     * @param jobId ID of the job the workflow is requested for.
     * @param nodeId The ID of a node. The node-id format: For nested nodes the node ids are concatenated with an &#39;:&#39;, e.g. 3:6:4. Nodes within wrapped metanodes require an additional trailing &#39;0&#39;, e.g. 3:6:0:4 (if 3:6 is a wrapped metanode).
     *
     * @return the result
     * @throws ServiceExceptions.NodeNotFoundException The requested node was not found.
     */
    java.util.List<FlowVariableEnt> getOutputFlowVariables(java.util.UUID jobId, String nodeId)  throws ServiceExceptions.NodeNotFoundException;
        
    /**
     * Provides the node&#39;s output port specifications for the given node-id.
     *
     * @param jobId ID of the job the workflow is requested for.
     * @param nodeId The ID of a node. The node-id format: For nested nodes the node ids are concatenated with an &#39;:&#39;, e.g. 3:6:4. Nodes within wrapped metanodes require an additional trailing &#39;0&#39;, e.g. 3:6:0:4 (if 3:6 is a wrapped metanode).
     *
     * @return the result
     * @throws ServiceExceptions.NodeNotFoundException The requested node was not found.
     */
    java.util.List<PortObjectSpecEnt> getOutputPortSpecs(java.util.UUID jobId, String nodeId)  throws ServiceExceptions.NodeNotFoundException;
        
    /**
     * Retrieves the root node referencing the workflow
     *
     * @param jobId ID of the job the workflow is requested for.
     *
     * @return the result
     */
    NodeEnt getRootNode(java.util.UUID jobId) ;
        
    /**
     * Provides the data for the node&#39;s view, if the node supports views. The data includes the view&#39;s and the representation.
     *
     * @param jobId ID of the job the workflow is requested for.
     * @param nodeId The ID of a node. The node-id format: For nested nodes the node ids are concatenated with an &#39;:&#39;, e.g. 3:6:4. Nodes within wrapped metanodes require an additional trailing &#39;0&#39;, e.g. 3:6:0:4 (if 3:6 is a wrapped metanode).
     *
     * @return the result
     * @throws ServiceExceptions.NodeNotFoundException The requested node was not found.
     * @throws ServiceExceptions.InvalidRequestException If the request is invalid for a reason. Please refer to the exception message for more details.
     */
    ViewDataEnt getViewData(java.util.UUID jobId, String nodeId)  throws ServiceExceptions.NodeNotFoundException, ServiceExceptions.InvalidRequestException;
        
    /**
     * Gives access to the dialog representations, values and configs of a wrapped metanode.
     *
     * @param jobId ID of the job the workflow is requested for.
     * @param nodeId The ID of a node. The node-id format: For nested nodes the node ids are concatenated with an &#39;:&#39;, e.g. 3:6:4. Nodes within wrapped metanodes require an additional trailing &#39;0&#39;, e.g. 3:6:0:4 (if 3:6 is a wrapped metanode).
     *
     * @return the result
     * @throws ServiceExceptions.NodeNotFoundException The requested node was not found.
     * @throws ServiceExceptions.InvalidRequestException If the request is invalid for a reason. Please refer to the exception message for more details.
     */
    MetaNodeDialogEnt getWMetaNodeDialog(java.util.UUID jobId, String nodeId)  throws ServiceExceptions.NodeNotFoundException, ServiceExceptions.InvalidRequestException;
        
    /**
     * Sets the node&#39;s bounds for the given node-id.
     *
     * @param jobId ID of the job the workflow is requested for.
     * @param nodeId The ID of a node. The node-id format: For nested nodes the node ids are concatenated with an &#39;:&#39;, e.g. 3:6:4. Nodes within wrapped metanodes require an additional trailing &#39;0&#39;, e.g. 3:6:0:4 (if 3:6 is a wrapped metanode).
     * @param boundsEnt The node bounds to set.
     *
     * 
     * @throws ServiceExceptions.NodeNotFoundException The requested node was not found.
     */
    void setNodeBounds(java.util.UUID jobId, String nodeId, BoundsEnt boundsEnt)  throws ServiceExceptions.NodeNotFoundException;
        
    /**
     * Sets the node&#39;s settings for the given node-id.
     *
     * @param jobId ID of the job the workflow is requested for.
     * @param nodeId The ID of a node. The node-id format: For nested nodes the node ids are concatenated with an &#39;:&#39;, e.g. 3:6:4. Nodes within wrapped metanodes require an additional trailing &#39;0&#39;, e.g. 3:6:0:4 (if 3:6 is a wrapped metanode).
     * @param nodeSettingsEnt The node settings to set.
     *
     * 
     * @throws ServiceExceptions.NodeNotFoundException The requested node was not found.
     * @throws ServiceExceptions.InvalidSettingsException If settings couldn&#39;t be applied.
     * @throws ServiceExceptions.IllegalStateException If node is not in the right state to apply the settings.
     */
    void setNodeSettings(java.util.UUID jobId, String nodeId, NodeSettingsEnt nodeSettingsEnt)  throws ServiceExceptions.NodeNotFoundException, ServiceExceptions.InvalidSettingsException, ServiceExceptions.IllegalStateException;
        
    /**
     * Saves back the view&#39;s value (e.g. title etc.) to the server.
     *
     * @param jobId ID of the job the workflow is requested for.
     * @param nodeId The ID of a node. The node-id format: For nested nodes the node ids are concatenated with an &#39;:&#39;, e.g. 3:6:4. Nodes within wrapped metanodes require an additional trailing &#39;0&#39;, e.g. 3:6:0:4 (if 3:6 is a wrapped metanode).
     * @param useAsDefault True if node settings are to be updated by view content.
     * @param javaObjectEnt The view value to set.
     *
     * 
     * @throws ServiceExceptions.NodeNotFoundException The requested node was not found.
     * @throws ServiceExceptions.InvalidRequestException If the request is invalid for a reason. Please refer to the exception message for more details.
     */
    void setViewValue(java.util.UUID jobId, String nodeId, Boolean useAsDefault, JavaObjectEnt javaObjectEnt)  throws ServiceExceptions.NodeNotFoundException, ServiceExceptions.InvalidRequestException;
        
}

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
package org.knime.gateway.api.service;

import org.knime.gateway.api.service.GatewayService;
import org.knime.gateway.api.service.util.ServiceExceptions;

import org.knime.gateway.api.entity.BoundsEnt;
import org.knime.gateway.api.entity.DataTableEnt;
import org.knime.gateway.api.entity.FlowVariableEnt;
import org.knime.gateway.api.entity.JavaObjectEnt;
import org.knime.gateway.api.entity.MetaNodeDialogEnt;
import org.knime.gateway.api.entity.NodeEnt;
import org.knime.gateway.api.entity.NodeFactoryKeyEnt;
import org.knime.gateway.api.entity.NodeSettingsEnt;
import org.knime.gateway.api.entity.PortObjectSpecEnt;
import org.knime.gateway.api.entity.ViewDataEnt;

/**
 * Operations on single nodes.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
@javax.annotation.Generated(value = {"com.knime.gateway.codegen.GatewayCodegen", "src-gen/org.knime.gateway.api-config.json"})
public interface NodeService extends GatewayService {

    /**
     * Retrieves and manipulates the node&#39;s state for the given node-id.
     *
     * @param jobId ID of the job the workflow is requested for.
     * @param nodeId The ID of a node. The node-id format: Node IDs always start with &#39;root&#39; and optionally followed by numbers separated by &#39;:&#39; refering to nested nodes/subworkflows,e.g. root:3:6:4. Nodes within components require an additional trailing &#39;0&#39;, e.g. &#39;root:3:6:0:4&#39; (if &#39;root:3:6&#39; is a component).
     * @param action The action (reset, cancel, execute) to be performed in order to change the node&#39;s state.
     *
     * @return the result
     * @throws ServiceExceptions.NodeNotFoundException The requested node was not found.
     * @throws ServiceExceptions.ActionNotAllowedException If the an action is not allowed because it&#39;s not applicable or it doesn&#39;t exist.
     */
    String changeAndGetNodeState(java.util.UUID jobId, org.knime.gateway.api.entity.NodeIDEnt nodeId, String action)  throws ServiceExceptions.NodeNotFoundException, ServiceExceptions.ActionNotAllowedException;
        
    /**
     * Creates and adds a new native node to the workflow.
     *
     * @param jobId ID of the job the workflow is requested for.
     * @param workflowId The ID of a worklow which has the same format as a node-id.
     * @param x the x coordinate to place the new node
     * @param y the y coordinate to place the new node
     * @param nodeFactoryKeyEnt The key representing the native node to be added to the workflow.
     *
     * @return the result
     * @throws ServiceExceptions.NotASubWorkflowException The requested node is not a sub-workflow (i.e. a meta- or sub-node), but is required to be.
     * @throws ServiceExceptions.NodeNotFoundException The requested node was not found.
     * @throws ServiceExceptions.InvalidRequestException If the request is invalid for a reason.
     */
    org.knime.gateway.api.entity.NodeIDEnt createNode(java.util.UUID jobId, org.knime.gateway.api.entity.NodeIDEnt workflowId, Integer x, Integer y, NodeFactoryKeyEnt nodeFactoryKeyEnt)  throws ServiceExceptions.NotASubWorkflowException, ServiceExceptions.NodeNotFoundException, ServiceExceptions.InvalidRequestException;
        
    /**
     * Provides the node&#39;s flow variables available for the node with the given node-id.
     *
     * @param jobId ID of the job the workflow is requested for.
     * @param nodeId The ID of a node. The node-id format: Node IDs always start with &#39;root&#39; and optionally followed by numbers separated by &#39;:&#39; refering to nested nodes/subworkflows,e.g. root:3:6:4. Nodes within components require an additional trailing &#39;0&#39;, e.g. &#39;root:3:6:0:4&#39; (if &#39;root:3:6&#39; is a component).
     *
     * @return the result
     * @throws ServiceExceptions.NodeNotFoundException The requested node was not found.
     */
    java.util.List<FlowVariableEnt> getInputFlowVariables(java.util.UUID jobId, org.knime.gateway.api.entity.NodeIDEnt nodeId)  throws ServiceExceptions.NodeNotFoundException;
        
    /**
     * Provides the node&#39;s input port specifications for the given node-id. I.e. all output port specs of the output ports connected to the input ports of this node.
     *
     * @param jobId ID of the job the workflow is requested for.
     * @param nodeId The ID of a node. The node-id format: Node IDs always start with &#39;root&#39; and optionally followed by numbers separated by &#39;:&#39; refering to nested nodes/subworkflows,e.g. root:3:6:4. Nodes within components require an additional trailing &#39;0&#39;, e.g. &#39;root:3:6:0:4&#39; (if &#39;root:3:6&#39; is a component).
     *
     * @return the result
     * @throws ServiceExceptions.NodeNotFoundException The requested node was not found.
     */
    java.util.List<PortObjectSpecEnt> getInputPortSpecs(java.util.UUID jobId, org.knime.gateway.api.entity.NodeIDEnt nodeId)  throws ServiceExceptions.NodeNotFoundException;
        
    /**
     * Retrieves the node for the given node-id.
     *
     * @param jobId ID of the job the workflow is requested for.
     * @param nodeId The ID of a node. The node-id format: Node IDs always start with &#39;root&#39; and optionally followed by numbers separated by &#39;:&#39; refering to nested nodes/subworkflows,e.g. root:3:6:4. Nodes within components require an additional trailing &#39;0&#39;, e.g. &#39;root:3:6:0:4&#39; (if &#39;root:3:6&#39; is a component).
     *
     * @return the result
     * @throws ServiceExceptions.NodeNotFoundException The requested node was not found.
     */
    NodeEnt getNode(java.util.UUID jobId, org.knime.gateway.api.entity.NodeIDEnt nodeId)  throws ServiceExceptions.NodeNotFoundException;
        
    /**
     * Retrieves the node&#39;s settings for the given node-id.
     *
     * @param jobId ID of the job the workflow is requested for.
     * @param nodeId The ID of a node. The node-id format: Node IDs always start with &#39;root&#39; and optionally followed by numbers separated by &#39;:&#39; refering to nested nodes/subworkflows,e.g. root:3:6:4. Nodes within components require an additional trailing &#39;0&#39;, e.g. &#39;root:3:6:0:4&#39; (if &#39;root:3:6&#39; is a component).
     *
     * @return the result
     * @throws ServiceExceptions.NodeNotFoundException The requested node was not found.
     */
    NodeSettingsEnt getNodeSettings(java.util.UUID jobId, org.knime.gateway.api.entity.NodeIDEnt nodeId)  throws ServiceExceptions.NodeNotFoundException;
        
    /**
     * Gives access to the table at the a certain port index (if it&#39;s a table). Otherwise will return &#39;not supported&#39;.
     *
     * @param jobId ID of the job the workflow is requested for.
     * @param nodeId The ID of a node. The node-id format: Node IDs always start with &#39;root&#39; and optionally followed by numbers separated by &#39;:&#39; refering to nested nodes/subworkflows,e.g. root:3:6:4. Nodes within components require an additional trailing &#39;0&#39;, e.g. &#39;root:3:6:0:4&#39; (if &#39;root:3:6&#39; is a component).
     * @param portIdx The port index to get the table for.
     * @param from Row index to start returning the rows. Rows from the beginning (i.e. index 0) will be returned.
     * @param size Number of rows to retrieve. If not given, all rows to the end of the table are returned.
     *
     * @return the result
     * @throws ServiceExceptions.NodeNotFoundException The requested node was not found.
     * @throws ServiceExceptions.InvalidRequestException If the request is invalid for a reason.
     */
    DataTableEnt getOutputDataTable(java.util.UUID jobId, org.knime.gateway.api.entity.NodeIDEnt nodeId, Integer portIdx, Long from, Integer size)  throws ServiceExceptions.NodeNotFoundException, ServiceExceptions.InvalidRequestException;
        
    /**
     * Provides the node&#39;s flow variables available for the node&#39;s output with the given node-id. For metanodes (not components) the same variables are returned as with &#39;... For metanodes (not components) the same variables are returned as with &#39;.../input/flowvariables&#39;.
     *
     * @param jobId ID of the job the workflow is requested for.
     * @param nodeId The ID of a node. The node-id format: Node IDs always start with &#39;root&#39; and optionally followed by numbers separated by &#39;:&#39; refering to nested nodes/subworkflows,e.g. root:3:6:4. Nodes within components require an additional trailing &#39;0&#39;, e.g. &#39;root:3:6:0:4&#39; (if &#39;root:3:6&#39; is a component).
     *
     * @return the result
     * @throws ServiceExceptions.NodeNotFoundException The requested node was not found.
     */
    java.util.List<FlowVariableEnt> getOutputFlowVariables(java.util.UUID jobId, org.knime.gateway.api.entity.NodeIDEnt nodeId)  throws ServiceExceptions.NodeNotFoundException;
        
    /**
     * Provides the node&#39;s output port specifications for the given node-id.
     *
     * @param jobId ID of the job the workflow is requested for.
     * @param nodeId The ID of a node. The node-id format: Node IDs always start with &#39;root&#39; and optionally followed by numbers separated by &#39;:&#39; refering to nested nodes/subworkflows,e.g. root:3:6:4. Nodes within components require an additional trailing &#39;0&#39;, e.g. &#39;root:3:6:0:4&#39; (if &#39;root:3:6&#39; is a component).
     *
     * @return the result
     * @throws ServiceExceptions.NodeNotFoundException The requested node was not found.
     */
    java.util.List<PortObjectSpecEnt> getOutputPortSpecs(java.util.UUID jobId, org.knime.gateway.api.entity.NodeIDEnt nodeId)  throws ServiceExceptions.NodeNotFoundException;
        
    /**
     * Provides the data for the node&#39;s view, if the node supports views. The data includes the view&#39;s and the representation.
     *
     * @param jobId ID of the job the workflow is requested for.
     * @param nodeId The ID of a node. The node-id format: Node IDs always start with &#39;root&#39; and optionally followed by numbers separated by &#39;:&#39; refering to nested nodes/subworkflows,e.g. root:3:6:4. Nodes within components require an additional trailing &#39;0&#39;, e.g. &#39;root:3:6:0:4&#39; (if &#39;root:3:6&#39; is a component).
     *
     * @return the result
     * @throws ServiceExceptions.NodeNotFoundException The requested node was not found.
     * @throws ServiceExceptions.InvalidRequestException If the request is invalid for a reason.
     */
    ViewDataEnt getViewData(java.util.UUID jobId, org.knime.gateway.api.entity.NodeIDEnt nodeId)  throws ServiceExceptions.NodeNotFoundException, ServiceExceptions.InvalidRequestException;
        
    /**
     * Gives access to the dialog representations, values and configs of a component.
     *
     * @param jobId ID of the job the workflow is requested for.
     * @param nodeId The ID of a node. The node-id format: Node IDs always start with &#39;root&#39; and optionally followed by numbers separated by &#39;:&#39; refering to nested nodes/subworkflows,e.g. root:3:6:4. Nodes within components require an additional trailing &#39;0&#39;, e.g. &#39;root:3:6:0:4&#39; (if &#39;root:3:6&#39; is a component).
     *
     * @return the result
     * @throws ServiceExceptions.NodeNotFoundException The requested node was not found.
     * @throws ServiceExceptions.InvalidRequestException If the request is invalid for a reason.
     */
    MetaNodeDialogEnt getWMetaNodeDialog(java.util.UUID jobId, org.knime.gateway.api.entity.NodeIDEnt nodeId)  throws ServiceExceptions.NodeNotFoundException, ServiceExceptions.InvalidRequestException;
        
    /**
     * Replaces the node with the given id and replaces it with a newly created node (supplied node factory). NOTE! The endpoint is not yet fully functional - only works with replacements of nodes with the same factory. See https://knime-com.atlassian.net/browse/SRV-1692 for follow-up.
     *
     * @param jobId ID of the job the workflow is requested for.
     * @param nodeId The ID of a node. The node-id format: Node IDs always start with &#39;root&#39; and optionally followed by numbers separated by &#39;:&#39; refering to nested nodes/subworkflows,e.g. root:3:6:4. Nodes within components require an additional trailing &#39;0&#39;, e.g. &#39;root:3:6:0:4&#39; (if &#39;root:3:6&#39; is a component).
     * @param nodeFactoryKeyEnt The node factory representing the native node replacement.
     *
     * @return the result
     * @throws ServiceExceptions.NodeNotFoundException The requested node was not found.
     * @throws ServiceExceptions.ActionNotAllowedException If the an action is not allowed because it&#39;s not applicable or it doesn&#39;t exist.
     */
    NodeEnt replaceNode(java.util.UUID jobId, org.knime.gateway.api.entity.NodeIDEnt nodeId, NodeFactoryKeyEnt nodeFactoryKeyEnt)  throws ServiceExceptions.NodeNotFoundException, ServiceExceptions.ActionNotAllowedException;
        
    /**
     * Sets the node&#39;s bounds for the given node-id.
     *
     * @param jobId ID of the job the workflow is requested for.
     * @param nodeId The ID of a node. The node-id format: Node IDs always start with &#39;root&#39; and optionally followed by numbers separated by &#39;:&#39; refering to nested nodes/subworkflows,e.g. root:3:6:4. Nodes within components require an additional trailing &#39;0&#39;, e.g. &#39;root:3:6:0:4&#39; (if &#39;root:3:6&#39; is a component).
     * @param boundsEnt The node bounds to set.
     *
     * 
     * @throws ServiceExceptions.NodeNotFoundException The requested node was not found.
     */
    void setNodeBounds(java.util.UUID jobId, org.knime.gateway.api.entity.NodeIDEnt nodeId, BoundsEnt boundsEnt)  throws ServiceExceptions.NodeNotFoundException;
        
    /**
     * Sets the node&#39;s settings for the given node-id.
     *
     * @param jobId ID of the job the workflow is requested for.
     * @param nodeId The ID of a node. The node-id format: Node IDs always start with &#39;root&#39; and optionally followed by numbers separated by &#39;:&#39; refering to nested nodes/subworkflows,e.g. root:3:6:4. Nodes within components require an additional trailing &#39;0&#39;, e.g. &#39;root:3:6:0:4&#39; (if &#39;root:3:6&#39; is a component).
     * @param nodeSettingsEnt The node settings to set.
     *
     * 
     * @throws ServiceExceptions.NodeNotFoundException The requested node was not found.
     * @throws ServiceExceptions.InvalidSettingsException If settings couldn&#39;t be applied.
     * @throws ServiceExceptions.IllegalStateException If node is not in the right state to apply the settings.
     */
    void setNodeSettings(java.util.UUID jobId, org.knime.gateway.api.entity.NodeIDEnt nodeId, NodeSettingsEnt nodeSettingsEnt)  throws ServiceExceptions.NodeNotFoundException, ServiceExceptions.InvalidSettingsException, ServiceExceptions.IllegalStateException;
        
    /**
     * Saves back the view&#39;s value (e.g. title etc.) to the server.
     *
     * @param jobId ID of the job the workflow is requested for.
     * @param nodeId The ID of a node. The node-id format: Node IDs always start with &#39;root&#39; and optionally followed by numbers separated by &#39;:&#39; refering to nested nodes/subworkflows,e.g. root:3:6:4. Nodes within components require an additional trailing &#39;0&#39;, e.g. &#39;root:3:6:0:4&#39; (if &#39;root:3:6&#39; is a component).
     * @param useAsDefault True if node settings are to be updated by view content.
     * @param javaObjectEnt The view value to set.
     *
     * 
     * @throws ServiceExceptions.NodeNotFoundException The requested node was not found.
     * @throws ServiceExceptions.InvalidRequestException If the request is invalid for a reason.
     */
    void setViewValue(java.util.UUID jobId, org.knime.gateway.api.entity.NodeIDEnt nodeId, Boolean useAsDefault, JavaObjectEnt javaObjectEnt)  throws ServiceExceptions.NodeNotFoundException, ServiceExceptions.InvalidRequestException;
        
}

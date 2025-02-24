/*
 * ------------------------------------------------------------------------
 *  Copyright by KNIME AG, Zurich, Switzerland
 *  Website: http://www.knime.com; Email: contact@knime.com
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License, Version 3, as
 *  published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but
 *  WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, see <http://www.gnu.org/licenses>.
 *
 *  Additional permission under GNU GPL version 3 section 7:
 *
 *  KNIME interoperates with ECLIPSE solely via ECLIPSE's plug-in APIs.
 *  Hence, KNIME and ECLIPSE are both independent programs and are not
 *  derived from each other. Should, however, the interpretation of the
 *  GNU GPL Version 3 ("License") under any applicable laws result in
 *  KNIME and ECLIPSE being a combined program, KNIME AG herewith grants
 *  you the additional permission to use and propagate KNIME together with
 *  ECLIPSE with only the license terms in place for ECLIPSE applying to
 *  ECLIPSE and the GNU GPL Version 3 applying for KNIME, provided the
 *  license terms of ECLIPSE themselves allow for the respective use and
 *  propagation of ECLIPSE together with KNIME.
 *
 *  Additional permission relating to nodes for KNIME that extend the Node
 *  Extension (and in particular that are based on subclasses of NodeModel,
 *  NodeDialog, and NodeView) and that only interoperate with KNIME through
 *  standard APIs ("Nodes"):
 *  Nodes are deemed to be separate and independent programs and to not be
 *  covered works.  Notwithstanding anything to the contrary in the
 *  License, the License does not apply to Nodes, you are not required to
 *  license Nodes under the License, and you are granted a license to
 *  prepare and propagate Nodes, in each case even if such Nodes are
 *  propagated with or for interoperation with KNIME.  The owner of a Node
 *  may freely choose the license terms applicable to such Node, including
 *  when such Node is propagated with or for interoperation with KNIME.
 * ------------------------------------------------------------------------
 */
package org.knime.gateway.api.webui.service;

import org.knime.gateway.api.service.GatewayService;
import org.knime.gateway.api.webui.service.util.ServiceExceptions;

import org.knime.gateway.api.webui.entity.NativeNodeDescriptionEnt;
import org.knime.gateway.api.webui.entity.NodeFactoryKeyEnt;

/**
 * Operations on individual nodes in a workflow.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
@jakarta.annotation.Generated(value = {"com.knime.gateway.codegen.GatewayCodegen", "src-gen/api/web-ui/configs/org.knime.gateway.api-config.json"})
public interface NodeService extends GatewayService {

    /**
     * Calls a data service of a specific type for a specific node.
     *
     * @param projectId ID of the workflow-project.
     * @param workflowId The ID of a workflow which has the same format as a node-id.
     * @param nodeId The ID of a node. The node-id format: Node IDs always start with &#39;root&#39; and optionally followed by numbers separated by &#39;:&#39; referring to nested nodes/subworkflows,e.g. root:3:6:4. Nodes within components require an additional trailing &#39;0&#39;, e.g. &#39;root:3:6:0:4&#39; (if &#39;root:3:6&#39; is a component).
     * @param extensionType The node ui-extension-type, i.e. dialog or view.
     * @param serviceType 
     * @param dataServiceRequest 
     *
     * @return the result
     * @throws ServiceExceptions.NodeNotFoundException The requested node was not found.
     * @throws ServiceExceptions.InvalidRequestException If the request is invalid for a reason.
     */
    String callNodeDataService(String projectId, org.knime.gateway.api.entity.NodeIDEnt workflowId, org.knime.gateway.api.entity.NodeIDEnt nodeId, String extensionType, String serviceType, String dataServiceRequest)  throws ServiceExceptions.NodeNotFoundException, ServiceExceptions.InvalidRequestException;
        
    /**
     * Changes state of a loop. The provided node-id must reference a loop-end node.
     *
     * @param projectId ID of the workflow-project.
     * @param workflowId The ID of a workflow which has the same format as a node-id.
     * @param nodeId The ID of a node. The node-id format: Node IDs always start with &#39;root&#39; and optionally followed by numbers separated by &#39;:&#39; referring to nested nodes/subworkflows,e.g. root:3:6:4. Nodes within components require an additional trailing &#39;0&#39;, e.g. &#39;root:3:6:0:4&#39; (if &#39;root:3:6&#39; is a component).
     * @param action The action (pause, resume, step) to be performed in order to change the loop state.
     *
     * 
     * @throws ServiceExceptions.NodeNotFoundException The requested node was not found.
     * @throws ServiceExceptions.OperationNotAllowedException If the an operation is not allowed, e.g., because it&#39;s not applicable.
     */
    void changeLoopState(String projectId, org.knime.gateway.api.entity.NodeIDEnt workflowId, org.knime.gateway.api.entity.NodeIDEnt nodeId, String action)  throws ServiceExceptions.NodeNotFoundException, ServiceExceptions.OperationNotAllowedException;
        
    /**
     * Changes the node state of multiple nodes represented by a list of node-ids.
     *
     * @param projectId ID of the workflow-project.
     * @param workflowId The ID of a workflow which has the same format as a node-id.
     * @param nodeIds The list of node ids of the nodes to be changed. All ids must reference nodes on the same workflow level. If no node ids are given the state of the parent workflow (i.e. the one referenced by workflow-id) is changed which is equivalent to change the states of all contained nodes.
     * @param action The action (reset, cancel, execute) to be performed in order to change the node&#39;s state.
     *
     * 
     * @throws ServiceExceptions.NodeNotFoundException The requested node was not found.
     * @throws ServiceExceptions.OperationNotAllowedException If the an operation is not allowed, e.g., because it&#39;s not applicable.
     */
    void changeNodeStates(String projectId, org.knime.gateway.api.entity.NodeIDEnt workflowId, java.util.List<org.knime.gateway.api.entity.NodeIDEnt> nodeIds, String action)  throws ServiceExceptions.NodeNotFoundException, ServiceExceptions.OperationNotAllowedException;
        
    /**
     * De-activates all the data service associated with the specified ui-extension.
     *
     * @param projectId ID of the workflow-project.
     * @param workflowId The ID of a workflow which has the same format as a node-id.
     * @param nodeId The ID of a node. The node-id format: Node IDs always start with &#39;root&#39; and optionally followed by numbers separated by &#39;:&#39; referring to nested nodes/subworkflows,e.g. root:3:6:4. Nodes within components require an additional trailing &#39;0&#39;, e.g. &#39;root:3:6:0:4&#39; (if &#39;root:3:6&#39; is a component).
     * @param extensionType The node ui-extension-type, i.e. dialog or view.
     *
     * 
     * @throws ServiceExceptions.NodeNotFoundException The requested node was not found.
     * @throws ServiceExceptions.InvalidRequestException If the request is invalid for a reason.
     */
    void deactivateNodeDataServices(String projectId, org.knime.gateway.api.entity.NodeIDEnt workflowId, org.knime.gateway.api.entity.NodeIDEnt nodeId, String extensionType)  throws ServiceExceptions.NodeNotFoundException, ServiceExceptions.InvalidRequestException;
        
    /**
     * Obtain the description of a given node.
     *
     * @param nodeFactoryKey The key identifying the node.
     *
     * @return the result
     * @throws ServiceExceptions.NodeNotFoundException The requested node was not found.
     * @throws ServiceExceptions.NodeDescriptionNotAvailableException A description for a given node could not be determined.
     */
    NativeNodeDescriptionEnt getNodeDescription(NodeFactoryKeyEnt nodeFactoryKey)  throws ServiceExceptions.NodeNotFoundException, ServiceExceptions.NodeDescriptionNotAvailableException;
        
    /**
     * Returns all the information on a node dialog required to render it.
     *
     * @param projectId ID of the workflow-project.
     * @param workflowId The ID of a workflow which has the same format as a node-id.
     * @param nodeId The ID of a node. The node-id format: Node IDs always start with &#39;root&#39; and optionally followed by numbers separated by &#39;:&#39; referring to nested nodes/subworkflows,e.g. root:3:6:4. Nodes within components require an additional trailing &#39;0&#39;, e.g. &#39;root:3:6:0:4&#39; (if &#39;root:3:6&#39; is a component).
     *
     * @return the result
     * @throws ServiceExceptions.NodeNotFoundException The requested node was not found.
     * @throws ServiceExceptions.InvalidRequestException If the request is invalid for a reason.
     */
    Object getNodeDialog(String projectId, org.knime.gateway.api.entity.NodeIDEnt workflowId, org.knime.gateway.api.entity.NodeIDEnt nodeId)  throws ServiceExceptions.NodeNotFoundException, ServiceExceptions.InvalidRequestException;
        
    /**
     * Returns all the information on a node view required to render it.
     *
     * @param projectId ID of the workflow-project.
     * @param workflowId The ID of a workflow which has the same format as a node-id.
     * @param nodeId The ID of a node. The node-id format: Node IDs always start with &#39;root&#39; and optionally followed by numbers separated by &#39;:&#39; referring to nested nodes/subworkflows,e.g. root:3:6:4. Nodes within components require an additional trailing &#39;0&#39;, e.g. &#39;root:3:6:0:4&#39; (if &#39;root:3:6&#39; is a component).
     *
     * @return the result
     * @throws ServiceExceptions.NodeNotFoundException The requested node was not found.
     * @throws ServiceExceptions.InvalidRequestException If the request is invalid for a reason.
     */
    Object getNodeView(String projectId, org.knime.gateway.api.entity.NodeIDEnt workflowId, org.knime.gateway.api.entity.NodeIDEnt nodeId)  throws ServiceExceptions.NodeNotFoundException, ServiceExceptions.InvalidRequestException;
        
    /**
     * Updates the data point selection (aka hiliting) for a single node as specified.
     *
     * @param projectId ID of the workflow-project.
     * @param workflowId The ID of a workflow which has the same format as a node-id.
     * @param nodeId The ID of a node. The node-id format: Node IDs always start with &#39;root&#39; and optionally followed by numbers separated by &#39;:&#39; referring to nested nodes/subworkflows,e.g. root:3:6:4. Nodes within components require an additional trailing &#39;0&#39;, e.g. &#39;root:3:6:0:4&#39; (if &#39;root:3:6&#39; is a component).
     * @param mode Whether to add, remove or replace the data point selection.
     * @param selection A list of strings that are translated to the row keys affected by the data point selection modification.
     *
     * 
     */
    void updateDataPointSelection(String projectId, org.knime.gateway.api.entity.NodeIDEnt workflowId, org.knime.gateway.api.entity.NodeIDEnt nodeId, String mode, java.util.List<String> selection)  throws ServiceExceptions.NodeNotFoundException;
        
}

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


/**
 * 
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
@jakarta.annotation.Generated(value = {"com.knime.gateway.codegen.GatewayCodegen", "src-gen/api/web-ui/configs/org.knime.gateway.api-config.json"})
public interface CompositeViewService extends GatewayService {

    /**
     * Iterates over all data services of nodes in the composite view and deactivates them. If a component is nested in another component, the data services of the view of nested component  are also recursively deactivated.
     *
     * @param projectId ID of the workflow-project.
     * @param workflowId The ID of a workflow which has the same format as a node-id.
     * @param nodeId The ID of a node. The node-id format: Node IDs always start with &#39;root&#39; and optionally followed by numbers separated by &#39;:&#39; referring to nested nodes/subworkflows,e.g. root:3:6:4. Nodes within components require an additional trailing &#39;0&#39;, e.g. &#39;root:3:6:0:4&#39; (if &#39;root:3:6&#39; is a component).
     *
     * 
     * @throws ServiceExceptions.ServiceCallException If a Gateway service call failed for some reason.
     * @throws ServiceExceptions.NodeNotFoundException The requested node was not found.
     */
    void deactivateAllCompositeViewDataServices(String projectId, org.knime.gateway.api.entity.NodeIDEnt workflowId, org.knime.gateway.api.entity.NodeIDEnt nodeId)  throws ServiceExceptions.ServiceCallException, ServiceExceptions.NodeNotFoundException;
        
    /**
     * Returns all the information on a node view required to render it.
     *
     * @param projectId ID of the workflow-project.
     * @param workflowId The ID of a workflow which has the same format as a node-id.
     * @param versionId Version ID of the project.
     * @param nodeId The ID of a node. The node-id format: Node IDs always start with &#39;root&#39; and optionally followed by numbers separated by &#39;:&#39; referring to nested nodes/subworkflows,e.g. root:3:6:4. Nodes within components require an additional trailing &#39;0&#39;, e.g. &#39;root:3:6:0:4&#39; (if &#39;root:3:6&#39; is a component).
     *
     * @return the result
     * @throws ServiceExceptions.ServiceCallException If a Gateway service call failed for some reason.
     * @throws ServiceExceptions.NodeNotFoundException The requested node was not found.
     */
    Object getCompositeViewPage(String projectId, org.knime.gateway.api.entity.NodeIDEnt workflowId, String versionId, org.knime.gateway.api.entity.NodeIDEnt nodeId)  throws ServiceExceptions.ServiceCallException, ServiceExceptions.NodeNotFoundException;
        
    /**
     * Query the current page while reexecuting
     *
     * @param projectId ID of the workflow-project.
     * @param workflowId The ID of a workflow which has the same format as a node-id.
     * @param nodeId The ID of a node. The node-id format: Node IDs always start with &#39;root&#39; and optionally followed by numbers separated by &#39;:&#39; referring to nested nodes/subworkflows,e.g. root:3:6:4. Nodes within components require an additional trailing &#39;0&#39;, e.g. &#39;root:3:6:0:4&#39; (if &#39;root:3:6&#39; is a component).
     *
     * @return the result
     * @throws ServiceExceptions.ServiceCallException If a Gateway service call failed for some reason.
     * @throws ServiceExceptions.NodeNotFoundException The requested node was not found.
     */
    Object pollCompleteComponentReexecutionStatus(String projectId, org.knime.gateway.api.entity.NodeIDEnt workflowId, org.knime.gateway.api.entity.NodeIDEnt nodeId)  throws ServiceExceptions.ServiceCallException, ServiceExceptions.NodeNotFoundException;
        
    /**
     * Query the current page while reexecuting
     *
     * @param projectId ID of the workflow-project.
     * @param workflowId The ID of a workflow which has the same format as a node-id.
     * @param nodeId The ID of a node. The node-id format: Node IDs always start with &#39;root&#39; and optionally followed by numbers separated by &#39;:&#39; referring to nested nodes/subworkflows,e.g. root:3:6:4. Nodes within components require an additional trailing &#39;0&#39;, e.g. &#39;root:3:6:0:4&#39; (if &#39;root:3:6&#39; is a component).
     * @param resetNodeIdSuffix The ID of the node that triggered the reexecution from within a component, i.e., there is no leading root.
     *
     * @return the result
     * @throws ServiceExceptions.ServiceCallException If a Gateway service call failed for some reason.
     * @throws ServiceExceptions.NodeNotFoundException The requested node was not found.
     */
    Object pollComponentReexecutionStatus(String projectId, org.knime.gateway.api.entity.NodeIDEnt workflowId, org.knime.gateway.api.entity.NodeIDEnt nodeId, String resetNodeIdSuffix)  throws ServiceExceptions.ServiceCallException, ServiceExceptions.NodeNotFoundException;
        
    /**
     * Applies viewValues as new default.  First the viewValues will be validated, then applied and when necessary the component will be executed.
     *
     * @param projectId ID of the workflow-project.
     * @param workflowId The ID of a workflow which has the same format as a node-id.
     * @param nodeId The ID of a node. The node-id format: Node IDs always start with &#39;root&#39; and optionally followed by numbers separated by &#39;:&#39; referring to nested nodes/subworkflows,e.g. root:3:6:4. Nodes within components require an additional trailing &#39;0&#39;, e.g. &#39;root:3:6:0:4&#39; (if &#39;root:3:6&#39; is a component).
     * @param viewValues 
     *
     * 
     * @throws ServiceExceptions.ServiceCallException If a Gateway service call failed for some reason.
     * @throws ServiceExceptions.NodeNotFoundException The requested node was not found.
     */
    void setViewValuesAsNewDefault(String projectId, org.knime.gateway.api.entity.NodeIDEnt workflowId, org.knime.gateway.api.entity.NodeIDEnt nodeId, java.util.Map<String, String> viewValues)  throws ServiceExceptions.ServiceCallException, ServiceExceptions.NodeNotFoundException;
        
    /**
     * Triggers the re-execution process (and updates the viewValues) for the whole component, i.e.,  every containing node will be re-executed. If a specific node has triggered the re-execution process use  &#39;trigger-reexecution/{resetNodeIdSuffix}&#39; to only re-execute that node together  with every down-stream node of it.
     *
     * @param projectId ID of the workflow-project.
     * @param workflowId The ID of a workflow which has the same format as a node-id.
     * @param nodeId The ID of a node. The node-id format: Node IDs always start with &#39;root&#39; and optionally followed by numbers separated by &#39;:&#39; referring to nested nodes/subworkflows,e.g. root:3:6:4. Nodes within components require an additional trailing &#39;0&#39;, e.g. &#39;root:3:6:0:4&#39; (if &#39;root:3:6&#39; is a component).
     * @param viewValues 
     *
     * @return the result
     * @throws ServiceExceptions.ServiceCallException If a Gateway service call failed for some reason.
     * @throws ServiceExceptions.NodeNotFoundException The requested node was not found.
     */
    Object triggerCompleteComponentReexecution(String projectId, org.knime.gateway.api.entity.NodeIDEnt workflowId, org.knime.gateway.api.entity.NodeIDEnt nodeId, java.util.Map<String, String> viewValues)  throws ServiceExceptions.ServiceCallException, ServiceExceptions.NodeNotFoundException;
        
    /**
     * Triggers the re-execution process (and updates the viewValues).
     *
     * @param projectId ID of the workflow-project.
     * @param workflowId The ID of a workflow which has the same format as a node-id.
     * @param nodeId The ID of a node. The node-id format: Node IDs always start with &#39;root&#39; and optionally followed by numbers separated by &#39;:&#39; referring to nested nodes/subworkflows,e.g. root:3:6:4. Nodes within components require an additional trailing &#39;0&#39;, e.g. &#39;root:3:6:0:4&#39; (if &#39;root:3:6&#39; is a component).
     * @param resetNodeIdSuffix The ID of the node that triggered the reexecution from within a component, i.e., there is no leading root.
     * @param viewValues 
     *
     * @return the result
     * @throws ServiceExceptions.ServiceCallException If a Gateway service call failed for some reason.
     * @throws ServiceExceptions.NodeNotFoundException The requested node was not found.
     */
    Object triggerComponentReexecution(String projectId, org.knime.gateway.api.entity.NodeIDEnt workflowId, org.knime.gateway.api.entity.NodeIDEnt nodeId, String resetNodeIdSuffix, java.util.Map<String, String> viewValues)  throws ServiceExceptions.ServiceCallException, ServiceExceptions.NodeNotFoundException;
        
}

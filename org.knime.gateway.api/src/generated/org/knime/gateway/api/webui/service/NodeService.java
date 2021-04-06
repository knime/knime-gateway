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
 * Operations on individual nodes.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
@javax.annotation.Generated(value = {"com.knime.gateway.codegen.GatewayCodegen", "src-gen/api/web-ui/configs/org.knime.gateway.api-config.json"})
public interface NodeService extends GatewayService {

    /**
     * Changes state of a loop. The provided node-id must reference a loop-end node.
     *
     * @param projectId ID of the workflow-project.
     * @param workflowId The ID of a worklow which has the same format as a node-id.
     * @param nodeId The ID of a node. The node-id format: Node IDs always start with &#39;root&#39; and optionally followed by numbers separated by &#39;:&#39; refering to nested nodes/subworkflows,e.g. root:3:6:4. Nodes within components require an additional trailing &#39;0&#39;, e.g. &#39;root:3:6:0:4&#39; (if &#39;root:3:6&#39; is a component).
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
     * @param workflowId The ID of a worklow which has the same format as a node-id.
     * @param nodeIds The list of node ids of the nodes to be changed. All ids must reference nodes on the same workflow level. If no node ids are given the state of the parent workflow (i.e. the one referenced by workflow-id)  is changed which is equivalent to change the states of all contained nodes.
     * @param action The action (reset, cancel, execute) to be performed in order to change the node&#39;s state.
     *
     * 
     * @throws ServiceExceptions.NodeNotFoundException The requested node was not found.
     * @throws ServiceExceptions.OperationNotAllowedException If the an operation is not allowed, e.g., because it&#39;s not applicable.
     */
    void changeNodeStates(String projectId, org.knime.gateway.api.entity.NodeIDEnt workflowId, java.util.List<org.knime.gateway.api.entity.NodeIDEnt> nodeIds, String action)  throws ServiceExceptions.NodeNotFoundException, ServiceExceptions.OperationNotAllowedException;
        
    /**
     * Performs text-based remote procedure calls for ports. The format of the rpc request and response depends on the port type that is being adressed.
     *
     * @param projectId ID of the workflow-project.
     * @param workflowId The ID of a worklow which has the same format as a node-id.
     * @param nodeId The ID of a node. The node-id format: Node IDs always start with &#39;root&#39; and optionally followed by numbers separated by &#39;:&#39; refering to nested nodes/subworkflows,e.g. root:3:6:4. Nodes within components require an additional trailing &#39;0&#39;, e.g. &#39;root:3:6:0:4&#39; (if &#39;root:3:6&#39; is a component).
     * @param portIdx The port index to get the table for.
     * @param body 
     *
     * @return the result
     * @throws ServiceExceptions.NodeNotFoundException The requested node was not found.
     * @throws ServiceExceptions.InvalidRequestException If the request is invalid for a reason.
     */
    String doPortRpc(String projectId, org.knime.gateway.api.entity.NodeIDEnt workflowId, org.knime.gateway.api.entity.NodeIDEnt nodeId, Integer portIdx, String body)  throws ServiceExceptions.NodeNotFoundException, ServiceExceptions.InvalidRequestException;
        
}

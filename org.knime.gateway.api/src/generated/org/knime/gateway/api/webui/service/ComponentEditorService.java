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
 * @since 5.6
 */
@jakarta.annotation.Generated(value = {"com.knime.gateway.codegen.GatewayCodegen", "src-gen/api/web-ui/configs/org.knime.gateway.api-config.json"})
public interface ComponentEditorService extends GatewayService {

    /**
     * Gets configuration layout for the component editor.
     *
     * @param projectId ID of the workflow-project.
     * @param workflowId The ID of a workflow which has the same format as a node-id.
     * @param nodeId The ID of a node. The node-id format: Node IDs always start with &#39;root&#39; and optionally followed by numbers separated by &#39;:&#39; referring to nested nodes/subworkflows,e.g. root:3:6:4. Nodes within components require an additional trailing &#39;0&#39;, e.g. &#39;root:3:6:0:4&#39; (if &#39;root:3:6&#39; is a component).
     *
     * @return the result
     * @throws ServiceExceptions.ServiceCallException If a Gateway service call failed for some reason.
     */
    String getConfigurationLayout(String projectId, org.knime.gateway.api.entity.NodeIDEnt workflowId, org.knime.gateway.api.entity.NodeIDEnt nodeId)  throws ServiceExceptions.ServiceCallException;

    /**
     * Gets configuration nodes for the component editor.
     *
     * @param projectId ID of the workflow-project.
     * @param workflowId The ID of a workflow which has the same format as a node-id.
     * @param nodeId The ID of a node. The node-id format: Node IDs always start with &#39;root&#39; and optionally followed by numbers separated by &#39;:&#39; referring to nested nodes/subworkflows,e.g. root:3:6:4. Nodes within components require an additional trailing &#39;0&#39;, e.g. &#39;root:3:6:0:4&#39; (if &#39;root:3:6&#39; is a component).
     *
     * @return the result
     * @throws ServiceExceptions.ServiceCallException If a Gateway service call failed for some reason.
     */
    String getConfigurationNodes(String projectId, org.knime.gateway.api.entity.NodeIDEnt workflowId, org.knime.gateway.api.entity.NodeIDEnt nodeId)  throws ServiceExceptions.ServiceCallException;

    /**
     * Gets view layout for the component editor.
     *
     * @param projectId ID of the workflow-project.
     * @param workflowId The ID of a workflow which has the same format as a node-id.
     * @param nodeId The ID of a node. The node-id format: Node IDs always start with &#39;root&#39; and optionally followed by numbers separated by &#39;:&#39; referring to nested nodes/subworkflows,e.g. root:3:6:4. Nodes within components require an additional trailing &#39;0&#39;, e.g. &#39;root:3:6:0:4&#39; (if &#39;root:3:6&#39; is a component).
     *
     * @return the result
     * @throws ServiceExceptions.ServiceCallException If a Gateway service call failed for some reason.
     */
    String getViewLayout(String projectId, org.knime.gateway.api.entity.NodeIDEnt workflowId, org.knime.gateway.api.entity.NodeIDEnt nodeId)  throws ServiceExceptions.ServiceCallException;

    /**
     * Gets view nodes for the component editor.
     *
     * @param projectId ID of the workflow-project.
     * @param workflowId The ID of a workflow which has the same format as a node-id.
     * @param nodeId The ID of a node. The node-id format: Node IDs always start with &#39;root&#39; and optionally followed by numbers separated by &#39;:&#39; referring to nested nodes/subworkflows,e.g. root:3:6:4. Nodes within components require an additional trailing &#39;0&#39;, e.g. &#39;root:3:6:0:4&#39; (if &#39;root:3:6&#39; is a component).
     *
     * @return the result
     * @throws ServiceExceptions.ServiceCallException If a Gateway service call failed for some reason.
     */
    String getViewNodes(String projectId, org.knime.gateway.api.entity.NodeIDEnt workflowId, org.knime.gateway.api.entity.NodeIDEnt nodeId)  throws ServiceExceptions.ServiceCallException;

    /**
     * Sets configuration layout for the component editor.
     *
     * @param projectId ID of the workflow-project.
     * @param workflowId The ID of a workflow which has the same format as a node-id.
     * @param nodeId The ID of a node. The node-id format: Node IDs always start with &#39;root&#39; and optionally followed by numbers separated by &#39;:&#39; referring to nested nodes/subworkflows,e.g. root:3:6:4. Nodes within components require an additional trailing &#39;0&#39;, e.g. &#39;root:3:6:0:4&#39; (if &#39;root:3:6&#39; is a component).
     * @param componentConfigurationLayout
     *
     *
     * @throws ServiceExceptions.ServiceCallException If a Gateway service call failed for some reason.
     */
    void setConfigurationLayout(String projectId, org.knime.gateway.api.entity.NodeIDEnt workflowId, org.knime.gateway.api.entity.NodeIDEnt nodeId, String componentConfigurationLayout)  throws ServiceExceptions.ServiceCallException;

    /**
     * Sets view layout for the component view editor.
     *
     * @param projectId ID of the workflow-project.
     * @param workflowId The ID of a workflow which has the same format as a node-id.
     * @param nodeId The ID of a node. The node-id format: Node IDs always start with &#39;root&#39; and optionally followed by numbers separated by &#39;:&#39; referring to nested nodes/subworkflows,e.g. root:3:6:4. Nodes within components require an additional trailing &#39;0&#39;, e.g. &#39;root:3:6:0:4&#39; (if &#39;root:3:6&#39; is a component).
     * @param componentViewLayout
     *
     *
     * @throws ServiceExceptions.ServiceCallException If a Gateway service call failed for some reason.
     */
    void setViewLayout(String projectId, org.knime.gateway.api.entity.NodeIDEnt workflowId, org.knime.gateway.api.entity.NodeIDEnt nodeId, String componentViewLayout)  throws ServiceExceptions.ServiceCallException;

}

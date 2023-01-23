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

import org.knime.gateway.api.webui.entity.SpaceItemEnt;
import org.knime.gateway.api.webui.entity.SpaceProviderEnt;
import org.knime.gateway.api.webui.entity.WorkflowGroupContentEnt;

/**
 * Operations on a single space (local workspace, hub space).
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
@javax.annotation.Generated(value = {"com.knime.gateway.codegen.GatewayCodegen", "src-gen/api/web-ui/configs/org.knime.gateway.api-config.json"})
public interface SpaceService extends GatewayService {

    /**
     * Create a new workflow within a given workflow group.
     *
     * @param spaceId The unique identifier of the space (local workspace, hub space). If &#39;local&#39; it refers to the local workspace.
     * @param spaceProviderId Identifies a space-provider.
     * @param workflowGroupId The unique identifier of the workflow group to get the contained space items for. If &#39;root&#39;, it refers to the root directory (workflow group).
     *
     * @return the result
     * @throws ServiceExceptions.IOException If there was an I/O error of some kind.
     * @throws ServiceExceptions.InvalidRequestException If the request is invalid for a reason.
     */
    SpaceItemEnt createWorkflow(String spaceId, String spaceProviderId, String workflowGroupId)  throws ServiceExceptions.IOException, ServiceExceptions.InvalidRequestException;
        
    /**
     * Deletes items from the space.
     *
     * @param spaceId The unique identifier of the space (local workspace, hub space). If &#39;local&#39; it refers to the local workspace.
     * @param spaceProviderId Identifies a space-provider.
     * @param spaceItemIds A list of identifiers of items in the space.
     *
     * 
     * @throws ServiceExceptions.IOException If there was an I/O error of some kind.
     * @throws ServiceExceptions.InvalidRequestException If the request is invalid for a reason.
     */
    void deleteItems(String spaceId, String spaceProviderId, java.util.List<String> spaceItemIds)  throws ServiceExceptions.IOException, ServiceExceptions.InvalidRequestException;
        
    /**
     * Mainly returns the spaces provided by this space-provider.
     *
     * @param spaceProviderId Identifies a space-provider.
     *
     * @return the result
     * @throws ServiceExceptions.InvalidRequestException If the request is invalid for a reason.
     */
    SpaceProviderEnt getSpaceProvider(String spaceProviderId)  throws ServiceExceptions.InvalidRequestException;
        
    /**
     * Get shallow list of workflows, components and data-files within a given workflow group.
     *
     * @param spaceId The unique identifier of the space (local workspace, hub space). If &#39;local&#39; it refers to the local workspace.
     * @param spaceProviderId Identifies a space-provider.
     * @param workflowGroupId The unique identifier of the workflow group to get the contained space items for. If &#39;root&#39;, it refers to the root directory (workflow group).
     *
     * @return the result
     * @throws ServiceExceptions.InvalidRequestException If the request is invalid for a reason.
     * @throws ServiceExceptions.IOException If there was an I/O error of some kind.
     */
    WorkflowGroupContentEnt listWorkflowGroup(String spaceId, String spaceProviderId, String workflowGroupId)  throws ServiceExceptions.InvalidRequestException, ServiceExceptions.IOException;
        
}

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

import org.knime.gateway.api.webui.entity.SpaceEnt;
import org.knime.gateway.api.webui.entity.SpaceItemEnt;
import org.knime.gateway.api.webui.entity.SpaceProviderEnt;
import org.knime.gateway.api.webui.entity.WorkflowGroupContentEnt;

/**
 * Operations on a single space (local workspace, hub space).
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
@jakarta.annotation.Generated(value = {"com.knime.gateway.codegen.GatewayCodegen", "src-gen/api/web-ui/configs/org.knime.gateway.api-config.json"})
public interface SpaceService extends GatewayService {

    /**
     * Create a new space within a given space provider.
     *
     * @param spaceProviderId Identifies a space-provider.
     * @param spaceGroupName Identifier name of a space-group.
     *
     * @return the result
     * @throws ServiceExceptions.IOException If there was an I/O error of some kind.
     */
    SpaceEnt createSpace(String spaceProviderId, String spaceGroupName)  throws ServiceExceptions.IOException;
        
    /**
     * Create a new workflow within a given workflow group.
     *
     * @param spaceId The unique identifier of the space (local workspace, hub space). If &#39;local&#39; it refers to the local workspace.
     * @param spaceProviderId Identifies a space-provider.
     * @param itemId The unique identifier of the space item. If &#39;root&#39;, it refers to the root directory (workflow group).
     * @param itemName Name given to a space item.
     *
     * @return the result
     * @throws ServiceExceptions.IOException If there was an I/O error of some kind.
     * @throws ServiceExceptions.InvalidRequestException If the request is invalid for a reason.
     */
    SpaceItemEnt createWorkflow(String spaceId, String spaceProviderId, String itemId, String itemName)  throws ServiceExceptions.IOException, ServiceExceptions.InvalidRequestException;
        
    /**
     * Create a new workflow group within a given workflow group.
     *
     * @param spaceId The unique identifier of the space (local workspace, hub space). If &#39;local&#39; it refers to the local workspace.
     * @param spaceProviderId Identifies a space-provider.
     * @param itemId The unique identifier of the space item. If &#39;root&#39;, it refers to the root directory (workflow group).
     *
     * @return the result
     * @throws ServiceExceptions.IOException If there was an I/O error of some kind.
     * @throws ServiceExceptions.InvalidRequestException If the request is invalid for a reason.
     */
    SpaceItemEnt createWorkflowGroup(String spaceId, String spaceProviderId, String itemId)  throws ServiceExceptions.IOException, ServiceExceptions.InvalidRequestException;
        
    /**
     * Deletes items from the space.
     *
     * @param spaceId The unique identifier of the space (local workspace, hub space). If &#39;local&#39; it refers to the local workspace.
     * @param spaceProviderId Identifies a space-provider.
     * @param itemIds A list of identifiers of items in the space.
     *
     * 
     * @throws ServiceExceptions.IOException If there was an I/O error of some kind.
     * @throws ServiceExceptions.InvalidRequestException If the request is invalid for a reason.
     */
    void deleteItems(String spaceId, String spaceProviderId, java.util.List<String> itemIds)  throws ServiceExceptions.IOException, ServiceExceptions.InvalidRequestException;
        
    /**
     * Deletes job from the space.
     *
     * @param spaceId The unique identifier of the space (local workspace, hub space). If &#39;local&#39; it refers to the local workspace.
     * @param spaceProviderId Identifies a space-provider.
     * @param itemId The unique identifier of the space item. If &#39;root&#39;, it refers to the root directory (workflow group).
     * @param jobId The ID of the job to delete
     *
     * 
     * @throws ServiceExceptions.IOException If there was an I/O error of some kind.
     * @throws ServiceExceptions.InvalidRequestException If the request is invalid for a reason.
     */
    void deleteJobsForWorkflow(String spaceId, String spaceProviderId, String itemId, String jobId)  throws ServiceExceptions.IOException, ServiceExceptions.InvalidRequestException;
        
    /**
     * Deletes schedule from the space.
     *
     * @param spaceId The unique identifier of the space (local workspace, hub space). If &#39;local&#39; it refers to the local workspace.
     * @param spaceProviderId Identifies a space-provider.
     * @param itemId The unique identifier of the space item. If &#39;root&#39;, it refers to the root directory (workflow group).
     * @param scheduleId The ID of the schedule to delete
     *
     * 
     * @throws ServiceExceptions.IOException If there was an I/O error of some kind.
     * @throws ServiceExceptions.InvalidRequestException If the request is invalid for a reason.
     */
    void deleteSchedulesForWorkflow(String spaceId, String spaceProviderId, String itemId, String scheduleId)  throws ServiceExceptions.IOException, ServiceExceptions.InvalidRequestException;
        
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
     * Lists the available jobs for the given workflow.
     *
     * @param spaceId The unique identifier of the space (local workspace, hub space). If &#39;local&#39; it refers to the local workspace.
     * @param spaceProviderId Identifies a space-provider.
     * @param itemId The unique identifier of the space item. If &#39;root&#39;, it refers to the root directory (workflow group).
     *
     * @return the result
     * @throws ServiceExceptions.InvalidRequestException If the request is invalid for a reason.
     */
    java.util.List<Object> listJobsForWorkflow(String spaceId, String spaceProviderId, String itemId)  throws ServiceExceptions.InvalidRequestException;
        
    /**
     * Lists the available schedules for the given workflow.
     *
     * @param spaceId The unique identifier of the space (local workspace, hub space). If &#39;local&#39; it refers to the local workspace.
     * @param spaceProviderId Identifies a space-provider.
     * @param itemId The unique identifier of the space item. If &#39;root&#39;, it refers to the root directory (workflow group).
     *
     * @return the result
     * @throws ServiceExceptions.InvalidRequestException If the request is invalid for a reason.
     */
    java.util.List<Object> listSchedulesForWorkflow(String spaceId, String spaceProviderId, String itemId)  throws ServiceExceptions.InvalidRequestException;
        
    /**
     * Get shallow list of workflows, components and data-files within a given workflow group.
     *
     * @param spaceId The unique identifier of the space (local workspace, hub space). If &#39;local&#39; it refers to the local workspace.
     * @param spaceProviderId Identifies a space-provider.
     * @param itemId The unique identifier of the space item. If &#39;root&#39;, it refers to the root directory (workflow group).
     *
     * @return the result
     * @throws ServiceExceptions.InvalidRequestException If the request is invalid for a reason.
     * @throws ServiceExceptions.IOException If there was an I/O error of some kind.
     */
    WorkflowGroupContentEnt listWorkflowGroup(String spaceId, String spaceProviderId, String itemId)  throws ServiceExceptions.InvalidRequestException, ServiceExceptions.IOException;
        
    /**
     * Move or copy space items to a different workflow group within its space.
     *
     * @param spaceId The unique identifier of the space (local workspace, hub space). If &#39;local&#39; it refers to the local workspace.
     * @param spaceProviderId Identifies a space-provider.
     * @param itemIds A list of identifiers of items in the space.
     * @param destWorkflowGroupItemId The destination workflow group item id, therefore the new parent.
     * @param collisionHandling How to solve potential name collisions.
     * @param copy Copy instead of move items.
     *
     * 
     * @throws ServiceExceptions.IOException If there was an I/O error of some kind.
     * @throws ServiceExceptions.InvalidRequestException If the request is invalid for a reason.
     */
    void moveOrCopyItems(String spaceId, String spaceProviderId, java.util.List<String> itemIds, String destWorkflowGroupItemId, String collisionHandling, Boolean copy)  throws ServiceExceptions.IOException, ServiceExceptions.InvalidRequestException;
        
    /**
     * Rename a space Item
     *
     * @param spaceProviderId Identifies a space-provider.
     * @param spaceId The unique identifier of the space (local workspace, hub space). If &#39;local&#39; it refers to the local workspace.
     * @param itemId The unique identifier of the space item. If &#39;root&#39;, it refers to the root directory (workflow group).
     * @param itemName Name given to a space item.
     *
     * @return the result
     * @throws ServiceExceptions.IOException If there was an I/O error of some kind.
     * @throws ServiceExceptions.InvalidRequestException If the request is invalid for a reason.
     * @throws ServiceExceptions.OperationNotAllowedException If the an operation is not allowed, e.g., because it&#39;s not applicable.
     */
    SpaceItemEnt renameItem(String spaceProviderId, String spaceId, String itemId, String itemName)  throws ServiceExceptions.IOException, ServiceExceptions.InvalidRequestException, ServiceExceptions.OperationNotAllowedException;
        
}

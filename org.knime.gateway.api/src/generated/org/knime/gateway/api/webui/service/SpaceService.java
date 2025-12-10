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

import org.knime.gateway.api.webui.entity.AncestorInfoEnt;
import org.knime.gateway.api.webui.entity.ComponentSearchItemEnt;
import org.knime.gateway.api.webui.entity.LinkVariantInfoEnt;
import org.knime.gateway.api.webui.entity.SpaceEnt;
import org.knime.gateway.api.webui.entity.SpaceGroupEnt;
import org.knime.gateway.api.webui.entity.SpaceItemEnt;
import org.knime.gateway.api.webui.entity.WorkflowGroupContentEnt;

/**
 * Operations on a single space (local, hub, ...).
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
     * @throws ServiceExceptions.ServiceCallException If a Gateway service call failed for some reason.
     * @throws ServiceExceptions.LoggedOutException If a web request could not be authorized because the space provider isn&#39;t logged in
     * @throws ServiceExceptions.NetworkException If a Gateway service call failed due to a network error.
     * @throws ServiceExceptions.OperationNotAllowedException If the an operation is not allowed, e.g., because it&#39;s not applicable.
     */
    SpaceEnt createSpace(String spaceProviderId, String spaceGroupName)  throws ServiceExceptions.ServiceCallException, ServiceExceptions.LoggedOutException, ServiceExceptions.NetworkException, ServiceExceptions.OperationNotAllowedException;
        
    /**
     * Create a new workflow within a given workflow group.
     *
     * @param spaceId The unique identifier of the space (local workspace, hub space). If &#39;local&#39; it refers to the local workspace.
     * @param spaceProviderId Identifies a space-provider.
     * @param itemId The unique identifier of the space item. If &#39;root&#39;, it refers to the root directory (workflow group).
     * @param itemName Name given to a space item.
     *
     * @return the result
     * @throws ServiceExceptions.OperationNotAllowedException If the an operation is not allowed, e.g., because it&#39;s not applicable.
     * @throws ServiceExceptions.ServiceCallException If a Gateway service call failed for some reason.
     * @throws ServiceExceptions.LoggedOutException If a web request could not be authorized because the space provider isn&#39;t logged in
     * @throws ServiceExceptions.NetworkException If a Gateway service call failed due to a network error.
     */
    SpaceItemEnt createWorkflow(String spaceId, String spaceProviderId, String itemId, String itemName)  throws ServiceExceptions.OperationNotAllowedException, ServiceExceptions.ServiceCallException, ServiceExceptions.LoggedOutException, ServiceExceptions.NetworkException;
        
    /**
     * Create a new workflow group within a given workflow group.
     *
     * @param spaceId The unique identifier of the space (local workspace, hub space). If &#39;local&#39; it refers to the local workspace.
     * @param spaceProviderId Identifies a space-provider.
     * @param itemId The unique identifier of the space item. If &#39;root&#39;, it refers to the root directory (workflow group).
     *
     * @return the result
     * @throws ServiceExceptions.OperationNotAllowedException If the an operation is not allowed, e.g., because it&#39;s not applicable.
     * @throws ServiceExceptions.ServiceCallException If a Gateway service call failed for some reason.
     * @throws ServiceExceptions.LoggedOutException If a web request could not be authorized because the space provider isn&#39;t logged in
     * @throws ServiceExceptions.NetworkException If a Gateway service call failed due to a network error.
     */
    SpaceItemEnt createWorkflowGroup(String spaceId, String spaceProviderId, String itemId)  throws ServiceExceptions.OperationNotAllowedException, ServiceExceptions.ServiceCallException, ServiceExceptions.LoggedOutException, ServiceExceptions.NetworkException;
        
    /**
     * Deletes items from the space.
     *
     * @param spaceId The unique identifier of the space (local workspace, hub space). If &#39;local&#39; it refers to the local workspace.
     * @param spaceProviderId Identifies a space-provider.
     * @param itemIds A list of identifiers of items in the space.
     * @param softDelete If true the specified items will be moved to the bin. Otherwise they will be permanently deleted.
     *
     * 
     * @throws ServiceExceptions.OperationNotAllowedException If the an operation is not allowed, e.g., because it&#39;s not applicable.
     * @throws ServiceExceptions.ServiceCallException If a Gateway service call failed for some reason.
     * @throws ServiceExceptions.LoggedOutException If a web request could not be authorized because the space provider isn&#39;t logged in
     * @throws ServiceExceptions.NetworkException If a Gateway service call failed due to a network error.
     */
    void deleteItems(String spaceId, String spaceProviderId, java.util.List<String> itemIds, Boolean softDelete)  throws ServiceExceptions.OperationNotAllowedException, ServiceExceptions.ServiceCallException, ServiceExceptions.LoggedOutException, ServiceExceptions.NetworkException;
        
    /**
     * Deletes job from the space.
     *
     * @param spaceId The unique identifier of the space (local workspace, hub space). If &#39;local&#39; it refers to the local workspace.
     * @param spaceProviderId Identifies a space-provider.
     * @param itemId The unique identifier of the space item. If &#39;root&#39;, it refers to the root directory (workflow group).
     * @param jobId The ID of the job to delete
     *
     * 
     * @throws ServiceExceptions.ServiceCallException If a Gateway service call failed for some reason.
     * @throws ServiceExceptions.LoggedOutException If a web request could not be authorized because the space provider isn&#39;t logged in
     * @throws ServiceExceptions.NetworkException If a Gateway service call failed due to a network error.
     */
    void deleteJobsForWorkflow(String spaceId, String spaceProviderId, String itemId, String jobId)  throws ServiceExceptions.ServiceCallException, ServiceExceptions.LoggedOutException, ServiceExceptions.NetworkException;
        
    /**
     * Deletes schedule from the space.
     *
     * @param spaceId The unique identifier of the space (local workspace, hub space). If &#39;local&#39; it refers to the local workspace.
     * @param spaceProviderId Identifies a space-provider.
     * @param itemId The unique identifier of the space item. If &#39;root&#39;, it refers to the root directory (workflow group).
     * @param scheduleId The ID of the schedule to delete
     *
     * 
     * @throws ServiceExceptions.ServiceCallException If a Gateway service call failed for some reason.
     * @throws ServiceExceptions.LoggedOutException If a web request could not be authorized because the space provider isn&#39;t logged in
     * @throws ServiceExceptions.NetworkException If a Gateway service call failed due to a network error.
     */
    void deleteSchedulesForWorkflow(String spaceId, String spaceProviderId, String itemId, String scheduleId)  throws ServiceExceptions.ServiceCallException, ServiceExceptions.LoggedOutException, ServiceExceptions.NetworkException;
        
    /**
     * Get ancestor information for a given space item.
     *
     * @param spaceProviderId Identifies a space-provider.
     * @param spaceId The unique identifier of the space (local workspace, hub space). If &#39;local&#39; it refers to the local workspace.
     * @param itemId The unique identifier of the space item. If &#39;root&#39;, it refers to the root directory (workflow group).
     *
     * @return the result
     * @throws ServiceExceptions.ServiceCallException If a Gateway service call failed for some reason.
     * @throws ServiceExceptions.LoggedOutException If a web request could not be authorized because the space provider isn&#39;t logged in
     * @throws ServiceExceptions.NetworkException If a Gateway service call failed due to a network error.
     */
    AncestorInfoEnt getAncestorInfo(String spaceProviderId, String spaceId, String itemId)  throws ServiceExceptions.ServiceCallException, ServiceExceptions.LoggedOutException, ServiceExceptions.NetworkException;
        
    /**
     * Returns the available link variants for the destination represented by the given provider and path.
     *
     * @param projectId ID of the workflow-project.
     * @param spaceId The unique identifier of the space (local workspace, hub space). If &#39;local&#39; it refers to the local workspace.
     * @param spaceProviderId Identifies a space-provider.
     * @param itemId The unique identifier of the space item. If &#39;root&#39;, it refers to the root directory (workflow group).
     *
     * @return the result
     * @throws ServiceExceptions.ServiceCallException If a Gateway service call failed for some reason.
     * @throws ServiceExceptions.LoggedOutException If a web request could not be authorized because the space provider isn&#39;t logged in
     * @throws ServiceExceptions.NetworkException If a Gateway service call failed due to a network error.
     */
    java.util.List<LinkVariantInfoEnt> getLinkVariantsForItem(String projectId, String spaceId, String spaceProviderId, String itemId)  throws ServiceExceptions.ServiceCallException, ServiceExceptions.LoggedOutException, ServiceExceptions.NetworkException;
        
    /**
     * Returns the spaces provided by this space-provider.
     *
     * @param spaceProviderId Identifies a space-provider.
     *
     * @return the result
     * @throws ServiceExceptions.ServiceCallException If a Gateway service call failed for some reason.
     * @throws ServiceExceptions.LoggedOutException If a web request could not be authorized because the space provider isn&#39;t logged in
     * @throws ServiceExceptions.NetworkException If a Gateway service call failed due to a network error.
     */
    java.util.List<SpaceGroupEnt> getSpaceGroups(String spaceProviderId)  throws ServiceExceptions.ServiceCallException, ServiceExceptions.LoggedOutException, ServiceExceptions.NetworkException;
        
    /**
     * Lists the available jobs for the given workflow.
     *
     * @param spaceId The unique identifier of the space (local workspace, hub space). If &#39;local&#39; it refers to the local workspace.
     * @param spaceProviderId Identifies a space-provider.
     * @param itemId The unique identifier of the space item. If &#39;root&#39;, it refers to the root directory (workflow group).
     *
     * @return the result
     * @throws ServiceExceptions.ServiceCallException If a Gateway service call failed for some reason.
     * @throws ServiceExceptions.LoggedOutException If a web request could not be authorized because the space provider isn&#39;t logged in
     * @throws ServiceExceptions.NetworkException If a Gateway service call failed due to a network error.
     */
    java.util.List<Object> listJobsForWorkflow(String spaceId, String spaceProviderId, String itemId)  throws ServiceExceptions.ServiceCallException, ServiceExceptions.LoggedOutException, ServiceExceptions.NetworkException;
        
    /**
     * Lists the available schedules for the given workflow.
     *
     * @param spaceId The unique identifier of the space (local workspace, hub space). If &#39;local&#39; it refers to the local workspace.
     * @param spaceProviderId Identifies a space-provider.
     * @param itemId The unique identifier of the space item. If &#39;root&#39;, it refers to the root directory (workflow group).
     *
     * @return the result
     * @throws ServiceExceptions.ServiceCallException If a Gateway service call failed for some reason.
     * @throws ServiceExceptions.LoggedOutException If a web request could not be authorized because the space provider isn&#39;t logged in
     * @throws ServiceExceptions.NetworkException If a Gateway service call failed due to a network error.
     */
    java.util.List<Object> listSchedulesForWorkflow(String spaceId, String spaceProviderId, String itemId)  throws ServiceExceptions.ServiceCallException, ServiceExceptions.LoggedOutException, ServiceExceptions.NetworkException;
        
    /**
     * Get shallow list of workflows, components and data-files within a given workflow group.
     *
     * @param spaceId The unique identifier of the space (local workspace, hub space). If &#39;local&#39; it refers to the local workspace.
     * @param spaceProviderId Identifies a space-provider.
     * @param itemId The unique identifier of the space item. If &#39;root&#39;, it refers to the root directory (workflow group).
     *
     * @return the result
     * @throws ServiceExceptions.ServiceCallException If a Gateway service call failed for some reason.
     * @throws ServiceExceptions.LoggedOutException If a web request could not be authorized because the space provider isn&#39;t logged in
     * @throws ServiceExceptions.NetworkException If a Gateway service call failed due to a network error.
     */
    WorkflowGroupContentEnt listWorkflowGroup(String spaceId, String spaceProviderId, String itemId)  throws ServiceExceptions.ServiceCallException, ServiceExceptions.LoggedOutException, ServiceExceptions.NetworkException;
        
    /**
     * Move or copy space items to a different workflow group within its space.
     *
     * @param spaceId The unique identifier of the space (local workspace, hub space). If &#39;local&#39; it refers to the local workspace.
     * @param spaceProviderId Identifies a space-provider.
     * @param itemIds A list of identifiers of items in the space.
     * @param destSpaceId The destination space id.
     * @param destWorkflowGroupItemId The destination workflow group item id, therefore the new parent.
     * @param copy Copy instead of move items.
     * @param collisionHandling How to solve potential name collisions.
     *
     * 
     * @throws ServiceExceptions.ServiceCallException If a Gateway service call failed for some reason.
     * @throws ServiceExceptions.CollisionException If there was a collision, e.g. due to naming conflicts
     * @throws ServiceExceptions.LoggedOutException If a web request could not be authorized because the space provider isn&#39;t logged in
     * @throws ServiceExceptions.NetworkException If a Gateway service call failed due to a network error.
     * @throws ServiceExceptions.OperationNotAllowedException If the an operation is not allowed, e.g., because it&#39;s not applicable.
     */
    void moveOrCopyItems(String spaceId, String spaceProviderId, java.util.List<String> itemIds, String destSpaceId, String destWorkflowGroupItemId, Boolean copy, String collisionHandling)  throws ServiceExceptions.ServiceCallException, ServiceExceptions.CollisionException, ServiceExceptions.LoggedOutException, ServiceExceptions.NetworkException, ServiceExceptions.OperationNotAllowedException;
        
    /**
     * Rename a space Item
     *
     * @param spaceProviderId Identifies a space-provider.
     * @param spaceId The unique identifier of the space (local workspace, hub space). If &#39;local&#39; it refers to the local workspace.
     * @param itemId The unique identifier of the space item. If &#39;root&#39;, it refers to the root directory (workflow group).
     * @param itemName Name given to a space item.
     *
     * @return the result
     * @throws ServiceExceptions.ServiceCallException If a Gateway service call failed for some reason.
     * @throws ServiceExceptions.LoggedOutException If a web request could not be authorized because the space provider isn&#39;t logged in
     * @throws ServiceExceptions.NetworkException If a Gateway service call failed due to a network error.
     * @throws ServiceExceptions.OperationNotAllowedException If the an operation is not allowed, e.g., because it&#39;s not applicable.
     */
    SpaceItemEnt renameItem(String spaceProviderId, String spaceId, String itemId, String itemName)  throws ServiceExceptions.ServiceCallException, ServiceExceptions.LoggedOutException, ServiceExceptions.NetworkException, ServiceExceptions.OperationNotAllowedException;
        
    /**
     * Rename a space
     *
     * @param spaceProviderId Identifies a space-provider.
     * @param spaceId The unique identifier of the space (local workspace, hub space). If &#39;local&#39; it refers to the local workspace.
     * @param spaceName Name given to a space.
     *
     * @return the result
     * @throws ServiceExceptions.ServiceCallException If a Gateway service call failed for some reason.
     * @throws ServiceExceptions.LoggedOutException If a web request could not be authorized because the space provider isn&#39;t logged in
     * @throws ServiceExceptions.NetworkException If a Gateway service call failed due to a network error.
     * @throws ServiceExceptions.OperationNotAllowedException If the an operation is not allowed, e.g., because it&#39;s not applicable.
     */
    SpaceEnt renameSpace(String spaceProviderId, String spaceId, String spaceName)  throws ServiceExceptions.ServiceCallException, ServiceExceptions.LoggedOutException, ServiceExceptions.NetworkException, ServiceExceptions.OperationNotAllowedException;
        
    /**
     * Search among components in this provider
     *
     * @param query Search term used to filter components by name, tags, or description.
     * @param limit Controls the maximum number of search results returned.
     * @param offset Controls the starting position for pagination of search results.
     *
     * @return the result
     * @throws ServiceExceptions.ServiceCallException If a Gateway service call failed for some reason.
     * @throws ServiceExceptions.LoggedOutException If a web request could not be authorized because the space provider isn&#39;t logged in
     * @throws ServiceExceptions.NetworkException If a Gateway service call failed due to a network error.
     */
    java.util.List<ComponentSearchItemEnt> searchComponents(String query, Integer limit, Integer offset)  throws ServiceExceptions.ServiceCallException, ServiceExceptions.LoggedOutException, ServiceExceptions.NetworkException;
        
}

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

import org.knime.gateway.api.entity.ConnectionEnt;
import org.knime.gateway.api.entity.PatchEnt;
import org.knime.gateway.api.entity.WorkflowPartsEnt;
import org.knime.gateway.api.entity.WorkflowSnapshotEnt;

/**
 * Operations on workflows.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
@javax.annotation.Generated(value = {"com.knime.gateway.codegen.GatewayCodegen", "src-gen/org.knime.gateway.api-config.json"})
public interface WorkflowService extends GatewayService {

    /**
     * Creates a new connection between two nodes. Note: replaces/removes existing connections if destination port is already in use.
     *
     * @param jobId ID of the job the workflow is requested for.
     * @param connectionEnt The connection.
     *
     * @return the result
     * @throws ServiceExceptions.ActionNotAllowedException If the an action is not allowed because it&#39;s not applicable or it doesn&#39;t exist.
     */
    org.knime.gateway.api.entity.ConnectionIDEnt createConnection(java.util.UUID jobId, ConnectionEnt connectionEnt)  throws ServiceExceptions.ActionNotAllowedException;
        
    /**
     * Selects and essentially copies the specified part of the workflow. It will still be available even if (sub)parts are deleted. The parts are referenced by a part-id.  Note: connections will be ignored and _not_ copied!
     *
     * @param jobId ID of the job the workflow is requested for.
     * @param workflowId The ID of a worklow which has the same format as a node-id.
     * @param workflowPartsEnt The actual part selection.
     *
     * @return the result
     * @throws ServiceExceptions.NotASubWorkflowException The requested node is not a sub-workflow (i.e. a meta- or sub-node), but is required to be.
     * @throws ServiceExceptions.NodeNotFoundException The requested node was not found.
     * @throws ServiceExceptions.InvalidRequestException If the request is invalid for a reason.
     */
    java.util.UUID createWorkflowCopy(java.util.UUID jobId, org.knime.gateway.api.entity.NodeIDEnt workflowId, WorkflowPartsEnt workflowPartsEnt)  throws ServiceExceptions.NotASubWorkflowException, ServiceExceptions.NodeNotFoundException, ServiceExceptions.InvalidRequestException;
        
    /**
     * Deletes the given workflow parts. Cannot be undone unless a copy has been made before.
     *
     * @param jobId ID of the job the workflow is requested for.
     * @param workflowId The ID of a worklow which has the same format as a node-id.
     * @param workflowPartsEnt The parts to be deleted.
     * @param copy If a copy should be created before removal. False by default. Please note that the copy will _only_ include the connections that are entirely enclosed by the parts to be removed (i.e. connections that are connecting two removed nodes - all others won&#39;t be kept)
     *
     * @return the result
     * @throws ServiceExceptions.NotASubWorkflowException The requested node is not a sub-workflow (i.e. a meta- or sub-node), but is required to be.
     * @throws ServiceExceptions.NodeNotFoundException The requested node was not found.
     * @throws ServiceExceptions.ActionNotAllowedException If the an action is not allowed because it&#39;s not applicable or it doesn&#39;t exist.
     */
    java.util.UUID deleteWorkflowParts(java.util.UUID jobId, org.knime.gateway.api.entity.NodeIDEnt workflowId, WorkflowPartsEnt workflowPartsEnt, Boolean copy)  throws ServiceExceptions.NotASubWorkflowException, ServiceExceptions.NodeNotFoundException, ServiceExceptions.ActionNotAllowedException;
        
    /**
     * Retrieves the complete structure (nodes, connections, annotations) of (sub-)workflows.
     *
     * @param jobId ID of the job the workflow is requested for.
     * @param workflowId The ID of a worklow which has the same format as a node-id.
     *
     * @return the result
     * @throws ServiceExceptions.NotASubWorkflowException The requested node is not a sub-workflow (i.e. a meta- or sub-node), but is required to be.
     * @throws ServiceExceptions.NodeNotFoundException The requested node was not found.
     */
    WorkflowSnapshotEnt getWorkflow(java.util.UUID jobId, org.knime.gateway.api.entity.NodeIDEnt workflowId)  throws ServiceExceptions.NotASubWorkflowException, ServiceExceptions.NodeNotFoundException;
        
    /**
     * Gives the changes of the sub-workflow as a patch.
     *
     * @param jobId ID of the job the workflow is requested for.
     * @param workflowId The ID of a worklow which has the same format as a node-id.
     * @param snapshotId The id of the workflow snapshot already retrieved.
     *
     * @return the result
     * @throws ServiceExceptions.NotASubWorkflowException The requested node is not a sub-workflow (i.e. a meta- or sub-node), but is required to be.
     * @throws ServiceExceptions.NotFoundException A resource couldn&#39;t be found.
     */
    PatchEnt getWorkflowDiff(java.util.UUID jobId, org.knime.gateway.api.entity.NodeIDEnt workflowId, java.util.UUID snapshotId)  throws ServiceExceptions.NotASubWorkflowException, ServiceExceptions.NotFoundException;
        
    /**
     * Pastes the referenced parts into the referenced (sub-)workflow.
     *
     * @param jobId ID of the job the workflow is requested for.
     * @param workflowId The ID of a worklow which has the same format as a node-id.
     * @param partsId The id referencing the parts to paste.
     * @param x The x position to paste the parts.
     * @param y The y position to paste the parts.
     *
     * @return the result
     * @throws ServiceExceptions.NotASubWorkflowException The requested node is not a sub-workflow (i.e. a meta- or sub-node), but is required to be.
     * @throws ServiceExceptions.NotFoundException A resource couldn&#39;t be found.
     */
    WorkflowPartsEnt pasteWorkflowParts(java.util.UUID jobId, org.knime.gateway.api.entity.NodeIDEnt workflowId, java.util.UUID partsId, Integer x, Integer y)  throws ServiceExceptions.NotASubWorkflowException, ServiceExceptions.NotFoundException;
        
}

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

import com.knime.gateway.v0.entity.ConnectionEnt;
import com.knime.gateway.v0.entity.PatchEnt;
import com.knime.gateway.v0.entity.WorkflowPartsEnt;
import com.knime.gateway.v0.entity.WorkflowSnapshotEnt;

/**
 * Operations on workflows.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
@javax.annotation.Generated(value = "org.knime.gateway.codegen.GatewayCodegen")
public interface WorkflowService extends GatewayService {

    /**
     * Creates a new connection between two nodes. Note: replaces/removes existing connections if destination port is already in use.
     *
     * @param jobId ID the job the workflow is requested for
     * @param connectionEnt The connection.
     *
     * @return the result
     * @throws ServiceExceptions.ActionNotAllowedException If an action is not allowed because it&#39;s not applicable or it doesn&#39;t exist. Please refer to the exception message for more details.
     */
    String createConnection(java.util.UUID jobId, ConnectionEnt connectionEnt)  throws ServiceExceptions.ActionNotAllowedException;
        
    /**
     * Selects and essentially copies the specified part of the workflow. It will still be available even if (sub)parts are deleted. The parts are referenced by a part-id.  Note: connections will be ignored and _not_ copied!
     *
     * @param jobId ID the job the workflow is requested for
     * @param workflowPartsEnt The actual part selection.
     *
     * @return the result
     * @throws ServiceExceptions.NotASubWorkflowException The requested node is not a sub-workflow (i.e. a meta- or sub-node), but is required to be.
     * @throws ServiceExceptions.NodeNotFoundException The requested node was not found.
     * @throws ServiceExceptions.InvalidRequestException If the request is invalid for a reason. Please refer to the exception message for more details.
     */
    java.util.UUID createWorkflowCopy(java.util.UUID jobId, WorkflowPartsEnt workflowPartsEnt)  throws ServiceExceptions.NotASubWorkflowException, ServiceExceptions.NodeNotFoundException, ServiceExceptions.InvalidRequestException;
        
    /**
     * Deletes the given workflow parts. Cannot be undone unless a copy has been made before.
     *
     * @param jobId ID the job the workflow is requested for
     * @param workflowPartsEnt The parts to be deleted.
     * @param copy If a copy should be created before removal. False by default. Please note that the copy will _only_ include the connections that are entirely enclosed by the parts to be removed (i.e. connections that are connecting two removed nodes - all others won&#39;t be kept)
     *
     * @return the result
     * @throws ServiceExceptions.NotASubWorkflowException The requested node is not a sub-workflow (i.e. a meta- or sub-node), but is required to be.
     * @throws ServiceExceptions.NodeNotFoundException The requested node was not found.
     * @throws ServiceExceptions.ActionNotAllowedException If an action is not allowed because it&#39;s not applicable or it doesn&#39;t exist. Please refer to the exception message for more details.
     */
    java.util.UUID deleteWorkflowParts(java.util.UUID jobId, WorkflowPartsEnt workflowPartsEnt, Boolean copy)  throws ServiceExceptions.NotASubWorkflowException, ServiceExceptions.NodeNotFoundException, ServiceExceptions.ActionNotAllowedException;
        
    /**
     * Retrieves the complete structure (nodes, connections, annotations) of sub-workflows.
     *
     * @param jobId ID the job the workflow is requested for
     * @param nodeId The ID of a node. The node-id format: For nested nodes the node ids are concatenated with an &#39;:&#39;, e.g. 3:6:4. Nodes within wrapped metanodes require an additional trailing &#39;0&#39;, e.g. 3:6:0:4 (if 3:6 is a wrapped metanode).
     *
     * @return the result
     * @throws ServiceExceptions.NotASubWorkflowException The requested node is not a sub-workflow (i.e. a meta- or sub-node), but is required to be.
     * @throws ServiceExceptions.NodeNotFoundException The requested node was not found.
     */
    WorkflowSnapshotEnt getSubWorkflow(java.util.UUID jobId, String nodeId)  throws ServiceExceptions.NotASubWorkflowException, ServiceExceptions.NodeNotFoundException;
        
    /**
     * Gives the changes of the sub-workflow as a patch.
     *
     * @param jobId ID the job the workflow is requested for
     * @param nodeId The ID of a node. The node-id format: For nested nodes the node ids are concatenated with an &#39;:&#39;, e.g. 3:6:4. Nodes within wrapped metanodes require an additional trailing &#39;0&#39;, e.g. 3:6:0:4 (if 3:6 is a wrapped metanode).
     * @param snapshotId The id of the workflow snapshot already retrieved.
     *
     * @return the result
     * @throws ServiceExceptions.NotASubWorkflowException The requested node is not a sub-workflow (i.e. a meta- or sub-node), but is required to be.
     * @throws ServiceExceptions.NotFoundException A resource couldn&#39;t be found. Please refer to the exception message for more details.
     */
    PatchEnt getSubWorkflowDiff(java.util.UUID jobId, String nodeId, java.util.UUID snapshotId)  throws ServiceExceptions.NotASubWorkflowException, ServiceExceptions.NotFoundException;
        
    /**
     * Retrieves the complete structure (nodes, connections, annotations) of the workflow
     *
     * @param jobId ID of the job the workflow is requested for.
     *
     * @return the result
     */
    WorkflowSnapshotEnt getWorkflow(java.util.UUID jobId) ;
        
    /**
     * Gives the changes of the workflow as patch. Please note that there is not always a snapshot available for the provided snapshot id, either because the snapshot never existed or has expired.
     *
     * @param jobId ID of the job the workflow is requested for.
     * @param snapshotId The id of the workflow snapshot already retrieved.
     *
     * @return the result
     * @throws ServiceExceptions.NotFoundException A resource couldn&#39;t be found. Please refer to the exception message for more details.
     */
    PatchEnt getWorkflowDiff(java.util.UUID jobId, java.util.UUID snapshotId)  throws ServiceExceptions.NotFoundException;
        
    /**
     * Pastes the referenced parts into the referenced (sub-)workflow.
     *
     * @param jobId ID of the job the workflow is requested for.
     * @param partsId The id referencing the parts to paste.
     * @param x The x position to paste the parts.
     * @param y The y position to paste the parts.
     * @param nodeId The ID of the node referencing a sub-workflow to paste the parts into. If none is given it will be pasted into the root workflow. For nested sub-workflows the node id&#39;s are concatenated with an &#39;:&#39; (e.g. 6:4:3).
     *
     * @return the result
     * @throws ServiceExceptions.NotASubWorkflowException The requested node is not a sub-workflow (i.e. a meta- or sub-node), but is required to be.
     * @throws ServiceExceptions.NotFoundException A resource couldn&#39;t be found. Please refer to the exception message for more details.
     */
    WorkflowPartsEnt pasteWorkflowParts(java.util.UUID jobId, java.util.UUID partsId, Integer x, Integer y, String nodeId)  throws ServiceExceptions.NotASubWorkflowException, ServiceExceptions.NotFoundException;
        
}

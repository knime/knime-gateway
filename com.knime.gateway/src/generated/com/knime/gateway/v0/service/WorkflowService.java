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

import com.knime.gateway.v0.entity.PatchEnt;
import com.knime.gateway.v0.entity.WorkflowSnapshotEnt;

/**
 * Operations on workflows.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
@javax.annotation.Generated(value = "org.knime.gateway.codegen.GatewayCodegen")
public interface WorkflowService extends GatewayService {

    /**
     * Retrieves the complete structure (nodes, connections, annotations) of sub-workflows.
     *
     * @param jobId ID the job the workflow is requested for
     * @param nodeId The ID of the node this sub-workflow is requested for. For nested sub-workflows the node id&#39;s are concatenated with an &#39;:&#39; (e.g. 6:4:3).
     *
     * @return the result
     * @throws ServiceExceptions.NotASubWorkflowException The requested node is not a sub-workflow (i.e. a meta- or sub-node), but is required to be.
     * @throws ServiceExceptions.NodeNotFoundException The requested node was not found.
     */
    WorkflowSnapshotEnt getSubWorkflow(java.util.UUID jobId, String nodeId)  throws ServiceExceptions.NotASubWorkflowException, ServiceExceptions.NodeNotFoundException;
        
    /**
     * Gives the changes of the sub-workflow as patch.
     *
     * @param jobId ID the job the workflow is requested for
     * @param nodeId The ID of the node this sub-workflow is requested for. For nested sub-workflows the node id&#39;s are concatenated with an &#39;:&#39; (e.g. 6:4:3).
     * @param snapshotId The id of the workflow-snapshot already retrieved.
     *
     * @return the result
     * @throws ServiceExceptions.NotASubWorkflowException The requested node is not a sub-workflow (i.e. a meta- or sub-node), but is required to be.
     * @throws ServiceExceptions.NotFoundException A resource couldn&#39;t be found. Please refer to the exception message for more details.
     */
    PatchEnt getSubWorkflowDiff(java.util.UUID jobId, String nodeId, java.util.UUID snapshotId)  throws ServiceExceptions.NotASubWorkflowException, ServiceExceptions.NotFoundException;
        
    /**
     * Retrieves the complete structure (nodes, connections, annotations) of the workflow
     *
     * @param jobId ID the job the workflow is requested for
     *
     * @return the result
     */
    WorkflowSnapshotEnt getWorkflow(java.util.UUID jobId) ;
        
    /**
     * Gives the changes of the workflow as patch. Please note that there is not always a snapshot available for the provided snapshot id, either because the snapshot never existed or has been expired
     *
     * @param jobId ID the job the workflow is requested for
     * @param snapshotId The id of the workflow-snapshot already retrieved.
     *
     * @return the result
     * @throws ServiceExceptions.NotFoundException A resource couldn&#39;t be found. Please refer to the exception message for more details.
     */
    PatchEnt getWorkflowDiff(java.util.UUID jobId, java.util.UUID snapshotId)  throws ServiceExceptions.NotFoundException;
        
}

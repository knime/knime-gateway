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

import com.knime.gateway.v0.entity.WorkflowEnt;

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
    WorkflowEnt getSubWorkflow(java.util.UUID jobId, String nodeId)  throws ServiceExceptions.NotASubWorkflowException, ServiceExceptions.NodeNotFoundException;
        
    /**
     * Retrieves the complete structure (nodes, connections, annotations) of the workflow
     *
     * @param jobId ID the job the workflow is requested for
     *
     * @return the result
     */
    WorkflowEnt getWorkflow(java.util.UUID jobId) ;
        
}

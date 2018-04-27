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

import com.knime.gateway.v0.entity.NodeEnt;

/**
 * Operations on single nodes.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
@javax.annotation.Generated(value = "org.knime.gateway.codegen.GatewayCodegen")
public interface NodeService extends GatewayService {

    /**
     * Changes the node&#39;s state for the given node-id. The following actions on the node state will be performed (if &lt;state&gt; -&gt; then &lt;action&gt;): executing -&gt; cancel; configured/idle -&gt; execute; executed -&gt; reset. If state cannot be changed (e.g. because of blocking successors), a &#39;not-allowed&#39; status/exception will be returned.
     *
     * @param jobId ID the job the workflow is requested for
     * @param nodeId The ID of the node the information is requested for. For nested nodes the node ids are concatenated with an &#39;:&#39;, e.g. 3:6:4
     *
     * @return the result
     * @throws ServiceExceptions.NodeNotFoundException The requested node was not found.
     * @throws ServiceExceptions.NotAllowedException A operation is not allowed to be performed. Please refer ot the exception message for more details.
     */
    String changeAndGetNodeState(java.util.UUID jobId, String nodeId)  throws ServiceExceptions.NodeNotFoundException, ServiceExceptions.NotAllowedException;
        
    /**
     * Retrieves the node for the given node-id.
     *
     * @param jobId ID the job the workflow is requested for
     * @param nodeId The ID of the node the information is requested for.
     *
     * @return the result
     * @throws ServiceExceptions.NodeNotFoundException The requested node was not found.
     */
    NodeEnt getNode(java.util.UUID jobId, String nodeId)  throws ServiceExceptions.NodeNotFoundException;
        
    /**
     * Retrieves the node&#39;s settings for the given node-id.
     *
     * @param jobId ID the job the workflow is requested for
     * @param nodeId The ID of the node the information is requested for. For nested nodes the node ids are concatenated with an &#39;:&#39;, e.g. 3:6:4
     *
     * @return the result
     * @throws ServiceExceptions.NodeNotFoundException The requested node was not found.
     */
    String getNodeSettings(java.util.UUID jobId, String nodeId)  throws ServiceExceptions.NodeNotFoundException;
        
    /**
     * Retrieves the root node referencing the workflow
     *
     * @param jobId ID the job the workflow is requested for
     *
     * @return the result
     */
    NodeEnt getRootNode(java.util.UUID jobId) ;
        
}

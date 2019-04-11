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
package com.knime.gateway.service;

import com.knime.gateway.service.GatewayService;
import com.knime.gateway.service.util.ServiceExceptions;

import com.knime.gateway.entity.BoundsEnt;

/**
 * Operations on single workflow annotations.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
@javax.annotation.Generated(value = {"org.knime.gateway.codegen.GatewayCodegen", "src-gen/com.knime.gateway-interfaces-config.json"})
public interface AnnotationService extends GatewayService {

    /**
     * Changes the bounds (x,y,width,height) of a workflow annotation in a sub-workflow.
     *
     * @param jobId ID of the job the workflow is requested for.
     * @param annoId 
     * @param boundsEnt 
     *
     * 
     * @throws ServiceExceptions.NotASubWorkflowException The requested node is not a sub-workflow (i.e. a meta- or sub-node), but is required to be.
     * @throws ServiceExceptions.NotFoundException A resource couldn&#39;t be found.
     */
    void setAnnotationBounds(java.util.UUID jobId, com.knime.gateway.entity.AnnotationIDEnt annoId, BoundsEnt boundsEnt)  throws ServiceExceptions.NotASubWorkflowException, ServiceExceptions.NotFoundException;
        
}

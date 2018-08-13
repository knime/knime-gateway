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
package com.knime.gateway.jsonrpc.local.service;

import com.knime.gateway.v0.entity.BoundsEnt;

import com.knime.gateway.v0.service.util.ServiceExceptions;

import com.googlecode.jsonrpc4j.JsonRpcMethod;

import com.knime.gateway.v0.service.AnnotationService;

/**
 * Interface that adds json rpc annotations. 
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
@javax.annotation.Generated(value = "org.knime.gateway.codegen.GatewayCodegen")
public interface JsonRpcAnnotationService extends AnnotationService {

	/**
     * {@inheritDoc}
     */
    @Override
    @JsonRpcMethod(value = "AnnotationService.setAnnotationBounds")
    void setAnnotationBounds(java.util.UUID jobId, String annoId, BoundsEnt bounds)  throws ServiceExceptions.NotFoundException;

	/**
     * {@inheritDoc}
     */
    @Override
    @JsonRpcMethod(value = "AnnotationService.setAnnotationBoundsInSubWorkflow")
    void setAnnotationBoundsInSubWorkflow(java.util.UUID jobId, String nodeId, String annoId, BoundsEnt bounds)  throws ServiceExceptions.NotASubWorkflowException, ServiceExceptions.NotFoundException;

}

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
package com.knime.gateway.jsonrpc.remote.service;

import com.knime.gateway.v0.entity.BoundsEnt;

import com.googlecode.jsonrpc4j.JsonRpcError;
import com.googlecode.jsonrpc4j.JsonRpcErrors;
import com.googlecode.jsonrpc4j.JsonRpcMethod;
import com.googlecode.jsonrpc4j.JsonRpcParam;
import com.googlecode.jsonrpc4j.JsonRpcService;

import com.knime.gateway.v0.service.util.ServiceExceptions;

import com.knime.gateway.v0.service.AnnotationService;

/**
 * Json rpc annotated class that wraps another service and delegates the method calls. 
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
@JsonRpcService(value = "AnnotationService")
@javax.annotation.Generated(value = "org.knime.gateway.codegen.GatewayCodegen")
public class JsonRpcAnnotationServiceWrapper implements AnnotationService {

    private final AnnotationService m_service;
    
    public JsonRpcAnnotationServiceWrapper(AnnotationService service) {
        m_service = service;
    }

	/**
     * {@inheritDoc}
     */
    @Override
    @JsonRpcMethod(value = "setAnnotationBounds")
    @JsonRpcErrors(value = {
        @JsonRpcError(exception = ServiceExceptions.NotFoundException.class, code = -32600,
            data = "404" /*per convention the data property contains the status code*/)
    })
    public void setAnnotationBounds(@JsonRpcParam(value="jobId") java.util.UUID jobId, @JsonRpcParam(value="annoId") String annoId, @JsonRpcParam(value="bounds") BoundsEnt bounds)  throws ServiceExceptions.NotFoundException {
        m_service.setAnnotationBounds(jobId, annoId, bounds);    
    }

	/**
     * {@inheritDoc}
     */
    @Override
    @JsonRpcMethod(value = "setAnnotationBoundsInSubWorkflow")
    @JsonRpcErrors(value = {
        @JsonRpcError(exception = ServiceExceptions.NotASubWorkflowException.class, code = -32600,
            data = "400" /*per convention the data property contains the status code*/),
        @JsonRpcError(exception = ServiceExceptions.NotFoundException.class, code = -32600,
            data = "404" /*per convention the data property contains the status code*/)
    })
    public void setAnnotationBoundsInSubWorkflow(@JsonRpcParam(value="jobId") java.util.UUID jobId, @JsonRpcParam(value="nodeId") String nodeId, @JsonRpcParam(value="annoId") String annoId, @JsonRpcParam(value="bounds") BoundsEnt bounds)  throws ServiceExceptions.NotASubWorkflowException, ServiceExceptions.NotFoundException {
        m_service.setAnnotationBoundsInSubWorkflow(jobId, nodeId, annoId, bounds);    
    }

}

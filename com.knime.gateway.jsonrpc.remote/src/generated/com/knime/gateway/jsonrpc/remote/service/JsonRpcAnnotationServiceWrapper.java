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

import com.knime.gateway.entity.BoundsEnt;

import com.googlecode.jsonrpc4j.JsonRpcError;
import com.googlecode.jsonrpc4j.JsonRpcErrors;
import com.googlecode.jsonrpc4j.JsonRpcMethod;
import com.googlecode.jsonrpc4j.JsonRpcParam;
import com.googlecode.jsonrpc4j.JsonRpcService;

import com.knime.gateway.service.util.ServiceExceptions;

import com.knime.gateway.service.AnnotationService;

/**
 * Json rpc annotated class that wraps another service and delegates the method calls. 
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
@JsonRpcService(value = "AnnotationService")
@javax.annotation.Generated(value = {"org.knime.gateway.codegen.GatewayCodegen", "src-gen/com.knime.gateway.jsonrpc.remote-config.json"})
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
        @JsonRpcError(exception = ServiceExceptions.NotASubWorkflowException.class, code = -32600,
            data = "NotASubWorkflowException" /*per convention the data property contains the exception name*/),
        @JsonRpcError(exception = ServiceExceptions.NotFoundException.class, code = -32600,
            data = "NotFoundException" /*per convention the data property contains the exception name*/)
    })
    public void setAnnotationBounds(@JsonRpcParam(value="jobId") java.util.UUID jobId, @JsonRpcParam(value="nodeId") com.knime.gateway.entity.NodeIDEnt nodeId, @JsonRpcParam(value="annoId") com.knime.gateway.entity.AnnotationIDEnt annoId, @JsonRpcParam(value="boundsEnt") BoundsEnt boundsEnt)  throws ServiceExceptions.NotASubWorkflowException, ServiceExceptions.NotFoundException {
        m_service.setAnnotationBounds(jobId, nodeId, annoId, boundsEnt);    
    }

}

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

import com.knime.gateway.v0.entity.ConnectionEnt;
import com.knime.gateway.v0.entity.PatchEnt;
import com.knime.gateway.v0.entity.WorkflowPartsEnt;
import com.knime.gateway.v0.entity.WorkflowSnapshotEnt;

import com.googlecode.jsonrpc4j.JsonRpcError;
import com.googlecode.jsonrpc4j.JsonRpcErrors;
import com.googlecode.jsonrpc4j.JsonRpcMethod;
import com.googlecode.jsonrpc4j.JsonRpcParam;
import com.googlecode.jsonrpc4j.JsonRpcService;

import com.knime.gateway.v0.service.util.ServiceExceptions;

import com.knime.gateway.v0.service.WorkflowService;

/**
 * Json rpc annotated class that wraps another service and delegates the method calls. 
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
@JsonRpcService(value = "WorkflowService")
@javax.annotation.Generated(value = "org.knime.gateway.codegen.GatewayCodegen")
public class JsonRpcWorkflowServiceWrapper implements WorkflowService {

    private final WorkflowService m_service;
    
    public JsonRpcWorkflowServiceWrapper(WorkflowService service) {
        m_service = service;
    }

	/**
     * {@inheritDoc}
     */
    @Override
    @JsonRpcMethod(value = "createConnection")
    @JsonRpcErrors(value = {
        @JsonRpcError(exception = ServiceExceptions.ActionNotAllowedException.class, code = -32600,
            data = "405" /*per convention the data property contains the status code*/)
    })
    public String createConnection(@JsonRpcParam(value="jobId") java.util.UUID jobId, @JsonRpcParam(value="connectionEnt") ConnectionEnt connectionEnt)  throws ServiceExceptions.ActionNotAllowedException {
        return m_service.createConnection(jobId, connectionEnt);    
    }

	/**
     * {@inheritDoc}
     */
    @Override
    @JsonRpcMethod(value = "createWorkflowCopy")
    @JsonRpcErrors(value = {
        @JsonRpcError(exception = ServiceExceptions.NotASubWorkflowException.class, code = -32600,
            data = "400" /*per convention the data property contains the status code*/),
        @JsonRpcError(exception = ServiceExceptions.NodeNotFoundException.class, code = -32600,
            data = "404" /*per convention the data property contains the status code*/),
        @JsonRpcError(exception = ServiceExceptions.InvalidRequestException.class, code = -32600,
            data = "405" /*per convention the data property contains the status code*/)
    })
    public java.util.UUID createWorkflowCopy(@JsonRpcParam(value="jobId") java.util.UUID jobId, @JsonRpcParam(value="workflowPartsEnt") WorkflowPartsEnt workflowPartsEnt)  throws ServiceExceptions.NotASubWorkflowException, ServiceExceptions.NodeNotFoundException, ServiceExceptions.InvalidRequestException {
        return m_service.createWorkflowCopy(jobId, workflowPartsEnt);    
    }

	/**
     * {@inheritDoc}
     */
    @Override
    @JsonRpcMethod(value = "deleteWorkflowParts")
    @JsonRpcErrors(value = {
        @JsonRpcError(exception = ServiceExceptions.NotASubWorkflowException.class, code = -32600,
            data = "400" /*per convention the data property contains the status code*/),
        @JsonRpcError(exception = ServiceExceptions.NodeNotFoundException.class, code = -32600,
            data = "404" /*per convention the data property contains the status code*/),
        @JsonRpcError(exception = ServiceExceptions.ActionNotAllowedException.class, code = -32600,
            data = "405" /*per convention the data property contains the status code*/)
    })
    public java.util.UUID deleteWorkflowParts(@JsonRpcParam(value="jobId") java.util.UUID jobId, @JsonRpcParam(value="workflowPartsEnt") WorkflowPartsEnt workflowPartsEnt, Boolean copy)  throws ServiceExceptions.NotASubWorkflowException, ServiceExceptions.NodeNotFoundException, ServiceExceptions.ActionNotAllowedException {
        return m_service.deleteWorkflowParts(jobId, workflowPartsEnt, copy);    
    }

	/**
     * {@inheritDoc}
     */
    @Override
    @JsonRpcMethod(value = "getSubWorkflow")
    @JsonRpcErrors(value = {
        @JsonRpcError(exception = ServiceExceptions.NotASubWorkflowException.class, code = -32600,
            data = "400" /*per convention the data property contains the status code*/),
        @JsonRpcError(exception = ServiceExceptions.NodeNotFoundException.class, code = -32600,
            data = "404" /*per convention the data property contains the status code*/)
    })
    public WorkflowSnapshotEnt getSubWorkflow(@JsonRpcParam(value="jobId") java.util.UUID jobId, @JsonRpcParam(value="nodeId") String nodeId)  throws ServiceExceptions.NotASubWorkflowException, ServiceExceptions.NodeNotFoundException {
        return m_service.getSubWorkflow(jobId, nodeId);    
    }

	/**
     * {@inheritDoc}
     */
    @Override
    @JsonRpcMethod(value = "getSubWorkflowDiff")
    @JsonRpcErrors(value = {
        @JsonRpcError(exception = ServiceExceptions.NotASubWorkflowException.class, code = -32600,
            data = "400" /*per convention the data property contains the status code*/),
        @JsonRpcError(exception = ServiceExceptions.NotFoundException.class, code = -32600,
            data = "404" /*per convention the data property contains the status code*/)
    })
    public PatchEnt getSubWorkflowDiff(@JsonRpcParam(value="jobId") java.util.UUID jobId, @JsonRpcParam(value="nodeId") String nodeId, java.util.UUID snapshotId)  throws ServiceExceptions.NotASubWorkflowException, ServiceExceptions.NotFoundException {
        return m_service.getSubWorkflowDiff(jobId, nodeId, snapshotId);    
    }

	/**
     * {@inheritDoc}
     */
    @Override
    @JsonRpcMethod(value = "getWorkflow")
    public WorkflowSnapshotEnt getWorkflow(@JsonRpcParam(value="jobId") java.util.UUID jobId)  {
        return m_service.getWorkflow(jobId);    
    }

	/**
     * {@inheritDoc}
     */
    @Override
    @JsonRpcMethod(value = "getWorkflowDiff")
    @JsonRpcErrors(value = {
        @JsonRpcError(exception = ServiceExceptions.NotFoundException.class, code = -32600,
            data = "404" /*per convention the data property contains the status code*/)
    })
    public PatchEnt getWorkflowDiff(@JsonRpcParam(value="jobId") java.util.UUID jobId, java.util.UUID snapshotId)  throws ServiceExceptions.NotFoundException {
        return m_service.getWorkflowDiff(jobId, snapshotId);    
    }

	/**
     * {@inheritDoc}
     */
    @Override
    @JsonRpcMethod(value = "pasteWorkflowParts")
    @JsonRpcErrors(value = {
        @JsonRpcError(exception = ServiceExceptions.NotASubWorkflowException.class, code = -32600,
            data = "400" /*per convention the data property contains the status code*/),
        @JsonRpcError(exception = ServiceExceptions.NotFoundException.class, code = -32600,
            data = "404" /*per convention the data property contains the status code*/)
    })
    public WorkflowPartsEnt pasteWorkflowParts(@JsonRpcParam(value="jobId") java.util.UUID jobId, @JsonRpcParam(value="partsId") java.util.UUID partsId, Integer x, Integer y, String nodeId)  throws ServiceExceptions.NotASubWorkflowException, ServiceExceptions.NotFoundException {
        return m_service.pasteWorkflowParts(jobId, partsId, x, y, nodeId);    
    }

}

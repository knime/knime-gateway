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

import com.knime.gateway.entity.ConnectionEnt;
import com.knime.gateway.entity.PatchEnt;
import com.knime.gateway.entity.WorkflowPartsEnt;
import com.knime.gateway.entity.WorkflowSnapshotEnt;

import com.googlecode.jsonrpc4j.JsonRpcError;
import com.googlecode.jsonrpc4j.JsonRpcErrors;
import com.googlecode.jsonrpc4j.JsonRpcMethod;
import com.googlecode.jsonrpc4j.JsonRpcParam;
import com.googlecode.jsonrpc4j.JsonRpcService;

import com.knime.gateway.service.util.ServiceExceptions;

import com.knime.gateway.service.WorkflowService;

/**
 * Json rpc annotated class that wraps another service and delegates the method calls. 
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
@JsonRpcService(value = "WorkflowService")
@javax.annotation.Generated(value = {"org.knime.gateway.codegen.GatewayCodegen", "src-gen/com.knime.gateway.jsonrpc.remote-config.json"})
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
            data = "ActionNotAllowedException" /*per convention the data property contains the exception name*/)
    })
    public com.knime.gateway.entity.ConnectionIDEnt createConnection(@JsonRpcParam(value="jobId") java.util.UUID jobId, @JsonRpcParam(value="connectionEnt") ConnectionEnt connectionEnt)  throws ServiceExceptions.ActionNotAllowedException {
        return m_service.createConnection(jobId, connectionEnt);    
    }

	/**
     * {@inheritDoc}
     */
    @Override
    @JsonRpcMethod(value = "createWorkflowCopy")
    @JsonRpcErrors(value = {
        @JsonRpcError(exception = ServiceExceptions.NotASubWorkflowException.class, code = -32600,
            data = "NotASubWorkflowException" /*per convention the data property contains the exception name*/),
        @JsonRpcError(exception = ServiceExceptions.NodeNotFoundException.class, code = -32600,
            data = "NodeNotFoundException" /*per convention the data property contains the exception name*/),
        @JsonRpcError(exception = ServiceExceptions.InvalidRequestException.class, code = -32600,
            data = "InvalidRequestException" /*per convention the data property contains the exception name*/)
    })
    public java.util.UUID createWorkflowCopy(@JsonRpcParam(value="jobId") java.util.UUID jobId, @JsonRpcParam(value="workflowId") com.knime.gateway.entity.NodeIDEnt workflowId, @JsonRpcParam(value="workflowPartsEnt") WorkflowPartsEnt workflowPartsEnt)  throws ServiceExceptions.NotASubWorkflowException, ServiceExceptions.NodeNotFoundException, ServiceExceptions.InvalidRequestException {
        return m_service.createWorkflowCopy(jobId, workflowId, workflowPartsEnt);    
    }

	/**
     * {@inheritDoc}
     */
    @Override
    @JsonRpcMethod(value = "deleteWorkflowParts")
    @JsonRpcErrors(value = {
        @JsonRpcError(exception = ServiceExceptions.NotASubWorkflowException.class, code = -32600,
            data = "NotASubWorkflowException" /*per convention the data property contains the exception name*/),
        @JsonRpcError(exception = ServiceExceptions.NodeNotFoundException.class, code = -32600,
            data = "NodeNotFoundException" /*per convention the data property contains the exception name*/),
        @JsonRpcError(exception = ServiceExceptions.ActionNotAllowedException.class, code = -32600,
            data = "ActionNotAllowedException" /*per convention the data property contains the exception name*/)
    })
    public java.util.UUID deleteWorkflowParts(@JsonRpcParam(value="jobId") java.util.UUID jobId, @JsonRpcParam(value="workflowId") com.knime.gateway.entity.NodeIDEnt workflowId, @JsonRpcParam(value="workflowPartsEnt") WorkflowPartsEnt workflowPartsEnt, Boolean copy)  throws ServiceExceptions.NotASubWorkflowException, ServiceExceptions.NodeNotFoundException, ServiceExceptions.ActionNotAllowedException {
        return m_service.deleteWorkflowParts(jobId, workflowId, workflowPartsEnt, copy);    
    }

	/**
     * {@inheritDoc}
     */
    @Override
    @JsonRpcMethod(value = "getWorkflow")
    @JsonRpcErrors(value = {
        @JsonRpcError(exception = ServiceExceptions.NotASubWorkflowException.class, code = -32600,
            data = "NotASubWorkflowException" /*per convention the data property contains the exception name*/),
        @JsonRpcError(exception = ServiceExceptions.NodeNotFoundException.class, code = -32600,
            data = "NodeNotFoundException" /*per convention the data property contains the exception name*/)
    })
    public WorkflowSnapshotEnt getWorkflow(@JsonRpcParam(value="jobId") java.util.UUID jobId, @JsonRpcParam(value="workflowId") com.knime.gateway.entity.NodeIDEnt workflowId)  throws ServiceExceptions.NotASubWorkflowException, ServiceExceptions.NodeNotFoundException {
        return m_service.getWorkflow(jobId, workflowId);    
    }

	/**
     * {@inheritDoc}
     */
    @Override
    @JsonRpcMethod(value = "getWorkflowDiff")
    @JsonRpcErrors(value = {
        @JsonRpcError(exception = ServiceExceptions.NotASubWorkflowException.class, code = -32600,
            data = "NotASubWorkflowException" /*per convention the data property contains the exception name*/),
        @JsonRpcError(exception = ServiceExceptions.NotFoundException.class, code = -32600,
            data = "NotFoundException" /*per convention the data property contains the exception name*/)
    })
    public PatchEnt getWorkflowDiff(@JsonRpcParam(value="jobId") java.util.UUID jobId, @JsonRpcParam(value="workflowId") com.knime.gateway.entity.NodeIDEnt workflowId, java.util.UUID snapshotId)  throws ServiceExceptions.NotASubWorkflowException, ServiceExceptions.NotFoundException {
        return m_service.getWorkflowDiff(jobId, workflowId, snapshotId);    
    }

	/**
     * {@inheritDoc}
     */
    @Override
    @JsonRpcMethod(value = "pasteWorkflowParts")
    @JsonRpcErrors(value = {
        @JsonRpcError(exception = ServiceExceptions.NotASubWorkflowException.class, code = -32600,
            data = "NotASubWorkflowException" /*per convention the data property contains the exception name*/),
        @JsonRpcError(exception = ServiceExceptions.NotFoundException.class, code = -32600,
            data = "NotFoundException" /*per convention the data property contains the exception name*/)
    })
    public WorkflowPartsEnt pasteWorkflowParts(@JsonRpcParam(value="jobId") java.util.UUID jobId, @JsonRpcParam(value="workflowId") com.knime.gateway.entity.NodeIDEnt workflowId, @JsonRpcParam(value="partsId") java.util.UUID partsId, Integer x, Integer y)  throws ServiceExceptions.NotASubWorkflowException, ServiceExceptions.NotFoundException {
        return m_service.pasteWorkflowParts(jobId, workflowId, partsId, x, y);    
    }

	/**
     * {@inheritDoc}
     */
    @Override
    @JsonRpcMethod(value = "redo")
    @JsonRpcErrors(value = {
        @JsonRpcError(exception = ServiceExceptions.NotASubWorkflowException.class, code = -32600,
            data = "NotASubWorkflowException" /*per convention the data property contains the exception name*/),
        @JsonRpcError(exception = ServiceExceptions.NotFoundException.class, code = -32600,
            data = "NotFoundException" /*per convention the data property contains the exception name*/)
    })
    public Boolean redo(@JsonRpcParam(value="jobId") java.util.UUID jobId, @JsonRpcParam(value="workflowId") com.knime.gateway.entity.NodeIDEnt workflowId)  throws ServiceExceptions.NotASubWorkflowException, ServiceExceptions.NotFoundException {
        return m_service.redo(jobId, workflowId);    
    }

	/**
     * {@inheritDoc}
     */
    @Override
    @JsonRpcMethod(value = "undo")
    @JsonRpcErrors(value = {
        @JsonRpcError(exception = ServiceExceptions.NotASubWorkflowException.class, code = -32600,
            data = "NotASubWorkflowException" /*per convention the data property contains the exception name*/),
        @JsonRpcError(exception = ServiceExceptions.NotFoundException.class, code = -32600,
            data = "NotFoundException" /*per convention the data property contains the exception name*/)
    })
    public Boolean undo(@JsonRpcParam(value="jobId") java.util.UUID jobId, @JsonRpcParam(value="workflowId") com.knime.gateway.entity.NodeIDEnt workflowId)  throws ServiceExceptions.NotASubWorkflowException, ServiceExceptions.NotFoundException {
        return m_service.undo(jobId, workflowId);    
    }

}

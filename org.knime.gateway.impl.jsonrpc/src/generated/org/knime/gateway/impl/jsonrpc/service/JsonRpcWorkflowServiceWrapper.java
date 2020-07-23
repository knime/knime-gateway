/*
 * ------------------------------------------------------------------------
 *  Copyright by KNIME AG, Zurich, Switzerland
 *  Website: http://www.knime.com; Email: contact@knime.com
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License, Version 3, as
 *  published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but
 *  WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, see <http://www.gnu.org/licenses>.
 *
 *  Additional permission under GNU GPL version 3 section 7:
 *
 *  KNIME interoperates with ECLIPSE solely via ECLIPSE's plug-in APIs.
 *  Hence, KNIME and ECLIPSE are both independent programs and are not
 *  derived from each other. Should, however, the interpretation of the
 *  GNU GPL Version 3 ("License") under any applicable laws result in
 *  KNIME and ECLIPSE being a combined program, KNIME AG herewith grants
 *  you the additional permission to use and propagate KNIME together with
 *  ECLIPSE with only the license terms in place for ECLIPSE applying to
 *  ECLIPSE and the GNU GPL Version 3 applying for KNIME, provided the
 *  license terms of ECLIPSE themselves allow for the respective use and
 *  propagation of ECLIPSE together with KNIME.
 *
 *  Additional permission relating to nodes for KNIME that extend the Node
 *  Extension (and in particular that are based on subclasses of NodeModel,
 *  NodeDialog, and NodeView) and that only interoperate with KNIME through
 *  standard APIs ("Nodes"):
 *  Nodes are deemed to be separate and independent programs and to not be
 *  covered works.  Notwithstanding anything to the contrary in the
 *  License, the License does not apply to Nodes, you are not required to
 *  license Nodes under the License, and you are granted a license to
 *  prepare and propagate Nodes, in each case even if such Nodes are
 *  propagated with or for interoperation with KNIME.  The owner of a Node
 *  may freely choose the license terms applicable to such Node, including
 *  when such Node is propagated with or for interoperation with KNIME.
 * ------------------------------------------------------------------------
 */
package org.knime.gateway.impl.jsonrpc.service;

import org.knime.gateway.api.entity.ConnectionEnt;
import org.knime.gateway.api.entity.PatchEnt;
import org.knime.gateway.api.entity.WorkflowPartsEnt;
import org.knime.gateway.api.entity.WorkflowSnapshotEnt;

import com.googlecode.jsonrpc4j.JsonRpcError;
import com.googlecode.jsonrpc4j.JsonRpcErrors;
import com.googlecode.jsonrpc4j.JsonRpcMethod;
import com.googlecode.jsonrpc4j.JsonRpcParam;
import com.googlecode.jsonrpc4j.JsonRpcService;

import org.knime.gateway.api.service.util.ServiceExceptions;

import org.knime.gateway.api.service.WorkflowService;

/**
 * Json rpc annotated class that wraps another service and delegates the method calls. 
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
@JsonRpcService(value = "WorkflowService")
@javax.annotation.Generated(value = {"com.knime.gateway.codegen.GatewayCodegen", "src-gen/org.knime.gateway.impl.jsonrpc-config.json"})
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
    public org.knime.gateway.api.entity.ConnectionIDEnt createConnection(@JsonRpcParam(value="jobId") java.util.UUID jobId, @JsonRpcParam(value="connectionEnt") ConnectionEnt connectionEnt)  throws ServiceExceptions.ActionNotAllowedException {
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
    public java.util.UUID createWorkflowCopy(@JsonRpcParam(value="jobId") java.util.UUID jobId, @JsonRpcParam(value="workflowId") org.knime.gateway.api.entity.NodeIDEnt workflowId, @JsonRpcParam(value="workflowPartsEnt") WorkflowPartsEnt workflowPartsEnt)  throws ServiceExceptions.NotASubWorkflowException, ServiceExceptions.NodeNotFoundException, ServiceExceptions.InvalidRequestException {
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
    public java.util.UUID deleteWorkflowParts(@JsonRpcParam(value="jobId") java.util.UUID jobId, @JsonRpcParam(value="workflowId") org.knime.gateway.api.entity.NodeIDEnt workflowId, @JsonRpcParam(value="workflowPartsEnt") WorkflowPartsEnt workflowPartsEnt, Boolean copy)  throws ServiceExceptions.NotASubWorkflowException, ServiceExceptions.NodeNotFoundException, ServiceExceptions.ActionNotAllowedException {
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
    public WorkflowSnapshotEnt getWorkflow(@JsonRpcParam(value="jobId") java.util.UUID jobId, @JsonRpcParam(value="workflowId") org.knime.gateway.api.entity.NodeIDEnt workflowId)  throws ServiceExceptions.NotASubWorkflowException, ServiceExceptions.NodeNotFoundException {
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
    public PatchEnt getWorkflowDiff(@JsonRpcParam(value="jobId") java.util.UUID jobId, @JsonRpcParam(value="workflowId") org.knime.gateway.api.entity.NodeIDEnt workflowId, java.util.UUID snapshotId)  throws ServiceExceptions.NotASubWorkflowException, ServiceExceptions.NotFoundException {
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
    public WorkflowPartsEnt pasteWorkflowParts(@JsonRpcParam(value="jobId") java.util.UUID jobId, @JsonRpcParam(value="workflowId") org.knime.gateway.api.entity.NodeIDEnt workflowId, @JsonRpcParam(value="partsId") java.util.UUID partsId, Integer x, Integer y)  throws ServiceExceptions.NotASubWorkflowException, ServiceExceptions.NotFoundException {
        return m_service.pasteWorkflowParts(jobId, workflowId, partsId, x, y);    
    }

}

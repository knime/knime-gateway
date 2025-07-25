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
package org.knime.gateway.impl.webui.jsonrpc.service;

import org.knime.gateway.api.webui.entity.CommandResultEnt;
import org.knime.gateway.api.webui.entity.NodeIdAndIsExecutedEnt;
import org.knime.gateway.api.webui.entity.WorkflowCommandEnt;
import org.knime.gateway.api.webui.entity.WorkflowMonitorStateSnapshotEnt;
import org.knime.gateway.api.webui.entity.WorkflowSnapshotEnt;

import com.googlecode.jsonrpc4j.JsonRpcError;
import com.googlecode.jsonrpc4j.JsonRpcErrors;
import com.googlecode.jsonrpc4j.JsonRpcMethod;
import com.googlecode.jsonrpc4j.JsonRpcParam;
import com.googlecode.jsonrpc4j.JsonRpcService;

import org.knime.gateway.api.webui.service.util.ServiceExceptions;

import org.knime.gateway.api.webui.service.WorkflowService;

/**
 * Json rpc annotated class that wraps another service and delegates the method calls. 
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
@JsonRpcService(value = "WorkflowService")
@jakarta.annotation.Generated(value = {"com.knime.gateway.codegen.GatewayCodegen", "src-gen/api/web-ui/configs/org.knime.gateway.impl.jsonrpc-config.json"})
public class JsonRpcWorkflowServiceWrapper implements WorkflowService {

    private final java.util.function.Supplier<WorkflowService> m_service;
    
    public JsonRpcWorkflowServiceWrapper(java.util.function.Supplier<WorkflowService> service) {
        m_service = service;
    }

	/**
     * {@inheritDoc}
     */
    @Override
    @JsonRpcMethod(value = "disposeVersion")
    @JsonRpcErrors(value = {
        @JsonRpcError(exception = ServiceExceptions.ServiceCallException.class, code = -32600,
            data = "ServiceCallException" /*per convention the data property contains the exception name*/)
    })
    public void disposeVersion(@JsonRpcParam(value="projectId") String projectId, @JsonRpcParam(value="version") String version)  throws ServiceExceptions.ServiceCallException {
        m_service.get().disposeVersion(projectId, version);    
    }

	/**
     * {@inheritDoc}
     */
    @Override
    @JsonRpcMethod(value = "executeWorkflowCommand")
    @JsonRpcErrors(value = {
        @JsonRpcError(exception = ServiceExceptions.ServiceCallException.class, code = -32600,
            data = "ServiceCallException" /*per convention the data property contains the exception name*/)
    })
    public CommandResultEnt executeWorkflowCommand(@JsonRpcParam(value="projectId") String projectId, @JsonRpcParam(value="workflowId") org.knime.gateway.api.entity.NodeIDEnt workflowId, @JsonRpcParam(value="workflowCommand") WorkflowCommandEnt workflowCommand)  throws ServiceExceptions.ServiceCallException {
        return m_service.get().executeWorkflowCommand(projectId, workflowId, workflowCommand);    
    }

	/**
     * {@inheritDoc}
     */
    @Override
    @JsonRpcMethod(value = "getUpdatableLinkedComponents")
    @JsonRpcErrors(value = {
        @JsonRpcError(exception = ServiceExceptions.NotASubWorkflowException.class, code = -32600,
            data = "NotASubWorkflowException" /*per convention the data property contains the exception name*/),
        @JsonRpcError(exception = ServiceExceptions.NodeNotFoundException.class, code = -32600,
            data = "NodeNotFoundException" /*per convention the data property contains the exception name*/),
        @JsonRpcError(exception = ServiceExceptions.InvalidRequestException.class, code = -32600,
            data = "InvalidRequestException" /*per convention the data property contains the exception name*/)
    })
    public java.util.List<NodeIdAndIsExecutedEnt> getUpdatableLinkedComponents(@JsonRpcParam(value="projectId") String projectId, @JsonRpcParam(value="workflowId") org.knime.gateway.api.entity.NodeIDEnt workflowId)  throws ServiceExceptions.NotASubWorkflowException, ServiceExceptions.NodeNotFoundException, ServiceExceptions.InvalidRequestException {
        return m_service.get().getUpdatableLinkedComponents(projectId, workflowId);    
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
    public WorkflowSnapshotEnt getWorkflow(@JsonRpcParam(value="projectId") String projectId, @JsonRpcParam(value="workflowId") org.knime.gateway.api.entity.NodeIDEnt workflowId, @JsonRpcParam(value="versionId") String versionId, @JsonRpcParam(value="includeInteractionInfo") Boolean includeInteractionInfo)  throws ServiceExceptions.NotASubWorkflowException, ServiceExceptions.NodeNotFoundException {
        return m_service.get().getWorkflow(projectId, workflowId, versionId, includeInteractionInfo);    
    }

	/**
     * {@inheritDoc}
     */
    @Override
    @JsonRpcMethod(value = "getWorkflowMonitorState")
    public WorkflowMonitorStateSnapshotEnt getWorkflowMonitorState(@JsonRpcParam(value="projectId") String projectId)  {
        return m_service.get().getWorkflowMonitorState(projectId);    
    }

	/**
     * {@inheritDoc}
     */
    @Override
    @JsonRpcMethod(value = "redoWorkflowCommand")
    @JsonRpcErrors(value = {
        @JsonRpcError(exception = ServiceExceptions.ServiceCallException.class, code = -32600,
            data = "ServiceCallException" /*per convention the data property contains the exception name*/)
    })
    public void redoWorkflowCommand(@JsonRpcParam(value="projectId") String projectId, @JsonRpcParam(value="workflowId") org.knime.gateway.api.entity.NodeIDEnt workflowId)  throws ServiceExceptions.ServiceCallException {
        m_service.get().redoWorkflowCommand(projectId, workflowId);    
    }

	/**
     * {@inheritDoc}
     */
    @Override
    @JsonRpcMethod(value = "saveProject")
    @JsonRpcErrors(value = {
        @JsonRpcError(exception = ServiceExceptions.ServiceCallException.class, code = -32600,
            data = "ServiceCallException" /*per convention the data property contains the exception name*/)
    })
    public void saveProject(@JsonRpcParam(value="projectId") String projectId)  throws ServiceExceptions.ServiceCallException {
        m_service.get().saveProject(projectId);    
    }

	/**
     * {@inheritDoc}
     */
    @Override
    @JsonRpcMethod(value = "undoWorkflowCommand")
    @JsonRpcErrors(value = {
        @JsonRpcError(exception = ServiceExceptions.ServiceCallException.class, code = -32600,
            data = "ServiceCallException" /*per convention the data property contains the exception name*/)
    })
    public void undoWorkflowCommand(@JsonRpcParam(value="projectId") String projectId, @JsonRpcParam(value="workflowId") org.knime.gateway.api.entity.NodeIDEnt workflowId)  throws ServiceExceptions.ServiceCallException {
        m_service.get().undoWorkflowCommand(projectId, workflowId);    
    }

}

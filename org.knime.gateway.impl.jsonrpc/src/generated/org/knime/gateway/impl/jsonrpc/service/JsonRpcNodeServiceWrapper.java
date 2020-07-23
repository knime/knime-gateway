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

import org.knime.gateway.api.entity.BoundsEnt;
import org.knime.gateway.api.entity.DataTableEnt;
import org.knime.gateway.api.entity.FlowVariableEnt;
import org.knime.gateway.api.entity.JavaObjectEnt;
import org.knime.gateway.api.entity.MetaNodeDialogEnt;
import org.knime.gateway.api.entity.NodeEnt;
import org.knime.gateway.api.entity.NodeFactoryKeyEnt;
import org.knime.gateway.api.entity.NodeSettingsEnt;
import org.knime.gateway.api.entity.PortObjectSpecEnt;
import org.knime.gateway.api.entity.ViewDataEnt;

import com.googlecode.jsonrpc4j.JsonRpcError;
import com.googlecode.jsonrpc4j.JsonRpcErrors;
import com.googlecode.jsonrpc4j.JsonRpcMethod;
import com.googlecode.jsonrpc4j.JsonRpcParam;
import com.googlecode.jsonrpc4j.JsonRpcService;

import org.knime.gateway.api.service.util.ServiceExceptions;

import org.knime.gateway.api.service.NodeService;

/**
 * Json rpc annotated class that wraps another service and delegates the method calls. 
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
@JsonRpcService(value = "NodeService")
@javax.annotation.Generated(value = {"com.knime.gateway.codegen.GatewayCodegen", "src-gen/org.knime.gateway.impl.jsonrpc-config.json"})
public class JsonRpcNodeServiceWrapper implements NodeService {

    private final NodeService m_service;
    
    public JsonRpcNodeServiceWrapper(NodeService service) {
        m_service = service;
    }

	/**
     * {@inheritDoc}
     */
    @Override
    @JsonRpcMethod(value = "changeAndGetNodeState")
    @JsonRpcErrors(value = {
        @JsonRpcError(exception = ServiceExceptions.NodeNotFoundException.class, code = -32600,
            data = "NodeNotFoundException" /*per convention the data property contains the exception name*/),
        @JsonRpcError(exception = ServiceExceptions.ActionNotAllowedException.class, code = -32600,
            data = "ActionNotAllowedException" /*per convention the data property contains the exception name*/)
    })
    public String changeAndGetNodeState(@JsonRpcParam(value="jobId") java.util.UUID jobId, @JsonRpcParam(value="nodeId") org.knime.gateway.api.entity.NodeIDEnt nodeId, String action)  throws ServiceExceptions.NodeNotFoundException, ServiceExceptions.ActionNotAllowedException {
        return m_service.changeAndGetNodeState(jobId, nodeId, action);    
    }

	/**
     * {@inheritDoc}
     */
    @Override
    @JsonRpcMethod(value = "createNode")
    @JsonRpcErrors(value = {
        @JsonRpcError(exception = ServiceExceptions.NotASubWorkflowException.class, code = -32600,
            data = "NotASubWorkflowException" /*per convention the data property contains the exception name*/),
        @JsonRpcError(exception = ServiceExceptions.NodeNotFoundException.class, code = -32600,
            data = "NodeNotFoundException" /*per convention the data property contains the exception name*/),
        @JsonRpcError(exception = ServiceExceptions.InvalidRequestException.class, code = -32600,
            data = "InvalidRequestException" /*per convention the data property contains the exception name*/)
    })
    public org.knime.gateway.api.entity.NodeIDEnt createNode(@JsonRpcParam(value="jobId") java.util.UUID jobId, @JsonRpcParam(value="workflowId") org.knime.gateway.api.entity.NodeIDEnt workflowId, Integer x, Integer y, @JsonRpcParam(value="nodeFactoryKeyEnt") NodeFactoryKeyEnt nodeFactoryKeyEnt)  throws ServiceExceptions.NotASubWorkflowException, ServiceExceptions.NodeNotFoundException, ServiceExceptions.InvalidRequestException {
        return m_service.createNode(jobId, workflowId, x, y, nodeFactoryKeyEnt);    
    }

	/**
     * {@inheritDoc}
     */
    @Override
    @JsonRpcMethod(value = "getInputFlowVariables")
    @JsonRpcErrors(value = {
        @JsonRpcError(exception = ServiceExceptions.NodeNotFoundException.class, code = -32600,
            data = "NodeNotFoundException" /*per convention the data property contains the exception name*/)
    })
    public java.util.List<FlowVariableEnt> getInputFlowVariables(@JsonRpcParam(value="jobId") java.util.UUID jobId, @JsonRpcParam(value="nodeId") org.knime.gateway.api.entity.NodeIDEnt nodeId)  throws ServiceExceptions.NodeNotFoundException {
        return m_service.getInputFlowVariables(jobId, nodeId);    
    }

	/**
     * {@inheritDoc}
     */
    @Override
    @JsonRpcMethod(value = "getInputPortSpecs")
    @JsonRpcErrors(value = {
        @JsonRpcError(exception = ServiceExceptions.NodeNotFoundException.class, code = -32600,
            data = "NodeNotFoundException" /*per convention the data property contains the exception name*/)
    })
    public java.util.List<PortObjectSpecEnt> getInputPortSpecs(@JsonRpcParam(value="jobId") java.util.UUID jobId, @JsonRpcParam(value="nodeId") org.knime.gateway.api.entity.NodeIDEnt nodeId)  throws ServiceExceptions.NodeNotFoundException {
        return m_service.getInputPortSpecs(jobId, nodeId);    
    }

	/**
     * {@inheritDoc}
     */
    @Override
    @JsonRpcMethod(value = "getNode")
    @JsonRpcErrors(value = {
        @JsonRpcError(exception = ServiceExceptions.NodeNotFoundException.class, code = -32600,
            data = "NodeNotFoundException" /*per convention the data property contains the exception name*/)
    })
    public NodeEnt getNode(@JsonRpcParam(value="jobId") java.util.UUID jobId, @JsonRpcParam(value="nodeId") org.knime.gateway.api.entity.NodeIDEnt nodeId)  throws ServiceExceptions.NodeNotFoundException {
        return m_service.getNode(jobId, nodeId);    
    }

	/**
     * {@inheritDoc}
     */
    @Override
    @JsonRpcMethod(value = "getNodeSettings")
    @JsonRpcErrors(value = {
        @JsonRpcError(exception = ServiceExceptions.NodeNotFoundException.class, code = -32600,
            data = "NodeNotFoundException" /*per convention the data property contains the exception name*/)
    })
    public NodeSettingsEnt getNodeSettings(@JsonRpcParam(value="jobId") java.util.UUID jobId, @JsonRpcParam(value="nodeId") org.knime.gateway.api.entity.NodeIDEnt nodeId)  throws ServiceExceptions.NodeNotFoundException {
        return m_service.getNodeSettings(jobId, nodeId);    
    }

	/**
     * {@inheritDoc}
     */
    @Override
    @JsonRpcMethod(value = "getOutputDataTable")
    @JsonRpcErrors(value = {
        @JsonRpcError(exception = ServiceExceptions.NodeNotFoundException.class, code = -32600,
            data = "NodeNotFoundException" /*per convention the data property contains the exception name*/),
        @JsonRpcError(exception = ServiceExceptions.InvalidRequestException.class, code = -32600,
            data = "InvalidRequestException" /*per convention the data property contains the exception name*/)
    })
    public DataTableEnt getOutputDataTable(@JsonRpcParam(value="jobId") java.util.UUID jobId, @JsonRpcParam(value="nodeId") org.knime.gateway.api.entity.NodeIDEnt nodeId, @JsonRpcParam(value="portIdx") Integer portIdx, Long from, Integer size)  throws ServiceExceptions.NodeNotFoundException, ServiceExceptions.InvalidRequestException {
        return m_service.getOutputDataTable(jobId, nodeId, portIdx, from, size);    
    }

	/**
     * {@inheritDoc}
     */
    @Override
    @JsonRpcMethod(value = "getOutputFlowVariables")
    @JsonRpcErrors(value = {
        @JsonRpcError(exception = ServiceExceptions.NodeNotFoundException.class, code = -32600,
            data = "NodeNotFoundException" /*per convention the data property contains the exception name*/)
    })
    public java.util.List<FlowVariableEnt> getOutputFlowVariables(@JsonRpcParam(value="jobId") java.util.UUID jobId, @JsonRpcParam(value="nodeId") org.knime.gateway.api.entity.NodeIDEnt nodeId)  throws ServiceExceptions.NodeNotFoundException {
        return m_service.getOutputFlowVariables(jobId, nodeId);    
    }

	/**
     * {@inheritDoc}
     */
    @Override
    @JsonRpcMethod(value = "getOutputPortSpecs")
    @JsonRpcErrors(value = {
        @JsonRpcError(exception = ServiceExceptions.NodeNotFoundException.class, code = -32600,
            data = "NodeNotFoundException" /*per convention the data property contains the exception name*/)
    })
    public java.util.List<PortObjectSpecEnt> getOutputPortSpecs(@JsonRpcParam(value="jobId") java.util.UUID jobId, @JsonRpcParam(value="nodeId") org.knime.gateway.api.entity.NodeIDEnt nodeId)  throws ServiceExceptions.NodeNotFoundException {
        return m_service.getOutputPortSpecs(jobId, nodeId);    
    }

	/**
     * {@inheritDoc}
     */
    @Override
    @JsonRpcMethod(value = "getViewData")
    @JsonRpcErrors(value = {
        @JsonRpcError(exception = ServiceExceptions.NodeNotFoundException.class, code = -32600,
            data = "NodeNotFoundException" /*per convention the data property contains the exception name*/),
        @JsonRpcError(exception = ServiceExceptions.InvalidRequestException.class, code = -32600,
            data = "InvalidRequestException" /*per convention the data property contains the exception name*/)
    })
    public ViewDataEnt getViewData(@JsonRpcParam(value="jobId") java.util.UUID jobId, @JsonRpcParam(value="nodeId") org.knime.gateway.api.entity.NodeIDEnt nodeId)  throws ServiceExceptions.NodeNotFoundException, ServiceExceptions.InvalidRequestException {
        return m_service.getViewData(jobId, nodeId);    
    }

	/**
     * {@inheritDoc}
     */
    @Override
    @JsonRpcMethod(value = "getWMetaNodeDialog")
    @JsonRpcErrors(value = {
        @JsonRpcError(exception = ServiceExceptions.NodeNotFoundException.class, code = -32600,
            data = "NodeNotFoundException" /*per convention the data property contains the exception name*/),
        @JsonRpcError(exception = ServiceExceptions.InvalidRequestException.class, code = -32600,
            data = "InvalidRequestException" /*per convention the data property contains the exception name*/)
    })
    public MetaNodeDialogEnt getWMetaNodeDialog(@JsonRpcParam(value="jobId") java.util.UUID jobId, @JsonRpcParam(value="nodeId") org.knime.gateway.api.entity.NodeIDEnt nodeId)  throws ServiceExceptions.NodeNotFoundException, ServiceExceptions.InvalidRequestException {
        return m_service.getWMetaNodeDialog(jobId, nodeId);    
    }

	/**
     * {@inheritDoc}
     */
    @Override
    @JsonRpcMethod(value = "replaceNode")
    @JsonRpcErrors(value = {
        @JsonRpcError(exception = ServiceExceptions.NodeNotFoundException.class, code = -32600,
            data = "NodeNotFoundException" /*per convention the data property contains the exception name*/),
        @JsonRpcError(exception = ServiceExceptions.ActionNotAllowedException.class, code = -32600,
            data = "ActionNotAllowedException" /*per convention the data property contains the exception name*/)
    })
    public NodeEnt replaceNode(@JsonRpcParam(value="jobId") java.util.UUID jobId, @JsonRpcParam(value="nodeId") org.knime.gateway.api.entity.NodeIDEnt nodeId, @JsonRpcParam(value="nodeFactoryKeyEnt") NodeFactoryKeyEnt nodeFactoryKeyEnt)  throws ServiceExceptions.NodeNotFoundException, ServiceExceptions.ActionNotAllowedException {
        return m_service.replaceNode(jobId, nodeId, nodeFactoryKeyEnt);    
    }

	/**
     * {@inheritDoc}
     */
    @Override
    @JsonRpcMethod(value = "setNodeBounds")
    @JsonRpcErrors(value = {
        @JsonRpcError(exception = ServiceExceptions.NodeNotFoundException.class, code = -32600,
            data = "NodeNotFoundException" /*per convention the data property contains the exception name*/)
    })
    public void setNodeBounds(@JsonRpcParam(value="jobId") java.util.UUID jobId, @JsonRpcParam(value="nodeId") org.knime.gateway.api.entity.NodeIDEnt nodeId, @JsonRpcParam(value="boundsEnt") BoundsEnt boundsEnt)  throws ServiceExceptions.NodeNotFoundException {
        m_service.setNodeBounds(jobId, nodeId, boundsEnt);    
    }

	/**
     * {@inheritDoc}
     */
    @Override
    @JsonRpcMethod(value = "setNodeSettings")
    @JsonRpcErrors(value = {
        @JsonRpcError(exception = ServiceExceptions.NodeNotFoundException.class, code = -32600,
            data = "NodeNotFoundException" /*per convention the data property contains the exception name*/),
        @JsonRpcError(exception = ServiceExceptions.InvalidSettingsException.class, code = -32600,
            data = "InvalidSettingsException" /*per convention the data property contains the exception name*/),
        @JsonRpcError(exception = ServiceExceptions.IllegalStateException.class, code = -32600,
            data = "IllegalStateException" /*per convention the data property contains the exception name*/)
    })
    public void setNodeSettings(@JsonRpcParam(value="jobId") java.util.UUID jobId, @JsonRpcParam(value="nodeId") org.knime.gateway.api.entity.NodeIDEnt nodeId, @JsonRpcParam(value="nodeSettingsEnt") NodeSettingsEnt nodeSettingsEnt)  throws ServiceExceptions.NodeNotFoundException, ServiceExceptions.InvalidSettingsException, ServiceExceptions.IllegalStateException {
        m_service.setNodeSettings(jobId, nodeId, nodeSettingsEnt);    
    }

	/**
     * {@inheritDoc}
     */
    @Override
    @JsonRpcMethod(value = "setViewValue")
    @JsonRpcErrors(value = {
        @JsonRpcError(exception = ServiceExceptions.NodeNotFoundException.class, code = -32600,
            data = "NodeNotFoundException" /*per convention the data property contains the exception name*/),
        @JsonRpcError(exception = ServiceExceptions.InvalidRequestException.class, code = -32600,
            data = "InvalidRequestException" /*per convention the data property contains the exception name*/)
    })
    public void setViewValue(@JsonRpcParam(value="jobId") java.util.UUID jobId, @JsonRpcParam(value="nodeId") org.knime.gateway.api.entity.NodeIDEnt nodeId, Boolean useAsDefault, @JsonRpcParam(value="javaObjectEnt") JavaObjectEnt javaObjectEnt)  throws ServiceExceptions.NodeNotFoundException, ServiceExceptions.InvalidRequestException {
        m_service.setViewValue(jobId, nodeId, useAsDefault, javaObjectEnt);    
    }

}

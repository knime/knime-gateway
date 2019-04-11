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
import com.knime.gateway.entity.DataTableEnt;
import com.knime.gateway.entity.FlowVariableEnt;
import com.knime.gateway.entity.JavaObjectEnt;
import com.knime.gateway.entity.MetaNodeDialogEnt;
import com.knime.gateway.entity.NodeEnt;
import com.knime.gateway.entity.NodeFactoryKeyEnt;
import com.knime.gateway.entity.NodeSettingsEnt;
import com.knime.gateway.entity.PortObjectSpecEnt;
import com.knime.gateway.entity.ViewDataEnt;

import com.googlecode.jsonrpc4j.JsonRpcError;
import com.googlecode.jsonrpc4j.JsonRpcErrors;
import com.googlecode.jsonrpc4j.JsonRpcMethod;
import com.googlecode.jsonrpc4j.JsonRpcParam;
import com.googlecode.jsonrpc4j.JsonRpcService;

import com.knime.gateway.service.util.ServiceExceptions;

import com.knime.gateway.service.NodeService;

/**
 * Json rpc annotated class that wraps another service and delegates the method calls. 
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
@JsonRpcService(value = "NodeService")
@javax.annotation.Generated(value = {"org.knime.gateway.codegen.GatewayCodegen", "src-gen/com.knime.gateway.jsonrpc.remote-config.json"})
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
    public String changeAndGetNodeState(@JsonRpcParam(value="jobId") java.util.UUID jobId, @JsonRpcParam(value="nodeId") com.knime.gateway.entity.NodeIDEnt nodeId, String action)  throws ServiceExceptions.NodeNotFoundException, ServiceExceptions.ActionNotAllowedException {
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
    public com.knime.gateway.entity.NodeIDEnt createNode(@JsonRpcParam(value="jobId") java.util.UUID jobId, Integer x, Integer y, @JsonRpcParam(value="nodeFactoryKeyEnt") NodeFactoryKeyEnt nodeFactoryKeyEnt, com.knime.gateway.entity.NodeIDEnt parentNodeId)  throws ServiceExceptions.NotASubWorkflowException, ServiceExceptions.NodeNotFoundException, ServiceExceptions.InvalidRequestException {
        return m_service.createNode(jobId, x, y, nodeFactoryKeyEnt, parentNodeId);    
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
    public java.util.List<FlowVariableEnt> getInputFlowVariables(@JsonRpcParam(value="jobId") java.util.UUID jobId, @JsonRpcParam(value="nodeId") com.knime.gateway.entity.NodeIDEnt nodeId)  throws ServiceExceptions.NodeNotFoundException {
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
    public java.util.List<PortObjectSpecEnt> getInputPortSpecs(@JsonRpcParam(value="jobId") java.util.UUID jobId, @JsonRpcParam(value="nodeId") com.knime.gateway.entity.NodeIDEnt nodeId)  throws ServiceExceptions.NodeNotFoundException {
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
    public NodeEnt getNode(@JsonRpcParam(value="jobId") java.util.UUID jobId, @JsonRpcParam(value="nodeId") com.knime.gateway.entity.NodeIDEnt nodeId)  throws ServiceExceptions.NodeNotFoundException {
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
    public NodeSettingsEnt getNodeSettings(@JsonRpcParam(value="jobId") java.util.UUID jobId, @JsonRpcParam(value="nodeId") com.knime.gateway.entity.NodeIDEnt nodeId)  throws ServiceExceptions.NodeNotFoundException {
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
    public DataTableEnt getOutputDataTable(@JsonRpcParam(value="jobId") java.util.UUID jobId, @JsonRpcParam(value="nodeId") com.knime.gateway.entity.NodeIDEnt nodeId, @JsonRpcParam(value="portIdx") Integer portIdx, Long from, Integer size)  throws ServiceExceptions.NodeNotFoundException, ServiceExceptions.InvalidRequestException {
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
    public java.util.List<FlowVariableEnt> getOutputFlowVariables(@JsonRpcParam(value="jobId") java.util.UUID jobId, @JsonRpcParam(value="nodeId") com.knime.gateway.entity.NodeIDEnt nodeId)  throws ServiceExceptions.NodeNotFoundException {
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
    public java.util.List<PortObjectSpecEnt> getOutputPortSpecs(@JsonRpcParam(value="jobId") java.util.UUID jobId, @JsonRpcParam(value="nodeId") com.knime.gateway.entity.NodeIDEnt nodeId)  throws ServiceExceptions.NodeNotFoundException {
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
    public ViewDataEnt getViewData(@JsonRpcParam(value="jobId") java.util.UUID jobId, @JsonRpcParam(value="nodeId") com.knime.gateway.entity.NodeIDEnt nodeId)  throws ServiceExceptions.NodeNotFoundException, ServiceExceptions.InvalidRequestException {
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
    public MetaNodeDialogEnt getWMetaNodeDialog(@JsonRpcParam(value="jobId") java.util.UUID jobId, @JsonRpcParam(value="nodeId") com.knime.gateway.entity.NodeIDEnt nodeId)  throws ServiceExceptions.NodeNotFoundException, ServiceExceptions.InvalidRequestException {
        return m_service.getWMetaNodeDialog(jobId, nodeId);    
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
    public void setNodeBounds(@JsonRpcParam(value="jobId") java.util.UUID jobId, @JsonRpcParam(value="nodeId") com.knime.gateway.entity.NodeIDEnt nodeId, @JsonRpcParam(value="boundsEnt") BoundsEnt boundsEnt)  throws ServiceExceptions.NodeNotFoundException {
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
    public void setNodeSettings(@JsonRpcParam(value="jobId") java.util.UUID jobId, @JsonRpcParam(value="nodeId") com.knime.gateway.entity.NodeIDEnt nodeId, @JsonRpcParam(value="nodeSettingsEnt") NodeSettingsEnt nodeSettingsEnt)  throws ServiceExceptions.NodeNotFoundException, ServiceExceptions.InvalidSettingsException, ServiceExceptions.IllegalStateException {
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
    public void setViewValue(@JsonRpcParam(value="jobId") java.util.UUID jobId, @JsonRpcParam(value="nodeId") com.knime.gateway.entity.NodeIDEnt nodeId, Boolean useAsDefault, @JsonRpcParam(value="javaObjectEnt") JavaObjectEnt javaObjectEnt)  throws ServiceExceptions.NodeNotFoundException, ServiceExceptions.InvalidRequestException {
        m_service.setViewValue(jobId, nodeId, useAsDefault, javaObjectEnt);    
    }

}

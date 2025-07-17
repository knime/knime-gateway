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

import org.knime.gateway.api.webui.entity.NativeNodeDescriptionEnt;
import org.knime.gateway.api.webui.entity.NodeFactoryKeyEnt;

import com.googlecode.jsonrpc4j.JsonRpcError;
import com.googlecode.jsonrpc4j.JsonRpcErrors;
import com.googlecode.jsonrpc4j.JsonRpcMethod;
import com.googlecode.jsonrpc4j.JsonRpcParam;
import com.googlecode.jsonrpc4j.JsonRpcService;

import org.knime.gateway.api.webui.service.util.ServiceExceptions;

import org.knime.gateway.api.webui.service.NodeService;

/**
 * Json rpc annotated class that wraps another service and delegates the method calls. 
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
@JsonRpcService(value = "NodeService")
@jakarta.annotation.Generated(value = {"com.knime.gateway.codegen.GatewayCodegen", "src-gen/api/web-ui/configs/org.knime.gateway.impl.jsonrpc-config.json"})
public class JsonRpcNodeServiceWrapper implements NodeService {

    private final java.util.function.Supplier<NodeService> m_service;
    
    public JsonRpcNodeServiceWrapper(java.util.function.Supplier<NodeService> service) {
        m_service = service;
    }

	/**
     * {@inheritDoc}
     */
    @Override
    @JsonRpcMethod(value = "callNodeDataService")
    @JsonRpcErrors(value = {
        @JsonRpcError(exception = ServiceExceptions.InvalidRequestException.class, code = -32600,
            data = "InvalidRequestException" /*per convention the data property contains the exception name*/),
        @JsonRpcError(exception = ServiceExceptions.NodeNotFoundException.class, code = -32600,
            data = "NodeNotFoundException" /*per convention the data property contains the exception name*/)
    })
    public String callNodeDataService(@JsonRpcParam(value="projectId") String projectId, @JsonRpcParam(value="workflowId") org.knime.gateway.api.entity.NodeIDEnt workflowId, @JsonRpcParam(value="versionId") String versionId, @JsonRpcParam(value="nodeId") org.knime.gateway.api.entity.NodeIDEnt nodeId, @JsonRpcParam(value="extensionType") String extensionType, @JsonRpcParam(value="serviceType") String serviceType, @JsonRpcParam(value="dataServiceRequest") String dataServiceRequest)  throws ServiceExceptions.InvalidRequestException, ServiceExceptions.NodeNotFoundException {
        return m_service.get().callNodeDataService(projectId, workflowId, versionId, nodeId, extensionType, serviceType, dataServiceRequest);    
    }

	/**
     * {@inheritDoc}
     */
    @Override
    @JsonRpcMethod(value = "changeLoopState")
    @JsonRpcErrors(value = {
        @JsonRpcError(exception = ServiceExceptions.NodeNotFoundException.class, code = -32600,
            data = "NodeNotFoundException" /*per convention the data property contains the exception name*/),
        @JsonRpcError(exception = ServiceExceptions.OperationNotAllowedException.class, code = -32600,
            data = "OperationNotAllowedException" /*per convention the data property contains the exception name*/)
    })
    public void changeLoopState(@JsonRpcParam(value="projectId") String projectId, @JsonRpcParam(value="workflowId") org.knime.gateway.api.entity.NodeIDEnt workflowId, @JsonRpcParam(value="nodeId") org.knime.gateway.api.entity.NodeIDEnt nodeId, @JsonRpcParam(value="action") String action)  throws ServiceExceptions.NodeNotFoundException, ServiceExceptions.OperationNotAllowedException {
        m_service.get().changeLoopState(projectId, workflowId, nodeId, action);    
    }

	/**
     * {@inheritDoc}
     */
    @Override
    @JsonRpcMethod(value = "changeNodeStates")
    @JsonRpcErrors(value = {
        @JsonRpcError(exception = ServiceExceptions.NodeNotFoundException.class, code = -32600,
            data = "NodeNotFoundException" /*per convention the data property contains the exception name*/),
        @JsonRpcError(exception = ServiceExceptions.OperationNotAllowedException.class, code = -32600,
            data = "OperationNotAllowedException" /*per convention the data property contains the exception name*/)
    })
    public void changeNodeStates(@JsonRpcParam(value="projectId") String projectId, @JsonRpcParam(value="workflowId") org.knime.gateway.api.entity.NodeIDEnt workflowId, @JsonRpcParam(value="nodeIds") java.util.List<org.knime.gateway.api.entity.NodeIDEnt> nodeIds, @JsonRpcParam(value="action") String action)  throws ServiceExceptions.NodeNotFoundException, ServiceExceptions.OperationNotAllowedException {
        m_service.get().changeNodeStates(projectId, workflowId, nodeIds, action);    
    }

	/**
     * {@inheritDoc}
     */
    @Override
    @JsonRpcMethod(value = "deactivateNodeDataServices")
    @JsonRpcErrors(value = {
        @JsonRpcError(exception = ServiceExceptions.InvalidRequestException.class, code = -32600,
            data = "InvalidRequestException" /*per convention the data property contains the exception name*/),
        @JsonRpcError(exception = ServiceExceptions.NodeNotFoundException.class, code = -32600,
            data = "NodeNotFoundException" /*per convention the data property contains the exception name*/)
    })
    public void deactivateNodeDataServices(@JsonRpcParam(value="projectId") String projectId, @JsonRpcParam(value="workflowId") org.knime.gateway.api.entity.NodeIDEnt workflowId, @JsonRpcParam(value="versionId") String versionId, @JsonRpcParam(value="nodeId") org.knime.gateway.api.entity.NodeIDEnt nodeId, @JsonRpcParam(value="extensionType") String extensionType)  throws ServiceExceptions.InvalidRequestException, ServiceExceptions.NodeNotFoundException {
        m_service.get().deactivateNodeDataServices(projectId, workflowId, versionId, nodeId, extensionType);    
    }

	/**
     * {@inheritDoc}
     */
    @Override
    @JsonRpcMethod(value = "getNodeDescription")
    @JsonRpcErrors(value = {
        @JsonRpcError(exception = ServiceExceptions.NodeNotFoundException.class, code = -32600,
            data = "NodeNotFoundException" /*per convention the data property contains the exception name*/),
        @JsonRpcError(exception = ServiceExceptions.NodeDescriptionNotAvailableException.class, code = -32600,
            data = "NodeDescriptionNotAvailableException" /*per convention the data property contains the exception name*/)
    })
    public NativeNodeDescriptionEnt getNodeDescription(@JsonRpcParam(value="nodeFactoryKey") NodeFactoryKeyEnt nodeFactoryKey)  throws ServiceExceptions.NodeNotFoundException, ServiceExceptions.NodeDescriptionNotAvailableException {
        return m_service.get().getNodeDescription(nodeFactoryKey);    
    }

	/**
     * {@inheritDoc}
     */
    @Override
    @JsonRpcMethod(value = "getNodeDialog")
    @JsonRpcErrors(value = {
        @JsonRpcError(exception = ServiceExceptions.InvalidRequestException.class, code = -32600,
            data = "InvalidRequestException" /*per convention the data property contains the exception name*/),
        @JsonRpcError(exception = ServiceExceptions.NodeNotFoundException.class, code = -32600,
            data = "NodeNotFoundException" /*per convention the data property contains the exception name*/)
    })
    public Object getNodeDialog(@JsonRpcParam(value="projectId") String projectId, @JsonRpcParam(value="workflowId") org.knime.gateway.api.entity.NodeIDEnt workflowId, @JsonRpcParam(value="versionId") String versionId, @JsonRpcParam(value="nodeId") org.knime.gateway.api.entity.NodeIDEnt nodeId)  throws ServiceExceptions.InvalidRequestException, ServiceExceptions.NodeNotFoundException {
        return m_service.get().getNodeDialog(projectId, workflowId, versionId, nodeId);    
    }

	/**
     * {@inheritDoc}
     */
    @Override
    @JsonRpcMethod(value = "getNodeView")
    @JsonRpcErrors(value = {
        @JsonRpcError(exception = ServiceExceptions.InvalidRequestException.class, code = -32600,
            data = "InvalidRequestException" /*per convention the data property contains the exception name*/),
        @JsonRpcError(exception = ServiceExceptions.NodeNotFoundException.class, code = -32600,
            data = "NodeNotFoundException" /*per convention the data property contains the exception name*/)
    })
    public Object getNodeView(@JsonRpcParam(value="projectId") String projectId, @JsonRpcParam(value="workflowId") org.knime.gateway.api.entity.NodeIDEnt workflowId, @JsonRpcParam(value="versionId") String versionId, @JsonRpcParam(value="nodeId") org.knime.gateway.api.entity.NodeIDEnt nodeId)  throws ServiceExceptions.InvalidRequestException, ServiceExceptions.NodeNotFoundException {
        return m_service.get().getNodeView(projectId, workflowId, versionId, nodeId);    
    }

	/**
     * {@inheritDoc}
     */
    @Override
    @JsonRpcMethod(value = "updateDataPointSelection")
    @JsonRpcErrors(value = {
        @JsonRpcError(exception = ServiceExceptions.NodeNotFoundException.class, code = -32600,
            data = "NodeNotFoundException" /*per convention the data property contains the exception name*/)
    })
    public void updateDataPointSelection(@JsonRpcParam(value="projectId") String projectId, @JsonRpcParam(value="workflowId") org.knime.gateway.api.entity.NodeIDEnt workflowId, @JsonRpcParam(value="versionId") String versionId, @JsonRpcParam(value="nodeId") org.knime.gateway.api.entity.NodeIDEnt nodeId, @JsonRpcParam(value="mode") String mode, @JsonRpcParam(value="selection") java.util.List<String> selection)  throws ServiceExceptions.NodeNotFoundException {
        m_service.get().updateDataPointSelection(projectId, workflowId, versionId, nodeId, mode, selection);    
    }

}

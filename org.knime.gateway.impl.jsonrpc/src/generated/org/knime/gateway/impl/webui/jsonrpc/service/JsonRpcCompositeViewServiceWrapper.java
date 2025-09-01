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


import com.googlecode.jsonrpc4j.JsonRpcError;
import com.googlecode.jsonrpc4j.JsonRpcErrors;
import com.googlecode.jsonrpc4j.JsonRpcMethod;
import com.googlecode.jsonrpc4j.JsonRpcParam;
import com.googlecode.jsonrpc4j.JsonRpcService;

import org.knime.gateway.api.webui.service.util.ServiceExceptions;

import org.knime.gateway.api.webui.service.CompositeViewService;

/**
 * Json rpc annotated class that wraps another service and delegates the method calls. 
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
@JsonRpcService(value = "CompositeViewService")
@jakarta.annotation.Generated(value = {"com.knime.gateway.codegen.GatewayCodegen", "src-gen/api/web-ui/configs/org.knime.gateway.impl.jsonrpc-config.json"})
public class JsonRpcCompositeViewServiceWrapper implements CompositeViewService {

    private final java.util.function.Supplier<CompositeViewService> m_service;
    
    public JsonRpcCompositeViewServiceWrapper(java.util.function.Supplier<CompositeViewService> service) {
        m_service = service;
    }

	/**
     * {@inheritDoc}
     */
    @Override
    @JsonRpcMethod(value = "deactivateAllCompositeViewDataServices")
    @JsonRpcErrors(value = {
        @JsonRpcError(exception = ServiceExceptions.ServiceCallException.class, code = -32600,
            data = "ServiceCallException" /*per convention the data property contains the exception name*/),
        @JsonRpcError(exception = ServiceExceptions.NodeNotFoundException.class, code = -32600,
            data = "NodeNotFoundException" /*per convention the data property contains the exception name*/)
    })
    public void deactivateAllCompositeViewDataServices(@JsonRpcParam(value="projectId") String projectId, @JsonRpcParam(value="workflowId") org.knime.gateway.api.entity.NodeIDEnt workflowId, @JsonRpcParam(value="nodeId") org.knime.gateway.api.entity.NodeIDEnt nodeId)  throws ServiceExceptions.ServiceCallException, ServiceExceptions.NodeNotFoundException {
        m_service.get().deactivateAllCompositeViewDataServices(projectId, workflowId, nodeId);    
    }

	/**
     * {@inheritDoc}
     */
    @Override
    @JsonRpcMethod(value = "getCompositeViewPage")
    @JsonRpcErrors(value = {
        @JsonRpcError(exception = ServiceExceptions.ServiceCallException.class, code = -32600,
            data = "ServiceCallException" /*per convention the data property contains the exception name*/),
        @JsonRpcError(exception = ServiceExceptions.NodeNotFoundException.class, code = -32600,
            data = "NodeNotFoundException" /*per convention the data property contains the exception name*/)
    })
    public Object getCompositeViewPage(@JsonRpcParam(value="projectId") String projectId, @JsonRpcParam(value="workflowId") org.knime.gateway.api.entity.NodeIDEnt workflowId, @JsonRpcParam(value="versionId") String versionId, @JsonRpcParam(value="nodeId") org.knime.gateway.api.entity.NodeIDEnt nodeId)  throws ServiceExceptions.ServiceCallException, ServiceExceptions.NodeNotFoundException {
        return m_service.get().getCompositeViewPage(projectId, workflowId, versionId, nodeId);    
    }

	/**
     * {@inheritDoc}
     */
    @Override
    @JsonRpcMethod(value = "pollCompleteComponentReexecutionStatus")
    @JsonRpcErrors(value = {
        @JsonRpcError(exception = ServiceExceptions.ServiceCallException.class, code = -32600,
            data = "ServiceCallException" /*per convention the data property contains the exception name*/),
        @JsonRpcError(exception = ServiceExceptions.NodeNotFoundException.class, code = -32600,
            data = "NodeNotFoundException" /*per convention the data property contains the exception name*/)
    })
    public Object pollCompleteComponentReexecutionStatus(@JsonRpcParam(value="projectId") String projectId, @JsonRpcParam(value="workflowId") org.knime.gateway.api.entity.NodeIDEnt workflowId, @JsonRpcParam(value="nodeId") org.knime.gateway.api.entity.NodeIDEnt nodeId)  throws ServiceExceptions.ServiceCallException, ServiceExceptions.NodeNotFoundException {
        return m_service.get().pollCompleteComponentReexecutionStatus(projectId, workflowId, nodeId);    
    }

	/**
     * {@inheritDoc}
     */
    @Override
    @JsonRpcMethod(value = "pollComponentReexecutionStatus")
    @JsonRpcErrors(value = {
        @JsonRpcError(exception = ServiceExceptions.ServiceCallException.class, code = -32600,
            data = "ServiceCallException" /*per convention the data property contains the exception name*/),
        @JsonRpcError(exception = ServiceExceptions.NodeNotFoundException.class, code = -32600,
            data = "NodeNotFoundException" /*per convention the data property contains the exception name*/)
    })
    public Object pollComponentReexecutionStatus(@JsonRpcParam(value="projectId") String projectId, @JsonRpcParam(value="workflowId") org.knime.gateway.api.entity.NodeIDEnt workflowId, @JsonRpcParam(value="nodeId") org.knime.gateway.api.entity.NodeIDEnt nodeId, @JsonRpcParam(value="resetNodeIdSuffix") String resetNodeIdSuffix)  throws ServiceExceptions.ServiceCallException, ServiceExceptions.NodeNotFoundException {
        return m_service.get().pollComponentReexecutionStatus(projectId, workflowId, nodeId, resetNodeIdSuffix);    
    }

	/**
     * {@inheritDoc}
     */
    @Override
    @JsonRpcMethod(value = "setViewValuesAsNewDefault")
    @JsonRpcErrors(value = {
        @JsonRpcError(exception = ServiceExceptions.ServiceCallException.class, code = -32600,
            data = "ServiceCallException" /*per convention the data property contains the exception name*/),
        @JsonRpcError(exception = ServiceExceptions.NodeNotFoundException.class, code = -32600,
            data = "NodeNotFoundException" /*per convention the data property contains the exception name*/)
    })
    public void setViewValuesAsNewDefault(@JsonRpcParam(value="projectId") String projectId, @JsonRpcParam(value="workflowId") org.knime.gateway.api.entity.NodeIDEnt workflowId, @JsonRpcParam(value="nodeId") org.knime.gateway.api.entity.NodeIDEnt nodeId, @JsonRpcParam(value="viewValues") java.util.Map<String, String> viewValues)  throws ServiceExceptions.ServiceCallException, ServiceExceptions.NodeNotFoundException {
        m_service.get().setViewValuesAsNewDefault(projectId, workflowId, nodeId, viewValues);    
    }

	/**
     * {@inheritDoc}
     */
    @Override
    @JsonRpcMethod(value = "triggerCompleteComponentReexecution")
    @JsonRpcErrors(value = {
        @JsonRpcError(exception = ServiceExceptions.ServiceCallException.class, code = -32600,
            data = "ServiceCallException" /*per convention the data property contains the exception name*/),
        @JsonRpcError(exception = ServiceExceptions.NodeNotFoundException.class, code = -32600,
            data = "NodeNotFoundException" /*per convention the data property contains the exception name*/)
    })
    public Object triggerCompleteComponentReexecution(@JsonRpcParam(value="projectId") String projectId, @JsonRpcParam(value="workflowId") org.knime.gateway.api.entity.NodeIDEnt workflowId, @JsonRpcParam(value="nodeId") org.knime.gateway.api.entity.NodeIDEnt nodeId, @JsonRpcParam(value="viewValues") java.util.Map<String, String> viewValues)  throws ServiceExceptions.ServiceCallException, ServiceExceptions.NodeNotFoundException {
        return m_service.get().triggerCompleteComponentReexecution(projectId, workflowId, nodeId, viewValues);    
    }

	/**
     * {@inheritDoc}
     */
    @Override
    @JsonRpcMethod(value = "triggerComponentReexecution")
    @JsonRpcErrors(value = {
        @JsonRpcError(exception = ServiceExceptions.ServiceCallException.class, code = -32600,
            data = "ServiceCallException" /*per convention the data property contains the exception name*/),
        @JsonRpcError(exception = ServiceExceptions.NodeNotFoundException.class, code = -32600,
            data = "NodeNotFoundException" /*per convention the data property contains the exception name*/)
    })
    public Object triggerComponentReexecution(@JsonRpcParam(value="projectId") String projectId, @JsonRpcParam(value="workflowId") org.knime.gateway.api.entity.NodeIDEnt workflowId, @JsonRpcParam(value="nodeId") org.knime.gateway.api.entity.NodeIDEnt nodeId, @JsonRpcParam(value="resetNodeIdSuffix") String resetNodeIdSuffix, @JsonRpcParam(value="viewValues") java.util.Map<String, String> viewValues)  throws ServiceExceptions.ServiceCallException, ServiceExceptions.NodeNotFoundException {
        return m_service.get().triggerComponentReexecution(projectId, workflowId, nodeId, resetNodeIdSuffix, viewValues);    
    }

}

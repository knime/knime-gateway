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

import org.knime.gateway.api.webui.service.PortService;

/**
 * Json rpc annotated class that wraps another service and delegates the method calls. 
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
@JsonRpcService(value = "PortService")
@javax.annotation.Generated(value = {"com.knime.gateway.codegen.GatewayCodegen", "src-gen/api/web-ui/configs/org.knime.gateway.impl.jsonrpc-config.json"})
public class JsonRpcPortServiceWrapper implements PortService {

    private final java.util.function.Supplier<PortService> m_service;
    
    public JsonRpcPortServiceWrapper(java.util.function.Supplier<PortService> service) {
        m_service = service;
    }

	/**
     * {@inheritDoc}
     */
    @Override
    @JsonRpcMethod(value = "callPortDataService")
    @JsonRpcErrors(value = {
        @JsonRpcError(exception = ServiceExceptions.NodeNotFoundException.class, code = -32600,
            data = "NodeNotFoundException" /*per convention the data property contains the exception name*/),
        @JsonRpcError(exception = ServiceExceptions.InvalidRequestException.class, code = -32600,
            data = "InvalidRequestException" /*per convention the data property contains the exception name*/)
    })
    public String callPortDataService(@JsonRpcParam(value="projectId") String projectId, @JsonRpcParam(value="workflowId") org.knime.gateway.api.entity.NodeIDEnt workflowId, @JsonRpcParam(value="nodeId") org.knime.gateway.api.entity.NodeIDEnt nodeId, @JsonRpcParam(value="portIdx") Integer portIdx, @JsonRpcParam(value="serviceType") String serviceType, @JsonRpcParam(value="body") String body)  throws ServiceExceptions.NodeNotFoundException, ServiceExceptions.InvalidRequestException {
        return m_service.get().callPortDataService(projectId, workflowId, nodeId, portIdx, serviceType, body);    
    }

	/**
     * {@inheritDoc}
     */
    @Override
    @JsonRpcMethod(value = "getPortView")
    @JsonRpcErrors(value = {
        @JsonRpcError(exception = ServiceExceptions.NodeNotFoundException.class, code = -32600,
            data = "NodeNotFoundException" /*per convention the data property contains the exception name*/),
        @JsonRpcError(exception = ServiceExceptions.InvalidRequestException.class, code = -32600,
            data = "InvalidRequestException" /*per convention the data property contains the exception name*/)
    })
    public Object getPortView(@JsonRpcParam(value="projectId") String projectId, @JsonRpcParam(value="workflowId") org.knime.gateway.api.entity.NodeIDEnt workflowId, @JsonRpcParam(value="nodeId") org.knime.gateway.api.entity.NodeIDEnt nodeId, @JsonRpcParam(value="portIdx") Integer portIdx)  throws ServiceExceptions.NodeNotFoundException, ServiceExceptions.InvalidRequestException {
        return m_service.get().getPortView(projectId, workflowId, nodeId, portIdx);    
    }

}
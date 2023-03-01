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

import org.knime.gateway.api.webui.entity.NodeGroupsEnt;
import org.knime.gateway.api.webui.entity.NodeSearchResultEnt;
import org.knime.gateway.api.webui.entity.NodeTemplateEnt;

import com.googlecode.jsonrpc4j.JsonRpcError;
import com.googlecode.jsonrpc4j.JsonRpcErrors;
import com.googlecode.jsonrpc4j.JsonRpcMethod;
import com.googlecode.jsonrpc4j.JsonRpcParam;
import com.googlecode.jsonrpc4j.JsonRpcService;

import org.knime.gateway.api.webui.service.util.ServiceExceptions;

import org.knime.gateway.api.webui.service.NodeRepositoryService;

/**
 * Json rpc annotated class that wraps another service and delegates the method calls. 
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
@JsonRpcService(value = "NodeRepositoryService")
@javax.annotation.Generated(value = {"com.knime.gateway.codegen.GatewayCodegen", "src-gen/api/web-ui/configs/org.knime.gateway.impl.jsonrpc-config.json"})
public class JsonRpcNodeRepositoryServiceWrapper implements NodeRepositoryService {

    private final java.util.function.Supplier<NodeRepositoryService> m_service;
    
    public JsonRpcNodeRepositoryServiceWrapper(java.util.function.Supplier<NodeRepositoryService> service) {
        m_service = service;
    }

	/**
     * {@inheritDoc}
     */
    @Override
    @JsonRpcMethod(value = "getNodeRecommendations")
    @JsonRpcErrors(value = {
        @JsonRpcError(exception = ServiceExceptions.OperationNotAllowedException.class, code = -32600,
            data = "OperationNotAllowedException" /*per convention the data property contains the exception name*/)
    })
    public java.util.List<NodeTemplateEnt> getNodeRecommendations(@JsonRpcParam(value="projectId") String projectId, @JsonRpcParam(value="workflowId") org.knime.gateway.api.entity.NodeIDEnt workflowId, @JsonRpcParam(value="nodeId") org.knime.gateway.api.entity.NodeIDEnt nodeId, @JsonRpcParam(value="portIdx") Integer portIdx, Integer nodesLimit, Boolean fullTemplateInfo)  throws ServiceExceptions.OperationNotAllowedException {
        return m_service.get().getNodeRecommendations(projectId, workflowId, nodeId, portIdx, nodesLimit, fullTemplateInfo);    
    }

	/**
     * {@inheritDoc}
     */
    @Override
    @JsonRpcMethod(value = "getNodeTemplates")
    public java.util.Map<String, NodeTemplateEnt> getNodeTemplates(@JsonRpcParam(value="requestBody") java.util.List<String> requestBody)  {
        return m_service.get().getNodeTemplates(requestBody);    
    }

	/**
     * {@inheritDoc}
     */
    @Override
    @JsonRpcMethod(value = "getNodesGroupedByTags")
    public NodeGroupsEnt getNodesGroupedByTags(Integer numNodesPerTag, Integer tagsOffset, Integer tagsLimit, Boolean fullTemplateInfo)  {
        return m_service.get().getNodesGroupedByTags(numNodesPerTag, tagsOffset, tagsLimit, fullTemplateInfo);    
    }

	/**
     * {@inheritDoc}
     */
    @Override
    @JsonRpcMethod(value = "searchNodes")
    public NodeSearchResultEnt searchNodes(String q, java.util.List<String> tags, Boolean allTagsMatch, Integer nodesOffset, Integer nodesLimit, Boolean fullTemplateInfo, Boolean additionalNodes)  {
        return m_service.get().searchNodes(q, tags, allTagsMatch, nodesOffset, nodesLimit, fullTemplateInfo, additionalNodes);    
    }

}

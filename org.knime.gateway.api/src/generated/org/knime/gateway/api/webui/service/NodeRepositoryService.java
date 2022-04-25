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
package org.knime.gateway.api.webui.service;

import org.knime.gateway.api.service.GatewayService;
import org.knime.gateway.api.webui.service.util.ServiceExceptions;

import org.knime.gateway.api.webui.entity.NodeGroupsEnt;
import org.knime.gateway.api.webui.entity.NodeSearchResultEnt;
import org.knime.gateway.api.webui.entity.NodeTemplateEnt;

/**
 * 
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
@javax.annotation.Generated(value = {"com.knime.gateway.codegen.GatewayCodegen", "src-gen/api/web-ui/configs/org.knime.gateway.api-config.json"})
public interface NodeRepositoryService extends GatewayService {

    /**
     * Compiles a list of node templates (with complete information, i.e. including icons, etc.). It doesn&#39;t actually change any state or create a new resource (despite the &#39;post&#39;).
     *
     * @param requestBody A list of template ids to request the node templates for.
     *
     * @return the result
     */
    java.util.Map<String, NodeTemplateEnt> getNodeTemplates(java.util.List<String> requestBody) ;
        
    /**
     * Returns a pre-defined set of groups (defined by tags) and nodes per group (the most frequently used ones in that group).
     *
     * @param numNodesPerTag The number of nodes per tag/group to be returned.
     * @param tagsOffset The number of tags to be skipped (for pagination).
     * @param tagsLimit The maximum number of tags to be returned (mainly for pagination).
     * @param fullTemplateInfo If true, the result will contain the full information for nodes/components (such as icon and port information). Otherwise only minimal information (such as name) will be included and the others omitted.
     *
     * @return the result
     */
    NodeGroupsEnt getNodesGroupedByTags(Integer numNodesPerTag, Integer tagsOffset, Integer tagsLimit, Boolean fullTemplateInfo) ;
        
    /**
     * Searches for nodes (and components) in the node repository.
     *
     * @param q The term to search for.
     * @param tags A list of tags. Only nodes/components having any/all tags will be included in the search result.
     * @param allTagsMatch If true, only the nodes/components that have all of the given tags are included in the search result. Otherwise nodes/components that have at least one of the given tags are included.
     * @param nodesOffset Number of nodes/components to be skipped in the search result (for pagination).
     * @param nodesLimit The maximum number of nodes/components in the search result (mainly for pagination).
     * @param fullTemplateInfo If true, the search result will contain the full information for nodes/components (such as icon and port information). Otherwise only minimal information (such as name) will be included and the others omitted.
     *
     * @return the result
     */
    NodeSearchResultEnt searchNodes(String q, java.util.List<String> tags, Boolean allTagsMatch, Integer nodesOffset, Integer nodesLimit, Boolean fullTemplateInfo) ;
        
}

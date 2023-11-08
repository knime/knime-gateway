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
package org.knime.gateway.impl.webui.entity;

import static org.knime.gateway.api.util.EntityUtil.immutable;

import org.knime.gateway.api.webui.entity.NodeTemplateEnt;

import org.knime.gateway.api.webui.entity.NodeSearchResultEnt;

/**
 * Represents the result of a node/component search in the node repository.
 *
 * @param nodes
 * @param totalNumNodes
 * @param totalNonPartitionNodes
 * @param tags
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
@jakarta.annotation.Generated(value = {"com.knime.gateway.codegen.GatewayCodegen", "src-gen/api/web-ui/configs/org.knime.gateway.impl-config.json"})
public record DefaultNodeSearchResultEnt(
    java.util.List<NodeTemplateEnt> nodes,
    Integer totalNumNodes,
    Integer totalNonPartitionNodes,
    java.util.List<String> tags) implements NodeSearchResultEnt {

    /**
     * Validation for required parameters not being {@code null}.
     */
    public DefaultNodeSearchResultEnt {
        if(nodes == null) {
            throw new IllegalArgumentException("<nodes> must not be null.");
        }
        if(totalNumNodes == null) {
            throw new IllegalArgumentException("<totalNumNodes> must not be null.");
        }
        if(totalNonPartitionNodes == null) {
            throw new IllegalArgumentException("<totalNonPartitionNodes> must not be null.");
        }
        if(tags == null) {
            throw new IllegalArgumentException("<tags> must not be null.");
        }
    }

    @Override
    public String getTypeID() {
        return "NodeSearchResult";
    }
  
    @Override
    public java.util.List<NodeTemplateEnt> getNodes() {
        return nodes;
    }
    
    @Override
    public Integer getTotalNumNodes() {
        return totalNumNodes;
    }
    
    @Override
    public Integer getTotalNonPartitionNodes() {
        return totalNonPartitionNodes;
    }
    
    @Override
    public java.util.List<String> getTags() {
        return tags;
    }
    
    /**
     * A builder for {@link DefaultNodeSearchResultEnt}.
     */
    public static class DefaultNodeSearchResultEntBuilder implements NodeSearchResultEntBuilder {

        private java.util.List<NodeTemplateEnt> m_nodes = new java.util.ArrayList<>();

        private Integer m_totalNumNodes;

        private Integer m_totalNonPartitionNodes;

        private java.util.List<String> m_tags = new java.util.ArrayList<>();

        @Override
        public DefaultNodeSearchResultEntBuilder setNodes(java.util.List<NodeTemplateEnt> nodes) {
             if(nodes == null) {
                 throw new IllegalArgumentException("<nodes> must not be null.");
             }
             m_nodes = nodes;
             return this;
        }

        @Override
        public DefaultNodeSearchResultEntBuilder setTotalNumNodes(Integer totalNumNodes) {
             if(totalNumNodes == null) {
                 throw new IllegalArgumentException("<totalNumNodes> must not be null.");
             }
             m_totalNumNodes = totalNumNodes;
             return this;
        }

        @Override
        public DefaultNodeSearchResultEntBuilder setTotalNonPartitionNodes(Integer totalNonPartitionNodes) {
             if(totalNonPartitionNodes == null) {
                 throw new IllegalArgumentException("<totalNonPartitionNodes> must not be null.");
             }
             m_totalNonPartitionNodes = totalNonPartitionNodes;
             return this;
        }

        @Override
        public DefaultNodeSearchResultEntBuilder setTags(java.util.List<String> tags) {
             if(tags == null) {
                 throw new IllegalArgumentException("<tags> must not be null.");
             }
             m_tags = tags;
             return this;
        }

        @Override
        public DefaultNodeSearchResultEnt build() {
            return new DefaultNodeSearchResultEnt(
                immutable(m_nodes),
                immutable(m_totalNumNodes),
                immutable(m_totalNonPartitionNodes),
                immutable(m_tags));
        }
    
    }

}

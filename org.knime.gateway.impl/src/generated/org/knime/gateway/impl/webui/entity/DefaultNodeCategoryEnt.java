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

import org.knime.gateway.api.webui.entity.CategoryMetadataEnt;
import org.knime.gateway.api.webui.entity.NodeTemplateEnt;

import org.knime.gateway.api.webui.entity.NodeCategoryEnt;

/**
 * A category of nodes, including metadata, directly contained nodes, and child categories.
 *
 * @param metadata
 * @param nodes
 * @param childCategories
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
@jakarta.annotation.Generated(value = {"com.knime.gateway.codegen.GatewayCodegen", "src-gen/api/web-ui/configs/org.knime.gateway.impl-config.json"})
public record DefaultNodeCategoryEnt(
    CategoryMetadataEnt metadata,
    java.util.List<NodeTemplateEnt> nodes,
    java.util.List<CategoryMetadataEnt> childCategories) implements NodeCategoryEnt {

    /**
     * Validation for required parameters not being {@code null}.
     */
    public DefaultNodeCategoryEnt {
    }

    @Override
    public String getTypeID() {
        return "NodeCategory";
    }
  
    @Override
    public CategoryMetadataEnt getMetadata() {
        return metadata;
    }
    
    @Override
    public java.util.List<NodeTemplateEnt> getNodes() {
        return nodes;
    }
    
    @Override
    public java.util.List<CategoryMetadataEnt> getChildCategories() {
        return childCategories;
    }
    
    /**
     * A builder for {@link DefaultNodeCategoryEnt}.
     */
    public static class DefaultNodeCategoryEntBuilder implements NodeCategoryEntBuilder {

        private CategoryMetadataEnt m_metadata;

        private java.util.List<NodeTemplateEnt> m_nodes;

        private java.util.List<CategoryMetadataEnt> m_childCategories;

        @Override
        public DefaultNodeCategoryEntBuilder setMetadata(CategoryMetadataEnt metadata) {
             m_metadata = metadata;
             return this;
        }

        @Override
        public DefaultNodeCategoryEntBuilder setNodes(java.util.List<NodeTemplateEnt> nodes) {
             m_nodes = nodes;
             return this;
        }

        @Override
        public DefaultNodeCategoryEntBuilder setChildCategories(java.util.List<CategoryMetadataEnt> childCategories) {
             m_childCategories = childCategories;
             return this;
        }

        @Override
        public DefaultNodeCategoryEnt build() {
            return new DefaultNodeCategoryEnt(
                immutable(m_metadata),
                immutable(m_nodes),
                immutable(m_childCategories));
        }
    
    }

}

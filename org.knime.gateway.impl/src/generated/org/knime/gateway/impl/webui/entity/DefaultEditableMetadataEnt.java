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

import org.knime.gateway.api.webui.entity.LinkEnt;
import org.knime.gateway.api.webui.entity.TypedTextEnt;

import org.knime.gateway.api.webui.entity.EditableMetadataEnt;

/**
 * Metadata properties of a workflow (project or component) that can be edited
 *
 * @param description
 * @param metadataType
 * @param tags
 * @param links
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
@jakarta.annotation.Generated(value = {"com.knime.gateway.codegen.GatewayCodegen", "src-gen/api/web-ui/configs/org.knime.gateway.impl-config.json"})
public record DefaultEditableMetadataEnt(
    TypedTextEnt description,
    MetadataTypeEnum metadataType,
    java.util.List<String> tags,
    java.util.List<LinkEnt> links) implements EditableMetadataEnt {

    /**
     * Validation for required parameters not being {@code null}.
     */
    public DefaultEditableMetadataEnt {
        if(metadataType == null) {
            throw new IllegalArgumentException("<metadataType> must not be null.");
        }
    }

    @Override
    public String getTypeID() {
        return "EditableMetadata";
    }
  
    @Override
    public TypedTextEnt getDescription() {
        return description;
    }
    
    @Override
    public MetadataTypeEnum getMetadataType() {
        return metadataType;
    }
    
    @Override
    public java.util.List<String> getTags() {
        return tags;
    }
    
    @Override
    public java.util.List<LinkEnt> getLinks() {
        return links;
    }
    
    /**
     * A builder for {@link DefaultEditableMetadataEnt}.
     */
    public static class DefaultEditableMetadataEntBuilder implements EditableMetadataEntBuilder {

        private TypedTextEnt m_description;

        private MetadataTypeEnum m_metadataType;

        private java.util.List<String> m_tags;

        private java.util.List<LinkEnt> m_links;

        @Override
        public DefaultEditableMetadataEntBuilder setDescription(TypedTextEnt description) {
             m_description = description;
             return this;
        }

        @Override
        public DefaultEditableMetadataEntBuilder setMetadataType(MetadataTypeEnum metadataType) {
             if(metadataType == null) {
                 throw new IllegalArgumentException("<metadataType> must not be null.");
             }
             m_metadataType = metadataType;
             return this;
        }

        @Override
        public DefaultEditableMetadataEntBuilder setTags(java.util.List<String> tags) {
             m_tags = tags;
             return this;
        }

        @Override
        public DefaultEditableMetadataEntBuilder setLinks(java.util.List<LinkEnt> links) {
             m_links = links;
             return this;
        }

        @Override
        public DefaultEditableMetadataEnt build() {
            return new DefaultEditableMetadataEnt(
                immutable(m_description),
                immutable(m_metadataType),
                immutable(m_tags),
                immutable(m_links));
        }
    
    }

}

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

import java.time.OffsetDateTime;
import org.knime.gateway.api.webui.entity.LinkEnt;
import org.knime.gateway.api.webui.entity.NodeDialogOptionGroupEnt;
import org.knime.gateway.api.webui.entity.NodePortDescriptionEnt;
import org.knime.gateway.api.webui.entity.NodeViewDescriptionEnt;
import org.knime.gateway.api.webui.entity.TypedTextEnt;
import org.knime.gateway.impl.webui.entity.DefaultComponentNodeAndDescriptionEnt;
import org.knime.gateway.impl.webui.entity.DefaultNodeDescriptionEnt;
import org.knime.gateway.impl.webui.entity.DefaultProjectMetadataEnt;

import org.knime.gateway.api.webui.entity.ComponentNodeDescriptionEnt;

/**
 * Description of certain aspects of a component. This is static information for a component which remains the same even if component is not part of a workflow.
 *
 * @param name
 * @param type
 * @param icon
 * @param options
 * @param views
 * @param inPorts
 * @param outPorts
 * @param description
 * @param tags
 * @param links
 * @param lastEdit
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
@jakarta.annotation.Generated(value = {"com.knime.gateway.codegen.GatewayCodegen", "src-gen/api/web-ui/configs/org.knime.gateway.impl-config.json"})
public record DefaultComponentNodeDescriptionEnt(
    String name,
    TypeEnum type,
    String icon,
    java.util.List<NodeDialogOptionGroupEnt> options,
    java.util.List<NodeViewDescriptionEnt> views,
    java.util.List<NodePortDescriptionEnt> inPorts,
    java.util.List<NodePortDescriptionEnt> outPorts,
    TypedTextEnt description,
    java.util.List<String> tags,
    java.util.List<LinkEnt> links,
    OffsetDateTime lastEdit) implements ComponentNodeDescriptionEnt {

    /**
     * Validation for required parameters not being {@code null}.
     */
    public DefaultComponentNodeDescriptionEnt {
        if(name == null) {
            throw new IllegalArgumentException("<name> must not be null.");
        }
    }

    @Override
    public String getTypeID() {
        return "ComponentNodeDescription";
    }
  
    @Override
    public String getName() {
        return name;
    }
    
    @Override
    public TypeEnum getType() {
        return type;
    }
    
    @Override
    public String getIcon() {
        return icon;
    }
    
    @Override
    public java.util.List<NodeDialogOptionGroupEnt> getOptions() {
        return options;
    }
    
    @Override
    public java.util.List<NodeViewDescriptionEnt> getViews() {
        return views;
    }
    
    @Override
    public java.util.List<NodePortDescriptionEnt> getInPorts() {
        return inPorts;
    }
    
    @Override
    public java.util.List<NodePortDescriptionEnt> getOutPorts() {
        return outPorts;
    }
    
    @Override
    public TypedTextEnt getDescription() {
        return description;
    }
    
    @Override
    public java.util.List<String> getTags() {
        return tags;
    }
    
    @Override
    public java.util.List<LinkEnt> getLinks() {
        return links;
    }
    
    @Override
    public OffsetDateTime getLastEdit() {
        return lastEdit;
    }
    
    /**
     * A builder for {@link DefaultComponentNodeDescriptionEnt}.
     */
    public static class DefaultComponentNodeDescriptionEntBuilder implements ComponentNodeDescriptionEntBuilder {

        private String m_name;

        private TypeEnum m_type;

        private String m_icon;

        private java.util.List<NodeDialogOptionGroupEnt> m_options;

        private java.util.List<NodeViewDescriptionEnt> m_views;

        private java.util.List<NodePortDescriptionEnt> m_inPorts;

        private java.util.List<NodePortDescriptionEnt> m_outPorts;

        private TypedTextEnt m_description;

        private java.util.List<String> m_tags;

        private java.util.List<LinkEnt> m_links;

        private OffsetDateTime m_lastEdit;

        @Override
        public DefaultComponentNodeDescriptionEntBuilder setName(String name) {
             if(name == null) {
                 throw new IllegalArgumentException("<name> must not be null.");
             }
             m_name = name;
             return this;
        }

        @Override
        public DefaultComponentNodeDescriptionEntBuilder setType(TypeEnum type) {
             m_type = type;
             return this;
        }

        @Override
        public DefaultComponentNodeDescriptionEntBuilder setIcon(String icon) {
             m_icon = icon;
             return this;
        }

        @Override
        public DefaultComponentNodeDescriptionEntBuilder setOptions(java.util.List<NodeDialogOptionGroupEnt> options) {
             m_options = options;
             return this;
        }

        @Override
        public DefaultComponentNodeDescriptionEntBuilder setViews(java.util.List<NodeViewDescriptionEnt> views) {
             m_views = views;
             return this;
        }

        @Override
        public DefaultComponentNodeDescriptionEntBuilder setInPorts(java.util.List<NodePortDescriptionEnt> inPorts) {
             m_inPorts = inPorts;
             return this;
        }

        @Override
        public DefaultComponentNodeDescriptionEntBuilder setOutPorts(java.util.List<NodePortDescriptionEnt> outPorts) {
             m_outPorts = outPorts;
             return this;
        }

        @Override
        public DefaultComponentNodeDescriptionEntBuilder setDescription(TypedTextEnt description) {
             m_description = description;
             return this;
        }

        @Override
        public DefaultComponentNodeDescriptionEntBuilder setTags(java.util.List<String> tags) {
             m_tags = tags;
             return this;
        }

        @Override
        public DefaultComponentNodeDescriptionEntBuilder setLinks(java.util.List<LinkEnt> links) {
             m_links = links;
             return this;
        }

        @Override
        public DefaultComponentNodeDescriptionEntBuilder setLastEdit(OffsetDateTime lastEdit) {
             m_lastEdit = lastEdit;
             return this;
        }

        @Override
        public DefaultComponentNodeDescriptionEnt build() {
            return new DefaultComponentNodeDescriptionEnt(
                immutable(m_name),
                immutable(m_type),
                immutable(m_icon),
                immutable(m_options),
                immutable(m_views),
                immutable(m_inPorts),
                immutable(m_outPorts),
                immutable(m_description),
                immutable(m_tags),
                immutable(m_links),
                immutable(m_lastEdit));
        }
    
    }

}

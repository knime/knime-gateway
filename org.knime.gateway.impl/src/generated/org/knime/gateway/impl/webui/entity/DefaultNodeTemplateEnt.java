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

import org.knime.gateway.api.webui.entity.ExtensionEnt;
import org.knime.gateway.api.webui.entity.NodeFactoryKeyEnt;
import org.knime.gateway.api.webui.entity.NodePortTemplateEnt;
import org.knime.gateway.impl.webui.entity.DefaultNativeNodeInvariantsEnt;

import org.knime.gateway.api.webui.entity.NodeTemplateEnt;

/**
 * Contains all the &#39;static&#39; properties of a node or component required to draw the node/component figure.
 *
 * @param name
 * @param type
 * @param icon
 * @param nodeFactory
 * @param extension
 * @param id
 * @param component
 * @param inPorts
 * @param outPorts
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
@jakarta.annotation.Generated(value = {"com.knime.gateway.codegen.GatewayCodegen", "src-gen/api/web-ui/configs/org.knime.gateway.impl-config.json"})
public record DefaultNodeTemplateEnt(
    String name,
    TypeEnum type,
    String icon,
    NodeFactoryKeyEnt nodeFactory,
    ExtensionEnt extension,
    String id,
    Boolean component,
    java.util.List<NodePortTemplateEnt> inPorts,
    java.util.List<NodePortTemplateEnt> outPorts) implements NodeTemplateEnt {

    /**
     * Validation for required parameters not being {@code null}.
     */
    public DefaultNodeTemplateEnt {
        if(name == null) {
            throw new IllegalArgumentException("<name> must not be null.");
        }
        if(type == null) {
            throw new IllegalArgumentException("<type> must not be null.");
        }
        if(id == null) {
            throw new IllegalArgumentException("<id> must not be null.");
        }
    }

    @Override
    public String getTypeID() {
        return "NodeTemplate";
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
    public NodeFactoryKeyEnt getNodeFactory() {
        return nodeFactory;
    }
    
    @Override
    public ExtensionEnt getExtension() {
        return extension;
    }
    
    @Override
    public String getId() {
        return id;
    }
    
    @Override
    public Boolean isComponent() {
        return component;
    }
    
    @Override
    public java.util.List<NodePortTemplateEnt> getInPorts() {
        return inPorts;
    }
    
    @Override
    public java.util.List<NodePortTemplateEnt> getOutPorts() {
        return outPorts;
    }
    
    /**
     * A builder for {@link DefaultNodeTemplateEnt}.
     */
    public static class DefaultNodeTemplateEntBuilder implements NodeTemplateEntBuilder {

        private String m_name;

        private TypeEnum m_type;

        private String m_icon;

        private NodeFactoryKeyEnt m_nodeFactory;

        private ExtensionEnt m_extension;

        private String m_id;

        private Boolean m_component;

        private java.util.List<NodePortTemplateEnt> m_inPorts;

        private java.util.List<NodePortTemplateEnt> m_outPorts;

        @Override
        public DefaultNodeTemplateEntBuilder setName(String name) {
             if(name == null) {
                 throw new IllegalArgumentException("<name> must not be null.");
             }
             m_name = name;
             return this;
        }

        @Override
        public DefaultNodeTemplateEntBuilder setType(TypeEnum type) {
             if(type == null) {
                 throw new IllegalArgumentException("<type> must not be null.");
             }
             m_type = type;
             return this;
        }

        @Override
        public DefaultNodeTemplateEntBuilder setIcon(String icon) {
             m_icon = icon;
             return this;
        }

        @Override
        public DefaultNodeTemplateEntBuilder setNodeFactory(NodeFactoryKeyEnt nodeFactory) {
             m_nodeFactory = nodeFactory;
             return this;
        }

        @Override
        public DefaultNodeTemplateEntBuilder setExtension(ExtensionEnt extension) {
             m_extension = extension;
             return this;
        }

        @Override
        public DefaultNodeTemplateEntBuilder setId(String id) {
             if(id == null) {
                 throw new IllegalArgumentException("<id> must not be null.");
             }
             m_id = id;
             return this;
        }

        @Override
        public DefaultNodeTemplateEntBuilder setComponent(Boolean component) {
             m_component = component;
             return this;
        }

        @Override
        public DefaultNodeTemplateEntBuilder setInPorts(java.util.List<NodePortTemplateEnt> inPorts) {
             m_inPorts = inPorts;
             return this;
        }

        @Override
        public DefaultNodeTemplateEntBuilder setOutPorts(java.util.List<NodePortTemplateEnt> outPorts) {
             m_outPorts = outPorts;
             return this;
        }

        @Override
        public DefaultNodeTemplateEnt build() {
            return new DefaultNodeTemplateEnt(
                immutable(m_name),
                immutable(m_type),
                immutable(m_icon),
                immutable(m_nodeFactory),
                immutable(m_extension),
                immutable(m_id),
                immutable(m_component),
                immutable(m_inPorts),
                immutable(m_outPorts));
        }
    
    }

}

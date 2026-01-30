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

import org.knime.gateway.api.webui.entity.ComponentSearchItemPortEnt;

import org.knime.gateway.api.webui.entity.ComponentSearchItemEnt;

/**
 * A result item of a component search on some Hub instance.
 *
 * @param id
 * @param name
 * @param description
 * @param icon
 * @param type
 * @param inPorts
 * @param outPorts
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
@jakarta.annotation.Generated(value = {"com.knime.gateway.codegen.GatewayCodegen", "src-gen/api/web-ui/configs/org.knime.gateway.impl-config.json"})
public record DefaultComponentSearchItemEnt(
    String id,
    String name,
    String description,
    String icon,
    TypeEnum type,
    java.util.List<ComponentSearchItemPortEnt> inPorts,
    java.util.List<ComponentSearchItemPortEnt> outPorts) implements ComponentSearchItemEnt {

    /**
     * Validation for required parameters not being {@code null}.
     */
    public DefaultComponentSearchItemEnt {
        if(id == null) {
            throw new IllegalArgumentException("<id> must not be null.");
        }
        if(name == null) {
            throw new IllegalArgumentException("<name> must not be null.");
        }
    }

    @Override
    public String getTypeID() {
        return "ComponentSearchItem";
    }
  
    @Override
    public String getId() {
        return id;
    }
    
    @Override
    public String getName() {
        return name;
    }
    
    @Override
    public String getDescription() {
        return description;
    }
    
    @Override
    public String getIcon() {
        return icon;
    }
    
    @Override
    public TypeEnum getType() {
        return type;
    }
    
    @Override
    public java.util.List<ComponentSearchItemPortEnt> getInPorts() {
        return inPorts;
    }
    
    @Override
    public java.util.List<ComponentSearchItemPortEnt> getOutPorts() {
        return outPorts;
    }
    
    /**
     * A builder for {@link DefaultComponentSearchItemEnt}.
     */
    public static class DefaultComponentSearchItemEntBuilder implements ComponentSearchItemEntBuilder {

        private String m_id;

        private String m_name;

        private String m_description;

        private String m_icon;

        private TypeEnum m_type;

        private java.util.List<ComponentSearchItemPortEnt> m_inPorts;

        private java.util.List<ComponentSearchItemPortEnt> m_outPorts;

        @Override
        public DefaultComponentSearchItemEntBuilder setId(String id) {
             if(id == null) {
                 throw new IllegalArgumentException("<id> must not be null.");
             }
             m_id = id;
             return this;
        }

        @Override
        public DefaultComponentSearchItemEntBuilder setName(String name) {
             if(name == null) {
                 throw new IllegalArgumentException("<name> must not be null.");
             }
             m_name = name;
             return this;
        }

        @Override
        public DefaultComponentSearchItemEntBuilder setDescription(String description) {
             m_description = description;
             return this;
        }

        @Override
        public DefaultComponentSearchItemEntBuilder setIcon(String icon) {
             m_icon = icon;
             return this;
        }

        @Override
        public DefaultComponentSearchItemEntBuilder setType(TypeEnum type) {
             m_type = type;
             return this;
        }

        @Override
        public DefaultComponentSearchItemEntBuilder setInPorts(java.util.List<ComponentSearchItemPortEnt> inPorts) {
             m_inPorts = inPorts;
             return this;
        }

        @Override
        public DefaultComponentSearchItemEntBuilder setOutPorts(java.util.List<ComponentSearchItemPortEnt> outPorts) {
             m_outPorts = outPorts;
             return this;
        }

        @Override
        public DefaultComponentSearchItemEnt build() {
            return new DefaultComponentSearchItemEnt(
                immutable(m_id),
                immutable(m_name),
                immutable(m_description),
                immutable(m_icon),
                immutable(m_type),
                immutable(m_inPorts),
                immutable(m_outPorts));
        }
    
    }

}

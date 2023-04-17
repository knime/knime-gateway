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


import org.knime.gateway.api.webui.entity.PortTypeEnt;

/**
 * Decribes the type of a port.
 *
 * @param name
 * @param kind
 * @param color
 * @param compatibleTypes
 * @param hidden
 * @param portViews
 * @param portSpecViews
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
@javax.annotation.Generated(value = {"com.knime.gateway.codegen.GatewayCodegen", "src-gen/api/web-ui/configs/org.knime.gateway.impl-config.json"})
public record DefaultPortTypeEnt(
    String name,
    KindEnum kind,
    String color,
    java.util.List<String> compatibleTypes,
    Boolean hidden,
    java.util.List<String> portViews,
    java.util.List<String> portSpecViews) implements PortTypeEnt {

    /**
     * Validation for required parameters not being {@code null}.
     */
    public DefaultPortTypeEnt {
        if(name == null) {
            throw new IllegalArgumentException("<name> must not be null.");
        }
        if(kind == null) {
            throw new IllegalArgumentException("<kind> must not be null.");
        }
    }

    @Override
    public String getTypeID() {
        return "PortType";
    }
  
    @Override
    public String getName() {
        return name;
    }
    
    @Override
    public KindEnum getKind() {
        return kind;
    }
    
    @Override
    public String getColor() {
        return color;
    }
    
    @Override
    public java.util.List<String> getCompatibleTypes() {
        return compatibleTypes;
    }
    
    @Override
    public Boolean isHidden() {
        return hidden;
    }
    
    @Override
    public java.util.List<String> getPortViews() {
        return portViews;
    }
    
    @Override
    public java.util.List<String> getPortSpecViews() {
        return portSpecViews;
    }
    
    /**
     * A builder for {@link DefaultPortTypeEnt}.
     */
    public static class DefaultPortTypeEntBuilder implements PortTypeEntBuilder {

        private String m_name;

        private KindEnum m_kind;

        private String m_color;

        private java.util.List<String> m_compatibleTypes;

        private Boolean m_hidden;

        private java.util.List<String> m_portViews;

        private java.util.List<String> m_portSpecViews;

        @Override
        public DefaultPortTypeEntBuilder setName(String name) {
             if(name == null) {
                 throw new IllegalArgumentException("<name> must not be null.");
             }
             m_name = name;
             return this;
        }

        @Override
        public DefaultPortTypeEntBuilder setKind(KindEnum kind) {
             if(kind == null) {
                 throw new IllegalArgumentException("<kind> must not be null.");
             }
             m_kind = kind;
             return this;
        }

        @Override
        public DefaultPortTypeEntBuilder setColor(String color) {
             m_color = color;
             return this;
        }

        @Override
        public DefaultPortTypeEntBuilder setCompatibleTypes(java.util.List<String> compatibleTypes) {
             m_compatibleTypes = compatibleTypes;
             return this;
        }

        @Override
        public DefaultPortTypeEntBuilder setHidden(Boolean hidden) {
             m_hidden = hidden;
             return this;
        }

        @Override
        public DefaultPortTypeEntBuilder setPortViews(java.util.List<String> portViews) {
             m_portViews = portViews;
             return this;
        }

        @Override
        public DefaultPortTypeEntBuilder setPortSpecViews(java.util.List<String> portSpecViews) {
             m_portSpecViews = portSpecViews;
             return this;
        }

        @Override
        public DefaultPortTypeEnt build() {
            return new DefaultPortTypeEnt(
                immutable(m_name),
                immutable(m_kind),
                immutable(m_color),
                immutable(m_compatibleTypes),
                immutable(m_hidden),
                immutable(m_portViews),
                immutable(m_portSpecViews));
        }
    
    }

}

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

import org.knime.gateway.impl.webui.entity.DefaultNodePortTemplateEnt;

import org.knime.gateway.api.webui.entity.NodePortEnt;

/**
 * A single port of a node.
 *
 * @param name
 * @param typeId
 * @param optional
 * @param info
 * @param index
 * @param connectedVia
 * @param inactive
 * @param portObjectVersion
 * @param portGroupId
 * @param canRemove
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 * @author Kai Franze, KNIME GmbH
 */
@javax.annotation.Generated(value = {"com.knime.gateway.codegen.GatewayCodegen", "src-gen/api/web-ui/configs/org.knime.gateway.impl-config.json"})
public record DefaultNodePortEnt(
    String name,
    String typeId,
    Boolean optional,
    String info,
    Integer index,
    java.util.List<org.knime.gateway.api.entity.ConnectionIDEnt> connectedVia,
    Boolean inactive,
    Integer portObjectVersion,
    String portGroupId,
    Boolean canRemove) implements NodePortEnt {

    /**
     * Canonical constructor for {@link DefaultNodePortEnt} including null checks for non-nullable parameters.
     *
     * @param name
     * @param typeId
     * @param optional
     * @param info
     * @param index
     * @param connectedVia
     * @param inactive
     * @param portObjectVersion
     * @param portGroupId
     * @param canRemove
     */
    public DefaultNodePortEnt {
        if(typeId == null) {
            throw new IllegalArgumentException("<typeId> must not be null.");
        }
        if(index == null) {
            throw new IllegalArgumentException("<index> must not be null.");
        }
    }

    @Override
    public String getTypeID() {
        return "NodePort";
    }
  
    @Override
    public String getName() {
        return name;
    }
    
    @Override
    public String getTypeId() {
        return typeId;
    }
    
    @Override
    public Boolean isOptional() {
        return optional;
    }
    
    @Override
    public String getInfo() {
        return info;
    }
    
    @Override
    public Integer getIndex() {
        return index;
    }
    
    @Override
    public java.util.List<org.knime.gateway.api.entity.ConnectionIDEnt> getConnectedVia() {
        return connectedVia;
    }
    
    @Override
    public Boolean isInactive() {
        return inactive;
    }
    
    @Override
    public Integer getPortObjectVersion() {
        return portObjectVersion;
    }
    
    @Override
    public String getPortGroupId() {
        return portGroupId;
    }
    
    @Override
    public Boolean isCanRemove() {
        return canRemove;
    }
    
    /**
     * A builder for {@link DefaultNodePortEnt}.
     */
    public static class DefaultNodePortEntBuilder implements NodePortEntBuilder {

        private String m_name;

        private String m_typeId;

        private Boolean m_optional;

        private String m_info;

        private Integer m_index;

        private java.util.List<org.knime.gateway.api.entity.ConnectionIDEnt> m_connectedVia;

        private Boolean m_inactive;

        private Integer m_portObjectVersion;

        private String m_portGroupId;

        private Boolean m_canRemove;

        @Override
        public DefaultNodePortEntBuilder setName(String name) {
             m_name = name;
             return this;
        }

        @Override
        public DefaultNodePortEntBuilder setTypeId(String typeId) {
             if(typeId == null) {
                 throw new IllegalArgumentException("<typeId> must not be null.");
             }
             m_typeId = typeId;
             return this;
        }

        @Override
        public DefaultNodePortEntBuilder setOptional(Boolean optional) {
             m_optional = optional;
             return this;
        }

        @Override
        public DefaultNodePortEntBuilder setInfo(String info) {
             m_info = info;
             return this;
        }

        @Override
        public DefaultNodePortEntBuilder setIndex(Integer index) {
             if(index == null) {
                 throw new IllegalArgumentException("<index> must not be null.");
             }
             m_index = index;
             return this;
        }

        @Override
        public DefaultNodePortEntBuilder setConnectedVia(java.util.List<org.knime.gateway.api.entity.ConnectionIDEnt> connectedVia) {
             m_connectedVia = connectedVia;
             return this;
        }

        @Override
        public DefaultNodePortEntBuilder setInactive(Boolean inactive) {
             m_inactive = inactive;
             return this;
        }

        @Override
        public DefaultNodePortEntBuilder setPortObjectVersion(Integer portObjectVersion) {
             m_portObjectVersion = portObjectVersion;
             return this;
        }

        @Override
        public DefaultNodePortEntBuilder setPortGroupId(String portGroupId) {
             m_portGroupId = portGroupId;
             return this;
        }

        @Override
        public DefaultNodePortEntBuilder setCanRemove(Boolean canRemove) {
             m_canRemove = canRemove;
             return this;
        }

        @Override
        public DefaultNodePortEnt build() {
            return new DefaultNodePortEnt(
                immutable(m_name),
                immutable(m_typeId),
                immutable(m_optional),
                immutable(m_info),
                immutable(m_index),
                immutable(m_connectedVia),
                immutable(m_inactive),
                immutable(m_portObjectVersion),
                immutable(m_portGroupId),
                immutable(m_canRemove));
        }
    
    }

}

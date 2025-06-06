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

import org.knime.gateway.impl.webui.entity.DefaultPortCommandEnt;

import org.knime.gateway.api.webui.entity.AddPortCommandEnt;

/**
 * Add a port to a node. In case of native nodes, the port will be appended to the given port group. In case of container nodes, port will be added as last port.
 *
 * @param kind
 * @param side
 * @param nodeId
 * @param portTypeId
 * @param portGroup
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
@jakarta.annotation.Generated(value = {"com.knime.gateway.codegen.GatewayCodegen", "src-gen/api/web-ui/configs/org.knime.gateway.impl-config.json"})
public record DefaultAddPortCommandEnt(
    KindEnum kind,
    SideEnum side,
    org.knime.gateway.api.entity.NodeIDEnt nodeId,
    String portTypeId,
    String portGroup) implements AddPortCommandEnt {

    /**
     * Validation for required parameters not being {@code null}.
     */
    public DefaultAddPortCommandEnt {
        if(kind == null) {
            throw new IllegalArgumentException("<kind> must not be null.");
        }
        if(side == null) {
            throw new IllegalArgumentException("<side> must not be null.");
        }
        if(nodeId == null) {
            throw new IllegalArgumentException("<nodeId> must not be null.");
        }
        if(portTypeId == null) {
            throw new IllegalArgumentException("<portTypeId> must not be null.");
        }
    }

    @Override
    public String getTypeID() {
        return "AddPortCommand";
    }
  
    @Override
    public KindEnum getKind() {
        return kind;
    }
    
    @Override
    public SideEnum getSide() {
        return side;
    }
    
    @Override
    public org.knime.gateway.api.entity.NodeIDEnt getNodeId() {
        return nodeId;
    }
    
    @Override
    public String getPortTypeId() {
        return portTypeId;
    }
    
    @Override
    public String getPortGroup() {
        return portGroup;
    }
    
    /**
     * A builder for {@link DefaultAddPortCommandEnt}.
     */
    public static class DefaultAddPortCommandEntBuilder implements AddPortCommandEntBuilder {

        private KindEnum m_kind;

        private SideEnum m_side;

        private org.knime.gateway.api.entity.NodeIDEnt m_nodeId;

        private String m_portTypeId;

        private String m_portGroup;

        @Override
        public DefaultAddPortCommandEntBuilder setKind(KindEnum kind) {
             if(kind == null) {
                 throw new IllegalArgumentException("<kind> must not be null.");
             }
             m_kind = kind;
             return this;
        }

        @Override
        public DefaultAddPortCommandEntBuilder setSide(SideEnum side) {
             if(side == null) {
                 throw new IllegalArgumentException("<side> must not be null.");
             }
             m_side = side;
             return this;
        }

        @Override
        public DefaultAddPortCommandEntBuilder setNodeId(org.knime.gateway.api.entity.NodeIDEnt nodeId) {
             if(nodeId == null) {
                 throw new IllegalArgumentException("<nodeId> must not be null.");
             }
             m_nodeId = nodeId;
             return this;
        }

        @Override
        public DefaultAddPortCommandEntBuilder setPortTypeId(String portTypeId) {
             if(portTypeId == null) {
                 throw new IllegalArgumentException("<portTypeId> must not be null.");
             }
             m_portTypeId = portTypeId;
             return this;
        }

        @Override
        public DefaultAddPortCommandEntBuilder setPortGroup(String portGroup) {
             m_portGroup = portGroup;
             return this;
        }

        @Override
        public DefaultAddPortCommandEnt build() {
            return new DefaultAddPortCommandEnt(
                immutable(m_kind),
                immutable(m_side),
                immutable(m_nodeId),
                immutable(m_portTypeId),
                immutable(m_portGroup));
        }
    
    }

}

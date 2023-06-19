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

import org.knime.gateway.api.webui.entity.NodeFactoryKeyEnt;
import org.knime.gateway.api.webui.entity.XYEnt;
import org.knime.gateway.impl.webui.entity.DefaultWorkflowCommandEnt;

import org.knime.gateway.api.webui.entity.InsertNodeCommandEnt;

/**
 * Inserts a node on top of an existing connection
 *
 * @param kind
 * @param connectionId
 * @param position
 * @param nodeFactory
 * @param nodeId
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
@jakarta.annotation.Generated(value = {"com.knime.gateway.codegen.GatewayCodegen", "src-gen/api/web-ui/configs/org.knime.gateway.impl-config.json"})
public record DefaultInsertNodeCommandEnt(
    KindEnum kind,
    org.knime.gateway.api.entity.ConnectionIDEnt connectionId,
    XYEnt position,
    NodeFactoryKeyEnt nodeFactory,
    org.knime.gateway.api.entity.NodeIDEnt nodeId) implements InsertNodeCommandEnt {

    /**
     * Validation for required parameters not being {@code null}.
     */
    public DefaultInsertNodeCommandEnt {
        if(kind == null) {
            throw new IllegalArgumentException("<kind> must not be null.");
        }
        if(connectionId == null) {
            throw new IllegalArgumentException("<connectionId> must not be null.");
        }
        if(position == null) {
            throw new IllegalArgumentException("<position> must not be null.");
        }
    }

    @Override
    public String getTypeID() {
        return "InsertNodeCommand";
    }
  
    @Override
    public KindEnum getKind() {
        return kind;
    }
    
    @Override
    public org.knime.gateway.api.entity.ConnectionIDEnt getConnectionId() {
        return connectionId;
    }
    
    @Override
    public XYEnt getPosition() {
        return position;
    }
    
    @Override
    public NodeFactoryKeyEnt getNodeFactory() {
        return nodeFactory;
    }
    
    @Override
    public org.knime.gateway.api.entity.NodeIDEnt getNodeId() {
        return nodeId;
    }
    
    /**
     * A builder for {@link DefaultInsertNodeCommandEnt}.
     */
    public static class DefaultInsertNodeCommandEntBuilder implements InsertNodeCommandEntBuilder {

        private KindEnum m_kind;

        private org.knime.gateway.api.entity.ConnectionIDEnt m_connectionId;

        private XYEnt m_position;

        private NodeFactoryKeyEnt m_nodeFactory;

        private org.knime.gateway.api.entity.NodeIDEnt m_nodeId;

        @Override
        public DefaultInsertNodeCommandEntBuilder setKind(KindEnum kind) {
             if(kind == null) {
                 throw new IllegalArgumentException("<kind> must not be null.");
             }
             m_kind = kind;
             return this;
        }

        @Override
        public DefaultInsertNodeCommandEntBuilder setConnectionId(org.knime.gateway.api.entity.ConnectionIDEnt connectionId) {
             if(connectionId == null) {
                 throw new IllegalArgumentException("<connectionId> must not be null.");
             }
             m_connectionId = connectionId;
             return this;
        }

        @Override
        public DefaultInsertNodeCommandEntBuilder setPosition(XYEnt position) {
             if(position == null) {
                 throw new IllegalArgumentException("<position> must not be null.");
             }
             m_position = position;
             return this;
        }

        @Override
        public DefaultInsertNodeCommandEntBuilder setNodeFactory(NodeFactoryKeyEnt nodeFactory) {
             m_nodeFactory = nodeFactory;
             return this;
        }

        @Override
        public DefaultInsertNodeCommandEntBuilder setNodeId(org.knime.gateway.api.entity.NodeIDEnt nodeId) {
             m_nodeId = nodeId;
             return this;
        }

        @Override
        public DefaultInsertNodeCommandEnt build() {
            return new DefaultInsertNodeCommandEnt(
                immutable(m_kind),
                immutable(m_connectionId),
                immutable(m_position),
                immutable(m_nodeFactory),
                immutable(m_nodeId));
        }
    
    }

}

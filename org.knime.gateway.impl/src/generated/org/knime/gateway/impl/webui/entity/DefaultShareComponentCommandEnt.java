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

import org.knime.gateway.impl.webui.entity.DefaultWorkflowCommandEnt;

import org.knime.gateway.api.webui.entity.ShareComponentCommandEnt;

/**
 * DefaultShareComponentCommandEnt
 *
 * @param kind
 * @param nodeId
 * @param destinationSpaceProviderId
 * @param destinationSpaceId
 * @param destinationItemId
 * @param collisionHandling
 * @param includeInputData
 * @param linkType
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
@jakarta.annotation.Generated(value = {"com.knime.gateway.codegen.GatewayCodegen", "src-gen/api/web-ui/configs/org.knime.gateway.impl-config.json"})
public record DefaultShareComponentCommandEnt(
    KindEnum kind,
    org.knime.gateway.api.entity.NodeIDEnt nodeId,
    String destinationSpaceProviderId,
    String destinationSpaceId,
    String destinationItemId,
    CollisionHandlingEnum collisionHandling,
    Boolean includeInputData,
    LinkTypeEnum linkType) implements ShareComponentCommandEnt {

    /**
     * Validation for required parameters not being {@code null}.
     */
    public DefaultShareComponentCommandEnt {
        if(kind == null) {
            throw new IllegalArgumentException("<kind> must not be null.");
        }
    }

    @Override
    public String getTypeID() {
        return "ShareComponentCommand";
    }
  
    @Override
    public KindEnum getKind() {
        return kind;
    }
    
    @Override
    public org.knime.gateway.api.entity.NodeIDEnt getNodeId() {
        return nodeId;
    }
    
    @Override
    public String getDestinationSpaceProviderId() {
        return destinationSpaceProviderId;
    }
    
    @Override
    public String getDestinationSpaceId() {
        return destinationSpaceId;
    }
    
    @Override
    public String getDestinationItemId() {
        return destinationItemId;
    }
    
    @Override
    public CollisionHandlingEnum getCollisionHandling() {
        return collisionHandling;
    }
    
    @Override
    public Boolean isIncludeInputData() {
        return includeInputData;
    }
    
    @Override
    public LinkTypeEnum getLinkType() {
        return linkType;
    }
    
    /**
     * A builder for {@link DefaultShareComponentCommandEnt}.
     */
    public static class DefaultShareComponentCommandEntBuilder implements ShareComponentCommandEntBuilder {

        private KindEnum m_kind;

        private org.knime.gateway.api.entity.NodeIDEnt m_nodeId;

        private String m_destinationSpaceProviderId;

        private String m_destinationSpaceId;

        private String m_destinationItemId;

        private CollisionHandlingEnum m_collisionHandling;

        private Boolean m_includeInputData;

        private LinkTypeEnum m_linkType;

        @Override
        public DefaultShareComponentCommandEntBuilder setKind(KindEnum kind) {
             if(kind == null) {
                 throw new IllegalArgumentException("<kind> must not be null.");
             }
             m_kind = kind;
             return this;
        }

        @Override
        public DefaultShareComponentCommandEntBuilder setNodeId(org.knime.gateway.api.entity.NodeIDEnt nodeId) {
             m_nodeId = nodeId;
             return this;
        }

        @Override
        public DefaultShareComponentCommandEntBuilder setDestinationSpaceProviderId(String destinationSpaceProviderId) {
             m_destinationSpaceProviderId = destinationSpaceProviderId;
             return this;
        }

        @Override
        public DefaultShareComponentCommandEntBuilder setDestinationSpaceId(String destinationSpaceId) {
             m_destinationSpaceId = destinationSpaceId;
             return this;
        }

        @Override
        public DefaultShareComponentCommandEntBuilder setDestinationItemId(String destinationItemId) {
             m_destinationItemId = destinationItemId;
             return this;
        }

        @Override
        public DefaultShareComponentCommandEntBuilder setCollisionHandling(CollisionHandlingEnum collisionHandling) {
             m_collisionHandling = collisionHandling;
             return this;
        }

        @Override
        public DefaultShareComponentCommandEntBuilder setIncludeInputData(Boolean includeInputData) {
             m_includeInputData = includeInputData;
             return this;
        }

        @Override
        public DefaultShareComponentCommandEntBuilder setLinkType(LinkTypeEnum linkType) {
             m_linkType = linkType;
             return this;
        }

        @Override
        public DefaultShareComponentCommandEnt build() {
            return new DefaultShareComponentCommandEnt(
                immutable(m_kind),
                immutable(m_nodeId),
                immutable(m_destinationSpaceProviderId),
                immutable(m_destinationSpaceId),
                immutable(m_destinationItemId),
                immutable(m_collisionHandling),
                immutable(m_includeInputData),
                immutable(m_linkType));
        }
    
    }

}

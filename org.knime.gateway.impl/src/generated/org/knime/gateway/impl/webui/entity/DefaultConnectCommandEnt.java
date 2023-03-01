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

import org.knime.gateway.api.webui.entity.ConnectCommandEnt;

/**
 * Connects two nodes (and by doing that possibly replacing another connection).
 *
 * @param kind
 * @param sourceNodeId
 * @param sourcePortIdx
 * @param destinationNodeId
 * @param destinationPortIdx
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
@javax.annotation.Generated(value = {"com.knime.gateway.codegen.GatewayCodegen", "src-gen/api/web-ui/configs/org.knime.gateway.impl-config.json"})
public record DefaultConnectCommandEnt(
    KindEnum kind,
    org.knime.gateway.api.entity.NodeIDEnt sourceNodeId,
    Integer sourcePortIdx,
    org.knime.gateway.api.entity.NodeIDEnt destinationNodeId,
    Integer destinationPortIdx) implements ConnectCommandEnt {

    /**
     * Validation for required parameters not being {@code null}.
     */
    public DefaultConnectCommandEnt {
        if(kind == null) {
            throw new IllegalArgumentException("<kind> must not be null.");
        }
        if(sourceNodeId == null) {
            throw new IllegalArgumentException("<sourceNodeId> must not be null.");
        }
        if(sourcePortIdx == null) {
            throw new IllegalArgumentException("<sourcePortIdx> must not be null.");
        }
        if(destinationNodeId == null) {
            throw new IllegalArgumentException("<destinationNodeId> must not be null.");
        }
        if(destinationPortIdx == null) {
            throw new IllegalArgumentException("<destinationPortIdx> must not be null.");
        }
    }

    @Override
    public String getTypeID() {
        return "ConnectCommand";
    }
  
    @Override
    public KindEnum getKind() {
        return kind;
    }
    
    @Override
    public org.knime.gateway.api.entity.NodeIDEnt getSourceNodeId() {
        return sourceNodeId;
    }
    
    @Override
    public Integer getSourcePortIdx() {
        return sourcePortIdx;
    }
    
    @Override
    public org.knime.gateway.api.entity.NodeIDEnt getDestinationNodeId() {
        return destinationNodeId;
    }
    
    @Override
    public Integer getDestinationPortIdx() {
        return destinationPortIdx;
    }
    
    /**
     * A builder for {@link DefaultConnectCommandEnt}.
     */
    public static class DefaultConnectCommandEntBuilder implements ConnectCommandEntBuilder {

        private KindEnum m_kind;

        private org.knime.gateway.api.entity.NodeIDEnt m_sourceNodeId;

        private Integer m_sourcePortIdx;

        private org.knime.gateway.api.entity.NodeIDEnt m_destinationNodeId;

        private Integer m_destinationPortIdx;

        @Override
        public DefaultConnectCommandEntBuilder setKind(KindEnum kind) {
             if(kind == null) {
                 throw new IllegalArgumentException("<kind> must not be null.");
             }
             m_kind = kind;
             return this;
        }

        @Override
        public DefaultConnectCommandEntBuilder setSourceNodeId(org.knime.gateway.api.entity.NodeIDEnt sourceNodeId) {
             if(sourceNodeId == null) {
                 throw new IllegalArgumentException("<sourceNodeId> must not be null.");
             }
             m_sourceNodeId = sourceNodeId;
             return this;
        }

        @Override
        public DefaultConnectCommandEntBuilder setSourcePortIdx(Integer sourcePortIdx) {
             if(sourcePortIdx == null) {
                 throw new IllegalArgumentException("<sourcePortIdx> must not be null.");
             }
             m_sourcePortIdx = sourcePortIdx;
             return this;
        }

        @Override
        public DefaultConnectCommandEntBuilder setDestinationNodeId(org.knime.gateway.api.entity.NodeIDEnt destinationNodeId) {
             if(destinationNodeId == null) {
                 throw new IllegalArgumentException("<destinationNodeId> must not be null.");
             }
             m_destinationNodeId = destinationNodeId;
             return this;
        }

        @Override
        public DefaultConnectCommandEntBuilder setDestinationPortIdx(Integer destinationPortIdx) {
             if(destinationPortIdx == null) {
                 throw new IllegalArgumentException("<destinationPortIdx> must not be null.");
             }
             m_destinationPortIdx = destinationPortIdx;
             return this;
        }

        @Override
        public DefaultConnectCommandEnt build() {
            return new DefaultConnectCommandEnt(
                immutable(m_kind),
                immutable(m_sourceNodeId),
                immutable(m_sourcePortIdx),
                immutable(m_destinationNodeId),
                immutable(m_destinationPortIdx));
        }
    
    }

}

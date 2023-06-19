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

import org.knime.gateway.api.webui.entity.AllowedConnectionActionsEnt;

import org.knime.gateway.api.webui.entity.ConnectionEnt;

/**
 * A single connection between two nodes.
 *
 * @param id
 * @param destNode
 * @param destPort
 * @param sourceNode
 * @param sourcePort
 * @param flowVariableConnection
 * @param streaming
 * @param label
 * @param allowedActions
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
@jakarta.annotation.Generated(value = {"com.knime.gateway.codegen.GatewayCodegen", "src-gen/api/web-ui/configs/org.knime.gateway.impl-config.json"})
public record DefaultConnectionEnt(
    org.knime.gateway.api.entity.ConnectionIDEnt id,
    org.knime.gateway.api.entity.NodeIDEnt destNode,
    Integer destPort,
    org.knime.gateway.api.entity.NodeIDEnt sourceNode,
    Integer sourcePort,
    Boolean flowVariableConnection,
    Boolean streaming,
    String label,
    AllowedConnectionActionsEnt allowedActions) implements ConnectionEnt {

    /**
     * Validation for required parameters not being {@code null}.
     */
    public DefaultConnectionEnt {
        if(id == null) {
            throw new IllegalArgumentException("<id> must not be null.");
        }
        if(destNode == null) {
            throw new IllegalArgumentException("<destNode> must not be null.");
        }
        if(destPort == null) {
            throw new IllegalArgumentException("<destPort> must not be null.");
        }
        if(sourceNode == null) {
            throw new IllegalArgumentException("<sourceNode> must not be null.");
        }
        if(sourcePort == null) {
            throw new IllegalArgumentException("<sourcePort> must not be null.");
        }
    }

    @Override
    public String getTypeID() {
        return "Connection";
    }
  
    @Override
    public org.knime.gateway.api.entity.ConnectionIDEnt getId() {
        return id;
    }
    
    @Override
    public org.knime.gateway.api.entity.NodeIDEnt getDestNode() {
        return destNode;
    }
    
    @Override
    public Integer getDestPort() {
        return destPort;
    }
    
    @Override
    public org.knime.gateway.api.entity.NodeIDEnt getSourceNode() {
        return sourceNode;
    }
    
    @Override
    public Integer getSourcePort() {
        return sourcePort;
    }
    
    @Override
    public Boolean isFlowVariableConnection() {
        return flowVariableConnection;
    }
    
    @Override
    public Boolean isStreaming() {
        return streaming;
    }
    
    @Override
    public String getLabel() {
        return label;
    }
    
    @Override
    public AllowedConnectionActionsEnt getAllowedActions() {
        return allowedActions;
    }
    
    /**
     * A builder for {@link DefaultConnectionEnt}.
     */
    public static class DefaultConnectionEntBuilder implements ConnectionEntBuilder {

        private org.knime.gateway.api.entity.ConnectionIDEnt m_id;

        private org.knime.gateway.api.entity.NodeIDEnt m_destNode;

        private Integer m_destPort;

        private org.knime.gateway.api.entity.NodeIDEnt m_sourceNode;

        private Integer m_sourcePort;

        private Boolean m_flowVariableConnection;

        private Boolean m_streaming;

        private String m_label;

        private AllowedConnectionActionsEnt m_allowedActions;

        @Override
        public DefaultConnectionEntBuilder setId(org.knime.gateway.api.entity.ConnectionIDEnt id) {
             if(id == null) {
                 throw new IllegalArgumentException("<id> must not be null.");
             }
             m_id = id;
             return this;
        }

        @Override
        public DefaultConnectionEntBuilder setDestNode(org.knime.gateway.api.entity.NodeIDEnt destNode) {
             if(destNode == null) {
                 throw new IllegalArgumentException("<destNode> must not be null.");
             }
             m_destNode = destNode;
             return this;
        }

        @Override
        public DefaultConnectionEntBuilder setDestPort(Integer destPort) {
             if(destPort == null) {
                 throw new IllegalArgumentException("<destPort> must not be null.");
             }
             m_destPort = destPort;
             return this;
        }

        @Override
        public DefaultConnectionEntBuilder setSourceNode(org.knime.gateway.api.entity.NodeIDEnt sourceNode) {
             if(sourceNode == null) {
                 throw new IllegalArgumentException("<sourceNode> must not be null.");
             }
             m_sourceNode = sourceNode;
             return this;
        }

        @Override
        public DefaultConnectionEntBuilder setSourcePort(Integer sourcePort) {
             if(sourcePort == null) {
                 throw new IllegalArgumentException("<sourcePort> must not be null.");
             }
             m_sourcePort = sourcePort;
             return this;
        }

        @Override
        public DefaultConnectionEntBuilder setFlowVariableConnection(Boolean flowVariableConnection) {
             m_flowVariableConnection = flowVariableConnection;
             return this;
        }

        @Override
        public DefaultConnectionEntBuilder setStreaming(Boolean streaming) {
             m_streaming = streaming;
             return this;
        }

        @Override
        public DefaultConnectionEntBuilder setLabel(String label) {
             m_label = label;
             return this;
        }

        @Override
        public DefaultConnectionEntBuilder setAllowedActions(AllowedConnectionActionsEnt allowedActions) {
             m_allowedActions = allowedActions;
             return this;
        }

        @Override
        public DefaultConnectionEnt build() {
            return new DefaultConnectionEnt(
                immutable(m_id),
                immutable(m_destNode),
                immutable(m_destPort),
                immutable(m_sourceNode),
                immutable(m_sourcePort),
                immutable(m_flowVariableConnection),
                immutable(m_streaming),
                immutable(m_label),
                immutable(m_allowedActions));
        }
    
    }

}

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

import java.math.BigDecimal;
import org.knime.gateway.api.webui.entity.AllowedNodeActionsEnt;
import org.knime.gateway.api.webui.entity.NodeAnnotationEnt;
import org.knime.gateway.api.webui.entity.NodeExecutionInfoEnt;
import org.knime.gateway.api.webui.entity.NodePortEnt;
import org.knime.gateway.api.webui.entity.XYEnt;

import org.knime.gateway.api.webui.entity.NodeEnt;

/**
 * Represents a node of certain kind (native node, component, metanode) in a workflow.
 *
 * @param id
 * @param inPorts
 * @param outPorts
 * @param annotation
 * @param position
 * @param kind
 * @param hasDialog
 * @param allowedActions
 * @param executionInfo
 * @param weight
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
@jakarta.annotation.Generated(value = {"com.knime.gateway.codegen.GatewayCodegen", "src-gen/api/web-ui/configs/org.knime.gateway.impl-config.json"})
public record DefaultNodeEnt(
    org.knime.gateway.api.entity.NodeIDEnt id,
    java.util.List<? extends NodePortEnt> inPorts,
    java.util.List<? extends NodePortEnt> outPorts,
    NodeAnnotationEnt annotation,
    XYEnt position,
    KindEnum kind,
    Boolean hasDialog,
    AllowedNodeActionsEnt allowedActions,
    NodeExecutionInfoEnt executionInfo,
    BigDecimal weight) implements NodeEnt {

    /**
     * Validation for required parameters not being {@code null}.
     */
    public DefaultNodeEnt {
        if(id == null) {
            throw new IllegalArgumentException("<id> must not be null.");
        }
        if(inPorts == null) {
            throw new IllegalArgumentException("<inPorts> must not be null.");
        }
        if(outPorts == null) {
            throw new IllegalArgumentException("<outPorts> must not be null.");
        }
        if(position == null) {
            throw new IllegalArgumentException("<position> must not be null.");
        }
        if(kind == null) {
            throw new IllegalArgumentException("<kind> must not be null.");
        }
    }

    @Override
    public String getTypeID() {
        return "Node";
    }
  
    @Override
    public org.knime.gateway.api.entity.NodeIDEnt getId() {
        return id;
    }
    
    @Override
    public java.util.List<? extends NodePortEnt> getInPorts() {
        return inPorts;
    }
    
    @Override
    public java.util.List<? extends NodePortEnt> getOutPorts() {
        return outPorts;
    }
    
    @Override
    public NodeAnnotationEnt getAnnotation() {
        return annotation;
    }
    
    @Override
    public XYEnt getPosition() {
        return position;
    }
    
    @Override
    public KindEnum getKind() {
        return kind;
    }
    
    @Override
    public Boolean hasDialog() {
        return hasDialog;
    }
    
    @Override
    public AllowedNodeActionsEnt getAllowedActions() {
        return allowedActions;
    }
    
    @Override
    public NodeExecutionInfoEnt getExecutionInfo() {
        return executionInfo;
    }
    
    @Override
    public BigDecimal getWeight() {
        return weight;
    }
    
    /**
     * A builder for {@link DefaultNodeEnt}.
     */
    public static class DefaultNodeEntBuilder implements NodeEntBuilder {

        private org.knime.gateway.api.entity.NodeIDEnt m_id;

        private java.util.List<? extends NodePortEnt> m_inPorts = new java.util.ArrayList<>();

        private java.util.List<? extends NodePortEnt> m_outPorts = new java.util.ArrayList<>();

        private NodeAnnotationEnt m_annotation;

        private XYEnt m_position;

        private KindEnum m_kind;

        private Boolean m_hasDialog;

        private AllowedNodeActionsEnt m_allowedActions;

        private NodeExecutionInfoEnt m_executionInfo;

        private BigDecimal m_weight;

        @Override
        public DefaultNodeEntBuilder setId(org.knime.gateway.api.entity.NodeIDEnt id) {
             if(id == null) {
                 throw new IllegalArgumentException("<id> must not be null.");
             }
             m_id = id;
             return this;
        }

        @Override
        public DefaultNodeEntBuilder setInPorts(java.util.List<? extends NodePortEnt> inPorts) {
             if(inPorts == null) {
                 throw new IllegalArgumentException("<inPorts> must not be null.");
             }
             m_inPorts = inPorts;
             return this;
        }

        @Override
        public DefaultNodeEntBuilder setOutPorts(java.util.List<? extends NodePortEnt> outPorts) {
             if(outPorts == null) {
                 throw new IllegalArgumentException("<outPorts> must not be null.");
             }
             m_outPorts = outPorts;
             return this;
        }

        @Override
        public DefaultNodeEntBuilder setAnnotation(NodeAnnotationEnt annotation) {
             m_annotation = annotation;
             return this;
        }

        @Override
        public DefaultNodeEntBuilder setPosition(XYEnt position) {
             if(position == null) {
                 throw new IllegalArgumentException("<position> must not be null.");
             }
             m_position = position;
             return this;
        }

        @Override
        public DefaultNodeEntBuilder setKind(KindEnum kind) {
             if(kind == null) {
                 throw new IllegalArgumentException("<kind> must not be null.");
             }
             m_kind = kind;
             return this;
        }

        @Override
        public DefaultNodeEntBuilder setHasDialog(Boolean hasDialog) {
             m_hasDialog = hasDialog;
             return this;
        }

        @Override
        public DefaultNodeEntBuilder setAllowedActions(AllowedNodeActionsEnt allowedActions) {
             m_allowedActions = allowedActions;
             return this;
        }

        @Override
        public DefaultNodeEntBuilder setExecutionInfo(NodeExecutionInfoEnt executionInfo) {
             m_executionInfo = executionInfo;
             return this;
        }

        @Override
        public DefaultNodeEntBuilder setWeight(BigDecimal weight) {
             m_weight = weight;
             return this;
        }

        @Override
        public DefaultNodeEnt build() {
            return new DefaultNodeEnt(
                immutable(m_id),
                immutable(m_inPorts),
                immutable(m_outPorts),
                immutable(m_annotation),
                immutable(m_position),
                immutable(m_kind),
                immutable(m_hasDialog),
                immutable(m_allowedActions),
                immutable(m_executionInfo),
                immutable(m_weight));
        }
    
    }

}

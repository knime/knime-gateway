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

import org.knime.gateway.api.webui.entity.AllowedNodeActionsEnt;
import org.knime.gateway.api.webui.entity.LoopInfoEnt;
import org.knime.gateway.api.webui.entity.NodeAnnotationEnt;
import org.knime.gateway.api.webui.entity.NodeExecutionInfoEnt;
import org.knime.gateway.api.webui.entity.NodePortEnt;
import org.knime.gateway.api.webui.entity.NodeStateEnt;
import org.knime.gateway.api.webui.entity.PortGroupEnt;
import org.knime.gateway.api.webui.entity.XYEnt;
import org.knime.gateway.impl.webui.entity.DefaultNodeEnt;

import org.knime.gateway.api.webui.entity.NativeNodeEnt;

/**
 * Native node extension of a node.
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
 * @param templateId
 * @param state
 * @param loopInfo
 * @param portGroups
 * @param hasView
 * @param isReexecutable
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
@javax.annotation.Generated(value = {"com.knime.gateway.codegen.GatewayCodegen", "src-gen/api/web-ui/configs/org.knime.gateway.impl-config.json"})
public record DefaultNativeNodeEnt(
    org.knime.gateway.api.entity.NodeIDEnt id,
    java.util.List<? extends NodePortEnt> inPorts,
    java.util.List<? extends NodePortEnt> outPorts,
    NodeAnnotationEnt annotation,
    XYEnt position,
    KindEnum kind,
    Boolean hasDialog,
    AllowedNodeActionsEnt allowedActions,
    NodeExecutionInfoEnt executionInfo,
    String templateId,
    NodeStateEnt state,
    LoopInfoEnt loopInfo,
    java.util.Map<String, PortGroupEnt> portGroups,
    Boolean hasView,
    Boolean isReexecutable) implements NativeNodeEnt {

    /**
     * Validation for required parameters not being {@code null}.
     */
    public DefaultNativeNodeEnt {
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
        if(templateId == null) {
            throw new IllegalArgumentException("<templateId> must not be null.");
        }
    }

    @Override
    public String getTypeID() {
        return "NativeNode";
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
    public String getTemplateId() {
        return templateId;
    }
    
    @Override
    public NodeStateEnt getState() {
        return state;
    }
    
    @Override
    public LoopInfoEnt getLoopInfo() {
        return loopInfo;
    }
    
    @Override
    public java.util.Map<String, PortGroupEnt> getPortGroups() {
        return portGroups;
    }
    
    @Override
    public Boolean hasView() {
        return hasView;
    }
    
    @Override
    public Boolean isReexecutable() {
        return isReexecutable;
    }
    
    /**
     * A builder for {@link DefaultNativeNodeEnt}.
     */
    public static class DefaultNativeNodeEntBuilder implements NativeNodeEntBuilder {

        private org.knime.gateway.api.entity.NodeIDEnt m_id;

        private java.util.List<? extends NodePortEnt> m_inPorts = new java.util.ArrayList<>();

        private java.util.List<? extends NodePortEnt> m_outPorts = new java.util.ArrayList<>();

        private NodeAnnotationEnt m_annotation;

        private XYEnt m_position;

        private KindEnum m_kind;

        private Boolean m_hasDialog;

        private AllowedNodeActionsEnt m_allowedActions;

        private NodeExecutionInfoEnt m_executionInfo;

        private String m_templateId;

        private NodeStateEnt m_state;

        private LoopInfoEnt m_loopInfo;

        private java.util.Map<String, PortGroupEnt> m_portGroups;

        private Boolean m_hasView;

        private Boolean m_isReexecutable;

        @Override
        public DefaultNativeNodeEntBuilder setId(org.knime.gateway.api.entity.NodeIDEnt id) {
             if(id == null) {
                 throw new IllegalArgumentException("<id> must not be null.");
             }
             m_id = id;
             return this;
        }

        @Override
        public DefaultNativeNodeEntBuilder setInPorts(java.util.List<? extends NodePortEnt> inPorts) {
             if(inPorts == null) {
                 throw new IllegalArgumentException("<inPorts> must not be null.");
             }
             m_inPorts = inPorts;
             return this;
        }

        @Override
        public DefaultNativeNodeEntBuilder setOutPorts(java.util.List<? extends NodePortEnt> outPorts) {
             if(outPorts == null) {
                 throw new IllegalArgumentException("<outPorts> must not be null.");
             }
             m_outPorts = outPorts;
             return this;
        }

        @Override
        public DefaultNativeNodeEntBuilder setAnnotation(NodeAnnotationEnt annotation) {
             m_annotation = annotation;
             return this;
        }

        @Override
        public DefaultNativeNodeEntBuilder setPosition(XYEnt position) {
             if(position == null) {
                 throw new IllegalArgumentException("<position> must not be null.");
             }
             m_position = position;
             return this;
        }

        @Override
        public DefaultNativeNodeEntBuilder setKind(KindEnum kind) {
             if(kind == null) {
                 throw new IllegalArgumentException("<kind> must not be null.");
             }
             m_kind = kind;
             return this;
        }

        @Override
        public DefaultNativeNodeEntBuilder setHasDialog(Boolean hasDialog) {
             m_hasDialog = hasDialog;
             return this;
        }

        @Override
        public DefaultNativeNodeEntBuilder setAllowedActions(AllowedNodeActionsEnt allowedActions) {
             m_allowedActions = allowedActions;
             return this;
        }

        @Override
        public DefaultNativeNodeEntBuilder setExecutionInfo(NodeExecutionInfoEnt executionInfo) {
             m_executionInfo = executionInfo;
             return this;
        }

        @Override
        public DefaultNativeNodeEntBuilder setTemplateId(String templateId) {
             if(templateId == null) {
                 throw new IllegalArgumentException("<templateId> must not be null.");
             }
             m_templateId = templateId;
             return this;
        }

        @Override
        public DefaultNativeNodeEntBuilder setState(NodeStateEnt state) {
             m_state = state;
             return this;
        }

        @Override
        public DefaultNativeNodeEntBuilder setLoopInfo(LoopInfoEnt loopInfo) {
             m_loopInfo = loopInfo;
             return this;
        }

        @Override
        public DefaultNativeNodeEntBuilder setPortGroups(java.util.Map<String, PortGroupEnt> portGroups) {
             m_portGroups = portGroups;
             return this;
        }

        @Override
        public DefaultNativeNodeEntBuilder setHasView(Boolean hasView) {
             m_hasView = hasView;
             return this;
        }

        @Override
        public DefaultNativeNodeEntBuilder setIsReexecutable(Boolean isReexecutable) {
             m_isReexecutable = isReexecutable;
             return this;
        }

        @Override
        public DefaultNativeNodeEnt build() {
            return new DefaultNativeNodeEnt(
                immutable(m_id),
                immutable(m_inPorts),
                immutable(m_outPorts),
                immutable(m_annotation),
                immutable(m_position),
                immutable(m_kind),
                immutable(m_hasDialog),
                immutable(m_allowedActions),
                immutable(m_executionInfo),
                immutable(m_templateId),
                immutable(m_state),
                immutable(m_loopInfo),
                immutable(m_portGroups),
                immutable(m_hasView),
                immutable(m_isReexecutable));
        }
    
    }

}

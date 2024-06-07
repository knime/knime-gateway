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

import org.knime.gateway.impl.webui.entity.DefaultEventEnt;

import org.knime.gateway.api.webui.entity.SelectionEventEnt;

/**
 * A selection (aka hiliting) event.
 *
 * @param projectId
 * @param workflowId
 * @param nodeId
 * @param portIndex
 * @param mode
 * @param selection
 * @param error
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
@jakarta.annotation.Generated(value = {"com.knime.gateway.codegen.GatewayCodegen", "src-gen/api/web-ui/configs/org.knime.gateway.impl-config.json"})
public record DefaultSelectionEventEnt(
    String projectId,
    org.knime.gateway.api.entity.NodeIDEnt workflowId,
    org.knime.gateway.api.entity.NodeIDEnt nodeId,
    Integer portIndex,
    ModeEnum mode,
    java.util.List<String> selection,
    String error) implements SelectionEventEnt {

    /**
     * Validation for required parameters not being {@code null}.
     */
    public DefaultSelectionEventEnt {
        if(projectId == null) {
            throw new IllegalArgumentException("<projectId> must not be null.");
        }
        if(workflowId == null) {
            throw new IllegalArgumentException("<workflowId> must not be null.");
        }
        if(nodeId == null) {
            throw new IllegalArgumentException("<nodeId> must not be null.");
        }
        if(mode == null) {
            throw new IllegalArgumentException("<mode> must not be null.");
        }
    }

    @Override
    public String getTypeID() {
        return "SelectionEvent";
    }
  
    @Override
    public String getProjectId() {
        return projectId;
    }
    
    @Override
    public org.knime.gateway.api.entity.NodeIDEnt getWorkflowId() {
        return workflowId;
    }
    
    @Override
    public org.knime.gateway.api.entity.NodeIDEnt getNodeId() {
        return nodeId;
    }
    
    @Override
    public Integer getPortIndex() {
        return portIndex;
    }
    
    @Override
    public ModeEnum getMode() {
        return mode;
    }
    
    @Override
    public java.util.List<String> getSelection() {
        return selection;
    }
    
    @Override
    public String getError() {
        return error;
    }
    
    /**
     * A builder for {@link DefaultSelectionEventEnt}.
     */
    public static class DefaultSelectionEventEntBuilder implements SelectionEventEntBuilder {

        private String m_projectId;

        private org.knime.gateway.api.entity.NodeIDEnt m_workflowId;

        private org.knime.gateway.api.entity.NodeIDEnt m_nodeId;

        private Integer m_portIndex;

        private ModeEnum m_mode;

        private java.util.List<String> m_selection;

        private String m_error;

        @Override
        public DefaultSelectionEventEntBuilder setProjectId(String projectId) {
             if(projectId == null) {
                 throw new IllegalArgumentException("<projectId> must not be null.");
             }
             m_projectId = projectId;
             return this;
        }

        @Override
        public DefaultSelectionEventEntBuilder setWorkflowId(org.knime.gateway.api.entity.NodeIDEnt workflowId) {
             if(workflowId == null) {
                 throw new IllegalArgumentException("<workflowId> must not be null.");
             }
             m_workflowId = workflowId;
             return this;
        }

        @Override
        public DefaultSelectionEventEntBuilder setNodeId(org.knime.gateway.api.entity.NodeIDEnt nodeId) {
             if(nodeId == null) {
                 throw new IllegalArgumentException("<nodeId> must not be null.");
             }
             m_nodeId = nodeId;
             return this;
        }

        @Override
        public DefaultSelectionEventEntBuilder setPortIndex(Integer portIndex) {
             m_portIndex = portIndex;
             return this;
        }

        @Override
        public DefaultSelectionEventEntBuilder setMode(ModeEnum mode) {
             if(mode == null) {
                 throw new IllegalArgumentException("<mode> must not be null.");
             }
             m_mode = mode;
             return this;
        }

        @Override
        public DefaultSelectionEventEntBuilder setSelection(java.util.List<String> selection) {
             m_selection = selection;
             return this;
        }

        @Override
        public DefaultSelectionEventEntBuilder setError(String error) {
             m_error = error;
             return this;
        }

        @Override
        public DefaultSelectionEventEnt build() {
            return new DefaultSelectionEventEnt(
                immutable(m_projectId),
                immutable(m_workflowId),
                immutable(m_nodeId),
                immutable(m_portIndex),
                immutable(m_mode),
                immutable(m_selection),
                immutable(m_error));
        }
    
    }

}

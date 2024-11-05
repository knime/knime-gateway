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

import org.knime.gateway.api.webui.entity.KaiMessageEnt;
import org.knime.gateway.api.webui.entity.XYEnt;

import org.knime.gateway.api.webui.entity.KaiRequestEnt;

/**
 * Encapsulates a request to K-AI which contains the entire conversation  as well as information on the open workflow, subworkflow and selected nodes. 
 *
 * @param conversationId
 * @param projectId
 * @param workflowId
 * @param selectedNodes
 * @param startPosition
 * @param messages
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
@jakarta.annotation.Generated(value = {"com.knime.gateway.codegen.GatewayCodegen", "src-gen/api/web-ui/configs/org.knime.gateway.impl-config.json"})
public record DefaultKaiRequestEnt(
    String conversationId,
    String projectId,
    String workflowId,
    java.util.List<String> selectedNodes,
    XYEnt startPosition,
    java.util.List<KaiMessageEnt> messages) implements KaiRequestEnt {

    /**
     * Validation for required parameters not being {@code null}.
     */
    public DefaultKaiRequestEnt {
        if(projectId == null) {
            throw new IllegalArgumentException("<projectId> must not be null.");
        }
        if(workflowId == null) {
            throw new IllegalArgumentException("<workflowId> must not be null.");
        }
        if(selectedNodes == null) {
            throw new IllegalArgumentException("<selectedNodes> must not be null.");
        }
        if(messages == null) {
            throw new IllegalArgumentException("<messages> must not be null.");
        }
    }

    @Override
    public String getTypeID() {
        return "KaiRequest";
    }
  
    @Override
    public String getConversationId() {
        return conversationId;
    }
    
    @Override
    public String getProjectId() {
        return projectId;
    }
    
    @Override
    public String getWorkflowId() {
        return workflowId;
    }
    
    @Override
    public java.util.List<String> getSelectedNodes() {
        return selectedNodes;
    }
    
    @Override
    public XYEnt getStartPosition() {
        return startPosition;
    }
    
    @Override
    public java.util.List<KaiMessageEnt> getMessages() {
        return messages;
    }
    
    /**
     * A builder for {@link DefaultKaiRequestEnt}.
     */
    public static class DefaultKaiRequestEntBuilder implements KaiRequestEntBuilder {

        private String m_conversationId;

        private String m_projectId;

        private String m_workflowId;

        private java.util.List<String> m_selectedNodes = new java.util.ArrayList<>();

        private XYEnt m_startPosition;

        private java.util.List<KaiMessageEnt> m_messages = new java.util.ArrayList<>();

        @Override
        public DefaultKaiRequestEntBuilder setConversationId(String conversationId) {
             m_conversationId = conversationId;
             return this;
        }

        @Override
        public DefaultKaiRequestEntBuilder setProjectId(String projectId) {
             if(projectId == null) {
                 throw new IllegalArgumentException("<projectId> must not be null.");
             }
             m_projectId = projectId;
             return this;
        }

        @Override
        public DefaultKaiRequestEntBuilder setWorkflowId(String workflowId) {
             if(workflowId == null) {
                 throw new IllegalArgumentException("<workflowId> must not be null.");
             }
             m_workflowId = workflowId;
             return this;
        }

        @Override
        public DefaultKaiRequestEntBuilder setSelectedNodes(java.util.List<String> selectedNodes) {
             if(selectedNodes == null) {
                 throw new IllegalArgumentException("<selectedNodes> must not be null.");
             }
             m_selectedNodes = selectedNodes;
             return this;
        }

        @Override
        public DefaultKaiRequestEntBuilder setStartPosition(XYEnt startPosition) {
             m_startPosition = startPosition;
             return this;
        }

        @Override
        public DefaultKaiRequestEntBuilder setMessages(java.util.List<KaiMessageEnt> messages) {
             if(messages == null) {
                 throw new IllegalArgumentException("<messages> must not be null.");
             }
             m_messages = messages;
             return this;
        }

        @Override
        public DefaultKaiRequestEnt build() {
            return new DefaultKaiRequestEnt(
                immutable(m_conversationId),
                immutable(m_projectId),
                immutable(m_workflowId),
                immutable(m_selectedNodes),
                immutable(m_startPosition),
                immutable(m_messages));
        }
    
    }

}

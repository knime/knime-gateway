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

import org.knime.gateway.api.webui.entity.ComponentNodeAndDescriptionEnt;

import org.knime.gateway.api.webui.entity.WorkflowMonitorMessageEnt;

/**
 * A message in the workflow monitor. &#x60;templateId&#x60; is only present if the node is a native node. &#x60;componentInfo&#x60; is only present if the node is a component node.
 *
 * @param templateId
 * @param componentInfo
 * @param workflowId
 * @param nodeId
 * @param name
 * @param message
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
@jakarta.annotation.Generated(value = {"com.knime.gateway.codegen.GatewayCodegen", "src-gen/api/web-ui/configs/org.knime.gateway.impl-config.json"})
public record DefaultWorkflowMonitorMessageEnt(
    String templateId,
    ComponentNodeAndDescriptionEnt componentInfo,
    org.knime.gateway.api.entity.NodeIDEnt workflowId,
    org.knime.gateway.api.entity.NodeIDEnt nodeId,
    String name,
    String message) implements WorkflowMonitorMessageEnt {

    /**
     * Validation for required parameters not being {@code null}.
     */
    public DefaultWorkflowMonitorMessageEnt {
        if(workflowId == null) {
            throw new IllegalArgumentException("<workflowId> must not be null.");
        }
        if(nodeId == null) {
            throw new IllegalArgumentException("<nodeId> must not be null.");
        }
        if(name == null) {
            throw new IllegalArgumentException("<name> must not be null.");
        }
        if(message == null) {
            throw new IllegalArgumentException("<message> must not be null.");
        }
    }

    @Override
    public String getTypeID() {
        return "WorkflowMonitorMessage";
    }
  
    @Override
    public String getTemplateId() {
        return templateId;
    }
    
    @Override
    public ComponentNodeAndDescriptionEnt getComponentInfo() {
        return componentInfo;
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
    public String getName() {
        return name;
    }
    
    @Override
    public String getMessage() {
        return message;
    }
    
    /**
     * A builder for {@link DefaultWorkflowMonitorMessageEnt}.
     */
    public static class DefaultWorkflowMonitorMessageEntBuilder implements WorkflowMonitorMessageEntBuilder {

        private String m_templateId;

        private ComponentNodeAndDescriptionEnt m_componentInfo;

        private org.knime.gateway.api.entity.NodeIDEnt m_workflowId;

        private org.knime.gateway.api.entity.NodeIDEnt m_nodeId;

        private String m_name;

        private String m_message;

        @Override
        public DefaultWorkflowMonitorMessageEntBuilder setTemplateId(String templateId) {
             m_templateId = templateId;
             return this;
        }

        @Override
        public DefaultWorkflowMonitorMessageEntBuilder setComponentInfo(ComponentNodeAndDescriptionEnt componentInfo) {
             m_componentInfo = componentInfo;
             return this;
        }

        @Override
        public DefaultWorkflowMonitorMessageEntBuilder setWorkflowId(org.knime.gateway.api.entity.NodeIDEnt workflowId) {
             if(workflowId == null) {
                 throw new IllegalArgumentException("<workflowId> must not be null.");
             }
             m_workflowId = workflowId;
             return this;
        }

        @Override
        public DefaultWorkflowMonitorMessageEntBuilder setNodeId(org.knime.gateway.api.entity.NodeIDEnt nodeId) {
             if(nodeId == null) {
                 throw new IllegalArgumentException("<nodeId> must not be null.");
             }
             m_nodeId = nodeId;
             return this;
        }

        @Override
        public DefaultWorkflowMonitorMessageEntBuilder setName(String name) {
             if(name == null) {
                 throw new IllegalArgumentException("<name> must not be null.");
             }
             m_name = name;
             return this;
        }

        @Override
        public DefaultWorkflowMonitorMessageEntBuilder setMessage(String message) {
             if(message == null) {
                 throw new IllegalArgumentException("<message> must not be null.");
             }
             m_message = message;
             return this;
        }

        @Override
        public DefaultWorkflowMonitorMessageEnt build() {
            return new DefaultWorkflowMonitorMessageEnt(
                immutable(m_templateId),
                immutable(m_componentInfo),
                immutable(m_workflowId),
                immutable(m_nodeId),
                immutable(m_name),
                immutable(m_message));
        }
    
    }

}

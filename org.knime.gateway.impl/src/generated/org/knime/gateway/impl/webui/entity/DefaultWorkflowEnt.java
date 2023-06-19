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

import org.knime.gateway.api.webui.entity.AllowedWorkflowActionsEnt;
import org.knime.gateway.api.webui.entity.ComponentNodeDescriptionEnt;
import org.knime.gateway.api.webui.entity.ConnectionEnt;
import org.knime.gateway.api.webui.entity.MetaPortsEnt;
import org.knime.gateway.api.webui.entity.NativeNodeInvariantsEnt;
import org.knime.gateway.api.webui.entity.ProjectMetadataEnt;
import org.knime.gateway.api.webui.entity.WorkflowAnnotationEnt;
import org.knime.gateway.api.webui.entity.WorkflowInfoEnt;

import org.knime.gateway.api.webui.entity.WorkflowEnt;

/**
 * The structure of a workflow.
 *
 * @param info
 * @param nodes
 * @param nodeTemplates
 * @param connections
 * @param workflowAnnotations
 * @param parents
 * @param metaInPorts
 * @param metaOutPorts
 * @param allowedActions
 * @param componentMetadata
 * @param projectMetadata
 * @param dirty
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
@jakarta.annotation.Generated(value = {"com.knime.gateway.codegen.GatewayCodegen", "src-gen/api/web-ui/configs/org.knime.gateway.impl-config.json"})
public record DefaultWorkflowEnt(
    WorkflowInfoEnt info,
    java.util.Map<String, org.knime.gateway.api.webui.entity.NodeEnt> nodes,
    java.util.Map<String, NativeNodeInvariantsEnt> nodeTemplates,
    java.util.Map<String, ConnectionEnt> connections,
    java.util.List<WorkflowAnnotationEnt> workflowAnnotations,
    java.util.List<WorkflowInfoEnt> parents,
    MetaPortsEnt metaInPorts,
    MetaPortsEnt metaOutPorts,
    AllowedWorkflowActionsEnt allowedActions,
    ComponentNodeDescriptionEnt componentMetadata,
    ProjectMetadataEnt projectMetadata,
    Boolean dirty) implements WorkflowEnt {

    /**
     * Validation for required parameters not being {@code null}.
     */
    public DefaultWorkflowEnt {
        if(info == null) {
            throw new IllegalArgumentException("<info> must not be null.");
        }
        if(nodes == null) {
            throw new IllegalArgumentException("<nodes> must not be null.");
        }
        if(nodeTemplates == null) {
            throw new IllegalArgumentException("<nodeTemplates> must not be null.");
        }
        if(connections == null) {
            throw new IllegalArgumentException("<connections> must not be null.");
        }
        if(workflowAnnotations == null) {
            throw new IllegalArgumentException("<workflowAnnotations> must not be null.");
        }
        if(dirty == null) {
            throw new IllegalArgumentException("<dirty> must not be null.");
        }
    }

    @Override
    public String getTypeID() {
        return "Workflow";
    }
  
    @Override
    public WorkflowInfoEnt getInfo() {
        return info;
    }
    
    @Override
    public java.util.Map<String, org.knime.gateway.api.webui.entity.NodeEnt> getNodes() {
        return nodes;
    }
    
    @Override
    public java.util.Map<String, NativeNodeInvariantsEnt> getNodeTemplates() {
        return nodeTemplates;
    }
    
    @Override
    public java.util.Map<String, ConnectionEnt> getConnections() {
        return connections;
    }
    
    @Override
    public java.util.List<WorkflowAnnotationEnt> getWorkflowAnnotations() {
        return workflowAnnotations;
    }
    
    @Override
    public java.util.List<WorkflowInfoEnt> getParents() {
        return parents;
    }
    
    @Override
    public MetaPortsEnt getMetaInPorts() {
        return metaInPorts;
    }
    
    @Override
    public MetaPortsEnt getMetaOutPorts() {
        return metaOutPorts;
    }
    
    @Override
    public AllowedWorkflowActionsEnt getAllowedActions() {
        return allowedActions;
    }
    
    @Override
    public ComponentNodeDescriptionEnt getComponentMetadata() {
        return componentMetadata;
    }
    
    @Override
    public ProjectMetadataEnt getProjectMetadata() {
        return projectMetadata;
    }
    
    @Override
    public Boolean isDirty() {
        return dirty;
    }
    
    /**
     * A builder for {@link DefaultWorkflowEnt}.
     */
    public static class DefaultWorkflowEntBuilder implements WorkflowEntBuilder {

        private WorkflowInfoEnt m_info;

        private java.util.Map<String, org.knime.gateway.api.webui.entity.NodeEnt> m_nodes = new java.util.HashMap<>();

        private java.util.Map<String, NativeNodeInvariantsEnt> m_nodeTemplates = new java.util.HashMap<>();

        private java.util.Map<String, ConnectionEnt> m_connections = new java.util.HashMap<>();

        private java.util.List<WorkflowAnnotationEnt> m_workflowAnnotations = new java.util.ArrayList<>();

        private java.util.List<WorkflowInfoEnt> m_parents;

        private MetaPortsEnt m_metaInPorts;

        private MetaPortsEnt m_metaOutPorts;

        private AllowedWorkflowActionsEnt m_allowedActions;

        private ComponentNodeDescriptionEnt m_componentMetadata;

        private ProjectMetadataEnt m_projectMetadata;

        private Boolean m_dirty;

        @Override
        public DefaultWorkflowEntBuilder setInfo(WorkflowInfoEnt info) {
             if(info == null) {
                 throw new IllegalArgumentException("<info> must not be null.");
             }
             m_info = info;
             return this;
        }

        @Override
        public DefaultWorkflowEntBuilder setNodes(java.util.Map<String, org.knime.gateway.api.webui.entity.NodeEnt> nodes) {
             if(nodes == null) {
                 throw new IllegalArgumentException("<nodes> must not be null.");
             }
             m_nodes = nodes;
             return this;
        }

        @Override
        public DefaultWorkflowEntBuilder setNodeTemplates(java.util.Map<String, NativeNodeInvariantsEnt> nodeTemplates) {
             if(nodeTemplates == null) {
                 throw new IllegalArgumentException("<nodeTemplates> must not be null.");
             }
             m_nodeTemplates = nodeTemplates;
             return this;
        }

        @Override
        public DefaultWorkflowEntBuilder setConnections(java.util.Map<String, ConnectionEnt> connections) {
             if(connections == null) {
                 throw new IllegalArgumentException("<connections> must not be null.");
             }
             m_connections = connections;
             return this;
        }

        @Override
        public DefaultWorkflowEntBuilder setWorkflowAnnotations(java.util.List<WorkflowAnnotationEnt> workflowAnnotations) {
             if(workflowAnnotations == null) {
                 throw new IllegalArgumentException("<workflowAnnotations> must not be null.");
             }
             m_workflowAnnotations = workflowAnnotations;
             return this;
        }

        @Override
        public DefaultWorkflowEntBuilder setParents(java.util.List<WorkflowInfoEnt> parents) {
             m_parents = parents;
             return this;
        }

        @Override
        public DefaultWorkflowEntBuilder setMetaInPorts(MetaPortsEnt metaInPorts) {
             m_metaInPorts = metaInPorts;
             return this;
        }

        @Override
        public DefaultWorkflowEntBuilder setMetaOutPorts(MetaPortsEnt metaOutPorts) {
             m_metaOutPorts = metaOutPorts;
             return this;
        }

        @Override
        public DefaultWorkflowEntBuilder setAllowedActions(AllowedWorkflowActionsEnt allowedActions) {
             m_allowedActions = allowedActions;
             return this;
        }

        @Override
        public DefaultWorkflowEntBuilder setComponentMetadata(ComponentNodeDescriptionEnt componentMetadata) {
             m_componentMetadata = componentMetadata;
             return this;
        }

        @Override
        public DefaultWorkflowEntBuilder setProjectMetadata(ProjectMetadataEnt projectMetadata) {
             m_projectMetadata = projectMetadata;
             return this;
        }

        @Override
        public DefaultWorkflowEntBuilder setDirty(Boolean dirty) {
             if(dirty == null) {
                 throw new IllegalArgumentException("<dirty> must not be null.");
             }
             m_dirty = dirty;
             return this;
        }

        @Override
        public DefaultWorkflowEnt build() {
            return new DefaultWorkflowEnt(
                immutable(m_info),
                immutable(m_nodes),
                immutable(m_nodeTemplates),
                immutable(m_connections),
                immutable(m_workflowAnnotations),
                immutable(m_parents),
                immutable(m_metaInPorts),
                immutable(m_metaOutPorts),
                immutable(m_allowedActions),
                immutable(m_componentMetadata),
                immutable(m_projectMetadata),
                immutable(m_dirty));
        }
    
    }

}

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

import java.util.Objects;

import org.apache.commons.lang3.builder.HashCodeBuilder;

import org.knime.gateway.api.webui.entity.AllowedActionsEnt;
import org.knime.gateway.api.webui.entity.ConnectionEnt;
import org.knime.gateway.api.webui.entity.MetaPortsEnt;
import org.knime.gateway.api.webui.entity.NodeTemplateEnt;
import org.knime.gateway.api.webui.entity.WorkflowAnnotationEnt;
import org.knime.gateway.api.webui.entity.WorkflowInfoEnt;

import org.knime.gateway.api.webui.entity.WorkflowEnt;

/**
 * The structure of a workflow.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
@javax.annotation.Generated(value = {"com.knime.gateway.codegen.GatewayCodegen", "src-gen/api/web-ui/configs/org.knime.gateway.impl-config.json"})
public class DefaultWorkflowEnt  implements WorkflowEnt {

  protected WorkflowInfoEnt m_info;
  protected java.util.Map<String, org.knime.gateway.api.webui.entity.NodeEnt> m_nodes;
  protected java.util.Map<String, NodeTemplateEnt> m_nodeTemplates;
  protected java.util.Map<String, ConnectionEnt> m_connections;
  protected java.util.List<WorkflowAnnotationEnt> m_workflowAnnotations;
  protected java.util.List<WorkflowInfoEnt> m_parents;
  protected MetaPortsEnt m_metaInPorts;
  protected MetaPortsEnt m_metaOutPorts;
  protected AllowedActionsEnt m_allowedActions;
  
  protected DefaultWorkflowEnt() {
    //for sub-classes
  }
  
  @Override
  public String getTypeID() {
    return "Workflow";
  }
  
  private DefaultWorkflowEnt(DefaultWorkflowEntBuilder builder) {
    
    if(builder.m_info == null) {
        throw new IllegalArgumentException("info must not be null.");
    }
    m_info = immutable(builder.m_info);
    if(builder.m_nodes == null) {
        throw new IllegalArgumentException("nodes must not be null.");
    }
    m_nodes = immutable(builder.m_nodes);
    if(builder.m_nodeTemplates == null) {
        throw new IllegalArgumentException("nodeTemplates must not be null.");
    }
    m_nodeTemplates = immutable(builder.m_nodeTemplates);
    if(builder.m_connections == null) {
        throw new IllegalArgumentException("connections must not be null.");
    }
    m_connections = immutable(builder.m_connections);
    if(builder.m_workflowAnnotations == null) {
        throw new IllegalArgumentException("workflowAnnotations must not be null.");
    }
    m_workflowAnnotations = immutable(builder.m_workflowAnnotations);
    m_parents = immutable(builder.m_parents);
    m_metaInPorts = immutable(builder.m_metaInPorts);
    m_metaOutPorts = immutable(builder.m_metaOutPorts);
    m_allowedActions = immutable(builder.m_allowedActions);
  }
  
   /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null) {
            return false;
        }
        if (getClass() != o.getClass()) {
            return false;
        }
        DefaultWorkflowEnt ent = (DefaultWorkflowEnt)o;
        return Objects.equals(m_info, ent.m_info) && Objects.equals(m_nodes, ent.m_nodes) && Objects.equals(m_nodeTemplates, ent.m_nodeTemplates) && Objects.equals(m_connections, ent.m_connections) && Objects.equals(m_workflowAnnotations, ent.m_workflowAnnotations) && Objects.equals(m_parents, ent.m_parents) && Objects.equals(m_metaInPorts, ent.m_metaInPorts) && Objects.equals(m_metaOutPorts, ent.m_metaOutPorts) && Objects.equals(m_allowedActions, ent.m_allowedActions);
    }


  
   /**
    * {@inheritDoc}
    */
   @Override
   public int hashCode() {
       return new HashCodeBuilder()
               .append(m_info)
               .append(m_nodes)
               .append(m_nodeTemplates)
               .append(m_connections)
               .append(m_workflowAnnotations)
               .append(m_parents)
               .append(m_metaInPorts)
               .append(m_metaOutPorts)
               .append(m_allowedActions)
               .toHashCode();
   }
  
	
	
  @Override
  public WorkflowInfoEnt getInfo() {
        return m_info;
  }
    
  @Override
  public java.util.Map<String, org.knime.gateway.api.webui.entity.NodeEnt> getNodes() {
        return m_nodes;
  }
    
  @Override
  public java.util.Map<String, NodeTemplateEnt> getNodeTemplates() {
        return m_nodeTemplates;
  }
    
  @Override
  public java.util.Map<String, ConnectionEnt> getConnections() {
        return m_connections;
  }
    
  @Override
  public java.util.List<WorkflowAnnotationEnt> getWorkflowAnnotations() {
        return m_workflowAnnotations;
  }
    
  @Override
  public java.util.List<WorkflowInfoEnt> getParents() {
        return m_parents;
  }
    
  @Override
  public MetaPortsEnt getMetaInPorts() {
        return m_metaInPorts;
  }
    
  @Override
  public MetaPortsEnt getMetaOutPorts() {
        return m_metaOutPorts;
  }
    
  @Override
  public AllowedActionsEnt getAllowedActions() {
        return m_allowedActions;
  }
    
  
    public static class DefaultWorkflowEntBuilder implements WorkflowEntBuilder {
    
        public DefaultWorkflowEntBuilder(){
            
        }
    
        private WorkflowInfoEnt m_info;
        private java.util.Map<String, org.knime.gateway.api.webui.entity.NodeEnt> m_nodes = new java.util.HashMap<>();
        private java.util.Map<String, NodeTemplateEnt> m_nodeTemplates = new java.util.HashMap<>();
        private java.util.Map<String, ConnectionEnt> m_connections = new java.util.HashMap<>();
        private java.util.List<WorkflowAnnotationEnt> m_workflowAnnotations = new java.util.ArrayList<>();
        private java.util.List<WorkflowInfoEnt> m_parents;
        private MetaPortsEnt m_metaInPorts;
        private MetaPortsEnt m_metaOutPorts;
        private AllowedActionsEnt m_allowedActions;

        @Override
        public DefaultWorkflowEntBuilder setInfo(WorkflowInfoEnt info) {
             if(info == null) {
                 throw new IllegalArgumentException("info must not be null.");
             }
             m_info = info;
             return this;
        }

        @Override
        public DefaultWorkflowEntBuilder setNodes(java.util.Map<String, org.knime.gateway.api.webui.entity.NodeEnt> nodes) {
             if(nodes == null) {
                 throw new IllegalArgumentException("nodes must not be null.");
             }
             m_nodes = nodes;
             return this;
        }

        @Override
        public DefaultWorkflowEntBuilder setNodeTemplates(java.util.Map<String, NodeTemplateEnt> nodeTemplates) {
             if(nodeTemplates == null) {
                 throw new IllegalArgumentException("nodeTemplates must not be null.");
             }
             m_nodeTemplates = nodeTemplates;
             return this;
        }

        @Override
        public DefaultWorkflowEntBuilder setConnections(java.util.Map<String, ConnectionEnt> connections) {
             if(connections == null) {
                 throw new IllegalArgumentException("connections must not be null.");
             }
             m_connections = connections;
             return this;
        }

        @Override
        public DefaultWorkflowEntBuilder setWorkflowAnnotations(java.util.List<WorkflowAnnotationEnt> workflowAnnotations) {
             if(workflowAnnotations == null) {
                 throw new IllegalArgumentException("workflowAnnotations must not be null.");
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
        public DefaultWorkflowEntBuilder setAllowedActions(AllowedActionsEnt allowedActions) {
             m_allowedActions = allowedActions;
             return this;
        }

        
        @Override
        public DefaultWorkflowEnt build() {
            return new DefaultWorkflowEnt(this);
        }
    
    }

}

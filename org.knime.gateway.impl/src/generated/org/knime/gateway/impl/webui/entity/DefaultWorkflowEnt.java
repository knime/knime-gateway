/* ------------------------------------------------------------------
 * This source code, its documentation and all appendant files
 * are protected by copyright law. All rights reserved.
 *
 * Copyright by KNIME AG, Zurich, Switzerland
 *
 * You may not modify, publish, transmit, transfer or sell, reproduce,
 * create derivative works from, distribute, perform, display, or in
 * any way exploit any of the content, in whole or in part, except as
 * otherwise expressly permitted in writing by the copyright owner or
 * as specified in the license file distributed with this product.
 *
 * If you have any questions please contact the copyright holder:
 * website: www.knime.com
 * email: contact@knime.com
 * ---------------------------------------------------------------------
 *
 */
package org.knime.gateway.impl.webui.entity;

import static org.knime.gateway.api.util.EntityUtil.immutable;

import java.util.Objects;

import org.knime.gateway.api.webui.entity.ConnectionEnt;
import org.knime.gateway.api.webui.entity.NodeEnt;
import org.knime.gateway.api.webui.entity.NodeTemplateEnt;
import org.knime.gateway.api.webui.entity.WorkflowAnnotationEnt;

import org.knime.gateway.api.webui.entity.WorkflowEnt;

/**
 * The structure of a workflow.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
@javax.annotation.Generated(value = {"com.knime.gateway.codegen.GatewayCodegen", "src-gen/api/web-ui/configs/org.knime.gateway.impl-config.json"})
public class DefaultWorkflowEnt  implements WorkflowEnt {

  protected String m_name;
  protected java.util.Map<String, NodeEnt> m_nodes;
  protected java.util.Map<String, NodeTemplateEnt> m_nodeTemplates;
  protected java.util.Map<String, ConnectionEnt> m_connections;
  protected java.util.Map<String, WorkflowAnnotationEnt> m_workflowAnnotations;
  
  protected DefaultWorkflowEnt() {
    //for sub-classes
  }
  
  @Override
  public String getTypeID() {
    return "Workflow";
  }
  
  private DefaultWorkflowEnt(DefaultWorkflowEntBuilder builder) {
    
    m_name = immutable(builder.m_name);
    m_nodes = immutable(builder.m_nodes);
    m_nodeTemplates = immutable(builder.m_nodeTemplates);
    m_connections = immutable(builder.m_connections);
    m_workflowAnnotations = immutable(builder.m_workflowAnnotations);
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
        return Objects.equals(m_name, ent.m_name) && Objects.equals(m_nodes, ent.m_nodes) && Objects.equals(m_nodeTemplates, ent.m_nodeTemplates) && Objects.equals(m_connections, ent.m_connections) && Objects.equals(m_workflowAnnotations, ent.m_workflowAnnotations);
    }


  @Override
  public String getName() {
        return m_name;
    }
    
  @Override
  public java.util.Map<String, NodeEnt> getNodes() {
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
  public java.util.Map<String, WorkflowAnnotationEnt> getWorkflowAnnotations() {
        return m_workflowAnnotations;
    }
    
  
    public static class DefaultWorkflowEntBuilder implements WorkflowEntBuilder {
    
        public DefaultWorkflowEntBuilder(){
            
        }
    
        private String m_name;
        private java.util.Map<String, NodeEnt> m_nodes = new java.util.HashMap<>();
        private java.util.Map<String, NodeTemplateEnt> m_nodeTemplates = new java.util.HashMap<>();
        private java.util.Map<String, ConnectionEnt> m_connections = new java.util.HashMap<>();
        private java.util.Map<String, WorkflowAnnotationEnt> m_workflowAnnotations = new java.util.HashMap<>();

        @Override
        public DefaultWorkflowEntBuilder setName(String name) {
             m_name = name;
             return this;
        }

        @Override
        public DefaultWorkflowEntBuilder setNodes(java.util.Map<String, NodeEnt> nodes) {
             m_nodes = nodes;
             return this;
        }

        @Override
        public DefaultWorkflowEntBuilder setNodeTemplates(java.util.Map<String, NodeTemplateEnt> nodeTemplates) {
             m_nodeTemplates = nodeTemplates;
             return this;
        }

        @Override
        public DefaultWorkflowEntBuilder setConnections(java.util.Map<String, ConnectionEnt> connections) {
             m_connections = connections;
             return this;
        }

        @Override
        public DefaultWorkflowEntBuilder setWorkflowAnnotations(java.util.Map<String, WorkflowAnnotationEnt> workflowAnnotations) {
             m_workflowAnnotations = workflowAnnotations;
             return this;
        }

        
        @Override
        public DefaultWorkflowEnt build() {
            return new DefaultWorkflowEnt(this);
        }
    
    }

}

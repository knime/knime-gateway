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
package com.knime.gateway.v0.entity.impl;

import static com.knime.gateway.util.DefaultEntUtil.immutable;

import java.util.Objects;

import com.knime.gateway.v0.entity.ConnectionEnt;
import com.knime.gateway.v0.entity.MetaPortInfoEnt;
import com.knime.gateway.v0.entity.NodeEnt;
import com.knime.gateway.v0.entity.WorkflowAnnotationEnt;
import com.knime.gateway.v0.entity.WorkflowUIInfoEnt;

import com.knime.gateway.v0.entity.WorkflowEnt;

/**
 * The structure of a workflow.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
@javax.annotation.Generated(value = "org.knime.gateway.codegen.GatewayCodegen")
public class DefaultWorkflowEnt  implements WorkflowEnt {

  protected java.util.Map<String, NodeEnt> m_nodes;
  protected java.util.List<ConnectionEnt> m_connections;
  protected java.util.List<MetaPortInfoEnt> m_metaInPortInfos;
  protected java.util.List<MetaPortInfoEnt> m_metaOutPortInfos;
  protected java.util.List<WorkflowAnnotationEnt> m_workflowAnnotations;
  protected WorkflowUIInfoEnt m_workflowUIInfo;
  protected Boolean m_hasCredentials;
  protected Boolean m_inWizardExecution;
  
  protected DefaultWorkflowEnt() {
    //for sub-classes
  }
  
  @Override
  public String getTypeID() {
    return "Workflow";
  }
  
  private DefaultWorkflowEnt(DefaultWorkflowEntBuilder builder) {
    
    m_nodes = immutable(builder.m_nodes);
    m_connections = immutable(builder.m_connections);
    m_metaInPortInfos = immutable(builder.m_metaInPortInfos);
    m_metaOutPortInfos = immutable(builder.m_metaOutPortInfos);
    m_workflowAnnotations = immutable(builder.m_workflowAnnotations);
    m_workflowUIInfo = immutable(builder.m_workflowUIInfo);
    m_hasCredentials = immutable(builder.m_hasCredentials);
    m_inWizardExecution = immutable(builder.m_inWizardExecution);
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
        return Objects.equals(m_nodes, ent.m_nodes) && Objects.equals(m_connections, ent.m_connections) && Objects.equals(m_metaInPortInfos, ent.m_metaInPortInfos) && Objects.equals(m_metaOutPortInfos, ent.m_metaOutPortInfos) && Objects.equals(m_workflowAnnotations, ent.m_workflowAnnotations) && Objects.equals(m_workflowUIInfo, ent.m_workflowUIInfo) && Objects.equals(m_hasCredentials, ent.m_hasCredentials) && Objects.equals(m_inWizardExecution, ent.m_inWizardExecution);
    }


  @Override
  public java.util.Map<String, NodeEnt> getNodes() {
        return m_nodes;
    }
    
  @Override
  public java.util.List<ConnectionEnt> getConnections() {
        return m_connections;
    }
    
  @Override
  public java.util.List<MetaPortInfoEnt> getMetaInPortInfos() {
        return m_metaInPortInfos;
    }
    
  @Override
  public java.util.List<MetaPortInfoEnt> getMetaOutPortInfos() {
        return m_metaOutPortInfos;
    }
    
  @Override
  public java.util.List<WorkflowAnnotationEnt> getWorkflowAnnotations() {
        return m_workflowAnnotations;
    }
    
  @Override
  public WorkflowUIInfoEnt getWorkflowUIInfo() {
        return m_workflowUIInfo;
    }
    
  @Override
  public Boolean hasCredentials() {
        return m_hasCredentials;
    }
    
  @Override
  public Boolean isInWizardExecution() {
        return m_inWizardExecution;
    }
    
  
    public static class DefaultWorkflowEntBuilder implements WorkflowEntBuilder {
    
        public DefaultWorkflowEntBuilder(){
            
        }
    
        private java.util.Map<String, NodeEnt> m_nodes = new java.util.HashMap<>();
        private java.util.List<ConnectionEnt> m_connections = new java.util.ArrayList<>();
        private java.util.List<MetaPortInfoEnt> m_metaInPortInfos = new java.util.ArrayList<>();
        private java.util.List<MetaPortInfoEnt> m_metaOutPortInfos = new java.util.ArrayList<>();
        private java.util.List<WorkflowAnnotationEnt> m_workflowAnnotations = new java.util.ArrayList<>();
        private WorkflowUIInfoEnt m_workflowUIInfo;
        private Boolean m_hasCredentials = null;
        private Boolean m_inWizardExecution = null;

        @Override
        public DefaultWorkflowEntBuilder setNodes(java.util.Map<String, NodeEnt> nodes) {
             m_nodes = nodes;
             return this;
        }

        @Override
        public DefaultWorkflowEntBuilder setConnections(java.util.List<ConnectionEnt> connections) {
             m_connections = connections;
             return this;
        }

        @Override
        public DefaultWorkflowEntBuilder setMetaInPortInfos(java.util.List<MetaPortInfoEnt> metaInPortInfos) {
             m_metaInPortInfos = metaInPortInfos;
             return this;
        }

        @Override
        public DefaultWorkflowEntBuilder setMetaOutPortInfos(java.util.List<MetaPortInfoEnt> metaOutPortInfos) {
             m_metaOutPortInfos = metaOutPortInfos;
             return this;
        }

        @Override
        public DefaultWorkflowEntBuilder setWorkflowAnnotations(java.util.List<WorkflowAnnotationEnt> workflowAnnotations) {
             m_workflowAnnotations = workflowAnnotations;
             return this;
        }

        @Override
        public DefaultWorkflowEntBuilder setWorkflowUIInfo(WorkflowUIInfoEnt workflowUIInfo) {
             m_workflowUIInfo = workflowUIInfo;
             return this;
        }

        @Override
        public DefaultWorkflowEntBuilder setHasCredentials(Boolean hasCredentials) {
             m_hasCredentials = hasCredentials;
             return this;
        }

        @Override
        public DefaultWorkflowEntBuilder setInWizardExecution(Boolean inWizardExecution) {
             m_inWizardExecution = inWizardExecution;
             return this;
        }

        
        @Override
        public DefaultWorkflowEnt build() {
            return new DefaultWorkflowEnt(this);
        }
    
    }

}

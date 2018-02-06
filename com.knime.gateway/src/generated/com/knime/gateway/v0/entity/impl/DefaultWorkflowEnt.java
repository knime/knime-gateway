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
  
  protected DefaultWorkflowEnt() {
    //for sub-classes
  }
  
  private DefaultWorkflowEnt(DefaultWorkflowEntBuilder builder) {
    
    m_nodes = builder.m_nodes;
    m_connections = builder.m_connections;
    m_metaInPortInfos = builder.m_metaInPortInfos;
    m_metaOutPortInfos = builder.m_metaOutPortInfos;
    m_workflowAnnotations = builder.m_workflowAnnotations;
    m_workflowUIInfo = builder.m_workflowUIInfo;
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
    
  
    public static class DefaultWorkflowEntBuilder implements WorkflowEntBuilder {
    
        public DefaultWorkflowEntBuilder(){
            
        }
    
        private java.util.Map<String, NodeEnt> m_nodes = new java.util.HashMap<>();
        private java.util.List<ConnectionEnt> m_connections = new java.util.ArrayList<>();
        private java.util.List<MetaPortInfoEnt> m_metaInPortInfos = new java.util.ArrayList<>();
        private java.util.List<MetaPortInfoEnt> m_metaOutPortInfos = new java.util.ArrayList<>();
        private java.util.List<WorkflowAnnotationEnt> m_workflowAnnotations = new java.util.ArrayList<>();
        private WorkflowUIInfoEnt m_workflowUIInfo;

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
        public DefaultWorkflowEnt build() {
            return new DefaultWorkflowEnt(this);
        }
    
    }

}

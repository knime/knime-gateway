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
package org.knime.gateway.impl.entity;

import static org.knime.gateway.api.util.EntityUtil.immutable;

import java.util.Objects;

import org.knime.gateway.api.entity.ConnectionEnt;
import org.knime.gateway.api.entity.MetaPortInfoEnt;
import org.knime.gateway.api.entity.NodeEnt;
import org.knime.gateway.api.entity.WorkflowAnnotationEnt;
import org.knime.gateway.api.entity.WorkflowUIInfoEnt;

import org.knime.gateway.api.entity.WorkflowEnt;

/**
 * The structure of a workflow.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
@javax.annotation.Generated(value = {"com.knime.gateway.codegen.GatewayCodegen", "src-gen/org.knime.gateway.impl-config.json"})
public class DefaultWorkflowEnt  implements WorkflowEnt {

  protected java.util.Map<String, NodeEnt> m_nodes;
  protected java.util.Map<String, ConnectionEnt> m_connections;
  protected java.util.List<MetaPortInfoEnt> m_metaInPortInfos;
  protected java.util.List<MetaPortInfoEnt> m_metaOutPortInfos;
  protected java.util.Map<String, WorkflowAnnotationEnt> m_workflowAnnotations;
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
  public java.util.Map<String, ConnectionEnt> getConnections() {
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
  public java.util.Map<String, WorkflowAnnotationEnt> getWorkflowAnnotations() {
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
        private java.util.Map<String, ConnectionEnt> m_connections = new java.util.HashMap<>();
        private java.util.List<MetaPortInfoEnt> m_metaInPortInfos = new java.util.ArrayList<>();
        private java.util.List<MetaPortInfoEnt> m_metaOutPortInfos = new java.util.ArrayList<>();
        private java.util.Map<String, WorkflowAnnotationEnt> m_workflowAnnotations = new java.util.HashMap<>();
        private WorkflowUIInfoEnt m_workflowUIInfo;
        private Boolean m_hasCredentials;
        private Boolean m_inWizardExecution;

        @Override
        public DefaultWorkflowEntBuilder setNodes(java.util.Map<String, NodeEnt> nodes) {
             m_nodes = nodes;
             return this;
        }

        @Override
        public DefaultWorkflowEntBuilder setConnections(java.util.Map<String, ConnectionEnt> connections) {
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
        public DefaultWorkflowEntBuilder setWorkflowAnnotations(java.util.Map<String, WorkflowAnnotationEnt> workflowAnnotations) {
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

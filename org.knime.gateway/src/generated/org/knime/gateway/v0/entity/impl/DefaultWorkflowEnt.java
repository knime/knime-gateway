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
 *  KNIME and ECLIPSE being a combined program, KNIME GMBH herewith grants
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
package org.knime.gateway.v0.entity.impl;

import org.knime.gateway.v0.entity.ConnectionEnt;
import org.knime.gateway.v0.entity.MetaPortInfoEnt;
import org.knime.gateway.v0.entity.NodeEnt;
import org.knime.gateway.v0.entity.WorkflowAnnotationEnt;
import org.knime.gateway.v0.entity.WorkflowUIInfoEnt;

import org.knime.gateway.v0.entity.WorkflowEnt;

/**
 * The structure of a workflow.
 *
 * @author Martin Horn, University of Konstanz
 */
// AUTO-GENERATED CODE; DO NOT MODIFY
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


  /**
   * The node map.
   * @return nodes
   **/
  @Override
    public java.util.Map<String, NodeEnt> getNodes() {
        return m_nodes;
    }
  /**
   * The list of connections.
   * @return connections
   **/
  @Override
    public java.util.List<ConnectionEnt> getConnections() {
        return m_connections;
    }
  /**
   * The inputs of a metanode (if this workflow is one).
   * @return metaInPortInfos
   **/
  @Override
    public java.util.List<MetaPortInfoEnt> getMetaInPortInfos() {
        return m_metaInPortInfos;
    }
  /**
   * The outputs of a metanode (if this workflow is one).
   * @return metaOutPortInfos
   **/
  @Override
    public java.util.List<MetaPortInfoEnt> getMetaOutPortInfos() {
        return m_metaOutPortInfos;
    }
  /**
   * List of all workflow annotations. TODO could be moved to an extra UI service in order to not polute the WorkflowEnt too much and separate UI logics.
   * @return workflowAnnotations
   **/
  @Override
    public java.util.List<WorkflowAnnotationEnt> getWorkflowAnnotations() {
        return m_workflowAnnotations;
    }
  /**
   * Additional workflow UI information such as grid settings, connection appearance etc. TODO could be moved to an extra UI service in order to not polute the WorkflowEnt too much and separate UI logics.
   * @return workflowUIInfo
   **/
  @Override
    public WorkflowUIInfoEnt getWorkflowUIInfo() {
        return m_workflowUIInfo;
    }
  
    public static class DefaultWorkflowEntBuilder implements WorkflowEntBuilder {
    
        public DefaultWorkflowEntBuilder(){
            
        }
    
        private java.util.Map<String, NodeEnt> m_nodes;
        private java.util.List<ConnectionEnt> m_connections;
        private java.util.List<MetaPortInfoEnt> m_metaInPortInfos;
        private java.util.List<MetaPortInfoEnt> m_metaOutPortInfos;
        private java.util.List<WorkflowAnnotationEnt> m_workflowAnnotations;
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

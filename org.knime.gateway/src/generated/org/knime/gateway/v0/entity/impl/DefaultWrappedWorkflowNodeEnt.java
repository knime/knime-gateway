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

import org.knime.gateway.v0.entity.JobManagerEnt;
import org.knime.gateway.v0.entity.NodeAnnotationEnt;
import org.knime.gateway.v0.entity.NodeInPortEnt;
import org.knime.gateway.v0.entity.NodeMessageEnt;
import org.knime.gateway.v0.entity.NodeOutPortEnt;
import org.knime.gateway.v0.entity.NodeUIInfoEnt;
import org.knime.gateway.v0.entity.impl.DefaultNodeEnt;

import org.knime.gateway.v0.entity.WrappedWorkflowNodeEnt;

/**
 * A node wrapping (referencing) a workflow (also referred to it as wrapped metanode or subnode) that almost behaves as a ordinary node.
 *
 * @author Martin Horn, University of Konstanz
 */
// AUTO-GENERATED CODE; DO NOT MODIFY
public class DefaultWrappedWorkflowNodeEnt extends DefaultNodeEnt implements WrappedWorkflowNodeEnt {

  protected java.util.List<NodeOutPortEnt> m_workflowIncomingPorts;
  protected java.util.List<NodeInPortEnt> m_workflowOutgoingPorts;
  protected Boolean m_encrypted;
  protected String m_virtualInNodeID;
  protected String m_virtualOutNodeID;
  
  
  private DefaultWrappedWorkflowNodeEnt(DefaultWrappedWorkflowNodeEntBuilder builder) {
    super();
    m_type = builder.m_type;
    m_name = builder.m_name;
    m_nodeID = builder.m_nodeID;
    m_nodeType = builder.m_nodeType;
    m_parentNodeID = builder.m_parentNodeID;
    m_rootWorkflowID = builder.m_rootWorkflowID;
    m_nodeMessage = builder.m_nodeMessage;
    m_nodeState = builder.m_nodeState;
    m_inPorts = builder.m_inPorts;
    m_outPorts = builder.m_outPorts;
    m_deletable = builder.m_deletable;
    m_hasDialog = builder.m_hasDialog;
    m_nodeAnnotation = builder.m_nodeAnnotation;
    m_jobManager = builder.m_jobManager;
    m_uIInfo = builder.m_uIInfo;
    m_workflowIncomingPorts = builder.m_workflowIncomingPorts;
    m_workflowOutgoingPorts = builder.m_workflowOutgoingPorts;
    m_encrypted = builder.m_encrypted;
    m_virtualInNodeID = builder.m_virtualInNodeID;
    m_virtualOutNodeID = builder.m_virtualOutNodeID;
  }


  /**
   * List of all incoming workflow ports.
   * @return workflowIncomingPorts
   **/
  @Override
    public java.util.List<NodeOutPortEnt> getWorkflowIncomingPorts() {
        return m_workflowIncomingPorts;
    }
  /**
   * List of all outgoing workflow ports.
   * @return workflowOutgoingPorts
   **/
  @Override
    public java.util.List<NodeInPortEnt> getWorkflowOutgoingPorts() {
        return m_workflowOutgoingPorts;
    }
  /**
   * Whether the referenced workflow is encrypted is required to be unlocked before it can be accessed.
   * @return encrypted
   **/
  @Override
    public Boolean isEncrypted() {
        return m_encrypted;
    }
  /**
   * Node ID of the virtual in-node (i.e. source).
   * @return virtualInNodeID
   **/
  @Override
    public String getVirtualInNodeID() {
        return m_virtualInNodeID;
    }
  /**
   * Node ID of the virtual out-node (i.e. sink).
   * @return virtualOutNodeID
   **/
  @Override
    public String getVirtualOutNodeID() {
        return m_virtualOutNodeID;
    }
  
    public static class DefaultWrappedWorkflowNodeEntBuilder implements WrappedWorkflowNodeEntBuilder {
    
        public DefaultWrappedWorkflowNodeEntBuilder(){
            super();
        }
    
        private String m_type;
        private String m_name;
        private String m_nodeID;
        private NodeTypeEnum m_nodeType;
        private String m_parentNodeID;
        private String m_rootWorkflowID;
        private NodeMessageEnt m_nodeMessage;
        private NodeStateEnum m_nodeState;
        private java.util.List<NodeInPortEnt> m_inPorts;
        private java.util.List<NodeOutPortEnt> m_outPorts;
        private Boolean m_deletable;
        private Boolean m_hasDialog;
        private NodeAnnotationEnt m_nodeAnnotation;
        private JobManagerEnt m_jobManager;
        private NodeUIInfoEnt m_uIInfo;
        private java.util.List<NodeOutPortEnt> m_workflowIncomingPorts;
        private java.util.List<NodeInPortEnt> m_workflowOutgoingPorts;
        private Boolean m_encrypted;
        private String m_virtualInNodeID;
        private String m_virtualOutNodeID;

        @Override
        public DefaultWrappedWorkflowNodeEntBuilder setType(String type) {
             m_type = type;
             return this;
        }
        @Override
        public DefaultWrappedWorkflowNodeEntBuilder setName(String name) {
             m_name = name;
             return this;
        }
        @Override
        public DefaultWrappedWorkflowNodeEntBuilder setNodeID(String nodeID) {
             m_nodeID = nodeID;
             return this;
        }
        @Override
        public DefaultWrappedWorkflowNodeEntBuilder setNodeType(NodeTypeEnum nodeType) {
             m_nodeType = nodeType;
             return this;
        }
        @Override
        public DefaultWrappedWorkflowNodeEntBuilder setParentNodeID(String parentNodeID) {
             m_parentNodeID = parentNodeID;
             return this;
        }
        @Override
        public DefaultWrappedWorkflowNodeEntBuilder setRootWorkflowID(String rootWorkflowID) {
             m_rootWorkflowID = rootWorkflowID;
             return this;
        }
        @Override
        public DefaultWrappedWorkflowNodeEntBuilder setNodeMessage(NodeMessageEnt nodeMessage) {
             m_nodeMessage = nodeMessage;
             return this;
        }
        @Override
        public DefaultWrappedWorkflowNodeEntBuilder setNodeState(NodeStateEnum nodeState) {
             m_nodeState = nodeState;
             return this;
        }
        @Override
        public DefaultWrappedWorkflowNodeEntBuilder setInPorts(java.util.List<NodeInPortEnt> inPorts) {
             m_inPorts = inPorts;
             return this;
        }
        @Override
        public DefaultWrappedWorkflowNodeEntBuilder setOutPorts(java.util.List<NodeOutPortEnt> outPorts) {
             m_outPorts = outPorts;
             return this;
        }
        @Override
        public DefaultWrappedWorkflowNodeEntBuilder setDeletable(Boolean deletable) {
             m_deletable = deletable;
             return this;
        }
        @Override
        public DefaultWrappedWorkflowNodeEntBuilder setHasDialog(Boolean hasDialog) {
             m_hasDialog = hasDialog;
             return this;
        }
        @Override
        public DefaultWrappedWorkflowNodeEntBuilder setNodeAnnotation(NodeAnnotationEnt nodeAnnotation) {
             m_nodeAnnotation = nodeAnnotation;
             return this;
        }
        @Override
        public DefaultWrappedWorkflowNodeEntBuilder setJobManager(JobManagerEnt jobManager) {
             m_jobManager = jobManager;
             return this;
        }
        @Override
        public DefaultWrappedWorkflowNodeEntBuilder setUIInfo(NodeUIInfoEnt uIInfo) {
             m_uIInfo = uIInfo;
             return this;
        }
        @Override
        public DefaultWrappedWorkflowNodeEntBuilder setWorkflowIncomingPorts(java.util.List<NodeOutPortEnt> workflowIncomingPorts) {
             m_workflowIncomingPorts = workflowIncomingPorts;
             return this;
        }
        @Override
        public DefaultWrappedWorkflowNodeEntBuilder setWorkflowOutgoingPorts(java.util.List<NodeInPortEnt> workflowOutgoingPorts) {
             m_workflowOutgoingPorts = workflowOutgoingPorts;
             return this;
        }
        @Override
        public DefaultWrappedWorkflowNodeEntBuilder setEncrypted(Boolean encrypted) {
             m_encrypted = encrypted;
             return this;
        }
        @Override
        public DefaultWrappedWorkflowNodeEntBuilder setVirtualInNodeID(String virtualInNodeID) {
             m_virtualInNodeID = virtualInNodeID;
             return this;
        }
        @Override
        public DefaultWrappedWorkflowNodeEntBuilder setVirtualOutNodeID(String virtualOutNodeID) {
             m_virtualOutNodeID = virtualOutNodeID;
             return this;
        }
        
        @Override
        public DefaultWrappedWorkflowNodeEnt build() {
            return new DefaultWrappedWorkflowNodeEnt(this);
        }
    
    }

}

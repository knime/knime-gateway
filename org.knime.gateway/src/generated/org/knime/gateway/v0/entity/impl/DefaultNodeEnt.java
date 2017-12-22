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

import org.knime.gateway.v0.entity.NodeEnt;

/**
 * A node.
 *
 * @author Martin Horn, University of Konstanz
 */
// AUTO-GENERATED CODE; DO NOT MODIFY
public class DefaultNodeEnt  implements NodeEnt {

  protected String m_type;
  protected String m_name;
  protected String m_nodeID;
  protected NodeTypeEnum m_nodeType;
  protected String m_parentNodeID;
  protected String m_rootWorkflowID;
  protected NodeMessageEnt m_nodeMessage;
  protected NodeStateEnum m_nodeState;
  protected java.util.List<NodeInPortEnt> m_inPorts;
  protected java.util.List<NodeOutPortEnt> m_outPorts;
  protected Boolean m_deletable;
  protected Boolean m_hasDialog;
  protected NodeAnnotationEnt m_nodeAnnotation;
  protected JobManagerEnt m_jobManager;
  protected NodeUIInfoEnt m_uIInfo;
  
  protected DefaultNodeEnt() {
    //for sub-classes
  }
  
  private DefaultNodeEnt(DefaultNodeEntBuilder builder) {
    
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
  }


  /**
   * Discriminator for inheritance.
   * @return type
   **/
  @Override
    public String getType() {
        return m_type;
    }
  /**
   * The node&#39;s name.
   * @return name
   **/
  @Override
    public String getName() {
        return m_name;
    }
  /**
   * The ID of the node.
   * @return nodeID
   **/
  @Override
    public String getNodeID() {
        return m_nodeID;
    }
  /**
   * The type of the node.
   * @return nodeType
   **/
  @Override
    public NodeTypeEnum getNodeType() {
        return m_nodeType;
    }
  /**
   * The parent node id of the node or not present if it&#39;s the root node.
   * @return parentNodeID
   **/
  @Override
    public String getParentNodeID() {
        return m_parentNodeID;
    }
  /**
   * The id of the root workflow this node is contained in or represents.
   * @return rootWorkflowID
   **/
  @Override
    public String getRootWorkflowID() {
        return m_rootWorkflowID;
    }
  /**
   * The current node message (warning, error, none).
   * @return nodeMessage
   **/
  @Override
    public NodeMessageEnt getNodeMessage() {
        return m_nodeMessage;
    }
  /**
   * The state of the node.
   * @return nodeState
   **/
  @Override
    public NodeStateEnum getNodeState() {
        return m_nodeState;
    }
  /**
   * The list of inputs.
   * @return inPorts
   **/
  @Override
    public java.util.List<NodeInPortEnt> getInPorts() {
        return m_inPorts;
    }
  /**
   * The list of outputs.
   * @return outPorts
   **/
  @Override
    public java.util.List<NodeOutPortEnt> getOutPorts() {
        return m_outPorts;
    }
  /**
   * Whether the node is deletable.
   * @return deletable
   **/
  @Override
    public Boolean isDeletable() {
        return m_deletable;
    }
  /**
   * Whether the node has a configuration dialog / user settings.
   * @return hasDialog
   **/
  @Override
    public Boolean isHasDialog() {
        return m_hasDialog;
    }
  /**
   * The annotation below the node.
   * @return nodeAnnotation
   **/
  @Override
    public NodeAnnotationEnt getNodeAnnotation() {
        return m_nodeAnnotation;
    }
  /**
   * The job manager (e.g. cluster or streaming).
   * @return jobManager
   **/
  @Override
    public JobManagerEnt getJobManager() {
        return m_jobManager;
    }
  /**
   * Get uIInfo
   * @return uIInfo
   **/
  @Override
    public NodeUIInfoEnt getUIInfo() {
        return m_uIInfo;
    }
  
    public static class DefaultNodeEntBuilder implements NodeEntBuilder {
    
        public DefaultNodeEntBuilder(){
            
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

        @Override
        public DefaultNodeEntBuilder setType(String type) {
             m_type = type;
             return this;
        }
        @Override
        public DefaultNodeEntBuilder setName(String name) {
             m_name = name;
             return this;
        }
        @Override
        public DefaultNodeEntBuilder setNodeID(String nodeID) {
             m_nodeID = nodeID;
             return this;
        }
        @Override
        public DefaultNodeEntBuilder setNodeType(NodeTypeEnum nodeType) {
             m_nodeType = nodeType;
             return this;
        }
        @Override
        public DefaultNodeEntBuilder setParentNodeID(String parentNodeID) {
             m_parentNodeID = parentNodeID;
             return this;
        }
        @Override
        public DefaultNodeEntBuilder setRootWorkflowID(String rootWorkflowID) {
             m_rootWorkflowID = rootWorkflowID;
             return this;
        }
        @Override
        public DefaultNodeEntBuilder setNodeMessage(NodeMessageEnt nodeMessage) {
             m_nodeMessage = nodeMessage;
             return this;
        }
        @Override
        public DefaultNodeEntBuilder setNodeState(NodeStateEnum nodeState) {
             m_nodeState = nodeState;
             return this;
        }
        @Override
        public DefaultNodeEntBuilder setInPorts(java.util.List<NodeInPortEnt> inPorts) {
             m_inPorts = inPorts;
             return this;
        }
        @Override
        public DefaultNodeEntBuilder setOutPorts(java.util.List<NodeOutPortEnt> outPorts) {
             m_outPorts = outPorts;
             return this;
        }
        @Override
        public DefaultNodeEntBuilder setDeletable(Boolean deletable) {
             m_deletable = deletable;
             return this;
        }
        @Override
        public DefaultNodeEntBuilder setHasDialog(Boolean hasDialog) {
             m_hasDialog = hasDialog;
             return this;
        }
        @Override
        public DefaultNodeEntBuilder setNodeAnnotation(NodeAnnotationEnt nodeAnnotation) {
             m_nodeAnnotation = nodeAnnotation;
             return this;
        }
        @Override
        public DefaultNodeEntBuilder setJobManager(JobManagerEnt jobManager) {
             m_jobManager = jobManager;
             return this;
        }
        @Override
        public DefaultNodeEntBuilder setUIInfo(NodeUIInfoEnt uIInfo) {
             m_uIInfo = uIInfo;
             return this;
        }
        
        @Override
        public DefaultNodeEnt build() {
            return new DefaultNodeEnt(this);
        }
    
    }

}

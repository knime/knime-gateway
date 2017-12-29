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
    
    if(builder.m_type == null) {
        throw new IllegalArgumentException("type must not be null.");
    }
    m_type = builder.m_type;
    if(builder.m_name == null) {
        throw new IllegalArgumentException("name must not be null.");
    }
    m_name = builder.m_name;
    if(builder.m_nodeID == null) {
        throw new IllegalArgumentException("nodeID must not be null.");
    }
    m_nodeID = builder.m_nodeID;
    if(builder.m_nodeType == null) {
        throw new IllegalArgumentException("nodeType must not be null.");
    }
    m_nodeType = builder.m_nodeType;
    m_parentNodeID = builder.m_parentNodeID;
    if(builder.m_rootWorkflowID == null) {
        throw new IllegalArgumentException("rootWorkflowID must not be null.");
    }
    m_rootWorkflowID = builder.m_rootWorkflowID;
    m_nodeMessage = builder.m_nodeMessage;
    if(builder.m_nodeState == null) {
        throw new IllegalArgumentException("nodeState must not be null.");
    }
    m_nodeState = builder.m_nodeState;
    m_inPorts = builder.m_inPorts;
    m_outPorts = builder.m_outPorts;
    m_deletable = builder.m_deletable;
    m_hasDialog = builder.m_hasDialog;
    m_nodeAnnotation = builder.m_nodeAnnotation;
    m_jobManager = builder.m_jobManager;
    m_uIInfo = builder.m_uIInfo;
  }


  @Override
  public String getType() {
        return m_type;
    }
    
  @Override
  public String getName() {
        return m_name;
    }
    
  @Override
  public String getNodeID() {
        return m_nodeID;
    }
    
  @Override
  public NodeTypeEnum getNodeType() {
        return m_nodeType;
    }
    
  @Override
  public String getParentNodeID() {
        return m_parentNodeID;
    }
    
  @Override
  public String getRootWorkflowID() {
        return m_rootWorkflowID;
    }
    
  @Override
  public NodeMessageEnt getNodeMessage() {
        return m_nodeMessage;
    }
    
  @Override
  public NodeStateEnum getNodeState() {
        return m_nodeState;
    }
    
  @Override
  public java.util.List<NodeInPortEnt> getInPorts() {
        return m_inPorts;
    }
    
  @Override
  public java.util.List<NodeOutPortEnt> getOutPorts() {
        return m_outPorts;
    }
    
  @Override
  public Boolean isDeletable() {
        return m_deletable;
    }
    
  @Override
  public Boolean isHasDialog() {
        return m_hasDialog;
    }
    
  @Override
  public NodeAnnotationEnt getNodeAnnotation() {
        return m_nodeAnnotation;
    }
    
  @Override
  public JobManagerEnt getJobManager() {
        return m_jobManager;
    }
    
  @Override
  public NodeUIInfoEnt getUIInfo() {
        return m_uIInfo;
    }
    
  
    public static class DefaultNodeEntBuilder implements NodeEntBuilder {
    
        public DefaultNodeEntBuilder(){
            
        }
    
        private String m_type = null;
        private String m_name = null;
        private String m_nodeID = null;
        private NodeTypeEnum m_nodeType = null;
        private String m_parentNodeID = null;
        private String m_rootWorkflowID = null;
        private NodeMessageEnt m_nodeMessage;
        private NodeStateEnum m_nodeState = null;
        private java.util.List<NodeInPortEnt> m_inPorts = new java.util.ArrayList<>();
        private java.util.List<NodeOutPortEnt> m_outPorts = new java.util.ArrayList<>();
        private Boolean m_deletable = false;
        private Boolean m_hasDialog = null;
        private NodeAnnotationEnt m_nodeAnnotation;
        private JobManagerEnt m_jobManager;
        private NodeUIInfoEnt m_uIInfo;

        @Override
        public DefaultNodeEntBuilder setType(String type) {
             if(type == null) {
                 throw new IllegalArgumentException("type must not be null.");
             }
             m_type = type;
             return this;
        }

        @Override
        public DefaultNodeEntBuilder setName(String name) {
             if(name == null) {
                 throw new IllegalArgumentException("name must not be null.");
             }
             m_name = name;
             return this;
        }

        @Override
        public DefaultNodeEntBuilder setNodeID(String nodeID) {
             if(nodeID == null) {
                 throw new IllegalArgumentException("nodeID must not be null.");
             }
             m_nodeID = nodeID;
             return this;
        }

        @Override
        public DefaultNodeEntBuilder setNodeType(NodeTypeEnum nodeType) {
             if(nodeType == null) {
                 throw new IllegalArgumentException("nodeType must not be null.");
             }
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
             if(rootWorkflowID == null) {
                 throw new IllegalArgumentException("rootWorkflowID must not be null.");
             }
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
             if(nodeState == null) {
                 throw new IllegalArgumentException("nodeState must not be null.");
             }
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

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

import com.knime.gateway.v0.entity.JobManagerEnt;
import com.knime.gateway.v0.entity.NodeAnnotationEnt;
import com.knime.gateway.v0.entity.NodeInPortEnt;
import com.knime.gateway.v0.entity.NodeMessageEnt;
import com.knime.gateway.v0.entity.NodeOutPortEnt;
import com.knime.gateway.v0.entity.NodeProgressEnt;
import com.knime.gateway.v0.entity.NodeStateEnt;
import com.knime.gateway.v0.entity.NodeUIInfoEnt;

import com.knime.gateway.v0.entity.NodeEnt;

/**
 * A node.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
@javax.annotation.Generated(value = "org.knime.gateway.codegen.GatewayCodegen")
public class DefaultNodeEnt  implements NodeEnt {

  protected String m_type;
  protected String m_name;
  protected String m_nodeID;
  protected NodeTypeEnum m_nodeType;
  protected String m_parentNodeID;
  protected java.util.UUID m_rootWorkflowID;
  protected NodeMessageEnt m_nodeMessage;
  protected NodeStateEnt m_nodeState;
  protected NodeProgressEnt m_progress;
  protected java.util.List<NodeInPortEnt> m_inPorts;
  protected java.util.List<NodeOutPortEnt> m_outPorts;
  protected Boolean m_deletable;
  protected Boolean m_resetable;
  protected Boolean m_hasDialog;
  protected NodeAnnotationEnt m_nodeAnnotation;
  protected JobManagerEnt m_jobManager;
  protected NodeUIInfoEnt m_uIInfo;
  
  protected DefaultNodeEnt() {
    //for sub-classes
  }
  
  @Override
  public String getTypeID() {
    return "Node";
  }
  
  private DefaultNodeEnt(DefaultNodeEntBuilder builder) {
    
    if(builder.m_type == null) {
        throw new IllegalArgumentException("type must not be null.");
    }
    m_type = immutable(builder.m_type);
    if(builder.m_name == null) {
        throw new IllegalArgumentException("name must not be null.");
    }
    m_name = immutable(builder.m_name);
    if(builder.m_nodeID == null) {
        throw new IllegalArgumentException("nodeID must not be null.");
    }
    m_nodeID = immutable(builder.m_nodeID);
    if(builder.m_nodeType == null) {
        throw new IllegalArgumentException("nodeType must not be null.");
    }
    m_nodeType = immutable(builder.m_nodeType);
    m_parentNodeID = immutable(builder.m_parentNodeID);
    if(builder.m_rootWorkflowID == null) {
        throw new IllegalArgumentException("rootWorkflowID must not be null.");
    }
    m_rootWorkflowID = immutable(builder.m_rootWorkflowID);
    m_nodeMessage = immutable(builder.m_nodeMessage);
    if(builder.m_nodeState == null) {
        throw new IllegalArgumentException("nodeState must not be null.");
    }
    m_nodeState = immutable(builder.m_nodeState);
    m_progress = immutable(builder.m_progress);
    m_inPorts = immutable(builder.m_inPorts);
    m_outPorts = immutable(builder.m_outPorts);
    m_deletable = immutable(builder.m_deletable);
    m_resetable = immutable(builder.m_resetable);
    m_hasDialog = immutable(builder.m_hasDialog);
    m_nodeAnnotation = immutable(builder.m_nodeAnnotation);
    m_jobManager = immutable(builder.m_jobManager);
    m_uIInfo = immutable(builder.m_uIInfo);
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
        DefaultNodeEnt ent = (DefaultNodeEnt)o;
        return Objects.equals(m_type, ent.m_type) && Objects.equals(m_name, ent.m_name) && Objects.equals(m_nodeID, ent.m_nodeID) && Objects.equals(m_nodeType, ent.m_nodeType) && Objects.equals(m_parentNodeID, ent.m_parentNodeID) && Objects.equals(m_rootWorkflowID, ent.m_rootWorkflowID) && Objects.equals(m_nodeMessage, ent.m_nodeMessage) && Objects.equals(m_nodeState, ent.m_nodeState) && Objects.equals(m_progress, ent.m_progress) && Objects.equals(m_inPorts, ent.m_inPorts) && Objects.equals(m_outPorts, ent.m_outPorts) && Objects.equals(m_deletable, ent.m_deletable) && Objects.equals(m_resetable, ent.m_resetable) && Objects.equals(m_hasDialog, ent.m_hasDialog) && Objects.equals(m_nodeAnnotation, ent.m_nodeAnnotation) && Objects.equals(m_jobManager, ent.m_jobManager) && Objects.equals(m_uIInfo, ent.m_uIInfo);
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
  public java.util.UUID getRootWorkflowID() {
        return m_rootWorkflowID;
    }
    
  @Override
  public NodeMessageEnt getNodeMessage() {
        return m_nodeMessage;
    }
    
  @Override
  public NodeStateEnt getNodeState() {
        return m_nodeState;
    }
    
  @Override
  public NodeProgressEnt getProgress() {
        return m_progress;
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
  public Boolean isResetable() {
        return m_resetable;
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
        private java.util.UUID m_rootWorkflowID = null;
        private NodeMessageEnt m_nodeMessage;
        private NodeStateEnt m_nodeState;
        private NodeProgressEnt m_progress;
        private java.util.List<NodeInPortEnt> m_inPorts = new java.util.ArrayList<>();
        private java.util.List<NodeOutPortEnt> m_outPorts = new java.util.ArrayList<>();
        private Boolean m_deletable = false;
        private Boolean m_resetable = false;
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
        public DefaultNodeEntBuilder setRootWorkflowID(java.util.UUID rootWorkflowID) {
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
        public DefaultNodeEntBuilder setNodeState(NodeStateEnt nodeState) {
             if(nodeState == null) {
                 throw new IllegalArgumentException("nodeState must not be null.");
             }
             m_nodeState = nodeState;
             return this;
        }

        @Override
        public DefaultNodeEntBuilder setProgress(NodeProgressEnt progress) {
             m_progress = progress;
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
        public DefaultNodeEntBuilder setResetable(Boolean resetable) {
             m_resetable = resetable;
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

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

import com.knime.gateway.v0.entity.JobManagerEnt;
import com.knime.gateway.v0.entity.NodeAnnotationEnt;
import com.knime.gateway.v0.entity.NodeInPortEnt;
import com.knime.gateway.v0.entity.NodeMessageEnt;
import com.knime.gateway.v0.entity.NodeOutPortEnt;
import com.knime.gateway.v0.entity.NodeUIInfoEnt;
import com.knime.gateway.v0.entity.impl.DefaultNodeEnt;

import com.knime.gateway.v0.entity.WrappedWorkflowNodeEnt;

/**
 * A node wrapping (referencing) a workflow (also referred to it as wrapped metanode or subnode) that almost behaves as a ordinary node.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
@javax.annotation.Generated(value = "org.knime.gateway.codegen.GatewayCodegen")
public class DefaultWrappedWorkflowNodeEnt extends DefaultNodeEnt implements WrappedWorkflowNodeEnt {

  protected java.util.List<NodeOutPortEnt> m_workflowIncomingPorts;
  protected java.util.List<NodeInPortEnt> m_workflowOutgoingPorts;
  protected Boolean m_encrypted;
  protected String m_virtualInNodeID;
  protected String m_virtualOutNodeID;
  
  
  private DefaultWrappedWorkflowNodeEnt(DefaultWrappedWorkflowNodeEntBuilder builder) {
    super();
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
    m_workflowIncomingPorts = builder.m_workflowIncomingPorts;
    m_workflowOutgoingPorts = builder.m_workflowOutgoingPorts;
    m_encrypted = builder.m_encrypted;
    m_virtualInNodeID = builder.m_virtualInNodeID;
    m_virtualOutNodeID = builder.m_virtualOutNodeID;
  }


  @Override
  public java.util.List<NodeOutPortEnt> getWorkflowIncomingPorts() {
        return m_workflowIncomingPorts;
    }
    
  @Override
  public java.util.List<NodeInPortEnt> getWorkflowOutgoingPorts() {
        return m_workflowOutgoingPorts;
    }
    
  @Override
  public Boolean isEncrypted() {
        return m_encrypted;
    }
    
  @Override
  public String getVirtualInNodeID() {
        return m_virtualInNodeID;
    }
    
  @Override
  public String getVirtualOutNodeID() {
        return m_virtualOutNodeID;
    }
    
  
    public static class DefaultWrappedWorkflowNodeEntBuilder implements WrappedWorkflowNodeEntBuilder {
    
        public DefaultWrappedWorkflowNodeEntBuilder(){
            super();
        }
    
        private String m_type = null;
        private String m_name = null;
        private String m_nodeID = null;
        private NodeTypeEnum m_nodeType = null;
        private String m_parentNodeID = null;
        private java.util.UUID m_rootWorkflowID = null;
        private NodeMessageEnt m_nodeMessage;
        private NodeStateEnum m_nodeState = null;
        private java.util.List<NodeInPortEnt> m_inPorts = new java.util.ArrayList<>();
        private java.util.List<NodeOutPortEnt> m_outPorts = new java.util.ArrayList<>();
        private Boolean m_deletable = false;
        private Boolean m_hasDialog = null;
        private NodeAnnotationEnt m_nodeAnnotation;
        private JobManagerEnt m_jobManager;
        private NodeUIInfoEnt m_uIInfo;
        private java.util.List<NodeOutPortEnt> m_workflowIncomingPorts = new java.util.ArrayList<>();
        private java.util.List<NodeInPortEnt> m_workflowOutgoingPorts = new java.util.ArrayList<>();
        private Boolean m_encrypted = null;
        private String m_virtualInNodeID = null;
        private String m_virtualOutNodeID = null;

        @Override
        public DefaultWrappedWorkflowNodeEntBuilder setType(String type) {
             if(type == null) {
                 throw new IllegalArgumentException("type must not be null.");
             }
             m_type = type;
             return this;
        }

        @Override
        public DefaultWrappedWorkflowNodeEntBuilder setName(String name) {
             if(name == null) {
                 throw new IllegalArgumentException("name must not be null.");
             }
             m_name = name;
             return this;
        }

        @Override
        public DefaultWrappedWorkflowNodeEntBuilder setNodeID(String nodeID) {
             if(nodeID == null) {
                 throw new IllegalArgumentException("nodeID must not be null.");
             }
             m_nodeID = nodeID;
             return this;
        }

        @Override
        public DefaultWrappedWorkflowNodeEntBuilder setNodeType(NodeTypeEnum nodeType) {
             if(nodeType == null) {
                 throw new IllegalArgumentException("nodeType must not be null.");
             }
             m_nodeType = nodeType;
             return this;
        }

        @Override
        public DefaultWrappedWorkflowNodeEntBuilder setParentNodeID(String parentNodeID) {
             m_parentNodeID = parentNodeID;
             return this;
        }

        @Override
        public DefaultWrappedWorkflowNodeEntBuilder setRootWorkflowID(java.util.UUID rootWorkflowID) {
             if(rootWorkflowID == null) {
                 throw new IllegalArgumentException("rootWorkflowID must not be null.");
             }
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
             if(nodeState == null) {
                 throw new IllegalArgumentException("nodeState must not be null.");
             }
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

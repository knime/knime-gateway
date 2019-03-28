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
package com.knime.gateway.entity.impl;

import static com.knime.gateway.util.EntityUtil.immutable;

import java.util.Objects;

import com.knime.gateway.entity.JobManagerEnt;
import com.knime.gateway.entity.NodeAnnotationEnt;
import com.knime.gateway.entity.NodeFactoryKeyEnt;
import com.knime.gateway.entity.NodeInPortEnt;
import com.knime.gateway.entity.NodeMessageEnt;
import com.knime.gateway.entity.NodeOutPortEnt;
import com.knime.gateway.entity.NodeProgressEnt;
import com.knime.gateway.entity.NodeStateEnt;
import com.knime.gateway.entity.NodeUIInfoEnt;
import com.knime.gateway.entity.impl.DefaultNodeEnt;

import com.knime.gateway.entity.NativeNodeEnt;

/**
 * Native node extension of a node.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
@javax.annotation.Generated(value = {"org.knime.gateway.codegen.GatewayCodegen", "src-gen/com.knime.gateway-implementations-config.json"})
public class DefaultNativeNodeEnt extends DefaultNodeEnt implements NativeNodeEnt {

  protected NodeFactoryKeyEnt m_nodeFactoryKey;
  protected Boolean m_inactive;
  
  protected DefaultNativeNodeEnt() {
    //for sub-classes
  }
  
  @Override
  public String getTypeID() {
    return "NativeNode";
  }
  
  private DefaultNativeNodeEnt(DefaultNativeNodeEntBuilder builder) {
    super();
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
    m_webViewNames = immutable(builder.m_webViewNames);
    m_jobManager = immutable(builder.m_jobManager);
    m_uIInfo = immutable(builder.m_uIInfo);
    if(builder.m_nodeFactoryKey == null) {
        throw new IllegalArgumentException("nodeFactoryKey must not be null.");
    }
    m_nodeFactoryKey = immutable(builder.m_nodeFactoryKey);
    m_inactive = immutable(builder.m_inactive);
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
        DefaultNativeNodeEnt ent = (DefaultNativeNodeEnt)o;
        return Objects.equals(m_type, ent.m_type) && Objects.equals(m_name, ent.m_name) && Objects.equals(m_nodeID, ent.m_nodeID) && Objects.equals(m_nodeType, ent.m_nodeType) && Objects.equals(m_parentNodeID, ent.m_parentNodeID) && Objects.equals(m_rootWorkflowID, ent.m_rootWorkflowID) && Objects.equals(m_nodeMessage, ent.m_nodeMessage) && Objects.equals(m_nodeState, ent.m_nodeState) && Objects.equals(m_progress, ent.m_progress) && Objects.equals(m_inPorts, ent.m_inPorts) && Objects.equals(m_outPorts, ent.m_outPorts) && Objects.equals(m_deletable, ent.m_deletable) && Objects.equals(m_resetable, ent.m_resetable) && Objects.equals(m_hasDialog, ent.m_hasDialog) && Objects.equals(m_nodeAnnotation, ent.m_nodeAnnotation) && Objects.equals(m_webViewNames, ent.m_webViewNames) && Objects.equals(m_jobManager, ent.m_jobManager) && Objects.equals(m_uIInfo, ent.m_uIInfo) && Objects.equals(m_nodeFactoryKey, ent.m_nodeFactoryKey) && Objects.equals(m_inactive, ent.m_inactive);
    }


  @Override
  public NodeFactoryKeyEnt getNodeFactoryKey() {
        return m_nodeFactoryKey;
    }
    
  @Override
  public Boolean isInactive() {
        return m_inactive;
    }
    
  
    public static class DefaultNativeNodeEntBuilder implements NativeNodeEntBuilder {
    
        public DefaultNativeNodeEntBuilder(){
            super();
        }
    
        private String m_type;
        private String m_name;
        private String m_nodeID;
        private NodeTypeEnum m_nodeType;
        private String m_parentNodeID;
        private java.util.UUID m_rootWorkflowID;
        private NodeMessageEnt m_nodeMessage;
        private NodeStateEnt m_nodeState;
        private NodeProgressEnt m_progress;
        private java.util.List<NodeInPortEnt> m_inPorts = new java.util.ArrayList<>();
        private java.util.List<NodeOutPortEnt> m_outPorts = new java.util.ArrayList<>();
        private Boolean m_deletable = false;
        private Boolean m_resetable = false;
        private Boolean m_hasDialog;
        private NodeAnnotationEnt m_nodeAnnotation;
        private java.util.List<String> m_webViewNames = new java.util.ArrayList<>();
        private JobManagerEnt m_jobManager;
        private NodeUIInfoEnt m_uIInfo;
        private NodeFactoryKeyEnt m_nodeFactoryKey;
        private Boolean m_inactive;

        @Override
        public DefaultNativeNodeEntBuilder setType(String type) {
             if(type == null) {
                 throw new IllegalArgumentException("type must not be null.");
             }
             m_type = type;
             return this;
        }

        @Override
        public DefaultNativeNodeEntBuilder setName(String name) {
             if(name == null) {
                 throw new IllegalArgumentException("name must not be null.");
             }
             m_name = name;
             return this;
        }

        @Override
        public DefaultNativeNodeEntBuilder setNodeID(String nodeID) {
             if(nodeID == null) {
                 throw new IllegalArgumentException("nodeID must not be null.");
             }
             m_nodeID = nodeID;
             return this;
        }

        @Override
        public DefaultNativeNodeEntBuilder setNodeType(NodeTypeEnum nodeType) {
             if(nodeType == null) {
                 throw new IllegalArgumentException("nodeType must not be null.");
             }
             m_nodeType = nodeType;
             return this;
        }

        @Override
        public DefaultNativeNodeEntBuilder setParentNodeID(String parentNodeID) {
             m_parentNodeID = parentNodeID;
             return this;
        }

        @Override
        public DefaultNativeNodeEntBuilder setRootWorkflowID(java.util.UUID rootWorkflowID) {
             if(rootWorkflowID == null) {
                 throw new IllegalArgumentException("rootWorkflowID must not be null.");
             }
             m_rootWorkflowID = rootWorkflowID;
             return this;
        }

        @Override
        public DefaultNativeNodeEntBuilder setNodeMessage(NodeMessageEnt nodeMessage) {
             m_nodeMessage = nodeMessage;
             return this;
        }

        @Override
        public DefaultNativeNodeEntBuilder setNodeState(NodeStateEnt nodeState) {
             if(nodeState == null) {
                 throw new IllegalArgumentException("nodeState must not be null.");
             }
             m_nodeState = nodeState;
             return this;
        }

        @Override
        public DefaultNativeNodeEntBuilder setProgress(NodeProgressEnt progress) {
             m_progress = progress;
             return this;
        }

        @Override
        public DefaultNativeNodeEntBuilder setInPorts(java.util.List<NodeInPortEnt> inPorts) {
             m_inPorts = inPorts;
             return this;
        }

        @Override
        public DefaultNativeNodeEntBuilder setOutPorts(java.util.List<NodeOutPortEnt> outPorts) {
             m_outPorts = outPorts;
             return this;
        }

        @Override
        public DefaultNativeNodeEntBuilder setDeletable(Boolean deletable) {
             m_deletable = deletable;
             return this;
        }

        @Override
        public DefaultNativeNodeEntBuilder setResetable(Boolean resetable) {
             m_resetable = resetable;
             return this;
        }

        @Override
        public DefaultNativeNodeEntBuilder setHasDialog(Boolean hasDialog) {
             m_hasDialog = hasDialog;
             return this;
        }

        @Override
        public DefaultNativeNodeEntBuilder setNodeAnnotation(NodeAnnotationEnt nodeAnnotation) {
             m_nodeAnnotation = nodeAnnotation;
             return this;
        }

        @Override
        public DefaultNativeNodeEntBuilder setWebViewNames(java.util.List<String> webViewNames) {
             m_webViewNames = webViewNames;
             return this;
        }

        @Override
        public DefaultNativeNodeEntBuilder setJobManager(JobManagerEnt jobManager) {
             m_jobManager = jobManager;
             return this;
        }

        @Override
        public DefaultNativeNodeEntBuilder setUIInfo(NodeUIInfoEnt uIInfo) {
             m_uIInfo = uIInfo;
             return this;
        }

        @Override
        public DefaultNativeNodeEntBuilder setNodeFactoryKey(NodeFactoryKeyEnt nodeFactoryKey) {
             if(nodeFactoryKey == null) {
                 throw new IllegalArgumentException("nodeFactoryKey must not be null.");
             }
             m_nodeFactoryKey = nodeFactoryKey;
             return this;
        }

        @Override
        public DefaultNativeNodeEntBuilder setInactive(Boolean inactive) {
             m_inactive = inactive;
             return this;
        }

        
        @Override
        public DefaultNativeNodeEnt build() {
            return new DefaultNativeNodeEnt(this);
        }
    
    }

}

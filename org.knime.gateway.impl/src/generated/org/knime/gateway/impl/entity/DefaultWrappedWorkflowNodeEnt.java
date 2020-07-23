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

import org.knime.gateway.api.entity.JobManagerEnt;
import org.knime.gateway.api.entity.NodeAnnotationEnt;
import org.knime.gateway.api.entity.NodeInPortEnt;
import org.knime.gateway.api.entity.NodeMessageEnt;
import org.knime.gateway.api.entity.NodeOutPortEnt;
import org.knime.gateway.api.entity.NodeProgressEnt;
import org.knime.gateway.api.entity.NodeStateEnt;
import org.knime.gateway.api.entity.NodeUIInfoEnt;
import org.knime.gateway.impl.entity.DefaultWorkflowNodeEnt;

import org.knime.gateway.api.entity.WrappedWorkflowNodeEnt;

/**
 * A node wrapping (referencing) a workflow (also referred to it as component or subnode) that almost behaves as a ordinary node.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
@javax.annotation.Generated(value = {"com.knime.gateway.codegen.GatewayCodegen", "src-gen/org.knime.gateway.impl-config.json"})
public class DefaultWrappedWorkflowNodeEnt extends DefaultWorkflowNodeEnt implements WrappedWorkflowNodeEnt {

  protected org.knime.gateway.api.entity.NodeIDEnt m_virtualInNodeID;
  protected org.knime.gateway.api.entity.NodeIDEnt m_virtualOutNodeID;
  protected Boolean m_inactive;
  protected Boolean m_hasWizardPage;
  
  protected DefaultWrappedWorkflowNodeEnt() {
    //for sub-classes
  }
  
  @Override
  public String getTypeID() {
    return "WrappedWorkflowNode";
  }
  
  private DefaultWrappedWorkflowNodeEnt(DefaultWrappedWorkflowNodeEntBuilder builder) {
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
    m_workflowIncomingPorts = immutable(builder.m_workflowIncomingPorts);
    m_workflowOutgoingPorts = immutable(builder.m_workflowOutgoingPorts);
    m_encrypted = immutable(builder.m_encrypted);
    m_workflowOutgoingPortNodeStates = immutable(builder.m_workflowOutgoingPortNodeStates);
    m_virtualInNodeID = immutable(builder.m_virtualInNodeID);
    m_virtualOutNodeID = immutable(builder.m_virtualOutNodeID);
    m_inactive = immutable(builder.m_inactive);
    m_hasWizardPage = immutable(builder.m_hasWizardPage);
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
        DefaultWrappedWorkflowNodeEnt ent = (DefaultWrappedWorkflowNodeEnt)o;
        return Objects.equals(m_type, ent.m_type) && Objects.equals(m_name, ent.m_name) && Objects.equals(m_nodeID, ent.m_nodeID) && Objects.equals(m_nodeType, ent.m_nodeType) && Objects.equals(m_parentNodeID, ent.m_parentNodeID) && Objects.equals(m_rootWorkflowID, ent.m_rootWorkflowID) && Objects.equals(m_nodeMessage, ent.m_nodeMessage) && Objects.equals(m_nodeState, ent.m_nodeState) && Objects.equals(m_progress, ent.m_progress) && Objects.equals(m_inPorts, ent.m_inPorts) && Objects.equals(m_outPorts, ent.m_outPorts) && Objects.equals(m_deletable, ent.m_deletable) && Objects.equals(m_resetable, ent.m_resetable) && Objects.equals(m_hasDialog, ent.m_hasDialog) && Objects.equals(m_nodeAnnotation, ent.m_nodeAnnotation) && Objects.equals(m_webViewNames, ent.m_webViewNames) && Objects.equals(m_jobManager, ent.m_jobManager) && Objects.equals(m_uIInfo, ent.m_uIInfo) && Objects.equals(m_workflowIncomingPorts, ent.m_workflowIncomingPorts) && Objects.equals(m_workflowOutgoingPorts, ent.m_workflowOutgoingPorts) && Objects.equals(m_encrypted, ent.m_encrypted) && Objects.equals(m_workflowOutgoingPortNodeStates, ent.m_workflowOutgoingPortNodeStates) && Objects.equals(m_virtualInNodeID, ent.m_virtualInNodeID) && Objects.equals(m_virtualOutNodeID, ent.m_virtualOutNodeID) && Objects.equals(m_inactive, ent.m_inactive) && Objects.equals(m_hasWizardPage, ent.m_hasWizardPage);
    }


  @Override
  public org.knime.gateway.api.entity.NodeIDEnt getVirtualInNodeID() {
        return m_virtualInNodeID;
    }
    
  @Override
  public org.knime.gateway.api.entity.NodeIDEnt getVirtualOutNodeID() {
        return m_virtualOutNodeID;
    }
    
  @Override
  public Boolean isInactive() {
        return m_inactive;
    }
    
  @Override
  public Boolean hasWizardPage() {
        return m_hasWizardPage;
    }
    
  
    public static class DefaultWrappedWorkflowNodeEntBuilder implements WrappedWorkflowNodeEntBuilder {
    
        public DefaultWrappedWorkflowNodeEntBuilder(){
            super();
        }
    
        private String m_type;
        private String m_name;
        private org.knime.gateway.api.entity.NodeIDEnt m_nodeID;
        private NodeTypeEnum m_nodeType;
        private org.knime.gateway.api.entity.NodeIDEnt m_parentNodeID;
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
        private java.util.List<NodeOutPortEnt> m_workflowIncomingPorts = new java.util.ArrayList<>();
        private java.util.List<NodeInPortEnt> m_workflowOutgoingPorts = new java.util.ArrayList<>();
        private Boolean m_encrypted;
        private java.util.List<NodeStateEnt> m_workflowOutgoingPortNodeStates = new java.util.ArrayList<>();
        private org.knime.gateway.api.entity.NodeIDEnt m_virtualInNodeID;
        private org.knime.gateway.api.entity.NodeIDEnt m_virtualOutNodeID;
        private Boolean m_inactive;
        private Boolean m_hasWizardPage;

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
        public DefaultWrappedWorkflowNodeEntBuilder setNodeID(org.knime.gateway.api.entity.NodeIDEnt nodeID) {
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
        public DefaultWrappedWorkflowNodeEntBuilder setParentNodeID(org.knime.gateway.api.entity.NodeIDEnt parentNodeID) {
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
        public DefaultWrappedWorkflowNodeEntBuilder setNodeState(NodeStateEnt nodeState) {
             if(nodeState == null) {
                 throw new IllegalArgumentException("nodeState must not be null.");
             }
             m_nodeState = nodeState;
             return this;
        }

        @Override
        public DefaultWrappedWorkflowNodeEntBuilder setProgress(NodeProgressEnt progress) {
             m_progress = progress;
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
        public DefaultWrappedWorkflowNodeEntBuilder setResetable(Boolean resetable) {
             m_resetable = resetable;
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
        public DefaultWrappedWorkflowNodeEntBuilder setWebViewNames(java.util.List<String> webViewNames) {
             m_webViewNames = webViewNames;
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
        public DefaultWrappedWorkflowNodeEntBuilder setWorkflowOutgoingPortNodeStates(java.util.List<NodeStateEnt> workflowOutgoingPortNodeStates) {
             m_workflowOutgoingPortNodeStates = workflowOutgoingPortNodeStates;
             return this;
        }

        @Override
        public DefaultWrappedWorkflowNodeEntBuilder setVirtualInNodeID(org.knime.gateway.api.entity.NodeIDEnt virtualInNodeID) {
             m_virtualInNodeID = virtualInNodeID;
             return this;
        }

        @Override
        public DefaultWrappedWorkflowNodeEntBuilder setVirtualOutNodeID(org.knime.gateway.api.entity.NodeIDEnt virtualOutNodeID) {
             m_virtualOutNodeID = virtualOutNodeID;
             return this;
        }

        @Override
        public DefaultWrappedWorkflowNodeEntBuilder setInactive(Boolean inactive) {
             m_inactive = inactive;
             return this;
        }

        @Override
        public DefaultWrappedWorkflowNodeEntBuilder setHasWizardPage(Boolean hasWizardPage) {
             m_hasWizardPage = hasWizardPage;
             return this;
        }

        
        @Override
        public DefaultWrappedWorkflowNodeEnt build() {
            return new DefaultWrappedWorkflowNodeEnt(this);
        }
    
    }

}

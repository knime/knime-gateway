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
import org.knime.gateway.v0.entity.NodeFactoryKeyEnt;
import org.knime.gateway.v0.entity.NodeInPortEnt;
import org.knime.gateway.v0.entity.NodeMessageEnt;
import org.knime.gateway.v0.entity.NodeOutPortEnt;
import org.knime.gateway.v0.entity.NodeUIInfoEnt;
import org.knime.gateway.v0.entity.impl.DefaultNodeEnt;

import org.knime.gateway.v0.entity.NativeNodeEnt;

/**
 * Native node extension of a node.
 *
 * @author Martin Horn, University of Konstanz
 */
// AUTO-GENERATED CODE; DO NOT MODIFY
public class DefaultNativeNodeEnt extends DefaultNodeEnt implements NativeNodeEnt {

  protected NodeFactoryKeyEnt m_nodeFactoryKey;
  
  
  private DefaultNativeNodeEnt(DefaultNativeNodeEntBuilder builder) {
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
    m_nodeFactoryKey = builder.m_nodeFactoryKey;
  }


  /**
   * The key/ID of the node factory defining all details.
   * @return nodeFactoryKey
   **/
  @Override
    public NodeFactoryKeyEnt getNodeFactoryKey() {
        return m_nodeFactoryKey;
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
        private NodeFactoryKeyEnt m_nodeFactoryKey;

        @Override
        public DefaultNativeNodeEntBuilder setType(String type) {
             m_type = type;
             return this;
        }
        @Override
        public DefaultNativeNodeEntBuilder setName(String name) {
             m_name = name;
             return this;
        }
        @Override
        public DefaultNativeNodeEntBuilder setNodeID(String nodeID) {
             m_nodeID = nodeID;
             return this;
        }
        @Override
        public DefaultNativeNodeEntBuilder setNodeType(NodeTypeEnum nodeType) {
             m_nodeType = nodeType;
             return this;
        }
        @Override
        public DefaultNativeNodeEntBuilder setParentNodeID(String parentNodeID) {
             m_parentNodeID = parentNodeID;
             return this;
        }
        @Override
        public DefaultNativeNodeEntBuilder setRootWorkflowID(String rootWorkflowID) {
             m_rootWorkflowID = rootWorkflowID;
             return this;
        }
        @Override
        public DefaultNativeNodeEntBuilder setNodeMessage(NodeMessageEnt nodeMessage) {
             m_nodeMessage = nodeMessage;
             return this;
        }
        @Override
        public DefaultNativeNodeEntBuilder setNodeState(NodeStateEnum nodeState) {
             m_nodeState = nodeState;
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
             m_nodeFactoryKey = nodeFactoryKey;
             return this;
        }
        
        @Override
        public DefaultNativeNodeEnt build() {
            return new DefaultNativeNodeEnt(this);
        }
    
    }

}

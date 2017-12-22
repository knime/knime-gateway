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
package org.knime.gateway.v0.entity;

import org.knime.gateway.v0.entity.JobManagerEnt;
import org.knime.gateway.v0.entity.NodeAnnotationEnt;
import org.knime.gateway.v0.entity.NodeEnt;
import org.knime.gateway.v0.entity.NodeInPortEnt;
import org.knime.gateway.v0.entity.NodeMessageEnt;
import org.knime.gateway.v0.entity.NodeOutPortEnt;
import org.knime.gateway.v0.entity.NodeUIInfoEnt;

import org.knime.gateway.entity.GatewayEntityBuilder;



/**
 * A node wrapping (referencing) a workflow (also referred to it as wrapped metanode or subnode) that almost behaves as a ordinary node.
 * 
 * @author Martin Horn, University of Konstanz
 */
// AUTO-GENERATED CODE; DO NOT MODIFY
public interface WrappedWorkflowNodeEnt extends NodeEnt {


  /**
   * List of all incoming workflow ports.
   * @return workflowIncomingPorts
   **/
  public java.util.List<NodeOutPortEnt> getWorkflowIncomingPorts();
  /**
   * List of all outgoing workflow ports.
   * @return workflowOutgoingPorts
   **/
  public java.util.List<NodeInPortEnt> getWorkflowOutgoingPorts();
  /**
   * Whether the referenced workflow is encrypted is required to be unlocked before it can be accessed.
   * @return encrypted
   **/
  public Boolean isEncrypted();
  /**
   * Node ID of the virtual in-node (i.e. source).
   * @return virtualInNodeID
   **/
  public String getVirtualInNodeID();
  /**
   * Node ID of the virtual out-node (i.e. sink).
   * @return virtualOutNodeID
   **/
  public String getVirtualOutNodeID();

    /**
     * The builder for the entity.
     */
    public interface WrappedWorkflowNodeEntBuilder extends GatewayEntityBuilder<WrappedWorkflowNodeEnt> {

        WrappedWorkflowNodeEntBuilder setType(String type);
        WrappedWorkflowNodeEntBuilder setName(String name);
        WrappedWorkflowNodeEntBuilder setNodeID(String nodeID);
        WrappedWorkflowNodeEntBuilder setNodeType(NodeTypeEnum nodeType);
        WrappedWorkflowNodeEntBuilder setParentNodeID(String parentNodeID);
        WrappedWorkflowNodeEntBuilder setRootWorkflowID(String rootWorkflowID);
        WrappedWorkflowNodeEntBuilder setNodeMessage(NodeMessageEnt nodeMessage);
        WrappedWorkflowNodeEntBuilder setNodeState(NodeStateEnum nodeState);
        WrappedWorkflowNodeEntBuilder setInPorts(java.util.List<NodeInPortEnt> inPorts);
        WrappedWorkflowNodeEntBuilder setOutPorts(java.util.List<NodeOutPortEnt> outPorts);
        WrappedWorkflowNodeEntBuilder setDeletable(Boolean deletable);
        WrappedWorkflowNodeEntBuilder setHasDialog(Boolean hasDialog);
        WrappedWorkflowNodeEntBuilder setNodeAnnotation(NodeAnnotationEnt nodeAnnotation);
        WrappedWorkflowNodeEntBuilder setJobManager(JobManagerEnt jobManager);
        WrappedWorkflowNodeEntBuilder setUIInfo(NodeUIInfoEnt uIInfo);
        WrappedWorkflowNodeEntBuilder setWorkflowIncomingPorts(java.util.List<NodeOutPortEnt> workflowIncomingPorts);
        WrappedWorkflowNodeEntBuilder setWorkflowOutgoingPorts(java.util.List<NodeInPortEnt> workflowOutgoingPorts);
        WrappedWorkflowNodeEntBuilder setEncrypted(Boolean encrypted);
        WrappedWorkflowNodeEntBuilder setVirtualInNodeID(String virtualInNodeID);
        WrappedWorkflowNodeEntBuilder setVirtualOutNodeID(String virtualOutNodeID);
        
        WrappedWorkflowNodeEnt build();
    
    }

}

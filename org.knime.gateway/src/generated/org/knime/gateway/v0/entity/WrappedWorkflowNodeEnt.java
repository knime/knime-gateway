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
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
@javax.annotation.Generated(value = "org.knime.gateway.codegen.GatewayCodegen")
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

        /**
         * Discriminator for inheritance. Must be the base name of this type/schema.
         * 
         * @param type the property value, NOT <code>null</code>! 
         * @return this entity builder for chaining
         */
        WrappedWorkflowNodeEntBuilder setType(String type);
        
        /**
         * The node&#39;s name.
         * 
         * @param name the property value, NOT <code>null</code>! 
         * @return this entity builder for chaining
         */
        WrappedWorkflowNodeEntBuilder setName(String name);
        
        /**
         * The ID of the node.
         * 
         * @param nodeID the property value, NOT <code>null</code>! 
         * @return this entity builder for chaining
         */
        WrappedWorkflowNodeEntBuilder setNodeID(String nodeID);
        
        /**
         * The type of the node.
         * 
         * @param nodeType the property value, NOT <code>null</code>! 
         * @return this entity builder for chaining
         */
        WrappedWorkflowNodeEntBuilder setNodeType(NodeTypeEnum nodeType);
        
        /**
         * The parent node id of the node or not present if it&#39;s the root node.
         * 
         * @param parentNodeID the property value,  
         * @return this entity builder for chaining
         */
        WrappedWorkflowNodeEntBuilder setParentNodeID(String parentNodeID);
        
        /**
         * The id of the root workflow this node is contained in or represents.
         * 
         * @param rootWorkflowID the property value, NOT <code>null</code>! 
         * @return this entity builder for chaining
         */
        WrappedWorkflowNodeEntBuilder setRootWorkflowID(java.util.UUID rootWorkflowID);
        
        /**
         * The current node message (warning, error, none).
         * 
         * @param nodeMessage the property value,  
         * @return this entity builder for chaining
         */
        WrappedWorkflowNodeEntBuilder setNodeMessage(NodeMessageEnt nodeMessage);
        
        /**
         * The state of the node.
         * 
         * @param nodeState the property value, NOT <code>null</code>! 
         * @return this entity builder for chaining
         */
        WrappedWorkflowNodeEntBuilder setNodeState(NodeStateEnum nodeState);
        
        /**
         * The list of inputs.
         * 
         * @param inPorts the property value,  
         * @return this entity builder for chaining
         */
        WrappedWorkflowNodeEntBuilder setInPorts(java.util.List<NodeInPortEnt> inPorts);
        
        /**
         * The list of outputs.
         * 
         * @param outPorts the property value,  
         * @return this entity builder for chaining
         */
        WrappedWorkflowNodeEntBuilder setOutPorts(java.util.List<NodeOutPortEnt> outPorts);
        
        /**
         * Whether the node is deletable.
         * 
         * @param deletable the property value,  
         * @return this entity builder for chaining
         */
        WrappedWorkflowNodeEntBuilder setDeletable(Boolean deletable);
        
        /**
         * Whether the node has a configuration dialog / user settings.
         * 
         * @param hasDialog the property value,  
         * @return this entity builder for chaining
         */
        WrappedWorkflowNodeEntBuilder setHasDialog(Boolean hasDialog);
        
        /**
         * The annotation below the node.
         * 
         * @param nodeAnnotation the property value,  
         * @return this entity builder for chaining
         */
        WrappedWorkflowNodeEntBuilder setNodeAnnotation(NodeAnnotationEnt nodeAnnotation);
        
        /**
         * The job manager (e.g. cluster or streaming).
         * 
         * @param jobManager the property value,  
         * @return this entity builder for chaining
         */
        WrappedWorkflowNodeEntBuilder setJobManager(JobManagerEnt jobManager);
        
        /**
         * 
         * @param uIInfo the property value,  
         * @return this entity builder for chaining
         */
        WrappedWorkflowNodeEntBuilder setUIInfo(NodeUIInfoEnt uIInfo);
        
        /**
         * List of all incoming workflow ports.
         * 
         * @param workflowIncomingPorts the property value,  
         * @return this entity builder for chaining
         */
        WrappedWorkflowNodeEntBuilder setWorkflowIncomingPorts(java.util.List<NodeOutPortEnt> workflowIncomingPorts);
        
        /**
         * List of all outgoing workflow ports.
         * 
         * @param workflowOutgoingPorts the property value,  
         * @return this entity builder for chaining
         */
        WrappedWorkflowNodeEntBuilder setWorkflowOutgoingPorts(java.util.List<NodeInPortEnt> workflowOutgoingPorts);
        
        /**
         * Whether the referenced workflow is encrypted is required to be unlocked before it can be accessed.
         * 
         * @param encrypted the property value,  
         * @return this entity builder for chaining
         */
        WrappedWorkflowNodeEntBuilder setEncrypted(Boolean encrypted);
        
        /**
         * Node ID of the virtual in-node (i.e. source).
         * 
         * @param virtualInNodeID the property value,  
         * @return this entity builder for chaining
         */
        WrappedWorkflowNodeEntBuilder setVirtualInNodeID(String virtualInNodeID);
        
        /**
         * Node ID of the virtual out-node (i.e. sink).
         * 
         * @param virtualOutNodeID the property value,  
         * @return this entity builder for chaining
         */
        WrappedWorkflowNodeEntBuilder setVirtualOutNodeID(String virtualOutNodeID);
        
        
        /**
        * Creates the entity from the builder.
        * 
        * @return the entity
        * @throws IllegalArgumentException most likely in case when a required property hasn't been set
        */
        @Override
        WrappedWorkflowNodeEnt build();
    
    }

}
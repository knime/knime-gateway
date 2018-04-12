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
package com.knime.gateway.v0.entity;

import com.knime.gateway.v0.entity.JobManagerEnt;
import com.knime.gateway.v0.entity.NodeAnnotationEnt;
import com.knime.gateway.v0.entity.NodeInPortEnt;
import com.knime.gateway.v0.entity.NodeMessageEnt;
import com.knime.gateway.v0.entity.NodeOutPortEnt;
import com.knime.gateway.v0.entity.NodeUIInfoEnt;
import com.knime.gateway.v0.entity.WorkflowNodeEnt;

import com.knime.gateway.entity.GatewayEntityBuilder;



/**
 * A node wrapping (referencing) a workflow (also referred to it as wrapped metanode or subnode) that almost behaves as a ordinary node.
 * 
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
@javax.annotation.Generated(value = "org.knime.gateway.codegen.GatewayCodegen")
public interface WrappedWorkflowNodeEnt extends WorkflowNodeEnt {


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

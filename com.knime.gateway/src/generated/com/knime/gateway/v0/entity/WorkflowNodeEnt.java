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
import com.knime.gateway.v0.entity.NodeEnt;
import com.knime.gateway.v0.entity.NodeInPortEnt;
import com.knime.gateway.v0.entity.NodeMessageEnt;
import com.knime.gateway.v0.entity.NodeOutPortEnt;
import com.knime.gateway.v0.entity.NodeProgressEnt;
import com.knime.gateway.v0.entity.NodeStateEnt;
import com.knime.gateway.v0.entity.NodeUIInfoEnt;

import com.knime.gateway.entity.GatewayEntityBuilder;



/**
 * A node containing (referencing) a workflow (also referred to it as metanode)
 * 
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
@javax.annotation.Generated(value = "org.knime.gateway.codegen.GatewayCodegen")
public interface WorkflowNodeEnt extends NodeEnt {


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
   * Whether the referenced workflow is encrypted and is required to be unlocked before it can be accessed.
   * @return encrypted 
   **/
  public Boolean isEncrypted();

  /**
   * The state of the inner node connected to a particular outport. TODO Should actually be part of a specialization of NodeOutPort (i.e. WorkflowOutPort) but doesn&#39;t work with inheritance and generics in Java.
   * @return workflowOutgoingPortNodeStates 
   **/
  public java.util.List<NodeStateEnt> getWorkflowOutgoingPortNodeStates();


    /**
     * The builder for the entity.
     */
    public interface WorkflowNodeEntBuilder extends GatewayEntityBuilder<WorkflowNodeEnt> {

        /**
         * Discriminator for inheritance. Must be the base name of this type/schema.
         * 
         * @param type the property value, NOT <code>null</code>! 
         * @return this entity builder for chaining
         */
        WorkflowNodeEntBuilder setType(String type);
        
        /**
         * The node&#39;s name.
         * 
         * @param name the property value, NOT <code>null</code>! 
         * @return this entity builder for chaining
         */
        WorkflowNodeEntBuilder setName(String name);
        
        /**
         * The ID of the node.
         * 
         * @param nodeID the property value, NOT <code>null</code>! 
         * @return this entity builder for chaining
         */
        WorkflowNodeEntBuilder setNodeID(String nodeID);
        
        /**
         * The type of the node.
         * 
         * @param nodeType the property value, NOT <code>null</code>! 
         * @return this entity builder for chaining
         */
        WorkflowNodeEntBuilder setNodeType(NodeTypeEnum nodeType);
        
        /**
         * The parent node id of the node or \&quot;root\&quot; if it&#39;s the root node/workflow.
         * 
         * @param parentNodeID the property value,  
         * @return this entity builder for chaining
         */
        WorkflowNodeEntBuilder setParentNodeID(String parentNodeID);
        
        /**
         * The id of the root workflow this node is contained in or represents.
         * 
         * @param rootWorkflowID the property value, NOT <code>null</code>! 
         * @return this entity builder for chaining
         */
        WorkflowNodeEntBuilder setRootWorkflowID(java.util.UUID rootWorkflowID);
        
        /**
   		 * Set nodeMessage
         * 
         * @param nodeMessage the property value,  
         * @return this entity builder for chaining
         */
        WorkflowNodeEntBuilder setNodeMessage(NodeMessageEnt nodeMessage);
        
        /**
   		 * Set nodeState
         * 
         * @param nodeState the property value, NOT <code>null</code>! 
         * @return this entity builder for chaining
         */
        WorkflowNodeEntBuilder setNodeState(NodeStateEnt nodeState);
        
        /**
   		 * Set progress
         * 
         * @param progress the property value,  
         * @return this entity builder for chaining
         */
        WorkflowNodeEntBuilder setProgress(NodeProgressEnt progress);
        
        /**
         * The list of inputs.
         * 
         * @param inPorts the property value,  
         * @return this entity builder for chaining
         */
        WorkflowNodeEntBuilder setInPorts(java.util.List<NodeInPortEnt> inPorts);
        
        /**
         * The list of outputs.
         * 
         * @param outPorts the property value,  
         * @return this entity builder for chaining
         */
        WorkflowNodeEntBuilder setOutPorts(java.util.List<NodeOutPortEnt> outPorts);
        
        /**
         * Whether the node is deletable.
         * 
         * @param deletable the property value,  
         * @return this entity builder for chaining
         */
        WorkflowNodeEntBuilder setDeletable(Boolean deletable);
        
        /**
         * Whether the node is resetable. Please note that it only represents the &#39;local&#39; reset-state but doesn&#39;t take the whole workflow into account (e.g. executing successors).
         * 
         * @param resetable the property value,  
         * @return this entity builder for chaining
         */
        WorkflowNodeEntBuilder setResetable(Boolean resetable);
        
        /**
         * Whether the node has a configuration dialog / user settings.
         * 
         * @param hasDialog the property value,  
         * @return this entity builder for chaining
         */
        WorkflowNodeEntBuilder setHasDialog(Boolean hasDialog);
        
        /**
   		 * Set nodeAnnotation
         * 
         * @param nodeAnnotation the property value,  
         * @return this entity builder for chaining
         */
        WorkflowNodeEntBuilder setNodeAnnotation(NodeAnnotationEnt nodeAnnotation);
        
        /**
         * The names of the available web views. Can be an empty list.
         * 
         * @param webViewNames the property value,  
         * @return this entity builder for chaining
         */
        WorkflowNodeEntBuilder setWebViewNames(java.util.List<String> webViewNames);
        
        /**
   		 * Set jobManager
         * 
         * @param jobManager the property value,  
         * @return this entity builder for chaining
         */
        WorkflowNodeEntBuilder setJobManager(JobManagerEnt jobManager);
        
        /**
   		 * Set uIInfo
         * 
         * @param uIInfo the property value,  
         * @return this entity builder for chaining
         */
        WorkflowNodeEntBuilder setUIInfo(NodeUIInfoEnt uIInfo);
        
        /**
         * List of all incoming workflow ports.
         * 
         * @param workflowIncomingPorts the property value,  
         * @return this entity builder for chaining
         */
        WorkflowNodeEntBuilder setWorkflowIncomingPorts(java.util.List<NodeOutPortEnt> workflowIncomingPorts);
        
        /**
         * List of all outgoing workflow ports.
         * 
         * @param workflowOutgoingPorts the property value,  
         * @return this entity builder for chaining
         */
        WorkflowNodeEntBuilder setWorkflowOutgoingPorts(java.util.List<NodeInPortEnt> workflowOutgoingPorts);
        
        /**
         * Whether the referenced workflow is encrypted and is required to be unlocked before it can be accessed.
         * 
         * @param encrypted the property value,  
         * @return this entity builder for chaining
         */
        WorkflowNodeEntBuilder setEncrypted(Boolean encrypted);
        
        /**
         * The state of the inner node connected to a particular outport. TODO Should actually be part of a specialization of NodeOutPort (i.e. WorkflowOutPort) but doesn&#39;t work with inheritance and generics in Java.
         * 
         * @param workflowOutgoingPortNodeStates the property value,  
         * @return this entity builder for chaining
         */
        WorkflowNodeEntBuilder setWorkflowOutgoingPortNodeStates(java.util.List<NodeStateEnt> workflowOutgoingPortNodeStates);
        
        
        /**
        * Creates the entity from the builder.
        * 
        * @return the entity
        * @throws IllegalArgumentException most likely in case when a required property hasn't been set
        */
        @Override
        WorkflowNodeEnt build();
    
    }

}

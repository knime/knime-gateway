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

import com.knime.gateway.entity.GatewayEntityBuilder;


import com.knime.gateway.entity.GatewayEntity;

/**
 * A node.
 * 
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
@javax.annotation.Generated(value = "org.knime.gateway.codegen.GatewayCodegen")
public interface NodeEnt extends GatewayEntity {

  /**
   * The type of the node.
   */
  public enum NodeTypeEnum {
    SOURCE("Source"),
    
    SINK("Sink"),
    
    LEARNER("Learner"),
    
    PREDICTOR("Predictor"),
    
    MANIPULATOR("Manipulator"),
    
    VISUALIZER("Visualizer"),
    
    META("Meta"),
    
    LOOPSTART("LoopStart"),
    
    LOOPEND("LoopEnd"),
    
    SCOPESTART("ScopeStart"),
    
    SCOPEEND("ScopeEnd"),
    
    QUICKFORM("QuickForm"),
    
    OTHER("Other"),
    
    MISSING("Missing"),
    
    UNKNOWN("Unknown"),
    
    SUBNODE("Subnode"),
    
    VIRTUALIN("VirtualIn"),
    
    VIRTUALOUT("VirtualOut");

    private String value;

    NodeTypeEnum(String value) {
      this.value = value;
    }

    @Override
    public String toString() {
      return String.valueOf(value);
    }

  }

  /**
   * The state of the node.
   */
  public enum NodeStateEnum {
    IDLE("IDLE"),
    
    CONFIGURED("CONFIGURED"),
    
    UNCONFIGURED_MARKEDFOREXEC("UNCONFIGURED_MARKEDFOREXEC"),
    
    CONFIGURED_MARKEDFOREXEC("CONFIGURED_MARKEDFOREXEC"),
    
    EXECUTED_MARKEDFOREXEC("EXECUTED_MARKEDFOREXEC"),
    
    CONFIGURED_QUEUED("CONFIGURED_QUEUED"),
    
    EXECUTED_QUEUED("EXECUTED_QUEUED"),
    
    PREEXECUTE("PREEXECUTE"),
    
    EXECUTING("EXECUTING"),
    
    EXECUTINGREMOTELY("EXECUTINGREMOTELY"),
    
    POSTEXECUTE("POSTEXECUTE"),
    
    EXECUTED("EXECUTED");

    private String value;

    NodeStateEnum(String value) {
      this.value = value;
    }

    @Override
    public String toString() {
      return String.valueOf(value);
    }

  }


  /**
   * Discriminator for inheritance. Must be the base name of this type/schema.
   * @return type , never <code>null</code>
   **/
  public String getType();

  /**
   * The node&#39;s name.
   * @return name , never <code>null</code>
   **/
  public String getName();

  /**
   * The ID of the node.
   * @return nodeID , never <code>null</code>
   **/
  public String getNodeID();

  /**
   * The type of the node.
   * @return nodeType , never <code>null</code>
   **/
  public NodeTypeEnum getNodeType();

  /**
   * The parent node id of the node or not present if it&#39;s the root node.
   * @return parentNodeID 
   **/
  public String getParentNodeID();

  /**
   * The id of the root workflow this node is contained in or represents.
   * @return rootWorkflowID , never <code>null</code>
   **/
  public java.util.UUID getRootWorkflowID();

  /**
   * The current node message (warning, error, none).
   * @return nodeMessage 
   **/
  public NodeMessageEnt getNodeMessage();

  /**
   * The state of the node.
   * @return nodeState , never <code>null</code>
   **/
  public NodeStateEnum getNodeState();

  /**
   * The list of inputs.
   * @return inPorts 
   **/
  public java.util.List<NodeInPortEnt> getInPorts();

  /**
   * The list of outputs.
   * @return outPorts 
   **/
  public java.util.List<NodeOutPortEnt> getOutPorts();

  /**
   * Whether the node is deletable.
   * @return deletable 
   **/
  public Boolean isDeletable();

  /**
   * Whether the node has a configuration dialog / user settings.
   * @return hasDialog 
   **/
  public Boolean isHasDialog();

  /**
   * The annotation below the node.
   * @return nodeAnnotation 
   **/
  public NodeAnnotationEnt getNodeAnnotation();

  /**
   * The job manager (e.g. cluster or streaming).
   * @return jobManager 
   **/
  public JobManagerEnt getJobManager();

  /**
   * Get uIInfo
   * @return uIInfo 
   **/
  public NodeUIInfoEnt getUIInfo();


    /**
     * The builder for the entity.
     */
    public interface NodeEntBuilder extends GatewayEntityBuilder<NodeEnt> {

        /**
         * Discriminator for inheritance. Must be the base name of this type/schema.
         * 
         * @param type the property value, NOT <code>null</code>! 
         * @return this entity builder for chaining
         */
        NodeEntBuilder setType(String type);
        
        /**
         * The node&#39;s name.
         * 
         * @param name the property value, NOT <code>null</code>! 
         * @return this entity builder for chaining
         */
        NodeEntBuilder setName(String name);
        
        /**
         * The ID of the node.
         * 
         * @param nodeID the property value, NOT <code>null</code>! 
         * @return this entity builder for chaining
         */
        NodeEntBuilder setNodeID(String nodeID);
        
        /**
         * The type of the node.
         * 
         * @param nodeType the property value, NOT <code>null</code>! 
         * @return this entity builder for chaining
         */
        NodeEntBuilder setNodeType(NodeTypeEnum nodeType);
        
        /**
         * The parent node id of the node or not present if it&#39;s the root node.
         * 
         * @param parentNodeID the property value,  
         * @return this entity builder for chaining
         */
        NodeEntBuilder setParentNodeID(String parentNodeID);
        
        /**
         * The id of the root workflow this node is contained in or represents.
         * 
         * @param rootWorkflowID the property value, NOT <code>null</code>! 
         * @return this entity builder for chaining
         */
        NodeEntBuilder setRootWorkflowID(java.util.UUID rootWorkflowID);
        
        /**
         * The current node message (warning, error, none).
         * 
         * @param nodeMessage the property value,  
         * @return this entity builder for chaining
         */
        NodeEntBuilder setNodeMessage(NodeMessageEnt nodeMessage);
        
        /**
         * The state of the node.
         * 
         * @param nodeState the property value, NOT <code>null</code>! 
         * @return this entity builder for chaining
         */
        NodeEntBuilder setNodeState(NodeStateEnum nodeState);
        
        /**
         * The list of inputs.
         * 
         * @param inPorts the property value,  
         * @return this entity builder for chaining
         */
        NodeEntBuilder setInPorts(java.util.List<NodeInPortEnt> inPorts);
        
        /**
         * The list of outputs.
         * 
         * @param outPorts the property value,  
         * @return this entity builder for chaining
         */
        NodeEntBuilder setOutPorts(java.util.List<NodeOutPortEnt> outPorts);
        
        /**
         * Whether the node is deletable.
         * 
         * @param deletable the property value,  
         * @return this entity builder for chaining
         */
        NodeEntBuilder setDeletable(Boolean deletable);
        
        /**
         * Whether the node has a configuration dialog / user settings.
         * 
         * @param hasDialog the property value,  
         * @return this entity builder for chaining
         */
        NodeEntBuilder setHasDialog(Boolean hasDialog);
        
        /**
         * The annotation below the node.
         * 
         * @param nodeAnnotation the property value,  
         * @return this entity builder for chaining
         */
        NodeEntBuilder setNodeAnnotation(NodeAnnotationEnt nodeAnnotation);
        
        /**
         * The job manager (e.g. cluster or streaming).
         * 
         * @param jobManager the property value,  
         * @return this entity builder for chaining
         */
        NodeEntBuilder setJobManager(JobManagerEnt jobManager);
        
        /**
         * 
         * @param uIInfo the property value,  
         * @return this entity builder for chaining
         */
        NodeEntBuilder setUIInfo(NodeUIInfoEnt uIInfo);
        
        
        /**
        * Creates the entity from the builder.
        * 
        * @return the entity
        * @throws IllegalArgumentException most likely in case when a required property hasn't been set
        */
        @Override
        NodeEnt build();
    
    }

}

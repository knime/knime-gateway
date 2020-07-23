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
package org.knime.gateway.api.entity;

import org.knime.gateway.api.entity.JobManagerEnt;
import org.knime.gateway.api.entity.NodeAnnotationEnt;
import org.knime.gateway.api.entity.NodeInPortEnt;
import org.knime.gateway.api.entity.NodeMessageEnt;
import org.knime.gateway.api.entity.NodeOutPortEnt;
import org.knime.gateway.api.entity.NodeProgressEnt;
import org.knime.gateway.api.entity.NodeStateEnt;
import org.knime.gateway.api.entity.NodeUIInfoEnt;

import org.knime.gateway.api.entity.GatewayEntityBuilder;


import org.knime.gateway.api.entity.GatewayEntity;

/**
 * A node.
 * 
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
@javax.annotation.Generated(value = {"com.knime.gateway.codegen.GatewayCodegen", "src-gen/org.knime.gateway.api-config.json"})
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
    
    WIDGET("Widget"),
    
    META("Meta"),
    
    LOOPSTART("LoopStart"),
    
    LOOPEND("LoopEnd"),
    
    SCOPESTART("ScopeStart"),
    
    SCOPEEND("ScopeEnd"),
    
    QUICKFORM("QuickForm"),
    
    CONFIGURATION("Configuration"),
    
    OTHER("Other"),
    
    MISSING("Missing"),
    
    UNKNOWN("Unknown"),
    
    SUBNODE("Subnode"),
    
    VIRTUALIN("VirtualIn"),
    
    VIRTUALOUT("VirtualOut"),
    
    CONTAINER("Container");

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
  public org.knime.gateway.api.entity.NodeIDEnt getNodeID();

  /**
   * The type of the node.
   * @return nodeType , never <code>null</code>
   **/
  public NodeTypeEnum getNodeType();

  /**
   * The parent node id of the node or \&quot;root\&quot; if it&#39;s the root node/workflow.
   * @return parentNodeID 
   **/
  public org.knime.gateway.api.entity.NodeIDEnt getParentNodeID();

  /**
   * The id of the root workflow this node is contained in or represents.
   * @return rootWorkflowID , never <code>null</code>
   **/
  public java.util.UUID getRootWorkflowID();

  /**
   * Get nodeMessage
   * @return nodeMessage 
   **/
  public NodeMessageEnt getNodeMessage();

  /**
   * Get nodeState
   * @return nodeState , never <code>null</code>
   **/
  public NodeStateEnt getNodeState();

  /**
   * Get progress
   * @return progress 
   **/
  public NodeProgressEnt getProgress();

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
   * Whether the node is resetable. Please note that it only represents the &#39;local&#39; reset-state but doesn&#39;t take the whole workflow into account (e.g. executing successors).
   * @return resetable 
   **/
  public Boolean isResetable();

  /**
   * Whether the node has a configuration dialog / user settings.
   * @return hasDialog 
   **/
  public Boolean hasDialog();

  /**
   * Get nodeAnnotation
   * @return nodeAnnotation 
   **/
  public NodeAnnotationEnt getNodeAnnotation();

  /**
   * The names of the available web views. Can be an empty list.
   * @return webViewNames 
   **/
  public java.util.List<String> getWebViewNames();

  /**
   * Get jobManager
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
        NodeEntBuilder setNodeID(org.knime.gateway.api.entity.NodeIDEnt nodeID);
        
        /**
         * The type of the node.
         * 
         * @param nodeType the property value, NOT <code>null</code>! 
         * @return this entity builder for chaining
         */
        NodeEntBuilder setNodeType(NodeTypeEnum nodeType);
        
        /**
         * The parent node id of the node or \&quot;root\&quot; if it&#39;s the root node/workflow.
         * 
         * @param parentNodeID the property value,  
         * @return this entity builder for chaining
         */
        NodeEntBuilder setParentNodeID(org.knime.gateway.api.entity.NodeIDEnt parentNodeID);
        
        /**
         * The id of the root workflow this node is contained in or represents.
         * 
         * @param rootWorkflowID the property value, NOT <code>null</code>! 
         * @return this entity builder for chaining
         */
        NodeEntBuilder setRootWorkflowID(java.util.UUID rootWorkflowID);
        
        /**
   		 * Set nodeMessage
         * 
         * @param nodeMessage the property value,  
         * @return this entity builder for chaining
         */
        NodeEntBuilder setNodeMessage(NodeMessageEnt nodeMessage);
        
        /**
   		 * Set nodeState
         * 
         * @param nodeState the property value, NOT <code>null</code>! 
         * @return this entity builder for chaining
         */
        NodeEntBuilder setNodeState(NodeStateEnt nodeState);
        
        /**
   		 * Set progress
         * 
         * @param progress the property value,  
         * @return this entity builder for chaining
         */
        NodeEntBuilder setProgress(NodeProgressEnt progress);
        
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
         * Whether the node is resetable. Please note that it only represents the &#39;local&#39; reset-state but doesn&#39;t take the whole workflow into account (e.g. executing successors).
         * 
         * @param resetable the property value,  
         * @return this entity builder for chaining
         */
        NodeEntBuilder setResetable(Boolean resetable);
        
        /**
         * Whether the node has a configuration dialog / user settings.
         * 
         * @param hasDialog the property value,  
         * @return this entity builder for chaining
         */
        NodeEntBuilder setHasDialog(Boolean hasDialog);
        
        /**
   		 * Set nodeAnnotation
         * 
         * @param nodeAnnotation the property value,  
         * @return this entity builder for chaining
         */
        NodeEntBuilder setNodeAnnotation(NodeAnnotationEnt nodeAnnotation);
        
        /**
         * The names of the available web views. Can be an empty list.
         * 
         * @param webViewNames the property value,  
         * @return this entity builder for chaining
         */
        NodeEntBuilder setWebViewNames(java.util.List<String> webViewNames);
        
        /**
   		 * Set jobManager
         * 
         * @param jobManager the property value,  
         * @return this entity builder for chaining
         */
        NodeEntBuilder setJobManager(JobManagerEnt jobManager);
        
        /**
   		 * Set uIInfo
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

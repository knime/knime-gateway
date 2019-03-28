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
package com.knime.gateway.entity;

import com.knime.gateway.entity.JobManagerEnt;
import com.knime.gateway.entity.NodeAnnotationEnt;
import com.knime.gateway.entity.NodeEnt;
import com.knime.gateway.entity.NodeFactoryKeyEnt;
import com.knime.gateway.entity.NodeInPortEnt;
import com.knime.gateway.entity.NodeMessageEnt;
import com.knime.gateway.entity.NodeOutPortEnt;
import com.knime.gateway.entity.NodeProgressEnt;
import com.knime.gateway.entity.NodeStateEnt;
import com.knime.gateway.entity.NodeUIInfoEnt;

import com.knime.gateway.entity.GatewayEntityBuilder;



/**
 * Native node extension of a node.
 * 
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
@javax.annotation.Generated(value = {"org.knime.gateway.codegen.GatewayCodegen", "src-gen/com.knime.gateway-interfaces-config.json"})
public interface NativeNodeEnt extends NodeEnt {


  /**
   * Get nodeFactoryKey
   * @return nodeFactoryKey , never <code>null</code>
   **/
  public NodeFactoryKeyEnt getNodeFactoryKey();

  /**
   * Whether this node is inactive, e.g. due to inactive connections
   * @return inactive 
   **/
  public Boolean isInactive();


    /**
     * The builder for the entity.
     */
    public interface NativeNodeEntBuilder extends GatewayEntityBuilder<NativeNodeEnt> {

        /**
         * Discriminator for inheritance. Must be the base name of this type/schema.
         * 
         * @param type the property value, NOT <code>null</code>! 
         * @return this entity builder for chaining
         */
        NativeNodeEntBuilder setType(String type);
        
        /**
         * The node&#39;s name.
         * 
         * @param name the property value, NOT <code>null</code>! 
         * @return this entity builder for chaining
         */
        NativeNodeEntBuilder setName(String name);
        
        /**
         * The ID of the node.
         * 
         * @param nodeID the property value, NOT <code>null</code>! 
         * @return this entity builder for chaining
         */
        NativeNodeEntBuilder setNodeID(String nodeID);
        
        /**
         * The type of the node.
         * 
         * @param nodeType the property value, NOT <code>null</code>! 
         * @return this entity builder for chaining
         */
        NativeNodeEntBuilder setNodeType(NodeTypeEnum nodeType);
        
        /**
         * The parent node id of the node or \&quot;root\&quot; if it&#39;s the root node/workflow.
         * 
         * @param parentNodeID the property value,  
         * @return this entity builder for chaining
         */
        NativeNodeEntBuilder setParentNodeID(String parentNodeID);
        
        /**
         * The id of the root workflow this node is contained in or represents.
         * 
         * @param rootWorkflowID the property value, NOT <code>null</code>! 
         * @return this entity builder for chaining
         */
        NativeNodeEntBuilder setRootWorkflowID(java.util.UUID rootWorkflowID);
        
        /**
   		 * Set nodeMessage
         * 
         * @param nodeMessage the property value,  
         * @return this entity builder for chaining
         */
        NativeNodeEntBuilder setNodeMessage(NodeMessageEnt nodeMessage);
        
        /**
   		 * Set nodeState
         * 
         * @param nodeState the property value, NOT <code>null</code>! 
         * @return this entity builder for chaining
         */
        NativeNodeEntBuilder setNodeState(NodeStateEnt nodeState);
        
        /**
   		 * Set progress
         * 
         * @param progress the property value,  
         * @return this entity builder for chaining
         */
        NativeNodeEntBuilder setProgress(NodeProgressEnt progress);
        
        /**
         * The list of inputs.
         * 
         * @param inPorts the property value,  
         * @return this entity builder for chaining
         */
        NativeNodeEntBuilder setInPorts(java.util.List<NodeInPortEnt> inPorts);
        
        /**
         * The list of outputs.
         * 
         * @param outPorts the property value,  
         * @return this entity builder for chaining
         */
        NativeNodeEntBuilder setOutPorts(java.util.List<NodeOutPortEnt> outPorts);
        
        /**
         * Whether the node is deletable.
         * 
         * @param deletable the property value,  
         * @return this entity builder for chaining
         */
        NativeNodeEntBuilder setDeletable(Boolean deletable);
        
        /**
         * Whether the node is resetable. Please note that it only represents the &#39;local&#39; reset-state but doesn&#39;t take the whole workflow into account (e.g. executing successors).
         * 
         * @param resetable the property value,  
         * @return this entity builder for chaining
         */
        NativeNodeEntBuilder setResetable(Boolean resetable);
        
        /**
         * Whether the node has a configuration dialog / user settings.
         * 
         * @param hasDialog the property value,  
         * @return this entity builder for chaining
         */
        NativeNodeEntBuilder setHasDialog(Boolean hasDialog);
        
        /**
   		 * Set nodeAnnotation
         * 
         * @param nodeAnnotation the property value,  
         * @return this entity builder for chaining
         */
        NativeNodeEntBuilder setNodeAnnotation(NodeAnnotationEnt nodeAnnotation);
        
        /**
         * The names of the available web views. Can be an empty list.
         * 
         * @param webViewNames the property value,  
         * @return this entity builder for chaining
         */
        NativeNodeEntBuilder setWebViewNames(java.util.List<String> webViewNames);
        
        /**
   		 * Set jobManager
         * 
         * @param jobManager the property value,  
         * @return this entity builder for chaining
         */
        NativeNodeEntBuilder setJobManager(JobManagerEnt jobManager);
        
        /**
   		 * Set uIInfo
         * 
         * @param uIInfo the property value,  
         * @return this entity builder for chaining
         */
        NativeNodeEntBuilder setUIInfo(NodeUIInfoEnt uIInfo);
        
        /**
   		 * Set nodeFactoryKey
         * 
         * @param nodeFactoryKey the property value, NOT <code>null</code>! 
         * @return this entity builder for chaining
         */
        NativeNodeEntBuilder setNodeFactoryKey(NodeFactoryKeyEnt nodeFactoryKey);
        
        /**
         * Whether this node is inactive, e.g. due to inactive connections
         * 
         * @param inactive the property value,  
         * @return this entity builder for chaining
         */
        NativeNodeEntBuilder setInactive(Boolean inactive);
        
        
        /**
        * Creates the entity from the builder.
        * 
        * @return the entity
        * @throws IllegalArgumentException most likely in case when a required property hasn't been set
        */
        @Override
        NativeNodeEnt build();
    
    }

}

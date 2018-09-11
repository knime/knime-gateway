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
import com.knime.gateway.v0.entity.NativeNodeEnt;
import com.knime.gateway.v0.entity.NodeAnnotationEnt;
import com.knime.gateway.v0.entity.NodeFactoryKeyEnt;
import com.knime.gateway.v0.entity.NodeInPortEnt;
import com.knime.gateway.v0.entity.NodeMessageEnt;
import com.knime.gateway.v0.entity.NodeOutPortEnt;
import com.knime.gateway.v0.entity.NodeProgressEnt;
import com.knime.gateway.v0.entity.NodeStateEnt;
import com.knime.gateway.v0.entity.NodeUIInfoEnt;

import com.knime.gateway.entity.GatewayEntityBuilder;



/**
 * Converter node.
 * 
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
@javax.annotation.Generated(value = "org.knime.gateway.codegen.GatewayCodegen")
public interface ConverterNodeEnt extends NativeNodeEnt {


  /**
   * Get outExecEnvInstanceID
   * @return outExecEnvInstanceID 
   **/
  public String getOutExecEnvInstanceID();


    /**
     * The builder for the entity.
     */
    public interface ConverterNodeEntBuilder extends GatewayEntityBuilder<ConverterNodeEnt> {

        /**
         * Discriminator for inheritance. Must be the base name of this type/schema.
         * 
         * @param type the property value, NOT <code>null</code>! 
         * @return this entity builder for chaining
         */
        ConverterNodeEntBuilder setType(String type);
        
        /**
         * The node&#39;s name.
         * 
         * @param name the property value, NOT <code>null</code>! 
         * @return this entity builder for chaining
         */
        ConverterNodeEntBuilder setName(String name);
        
        /**
         * The ID of the node.
         * 
         * @param nodeID the property value, NOT <code>null</code>! 
         * @return this entity builder for chaining
         */
        ConverterNodeEntBuilder setNodeID(String nodeID);
        
        /**
         * The type of the node.
         * 
         * @param nodeType the property value, NOT <code>null</code>! 
         * @return this entity builder for chaining
         */
        ConverterNodeEntBuilder setNodeType(NodeTypeEnum nodeType);
        
        /**
         * The parent node id of the node or \&quot;root\&quot; if it&#39;s the root node/workflow.
         * 
         * @param parentNodeID the property value,  
         * @return this entity builder for chaining
         */
        ConverterNodeEntBuilder setParentNodeID(String parentNodeID);
        
        /**
         * The id of the root workflow this node is contained in or represents.
         * 
         * @param rootWorkflowID the property value, NOT <code>null</code>! 
         * @return this entity builder for chaining
         */
        ConverterNodeEntBuilder setRootWorkflowID(java.util.UUID rootWorkflowID);
        
        /**
         * The current node message (warning, error, none).
         * 
         * @param nodeMessage the property value,  
         * @return this entity builder for chaining
         */
        ConverterNodeEntBuilder setNodeMessage(NodeMessageEnt nodeMessage);
        
        /**
         * The state of the node.
         * 
         * @param nodeState the property value, NOT <code>null</code>! 
         * @return this entity builder for chaining
         */
        ConverterNodeEntBuilder setNodeState(NodeStateEnt nodeState);
        
        /**
         * The node&#39;s progress.
         * 
         * @param progress the property value,  
         * @return this entity builder for chaining
         */
        ConverterNodeEntBuilder setProgress(NodeProgressEnt progress);
        
        /**
         * The list of inputs.
         * 
         * @param inPorts the property value,  
         * @return this entity builder for chaining
         */
        ConverterNodeEntBuilder setInPorts(java.util.List<NodeInPortEnt> inPorts);
        
        /**
         * The list of outputs.
         * 
         * @param outPorts the property value,  
         * @return this entity builder for chaining
         */
        ConverterNodeEntBuilder setOutPorts(java.util.List<NodeOutPortEnt> outPorts);
        
        /**
         * Whether the node is deletable.
         * 
         * @param deletable the property value,  
         * @return this entity builder for chaining
         */
        ConverterNodeEntBuilder setDeletable(Boolean deletable);
        
        /**
         * Whether the node is resetable. Please note that it only represents the &#39;local&#39; reset-state but doesn&#39;t take the whole workflow into account (e.g. executing successors).
         * 
         * @param resetable the property value,  
         * @return this entity builder for chaining
         */
        ConverterNodeEntBuilder setResetable(Boolean resetable);
        
        /**
         * Whether the node has a configuration dialog / user settings.
         * 
         * @param hasDialog the property value,  
         * @return this entity builder for chaining
         */
        ConverterNodeEntBuilder setHasDialog(Boolean hasDialog);
        
        /**
         * The annotation below the node.
         * 
         * @param nodeAnnotation the property value,  
         * @return this entity builder for chaining
         */
        ConverterNodeEntBuilder setNodeAnnotation(NodeAnnotationEnt nodeAnnotation);
        
        /**
         * The names of the available web views. Can be an empty list.
         * 
         * @param webViewNames the property value,  
         * @return this entity builder for chaining
         */
        ConverterNodeEntBuilder setWebViewNames(java.util.List<String> webViewNames);
        
        /**
         * The job manager (e.g. cluster or streaming).
         * 
         * @param jobManager the property value,  
         * @return this entity builder for chaining
         */
        ConverterNodeEntBuilder setJobManager(JobManagerEnt jobManager);
        
        /**
         * 
         * @param uIInfo the property value,  
         * @return this entity builder for chaining
         */
        ConverterNodeEntBuilder setUIInfo(NodeUIInfoEnt uIInfo);
        
        /**
         * 
         * @param execEnvInstanceID the property value,  
         * @return this entity builder for chaining
         */
        ConverterNodeEntBuilder setExecEnvInstanceID(String execEnvInstanceID);
        
        /**
         * The key/ID of the node factory defining all details.
         * 
         * @param nodeFactoryKey the property value, NOT <code>null</code>! 
         * @return this entity builder for chaining
         */
        ConverterNodeEntBuilder setNodeFactoryKey(NodeFactoryKeyEnt nodeFactoryKey);
        
        /**
         * Whether this node is inactive, e.g. due to inactive connections
         * 
         * @param inactive the property value,  
         * @return this entity builder for chaining
         */
        ConverterNodeEntBuilder setInactive(Boolean inactive);
        
        /**
         * 
         * @param outExecEnvInstanceID the property value,  
         * @return this entity builder for chaining
         */
        ConverterNodeEntBuilder setOutExecEnvInstanceID(String outExecEnvInstanceID);
        
        
        /**
        * Creates the entity from the builder.
        * 
        * @return the entity
        * @throws IllegalArgumentException most likely in case when a required property hasn't been set
        */
        @Override
        ConverterNodeEnt build();
    
    }

}

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
package org.knime.gateway.api.webui.entity;

import org.knime.gateway.api.webui.entity.NodeAnnotationEnt;
import org.knime.gateway.api.webui.entity.NodeEnt;
import org.knime.gateway.api.webui.entity.NodeInPortEnt;
import org.knime.gateway.api.webui.entity.NodeMessageEnt;
import org.knime.gateway.api.webui.entity.NodeOutPortEnt;
import org.knime.gateway.api.webui.entity.NodeProgressEnt;
import org.knime.gateway.api.webui.entity.NodeStateEnt;

import org.knime.gateway.api.entity.GatewayEntityBuilder;



/**
 * A node containing (referencing) a workflow (also referred to it as metanode)
 * 
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
@javax.annotation.Generated(value = {"com.knime.gateway.codegen.GatewayCodegen", "src-gen/api/web-ui/configs/org.knime.gateway.api-config.json"})
public interface WorkflowNodeEnt extends NodeEnt {


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
        WorkflowNodeEntBuilder setNodeID(org.knime.gateway.api.entity.NodeIDEnt nodeID);
        
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
        WorkflowNodeEntBuilder setParentNodeID(org.knime.gateway.api.entity.NodeIDEnt parentNodeID);
        
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
   		 * Set nodeAnnotation
         * 
         * @param nodeAnnotation the property value,  
         * @return this entity builder for chaining
         */
        WorkflowNodeEntBuilder setNodeAnnotation(NodeAnnotationEnt nodeAnnotation);
        
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

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
import org.knime.gateway.api.webui.entity.NodeInPortEnt;
import org.knime.gateway.api.webui.entity.NodeMessageEnt;
import org.knime.gateway.api.webui.entity.NodeOutPortEnt;
import org.knime.gateway.api.webui.entity.NodeProgressEnt;
import org.knime.gateway.api.webui.entity.NodeStateEnt;
import org.knime.gateway.api.webui.entity.WorkflowNodeEnt;

import org.knime.gateway.api.entity.GatewayEntityBuilder;



/**
 * A node wrapping (referencing) a workflow (also referred to it as component or subnode) that almost behaves as a ordinary node.
 * 
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
@javax.annotation.Generated(value = {"com.knime.gateway.codegen.GatewayCodegen", "src-gen/api/web-ui/configs/org.knime.gateway.api-config.json"})
public interface ComponentNodeEnt extends WorkflowNodeEnt {



    /**
     * The builder for the entity.
     */
    public interface ComponentNodeEntBuilder extends GatewayEntityBuilder<ComponentNodeEnt> {

        /**
         * Discriminator for inheritance. Must be the base name of this type/schema.
         * 
         * @param type the property value, NOT <code>null</code>! 
         * @return this entity builder for chaining
         */
        ComponentNodeEntBuilder setType(String type);
        
        /**
         * The node&#39;s name.
         * 
         * @param name the property value, NOT <code>null</code>! 
         * @return this entity builder for chaining
         */
        ComponentNodeEntBuilder setName(String name);
        
        /**
         * The ID of the node.
         * 
         * @param nodeID the property value, NOT <code>null</code>! 
         * @return this entity builder for chaining
         */
        ComponentNodeEntBuilder setNodeID(org.knime.gateway.api.entity.NodeIDEnt nodeID);
        
        /**
         * The type of the node.
         * 
         * @param nodeType the property value, NOT <code>null</code>! 
         * @return this entity builder for chaining
         */
        ComponentNodeEntBuilder setNodeType(NodeTypeEnum nodeType);
        
        /**
         * The parent node id of the node or \&quot;root\&quot; if it&#39;s the root node/workflow.
         * 
         * @param parentNodeID the property value,  
         * @return this entity builder for chaining
         */
        ComponentNodeEntBuilder setParentNodeID(org.knime.gateway.api.entity.NodeIDEnt parentNodeID);
        
        /**
         * The id of the root workflow this node is contained in or represents.
         * 
         * @param rootWorkflowID the property value, NOT <code>null</code>! 
         * @return this entity builder for chaining
         */
        ComponentNodeEntBuilder setRootWorkflowID(java.util.UUID rootWorkflowID);
        
        /**
   		 * Set nodeMessage
         * 
         * @param nodeMessage the property value,  
         * @return this entity builder for chaining
         */
        ComponentNodeEntBuilder setNodeMessage(NodeMessageEnt nodeMessage);
        
        /**
   		 * Set nodeState
         * 
         * @param nodeState the property value, NOT <code>null</code>! 
         * @return this entity builder for chaining
         */
        ComponentNodeEntBuilder setNodeState(NodeStateEnt nodeState);
        
        /**
   		 * Set progress
         * 
         * @param progress the property value,  
         * @return this entity builder for chaining
         */
        ComponentNodeEntBuilder setProgress(NodeProgressEnt progress);
        
        /**
         * The list of inputs.
         * 
         * @param inPorts the property value,  
         * @return this entity builder for chaining
         */
        ComponentNodeEntBuilder setInPorts(java.util.List<NodeInPortEnt> inPorts);
        
        /**
         * The list of outputs.
         * 
         * @param outPorts the property value,  
         * @return this entity builder for chaining
         */
        ComponentNodeEntBuilder setOutPorts(java.util.List<NodeOutPortEnt> outPorts);
        
        /**
   		 * Set nodeAnnotation
         * 
         * @param nodeAnnotation the property value,  
         * @return this entity builder for chaining
         */
        ComponentNodeEntBuilder setNodeAnnotation(NodeAnnotationEnt nodeAnnotation);
        
        /**
         * The state of the inner node connected to a particular outport. TODO Should actually be part of a specialization of NodeOutPort (i.e. WorkflowOutPort) but doesn&#39;t work with inheritance and generics in Java.
         * 
         * @param workflowOutgoingPortNodeStates the property value,  
         * @return this entity builder for chaining
         */
        ComponentNodeEntBuilder setWorkflowOutgoingPortNodeStates(java.util.List<NodeStateEnt> workflowOutgoingPortNodeStates);
        
        
        /**
        * Creates the entity from the builder.
        * 
        * @return the entity
        * @throws IllegalArgumentException most likely in case when a required property hasn't been set
        */
        @Override
        ComponentNodeEnt build();
    
    }

}

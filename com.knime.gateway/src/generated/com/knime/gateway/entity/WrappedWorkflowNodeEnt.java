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
import com.knime.gateway.entity.NodeInPortEnt;
import com.knime.gateway.entity.NodeMessageEnt;
import com.knime.gateway.entity.NodeOutPortEnt;
import com.knime.gateway.entity.NodeProgressEnt;
import com.knime.gateway.entity.NodeStateEnt;
import com.knime.gateway.entity.NodeUIInfoEnt;
import com.knime.gateway.entity.WorkflowNodeEnt;

import com.knime.gateway.entity.GatewayEntityBuilder;



/**
 * A node wrapping (referencing) a workflow (also referred to it as component or subnode) that almost behaves as a ordinary node.
 * 
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
@javax.annotation.Generated(value = {"org.knime.gateway.codegen.GatewayCodegen", "src-gen/com.knime.gateway-interfaces-config.json"})
public interface WrappedWorkflowNodeEnt extends WorkflowNodeEnt {


  /**
   * Node ID of the virtual in-node (i.e. source). The node-id format: Node IDs always start with &#39;root&#39; and optionally followed by numbers separated by &#39;:&#39; refering to nested nodes/subworkflows,e.g. root:3:6:4. Nodes within components require an additional trailing &#39;0&#39;, e.g. &#39;root:3:6:0:4&#39; (if &#39;root:3:6&#39; is a component). 
   * @return virtualInNodeID 
   **/
  public com.knime.gateway.entity.NodeIDEnt getVirtualInNodeID();

  /**
   * Node ID of the virtual out-node (i.e. sink). The node-id format: Node IDs always start with &#39;root&#39; and optionally followed by numbers separated by &#39;:&#39; refering to nested nodes/subworkflows,e.g. root:3:6:4. Nodes within components require an additional trailing &#39;0&#39;, e.g. &#39;root:3:6:0:4&#39; (if &#39;root:3:6&#39; is a component). 
   * @return virtualOutNodeID 
   **/
  public com.knime.gateway.entity.NodeIDEnt getVirtualOutNodeID();

  /**
   * Whether this node is inactive, e.g. due to inactive connections
   * @return inactive 
   **/
  public Boolean isInactive();

  /**
   * Whether the node has a wizard page (i.e. a composite web view)
   * @return hasWizardPage 
   **/
  public Boolean hasWizardPage();


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
        WrappedWorkflowNodeEntBuilder setNodeID(com.knime.gateway.entity.NodeIDEnt nodeID);
        
        /**
         * The type of the node.
         * 
         * @param nodeType the property value, NOT <code>null</code>! 
         * @return this entity builder for chaining
         */
        WrappedWorkflowNodeEntBuilder setNodeType(NodeTypeEnum nodeType);
        
        /**
         * The parent node id of the node or \&quot;root\&quot; if it&#39;s the root node/workflow.
         * 
         * @param parentNodeID the property value,  
         * @return this entity builder for chaining
         */
        WrappedWorkflowNodeEntBuilder setParentNodeID(com.knime.gateway.entity.NodeIDEnt parentNodeID);
        
        /**
         * The id of the root workflow this node is contained in or represents.
         * 
         * @param rootWorkflowID the property value, NOT <code>null</code>! 
         * @return this entity builder for chaining
         */
        WrappedWorkflowNodeEntBuilder setRootWorkflowID(java.util.UUID rootWorkflowID);
        
        /**
   		 * Set nodeMessage
         * 
         * @param nodeMessage the property value,  
         * @return this entity builder for chaining
         */
        WrappedWorkflowNodeEntBuilder setNodeMessage(NodeMessageEnt nodeMessage);
        
        /**
   		 * Set nodeState
         * 
         * @param nodeState the property value, NOT <code>null</code>! 
         * @return this entity builder for chaining
         */
        WrappedWorkflowNodeEntBuilder setNodeState(NodeStateEnt nodeState);
        
        /**
   		 * Set progress
         * 
         * @param progress the property value,  
         * @return this entity builder for chaining
         */
        WrappedWorkflowNodeEntBuilder setProgress(NodeProgressEnt progress);
        
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
         * Whether the node is resetable. Please note that it only represents the &#39;local&#39; reset-state but doesn&#39;t take the whole workflow into account (e.g. executing successors).
         * 
         * @param resetable the property value,  
         * @return this entity builder for chaining
         */
        WrappedWorkflowNodeEntBuilder setResetable(Boolean resetable);
        
        /**
         * Whether the node has a configuration dialog / user settings.
         * 
         * @param hasDialog the property value,  
         * @return this entity builder for chaining
         */
        WrappedWorkflowNodeEntBuilder setHasDialog(Boolean hasDialog);
        
        /**
   		 * Set nodeAnnotation
         * 
         * @param nodeAnnotation the property value,  
         * @return this entity builder for chaining
         */
        WrappedWorkflowNodeEntBuilder setNodeAnnotation(NodeAnnotationEnt nodeAnnotation);
        
        /**
         * The names of the available web views. Can be an empty list.
         * 
         * @param webViewNames the property value,  
         * @return this entity builder for chaining
         */
        WrappedWorkflowNodeEntBuilder setWebViewNames(java.util.List<String> webViewNames);
        
        /**
   		 * Set jobManager
         * 
         * @param jobManager the property value,  
         * @return this entity builder for chaining
         */
        WrappedWorkflowNodeEntBuilder setJobManager(JobManagerEnt jobManager);
        
        /**
   		 * Set uIInfo
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
         * Whether the referenced workflow is encrypted and is required to be unlocked before it can be accessed.
         * 
         * @param encrypted the property value,  
         * @return this entity builder for chaining
         */
        WrappedWorkflowNodeEntBuilder setEncrypted(Boolean encrypted);
        
        /**
         * The state of the inner node connected to a particular outport. TODO Should actually be part of a specialization of NodeOutPort (i.e. WorkflowOutPort) but doesn&#39;t work with inheritance and generics in Java.
         * 
         * @param workflowOutgoingPortNodeStates the property value,  
         * @return this entity builder for chaining
         */
        WrappedWorkflowNodeEntBuilder setWorkflowOutgoingPortNodeStates(java.util.List<NodeStateEnt> workflowOutgoingPortNodeStates);
        
        /**
         * Node ID of the virtual in-node (i.e. source). The node-id format: Node IDs always start with &#39;root&#39; and optionally followed by numbers separated by &#39;:&#39; refering to nested nodes/subworkflows,e.g. root:3:6:4. Nodes within components require an additional trailing &#39;0&#39;, e.g. &#39;root:3:6:0:4&#39; (if &#39;root:3:6&#39; is a component). 
         * 
         * @param virtualInNodeID the property value,  
         * @return this entity builder for chaining
         */
        WrappedWorkflowNodeEntBuilder setVirtualInNodeID(com.knime.gateway.entity.NodeIDEnt virtualInNodeID);
        
        /**
         * Node ID of the virtual out-node (i.e. sink). The node-id format: Node IDs always start with &#39;root&#39; and optionally followed by numbers separated by &#39;:&#39; refering to nested nodes/subworkflows,e.g. root:3:6:4. Nodes within components require an additional trailing &#39;0&#39;, e.g. &#39;root:3:6:0:4&#39; (if &#39;root:3:6&#39; is a component). 
         * 
         * @param virtualOutNodeID the property value,  
         * @return this entity builder for chaining
         */
        WrappedWorkflowNodeEntBuilder setVirtualOutNodeID(com.knime.gateway.entity.NodeIDEnt virtualOutNodeID);
        
        /**
         * Whether this node is inactive, e.g. due to inactive connections
         * 
         * @param inactive the property value,  
         * @return this entity builder for chaining
         */
        WrappedWorkflowNodeEntBuilder setInactive(Boolean inactive);
        
        /**
         * Whether the node has a wizard page (i.e. a composite web view)
         * 
         * @param hasWizardPage the property value,  
         * @return this entity builder for chaining
         */
        WrappedWorkflowNodeEntBuilder setHasWizardPage(Boolean hasWizardPage);
        
        
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

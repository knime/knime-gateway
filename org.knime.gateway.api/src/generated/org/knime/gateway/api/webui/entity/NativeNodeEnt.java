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
import org.knime.gateway.api.webui.entity.NodeExecutionStateEnt;
import org.knime.gateway.api.webui.entity.NodePortEnt;
import org.knime.gateway.api.webui.entity.XYEnt;

import org.knime.gateway.api.entity.GatewayEntityBuilder;



/**
 * Native node extension of a node.
 * 
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
@javax.annotation.Generated(value = {"com.knime.gateway.codegen.GatewayCodegen", "src-gen/api/web-ui/configs/org.knime.gateway.api-config.json"})
public interface NativeNodeEnt extends NodeEnt {


  /**
   * The id of the node template this node is an instance of.
   * @return templateId , never <code>null</code>
   **/
  public String getTemplateId();

  /**
   * Get state
   * @return state 
   **/
  public NodeExecutionStateEnt getState();


    /**
     * The builder for the entity.
     */
    public interface NativeNodeEntBuilder extends GatewayEntityBuilder<NativeNodeEnt> {

        /**
         * The id of the node.
         * 
         * @param id the property value, NOT <code>null</code>! 
         * @return this entity builder for chaining
         */
        NativeNodeEntBuilder setId(org.knime.gateway.api.entity.NodeIDEnt id);
        
        /**
         * The list of inputs.
         * 
         * @param inPorts the property value,  
         * @return this entity builder for chaining
         */
        NativeNodeEntBuilder setInPorts(java.util.List<NodePortEnt> inPorts);
        
        /**
         * The list of outputs.
         * 
         * @param outPorts the property value,  
         * @return this entity builder for chaining
         */
        NativeNodeEntBuilder setOutPorts(java.util.List<NodePortEnt> outPorts);
        
        /**
   		 * Set annotation
         * 
         * @param annotation the property value,  
         * @return this entity builder for chaining
         */
        NativeNodeEntBuilder setAnnotation(NodeAnnotationEnt annotation);
        
        /**
   		 * Set position
         * 
         * @param position the property value, NOT <code>null</code>! 
         * @return this entity builder for chaining
         */
        NativeNodeEntBuilder setPosition(XYEnt position);
        
        /**
         * Whether it&#39;s a native node, component or a metanode.
         * 
         * @param propertyClass the property value, NOT <code>null</code>! 
         * @return this entity builder for chaining
         */
        NativeNodeEntBuilder setPropertyClass(PropertyClassEnum propertyClass);
        
        /**
         * The id of the node template this node is an instance of.
         * 
         * @param templateId the property value, NOT <code>null</code>! 
         * @return this entity builder for chaining
         */
        NativeNodeEntBuilder setTemplateId(String templateId);
        
        /**
   		 * Set state
         * 
         * @param state the property value,  
         * @return this entity builder for chaining
         */
        NativeNodeEntBuilder setState(NodeExecutionStateEnt state);
        
        
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

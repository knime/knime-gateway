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
import org.knime.gateway.api.webui.entity.NodePortEnt;
import org.knime.gateway.api.webui.entity.XYEnt;

import org.knime.gateway.api.entity.GatewayEntityBuilder;


import org.knime.gateway.api.entity.GatewayEntity;

/**
 * A node.
 * 
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
@javax.annotation.Generated(value = {"com.knime.gateway.codegen.GatewayCodegen", "src-gen/api/web-ui/configs/org.knime.gateway.api-config.json"})
public interface NodeEnt extends GatewayEntity {

  /**
   * Whether it&#39;s a native node, component or a metanode.
   */
  public enum KindEnum {
    NODE("node"),
    
    COMPONENT("component"),
    
    METANODE("metanode");

    private String value;

    KindEnum(String value) {
      this.value = value;
    }

    @Override
    public String toString() {
      return String.valueOf(value);
    }

  }


  /**
   * The id of the node.
   * @return id , never <code>null</code>
   **/
  public org.knime.gateway.api.entity.NodeIDEnt getId();

  /**
   * The list of inputs.
   * @return inPorts , never <code>null</code>
   **/
  public java.util.List<NodePortEnt> getInPorts();

  /**
   * The list of outputs.
   * @return outPorts , never <code>null</code>
   **/
  public java.util.List<NodePortEnt> getOutPorts();

  /**
   * Get annotation
   * @return annotation 
   **/
  public NodeAnnotationEnt getAnnotation();

  /**
   * Get position
   * @return position , never <code>null</code>
   **/
  public XYEnt getPosition();

  /**
   * Whether it&#39;s a native node, component or a metanode.
   * @return kind , never <code>null</code>
   **/
  public KindEnum getKind();


    /**
     * The builder for the entity.
     */
    public interface NodeEntBuilder extends GatewayEntityBuilder<NodeEnt> {

        /**
         * The id of the node.
         * 
         * @param id the property value, NOT <code>null</code>! 
         * @return this entity builder for chaining
         */
        NodeEntBuilder setId(org.knime.gateway.api.entity.NodeIDEnt id);
        
        /**
         * The list of inputs.
         * 
         * @param inPorts the property value, NOT <code>null</code>! 
         * @return this entity builder for chaining
         */
        NodeEntBuilder setInPorts(java.util.List<NodePortEnt> inPorts);
        
        /**
         * The list of outputs.
         * 
         * @param outPorts the property value, NOT <code>null</code>! 
         * @return this entity builder for chaining
         */
        NodeEntBuilder setOutPorts(java.util.List<NodePortEnt> outPorts);
        
        /**
   		 * Set annotation
         * 
         * @param annotation the property value,  
         * @return this entity builder for chaining
         */
        NodeEntBuilder setAnnotation(NodeAnnotationEnt annotation);
        
        /**
   		 * Set position
         * 
         * @param position the property value, NOT <code>null</code>! 
         * @return this entity builder for chaining
         */
        NodeEntBuilder setPosition(XYEnt position);
        
        /**
         * Whether it&#39;s a native node, component or a metanode.
         * 
         * @param kind the property value, NOT <code>null</code>! 
         * @return this entity builder for chaining
         */
        NodeEntBuilder setKind(KindEnum kind);
        
        
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

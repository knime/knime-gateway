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


import org.knime.gateway.api.entity.GatewayEntityBuilder;


import org.knime.gateway.api.entity.GatewayEntity;

/**
 * General/static properties of a node.
 * 
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
@javax.annotation.Generated(value = {"com.knime.gateway.codegen.GatewayCodegen", "src-gen/api/web-ui/configs/org.knime.gateway.api-config.json"})
public interface NodeTemplateEnt extends GatewayEntity {

  /**
   * The type of the node.
   */
  public enum TypeEnum {
    SOURCE("Source"),
    
    SINK("Sink"),
    
    LEARNER("Learner"),
    
    PREDICTOR("Predictor"),
    
    MANIPULATOR("Manipulator"),
    
    VISUALIZER("Visualizer"),
    
    WIDGET("Widget"),
    
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

    TypeEnum(String value) {
      this.value = value;
    }

    @Override
    public String toString() {
      return String.valueOf(value);
    }

  }


  /**
   * The node&#39;s name.
   * @return name , never <code>null</code>
   **/
  public String getName();

  /**
   * The type of the node.
   * @return type , never <code>null</code>
   **/
  public TypeEnum getType();

  /**
   * The icon encoded in a data-url.
   * @return icon 
   **/
  public String getIcon();


    /**
     * The builder for the entity.
     */
    public interface NodeTemplateEntBuilder extends GatewayEntityBuilder<NodeTemplateEnt> {

        /**
         * The node&#39;s name.
         * 
         * @param name the property value, NOT <code>null</code>! 
         * @return this entity builder for chaining
         */
        NodeTemplateEntBuilder setName(String name);
        
        /**
         * The type of the node.
         * 
         * @param type the property value, NOT <code>null</code>! 
         * @return this entity builder for chaining
         */
        NodeTemplateEntBuilder setType(TypeEnum type);
        
        /**
         * The icon encoded in a data-url.
         * 
         * @param icon the property value,  
         * @return this entity builder for chaining
         */
        NodeTemplateEntBuilder setIcon(String icon);
        
        
        /**
        * Creates the entity from the builder.
        * 
        * @return the entity
        * @throws IllegalArgumentException most likely in case when a required property hasn't been set
        */
        @Override
        NodeTemplateEnt build();
    
    }

}

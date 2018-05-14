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


import com.knime.gateway.entity.GatewayEntityBuilder;


import com.knime.gateway.entity.GatewayEntity;

/**
 * Local variable of basic type which is passed along connections in a workflow.
 * 
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
@javax.annotation.Generated(value = "org.knime.gateway.codegen.GatewayCodegen")
public interface FlowVariableEnt extends GatewayEntity {

  /**
   * The flow variables&#39;s type.
   */
  public enum TypeEnum {
    DOUBLE("DOUBLE"),
    
    INTEGER("INTEGER"),
    
    STRING("STRING");

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
   * The flow variable&#39;s name.
   * @return name 
   **/
  public String getName();

  /**
   * The flow variables&#39;s type.
   * @return type 
   **/
  public TypeEnum getType();

  /**
   * The actual value.
   * @return value 
   **/
  public String getValue();


    /**
     * The builder for the entity.
     */
    public interface FlowVariableEntBuilder extends GatewayEntityBuilder<FlowVariableEnt> {

        /**
         * The flow variable&#39;s name.
         * 
         * @param name the property value,  
         * @return this entity builder for chaining
         */
        FlowVariableEntBuilder setName(String name);
        
        /**
         * The flow variables&#39;s type.
         * 
         * @param type the property value,  
         * @return this entity builder for chaining
         */
        FlowVariableEntBuilder setType(TypeEnum type);
        
        /**
         * The actual value.
         * 
         * @param value the property value,  
         * @return this entity builder for chaining
         */
        FlowVariableEntBuilder setValue(String value);
        
        
        /**
        * Creates the entity from the builder.
        * 
        * @return the entity
        * @throws IllegalArgumentException most likely in case when a required property hasn't been set
        */
        @Override
        FlowVariableEnt build();
    
    }

}

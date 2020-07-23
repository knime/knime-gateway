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
package org.knime.gateway.api.entity;


import org.knime.gateway.api.entity.GatewayEntityBuilder;


import org.knime.gateway.api.entity.GatewayEntity;

/**
 * A JSONPatch document as defined by RFC 6902
 * 
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
@javax.annotation.Generated(value = {"com.knime.gateway.codegen.GatewayCodegen", "src-gen/org.knime.gateway.api-config.json"})
public interface PatchOpEnt extends GatewayEntity {

  /**
   * The operation to be performed
   */
  public enum OpEnum {
    ADD("add"),
    
    REMOVE("remove"),
    
    REPLACE("replace"),
    
    MOVE("move"),
    
    COPY("copy"),
    
    TEST("test");

    private String value;

    OpEnum(String value) {
      this.value = value;
    }

    @Override
    public String toString() {
      return String.valueOf(value);
    }

  }


  /**
   * The operation to be performed
   * @return op , never <code>null</code>
   **/
  public OpEnum getOp();

  /**
   * A JSON-Pointer
   * @return path , never <code>null</code>
   **/
  public String getPath();

  /**
   * The value to be used within the operations.
   * @return value 
   **/
  public Object getValue();

  /**
   * A string containing a JSON Pointer value.
   * @return from 
   **/
  public String getFrom();


    /**
     * The builder for the entity.
     */
    public interface PatchOpEntBuilder extends GatewayEntityBuilder<PatchOpEnt> {

        /**
         * The operation to be performed
         * 
         * @param op the property value, NOT <code>null</code>! 
         * @return this entity builder for chaining
         */
        PatchOpEntBuilder setOp(OpEnum op);
        
        /**
         * A JSON-Pointer
         * 
         * @param path the property value, NOT <code>null</code>! 
         * @return this entity builder for chaining
         */
        PatchOpEntBuilder setPath(String path);
        
        /**
         * The value to be used within the operations.
         * 
         * @param value the property value,  
         * @return this entity builder for chaining
         */
        PatchOpEntBuilder setValue(Object value);
        
        /**
         * A string containing a JSON Pointer value.
         * 
         * @param from the property value,  
         * @return this entity builder for chaining
         */
        PatchOpEntBuilder setFrom(String from);
        
        
        /**
        * Creates the entity from the builder.
        * 
        * @return the entity
        * @throws IllegalArgumentException most likely in case when a required property hasn't been set
        */
        @Override
        PatchOpEnt build();
    
    }

}

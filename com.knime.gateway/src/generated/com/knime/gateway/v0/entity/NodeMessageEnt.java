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
 * node message
 * 
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
@javax.annotation.Generated(value = {"org.knime.gateway.codegen.GatewayCodegen", "src-gen/com.knime.gateway-interfaces-config.json"})
public interface NodeMessageEnt extends GatewayEntity {


  /**
   * Get type
   * @return type 
   **/
  public String getType();

  /**
   * Get message
   * @return message , never <code>null</code>
   **/
  public String getMessage();


    /**
     * The builder for the entity.
     */
    public interface NodeMessageEntBuilder extends GatewayEntityBuilder<NodeMessageEnt> {

        /**
   		 * Set type
         * 
         * @param type the property value,  
         * @return this entity builder for chaining
         */
        NodeMessageEntBuilder setType(String type);
        
        /**
   		 * Set message
         * 
         * @param message the property value, NOT <code>null</code>! 
         * @return this entity builder for chaining
         */
        NodeMessageEntBuilder setMessage(String message);
        
        
        /**
        * Creates the entity from the builder.
        * 
        * @return the entity
        * @throws IllegalArgumentException most likely in case when a required property hasn't been set
        */
        @Override
        NodeMessageEnt build();
    
    }

}

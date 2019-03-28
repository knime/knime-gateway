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


import com.knime.gateway.entity.GatewayEntityBuilder;


import com.knime.gateway.entity.GatewayEntity;

/**
 * Details of an exception thrown by gateway implementations such as services.
 * 
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
@javax.annotation.Generated(value = {"org.knime.gateway.codegen.GatewayCodegen", "src-gen/com.knime.gateway-interfaces-config.json"})
public interface GatewayExceptionEnt extends GatewayEntity {


  /**
   * Simple name of the gateway executor exception.
   * @return exceptionName 
   **/
  public String getExceptionName();

  /**
   * The more detailed exception message.
   * @return exceptionMessage 
   **/
  public String getExceptionMessage();


    /**
     * The builder for the entity.
     */
    public interface GatewayExceptionEntBuilder extends GatewayEntityBuilder<GatewayExceptionEnt> {

        /**
         * Simple name of the gateway executor exception.
         * 
         * @param exceptionName the property value,  
         * @return this entity builder for chaining
         */
        GatewayExceptionEntBuilder setExceptionName(String exceptionName);
        
        /**
         * The more detailed exception message.
         * 
         * @param exceptionMessage the property value,  
         * @return this entity builder for chaining
         */
        GatewayExceptionEntBuilder setExceptionMessage(String exceptionMessage);
        
        
        /**
        * Creates the entity from the builder.
        * 
        * @return the entity
        * @throws IllegalArgumentException most likely in case when a required property hasn't been set
        */
        @Override
        GatewayExceptionEnt build();
    
    }

}

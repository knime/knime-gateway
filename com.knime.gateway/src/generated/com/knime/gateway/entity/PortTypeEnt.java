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
 * The type of a port.
 * 
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
@javax.annotation.Generated(value = {"org.knime.gateway.codegen.GatewayCodegen", "src-gen/com.knime.gateway-interfaces-config.json"})
public interface PortTypeEnt extends GatewayEntity {


  /**
   * Port type class name (for coloring, connection checks).
   * @return portObjectClassName , never <code>null</code>
   **/
  public String getPortObjectClassName();

  /**
   * Whether the port is optional, only applies to input ports.
   * @return optional , never <code>null</code>
   **/
  public Boolean isOptional();


    /**
     * The builder for the entity.
     */
    public interface PortTypeEntBuilder extends GatewayEntityBuilder<PortTypeEnt> {

        /**
         * Port type class name (for coloring, connection checks).
         * 
         * @param portObjectClassName the property value, NOT <code>null</code>! 
         * @return this entity builder for chaining
         */
        PortTypeEntBuilder setPortObjectClassName(String portObjectClassName);
        
        /**
         * Whether the port is optional, only applies to input ports.
         * 
         * @param optional the property value, NOT <code>null</code>! 
         * @return this entity builder for chaining
         */
        PortTypeEntBuilder setOptional(Boolean optional);
        
        
        /**
        * Creates the entity from the builder.
        * 
        * @return the entity
        * @throws IllegalArgumentException most likely in case when a required property hasn't been set
        */
        @Override
        PortTypeEnt build();
    
    }

}

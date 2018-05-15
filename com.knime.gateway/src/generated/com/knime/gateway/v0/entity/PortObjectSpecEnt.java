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

import com.knime.gateway.v0.entity.PortTypeEnt;

import com.knime.gateway.entity.GatewayEntityBuilder;


import com.knime.gateway.entity.GatewayEntity;

/**
 * Specification of a port object.
 * 
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
@javax.annotation.Generated(value = "org.knime.gateway.codegen.GatewayCodegen")
public interface PortObjectSpecEnt extends GatewayEntity {


  /**
   * The port type the specification refers to.
   * @return type , never <code>null</code>
   **/
  public PortTypeEnt getType();

  /**
   * The actual port object spec representation.
   * @return representation 
   **/
  public String getRepresentation();

  /**
   * Flag indicating whether the port is inactive. If true, there will be no representation available.
   * @return inactive , never <code>null</code>
   **/
  public Boolean isInactive();


    /**
     * The builder for the entity.
     */
    public interface PortObjectSpecEntBuilder extends GatewayEntityBuilder<PortObjectSpecEnt> {

        /**
         * The port type the specification refers to.
         * 
         * @param type the property value, NOT <code>null</code>! 
         * @return this entity builder for chaining
         */
        PortObjectSpecEntBuilder setType(PortTypeEnt type);
        
        /**
         * The actual port object spec representation.
         * 
         * @param representation the property value,  
         * @return this entity builder for chaining
         */
        PortObjectSpecEntBuilder setRepresentation(String representation);
        
        /**
         * Flag indicating whether the port is inactive. If true, there will be no representation available.
         * 
         * @param inactive the property value, NOT <code>null</code>! 
         * @return this entity builder for chaining
         */
        PortObjectSpecEntBuilder setInactive(Boolean inactive);
        
        
        /**
        * Creates the entity from the builder.
        * 
        * @return the entity
        * @throws IllegalArgumentException most likely in case when a required property hasn't been set
        */
        @Override
        PortObjectSpecEnt build();
    
    }

}

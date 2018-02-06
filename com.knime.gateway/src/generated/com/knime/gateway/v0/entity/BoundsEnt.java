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
 * Node dimensions - position and size.
 * 
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
@javax.annotation.Generated(value = "org.knime.gateway.codegen.GatewayCodegen")
public interface BoundsEnt extends GatewayEntity {


  /**
   * Get x
   * @return x 
   **/
  public Integer getX();

  /**
   * Get y
   * @return y 
   **/
  public Integer getY();

  /**
   * Get width
   * @return width 
   **/
  public Integer getWidth();

  /**
   * Get height
   * @return height 
   **/
  public Integer getHeight();


    /**
     * The builder for the entity.
     */
    public interface BoundsEntBuilder extends GatewayEntityBuilder<BoundsEnt> {

        /**
         * 
         * @param x the property value,  
         * @return this entity builder for chaining
         */
        BoundsEntBuilder setX(Integer x);
        
        /**
         * 
         * @param y the property value,  
         * @return this entity builder for chaining
         */
        BoundsEntBuilder setY(Integer y);
        
        /**
         * 
         * @param width the property value,  
         * @return this entity builder for chaining
         */
        BoundsEntBuilder setWidth(Integer width);
        
        /**
         * 
         * @param height the property value,  
         * @return this entity builder for chaining
         */
        BoundsEntBuilder setHeight(Integer height);
        
        
        /**
        * Creates the entity from the builder.
        * 
        * @return the entity
        * @throws IllegalArgumentException most likely in case when a required property hasn't been set
        */
        @Override
        BoundsEnt build();
    
    }

}

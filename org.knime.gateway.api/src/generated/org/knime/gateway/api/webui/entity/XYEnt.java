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
 * XYEnt
 * 
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
@javax.annotation.Generated(value = {"com.knime.gateway.codegen.GatewayCodegen", "src-gen/api/web-ui/configs/org.knime.gateway.api-config.json"})
public interface XYEnt extends GatewayEntity {


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
     * The builder for the entity.
     */
    public interface XYEntBuilder extends GatewayEntityBuilder<XYEnt> {

        /**
   		 * Set x
         * 
         * @param x the property value,  
         * @return this entity builder for chaining
         */
        XYEntBuilder setX(Integer x);
        
        /**
   		 * Set y
         * 
         * @param y the property value,  
         * @return this entity builder for chaining
         */
        XYEntBuilder setY(Integer y);
        
        
        /**
        * Creates the entity from the builder.
        * 
        * @return the entity
        * @throws IllegalArgumentException most likely in case when a required property hasn't been set
        */
        @Override
        XYEnt build();
    
    }

}

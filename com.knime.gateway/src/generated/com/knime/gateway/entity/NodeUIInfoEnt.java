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

import com.knime.gateway.entity.BoundsEnt;

import com.knime.gateway.entity.GatewayEntityBuilder;


import com.knime.gateway.entity.GatewayEntity;

/**
 * Essentially the position of a node including some flags indicating a necessary correction of that given position.
 * 
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
@javax.annotation.Generated(value = {"org.knime.gateway.codegen.GatewayCodegen", "src-gen/com.knime.gateway-interfaces-config.json"})
public interface NodeUIInfoEnt extends GatewayEntity {


  /**
   * Get bounds
   * @return bounds , never <code>null</code>
   **/
  public BoundsEnt getBounds();

  /**
   * Get symbolRelative
   * @return symbolRelative 
   **/
  public Boolean isSymbolRelative();

  /**
   * Get dropLocation
   * @return dropLocation 
   **/
  public Boolean isDropLocation();

  /**
   * Get snapToGrid
   * @return snapToGrid 
   **/
  public Boolean isSnapToGrid();


    /**
     * The builder for the entity.
     */
    public interface NodeUIInfoEntBuilder extends GatewayEntityBuilder<NodeUIInfoEnt> {

        /**
   		 * Set bounds
         * 
         * @param bounds the property value, NOT <code>null</code>! 
         * @return this entity builder for chaining
         */
        NodeUIInfoEntBuilder setBounds(BoundsEnt bounds);
        
        /**
   		 * Set symbolRelative
         * 
         * @param symbolRelative the property value,  
         * @return this entity builder for chaining
         */
        NodeUIInfoEntBuilder setSymbolRelative(Boolean symbolRelative);
        
        /**
   		 * Set dropLocation
         * 
         * @param dropLocation the property value,  
         * @return this entity builder for chaining
         */
        NodeUIInfoEntBuilder setDropLocation(Boolean dropLocation);
        
        /**
   		 * Set snapToGrid
         * 
         * @param snapToGrid the property value,  
         * @return this entity builder for chaining
         */
        NodeUIInfoEntBuilder setSnapToGrid(Boolean snapToGrid);
        
        
        /**
        * Creates the entity from the builder.
        * 
        * @return the entity
        * @throws IllegalArgumentException most likely in case when a required property hasn't been set
        */
        @Override
        NodeUIInfoEnt build();
    
    }

}
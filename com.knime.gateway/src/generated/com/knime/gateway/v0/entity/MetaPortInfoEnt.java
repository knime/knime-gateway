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
 * The port of a metanode.
 * 
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
@javax.annotation.Generated(value = {"org.knime.gateway.codegen.GatewayCodegen", "src-gen/com.knime.gateway-interfaces-config.json"})
public interface MetaPortInfoEnt extends GatewayEntity {


  /**
   * Get portType
   * @return portType , never <code>null</code>
   **/
  public PortTypeEnt getPortType();

  /**
   * Whether it is connected.
   * @return connected 
   **/
  public Boolean isConnected();

  /**
   * The message (summary of upstream node port).
   * @return message 
   **/
  public String getMessage();

  /**
   * The old index.
   * @return oldIndex 
   **/
  public Integer getOldIndex();

  /**
   * The new index.
   * @return newIndex 
   **/
  public Integer getNewIndex();


    /**
     * The builder for the entity.
     */
    public interface MetaPortInfoEntBuilder extends GatewayEntityBuilder<MetaPortInfoEnt> {

        /**
   		 * Set portType
         * 
         * @param portType the property value, NOT <code>null</code>! 
         * @return this entity builder for chaining
         */
        MetaPortInfoEntBuilder setPortType(PortTypeEnt portType);
        
        /**
         * Whether it is connected.
         * 
         * @param connected the property value,  
         * @return this entity builder for chaining
         */
        MetaPortInfoEntBuilder setConnected(Boolean connected);
        
        /**
         * The message (summary of upstream node port).
         * 
         * @param message the property value,  
         * @return this entity builder for chaining
         */
        MetaPortInfoEntBuilder setMessage(String message);
        
        /**
         * The old index.
         * 
         * @param oldIndex the property value,  
         * @return this entity builder for chaining
         */
        MetaPortInfoEntBuilder setOldIndex(Integer oldIndex);
        
        /**
         * The new index.
         * 
         * @param newIndex the property value,  
         * @return this entity builder for chaining
         */
        MetaPortInfoEntBuilder setNewIndex(Integer newIndex);
        
        
        /**
        * Creates the entity from the builder.
        * 
        * @return the entity
        * @throws IllegalArgumentException most likely in case when a required property hasn't been set
        */
        @Override
        MetaPortInfoEnt build();
    
    }

}

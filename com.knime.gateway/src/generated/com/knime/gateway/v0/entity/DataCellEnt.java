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
 * A cell of a data table.
 * 
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
@javax.annotation.Generated(value = "org.knime.gateway.codegen.GatewayCodegen")
public interface DataCellEnt extends GatewayEntity {


  /**
   * The cell value as a string.
   * @return valueAsString 
   **/
  public String getValueAsString();


    /**
     * The builder for the entity.
     */
    public interface DataCellEntBuilder extends GatewayEntityBuilder<DataCellEnt> {

        /**
         * The cell value as a string.
         * 
         * @param valueAsString the property value,  
         * @return this entity builder for chaining
         */
        DataCellEntBuilder setValueAsString(String valueAsString);
        
        
        /**
        * Creates the entity from the builder.
        * 
        * @return the entity
        * @throws IllegalArgumentException most likely in case when a required property hasn't been set
        */
        @Override
        DataCellEnt build();
    
    }

}

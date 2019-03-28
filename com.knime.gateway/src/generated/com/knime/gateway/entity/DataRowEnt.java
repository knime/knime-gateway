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

import com.knime.gateway.entity.DataCellEnt;

import com.knime.gateway.entity.GatewayEntityBuilder;


import com.knime.gateway.entity.GatewayEntity;

/**
 * A data row.
 * 
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
@javax.annotation.Generated(value = {"org.knime.gateway.codegen.GatewayCodegen", "src-gen/com.knime.gateway-interfaces-config.json"})
public interface DataRowEnt extends GatewayEntity {


  /**
   * The row id.
   * @return rowID 
   **/
  public String getRowID();

  /**
   * The column list.
   * @return columns 
   **/
  public java.util.List<DataCellEnt> getColumns();


    /**
     * The builder for the entity.
     */
    public interface DataRowEntBuilder extends GatewayEntityBuilder<DataRowEnt> {

        /**
         * The row id.
         * 
         * @param rowID the property value,  
         * @return this entity builder for chaining
         */
        DataRowEntBuilder setRowID(String rowID);
        
        /**
         * The column list.
         * 
         * @param columns the property value,  
         * @return this entity builder for chaining
         */
        DataRowEntBuilder setColumns(java.util.List<DataCellEnt> columns);
        
        
        /**
        * Creates the entity from the builder.
        * 
        * @return the entity
        * @throws IllegalArgumentException most likely in case when a required property hasn't been set
        */
        @Override
        DataRowEnt build();
    
    }

}

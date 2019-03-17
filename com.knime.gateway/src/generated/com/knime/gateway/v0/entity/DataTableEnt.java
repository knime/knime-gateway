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

import com.knime.gateway.v0.entity.DataRowEnt;

import com.knime.gateway.entity.GatewayEntityBuilder;


import com.knime.gateway.entity.GatewayEntity;

/**
 * A data table. Might not contain all rows but only a chunk of rows.
 * 
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
@javax.annotation.Generated(value = {"org.knime.gateway.codegen.GatewayCodegen", "src-gen/com.knime.gateway-interfaces-config.json"})
public interface DataTableEnt extends GatewayEntity {


  /**
   * The column names.
   * @return columnNames 
   **/
  public java.util.List<String> getColumnNames();

  /**
   * The total number of rows. Might be larger than the number of rows contained here. If a value &lt; 0 indicates that the row count is not know.
   * @return numTotalRows 
   **/
  public Long getNumTotalRows();

  /**
   * The actual rows.
   * @return rows 
   **/
  public java.util.List<DataRowEnt> getRows();


    /**
     * The builder for the entity.
     */
    public interface DataTableEntBuilder extends GatewayEntityBuilder<DataTableEnt> {

        /**
         * The column names.
         * 
         * @param columnNames the property value,  
         * @return this entity builder for chaining
         */
        DataTableEntBuilder setColumnNames(java.util.List<String> columnNames);
        
        /**
         * The total number of rows. Might be larger than the number of rows contained here. If a value &lt; 0 indicates that the row count is not know.
         * 
         * @param numTotalRows the property value,  
         * @return this entity builder for chaining
         */
        DataTableEntBuilder setNumTotalRows(Long numTotalRows);
        
        /**
         * The actual rows.
         * 
         * @param rows the property value,  
         * @return this entity builder for chaining
         */
        DataTableEntBuilder setRows(java.util.List<DataRowEnt> rows);
        
        
        /**
        * Creates the entity from the builder.
        * 
        * @return the entity
        * @throws IllegalArgumentException most likely in case when a required property hasn't been set
        */
        @Override
        DataTableEnt build();
    
    }

}

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
package org.knime.gateway.impl.entity;

import static org.knime.gateway.api.util.EntityUtil.immutable;

import java.util.Objects;

import org.knime.gateway.api.entity.DataRowEnt;

import org.knime.gateway.api.entity.DataTableEnt;

/**
 * A data table. Might not contain all rows but only a chunk of rows.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
@javax.annotation.Generated(value = {"com.knime.gateway.codegen.GatewayCodegen", "src-gen/org.knime.gateway.impl-config.json"})
public class DefaultDataTableEnt  implements DataTableEnt {

  protected java.util.List<String> m_columnNames;
  protected Long m_numTotalRows;
  protected java.util.List<DataRowEnt> m_rows;
  
  protected DefaultDataTableEnt() {
    //for sub-classes
  }
  
  @Override
  public String getTypeID() {
    return "DataTable";
  }
  
  private DefaultDataTableEnt(DefaultDataTableEntBuilder builder) {
    
    m_columnNames = immutable(builder.m_columnNames);
    m_numTotalRows = immutable(builder.m_numTotalRows);
    m_rows = immutable(builder.m_rows);
  }
  
   /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null) {
            return false;
        }
        if (getClass() != o.getClass()) {
            return false;
        }
        DefaultDataTableEnt ent = (DefaultDataTableEnt)o;
        return Objects.equals(m_columnNames, ent.m_columnNames) && Objects.equals(m_numTotalRows, ent.m_numTotalRows) && Objects.equals(m_rows, ent.m_rows);
    }


  @Override
  public java.util.List<String> getColumnNames() {
        return m_columnNames;
    }
    
  @Override
  public Long getNumTotalRows() {
        return m_numTotalRows;
    }
    
  @Override
  public java.util.List<DataRowEnt> getRows() {
        return m_rows;
    }
    
  
    public static class DefaultDataTableEntBuilder implements DataTableEntBuilder {
    
        public DefaultDataTableEntBuilder(){
            
        }
    
        private java.util.List<String> m_columnNames = new java.util.ArrayList<>();
        private Long m_numTotalRows;
        private java.util.List<DataRowEnt> m_rows = new java.util.ArrayList<>();

        @Override
        public DefaultDataTableEntBuilder setColumnNames(java.util.List<String> columnNames) {
             m_columnNames = columnNames;
             return this;
        }

        @Override
        public DefaultDataTableEntBuilder setNumTotalRows(Long numTotalRows) {
             m_numTotalRows = numTotalRows;
             return this;
        }

        @Override
        public DefaultDataTableEntBuilder setRows(java.util.List<DataRowEnt> rows) {
             m_rows = rows;
             return this;
        }

        
        @Override
        public DefaultDataTableEnt build() {
            return new DefaultDataTableEnt(this);
        }
    
    }

}

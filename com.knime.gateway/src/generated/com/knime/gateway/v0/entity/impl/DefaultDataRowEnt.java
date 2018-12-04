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
package com.knime.gateway.v0.entity.impl;

import static com.knime.gateway.util.EntityUtil.immutable;

import java.util.Objects;

import com.knime.gateway.v0.entity.DataCellEnt;

import com.knime.gateway.v0.entity.DataRowEnt;

/**
 * A data row.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
@javax.annotation.Generated(value = "org.knime.gateway.codegen.GatewayCodegen")
public class DefaultDataRowEnt  implements DataRowEnt {

  protected String m_rowID;
  protected java.util.List<DataCellEnt> m_columns;
  
  protected DefaultDataRowEnt() {
    //for sub-classes
  }
  
  @Override
  public String getTypeID() {
    return "DataRow";
  }
  
  private DefaultDataRowEnt(DefaultDataRowEntBuilder builder) {
    
    m_rowID = immutable(builder.m_rowID);
    m_columns = immutable(builder.m_columns);
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
        DefaultDataRowEnt ent = (DefaultDataRowEnt)o;
        return Objects.equals(m_rowID, ent.m_rowID) && Objects.equals(m_columns, ent.m_columns);
    }


  @Override
  public String getRowID() {
        return m_rowID;
    }
    
  @Override
  public java.util.List<DataCellEnt> getColumns() {
        return m_columns;
    }
    
  
    public static class DefaultDataRowEntBuilder implements DataRowEntBuilder {
    
        public DefaultDataRowEntBuilder(){
            
        }
    
        private String m_rowID;
        private java.util.List<DataCellEnt> m_columns = new java.util.ArrayList<>();

        @Override
        public DefaultDataRowEntBuilder setRowID(String rowID) {
             m_rowID = rowID;
             return this;
        }

        @Override
        public DefaultDataRowEntBuilder setColumns(java.util.List<DataCellEnt> columns) {
             m_columns = columns;
             return this;
        }

        
        @Override
        public DefaultDataRowEnt build() {
            return new DefaultDataRowEnt(this);
        }
    
    }

}

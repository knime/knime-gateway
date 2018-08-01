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

/**
 * A cell of a data table.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
@javax.annotation.Generated(value = "org.knime.gateway.codegen.GatewayCodegen")
public class DefaultDataCellEnt  implements DataCellEnt {

  protected String m_valueAsString;
  
  protected DefaultDataCellEnt() {
    //for sub-classes
  }
  
  @Override
  public String getTypeID() {
    return "DataCell";
  }
  
  private DefaultDataCellEnt(DefaultDataCellEntBuilder builder) {
    
    m_valueAsString = immutable(builder.m_valueAsString);
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
        DefaultDataCellEnt ent = (DefaultDataCellEnt)o;
        return Objects.equals(m_valueAsString, ent.m_valueAsString);
    }


  @Override
  public String getValueAsString() {
        return m_valueAsString;
    }
    
  
    public static class DefaultDataCellEntBuilder implements DataCellEntBuilder {
    
        public DefaultDataCellEntBuilder(){
            
        }
    
        private String m_valueAsString = null;

        @Override
        public DefaultDataCellEntBuilder setValueAsString(String valueAsString) {
             m_valueAsString = valueAsString;
             return this;
        }

        
        @Override
        public DefaultDataCellEnt build() {
            return new DefaultDataCellEnt(this);
        }
    
    }

}

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

  protected String m_type;
  protected String m_valueAsString;
  protected Boolean m_missing;
  protected Boolean m_binary;
  protected Boolean m_problem;
  
  protected DefaultDataCellEnt() {
    //for sub-classes
  }
  
  @Override
  public String getTypeID() {
    return "DataCell";
  }
  
  private DefaultDataCellEnt(DefaultDataCellEntBuilder builder) {
    
    m_type = immutable(builder.m_type);
    m_valueAsString = immutable(builder.m_valueAsString);
    m_missing = immutable(builder.m_missing);
    m_binary = immutable(builder.m_binary);
    m_problem = immutable(builder.m_problem);
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
        return Objects.equals(m_type, ent.m_type) && Objects.equals(m_valueAsString, ent.m_valueAsString) && Objects.equals(m_missing, ent.m_missing) && Objects.equals(m_binary, ent.m_binary) && Objects.equals(m_problem, ent.m_problem);
    }


  @Override
  public String getType() {
        return m_type;
    }
    
  @Override
  public String getValueAsString() {
        return m_valueAsString;
    }
    
  @Override
  public Boolean isMissing() {
        return m_missing;
    }
    
  @Override
  public Boolean isBinary() {
        return m_binary;
    }
    
  @Override
  public Boolean isProblem() {
        return m_problem;
    }
    
  
    public static class DefaultDataCellEntBuilder implements DataCellEntBuilder {
    
        public DefaultDataCellEntBuilder(){
            
        }
    
        private String m_type = null;
        private String m_valueAsString = null;
        private Boolean m_missing = null;
        private Boolean m_binary = null;
        private Boolean m_problem = null;

        @Override
        public DefaultDataCellEntBuilder setType(String type) {
             m_type = type;
             return this;
        }

        @Override
        public DefaultDataCellEntBuilder setValueAsString(String valueAsString) {
             m_valueAsString = valueAsString;
             return this;
        }

        @Override
        public DefaultDataCellEntBuilder setMissing(Boolean missing) {
             m_missing = missing;
             return this;
        }

        @Override
        public DefaultDataCellEntBuilder setBinary(Boolean binary) {
             m_binary = binary;
             return this;
        }

        @Override
        public DefaultDataCellEntBuilder setProblem(Boolean problem) {
             m_problem = problem;
             return this;
        }

        
        @Override
        public DefaultDataCellEnt build() {
            return new DefaultDataCellEnt(this);
        }
    
    }

}

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

import static com.knime.gateway.util.DefaultEntUtil.immutable;

import java.util.Objects;

import com.knime.gateway.v0.entity.PortTypeEnt;

import com.knime.gateway.v0.entity.PortObjectSpecEnt;

/**
 * Specification of a port object.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
@javax.annotation.Generated(value = "org.knime.gateway.codegen.GatewayCodegen")
public class DefaultPortObjectSpecEnt  implements PortObjectSpecEnt {

  protected PortTypeEnt m_type;
  protected String m_representation;
  
  protected DefaultPortObjectSpecEnt() {
    //for sub-classes
  }
  
  @Override
  public String getTypeID() {
    return "PortObjectSpec";
  }
  
  private DefaultPortObjectSpecEnt(DefaultPortObjectSpecEntBuilder builder) {
    
    m_type = immutable(builder.m_type);
    m_representation = immutable(builder.m_representation);
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
        DefaultPortObjectSpecEnt ent = (DefaultPortObjectSpecEnt)o;
        return Objects.equals(m_type, ent.m_type) && Objects.equals(m_representation, ent.m_representation);
    }


  @Override
  public PortTypeEnt getType() {
        return m_type;
    }
    
  @Override
  public String getRepresentation() {
        return m_representation;
    }
    
  
    public static class DefaultPortObjectSpecEntBuilder implements PortObjectSpecEntBuilder {
    
        public DefaultPortObjectSpecEntBuilder(){
            
        }
    
        private PortTypeEnt m_type;
        private String m_representation = null;

        @Override
        public DefaultPortObjectSpecEntBuilder setType(PortTypeEnt type) {
             m_type = type;
             return this;
        }

        @Override
        public DefaultPortObjectSpecEntBuilder setRepresentation(String representation) {
             m_representation = representation;
             return this;
        }

        
        @Override
        public DefaultPortObjectSpecEnt build() {
            return new DefaultPortObjectSpecEnt(this);
        }
    
    }

}

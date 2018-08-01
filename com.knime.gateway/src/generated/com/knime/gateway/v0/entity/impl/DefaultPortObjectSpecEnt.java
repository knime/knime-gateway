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
  protected Boolean m_inactive;
  
  protected DefaultPortObjectSpecEnt() {
    //for sub-classes
  }
  
  @Override
  public String getTypeID() {
    return "PortObjectSpec";
  }
  
  private DefaultPortObjectSpecEnt(DefaultPortObjectSpecEntBuilder builder) {
    
    if(builder.m_type == null) {
        throw new IllegalArgumentException("type must not be null.");
    }
    m_type = immutable(builder.m_type);
    m_representation = immutable(builder.m_representation);
    if(builder.m_inactive == null) {
        throw new IllegalArgumentException("inactive must not be null.");
    }
    m_inactive = immutable(builder.m_inactive);
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
        return Objects.equals(m_type, ent.m_type) && Objects.equals(m_representation, ent.m_representation) && Objects.equals(m_inactive, ent.m_inactive);
    }


  @Override
  public PortTypeEnt getType() {
        return m_type;
    }
    
  @Override
  public String getRepresentation() {
        return m_representation;
    }
    
  @Override
  public Boolean isInactive() {
        return m_inactive;
    }
    
  
    public static class DefaultPortObjectSpecEntBuilder implements PortObjectSpecEntBuilder {
    
        public DefaultPortObjectSpecEntBuilder(){
            
        }
    
        private PortTypeEnt m_type;
        private String m_representation = null;
        private Boolean m_inactive = null;

        @Override
        public DefaultPortObjectSpecEntBuilder setType(PortTypeEnt type) {
             if(type == null) {
                 throw new IllegalArgumentException("type must not be null.");
             }
             m_type = type;
             return this;
        }

        @Override
        public DefaultPortObjectSpecEntBuilder setRepresentation(String representation) {
             m_representation = representation;
             return this;
        }

        @Override
        public DefaultPortObjectSpecEntBuilder setInactive(Boolean inactive) {
             if(inactive == null) {
                 throw new IllegalArgumentException("inactive must not be null.");
             }
             m_inactive = inactive;
             return this;
        }

        
        @Override
        public DefaultPortObjectSpecEnt build() {
            return new DefaultPortObjectSpecEnt(this);
        }
    
    }

}

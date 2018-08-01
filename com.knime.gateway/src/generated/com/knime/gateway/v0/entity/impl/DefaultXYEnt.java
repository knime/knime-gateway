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


import com.knime.gateway.v0.entity.XYEnt;

/**
 * XY coordinate
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
@javax.annotation.Generated(value = "org.knime.gateway.codegen.GatewayCodegen")
public class DefaultXYEnt  implements XYEnt {

  protected Integer m_x;
  protected Integer m_y;
  
  protected DefaultXYEnt() {
    //for sub-classes
  }
  
  @Override
  public String getTypeID() {
    return "XY";
  }
  
  private DefaultXYEnt(DefaultXYEntBuilder builder) {
    
    m_x = immutable(builder.m_x);
    m_y = immutable(builder.m_y);
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
        DefaultXYEnt ent = (DefaultXYEnt)o;
        return Objects.equals(m_x, ent.m_x) && Objects.equals(m_y, ent.m_y);
    }


  @Override
  public Integer getX() {
        return m_x;
    }
    
  @Override
  public Integer getY() {
        return m_y;
    }
    
  
    public static class DefaultXYEntBuilder implements XYEntBuilder {
    
        public DefaultXYEntBuilder(){
            
        }
    
        private Integer m_x = null;
        private Integer m_y = null;

        @Override
        public DefaultXYEntBuilder setX(Integer x) {
             m_x = x;
             return this;
        }

        @Override
        public DefaultXYEntBuilder setY(Integer y) {
             m_y = y;
             return this;
        }

        
        @Override
        public DefaultXYEnt build() {
            return new DefaultXYEnt(this);
        }
    
    }

}

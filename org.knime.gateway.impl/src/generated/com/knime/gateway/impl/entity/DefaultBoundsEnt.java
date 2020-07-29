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
package com.knime.gateway.impl.entity;

import static org.knime.gateway.api.util.EntityUtil.immutable;

import java.util.Objects;


import org.knime.gateway.api.entity.BoundsEnt;

/**
 * Node dimensions - position and size.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
@javax.annotation.Generated(value = {"com.knime.gateway.codegen.GatewayCodegen", "src-gen/api/java-ui/configs/com.knime.gateway.impl-config.json"})
public class DefaultBoundsEnt  implements BoundsEnt {

  protected Integer m_x;
  protected Integer m_y;
  protected Integer m_width;
  protected Integer m_height;
  
  protected DefaultBoundsEnt() {
    //for sub-classes
  }
  
  @Override
  public String getTypeID() {
    return "Bounds";
  }
  
  private DefaultBoundsEnt(DefaultBoundsEntBuilder builder) {
    
    if(builder.m_x == null) {
        throw new IllegalArgumentException("x must not be null.");
    }
    m_x = immutable(builder.m_x);
    if(builder.m_y == null) {
        throw new IllegalArgumentException("y must not be null.");
    }
    m_y = immutable(builder.m_y);
    if(builder.m_width == null) {
        throw new IllegalArgumentException("width must not be null.");
    }
    m_width = immutable(builder.m_width);
    if(builder.m_height == null) {
        throw new IllegalArgumentException("height must not be null.");
    }
    m_height = immutable(builder.m_height);
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
        DefaultBoundsEnt ent = (DefaultBoundsEnt)o;
        return Objects.equals(m_x, ent.m_x) && Objects.equals(m_y, ent.m_y) && Objects.equals(m_width, ent.m_width) && Objects.equals(m_height, ent.m_height);
    }


  @Override
  public Integer getX() {
        return m_x;
    }
    
  @Override
  public Integer getY() {
        return m_y;
    }
    
  @Override
  public Integer getWidth() {
        return m_width;
    }
    
  @Override
  public Integer getHeight() {
        return m_height;
    }
    
  
    public static class DefaultBoundsEntBuilder implements BoundsEntBuilder {
    
        public DefaultBoundsEntBuilder(){
            
        }
    
        private Integer m_x;
        private Integer m_y;
        private Integer m_width;
        private Integer m_height;

        @Override
        public DefaultBoundsEntBuilder setX(Integer x) {
             if(x == null) {
                 throw new IllegalArgumentException("x must not be null.");
             }
             m_x = x;
             return this;
        }

        @Override
        public DefaultBoundsEntBuilder setY(Integer y) {
             if(y == null) {
                 throw new IllegalArgumentException("y must not be null.");
             }
             m_y = y;
             return this;
        }

        @Override
        public DefaultBoundsEntBuilder setWidth(Integer width) {
             if(width == null) {
                 throw new IllegalArgumentException("width must not be null.");
             }
             m_width = width;
             return this;
        }

        @Override
        public DefaultBoundsEntBuilder setHeight(Integer height) {
             if(height == null) {
                 throw new IllegalArgumentException("height must not be null.");
             }
             m_height = height;
             return this;
        }

        
        @Override
        public DefaultBoundsEnt build() {
            return new DefaultBoundsEnt(this);
        }
    
    }

}

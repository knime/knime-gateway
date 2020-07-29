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

import com.knime.gateway.api.entity.BoundsEnt;

import org.knime.gateway.api.entity.NodeUIInfoEnt;

/**
 * Essentially the position of a node including some flags indicating a necessary correction of that given position.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
@javax.annotation.Generated(value = {"com.knime.gateway.codegen.GatewayCodegen", "src-gen/api/java-ui/configs/com.knime.gateway.impl-config.json"})
public class DefaultNodeUIInfoEnt  implements NodeUIInfoEnt {

  protected BoundsEnt m_bounds;
  protected Boolean m_symbolRelative;
  protected Boolean m_dropLocation;
  protected Boolean m_snapToGrid;
  
  protected DefaultNodeUIInfoEnt() {
    //for sub-classes
  }
  
  @Override
  public String getTypeID() {
    return "NodeUIInfo";
  }
  
  private DefaultNodeUIInfoEnt(DefaultNodeUIInfoEntBuilder builder) {
    
    if(builder.m_bounds == null) {
        throw new IllegalArgumentException("bounds must not be null.");
    }
    m_bounds = immutable(builder.m_bounds);
    m_symbolRelative = immutable(builder.m_symbolRelative);
    m_dropLocation = immutable(builder.m_dropLocation);
    m_snapToGrid = immutable(builder.m_snapToGrid);
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
        DefaultNodeUIInfoEnt ent = (DefaultNodeUIInfoEnt)o;
        return Objects.equals(m_bounds, ent.m_bounds) && Objects.equals(m_symbolRelative, ent.m_symbolRelative) && Objects.equals(m_dropLocation, ent.m_dropLocation) && Objects.equals(m_snapToGrid, ent.m_snapToGrid);
    }


  @Override
  public BoundsEnt getBounds() {
        return m_bounds;
    }
    
  @Override
  public Boolean isSymbolRelative() {
        return m_symbolRelative;
    }
    
  @Override
  public Boolean isDropLocation() {
        return m_dropLocation;
    }
    
  @Override
  public Boolean isSnapToGrid() {
        return m_snapToGrid;
    }
    
  
    public static class DefaultNodeUIInfoEntBuilder implements NodeUIInfoEntBuilder {
    
        public DefaultNodeUIInfoEntBuilder(){
            
        }
    
        private BoundsEnt m_bounds;
        private Boolean m_symbolRelative = false;
        private Boolean m_dropLocation = false;
        private Boolean m_snapToGrid = false;

        @Override
        public DefaultNodeUIInfoEntBuilder setBounds(BoundsEnt bounds) {
             if(bounds == null) {
                 throw new IllegalArgumentException("bounds must not be null.");
             }
             m_bounds = bounds;
             return this;
        }

        @Override
        public DefaultNodeUIInfoEntBuilder setSymbolRelative(Boolean symbolRelative) {
             m_symbolRelative = symbolRelative;
             return this;
        }

        @Override
        public DefaultNodeUIInfoEntBuilder setDropLocation(Boolean dropLocation) {
             m_dropLocation = dropLocation;
             return this;
        }

        @Override
        public DefaultNodeUIInfoEntBuilder setSnapToGrid(Boolean snapToGrid) {
             m_snapToGrid = snapToGrid;
             return this;
        }

        
        @Override
        public DefaultNodeUIInfoEnt build() {
            return new DefaultNodeUIInfoEnt(this);
        }
    
    }

}

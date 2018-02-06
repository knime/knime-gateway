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

import com.knime.gateway.v0.entity.BoundsEnt;

import com.knime.gateway.v0.entity.NodeUIInfoEnt;

/**
 * DefaultNodeUIInfoEnt
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
@javax.annotation.Generated(value = "org.knime.gateway.codegen.GatewayCodegen")
public class DefaultNodeUIInfoEnt  implements NodeUIInfoEnt {

  protected BoundsEnt m_bounds;
  protected Boolean m_symbolRelative;
  protected Boolean m_hasAbsoluteCoordinates;
  protected Boolean m_dropLocation;
  protected Boolean m_snapToGrid;
  
  protected DefaultNodeUIInfoEnt() {
    //for sub-classes
  }
  
  private DefaultNodeUIInfoEnt(DefaultNodeUIInfoEntBuilder builder) {
    
    m_bounds = builder.m_bounds;
    m_symbolRelative = builder.m_symbolRelative;
    m_hasAbsoluteCoordinates = builder.m_hasAbsoluteCoordinates;
    m_dropLocation = builder.m_dropLocation;
    m_snapToGrid = builder.m_snapToGrid;
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
  public Boolean isHasAbsoluteCoordinates() {
        return m_hasAbsoluteCoordinates;
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
        private Boolean m_symbolRelative = null;
        private Boolean m_hasAbsoluteCoordinates = null;
        private Boolean m_dropLocation = null;
        private Boolean m_snapToGrid = null;

        @Override
        public DefaultNodeUIInfoEntBuilder setBounds(BoundsEnt bounds) {
             m_bounds = bounds;
             return this;
        }

        @Override
        public DefaultNodeUIInfoEntBuilder setSymbolRelative(Boolean symbolRelative) {
             m_symbolRelative = symbolRelative;
             return this;
        }

        @Override
        public DefaultNodeUIInfoEntBuilder setHasAbsoluteCoordinates(Boolean hasAbsoluteCoordinates) {
             m_hasAbsoluteCoordinates = hasAbsoluteCoordinates;
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

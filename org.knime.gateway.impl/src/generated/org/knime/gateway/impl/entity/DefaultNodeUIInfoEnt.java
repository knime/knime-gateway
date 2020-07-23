/*
 * ------------------------------------------------------------------------
 *  Copyright by KNIME AG, Zurich, Switzerland
 *  Website: http://www.knime.com; Email: contact@knime.com
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License, Version 3, as
 *  published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but
 *  WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, see <http://www.gnu.org/licenses>.
 *
 *  Additional permission under GNU GPL version 3 section 7:
 *
 *  KNIME interoperates with ECLIPSE solely via ECLIPSE's plug-in APIs.
 *  Hence, KNIME and ECLIPSE are both independent programs and are not
 *  derived from each other. Should, however, the interpretation of the
 *  GNU GPL Version 3 ("License") under any applicable laws result in
 *  KNIME and ECLIPSE being a combined program, KNIME AG herewith grants
 *  you the additional permission to use and propagate KNIME together with
 *  ECLIPSE with only the license terms in place for ECLIPSE applying to
 *  ECLIPSE and the GNU GPL Version 3 applying for KNIME, provided the
 *  license terms of ECLIPSE themselves allow for the respective use and
 *  propagation of ECLIPSE together with KNIME.
 *
 *  Additional permission relating to nodes for KNIME that extend the Node
 *  Extension (and in particular that are based on subclasses of NodeModel,
 *  NodeDialog, and NodeView) and that only interoperate with KNIME through
 *  standard APIs ("Nodes"):
 *  Nodes are deemed to be separate and independent programs and to not be
 *  covered works.  Notwithstanding anything to the contrary in the
 *  License, the License does not apply to Nodes, you are not required to
 *  license Nodes under the License, and you are granted a license to
 *  prepare and propagate Nodes, in each case even if such Nodes are
 *  propagated with or for interoperation with KNIME.  The owner of a Node
 *  may freely choose the license terms applicable to such Node, including
 *  when such Node is propagated with or for interoperation with KNIME.
 * ------------------------------------------------------------------------
 */
package org.knime.gateway.impl.entity;

import static org.knime.gateway.api.util.EntityUtil.immutable;

import java.util.Objects;

import org.knime.gateway.api.entity.BoundsEnt;

import org.knime.gateway.api.entity.NodeUIInfoEnt;

/**
 * Essentially the position of a node including some flags indicating a necessary correction of that given position.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
@javax.annotation.Generated(value = {"com.knime.gateway.codegen.GatewayCodegen", "src-gen/org.knime.gateway.impl-config.json"})
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

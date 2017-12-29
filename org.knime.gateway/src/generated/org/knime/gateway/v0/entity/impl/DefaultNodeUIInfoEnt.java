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
 *  KNIME and ECLIPSE being a combined program, KNIME GMBH herewith grants
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
package org.knime.gateway.v0.entity.impl;

import org.knime.gateway.v0.entity.BoundsEnt;

import org.knime.gateway.v0.entity.NodeUIInfoEnt;

/**
 * DefaultNodeUIInfoEnt
 *
 * @author Martin Horn, University of Konstanz
 */
// AUTO-GENERATED CODE; DO NOT MODIFY
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

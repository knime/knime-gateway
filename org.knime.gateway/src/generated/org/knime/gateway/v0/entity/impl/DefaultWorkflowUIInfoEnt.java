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

import java.math.BigDecimal;

import org.knime.gateway.v0.entity.WorkflowUIInfoEnt;

/**
 * Contains workflow UI-related properties such as grid settings, connection appearance etc.
 *
 * @author Martin Horn, University of Konstanz
 */
@javax.annotation.Generated(value = "org.knime.gateway.codegen.GatewayCodegen", date = "2018-01-10T17:43:16.092+01:00")
public class DefaultWorkflowUIInfoEnt  implements WorkflowUIInfoEnt {

  protected Integer m_gridX;
  protected Integer m_gridY;
  protected Boolean m_snapToGrid;
  protected Boolean m_showGrid;
  protected BigDecimal m_zoomLevel;
  protected Boolean m_hasCurvedConnection;
  protected Integer m_connectionLineWidth;
  
  protected DefaultWorkflowUIInfoEnt() {
    //for sub-classes
  }
  
  private DefaultWorkflowUIInfoEnt(DefaultWorkflowUIInfoEntBuilder builder) {
    
    m_gridX = builder.m_gridX;
    m_gridY = builder.m_gridY;
    m_snapToGrid = builder.m_snapToGrid;
    m_showGrid = builder.m_showGrid;
    m_zoomLevel = builder.m_zoomLevel;
    m_hasCurvedConnection = builder.m_hasCurvedConnection;
    m_connectionLineWidth = builder.m_connectionLineWidth;
  }


  @Override
  public Integer getGridX() {
        return m_gridX;
    }
    
  @Override
  public Integer getGridY() {
        return m_gridY;
    }
    
  @Override
  public Boolean isSnapToGrid() {
        return m_snapToGrid;
    }
    
  @Override
  public Boolean isShowGrid() {
        return m_showGrid;
    }
    
  @Override
  public BigDecimal getZoomLevel() {
        return m_zoomLevel;
    }
    
  @Override
  public Boolean isHasCurvedConnection() {
        return m_hasCurvedConnection;
    }
    
  @Override
  public Integer getConnectionLineWidth() {
        return m_connectionLineWidth;
    }
    
  
    public static class DefaultWorkflowUIInfoEntBuilder implements WorkflowUIInfoEntBuilder {
    
        public DefaultWorkflowUIInfoEntBuilder(){
            
        }
    
        private Integer m_gridX = null;
        private Integer m_gridY = null;
        private Boolean m_snapToGrid = null;
        private Boolean m_showGrid = null;
        private BigDecimal m_zoomLevel = null;
        private Boolean m_hasCurvedConnection = null;
        private Integer m_connectionLineWidth = null;

        @Override
        public DefaultWorkflowUIInfoEntBuilder setGridX(Integer gridX) {
             m_gridX = gridX;
             return this;
        }

        @Override
        public DefaultWorkflowUIInfoEntBuilder setGridY(Integer gridY) {
             m_gridY = gridY;
             return this;
        }

        @Override
        public DefaultWorkflowUIInfoEntBuilder setSnapToGrid(Boolean snapToGrid) {
             m_snapToGrid = snapToGrid;
             return this;
        }

        @Override
        public DefaultWorkflowUIInfoEntBuilder setShowGrid(Boolean showGrid) {
             m_showGrid = showGrid;
             return this;
        }

        @Override
        public DefaultWorkflowUIInfoEntBuilder setZoomLevel(BigDecimal zoomLevel) {
             m_zoomLevel = zoomLevel;
             return this;
        }

        @Override
        public DefaultWorkflowUIInfoEntBuilder setHasCurvedConnection(Boolean hasCurvedConnection) {
             m_hasCurvedConnection = hasCurvedConnection;
             return this;
        }

        @Override
        public DefaultWorkflowUIInfoEntBuilder setConnectionLineWidth(Integer connectionLineWidth) {
             m_connectionLineWidth = connectionLineWidth;
             return this;
        }

        
        @Override
        public DefaultWorkflowUIInfoEnt build() {
            return new DefaultWorkflowUIInfoEnt(this);
        }
    
    }

}

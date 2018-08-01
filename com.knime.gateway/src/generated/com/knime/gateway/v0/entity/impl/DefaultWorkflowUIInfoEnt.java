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

import java.math.BigDecimal;

import com.knime.gateway.v0.entity.WorkflowUIInfoEnt;

/**
 * Contains workflow UI-related properties such as grid settings, connection appearance etc.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
@javax.annotation.Generated(value = "org.knime.gateway.codegen.GatewayCodegen")
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
  
  @Override
  public String getTypeID() {
    return "WorkflowUIInfo";
  }
  
  private DefaultWorkflowUIInfoEnt(DefaultWorkflowUIInfoEntBuilder builder) {
    
    m_gridX = immutable(builder.m_gridX);
    m_gridY = immutable(builder.m_gridY);
    m_snapToGrid = immutable(builder.m_snapToGrid);
    m_showGrid = immutable(builder.m_showGrid);
    m_zoomLevel = immutable(builder.m_zoomLevel);
    m_hasCurvedConnection = immutable(builder.m_hasCurvedConnection);
    m_connectionLineWidth = immutable(builder.m_connectionLineWidth);
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
        DefaultWorkflowUIInfoEnt ent = (DefaultWorkflowUIInfoEnt)o;
        return Objects.equals(m_gridX, ent.m_gridX) && Objects.equals(m_gridY, ent.m_gridY) && Objects.equals(m_snapToGrid, ent.m_snapToGrid) && Objects.equals(m_showGrid, ent.m_showGrid) && Objects.equals(m_zoomLevel, ent.m_zoomLevel) && Objects.equals(m_hasCurvedConnection, ent.m_hasCurvedConnection) && Objects.equals(m_connectionLineWidth, ent.m_connectionLineWidth);
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
  public Boolean hasCurvedConnection() {
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

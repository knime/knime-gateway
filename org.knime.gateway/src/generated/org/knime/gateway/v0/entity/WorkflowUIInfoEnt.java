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
package org.knime.gateway.v0.entity;

import java.math.BigDecimal;

import org.knime.gateway.entity.GatewayEntityBuilder;


import org.knime.gateway.entity.GatewayEntity;

/**
 * Contains workflow UI-related properties such as grid settings, connection appearance etc.
 * 
 * @author Martin Horn, University of Konstanz
 */
// AUTO-GENERATED CODE; DO NOT MODIFY
public interface WorkflowUIInfoEnt extends GatewayEntity {


  /**
   * Grid size in X direction.
   * @return gridX 
   **/
  public Integer getGridX();

  /**
   * Grid size in Y direction.
   * @return gridY 
   **/
  public Integer getGridY();

  /**
   * Whether to snap to the grid.
   * @return snapToGrid 
   **/
  public Boolean isSnapToGrid();

  /**
   * Whether to show the grid lines.
   * @return showGrid 
   **/
  public Boolean isShowGrid();

  /**
   * Workflow zoom leve, i.e. its magnification.
   * @return zoomLevel 
   **/
  public BigDecimal getZoomLevel();

  /**
   * Whether connections are rendered as curves.
   * @return hasCurvedConnection 
   **/
  public Boolean isHasCurvedConnection();

  /**
   * Width of the line connecting two nodes.
   * @return connectionLineWidth 
   **/
  public Integer getConnectionLineWidth();


    /**
     * The builder for the entity.
     */
    public interface WorkflowUIInfoEntBuilder extends GatewayEntityBuilder<WorkflowUIInfoEnt> {

        /**
         * Grid size in X direction.
         * 
         * @param gridX the property value,  
         * @return this entity builder for chaining
         */
        WorkflowUIInfoEntBuilder setGridX(Integer gridX);
        
        /**
         * Grid size in Y direction.
         * 
         * @param gridY the property value,  
         * @return this entity builder for chaining
         */
        WorkflowUIInfoEntBuilder setGridY(Integer gridY);
        
        /**
         * Whether to snap to the grid.
         * 
         * @param snapToGrid the property value,  
         * @return this entity builder for chaining
         */
        WorkflowUIInfoEntBuilder setSnapToGrid(Boolean snapToGrid);
        
        /**
         * Whether to show the grid lines.
         * 
         * @param showGrid the property value,  
         * @return this entity builder for chaining
         */
        WorkflowUIInfoEntBuilder setShowGrid(Boolean showGrid);
        
        /**
         * Workflow zoom leve, i.e. its magnification.
         * 
         * @param zoomLevel the property value,  
         * @return this entity builder for chaining
         */
        WorkflowUIInfoEntBuilder setZoomLevel(BigDecimal zoomLevel);
        
        /**
         * Whether connections are rendered as curves.
         * 
         * @param hasCurvedConnection the property value,  
         * @return this entity builder for chaining
         */
        WorkflowUIInfoEntBuilder setHasCurvedConnection(Boolean hasCurvedConnection);
        
        /**
         * Width of the line connecting two nodes.
         * 
         * @param connectionLineWidth the property value,  
         * @return this entity builder for chaining
         */
        WorkflowUIInfoEntBuilder setConnectionLineWidth(Integer connectionLineWidth);
        
        
        /**
        * Creates the entity from the builder.
        * 
        * @return the entity
        * @throws IllegalArgumentException most likely in case when a required property hasn't been set
        */
        @Override
        WorkflowUIInfoEnt build();
    
    }

}

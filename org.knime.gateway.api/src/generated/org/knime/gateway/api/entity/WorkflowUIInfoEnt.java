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
package org.knime.gateway.api.entity;

import java.math.BigDecimal;

import org.knime.gateway.api.entity.GatewayEntityBuilder;


import org.knime.gateway.api.entity.GatewayEntity;

/**
 * Contains workflow UI-related properties such as grid settings, connection appearance etc.
 * 
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
@javax.annotation.Generated(value = {"com.knime.gateway.codegen.GatewayCodegen", "src-gen/org.knime.gateway.api-config.json"})
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
  public Boolean hasCurvedConnection();

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

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
 * Details and statistics on a node still in execution.
 * 
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
@javax.annotation.Generated(value = {"com.knime.gateway.codegen.GatewayCodegen", "src-gen/org.knime.gateway.api-config.json"})
public interface NodeExecutingStatisticsEnt extends GatewayEntity {


  /**
   * Get name
   * @return name 
   **/
  public String getName();

  /**
   * The node annotation if set.
   * @return annotation 
   **/
  public String getAnnotation();

  /**
   * Get nodeID
   * @return nodeID 
   **/
  public String getNodeID();

  /**
   * The time the execution took so far (ms).
   * @return executionDuration 
   **/
  public BigDecimal getExecutionDuration();

  /**
   * Get progress
   * @return progress 
   **/
  public BigDecimal getProgress();


    /**
     * The builder for the entity.
     */
    public interface NodeExecutingStatisticsEntBuilder extends GatewayEntityBuilder<NodeExecutingStatisticsEnt> {

        /**
   		 * Set name
         * 
         * @param name the property value,  
         * @return this entity builder for chaining
         */
        NodeExecutingStatisticsEntBuilder setName(String name);
        
        /**
         * The node annotation if set.
         * 
         * @param annotation the property value,  
         * @return this entity builder for chaining
         */
        NodeExecutingStatisticsEntBuilder setAnnotation(String annotation);
        
        /**
   		 * Set nodeID
         * 
         * @param nodeID the property value,  
         * @return this entity builder for chaining
         */
        NodeExecutingStatisticsEntBuilder setNodeID(String nodeID);
        
        /**
         * The time the execution took so far (ms).
         * 
         * @param executionDuration the property value,  
         * @return this entity builder for chaining
         */
        NodeExecutingStatisticsEntBuilder setExecutionDuration(BigDecimal executionDuration);
        
        /**
   		 * Set progress
         * 
         * @param progress the property value,  
         * @return this entity builder for chaining
         */
        NodeExecutingStatisticsEntBuilder setProgress(BigDecimal progress);
        
        
        /**
        * Creates the entity from the builder.
        * 
        * @return the entity
        * @throws IllegalArgumentException most likely in case when a required property hasn't been set
        */
        @Override
        NodeExecutingStatisticsEnt build();
    
    }

}

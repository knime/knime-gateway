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
package com.knime.gateway.entity;

import java.math.BigDecimal;

import com.knime.gateway.entity.GatewayEntityBuilder;


import com.knime.gateway.entity.GatewayEntity;

/**
 * Details and statistics on a node already executed.
 * 
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
@javax.annotation.Generated(value = {"org.knime.gateway.codegen.GatewayCodegen", "src-gen/com.knime.gateway-interfaces-config.json"})
public interface NodeExecutedStatisticsEnt extends GatewayEntity {


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
   * The time it took for the node to finish (ms).
   * @return executionDuration 
   **/
  public BigDecimal getExecutionDuration();

  /**
   * How often the node has been executed (&gt;1 if node is in a loop)
   * @return runs 
   **/
  public BigDecimal getRuns();


    /**
     * The builder for the entity.
     */
    public interface NodeExecutedStatisticsEntBuilder extends GatewayEntityBuilder<NodeExecutedStatisticsEnt> {

        /**
   		 * Set name
         * 
         * @param name the property value,  
         * @return this entity builder for chaining
         */
        NodeExecutedStatisticsEntBuilder setName(String name);
        
        /**
         * The node annotation if set.
         * 
         * @param annotation the property value,  
         * @return this entity builder for chaining
         */
        NodeExecutedStatisticsEntBuilder setAnnotation(String annotation);
        
        /**
   		 * Set nodeID
         * 
         * @param nodeID the property value,  
         * @return this entity builder for chaining
         */
        NodeExecutedStatisticsEntBuilder setNodeID(String nodeID);
        
        /**
         * The time it took for the node to finish (ms).
         * 
         * @param executionDuration the property value,  
         * @return this entity builder for chaining
         */
        NodeExecutedStatisticsEntBuilder setExecutionDuration(BigDecimal executionDuration);
        
        /**
         * How often the node has been executed (&gt;1 if node is in a loop)
         * 
         * @param runs the property value,  
         * @return this entity builder for chaining
         */
        NodeExecutedStatisticsEntBuilder setRuns(BigDecimal runs);
        
        
        /**
        * Creates the entity from the builder.
        * 
        * @return the entity
        * @throws IllegalArgumentException most likely in case when a required property hasn't been set
        */
        @Override
        NodeExecutedStatisticsEnt build();
    
    }

}

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

import com.knime.gateway.entity.NodeExecutedStatisticsEnt;
import com.knime.gateway.entity.NodeExecutingStatisticsEnt;
import java.math.BigDecimal;

import com.knime.gateway.entity.GatewayEntityBuilder;


import com.knime.gateway.entity.GatewayEntity;

/**
 * Statistics and progress on the workflow execution.
 * 
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
@javax.annotation.Generated(value = {"org.knime.gateway.codegen.GatewayCodegen", "src-gen/com.knime.gateway-interfaces-config.json"})
public interface ExecutionStatisticsEnt extends GatewayEntity {


  /**
   * Total time execution took or is still taking, in milliseconds.
   * @return totalExecutionDuration 
   **/
  public BigDecimal getTotalExecutionDuration();

  /**
   * Nodes executed since the last snapshot (or triggered execution). In the order of their end time. 
   * @return nodesExecuted 
   **/
  public java.util.List<NodeExecutedStatisticsEnt> getNodesExecuted();

  /**
   * Nodes currently executing. In the order of their start time. 
   * @return nodesExecuting 
   **/
  public java.util.List<NodeExecutingStatisticsEnt> getNodesExecuting();


    /**
     * The builder for the entity.
     */
    public interface ExecutionStatisticsEntBuilder extends GatewayEntityBuilder<ExecutionStatisticsEnt> {

        /**
         * Total time execution took or is still taking, in milliseconds.
         * 
         * @param totalExecutionDuration the property value,  
         * @return this entity builder for chaining
         */
        ExecutionStatisticsEntBuilder setTotalExecutionDuration(BigDecimal totalExecutionDuration);
        
        /**
         * Nodes executed since the last snapshot (or triggered execution). In the order of their end time. 
         * 
         * @param nodesExecuted the property value,  
         * @return this entity builder for chaining
         */
        ExecutionStatisticsEntBuilder setNodesExecuted(java.util.List<NodeExecutedStatisticsEnt> nodesExecuted);
        
        /**
         * Nodes currently executing. In the order of their start time. 
         * 
         * @param nodesExecuting the property value,  
         * @return this entity builder for chaining
         */
        ExecutionStatisticsEntBuilder setNodesExecuting(java.util.List<NodeExecutingStatisticsEnt> nodesExecuting);
        
        
        /**
        * Creates the entity from the builder.
        * 
        * @return the entity
        * @throws IllegalArgumentException most likely in case when a required property hasn't been set
        */
        @Override
        ExecutionStatisticsEnt build();
    
    }

}

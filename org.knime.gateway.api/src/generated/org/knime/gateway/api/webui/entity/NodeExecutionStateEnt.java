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
package org.knime.gateway.api.webui.entity;

import java.math.BigDecimal;

import org.knime.gateway.api.entity.GatewayEntityBuilder;


import org.knime.gateway.api.entity.GatewayEntity;

/**
 * Encapsulates properties around a node&#39;s execution state.
 * 
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
@javax.annotation.Generated(value = {"com.knime.gateway.codegen.GatewayCodegen", "src-gen/api/web-ui/configs/org.knime.gateway.api-config.json"})
public interface NodeExecutionStateEnt extends GatewayEntity {

  /**
   * Different execution states of a node. It is not given, if the node is inactive.
   */
  public enum StateEnum {
    IDLE("IDLE"),
    
    CONFIGURED("CONFIGURED"),
    
    EXECUTED("EXECUTED"),
    
    EXECUTING("EXECUTING"),
    
    QUEUED("QUEUED"),
    
    HALTED("HALTED");

    private String value;

    StateEnum(String value) {
      this.value = value;
    }

    @Override
    public String toString() {
      return String.valueOf(value);
    }

  }


  /**
   * Different execution states of a node. It is not given, if the node is inactive.
   * @return state 
   **/
  public StateEnum getState();

  /**
   * Get progress
   * @return progress 
   **/
  public BigDecimal getProgress();

  /**
   * Get progressMessage
   * @return progressMessage 
   **/
  public String getProgressMessage();

  /**
   * Get error
   * @return error 
   **/
  public String getError();

  /**
   * Get warning
   * @return warning 
   **/
  public String getWarning();


    /**
     * The builder for the entity.
     */
    public interface NodeExecutionStateEntBuilder extends GatewayEntityBuilder<NodeExecutionStateEnt> {

        /**
         * Different execution states of a node. It is not given, if the node is inactive.
         * 
         * @param state the property value,  
         * @return this entity builder for chaining
         */
        NodeExecutionStateEntBuilder setState(StateEnum state);
        
        /**
   		 * Set progress
         * 
         * @param progress the property value,  
         * @return this entity builder for chaining
         */
        NodeExecutionStateEntBuilder setProgress(BigDecimal progress);
        
        /**
   		 * Set progressMessage
         * 
         * @param progressMessage the property value,  
         * @return this entity builder for chaining
         */
        NodeExecutionStateEntBuilder setProgressMessage(String progressMessage);
        
        /**
   		 * Set error
         * 
         * @param error the property value,  
         * @return this entity builder for chaining
         */
        NodeExecutionStateEntBuilder setError(String error);
        
        /**
   		 * Set warning
         * 
         * @param warning the property value,  
         * @return this entity builder for chaining
         */
        NodeExecutionStateEntBuilder setWarning(String warning);
        
        
        /**
        * Creates the entity from the builder.
        * 
        * @return the entity
        * @throws IllegalArgumentException most likely in case when a required property hasn't been set
        */
        @Override
        NodeExecutionStateEnt build();
    
    }

}

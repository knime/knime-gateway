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
package com.knime.gateway.v0.entity;


import com.knime.gateway.entity.GatewayEntityBuilder;


import com.knime.gateway.entity.GatewayEntity;

/**
 * NodeStateEnt
 * 
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
@javax.annotation.Generated(value = "org.knime.gateway.codegen.GatewayCodegen")
public interface NodeStateEnt extends GatewayEntity {

  /**
   * Gets or Sets state
   */
  public enum StateEnum {
    IDLE("IDLE"),
    
    CONFIGURED("CONFIGURED"),
    
    UNCONFIGURED_MARKEDFOREXEC("UNCONFIGURED_MARKEDFOREXEC"),
    
    CONFIGURED_MARKEDFOREXEC("CONFIGURED_MARKEDFOREXEC"),
    
    EXECUTED_MARKEDFOREXEC("EXECUTED_MARKEDFOREXEC"),
    
    CONFIGURED_QUEUED("CONFIGURED_QUEUED"),
    
    EXECUTED_QUEUED("EXECUTED_QUEUED"),
    
    PREEXECUTE("PREEXECUTE"),
    
    EXECUTING("EXECUTING"),
    
    EXECUTINGREMOTELY("EXECUTINGREMOTELY"),
    
    POSTEXECUTE("POSTEXECUTE"),
    
    EXECUTED("EXECUTED");

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
   * Get state
   * @return state , never <code>null</code>
   **/
  public StateEnum getState();


    /**
     * The builder for the entity.
     */
    public interface NodeStateEntBuilder extends GatewayEntityBuilder<NodeStateEnt> {

        /**
         * 
         * @param state the property value, NOT <code>null</code>! 
         * @return this entity builder for chaining
         */
        NodeStateEntBuilder setState(StateEnum state);
        
        
        /**
        * Creates the entity from the builder.
        * 
        * @return the entity
        * @throws IllegalArgumentException most likely in case when a required property hasn't been set
        */
        @Override
        NodeStateEnt build();
    
    }

}

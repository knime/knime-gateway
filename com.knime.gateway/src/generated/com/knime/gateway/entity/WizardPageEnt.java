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

import com.knime.gateway.entity.NodeMessageEnt;

import com.knime.gateway.entity.GatewayEntityBuilder;


import com.knime.gateway.entity.GatewayEntity;

/**
 * Wizard page as returned, e.g., by the next-page and current-page endpoints. 
 * 
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
@javax.annotation.Generated(value = {"org.knime.gateway.codegen.GatewayCodegen", "src-gen/com.knime.gateway-interfaces-config.json"})
public interface WizardPageEnt extends GatewayEntity {

  /**
   * The wizard execution state. 
   */
  public enum WizardExecutionStateEnum {
    INTERACTION_REQUIRED("INTERACTION_REQUIRED"),
    
    EXECUTING("EXECUTING"),
    
    EXECUTION_FINISHED("EXECUTION_FINISHED"),
    
    EXECUTION_FAILED("EXECUTION_FAILED"),
    
    UNDEFINED("UNDEFINED");

    private String value;

    WizardExecutionStateEnum(String value) {
      this.value = value;
    }

    @Override
    public String toString() {
      return String.valueOf(value);
    }

  }


  /**
   * The actual page content as provided by a component. Page content is guaranteed to be not available if wizard execution state is &#39;executing&#39;, &#39;undefined&#39; or &#39;execution_failed&#39;. 
   * @return wizardPageContent 
   **/
  public Object getWizardPageContent();

  /**
   * The wizard execution state. 
   * @return wizardExecutionState 
   **/
  public WizardExecutionStateEnum getWizardExecutionState();

  /**
   * The node (name and id) to node message map. Only available if workflow execution finished or failed.
   * @return nodeMessages 
   **/
  public java.util.Map<String, NodeMessageEnt> getNodeMessages();

  /**
   * Whether there is a previous page available or not.
   * @return hasPreviousPage 
   **/
  public Boolean hasPreviousPage();


    /**
     * The builder for the entity.
     */
    public interface WizardPageEntBuilder extends GatewayEntityBuilder<WizardPageEnt> {

        /**
         * The actual page content as provided by a component. Page content is guaranteed to be not available if wizard execution state is &#39;executing&#39;, &#39;undefined&#39; or &#39;execution_failed&#39;. 
         * 
         * @param wizardPageContent the property value,  
         * @return this entity builder for chaining
         */
        WizardPageEntBuilder setWizardPageContent(Object wizardPageContent);
        
        /**
         * The wizard execution state. 
         * 
         * @param wizardExecutionState the property value,  
         * @return this entity builder for chaining
         */
        WizardPageEntBuilder setWizardExecutionState(WizardExecutionStateEnum wizardExecutionState);
        
        /**
         * The node (name and id) to node message map. Only available if workflow execution finished or failed.
         * 
         * @param nodeMessages the property value,  
         * @return this entity builder for chaining
         */
        WizardPageEntBuilder setNodeMessages(java.util.Map<String, NodeMessageEnt> nodeMessages);
        
        /**
         * Whether there is a previous page available or not.
         * 
         * @param hasPreviousPage the property value,  
         * @return this entity builder for chaining
         */
        WizardPageEntBuilder setHasPreviousPage(Boolean hasPreviousPage);
        
        
        /**
        * Creates the entity from the builder.
        * 
        * @return the entity
        * @throws IllegalArgumentException most likely in case when a required property hasn't been set
        */
        @Override
        WizardPageEnt build();
    
    }

}

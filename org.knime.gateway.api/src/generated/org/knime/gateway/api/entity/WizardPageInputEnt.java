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


import org.knime.gateway.api.entity.GatewayEntityBuilder;


import org.knime.gateway.api.entity.GatewayEntity;

/**
 * Input data required to execute one wizard page of a workflow.
 * 
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
@javax.annotation.Generated(value = {"com.knime.gateway.codegen.GatewayCodegen", "src-gen/org.knime.gateway.api-config.json"})
public interface WizardPageInputEnt extends GatewayEntity {


  /**
   * A map from node-ids to input parameters.
   * @return viewValues 
   **/
  public java.util.Map<String, String> getViewValues();


    /**
     * The builder for the entity.
     */
    public interface WizardPageInputEntBuilder extends GatewayEntityBuilder<WizardPageInputEnt> {

        /**
         * A map from node-ids to input parameters.
         * 
         * @param viewValues the property value,  
         * @return this entity builder for chaining
         */
        WizardPageInputEntBuilder setViewValues(java.util.Map<String, String> viewValues);
        
        
        /**
        * Creates the entity from the builder.
        * 
        * @return the entity
        * @throws IllegalArgumentException most likely in case when a required property hasn't been set
        */
        @Override
        WizardPageInputEnt build();
    
    }

}

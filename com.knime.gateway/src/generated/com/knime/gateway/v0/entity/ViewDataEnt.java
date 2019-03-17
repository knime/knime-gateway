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

import com.knime.gateway.v0.entity.JavaObjectEnt;

import com.knime.gateway.entity.GatewayEntityBuilder;


import com.knime.gateway.entity.GatewayEntity;

/**
 * The data for a node&#39;s view encompasing the view representation and value.
 * 
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
@javax.annotation.Generated(value = {"org.knime.gateway.codegen.GatewayCodegen", "src-gen/com.knime.gateway-interfaces-config.json"})
public interface ViewDataEnt extends GatewayEntity {


  /**
   * The object id used in the javascript implementation of the view.
   * @return javascriptObjectID 
   **/
  public String getJavascriptObjectID();

  /**
   * Get viewRepresentation
   * @return viewRepresentation 
   **/
  public JavaObjectEnt getViewRepresentation();

  /**
   * Get viewValue
   * @return viewValue 
   **/
  public JavaObjectEnt getViewValue();

  /**
   * Property set in the configuration dialog to the node to skip this node in the wizard execution.
   * @return hideInWizard 
   **/
  public Boolean isHideInWizard();


    /**
     * The builder for the entity.
     */
    public interface ViewDataEntBuilder extends GatewayEntityBuilder<ViewDataEnt> {

        /**
         * The object id used in the javascript implementation of the view.
         * 
         * @param javascriptObjectID the property value,  
         * @return this entity builder for chaining
         */
        ViewDataEntBuilder setJavascriptObjectID(String javascriptObjectID);
        
        /**
   		 * Set viewRepresentation
         * 
         * @param viewRepresentation the property value,  
         * @return this entity builder for chaining
         */
        ViewDataEntBuilder setViewRepresentation(JavaObjectEnt viewRepresentation);
        
        /**
   		 * Set viewValue
         * 
         * @param viewValue the property value,  
         * @return this entity builder for chaining
         */
        ViewDataEntBuilder setViewValue(JavaObjectEnt viewValue);
        
        /**
         * Property set in the configuration dialog to the node to skip this node in the wizard execution.
         * 
         * @param hideInWizard the property value,  
         * @return this entity builder for chaining
         */
        ViewDataEntBuilder setHideInWizard(Boolean hideInWizard);
        
        
        /**
        * Creates the entity from the builder.
        * 
        * @return the entity
        * @throws IllegalArgumentException most likely in case when a required property hasn't been set
        */
        @Override
        ViewDataEnt build();
    
    }

}

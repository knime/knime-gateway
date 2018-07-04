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
 * Settings of a node.
 * 
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
@javax.annotation.Generated(value = "org.knime.gateway.codegen.GatewayCodegen")
public interface NodeSettingsEnt extends GatewayEntity {


  /**
   * The actual settings as a json-string.
   * @return jsonContent 
   **/
  public String getJsonContent();


    /**
     * The builder for the entity.
     */
    public interface NodeSettingsEntBuilder extends GatewayEntityBuilder<NodeSettingsEnt> {

        /**
         * The actual settings as a json-string.
         * 
         * @param jsonContent the property value,  
         * @return this entity builder for chaining
         */
        NodeSettingsEntBuilder setJsonContent(String jsonContent);
        
        
        /**
        * Creates the entity from the builder.
        * 
        * @return the entity
        * @throws IllegalArgumentException most likely in case when a required property hasn't been set
        */
        @Override
        NodeSettingsEnt build();
    
    }

}

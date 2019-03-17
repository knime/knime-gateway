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
 * Object to identify a node-specific node implementation.
 * 
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
@javax.annotation.Generated(value = {"org.knime.gateway.codegen.GatewayCodegen", "src-gen/com.knime.gateway-interfaces-config.json"})
public interface NodeFactoryKeyEnt extends GatewayEntity {


  /**
   * The fully qualified java classname.
   * @return className , never <code>null</code>
   **/
  public String getClassName();

  /**
   * Additional settings in order to be able to re-create nodes. Only required in case of &#39;dynamic&#39; node factories.
   * @return settings 
   **/
  public String getSettings();


    /**
     * The builder for the entity.
     */
    public interface NodeFactoryKeyEntBuilder extends GatewayEntityBuilder<NodeFactoryKeyEnt> {

        /**
         * The fully qualified java classname.
         * 
         * @param className the property value, NOT <code>null</code>! 
         * @return this entity builder for chaining
         */
        NodeFactoryKeyEntBuilder setClassName(String className);
        
        /**
         * Additional settings in order to be able to re-create nodes. Only required in case of &#39;dynamic&#39; node factories.
         * 
         * @param settings the property value,  
         * @return this entity builder for chaining
         */
        NodeFactoryKeyEntBuilder setSettings(String settings);
        
        
        /**
        * Creates the entity from the builder.
        * 
        * @return the entity
        * @throws IllegalArgumentException most likely in case when a required property hasn't been set
        */
        @Override
        NodeFactoryKeyEnt build();
    
    }

}

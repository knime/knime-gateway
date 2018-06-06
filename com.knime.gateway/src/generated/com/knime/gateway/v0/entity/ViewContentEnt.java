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
 * A view content object.
 * 
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
@javax.annotation.Generated(value = "org.knime.gateway.codegen.GatewayCodegen")
public interface ViewContentEnt extends GatewayEntity {


  /**
   * The class name of the view content object.
   * @return classname 
   **/
  public String getClassname();

  /**
   * The actual content as a json string.
   * @return content 
   **/
  public String getContent();


    /**
     * The builder for the entity.
     */
    public interface ViewContentEntBuilder extends GatewayEntityBuilder<ViewContentEnt> {

        /**
         * The class name of the view content object.
         * 
         * @param classname the property value,  
         * @return this entity builder for chaining
         */
        ViewContentEntBuilder setClassname(String classname);
        
        /**
         * The actual content as a json string.
         * 
         * @param content the property value,  
         * @return this entity builder for chaining
         */
        ViewContentEntBuilder setContent(String content);
        
        
        /**
        * Creates the entity from the builder.
        * 
        * @return the entity
        * @throws IllegalArgumentException most likely in case when a required property hasn't been set
        */
        @Override
        ViewContentEnt build();
    
    }

}

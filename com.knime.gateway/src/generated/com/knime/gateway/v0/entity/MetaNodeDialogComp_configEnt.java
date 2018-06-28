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
 * Dialog component&#39;s config.
 * 
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
@javax.annotation.Generated(value = "org.knime.gateway.codegen.GatewayCodegen")
public interface MetaNodeDialogComp_configEnt extends GatewayEntity {


  /**
   * The java type of the dialog config.
   * @return classname 
   **/
  public String getClassname();

  /**
   * The actual config ocntent as json-string.
   * @return content 
   **/
  public String getContent();


    /**
     * The builder for the entity.
     */
    public interface MetaNodeDialogComp_configEntBuilder extends GatewayEntityBuilder<MetaNodeDialogComp_configEnt> {

        /**
         * The java type of the dialog config.
         * 
         * @param classname the property value,  
         * @return this entity builder for chaining
         */
        MetaNodeDialogComp_configEntBuilder setClassname(String classname);
        
        /**
         * The actual config ocntent as json-string.
         * 
         * @param content the property value,  
         * @return this entity builder for chaining
         */
        MetaNodeDialogComp_configEntBuilder setContent(String content);
        
        
        /**
        * Creates the entity from the builder.
        * 
        * @return the entity
        * @throws IllegalArgumentException most likely in case when a required property hasn't been set
        */
        @Override
        MetaNodeDialogComp_configEnt build();
    
    }

}

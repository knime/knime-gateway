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


import com.knime.gateway.entity.GatewayEntityBuilder;


import com.knime.gateway.entity.GatewayEntity;

/**
 * ViewTemplateEnt
 * 
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
@javax.annotation.Generated(value = {"org.knime.gateway.codegen.GatewayCodegen", "src-gen/com.knime.gateway-interfaces-config.json"})
public interface ViewTemplateEnt extends GatewayEntity {


  /**
   * TODO
   * @return javascriptLibraries 
   **/
  public java.util.List<String> getJavascriptLibraries();

  /**
   * TODO
   * @return stylesheets 
   **/
  public java.util.List<String> getStylesheets();

  /**
   * Get namespace
   * @return namespace 
   **/
  public String getNamespace();

  /**
   * Get initMethodName
   * @return initMethodName 
   **/
  public String getInitMethodName();

  /**
   * Get validateMethodName
   * @return validateMethodName 
   **/
  public String getValidateMethodName();

  /**
   * Get setValidationErrorMethodName
   * @return setValidationErrorMethodName 
   **/
  public String getSetValidationErrorMethodName();

  /**
   * Get getViewValueMethodName
   * @return getViewValueMethodName 
   **/
  public String getGetViewValueMethodName();


    /**
     * The builder for the entity.
     */
    public interface ViewTemplateEntBuilder extends GatewayEntityBuilder<ViewTemplateEnt> {

        /**
         * TODO
         * 
         * @param javascriptLibraries the property value,  
         * @return this entity builder for chaining
         */
        ViewTemplateEntBuilder setJavascriptLibraries(java.util.List<String> javascriptLibraries);
        
        /**
         * TODO
         * 
         * @param stylesheets the property value,  
         * @return this entity builder for chaining
         */
        ViewTemplateEntBuilder setStylesheets(java.util.List<String> stylesheets);
        
        /**
   		 * Set namespace
         * 
         * @param namespace the property value,  
         * @return this entity builder for chaining
         */
        ViewTemplateEntBuilder setNamespace(String namespace);
        
        /**
   		 * Set initMethodName
         * 
         * @param initMethodName the property value,  
         * @return this entity builder for chaining
         */
        ViewTemplateEntBuilder setInitMethodName(String initMethodName);
        
        /**
   		 * Set validateMethodName
         * 
         * @param validateMethodName the property value,  
         * @return this entity builder for chaining
         */
        ViewTemplateEntBuilder setValidateMethodName(String validateMethodName);
        
        /**
   		 * Set setValidationErrorMethodName
         * 
         * @param setValidationErrorMethodName the property value,  
         * @return this entity builder for chaining
         */
        ViewTemplateEntBuilder setSetValidationErrorMethodName(String setValidationErrorMethodName);
        
        /**
   		 * Set getViewValueMethodName
         * 
         * @param getViewValueMethodName the property value,  
         * @return this entity builder for chaining
         */
        ViewTemplateEntBuilder setGetViewValueMethodName(String getViewValueMethodName);
        
        
        /**
        * Creates the entity from the builder.
        * 
        * @return the entity
        * @throws IllegalArgumentException most likely in case when a required property hasn't been set
        */
        @Override
        ViewTemplateEnt build();
    
    }

}

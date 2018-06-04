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

import com.knime.gateway.v0.entity.WebView_viewRepresentationEnt;
import com.knime.gateway.v0.entity.WebView_viewValueEnt;

import com.knime.gateway.entity.GatewayEntityBuilder;


import com.knime.gateway.entity.GatewayEntity;

/**
 * Represents a web view, e.g. Javascript.
 * 
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
@javax.annotation.Generated(value = "org.knime.gateway.codegen.GatewayCodegen")
public interface WebViewEnt extends GatewayEntity {


  /**
   * The object id used in the javascript implementation of the view.
   * @return javascriptObjectID 
   **/
  public String getJavascriptObjectID();

  /**
   * Get viewRepresentation
   * @return viewRepresentation 
   **/
  public WebView_viewRepresentationEnt getViewRepresentation();

  /**
   * Get viewValue
   * @return viewValue 
   **/
  public WebView_viewValueEnt getViewValue();

  /**
   * The path to the generated HTML containing the view or null if not applicable.
   * @return viewHTMLPath 
   **/
  public String getViewHTMLPath();

  /**
   * Property set in the configuration dialog to the node to skip this node in the wizard execution.
   * @return hideInWizard 
   **/
  public Boolean isHideInWizard();


    /**
     * The builder for the entity.
     */
    public interface WebViewEntBuilder extends GatewayEntityBuilder<WebViewEnt> {

        /**
         * The object id used in the javascript implementation of the view.
         * 
         * @param javascriptObjectID the property value,  
         * @return this entity builder for chaining
         */
        WebViewEntBuilder setJavascriptObjectID(String javascriptObjectID);
        
        /**
         * 
         * @param viewRepresentation the property value,  
         * @return this entity builder for chaining
         */
        WebViewEntBuilder setViewRepresentation(WebView_viewRepresentationEnt viewRepresentation);
        
        /**
         * 
         * @param viewValue the property value,  
         * @return this entity builder for chaining
         */
        WebViewEntBuilder setViewValue(WebView_viewValueEnt viewValue);
        
        /**
         * The path to the generated HTML containing the view or null if not applicable.
         * 
         * @param viewHTMLPath the property value,  
         * @return this entity builder for chaining
         */
        WebViewEntBuilder setViewHTMLPath(String viewHTMLPath);
        
        /**
         * Property set in the configuration dialog to the node to skip this node in the wizard execution.
         * 
         * @param hideInWizard the property value,  
         * @return this entity builder for chaining
         */
        WebViewEntBuilder setHideInWizard(Boolean hideInWizard);
        
        
        /**
        * Creates the entity from the builder.
        * 
        * @return the entity
        * @throws IllegalArgumentException most likely in case when a required property hasn't been set
        */
        @Override
        WebViewEnt build();
    
    }

}

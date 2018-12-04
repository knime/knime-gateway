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

import com.knime.gateway.v0.entity.BoundsEnt;
import com.knime.gateway.v0.entity.StyleRangeEnt;

import com.knime.gateway.entity.GatewayEntityBuilder;


import com.knime.gateway.entity.GatewayEntity;

/**
 * A text annotation.
 * 
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
@javax.annotation.Generated(value = "org.knime.gateway.codegen.GatewayCodegen")
public interface AnnotationEnt extends GatewayEntity {


  /**
   * Discriminator for inheritance. Must be the base name of this type/schema.
   * @return type , never <code>null</code>
   **/
  public String getType();

  /**
   * Get text
   * @return text 
   **/
  public String getText();

  /**
   * Get backgroundColor
   * @return backgroundColor 
   **/
  public Integer getBackgroundColor();

  /**
   * Get bounds
   * @return bounds 
   **/
  public BoundsEnt getBounds();

  /**
   * Get textAlignment
   * @return textAlignment 
   **/
  public String getTextAlignment();

  /**
   * Get borderSize
   * @return borderSize 
   **/
  public Integer getBorderSize();

  /**
   * Get borderColor
   * @return borderColor 
   **/
  public Integer getBorderColor();

  /**
   * Get defaultFontSize
   * @return defaultFontSize 
   **/
  public Integer getDefaultFontSize();

  /**
   * Get version
   * @return version 
   **/
  public Integer getVersion();

  /**
   * Defines ranges of different styles within the annotation.
   * @return styleRanges 
   **/
  public java.util.List<StyleRangeEnt> getStyleRanges();


    /**
     * The builder for the entity.
     */
    public interface AnnotationEntBuilder extends GatewayEntityBuilder<AnnotationEnt> {

        /**
         * Discriminator for inheritance. Must be the base name of this type/schema.
         * 
         * @param type the property value, NOT <code>null</code>! 
         * @return this entity builder for chaining
         */
        AnnotationEntBuilder setType(String type);
        
        /**
   		 * Set text
         * 
         * @param text the property value,  
         * @return this entity builder for chaining
         */
        AnnotationEntBuilder setText(String text);
        
        /**
   		 * Set backgroundColor
         * 
         * @param backgroundColor the property value,  
         * @return this entity builder for chaining
         */
        AnnotationEntBuilder setBackgroundColor(Integer backgroundColor);
        
        /**
   		 * Set bounds
         * 
         * @param bounds the property value,  
         * @return this entity builder for chaining
         */
        AnnotationEntBuilder setBounds(BoundsEnt bounds);
        
        /**
   		 * Set textAlignment
         * 
         * @param textAlignment the property value,  
         * @return this entity builder for chaining
         */
        AnnotationEntBuilder setTextAlignment(String textAlignment);
        
        /**
   		 * Set borderSize
         * 
         * @param borderSize the property value,  
         * @return this entity builder for chaining
         */
        AnnotationEntBuilder setBorderSize(Integer borderSize);
        
        /**
   		 * Set borderColor
         * 
         * @param borderColor the property value,  
         * @return this entity builder for chaining
         */
        AnnotationEntBuilder setBorderColor(Integer borderColor);
        
        /**
   		 * Set defaultFontSize
         * 
         * @param defaultFontSize the property value,  
         * @return this entity builder for chaining
         */
        AnnotationEntBuilder setDefaultFontSize(Integer defaultFontSize);
        
        /**
   		 * Set version
         * 
         * @param version the property value,  
         * @return this entity builder for chaining
         */
        AnnotationEntBuilder setVersion(Integer version);
        
        /**
         * Defines ranges of different styles within the annotation.
         * 
         * @param styleRanges the property value,  
         * @return this entity builder for chaining
         */
        AnnotationEntBuilder setStyleRanges(java.util.List<StyleRangeEnt> styleRanges);
        
        
        /**
        * Creates the entity from the builder.
        * 
        * @return the entity
        * @throws IllegalArgumentException most likely in case when a required property hasn't been set
        */
        @Override
        AnnotationEnt build();
    
    }

}

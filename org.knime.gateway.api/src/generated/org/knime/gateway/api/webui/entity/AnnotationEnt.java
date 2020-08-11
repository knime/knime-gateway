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
package org.knime.gateway.api.webui.entity;

import org.knime.gateway.api.webui.entity.StyleRangeEnt;

import org.knime.gateway.api.entity.GatewayEntityBuilder;


import org.knime.gateway.api.entity.GatewayEntity;

/**
 * A text annotation.
 * 
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
@javax.annotation.Generated(value = {"com.knime.gateway.codegen.GatewayCodegen", "src-gen/api/web-ui/configs/org.knime.gateway.api-config.json"})
public interface AnnotationEnt extends GatewayEntity {

  /**
   * Gets or Sets textAlign
   */
  public enum TextAlignEnum {
    LEFT("left"),
    
    CENTER("center"),
    
    RIGHT("right");

    private String value;

    TextAlignEnum(String value) {
      this.value = value;
    }

    @Override
    public String toString() {
      return String.valueOf(value);
    }

  }


  /**
   * Get text
   * @return text 
   **/
  public String getText();

  /**
   * Get backgroundColor
   * @return backgroundColor 
   **/
  public String getBackgroundColor();

  /**
   * Get textAlign
   * @return textAlign 
   **/
  public TextAlignEnum getTextAlign();

  /**
   * Get borderWidth
   * @return borderWidth 
   **/
  public Integer getBorderWidth();

  /**
   * Get borderColor
   * @return borderColor 
   **/
  public String getBorderColor();

  /**
   * Defines ranges of different styles within the annotation.
   * @return styleRanges , never <code>null</code>
   **/
  public java.util.List<StyleRangeEnt> getStyleRanges();


    /**
     * The builder for the entity.
     */
    public interface AnnotationEntBuilder extends GatewayEntityBuilder<AnnotationEnt> {

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
        AnnotationEntBuilder setBackgroundColor(String backgroundColor);
        
        /**
   		 * Set textAlign
         * 
         * @param textAlign the property value,  
         * @return this entity builder for chaining
         */
        AnnotationEntBuilder setTextAlign(TextAlignEnum textAlign);
        
        /**
   		 * Set borderWidth
         * 
         * @param borderWidth the property value,  
         * @return this entity builder for chaining
         */
        AnnotationEntBuilder setBorderWidth(Integer borderWidth);
        
        /**
   		 * Set borderColor
         * 
         * @param borderColor the property value,  
         * @return this entity builder for chaining
         */
        AnnotationEntBuilder setBorderColor(String borderColor);
        
        /**
         * Defines ranges of different styles within the annotation.
         * 
         * @param styleRanges the property value, NOT <code>null</code>! 
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

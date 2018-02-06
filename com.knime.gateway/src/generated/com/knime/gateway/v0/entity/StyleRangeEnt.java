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
 * Defines the style of a range (e.g. within a workflow annotation).
 * 
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
@javax.annotation.Generated(value = "org.knime.gateway.codegen.GatewayCodegen")
public interface StyleRangeEnt extends GatewayEntity {

  /**
   * The font style, e.g. normal, bold or italic.
   */
  public enum FontStyleEnum {
    NORMAL("normal"),
    
    BOLD("bold"),
    
    ITALIC("italic");

    private String value;

    FontStyleEnum(String value) {
      this.value = value;
    }

    @Override
    public String toString() {
      return String.valueOf(value);
    }

  }


  /**
   * Style range start.
   * @return start 
   **/
  public Integer getStart();

  /**
   * Style range length.
   * @return length 
   **/
  public Integer getLength();

  /**
   * Style range font name.
   * @return fontName 
   **/
  public String getFontName();

  /**
   * The font style, e.g. normal, bold or italic.
   * @return fontStyle 
   **/
  public FontStyleEnum getFontStyle();

  /**
   * Style range font size.
   * @return fontSize 
   **/
  public Integer getFontSize();

  /**
   * Style range foreground color.
   * @return foregroundColor 
   **/
  public Integer getForegroundColor();


    /**
     * The builder for the entity.
     */
    public interface StyleRangeEntBuilder extends GatewayEntityBuilder<StyleRangeEnt> {

        /**
         * Style range start.
         * 
         * @param start the property value,  
         * @return this entity builder for chaining
         */
        StyleRangeEntBuilder setStart(Integer start);
        
        /**
         * Style range length.
         * 
         * @param length the property value,  
         * @return this entity builder for chaining
         */
        StyleRangeEntBuilder setLength(Integer length);
        
        /**
         * Style range font name.
         * 
         * @param fontName the property value,  
         * @return this entity builder for chaining
         */
        StyleRangeEntBuilder setFontName(String fontName);
        
        /**
         * The font style, e.g. normal, bold or italic.
         * 
         * @param fontStyle the property value,  
         * @return this entity builder for chaining
         */
        StyleRangeEntBuilder setFontStyle(FontStyleEnum fontStyle);
        
        /**
         * Style range font size.
         * 
         * @param fontSize the property value,  
         * @return this entity builder for chaining
         */
        StyleRangeEntBuilder setFontSize(Integer fontSize);
        
        /**
         * Style range foreground color.
         * 
         * @param foregroundColor the property value,  
         * @return this entity builder for chaining
         */
        StyleRangeEntBuilder setForegroundColor(Integer foregroundColor);
        
        
        /**
        * Creates the entity from the builder.
        * 
        * @return the entity
        * @throws IllegalArgumentException most likely in case when a required property hasn't been set
        */
        @Override
        StyleRangeEnt build();
    
    }

}

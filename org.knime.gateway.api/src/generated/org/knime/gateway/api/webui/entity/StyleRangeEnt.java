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


import org.knime.gateway.api.entity.GatewayEntityBuilder;


import org.knime.gateway.api.entity.GatewayEntity;

/**
 * Defines the style of a range (e.g. within a workflow annotation).
 * 
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
@javax.annotation.Generated(value = {"com.knime.gateway.codegen.GatewayCodegen", "src-gen/api/web-ui/configs/org.knime.gateway.api-config.json"})
public interface StyleRangeEnt extends GatewayEntity {


  /**
   * Style range start.
   * @return start , never <code>null</code>
   **/
  public Integer getStart();

  /**
   * Style range length.
   * @return length , never <code>null</code>
   **/
  public Integer getLength();

  /**
   * Get bold
   * @return bold 
   **/
  public Boolean isBold();

  /**
   * Get italic
   * @return italic 
   **/
  public Boolean isItalic();

  /**
   * Style range font size.
   * @return fontSize , never <code>null</code>
   **/
  public Integer getFontSize();

  /**
   * Style range foreground color.
   * @return color 
   **/
  public String getColor();


    /**
     * The builder for the entity.
     */
    public interface StyleRangeEntBuilder extends GatewayEntityBuilder<StyleRangeEnt> {

        /**
         * Style range start.
         * 
         * @param start the property value, NOT <code>null</code>! 
         * @return this entity builder for chaining
         */
        StyleRangeEntBuilder setStart(Integer start);
        
        /**
         * Style range length.
         * 
         * @param length the property value, NOT <code>null</code>! 
         * @return this entity builder for chaining
         */
        StyleRangeEntBuilder setLength(Integer length);
        
        /**
   		 * Set bold
         * 
         * @param bold the property value,  
         * @return this entity builder for chaining
         */
        StyleRangeEntBuilder setBold(Boolean bold);
        
        /**
   		 * Set italic
         * 
         * @param italic the property value,  
         * @return this entity builder for chaining
         */
        StyleRangeEntBuilder setItalic(Boolean italic);
        
        /**
         * Style range font size.
         * 
         * @param fontSize the property value, NOT <code>null</code>! 
         * @return this entity builder for chaining
         */
        StyleRangeEntBuilder setFontSize(Integer fontSize);
        
        /**
         * Style range foreground color.
         * 
         * @param color the property value,  
         * @return this entity builder for chaining
         */
        StyleRangeEntBuilder setColor(String color);
        
        
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

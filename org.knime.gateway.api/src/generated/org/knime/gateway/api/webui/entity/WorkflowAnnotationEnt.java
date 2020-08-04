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

import org.knime.gateway.api.webui.entity.AnnotationEnt;
import org.knime.gateway.api.webui.entity.BoundsEnt;

import org.knime.gateway.api.entity.GatewayEntityBuilder;



/**
 * A workflow annotation.
 * 
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
@javax.annotation.Generated(value = {"com.knime.gateway.codegen.GatewayCodegen", "src-gen/api/web-ui/configs/org.knime.gateway.api-config.json"})
public interface WorkflowAnnotationEnt extends AnnotationEnt {

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
   * Get textAlign
   * @return textAlign 
   **/
  public TextAlignEnum getTextAlign();

  /**
   * Get defaultFontSize
   * @return defaultFontSize 
   **/
  public Integer getDefaultFontSize();

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
   * Get backgroundColor
   * @return backgroundColor 
   **/
  public String getBackgroundColor();

  /**
   * Get bounds
   * @return bounds 
   **/
  public BoundsEnt getBounds();


    /**
     * The builder for the entity.
     */
    public interface WorkflowAnnotationEntBuilder extends GatewayEntityBuilder<WorkflowAnnotationEnt> {

        /**
   		 * Set text
         * 
         * @param text the property value, NOT <code>null</code>! 
         * @return this entity builder for chaining
         */
        WorkflowAnnotationEntBuilder setText(String text);
        
        /**
   		 * Set textAlign
         * 
         * @param textAlign the property value,  
         * @return this entity builder for chaining
         */
        WorkflowAnnotationEntBuilder setTextAlign(TextAlignEnum textAlign);
        
        /**
   		 * Set defaultFontSize
         * 
         * @param defaultFontSize the property value,  
         * @return this entity builder for chaining
         */
        WorkflowAnnotationEntBuilder setDefaultFontSize(Integer defaultFontSize);
        
        /**
   		 * Set borderWidth
         * 
         * @param borderWidth the property value,  
         * @return this entity builder for chaining
         */
        WorkflowAnnotationEntBuilder setBorderWidth(Integer borderWidth);
        
        /**
   		 * Set borderColor
         * 
         * @param borderColor the property value,  
         * @return this entity builder for chaining
         */
        WorkflowAnnotationEntBuilder setBorderColor(String borderColor);
        
        /**
   		 * Set backgroundColor
         * 
         * @param backgroundColor the property value,  
         * @return this entity builder for chaining
         */
        WorkflowAnnotationEntBuilder setBackgroundColor(String backgroundColor);
        
        /**
   		 * Set bounds
         * 
         * @param bounds the property value,  
         * @return this entity builder for chaining
         */
        WorkflowAnnotationEntBuilder setBounds(BoundsEnt bounds);
        
        
        /**
        * Creates the entity from the builder.
        * 
        * @return the entity
        * @throws IllegalArgumentException most likely in case when a required property hasn't been set
        */
        @Override
        WorkflowAnnotationEnt build();
    
    }

}

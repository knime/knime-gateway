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

import com.knime.gateway.v0.entity.AnnotationEnt;
import com.knime.gateway.v0.entity.BoundsEnt;
import com.knime.gateway.v0.entity.StyleRangeEnt;

import com.knime.gateway.entity.GatewayEntityBuilder;



/**
 * A workflow annotation.
 * 
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
@javax.annotation.Generated(value = {"org.knime.gateway.codegen.GatewayCodegen", "src-gen/com.knime.gateway-interfaces-config.json"})
public interface WorkflowAnnotationEnt extends AnnotationEnt {


  /**
   * Identifier for the workflow annotations.
   * @return annotationID , never <code>null</code>
   **/
  public String getAnnotationID();


    /**
     * The builder for the entity.
     */
    public interface WorkflowAnnotationEntBuilder extends GatewayEntityBuilder<WorkflowAnnotationEnt> {

        /**
         * Discriminator for inheritance. Must be the base name of this type/schema.
         * 
         * @param type the property value, NOT <code>null</code>! 
         * @return this entity builder for chaining
         */
        WorkflowAnnotationEntBuilder setType(String type);
        
        /**
   		 * Set text
         * 
         * @param text the property value,  
         * @return this entity builder for chaining
         */
        WorkflowAnnotationEntBuilder setText(String text);
        
        /**
   		 * Set backgroundColor
         * 
         * @param backgroundColor the property value,  
         * @return this entity builder for chaining
         */
        WorkflowAnnotationEntBuilder setBackgroundColor(Integer backgroundColor);
        
        /**
   		 * Set bounds
         * 
         * @param bounds the property value,  
         * @return this entity builder for chaining
         */
        WorkflowAnnotationEntBuilder setBounds(BoundsEnt bounds);
        
        /**
   		 * Set textAlignment
         * 
         * @param textAlignment the property value,  
         * @return this entity builder for chaining
         */
        WorkflowAnnotationEntBuilder setTextAlignment(String textAlignment);
        
        /**
   		 * Set borderSize
         * 
         * @param borderSize the property value,  
         * @return this entity builder for chaining
         */
        WorkflowAnnotationEntBuilder setBorderSize(Integer borderSize);
        
        /**
   		 * Set borderColor
         * 
         * @param borderColor the property value,  
         * @return this entity builder for chaining
         */
        WorkflowAnnotationEntBuilder setBorderColor(Integer borderColor);
        
        /**
   		 * Set defaultFontSize
         * 
         * @param defaultFontSize the property value,  
         * @return this entity builder for chaining
         */
        WorkflowAnnotationEntBuilder setDefaultFontSize(Integer defaultFontSize);
        
        /**
   		 * Set version
         * 
         * @param version the property value,  
         * @return this entity builder for chaining
         */
        WorkflowAnnotationEntBuilder setVersion(Integer version);
        
        /**
         * Defines ranges of different styles within the annotation.
         * 
         * @param styleRanges the property value,  
         * @return this entity builder for chaining
         */
        WorkflowAnnotationEntBuilder setStyleRanges(java.util.List<StyleRangeEnt> styleRanges);
        
        /**
         * Identifier for the workflow annotations.
         * 
         * @param annotationID the property value, NOT <code>null</code>! 
         * @return this entity builder for chaining
         */
        WorkflowAnnotationEntBuilder setAnnotationID(String annotationID);
        
        
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

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
 * The annotation to a node.
 * 
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
@javax.annotation.Generated(value = {"org.knime.gateway.codegen.GatewayCodegen", "src-gen/com.knime.gateway-interfaces-config.json"})
public interface NodeAnnotationEnt extends AnnotationEnt {


  /**
   * Default node annotation.
   * @return _default 
   **/
  public Boolean isDefault();


    /**
     * The builder for the entity.
     */
    public interface NodeAnnotationEntBuilder extends GatewayEntityBuilder<NodeAnnotationEnt> {

        /**
         * Discriminator for inheritance. Must be the base name of this type/schema.
         * 
         * @param type the property value, NOT <code>null</code>! 
         * @return this entity builder for chaining
         */
        NodeAnnotationEntBuilder setType(String type);
        
        /**
   		 * Set text
         * 
         * @param text the property value,  
         * @return this entity builder for chaining
         */
        NodeAnnotationEntBuilder setText(String text);
        
        /**
   		 * Set backgroundColor
         * 
         * @param backgroundColor the property value,  
         * @return this entity builder for chaining
         */
        NodeAnnotationEntBuilder setBackgroundColor(Integer backgroundColor);
        
        /**
   		 * Set bounds
         * 
         * @param bounds the property value,  
         * @return this entity builder for chaining
         */
        NodeAnnotationEntBuilder setBounds(BoundsEnt bounds);
        
        /**
   		 * Set textAlignment
         * 
         * @param textAlignment the property value,  
         * @return this entity builder for chaining
         */
        NodeAnnotationEntBuilder setTextAlignment(String textAlignment);
        
        /**
   		 * Set borderSize
         * 
         * @param borderSize the property value,  
         * @return this entity builder for chaining
         */
        NodeAnnotationEntBuilder setBorderSize(Integer borderSize);
        
        /**
   		 * Set borderColor
         * 
         * @param borderColor the property value,  
         * @return this entity builder for chaining
         */
        NodeAnnotationEntBuilder setBorderColor(Integer borderColor);
        
        /**
   		 * Set defaultFontSize
         * 
         * @param defaultFontSize the property value,  
         * @return this entity builder for chaining
         */
        NodeAnnotationEntBuilder setDefaultFontSize(Integer defaultFontSize);
        
        /**
   		 * Set version
         * 
         * @param version the property value,  
         * @return this entity builder for chaining
         */
        NodeAnnotationEntBuilder setVersion(Integer version);
        
        /**
         * Defines ranges of different styles within the annotation.
         * 
         * @param styleRanges the property value,  
         * @return this entity builder for chaining
         */
        NodeAnnotationEntBuilder setStyleRanges(java.util.List<StyleRangeEnt> styleRanges);
        
        /**
         * Default node annotation.
         * 
         * @param _default the property value,  
         * @return this entity builder for chaining
         */
        NodeAnnotationEntBuilder setDefault(Boolean _default);
        
        
        /**
        * Creates the entity from the builder.
        * 
        * @return the entity
        * @throws IllegalArgumentException most likely in case when a required property hasn't been set
        */
        @Override
        NodeAnnotationEnt build();
    
    }

}

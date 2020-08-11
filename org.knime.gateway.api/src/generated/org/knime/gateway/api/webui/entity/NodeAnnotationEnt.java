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
import org.knime.gateway.api.webui.entity.StyleRangeEnt;

import org.knime.gateway.api.entity.GatewayEntityBuilder;



/**
 * The annotation to a node.
 * 
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
@javax.annotation.Generated(value = {"com.knime.gateway.codegen.GatewayCodegen", "src-gen/api/web-ui/configs/org.knime.gateway.api-config.json"})
public interface NodeAnnotationEnt extends AnnotationEnt {



    /**
     * The builder for the entity.
     */
    public interface NodeAnnotationEntBuilder extends GatewayEntityBuilder<NodeAnnotationEnt> {

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
        NodeAnnotationEntBuilder setBackgroundColor(String backgroundColor);
        
        /**
   		 * Set textAlign
         * 
         * @param textAlign the property value,  
         * @return this entity builder for chaining
         */
        NodeAnnotationEntBuilder setTextAlign(TextAlignEnum textAlign);
        
        /**
   		 * Set borderWidth
         * 
         * @param borderWidth the property value,  
         * @return this entity builder for chaining
         */
        NodeAnnotationEntBuilder setBorderWidth(Integer borderWidth);
        
        /**
   		 * Set borderColor
         * 
         * @param borderColor the property value,  
         * @return this entity builder for chaining
         */
        NodeAnnotationEntBuilder setBorderColor(String borderColor);
        
        /**
         * Defines ranges of different styles within the annotation.
         * 
         * @param styleRanges the property value, NOT <code>null</code>! 
         * @return this entity builder for chaining
         */
        NodeAnnotationEntBuilder setStyleRanges(java.util.List<StyleRangeEnt> styleRanges);
        
        
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

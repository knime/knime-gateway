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
 * A text annotation.
 * 
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
@javax.annotation.Generated(value = {"com.knime.gateway.codegen.GatewayCodegen", "src-gen/api/web-ui/configs/org.knime.gateway.api-config.json"})
public interface AnnotationEnt extends GatewayEntity {


  /**
   * Discriminator for inheritance. Must be the base name of this type/schema.
   * @return objectType , never <code>null</code>
   **/
  public String getObjectType();

  /**
   * Get text
   * @return text 
   **/
  public String getText();


    /**
     * The builder for the entity.
     */
    public interface AnnotationEntBuilder extends GatewayEntityBuilder<AnnotationEnt> {

        /**
         * Discriminator for inheritance. Must be the base name of this type/schema.
         * 
         * @param objectType the property value, NOT <code>null</code>! 
         * @return this entity builder for chaining
         */
        AnnotationEntBuilder setObjectType(String objectType);
        
        /**
   		 * Set text
         * 
         * @param text the property value,  
         * @return this entity builder for chaining
         */
        AnnotationEntBuilder setText(String text);
        
        
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

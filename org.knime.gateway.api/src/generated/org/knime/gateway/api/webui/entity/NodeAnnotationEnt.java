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

import org.knime.gateway.api.entity.GatewayEntityBuilder;



/**
 * The annotation to a node.
 * 
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
@javax.annotation.Generated(value = {"com.knime.gateway.codegen.GatewayCodegen", "src-gen/api/web-ui/configs/org.knime.gateway.api-config.json"})
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
         * @param objectType the property value, NOT <code>null</code>! 
         * @return this entity builder for chaining
         */
        NodeAnnotationEntBuilder setObjectType(String objectType);
        
        /**
   		 * Set text
         * 
         * @param text the property value,  
         * @return this entity builder for chaining
         */
        NodeAnnotationEntBuilder setText(String text);
        
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

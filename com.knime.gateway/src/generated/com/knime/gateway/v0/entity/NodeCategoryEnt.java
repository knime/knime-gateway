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

import com.knime.gateway.v0.entity.NodeCategoryEnt;
import com.knime.gateway.v0.entity.NodeFactoryKeyEnt;

import com.knime.gateway.entity.GatewayEntityBuilder;


import com.knime.gateway.entity.GatewayEntity;

/**
 * a category in the node repository
 * 
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
@javax.annotation.Generated(value = "org.knime.gateway.codegen.GatewayCodegen")
public interface NodeCategoryEnt extends GatewayEntity {


  /**
   * Get name
   * @return name 
   **/
  public String getName();

  /**
   * Get categoryChildren
   * @return categoryChildren 
   **/
  public java.util.List<NodeCategoryEnt> getCategoryChildren();

  /**
   * Get nodeFactoryChildren
   * @return nodeFactoryChildren 
   **/
  public java.util.List<NodeFactoryKeyEnt> getNodeFactoryChildren();


    /**
     * The builder for the entity.
     */
    public interface NodeCategoryEntBuilder extends GatewayEntityBuilder<NodeCategoryEnt> {

        /**
         * 
         * @param name the property value,  
         * @return this entity builder for chaining
         */
        NodeCategoryEntBuilder setName(String name);
        
        /**
         * 
         * @param categoryChildren the property value,  
         * @return this entity builder for chaining
         */
        NodeCategoryEntBuilder setCategoryChildren(java.util.List<NodeCategoryEnt> categoryChildren);
        
        /**
         * 
         * @param nodeFactoryChildren the property value,  
         * @return this entity builder for chaining
         */
        NodeCategoryEntBuilder setNodeFactoryChildren(java.util.List<NodeFactoryKeyEnt> nodeFactoryChildren);
        
        
        /**
        * Creates the entity from the builder.
        * 
        * @return the entity
        * @throws IllegalArgumentException most likely in case when a required property hasn't been set
        */
        @Override
        NodeCategoryEnt build();
    
    }

}

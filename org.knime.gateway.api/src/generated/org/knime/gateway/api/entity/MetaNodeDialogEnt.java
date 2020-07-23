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
package org.knime.gateway.api.entity;

import org.knime.gateway.api.entity.MetaNodeDialogCompEnt;

import org.knime.gateway.api.entity.GatewayEntityBuilder;


import org.knime.gateway.api.entity.GatewayEntity;

/**
 * A representation of a metanode dialog.
 * 
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
@javax.annotation.Generated(value = {"com.knime.gateway.codegen.GatewayCodegen", "src-gen/org.knime.gateway.api-config.json"})
public interface MetaNodeDialogEnt extends GatewayEntity {


  /**
   * List of contributing dialog components.
   * @return components 
   **/
  public java.util.List<MetaNodeDialogCompEnt> getComponents();


    /**
     * The builder for the entity.
     */
    public interface MetaNodeDialogEntBuilder extends GatewayEntityBuilder<MetaNodeDialogEnt> {

        /**
         * List of contributing dialog components.
         * 
         * @param components the property value,  
         * @return this entity builder for chaining
         */
        MetaNodeDialogEntBuilder setComponents(java.util.List<MetaNodeDialogCompEnt> components);
        
        
        /**
        * Creates the entity from the builder.
        * 
        * @return the entity
        * @throws IllegalArgumentException most likely in case when a required property hasn't been set
        */
        @Override
        MetaNodeDialogEnt build();
    
    }

}

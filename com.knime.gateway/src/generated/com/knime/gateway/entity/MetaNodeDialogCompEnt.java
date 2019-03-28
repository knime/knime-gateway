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
package com.knime.gateway.entity;

import com.knime.gateway.entity.JavaObjectEnt;

import com.knime.gateway.entity.GatewayEntityBuilder;


import com.knime.gateway.entity.GatewayEntity;

/**
 * A component that is part of a metanode dialog.
 * 
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
@javax.annotation.Generated(value = {"org.knime.gateway.codegen.GatewayCodegen", "src-gen/com.knime.gateway-interfaces-config.json"})
public interface MetaNodeDialogCompEnt extends GatewayEntity {


  /**
   * Parameter name for external parametrization.
   * @return paramName 
   **/
  public String getParamName();

  /**
   * The id of the node contributing to this dialog.
   * @return nodeID 
   **/
  public String getNodeID();

  /**
   * Whether the component should be hidden in the subnode&#39;s dialog.
   * @return isHideInDialog 
   **/
  public Boolean isIsHideInDialog();

  /**
   * Get representation
   * @return representation 
   **/
  public JavaObjectEnt getRepresentation();


    /**
     * The builder for the entity.
     */
    public interface MetaNodeDialogCompEntBuilder extends GatewayEntityBuilder<MetaNodeDialogCompEnt> {

        /**
         * Parameter name for external parametrization.
         * 
         * @param paramName the property value,  
         * @return this entity builder for chaining
         */
        MetaNodeDialogCompEntBuilder setParamName(String paramName);
        
        /**
         * The id of the node contributing to this dialog.
         * 
         * @param nodeID the property value,  
         * @return this entity builder for chaining
         */
        MetaNodeDialogCompEntBuilder setNodeID(String nodeID);
        
        /**
         * Whether the component should be hidden in the subnode&#39;s dialog.
         * 
         * @param isHideInDialog the property value,  
         * @return this entity builder for chaining
         */
        MetaNodeDialogCompEntBuilder setIsHideInDialog(Boolean isHideInDialog);
        
        /**
   		 * Set representation
         * 
         * @param representation the property value,  
         * @return this entity builder for chaining
         */
        MetaNodeDialogCompEntBuilder setRepresentation(JavaObjectEnt representation);
        
        
        /**
        * Creates the entity from the builder.
        * 
        * @return the entity
        * @throws IllegalArgumentException most likely in case when a required property hasn't been set
        */
        @Override
        MetaNodeDialogCompEnt build();
    
    }

}

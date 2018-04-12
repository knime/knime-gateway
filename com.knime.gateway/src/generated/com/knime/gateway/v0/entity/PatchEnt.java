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

import com.knime.gateway.v0.entity.PatchOpEnt;

import com.knime.gateway.entity.GatewayEntityBuilder;


import com.knime.gateway.entity.GatewayEntity;

/**
 * A list of patch operations.
 * 
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
@javax.annotation.Generated(value = "org.knime.gateway.codegen.GatewayCodegen")
public interface PatchEnt extends GatewayEntity {


  /**
   * The patch operations that make up this patch.
   * @return ops 
   **/
  public java.util.List<PatchOpEnt> getOps();

  /**
   * A unique identifier for the version of the object this patch is applied to. Should only be missing if patch is empty!
   * @return snapshotID 
   **/
  public java.util.UUID getSnapshotID();

  /**
   * ID of the entity this patch can exclusively be applied to. Should only be missing, if patch is empty!
   * @return targetTypeID 
   **/
  public String getTargetTypeID();


    /**
     * The builder for the entity.
     */
    public interface PatchEntBuilder extends GatewayEntityBuilder<PatchEnt> {

        /**
         * The patch operations that make up this patch.
         * 
         * @param ops the property value,  
         * @return this entity builder for chaining
         */
        PatchEntBuilder setOps(java.util.List<PatchOpEnt> ops);
        
        /**
         * A unique identifier for the version of the object this patch is applied to. Should only be missing if patch is empty!
         * 
         * @param snapshotID the property value,  
         * @return this entity builder for chaining
         */
        PatchEntBuilder setSnapshotID(java.util.UUID snapshotID);
        
        /**
         * ID of the entity this patch can exclusively be applied to. Should only be missing, if patch is empty!
         * 
         * @param targetTypeID the property value,  
         * @return this entity builder for chaining
         */
        PatchEntBuilder setTargetTypeID(String targetTypeID);
        
        
        /**
        * Creates the entity from the builder.
        * 
        * @return the entity
        * @throws IllegalArgumentException most likely in case when a required property hasn't been set
        */
        @Override
        PatchEnt build();
    
    }

}

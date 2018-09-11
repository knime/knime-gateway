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


import com.knime.gateway.entity.GatewayEntityBuilder;


import com.knime.gateway.entity.GatewayEntity;

/**
 * Execution environment instance
 * 
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
@javax.annotation.Generated(value = "org.knime.gateway.codegen.GatewayCodegen")
public interface ExecEnvEnt extends GatewayEntity {


  /**
   * Get execEnvID
   * @return execEnvID 
   **/
  public String getExecEnvID();

  /**
   * Get instanceID
   * @return instanceID 
   **/
  public String getInstanceID();

  /**
   * Get typeName
   * @return typeName 
   **/
  public String getTypeName();

  /**
   * Get allowedNodeTypes
   * @return allowedNodeTypes 
   **/
  public java.util.List<String> getAllowedNodeTypes();


    /**
     * The builder for the entity.
     */
    public interface ExecEnvEntBuilder extends GatewayEntityBuilder<ExecEnvEnt> {

        /**
         * 
         * @param execEnvID the property value,  
         * @return this entity builder for chaining
         */
        ExecEnvEntBuilder setExecEnvID(String execEnvID);
        
        /**
         * 
         * @param instanceID the property value,  
         * @return this entity builder for chaining
         */
        ExecEnvEntBuilder setInstanceID(String instanceID);
        
        /**
         * 
         * @param typeName the property value,  
         * @return this entity builder for chaining
         */
        ExecEnvEntBuilder setTypeName(String typeName);
        
        /**
         * 
         * @param allowedNodeTypes the property value,  
         * @return this entity builder for chaining
         */
        ExecEnvEntBuilder setAllowedNodeTypes(java.util.List<String> allowedNodeTypes);
        
        
        /**
        * Creates the entity from the builder.
        * 
        * @return the entity
        * @throws IllegalArgumentException most likely in case when a required property hasn't been set
        */
        @Override
        ExecEnvEnt build();
    
    }

}

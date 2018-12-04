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

import com.knime.gateway.v0.entity.WorkflowEnt;

import com.knime.gateway.entity.GatewayEntityBuilder;


import com.knime.gateway.entity.GatewayEntity;

/**
 * A workflow with an additional snapshot id.
 * 
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
@javax.annotation.Generated(value = "org.knime.gateway.codegen.GatewayCodegen")
public interface WorkflowSnapshotEnt extends GatewayEntity {


  /**
   * Get workflow
   * @return workflow , never <code>null</code>
   **/
  public WorkflowEnt getWorkflow();

  /**
   * A unique identifier for the snapshot.
   * @return snapshotID , never <code>null</code>
   **/
  public java.util.UUID getSnapshotID();


    /**
     * The builder for the entity.
     */
    public interface WorkflowSnapshotEntBuilder extends GatewayEntityBuilder<WorkflowSnapshotEnt> {

        /**
   		 * Set workflow
         * 
         * @param workflow the property value, NOT <code>null</code>! 
         * @return this entity builder for chaining
         */
        WorkflowSnapshotEntBuilder setWorkflow(WorkflowEnt workflow);
        
        /**
         * A unique identifier for the snapshot.
         * 
         * @param snapshotID the property value, NOT <code>null</code>! 
         * @return this entity builder for chaining
         */
        WorkflowSnapshotEntBuilder setSnapshotID(java.util.UUID snapshotID);
        
        
        /**
        * Creates the entity from the builder.
        * 
        * @return the entity
        * @throws IllegalArgumentException most likely in case when a required property hasn't been set
        */
        @Override
        WorkflowSnapshotEnt build();
    
    }

}

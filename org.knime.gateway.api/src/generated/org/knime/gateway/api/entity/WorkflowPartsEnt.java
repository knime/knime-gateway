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


import org.knime.gateway.api.entity.GatewayEntityBuilder;


import org.knime.gateway.api.entity.GatewayEntity;

/**
 * Represents a selection of parts of a workflow (i.e collections of nodes, connection, annotations etc.), e.g. to be copied or deleted.
 * 
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
@javax.annotation.Generated(value = {"com.knime.gateway.codegen.GatewayCodegen", "src-gen/org.knime.gateway.api-config.json"})
public interface WorkflowPartsEnt extends GatewayEntity {


  /**
   * The ids of the nodes referenced.
   * @return nodeIDs 
   **/
  public java.util.List<org.knime.gateway.api.entity.NodeIDEnt> getNodeIDs();

  /**
   * The ids of the connections referenced. The id has the following format: &lt;dest-node-id&gt;_&lt;dest-port-idx&gt;
   * @return connectionIDs 
   **/
  public java.util.List<org.knime.gateway.api.entity.ConnectionIDEnt> getConnectionIDs();

  /**
   * The ids of the workflow annotations referenced.
   * @return annotationIDs 
   **/
  public java.util.List<org.knime.gateway.api.entity.AnnotationIDEnt> getAnnotationIDs();


    /**
     * The builder for the entity.
     */
    public interface WorkflowPartsEntBuilder extends GatewayEntityBuilder<WorkflowPartsEnt> {

        /**
         * The ids of the nodes referenced.
         * 
         * @param nodeIDs the property value,  
         * @return this entity builder for chaining
         */
        WorkflowPartsEntBuilder setNodeIDs(java.util.List<org.knime.gateway.api.entity.NodeIDEnt> nodeIDs);
        
        /**
         * The ids of the connections referenced. The id has the following format: &lt;dest-node-id&gt;_&lt;dest-port-idx&gt;
         * 
         * @param connectionIDs the property value,  
         * @return this entity builder for chaining
         */
        WorkflowPartsEntBuilder setConnectionIDs(java.util.List<org.knime.gateway.api.entity.ConnectionIDEnt> connectionIDs);
        
        /**
         * The ids of the workflow annotations referenced.
         * 
         * @param annotationIDs the property value,  
         * @return this entity builder for chaining
         */
        WorkflowPartsEntBuilder setAnnotationIDs(java.util.List<org.knime.gateway.api.entity.AnnotationIDEnt> annotationIDs);
        
        
        /**
        * Creates the entity from the builder.
        * 
        * @return the entity
        * @throws IllegalArgumentException most likely in case when a required property hasn't been set
        */
        @Override
        WorkflowPartsEnt build();
    
    }

}
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

import org.knime.gateway.api.webui.entity.ConnectionEnt;
import org.knime.gateway.api.webui.entity.NodeEnt;

import org.knime.gateway.api.entity.GatewayEntityBuilder;


import org.knime.gateway.api.entity.GatewayEntity;

/**
 * The structure of a workflow.
 * 
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
@javax.annotation.Generated(value = {"com.knime.gateway.codegen.GatewayCodegen", "src-gen/api/web-ui/configs/org.knime.gateway.api-config.json"})
public interface WorkflowEnt extends GatewayEntity {


  /**
   * Get name
   * @return name 
   **/
  public String getName();

  /**
   * The node map.
   * @return nodes 
   **/
  public java.util.Map<String, NodeEnt> getNodes();

  /**
   * The list of connections.
   * @return connections 
   **/
  public java.util.Map<String, ConnectionEnt> getConnections();


    /**
     * The builder for the entity.
     */
    public interface WorkflowEntBuilder extends GatewayEntityBuilder<WorkflowEnt> {

        /**
   		 * Set name
         * 
         * @param name the property value,  
         * @return this entity builder for chaining
         */
        WorkflowEntBuilder setName(String name);
        
        /**
         * The node map.
         * 
         * @param nodes the property value,  
         * @return this entity builder for chaining
         */
        WorkflowEntBuilder setNodes(java.util.Map<String, NodeEnt> nodes);
        
        /**
         * The list of connections.
         * 
         * @param connections the property value,  
         * @return this entity builder for chaining
         */
        WorkflowEntBuilder setConnections(java.util.Map<String, ConnectionEnt> connections);
        
        
        /**
        * Creates the entity from the builder.
        * 
        * @return the entity
        * @throws IllegalArgumentException most likely in case when a required property hasn't been set
        */
        @Override
        WorkflowEnt build();
    
    }

}

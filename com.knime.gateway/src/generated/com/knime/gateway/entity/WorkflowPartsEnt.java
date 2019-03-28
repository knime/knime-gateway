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


import com.knime.gateway.entity.GatewayEntityBuilder;


import com.knime.gateway.entity.GatewayEntity;

/**
 * Represents a selection of parts of a workflow (i.e collections of nodes, connection, annotations etc.), e.g. to be copied or deleted.
 * 
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
@javax.annotation.Generated(value = {"org.knime.gateway.codegen.GatewayCodegen", "src-gen/com.knime.gateway-interfaces-config.json"})
public interface WorkflowPartsEnt extends GatewayEntity {


  /**
   * The parent node id of parts or &#39;root&#39; if it&#39;s the root node/workflow. The node-id format: For nested nodes the node ids are concatenated with an &#39;:&#39;, e.g. 3:6:4. Nodes within wrapped metanodes required an additional trailing &#39;0&#39;, e.g. 3:6:0:4 (if 3:6 is a wrapped metanode).
   * @return parentNodeID , never <code>null</code>
   **/
  public String getParentNodeID();

  /**
   * The ids of the nodes referenced.
   * @return nodeIDs 
   **/
  public java.util.List<String> getNodeIDs();

  /**
   * The ids of the connections referenced. The id has the following format: &lt;dest-node-id&gt;_&lt;dest-port-idx&gt;
   * @return connectionIDs 
   **/
  public java.util.List<String> getConnectionIDs();

  /**
   * The ids of the workflow annotations referenced.
   * @return annotationIDs 
   **/
  public java.util.List<String> getAnnotationIDs();


    /**
     * The builder for the entity.
     */
    public interface WorkflowPartsEntBuilder extends GatewayEntityBuilder<WorkflowPartsEnt> {

        /**
         * The parent node id of parts or &#39;root&#39; if it&#39;s the root node/workflow. The node-id format: For nested nodes the node ids are concatenated with an &#39;:&#39;, e.g. 3:6:4. Nodes within wrapped metanodes required an additional trailing &#39;0&#39;, e.g. 3:6:0:4 (if 3:6 is a wrapped metanode).
         * 
         * @param parentNodeID the property value, NOT <code>null</code>! 
         * @return this entity builder for chaining
         */
        WorkflowPartsEntBuilder setParentNodeID(String parentNodeID);
        
        /**
         * The ids of the nodes referenced.
         * 
         * @param nodeIDs the property value,  
         * @return this entity builder for chaining
         */
        WorkflowPartsEntBuilder setNodeIDs(java.util.List<String> nodeIDs);
        
        /**
         * The ids of the connections referenced. The id has the following format: &lt;dest-node-id&gt;_&lt;dest-port-idx&gt;
         * 
         * @param connectionIDs the property value,  
         * @return this entity builder for chaining
         */
        WorkflowPartsEntBuilder setConnectionIDs(java.util.List<String> connectionIDs);
        
        /**
         * The ids of the workflow annotations referenced.
         * 
         * @param annotationIDs the property value,  
         * @return this entity builder for chaining
         */
        WorkflowPartsEntBuilder setAnnotationIDs(java.util.List<String> annotationIDs);
        
        
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

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

import org.knime.gateway.api.entity.XYEnt;

import org.knime.gateway.api.entity.GatewayEntityBuilder;


import org.knime.gateway.api.entity.GatewayEntity;

/**
 * A single connection between two nodes.
 * 
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
@javax.annotation.Generated(value = {"com.knime.gateway.codegen.GatewayCodegen", "src-gen/org.knime.gateway.api-config.json"})
public interface ConnectionEnt extends GatewayEntity {

  /**
   * The type of the connection (standard, workflow input / output /through).
   */
  public enum TypeEnum {
    STD("STD"),
    
    WFMIN("WFMIN"),
    
    WFMOUT("WFMOUT"),
    
    WFMTHROUGH("WFMTHROUGH");

    private String value;

    TypeEnum(String value) {
      this.value = value;
    }

    @Override
    public String toString() {
      return String.valueOf(value);
    }

  }


  /**
   * The destination node. The node-id format: Node IDs always start with &#39;root&#39; and optionally followed by numbers separated by &#39;:&#39; refering to nested nodes/subworkflows,e.g. root:3:6:4. Nodes within components require an additional trailing &#39;0&#39;, e.g. &#39;root:3:6:0:4&#39; (if &#39;root:3:6&#39; is a component).
   * @return dest , never <code>null</code>
   **/
  public org.knime.gateway.api.entity.NodeIDEnt getDest();

  /**
   * The destination port, starting at 0.
   * @return destPort , never <code>null</code>
   **/
  public Integer getDestPort();

  /**
   * The source node. The node-id format: Node IDs always start with &#39;root&#39; and optionally followed by numbers separated by &#39;:&#39; refering to nested nodes/subworkflows,e.g. root:3:6:4. Nodes within components require an additional trailing &#39;0&#39;, e.g. &#39;root:3:6:0:4&#39; (if &#39;root:3:6&#39; is a component).
   * @return source , never <code>null</code>
   **/
  public org.knime.gateway.api.entity.NodeIDEnt getSource();

  /**
   * The source port, starting at 0.
   * @return sourcePort , never <code>null</code>
   **/
  public Integer getSourcePort();

  /**
   * Whether the connection can currently be deleted.
   * @return deletable 
   **/
  public Boolean isDeletable();

  /**
   * Whether it&#39;s a connection between two flow variable ports.
   * @return flowVariablePortConnection 
   **/
  public Boolean isFlowVariablePortConnection();

  /**
   * Get bendPoints
   * @return bendPoints 
   **/
  public java.util.List<XYEnt> getBendPoints();

  /**
   * The type of the connection (standard, workflow input / output /through).
   * @return type , never <code>null</code>
   **/
  public TypeEnum getType();


    /**
     * The builder for the entity.
     */
    public interface ConnectionEntBuilder extends GatewayEntityBuilder<ConnectionEnt> {

        /**
         * The destination node. The node-id format: Node IDs always start with &#39;root&#39; and optionally followed by numbers separated by &#39;:&#39; refering to nested nodes/subworkflows,e.g. root:3:6:4. Nodes within components require an additional trailing &#39;0&#39;, e.g. &#39;root:3:6:0:4&#39; (if &#39;root:3:6&#39; is a component).
         * 
         * @param dest the property value, NOT <code>null</code>! 
         * @return this entity builder for chaining
         */
        ConnectionEntBuilder setDest(org.knime.gateway.api.entity.NodeIDEnt dest);
        
        /**
         * The destination port, starting at 0.
         * 
         * @param destPort the property value, NOT <code>null</code>! 
         * @return this entity builder for chaining
         */
        ConnectionEntBuilder setDestPort(Integer destPort);
        
        /**
         * The source node. The node-id format: Node IDs always start with &#39;root&#39; and optionally followed by numbers separated by &#39;:&#39; refering to nested nodes/subworkflows,e.g. root:3:6:4. Nodes within components require an additional trailing &#39;0&#39;, e.g. &#39;root:3:6:0:4&#39; (if &#39;root:3:6&#39; is a component).
         * 
         * @param source the property value, NOT <code>null</code>! 
         * @return this entity builder for chaining
         */
        ConnectionEntBuilder setSource(org.knime.gateway.api.entity.NodeIDEnt source);
        
        /**
         * The source port, starting at 0.
         * 
         * @param sourcePort the property value, NOT <code>null</code>! 
         * @return this entity builder for chaining
         */
        ConnectionEntBuilder setSourcePort(Integer sourcePort);
        
        /**
         * Whether the connection can currently be deleted.
         * 
         * @param deletable the property value,  
         * @return this entity builder for chaining
         */
        ConnectionEntBuilder setDeletable(Boolean deletable);
        
        /**
         * Whether it&#39;s a connection between two flow variable ports.
         * 
         * @param flowVariablePortConnection the property value,  
         * @return this entity builder for chaining
         */
        ConnectionEntBuilder setFlowVariablePortConnection(Boolean flowVariablePortConnection);
        
        /**
   		 * Set bendPoints
         * 
         * @param bendPoints the property value,  
         * @return this entity builder for chaining
         */
        ConnectionEntBuilder setBendPoints(java.util.List<XYEnt> bendPoints);
        
        /**
         * The type of the connection (standard, workflow input / output /through).
         * 
         * @param type the property value, NOT <code>null</code>! 
         * @return this entity builder for chaining
         */
        ConnectionEntBuilder setType(TypeEnum type);
        
        
        /**
        * Creates the entity from the builder.
        * 
        * @return the entity
        * @throws IllegalArgumentException most likely in case when a required property hasn't been set
        */
        @Override
        ConnectionEnt build();
    
    }

}

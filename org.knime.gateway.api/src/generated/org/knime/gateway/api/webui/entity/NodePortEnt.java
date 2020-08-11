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


import org.knime.gateway.api.entity.GatewayEntityBuilder;


import org.knime.gateway.api.entity.GatewayEntity;

/**
 * A single port of a node.
 * 
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
@javax.annotation.Generated(value = {"com.knime.gateway.codegen.GatewayCodegen", "src-gen/api/web-ui/configs/org.knime.gateway.api-config.json"})
public interface NodePortEnt extends GatewayEntity {

  /**
   * Gets or Sets type
   */
  public enum TypeEnum {
    TABLE("table"),
    
    FLOWVARIABLE("flowVariable"),
    
    OTHER("other");

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
   * A descriptive name for the port (taken from the node description)
   * @return name 
   **/
  public String getName();

  /**
   * Additional port info if the port carries data (i.e. if the respective node is executed and the port is active).
   * @return info 
   **/
  public String getInfo();

  /**
   * The index starting at 0.
   * @return index , never <code>null</code>
   **/
  public Integer getIndex();

  /**
   * Get type
   * @return type , never <code>null</code>
   **/
  public TypeEnum getType();

  /**
   * The color of the port in case of type &#39;other&#39;.
   * @return color 
   **/
  public String getColor();

  /**
   * Get connectedVia
   * @return connectedVia 
   **/
  public java.util.List<org.knime.gateway.api.entity.ConnectionIDEnt> getConnectedVia();

  /**
   * Get optional
   * @return optional 
   **/
  public Boolean isOptional();

  /**
   * Get inactive
   * @return inactive 
   **/
  public Boolean isInactive();


    /**
     * The builder for the entity.
     */
    public interface NodePortEntBuilder extends GatewayEntityBuilder<NodePortEnt> {

        /**
         * A descriptive name for the port (taken from the node description)
         * 
         * @param name the property value,  
         * @return this entity builder for chaining
         */
        NodePortEntBuilder setName(String name);
        
        /**
         * Additional port info if the port carries data (i.e. if the respective node is executed and the port is active).
         * 
         * @param info the property value,  
         * @return this entity builder for chaining
         */
        NodePortEntBuilder setInfo(String info);
        
        /**
         * The index starting at 0.
         * 
         * @param index the property value, NOT <code>null</code>! 
         * @return this entity builder for chaining
         */
        NodePortEntBuilder setIndex(Integer index);
        
        /**
   		 * Set type
         * 
         * @param type the property value, NOT <code>null</code>! 
         * @return this entity builder for chaining
         */
        NodePortEntBuilder setType(TypeEnum type);
        
        /**
         * The color of the port in case of type &#39;other&#39;.
         * 
         * @param color the property value,  
         * @return this entity builder for chaining
         */
        NodePortEntBuilder setColor(String color);
        
        /**
   		 * Set connectedVia
         * 
         * @param connectedVia the property value,  
         * @return this entity builder for chaining
         */
        NodePortEntBuilder setConnectedVia(java.util.List<org.knime.gateway.api.entity.ConnectionIDEnt> connectedVia);
        
        /**
   		 * Set optional
         * 
         * @param optional the property value,  
         * @return this entity builder for chaining
         */
        NodePortEntBuilder setOptional(Boolean optional);
        
        /**
   		 * Set inactive
         * 
         * @param inactive the property value,  
         * @return this entity builder for chaining
         */
        NodePortEntBuilder setInactive(Boolean inactive);
        
        
        /**
        * Creates the entity from the builder.
        * 
        * @return the entity
        * @throws IllegalArgumentException most likely in case when a required property hasn't been set
        */
        @Override
        NodePortEnt build();
    
    }

}

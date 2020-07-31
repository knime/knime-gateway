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

import org.knime.gateway.api.webui.entity.NodePortEnt;
import org.knime.gateway.api.webui.entity.PortTypeEnt;

import org.knime.gateway.api.entity.GatewayEntityBuilder;



/**
 * The output port of a node.
 * 
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
@javax.annotation.Generated(value = {"com.knime.gateway.codegen.GatewayCodegen", "src-gen/api/web-ui/configs/org.knime.gateway.api-config.json"})
public interface NodeOutPortEnt extends NodePortEnt {


  /**
   * The port summary, e.g. num of rows and cols in case of a table.
   * @return summary 
   **/
  public String getSummary();

  /**
   * Determines whether the output is inactive.
   * @return inactive 
   **/
  public Boolean isInactive();


    /**
     * The builder for the entity.
     */
    public interface NodeOutPortEntBuilder extends GatewayEntityBuilder<NodeOutPortEnt> {

        /**
         * Discriminator for inheritance. Must be the base name of this type/schema.
         * 
         * @param type the property value, NOT <code>null</code>! 
         * @return this entity builder for chaining
         */
        NodeOutPortEntBuilder setType(String type);
        
        /**
         * The index starting at 0.
         * 
         * @param portIndex the property value, NOT <code>null</code>! 
         * @return this entity builder for chaining
         */
        NodeOutPortEntBuilder setPortIndex(Integer portIndex);
        
        /**
   		 * Set portType
         * 
         * @param portType the property value, NOT <code>null</code>! 
         * @return this entity builder for chaining
         */
        NodeOutPortEntBuilder setPortType(PortTypeEnt portType);
        
        /**
         * The name of the port.
         * 
         * @param portName the property value,  
         * @return this entity builder for chaining
         */
        NodeOutPortEntBuilder setPortName(String portName);
        
        /**
         * The port summary, e.g. num of rows and cols in case of a table.
         * 
         * @param summary the property value,  
         * @return this entity builder for chaining
         */
        NodeOutPortEntBuilder setSummary(String summary);
        
        /**
         * Determines whether the output is inactive.
         * 
         * @param inactive the property value,  
         * @return this entity builder for chaining
         */
        NodeOutPortEntBuilder setInactive(Boolean inactive);
        
        
        /**
        * Creates the entity from the builder.
        * 
        * @return the entity
        * @throws IllegalArgumentException most likely in case when a required property hasn't been set
        */
        @Override
        NodeOutPortEnt build();
    
    }

}

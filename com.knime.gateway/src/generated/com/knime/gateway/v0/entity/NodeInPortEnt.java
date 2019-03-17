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

import com.knime.gateway.v0.entity.NodePortEnt;
import com.knime.gateway.v0.entity.PortTypeEnt;

import com.knime.gateway.entity.GatewayEntityBuilder;



/**
 * An input port of a node.
 * 
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
@javax.annotation.Generated(value = {"org.knime.gateway.codegen.GatewayCodegen", "src-gen/com.knime.gateway-interfaces-config.json"})
public interface NodeInPortEnt extends NodePortEnt {



    /**
     * The builder for the entity.
     */
    public interface NodeInPortEntBuilder extends GatewayEntityBuilder<NodeInPortEnt> {

        /**
         * Discriminator for inheritance. Must be the base name of this type/schema.
         * 
         * @param type the property value, NOT <code>null</code>! 
         * @return this entity builder for chaining
         */
        NodeInPortEntBuilder setType(String type);
        
        /**
         * The index starting at 0.
         * 
         * @param portIndex the property value, NOT <code>null</code>! 
         * @return this entity builder for chaining
         */
        NodeInPortEntBuilder setPortIndex(Integer portIndex);
        
        /**
   		 * Set portType
         * 
         * @param portType the property value, NOT <code>null</code>! 
         * @return this entity builder for chaining
         */
        NodeInPortEntBuilder setPortType(PortTypeEnt portType);
        
        /**
         * The name of the port.
         * 
         * @param portName the property value,  
         * @return this entity builder for chaining
         */
        NodeInPortEntBuilder setPortName(String portName);
        
        
        /**
        * Creates the entity from the builder.
        * 
        * @return the entity
        * @throws IllegalArgumentException most likely in case when a required property hasn't been set
        */
        @Override
        NodeInPortEnt build();
    
    }

}

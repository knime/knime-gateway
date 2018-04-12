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
package com.knime.gateway.v0.entity.impl;

import com.knime.gateway.v0.entity.PortTypeEnt;
import com.knime.gateway.v0.entity.impl.DefaultNodePortEnt;

import com.knime.gateway.v0.entity.NodeInPortEnt;

/**
 * An input port of a node.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
@javax.annotation.Generated(value = "org.knime.gateway.codegen.GatewayCodegen")
public class DefaultNodeInPortEnt extends DefaultNodePortEnt implements NodeInPortEnt {

  
  protected DefaultNodeInPortEnt() {
    //for sub-classes
  }
  
  @Override
  public String getTypeID() {
    return "NodeInPort";
  }
  
  private DefaultNodeInPortEnt(DefaultNodeInPortEntBuilder builder) {
    super();
    if(builder.m_type == null) {
        throw new IllegalArgumentException("type must not be null.");
    }
    m_type = builder.m_type;
    if(builder.m_portIndex == null) {
        throw new IllegalArgumentException("portIndex must not be null.");
    }
    m_portIndex = builder.m_portIndex;
    if(builder.m_portType == null) {
        throw new IllegalArgumentException("portType must not be null.");
    }
    m_portType = builder.m_portType;
    m_portName = builder.m_portName;
  }


  
    public static class DefaultNodeInPortEntBuilder implements NodeInPortEntBuilder {
    
        public DefaultNodeInPortEntBuilder(){
            super();
        }
    
        private String m_type = null;
        private Integer m_portIndex = null;
        private PortTypeEnt m_portType;
        private String m_portName = null;

        @Override
        public DefaultNodeInPortEntBuilder setType(String type) {
             if(type == null) {
                 throw new IllegalArgumentException("type must not be null.");
             }
             m_type = type;
             return this;
        }

        @Override
        public DefaultNodeInPortEntBuilder setPortIndex(Integer portIndex) {
             if(portIndex == null) {
                 throw new IllegalArgumentException("portIndex must not be null.");
             }
             m_portIndex = portIndex;
             return this;
        }

        @Override
        public DefaultNodeInPortEntBuilder setPortType(PortTypeEnt portType) {
             if(portType == null) {
                 throw new IllegalArgumentException("portType must not be null.");
             }
             m_portType = portType;
             return this;
        }

        @Override
        public DefaultNodeInPortEntBuilder setPortName(String portName) {
             m_portName = portName;
             return this;
        }

        
        @Override
        public DefaultNodeInPortEnt build() {
            return new DefaultNodeInPortEnt(this);
        }
    
    }

}

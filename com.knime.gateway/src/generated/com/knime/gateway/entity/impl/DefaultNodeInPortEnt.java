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
package com.knime.gateway.entity.impl;

import static com.knime.gateway.util.EntityUtil.immutable;

import java.util.Objects;

import com.knime.gateway.entity.PortTypeEnt;
import com.knime.gateway.entity.impl.DefaultNodePortEnt;

import com.knime.gateway.entity.NodeInPortEnt;

/**
 * An input port of a node.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
@javax.annotation.Generated(value = {"org.knime.gateway.codegen.GatewayCodegen", "src-gen/com.knime.gateway-implementations-config.json"})
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
    m_type = immutable(builder.m_type);
    if(builder.m_portIndex == null) {
        throw new IllegalArgumentException("portIndex must not be null.");
    }
    m_portIndex = immutable(builder.m_portIndex);
    if(builder.m_portType == null) {
        throw new IllegalArgumentException("portType must not be null.");
    }
    m_portType = immutable(builder.m_portType);
    m_portName = immutable(builder.m_portName);
  }
  
   /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null) {
            return false;
        }
        if (getClass() != o.getClass()) {
            return false;
        }
        DefaultNodeInPortEnt ent = (DefaultNodeInPortEnt)o;
        return Objects.equals(m_type, ent.m_type) && Objects.equals(m_portIndex, ent.m_portIndex) && Objects.equals(m_portType, ent.m_portType) && Objects.equals(m_portName, ent.m_portName);
    }


  
    public static class DefaultNodeInPortEntBuilder implements NodeInPortEntBuilder {
    
        public DefaultNodeInPortEntBuilder(){
            super();
        }
    
        private String m_type;
        private Integer m_portIndex;
        private PortTypeEnt m_portType;
        private String m_portName;

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

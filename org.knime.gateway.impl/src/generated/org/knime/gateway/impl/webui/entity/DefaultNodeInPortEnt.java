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
package org.knime.gateway.impl.webui.entity;

import static org.knime.gateway.api.util.EntityUtil.immutable;

import java.util.Objects;

import org.knime.gateway.api.webui.entity.PortTypeEnt;
import org.knime.gateway.impl.webui.entity.DefaultNodePortEnt;

import org.knime.gateway.api.webui.entity.NodeInPortEnt;

/**
 * An input port of a node.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
@javax.annotation.Generated(value = {"com.knime.gateway.codegen.GatewayCodegen", "src-gen/api/web-ui/configs/org.knime.gateway.impl-config.json"})
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
    if(builder.m_objectType == null) {
        throw new IllegalArgumentException("objectType must not be null.");
    }
    m_objectType = immutable(builder.m_objectType);
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
        return Objects.equals(m_objectType, ent.m_objectType) && Objects.equals(m_portIndex, ent.m_portIndex) && Objects.equals(m_portType, ent.m_portType) && Objects.equals(m_portName, ent.m_portName);
    }


  
    public static class DefaultNodeInPortEntBuilder implements NodeInPortEntBuilder {
    
        public DefaultNodeInPortEntBuilder(){
            super();
        }
    
        private String m_objectType;
        private Integer m_portIndex;
        private PortTypeEnt m_portType;
        private String m_portName;

        @Override
        public DefaultNodeInPortEntBuilder setObjectType(String objectType) {
             if(objectType == null) {
                 throw new IllegalArgumentException("objectType must not be null.");
             }
             m_objectType = objectType;
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

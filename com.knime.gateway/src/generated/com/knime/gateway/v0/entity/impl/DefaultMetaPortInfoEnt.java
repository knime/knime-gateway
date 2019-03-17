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

import static com.knime.gateway.util.EntityUtil.immutable;

import java.util.Objects;

import com.knime.gateway.v0.entity.PortTypeEnt;

import com.knime.gateway.v0.entity.MetaPortInfoEnt;

/**
 * The port of a metanode.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
@javax.annotation.Generated(value = {"org.knime.gateway.codegen.GatewayCodegen", "src-gen/com.knime.gateway-implementations-config.json"})
public class DefaultMetaPortInfoEnt  implements MetaPortInfoEnt {

  protected PortTypeEnt m_portType;
  protected Boolean m_connected;
  protected String m_message;
  protected Integer m_oldIndex;
  protected Integer m_newIndex;
  
  protected DefaultMetaPortInfoEnt() {
    //for sub-classes
  }
  
  @Override
  public String getTypeID() {
    return "MetaPortInfo";
  }
  
  private DefaultMetaPortInfoEnt(DefaultMetaPortInfoEntBuilder builder) {
    
    if(builder.m_portType == null) {
        throw new IllegalArgumentException("portType must not be null.");
    }
    m_portType = immutable(builder.m_portType);
    m_connected = immutable(builder.m_connected);
    m_message = immutable(builder.m_message);
    m_oldIndex = immutable(builder.m_oldIndex);
    m_newIndex = immutable(builder.m_newIndex);
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
        DefaultMetaPortInfoEnt ent = (DefaultMetaPortInfoEnt)o;
        return Objects.equals(m_portType, ent.m_portType) && Objects.equals(m_connected, ent.m_connected) && Objects.equals(m_message, ent.m_message) && Objects.equals(m_oldIndex, ent.m_oldIndex) && Objects.equals(m_newIndex, ent.m_newIndex);
    }


  @Override
  public PortTypeEnt getPortType() {
        return m_portType;
    }
    
  @Override
  public Boolean isConnected() {
        return m_connected;
    }
    
  @Override
  public String getMessage() {
        return m_message;
    }
    
  @Override
  public Integer getOldIndex() {
        return m_oldIndex;
    }
    
  @Override
  public Integer getNewIndex() {
        return m_newIndex;
    }
    
  
    public static class DefaultMetaPortInfoEntBuilder implements MetaPortInfoEntBuilder {
    
        public DefaultMetaPortInfoEntBuilder(){
            
        }
    
        private PortTypeEnt m_portType;
        private Boolean m_connected;
        private String m_message;
        private Integer m_oldIndex;
        private Integer m_newIndex;

        @Override
        public DefaultMetaPortInfoEntBuilder setPortType(PortTypeEnt portType) {
             if(portType == null) {
                 throw new IllegalArgumentException("portType must not be null.");
             }
             m_portType = portType;
             return this;
        }

        @Override
        public DefaultMetaPortInfoEntBuilder setConnected(Boolean connected) {
             m_connected = connected;
             return this;
        }

        @Override
        public DefaultMetaPortInfoEntBuilder setMessage(String message) {
             m_message = message;
             return this;
        }

        @Override
        public DefaultMetaPortInfoEntBuilder setOldIndex(Integer oldIndex) {
             m_oldIndex = oldIndex;
             return this;
        }

        @Override
        public DefaultMetaPortInfoEntBuilder setNewIndex(Integer newIndex) {
             m_newIndex = newIndex;
             return this;
        }

        
        @Override
        public DefaultMetaPortInfoEnt build() {
            return new DefaultMetaPortInfoEnt(this);
        }
    
    }

}

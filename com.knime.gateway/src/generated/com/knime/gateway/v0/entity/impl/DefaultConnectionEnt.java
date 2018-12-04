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

import com.knime.gateway.v0.entity.XYEnt;

import com.knime.gateway.v0.entity.ConnectionEnt;

/**
 * A single connection between two nodes.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
@javax.annotation.Generated(value = "org.knime.gateway.codegen.GatewayCodegen")
public class DefaultConnectionEnt  implements ConnectionEnt {

  protected String m_dest;
  protected Integer m_destPort;
  protected String m_source;
  protected Integer m_sourcePort;
  protected Boolean m_deletable;
  protected Boolean m_flowVariablePortConnection;
  protected java.util.List<XYEnt> m_bendPoints;
  protected TypeEnum m_type;
  
  protected DefaultConnectionEnt() {
    //for sub-classes
  }
  
  @Override
  public String getTypeID() {
    return "Connection";
  }
  
  private DefaultConnectionEnt(DefaultConnectionEntBuilder builder) {
    
    if(builder.m_dest == null) {
        throw new IllegalArgumentException("dest must not be null.");
    }
    m_dest = immutable(builder.m_dest);
    if(builder.m_destPort == null) {
        throw new IllegalArgumentException("destPort must not be null.");
    }
    m_destPort = immutable(builder.m_destPort);
    if(builder.m_source == null) {
        throw new IllegalArgumentException("source must not be null.");
    }
    m_source = immutable(builder.m_source);
    if(builder.m_sourcePort == null) {
        throw new IllegalArgumentException("sourcePort must not be null.");
    }
    m_sourcePort = immutable(builder.m_sourcePort);
    m_deletable = immutable(builder.m_deletable);
    m_flowVariablePortConnection = immutable(builder.m_flowVariablePortConnection);
    m_bendPoints = immutable(builder.m_bendPoints);
    if(builder.m_type == null) {
        throw new IllegalArgumentException("type must not be null.");
    }
    m_type = immutable(builder.m_type);
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
        DefaultConnectionEnt ent = (DefaultConnectionEnt)o;
        return Objects.equals(m_dest, ent.m_dest) && Objects.equals(m_destPort, ent.m_destPort) && Objects.equals(m_source, ent.m_source) && Objects.equals(m_sourcePort, ent.m_sourcePort) && Objects.equals(m_deletable, ent.m_deletable) && Objects.equals(m_flowVariablePortConnection, ent.m_flowVariablePortConnection) && Objects.equals(m_bendPoints, ent.m_bendPoints) && Objects.equals(m_type, ent.m_type);
    }


  @Override
  public String getDest() {
        return m_dest;
    }
    
  @Override
  public Integer getDestPort() {
        return m_destPort;
    }
    
  @Override
  public String getSource() {
        return m_source;
    }
    
  @Override
  public Integer getSourcePort() {
        return m_sourcePort;
    }
    
  @Override
  public Boolean isDeletable() {
        return m_deletable;
    }
    
  @Override
  public Boolean isFlowVariablePortConnection() {
        return m_flowVariablePortConnection;
    }
    
  @Override
  public java.util.List<XYEnt> getBendPoints() {
        return m_bendPoints;
    }
    
  @Override
  public TypeEnum getType() {
        return m_type;
    }
    
  
    public static class DefaultConnectionEntBuilder implements ConnectionEntBuilder {
    
        public DefaultConnectionEntBuilder(){
            
        }
    
        private String m_dest;
        private Integer m_destPort;
        private String m_source;
        private Integer m_sourcePort;
        private Boolean m_deletable;
        private Boolean m_flowVariablePortConnection;
        private java.util.List<XYEnt> m_bendPoints = new java.util.ArrayList<>();
        private TypeEnum m_type;

        @Override
        public DefaultConnectionEntBuilder setDest(String dest) {
             if(dest == null) {
                 throw new IllegalArgumentException("dest must not be null.");
             }
             m_dest = dest;
             return this;
        }

        @Override
        public DefaultConnectionEntBuilder setDestPort(Integer destPort) {
             if(destPort == null) {
                 throw new IllegalArgumentException("destPort must not be null.");
             }
             m_destPort = destPort;
             return this;
        }

        @Override
        public DefaultConnectionEntBuilder setSource(String source) {
             if(source == null) {
                 throw new IllegalArgumentException("source must not be null.");
             }
             m_source = source;
             return this;
        }

        @Override
        public DefaultConnectionEntBuilder setSourcePort(Integer sourcePort) {
             if(sourcePort == null) {
                 throw new IllegalArgumentException("sourcePort must not be null.");
             }
             m_sourcePort = sourcePort;
             return this;
        }

        @Override
        public DefaultConnectionEntBuilder setDeletable(Boolean deletable) {
             m_deletable = deletable;
             return this;
        }

        @Override
        public DefaultConnectionEntBuilder setFlowVariablePortConnection(Boolean flowVariablePortConnection) {
             m_flowVariablePortConnection = flowVariablePortConnection;
             return this;
        }

        @Override
        public DefaultConnectionEntBuilder setBendPoints(java.util.List<XYEnt> bendPoints) {
             m_bendPoints = bendPoints;
             return this;
        }

        @Override
        public DefaultConnectionEntBuilder setType(TypeEnum type) {
             if(type == null) {
                 throw new IllegalArgumentException("type must not be null.");
             }
             m_type = type;
             return this;
        }

        
        @Override
        public DefaultConnectionEnt build() {
            return new DefaultConnectionEnt(this);
        }
    
    }

}

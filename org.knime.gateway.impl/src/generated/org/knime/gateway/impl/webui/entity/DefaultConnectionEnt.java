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


import org.knime.gateway.api.webui.entity.ConnectionEnt;

/**
 * A single connection between two nodes.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
@javax.annotation.Generated(value = {"com.knime.gateway.codegen.GatewayCodegen", "src-gen/api/web-ui/configs/org.knime.gateway.impl-config.json"})
public class DefaultConnectionEnt  implements ConnectionEnt {

  protected String m_type;
  protected org.knime.gateway.api.entity.NodeIDEnt m_dest;
  protected Integer m_destPort;
  protected org.knime.gateway.api.entity.NodeIDEnt m_source;
  protected Integer m_sourcePort;
  
  protected DefaultConnectionEnt() {
    //for sub-classes
  }
  
  @Override
  public String getTypeID() {
    return "Connection";
  }
  
  private DefaultConnectionEnt(DefaultConnectionEntBuilder builder) {
    
    m_type = immutable(builder.m_type);
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
        return Objects.equals(m_type, ent.m_type) && Objects.equals(m_dest, ent.m_dest) && Objects.equals(m_destPort, ent.m_destPort) && Objects.equals(m_source, ent.m_source) && Objects.equals(m_sourcePort, ent.m_sourcePort);
    }


  @Override
  public String getType() {
        return m_type;
    }
    
  @Override
  public org.knime.gateway.api.entity.NodeIDEnt getDest() {
        return m_dest;
    }
    
  @Override
  public Integer getDestPort() {
        return m_destPort;
    }
    
  @Override
  public org.knime.gateway.api.entity.NodeIDEnt getSource() {
        return m_source;
    }
    
  @Override
  public Integer getSourcePort() {
        return m_sourcePort;
    }
    
  
    public static class DefaultConnectionEntBuilder implements ConnectionEntBuilder {
    
        public DefaultConnectionEntBuilder(){
            
        }
    
        private String m_type;
        private org.knime.gateway.api.entity.NodeIDEnt m_dest;
        private Integer m_destPort;
        private org.knime.gateway.api.entity.NodeIDEnt m_source;
        private Integer m_sourcePort;

        @Override
        public DefaultConnectionEntBuilder setType(String type) {
             m_type = type;
             return this;
        }

        @Override
        public DefaultConnectionEntBuilder setDest(org.knime.gateway.api.entity.NodeIDEnt dest) {
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
        public DefaultConnectionEntBuilder setSource(org.knime.gateway.api.entity.NodeIDEnt source) {
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
        public DefaultConnectionEnt build() {
            return new DefaultConnectionEnt(this);
        }
    
    }

}

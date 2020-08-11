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

import org.apache.commons.lang3.builder.HashCodeBuilder;


import org.knime.gateway.api.webui.entity.ConnectionEnt;

/**
 * A single connection between two nodes.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
@javax.annotation.Generated(value = {"com.knime.gateway.codegen.GatewayCodegen", "src-gen/api/web-ui/configs/org.knime.gateway.impl-config.json"})
public class DefaultConnectionEnt  implements ConnectionEnt {

  protected org.knime.gateway.api.entity.NodeIDEnt m_destNode;
  protected Integer m_destPort;
  protected org.knime.gateway.api.entity.NodeIDEnt m_sourceNode;
  protected Integer m_sourcePort;
  protected Boolean m_flowVariableConnection;
  
  protected DefaultConnectionEnt() {
    //for sub-classes
  }
  
  @Override
  public String getTypeID() {
    return "Connection";
  }
  
  private DefaultConnectionEnt(DefaultConnectionEntBuilder builder) {
    
    if(builder.m_destNode == null) {
        throw new IllegalArgumentException("destNode must not be null.");
    }
    m_destNode = immutable(builder.m_destNode);
    if(builder.m_destPort == null) {
        throw new IllegalArgumentException("destPort must not be null.");
    }
    m_destPort = immutable(builder.m_destPort);
    if(builder.m_sourceNode == null) {
        throw new IllegalArgumentException("sourceNode must not be null.");
    }
    m_sourceNode = immutable(builder.m_sourceNode);
    if(builder.m_sourcePort == null) {
        throw new IllegalArgumentException("sourcePort must not be null.");
    }
    m_sourcePort = immutable(builder.m_sourcePort);
    m_flowVariableConnection = immutable(builder.m_flowVariableConnection);
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
        return Objects.equals(m_destNode, ent.m_destNode) && Objects.equals(m_destPort, ent.m_destPort) && Objects.equals(m_sourceNode, ent.m_sourceNode) && Objects.equals(m_sourcePort, ent.m_sourcePort) && Objects.equals(m_flowVariableConnection, ent.m_flowVariableConnection);
    }


  
   /**
    * {@inheritDoc}
    */
   @Override
   public int hashCode() {
       return new HashCodeBuilder()
               .append(m_destNode)
               .append(m_destPort)
               .append(m_sourceNode)
               .append(m_sourcePort)
               .append(m_flowVariableConnection)
               .toHashCode();
   }
  
	
	
  @Override
  public org.knime.gateway.api.entity.NodeIDEnt getDestNode() {
        return m_destNode;
  }
    
  @Override
  public Integer getDestPort() {
        return m_destPort;
  }
    
  @Override
  public org.knime.gateway.api.entity.NodeIDEnt getSourceNode() {
        return m_sourceNode;
  }
    
  @Override
  public Integer getSourcePort() {
        return m_sourcePort;
  }
    
  @Override
  public Boolean isFlowVariableConnection() {
        return m_flowVariableConnection;
  }
    
  
    public static class DefaultConnectionEntBuilder implements ConnectionEntBuilder {
    
        public DefaultConnectionEntBuilder(){
            
        }
    
        private org.knime.gateway.api.entity.NodeIDEnt m_destNode;
        private Integer m_destPort;
        private org.knime.gateway.api.entity.NodeIDEnt m_sourceNode;
        private Integer m_sourcePort;
        private Boolean m_flowVariableConnection;

        @Override
        public DefaultConnectionEntBuilder setDestNode(org.knime.gateway.api.entity.NodeIDEnt destNode) {
             if(destNode == null) {
                 throw new IllegalArgumentException("destNode must not be null.");
             }
             m_destNode = destNode;
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
        public DefaultConnectionEntBuilder setSourceNode(org.knime.gateway.api.entity.NodeIDEnt sourceNode) {
             if(sourceNode == null) {
                 throw new IllegalArgumentException("sourceNode must not be null.");
             }
             m_sourceNode = sourceNode;
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
        public DefaultConnectionEntBuilder setFlowVariableConnection(Boolean flowVariableConnection) {
             m_flowVariableConnection = flowVariableConnection;
             return this;
        }

        
        @Override
        public DefaultConnectionEnt build() {
            return new DefaultConnectionEnt(this);
        }
    
    }

}

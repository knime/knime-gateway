/*
 * ------------------------------------------------------------------------
 *  Copyright by KNIME AG, Zurich, Switzerland
 *  Website: http://www.knime.com; Email: contact@knime.com
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License, Version 3, as
 *  published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but
 *  WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, see <http://www.gnu.org/licenses>.
 *
 *  Additional permission under GNU GPL version 3 section 7:
 *
 *  KNIME interoperates with ECLIPSE solely via ECLIPSE's plug-in APIs.
 *  Hence, KNIME and ECLIPSE are both independent programs and are not
 *  derived from each other. Should, however, the interpretation of the
 *  GNU GPL Version 3 ("License") under any applicable laws result in
 *  KNIME and ECLIPSE being a combined program, KNIME AG herewith grants
 *  you the additional permission to use and propagate KNIME together with
 *  ECLIPSE with only the license terms in place for ECLIPSE applying to
 *  ECLIPSE and the GNU GPL Version 3 applying for KNIME, provided the
 *  license terms of ECLIPSE themselves allow for the respective use and
 *  propagation of ECLIPSE together with KNIME.
 *
 *  Additional permission relating to nodes for KNIME that extend the Node
 *  Extension (and in particular that are based on subclasses of NodeModel,
 *  NodeDialog, and NodeView) and that only interoperate with KNIME through
 *  standard APIs ("Nodes"):
 *  Nodes are deemed to be separate and independent programs and to not be
 *  covered works.  Notwithstanding anything to the contrary in the
 *  License, the License does not apply to Nodes, you are not required to
 *  license Nodes under the License, and you are granted a license to
 *  prepare and propagate Nodes, in each case even if such Nodes are
 *  propagated with or for interoperation with KNIME.  The owner of a Node
 *  may freely choose the license terms applicable to such Node, including
 *  when such Node is propagated with or for interoperation with KNIME.
 * ------------------------------------------------------------------------
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
public class DefaultConnectionEnt implements ConnectionEnt {

  protected org.knime.gateway.api.entity.NodeIDEnt m_destNode;
  protected Integer m_destPort;
  protected org.knime.gateway.api.entity.NodeIDEnt m_sourceNode;
  protected Integer m_sourcePort;
  protected Boolean m_flowVariableConnection;
  protected Boolean m_streaming;
  protected String m_label;
  protected Boolean m_canDelete;
  
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
    m_streaming = immutable(builder.m_streaming);
    m_label = immutable(builder.m_label);
    m_canDelete = immutable(builder.m_canDelete);
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
        return Objects.equals(m_destNode, ent.m_destNode) && Objects.equals(m_destPort, ent.m_destPort) && Objects.equals(m_sourceNode, ent.m_sourceNode) && Objects.equals(m_sourcePort, ent.m_sourcePort) && Objects.equals(m_flowVariableConnection, ent.m_flowVariableConnection) && Objects.equals(m_streaming, ent.m_streaming) && Objects.equals(m_label, ent.m_label) && Objects.equals(m_canDelete, ent.m_canDelete);
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
               .append(m_streaming)
               .append(m_label)
               .append(m_canDelete)
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
    
  @Override
  public Boolean isStreaming() {
        return m_streaming;
  }
    
  @Override
  public String getLabel() {
        return m_label;
  }
    
  @Override
  public Boolean isCanDelete() {
        return m_canDelete;
  }
    
  
    public static class DefaultConnectionEntBuilder implements ConnectionEntBuilder {
    
        public DefaultConnectionEntBuilder(){
            
        }
    
        private org.knime.gateway.api.entity.NodeIDEnt m_destNode;
        private Integer m_destPort;
        private org.knime.gateway.api.entity.NodeIDEnt m_sourceNode;
        private Integer m_sourcePort;
        private Boolean m_flowVariableConnection;
        private Boolean m_streaming;
        private String m_label;
        private Boolean m_canDelete;

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
        public DefaultConnectionEntBuilder setStreaming(Boolean streaming) {
             m_streaming = streaming;
             return this;
        }

        @Override
        public DefaultConnectionEntBuilder setLabel(String label) {
             m_label = label;
             return this;
        }

        @Override
        public DefaultConnectionEntBuilder setCanDelete(Boolean canDelete) {
             m_canDelete = canDelete;
             return this;
        }

        
        @Override
        public DefaultConnectionEnt build() {
            return new DefaultConnectionEnt(this);
        }
    
    }

}

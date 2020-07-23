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
package org.knime.gateway.impl.entity;

import static org.knime.gateway.api.util.EntityUtil.immutable;

import java.util.Objects;

import org.knime.gateway.api.entity.XYEnt;

import org.knime.gateway.api.entity.ConnectionEnt;

/**
 * A single connection between two nodes.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
@javax.annotation.Generated(value = {"com.knime.gateway.codegen.GatewayCodegen", "src-gen/org.knime.gateway.impl-config.json"})
public class DefaultConnectionEnt  implements ConnectionEnt {

  protected org.knime.gateway.api.entity.NodeIDEnt m_dest;
  protected Integer m_destPort;
  protected org.knime.gateway.api.entity.NodeIDEnt m_source;
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
    
        private org.knime.gateway.api.entity.NodeIDEnt m_dest;
        private Integer m_destPort;
        private org.knime.gateway.api.entity.NodeIDEnt m_source;
        private Integer m_sourcePort;
        private Boolean m_deletable;
        private Boolean m_flowVariablePortConnection;
        private java.util.List<XYEnt> m_bendPoints = new java.util.ArrayList<>();
        private TypeEnum m_type;

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

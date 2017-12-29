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
 *  KNIME and ECLIPSE being a combined program, KNIME GMBH herewith grants
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
package org.knime.gateway.v0.entity.impl;

import org.knime.gateway.v0.entity.XYEnt;

import org.knime.gateway.v0.entity.ConnectionEnt;

/**
 * A single connection between two nodes.
 *
 * @author Martin Horn, University of Konstanz
 */
// AUTO-GENERATED CODE; DO NOT MODIFY
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
  
  private DefaultConnectionEnt(DefaultConnectionEntBuilder builder) {
    
    if(builder.m_dest == null) {
        throw new IllegalArgumentException("dest must not be null.");
    }
    m_dest = builder.m_dest;
    if(builder.m_destPort == null) {
        throw new IllegalArgumentException("destPort must not be null.");
    }
    m_destPort = builder.m_destPort;
    if(builder.m_source == null) {
        throw new IllegalArgumentException("source must not be null.");
    }
    m_source = builder.m_source;
    if(builder.m_sourcePort == null) {
        throw new IllegalArgumentException("sourcePort must not be null.");
    }
    m_sourcePort = builder.m_sourcePort;
    m_deletable = builder.m_deletable;
    m_flowVariablePortConnection = builder.m_flowVariablePortConnection;
    m_bendPoints = builder.m_bendPoints;
    m_type = builder.m_type;
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
    
        private String m_dest = null;
        private Integer m_destPort = null;
        private String m_source = null;
        private Integer m_sourcePort = null;
        private Boolean m_deletable = null;
        private Boolean m_flowVariablePortConnection = null;
        private java.util.List<XYEnt> m_bendPoints = new java.util.ArrayList<>();
        private TypeEnum m_type = null;

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
             m_type = type;
             return this;
        }

        
        @Override
        public DefaultConnectionEnt build() {
            return new DefaultConnectionEnt(this);
        }
    
    }

}

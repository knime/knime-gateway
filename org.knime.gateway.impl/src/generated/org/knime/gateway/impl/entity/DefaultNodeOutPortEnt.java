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

import org.knime.gateway.api.entity.PortTypeEnt;
import org.knime.gateway.impl.entity.DefaultNodePortEnt;

import org.knime.gateway.api.entity.NodeOutPortEnt;

/**
 * The output port of a node.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
@javax.annotation.Generated(value = {"com.knime.gateway.codegen.GatewayCodegen", "src-gen/org.knime.gateway.impl-config.json"})
public class DefaultNodeOutPortEnt extends DefaultNodePortEnt implements NodeOutPortEnt {

  protected String m_summary;
  protected Boolean m_inactive;
  
  protected DefaultNodeOutPortEnt() {
    //for sub-classes
  }
  
  @Override
  public String getTypeID() {
    return "NodeOutPort";
  }
  
  private DefaultNodeOutPortEnt(DefaultNodeOutPortEntBuilder builder) {
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
    m_summary = immutable(builder.m_summary);
    m_inactive = immutable(builder.m_inactive);
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
        DefaultNodeOutPortEnt ent = (DefaultNodeOutPortEnt)o;
        return Objects.equals(m_type, ent.m_type) && Objects.equals(m_portIndex, ent.m_portIndex) && Objects.equals(m_portType, ent.m_portType) && Objects.equals(m_portName, ent.m_portName) && Objects.equals(m_summary, ent.m_summary) && Objects.equals(m_inactive, ent.m_inactive);
    }


  @Override
  public String getSummary() {
        return m_summary;
    }
    
  @Override
  public Boolean isInactive() {
        return m_inactive;
    }
    
  
    public static class DefaultNodeOutPortEntBuilder implements NodeOutPortEntBuilder {
    
        public DefaultNodeOutPortEntBuilder(){
            super();
        }
    
        private String m_type;
        private Integer m_portIndex;
        private PortTypeEnt m_portType;
        private String m_portName;
        private String m_summary;
        private Boolean m_inactive;

        @Override
        public DefaultNodeOutPortEntBuilder setType(String type) {
             if(type == null) {
                 throw new IllegalArgumentException("type must not be null.");
             }
             m_type = type;
             return this;
        }

        @Override
        public DefaultNodeOutPortEntBuilder setPortIndex(Integer portIndex) {
             if(portIndex == null) {
                 throw new IllegalArgumentException("portIndex must not be null.");
             }
             m_portIndex = portIndex;
             return this;
        }

        @Override
        public DefaultNodeOutPortEntBuilder setPortType(PortTypeEnt portType) {
             if(portType == null) {
                 throw new IllegalArgumentException("portType must not be null.");
             }
             m_portType = portType;
             return this;
        }

        @Override
        public DefaultNodeOutPortEntBuilder setPortName(String portName) {
             m_portName = portName;
             return this;
        }

        @Override
        public DefaultNodeOutPortEntBuilder setSummary(String summary) {
             m_summary = summary;
             return this;
        }

        @Override
        public DefaultNodeOutPortEntBuilder setInactive(Boolean inactive) {
             m_inactive = inactive;
             return this;
        }

        
        @Override
        public DefaultNodeOutPortEnt build() {
            return new DefaultNodeOutPortEnt(this);
        }
    
    }

}

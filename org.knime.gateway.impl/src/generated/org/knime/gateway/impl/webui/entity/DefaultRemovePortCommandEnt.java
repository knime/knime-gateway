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

import org.knime.gateway.impl.webui.entity.DefaultPortCommandEnt;

import org.knime.gateway.api.webui.entity.RemovePortCommandEnt;

/**
 * Remove a port from a node
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
@javax.annotation.Generated(value = {"com.knime.gateway.codegen.GatewayCodegen", "src-gen/api/web-ui/configs/org.knime.gateway.impl-config.json"})
public class DefaultRemovePortCommandEnt implements RemovePortCommandEnt {

  protected KindEnum m_kind;
  protected SideEnum m_side;
  protected String m_portGroup;
  protected org.knime.gateway.api.entity.NodeIDEnt m_nodeId;
  protected Integer m_portIndex;
  
  protected DefaultRemovePortCommandEnt() {
    //for sub-classes
  }
  
  @Override
  public String getTypeID() {
    return "RemovePortCommand";
  }
  
  private DefaultRemovePortCommandEnt(DefaultRemovePortCommandEntBuilder builder) {
    super();
    if(builder.m_kind == null) {
        throw new IllegalArgumentException("kind must not be null.");
    }
    m_kind = immutable(builder.m_kind);
    if(builder.m_side == null) {
        throw new IllegalArgumentException("side must not be null.");
    }
    m_side = immutable(builder.m_side);
    m_portGroup = immutable(builder.m_portGroup);
    if(builder.m_nodeId == null) {
        throw new IllegalArgumentException("nodeId must not be null.");
    }
    m_nodeId = immutable(builder.m_nodeId);
    if(builder.m_portIndex == null) {
        throw new IllegalArgumentException("portIndex must not be null.");
    }
    m_portIndex = immutable(builder.m_portIndex);
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
        DefaultRemovePortCommandEnt ent = (DefaultRemovePortCommandEnt)o;
        return Objects.equals(m_kind, ent.m_kind) && Objects.equals(m_side, ent.m_side) && Objects.equals(m_portGroup, ent.m_portGroup) && Objects.equals(m_nodeId, ent.m_nodeId) && Objects.equals(m_portIndex, ent.m_portIndex);
    }


  
   /**
    * {@inheritDoc}
    */
   @Override
   public int hashCode() {
       return new HashCodeBuilder()
               .append(m_kind)
               .append(m_side)
               .append(m_portGroup)
               .append(m_nodeId)
               .append(m_portIndex)
               .toHashCode();
   }
  
	
	
  @Override
  public KindEnum getKind() {
        return m_kind;
  }
    
  @Override
  public SideEnum getSide() {
        return m_side;
  }
    
  @Override
  public String getPortGroup() {
        return m_portGroup;
  }
    
  @Override
  public org.knime.gateway.api.entity.NodeIDEnt getNodeId() {
        return m_nodeId;
  }
    
  @Override
  public Integer getPortIndex() {
        return m_portIndex;
  }
    
  
    public static class DefaultRemovePortCommandEntBuilder implements RemovePortCommandEntBuilder {
    
        public DefaultRemovePortCommandEntBuilder(){
            super();
        }
    
        private KindEnum m_kind;
        private SideEnum m_side;
        private String m_portGroup;
        private org.knime.gateway.api.entity.NodeIDEnt m_nodeId;
        private Integer m_portIndex;

        @Override
        public DefaultRemovePortCommandEntBuilder setKind(KindEnum kind) {
             if(kind == null) {
                 throw new IllegalArgumentException("kind must not be null.");
             }
             m_kind = kind;
             return this;
        }

        @Override
        public DefaultRemovePortCommandEntBuilder setSide(SideEnum side) {
             if(side == null) {
                 throw new IllegalArgumentException("side must not be null.");
             }
             m_side = side;
             return this;
        }

        @Override
        public DefaultRemovePortCommandEntBuilder setPortGroup(String portGroup) {
             m_portGroup = portGroup;
             return this;
        }

        @Override
        public DefaultRemovePortCommandEntBuilder setNodeId(org.knime.gateway.api.entity.NodeIDEnt nodeId) {
             if(nodeId == null) {
                 throw new IllegalArgumentException("nodeId must not be null.");
             }
             m_nodeId = nodeId;
             return this;
        }

        @Override
        public DefaultRemovePortCommandEntBuilder setPortIndex(Integer portIndex) {
             if(portIndex == null) {
                 throw new IllegalArgumentException("portIndex must not be null.");
             }
             m_portIndex = portIndex;
             return this;
        }

        
        @Override
        public DefaultRemovePortCommandEnt build() {
            return new DefaultRemovePortCommandEnt(this);
        }
    
    }

}

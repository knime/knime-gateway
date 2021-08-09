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

import org.knime.gateway.api.webui.entity.NodePortEnt;

import org.knime.gateway.api.webui.entity.MetaPortsEnt;

/**
 * Describes the ports(-bar) leading into or leaving a metanode&#39;s workflow. Is not given if there aren&#39;t any metanode inputs/outputs or if it&#39;s not a metanode&#39;s workflow (but a component&#39;s workflow or the root).
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
@javax.annotation.Generated(value = {"com.knime.gateway.codegen.GatewayCodegen", "src-gen/api/web-ui/configs/org.knime.gateway.impl-config.json"})
public class DefaultMetaPortsEnt implements MetaPortsEnt {

  protected Integer m_xPos;
  protected java.util.List<NodePortEnt> m_ports;
  
  protected DefaultMetaPortsEnt() {
    //for sub-classes
  }
  
  @Override
  public String getTypeID() {
    return "MetaPorts";
  }
  
  private DefaultMetaPortsEnt(DefaultMetaPortsEntBuilder builder) {
    
    m_xPos = immutable(builder.m_xPos);
    m_ports = immutable(builder.m_ports);
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
        DefaultMetaPortsEnt ent = (DefaultMetaPortsEnt)o;
        return Objects.equals(m_xPos, ent.m_xPos) && Objects.equals(m_ports, ent.m_ports);
    }


  
   /**
    * {@inheritDoc}
    */
   @Override
   public int hashCode() {
       return new HashCodeBuilder()
               .append(m_xPos)
               .append(m_ports)
               .toHashCode();
   }
  
	
	
  @Override
  public Integer getXPos() {
        return m_xPos;
  }
    
  @Override
  public java.util.List<NodePortEnt> getPorts() {
        return m_ports;
  }
    
  
    public static class DefaultMetaPortsEntBuilder implements MetaPortsEntBuilder {
    
        public DefaultMetaPortsEntBuilder(){
            
        }
    
        private Integer m_xPos;
        private java.util.List<NodePortEnt> m_ports;

        @Override
        public DefaultMetaPortsEntBuilder setXPos(Integer xPos) {
             m_xPos = xPos;
             return this;
        }

        @Override
        public DefaultMetaPortsEntBuilder setPorts(java.util.List<NodePortEnt> ports) {
             m_ports = ports;
             return this;
        }

        
        @Override
        public DefaultMetaPortsEnt build() {
            return new DefaultMetaPortsEnt(this);
        }
    
    }

}

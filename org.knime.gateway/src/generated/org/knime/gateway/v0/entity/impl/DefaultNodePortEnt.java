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
package org.knime.gateway.v0.entity.impl;

import org.knime.gateway.v0.entity.PortTypeEnt;

import org.knime.gateway.v0.entity.NodePortEnt;

/**
 * A single port of a node.
 *
 * @author Martin Horn, University of Konstanz
 */
// AUTO-GENERATED CODE; DO NOT MODIFY
public class DefaultNodePortEnt  implements NodePortEnt {

  protected Integer m_type;
  protected Integer m_portIndex;
  protected PortTypeEnt m_portType;
  protected String m_portName;
  
  protected DefaultNodePortEnt() {
    //for sub-classes
  }
  
  private DefaultNodePortEnt(DefaultNodePortEntBuilder builder) {
    
    m_type = builder.m_type;
    m_portIndex = builder.m_portIndex;
    m_portType = builder.m_portType;
    m_portName = builder.m_portName;
  }


  /**
   * discriminator for inheritance.
   * @return type
   **/
  @Override
    public Integer getType() {
        return m_type;
    }
  /**
   * The index starting at 0.
   * @return portIndex
   **/
  @Override
    public Integer getPortIndex() {
        return m_portIndex;
    }
  /**
   * The type of the port.
   * @return portType
   **/
  @Override
    public PortTypeEnt getPortType() {
        return m_portType;
    }
  /**
   * The name of the port.
   * @return portName
   **/
  @Override
    public String getPortName() {
        return m_portName;
    }
  
    public static class DefaultNodePortEntBuilder implements NodePortEntBuilder {
    
        public DefaultNodePortEntBuilder(){
            
        }
    
        private Integer m_type;
        private Integer m_portIndex;
        private PortTypeEnt m_portType;
        private String m_portName;

        @Override
        public DefaultNodePortEntBuilder setType(Integer type) {
             m_type = type;
             return this;
        }
        @Override
        public DefaultNodePortEntBuilder setPortIndex(Integer portIndex) {
             m_portIndex = portIndex;
             return this;
        }
        @Override
        public DefaultNodePortEntBuilder setPortType(PortTypeEnt portType) {
             m_portType = portType;
             return this;
        }
        @Override
        public DefaultNodePortEntBuilder setPortName(String portName) {
             m_portName = portName;
             return this;
        }
        
        @Override
        public DefaultNodePortEnt build() {
            return new DefaultNodePortEnt(this);
        }
    
    }

}

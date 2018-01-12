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

/**
 * The type of a port.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
@javax.annotation.Generated(value = "org.knime.gateway.codegen.GatewayCodegen")
public class DefaultPortTypeEnt  implements PortTypeEnt {

  protected String m_portObjectClassName;
  protected Boolean m_optional;
  
  protected DefaultPortTypeEnt() {
    //for sub-classes
  }
  
  private DefaultPortTypeEnt(DefaultPortTypeEntBuilder builder) {
    
    if(builder.m_portObjectClassName == null) {
        throw new IllegalArgumentException("portObjectClassName must not be null.");
    }
    m_portObjectClassName = builder.m_portObjectClassName;
    if(builder.m_optional == null) {
        throw new IllegalArgumentException("optional must not be null.");
    }
    m_optional = builder.m_optional;
  }


  @Override
  public String getPortObjectClassName() {
        return m_portObjectClassName;
    }
    
  @Override
  public Boolean isOptional() {
        return m_optional;
    }
    
  
    public static class DefaultPortTypeEntBuilder implements PortTypeEntBuilder {
    
        public DefaultPortTypeEntBuilder(){
            
        }
    
        private String m_portObjectClassName = null;
        private Boolean m_optional = null;

        @Override
        public DefaultPortTypeEntBuilder setPortObjectClassName(String portObjectClassName) {
             if(portObjectClassName == null) {
                 throw new IllegalArgumentException("portObjectClassName must not be null.");
             }
             m_portObjectClassName = portObjectClassName;
             return this;
        }

        @Override
        public DefaultPortTypeEntBuilder setOptional(Boolean optional) {
             if(optional == null) {
                 throw new IllegalArgumentException("optional must not be null.");
             }
             m_optional = optional;
             return this;
        }

        
        @Override
        public DefaultPortTypeEnt build() {
            return new DefaultPortTypeEnt(this);
        }
    
    }

}

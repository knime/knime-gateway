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


import org.knime.gateway.api.webui.entity.PortActionEnt;

/**
 * An action that can currently be performed on this port.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
@javax.annotation.Generated(value = {"com.knime.gateway.codegen.GatewayCodegen", "src-gen/api/web-ui/configs/org.knime.gateway.impl-config.json"})
public class DefaultPortActionEnt implements PortActionEnt {

  protected TypeEnum m_type;
  protected String m_portGroupName;
  protected java.util.List<String> m_supportedPortTypeIds;
  
  protected DefaultPortActionEnt() {
    //for sub-classes
  }
  
  @Override
  public String getTypeID() {
    return "PortAction";
  }
  
  private DefaultPortActionEnt(DefaultPortActionEntBuilder builder) {
    
    if(builder.m_type == null) {
        throw new IllegalArgumentException("type must not be null.");
    }
    m_type = immutable(builder.m_type);
    m_portGroupName = immutable(builder.m_portGroupName);
    m_supportedPortTypeIds = immutable(builder.m_supportedPortTypeIds);
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
        DefaultPortActionEnt ent = (DefaultPortActionEnt)o;
        return Objects.equals(m_type, ent.m_type) && Objects.equals(m_portGroupName, ent.m_portGroupName) && Objects.equals(m_supportedPortTypeIds, ent.m_supportedPortTypeIds);
    }


  
   /**
    * {@inheritDoc}
    */
   @Override
   public int hashCode() {
       return new HashCodeBuilder()
               .append(m_type)
               .append(m_portGroupName)
               .append(m_supportedPortTypeIds)
               .toHashCode();
   }
  
	
	
  @Override
  public TypeEnum getType() {
        return m_type;
  }
    
  @Override
  public String getPortGroupName() {
        return m_portGroupName;
  }
    
  @Override
  public java.util.List<String> getSupportedPortTypeIds() {
        return m_supportedPortTypeIds;
  }
    
  
    public static class DefaultPortActionEntBuilder implements PortActionEntBuilder {
    
        public DefaultPortActionEntBuilder(){
            
        }
    
        private TypeEnum m_type;
        private String m_portGroupName;
        private java.util.List<String> m_supportedPortTypeIds;

        @Override
        public DefaultPortActionEntBuilder setType(TypeEnum type) {
             if(type == null) {
                 throw new IllegalArgumentException("type must not be null.");
             }
             m_type = type;
             return this;
        }

        @Override
        public DefaultPortActionEntBuilder setPortGroupName(String portGroupName) {
             m_portGroupName = portGroupName;
             return this;
        }

        @Override
        public DefaultPortActionEntBuilder setSupportedPortTypeIds(java.util.List<String> supportedPortTypeIds) {
             m_supportedPortTypeIds = supportedPortTypeIds;
             return this;
        }

        
        @Override
        public DefaultPortActionEnt build() {
            return new DefaultPortActionEnt(this);
        }
    
    }

}

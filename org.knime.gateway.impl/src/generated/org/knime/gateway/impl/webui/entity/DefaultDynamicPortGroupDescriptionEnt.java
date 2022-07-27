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

import org.knime.gateway.api.webui.entity.NodePortTemplateEnt;

import org.knime.gateway.api.webui.entity.DynamicPortGroupDescriptionEnt;

/**
 * The description of a dynamic port group. A dynamic port group is a collection of dynamic ports, grouped by a common identifier, e.g. \&quot;Input\&quot; or \&quot;Output\&quot;.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
@javax.annotation.Generated(value = {"com.knime.gateway.codegen.GatewayCodegen", "src-gen/api/web-ui/configs/org.knime.gateway.impl-config.json"})
public class DefaultDynamicPortGroupDescriptionEnt implements DynamicPortGroupDescriptionEnt {

  protected String m_name;
  protected String m_identifier;
  protected String m_description;
  protected java.util.List<NodePortTemplateEnt> m_supportedPortTypes;
  
  protected DefaultDynamicPortGroupDescriptionEnt() {
    //for sub-classes
  }
  
  @Override
  public String getTypeID() {
    return "DynamicPortGroupDescription";
  }
  
  private DefaultDynamicPortGroupDescriptionEnt(DefaultDynamicPortGroupDescriptionEntBuilder builder) {
    
    if(builder.m_name == null) {
        throw new IllegalArgumentException("name must not be null.");
    }
    m_name = immutable(builder.m_name);
    if(builder.m_identifier == null) {
        throw new IllegalArgumentException("identifier must not be null.");
    }
    m_identifier = immutable(builder.m_identifier);
    m_description = immutable(builder.m_description);
    m_supportedPortTypes = immutable(builder.m_supportedPortTypes);
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
        DefaultDynamicPortGroupDescriptionEnt ent = (DefaultDynamicPortGroupDescriptionEnt)o;
        return Objects.equals(m_name, ent.m_name) && Objects.equals(m_identifier, ent.m_identifier) && Objects.equals(m_description, ent.m_description) && Objects.equals(m_supportedPortTypes, ent.m_supportedPortTypes);
    }


  
   /**
    * {@inheritDoc}
    */
   @Override
   public int hashCode() {
       return new HashCodeBuilder()
               .append(m_name)
               .append(m_identifier)
               .append(m_description)
               .append(m_supportedPortTypes)
               .toHashCode();
   }
  
	
	
  @Override
  public String getName() {
        return m_name;
  }
    
  @Override
  public String getIdentifier() {
        return m_identifier;
  }
    
  @Override
  public String getDescription() {
        return m_description;
  }
    
  @Override
  public java.util.List<NodePortTemplateEnt> getSupportedPortTypes() {
        return m_supportedPortTypes;
  }
    
  
    public static class DefaultDynamicPortGroupDescriptionEntBuilder implements DynamicPortGroupDescriptionEntBuilder {
    
        public DefaultDynamicPortGroupDescriptionEntBuilder(){
            
        }
    
        private String m_name;
        private String m_identifier;
        private String m_description;
        private java.util.List<NodePortTemplateEnt> m_supportedPortTypes;

        @Override
        public DefaultDynamicPortGroupDescriptionEntBuilder setName(String name) {
             if(name == null) {
                 throw new IllegalArgumentException("name must not be null.");
             }
             m_name = name;
             return this;
        }

        @Override
        public DefaultDynamicPortGroupDescriptionEntBuilder setIdentifier(String identifier) {
             if(identifier == null) {
                 throw new IllegalArgumentException("identifier must not be null.");
             }
             m_identifier = identifier;
             return this;
        }

        @Override
        public DefaultDynamicPortGroupDescriptionEntBuilder setDescription(String description) {
             m_description = description;
             return this;
        }

        @Override
        public DefaultDynamicPortGroupDescriptionEntBuilder setSupportedPortTypes(java.util.List<NodePortTemplateEnt> supportedPortTypes) {
             m_supportedPortTypes = supportedPortTypes;
             return this;
        }

        
        @Override
        public DefaultDynamicPortGroupDescriptionEnt build() {
            return new DefaultDynamicPortGroupDescriptionEnt(this);
        }
    
    }

}

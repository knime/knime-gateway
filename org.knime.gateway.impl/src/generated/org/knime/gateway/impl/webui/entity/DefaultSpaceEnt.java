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


import org.knime.gateway.api.webui.entity.SpaceEnt;

/**
 * Represents a single space (local workspace, hub space, ...).
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
@javax.annotation.Generated(value = {"com.knime.gateway.codegen.GatewayCodegen", "src-gen/api/web-ui/configs/org.knime.gateway.impl-config.json"})
public class DefaultSpaceEnt implements SpaceEnt {

  protected String m_id;
  protected String m_name;
  protected String m_owner;
  protected String m_description;
  protected Boolean m__private;
  
  protected DefaultSpaceEnt() {
    //for sub-classes
  }
  
  @Override
  public String getTypeID() {
    return "Space";
  }
  
  private DefaultSpaceEnt(DefaultSpaceEntBuilder builder) {
    
    if(builder.m_id == null) {
        throw new IllegalArgumentException("id must not be null.");
    }
    m_id = immutable(builder.m_id);
    if(builder.m_name == null) {
        throw new IllegalArgumentException("name must not be null.");
    }
    m_name = immutable(builder.m_name);
    if(builder.m_owner == null) {
        throw new IllegalArgumentException("owner must not be null.");
    }
    m_owner = immutable(builder.m_owner);
    m_description = immutable(builder.m_description);
    m__private = immutable(builder.m__private);
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
        DefaultSpaceEnt ent = (DefaultSpaceEnt)o;
        return Objects.equals(m_id, ent.m_id) && Objects.equals(m_name, ent.m_name) && Objects.equals(m_owner, ent.m_owner) && Objects.equals(m_description, ent.m_description) && Objects.equals(m__private, ent.m__private);
    }


  
   /**
    * {@inheritDoc}
    */
   @Override
   public int hashCode() {
       return new HashCodeBuilder()
               .append(m_id)
               .append(m_name)
               .append(m_owner)
               .append(m_description)
               .append(m__private)
               .toHashCode();
   }
  
	
	
  @Override
  public String getId() {
        return m_id;
  }
    
  @Override
  public String getName() {
        return m_name;
  }
    
  @Override
  public String getOwner() {
        return m_owner;
  }
    
  @Override
  public String getDescription() {
        return m_description;
  }
    
  @Override
  public Boolean isPrivate() {
        return m__private;
  }
    
  
    public static class DefaultSpaceEntBuilder implements SpaceEntBuilder {
    
        public DefaultSpaceEntBuilder(){
            
        }
    
        private String m_id;
        private String m_name;
        private String m_owner;
        private String m_description;
        private Boolean m__private;

        @Override
        public DefaultSpaceEntBuilder setId(String id) {
             if(id == null) {
                 throw new IllegalArgumentException("id must not be null.");
             }
             m_id = id;
             return this;
        }

        @Override
        public DefaultSpaceEntBuilder setName(String name) {
             if(name == null) {
                 throw new IllegalArgumentException("name must not be null.");
             }
             m_name = name;
             return this;
        }

        @Override
        public DefaultSpaceEntBuilder setOwner(String owner) {
             if(owner == null) {
                 throw new IllegalArgumentException("owner must not be null.");
             }
             m_owner = owner;
             return this;
        }

        @Override
        public DefaultSpaceEntBuilder setDescription(String description) {
             m_description = description;
             return this;
        }

        @Override
        public DefaultSpaceEntBuilder setPrivate(Boolean _private) {
             m__private = _private;
             return this;
        }

        
        @Override
        public DefaultSpaceEnt build() {
            return new DefaultSpaceEnt(this);
        }
    
    }

}

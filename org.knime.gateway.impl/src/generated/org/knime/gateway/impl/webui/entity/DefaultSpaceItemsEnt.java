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

import org.knime.gateway.api.webui.entity.SpaceItemEnt;

import org.knime.gateway.api.webui.entity.SpaceItemsEnt;

/**
 * A list of items on a particular level in a space
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
@javax.annotation.Generated(value = {"com.knime.gateway.codegen.GatewayCodegen", "src-gen/api/web-ui/configs/org.knime.gateway.impl-config.json"})
public class DefaultSpaceItemsEnt implements SpaceItemsEnt {

  protected java.util.List<String> m_pathIds;
  protected java.util.List<String> m_pathNames;
  protected java.util.List<SpaceItemEnt> m_items;
  
  protected DefaultSpaceItemsEnt() {
    //for sub-classes
  }
  
  @Override
  public String getTypeID() {
    return "SpaceItems";
  }
  
  private DefaultSpaceItemsEnt(DefaultSpaceItemsEntBuilder builder) {
    
    if(builder.m_pathIds == null) {
        throw new IllegalArgumentException("pathIds must not be null.");
    }
    m_pathIds = immutable(builder.m_pathIds);
    if(builder.m_pathNames == null) {
        throw new IllegalArgumentException("pathNames must not be null.");
    }
    m_pathNames = immutable(builder.m_pathNames);
    if(builder.m_items == null) {
        throw new IllegalArgumentException("items must not be null.");
    }
    m_items = immutable(builder.m_items);
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
        DefaultSpaceItemsEnt ent = (DefaultSpaceItemsEnt)o;
        return Objects.equals(m_pathIds, ent.m_pathIds) && Objects.equals(m_pathNames, ent.m_pathNames) && Objects.equals(m_items, ent.m_items);
    }


  
   /**
    * {@inheritDoc}
    */
   @Override
   public int hashCode() {
       return new HashCodeBuilder()
               .append(m_pathIds)
               .append(m_pathNames)
               .append(m_items)
               .toHashCode();
   }
  
	
	
  @Override
  public java.util.List<String> getPathIds() {
        return m_pathIds;
  }
    
  @Override
  public java.util.List<String> getPathNames() {
        return m_pathNames;
  }
    
  @Override
  public java.util.List<SpaceItemEnt> getItems() {
        return m_items;
  }
    
  
    public static class DefaultSpaceItemsEntBuilder implements SpaceItemsEntBuilder {
    
        public DefaultSpaceItemsEntBuilder(){
            
        }
    
        private java.util.List<String> m_pathIds = new java.util.ArrayList<>();
        private java.util.List<String> m_pathNames = new java.util.ArrayList<>();
        private java.util.List<SpaceItemEnt> m_items = new java.util.ArrayList<>();

        @Override
        public DefaultSpaceItemsEntBuilder setPathIds(java.util.List<String> pathIds) {
             if(pathIds == null) {
                 throw new IllegalArgumentException("pathIds must not be null.");
             }
             m_pathIds = pathIds;
             return this;
        }

        @Override
        public DefaultSpaceItemsEntBuilder setPathNames(java.util.List<String> pathNames) {
             if(pathNames == null) {
                 throw new IllegalArgumentException("pathNames must not be null.");
             }
             m_pathNames = pathNames;
             return this;
        }

        @Override
        public DefaultSpaceItemsEntBuilder setItems(java.util.List<SpaceItemEnt> items) {
             if(items == null) {
                 throw new IllegalArgumentException("items must not be null.");
             }
             m_items = items;
             return this;
        }

        
        @Override
        public DefaultSpaceItemsEnt build() {
            return new DefaultSpaceItemsEnt(this);
        }
    
    }

}

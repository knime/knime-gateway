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


import org.knime.gateway.api.webui.entity.SpaceItemReferenceEnt;

/**
 * Describes from where a workflow project originates.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
@javax.annotation.Generated(value = {"com.knime.gateway.codegen.GatewayCodegen", "src-gen/api/web-ui/configs/org.knime.gateway.impl-config.json"})
public class DefaultSpaceItemReferenceEnt implements SpaceItemReferenceEnt {

  protected String m_providerId;
  protected String m_spaceId;
  protected String m_itemId;
  protected java.util.List<String> m_ancestorItemIds;
  
  protected DefaultSpaceItemReferenceEnt() {
    //for sub-classes
  }
  
  @Override
  public String getTypeID() {
    return "SpaceItemReference";
  }
  
  private DefaultSpaceItemReferenceEnt(DefaultSpaceItemReferenceEntBuilder builder) {
    
    if(builder.m_providerId == null) {
        throw new IllegalArgumentException("providerId must not be null.");
    }
    m_providerId = immutable(builder.m_providerId);
    if(builder.m_spaceId == null) {
        throw new IllegalArgumentException("spaceId must not be null.");
    }
    m_spaceId = immutable(builder.m_spaceId);
    if(builder.m_itemId == null) {
        throw new IllegalArgumentException("itemId must not be null.");
    }
    m_itemId = immutable(builder.m_itemId);
    m_ancestorItemIds = immutable(builder.m_ancestorItemIds);
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
        DefaultSpaceItemReferenceEnt ent = (DefaultSpaceItemReferenceEnt)o;
        return Objects.equals(m_providerId, ent.m_providerId) && Objects.equals(m_spaceId, ent.m_spaceId) && Objects.equals(m_itemId, ent.m_itemId) && Objects.equals(m_ancestorItemIds, ent.m_ancestorItemIds);
    }


  
   /**
    * {@inheritDoc}
    */
   @Override
   public int hashCode() {
       return new HashCodeBuilder()
               .append(m_providerId)
               .append(m_spaceId)
               .append(m_itemId)
               .append(m_ancestorItemIds)
               .toHashCode();
   }
  
	
	
  @Override
  public String getProviderId() {
        return m_providerId;
  }
    
  @Override
  public String getSpaceId() {
        return m_spaceId;
  }
    
  @Override
  public String getItemId() {
        return m_itemId;
  }
    
  @Override
  public java.util.List<String> getAncestorItemIds() {
        return m_ancestorItemIds;
  }
    
  
    public static class DefaultSpaceItemReferenceEntBuilder implements SpaceItemReferenceEntBuilder {
    
        public DefaultSpaceItemReferenceEntBuilder(){
            
        }
    
        private String m_providerId;
        private String m_spaceId;
        private String m_itemId;
        private java.util.List<String> m_ancestorItemIds;

        @Override
        public DefaultSpaceItemReferenceEntBuilder setProviderId(String providerId) {
             if(providerId == null) {
                 throw new IllegalArgumentException("providerId must not be null.");
             }
             m_providerId = providerId;
             return this;
        }

        @Override
        public DefaultSpaceItemReferenceEntBuilder setSpaceId(String spaceId) {
             if(spaceId == null) {
                 throw new IllegalArgumentException("spaceId must not be null.");
             }
             m_spaceId = spaceId;
             return this;
        }

        @Override
        public DefaultSpaceItemReferenceEntBuilder setItemId(String itemId) {
             if(itemId == null) {
                 throw new IllegalArgumentException("itemId must not be null.");
             }
             m_itemId = itemId;
             return this;
        }

        @Override
        public DefaultSpaceItemReferenceEntBuilder setAncestorItemIds(java.util.List<String> ancestorItemIds) {
             m_ancestorItemIds = ancestorItemIds;
             return this;
        }

        
        @Override
        public DefaultSpaceItemReferenceEnt build() {
            return new DefaultSpaceItemReferenceEnt(this);
        }
    
    }

}

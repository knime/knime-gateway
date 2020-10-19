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

import java.time.OffsetDateTime;
import org.knime.gateway.api.webui.entity.LinkEnt;

import org.knime.gateway.api.webui.entity.ProjectMetadataEnt;

/**
 * The metadata for a workflow project, i.e. for the root-workflow.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
@javax.annotation.Generated(value = {"com.knime.gateway.codegen.GatewayCodegen", "src-gen/api/web-ui/configs/org.knime.gateway.impl-config.json"})
public class DefaultProjectMetadataEnt implements ProjectMetadataEnt {

  protected String m_title;
  protected String m_description;
  protected java.util.List<String> m_tags;
  protected java.util.List<LinkEnt> m_links;
  protected OffsetDateTime m_lastEdit;
  
  protected DefaultProjectMetadataEnt() {
    //for sub-classes
  }
  
  @Override
  public String getTypeID() {
    return "ProjectMetadata";
  }
  
  private DefaultProjectMetadataEnt(DefaultProjectMetadataEntBuilder builder) {
    
    m_title = immutable(builder.m_title);
    m_description = immutable(builder.m_description);
    m_tags = immutable(builder.m_tags);
    m_links = immutable(builder.m_links);
    m_lastEdit = immutable(builder.m_lastEdit);
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
        DefaultProjectMetadataEnt ent = (DefaultProjectMetadataEnt)o;
        return Objects.equals(m_title, ent.m_title) && Objects.equals(m_description, ent.m_description) && Objects.equals(m_tags, ent.m_tags) && Objects.equals(m_links, ent.m_links) && Objects.equals(m_lastEdit, ent.m_lastEdit);
    }


  
   /**
    * {@inheritDoc}
    */
   @Override
   public int hashCode() {
       return new HashCodeBuilder()
               .append(m_title)
               .append(m_description)
               .append(m_tags)
               .append(m_links)
               .append(m_lastEdit)
               .toHashCode();
   }
  
	
	
  @Override
  public String getTitle() {
        return m_title;
  }
    
  @Override
  public String getDescription() {
        return m_description;
  }
    
  @Override
  public java.util.List<String> getTags() {
        return m_tags;
  }
    
  @Override
  public java.util.List<LinkEnt> getLinks() {
        return m_links;
  }
    
  @Override
  public OffsetDateTime getLastEdit() {
        return m_lastEdit;
  }
    
  
    public static class DefaultProjectMetadataEntBuilder implements ProjectMetadataEntBuilder {
    
        public DefaultProjectMetadataEntBuilder(){
            
        }
    
        private String m_title;
        private String m_description;
        private java.util.List<String> m_tags;
        private java.util.List<LinkEnt> m_links;
        private OffsetDateTime m_lastEdit;

        @Override
        public DefaultProjectMetadataEntBuilder setTitle(String title) {
             m_title = title;
             return this;
        }

        @Override
        public DefaultProjectMetadataEntBuilder setDescription(String description) {
             m_description = description;
             return this;
        }

        @Override
        public DefaultProjectMetadataEntBuilder setTags(java.util.List<String> tags) {
             m_tags = tags;
             return this;
        }

        @Override
        public DefaultProjectMetadataEntBuilder setLinks(java.util.List<LinkEnt> links) {
             m_links = links;
             return this;
        }

        @Override
        public DefaultProjectMetadataEntBuilder setLastEdit(OffsetDateTime lastEdit) {
             m_lastEdit = lastEdit;
             return this;
        }

        
        @Override
        public DefaultProjectMetadataEnt build() {
            return new DefaultProjectMetadataEnt(this);
        }
    
    }

}

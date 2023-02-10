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

import org.knime.gateway.api.webui.entity.SpaceItemIdEnt;

import org.knime.gateway.api.webui.entity.WorkflowProjectEnt;

/**
 * Represents an entire workflow project.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
@javax.annotation.Generated(value = {"com.knime.gateway.codegen.GatewayCodegen", "src-gen/api/web-ui/configs/org.knime.gateway.impl-config.json"})
public class DefaultWorkflowProjectEnt implements WorkflowProjectEnt {

  protected String m_projectId;
  protected SpaceItemIdEnt m_origin;
  protected String m_name;
  protected org.knime.gateway.api.entity.NodeIDEnt m_activeWorkflowId;
  
  protected DefaultWorkflowProjectEnt() {
    //for sub-classes
  }
  
  @Override
  public String getTypeID() {
    return "WorkflowProject";
  }
  
  private DefaultWorkflowProjectEnt(DefaultWorkflowProjectEntBuilder builder) {
    
    if(builder.m_projectId == null) {
        throw new IllegalArgumentException("projectId must not be null.");
    }
    m_projectId = immutable(builder.m_projectId);
    if(builder.m_origin == null) {
        throw new IllegalArgumentException("origin must not be null.");
    }
    m_origin = immutable(builder.m_origin);
    if(builder.m_name == null) {
        throw new IllegalArgumentException("name must not be null.");
    }
    m_name = immutable(builder.m_name);
    m_activeWorkflowId = immutable(builder.m_activeWorkflowId);
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
        DefaultWorkflowProjectEnt ent = (DefaultWorkflowProjectEnt)o;
        return Objects.equals(m_projectId, ent.m_projectId) && Objects.equals(m_origin, ent.m_origin) && Objects.equals(m_name, ent.m_name) && Objects.equals(m_activeWorkflowId, ent.m_activeWorkflowId);
    }


  
   /**
    * {@inheritDoc}
    */
   @Override
   public int hashCode() {
       return new HashCodeBuilder()
               .append(m_projectId)
               .append(m_origin)
               .append(m_name)
               .append(m_activeWorkflowId)
               .toHashCode();
   }
  
	
	
  @Override
  public String getProjectId() {
        return m_projectId;
  }
    
  @Override
  public SpaceItemIdEnt getOrigin() {
        return m_origin;
  }
    
  @Override
  public String getName() {
        return m_name;
  }
    
  @Override
  public org.knime.gateway.api.entity.NodeIDEnt getActiveWorkflowId() {
        return m_activeWorkflowId;
  }
    
  
    public static class DefaultWorkflowProjectEntBuilder implements WorkflowProjectEntBuilder {
    
        public DefaultWorkflowProjectEntBuilder(){
            
        }
    
        private String m_projectId;
        private SpaceItemIdEnt m_origin;
        private String m_name;
        private org.knime.gateway.api.entity.NodeIDEnt m_activeWorkflowId;

        @Override
        public DefaultWorkflowProjectEntBuilder setProjectId(String projectId) {
             if(projectId == null) {
                 throw new IllegalArgumentException("projectId must not be null.");
             }
             m_projectId = projectId;
             return this;
        }

        @Override
        public DefaultWorkflowProjectEntBuilder setOrigin(SpaceItemIdEnt origin) {
             if(origin == null) {
                 throw new IllegalArgumentException("origin must not be null.");
             }
             m_origin = origin;
             return this;
        }

        @Override
        public DefaultWorkflowProjectEntBuilder setName(String name) {
             if(name == null) {
                 throw new IllegalArgumentException("name must not be null.");
             }
             m_name = name;
             return this;
        }

        @Override
        public DefaultWorkflowProjectEntBuilder setActiveWorkflowId(org.knime.gateway.api.entity.NodeIDEnt activeWorkflowId) {
             m_activeWorkflowId = activeWorkflowId;
             return this;
        }

        
        @Override
        public DefaultWorkflowProjectEnt build() {
            return new DefaultWorkflowProjectEnt(this);
        }
    
    }

}

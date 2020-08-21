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

import org.knime.gateway.impl.webui.entity.DefaultEventTypeEnt;

import org.knime.gateway.api.webui.entity.WorkflowChangedEventTypeEnt;

/**
 * Event type to register for &#39;WorkflowChangedEvent&#39;s.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
@javax.annotation.Generated(value = {"com.knime.gateway.codegen.GatewayCodegen", "src-gen/api/web-ui/configs/org.knime.gateway.impl-config.json"})
public class DefaultWorkflowChangedEventTypeEnt extends DefaultEventTypeEnt implements WorkflowChangedEventTypeEnt {

  protected String m_projectId;
  protected org.knime.gateway.api.entity.NodeIDEnt m_workflowId;
  protected java.util.UUID m_snapshotId;
  
  protected DefaultWorkflowChangedEventTypeEnt() {
    //for sub-classes
  }
  
  @Override
  public String getTypeID() {
    return "WorkflowChangedEventType";
  }
  
  private DefaultWorkflowChangedEventTypeEnt(DefaultWorkflowChangedEventTypeEntBuilder builder) {
    super();
    m_typeId = immutable(builder.m_typeId);
    if(builder.m_projectId == null) {
        throw new IllegalArgumentException("projectId must not be null.");
    }
    m_projectId = immutable(builder.m_projectId);
    if(builder.m_workflowId == null) {
        throw new IllegalArgumentException("workflowId must not be null.");
    }
    m_workflowId = immutable(builder.m_workflowId);
    if(builder.m_snapshotId == null) {
        throw new IllegalArgumentException("snapshotId must not be null.");
    }
    m_snapshotId = immutable(builder.m_snapshotId);
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
        DefaultWorkflowChangedEventTypeEnt ent = (DefaultWorkflowChangedEventTypeEnt)o;
        return Objects.equals(m_typeId, ent.m_typeId) && Objects.equals(m_projectId, ent.m_projectId) && Objects.equals(m_workflowId, ent.m_workflowId) && Objects.equals(m_snapshotId, ent.m_snapshotId);
    }


  
   /**
    * {@inheritDoc}
    */
   @Override
   public int hashCode() {
       return new HashCodeBuilder()
               .append(m_typeId)
               .append(m_projectId)
               .append(m_workflowId)
               .append(m_snapshotId)
               .toHashCode();
   }
  
	
	
  @Override
  public String getProjectId() {
        return m_projectId;
  }
    
  @Override
  public org.knime.gateway.api.entity.NodeIDEnt getWorkflowId() {
        return m_workflowId;
  }
    
  @Override
  public java.util.UUID getSnapshotId() {
        return m_snapshotId;
  }
    
  
    public static class DefaultWorkflowChangedEventTypeEntBuilder implements WorkflowChangedEventTypeEntBuilder {
    
        public DefaultWorkflowChangedEventTypeEntBuilder(){
            super();
        }
    
        private String m_typeId;
        private String m_projectId;
        private org.knime.gateway.api.entity.NodeIDEnt m_workflowId;
        private java.util.UUID m_snapshotId;

        @Override
        public DefaultWorkflowChangedEventTypeEntBuilder setTypeId(String typeId) {
             m_typeId = typeId;
             return this;
        }

        @Override
        public DefaultWorkflowChangedEventTypeEntBuilder setProjectId(String projectId) {
             if(projectId == null) {
                 throw new IllegalArgumentException("projectId must not be null.");
             }
             m_projectId = projectId;
             return this;
        }

        @Override
        public DefaultWorkflowChangedEventTypeEntBuilder setWorkflowId(org.knime.gateway.api.entity.NodeIDEnt workflowId) {
             if(workflowId == null) {
                 throw new IllegalArgumentException("workflowId must not be null.");
             }
             m_workflowId = workflowId;
             return this;
        }

        @Override
        public DefaultWorkflowChangedEventTypeEntBuilder setSnapshotId(java.util.UUID snapshotId) {
             if(snapshotId == null) {
                 throw new IllegalArgumentException("snapshotId must not be null.");
             }
             m_snapshotId = snapshotId;
             return this;
        }

        
        @Override
        public DefaultWorkflowChangedEventTypeEnt build() {
            return new DefaultWorkflowChangedEventTypeEnt(this);
        }
    
    }

}

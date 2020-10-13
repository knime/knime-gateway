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

import org.knime.gateway.api.webui.entity.PatchEnt;
import org.knime.gateway.impl.webui.entity.DefaultEventEnt;

import org.knime.gateway.api.webui.entity.WorkflowChangedEventEnt;

/**
 * Event for all kind of workflow changes.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
@javax.annotation.Generated(value = {"com.knime.gateway.codegen.GatewayCodegen", "src-gen/api/web-ui/configs/org.knime.gateway.impl-config.json"})
public class DefaultWorkflowChangedEventEnt extends DefaultEventEnt implements WorkflowChangedEventEnt {

  protected String m_snapshotId;
  protected String m_previousSnapshotId;
  protected PatchEnt m_patch;
  
  protected DefaultWorkflowChangedEventEnt() {
    //for sub-classes
  }
  
  @Override
  public String getTypeID() {
    return "WorkflowChangedEvent";
  }
  
  private DefaultWorkflowChangedEventEnt(DefaultWorkflowChangedEventEntBuilder builder) {
    super();
    if(builder.m_snapshotId == null) {
        throw new IllegalArgumentException("snapshotId must not be null.");
    }
    m_snapshotId = immutable(builder.m_snapshotId);
    if(builder.m_previousSnapshotId == null) {
        throw new IllegalArgumentException("previousSnapshotId must not be null.");
    }
    m_previousSnapshotId = immutable(builder.m_previousSnapshotId);
    if(builder.m_patch == null) {
        throw new IllegalArgumentException("patch must not be null.");
    }
    m_patch = immutable(builder.m_patch);
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
        DefaultWorkflowChangedEventEnt ent = (DefaultWorkflowChangedEventEnt)o;
        return Objects.equals(m_snapshotId, ent.m_snapshotId) && Objects.equals(m_previousSnapshotId, ent.m_previousSnapshotId) && Objects.equals(m_patch, ent.m_patch);
    }


  
   /**
    * {@inheritDoc}
    */
   @Override
   public int hashCode() {
       return new HashCodeBuilder()
               .append(m_snapshotId)
               .append(m_previousSnapshotId)
               .append(m_patch)
               .toHashCode();
   }
  
	
	
  @Override
  public String getSnapshotId() {
        return m_snapshotId;
  }
    
  @Override
  public String getPreviousSnapshotId() {
        return m_previousSnapshotId;
  }
    
  @Override
  public PatchEnt getPatch() {
        return m_patch;
  }
    
  
    public static class DefaultWorkflowChangedEventEntBuilder implements WorkflowChangedEventEntBuilder {
    
        public DefaultWorkflowChangedEventEntBuilder(){
            super();
        }
    
        private String m_snapshotId;
        private String m_previousSnapshotId;
        private PatchEnt m_patch;

        @Override
        public DefaultWorkflowChangedEventEntBuilder setSnapshotId(String snapshotId) {
             if(snapshotId == null) {
                 throw new IllegalArgumentException("snapshotId must not be null.");
             }
             m_snapshotId = snapshotId;
             return this;
        }

        @Override
        public DefaultWorkflowChangedEventEntBuilder setPreviousSnapshotId(String previousSnapshotId) {
             if(previousSnapshotId == null) {
                 throw new IllegalArgumentException("previousSnapshotId must not be null.");
             }
             m_previousSnapshotId = previousSnapshotId;
             return this;
        }

        @Override
        public DefaultWorkflowChangedEventEntBuilder setPatch(PatchEnt patch) {
             if(patch == null) {
                 throw new IllegalArgumentException("patch must not be null.");
             }
             m_patch = patch;
             return this;
        }

        
        @Override
        public DefaultWorkflowChangedEventEnt build() {
            return new DefaultWorkflowChangedEventEnt(this);
        }
    
    }

}

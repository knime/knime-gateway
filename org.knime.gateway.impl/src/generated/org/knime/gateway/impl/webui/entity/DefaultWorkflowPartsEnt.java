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


import org.knime.gateway.api.webui.entity.WorkflowPartsEnt;

/**
 * Represents parts of a workflow referenced by their ids.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
@javax.annotation.Generated(value = {"com.knime.gateway.codegen.GatewayCodegen", "src-gen/api/web-ui/configs/org.knime.gateway.impl-config.json"})
public class DefaultWorkflowPartsEnt implements WorkflowPartsEnt {

  protected java.util.List<org.knime.gateway.api.entity.NodeIDEnt> m_nodeIDs;
  protected java.util.List<org.knime.gateway.api.entity.AnnotationIDEnt> m_annotationIDs;
  
  protected DefaultWorkflowPartsEnt() {
    //for sub-classes
  }
  
  @Override
  public String getTypeID() {
    return "WorkflowParts";
  }
  
  private DefaultWorkflowPartsEnt(DefaultWorkflowPartsEntBuilder builder) {
    
    if(builder.m_nodeIDs == null) {
        throw new IllegalArgumentException("nodeIDs must not be null.");
    }
    m_nodeIDs = immutable(builder.m_nodeIDs);
    if(builder.m_annotationIDs == null) {
        throw new IllegalArgumentException("annotationIDs must not be null.");
    }
    m_annotationIDs = immutable(builder.m_annotationIDs);
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
        DefaultWorkflowPartsEnt ent = (DefaultWorkflowPartsEnt)o;
        return Objects.equals(m_nodeIDs, ent.m_nodeIDs) && Objects.equals(m_annotationIDs, ent.m_annotationIDs);
    }


  
   /**
    * {@inheritDoc}
    */
   @Override
   public int hashCode() {
       return new HashCodeBuilder()
               .append(m_nodeIDs)
               .append(m_annotationIDs)
               .toHashCode();
   }
  
	
	
  @Override
  public java.util.List<org.knime.gateway.api.entity.NodeIDEnt> getNodeIDs() {
        return m_nodeIDs;
  }
    
  @Override
  public java.util.List<org.knime.gateway.api.entity.AnnotationIDEnt> getAnnotationIDs() {
        return m_annotationIDs;
  }
    
  
    public static class DefaultWorkflowPartsEntBuilder implements WorkflowPartsEntBuilder {
    
        public DefaultWorkflowPartsEntBuilder(){
            
        }
    
        private java.util.List<org.knime.gateway.api.entity.NodeIDEnt> m_nodeIDs = new java.util.ArrayList<>();
        private java.util.List<org.knime.gateway.api.entity.AnnotationIDEnt> m_annotationIDs = new java.util.ArrayList<>();

        @Override
        public DefaultWorkflowPartsEntBuilder setNodeIDs(java.util.List<org.knime.gateway.api.entity.NodeIDEnt> nodeIDs) {
             if(nodeIDs == null) {
                 throw new IllegalArgumentException("nodeIDs must not be null.");
             }
             m_nodeIDs = nodeIDs;
             return this;
        }

        @Override
        public DefaultWorkflowPartsEntBuilder setAnnotationIDs(java.util.List<org.knime.gateway.api.entity.AnnotationIDEnt> annotationIDs) {
             if(annotationIDs == null) {
                 throw new IllegalArgumentException("annotationIDs must not be null.");
             }
             m_annotationIDs = annotationIDs;
             return this;
        }

        
        @Override
        public DefaultWorkflowPartsEnt build() {
            return new DefaultWorkflowPartsEnt(this);
        }
    
    }

}

/* ------------------------------------------------------------------
 * This source code, its documentation and all appendant files
 * are protected by copyright law. All rights reserved.
 *
 * Copyright by KNIME AG, Zurich, Switzerland
 *
 * You may not modify, publish, transmit, transfer or sell, reproduce,
 * create derivative works from, distribute, perform, display, or in
 * any way exploit any of the content, in whole or in part, except as
 * otherwise expressly permitted in writing by the copyright owner or
 * as specified in the license file distributed with this product.
 *
 * If you have any questions please contact the copyright holder:
 * website: www.knime.com
 * email: contact@knime.com
 * ---------------------------------------------------------------------
 *
 */
package com.knime.gateway.impl.entity;

import static org.knime.gateway.api.util.EntityUtil.immutable;

import java.util.Objects;


import org.knime.gateway.api.entity.WorkflowPartsEnt;

/**
 * Represents a selection of parts of a workflow (i.e collections of nodes, connection, annotations etc.), e.g. to be copied or deleted.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
@javax.annotation.Generated(value = {"com.knime.gateway.codegen.GatewayCodegen", "src-gen/api/java-ui/configs/com.knime.gateway.impl-config.json"})
public class DefaultWorkflowPartsEnt  implements WorkflowPartsEnt {

  protected java.util.List<org.knime.gateway.api.entity.NodeIDEnt> m_nodeIDs;
  protected java.util.List<org.knime.gateway.api.entity.ConnectionIDEnt> m_connectionIDs;
  protected java.util.List<org.knime.gateway.api.entity.AnnotationIDEnt> m_annotationIDs;
  
  protected DefaultWorkflowPartsEnt() {
    //for sub-classes
  }
  
  @Override
  public String getTypeID() {
    return "WorkflowParts";
  }
  
  private DefaultWorkflowPartsEnt(DefaultWorkflowPartsEntBuilder builder) {
    
    m_nodeIDs = immutable(builder.m_nodeIDs);
    m_connectionIDs = immutable(builder.m_connectionIDs);
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
        return Objects.equals(m_nodeIDs, ent.m_nodeIDs) && Objects.equals(m_connectionIDs, ent.m_connectionIDs) && Objects.equals(m_annotationIDs, ent.m_annotationIDs);
    }


  @Override
  public java.util.List<org.knime.gateway.api.entity.NodeIDEnt> getNodeIDs() {
        return m_nodeIDs;
    }
    
  @Override
  public java.util.List<org.knime.gateway.api.entity.ConnectionIDEnt> getConnectionIDs() {
        return m_connectionIDs;
    }
    
  @Override
  public java.util.List<org.knime.gateway.api.entity.AnnotationIDEnt> getAnnotationIDs() {
        return m_annotationIDs;
    }
    
  
    public static class DefaultWorkflowPartsEntBuilder implements WorkflowPartsEntBuilder {
    
        public DefaultWorkflowPartsEntBuilder(){
            
        }
    
        private java.util.List<org.knime.gateway.api.entity.NodeIDEnt> m_nodeIDs = new java.util.ArrayList<>();
        private java.util.List<org.knime.gateway.api.entity.ConnectionIDEnt> m_connectionIDs = new java.util.ArrayList<>();
        private java.util.List<org.knime.gateway.api.entity.AnnotationIDEnt> m_annotationIDs = new java.util.ArrayList<>();

        @Override
        public DefaultWorkflowPartsEntBuilder setNodeIDs(java.util.List<org.knime.gateway.api.entity.NodeIDEnt> nodeIDs) {
             m_nodeIDs = nodeIDs;
             return this;
        }

        @Override
        public DefaultWorkflowPartsEntBuilder setConnectionIDs(java.util.List<org.knime.gateway.api.entity.ConnectionIDEnt> connectionIDs) {
             m_connectionIDs = connectionIDs;
             return this;
        }

        @Override
        public DefaultWorkflowPartsEntBuilder setAnnotationIDs(java.util.List<org.knime.gateway.api.entity.AnnotationIDEnt> annotationIDs) {
             m_annotationIDs = annotationIDs;
             return this;
        }

        
        @Override
        public DefaultWorkflowPartsEnt build() {
            return new DefaultWorkflowPartsEnt(this);
        }
    
    }

}

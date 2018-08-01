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
package com.knime.gateway.v0.entity.impl;

import static com.knime.gateway.util.EntityUtil.immutable;

import java.util.Objects;


import com.knime.gateway.v0.entity.WorkflowPartsEnt;

/**
 * Represents a selection of parts of a workflow (i.e collections of nodes, connection, annotations etc.), e.g. to be copied or deleted.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
@javax.annotation.Generated(value = "org.knime.gateway.codegen.GatewayCodegen")
public class DefaultWorkflowPartsEnt  implements WorkflowPartsEnt {

  protected String m_parentNodeID;
  protected java.util.List<String> m_nodeIDs;
  protected java.util.List<String> m_connectionIDs;
  protected java.util.List<String> m_annotationIDs;
  
  protected DefaultWorkflowPartsEnt() {
    //for sub-classes
  }
  
  @Override
  public String getTypeID() {
    return "WorkflowParts";
  }
  
  private DefaultWorkflowPartsEnt(DefaultWorkflowPartsEntBuilder builder) {
    
    if(builder.m_parentNodeID == null) {
        throw new IllegalArgumentException("parentNodeID must not be null.");
    }
    m_parentNodeID = immutable(builder.m_parentNodeID);
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
        return Objects.equals(m_parentNodeID, ent.m_parentNodeID) && Objects.equals(m_nodeIDs, ent.m_nodeIDs) && Objects.equals(m_connectionIDs, ent.m_connectionIDs) && Objects.equals(m_annotationIDs, ent.m_annotationIDs);
    }


  @Override
  public String getParentNodeID() {
        return m_parentNodeID;
    }
    
  @Override
  public java.util.List<String> getNodeIDs() {
        return m_nodeIDs;
    }
    
  @Override
  public java.util.List<String> getConnectionIDs() {
        return m_connectionIDs;
    }
    
  @Override
  public java.util.List<String> getAnnotationIDs() {
        return m_annotationIDs;
    }
    
  
    public static class DefaultWorkflowPartsEntBuilder implements WorkflowPartsEntBuilder {
    
        public DefaultWorkflowPartsEntBuilder(){
            
        }
    
        private String m_parentNodeID = null;
        private java.util.List<String> m_nodeIDs = new java.util.ArrayList<>();
        private java.util.List<String> m_connectionIDs = new java.util.ArrayList<>();
        private java.util.List<String> m_annotationIDs = new java.util.ArrayList<>();

        @Override
        public DefaultWorkflowPartsEntBuilder setParentNodeID(String parentNodeID) {
             if(parentNodeID == null) {
                 throw new IllegalArgumentException("parentNodeID must not be null.");
             }
             m_parentNodeID = parentNodeID;
             return this;
        }

        @Override
        public DefaultWorkflowPartsEntBuilder setNodeIDs(java.util.List<String> nodeIDs) {
             m_nodeIDs = nodeIDs;
             return this;
        }

        @Override
        public DefaultWorkflowPartsEntBuilder setConnectionIDs(java.util.List<String> connectionIDs) {
             m_connectionIDs = connectionIDs;
             return this;
        }

        @Override
        public DefaultWorkflowPartsEntBuilder setAnnotationIDs(java.util.List<String> annotationIDs) {
             m_annotationIDs = annotationIDs;
             return this;
        }

        
        @Override
        public DefaultWorkflowPartsEnt build() {
            return new DefaultWorkflowPartsEnt(this);
        }
    
    }

}

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

import static com.knime.gateway.util.DefaultEntUtil.immutable;

import com.knime.gateway.v0.entity.WorkflowEnt;

import com.knime.gateway.v0.entity.WorkflowSnapshotEnt;

/**
 * A workflow with an additional snapshot id.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
@javax.annotation.Generated(value = "org.knime.gateway.codegen.GatewayCodegen")
public class DefaultWorkflowSnapshotEnt  implements WorkflowSnapshotEnt {

  protected WorkflowEnt m_workflow;
  protected java.util.UUID m_snapshotID;
  
  protected DefaultWorkflowSnapshotEnt() {
    //for sub-classes
  }
  
  @Override
  public String getTypeID() {
    return "WorkflowSnapshot";
  }
  
  private DefaultWorkflowSnapshotEnt(DefaultWorkflowSnapshotEntBuilder builder) {
    
    if(builder.m_workflow == null) {
        throw new IllegalArgumentException("workflow must not be null.");
    }
    m_workflow = immutable(builder.m_workflow);
    if(builder.m_snapshotID == null) {
        throw new IllegalArgumentException("snapshotID must not be null.");
    }
    m_snapshotID = immutable(builder.m_snapshotID);
  }


  @Override
  public WorkflowEnt getWorkflow() {
        return m_workflow;
    }
    
  @Override
  public java.util.UUID getSnapshotID() {
        return m_snapshotID;
    }
    
  
    public static class DefaultWorkflowSnapshotEntBuilder implements WorkflowSnapshotEntBuilder {
    
        public DefaultWorkflowSnapshotEntBuilder(){
            
        }
    
        private WorkflowEnt m_workflow;
        private java.util.UUID m_snapshotID = null;

        @Override
        public DefaultWorkflowSnapshotEntBuilder setWorkflow(WorkflowEnt workflow) {
             if(workflow == null) {
                 throw new IllegalArgumentException("workflow must not be null.");
             }
             m_workflow = workflow;
             return this;
        }

        @Override
        public DefaultWorkflowSnapshotEntBuilder setSnapshotID(java.util.UUID snapshotID) {
             if(snapshotID == null) {
                 throw new IllegalArgumentException("snapshotID must not be null.");
             }
             m_snapshotID = snapshotID;
             return this;
        }

        
        @Override
        public DefaultWorkflowSnapshotEnt build() {
            return new DefaultWorkflowSnapshotEnt(this);
        }
    
    }

}

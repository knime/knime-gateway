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
package org.knime.gateway.impl.webui.entity;

import static org.knime.gateway.api.util.EntityUtil.immutable;

import java.util.Objects;


import org.knime.gateway.api.webui.entity.WorkflowEnt;

/**
 * The structure of a workflow.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
@javax.annotation.Generated(value = {"com.knime.gateway.codegen.GatewayCodegen", "src-gen/api/web-ui/configs/org.knime.gateway.impl-config.json"})
public class DefaultWorkflowEnt  implements WorkflowEnt {

  protected String m_test;
  
  protected DefaultWorkflowEnt() {
    //for sub-classes
  }
  
  @Override
  public String getTypeID() {
    return "Workflow";
  }
  
  private DefaultWorkflowEnt(DefaultWorkflowEntBuilder builder) {
    
    m_test = immutable(builder.m_test);
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
        DefaultWorkflowEnt ent = (DefaultWorkflowEnt)o;
        return Objects.equals(m_test, ent.m_test);
    }


  @Override
  public String getTest() {
        return m_test;
    }
    
  
    public static class DefaultWorkflowEntBuilder implements WorkflowEntBuilder {
    
        public DefaultWorkflowEntBuilder(){
            
        }
    
        private String m_test;

        @Override
        public DefaultWorkflowEntBuilder setTest(String test) {
             m_test = test;
             return this;
        }

        
        @Override
        public DefaultWorkflowEnt build() {
            return new DefaultWorkflowEnt(this);
        }
    
    }

}

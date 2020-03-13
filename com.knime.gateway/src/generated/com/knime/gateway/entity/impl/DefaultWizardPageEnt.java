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
package com.knime.gateway.entity.impl;

import static com.knime.gateway.util.EntityUtil.immutable;

import java.util.Objects;

import com.knime.gateway.entity.NodeMessageEnt;

import com.knime.gateway.entity.WizardPageEnt;

/**
 * Wizard page as returned, e.g., by the next-page and current-page endpoints. 
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
@javax.annotation.Generated(value = {"org.knime.gateway.codegen.GatewayCodegen", "src-gen/com.knime.gateway-implementations-config.json"})
public class DefaultWizardPageEnt  implements WizardPageEnt {

  protected Object m_wizardPageContent;
  protected WizardExecutionStateEnum m_wizardExecutionState;
  protected java.util.Map<String, NodeMessageEnt> m_nodeMessages;
  protected Boolean m_hasPreviousPage;
  protected Boolean m_hasReport;
  
  protected DefaultWizardPageEnt() {
    //for sub-classes
  }
  
  @Override
  public String getTypeID() {
    return "WizardPage";
  }
  
  private DefaultWizardPageEnt(DefaultWizardPageEntBuilder builder) {
    
    m_wizardPageContent = immutable(builder.m_wizardPageContent);
    m_wizardExecutionState = immutable(builder.m_wizardExecutionState);
    m_nodeMessages = immutable(builder.m_nodeMessages);
    m_hasPreviousPage = immutable(builder.m_hasPreviousPage);
    m_hasReport = immutable(builder.m_hasReport);
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
        DefaultWizardPageEnt ent = (DefaultWizardPageEnt)o;
        return Objects.equals(m_wizardPageContent, ent.m_wizardPageContent) && Objects.equals(m_wizardExecutionState, ent.m_wizardExecutionState) && Objects.equals(m_nodeMessages, ent.m_nodeMessages) && Objects.equals(m_hasPreviousPage, ent.m_hasPreviousPage) && Objects.equals(m_hasReport, ent.m_hasReport);
    }


  @Override
  public Object getWizardPageContent() {
        return m_wizardPageContent;
    }
    
  @Override
  public WizardExecutionStateEnum getWizardExecutionState() {
        return m_wizardExecutionState;
    }
    
  @Override
  public java.util.Map<String, NodeMessageEnt> getNodeMessages() {
        return m_nodeMessages;
    }
    
  @Override
  public Boolean hasPreviousPage() {
        return m_hasPreviousPage;
    }
    
  @Override
  public Boolean hasReport() {
        return m_hasReport;
    }
    
  
    public static class DefaultWizardPageEntBuilder implements WizardPageEntBuilder {
    
        public DefaultWizardPageEntBuilder(){
            
        }
    
        private Object m_wizardPageContent = null;
        private WizardExecutionStateEnum m_wizardExecutionState;
        private java.util.Map<String, NodeMessageEnt> m_nodeMessages = new java.util.HashMap<>();
        private Boolean m_hasPreviousPage;
        private Boolean m_hasReport;

        @Override
        public DefaultWizardPageEntBuilder setWizardPageContent(Object wizardPageContent) {
             m_wizardPageContent = wizardPageContent;
             return this;
        }

        @Override
        public DefaultWizardPageEntBuilder setWizardExecutionState(WizardExecutionStateEnum wizardExecutionState) {
             m_wizardExecutionState = wizardExecutionState;
             return this;
        }

        @Override
        public DefaultWizardPageEntBuilder setNodeMessages(java.util.Map<String, NodeMessageEnt> nodeMessages) {
             m_nodeMessages = nodeMessages;
             return this;
        }

        @Override
        public DefaultWizardPageEntBuilder setHasPreviousPage(Boolean hasPreviousPage) {
             m_hasPreviousPage = hasPreviousPage;
             return this;
        }

        @Override
        public DefaultWizardPageEntBuilder setHasReport(Boolean hasReport) {
             m_hasReport = hasReport;
             return this;
        }

        
        @Override
        public DefaultWizardPageEnt build() {
            return new DefaultWizardPageEnt(this);
        }
    
    }

}

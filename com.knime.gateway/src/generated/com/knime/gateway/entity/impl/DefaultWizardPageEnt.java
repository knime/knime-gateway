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

  protected String m_wizardPageContent;
  protected WizardExecutionStateEnum m_wizardExecutionState;
  protected java.util.Map<String, NodeMessageEnt> m_nodeMessages;
  protected Boolean m_hasPreviousPage;
  
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
        return Objects.equals(m_wizardPageContent, ent.m_wizardPageContent) && Objects.equals(m_wizardExecutionState, ent.m_wizardExecutionState) && Objects.equals(m_nodeMessages, ent.m_nodeMessages) && Objects.equals(m_hasPreviousPage, ent.m_hasPreviousPage);
    }


  @Override
  public String getWizardPageContent() {
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
    
  
    public static class DefaultWizardPageEntBuilder implements WizardPageEntBuilder {
    
        public DefaultWizardPageEntBuilder(){
            
        }
    
        private String m_wizardPageContent;
        private WizardExecutionStateEnum m_wizardExecutionState;
        private java.util.Map<String, NodeMessageEnt> m_nodeMessages = new java.util.HashMap<>();
        private Boolean m_hasPreviousPage;

        @Override
        public DefaultWizardPageEntBuilder setWizardPageContent(String wizardPageContent) {
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
        public DefaultWizardPageEnt build() {
            return new DefaultWizardPageEnt(this);
        }
    
    }

}

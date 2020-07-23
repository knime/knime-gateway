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
package org.knime.gateway.impl.entity;

import static org.knime.gateway.api.util.EntityUtil.immutable;

import java.util.Objects;

import org.knime.gateway.api.entity.NodeMessageEnt;

import org.knime.gateway.api.entity.WizardPageEnt;

/**
 * Wizard page as returned, e.g., by the next-page and current-page endpoints. 
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
@javax.annotation.Generated(value = {"com.knime.gateway.codegen.GatewayCodegen", "src-gen/org.knime.gateway.impl-config.json"})
public class DefaultWizardPageEnt  implements WizardPageEnt {

  protected Object m_wizardPageContent;
  protected WizardExecutionStateEnum m_wizardExecutionState;
  protected java.util.Map<String, NodeMessageEnt> m_nodeMessages;
  protected Boolean m_hasPreviousPage;
  protected Boolean m_hasNextPage;
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
    m_hasNextPage = immutable(builder.m_hasNextPage);
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
        return Objects.equals(m_wizardPageContent, ent.m_wizardPageContent) && Objects.equals(m_wizardExecutionState, ent.m_wizardExecutionState) && Objects.equals(m_nodeMessages, ent.m_nodeMessages) && Objects.equals(m_hasPreviousPage, ent.m_hasPreviousPage) && Objects.equals(m_hasNextPage, ent.m_hasNextPage) && Objects.equals(m_hasReport, ent.m_hasReport);
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
  public Boolean hasNextPage() {
        return m_hasNextPage;
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
        private Boolean m_hasNextPage;
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
        public DefaultWizardPageEntBuilder setHasNextPage(Boolean hasNextPage) {
             m_hasNextPage = hasNextPage;
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

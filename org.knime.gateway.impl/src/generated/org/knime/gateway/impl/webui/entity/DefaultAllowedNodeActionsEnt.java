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

import org.knime.gateway.impl.webui.entity.DefaultAllowedActionsEnt;

import org.knime.gateway.api.webui.entity.AllowedNodeActionsEnt;

/**
 * Set of actions allowed specific to nodes.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
@javax.annotation.Generated(value = {"com.knime.gateway.codegen.GatewayCodegen", "src-gen/api/web-ui/configs/org.knime.gateway.impl-config.json"})
public class DefaultAllowedNodeActionsEnt implements AllowedNodeActionsEnt {

  protected Boolean m_canExecute;
  protected Boolean m_canCancel;
  protected Boolean m_canReset;
  protected Boolean m_canOpenDialog;
  protected Boolean m_canOpenLegacyFlowVariableDialog;
  protected Boolean m_canOpenView;
  protected Boolean m_canDelete;
  
  protected DefaultAllowedNodeActionsEnt() {
    //for sub-classes
  }
  
  @Override
  public String getTypeID() {
    return "AllowedNodeActions";
  }
  
  private DefaultAllowedNodeActionsEnt(DefaultAllowedNodeActionsEntBuilder builder) {
    super();
    if(builder.m_canExecute == null) {
        throw new IllegalArgumentException("canExecute must not be null.");
    }
    m_canExecute = immutable(builder.m_canExecute);
    if(builder.m_canCancel == null) {
        throw new IllegalArgumentException("canCancel must not be null.");
    }
    m_canCancel = immutable(builder.m_canCancel);
    if(builder.m_canReset == null) {
        throw new IllegalArgumentException("canReset must not be null.");
    }
    m_canReset = immutable(builder.m_canReset);
    m_canOpenDialog = immutable(builder.m_canOpenDialog);
    m_canOpenLegacyFlowVariableDialog = immutable(builder.m_canOpenLegacyFlowVariableDialog);
    m_canOpenView = immutable(builder.m_canOpenView);
    m_canDelete = immutable(builder.m_canDelete);
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
        DefaultAllowedNodeActionsEnt ent = (DefaultAllowedNodeActionsEnt)o;
        return Objects.equals(m_canExecute, ent.m_canExecute) && Objects.equals(m_canCancel, ent.m_canCancel) && Objects.equals(m_canReset, ent.m_canReset) && Objects.equals(m_canOpenDialog, ent.m_canOpenDialog) && Objects.equals(m_canOpenLegacyFlowVariableDialog, ent.m_canOpenLegacyFlowVariableDialog) && Objects.equals(m_canOpenView, ent.m_canOpenView) && Objects.equals(m_canDelete, ent.m_canDelete);
    }


  
   /**
    * {@inheritDoc}
    */
   @Override
   public int hashCode() {
       return new HashCodeBuilder()
               .append(m_canExecute)
               .append(m_canCancel)
               .append(m_canReset)
               .append(m_canOpenDialog)
               .append(m_canOpenLegacyFlowVariableDialog)
               .append(m_canOpenView)
               .append(m_canDelete)
               .toHashCode();
   }
  
	
	
  @Override
  public Boolean isCanExecute() {
        return m_canExecute;
  }
    
  @Override
  public Boolean isCanCancel() {
        return m_canCancel;
  }
    
  @Override
  public Boolean isCanReset() {
        return m_canReset;
  }
    
  @Override
  public Boolean isCanOpenDialog() {
        return m_canOpenDialog;
  }
    
  @Override
  public Boolean isCanOpenLegacyFlowVariableDialog() {
        return m_canOpenLegacyFlowVariableDialog;
  }
    
  @Override
  public Boolean isCanOpenView() {
        return m_canOpenView;
  }
    
  @Override
  public Boolean isCanDelete() {
        return m_canDelete;
  }
    
  
    public static class DefaultAllowedNodeActionsEntBuilder implements AllowedNodeActionsEntBuilder {
    
        public DefaultAllowedNodeActionsEntBuilder(){
            super();
        }
    
        private Boolean m_canExecute;
        private Boolean m_canCancel;
        private Boolean m_canReset;
        private Boolean m_canOpenDialog;
        private Boolean m_canOpenLegacyFlowVariableDialog;
        private Boolean m_canOpenView;
        private Boolean m_canDelete;

        @Override
        public DefaultAllowedNodeActionsEntBuilder setCanExecute(Boolean canExecute) {
             if(canExecute == null) {
                 throw new IllegalArgumentException("canExecute must not be null.");
             }
             m_canExecute = canExecute;
             return this;
        }

        @Override
        public DefaultAllowedNodeActionsEntBuilder setCanCancel(Boolean canCancel) {
             if(canCancel == null) {
                 throw new IllegalArgumentException("canCancel must not be null.");
             }
             m_canCancel = canCancel;
             return this;
        }

        @Override
        public DefaultAllowedNodeActionsEntBuilder setCanReset(Boolean canReset) {
             if(canReset == null) {
                 throw new IllegalArgumentException("canReset must not be null.");
             }
             m_canReset = canReset;
             return this;
        }

        @Override
        public DefaultAllowedNodeActionsEntBuilder setCanOpenDialog(Boolean canOpenDialog) {
             m_canOpenDialog = canOpenDialog;
             return this;
        }

        @Override
        public DefaultAllowedNodeActionsEntBuilder setCanOpenLegacyFlowVariableDialog(Boolean canOpenLegacyFlowVariableDialog) {
             m_canOpenLegacyFlowVariableDialog = canOpenLegacyFlowVariableDialog;
             return this;
        }

        @Override
        public DefaultAllowedNodeActionsEntBuilder setCanOpenView(Boolean canOpenView) {
             m_canOpenView = canOpenView;
             return this;
        }

        @Override
        public DefaultAllowedNodeActionsEntBuilder setCanDelete(Boolean canDelete) {
             m_canDelete = canDelete;
             return this;
        }

        
        @Override
        public DefaultAllowedNodeActionsEnt build() {
            return new DefaultAllowedNodeActionsEnt(this);
        }
    
    }

}

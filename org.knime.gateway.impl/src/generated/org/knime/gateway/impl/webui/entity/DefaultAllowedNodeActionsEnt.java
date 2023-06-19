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

import org.knime.gateway.impl.webui.entity.DefaultAllowedActionsEnt;

import org.knime.gateway.api.webui.entity.AllowedNodeActionsEnt;

/**
 * Set of actions allowed specific to nodes.
 *
 * @param canExecute
 * @param canCancel
 * @param canReset
 * @param canOpenDialog
 * @param canOpenLegacyFlowVariableDialog
 * @param canOpenView
 * @param canDelete
 * @param canExpand
 * @param canCollapse
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
@jakarta.annotation.Generated(value = {"com.knime.gateway.codegen.GatewayCodegen", "src-gen/api/web-ui/configs/org.knime.gateway.impl-config.json"})
public record DefaultAllowedNodeActionsEnt(
    Boolean canExecute,
    Boolean canCancel,
    Boolean canReset,
    Boolean canOpenDialog,
    Boolean canOpenLegacyFlowVariableDialog,
    Boolean canOpenView,
    Boolean canDelete,
    CanExpandEnum canExpand,
    CanCollapseEnum canCollapse) implements AllowedNodeActionsEnt {

    /**
     * Validation for required parameters not being {@code null}.
     */
    public DefaultAllowedNodeActionsEnt {
        if(canExecute == null) {
            throw new IllegalArgumentException("<canExecute> must not be null.");
        }
        if(canCancel == null) {
            throw new IllegalArgumentException("<canCancel> must not be null.");
        }
        if(canReset == null) {
            throw new IllegalArgumentException("<canReset> must not be null.");
        }
    }

    @Override
    public String getTypeID() {
        return "AllowedNodeActions";
    }
  
    @Override
    public Boolean isCanExecute() {
        return canExecute;
    }
    
    @Override
    public Boolean isCanCancel() {
        return canCancel;
    }
    
    @Override
    public Boolean isCanReset() {
        return canReset;
    }
    
    @Override
    public Boolean isCanOpenDialog() {
        return canOpenDialog;
    }
    
    @Override
    public Boolean isCanOpenLegacyFlowVariableDialog() {
        return canOpenLegacyFlowVariableDialog;
    }
    
    @Override
    public Boolean isCanOpenView() {
        return canOpenView;
    }
    
    @Override
    public Boolean isCanDelete() {
        return canDelete;
    }
    
    @Override
    public CanExpandEnum getCanExpand() {
        return canExpand;
    }
    
    @Override
    public CanCollapseEnum getCanCollapse() {
        return canCollapse;
    }
    
    /**
     * A builder for {@link DefaultAllowedNodeActionsEnt}.
     */
    public static class DefaultAllowedNodeActionsEntBuilder implements AllowedNodeActionsEntBuilder {

        private Boolean m_canExecute;

        private Boolean m_canCancel;

        private Boolean m_canReset;

        private Boolean m_canOpenDialog;

        private Boolean m_canOpenLegacyFlowVariableDialog;

        private Boolean m_canOpenView;

        private Boolean m_canDelete;

        private CanExpandEnum m_canExpand;

        private CanCollapseEnum m_canCollapse;

        @Override
        public DefaultAllowedNodeActionsEntBuilder setCanExecute(Boolean canExecute) {
             if(canExecute == null) {
                 throw new IllegalArgumentException("<canExecute> must not be null.");
             }
             m_canExecute = canExecute;
             return this;
        }

        @Override
        public DefaultAllowedNodeActionsEntBuilder setCanCancel(Boolean canCancel) {
             if(canCancel == null) {
                 throw new IllegalArgumentException("<canCancel> must not be null.");
             }
             m_canCancel = canCancel;
             return this;
        }

        @Override
        public DefaultAllowedNodeActionsEntBuilder setCanReset(Boolean canReset) {
             if(canReset == null) {
                 throw new IllegalArgumentException("<canReset> must not be null.");
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
        public DefaultAllowedNodeActionsEntBuilder setCanExpand(CanExpandEnum canExpand) {
             m_canExpand = canExpand;
             return this;
        }

        @Override
        public DefaultAllowedNodeActionsEntBuilder setCanCollapse(CanCollapseEnum canCollapse) {
             m_canCollapse = canCollapse;
             return this;
        }

        @Override
        public DefaultAllowedNodeActionsEnt build() {
            return new DefaultAllowedNodeActionsEnt(
                immutable(m_canExecute),
                immutable(m_canCancel),
                immutable(m_canReset),
                immutable(m_canOpenDialog),
                immutable(m_canOpenLegacyFlowVariableDialog),
                immutable(m_canOpenView),
                immutable(m_canDelete),
                immutable(m_canExpand),
                immutable(m_canCollapse));
        }
    
    }

}

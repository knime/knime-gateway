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


import org.knime.gateway.api.webui.entity.PermissionsEnt;

/**
 * DefaultPermissionsEnt
 *
 * @param canConfigureNodes
 * @param canEditWorkflow
 * @param canAccessNodeRepository
 * @param canAccessKAIPanel
 * @param canAccessSpaceExplorer
 * @param showRemoteWorkflowInfo
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
@jakarta.annotation.Generated(value = {"com.knime.gateway.codegen.GatewayCodegen", "src-gen/api/web-ui/configs/org.knime.gateway.impl-config.json"})
public record DefaultPermissionsEnt(
    Boolean canConfigureNodes,
    Boolean canEditWorkflow,
    Boolean canAccessNodeRepository,
    Boolean canAccessKAIPanel,
    Boolean canAccessSpaceExplorer,
    Boolean showRemoteWorkflowInfo) implements PermissionsEnt {

    /**
     * Validation for required parameters not being {@code null}.
     */
    public DefaultPermissionsEnt {
        if(canConfigureNodes == null) {
            throw new IllegalArgumentException("<canConfigureNodes> must not be null.");
        }
        if(canEditWorkflow == null) {
            throw new IllegalArgumentException("<canEditWorkflow> must not be null.");
        }
        if(canAccessNodeRepository == null) {
            throw new IllegalArgumentException("<canAccessNodeRepository> must not be null.");
        }
        if(canAccessKAIPanel == null) {
            throw new IllegalArgumentException("<canAccessKAIPanel> must not be null.");
        }
        if(canAccessSpaceExplorer == null) {
            throw new IllegalArgumentException("<canAccessSpaceExplorer> must not be null.");
        }
        if(showRemoteWorkflowInfo == null) {
            throw new IllegalArgumentException("<showRemoteWorkflowInfo> must not be null.");
        }
    }

    @Override
    public String getTypeID() {
        return "Permissions";
    }
  
    @Override
    public Boolean isCanConfigureNodes() {
        return canConfigureNodes;
    }
    
    @Override
    public Boolean isCanEditWorkflow() {
        return canEditWorkflow;
    }
    
    @Override
    public Boolean isCanAccessNodeRepository() {
        return canAccessNodeRepository;
    }
    
    @Override
    public Boolean isCanAccessKAIPanel() {
        return canAccessKAIPanel;
    }
    
    @Override
    public Boolean isCanAccessSpaceExplorer() {
        return canAccessSpaceExplorer;
    }
    
    @Override
    public Boolean isShowRemoteWorkflowInfo() {
        return showRemoteWorkflowInfo;
    }
    
    /**
     * A builder for {@link DefaultPermissionsEnt}.
     */
    public static class DefaultPermissionsEntBuilder implements PermissionsEntBuilder {

        private Boolean m_canConfigureNodes;

        private Boolean m_canEditWorkflow;

        private Boolean m_canAccessNodeRepository;

        private Boolean m_canAccessKAIPanel;

        private Boolean m_canAccessSpaceExplorer;

        private Boolean m_showRemoteWorkflowInfo;

        @Override
        public DefaultPermissionsEntBuilder setCanConfigureNodes(Boolean canConfigureNodes) {
             if(canConfigureNodes == null) {
                 throw new IllegalArgumentException("<canConfigureNodes> must not be null.");
             }
             m_canConfigureNodes = canConfigureNodes;
             return this;
        }

        @Override
        public DefaultPermissionsEntBuilder setCanEditWorkflow(Boolean canEditWorkflow) {
             if(canEditWorkflow == null) {
                 throw new IllegalArgumentException("<canEditWorkflow> must not be null.");
             }
             m_canEditWorkflow = canEditWorkflow;
             return this;
        }

        @Override
        public DefaultPermissionsEntBuilder setCanAccessNodeRepository(Boolean canAccessNodeRepository) {
             if(canAccessNodeRepository == null) {
                 throw new IllegalArgumentException("<canAccessNodeRepository> must not be null.");
             }
             m_canAccessNodeRepository = canAccessNodeRepository;
             return this;
        }

        @Override
        public DefaultPermissionsEntBuilder setCanAccessKAIPanel(Boolean canAccessKAIPanel) {
             if(canAccessKAIPanel == null) {
                 throw new IllegalArgumentException("<canAccessKAIPanel> must not be null.");
             }
             m_canAccessKAIPanel = canAccessKAIPanel;
             return this;
        }

        @Override
        public DefaultPermissionsEntBuilder setCanAccessSpaceExplorer(Boolean canAccessSpaceExplorer) {
             if(canAccessSpaceExplorer == null) {
                 throw new IllegalArgumentException("<canAccessSpaceExplorer> must not be null.");
             }
             m_canAccessSpaceExplorer = canAccessSpaceExplorer;
             return this;
        }

        @Override
        public DefaultPermissionsEntBuilder setShowRemoteWorkflowInfo(Boolean showRemoteWorkflowInfo) {
             if(showRemoteWorkflowInfo == null) {
                 throw new IllegalArgumentException("<showRemoteWorkflowInfo> must not be null.");
             }
             m_showRemoteWorkflowInfo = showRemoteWorkflowInfo;
             return this;
        }

        @Override
        public DefaultPermissionsEnt build() {
            return new DefaultPermissionsEnt(
                immutable(m_canConfigureNodes),
                immutable(m_canEditWorkflow),
                immutable(m_canAccessNodeRepository),
                immutable(m_canAccessKAIPanel),
                immutable(m_canAccessSpaceExplorer),
                immutable(m_showRemoteWorkflowInfo));
        }
    
    }

}

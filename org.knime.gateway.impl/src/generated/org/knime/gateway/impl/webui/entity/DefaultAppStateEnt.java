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
import org.knime.gateway.api.webui.entity.PortTypeEnt;
import org.knime.gateway.api.webui.entity.ProjectEnt;

import org.knime.gateway.api.webui.entity.AppStateEnt;

/**
 * Represents the global application state.
 *
 * @param openProjects
 * @param availablePortTypes
 * @param suggestedPortTypeIds
 * @param availableComponentTypes
 * @param hasNodeRecommendationsEnabled
 * @param featureFlags
 * @param permissions
 * @param scrollToZoomEnabled
 * @param hasNodeCollectionActive
 * @param activeNodeCollection
 * @param confirmNodeConfigChanges
 * @param devMode
 * @param fileExtensionToNodeTemplateId
 * @param nodeRepositoryLoaded
 * @param analyticsPlatformDownloadURL
 * @param isSubnodeLockingEnabled
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
@jakarta.annotation.Generated(value = {"com.knime.gateway.codegen.GatewayCodegen", "src-gen/api/web-ui/configs/org.knime.gateway.impl-config.json"})
public record DefaultAppStateEnt(
    java.util.List<ProjectEnt> openProjects,
    java.util.Map<String, PortTypeEnt> availablePortTypes,
    java.util.List<String> suggestedPortTypeIds,
    java.util.List<String> availableComponentTypes,
    Boolean hasNodeRecommendationsEnabled,
    java.util.Map<String, Object> featureFlags,
    PermissionsEnt permissions,
    Boolean scrollToZoomEnabled,
    Boolean hasNodeCollectionActive,
    String activeNodeCollection,
    Boolean confirmNodeConfigChanges,
    Boolean devMode,
    java.util.Map<String, String> fileExtensionToNodeTemplateId,
    Boolean nodeRepositoryLoaded,
    String analyticsPlatformDownloadURL,
    Boolean isSubnodeLockingEnabled) implements AppStateEnt {

    /**
     * Validation for required parameters not being {@code null}.
     */
    public DefaultAppStateEnt {
    }

    @Override
    public String getTypeID() {
        return "AppState";
    }
  
    @Override
    public java.util.List<ProjectEnt> getOpenProjects() {
        return openProjects;
    }
    
    @Override
    public java.util.Map<String, PortTypeEnt> getAvailablePortTypes() {
        return availablePortTypes;
    }
    
    @Override
    public java.util.List<String> getSuggestedPortTypeIds() {
        return suggestedPortTypeIds;
    }
    
    @Override
    public java.util.List<String> getAvailableComponentTypes() {
        return availableComponentTypes;
    }
    
    @Override
    public Boolean hasNodeRecommendationsEnabled() {
        return hasNodeRecommendationsEnabled;
    }
    
    @Override
    public java.util.Map<String, Object> getFeatureFlags() {
        return featureFlags;
    }
    
    @Override
    public PermissionsEnt getPermissions() {
        return permissions;
    }
    
    @Override
    public Boolean isScrollToZoomEnabled() {
        return scrollToZoomEnabled;
    }
    
    @Override
    public Boolean hasNodeCollectionActive() {
        return hasNodeCollectionActive;
    }
    
    @Override
    public String getActiveNodeCollection() {
        return activeNodeCollection;
    }
    
    @Override
    public Boolean isConfirmNodeConfigChanges() {
        return confirmNodeConfigChanges;
    }
    
    @Override
    public Boolean isDevMode() {
        return devMode;
    }
    
    @Override
    public java.util.Map<String, String> getFileExtensionToNodeTemplateId() {
        return fileExtensionToNodeTemplateId;
    }
    
    @Override
    public Boolean isNodeRepositoryLoaded() {
        return nodeRepositoryLoaded;
    }
    
    @Override
    public String getAnalyticsPlatformDownloadURL() {
        return analyticsPlatformDownloadURL;
    }
    
    @Override
    public Boolean isSubnodeLockingEnabled() {
        return isSubnodeLockingEnabled;
    }
    
    /**
     * A builder for {@link DefaultAppStateEnt}.
     */
    public static class DefaultAppStateEntBuilder implements AppStateEntBuilder {

        private java.util.List<ProjectEnt> m_openProjects;

        private java.util.Map<String, PortTypeEnt> m_availablePortTypes;

        private java.util.List<String> m_suggestedPortTypeIds;

        private java.util.List<String> m_availableComponentTypes;

        private Boolean m_hasNodeRecommendationsEnabled;

        private java.util.Map<String, Object> m_featureFlags;

        private PermissionsEnt m_permissions;

        private Boolean m_scrollToZoomEnabled;

        private Boolean m_hasNodeCollectionActive;

        private String m_activeNodeCollection;

        private Boolean m_confirmNodeConfigChanges;

        private Boolean m_devMode;

        private java.util.Map<String, String> m_fileExtensionToNodeTemplateId;

        private Boolean m_nodeRepositoryLoaded;

        private String m_analyticsPlatformDownloadURL;

        private Boolean m_isSubnodeLockingEnabled;

        @Override
        public DefaultAppStateEntBuilder setOpenProjects(java.util.List<ProjectEnt> openProjects) {
             m_openProjects = openProjects;
             return this;
        }

        @Override
        public DefaultAppStateEntBuilder setAvailablePortTypes(java.util.Map<String, PortTypeEnt> availablePortTypes) {
             m_availablePortTypes = availablePortTypes;
             return this;
        }

        @Override
        public DefaultAppStateEntBuilder setSuggestedPortTypeIds(java.util.List<String> suggestedPortTypeIds) {
             m_suggestedPortTypeIds = suggestedPortTypeIds;
             return this;
        }

        @Override
        public DefaultAppStateEntBuilder setAvailableComponentTypes(java.util.List<String> availableComponentTypes) {
             m_availableComponentTypes = availableComponentTypes;
             return this;
        }

        @Override
        public DefaultAppStateEntBuilder setHasNodeRecommendationsEnabled(Boolean hasNodeRecommendationsEnabled) {
             m_hasNodeRecommendationsEnabled = hasNodeRecommendationsEnabled;
             return this;
        }

        @Override
        public DefaultAppStateEntBuilder setFeatureFlags(java.util.Map<String, Object> featureFlags) {
             m_featureFlags = featureFlags;
             return this;
        }

        @Override
        public DefaultAppStateEntBuilder setPermissions(PermissionsEnt permissions) {
             m_permissions = permissions;
             return this;
        }

        @Override
        public DefaultAppStateEntBuilder setScrollToZoomEnabled(Boolean scrollToZoomEnabled) {
             m_scrollToZoomEnabled = scrollToZoomEnabled;
             return this;
        }

        @Override
        public DefaultAppStateEntBuilder setHasNodeCollectionActive(Boolean hasNodeCollectionActive) {
             m_hasNodeCollectionActive = hasNodeCollectionActive;
             return this;
        }

        @Override
        public DefaultAppStateEntBuilder setActiveNodeCollection(String activeNodeCollection) {
             m_activeNodeCollection = activeNodeCollection;
             return this;
        }

        @Override
        public DefaultAppStateEntBuilder setConfirmNodeConfigChanges(Boolean confirmNodeConfigChanges) {
             m_confirmNodeConfigChanges = confirmNodeConfigChanges;
             return this;
        }

        @Override
        public DefaultAppStateEntBuilder setDevMode(Boolean devMode) {
             m_devMode = devMode;
             return this;
        }

        @Override
        public DefaultAppStateEntBuilder setFileExtensionToNodeTemplateId(java.util.Map<String, String> fileExtensionToNodeTemplateId) {
             m_fileExtensionToNodeTemplateId = fileExtensionToNodeTemplateId;
             return this;
        }

        @Override
        public DefaultAppStateEntBuilder setNodeRepositoryLoaded(Boolean nodeRepositoryLoaded) {
             m_nodeRepositoryLoaded = nodeRepositoryLoaded;
             return this;
        }

        @Override
        public DefaultAppStateEntBuilder setAnalyticsPlatformDownloadURL(String analyticsPlatformDownloadURL) {
             m_analyticsPlatformDownloadURL = analyticsPlatformDownloadURL;
             return this;
        }

        @Override
        public DefaultAppStateEntBuilder setIsSubnodeLockingEnabled(Boolean isSubnodeLockingEnabled) {
             m_isSubnodeLockingEnabled = isSubnodeLockingEnabled;
             return this;
        }

        @Override
        public DefaultAppStateEnt build() {
            return new DefaultAppStateEnt(
                immutable(m_openProjects),
                immutable(m_availablePortTypes),
                immutable(m_suggestedPortTypeIds),
                immutable(m_availableComponentTypes),
                immutable(m_hasNodeRecommendationsEnabled),
                immutable(m_featureFlags),
                immutable(m_permissions),
                immutable(m_scrollToZoomEnabled),
                immutable(m_hasNodeCollectionActive),
                immutable(m_activeNodeCollection),
                immutable(m_confirmNodeConfigChanges),
                immutable(m_devMode),
                immutable(m_fileExtensionToNodeTemplateId),
                immutable(m_nodeRepositoryLoaded),
                immutable(m_analyticsPlatformDownloadURL),
                immutable(m_isSubnodeLockingEnabled));
        }
    
    }

}

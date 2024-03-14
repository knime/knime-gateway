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
package org.knime.gateway.json.webui.entity;

import org.knime.gateway.api.webui.entity.ExampleProjectEnt;
import org.knime.gateway.api.webui.entity.PermissionsEnt;
import org.knime.gateway.api.webui.entity.PortTypeEnt;
import org.knime.gateway.api.webui.entity.ProjectEnt;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import org.knime.gateway.api.webui.entity.AppStateEnt;
import org.knime.gateway.impl.webui.entity.DefaultAppStateEnt.DefaultAppStateEntBuilder;

/**
 * MixIn class for entity implementations that adds jackson annotations for de-/serialization.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */

@JsonDeserialize(builder=DefaultAppStateEntBuilder.class)
@JsonSerialize(as=AppStateEnt.class)
@jakarta.annotation.Generated(value = {"com.knime.gateway.codegen.GatewayCodegen", "src-gen/api/web-ui/configs/org.knime.gateway.json-config.json"})
public interface AppStateEntMixIn extends AppStateEnt {

    @Override
    @JsonIgnore
    public String getTypeID();

    @Override
    @JsonProperty("openProjects")
    public java.util.List<ProjectEnt> getOpenProjects();
    
    @Override
    @JsonProperty("exampleProjects")
    public java.util.List<ExampleProjectEnt> getExampleProjects();
    
    @Override
    @JsonProperty("availablePortTypes")
    public java.util.Map<String, PortTypeEnt> getAvailablePortTypes();
    
    @Override
    @JsonProperty("suggestedPortTypeIds")
    public java.util.List<String> getSuggestedPortTypeIds();
    
    @Override
    @JsonProperty("availableComponentTypes")
    public java.util.List<String> getAvailableComponentTypes();
    
    @Override
    @JsonProperty("hasNodeRecommendationsEnabled")
    public Boolean hasNodeRecommendationsEnabled();
    
    @Override
    @JsonProperty("featureFlags")
    public java.util.Map<String, Object> getFeatureFlags();
    
    @Override
    @JsonProperty("permissions")
    public PermissionsEnt getPermissions();
    
    @Override
    @JsonProperty("scrollToZoomEnabled")
    public Boolean isScrollToZoomEnabled();
    
    @Override
    @JsonProperty("hasNodeCollectionActive")
    public Boolean hasNodeCollectionActive();
    
    @Override
    @JsonProperty("activeNodeCollection")
    public String getActiveNodeCollection();
    
    @Override
    @JsonProperty("devMode")
    public Boolean isDevMode();
    
    @Override
    @JsonProperty("fileExtensionToNodeTemplateId")
    public java.util.Map<String, String> getFileExtensionToNodeTemplateId();
    
    @Override
    @JsonProperty("nodeRepositoryLoaded")
    public Boolean isNodeRepositoryLoaded();
    
    @Override
    @JsonProperty("analyticsPlatformDownloadURL")
    public String getAnalyticsPlatformDownloadURL();
    

    /**
     * MixIn class for entity builder implementations that adds jackson annotations for the de-/serialization.
     *
     * @author Martin Horn, University of Konstanz
     */

    // AUTO-GENERATED CODE; DO NOT MODIFY
    public static interface AppStateEntMixInBuilder extends AppStateEntBuilder {
    
        @Override
        public AppStateEntMixIn build();
    
        @Override
        @JsonProperty("openProjects")
        public AppStateEntMixInBuilder setOpenProjects(final java.util.List<ProjectEnt> openProjects);
        
        @Override
        @JsonProperty("exampleProjects")
        public AppStateEntMixInBuilder setExampleProjects(final java.util.List<ExampleProjectEnt> exampleProjects);
        
        @Override
        @JsonProperty("availablePortTypes")
        public AppStateEntMixInBuilder setAvailablePortTypes(final java.util.Map<String, PortTypeEnt> availablePortTypes);
        
        @Override
        @JsonProperty("suggestedPortTypeIds")
        public AppStateEntMixInBuilder setSuggestedPortTypeIds(final java.util.List<String> suggestedPortTypeIds);
        
        @Override
        @JsonProperty("availableComponentTypes")
        public AppStateEntMixInBuilder setAvailableComponentTypes(final java.util.List<String> availableComponentTypes);
        
        @Override
        @JsonProperty("hasNodeRecommendationsEnabled")
        public AppStateEntMixInBuilder setHasNodeRecommendationsEnabled(final Boolean hasNodeRecommendationsEnabled);
        
        @Override
        @JsonProperty("featureFlags")
        public AppStateEntMixInBuilder setFeatureFlags(final java.util.Map<String, Object> featureFlags);
        
        @Override
        @JsonProperty("permissions")
        public AppStateEntMixInBuilder setPermissions(final PermissionsEnt permissions);
        
        @Override
        @JsonProperty("scrollToZoomEnabled")
        public AppStateEntMixInBuilder setScrollToZoomEnabled(final Boolean scrollToZoomEnabled);
        
        @Override
        @JsonProperty("hasNodeCollectionActive")
        public AppStateEntMixInBuilder setHasNodeCollectionActive(final Boolean hasNodeCollectionActive);
        
        @Override
        @JsonProperty("activeNodeCollection")
        public AppStateEntMixInBuilder setActiveNodeCollection(final String activeNodeCollection);
        
        @Override
        @JsonProperty("devMode")
        public AppStateEntMixInBuilder setDevMode(final Boolean devMode);
        
        @Override
        @JsonProperty("fileExtensionToNodeTemplateId")
        public AppStateEntMixInBuilder setFileExtensionToNodeTemplateId(final java.util.Map<String, String> fileExtensionToNodeTemplateId);
        
        @Override
        @JsonProperty("nodeRepositoryLoaded")
        public AppStateEntMixInBuilder setNodeRepositoryLoaded(final Boolean nodeRepositoryLoaded);
        
        @Override
        @JsonProperty("analyticsPlatformDownloadURL")
        public AppStateEntMixInBuilder setAnalyticsPlatformDownloadURL(final String analyticsPlatformDownloadURL);
        
    }


}


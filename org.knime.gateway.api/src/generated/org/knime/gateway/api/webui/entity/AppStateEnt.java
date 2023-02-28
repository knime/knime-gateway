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
package org.knime.gateway.api.webui.entity;

import org.knime.gateway.api.webui.entity.ExampleProjectEnt;
import org.knime.gateway.api.webui.entity.PortTypeEnt;
import org.knime.gateway.api.webui.entity.WorkflowProjectEnt;

import java.util.function.BiConsumer;

import org.knime.core.util.Pair;

import org.knime.gateway.api.entity.GatewayEntityBuilder;


import org.knime.gateway.api.entity.GatewayEntity;

/**
 * Represents the global application state.
 * 
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
@javax.annotation.Generated(value = {"com.knime.gateway.codegen.GatewayCodegen", "src-gen/api/web-ui/configs/org.knime.gateway.api-config.json"})
public interface AppStateEnt extends GatewayEntity {


  /**
   * List of all opened workflow projects.
   * @return openProjects 
   **/
  public java.util.List<WorkflowProjectEnt> getOpenProjects();

  /**
   * List of example projects, e.g., to be shown on and opened from the &#39;get started&#39; page.
   * @return exampleProjects 
   **/
  public java.util.List<ExampleProjectEnt> getExampleProjects();

  /**
   * All port types available in this installation. Map from port type ID to port type entity.
   * @return availablePortTypes 
   **/
  public java.util.Map<String, PortTypeEnt> getAvailablePortTypes();

  /**
   * When the user is prompted to select a port type, this subset of types may be used as suggestions.
   * @return suggestedPortTypeIds 
   **/
  public java.util.List<String> getSuggestedPortTypeIds();

  /**
   * If true, node recommendation features can be used, otherwise they have to be disabled.
   * @return hasNodeRecommendationsEnabled 
   **/
  public Boolean hasNodeRecommendationsEnabled();

  /**
   * Maps a feature-flag key to a feature-flag value (usually, but not necessarily, a boolean). The feature-flags are usually specified/controlled through jvm system properties.
   * @return featureFlags 
   **/
  public java.util.Map<String, Object> getFeatureFlags();

  /**
   * If true, scrolling in the workflow canvas will be interpreted as zooming
   * @return scrollToZoomEnabled 
   **/
  public Boolean isScrollToZoomEnabled();

  /**
   * If true, a node collection is configured on the preference page. The node search will show the nodes of the collection first and the category groups and node recommendations will only show nodes from the collection.
   * @return hasNodeCollectionActive 
   **/
  public Boolean hasNodeCollectionActive();

  /**
   * If true, dev mode specific buttons will be shown.
   * @return devMode 
   **/
  public Boolean isDevMode();

  /**
   * Get fileExtensionToNodeTemplateId
   * @return fileExtensionToNodeTemplateId 
   **/
  public java.util.Map<String, String> getFileExtensionToNodeTemplateId();


  @Override
  default void forEachPropertyValue(final GatewayEntity other,
      final BiConsumer<String, Pair<Object, Object>> valueConsumer) {
      var e = (AppStateEnt)other;
      valueConsumer.accept("openProjects", Pair.create(getOpenProjects(), e.getOpenProjects()));
      valueConsumer.accept("exampleProjects", Pair.create(getExampleProjects(), e.getExampleProjects()));
      valueConsumer.accept("availablePortTypes", Pair.create(getAvailablePortTypes(), e.getAvailablePortTypes()));
      valueConsumer.accept("suggestedPortTypeIds", Pair.create(getSuggestedPortTypeIds(), e.getSuggestedPortTypeIds()));
      valueConsumer.accept("hasNodeRecommendationsEnabled", Pair.create(hasNodeRecommendationsEnabled(), e.hasNodeRecommendationsEnabled()));
      valueConsumer.accept("featureFlags", Pair.create(getFeatureFlags(), e.getFeatureFlags()));
      valueConsumer.accept("scrollToZoomEnabled", Pair.create(isScrollToZoomEnabled(), e.isScrollToZoomEnabled()));
      valueConsumer.accept("hasNodeCollectionActive", Pair.create(hasNodeCollectionActive(), e.hasNodeCollectionActive()));
      valueConsumer.accept("devMode", Pair.create(isDevMode(), e.isDevMode()));
  }

    /**
     * The builder for the entity.
     */
    public interface AppStateEntBuilder extends GatewayEntityBuilder<AppStateEnt> {

        /**
         * List of all opened workflow projects.
         * 
         * @param openProjects the property value,  
         * @return this entity builder for chaining
         */
        AppStateEntBuilder setOpenProjects(java.util.List<WorkflowProjectEnt> openProjects);
        
        /**
         * List of example projects, e.g., to be shown on and opened from the &#39;get started&#39; page.
         * 
         * @param exampleProjects the property value,  
         * @return this entity builder for chaining
         */
        AppStateEntBuilder setExampleProjects(java.util.List<ExampleProjectEnt> exampleProjects);
        
        /**
         * All port types available in this installation. Map from port type ID to port type entity.
         * 
         * @param availablePortTypes the property value,  
         * @return this entity builder for chaining
         */
        AppStateEntBuilder setAvailablePortTypes(java.util.Map<String, PortTypeEnt> availablePortTypes);
        
        /**
         * When the user is prompted to select a port type, this subset of types may be used as suggestions.
         * 
         * @param suggestedPortTypeIds the property value,  
         * @return this entity builder for chaining
         */
        AppStateEntBuilder setSuggestedPortTypeIds(java.util.List<String> suggestedPortTypeIds);
        
        /**
         * If true, node recommendation features can be used, otherwise they have to be disabled.
         * 
         * @param hasNodeRecommendationsEnabled the property value,  
         * @return this entity builder for chaining
         */
        AppStateEntBuilder setHasNodeRecommendationsEnabled(Boolean hasNodeRecommendationsEnabled);
        
        /**
         * Maps a feature-flag key to a feature-flag value (usually, but not necessarily, a boolean). The feature-flags are usually specified/controlled through jvm system properties.
         * 
         * @param featureFlags the property value,  
         * @return this entity builder for chaining
         */
        AppStateEntBuilder setFeatureFlags(java.util.Map<String, Object> featureFlags);
        
        /**
         * If true, scrolling in the workflow canvas will be interpreted as zooming
         * 
         * @param scrollToZoomEnabled the property value,  
         * @return this entity builder for chaining
         */
        AppStateEntBuilder setScrollToZoomEnabled(Boolean scrollToZoomEnabled);
        
        /**
         * If true, a node collection is configured on the preference page. The node search will show the nodes of the collection first and the category groups and node recommendations will only show nodes from the collection.
         * 
         * @param hasNodeCollectionActive the property value,  
         * @return this entity builder for chaining
         */
        AppStateEntBuilder setHasNodeCollectionActive(Boolean hasNodeCollectionActive);
        
        /**
         * If true, dev mode specific buttons will be shown.
         * 
         * @param devMode the property value,  
         * @return this entity builder for chaining
         */
        AppStateEntBuilder setDevMode(Boolean devMode);
        
        /**
   		 * Set fileExtensionToNodeTemplateId
         * 
         * @param fileExtensionToNodeTemplateId the property value,  
         * @return this entity builder for chaining
         */
        AppStateEntBuilder setFileExtensionToNodeTemplateId(java.util.Map<String, String> fileExtensionToNodeTemplateId);
        
        
        /**
        * Creates the entity from the builder.
        * 
        * @return the entity
        * @throws IllegalArgumentException most likely in case when a required property hasn't been set
        */
        @Override
        AppStateEnt build();
    
    }

}

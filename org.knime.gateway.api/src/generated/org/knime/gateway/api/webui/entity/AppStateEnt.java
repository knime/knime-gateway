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

import org.knime.gateway.api.webui.entity.PortTypeEnt;
import org.knime.gateway.api.webui.entity.ProjectEnt;
import org.knime.gateway.api.webui.entity.SpaceProviderEnt;

import java.util.function.BiConsumer;

import org.knime.core.util.Pair;

import org.knime.gateway.api.entity.GatewayEntityBuilder;


import org.knime.gateway.api.entity.GatewayEntity;

/**
 * Represents the global application state.
 * 
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
@jakarta.annotation.Generated(value = {"com.knime.gateway.codegen.GatewayCodegen", "src-gen/api/web-ui/configs/org.knime.gateway.api-config.json"})
public interface AppStateEnt extends GatewayEntity {

  /**
   * The general mode the app is initialized with.  Control various aspects of the app  (ui elements being hidden/shown, whether a workflow can be edited, ...)
   */
  public enum AppModeEnum {
    DEFAULT("default"),
    
    JOB_VIEWER("job-viewer"),
    
    PLAYGROUND("playground");

    private String value;

    AppModeEnum(String value) {
      this.value = value;
    }

    @Override
    public String toString() {
      return String.valueOf(value);
    }

  }

  /**
   * Specify which renderer to use.
   */
  public enum CanvasRendererEnum {
    WEBGL("WebGL"),
    
    SVG("SVG");

    private String value;

    CanvasRendererEnum(String value) {
      this.value = value;
    }

    @Override
    public String toString() {
      return String.valueOf(value);
    }

  }


  /**
   * The general mode the app is initialized with.  Control various aspects of the app  (ui elements being hidden/shown, whether a workflow can be edited, ...)
   * @return appMode 
   **/
  public AppModeEnum getAppMode();

  /**
   * List of all opened workflow projects.
   * @return openProjects 
   **/
  public java.util.List<ProjectEnt> getOpenProjects();

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
   * Available component node types.
   * @return availableComponentTypes 
   **/
  public java.util.List<String> getAvailableComponentTypes();

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
   * Whether the application should use embedded dialogs or detached dialogs.
   * @return useEmbeddedDialogs 
   **/
  public Boolean isUseEmbeddedDialogs();

  /**
   * Specify which renderer to use.
   * @return canvasRenderer 
   **/
  public CanvasRendererEnum getCanvasRenderer();

  /**
   * Whether all K-AI-related features (chat sidebar, build mode, scripting assistance, etc.) are enabled.
   * @return isKaiEnabled 
   **/
  public Boolean isKaiEnabled();

  /**
   * Display name of currently active node collection. Compatible with \&quot;Search in {activeNodeCollection} nodes\&quot;.
   * @return activeNodeCollection 
   **/
  public String getActiveNodeCollection();

  /**
   * Whether to always confirm node config changes or apply them automatically when de-selecting a node.
   * @return confirmNodeConfigChanges 
   **/
  public Boolean isConfirmNodeConfigChanges();

  /**
   * If true, dev mode specific buttons will be shown.
   * @return devMode 
   **/
  public Boolean isDevMode();

  /**
   * Mapping from file extension (e.g. \&quot;csv\&quot;) to the template ID of a node that can process such files
   * @return fileExtensionToNodeTemplateId 
   **/
  public java.util.Map<String, String> getFileExtensionToNodeTemplateId();

  /**
   * Whether the node repository is loaded (and ready to be used) or not.
   * @return nodeRepositoryLoaded 
   **/
  public Boolean isNodeRepositoryLoaded();

  /**
   * Web URL to send the user to to download the desktop edition of the Analytics Platform.
   * @return analyticsPlatformDownloadURL 
   **/
  public String getAnalyticsPlatformDownloadURL();

  /**
   * Whether to enable the locking of metanodes and components
   * @return isSubnodeLockingEnabled 
   **/
  public Boolean isSubnodeLockingEnabled();

  /**
   * A list of all available space providers.
   * @return spaceProviders 
   **/
  public java.util.List<SpaceProviderEnt> getSpaceProviders();


  @Override
  default void forEachPropertyValue(final GatewayEntity other,
      final BiConsumer<String, Pair<Object, Object>> valueConsumer) {
      var e = (AppStateEnt)other;
      valueConsumer.accept("appMode", Pair.create(getAppMode(), e.getAppMode()));
      valueConsumer.accept("openProjects", Pair.create(getOpenProjects(), e.getOpenProjects()));
      valueConsumer.accept("availablePortTypes", Pair.create(getAvailablePortTypes(), e.getAvailablePortTypes()));
      valueConsumer.accept("suggestedPortTypeIds", Pair.create(getSuggestedPortTypeIds(), e.getSuggestedPortTypeIds()));
      valueConsumer.accept("availableComponentTypes", Pair.create(getAvailableComponentTypes(), e.getAvailableComponentTypes()));
      valueConsumer.accept("hasNodeRecommendationsEnabled", Pair.create(hasNodeRecommendationsEnabled(), e.hasNodeRecommendationsEnabled()));
      valueConsumer.accept("featureFlags", Pair.create(getFeatureFlags(), e.getFeatureFlags()));
      valueConsumer.accept("scrollToZoomEnabled", Pair.create(isScrollToZoomEnabled(), e.isScrollToZoomEnabled()));
      valueConsumer.accept("hasNodeCollectionActive", Pair.create(hasNodeCollectionActive(), e.hasNodeCollectionActive()));
      valueConsumer.accept("useEmbeddedDialogs", Pair.create(isUseEmbeddedDialogs(), e.isUseEmbeddedDialogs()));
      valueConsumer.accept("canvasRenderer", Pair.create(getCanvasRenderer(), e.getCanvasRenderer()));
      valueConsumer.accept("isKaiEnabled", Pair.create(isKaiEnabled(), e.isKaiEnabled()));
      valueConsumer.accept("activeNodeCollection", Pair.create(getActiveNodeCollection(), e.getActiveNodeCollection()));
      valueConsumer.accept("confirmNodeConfigChanges", Pair.create(isConfirmNodeConfigChanges(), e.isConfirmNodeConfigChanges()));
      valueConsumer.accept("devMode", Pair.create(isDevMode(), e.isDevMode()));
      valueConsumer.accept("fileExtensionToNodeTemplateId", Pair.create(getFileExtensionToNodeTemplateId(), e.getFileExtensionToNodeTemplateId()));
      valueConsumer.accept("nodeRepositoryLoaded", Pair.create(isNodeRepositoryLoaded(), e.isNodeRepositoryLoaded()));
      valueConsumer.accept("analyticsPlatformDownloadURL", Pair.create(getAnalyticsPlatformDownloadURL(), e.getAnalyticsPlatformDownloadURL()));
      valueConsumer.accept("isSubnodeLockingEnabled", Pair.create(isSubnodeLockingEnabled(), e.isSubnodeLockingEnabled()));
      valueConsumer.accept("spaceProviders", Pair.create(getSpaceProviders(), e.getSpaceProviders()));
  }

    /**
     * The builder for the entity.
     */
    public interface AppStateEntBuilder extends GatewayEntityBuilder<AppStateEnt> {

        /**
         * The general mode the app is initialized with.  Control various aspects of the app  (ui elements being hidden/shown, whether a workflow can be edited, ...)
         * 
         * @param appMode the property value,  
         * @return this entity builder for chaining
         */
        AppStateEntBuilder setAppMode(AppModeEnum appMode);
        
        /**
         * List of all opened workflow projects.
         * 
         * @param openProjects the property value,  
         * @return this entity builder for chaining
         */
        AppStateEntBuilder setOpenProjects(java.util.List<ProjectEnt> openProjects);
        
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
         * Available component node types.
         * 
         * @param availableComponentTypes the property value,  
         * @return this entity builder for chaining
         */
        AppStateEntBuilder setAvailableComponentTypes(java.util.List<String> availableComponentTypes);
        
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
         * Whether the application should use embedded dialogs or detached dialogs.
         * 
         * @param useEmbeddedDialogs the property value,  
         * @return this entity builder for chaining
         */
        AppStateEntBuilder setUseEmbeddedDialogs(Boolean useEmbeddedDialogs);
        
        /**
         * Specify which renderer to use.
         * 
         * @param canvasRenderer the property value,  
         * @return this entity builder for chaining
         */
        AppStateEntBuilder setCanvasRenderer(CanvasRendererEnum canvasRenderer);
        
        /**
         * Whether all K-AI-related features (chat sidebar, build mode, scripting assistance, etc.) are enabled.
         * 
         * @param isKaiEnabled the property value,  
         * @return this entity builder for chaining
         */
        AppStateEntBuilder setIsKaiEnabled(Boolean isKaiEnabled);
        
        /**
         * Display name of currently active node collection. Compatible with \&quot;Search in {activeNodeCollection} nodes\&quot;.
         * 
         * @param activeNodeCollection the property value,  
         * @return this entity builder for chaining
         */
        AppStateEntBuilder setActiveNodeCollection(String activeNodeCollection);
        
        /**
         * Whether to always confirm node config changes or apply them automatically when de-selecting a node.
         * 
         * @param confirmNodeConfigChanges the property value,  
         * @return this entity builder for chaining
         */
        AppStateEntBuilder setConfirmNodeConfigChanges(Boolean confirmNodeConfigChanges);
        
        /**
         * If true, dev mode specific buttons will be shown.
         * 
         * @param devMode the property value,  
         * @return this entity builder for chaining
         */
        AppStateEntBuilder setDevMode(Boolean devMode);
        
        /**
         * Mapping from file extension (e.g. \&quot;csv\&quot;) to the template ID of a node that can process such files
         * 
         * @param fileExtensionToNodeTemplateId the property value,  
         * @return this entity builder for chaining
         */
        AppStateEntBuilder setFileExtensionToNodeTemplateId(java.util.Map<String, String> fileExtensionToNodeTemplateId);
        
        /**
         * Whether the node repository is loaded (and ready to be used) or not.
         * 
         * @param nodeRepositoryLoaded the property value,  
         * @return this entity builder for chaining
         */
        AppStateEntBuilder setNodeRepositoryLoaded(Boolean nodeRepositoryLoaded);
        
        /**
         * Web URL to send the user to to download the desktop edition of the Analytics Platform.
         * 
         * @param analyticsPlatformDownloadURL the property value,  
         * @return this entity builder for chaining
         */
        AppStateEntBuilder setAnalyticsPlatformDownloadURL(String analyticsPlatformDownloadURL);
        
        /**
         * Whether to enable the locking of metanodes and components
         * 
         * @param isSubnodeLockingEnabled the property value,  
         * @return this entity builder for chaining
         */
        AppStateEntBuilder setIsSubnodeLockingEnabled(Boolean isSubnodeLockingEnabled);
        
        /**
         * A list of all available space providers.
         * 
         * @param spaceProviders the property value,  
         * @return this entity builder for chaining
         */
        AppStateEntBuilder setSpaceProviders(java.util.List<SpaceProviderEnt> spaceProviders);
        
        
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

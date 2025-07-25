/*
 * ------------------------------------------------------------------------
 *
 *  Copyright by KNIME AG, Zurich, Switzerland
 *  Website: http://www.knime.org; Email: contact@knime.org
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
 * ---------------------------------------------------------------------
 *
 * History
 *   Jan 25, 2023 (hornm): created
 */
package org.knime.gateway.impl.webui.entity;

import static org.knime.gateway.api.entity.EntityBuilderManager.builder;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.knime.core.node.KNIMEConstants;
import org.knime.core.node.extension.NodeSpecCollectionProvider;
import org.knime.core.node.port.PortTypeRegistry;
import org.knime.core.node.workflow.ComponentMetadata;
import org.knime.core.webui.WebUIUtil;
import org.knime.gateway.api.entity.NodeIDEnt;
import org.knime.gateway.api.util.CoreUtil;
import org.knime.gateway.api.util.VersionId;
import org.knime.gateway.api.webui.entity.AppStateEnt;
import org.knime.gateway.api.webui.entity.AppStateEnt.AppModeEnum;
import org.knime.gateway.api.webui.entity.AppStateEnt.AppStateEntBuilder;
import org.knime.gateway.api.webui.entity.PortTypeEnt;
import org.knime.gateway.api.webui.entity.ProjectEnt;
import org.knime.gateway.api.webui.entity.ProjectEnt.ProjectEntBuilder;
import org.knime.gateway.api.webui.entity.SpaceItemReferenceEnt;
import org.knime.gateway.api.webui.entity.SpaceProviderEnt;
import org.knime.gateway.api.webui.entity.SpaceProviderEnt.ConnectionModeEnum;
import org.knime.gateway.api.webui.entity.SpaceProviderEnt.TypeEnum;
import org.knime.gateway.api.webui.util.EntityFactory;
import org.knime.gateway.impl.project.Origin;
import org.knime.gateway.impl.project.Project;
import org.knime.gateway.impl.project.ProjectManager;
import org.knime.gateway.impl.webui.NodeFactoryProvider;
import org.knime.gateway.impl.webui.PreferencesProvider;
import org.knime.gateway.impl.webui.featureflags.FeatureFlags;
import org.knime.gateway.impl.webui.kai.KaiHandler;
import org.knime.gateway.impl.webui.modes.WebUIMode;
import org.knime.gateway.impl.webui.repo.NodeCollections;
import org.knime.gateway.impl.webui.spaces.SpaceProvider;
import org.knime.gateway.impl.webui.spaces.SpaceProvider.SpaceProviderConnection;
import org.knime.gateway.impl.webui.spaces.SpaceProviders;
import org.knime.gateway.impl.webui.spaces.local.LocalSpace;

/**
 * Utility methods to build {@link AppStateEnt}-instances. Usually it would be part of the {@link EntityFactory}.
 * However, in order to build the {@link AppStateEnt} many classes are required which aren't available to the
 * {@link EntityFactory}.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
public final class AppStateEntityFactory {

    private static final Map<String, PortTypeEnt> AVAILABLE_PORT_TYPE_ENTS = getAvailablePortTypeEnts();

    private static final List<String> AVAILABLE_COMPONENT_TYPES = getAvailableComponentTypes();

    /**
     * When the user is prompted to select a port type, this subset of types may be used as suggestions (if the
     * respective port type is installed).
     */
    private static final List<String> SUGGESTED_PORT_TYPE_IDS = List.of( //
        "org.knime.core.node.BufferedDataTable", //
        "org.knime.database.port.DBDataPortObject", //
        "org.knime.database.port.DBSessionPortObject", //
        "org.knime.core.node.port.flowvariable.FlowVariablePortObject", //
        "org.knime.core.node.port.PortObject", //
        "org.knime.core.node.workflow.capture.WorkflowPortObject");

    private static final List<String> AVAILABLE_SUGGESTED_PORT_TYPE_IDS = getSuggestedPortTypeIds();

    private AppStateEntityFactory() {
        // utility class
    }

    /**
     * Holds instances of service dependencies needed for building this application state.
     */
    @SuppressWarnings("javadoc")
    public record ServiceDependencies( //
            ProjectManager projectManager, //
            PreferencesProvider preferencesProvider, //
            SpaceProviders spaceProviders, //
            NodeFactoryProvider nodeFactoryProvider, //
            NodeCollections nodeCollections, //
            KaiHandler kaiHandler //
    ) {
    }

    /**
     * Properties added here potentially also need to be considered in
     * {@link #buildAppStateEntDiff(AppStateEnt, AppStateEnt, boolean)}.
     *
     * @param dependencies Service dependencies needed for building this application state
     * @param projectFilter determines the projects to include in the app state
     * @param isActiveProject determines the projects to be set to active (see {@link ProjectEnt#getActiveWorkflowId()};
     *            if {@code null} {@link ProjectManager#isActiveProject(String)} is used
     * @return a new application state entity instance
     */
    public static AppStateEnt buildAppStateEnt(final ProjectFilter projectFilter,
        final Predicate<String> isActiveProject, final ServiceDependencies dependencies) {
        var projects = getProjectEnts( //
            dependencies.projectManager(), //
            dependencies.spaceProviders(), //
            projectFilter.predicate(), //
            isActiveProject == null ? dependencies.projectManager()::isActiveProject : isActiveProject //
        );
        var activeCollection =
            Optional.ofNullable(dependencies.nodeCollections()).flatMap(NodeCollections::getActiveCollection);
        var kaiHandler = dependencies.kaiHandler();
        var appMode = getAppModeEnum();
        return builder(AppStateEntBuilder.class) //
            .setAppMode(appMode) //
            .setOpenProjects(projects) //
            .setAvailablePortTypes(AVAILABLE_PORT_TYPE_ENTS) //
            .setSuggestedPortTypeIds(AVAILABLE_SUGGESTED_PORT_TYPE_IDS) //
            .setAvailableComponentTypes(AVAILABLE_COMPONENT_TYPES) //
            .setScrollToZoomEnabled(dependencies.preferencesProvider().isScrollToZoomEnabled()) //
            .setHasNodeCollectionActive(activeCollection.isPresent()) //
            .setActiveNodeCollection( //
                activeCollection //
                    .map(NodeCollections.NodeCollection::displayName) //
                    .orElse("all") //
            ) //
            .setConfirmNodeConfigChanges(dependencies.preferencesProvider().confirmNodeConfigChanges()) //
            .setHasNodeRecommendationsEnabled(dependencies.preferencesProvider().hasNodeRecommendationsEnabled()) //
            .setFeatureFlags(FeatureFlags.getFeatureFlags()) //
            .setDevMode(WebUIUtil.isInDevMode()) //
            .setUseEmbeddedDialogs(dependencies.preferencesProvider().useEmbeddedDialogs()) //
            .setCanvasRenderer(dependencies.preferencesProvider().canvasRenderer()) //
            .setIsKaiEnabled(kaiHandler == null ? Boolean.FALSE : kaiHandler.isKaiEnabled()) //
            .setFileExtensionToNodeTemplateId( //
                dependencies.nodeFactoryProvider() == null //
                    ? Collections.emptyMap() //
                    : dependencies.nodeFactoryProvider().getFileExtensionToNodeFactoryMap() //
            ) //
            .setNodeRepositoryLoaded(NodeSpecCollectionProvider.Progress.isDone()) //
            .setAnalyticsPlatformDownloadURL(getAnalyticsPlatformDownloadURL()) //
            .setIsSubnodeLockingEnabled(getIsSubnodeLockingEnabled()) //
            // TODO HUB-9598 only include when not read-only connection?
            .setSpaceProviders(
                appMode == AppModeEnum.DEFAULT ? buildSpaceProviderEnts(dependencies.spaceProviders(), false) : null) //
            .build();
    }

    /**
     * Determines which projects to include in the app state.
     */
    public static final class ProjectFilter {

        private static final String NO_PROJECTS = null;

        private static final String ALL_PROJECTS = "";

        private final String m_projectId;

        /**
         * @return a project filter that includes all projects
         */
        public static ProjectFilter all() {
            return new ProjectFilter(ALL_PROJECTS);
        }

        /**
         * @return a project filter that includes no project.
         */
        public static ProjectFilter none() {
            return new ProjectFilter(NO_PROJECTS);
        }

        /**
         * @param projectId
         * @return a project filter that includes only the project with the given id
         */
        public static ProjectFilter single(final String projectId) {
            return new ProjectFilter(projectId);
        }

        private ProjectFilter(final String projectId) {
            m_projectId = projectId;
        }

        private Predicate<String> predicate() {
            if (Objects.equals(NO_PROJECTS, m_projectId)) {
                return id -> false;
            } else if (ALL_PROJECTS.equals(m_projectId)) {
                return id -> true;
            } else {
                return id -> id.equals(m_projectId);
            }
        }

    }

    private static AppModeEnum getAppModeEnum() {
        return switch (WebUIMode.getMode()) {
            case DEFAULT -> AppModeEnum.DEFAULT;
            case JOB_VIEWER -> AppModeEnum.JOB_VIEWER;
            case PLAYGROUND -> AppModeEnum.PLAYGROUND;
        };
    }

    /**
     * Build an app state entity with only the changed properties set.
     *
     * @param oldAppState
     * @param newAppState
     * @param includeProjectSpecificInfosInDiff
     * @return the app state where only the properties are set which have changed
     */
    public static AppStateEnt buildAppStateEntDiff(final AppStateEnt oldAppState, final AppStateEnt newAppState,
        final boolean includeProjectSpecificInfosInDiff) {
        if (oldAppState == null) { // If there is no previous app state, no checks are needed
            return newAppState;
        } else { // Only set what has changed (except for properties we know that are static)
            var builder = builder(AppStateEntBuilder.class);
            if (includeProjectSpecificInfosInDiff) {
                setIfChanged(oldAppState, newAppState, AppStateEnt::getOpenProjects, builder::setOpenProjects);
            }
            setIfChanged(oldAppState, newAppState, AppStateEnt::hasNodeCollectionActive,
                builder::setHasNodeCollectionActive);
            setIfChanged(oldAppState, newAppState, AppStateEnt::getActiveNodeCollection,
                builder::setActiveNodeCollection);
            setIfChanged(oldAppState, newAppState, AppStateEnt::hasNodeRecommendationsEnabled,
                builder::setHasNodeRecommendationsEnabled);
            setIfChanged(oldAppState, newAppState, AppStateEnt::isScrollToZoomEnabled, builder::setScrollToZoomEnabled);
            setIfChanged(oldAppState, newAppState, AppStateEnt::isConfirmNodeConfigChanges,
                builder::setConfirmNodeConfigChanges);
            setIfChanged(oldAppState, newAppState, AppStateEnt::isUseEmbeddedDialogs, builder::setUseEmbeddedDialogs);
            setIfChanged(oldAppState, newAppState, AppStateEnt::isKaiEnabled, builder::setIsKaiEnabled);
            setIfChanged(oldAppState, newAppState, AppStateEnt::isNodeRepositoryLoaded,
                builder::setNodeRepositoryLoaded);
            setIfChanged(oldAppState, newAppState, AppStateEnt::getCanvasRenderer, builder::setCanvasRenderer);
            if (getAppModeEnum() == AppModeEnum.DEFAULT) {
                setIfChanged(oldAppState, newAppState, AppStateEnt::getSpaceProviders, builder::setSpaceProviders);
            }
            return builder.build();
        }
    }

    private static <T> void setIfChanged(final AppStateEnt oldAppState, final AppStateEnt newAppState,
        final Function<AppStateEnt, T> getter, final Consumer<T> setter) {
        var oldVal = getter.apply(oldAppState);
        var newVal = getter.apply(newAppState);
        if (!Objects.equals(oldVal, newVal)) {
            setter.accept(newVal);
        }
    }

    private static List<ProjectEnt> getProjectEnts(final ProjectManager projectManager,
        final SpaceProviders spaceProviders, final Predicate<String> projectFilter,
        final Predicate<String> isActiveProject) {
        return projectManager.getProjectIds().stream() //
            .filter(projectFilter) //
            .flatMap(id -> projectManager.getProject(id).stream()) //
            .map(project -> buildProjectEnt(project, isActiveProject, spaceProviders,
                projectManager::getActiveVersionForProject)) //
            .toList();
    }

    private static Map<String, PortTypeEnt> getAvailablePortTypeEnts() {
        var availablePortTypes = PortTypeRegistry.getInstance().availablePortTypes();
        return availablePortTypes.stream() //
            .distinct() //
            .collect(Collectors.toMap( //
                CoreUtil::getPortTypeId, //
                pt -> EntityFactory.PortType.buildPortTypeEnt(pt, availablePortTypes, true) //
            ));
    }

    private static List<String> getAvailableComponentTypes() {
        return Arrays.stream(ComponentMetadata.ComponentNodeType.values()) //
            .map(ComponentMetadata.ComponentNodeType::getDisplayText) //
            .toList();
    }

    private static List<String> getSuggestedPortTypeIds() {
        var portTypeRegistry = PortTypeRegistry.getInstance();
        return SUGGESTED_PORT_TYPE_IDS.stream() //
            .filter(id -> portTypeRegistry.getObjectClass(id).isPresent()) //
            .toList();
    }

    private static ProjectEnt buildProjectEnt(final Project project, final Predicate<String> isActiveProject,
        final SpaceProviders spaceProviders, final Function<String, Optional<VersionId>> getActiveVersionForProject) {
        final var projectEntBuilder = builder(ProjectEntBuilder.class) //
            .setName(project.getName()) //
            .setProjectId(project.getID());

        // optionally set an active workflow for this workflow project
        if (isActiveProject.test(project.getID())) {
            projectEntBuilder.setActiveWorkflowId(NodeIDEnt.getRootID());
        }

        project.getOrigin().ifPresent(origin -> {
            var activeVersion = getActiveVersionForProject.apply(project.getID());
            var originEnt = buildSpaceItemReferenceEnt(origin, spaceProviders, activeVersion);
            projectEntBuilder.setOrigin(originEnt);
        });
        return projectEntBuilder.build();
    }

    private static SpaceItemReferenceEnt buildSpaceItemReferenceEnt(final Origin origin,
        final SpaceProviders spaceProviders, final Optional<VersionId> activeVersion) {
        return builder(SpaceItemReferenceEnt.SpaceItemReferenceEntBuilder.class) //
            .setProviderId(origin.providerId()) //
            .setSpaceId(origin.spaceId()) //
            .setItemId(origin.itemId()) //
            .setProjectType(origin.projectType().orElse(null)) //
            .setVersionId(activeVersion.map(VersionId::toString).orElse(null)) //
            .setVersion(origin.itemVersion().orElse(null)) //
            .setAncestorItemIds(getAncestorItemIds(origin, spaceProviders)) //
            .build();
    }

    private static List<String> getAncestorItemIds(final Origin origin, final SpaceProviders spaceProviders) {
        // ancestor item ids are only required for local projects because it's used to
        // * mark folders that contain open projects
        // * disallow folders to be moved if they contain opened local projects
        //   (because they can't be moved while open)
        // ... in the space explorer.
        // Open hub-projects, e.g., aren't associated with space-items because they are considered a copy.
        if (origin.isLocal()) {
            var localSpace = (LocalSpace)spaceProviders.getSpace(origin.providerId(), origin.spaceId());
            return localSpace.getAncestorItemIds(origin.itemId());
        } else {
            return null; // NOSONAR null return value is reasonable (want to sent entity property to null)
        }
    }

    /**
     * @return Web URL to send the user to download the desktop edition of the Analytics Platform, or null if not
     *         configured.
     */
    private static String getAnalyticsPlatformDownloadURL() {
        return System.getProperty("org.knime.ui.analytics_platform_download_url");
    }

    /**
     * @return If the context menu option for locking is available for metanodes and components
     */
    private static Boolean getIsSubnodeLockingEnabled() {
        var value = System.getProperty(KNIMEConstants.PROPERTY_SHOW_METANODE_LOCK_ACTION);
        if (value == null) {
            return Boolean.FALSE;
        }
        return Boolean.valueOf(value);
    }

    private static List<SpaceProviderEnt> buildSpaceProviderEnts(final SpaceProviders spaceProviders,
        final boolean doConnect) {
        return spaceProviders.getAllSpaceProviders().stream().map(sp -> buildSpaceProviderEnt(sp, doConnect)).toList();
    }

    /**
     * @param spaceProvider
     * @param doConnect whether to connect to the space provider if necessary
     *
     * @return new instance of {@link SpaceProviderEnt}
     */
    public static SpaceProviderEnt buildSpaceProviderEnt(final SpaceProvider spaceProvider, final boolean doConnect) {
        final var type = spaceProvider.getType();
        final var isLocalSpaceProvider = type == TypeEnum.LOCAL;
        final var connectionMode = ConnectionModeEnum.valueOf(isLocalSpaceProvider ? "AUTOMATIC" : "AUTHENTICATED");
        final var optConnection = spaceProvider.getConnection(doConnect); // connect if necessary
        final var username = optConnection //
                .map(SpaceProviderConnection::getUsername) //
                .filter(Predicate.not(String::isEmpty)) //
                .orElse(null);
        return EntityFactory.Space.buildSpaceProviderEnt(spaceProvider.getId(), spaceProvider.getName(), type,
            isLocalSpaceProvider || optConnection.isPresent(), connectionMode,
            isLocalSpaceProvider ? null : spaceProvider.getServerAddress().orElse(null),
            isLocalSpaceProvider ? null : username,
            optConnection.map(SpaceProviderConnection::getResetOnUploadMode).orElse(null));
    }
}

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

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.knime.core.node.NodeLogger;
import org.knime.core.node.extension.NodeSpecCollectionProvider;
import org.knime.core.node.port.PortTypeRegistry;
import org.knime.core.node.workflow.ComponentMetadata;
import org.knime.core.node.workflow.WorkflowPersistor;
import org.knime.core.webui.WebUIUtil;
import org.knime.gateway.api.entity.NodeIDEnt;
import org.knime.gateway.api.util.CoreUtil;
import org.knime.gateway.api.webui.entity.AppStateEnt;
import org.knime.gateway.api.webui.entity.AppStateEnt.AppStateEntBuilder;
import org.knime.gateway.api.webui.entity.ExampleProjectEnt;
import org.knime.gateway.api.webui.entity.ExampleProjectEnt.ExampleProjectEntBuilder;
import org.knime.gateway.api.webui.entity.PortTypeEnt;
import org.knime.gateway.api.webui.entity.ProjectEnt;
import org.knime.gateway.api.webui.entity.ProjectEnt.ProjectEntBuilder;
import org.knime.gateway.api.webui.entity.SpaceItemReferenceEnt;
import org.knime.gateway.api.webui.entity.SpaceItemReferenceEnt.ProjectTypeEnum;
import org.knime.gateway.api.webui.entity.SpaceItemReferenceEnt.SpaceItemReferenceEntBuilder;
import org.knime.gateway.api.webui.util.EntityFactory;
import org.knime.gateway.impl.project.Project;
import org.knime.gateway.impl.project.ProjectManager;
import org.knime.gateway.impl.webui.ExampleProjects;
import org.knime.gateway.impl.webui.NodeCollections;
import org.knime.gateway.impl.webui.NodeFactoryProvider;
import org.knime.gateway.impl.webui.PreferencesProvider;
import org.knime.gateway.impl.webui.featureflags.FeatureFlags;
import org.knime.gateway.impl.webui.modes.Permissions;
import org.knime.gateway.impl.webui.spaces.SpaceProvider;
import org.knime.gateway.impl.webui.spaces.SpaceProviders;
import org.knime.gateway.impl.webui.spaces.local.LocalWorkspace;

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
    public record ServiceDependencies( //
        ProjectManager projectManager, //
        PreferencesProvider preferencesProvider, //
        ExampleProjects exampleProjects, //
        SpaceProviders spaceProviders, //
        NodeFactoryProvider nodeFactoryProvider, //
        NodeCollections nodeCollections //
    ) {
    }

    /**
     * Properties added here potentially also need to be considered in
     * {@link #buildAppStateEntDiff(AppStateEnt, AppStateEnt)}.
     *
     * @param previousAppState the previously created app state, or {@code null} if there is none
     * @param dependencies Service dependencies needed for building this application state
     * @param workflowProjectFilter filters the workflow projects to be included in the app state; or {@code null} if
     *            all projects are to be included
     * @param isActiveProject determines the projects to be set to active (see
     *            {@link WorkflowProjectEnt#getActiveWorkflowId()}; if {@code null}
     *            {@link ProjectManager#isActiveProject(String)} is used
     * @return a new application state entity instance
     */
    public static AppStateEnt buildAppStateEnt(final AppStateEnt previousAppState,
        final Predicate<String> workflowProjectFilter, final Predicate<String> isActiveProject,
        final ServiceDependencies dependencies) {
        var exampleProjects =
            dependencies.exampleProjects() == null ? null : buildExampleProjects(dependencies.exampleProjects());
        if (exampleProjects == null && previousAppState != null) {
            exampleProjects = previousAppState.getExampleProjects();
        }
        var projects = getProjectEnts( //
            dependencies.projectManager(), //
            dependencies.spaceProviders(), //
            workflowProjectFilter == null ? id -> true : workflowProjectFilter, //
            isActiveProject == null ? dependencies.projectManager()::isActiveProject : isActiveProject //
        );
        var activeCollection =
            Optional.ofNullable(dependencies.nodeCollections()).flatMap(NodeCollections::getActiveCollection);
        return builder(AppStateEntBuilder.class) //
            .setOpenProjects(projects.isEmpty() ? null : projects) //
            .setExampleProjects(exampleProjects) //
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
            .setHasNodeRecommendationsEnabled(dependencies.preferencesProvider().hasNodeRecommendationsEnabled()) //
            .setFeatureFlags(FeatureFlags.getFeatureFlags()) //
            .setPermissions(Permissions.getPermissions())//
            .setDevMode(WebUIUtil.isInDevMode()) //
            .setFileExtensionToNodeTemplateId( //
                dependencies.nodeFactoryProvider() == null //
                    ? Collections.emptyMap() //
                    : dependencies.nodeFactoryProvider().getFileExtensionToNodeFactoryMap() //
            ) //
            .setNodeRepositoryLoaded(NodeSpecCollectionProvider.Progress.isDone()) //
            .setAnalyticsPlatformDownloadURL(getAnalyticsPlatformDownloadURL()) //
            .build();
    }

    /**
     * Build an app state entity with only the changed properties set.
     *
     * @param oldAppState
     * @param newAppState
     * @return the app state where only the properties are set which have changed
     */
    public static AppStateEnt buildAppStateEntDiff(final AppStateEnt oldAppState, final AppStateEnt newAppState) {
        if (oldAppState == null) { // If there is no previous app state, no checks are needed
            return newAppState;
        } else { // Only set what has changed (except for properties we know that are static)
            var builder = builder(AppStateEntBuilder.class);
            setIfChanged(oldAppState, newAppState, AppStateEnt::getOpenProjects, builder::setOpenProjects);
            setIfChanged(oldAppState, newAppState, AppStateEnt::hasNodeCollectionActive,
                builder::setHasNodeCollectionActive);
            setIfChanged(oldAppState, newAppState, AppStateEnt::getActiveNodeCollection,
                builder::setActiveNodeCollection);
            setIfChanged(oldAppState, newAppState, AppStateEnt::hasNodeRecommendationsEnabled,
                builder::setHasNodeRecommendationsEnabled);
            setIfChanged(oldAppState, newAppState, AppStateEnt::isScrollToZoomEnabled, builder::setScrollToZoomEnabled);
            setIfChanged(oldAppState, newAppState, AppStateEnt::isNodeRepositoryLoaded,
                builder::setNodeRepositoryLoaded);
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
            .map(wp -> buildWorkflowProjectEnt(wp, isActiveProject, spaceProviders)) //
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

    private static List<ExampleProjectEnt> buildExampleProjects(final ExampleProjects exampleProjects) {
        var localWorkspace = exampleProjects.getLocalWorkspace();
        return exampleProjects.getRelativeExampleProjectPaths().stream() //
            .map(s -> localWorkspace.getLocalRootPath().resolve(Path.of(s))) //
            .filter(Files::exists) //
            .map(f -> buildExampleProject(f, localWorkspace)) //
            .filter(Objects::nonNull) //
            .toList();
    }

    private static ExampleProjectEnt buildExampleProject(final Path workflowDir, final LocalWorkspace localWorkspace) {
        var svgFile = workflowDir.resolve(WorkflowPersistor.SVG_WORKFLOW_FILE);
        byte[] svg;
        try {
            svg = Files.readAllBytes(svgFile);
        } catch (IOException ex) {
            NodeLogger.getLogger(AppStateEntityFactory.class)
                .error("Svg for workflow '" + workflowDir + "' could not be read", ex);
            return null;
        }
        var name = workflowDir.getFileName().toString();
        var svgEncoded = Base64.getEncoder().encodeToString(svg);
        var itemId = localWorkspace.getItemId(workflowDir);
        return buildExampleProject(name, svgEncoded, itemId);
    }

    private static ExampleProjectEnt buildExampleProject(final String name, final String svg, final String itemId) {
        var origin = builder(SpaceItemReferenceEntBuilder.class) //
            .setItemId(itemId) //
            .setSpaceId(LocalWorkspace.LOCAL_WORKSPACE_ID) //
            .setProjectType(ProjectTypeEnum.WORKFLOW) //
            .setProviderId(SpaceProvider.LOCAL_SPACE_PROVIDER_ID) //
            .build();
        return builder(ExampleProjectEntBuilder.class) //
            .setName(name) //
            .setSvg(svg) //
            .setOrigin(origin) //
            .build();
    }

    private static ProjectEnt buildWorkflowProjectEnt(final Project p, final Predicate<String> isActiveProject,
        final SpaceProviders spaceProviders) {
        final var projectEntBuilder = builder(ProjectEntBuilder.class) //
            .setName(p.getName()) //
            .setProjectId(p.getID());

        // optionally set an active workflow for this workflow project
        if (isActiveProject.test(p.getID())) {
            projectEntBuilder.setActiveWorkflowId(NodeIDEnt.getRootID());
        }

        p.getOrigin().ifPresent(origin -> {
            var originEnt = buildSpaceItemReferenceEnt(origin, spaceProviders);
            projectEntBuilder.setOrigin(originEnt);
        });
        return projectEntBuilder.build();
    }

    private static SpaceItemReferenceEnt buildSpaceItemReferenceEnt(final Project.Origin origin,
        final SpaceProviders spaceProviders) {
        return builder(SpaceItemReferenceEnt.SpaceItemReferenceEntBuilder.class) //
            .setProviderId(origin.getProviderId()) //
            .setSpaceId(origin.getSpaceId()) //
            .setItemId(origin.getItemId()) //
            .setProjectType(origin.getProjectType()) //
            .setAncestorItemIds(getAncestorItemIds(origin, spaceProviders)) //
            .build();
    }

    private static List<String> getAncestorItemIds(final Project.Origin origin, final SpaceProviders spaceProviders) {
        return SpaceProviders.getSpaceOptional(spaceProviders, origin.getProviderId(), origin.getSpaceId()) //
            // ancestor item ids are only required for local projects because it's used to
            // * mark folders that contain open projects
            // * disallow folders to be moved if they contain opened local projects
            //   (because they can't be moved while open)
            // ... in the space explorer.
            // Open hub-projects, e.g., aren't associated with space-items because they are considered a copy.
            .filter(LocalWorkspace.class::isInstance) //
            .map(space -> space.getAncestorItemIds(origin.getItemId())) //
            .orElse(null);
    }

    /**
     * @return Web URL to send the user to to download the desktop edition of the Analytics Platform, or null if not
     *         configured.
     */
    private static String getAnalyticsPlatformDownloadURL() {
        return System.getProperty("org.knime.ui.analytics_platform_download_url");
    }

}

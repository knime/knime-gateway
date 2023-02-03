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

import static java.util.stream.Collectors.toList;
import static org.knime.gateway.api.entity.EntityBuilderManager.builder;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.knime.core.node.BufferedDataTable;
import org.knime.core.node.NodeLogger;
import org.knime.core.node.port.PortObject;
import org.knime.core.node.port.PortType;
import org.knime.core.node.port.PortTypeRegistry;
import org.knime.core.node.port.database.DatabaseConnectionPortObject;
import org.knime.core.node.port.database.DatabasePortObject;
import org.knime.core.node.port.flowvariable.FlowVariablePortObject;
import org.knime.core.node.workflow.WorkflowPersistor;
import org.knime.core.node.workflow.capture.WorkflowPortObject;
import org.knime.core.webui.WebUIUtil;
import org.knime.gateway.api.entity.NodeIDEnt;
import org.knime.gateway.api.util.CoreUtil;
import org.knime.gateway.api.webui.entity.AppStateEnt;
import org.knime.gateway.api.webui.entity.AppStateEnt.AppStateEntBuilder;
import org.knime.gateway.api.webui.entity.ExampleProjectEnt;
import org.knime.gateway.api.webui.entity.ExampleProjectEnt.ExampleProjectEntBuilder;
import org.knime.gateway.api.webui.entity.PortTypeEnt;
import org.knime.gateway.api.webui.entity.WorkflowProjectEnt;
import org.knime.gateway.api.webui.entity.WorkflowProjectEnt.WorkflowProjectEntBuilder;
import org.knime.gateway.api.webui.entity.WorkflowProjectOriginEnt;
import org.knime.gateway.api.webui.entity.WorkflowProjectOriginEnt.WorkflowProjectOriginEntBuilder;
import org.knime.gateway.api.webui.util.EntityFactory;
import org.knime.gateway.api.webui.util.WorkflowBuildContext;
import org.knime.gateway.impl.project.WorkflowProject;
import org.knime.gateway.impl.project.WorkflowProjectManager;
import org.knime.gateway.impl.webui.ExampleProjects;
import org.knime.gateway.impl.webui.PreferencesProvider;
import org.knime.gateway.impl.webui.WorkflowKey;
import org.knime.gateway.impl.webui.WorkflowMiddleware;
import org.knime.gateway.impl.webui.spaces.LocalWorkspace;
import org.knime.gateway.impl.webui.spaces.SpaceProviders;

/**
 * Utility methods to build {@link AppStateEnt}-instances. Usually it would be part of the {@link EntityFactory}.
 * However, in order to build the {@link AppStateEnt} many classes are required which aren't available to the
 * {@link EntityFactory}.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
public final class AppStateEntityFactory {

    private static final Set<PortType> AVAILABLE_PORT_TYPES =
        PortTypeRegistry.getInstance().availablePortTypes().stream()//
            .collect(Collectors.toSet());

    /**
     * When the user is prompted to select a port type, this subset of types may be used as suggestions.
     */
    private static final List<PortType> SUGGESTED_PORT_TYPES = List.of(BufferedDataTable.TYPE, // Data
        DatabaseConnectionPortObject.TYPE, // Database Connection, TODO: Update deprecated type here
        DatabasePortObject.TYPE, // Database Query, TODO: Update deprecated type here, too
        FlowVariablePortObject.TYPE, // Flow Variable
        PortObject.TYPE, // Generic
        WorkflowPortObject.TYPE // Workflow
    );

    private AppStateEntityFactory() {
        // utility class
    }

    /**
     * @param previousAppState
     * @param workflowProjectManager
     * @param workflowMiddleware
     * @param preferenceProvider
     * @param exampleProjects if {@code null}, no example projects will be added to the app-state
     * @param spaceProviders used to, e.g., determine the ancestor item ids for a given item-id
     * @return a new entity instance
     */
    public static AppStateEnt buildAppStateEnt(final AppStateEnt previousAppState,
        final WorkflowProjectManager workflowProjectManager, final WorkflowMiddleware workflowMiddleware,
        final PreferencesProvider preferenceProvider, final ExampleProjects exampleProjects,
        final SpaceProviders spaceProviders) {
        Map<String, PortTypeEnt> availablePortTypeEnts = null;
        List<String> suggestedPortTypeIds = null;
        Boolean nodeRepoFilterEnabled = preferenceProvider.isNodeRepoFilterEnabled();
        List<ExampleProjectEnt> exampleProjectEnts = null;
        var projectEnts = getProjectEnts(workflowProjectManager, workflowMiddleware, spaceProviders);
        if (previousAppState == null) { // If there is no previous app state, no checks are needed
            availablePortTypeEnts = getAvailablePortTypeEnts();
            suggestedPortTypeIds = getSuggestedPortTypeIds();
            if (exampleProjects != null) {
                exampleProjectEnts = buildExampleProjects(exampleProjects);
            }
        } else { // Only set what has changed
            if (Objects.equals(previousAppState.getOpenProjects(), projectEnts)) {
                projectEnts = null;
            }
            if (Objects.equals(previousAppState.isNodeRepoFilterEnabled(), nodeRepoFilterEnabled)) {
                nodeRepoFilterEnabled = null;
            }
        }
        return builder(AppStateEntBuilder.class) //
            .setOpenProjects(projectEnts) //
            .setExampleProjects(exampleProjectEnts) //
            .setAvailablePortTypes(availablePortTypeEnts) //
            .setSuggestedPortTypeIds(suggestedPortTypeIds) //
            .setNodeRepoFilterEnabled(nodeRepoFilterEnabled) //
            .setHasNodeRecommendationsEnabled(preferenceProvider.hasNodeRecommendationsEnabled()) // This setting is always sent
            .setFeatureFlags(getFeatureFlags()) // This setting is always sent
            .setDevMode(WebUIUtil.isInDevMode()) //
            .build();

    }

    private static List<WorkflowProjectEnt> getProjectEnts(final WorkflowProjectManager workflowProjectManager,
        final WorkflowMiddleware workflowMiddleware, final SpaceProviders spaceProviders) {
        return workflowProjectManager.getWorkflowProjectsIds().stream() //
            .flatMap(id -> workflowProjectManager.getWorkflowProject(id).stream()) //
            .map(wp -> buildWorkflowProjectEnt(wp, workflowProjectManager, workflowMiddleware, spaceProviders)) //
            .collect(toList());
    }

    private static Map<String, PortTypeEnt> getAvailablePortTypeEnts() {
        return AVAILABLE_PORT_TYPES.stream() //
            .collect(Collectors.toMap( //
                CoreUtil::getPortTypeId, //
                pt -> EntityFactory.PortType.buildPortTypeEnt(pt, AVAILABLE_PORT_TYPES, true) //
            ));
    }

    private static List<String> getSuggestedPortTypeIds() {
        return SUGGESTED_PORT_TYPES.stream() //
            .map(CoreUtil::getPortTypeId) //
            .collect(toList());
    }

    private static List<ExampleProjectEnt> buildExampleProjects(final ExampleProjects exampleProjects) {
        var localWorkspace = exampleProjects.getLocalWorkspace();
        return exampleProjects.getRelativeExampleProjectPaths().stream() //
            .map(s -> localWorkspace.getLocalRootPath().resolve(Path.of(s))) //
            .filter(Files::exists) //
            .map(f -> buildExampleProject(f, localWorkspace)) //
            .filter(Objects::nonNull) //
            .collect(Collectors.toList());
    }


    private static ExampleProjectEnt buildExampleProject(final Path workflowDir, final LocalWorkspace localWorkspace) {
        var name = workflowDir.getFileName().toString();
        var svgFile = workflowDir.resolve(WorkflowPersistor.SVG_WORKFLOW_FILE);
        byte[] svg;
        try {
            svg = Files.readAllBytes(svgFile);
        } catch (IOException ex) {
            NodeLogger.getLogger(AppStateEntityFactory.class)
                .error("Svg for workflow '" + workflowDir + "' could not be read", ex);
            return null;
        }
        var svgEncoded = Base64.getEncoder().encodeToString(svg);
        var itemId = localWorkspace.getItemId(workflowDir);
        return buildExampleProject(name, svgEncoded, itemId);
    }

    private static ExampleProjectEnt buildExampleProject(final String name, final String svg, final String itemId) {
        var origin = builder(WorkflowProjectOriginEntBuilder.class) //
            .setItemId(itemId) //
            .setSpaceId(LocalWorkspace.LOCAL_WORKSPACE_ID) //
            .setProviderId("local").build();
        return builder(ExampleProjectEntBuilder.class) //
            .setName(name) //
            .setSvg(svg) //
            .setOrigin(origin) //
            .build();
    }

    /**
     * Access feature flags in system properties
     *
     * @return A map of feature flag keys and their values
     */
    private static Map<String, Object> getFeatureFlags() {
        var featureFlagsPrefix = "org.knime.ui.feature.";
        var f1 = featureFlagsPrefix + "embedded_views_and_dialogs";
        return Map.of(f1, Boolean.getBoolean(f1));
    }

    private static WorkflowProjectEnt buildWorkflowProjectEnt(final WorkflowProject wp,
        final WorkflowProjectManager workflowProjectManager, final WorkflowMiddleware workflowMiddleware,
        final SpaceProviders spaceProviders) {
        final WorkflowProjectEntBuilder projectEntBuilder =
            builder(WorkflowProjectEntBuilder.class).setName(wp.getName()).setProjectId(wp.getID());

        // optionally set an active workflow for this workflow project
        if (workflowProjectManager.isActiveWorkflowProject(wp.getID())) {
            var activeWorkflow = workflowMiddleware.buildWorkflowSnapshotEnt( //
                new WorkflowKey(wp.getID(), NodeIDEnt.getRootID()), //
                () -> WorkflowBuildContext.builder().includeInteractionInfo(true) //
            );
            projectEntBuilder.setActiveWorkflow(activeWorkflow);
        }

        wp.getOrigin()
            .ifPresent(origin -> projectEntBuilder.setOrigin(buildWorkflowProjectOriginEnt(origin, spaceProviders)));
        return projectEntBuilder.build();
    }

    private static WorkflowProjectOriginEnt buildWorkflowProjectOriginEnt(final WorkflowProject.Origin origin,
        final SpaceProviders spaceProviders) {
        return builder(WorkflowProjectOriginEnt.WorkflowProjectOriginEntBuilder.class)
            .setProviderId(origin.getProviderId()) //
            .setSpaceId(origin.getSpaceId()) //
            .setItemId(origin.getItemId()) //
            .setAncestorItemIds(getAncestorItemIds(origin, spaceProviders)) //
            .build();
    }

    private static List<String> getAncestorItemIds(final WorkflowProject.Origin origin,
        final SpaceProviders spaceProviders) {
        var space =
            SpaceProviders.getSpaceOptional(spaceProviders, origin.getProviderId(), origin.getSpaceId()).orElse(null);
        if (space != null) {
            return space.getAncestorItemIds(origin.getItemId());
        }
        NodeLogger.getLogger(AppStateEntityFactory.class)
            .error("Ancestor item-ids couldn't be determined for workflow project '" + origin.getItemId() + "'");
        return List.of();
    }

}

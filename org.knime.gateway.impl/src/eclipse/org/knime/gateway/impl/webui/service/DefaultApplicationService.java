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
 *   Aug 21, 2020 (hornm): created
 */
package org.knime.gateway.impl.webui.service;

import static java.util.stream.Collectors.toList;
import static org.knime.gateway.api.entity.EntityBuilderManager.builder;

import java.io.IOException;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.knime.core.node.BufferedDataTable;
import org.knime.core.node.NodeLogger;
import org.knime.core.node.port.PortObject;
import org.knime.core.node.port.PortType;
import org.knime.core.node.port.PortTypeRegistry;
import org.knime.core.node.port.database.DatabaseConnectionPortObject;
import org.knime.core.node.port.database.DatabasePortObject;
import org.knime.core.node.port.flowvariable.FlowVariablePortObject;
import org.knime.core.node.workflow.capture.WorkflowPortObject;
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
import org.knime.gateway.api.webui.service.ApplicationService;
import org.knime.gateway.api.webui.util.EntityFactory;
import org.knime.gateway.api.webui.util.WorkflowBuildContext;
import org.knime.gateway.impl.project.WorkflowProject;
import org.knime.gateway.impl.project.WorkflowProjectManager;
import org.knime.gateway.impl.webui.AppStateUpdater;
import org.knime.gateway.impl.webui.PreferencesProvider;
import org.knime.gateway.impl.webui.WorkflowKey;
import org.knime.gateway.impl.webui.WorkflowMiddleware;
import org.knime.gateway.impl.webui.spaces.LocalWorkspace;

/**
 * The default implementation of the {@link ApplicationService}-interface.
 *
 * @noextend This class is not intended to be subclassed by clients.
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 * @author Kai Franze, KNIME GmbH
 */
public final class DefaultApplicationService implements ApplicationService {

    private final AppStateUpdater m_appStateUpdater =
        ServiceDependencies.getServiceDependency(AppStateUpdater.class, true);

    private final WorkflowProjectManager m_workflowProjectManager =
        ServiceDependencies.getServiceDependency(WorkflowProjectManager.class, true);

    private final WorkflowMiddleware m_workflowMiddleware =
        ServiceDependencies.getServiceDependency(WorkflowMiddleware.class, true);

    private final PreferencesProvider m_preferencesProvider =
        ServiceDependencies.getServiceDependency(PreferencesProvider.class, true);

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

    /**
     * Returns the singleton instance for this service.
     *
     * @return the singleton instance
     */
    public static DefaultApplicationService getInstance() {
        return ServiceInstances.getDefaultServiceInstance(DefaultApplicationService.class);
    }

    DefaultApplicationService() {
        // singleton
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void dispose() {
        m_appStateUpdater.setLastAppState(null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AppStateEnt getState() {
        var appState = buildAppStateEnt(null, m_workflowProjectManager, m_workflowMiddleware, m_preferencesProvider);
        m_appStateUpdater.setLastAppState(appState);
        return appState;
    }

    /**
     * @param previousAppState
     * @param workflowProjectManager
     * @param workflowMiddleware
     * @param preferenceProvider
     * @return a new entity instance
     */
    public static AppStateEnt buildAppStateEnt(final AppStateEnt previousAppState,
        final WorkflowProjectManager workflowProjectManager, final WorkflowMiddleware workflowMiddleware,
        final PreferencesProvider preferenceProvider) {
        Map<String, PortTypeEnt> availablePortTypeEnts = null;
        List<String> suggestedPortTypeIds = null;
        Boolean nodeRepoFilterEnabled = preferenceProvider.isNodeRepoFilterEnabled();
        List<ExampleProjectEnt> exampleProjects = null;
        var projectEnts = getProjectEnts(workflowProjectManager, workflowMiddleware);
        if (previousAppState == null) { // If there is no previous app state, no checks are needed
            availablePortTypeEnts = getAvailablePortTypeEnts();
            suggestedPortTypeIds = getSuggestedPortTypeIds();
            exampleProjects = buildExampleProjects();
        } else { // Only set what has changed
            if (areOpenProjectsEqual(previousAppState.getOpenProjects(), projectEnts)) {
                projectEnts = null;
            }
            if (Objects.equals(previousAppState.isNodeRepoFilterEnabled(), nodeRepoFilterEnabled)) {
                nodeRepoFilterEnabled = null;
            }
        }
        return builder(AppStateEntBuilder.class) //
            .setOpenProjects(projectEnts) //
            .setExampleProjects(exampleProjects) //
            .setAvailablePortTypes(availablePortTypeEnts) //
            .setSuggestedPortTypeIds(suggestedPortTypeIds) //
            .setNodeRepoFilterEnabled(nodeRepoFilterEnabled) //
            .setHasNodeRecommendationsEnabled(preferenceProvider.hasNodeRecommendationsEnabled()) // This setting is always sent
            .setFeatureFlags(getFeatureFlags()) // This setting is always sent
            .build();

    }

    private static boolean areOpenProjectsEqual(final List<WorkflowProjectEnt> previousProjects,
        final List<WorkflowProjectEnt> newProjects) {
        if (previousProjects == null && newProjects == null) {
            return true;
        }
        if ((previousProjects == null ^ newProjects == null) || (previousProjects.size() != newProjects.size())) {
            return false;
        }
        return IntStream.range(0, previousProjects.size()).allMatch(i -> {
            var left = previousProjects.get(i);
            var right = newProjects.get(i);
            return left.equals(right);
        });
    }

    private static List<WorkflowProjectEnt> getProjectEnts(final WorkflowProjectManager workflowProjectManager,
        final WorkflowMiddleware workflowMiddleware) {
        return workflowProjectManager.getWorkflowProjectsIds().stream() //
            .map(id -> workflowProjectManager.getWorkflowProject(id).orElse(null)) //
            .filter(Objects::nonNull)//
            .map(wp -> buildWorkflowProjectEnt(wp, workflowProjectManager, workflowMiddleware)) //
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

    private static List<ExampleProjectEnt> buildExampleProjects() {
        String dummySvg;
        try (var is = DefaultNodeRepositoryService.class.getResourceAsStream("/files/workflow.svg")) {
            dummySvg = Base64.getEncoder().encodeToString(is.readAllBytes());
        } catch (IOException ex) {
            NodeLogger.getLogger(DefaultApplicationService.class).error("Failed to read workflow svg", ex);
            return List.of();
        }
        return List.of( //
            buildExampleProject("Read excel file", dummySvg), //
            buildExampleProject("How to do v-lookup", dummySvg), //
            buildExampleProject("Merge excel file", dummySvg));
    }

    private static ExampleProjectEnt buildExampleProject(final String name, final String svg) {
        var origin = builder(WorkflowProjectOriginEntBuilder.class) //
            .setItemId("TODO") //
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
        final WorkflowProjectManager workflowProjectManager, final WorkflowMiddleware workflowMiddleware) {
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

        wp.getOrigin().ifPresent(origin -> projectEntBuilder.setOrigin(buildWorkflowProjectOriginEnt(origin)));
        return projectEntBuilder.build();
    }

    private static WorkflowProjectOriginEnt buildWorkflowProjectOriginEnt(final WorkflowProject.Origin origin) {
        return builder(WorkflowProjectOriginEnt.WorkflowProjectOriginEntBuilder.class)
            .setProviderId(origin.getProviderId()) //
            .setSpaceId(origin.getSpaceId()) //
            .setItemId(origin.getItemId()) //
            .build();
    }

}

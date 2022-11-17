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

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.knime.core.node.NodeLogger;
import org.knime.core.node.workflow.SubNodeContainer;
import org.knime.core.node.workflow.WorkflowManager;
import org.knime.core.ui.workflowcoach.NodeRecommendationManager;
import org.knime.gateway.api.entity.NodeIDEnt;
import org.knime.gateway.api.util.CoreUtil;
import org.knime.gateway.api.webui.entity.AppStateEnt;
import org.knime.gateway.api.webui.entity.AppStateEnt.AppStateEntBuilder;
import org.knime.gateway.api.webui.entity.PortTypeEnt;
import org.knime.gateway.api.webui.entity.WorkflowProjectEnt;
import org.knime.gateway.api.webui.entity.WorkflowProjectEnt.WorkflowProjectEntBuilder;
import org.knime.gateway.api.webui.service.ApplicationService;
import org.knime.gateway.api.webui.util.EntityBuilderUtil;
import org.knime.gateway.api.webui.util.WorkflowBuildContext;
import org.knime.gateway.impl.project.WorkflowProject;
import org.knime.gateway.impl.project.WorkflowProjectManager;
import org.knime.gateway.impl.service.util.DefaultServiceUtil;
import org.knime.gateway.impl.webui.AppStateProvider;
import org.knime.gateway.impl.webui.AppStateProvider.AppState;
import org.knime.gateway.impl.webui.AppStateProvider.AppState.OpenedWorkflow;
import org.knime.gateway.impl.webui.WorkflowKey;
import org.knime.gateway.impl.webui.WorkflowMiddleware;

/**
 * The default implementation of the {@link ApplicationService}-interface.
 *
 * @noextend This class is not intended to be subclassed by clients.
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 * @author Kai Franze, KNIME GmbH
 */
public final class DefaultApplicationService implements ApplicationService {

    private final AppStateProvider m_appStateProvider =
        ServiceDependencies.getServiceDependency(AppStateProvider.class, true);

    private final WorkflowProjectManager m_workflowProjectManager =
        ServiceDependencies.getServiceDependency(WorkflowProjectManager.class, true);

    private final WorkflowMiddleware m_workflowMiddleware =
        ServiceDependencies.getServiceDependency(WorkflowMiddleware.class, true);

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
    public AppStateEnt getState() {
        return buildAppStateEnt(null, m_appStateProvider.getAppState(), m_workflowProjectManager, m_workflowMiddleware);
    }

    /**
     * Helper to create a {@link AppStateEnt}-instance from an {@link AppState}.
     *
     * @param oldAppState
     * @param newAppState
     * @param workflowProjectManager
     * @param workflowMiddleware
     * @return a new entity instance
     */
    public static AppStateEnt buildAppStateEnt(final AppState oldAppState, final AppState newAppState,
        final WorkflowProjectManager workflowProjectManager, final WorkflowMiddleware workflowMiddleware) {
        List<WorkflowProjectEnt> projectEnts = null;
        Map<String, PortTypeEnt> availablePortTypeEnts = null;
        List<String> suggestedPortTypeIds = null;
        Map<String, Object> featureFlags = null;

        if(areOpenedWorkflowsDifferent(oldAppState, newAppState)) {
            projectEnts = getProjectEnts(newAppState, workflowProjectManager, workflowMiddleware);
            availablePortTypeEnts = getAvailablePortTypeEnts(newAppState);
            suggestedPortTypeIds = getSuggestedPortTypeIds(newAppState);
            featureFlags = getFeatureFlags();
        }

        return builder(AppStateEntBuilder.class) //
            .setOpenProjects(projectEnts) //
            .setAvailablePortTypes(availablePortTypeEnts) //
            .setSuggestedPortTypeIds(suggestedPortTypeIds) //
            .setFeatureFlags(featureFlags) //
            .setHasNodeRecommendationsEnabled(NodeRecommendationManager.isEnabled()) // This setting is always sent
            .build();

    }

    private static boolean areOpenedWorkflowsDifferent(final AppState oldAppState, final AppState newAppState) {
        List<OpenedWorkflow> oldOpenedWorkflows =
            oldAppState == null ? Collections.emptyList() : oldAppState.getOpenedWorkflows();
        List<OpenedWorkflow> newOpenedWorkflow =
            newAppState == null ? Collections.emptyList() : newAppState.getOpenedWorkflows();
        if (oldOpenedWorkflows.size() != newOpenedWorkflow.size()) {
            return true;
        }
        Predicate<OpenedWorkflow> existsEqualWorkflow = outerWf -> oldOpenedWorkflows.stream().anyMatch(innerWf -> {
            var sameProjectId = outerWf.getProjectId().equals(innerWf.getProjectId());
            var sameWorkflowId = outerWf.getWorkflowId().equals(innerWf.getWorkflowId());
            return sameProjectId && sameWorkflowId;
        });

        var areOpenedWorkflowsEqual = oldOpenedWorkflows.stream().allMatch(existsEqualWorkflow);
        return !areOpenedWorkflowsEqual;
    }

    private static List<WorkflowProjectEnt> getProjectEnts(final AppState appState,
        final WorkflowProjectManager workflowProjectManager, final WorkflowMiddleware workflowMiddleware) {
        return appState == null ? Collections.emptyList() //
            : appState.getOpenedWorkflows().stream() //
                .map(w -> buildWorkflowProjectEnt(w, workflowProjectManager, workflowMiddleware)) //
                .filter(Objects::nonNull)//
                .collect(toList());
    }

    private static Map<String, PortTypeEnt> getAvailablePortTypeEnts(final AppState appState) {
        if (appState == null) {
            return Collections.emptyMap();
        }
        var allAvailablePortTypes = appState.getAvailablePortTypes();
        return allAvailablePortTypes.stream() //
            .collect(Collectors.toMap( //
                CoreUtil::getPortTypeId, //
                pt -> EntityBuilderUtil.buildPortTypeEnt(pt, allAvailablePortTypes, true) //
            ));
    }

    private static List<String> getSuggestedPortTypeIds(final AppState appState) {
        return appState == null ? Collections.emptyList() //
            : appState.getSuggestedPortTypes().stream() //
                .map(CoreUtil::getPortTypeId) //
                .collect(toList());
    }

    private static WorkflowProjectEnt buildWorkflowProjectEnt(final OpenedWorkflow wf,
        final WorkflowProjectManager workflowProjectManager, final WorkflowMiddleware workflowMiddleware) {
        WorkflowProject wp = workflowProjectManager.getWorkflowProject(wf.getProjectId()).orElse(null);
        if (wp == null) {
            return null;
        }

        final WorkflowProjectEntBuilder builder =
            builder(WorkflowProjectEntBuilder.class).setName(wp.getName()).setProjectId(wp.getID());

        // optionally set an active workflow for this workflow project
        if (wf.isVisible()) {
            String wfId = wf.getWorkflowId();
            WorkflowManager wfm = workflowProjectManager.openAndCacheWorkflow(wf.getProjectId()).orElse(null);
            if (wfm != null && !wfId.equals(NodeIDEnt.getRootID().toString())) {
                var nc =
                    wfm.findNodeContainer(DefaultServiceUtil.entityToNodeID(wf.getProjectId(), new NodeIDEnt(wfId)));
                if (nc instanceof SubNodeContainer) {
                    wfm = ((SubNodeContainer)nc).getWorkflowManager();
                } else if (nc instanceof WorkflowManager) {
                    wfm = (WorkflowManager)nc;
                } else {
                    //
                }
            }
            if (wfm != null) {
                builder.setActiveWorkflow(
                    workflowMiddleware.buildWorkflowSnapshotEnt(new WorkflowKey(wp.getID(), new NodeIDEnt(wfm.getID())),
                        () -> WorkflowBuildContext.builder().includeInteractionInfo(true)));
            } else {
                NodeLogger.getLogger(DefaultApplicationService.class).warn(String.format(
                    "Workflow '%s' of project '%s' could not be loaded", wf.getWorkflowId(), wf.getProjectId()));
            }
        }
        return builder.build();
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

}

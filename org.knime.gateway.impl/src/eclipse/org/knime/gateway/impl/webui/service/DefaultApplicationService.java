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

import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

import org.knime.core.node.NodeLogger;
import org.knime.core.node.workflow.NodeContainer;
import org.knime.core.node.workflow.SubNodeContainer;
import org.knime.core.node.workflow.WorkflowManager;
import org.knime.gateway.api.entity.NodeIDEnt;
import org.knime.gateway.api.webui.entity.AppStateEnt;
import org.knime.gateway.api.webui.entity.AppStateEnt.AppStateEntBuilder;
import org.knime.gateway.api.webui.entity.WorkflowProjectEnt;
import org.knime.gateway.api.webui.entity.WorkflowProjectEnt.WorkflowProjectEntBuilder;
import org.knime.gateway.api.webui.service.ApplicationService;
import org.knime.gateway.api.webui.util.WorkflowBuildContext;
import org.knime.gateway.impl.project.WorkflowProject;
import org.knime.gateway.impl.project.WorkflowProjectManager;
import org.knime.gateway.impl.service.util.DefaultServiceUtil;
import org.knime.gateway.impl.webui.AppState;
import org.knime.gateway.impl.webui.WorkflowKey;
import org.knime.gateway.impl.webui.WorkflowStatefulUtil;
import org.knime.gateway.impl.webui.AppState.OpenedWorkflow;

/**
 * The default implementation of the {@link ApplicationService}-interface.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
public final class DefaultApplicationService implements ApplicationService {
    private static final DefaultApplicationService INSTANCE = new DefaultApplicationService();

    private Supplier<AppState> m_appStateSupplier = null;

    /**
     * Returns the singleton instance for this service.
     *
     * @return the singleton instance
     */
    public static DefaultApplicationService getInstance() {
        return INSTANCE;
    }

    private DefaultApplicationService() {
        // singleton
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AppStateEnt getState() {
        AppStateEntBuilder builder = builder(AppStateEntBuilder.class);
        if (m_appStateSupplier == null) {
            return builder.build();
        }
        AppState appState = m_appStateSupplier.get();
        if (appState == null) {
            return builder.build();
        }
        List<WorkflowProjectEnt> projects = appState.getOpenedWorkflows().stream()
            .map(DefaultApplicationService::buildWorkflowProjectEnt).filter(Objects::nonNull).collect(toList());
        return builder.setOpenedWorkflows(projects).build();
    }

    private static WorkflowProjectEnt buildWorkflowProjectEnt(final OpenedWorkflow wf) {
        WorkflowProject wp = WorkflowProjectManager.getWorkflowProject(wf.getProjectId()).orElse(null);
        if (wp == null) {
            return null;
        }

        final WorkflowProjectEntBuilder builder =
            builder(WorkflowProjectEntBuilder.class).setName(wp.getName()).setProjectId(wp.getID());

        // optionally set an active workflow for this workflow project
        if (wf.isVisible()) {
            String wfId = wf.getWorkflowId();
            WorkflowManager wfm = WorkflowProjectManager.openAndCacheWorkflow(wf.getProjectId()).orElse(null);
            if (wfm != null && !wfId.equals(NodeIDEnt.getRootID().toString())) {
                NodeContainer nc =
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
                builder.setActiveWorkflow(WorkflowStatefulUtil.getInstance().buildWorkflowSnapshotEntOrGetFromCache(
                    new WorkflowKey(wp.getID(), new NodeIDEnt(wfm.getID())),
                    () -> WorkflowBuildContext.builder().includeInteractionInfo(true)));
            } else {
                NodeLogger.getLogger(DefaultApplicationService.class).warn(String.format(
                    "Workflow '%s' of project '%s' could not be loaded", wf.getWorkflowId(), wf.getProjectId()));
            }
        }
        return builder.build();
    }

    /**
     * Sets the state supplier, i.e. a function that can be called to get the current app state.
     *
     * @param stateSupplier the app state supplier
     * @throws IllegalStateException if the app state supplier is already set
     */
    public void setAppStateSupplier(final Supplier<AppState> stateSupplier) {
        if(m_appStateSupplier != null) {
            throw new IllegalStateException("App state supplier is already set");
        }
        m_appStateSupplier = stateSupplier;
    }

    /**
     * Removes the application state supplier that has been set via {@link #setAppStateSupplier(Supplier)}.
     *
     * Mainly for testing purposes.
     */
    public void clearAppStateSupplier() {
        m_appStateSupplier = null;
    }
}

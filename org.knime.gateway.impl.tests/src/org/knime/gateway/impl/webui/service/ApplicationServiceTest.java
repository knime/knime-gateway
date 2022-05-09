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
 */
package org.knime.gateway.impl.webui.service;

import static org.junit.Assert.assertNotSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Set;
import java.util.function.Supplier;

import org.junit.Test;
import org.knime.core.node.BufferedDataTable;
import org.knime.core.node.port.PortObject;
import org.knime.core.node.port.database.DatabaseConnectionPortObject;
import org.knime.core.node.port.database.DatabasePortObject;
import org.knime.gateway.api.entity.NodeIDEnt;
import org.knime.gateway.api.webui.entity.AppStateEnt;
import org.knime.gateway.impl.project.WorkflowProjectManager;
import org.knime.gateway.impl.webui.AppStateProvider;
import org.knime.gateway.impl.webui.AppStateProvider.AppState;
import org.knime.gateway.impl.webui.AppStateProvider.AppState.OpenedWorkflow;
import org.knime.gateway.impl.webui.WorkflowMiddleware;
import org.knime.gateway.testing.helper.TestWorkflowCollection;

/**
 * Tests for the {@link DefaultApplicationService}-implementation.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
public class ApplicationServiceTest extends GatewayServiceTest {

    /**
     * Test to get the app state.
     *
     * @throws Exception
     */
    @Test
    public void testGetAppState() throws Exception {
        String workflowProjectId = "the_workflow_project_id";
        loadWorkflow(TestWorkflowCollection.GENERAL_WEB_UI, workflowProjectId);

        AppState appState = mock(AppState.class);
        Supplier<AppState> appStateSupplier = mock(Supplier.class);
        AppStateProvider appStateProvider = new AppStateProvider(appStateSupplier);
        when(appStateSupplier.get()).thenReturn(appState);
        ServiceDependencies.setServiceDependency(AppStateProvider.class, appStateProvider);
        ServiceDependencies.setServiceDependency(WorkflowMiddleware.class,
            new WorkflowMiddleware(WorkflowProjectManager.getInstance()));
        ServiceDependencies.setServiceDependency(WorkflowProjectManager.class, WorkflowProjectManager.getInstance());

        var appService = DefaultApplicationService.getInstance();

        cr(appService.getState(), "empty_appstate");

        when(appState.getOpenedWorkflows()).thenReturn(List.of(createOpenedWorkflow(workflowProjectId)));
        var assumedAvailablePortTypes =
            Set.of(BufferedDataTable.TYPE, DatabaseConnectionPortObject.TYPE, PortObject.TYPE, DatabasePortObject.TYPE);
        when(appState.getAvailablePortTypes()).thenReturn(assumedAvailablePortTypes);
        var assumedSuggestedPortTypes = AppState.SUGGESTED_PORT_TYPES;
        when(appState.getSuggestedPortTypes()).thenReturn(assumedSuggestedPortTypes);

        AppStateEnt appStateEnt = appService.getState();
        cr(appStateEnt, "appstate");

        // test that a new workflow entity instance is created even though the workflow didn't change (see NXT-866)
        AppStateEnt appStateEnt2 = appService.getState();
        assertNotSame(
            appStateEnt.getOpenedWorkflows().get(0).getActiveWorkflow().getWorkflow(),
            appStateEnt2.getOpenedWorkflows().get(0).getActiveWorkflow().getWorkflow()
        );

        ServiceInstances.disposeAllServiceInstancesAndDependencies();

    }

    private static OpenedWorkflow createOpenedWorkflow(final String workflowProjectId) {
        return new OpenedWorkflow() {

            @Override
            public String getProjectId() {
                return workflowProjectId;
            }

            @Override
            public String getWorkflowId() {
                return NodeIDEnt.getRootID().toString();
            }

            @Override
            public boolean isVisible() {
                return true;
            }
        };
    }
}

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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.knime.gateway.api.entity.EntityBuilderManager.builder;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.junit.Test;
import org.knime.core.node.BufferedDataTable;
import org.knime.core.node.port.PortObject;
import org.knime.core.node.port.database.DatabaseConnectionPortObject;
import org.knime.core.node.port.database.DatabasePortObject;
import org.knime.core.node.port.viewproperty.ShapeHandlerPortObject;
import org.knime.core.node.workflow.WorkflowPersistor;
import org.knime.gateway.api.webui.entity.AppStateEnt;
import org.knime.gateway.impl.project.ProjectManager;
import org.knime.gateway.impl.webui.ExampleProjects;
import org.knime.gateway.impl.webui.featureflags.FeatureFlags;
import org.knime.gateway.impl.webui.spaces.Space;
import org.knime.gateway.impl.webui.spaces.SpaceProvider;
import org.knime.gateway.impl.webui.spaces.SpaceProviders;
import org.knime.gateway.impl.webui.spaces.local.LocalWorkspace;
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
        loadWorkflow(TestWorkflowCollection.HOLLOW, workflowProjectId);
        ProjectManager.getInstance().openAndCacheProject(workflowProjectId);
        ProjectManager.getInstance().setProjectActive(workflowProjectId);

        var appService = DefaultApplicationService.getInstance();

        AppStateEnt appStateEnt = appService.getState();
        assertThat(appStateEnt.hasNodeRecommendationsEnabled(), not(is(nullValue())));
        AppStateEnt appStateEntStripped = stripAppState(appStateEnt);
        cr(appStateEntStripped, "appstate");
    }

    /**
     * This test ensures that the default states for feature flags are accurately reflected in the app state.
     *
     * @throws Exception
     */
    @Test
    public void testGetAppStateWithFeatureFlagsNotSet() throws Exception {
        String workflowProjectId = "the_workflow_project_id";
        loadWorkflow(TestWorkflowCollection.HOLLOW, workflowProjectId);

        var appService = DefaultApplicationService.getInstance();

        var featureFlagKeyF1 = "org.knime.ui.feature.embedded_views_and_dialogs";
        var featureFlagKeyF2 = "org.knime.ui.feature.ai_assistant";
        var featureFlagKeyF3 = "org.knime.ui.feature.ai_assistant_installed";

        var appStateEnt = appService.getState();
        assertThat(appStateEnt.getFeatureFlags().get(featureFlagKeyF1), is(false));
        assertThat(appStateEnt.getFeatureFlags().get(featureFlagKeyF2), is(true));
        assertThat(appStateEnt.getFeatureFlags().get(featureFlagKeyF3), is(false));

        System.clearProperty(featureFlagKeyF1);
        System.clearProperty(featureFlagKeyF2);
        System.clearProperty(featureFlagKeyF3);
    }

    /**
     * Makes sure that feature flags supplied via system properties find their way into the app state returned by the
     * application service.
     *
     * @throws Exception
     */
    @Test
    public void testGetAppStateWithFeatureFlagsEnabled() throws Exception {
        String workflowProjectId = "the_workflow_project_id";
        loadWorkflow(TestWorkflowCollection.HOLLOW, workflowProjectId);

        var appService = DefaultApplicationService.getInstance();

        var featureFlagKeyF1 = "org.knime.ui.feature.embedded_views_and_dialogs";
        var featureFlagKeyF2 = "org.knime.ui.feature.ai_assistant";
        var featureFlagKeyF3 = "org.knime.ui.feature.ai_assistant_installed";

        System.setProperty(featureFlagKeyF1, "true");
        System.setProperty(featureFlagKeyF2, "true");

        FeatureFlags.setAiAssistantBackendAvailabe();

        var appStateEnt = appService.getState();
        assertThat(appStateEnt.getFeatureFlags().get(featureFlagKeyF1), is(true));
        assertThat(appStateEnt.getFeatureFlags().get(featureFlagKeyF2), is(true));
        assertThat(appStateEnt.getFeatureFlags().get(featureFlagKeyF3), is(true));

        System.clearProperty(featureFlagKeyF1);
        System.clearProperty(featureFlagKeyF2);
        System.clearProperty(featureFlagKeyF3);
    }

    /**
     * Makes sure that permissions applied by system properties are correctly added to the AppState.
     *
     * @throws Exception
     */
    @Test
    public void testGetAppStateWithPermissionsDefault() throws Exception {
        String workflowProjectId = "the_workflow_project_id";
        loadWorkflow(TestWorkflowCollection.HOLLOW, workflowProjectId);

        var appService = DefaultApplicationService.getInstance();

        var appStateEnt = appService.getState();
        assertThat(appStateEnt.getPermissions().isCanEditWorkflow(), is(true));
        assertThat(appStateEnt.getPermissions().isCanConfigureNodes(), is(true));
        assertThat(appStateEnt.getPermissions().isCanAccessSpaceExplorer(), is(true));
        assertThat(appStateEnt.getPermissions().isCanAccessNodeRepository(), is(true));
        assertThat(appStateEnt.getPermissions().isCanAccessKAIPanel(), is(true));
    }

    /**
     * Makes sure that permissions related to the job viewer are applied correctly.
     *
     * @throws Exception
     */
    @Test
    public void testGetAppStateWithPermissionsJobViewer() throws Exception {
        String workflowProjectId = "the_workflow_project_id";
        loadWorkflow(TestWorkflowCollection.HOLLOW, workflowProjectId);

        var appService = DefaultApplicationService.getInstance();

        var permissionsFlagKey = "org.knime.ui.mode";

        System.setProperty(permissionsFlagKey, "JOB-VIEWER");

        var appStateEnt = appService.getState();
        assertThat(appStateEnt.getPermissions().isCanEditWorkflow(), is(false));
        assertThat(appStateEnt.getPermissions().isCanConfigureNodes(), is(false));
        assertThat(appStateEnt.getPermissions().isCanAccessSpaceExplorer(), is(false));
        assertThat(appStateEnt.getPermissions().isCanAccessNodeRepository(), is(false));
        assertThat(appStateEnt.getPermissions().isCanAccessKAIPanel(), is(false));

        System.clearProperty(permissionsFlagKey);
    }

    @Override
    protected SpaceProviders createSpaceProviders() {
        var space = mock(Space.class);
        var spaceProvider = mock(SpaceProvider.class);
        when(spaceProvider.getSpace(any())).thenReturn(space);
        var spaceProviders = mock(SpaceProviders.class);
        when(spaceProviders.getProvidersMap()).thenReturn(Map.of("Provider ID for testing", spaceProvider));
        return spaceProviders;
    }

    @Override
    protected ExampleProjects createExampleProjects() {
        return new ExampleProjects() {

            @Override
            public LocalWorkspace getLocalWorkspace() {
                try {
                    return mockLocalWorkspace();
                } catch (IOException ex) {
                    throw new IllegalStateException(ex);
                }
            }

            @Override
            public List<String> getRelativeExampleProjectPaths() {
                return List.of("wfDir1", "wfDir2");
            }
        };
    }

    private static LocalWorkspace mockLocalWorkspace() throws IOException {
        var root = Files.createTempDirectory("application_service_test");
        var wfDir1 = root.resolve("wfDir1");
        Files.createDirectory(wfDir1);
        Files.writeString(wfDir1.resolve(WorkflowPersistor.SVG_WORKFLOW_FILE), "svg file content");
        var wfDir2 = root.resolve("wfDir2");
        Files.createDirectory(wfDir2);
        Files.writeString(wfDir2.resolve(WorkflowPersistor.SVG_WORKFLOW_FILE), "svg file content 2");
        return new LocalWorkspace(root);
    }

    private static AppStateEnt stripAppState(final AppStateEnt appStateEnt) {
        var availablePortTypes = appStateEnt.getAvailablePortTypes().entrySet().stream().filter(e -> {
            var k = e.getKey();
            return k.equals(BufferedDataTable.class.getName()) || k.equals(PortObject.class.getName())
                || k.equals(DatabaseConnectionPortObject.class.getName())
                || k.equals(DatabasePortObject.class.getName()) || k.equals(ShapeHandlerPortObject.class.getName());
        }).collect(Collectors.toMap(Entry::getKey, Entry::getValue));
        return builder(AppStateEnt.AppStateEntBuilder.class) //
            .setOpenProjects(appStateEnt.getOpenProjects()) //
            .setExampleProjects(appStateEnt.getExampleProjects()) //
            .setAvailablePortTypes(availablePortTypes) //
            .setAvailableComponentTypes(appStateEnt.getAvailableComponentTypes())
            .setSuggestedPortTypeIds(appStateEnt.getSuggestedPortTypeIds()) //
            .setFeatureFlags(appStateEnt.getFeatureFlags()) //
            .build();
    }
}

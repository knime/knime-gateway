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
 *   Aug 3, 2020 (hornm): created
 */
package org.knime.gateway.testing.helper.webui;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;
import static org.knime.gateway.testing.helper.webui.SpaceServiceTestHelper.createSpaceProvider;
import static org.knime.gateway.testing.helper.webui.SpaceServiceTestHelper.createSpaceProvidersManager;
import static org.knime.gateway.testing.helper.webui.WorkflowCommandTestHelper.buildAddNodeCommand;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.awaitility.Awaitility;
import org.knime.core.util.Pair;
import org.knime.gateway.api.entity.NodeIDEnt;
import org.knime.gateway.api.util.VersionId;
import org.knime.gateway.api.webui.entity.ComponentNodeEnt;
import org.knime.gateway.api.webui.entity.NativeNodeEnt;
import org.knime.gateway.api.webui.entity.NodeEnt;
import org.knime.gateway.api.webui.entity.NodeStateEnt.ExecutionStateEnum;
import org.knime.gateway.api.webui.entity.WorkflowEnt;
import org.knime.gateway.api.webui.service.WorkflowService;
import org.knime.gateway.api.webui.service.util.MutableServiceCallException;
import org.knime.gateway.api.webui.service.util.ServiceExceptions.ServiceCallException;
import org.knime.gateway.impl.project.ProjectManager;
import org.knime.gateway.impl.webui.service.ServiceDependencies;
import org.knime.gateway.impl.webui.spaces.Space;
import org.knime.gateway.impl.webui.spaces.SpaceProvidersManager;
import org.knime.gateway.testing.helper.ResultChecker;
import org.knime.gateway.testing.helper.ServiceProvider;
import org.knime.gateway.testing.helper.TestWorkflow;
import org.knime.gateway.testing.helper.TestWorkflowCollection;
import org.knime.gateway.testing.helper.WorkflowExecutor;
import org.knime.gateway.testing.helper.WorkflowLoader;

/**
 * Test for the endpoints to view/render a workflow.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 * @author Kai Franze, KNIME GmbH
 */
@SuppressWarnings({"java:S112", "java:S1192", "java:S3655", "java:S1874", "javadoc"})
// generic exceptions, repeated string literals, Optional#get without presence check, deprecated classes
public class WorkflowServiceTestHelper extends WebUIGatewayServiceTestHelper {

    /**
     * @param entityResultChecker
     * @param serviceProvider
     * @param workflowLoader
     * @param workflowExecutor
     * @param projectManager
     */
    public WorkflowServiceTestHelper(final ResultChecker entityResultChecker, final ServiceProvider serviceProvider,
        final WorkflowLoader workflowLoader, final WorkflowExecutor workflowExecutor,
        final ProjectManager projectManager) {
        super(WorkflowServiceTestHelper.class, entityResultChecker, serviceProvider, workflowLoader, workflowExecutor,
            projectManager);
    }

    /**
     * Tests to get the workflow.
     */
    public void testGetWorkflow() throws Exception {
        String wfId = loadWorkflow(TestWorkflowCollection.GENERAL_WEB_UI);

        // check un-executed
        WorkflowEnt workflow = ws().getWorkflow(wfId, NodeIDEnt.getRootID(), null, Boolean.TRUE).getWorkflow();
        cr(workflow, "workflowent_root");

        // get a metanode's workflow
        workflow = ws().getWorkflow(wfId, new NodeIDEnt(6), null, Boolean.TRUE).getWorkflow();
        cr(workflow, "workflowent_6");

        // get a component's workflow
        workflow = ws().getWorkflow(wfId, new NodeIDEnt(23), null, Boolean.FALSE).getWorkflow();
        cr(workflow, "workflowent_23");

        // check executed
        executeWorkflow(wfId);
        workflow = ws().getWorkflow(wfId, NodeIDEnt.getRootID(), null, Boolean.TRUE).getWorkflow();
        cr(workflow, "workflowent_root_executed");

        // get a workflow of a linked component
        workflow = ws().getWorkflow(wfId, new NodeIDEnt(183), null, Boolean.FALSE).getWorkflow();
        cr(workflow, "workflowent_183_linked_component");
    }

    public void testGetWorkflowVersion() throws Exception {
        var testWorkflowWithVersion = TestWorkflow.WithVersion.of( //
            TestWorkflowCollection.VERSIONS_CURRENT_STATE, //
            TestWorkflowCollection.VERSIONS_EARLIER_VERSION::getWorkflowDir //
        );
        var wfId = loadWorkflow(testWorkflowWithVersion);

        // this is expected to be the "current-state" workflow
        var workflow = ws().getWorkflow(wfId, NodeIDEnt.getRootID(), null, Boolean.TRUE).getWorkflow();
        assertTrue("Current state workflow is returned",
            workflow.getWorkflowAnnotations().stream()
                .anyMatch(annotation -> annotation.getText().getValue().toLowerCase().contains("current state"))
                && workflow.getInfo().getVersion() == null);
        workflow = ws().getWorkflow(wfId, NodeIDEnt.getRootID(), VersionId.currentState().toString(), Boolean.TRUE)
            .getWorkflow();
        assertTrue("Current state workflow is returned",
            workflow.getWorkflowAnnotations().stream()
                .anyMatch(annotation -> annotation.getText().getValue().toLowerCase().contains("current state"))
                && workflow.getInfo().getVersion() == null);

        var version = new VersionId.Fixed(5); // actual value does not matter, we always load "the other" workflow
        loadVersionAndSetActive(wfId, version);

        var versionWorkflow =
            ws().getWorkflow(wfId, NodeIDEnt.getRootID(), version.toString(), Boolean.TRUE).getWorkflow();
        assertTrue("Version workflow is returned",
            versionWorkflow.getWorkflowAnnotations().stream()
                .anyMatch(annotation -> annotation.getText().getValue().toLowerCase().contains("earlier version"))
                && versionWorkflow.getInfo().getVersion().equals(version.toString()));
    }

    public void testGetWorkflowVersionThrows() throws Exception {
        var testWorkflowWithVersion = TestWorkflow.WithVersion.of( //
            TestWorkflowCollection.VERSIONS_CURRENT_STATE, //
            TestWorkflowCollection.VERSIONS_EARLIER_VERSION::getWorkflowDir //
        );
        var projectId = loadWorkflow(testWorkflowWithVersion);

        var version = new VersionId.Fixed(4);
        loadVersionAndSetActive(projectId, version);

        // Try to get the workflow for a different project ID, throws
        var ex1 = assertThrows(Throwable.class,
            () -> ws().getWorkflow(projectId + "_diff", NodeIDEnt.getRootID(), version.toString(), Boolean.FALSE));
        assertThat(ex1.getMessage(), anyOf(containsString("Project for ID \"" + projectId + "_diff\" not found"),
            containsString("unexpected error code")));

        // Get the correct workflow, doesn't throw
        ws().getWorkflow(projectId, NodeIDEnt.getRootID(), version.toString(), Boolean.FALSE);
    }

    public void testExecutionThrowsWhenNotCurrentState() throws Exception {
        var testWorkflowWithVersion = TestWorkflow.WithVersion.of( //
            TestWorkflowCollection.VERSIONS_CURRENT_STATE, //
            TestWorkflowCollection.VERSIONS_EARLIER_VERSION::getWorkflowDir //
        );
        var projectId = loadWorkflow(testWorkflowWithVersion);

        // Current state, doesn't throw
        var command = buildAddNodeCommand("org.knime.base.node.preproc.filter.row.RowFilterNodeFactory", null, 12, 13,
            null, null, null);
        ws().executeWorkflowCommand(projectId, NodeIDEnt.getRootID(), command);
        ws().undoWorkflowCommand(projectId, NodeIDEnt.getRootID());

        var version = new VersionId.Fixed(5);
        loadVersionAndSetActive(projectId, version);

        // Earlier version, throws
        var ex1 =
            assertThrows(Throwable.class, () -> ws().executeWorkflowCommand(projectId, NodeIDEnt.getRootID(), command));
        assertThat(ex1.getMessage(), anyOf(containsString("Project version \"current-state\" is not active"),
            containsString("unexpected error code")));
        var ex2 = assertThrows(Throwable.class, () -> ws().undoWorkflowCommand(projectId, NodeIDEnt.getRootID()));
        assertThat(ex2.getMessage(), anyOf(containsString("Project version \"current-state\" is not active"),
            containsString("unexpected error code")));
        var ex3 = assertThrows(Throwable.class, () -> ws().redoWorkflowCommand(projectId, NodeIDEnt.getRootID()));
        assertThat(ex3.getMessage(), anyOf(containsString("Project version \"current-state\" is not active"),
            containsString("unexpected error code")));
    }

    /**
     * Tests to get a workflow of a component workflow.
     */
    public void testGetComponentProjectWorkflow() throws Exception {
        String wfId = loadComponent(TestWorkflowCollection.COMPONENT_PROJECT);

        WorkflowEnt workflow = ws().getWorkflow(wfId, NodeIDEnt.getRootID(), null, Boolean.TRUE).getWorkflow();
        cr(workflow, "component_project");

        workflow = ws().getWorkflow(wfId, new NodeIDEnt(5), null, Boolean.TRUE).getWorkflow();
        cr(workflow, "component_in_component_project_l1");

        workflow = ws().getWorkflow(wfId, new NodeIDEnt(5, 0, 7), null, Boolean.TRUE).getWorkflow();
        cr(workflow, "component_in_component_project_l2");
    }

    /**
     * Test construction of the workflow entity of a project containing nested linked components
     */
    public void testGetNestedLinkedComponentsProject() throws Exception {
        String wfId = loadWorkflow(TestWorkflowCollection.NESTED_LINKED_COMPONENT_PROJECT);
        WorkflowEnt workflow = ws().getWorkflow(wfId, new NodeIDEnt(2), null, Boolean.TRUE).getWorkflow();
        cr(workflow, "nested_linked_component");
    }

    /**
     * Tests the correct mapping of the node execution states.
     */
    public void testNodeExecutionStates() throws Exception {
        String wfId = loadWorkflow(TestWorkflowCollection.EXECUTION_STATES);

        WorkflowEnt workflow = ws().getWorkflow(wfId, NodeIDEnt.getRootID(), null, Boolean.FALSE).getWorkflow();
        cr(getNodeStates(workflow), "node_states");

        executeWorkflowAsync(wfId);
        Awaitility.await().atMost(5, TimeUnit.SECONDS).pollInterval(100, TimeUnit.MILLISECONDS).untilAsserted(() -> {
            WorkflowEnt w = ws().getWorkflow(wfId, NodeIDEnt.getRootID(), null, Boolean.FALSE).getWorkflow();
            assertThat(((NativeNodeEnt)w.getNodes().get("root:4")).getState().getExecutionState(),
                is(ExecutionStateEnum.EXECUTED));
        });
        workflow = ws().getWorkflow(wfId, NodeIDEnt.getRootID(), null, Boolean.FALSE).getWorkflow();
        cr(getNodeStates(workflow), "node_states_execution");
    }

    private static Map<String, ExecutionStateEnum> getNodeStates(final WorkflowEnt w) {
        return w.getNodes().entrySet().stream().map(e -> { // NOSONAR
            ExecutionStateEnum state = null;
            NodeEnt n = e.getValue();
            if (n instanceof NativeNodeEnt nativeEnt) {
                state = nativeEnt.getState().getExecutionState();
            } else if (n instanceof ComponentNodeEnt componentEnt) {
                state = componentEnt.getState().getExecutionState();
            } else {
                //
            }
            return Pair.create(e.getKey(), state);
        }).collect(Collectors.toMap(Pair::getFirst, Pair::getSecond));
    }

    /**
     * Tests the {@link WorkflowEnt#getAllowedActions()} property in partcular.
     */
    public void testGetAllowedActionsInfo() throws Exception {
        String wfId = loadWorkflow(TestWorkflowCollection.GENERAL_WEB_UI);

        WorkflowEnt workflow = ws().getWorkflow(wfId, NodeIDEnt.getRootID(), null, Boolean.TRUE).getWorkflow();

        // check the allowed actions on the workflow itself
        cr(workflow.getAllowedActions(), "allowedactions_root");

        // check for component, node and metanode
        cr(workflow.getAllowedActions(), "allowedactions_8");
        cr(workflow.getAllowedActions(), "allowedactions_12");
        cr(workflow.getAllowedActions(), "allowedactions_6");
    }

    /**
     * Tests the metadata of the project workflow and components.
     */
    public void testWorkflowAndComponentMetadata() throws Exception {
        String wfId = loadWorkflow(TestWorkflowCollection.METADATA);

        // checks the metadata of the project workflow
        WorkflowEnt workflow = ws().getWorkflow(wfId, NodeIDEnt.getRootID(), null, Boolean.FALSE).getWorkflow();
        cr(workflow.getMetadata(), "projectmetadataent");

        // checks the metadata of a component
        workflow = ws().getWorkflow(wfId, new NodeIDEnt(4), null, Boolean.FALSE).getWorkflow();
        cr(workflow.getMetadata(), "componentmetadataent_4");

        // check the metadata of a metanode (= project metadata)
        workflow = ws().getWorkflow(wfId, new NodeIDEnt(2), null, Boolean.FALSE).getWorkflow();
        cr(workflow.getMetadata(), "projectmetadataent");
    }

    /**
     * Tests {@link WorkflowService#restoreWorkflowVersion(String, String)}.}
     *
     * @throws Exception
     */
    public void testRestoreVersionHappyPath() throws Exception {
        var providerId = "Provider ID for testing";
        var spaceId = "Space ID for testing";

        var space = mock(Space.class);
        when(space.getId()).thenReturn(spaceId);

        var spaceProvider = createSpaceProvider(providerId, "Provider name for testing", space);
        var spaceProviderManager = createSpaceProvidersManager(spaceProvider);
        ServiceDependencies.setServiceDependency(SpaceProvidersManager.class, spaceProviderManager);

        var testWorkflowWithVersion = TestWorkflow.WithVersion.of( //
            TestWorkflowCollection.VERSIONS_CURRENT_STATE, //
            TestWorkflowCollection.VERSIONS_EARLIER_VERSION::getWorkflowDir //
        );
        var wfId = loadWorkflow(testWorkflowWithVersion);

        ws().restoreVersion(wfId, "42");
        verify(space).restoreItemVersion("Item ID for testing", VersionId.parse("42"));
    }

    /**
     * Tests {@link WorkflowService#restoreWorkflowVersion(String, String)}.}
     *
     * @throws Exception
     */
    public void testRestoreVersion() throws Exception {
        var providerId = "Provider ID for testing";
        var spaceId = "Space ID for testing";

        var space = mock(Space.class);
        when(space.getId()).thenReturn(spaceId);
        doThrow(new MutableServiceCallException(List.of(), false)).when(space).restoreItemVersion(anyString(),
            eq(VersionId.parse("666")));

        var spaceProvider = createSpaceProvider(providerId, "Provider name for testing", space);
        var spaceProviderManager = createSpaceProvidersManager(spaceProvider);
        ServiceDependencies.setServiceDependency(SpaceProvidersManager.class, spaceProviderManager);

        var testWorkflowWithVersion = TestWorkflow.WithVersion.of( //
            TestWorkflowCollection.VERSIONS_CURRENT_STATE, //
            TestWorkflowCollection.VERSIONS_EARLIER_VERSION::getWorkflowDir //
        );
        var wfId = loadWorkflow(testWorkflowWithVersion);

        // Happy path
        ws().restoreVersion(wfId, "42");
        verify(space).restoreItemVersion("Item ID for testing", VersionId.parse("42"));

        // Throws an exception
        assertThrows("Evil version used", ServiceCallException.class, () -> ws().restoreVersion(wfId, "666"));
        assertThrows("Non-existing project ID", Throwable.class,
            () -> ws().restoreVersion(wfId + "_diff", "42"));
    }

}

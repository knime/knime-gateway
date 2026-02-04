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
 *   Mar 7, 2025 (hornm): created
 */
package org.knime.gateway.testing.helper.webui;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.knime.gateway.api.entity.EntityBuilderManager.builder;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

import org.awaitility.Awaitility;
import org.knime.core.node.CanceledExecutionException;
import org.knime.core.node.ExecutionMonitor;
import org.knime.core.util.Version;
import org.knime.gateway.api.entity.NodeIDEnt;
import org.knime.gateway.api.util.CoreUtil;
import org.knime.gateway.api.webui.entity.AddComponentCommandEnt;
import org.knime.gateway.api.webui.entity.AddComponentPlaceholderResultEnt;
import org.knime.gateway.api.webui.entity.AutoConnectOptionsEnt;
import org.knime.gateway.api.webui.entity.ComponentNodeEnt;
import org.knime.gateway.api.webui.entity.ComponentPlaceholderEnt;
import org.knime.gateway.api.webui.entity.ComponentPlaceholderEnt.StateEnum;
import org.knime.gateway.api.webui.entity.ConnectionEnt;
import org.knime.gateway.api.webui.entity.DeleteCommandEnt.DeleteCommandEntBuilder;
import org.knime.gateway.api.webui.entity.PatchOpEnt.OpEnum;
import org.knime.gateway.api.webui.entity.ReplacementOptionsEnt;
import org.knime.gateway.api.webui.entity.SpaceGroupEnt;
import org.knime.gateway.api.webui.entity.WorkflowChangedEventTypeEnt.WorkflowChangedEventTypeEntBuilder;
import org.knime.gateway.api.webui.entity.WorkflowCommandEnt;
import org.knime.gateway.api.webui.entity.WorkflowCommandEnt.KindEnum;
import org.knime.gateway.api.webui.entity.WorkflowEnt;
import org.knime.gateway.api.webui.entity.XYEnt.XYEntBuilder;
import org.knime.gateway.impl.project.ProjectManager;
import org.knime.gateway.impl.webui.WorkflowMiddleware;
import org.knime.gateway.impl.webui.service.ServiceDependencies;
import org.knime.gateway.impl.webui.service.events.EventConsumer;
import org.knime.gateway.impl.webui.spaces.Space;
import org.knime.gateway.impl.webui.spaces.SpaceGroup;
import org.knime.gateway.impl.webui.spaces.SpaceProvider;
import org.knime.gateway.impl.webui.spaces.SpaceProvidersManager;
import org.knime.gateway.testing.helper.ResultChecker;
import org.knime.gateway.testing.helper.ServiceProvider;
import org.knime.gateway.testing.helper.TestWorkflowCollection;
import org.knime.gateway.testing.helper.WorkflowExecutor;
import org.knime.gateway.testing.helper.WorkflowLoader;
import org.mockito.Mockito;
import org.mockito.stubbing.Answer;

/**
 * Tests for execution of the {@link AddComponentCommandEnt}.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
public class AddComponentCommandTestHelper extends WebUIGatewayServiceTestHelper {

    private static final String TEST_ITEM_ID = "test-item-id";

    private static final String TEST_SPACE_ID = "test-space-id";

    private static final String TEST_COMPONENT_NAME = "component";

    private static final String TEST_COMPONENT_ITEM_NAME = "component name";

    private static final String TEST_PROVIDER_ID = "local-testing";

    private static final String TEST_PROVIDER_NAME = "local-testing-name";

    /**
     * Creates a test helper for AddComponent command execution.
     *
     * @param entityResultChecker result checker to validate responses
     * @param serviceProvider service provider backing the helper
     * @param workflowLoader workflow loader for test workflows
     * @param workflowExecutor workflow command executor
     */
    public AddComponentCommandTestHelper(final ResultChecker entityResultChecker, final ServiceProvider serviceProvider,
        final WorkflowLoader workflowLoader, final WorkflowExecutor workflowExecutor) {
        super(AddComponentCommandTestHelper.class, entityResultChecker, serviceProvider, workflowLoader,
            workflowExecutor);
    }

    /**
     * Test the AddComponent command.
     *
     * @throws Exception -
     */
    public void testAddComponentCommand() throws Exception {
        var space = createSpace(TEST_SPACE_ID, TEST_ITEM_ID, TEST_COMPONENT_ITEM_NAME, 200, null);
        var spaceProvider = createSpaceProvider(space);
        var spaceProviderManager = SpaceProviderUtilities.createSpaceProvidersManager(spaceProvider);
        ServiceDependencies.setServiceDependency(SpaceProvidersManager.class, spaceProviderManager);
        var events = Collections.synchronizedList(new ArrayList<Object>());
        ServiceDependencies.setServiceDependency(EventConsumer.class, (name, event) -> events.add(event));
        ServiceDependencies.setServiceDependency(WorkflowMiddleware.class,
            new WorkflowMiddleware(ProjectManager.getInstance(), spaceProviderManager));

        final String projectId = loadWorkflow(TestWorkflowCollection.HOLLOW);
        var command = builder(AddComponentCommandEnt.AddComponentCommandEntBuilder.class) //
            .setKind(KindEnum.ADD_COMPONENT) //
            .setPosition(builder(XYEntBuilder.class).setX(10).setY(20).build()) //
            .setProviderId(TEST_PROVIDER_ID) //
            .setSpaceId(TEST_SPACE_ID) //
            .setItemId(TEST_ITEM_ID) //
            .setName(TEST_COMPONENT_NAME) //
            .build();

        // in order to get proper workflow changes events subsequently (to have a version 0 to compare against)
        var snapshotId = ws().getWorkflow(projectId, NodeIDEnt.getRootID(), null, Boolean.TRUE).getSnapshotId();
        es().addEventListener(builder(WorkflowChangedEventTypeEntBuilder.class).setProjectId(projectId)
            .setWorkflowId(NodeIDEnt.getRootID()).setTypeId("WorkflowChangedEventType").setSnapshotId(snapshotId)
            .build());

        // the actual test -> execute add-component command
        var commandResult =
            (AddComponentPlaceholderResultEnt)ws().executeWorkflowCommand(projectId, NodeIDEnt.getRootID(), command);

        // check events
        var placeholderPatch = EventServiceTestHelper.waitAndFindPatchOpForPath("/componentPlaceholders", events);
        assertThat(placeholderPatch.getOp(), is(OpEnum.ADD));
        var placeholder = ((Collection<ComponentPlaceholderEnt>)placeholderPatch.getValue()).iterator().next();
        assertThat(placeholder.getId(), is(commandResult.getNewPlaceholderId()));
        assertThat(placeholder.getState(), is(StateEnum.LOADING));
        placeholderPatch = EventServiceTestHelper.waitAndFindPatchOpForPath("/componentPlaceholders/0/state", events);
        assertThat(placeholderPatch.getOp(), is(OpEnum.REPLACE));
        assertThat(placeholderPatch.getValue(), is(StateEnum.SUCCESS));
        placeholderPatch =
            EventServiceTestHelper.waitAndFindPatchOpForPath("/componentPlaceholders/0/componentId", events);
        assertThat(placeholderPatch.getOp(), is(OpEnum.ADD));
        assertThat(placeholderPatch.getValue(), is("root:3"));

        // check the added component
        var workflow = ws().getWorkflow(projectId, NodeIDEnt.getRootID(), null, Boolean.TRUE).getWorkflow();
        var component = workflow.getNodes().get("root:3");
        cr(component, "added_component");

        // make sure placeholders are cleaned up
        assertThat(workflow.getComponentPlaceholders(), is(nullValue()));
    }

    /**
     * Makes sure that the component load job is cancelled when parent workflow is disposed.
     *
     * @throws Exception
     */
    public void testCancelComponentLoadJobOnWorkflowRemoval() throws Exception {
        var wasCancelled = new AtomicBoolean();
        var space = createSpace(TEST_SPACE_ID, TEST_ITEM_ID, TEST_COMPONENT_ITEM_NAME, 200, wasCancelled);
        var spaceProvider = createSpaceProvider(space);
        var spaceProviderManager = SpaceProviderUtilities.createSpaceProvidersManager(spaceProvider);
        ServiceDependencies.setServiceDependency(SpaceProvidersManager.class, spaceProviderManager);
        var events = Collections.synchronizedList(new ArrayList<Object>());
        ServiceDependencies.setServiceDependency(EventConsumer.class, (name, event) -> events.add(event));
        ServiceDependencies.setServiceDependency(WorkflowMiddleware.class,
            new WorkflowMiddleware(ProjectManager.getInstance(), spaceProviderManager));

        final String projectId = loadWorkflow(TestWorkflowCollection.HOLLOW);
        WorkflowCommandEnt command = builder(AddComponentCommandEnt.AddComponentCommandEntBuilder.class) //
            .setKind(KindEnum.ADD_COMPONENT) //
            .setPosition(builder(XYEntBuilder.class).setX(10).setY(20).build()) //
            .setProviderId(TEST_PROVIDER_ID) //
            .setSpaceId(TEST_SPACE_ID) //
            .setItemId(TEST_ITEM_ID) //
            .setName(TEST_COMPONENT_NAME) //
            .build();

        // add component within metanode
        var metanodeId = new NodeIDEnt(1);
        ws().executeWorkflowCommand(projectId, metanodeId, command);

        // delete the metanode
        command = builder(DeleteCommandEntBuilder.class) //
            .setKind(KindEnum.DELETE) //
            .setNodeIds(List.of(metanodeId)) //
            .build();
        ws().executeWorkflowCommand(projectId, NodeIDEnt.getRootID(), command);

        // check
        Awaitility.await().untilAsserted(() -> assertThat(wasCancelled.get(), is(true)));
    }

    /**
     * Verifies that an add-component command with replacement options replaces the targeted node and returns a
     * placeholder id that resolves to the newly loaded component.
     *
     * @throws Exception on test failure
     */
    public void testAddComponentReplaceCommand() throws Exception {
        var space = AddComponentCommandTestHelper.createSpace( //
                TEST_SPACE_ID, //
                TEST_ITEM_ID, //
            TEST_COMPONENT_ITEM_NAME, //
            200, //
            null //
        );
        var spaceProvider = AddComponentCommandTestHelper.createSpaceProvider(space);
        var spaceProviderManager = SpaceProviderUtilities.createSpaceProvidersManager(spaceProvider);
        ServiceDependencies.setServiceDependency(SpaceProvidersManager.class, spaceProviderManager);
        ServiceDependencies.setServiceDependency(WorkflowMiddleware.class,
            new WorkflowMiddleware(ProjectManager.getInstance(), spaceProviderManager));

        final String projectId = loadWorkflow(TestWorkflowCollection.HOLLOW);
        var command = builder(AddComponentCommandEnt.AddComponentCommandEntBuilder.class) //
            .setKind(KindEnum.ADD_COMPONENT) //
            .setPosition(builder(XYEntBuilder.class).setX(10).setY(20).build()) //
            .setProviderId(TEST_PROVIDER_ID) //
            .setSpaceId(TEST_SPACE_ID) //
            .setItemId(TEST_ITEM_ID) //
            .setName(TEST_COMPONENT_NAME) //
            .setReplacementOptions(builder(ReplacementOptionsEnt.ReplacementOptionsEntBuilder.class)
                .setTargetNodeId(new NodeIDEnt(2)).build()) //
            .build();

        var commandResult = (AddComponentPlaceholderResultEnt)ws().executeWorkflowCommand( //
            projectId, //
            NodeIDEnt.getRootID(), //
            command //
        );
        assertThat(commandResult.getNewPlaceholderId(), is(notNullValue()));

        Awaitility.await().untilAsserted(() -> {
            var workflow = ws().getWorkflow(projectId, NodeIDEnt.getRootID(), null, Boolean.TRUE).getWorkflow();
            assertThat(workflow.getNodes().containsKey("root:2"), is(false));
            assertThat(workflow.getNodes().containsKey("root:3"), is(true));
            var componentNode = workflow.getNodes().get("root:3");
            assertThat(componentNode, is(instanceOf(ComponentNodeEnt.class)));
            assertThat(((ComponentNodeEnt)componentNode).getName(), is(TEST_COMPONENT_NAME));
        });
    }

    /**
     * Verifies undo/redo behavior when replacing an existing node with a component.
     * <p>
     * The test executes an {@code AddComponent} command with replacement options, then undoes and redoes the command
     * and asserts that the original node and the newly created component node appear and disappear as expected.
     *
     * @throws Exception on test failure
     */
    public void testAddComponentReplaceUndoRedo() throws Exception {
        var space = AddComponentCommandTestHelper.createSpace( //
                TEST_SPACE_ID, //
                TEST_ITEM_ID, //
            TEST_COMPONENT_ITEM_NAME, //
            200, //
            null //
        );
        var spaceProvider = AddComponentCommandTestHelper.createSpaceProvider(space);
        var spaceProviderManager = SpaceProviderUtilities.createSpaceProvidersManager(spaceProvider);
        ServiceDependencies.setServiceDependency(SpaceProvidersManager.class, spaceProviderManager);
        ServiceDependencies.setServiceDependency(WorkflowMiddleware.class,
            new WorkflowMiddleware(ProjectManager.getInstance(), spaceProviderManager));

        final String projectId = loadWorkflow(TestWorkflowCollection.HOLLOW);
        var command = builder(AddComponentCommandEnt.AddComponentCommandEntBuilder.class) //
            .setKind(KindEnum.ADD_COMPONENT) //
            .setPosition(builder(XYEntBuilder.class).setX(10).setY(20).build()) //
            .setProviderId(TEST_PROVIDER_ID) //
            .setSpaceId(TEST_SPACE_ID) //
            .setItemId(TEST_ITEM_ID) //
            .setName(TEST_COMPONENT_NAME) //
            .setReplacementOptions(builder(ReplacementOptionsEnt.ReplacementOptionsEntBuilder.class)
                .setTargetNodeId(new NodeIDEnt(2)).build()) //
            .build();

        ws().executeWorkflowCommand(projectId, NodeIDEnt.getRootID(), command);

        Awaitility.await().untilAsserted(() -> {
            var workflow = ws().getWorkflow(projectId, NodeIDEnt.getRootID(), null, Boolean.TRUE).getWorkflow();
            assertThat(workflow.getNodes().containsKey("root:2"), is(false));
            assertThat(findComponentNode(workflow, TEST_COMPONENT_NAME), is(notNullValue()));
        });

        ws().undoWorkflowCommand(projectId, NodeIDEnt.getRootID());

        Awaitility.await().untilAsserted(() -> {
            var workflow = ws().getWorkflow(projectId, NodeIDEnt.getRootID(), null, Boolean.TRUE).getWorkflow();
            assertThat(workflow.getNodes().containsKey("root:2"), is(true));
            assertThat(findComponentNode(workflow, TEST_COMPONENT_NAME), is(nullValue()));
        });

        ws().redoWorkflowCommand(projectId, NodeIDEnt.getRootID());

        Awaitility.await().untilAsserted(() -> {
            var workflow = ws().getWorkflow(projectId, NodeIDEnt.getRootID(), null, Boolean.TRUE).getWorkflow();
            assertThat(workflow.getNodes().containsKey("root:2"), is(false));
            assertThat(findComponentNode(workflow, TEST_COMPONENT_NAME), is(notNullValue()));
        });
    }

    /**
     * Verifies auto-connect behavior when adding a component and that the new component is connected as configured.
     *
     * @throws Exception on test failure
     */
    public void testAddComponentConnectCommand() throws Exception {
        var space = AddComponentCommandTestHelper.createSpace( //
                TEST_SPACE_ID, //
                TEST_ITEM_ID, //
            TEST_COMPONENT_ITEM_NAME, //
            200, //
            null //
        );
        var spaceProvider = AddComponentCommandTestHelper.createSpaceProvider(space);
        var spaceProviderManager = SpaceProviderUtilities.createSpaceProvidersManager(spaceProvider);
        ServiceDependencies.setServiceDependency(SpaceProvidersManager.class, spaceProviderManager);
        ServiceDependencies.setServiceDependency(WorkflowMiddleware.class,
            new WorkflowMiddleware(ProjectManager.getInstance(), spaceProviderManager));

        final String projectId = loadWorkflow(TestWorkflowCollection.GENERAL_WEB_UI);
        var initialWorkflow = ws().getWorkflow(projectId, NodeIDEnt.getRootID(), null, Boolean.TRUE).getWorkflow();
        var targetConnection = findConnection(initialWorkflow, "root:1", "root:4");
        var autoConnectOptions = builder(AutoConnectOptionsEnt.AutoConnectOptionsEntBuilder.class) //
            .setTargetNodeId(targetConnection.getDestNode()) //
            .setTargetNodePortIdx(targetConnection.getDestPort()) //
            .setNodeRelation(AutoConnectOptionsEnt.NodeRelationEnum.SUCCESSORS) //
            .build();
        var command = builder(AddComponentCommandEnt.AddComponentCommandEntBuilder.class) //
            .setKind(KindEnum.ADD_COMPONENT) //
            .setPosition(builder(XYEntBuilder.class).setX(10).setY(20).build()) //
            .setProviderId(TEST_PROVIDER_ID) //
            .setSpaceId(TEST_SPACE_ID) //
            .setItemId(TEST_ITEM_ID) //
            .setName(TEST_COMPONENT_NAME) //
            .setAutoConnectOptions(autoConnectOptions) //
            .build();

        ws().executeWorkflowCommand(projectId, NodeIDEnt.getRootID(), command);

        Awaitility.await().untilAsserted(() -> {
            var workflow = ws().getWorkflow(projectId, NodeIDEnt.getRootID(), null, Boolean.TRUE).getWorkflow();
            var component = findComponentNode(workflow, TEST_COMPONENT_NAME);
            assertThat(component, is(notNullValue()));
            var componentId = component.getId().toString();
            var hasConnection = workflow.getConnections().values().stream().anyMatch(connection ->
                connection.getSourceNode().toString().equals(targetConnection.getDestNode().toString())
                    && connection.getDestNode().toString().equals(componentId));
            assertThat(hasConnection, is(true));
        });
    }

    /**
     * Finds the first connection matching source and destination ids.
     *
     * @param workflow workflow to search
     * @param sourceNodeId source node id
     * @param destNodeId destination node id
     * @return matching connection
     */
    private static ConnectionEnt findConnection(final WorkflowEnt workflow, final String sourceNodeId,
        final String destNodeId) {
        return workflow.getConnections().values().stream()
            .filter(connection -> connection.getSourceNode().toString().equals(sourceNodeId)
                && connection.getDestNode().toString().equals(destNodeId))
            .findFirst()
            .orElseThrow();
    }

    /**
     * Finds the first component node with the given name.
     *
     * @param workflow workflow to search
     * @param name component name
     * @return component node or null if none exists
     */
    private static ComponentNodeEnt findComponentNode(final WorkflowEnt workflow, final String name) {
        return workflow.getNodes().values().stream().filter(ComponentNodeEnt.class::isInstance)
            .map(ComponentNodeEnt.class::cast).filter(node -> name.equals(node.getName())).findFirst().orElse(null);
    }

    /**
     * @param space the space to return on {@link SpaceProvider#getSpace(String)}
     */
    @SuppressWarnings("java:S1188") // anonymous test helper implementation
    static SpaceProvider createSpaceProvider(final Space space) {
        return new SpaceProvider() {

            @Override
            public void init(final Consumer<String> loginErrorHandler) {
                // do nothing
            }

            @Override
            public String getId() {
                return TEST_PROVIDER_ID;
            }

            @Override
            public List<SpaceGroupEnt> toEntity() {
                throw new UnsupportedOperationException();
            }

            @Override
            public String getName() {
                return TEST_PROVIDER_NAME;
            }

            @Override
            public Space getSpace(final String spaceId) {
                return Optional.of(space).filter(s -> s.getId().equals(spaceId)).orElseThrow();
            }

            @Override
            public Version getServerVersion() {
                throw new UnsupportedOperationException();
            }

            @Override
            public SpaceGroup<Space> getSpaceGroup(final String spaceGroupName) {
                throw new UnsupportedOperationException();
            }
        };
    }

    /**
     * Creates a mocked space with a component-item of the given id and name.
     *
     * @param spaceId -
     * @param componentItemId -
     * @param componentName -
     * @param toLocalAbsolutePathDelayInMs the time the {@link Space#toLocalAbsolutePath(ExecutionMonitor, String)}-call
     *            should be delayed until it returns - helps to mimic a longer component-loading operation
     * @param toLocalAbsolutePathWasCancelled will be set to true a cancellation was triggered while in the
     *            {@link Space#toLocalAbsolutePath(ExecutionMonitor, String)} method
     * @return a mocked space
     */
    @SuppressWarnings({"java:S112", "java:S1075", "java:S5612"})
    static Space createSpace(final String spaceId, final String componentItemId, final String componentName,
        final long toLocalAbsolutePathDelayInMs, final AtomicBoolean toLocalAbsolutePathWasCancelled) throws Exception {
        var spaceMock = Mockito.mock(Space.class);
        when(spaceMock.getId()).thenReturn(spaceId);
        when(spaceMock.getItemName(componentItemId)).thenReturn(componentName);
        var uri = new URI("knime://LOCAL/component/");
        when(spaceMock.toKnimeUrl(componentItemId)).thenReturn(uri);
        var componentPath = CoreUtil
            .resolveToFile("/files/test_workspace_to_list/component", AddComponentCommandTestHelper.class).toPath();
        Answer<Object> mockAnswer = i -> {
            // ensures that 'component loading' takes a bit longer to make the test deterministic
            // (when checking for the placeholder state - which is 'loading' on the first event)
            // and also makes it 'cancellable'
            var exec = (ExecutionMonitor)i.getArgument(0);
            var start = System.currentTimeMillis();
            while (System.currentTimeMillis() - start < toLocalAbsolutePathDelayInMs) {
                Thread.sleep(100);
                try {
                    exec.checkCanceled();
                } catch (CanceledExecutionException ex) {
                    if (toLocalAbsolutePathWasCancelled != null) {
                        toLocalAbsolutePathWasCancelled.set(true);
                    }
                    throw ex;
                }
            }
            return Optional.of(componentPath);
        };
        // take care to mock each overload to be more forgiving of implementation details
        when(spaceMock.toLocalAbsolutePath(any())).thenAnswer(mockAnswer);
        when(spaceMock.toLocalAbsolutePath(any(), any())).thenAnswer(mockAnswer);
        when(spaceMock.toLocalAbsolutePath(any(), any(), any())).thenAnswer(mockAnswer);
        return spaceMock;
    }

}

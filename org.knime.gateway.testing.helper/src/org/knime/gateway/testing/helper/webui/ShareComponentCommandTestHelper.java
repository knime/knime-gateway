/*
 * ------------------------------------------------------------------------
 *
 *  Copyright by KNIME AG, Zurich, Switzerland
 *  Website: http://www.knime.com; Email: contact@knime.com
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
 *   Created on Nov 6, 2024 by Assistant
 */
package org.knime.gateway.testing.helper.webui;

import org.knime.core.node.BufferedDataTable;
import org.knime.core.node.workflow.NodeID;
import org.knime.core.node.workflow.SubNodeContainer;
import org.knime.core.node.workflow.WorkflowManager;
import org.knime.gateway.api.entity.NodeIDEnt;
import org.knime.gateway.api.webui.entity.CollapseCommandEnt;
import org.knime.gateway.api.webui.entity.CollapseResultEnt;
import org.knime.gateway.api.webui.entity.LinkVariantEnt;
import org.knime.gateway.api.webui.entity.ShareComponentCommandEnt;
import org.knime.gateway.api.webui.entity.ShareComponentCommandEnt.CollisionHandlingEnum;
import org.knime.gateway.api.webui.entity.ShareComponentCommandEnt.ShareComponentCommandEntBuilder;
import org.knime.gateway.api.webui.entity.ShareComponentResultEnt;
import org.knime.gateway.api.webui.entity.SpaceGroupEnt;
import org.knime.gateway.api.webui.entity.WorkflowCommandEnt.KindEnum;
import org.knime.gateway.api.webui.service.WorkflowService;
import org.knime.gateway.api.webui.service.util.MutableServiceCallException;
import org.knime.gateway.api.webui.service.util.ServiceExceptions;
import org.knime.gateway.impl.project.ProjectManager;
import org.knime.gateway.impl.webui.WorkflowMiddleware;
import org.knime.gateway.impl.webui.service.ServiceDependencies;
import org.knime.gateway.impl.webui.spaces.Space;
import org.knime.gateway.impl.webui.spaces.SpaceProvider;
import org.knime.gateway.impl.webui.spaces.SpaceProvidersManager;
import org.knime.gateway.testing.helper.ResultChecker;
import org.knime.gateway.testing.helper.ServiceProvider;
import org.knime.gateway.testing.helper.TestWorkflowCollection;
import org.knime.gateway.testing.helper.WorkflowExecutor;
import org.knime.gateway.testing.helper.WorkflowLoader;
import org.knime.gateway.testing.helper.webui.node.NoOpDummyNodeFactory;

import java.net.URI;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.knime.gateway.api.entity.EntityBuilderManager.builder;
import static org.knime.gateway.api.util.KnimeUrls.buildLinkVariantEnt;
import static org.mockito.AdditionalMatchers.not;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Tests for execution of the {@link ShareComponentCommandEnt}.
 *
 * @author Assistant
 */
public class ShareComponentCommandTestHelper extends WebUIGatewayServiceTestHelper {

    @SuppressWarnings("javadoc")
    public ShareComponentCommandTestHelper(final ResultChecker entityResultChecker, final ServiceProvider serviceProvider,
                                           final WorkflowLoader workflowLoader, final WorkflowExecutor workflowExecutor) {
        super(ShareComponentCommandTestHelper.class, entityResultChecker, serviceProvider, workflowLoader,
            workflowExecutor);
    }

    /**
     * Test the ShareComponent command happy path.
     *
     * @throws Exception -
     */
    public void testShareComponentCommand() throws Exception {
        var destSpaceId = "test-dest-space-id";
        var destItemId = "test-dest-item-id";
        var componentName = "test-component";
        
        // Create a mock destination space
        var destinationSpace = createMockDestinationSpace(destSpaceId, destItemId, componentName);
        var spaceProvider = createSpaceProvider(destinationSpace);
        var spaceProviderManager = SpaceServiceTestHelper.createSpaceProvidersManager(spaceProvider);
        ServiceDependencies.setServiceDependency(SpaceProvidersManager.class, spaceProviderManager);
        var projectManager = ProjectManager.getInstance();
        ServiceDependencies.setServiceDependency(WorkflowMiddleware.class,
            new WorkflowMiddleware(projectManager, spaceProviderManager));

        // Load a workflow to host our test component
        final var projectId = loadWorkflow(TestWorkflowCollection.HOLLOW);
        final var wfm = projectManager.getProject(projectId).orElseThrow().getWorkflowManagerIfLoaded().orElseThrow();

        // Create a node and encapsulate it in a component
        final var componentId = createComponentInWorkflow(wfm, projectId, ws());

        // Build the ShareComponent command
        var command = builder(ShareComponentCommandEntBuilder.class) //
            .setKind(KindEnum.SHARE_COMPONENT) //
            .setNodeId(new NodeIDEnt(componentId)) //
            .setDestinationSpaceProviderId("local-testing") //
            .setDestinationSpaceId(destSpaceId) //
            .setDestinationItemId(destItemId) //
            .setCollisionHandling(CollisionHandlingEnum.NOOP) //
            .setIncludeInputData(false) //
            .setLinkVariant(buildLinkVariantEnt(LinkVariantEnt.VariantEnum.MOUNTPOINT_ABSOLUTE_ID)) //
            .build();

        // Execute the ShareComponent command
        var commandResult = (ShareComponentResultEnt)ws().executeWorkflowCommand(projectId, NodeIDEnt.getRootID(), command);

        // Verify the result
        assertThat(commandResult, is(notNullValue()));
        assertThat(commandResult.isNameCollision(), is(false));

        // Verify the component was shared (check template information)
        final var component = wfm.getNodeContainer(componentId, SubNodeContainer.class, false);
        assertThat(component, is(notNullValue()));
        assertThat(component.getTemplateInformation().getRole().name(), 
            is(org.knime.core.node.workflow.MetaNodeTemplateInformation.Role.Link.name()));
    }

    /**
     * Test the ShareComponent command with name collision detection.
     *
     * @throws Exception -
     */
    public void testShareComponentCommandWithNameCollision() throws Exception {
        var destSpaceId = "test-dest-space-id";
        var destItemId = "test-dest-item-id";
        var componentName = "test-component";
        
        // Create a mock destination space that reports a collision
        var destinationSpace = createMockDestinationSpaceWithCollision(destSpaceId, destItemId, componentName);
        var spaceProvider = createSpaceProvider(destinationSpace);
        var spaceProviderManager = SpaceServiceTestHelper.createSpaceProvidersManager(spaceProvider);
        ServiceDependencies.setServiceDependency(SpaceProvidersManager.class, spaceProviderManager);
        var projectManager = ProjectManager.getInstance();
        ServiceDependencies.setServiceDependency(WorkflowMiddleware.class,
            new WorkflowMiddleware(projectManager, spaceProviderManager));

        // Load a workflow to host our test component
        final var projectId = loadWorkflow(TestWorkflowCollection.HOLLOW);
        final var wfm = projectManager.getProject(projectId).orElseThrow().getWorkflowManagerIfLoaded().orElseThrow();
        
        // Create a node and encapsulate it in a component
        final var componentId = createComponentInWorkflow(wfm, projectId, ws());

        // Build the ShareComponent command WITHOUT collision handling
        var command = builder(ShareComponentCommandEntBuilder.class) //
            .setKind(KindEnum.SHARE_COMPONENT) //
            .setNodeId(new NodeIDEnt(componentId)) //
            .setDestinationSpaceProviderId("local-testing") //
            .setDestinationSpaceId(destSpaceId) //
            .setDestinationItemId(destItemId) //
            .setIncludeInputData(false) //
            .setLinkVariant(buildLinkVariantEnt(LinkVariantEnt.VariantEnum.MOUNTPOINT_ABSOLUTE_ID)) //
            .build();

        // Execute the ShareComponent command
        var commandResult = (ShareComponentResultEnt)ws().executeWorkflowCommand(projectId, NodeIDEnt.getRootID(), command);

        // Verify the collision was detected
        assertThat(commandResult, is(notNullValue()));
        assertThat(commandResult.isNameCollision(), is(true));
    }

    /**
     * Test the ShareComponent command with autorename collision handling.
     *
     * @throws Exception -
     */
    public void testShareComponentCommandWithAutorename() throws Exception {
        var destSpaceId = "test-dest-space-id";
        var destItemId = "test-dest-item-id";
        var componentName = "test-component";
        var renamedItemId = "test-dest-item-id-renamed";
        
        // Create a mock destination space that reports a collision but supports autorename
        var destinationSpace = createMockDestinationSpaceWithAutorename(destSpaceId, destItemId, renamedItemId, componentName);
        var spaceProvider = createSpaceProvider(destinationSpace);
        var spaceProviderManager = SpaceServiceTestHelper.createSpaceProvidersManager(spaceProvider);
        ServiceDependencies.setServiceDependency(SpaceProvidersManager.class, spaceProviderManager);
        var projectManager = ProjectManager.getInstance();
        ServiceDependencies.setServiceDependency(WorkflowMiddleware.class,
            new WorkflowMiddleware(projectManager, spaceProviderManager));

        // Load a workflow to host our test component
        final String projectId = loadWorkflow(TestWorkflowCollection.HOLLOW);
        final var wfm = projectManager.getProject(projectId).orElseThrow().getWorkflowManagerIfLoaded().orElseThrow();
        
        // Create a node and encapsulate it in a component
        final var componentId = createComponentInWorkflow(wfm, projectId, ws());

        // Build the ShareComponent command WITH autorename collision handling
        var command = builder(ShareComponentCommandEntBuilder.class) //
            .setKind(KindEnum.SHARE_COMPONENT) //
            .setNodeId(new NodeIDEnt(componentId)) //
            .setDestinationSpaceProviderId("local-testing") //
            .setDestinationSpaceId(destSpaceId) //
            .setDestinationItemId(destItemId) //
            .setCollisionHandling(CollisionHandlingEnum.AUTORENAME) //
            .setIncludeInputData(false) //
            .setLinkVariant(buildLinkVariantEnt(LinkVariantEnt.VariantEnum.MOUNTPOINT_ABSOLUTE_ID)) //
            .build();

        // Execute the ShareComponent command
        var commandResult = (ShareComponentResultEnt)ws().executeWorkflowCommand(projectId, NodeIDEnt.getRootID(), command);

        // Verify the command succeeded and item was renamed
        assertThat(commandResult, is(notNullValue()));
        assertThat(commandResult.isNameCollision(), is(false));

        // Verify the component was shared (check template information)
        final var component = wfm.getNodeContainer(componentId, SubNodeContainer.class, false);
        assertThat(component, is(notNullValue()));
        assertThat(component.getTemplateInformation().getRole().name(), 
            is(org.knime.core.node.workflow.MetaNodeTemplateInformation.Role.Link.name()));
    }

    /**
     * Creates a node and encapsulates it in a component (SubNodeContainer).
     */
    static NodeID createComponentInWorkflow(WorkflowManager wfm, String projectId, WorkflowService workflowService) throws Exception {
        // Create a simple test node using NoOpDummyNodeFactory
        var nodeFactory = new NoOpDummyNodeFactory() {
            @Override
            protected Optional<PortsConfigurationBuilder> createPortsConfigBuilder() {
                var builder = new PortsConfigurationBuilder();
                builder.addFixedInputPortGroup("input", BufferedDataTable.TYPE);
                builder.addFixedOutputPortGroup("output", BufferedDataTable.TYPE);
                return Optional.of(builder);
            }
        };
        
        // Create and add the node to the workflow
        var nnc = org.knime.testing.util.WorkflowManagerUtil.createAndAddNode(wfm, nodeFactory);
        
        // Collapse the node into a component
        var command = builder(CollapseCommandEnt.CollapseCommandEntBuilder.class)
            .setKind(KindEnum.COLLAPSE)
            .setContainerType(CollapseCommandEnt.ContainerTypeEnum.COMPONENT)
            .setNodeIds(List.of(new NodeIDEnt(nnc.getID())))
            .setAnnotationIds(Collections.emptyList())
            .build();
        
        var result = (CollapseResultEnt)workflowService.executeWorkflowCommand(
            projectId, NodeIDEnt.getRootID(), command);
        
        // Return the ID of the newly created component
        return result.getNewNodeId().toNodeID(wfm);
    }

    /**
     * Creates a space provider with the given space.
     */
    static SpaceProvider createSpaceProvider(final Space space) {
        return new SpaceProvider() {
            @Override
            public void init(Consumer<String> loginErrorHandler) {

            }

            @Override
            public String getId() {
                return "local-testing";
            }

            @Override
            public String getName() {
                return "local-testing-name";
            }

            @Override
            public Space getSpace(final String spaceId) {
                return Optional.of(space).filter(s -> s.getId().equals(spaceId)).orElseThrow();
            }

            @Override
            public org.knime.core.util.Version getServerVersion() {
                throw new UnsupportedOperationException();
            }

            @Override
            public org.knime.gateway.impl.webui.spaces.SpaceGroup<Space> getSpaceGroup(final String spaceGroupName) {
                throw new UnsupportedOperationException();
            }

            @Override
            public List<SpaceGroupEnt> toEntity() throws ServiceExceptions.NetworkException, ServiceExceptions.LoggedOutException, MutableServiceCallException {
                return List.of();
            }
        };
    }

    /**
     * Creates a mock destination space for successful sharing.
     */
    @SuppressWarnings("java:S112") // raw `Exception` is OK in tests
    static Space createMockDestinationSpace(final String spaceId, final String destItemId, final String componentName) throws Exception {
        var spaceMock = mock(Space.class);
        when(spaceMock.getId()).thenReturn(spaceId);
        when(spaceMock.getItemName(anyString())).thenReturn(componentName);
        
        // No collision
        when(spaceMock.getItemIdForName(anyString(), anyString())).thenReturn(Optional.empty());
        
        // Mock space upload methods
        var uri = new URI("knime://LOCAL/shared-component/");
        when(spaceMock.toKnimeUrl(anyString())).thenReturn(uri);
        when(spaceMock.toPathBasedKnimeUrl(anyString())).thenReturn(uri);
        
        // Mock importWorkflowOrWorkflowGroup method - this is what ShareComponent actually calls
        var mockItemEnt = mock(org.knime.gateway.api.webui.entity.SpaceItemEnt.class);
        when(mockItemEnt.getId()).thenReturn(destItemId);
        when(mockItemEnt.getName()).thenReturn(componentName);
        when(spaceMock.importWorkflowOrWorkflowGroup(any(Path.class), anyString(), any(), any(), any()))
            .thenReturn(mockItemEnt);
        
        return spaceMock;
    }

    /**
     * Creates a mock destination space that reports a name collision.
     */
    @SuppressWarnings("java:S112") // raw `Exception` is OK in tests
    private static Space createMockDestinationSpaceWithCollision(final String spaceId, final String destItemId, final String componentName) throws Exception {
        var spaceMock = mock(Space.class);
        when(spaceMock.getId()).thenReturn(spaceId);
        when(spaceMock.getItemName(anyString())).thenReturn(componentName);
        
        // Simulate collision - item already exists
        when(spaceMock.getItemIdForName(anyString(), anyString())).thenReturn(Optional.of("existing-item-id"));
        
        // Mock space URLs (though these won't be called in collision case)
        var uri = new URI("knime://LOCAL/shared-component/");
        when(spaceMock.toKnimeUrl(anyString())).thenReturn(uri);
        when(spaceMock.toPathBasedKnimeUrl(anyString())).thenReturn(uri);
        
        return spaceMock;
    }

    /**
     * Creates a mock destination space that reports a name collision but supports autorename.
     */
    @SuppressWarnings("java:S112") // raw `Exception` is OK in tests
    private static Space createMockDestinationSpaceWithAutorename(final String spaceId, final String destItemId, 
            final String renamedItemId, final String componentName) throws Exception {
        var spaceMock = mock(Space.class);
        when(spaceMock.getId()).thenReturn(spaceId);
        when(spaceMock.getItemName(anyString())).thenReturn(componentName);
        
        // Simulate collision for the original name, but no collision for autorename
        when(spaceMock.getItemIdForName(anyString(), eq(componentName))).thenReturn(Optional.of("existing-item-id"));
        when(spaceMock.getItemIdForName(anyString(), not(eq(componentName)))).thenReturn(Optional.empty());
        
        // Mock space URLs
        var uri = new URI("knime://LOCAL/shared-component/");
        when(spaceMock.toKnimeUrl(anyString())).thenReturn(uri);
        when(spaceMock.toPathBasedKnimeUrl(anyString())).thenReturn(uri);
        
        // Mock importWorkflowOrWorkflowGroup method to return the renamed item
        var mockItemEnt = mock(org.knime.gateway.api.webui.entity.SpaceItemEnt.class);
        when(mockItemEnt.getId()).thenReturn(renamedItemId);
        when(mockItemEnt.getName()).thenReturn(componentName); // The component keeps its original name
        when(spaceMock.importWorkflowOrWorkflowGroup(any(Path.class), anyString(), any(), any(), any()))
            .thenReturn(mockItemEnt);
        
        return spaceMock;
    }
}
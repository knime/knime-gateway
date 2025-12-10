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

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

import org.knime.core.node.BufferedDataTable;
import org.knime.core.node.CanceledExecutionException;
import org.knime.core.node.workflow.NodeID;
import org.knime.core.node.workflow.SubNodeContainer;
import org.knime.core.node.workflow.WorkflowManager;
import org.knime.core.node.workflow.contextv2.WorkflowContextV2;
import org.knime.core.util.exception.ResourceAccessException;
import org.knime.gateway.api.entity.NodeIDEnt;
import org.knime.gateway.api.webui.entity.CollapseCommandEnt;
import org.knime.gateway.api.webui.entity.CollapseResultEnt;
import org.knime.gateway.api.webui.entity.LinkVariantEnt;
import org.knime.gateway.api.webui.entity.LinkVariantInfoEnt;
import org.knime.gateway.api.webui.entity.ShareComponentCommandEnt;
import org.knime.gateway.api.webui.entity.ShareComponentCommandEnt.CollisionHandlingEnum;
import org.knime.gateway.api.webui.entity.ShareComponentCommandEnt.ShareComponentCommandEntBuilder;
import org.knime.gateway.api.webui.entity.ShareComponentResultEnt;
import org.knime.gateway.api.webui.entity.SpaceGroupEnt;
import org.knime.gateway.api.webui.entity.SpaceItemEnt;
import org.knime.gateway.api.webui.entity.WorkflowCommandEnt.KindEnum;
import org.knime.gateway.api.webui.service.WorkflowService;
import org.knime.gateway.api.webui.service.util.MutableServiceCallException;
import org.knime.gateway.api.webui.service.util.ServiceExceptions;
import org.knime.gateway.impl.project.ProjectManager;
import org.knime.gateway.impl.webui.WorkflowMiddleware;
import org.knime.gateway.impl.webui.service.ServiceDependencies;
import org.knime.gateway.impl.webui.spaces.LinkVariants;
import org.knime.gateway.impl.webui.spaces.Space;
import org.knime.gateway.impl.webui.spaces.SpaceGroup;
import org.knime.gateway.impl.webui.spaces.SpaceProvider;
import org.knime.gateway.impl.webui.spaces.SpaceProvidersManager;
import org.knime.gateway.testing.helper.ResultChecker;
import org.knime.gateway.testing.helper.ServiceProvider;
import org.knime.gateway.testing.helper.TestWorkflowCollection;
import org.knime.gateway.testing.helper.WorkflowExecutor;
import org.knime.gateway.testing.helper.WorkflowLoader;
import org.knime.gateway.testing.helper.webui.node.NoOpDummyNodeFactory;

/**
 * Tests for execution of the {@link ShareComponentCommandEnt}.
 *
 * @author
 */
@SuppressWarnings({"java:S1130", "java:S1186", "java:S112", "java:S1188"})
public class ShareComponentCommandTestHelper extends WebUIGatewayServiceTestHelper {

    @SuppressWarnings("javadoc")
    public ShareComponentCommandTestHelper(final ResultChecker entityResultChecker,
        final ServiceProvider serviceProvider, final WorkflowLoader workflowLoader,
        final WorkflowExecutor workflowExecutor) {
        super(ShareComponentCommandTestHelper.class, entityResultChecker, serviceProvider, workflowLoader,
            workflowExecutor);
    }

    private record TestContext(Map<String, Space> spaces, Map<LinkVariantEnt.VariantEnum, URI> variants,
            SpaceProvider provider, String projectId, WorkflowManager wfm, NodeID componentId) {

        SubNodeContainer getComponent() {
            return this.wfm().getNodeContainer(this.componentId(), SubNodeContainer.class, false);
        }

        ShareComponentResultEnt executeCommand(ShareComponentCommandEnt command, WorkflowService workflowService)
            throws ServiceExceptions.ServiceCallException {
            return (ShareComponentResultEnt)workflowService.executeWorkflowCommand(this.projectId(),
                NodeIDEnt.getRootID(), command);
        }

    }

    private TestContext m_context;

    private void setUp() throws Exception {

        var provider = new SpaceProvider() {

            @Override
            public void init(Consumer<String> loginErrorHandler) {

            }

            @Override
            public String getId() {
                return "provider-id";
            }

            @Override
            public String getName() {
                return "provider-name";
            }

            @Override
            public Space getSpace(String spaceId) throws ServiceExceptions.NetworkException,
                ServiceExceptions.LoggedOutException, MutableServiceCallException {
                return m_context.spaces().get(spaceId);
            }

            @Override
            public SpaceGroup<? extends Space> getSpaceGroup(String spaceGroupName)
                throws ServiceExceptions.NetworkException, ServiceExceptions.LoggedOutException,
                MutableServiceCallException {
                throw new UnsupportedOperationException();
            }

            @Override
            public List<SpaceGroupEnt> toEntity() throws ServiceExceptions.NetworkException,
                ServiceExceptions.LoggedOutException, MutableServiceCallException {
                return List.of();
            }
        };

        var spaceProvidersManager = SpaceProviderUtilities.createSpaceProvidersManager(provider);

        ServiceDependencies.setServiceDependency(SpaceProvidersManager.class, spaceProvidersManager);

        ServiceDependencies.setServiceDependency(WorkflowMiddleware.class,
            new WorkflowMiddleware(ProjectManager.getInstance(), spaceProvidersManager));

        ServiceDependencies.setServiceDependency(LinkVariants.class, new LinkVariants() {
            @Override
            public LinkVariantInfoEnt toLinkVariantInfoEnt(LinkVariantEnt.VariantEnum type) {
                return null;
            }

            @Override
            public Map<LinkVariantEnt.VariantEnum, URI> getVariants(URI originalUri, WorkflowContextV2 currentContext)
                throws ResourceAccessException {
                return m_context.variants();
            }
        });

        final var projectId = loadWorkflow(TestWorkflowCollection.HOLLOW);
        final var wfm =
            ProjectManager.getInstance().getProject(projectId).orElseThrow().getWorkflowManagerIfLoaded().orElseThrow();
        // Create a node and encapsulate it in a component
        final var componentId = createComponentInWorkflow(wfm, projectId, ws());

        m_context = new TestContext( //
            new HashMap<>(), //
            new EnumMap<>(LinkVariantEnt.VariantEnum.class), //
            provider, projectId, wfm, componentId //
        );

    }

    private void tearDown() {
        m_context = null;
    }

    private static Space mockSpace()
        throws CanceledExecutionException, MutableServiceCallException, ServiceExceptions.NetworkException,
        ServiceExceptions.LoggedOutException, ServiceExceptions.OperationNotAllowedException, URISyntaxException {
        var space = mock(Space.class);
        when(space.getId()).thenReturn("some-space-id");

        var uri = new URI("knime://provider-id/destination-group"); // NOSONAR
        when(space.toKnimeUrl(anyString())).thenReturn(uri);
        when(space.toPathBasedKnimeUrl(anyString())).thenReturn(uri);

        return space;
    }

    /**
     * Test the ShareComponent command happy path.
     *
     * @throws Exception -
     */
    public void testShareComponentCommand() throws Exception {
        setUp();
        var space = mockSpace();
        m_context.spaces().put(space.getId(), space);
        // no collisions
        when(space.getItemIdForName(anyString(), anyString())).thenReturn(Optional.empty());

        var linkVariant = LinkVariantEnt.VariantEnum.MOUNTPOINT_ABSOLUTE_ID;
        m_context.variants().put(linkVariant, new URI("foo"));

        var importedItemId = "imported-item-id";
        when(space.importWorkflowOrWorkflowGroup( //
            any(Path.class), //
            anyString(), //
            any(), //
            any(), //
            any()) //
        ).thenReturn(builder(SpaceItemEnt.SpaceItemEntBuilder.class) //
            .setId(importedItemId) //
            .setName("imported-item-name") //
            .setType(SpaceItemEnt.TypeEnum.COMPONENT) //
            .build());

        // Build the ShareComponent command
        // Execute the ShareComponent command
        var commandResult = m_context.executeCommand(builder(ShareComponentCommandEntBuilder.class) //
            .setKind(KindEnum.SHARE_COMPONENT) //
            .setNodeId(new NodeIDEnt(m_context.componentId())) //
            .setDestinationSpaceProviderId(m_context.provider().getId()) //
            .setDestinationSpaceId(space.getId()) //
            .setDestinationItemId("some-item-id") //
            .setCollisionHandling(CollisionHandlingEnum.NOOP) //
            .setIncludeInputData(false) //
            .setLinkVariant(buildLinkVariantEnt(linkVariant)) //
            .build(), ws());

        // Verify command result
        assertThat(commandResult, is(notNullValue()));
        assertThat(commandResult.isNameCollision(), is(false));
        assertThat(commandResult.getUploadedItem().getProviderId(), is(m_context.provider().getId()));
        assertThat(commandResult.getUploadedItem().getSpaceId(), is(space.getId()));
        assertThat(commandResult.getUploadedItem().getItemId(), is(importedItemId));

        // Verify workflow state
        final var component = m_context.getComponent();
        // component exists
        assertThat(component, is(notNullValue()));
        // component is now a link
        assertThat(component.getTemplateInformation().getRole().name(),
            is(org.knime.core.node.workflow.MetaNodeTemplateInformation.Role.Link.name()));
        var componentLinkTarget = component.getTemplateInformation().getSourceURI();
        // component link points to URI provided by LinkVariants
        assertThat(componentLinkTarget, is(new URI("foo")));
        tearDown();
    }

    /**
     * Test the ShareComponent command with name collision detection.
     *
     * @throws Exception -
     */
    public void testShareComponentCommandWithNameCollision() throws Exception {
        setUp();
        var space = mockSpace();
        m_context.spaces().put(space.getId(), space);
        // Force name collision
        when(space.getItemIdForName(anyString(), anyString())) //
            .thenReturn(Optional.of("existing-item-id"));

        var variant = LinkVariantEnt.VariantEnum.MOUNTPOINT_ABSOLUTE_ID;
        m_context.variants().put(variant, new URI("foo"));

        var importedItemId = "imported-item-id";
        when(space.importWorkflowOrWorkflowGroup( //
            any(Path.class), //
            anyString(), //
            any(), //
            any(), //
            any()) //
        ).thenReturn(builder(SpaceItemEnt.SpaceItemEntBuilder.class) //
            .setId(importedItemId) //
            .setName("imported-item-name") //
            .setType(SpaceItemEnt.TypeEnum.COMPONENT) //
            .build());

        // Build the ShareComponent command WITHOUT collision handling

        // Execute the ShareComponent command
        var commandResult = m_context.executeCommand(builder(ShareComponentCommandEntBuilder.class) //
            .setKind(KindEnum.SHARE_COMPONENT) //
            .setNodeId(new NodeIDEnt(m_context.componentId())) //
            .setDestinationSpaceProviderId(m_context.provider().getId()) //
            .setDestinationSpaceId(space.getId()) //
            .setDestinationItemId("some-item-id") //
            .setIncludeInputData(false) //
            .setLinkVariant(buildLinkVariantEnt(variant)) //
            .build(), ws());

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
        setUp();
        var space = mockSpace();
        m_context.spaces().put(space.getId(), space);

        final var component = m_context.wfm().getNodeContainer(m_context.componentId(), SubNodeContainer.class, false);
        // facilitate name collision
        when(space.getItemIdForName(anyString(), eq(component.getName()))) //
            .thenReturn(Optional.of("existing-item-id"));
        when(space.getItemIdForName(anyString(), not(eq(component.getName())))) //
            .thenReturn(Optional.empty());

        var importedItemId = "imported-item-id";
        when(space.importWorkflowOrWorkflowGroup( //
            any(Path.class), //
            anyString(), //
            any(), //
            any(), //
            any()) //
        ).thenReturn(builder(SpaceItemEnt.SpaceItemEntBuilder.class) //
            .setId(importedItemId) //
            .setName("imported-item-name") //
            .setType(SpaceItemEnt.TypeEnum.COMPONENT) //
            .build());

        var linkVariant = LinkVariantEnt.VariantEnum.MOUNTPOINT_ABSOLUTE_ID;
        m_context.variants().put(linkVariant, new URI("foo"));

        // Execute the ShareComponent command
        var commandResult = m_context.executeCommand(builder(ShareComponentCommandEntBuilder.class) //
            .setKind(KindEnum.SHARE_COMPONENT) //
            .setNodeId(new NodeIDEnt(m_context.componentId())) //
            .setDestinationSpaceProviderId(m_context.provider().getId()) //
            .setDestinationSpaceId(space.getId()) //
            .setDestinationItemId("some-item-id") //
            // Build the ShareComponent command WITH autorename collision handling
            .setCollisionHandling(CollisionHandlingEnum.AUTORENAME) //
            .setIncludeInputData(false) //
            .setLinkVariant(buildLinkVariantEnt(linkVariant)) //
            .build(), ws());

        // Verify the command succeeded and item was renamed
        assertThat(commandResult, is(notNullValue()));
        assertThat(commandResult.isNameCollision(), is(false));

        // Verify the component was shared (check template information)
        assertThat(component, is(notNullValue()));
        assertThat(component.getTemplateInformation().getRole().name(),
            is(org.knime.core.node.workflow.MetaNodeTemplateInformation.Role.Link.name()));
        tearDown();
    }

    /**
     * Creates a node and encapsulates it in a component (SubNodeContainer).
     */
    static NodeID createComponentInWorkflow(final WorkflowManager wfm, final String projectId,
        final WorkflowService workflowService) throws Exception {
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
        var command = builder(CollapseCommandEnt.CollapseCommandEntBuilder.class) //
            .setKind(KindEnum.COLLAPSE) //
            .setContainerType(CollapseCommandEnt.ContainerTypeEnum.COMPONENT) //
            .setNodeIds(List.of(new NodeIDEnt(nnc.getID()))) //
            .setAnnotationIds(Collections.emptyList()) //
            .build(); //

        var result =
            (CollapseResultEnt)workflowService.executeWorkflowCommand(projectId, NodeIDEnt.getRootID(), command);

        // Return the ID of the newly created component
        return result.getNewNodeId().toNodeID(wfm);
    }

}

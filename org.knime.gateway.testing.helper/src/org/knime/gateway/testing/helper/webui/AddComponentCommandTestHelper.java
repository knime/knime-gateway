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
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.knime.gateway.api.entity.EntityBuilderManager.builder;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

import org.knime.core.node.CanceledExecutionException;
import org.knime.core.util.Version;
import org.knime.gateway.api.entity.NodeIDEnt;
import org.knime.gateway.api.util.CoreUtil;
import org.knime.gateway.api.webui.entity.AddComponentCommandEnt;
import org.knime.gateway.api.webui.entity.AddComponentCommandEnt.AddComponentCommandEntBuilder;
import org.knime.gateway.api.webui.entity.AddComponentPlaceholderResultEnt;
import org.knime.gateway.api.webui.entity.ComponentPlaceholderEnt;
import org.knime.gateway.api.webui.entity.ComponentPlaceholderEnt.StateEnum;
import org.knime.gateway.api.webui.entity.PatchOpEnt.OpEnum;
import org.knime.gateway.api.webui.entity.SpaceGroupEnt;
import org.knime.gateway.api.webui.entity.WorkflowChangedEventTypeEnt.WorkflowChangedEventTypeEntBuilder;
import org.knime.gateway.api.webui.entity.WorkflowCommandEnt.KindEnum;
import org.knime.gateway.api.webui.entity.XYEnt.XYEntBuilder;
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

/**
 * Tests for execution of the {@link AddComponentCommandEnt}.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
public class AddComponentCommandTestHelper extends WebUIGatewayServiceTestHelper {

    @SuppressWarnings("javadoc")
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
        var itemId = "test-item-id";
        var spaceId = "test-space-id";
        var space = createSpace(spaceId, itemId, "component name", 200);
        var spaceProvider = createSpaceProvider(space);
        var spaceProviderManager = SpaceServiceTestHelper.createSpaceProvidersManager(spaceProvider);
        ServiceDependencies.setServiceDependency(SpaceProvidersManager.class, spaceProviderManager);
        var events = Collections.synchronizedList(new ArrayList<Object>());
        ServiceDependencies.setServiceDependency(EventConsumer.class, (name, event) -> events.add(event));

        final String projectId = loadWorkflow(TestWorkflowCollection.HOLLOW);
        var command = builder(AddComponentCommandEntBuilder.class) //
            .setKind(KindEnum.ADD_COMPONENT) //
            .setPosition(builder(XYEntBuilder.class).setX(10).setY(20).build()) //
            .setProviderId("local-testing") //
            .setSpaceId(spaceId) //
            .setItemId(itemId) //
            .setName("component") //
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
     * @param space the space to return on {@link SpaceProvider#getSpace(String)}
     */
    static SpaceProvider createSpaceProvider(final Space space) {
        return new SpaceProvider() {

            @Override
            public void init(final Consumer<String> loginErrorHandler) {
                // do nothing
            }

            @Override
            public String getId() {
                return "local-testing";
            }

            @Override
            public List<SpaceGroupEnt> toEntity() {
                throw new UnsupportedOperationException();
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
            public Version getServerVersion() {
                throw new UnsupportedOperationException();
            }

            @Override
            public SpaceGroup<?> getSpaceGroup(final String spaceGroupName) {
                throw new UnsupportedOperationException();
            }
        };
    }

    /**
     *
     * @param spaceId
     * @param componentItemId
     * @param componentName
     * @param toKnimeUrlDelayInMs the time the {@link Space#toKnimeUrl(String)}-call should be delayed until it returns
     *            - helps to mimic a longer component-loading operation
     * @return a mocked space
     */
    static Space createSpace(final String spaceId, final String componentItemId, final String componentName,
        final long toKnimeUrlDelayInMs) {
        var spaceMock = Mockito.mock(Space.class);
        when(spaceMock.getId()).thenReturn(spaceId);
        when(spaceMock.getItemName(componentItemId)).thenReturn(componentName);
        try {
            when(spaceMock.toKnimeUrl(componentItemId)).thenAnswer(i -> {
                try {
                    // ensures that 'component loading' takes a bit longer to make the test deterministic
                    // (when checking for the placeholder state - which is 'loading' on the first event)
                    Thread.sleep(toKnimeUrlDelayInMs);
                } catch (InterruptedException ex) { // NOSONAR
                    //
                }
                return new URI("knime://LOCAL/component/");
            });
            var componentPath = CoreUtil
                .resolveToFile("/files/test_workspace_to_list/component", AddComponentCommandTestHelper.class).toPath();
            when(spaceMock.toLocalAbsolutePath(any(), eq(componentItemId))).thenReturn(Optional.of(componentPath));
        } catch (CanceledExecutionException | IOException ex) {
            throw new AssertionError(ex);
        }
        return spaceMock;
    }

}

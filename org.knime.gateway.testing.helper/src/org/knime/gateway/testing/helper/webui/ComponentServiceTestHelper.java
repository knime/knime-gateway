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
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThrows;
import static org.knime.gateway.api.entity.EntityBuilderManager.builder;
import static org.knime.gateway.api.entity.NodeIDEnt.getRootID;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.atomic.AtomicBoolean;

import org.awaitility.Awaitility;
import org.knime.gateway.api.entity.NodeIDEnt;
import org.knime.gateway.api.util.VersionId;
import org.knime.gateway.api.webui.entity.AddComponentCommandEnt.AddComponentCommandEntBuilder;
import org.knime.gateway.api.webui.entity.AddComponentPlaceholderResultEnt;
import org.knime.gateway.api.webui.entity.ComponentPlaceholderEnt;
import org.knime.gateway.api.webui.entity.ComponentPlaceholderEnt.StateEnum;
import org.knime.gateway.api.webui.entity.PatchOpEnt.OpEnum;
import org.knime.gateway.api.webui.entity.WorkflowChangedEventTypeEnt.WorkflowChangedEventTypeEntBuilder;
import org.knime.gateway.api.webui.entity.WorkflowCommandEnt.KindEnum;
import org.knime.gateway.api.webui.entity.XYEnt.XYEntBuilder;
import org.knime.gateway.api.webui.service.ComponentService;
import org.knime.gateway.api.webui.service.util.ServiceExceptions.ServiceCallException;
import org.knime.gateway.impl.project.ProjectManager;
import org.knime.gateway.impl.webui.WorkflowMiddleware;
import org.knime.gateway.impl.webui.service.ServiceDependencies;
import org.knime.gateway.impl.webui.service.events.EventConsumer;
import org.knime.gateway.impl.webui.spaces.SpaceProvidersManager;
import org.knime.gateway.json.util.ObjectMapperUtil;
import org.knime.gateway.testing.helper.ResultChecker;
import org.knime.gateway.testing.helper.ServiceProvider;
import org.knime.gateway.testing.helper.TestWorkflow;
import org.knime.gateway.testing.helper.TestWorkflowCollection;
import org.knime.gateway.testing.helper.WorkflowExecutor;
import org.knime.gateway.testing.helper.WorkflowLoader;

/**
 * Test for the endpoints of the node service.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 * @author Kai Franze, KNIME GmbH, Germany
 * @author Tobias Kampmann, TNG
 */
public class ComponentServiceTestHelper extends WebUIGatewayServiceTestHelper {

    /**
     * @param entityResultChecker
     * @param serviceProvider
     * @param workflowLoader
     * @param workflowExecutor
     */
    public ComponentServiceTestHelper(final ResultChecker entityResultChecker, final ServiceProvider serviceProvider,
        final WorkflowLoader workflowLoader, final WorkflowExecutor workflowExecutor) {
        super(ComponentServiceTestHelper.class, entityResultChecker, serviceProvider, workflowLoader, workflowExecutor);
    }

    /**
     * Tests {@link ComponentService#getCompositeViewPage(String, NodeIDEnt, String, NodeIDEnt)}.
     *
     * @throws Exception
     */
    public void testCompositeViewPage() throws Exception {
        var projectId = loadWorkflow(TestWorkflowCollection.VIEW_NODES);
        var compositeViewPage =
            (String)cs().getCompositeViewPage(projectId, NodeIDEnt.getRootID(), null, new NodeIDEnt("root:11"));
        var compositeViewPageTree = ObjectMapperUtil.getInstance().getObjectMapper().readTree(compositeViewPage);
        var nodeView = compositeViewPageTree.get("nodeViews").get("11:0:10");
        assertThat(nodeView.get("nodeInfo").get("nodeAnnotation").asText(), is("novel view-node"));
        var webNode = compositeViewPageTree.get("webNodes").get("11:0:9");
        assertThat(webNode.get("nodeInfo").get("nodeAnnotation").asText(), is("JS view-node"));
    }

    /**
     * Tests {@link ComponentService#getCompositeViewPage(String, NodeIDEnt, String, NodeIDEnt)} with versions.
     *
     * @throws Exception
     */
    public void testCompositeViewPageWithVersions() throws Exception {
        var testWorkflowWithVersion = TestWorkflow.WithVersion.of( //
            TestWorkflowCollection.VERSIONS_EXTENDED_CURRENT_STATE, //
            TestWorkflowCollection.VERSIONS_EXTENDED_EARLIER_VERSION::getWorkflowDir //
        );
        var projectId = loadWorkflow(testWorkflowWithVersion);
        var nodeId = new NodeIDEnt("root:9"); // Component

        // Current state
        var currentStateCompositeViewPage = (String)cs().getCompositeViewPage(projectId, NodeIDEnt.getRootID(),
            VersionId.currentState().toString(), nodeId);
        var currentStateCompositeViewNode =
            ObjectMapperUtil.getInstance().getObjectMapper().readTree(currentStateCompositeViewPage);

        var version = VersionId.parse("2");
        ws().getWorkflow(projectId, NodeIDEnt.getRootID(), version.toString(), false);

        // Earlier version
        var versionCompositeViewPage =
            (String)cs().getCompositeViewPage(projectId, NodeIDEnt.getRootID(), version.toString(), nodeId);
        var versionCompositeViewNode =
            ObjectMapperUtil.getInstance().getObjectMapper().readTree(versionCompositeViewPage);

        // the views should be different
        assertThat(currentStateCompositeViewNode.get("nodeViews"), is(not(versionCompositeViewNode.get("nodeViews"))));
    }

    /**
     * Tests {@link ComponentService#getComponentDescription(String, NodeIDEnt, String, NodeIDEnt)}.
     *
     * @throws Exception
     */
    public void testGetComponentDescription() throws Exception {
        var projectId = loadWorkflow(TestWorkflowCollection.METADATA);

        // New component
        var componentNew = new NodeIDEnt(9);
        var descNew = cs().getComponentDescription(projectId, getRootID(), null, componentNew);
        cr(descNew, "component_description_new");

        // Component with partial description
        var componentPartial = new NodeIDEnt(4);
        var descPartial = cs().getComponentDescription(projectId, getRootID(), null, componentPartial);
        cr(descPartial, "component_description_partial");

        // Component with full description
        var componentFull = new NodeIDEnt(6);
        var descFull = cs().getComponentDescription(projectId, getRootID(), null, componentFull);
        cr(descFull, "component_description_full");

        // Empty component
        var componentEmpty = new NodeIDEnt(11);
        var descEmpty = cs().getComponentDescription(projectId, getRootID(), null, componentEmpty);
        cr(descEmpty, "component_description_empty");

        // Metanode
        var metanode = new NodeIDEnt(2);
        assertThrows(ServiceCallException.class,
            () -> cs().getComponentDescription(projectId, getRootID(), null, metanode));

        // Native node
        var nativeNode = new NodeIDEnt(5);
        assertThrows(ServiceCallException.class,
            () -> cs().getComponentDescription(projectId, getRootID(), null, nativeNode));

        // Non-existing node
        var nan = new NodeIDEnt(99);
        assertThrows(ServiceCallException.class, () -> cs().getComponentDescription(projectId, getRootID(), null, nan));
    }

    /**
     * Tests {@link ComponentService#getComponentDescription(String, NodeIDEnt, String, NodeIDEnt)}.
     *
     * @throws Exception
     */
    public void testGetComponentDescriptionWithVersions() throws Exception {
        var testWorkflowWithVersion = TestWorkflow.WithVersion.of( //
            TestWorkflowCollection.VERSIONS_EXTENDED_CURRENT_STATE, //
            TestWorkflowCollection.VERSIONS_EXTENDED_EARLIER_VERSION::getWorkflowDir //
        );
        var projectId = loadWorkflow(testWorkflowWithVersion);
        var nodeId = new NodeIDEnt("root:9"); // Component

        // Current state
        var currentCompDesc =
            cs().getComponentDescription(projectId, getRootID(), VersionId.currentState().toString(), nodeId);

        var version = VersionId.parse("2");
        ws().getWorkflow(projectId, NodeIDEnt.getRootID(), version.toString(), false);

        // Earlier version
        var versionCompDesc = cs().getComponentDescription(projectId, getRootID(), version.toString(), nodeId);

        // the descriptions should be different
        assertThat(currentCompDesc.getDescription().getValue(), is(not(versionCompDesc.getDescription().getValue())));
    }

    /**
     * Tests {@link ComponentService#cancelOrRetryComponentLoadJob(String, NodeIDEnt, String, String)} .
     *
     * @throws Exception
     */
    public void testCancelAndRetryComponentLoadJob() throws Exception {
        var itemId = "test-item-id";
        var spaceId = "test-space-id";
        var wasCancelled = new AtomicBoolean();
        var space = AddComponentCommandTestHelper.createSpace(spaceId, itemId, "component name", 1000, wasCancelled);
        var spaceProvider = AddComponentCommandTestHelper.createSpaceProvider(space);
        var spaceProviderManager = SpaceServiceTestHelper.createSpaceProvidersManager(spaceProvider);
        ServiceDependencies.setServiceDependency(SpaceProvidersManager.class, spaceProviderManager);
        var events = Collections.synchronizedList(new ArrayList<Object>());
        ServiceDependencies.setServiceDependency(EventConsumer.class, (name, event) -> events.add(event));
        ServiceDependencies.setServiceDependency(WorkflowMiddleware.class,
            new WorkflowMiddleware(ProjectManager.getInstance(), spaceProviderManager));

        final String projectId = loadWorkflow(TestWorkflowCollection.HOLLOW);
        var command = builder(AddComponentCommandEntBuilder.class) //
            .setKind(KindEnum.ADD_COMPONENT) //
            .setPosition(builder(XYEntBuilder.class).setX(10).setY(20).build()) //
            .setProviderId("local-testing") //
            .setSpaceId(spaceId) //
            .setItemId(itemId) //
            .setName("component name") //
            .build();

        // in order to get proper workflow changes events subsequently (to have a version 0 to compare against)
        var snapshotId = ws().getWorkflow(projectId, NodeIDEnt.getRootID(), null, Boolean.TRUE).getSnapshotId();
        es().addEventListener(builder(WorkflowChangedEventTypeEntBuilder.class).setProjectId(projectId)
            .setWorkflowId(NodeIDEnt.getRootID()).setTypeId("WorkflowChangedEventType").setSnapshotId(snapshotId)
            .build());

        // execute add-component command
        var commandResult =
            (AddComponentPlaceholderResultEnt)ws().executeWorkflowCommand(projectId, NodeIDEnt.getRootID(), command);
        // sanity check
        var placeholderPatch = EventServiceTestHelper.waitAndFindPatchOpForPath("/componentPlaceholders", events);
        assertThat(placeholderPatch.getOp(), is(OpEnum.ADD));
        var placeholder = ((Collection<ComponentPlaceholderEnt>)placeholderPatch.getValue()).iterator().next();
        assertThat(placeholder.getId(), is(commandResult.getNewPlaceholderId()));
        assertThat(placeholder.getName(), is("component name"));
        assertThat(placeholder.getState(), is(StateEnum.LOADING));
        events.clear();

        // the actual test -> cancel
        cs().cancelOrRetryComponentLoadJob(projectId, getRootID(), commandResult.getNewPlaceholderId(), "cancel");
        var statePatch = EventServiceTestHelper.waitAndFindPatchOpForPath("/componentPlaceholders/0/state", events);
        assertThat(statePatch.getOp(), is(OpEnum.REPLACE));
        assertThat(statePatch.getValue(), is(StateEnum.ERROR));
        var messagePatch = EventServiceTestHelper.waitAndFindPatchOpForPath("/componentPlaceholders/0/message", events);
        assertThat(messagePatch.getOp(), is(OpEnum.REPLACE));
        assertThat(messagePatch.getValue(), is("Component loading cancelled"));
        Awaitility.await().untilAsserted(() -> assertThat(wasCancelled.get(), is(true)));
        events.clear();

        // retry
        cs().cancelOrRetryComponentLoadJob(projectId, getRootID(), commandResult.getNewPlaceholderId(), "retry");
        statePatch = EventServiceTestHelper.waitAndFindPatchOpForPath("/componentPlaceholders/0/state", events);
        assertThat(statePatch.getOp(), is(OpEnum.REPLACE));
        assertThat(statePatch.getValue(), is(StateEnum.LOADING));
        messagePatch = EventServiceTestHelper.waitAndFindPatchOpForPath("/componentPlaceholders/0/message", events);
        assertThat(messagePatch.getOp(), is(OpEnum.REPLACE));
    }

}

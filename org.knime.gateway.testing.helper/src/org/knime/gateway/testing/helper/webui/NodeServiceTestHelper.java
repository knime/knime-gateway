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

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThrows;
import static org.knime.gateway.api.entity.EntityBuilderManager.builder;
import static org.knime.gateway.api.entity.NodeIDEnt.getRootID;

import java.util.Collections;
import java.util.concurrent.TimeUnit;

import org.awaitility.Awaitility;
import org.knime.core.webui.data.RpcDataService;
import org.knime.gateway.api.entity.NodeIDEnt;
import org.knime.gateway.api.webui.entity.ComponentNodeEnt;
import org.knime.gateway.api.webui.entity.LoopInfoEnt.StatusEnum;
import org.knime.gateway.api.webui.entity.NativeNodeDescriptionEnt;
import org.knime.gateway.api.webui.entity.NativeNodeEnt;
import org.knime.gateway.api.webui.entity.NodeFactoryKeyEnt;
import org.knime.gateway.api.webui.entity.NodeFactoryKeyEnt.NodeFactoryKeyEntBuilder;
import org.knime.gateway.api.webui.entity.NodeStateEnt.ExecutionStateEnum;
import org.knime.gateway.api.webui.entity.WorkflowEnt;
import org.knime.gateway.api.webui.service.NodeService;
import org.knime.gateway.api.webui.service.util.ServiceExceptions.InvalidRequestException;
import org.knime.gateway.api.webui.service.util.ServiceExceptions.NodeDescriptionNotAvailableException;
import org.knime.gateway.api.webui.service.util.ServiceExceptions.NodeNotFoundException;
import org.knime.gateway.api.webui.service.util.ServiceExceptions.NotASubWorkflowException;
import org.knime.gateway.api.webui.service.util.ServiceExceptions.OperationNotAllowedException;
import org.knime.gateway.json.util.ObjectMapperUtil;
import org.knime.gateway.testing.helper.ResultChecker;
import org.knime.gateway.testing.helper.ServiceProvider;
import org.knime.gateway.testing.helper.TestWorkflowCollection;
import org.knime.gateway.testing.helper.WorkflowExecutor;
import org.knime.gateway.testing.helper.WorkflowLoader;
import org.knime.gateway.testing.helper.webui.node.DummyNodeFactory_v41;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * Test for the endpoints of the node service.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
public class NodeServiceTestHelper extends WebUIGatewayServiceTestHelper {

    /**
     * @param entityResultChecker
     * @param serviceProvider
     * @param workflowLoader
     * @param workflowExecutor
     */
    public NodeServiceTestHelper(final ResultChecker entityResultChecker, final ServiceProvider serviceProvider,
        final WorkflowLoader workflowLoader, final WorkflowExecutor workflowExecutor) {
        super(NodeServiceTestHelper.class, entityResultChecker, serviceProvider, workflowLoader, workflowExecutor);
    }

    /**
     * Tests to change the node state.
     *
     * @throws Exception
     */
    public void testChangeNodeState() throws Exception {
        final String wfId = loadWorkflow(TestWorkflowCollection.GENERAL_WEB_UI);

        // one test that it generally works
        NativeNodeEnt nodeEnt = (NativeNodeEnt)ws().getWorkflow(wfId, getRootID(), Boolean.FALSE)
            .getWorkflow().getNodes().get("root:1");
        assertThat(nodeEnt.getState().getExecutionState(), is(ExecutionStateEnum.CONFIGURED));
        ns().changeNodeStates(wfId, getRootID(), singletonList(new NodeIDEnt(1)), "execute");
        Awaitility.await().atMost(5, TimeUnit.SECONDS).pollInterval(1, TimeUnit.MILLISECONDS).untilAsserted(() -> {
            NativeNodeEnt nodeEnt2 = (NativeNodeEnt)ws().getWorkflow(wfId, NodeIDEnt.getRootID(), Boolean.FALSE).getWorkflow()
                .getNodes().get("root:1");
            assertThat(nodeEnt2.getState().getExecutionState(), is(ExecutionStateEnum.EXECUTED));
        });

        // test node not found exception
        assertThrows(NodeNotFoundException.class, () -> {
            ns().changeNodeStates(wfId, getRootID(), singletonList(new NodeIDEnt(83747273)), "execute");
        });

        // test operation not allow exception
        assertThrows(OperationNotAllowedException.class, () -> {
            ns().changeNodeStates(wfId, getRootID(), singletonList(new NodeIDEnt(1)), "blub");
        });
    }

    /**
     * Tests to change the node state of all nodes contained in a (sub-)workflow.
     *
     * @throws Exception
     */
    public void testChangeNodeStateAllNodes() throws Exception {
        final String wfId = loadWorkflow(TestWorkflowCollection.LOOP_EXECUTION);

        ns().changeNodeStates(wfId, new NodeIDEnt(5), emptyList(), "execute");
        Awaitility.await().atMost(10, TimeUnit.SECONDS).pollInterval(100, TimeUnit.MILLISECONDS).untilAsserted(() -> {
            NativeNodeEnt nodeEnt = (NativeNodeEnt)ws().getWorkflow(wfId, new NodeIDEnt(5), Boolean.FALSE)
                .getWorkflow().getNodes().get("root:5:0:4");
            assertThat(nodeEnt.getState().getExecutionState(), is(ExecutionStateEnum.EXECUTED));
        });

        ns().changeNodeStates(wfId, getRootID(), emptyList(), "execute");
        Awaitility.await().atMost(10, TimeUnit.SECONDS).pollInterval(100, TimeUnit.MILLISECONDS).untilAsserted(() -> {
            NativeNodeEnt nodeEnt = (NativeNodeEnt)ws().getWorkflow(wfId, new NodeIDEnt(5), Boolean.FALSE).getWorkflow()
                .getNodes().get("root:5:0:4");
            assertThat(nodeEnt.getState().getExecutionState(), is(ExecutionStateEnum.EXECUTED));
        });
    }

    /**
     * Tests to change the node execution state on a component project.
     *
     * @throws Exception
     */
    public void testChangeNodeStateOfComponentProject() throws Exception {
        String wfId = loadComponent(TestWorkflowCollection.COMPONENT_PROJECT);

        // test execution on root level
        ns().changeNodeStates(wfId, getRootID(), emptyList(), "execute");
        NodeIDEnt n7 = new NodeIDEnt(7);
        Awaitility.await().atMost(2, TimeUnit.SECONDS).pollInterval(10, TimeUnit.MILLISECONDS).untilAsserted(() -> {
            NativeNodeEnt node = getNativeNodeEnt(wfId, getRootID(), n7);
            assertThat(node.getState().getExecutionState(), is(ExecutionStateEnum.EXECUTED));
        });

        // reset on root level
        ns().changeNodeStates(wfId, getRootID(), emptyList(), "reset");
        Awaitility.await().atMost(2, TimeUnit.SECONDS).pollInterval(10, TimeUnit.MILLISECONDS).untilAsserted(() -> {
            NativeNodeEnt node = getNativeNodeEnt(wfId, getRootID(), n7);
            assertThat(node.getState().getExecutionState(), is(ExecutionStateEnum.CONFIGURED));
        });
    }

    /**
     * Tests the change of the loop execution state (step, pause, resume).
     *
     * @throws Exception
     */
    public void testChangeLoopExecutionState() throws Exception {
        String wfId = loadWorkflow(TestWorkflowCollection.LOOP_EXECUTION);
        testChangeLoopExecutionState(wfId, getRootID());
    }

    /**
     * Tests the change of the loop execution state (step, pause, resume) within a sub-workflow.
     *
     * @throws Exception
     */
    public void testChangeLoopExecutionStateInSubWorkflow() throws Exception {
        String wfId = loadWorkflow(TestWorkflowCollection.LOOP_EXECUTION);
        NodeIDEnt component = new NodeIDEnt(5,0);
        testChangeLoopExecutionState(wfId, component);
        ns().changeNodeStates(wfId, component, Collections.emptyList(), "cancel");
        await().atMost(5, TimeUnit.SECONDS).pollInterval(100, TimeUnit.MILLISECONDS).untilAsserted(() -> {
            WorkflowEnt wf = ws().getWorkflow(wfId, getRootID(), Boolean.FALSE).getWorkflow();
            assertThat(((ComponentNodeEnt)wf.getNodes().get("root:5")).getState().getExecutionState(),
                is(not(ExecutionStateEnum.EXECUTING)));
        });
    }

    private void testChangeLoopExecutionState(final String wfId, final NodeIDEnt subWfId) throws Exception {
        NodeIDEnt n4 = subWfId.appendNodeID(4);

        // step before first iteration
        ns().changeLoopState(wfId, subWfId, n4, "step");
        cr(getNativeNodeEnt(wfId, subWfId, n4).getLoopInfo(), "loop_info_not_executed");
        await().atMost(5, TimeUnit.SECONDS).pollInterval(200, TimeUnit.MILLISECONDS).untilAsserted(() -> {
            NativeNodeEnt node = getNativeNodeEnt(wfId, subWfId, n4);
            assertThat(node.getLoopInfo().getStatus(), is(StatusEnum.PAUSED));
        });
        cr(getNativeNodeEnt(wfId, subWfId, n4).getLoopInfo(), "loop_info_paused");

        // strange race condition causing a NPE otherwise
        Thread.sleep(1000);

        // step while paused
        ns().changeLoopState(wfId, subWfId, n4, "step");
        await().atMost(5, TimeUnit.SECONDS).pollInterval(100, TimeUnit.MILLISECONDS).untilAsserted(() -> {
            NativeNodeEnt node = getNativeNodeEnt(wfId, subWfId, n4);
            assertThat(node.getLoopInfo().getStatus(), is(StatusEnum.PAUSED));
        });
        cr(getNativeNodeEnt(wfId, subWfId, n4).getLoopInfo(), "loop_info_paused");

        // resume
        ns().changeLoopState(wfId, subWfId, n4, "resume");
        await().atMost(5, TimeUnit.SECONDS).pollInterval(100, TimeUnit.MILLISECONDS).untilAsserted(() -> {
            NativeNodeEnt node = getNativeNodeEnt(wfId, subWfId, n4);
            assertThat(node.getLoopInfo().getStatus(), is(StatusEnum.FINISHED));
        });
        cr(getNativeNodeEnt(wfId, subWfId, n4).getLoopInfo(), "loop_info_finished");

        // pause execution
        ns().changeNodeStates(wfId, subWfId, singletonList(subWfId.appendNodeID(1)), "reset");
        ns().changeNodeStates(wfId, subWfId, emptyList(), "execute");
        ns().changeLoopState(wfId, subWfId, n4, "pause");
        await().atMost(5, TimeUnit.SECONDS).pollInterval(100, TimeUnit.MILLISECONDS).untilAsserted(() -> {
            NativeNodeEnt node = getNativeNodeEnt(wfId, subWfId, n4);
            assertThat(node.getLoopInfo().getStatus(), is(StatusEnum.PAUSED));
        });

        // no loop info for non loop end nodes
        assertThat(getNativeNodeEnt(wfId, subWfId, subWfId.appendNodeID(2)).getLoopInfo(), is(nullValue()));

        // test node not found exception
        assertThrows(NodeNotFoundException.class, () -> {
            ns().changeLoopState(wfId, subWfId, new NodeIDEnt(83747273), "pause");
        });

        // test operation not allow exception
        assertThrows(OperationNotAllowedException.class, () -> {
            ns().changeLoopState(wfId, subWfId, n4, "blub");
        });

        // test on non-loop-end node
        assertThrows(OperationNotAllowedException.class, () -> {
            ns().changeLoopState(wfId, subWfId, subWfId.appendNodeID(2), "pause");
        });
    }

    private NativeNodeEnt getNativeNodeEnt(final String projectId, final NodeIDEnt workflowId, final NodeIDEnt nodeId)
        throws NotASubWorkflowException, NodeNotFoundException {
        return (NativeNodeEnt)ws().getWorkflow(projectId, workflowId, Boolean.TRUE).getWorkflow().getNodes()
            .get(nodeId.toString());
    }

    /**
     * Tests {@link NodeService#getNodeDescription(NodeFactoryKeyEnt)}.
     *
     * @throws NodeNotFoundException
     * @throws NodeDescriptionNotAvailableException
     */
    public void testGetNodeDescription() throws NodeNotFoundException, NodeDescriptionNotAvailableException {
        // example for elements in 4.1 schema and rich formatting
        testNodeDescriptionSnapshot(DummyNodeFactory_v41.class.getName());

        // example for v2.7 schema, handles optional options differently
        testNodeDescriptionSnapshot("org.knime.base.node.io.tablecreator.TableCreator2NodeFactory");

        // optional input port
        testNodeDescriptionSnapshot("org.knime.js.base.node.configuration.input.slider.IntegerSliderDialogNodeFactory");

        // example for dynamic port groups: "Concatenate"
        testNodeDescriptionSnapshot("org.knime.base.node.preproc.append.row.AppendedRowsNodeFactory");

        // example for flow variable output port
        testNodeDescriptionSnapshot("org.knime.base.node.meta.looper.variable.start.LoopStartVariable3NodeFactory");

        // example for dynamically created node description
        // note that "correct" behaviour is that a single dialog option is produced
        // see also org.knime.distance.util.DistanceMeasureDescriptionFactory, works similarly.
        testNodeDescriptionSnapshot("org.knime.base.node.preproc.pmml.missingval.compute.MissingValueHandlerNodeFactory");

        // dynamic JS node -- have their own schema
        testNodeDescriptionSnapshot("org.knime.dynamic.js.v30.DynamicJSNodeFactory",
                "{\"name\":\"settings\",\"value\":{\"nodeDir\":{\"type\":\"string\",\"value\":\"org.knime.dynamic.js.base:nodes/:barChart\"}}}",
                "barChart");
    }

    private void testNodeDescriptionSnapshot(final String classname) throws NodeNotFoundException, NodeDescriptionNotAvailableException {
        testNodeDescriptionSnapshot(classname, null, null);
    }

    private void testNodeDescriptionSnapshot(final String classname, final String settings, final String settingsReadable) throws NodeNotFoundException, NodeDescriptionNotAvailableException {
        NodeFactoryKeyEnt keyEnt = builder(NodeFactoryKeyEntBuilder.class)
                .setClassName(classname)
                .setSettings(settings)
                .build();

        NativeNodeDescriptionEnt ndEnt = ns().getNodeDescription(keyEnt);

        cr(ndEnt, "node_description_" + classname + (settingsReadable != null ? "_" + settingsReadable : ""));
    }

    /**
     * Tests {@link NodeService#getNodeDialog(String, NodeIDEnt, NodeIDEnt)}.
     *
     * @throws Exception
     */
    public void testGetNodeDialog() throws Exception {
        // needs to be set for {@link SubNodeContainerDialogFactory} to return the JS based node dialog
        var key = "org.knime.component.ui.mode";
        var componentUiMode = System.setProperty(key, "js");

        var projectId = loadWorkflow(TestWorkflowCollection.VIEW_NODES);

        var workflow = ws().getWorkflow(projectId, getRootID(), Boolean.FALSE).getWorkflow();
        assertThat(((NativeNodeEnt)workflow.getNodes().get("root:1")).hasDialog(), is(Boolean.TRUE));
        assertThat(((ComponentNodeEnt)workflow.getNodes().get("root:14")).hasDialog(), is(nullValue()));
        assertThat(((ComponentNodeEnt)workflow.getNodes().get("root:17")).hasDialog(), is(Boolean.TRUE));

        // dialog of a native node
        var dialogEnt = ns().getNodeDialog(projectId, getRootID(), new NodeIDEnt(1));
        var nodeDialogJsonNode =
            ObjectMapperUtil.getInstance().getObjectMapper().convertValue(dialogEnt, JsonNode.class);
        assertThat(nodeDialogJsonNode.get("projectId").textValue(), containsString("view nodes"));
        assertThat(nodeDialogJsonNode.get("workflowId").textValue(), is("root"));
        assertThat(nodeDialogJsonNode.get("nodeId").textValue(), is("root:1"));
        assertThat(nodeDialogJsonNode.get("extensionType").textValue(), is("dialog"));
        assertThat(nodeDialogJsonNode.get("initialData").textValue(), notNullValue());
        var resourceInfo = nodeDialogJsonNode.get("resourceInfo");
        assertThat(resourceInfo.get("id").textValue(), is("defaultdialog"));
        assertThat(resourceInfo.get("type").textValue(), is("VUE_COMPONENT_LIB"));

        var message = assertThrows(InvalidRequestException.class,
            () -> ns().getNodeDialog(projectId, getRootID(), new NodeIDEnt(3))).getMessage();
        assertThat(message, containsString("doesn't have a dialog"));

        // request dialog of a component without any configuration nodes
        message = assertThrows(InvalidRequestException.class,
            () -> ns().getNodeDialog(projectId, getRootID(), new NodeIDEnt(14))).getMessage();
        assertThat(message, containsString("doesn't have a dialog"));

        // request dialog of a component with a configuration node
        var componentDialogEnt = ns().getNodeDialog(projectId, getRootID(), new NodeIDEnt(17));
        nodeDialogJsonNode =
            ObjectMapperUtil.getInstance().getObjectMapper().convertValue(componentDialogEnt, JsonNode.class);
        assertThat(nodeDialogJsonNode.get("extensionType").textValue(), is("dialog"));
        assertThat(nodeDialogJsonNode.get("initialData").textValue(), notNullValue());
        resourceInfo = nodeDialogJsonNode.get("resourceInfo");
        assertThat(resourceInfo.get("id").textValue(), is("defaultdialog"));
        assertThat(resourceInfo.get("type").textValue(), is("VUE_COMPONENT_LIB"));

        if (componentUiMode == null) {
            System.clearProperty(key);
        } else {
            System.setProperty(key, componentUiMode);
        }
    }

    /**
     * Tests {@link NodeService#getNodeView(String, NodeIDEnt, NodeIDEnt)}.
     *
     * @throws Exception
     */
    public void testGetNodeView() throws Exception {
        var projectId = loadWorkflow(TestWorkflowCollection.VIEW_NODES);

        var message = assertThrows(InvalidRequestException.class,
            () -> ns().getNodeView(projectId, getRootID(), new NodeIDEnt(1))).getMessage();
        assertThat(message, containsString("is not executed"));

        executeWorkflow(projectId);

        assertThat(((NativeNodeEnt)ws().getWorkflow(projectId, getRootID(), Boolean.FALSE).getWorkflow().getNodes()
                .get("root:1")).hasView(), is(Boolean.TRUE));

        var viewEnt = ns().getNodeView(projectId, getRootID(), new NodeIDEnt(1));
        var nodeViewJsonNode =
            ObjectMapperUtil.getInstance().getObjectMapper().convertValue(viewEnt, JsonNode.class);
        assertThat(nodeViewJsonNode.get("projectId").textValue(), containsString("view nodes"));
        assertThat(nodeViewJsonNode.get("workflowId").textValue(), is("root"));
        assertThat(nodeViewJsonNode.get("nodeId").textValue(), is("root:1"));
        assertThat(nodeViewJsonNode.get("extensionType").textValue(), is("view"));
        assertThat(nodeViewJsonNode.get("initialData").textValue(), notNullValue());
        var resourceInfo = nodeViewJsonNode.get("resourceInfo");
        assertThat(resourceInfo.get("id").textValue(),
            is("org.knime.base.views.node.scatterplot.ScatterPlotNodeFactory"));
        assertThat(resourceInfo.get("type").textValue(), is("HTML"));

        message = assertThrows(InvalidRequestException.class,
            () -> ns().getNodeView(projectId, getRootID(), new NodeIDEnt(3))).getMessage();
        assertThat(message, containsString("doesn't have a view"));
    }

    /**
     * Tests {@link NodeService#callNodeDataService(String, NodeIDEnt, NodeIDEnt, String, String, String)}.
     *
     * @throws Exception
     */
    public void testCallNodeDataService() throws Exception {
        var projectId = loadWorkflow(TestWorkflowCollection.VIEW_NODES);
        executeWorkflow(projectId);

        // initialData
        var initialData = ns().callNodeDataService(projectId, getRootID(), new NodeIDEnt(1), "view", "initial_data", "");
        var jsonNode = ObjectMapperUtil.getInstance().getObjectMapper().readTree(initialData);
        assertThat(jsonNode.get("result").get("data"), notNullValue());

        // data
        var jsonRpcRequest = RpcDataService.jsonRpcRequest("getData", "Universe_0_0", "Universe_0_1", "<none>", "2");
        var data = ns().callNodeDataService(projectId, getRootID(), new NodeIDEnt(1), "view", "data", jsonRpcRequest);
        jsonNode = ObjectMapperUtil.getInstance().getObjectMapper().readTree(data);
        assertThat(jsonNode.get("result").get("points"), notNullValue());
        assertThat(jsonNode.get("id").intValue(), is(1));

        // errors
        var message = assertThrows(InvalidRequestException.class,
            () -> ns().callNodeDataService(projectId, getRootID(), new NodeIDEnt(1), "view", "nonsense", ""))
                .getMessage();
        assertThat(message, containsString("Unknown service type"));
        message = assertThrows(InvalidRequestException.class,
            () -> ns().callNodeDataService(projectId, getRootID(), new NodeIDEnt(1), "nonsense", "data", ""))
                .getMessage();
        assertThat(message, containsString("Unknown target"));

    }

}

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

import static java.util.Collections.singletonList;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThrows;
import static org.knime.gateway.api.entity.NodeIDEnt.getRootID;

import java.util.concurrent.TimeUnit;

import org.awaitility.Awaitility;
import org.knime.gateway.api.entity.NodeIDEnt;
import org.knime.gateway.api.webui.entity.LoopInfoEnt.StatusEnum;
import org.knime.gateway.api.webui.entity.NativeNodeEnt;
import org.knime.gateway.api.webui.entity.NodeStateEnt.ExecutionStateEnum;
import org.knime.gateway.api.webui.service.NodeService;
import org.knime.gateway.api.webui.service.util.ServiceExceptions.NodeNotFoundException;
import org.knime.gateway.api.webui.service.util.ServiceExceptions.NotASubWorkflowException;
import org.knime.gateway.api.webui.service.util.ServiceExceptions.OperationNotAllowedException;
import org.knime.gateway.json.util.ObjectMapperUtil;
import org.knime.gateway.testing.helper.ResultChecker;
import org.knime.gateway.testing.helper.ServiceProvider;
import org.knime.gateway.testing.helper.TestWorkflowCollection;
import org.knime.gateway.testing.helper.WorkflowExecutor;
import org.knime.gateway.testing.helper.WorkflowLoader;

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
    protected NodeServiceTestHelper(final ResultChecker entityResultChecker, final ServiceProvider serviceProvider,
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
     * Tests to change the node execution state on a component project.
     *
     * @throws Exception
     */
    public void testChangeNodeStateOfComponentProject() throws Exception {
        String wfId = loadComponent(TestWorkflowCollection.COMPONENT_PROJECT);

        // test execution on root level
        ns().changeNodeStates(wfId, getRootID(), singletonList(getRootID()), "execute");
        NodeIDEnt n7 = new NodeIDEnt(7);
        Awaitility.await().atMost(2, TimeUnit.SECONDS).pollInterval(10, TimeUnit.MILLISECONDS).untilAsserted(() -> {
            NativeNodeEnt node = getNativeNodeEnt(wfId, getRootID(), n7);
            assertThat(node.getState().getExecutionState(), is(ExecutionStateEnum.EXECUTED));
        });

        // reset on root level
        ns().changeNodeStates(wfId, getRootID(), singletonList(getRootID()), "reset");
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
        testChangeLoopExecutionState(wfId, new NodeIDEnt(5, 0));
    }

    private void testChangeLoopExecutionState(final String wfId, final NodeIDEnt subWfId) throws Exception {
        NodeIDEnt n4 = subWfId.appendNodeID(4);
        // step before first iteration
        ns().changeLoopState(wfId, subWfId, n4, "step");
        cr(getNativeNodeEnt(wfId, subWfId, n4).getLoopInfo(), "loop_info_not_executed");
        await().atMost(5, TimeUnit.SECONDS).pollInterval(100, TimeUnit.MILLISECONDS).untilAsserted(() -> {
            NativeNodeEnt node = getNativeNodeEnt(wfId, subWfId, n4);
            assertThat(node.getLoopInfo().getStatus(), is(StatusEnum.PAUSED));
        });
        cr(getNativeNodeEnt(wfId, subWfId, n4).getLoopInfo(), "loop_info_paused");

        // step while paused
        ns().changeLoopState(wfId, subWfId, n4, "step");
        await().atMost(2, TimeUnit.SECONDS).pollInterval(100, TimeUnit.MILLISECONDS).untilAsserted(() -> {
            NativeNodeEnt node = getNativeNodeEnt(wfId, subWfId, n4);
            assertThat(node.getLoopInfo().getStatus(), is(StatusEnum.PAUSED));
        });
        cr(getNativeNodeEnt(wfId, subWfId, n4).getLoopInfo(), "loop_info_paused");

        // resume
        ns().changeLoopState(wfId, subWfId, n4, "resume");
        await().atMost(2, TimeUnit.SECONDS).pollInterval(100, TimeUnit.MILLISECONDS).untilAsserted(() -> {
            NativeNodeEnt node = getNativeNodeEnt(wfId, subWfId, n4);
            assertThat(node.getLoopInfo().getStatus(), is(StatusEnum.FINISHED));
        });
        cr(getNativeNodeEnt(wfId, subWfId, n4).getLoopInfo(), "loop_info_finished");

        // pause execution
        ns().changeNodeStates(wfId, subWfId, singletonList(new NodeIDEnt(1)), "reset");
        ns().changeNodeStates(wfId, subWfId, singletonList(NodeIDEnt.getRootID()), "execute");
        ns().changeLoopState(wfId, subWfId, n4, "pause");
        await().atMost(2, TimeUnit.SECONDS).pollInterval(100, TimeUnit.MILLISECONDS).untilAsserted(() -> {
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
     * Test for {@link NodeService#doPortRpc(String, NodeIDEnt, NodeIDEnt, Integer, String)}.
     *
     * @throws Exception
     */
    public void testDoPortRpc() throws Exception {
        final String wfId = loadWorkflow(TestWorkflowCollection.GENERAL_WEB_UI);

        // table
        String rpcRes = ns().doPortRpc(wfId, getRootID(), new NodeIDEnt(1), 1,
            "{\"jsonrpc\":\"2.0\",\"id\":1,\"method\":\"getTable\",\"params\":[2,5]}");
        JsonNode json = ObjectMapperUtil.getInstance().getObjectMapper().readValue(rpcRes, JsonNode.class);
        assertThat(json.get("jsonrpc").asText(), is("2.0"));
        assertThat(json.get("id").asInt(), is(1));
        assertThat(json.get("result"), notNullValue());

        // flow variables
        rpcRes = ns().doPortRpc(wfId, getRootID(), new NodeIDEnt(1), 0,
            "{\"jsonrpc\":\"2.0\",\"id\":1,\"method\":\"getFlowVariables\"}");
        json = ObjectMapperUtil.getInstance().getObjectMapper().readValue(rpcRes, JsonNode.class);
        assertThat(json.get("jsonrpc").asText(), is("2.0"));
        assertThat(json.get("id").asInt(), is(1));
        assertThat(json.get("result"), notNullValue());
    }

}

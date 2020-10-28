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

import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.awaitility.Awaitility;
import org.knime.core.util.Pair;
import org.knime.gateway.api.entity.NodeIDEnt;
import org.knime.gateway.api.webui.entity.ComponentNodeEnt;
import org.knime.gateway.api.webui.entity.NativeNodeEnt;
import org.knime.gateway.api.webui.entity.NodeEnt;
import org.knime.gateway.api.webui.entity.NodeStateEnt.ExecutionStateEnum;
import org.knime.gateway.api.webui.entity.WorkflowEnt;
import org.knime.gateway.testing.helper.ResultChecker;
import org.knime.gateway.testing.helper.TestWorkflowCollection;
import org.knime.gateway.testing.helper.WorkflowExecutor;
import org.knime.gateway.testing.helper.WorkflowLoader;

/**
 * Test for the endpoints to view/render a workflow.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
public class WorkflowServiceTestHelper extends WebUIGatewayServiceTestHelper {

    /**
     * @param entityResultChecker
     * @param serviceProvider
     * @param workflowLoader
     * @param workflowExecutor
     */
    protected WorkflowServiceTestHelper(final ResultChecker entityResultChecker, final ServiceProvider serviceProvider,
        final WorkflowLoader workflowLoader, final WorkflowExecutor workflowExecutor) {
        super("workflowservice", entityResultChecker, serviceProvider, workflowLoader, workflowExecutor);
    }

    /**
     * Tests to get the workflow.
     *
     * @throws Exception
     */
    public void testGetWorkflow() throws Exception {
        String wfId = loadWorkflow(TestWorkflowCollection.GENERAL_WEB_UI);

        // check un-executed
        WorkflowEnt workflow = ws().getWorkflow(wfId, NodeIDEnt.getRootID(), false).getWorkflow();
        cr(workflow, "workflowent_root");

        // get a metanode's workflow
        workflow = ws().getWorkflow(wfId, new NodeIDEnt(6), false).getWorkflow();
        cr(workflow, "workflowent_6");

        // get a component's workflow
        workflow = ws().getWorkflow(wfId, new NodeIDEnt(23), false).getWorkflow();
        cr(workflow, "workflowent_23");

        //check executed
        executeWorkflow(wfId);
        workflow = ws().getWorkflow(wfId, NodeIDEnt.getRootID(), false).getWorkflow();
        cr(workflow, "worklfowent_root_executed");
    }

    /**
     * Tests to get a workflow of a component workflow.
     *
     * @throws Exception
     */
    public void testGetComponentProjectWorkflow() throws Exception {
        String wfId = loadWorkflow(TestWorkflowCollection.COMPONENT_PROJECT);

        WorkflowEnt workflow = ws().getWorkflow(wfId, NodeIDEnt.getRootID(), true).getWorkflow();
        cr(workflow, "component_project");

        workflow = ws().getWorkflow(wfId, new NodeIDEnt(5), true).getWorkflow();
        cr(workflow, "component_in_component_project_l1");

        workflow = ws().getWorkflow(wfId, new NodeIDEnt(5,0,7), true).getWorkflow();
        cr(workflow, "component_in_component_project_l2");
    }

    /**
     * Tests the correct mapping of the node execution states.
     *
     * @throws Exception
     */
    public void testNodeExecutionStates() throws Exception {
        String wfId = loadWorkflow(TestWorkflowCollection.EXECUTION_STATES);

        WorkflowEnt workflow = ws().getWorkflow(wfId, NodeIDEnt.getRootID(), false).getWorkflow();
        cr(getNodeStates(workflow), "node_states");

        executeWorkflowAsync(wfId);
        Awaitility.await().atMost(5, TimeUnit.SECONDS).pollInterval(1, TimeUnit.MILLISECONDS).untilAsserted(() -> {
            WorkflowEnt w = ws().getWorkflow(wfId, NodeIDEnt.getRootID(), false).getWorkflow();
            assertThat(((NativeNodeEnt)w.getNodes().get("root:4")).getState().getExecutionState(),
                is(ExecutionStateEnum.EXECUTED));
        });
        workflow = ws().getWorkflow(wfId, NodeIDEnt.getRootID(), false).getWorkflow();
        cr(getNodeStates(workflow), "node_states_execution");
    }

    private static Map<String, ExecutionStateEnum> getNodeStates(final WorkflowEnt w) {
        return w.getNodes().entrySet().stream().map(e -> { // NOSONAR
            ExecutionStateEnum state = null;
            NodeEnt n = e.getValue();
            if (n instanceof NativeNodeEnt) {
                state = ((NativeNodeEnt)n).getState().getExecutionState();
            } else if (n instanceof ComponentNodeEnt) {
                state = ((ComponentNodeEnt)n).getState().getExecutionState();
            } else {
                //
            }
            return Pair.create(e.getKey(), state);
        }).collect(Collectors.toMap(Pair::getFirst, Pair::getSecond));
    }

    /**
     * Tests the {@link WorkflowEnt#getAllowedActions()} property in partcular.
     *
     * @throws Exception
     */
    public void testGetAllowedActionsInfo() throws Exception {
        String wfId = loadWorkflow(TestWorkflowCollection.GENERAL_WEB_UI);

        WorkflowEnt workflow = ws().getWorkflow(wfId, NodeIDEnt.getRootID(), true).getWorkflow();

        // check the allowed actions on the workflow itself
        cr(workflow.getAllowedActions(), "allowedactions_root");

        // check for component, node and metanode
        cr(workflow.getAllowedActions(), "allowedactions_8");
        cr(workflow.getAllowedActions(), "allowedactions_12");
        cr(workflow.getAllowedActions(), "allowedactions_6");
    }

}

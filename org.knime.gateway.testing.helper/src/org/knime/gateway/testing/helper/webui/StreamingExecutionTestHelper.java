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
 *   Dec 4, 2020 (hornm): created
 */
package org.knime.gateway.testing.helper.webui;

import static org.awaitility.Awaitility.await;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.knime.gateway.api.entity.NodeIDEnt;
import org.knime.gateway.api.webui.entity.ComponentNodeEnt;
import org.knime.gateway.api.webui.entity.NodeEnt;
import org.knime.gateway.api.webui.entity.NodeStateEnt;
import org.knime.gateway.api.webui.entity.NodeStateEnt.ExecutionStateEnum;
import org.knime.gateway.api.webui.entity.WorkflowEnt;
import org.knime.gateway.testing.helper.ResultChecker;
import org.knime.gateway.testing.helper.ServiceProvider;
import org.knime.gateway.testing.helper.TestWorkflowCollection;
import org.knime.gateway.testing.helper.WorkflowExecutor;
import org.knime.gateway.testing.helper.WorkflowLoader;

/**
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
public class StreamingExecutionTestHelper extends WebUIGatewayServiceTestHelper {

    /**
     * @param entityResultChecker
     * @param serviceProvider
     * @param workflowLoader
     * @param workflowExecutor
     */
    protected StreamingExecutionTestHelper(final ResultChecker entityResultChecker, final ServiceProvider serviceProvider,
        final WorkflowLoader workflowLoader, final WorkflowExecutor workflowExecutor) {
        super(StreamingExecutionTestHelper.class, entityResultChecker, serviceProvider, workflowLoader,
            workflowExecutor);
    }



    /**
     * Snapshot test for node's job manager property.
     *
     * @throws Exception
     */
    public void testJobManagerProperty() throws Exception {
        String wfId = loadWorkflow(TestWorkflowCollection.STREAMING_EXECUTION);
        Map<String, NodeEnt> nodes = ws().getWorkflow(wfId, NodeIDEnt.getRootID(), Boolean.FALSE).getWorkflow().getNodes();
        cr(nodes.get("root:4").getExecutionInfo(), "custom_job_manager");
        cr(nodes.get("root:3").getExecutionInfo(), "streaming_job_manager");
    }

    /**
     * Tests the snapshot of a streamed workflow (i.e. the workflow of a streamed component).
     *
     * @throws Exception
     */
    public void testStreamedWorkflow() throws Exception {
        String wfId = loadWorkflow(TestWorkflowCollection.STREAMING_EXECUTION);
        executeWorkflowAsync(wfId);
        await().atMost(4, TimeUnit.SECONDS).pollInterval(100, TimeUnit.MILLISECONDS).until(() -> {
            Map<String, NodeEnt> nodes = ws().getWorkflow(wfId, NodeIDEnt.getRootID(), Boolean.FALSE).getWorkflow().getNodes();
            NodeStateEnt s1 = ((ComponentNodeEnt)nodes.get("root:3")).getState();
            NodeStateEnt s2 = ((ComponentNodeEnt)nodes.get("root:5")).getState();
            return s1.getExecutionState() == ExecutionStateEnum.EXECUTED
                && s2.getExecutionState() == ExecutionStateEnum.EXECUTING;
        });

        WorkflowEnt componentWf3 = ws().getWorkflow(wfId, new NodeIDEnt(3), Boolean.FALSE).getWorkflow();
        cr(componentWf3.getConnections(), "streamed_connections_finished");
        cr(ws().getWorkflow(wfId, new NodeIDEnt(5), Boolean.FALSE).getWorkflow().getConnections(),
            "streamed_connections_in_progress");

        cr(componentWf3.getNodes().get("root:3:0:5"), "streamable_node_5");
        cr(componentWf3.getNodes().get("root:3:0:7"), "not_streamable_node_7");
        cr(componentWf3.getInfo(), "streamed_workflow_info");
    }

}

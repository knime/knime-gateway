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
 *   Jan 11, 2021 (hornm): created
 */
package org.knime.core.rpc;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.junit.Test;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeFactory;
import org.knime.core.node.NodeSettings;
import org.knime.core.node.extension.NodeFactoryExtensionManager;
import org.knime.core.node.workflow.NativeNodeContainer;
import org.knime.core.node.workflow.NodeContainer;
import org.knime.core.node.workflow.NodeID;
import org.knime.core.node.workflow.WorkflowContext;
import org.knime.core.node.workflow.WorkflowCreationHelper;
import org.knime.core.node.workflow.WorkflowManager;
import org.knime.core.node.workflow.WorkflowPersistor;
import org.knime.core.rpc.RpcServer;
import org.knime.core.util.FileUtil;
import org.knime.gateway.impl.project.WorkflowProjectManager;
import org.knime.gateway.testing.helper.LocalWorkflowLoader;
import org.knime.gateway.testing.helper.TestWorkflowCollection;
import org.knime.gateway.testing.helper.rpc.node.SingleRpcNodeFactory;

/**
 * Tests for {@link RpcServerManager}.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
public class RpcServerManagerTest {

    /**
     * Tests the {@link RpcServerManager#doRpc(NodeContainer, int, String)} for rpc requests on ports. Makes especially
     * sure that the response reflects the changed underlying port data (port object, port object spec).
     *
     * @throws Exception
     */
    @Test
    public void testDoPortRpc() throws Exception {
        LocalWorkflowLoader loader = new LocalWorkflowLoader();
        String wfId = loader.loadWorkflow(TestWorkflowCollection.GENERAL_WEB_UI);
        WorkflowManager wfm = WorkflowProjectManager.openAndCacheWorkflow(wfId).orElse(null);
        NodeContainer dataGen = wfm.getNodeContainer(wfm.getID().createChild(1)); // NOSONAR wfm never null
        String rpcTableRequest = "{\"jsonrpc\":\"2.0\",\"id\":1,\"method\":\"getTable\",\"params\":[2,5]}";
        String rpcFlowVarRequest = "{\"jsonrpc\":\"2.0\",\"id\":1,\"method\":\"getFlowVariables\"}";

        String rpcTableResponse = RpcServerManager.getInstance().doRpc(dataGen, 1, rpcTableRequest);
        checkRpcTableResponse(rpcTableResponse, 5, -1);
        String rpcFlowVarResponse = RpcServerManager.getInstance().doRpc(dataGen, 0, rpcFlowVarRequest);
        checkRpcFlowVarResponse(rpcFlowVarResponse, "exposed");

        // change config and check again
        changeDataGenConfig(wfm, dataGen.getID(), 1, "test1");
        rpcTableResponse = RpcServerManager.getInstance().doRpc(dataGen, 1, rpcTableRequest);
        checkRpcTableResponse(rpcTableResponse, 3, -1);
        rpcFlowVarResponse = RpcServerManager.getInstance().doRpc(dataGen, 0, rpcFlowVarRequest);
        checkRpcFlowVarResponse(rpcFlowVarResponse, "test1");

        /* test with data (i.e. executed node) */

        wfm.executeUpToHere(dataGen.getID());
        wfm.waitWhileInExecution(5, TimeUnit.SECONDS);
        rpcTableResponse = RpcServerManager.getInstance().doRpc(dataGen, 1, rpcTableRequest);
        checkRpcTableResponse(rpcTableResponse, 3, 8);
        rpcFlowVarResponse = RpcServerManager.getInstance().doRpc(dataGen, 0, rpcFlowVarRequest);
        checkRpcFlowVarResponse(rpcFlowVarResponse, "test1");

        // change config and check again
        wfm.resetAndConfigureNode(dataGen.getID());
        changeDataGenConfig(wfm, dataGen.getID(), 2, "test2");
        wfm.executeUpToHere(dataGen.getID());
        wfm.waitWhileInExecution(5, TimeUnit.SECONDS);
        rpcTableResponse = RpcServerManager.getInstance().doRpc(dataGen, 1, rpcTableRequest);
        checkRpcTableResponse(rpcTableResponse, 5, 8);
        rpcFlowVarResponse = RpcServerManager.getInstance().doRpc(dataGen, 0, rpcFlowVarRequest);
        checkRpcFlowVarResponse(rpcFlowVarResponse, "test2");

        // check number of cached port rpc servers before and after gc just to make sure that all the
        // RpcServer-instances are only weakly referenced and are removed from memory
        Map<Integer, WeakReference<RpcServer>> portRpcServerCache = RpcServerManager.getInstance().getPortRpcServerCache();
        assertThat("unexpected number of cached port rpc servers", portRpcServerCache.size(), is(2));
        System.gc(); // NOSONAR
        assertTrue(portRpcServerCache.values().stream().allMatch(ref -> ref.get() == null));
        RpcServerManager.getInstance().doRpc(dataGen, 1, rpcTableRequest);
        assertThat("unexpected number of cached port rpc servers", portRpcServerCache.size(), is(1));
        assertTrue(portRpcServerCache.values().stream().allMatch(ref -> ref.get() != null));

        loader.disposeWorkflows();
    }

    /**
     * Test for {@link RpcServerManager#doRpc(NodeContainer, int, String)}.
     *
     * @throws Exception
     */
    @Test
    public void testDoNodeRpc() throws Exception {
        WorkflowManager wfm = createEmptyWorkflow();
        NodeFactory<?> factory = NodeFactoryExtensionManager.getInstance()
            .createNodeFactory(SingleRpcNodeFactory.class.getName()).orElse(null);
        NodeID id = wfm.createAndAddNode(factory);

        String rpcReq = "{\"jsonrpc\":\"2.0\",\"id\":1,\"method\":\"method\"}";
        String rpcRes = RpcServerManager.getInstance().doRpc((NativeNodeContainer)wfm.getNodeContainer(id), rpcReq);
        assertThat(rpcRes, is("{\"jsonrpc\":\"2.0\",\"id\":1,\"result\":\"result1234\"}\n"));

        // check number of cached node rpc servers before and after gc just to make sure that they are properly removed
        // from memory
        Map<Integer, WeakReference<RpcServer>> nodeRpcServerCache = RpcServerManager.getInstance().getNodeRpcServerCache();
        assertThat("unexpected number of cached node rpc servers", nodeRpcServerCache.size(), is(1));
        System.gc(); // NOSONAR
        assertTrue(nodeRpcServerCache.values().stream().allMatch(ref -> ref.get() == null));
        RpcServerManager.getInstance().doRpc((NativeNodeContainer)wfm.getNodeContainer(id), rpcReq);
        assertTrue(nodeRpcServerCache.values().stream().allMatch(ref -> ref.get() != null));

        wfm.getParent().removeProject(wfm.getID());
    }

    private static void changeDataGenConfig(final WorkflowManager wfm, final NodeID id, final int universeSize,
        final String exposedVarName) throws InvalidSettingsException {
        NodeSettings ns = new NodeSettings("settings");
        wfm.saveNodeSettings(id, ns);
        NodeSettings model = ns.getNodeSettings("model");
        NodeSettings unisize = model.getNodeSettings("unisize");
        unisize.addInt("0", universeSize);
        unisize.addInt("1", universeSize);

        NodeSettings noiseVariable = ns.getNodeSettings("variables").getNodeSettings("tree").getNodeSettings("noise");
        noiseVariable.addString("exposed_variable", exposedVarName);

        wfm.loadNodeSettings(id, ns);
    }

    private static void checkRpcTableResponse(final String res, final int colCount, final int rowCount) {
        assertThat("wrong number of expected columns", res, containsString("\"totalNumColumns\":" + colCount));
        assertThat("wrong number of expected rows", res, containsString("\"totalNumRows\":" + rowCount));
    }

    private static WorkflowManager createEmptyWorkflow() throws IOException {
        File dir = FileUtil.createTempDir("workflow");
        File workflowFile = new File(dir, WorkflowPersistor.WORKFLOW_FILE);
        if (workflowFile.createNewFile()) {
            WorkflowCreationHelper creationHelper = new WorkflowCreationHelper();
            WorkflowContext.Factory fac = new WorkflowContext.Factory(workflowFile.getParentFile());
            creationHelper.setWorkflowContext(fac.createContext());

            return WorkflowManager.ROOT.createAndAddProject("workflow", creationHelper);
        } else {
            throw new IllegalStateException("Creating empty workflow failed");
        }
    }


    private static void checkRpcFlowVarResponse(final String res, final String flowVarName) {
        assertThat("expected flow variable not found", res, containsString("\"name\":\"" + flowVarName));
    }

}

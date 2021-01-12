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
package org.knime.gateway.impl.rpc;

import static org.awaitility.Awaitility.await;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;

import java.util.concurrent.TimeUnit;

import org.junit.Test;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeSettings;
import org.knime.core.node.workflow.NodeContainer;
import org.knime.core.node.workflow.NodeID;
import org.knime.core.node.workflow.WorkflowManager;
import org.knime.gateway.impl.project.WorkflowProjectManager;
import org.knime.gateway.testing.helper.LocalWorkflowLoader;
import org.knime.gateway.testing.helper.TestWorkflowCollection;

/**
 * Tests for {@link RpcServerManager}.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
public class RpcServiceManagerTest {

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
        String rpcRequest = "{\"jsonrpc\":\"2.0\",\"id\":1,\"method\":\"getTable\",\"params\":[2,5]}";

        String rpcResponse = RpcServerManager.getInstance().doRpc(dataGen, 1, rpcRequest);
        checkRpcResponse(rpcResponse, 5, -1);

        // change config and check again
        changeDataGenConfig(wfm, dataGen.getID(), 1);
        rpcResponse = RpcServerManager.getInstance().doRpc(dataGen, 1, rpcRequest);
        checkRpcResponse(rpcResponse, 3, -1);

        /* test with data (i.e. executed node) */

        wfm.executeUpToHere(dataGen.getID());
        wfm.waitWhileInExecution(5, TimeUnit.SECONDS);
        rpcResponse = RpcServerManager.getInstance().doRpc(dataGen, 1, rpcRequest);
        checkRpcResponse(rpcResponse, 3, 8);

        // change config and check again
        wfm.resetAndConfigureNode(dataGen.getID());
        changeDataGenConfig(wfm, dataGen.getID(), 2);
        wfm.executeUpToHere(dataGen.getID());
        wfm.waitWhileInExecution(5, TimeUnit.SECONDS);
        rpcResponse = RpcServerManager.getInstance().doRpc(dataGen, 1, rpcRequest);
        checkRpcResponse(rpcResponse, 5, 8);

        loader.disposeWorkflows();

        // check number of cached port rpc servers before and after gc
        // Explanation: The RpcServerManager caches the port rpc servers in a map from PortObject to RpcServer, where
        // the keys (PortObject) are weak references to not hinder their removal from memory.
        // However, the RpcServer implementation for BufferedDataTable (a PortObject), e.g., also holds a reference
        // which must be a weak reference, too! Thus, those lines below essentially make sure that no RpcServer keeps
        // a solid reference on the PortObject they are operating on.
        assertThat("unexpected number of cached port rpc servers",
            RpcServerManager.getInstance().getNumCachedPortRpcServers(), is(2));
        System.gc(); // NOSONAR
        await().atMost(5, TimeUnit.SECONDS).pollInterval(100, TimeUnit.MILLISECONDS)
            .untilAsserted(() -> assertThat("port rpc server cache expected to be empty",
                RpcServerManager.getInstance().getNumCachedPortRpcServers(), is(0)));
    }

    private static void changeDataGenConfig(final WorkflowManager wfm, final NodeID id, final int universeSize)
        throws InvalidSettingsException {
        NodeSettings ns = new NodeSettings("settings");
        wfm.saveNodeSettings(id, ns);
        NodeSettings model = ns.getNodeSettings("model");
        NodeSettings unisize = model.getNodeSettings("unisize");
        unisize.addInt("0", universeSize);
        unisize.addInt("1", universeSize);
        wfm.loadNodeSettings(id, ns);
    }

    private static void checkRpcResponse(final String res, final int colCount, final int rowCount) {
        assertThat("wrong number of expected columns", res, containsString("\"totalNumColumns\":" + colCount));
        assertThat("wrong number of expected rows", res, containsString("\"totalNumRows\":" + rowCount));
    }

}

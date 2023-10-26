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
package org.knime.core.webui.data.rpc;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import java.io.File;
import java.io.IOException;

import org.junit.Test;
import org.knime.core.node.NodeFactory;
import org.knime.core.node.extension.NodeFactoryProvider;
import org.knime.core.node.workflow.NativeNodeContainer;
import org.knime.core.node.workflow.NodeID;
import org.knime.core.node.workflow.WorkflowCreationHelper;
import org.knime.core.node.workflow.WorkflowManager;
import org.knime.core.node.workflow.WorkflowPersistor;
import org.knime.core.node.workflow.contextv2.WorkflowContextV2;
import org.knime.core.util.FileUtil;
import org.knime.gateway.testing.helper.rpc.node.SingleRpcNodeFactory;

/**
 * Tests for {@link RpcServerManager}.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 *
 * @deprecated to be removed with the {@link RpcServerManager}.
 */
@Deprecated(forRemoval = true)
public class RpcServerManagerTest {

    /**
     * Test for {@link RpcServerManager#doRpc(NativeNodeContainer, String)}.
     *
     * @throws Exception
     */
    @Test
    public void testDoNodeRpc() throws Exception {
        WorkflowManager wfm = createEmptyWorkflow();
        NodeFactory<?> factory = NodeFactoryProvider.getInstance() //
                .getNodeFactory(SingleRpcNodeFactory.class.getName()).orElse(null);
        NodeID id = wfm.createAndAddNode(factory);

        String rpcReq = "{\"jsonrpc\":\"2.0\",\"id\":1,\"method\":\"method\"}";
        String rpcRes = RpcServerManager.getInstance().doRpc((NativeNodeContainer)wfm.getNodeContainer(id), rpcReq);
        assertThat(rpcRes, is("{\"jsonrpc\":\"2.0\",\"id\":1,\"result\":\"result1234\"}\n"));

        wfm.getParent().removeProject(wfm.getID());
    }

    private static WorkflowManager createEmptyWorkflow() throws IOException {
        File dir = FileUtil.createTempDir("workflow");
        File workflowFile = new File(dir, WorkflowPersistor.WORKFLOW_FILE);
        if (workflowFile.createNewFile()) {
            WorkflowCreationHelper creationHelper = new WorkflowCreationHelper(
                WorkflowContextV2.forTemporaryWorkflow(workflowFile.getParentFile().toPath(), null));
            return WorkflowManager.ROOT.createAndAddProject("workflow", creationHelper);
        } else {
            throw new IllegalStateException("Creating empty workflow failed");
        }
    }

}

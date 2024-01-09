/*
 * ------------------------------------------------------------------------
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
 * ------------------------------------------------------------------------
 */
package org.knime.gateway.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.knime.core.node.workflow.NodeID;
import org.knime.core.node.workflow.SubNodeContainer;
import org.knime.core.node.workflow.WorkflowManager;
import org.knime.gateway.api.tests.TestWorkflowCollection;
import org.knime.gateway.api.util.CoreUtil;
import org.knime.gateway.api.util.DependentNodeProperties;

/**
 * Tests the correctness of the alternative way to determine node properties
 * that depend on other nodes in the workflow graph.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
class EnhNXT264_AlternativeDeterminationOfDependentNodeProperties {

    WorkflowManager m_wfm;

	@SuppressWarnings("javadoc")
    @BeforeEach
    public void loadWorkflow() throws Exception {
        m_wfm = TestWorkflowCollection.NODE_DEPENDECY.loadWorkflow();
    }

	/**
	 * Mainly tests the correctness of the dependent node properties as determined
	 * in the alternative way. It is done by comparing the results of the
	 * {@link WorkflowManager#canExecuteNode(NodeID)} and
	 * {@link WorkflowManager#canResetNode(NodeID)} obtained in the 'classic' way to
	 * the results as obtained by the alternative way (where the dependent node
	 * properties are determined in one rush and cached).
	 *
	 * @throws Exception
	 */
	@Test
	void testCorrectnessOfDependentNodeProperties() throws Exception {
		NodeID parentId = m_wfm.getID();
		var metanode_209 = (WorkflowManager) m_wfm.getNodeContainer(parentId.createChild(209));
        var metanode_219 = ((WorkflowManager)m_wfm.getNodeContainer(parentId.createChild(219)));
		var component_214 = ((SubNodeContainer) m_wfm.getNodeContainer(parentId.createChild(214)))
				.getWorkflowManager();
		var component_215 = ((SubNodeContainer) m_wfm.getNodeContainer(parentId.createChild(215)))
				.getWorkflowManager();
		var component_212 = ((SubNodeContainer) m_wfm.getNodeContainer(parentId.createChild(212)))
				.getWorkflowManager();
		var wait_216 = parentId.createChild(216);
		var wait_203 = parentId.createChild(203);
		var wait_195 = parentId.createChild(195);
		var wait_225 = parentId.createChild(225);

		checkCanExecuteAndCanResetFlagsForAllNodes(m_wfm);
		checkCanExecuteAndCanResetFlagsForAllNodes(metanode_209);
		checkCanExecuteAndCanResetFlagsForAllNodes(metanode_219);
		checkCanExecuteAndCanResetFlagsForAllNodes(component_214);
		checkCanExecuteAndCanResetFlagsForAllNodes(component_215);
		checkCanExecuteAndCanResetFlagsForAllNodes(component_212);

		// execute 'Wait ...' nodes
		m_wfm.executeUpToHere(wait_203, wait_195);
		await().atMost(5, TimeUnit.SECONDS).pollInterval(100, TimeUnit.MILLISECONDS).untilAsserted(() -> {
			assertThat(m_wfm.getNodeContainerState().isExecutionInProgress()).isTrue();
		});
		checkCanExecuteAndCanResetFlagsForAllNodes(m_wfm);
		checkCanExecuteAndCanResetFlagsForAllNodes(metanode_209);
		checkCanExecuteAndCanResetFlagsForAllNodes(component_214);

		// cancel 'Wait...' node again
		m_wfm.cancelExecution(m_wfm.getNodeContainer(wait_216));
		await().atMost(5, TimeUnit.SECONDS).pollInterval(100, TimeUnit.MILLISECONDS).untilAsserted(() -> {
			assertThat(m_wfm.getNodeContainer(wait_216).getNodeContainerState().isConfigured()).isTrue();
		});
		checkCanExecuteAndCanResetFlagsForAllNodes(m_wfm);
		checkCanExecuteAndCanResetFlagsForAllNodes(metanode_209);
		checkCanExecuteAndCanResetFlagsForAllNodes(component_214);

		// add a connection within the component to make sure that the
		// 'hasExecutablePredeccessor' property
		// of the contained nodes changes
		component_212.addConnection(component_212.getID().createChild(212), 1, component_212.getID().createChild(214),
				1);
		checkCanExecuteAndCanResetFlagsForAllNodes(component_212);

		// Should actually test that only certain branches within a metanode are regarded as 'executable'.
		// However, at the moment all predecessors of a metanode are taken into account at once which is not 100% correct
		// (ticket: NXT-869)
        checkCanExecuteAndCanResetFlagsForAllNodes(metanode_219);

		// Test a metanode with a 'through' connection
		WorkflowManager metanode_25 = (WorkflowManager) m_wfm.getNodeContainer(parentId.createChild(25));
		checkCanExecuteAndCanResetFlagsForAllNodes(metanode_25);

		// Test a double-nested metanode (with direct connection to the metanode-parent)
        var metanode_224_23 =
            (WorkflowManager)m_wfm.findNodeContainer(parentId.createChild(224).createChild(23));
        checkCanExecuteAndCanResetFlagsForAllNodes(metanode_224_23);
        // ... with executing successor for metanode 224
        m_wfm.executeUpToHere(wait_225);
        var metanode_224 = m_wfm.getNodeContainer(parentId.createChild(224));
        await().atMost(5, TimeUnit.SECONDS).pollInterval(100, TimeUnit.MILLISECONDS).untilAsserted(() -> {
            assertThat(metanode_224.getNodeContainerState().isExecuted()).isTrue();
        });
        checkCanExecuteAndCanResetFlagsForAllNodes(metanode_224_23);

		// test absence of node id
        DependentNodeProperties props = DependentNodeProperties.determineDependentNodeProperties(metanode_25);
        assertThat(props.canExecuteNode(new NodeID(9999))).isFalse();
        assertThat(props.canResetNode(new NodeID(9999))).isFalse();

	}

	@SuppressWarnings("javadoc")
    @AfterEach
    public void cancelWorkflow() throws InterruptedException {
        CoreUtil.cancelAndCloseLoadedWorkflow(m_wfm);
    }

	private static void checkCanExecuteAndCanResetFlagsForAllNodes(final WorkflowManager wfm) {
		List<NodeID> nodes = wfm.getNodeContainers().stream().map(nc -> nc.getID()).collect(Collectors.toList());

		List<Boolean> canExecute = nodes.stream().map(wfm::canExecuteNode).collect(Collectors.toList());
		List<Boolean> canReset = nodes.stream().map(wfm::canResetNode).collect(Collectors.toList());

		DependentNodeProperties props = DependentNodeProperties.determineDependentNodeProperties(wfm);

		for (int i = 0; i < nodes.size(); i++) {
			NodeID id = nodes.get(i);
            assertThat(props.canExecuteNode(id)).as("'canExecute' flag differs for node " + id)
                .isEqualTo(canExecute.get(i));
            assertThat(props.canResetNode(id)).as("'canReset' flag differs for node " + id).isEqualTo(canReset.get(i));
		}
	}
}

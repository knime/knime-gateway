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
 *   May 3, 2024 (hornm): created
 */
package org.knime.gateway.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertThrows;

import java.util.List;
import java.util.UUID;

import org.junit.Before;
import org.junit.Test;
import org.knime.core.node.workflow.WorkflowManager;
import org.knime.core.util.Pair;
import org.knime.gateway.api.entity.NodeIDEnt;
import org.knime.gateway.api.webui.entity.WorkflowMonitorMessageEnt;
import org.knime.gateway.api.webui.util.EntityFactory;
import org.knime.gateway.impl.service.util.DefaultServiceUtil;
import org.knime.gateway.impl.webui.service.DefaultWorkflowService;
import org.knime.gateway.impl.webui.service.GatewayServiceTest;
import org.knime.gateway.testing.helper.TestWorkflowCollection;

/**
 * A mixture of tests around encrypted metanodes and components, i.e. to make sure we don't expose their content
 * accidentally.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
@SuppressWarnings({"javadoc", "java:S5960"}) // assertions in production code (this is test code)
public class EncryptedComponentAndMetanodeTest extends GatewayServiceTest {

    private Pair<UUID, WorkflowManager> m_wfm;

    private static final NodeIDEnt ENCRYPTED_COMPONENT = new NodeIDEnt(2);

    private static final NodeIDEnt ENCRYPTED_METANODE = new NodeIDEnt(3);

    /**
     * Loads the workflow to test.
     *
     * @throws Exception
     */
    @Before
    public void loadWorkflow() throws Exception {
        m_wfm = loadWorkflow(TestWorkflowCollection.ENCRYPTED_METANODE_AND_COMPONENT);
    }

    /**
     * Tests the {@link DefaultServiceUtil}.
     */
    @Test
    public void testDefaultServiceUtil() {
        assertThrows(IllegalStateException.class,
            () -> DefaultServiceUtil.getWorkflowManager(m_wfm.getFirst().toString(), ENCRYPTED_COMPONENT));
        assertThrows(IllegalStateException.class,
            () -> DefaultServiceUtil.getWorkflowManager(m_wfm.getFirst().toString(), ENCRYPTED_METANODE));
    }

    @Test
    public void testMessagesForLockedMetanode() {
        m_wfm.getSecond().executeAllAndWaitUntilDone();
        var state = EntityFactory.WorkflowMonitorState.buildWorkflowMonitorStateEnt(m_wfm.getSecond());

        var idsWithErrors = state.getErrors().stream().map(WorkflowMonitorMessageEnt::getNodeId);
        assertThat(idsWithErrors).doesNotContain(ENCRYPTED_METANODE);
        assertNoMessageForContainedNodes(ENCRYPTED_METANODE, state.getErrors());

        var idsWithWarnings = state.getWarnings().stream().map(WorkflowMonitorMessageEnt::getNodeId);
        assertThat(idsWithWarnings).doesNotContain(ENCRYPTED_METANODE);
        assertNoMessageForContainedNodes(ENCRYPTED_METANODE, state.getWarnings());
    }

    private static void assertNoMessageForContainedNodes(final NodeIDEnt container, final List<WorkflowMonitorMessageEnt> messages) {
        var haveMessagesOfChildren = messages.stream().anyMatch(m -> {
            // matches if e.g. m.getNodeId()=[3, 2] and container.getNodeIDs=[3]
            return m.getNodeId().getNodeIDs()[0] == container.getNodeIDs()[0];
        });
        assertThat(!haveMessagesOfChildren);
    }

    @Test
    public void testMessagesForLockedComponent() {
        m_wfm.getSecond().executeAllAndWaitUntilDone();
        var state = EntityFactory.WorkflowMonitorState.buildWorkflowMonitorStateEnt(m_wfm.getSecond());
        var idsWithErrors = state.getErrors().stream().map(WorkflowMonitorMessageEnt::getNodeId);
        assertThat(idsWithErrors).contains(ENCRYPTED_COMPONENT);
        assertNoMessageForContainedNodes(ENCRYPTED_COMPONENT, state.getErrors());
        assertNoMessageForContainedNodes(ENCRYPTED_COMPONENT, state.getWarnings());
    }

    /**
     * Tests the {@link DefaultWorkflowService}-implementation.
     *
     */
    @Test
    public void testWorkflowService() {
        var ws = DefaultWorkflowService.getInstance();
        var projectId = m_wfm.getFirst().toString();
        assertThrows(IllegalStateException.class, () -> ws.getWorkflow(projectId, new NodeIDEnt(2), Boolean.FALSE, null));

        var monitorState = ws.getWorkflowMonitorState(projectId).getState();
        assertThat(monitorState.getErrors()).isEmpty();
        assertThat(monitorState.getWarnings()).isEmpty();
    }

}

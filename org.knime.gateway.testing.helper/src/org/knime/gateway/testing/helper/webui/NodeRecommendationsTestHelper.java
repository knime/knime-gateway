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
 *   Aug 24, 2022 (Kai Franze, KNIME GmbH): created
 */
package org.knime.gateway.testing.helper.webui;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.lessThanOrEqualTo;
import static org.junit.Assert.assertThrows;
import static org.knime.gateway.api.entity.NodeIDEnt.getRootID;

import java.util.List;

import org.hamcrest.core.IsNull;
import org.knime.gateway.api.entity.NodeIDEnt;
import org.knime.gateway.api.webui.entity.NodeTemplateEnt;
import org.knime.gateway.api.webui.service.NodeRepositoryService;
import org.knime.gateway.api.webui.service.util.ServiceExceptions.OperationNotAllowedException;
import org.knime.gateway.testing.helper.ResultChecker;
import org.knime.gateway.testing.helper.ServiceProvider;
import org.knime.gateway.testing.helper.TestWorkflowCollection;
import org.knime.gateway.testing.helper.WorkflowExecutor;
import org.knime.gateway.testing.helper.WorkflowLoader;

/**
 * Test {@link NodeRepositoryService#getNodeRecommendations(String, NodeIDEnt, NodeIDEnt, Integer, Integer, Boolean)}
 *
 * @author Kai Franze, KNIME GmbH
 */
public class NodeRecommendationsTestHelper extends WebUIGatewayServiceTestHelper {

    private final NodeIDEnt m_datagenerator = new NodeIDEnt(1);

    private final NodeIDEnt m_columnFilter = new NodeIDEnt(2);

    private final NodeIDEnt m_metanode = new NodeIDEnt(6);

    private final NodeIDEnt m_component = new NodeIDEnt(12);

    private static final String SUCCESSORS = "SUCCESSORS";

    private static final String PREDECESSORS = "PREDECESSORS";

    /**
     * @param entityResultChecker
     * @param serviceProvider
     * @param workflowLoader
     * @param workflowExecutor
     */
    public NodeRecommendationsTestHelper(final ResultChecker entityResultChecker, final ServiceProvider serviceProvider,
        final WorkflowLoader workflowLoader, final WorkflowExecutor workflowExecutor) {
        super(NodeServiceTestHelper.class, entityResultChecker, serviceProvider, workflowLoader, workflowExecutor);
    }

    /**
     * Tests node recommendations for nodes present on the workflow
     *
     * @throws Exception
     */
    public void testNodeRecommendations() throws Exception {
        executeRecommendationsTest(m_datagenerator, 1, SUCCESSORS);
        executeRecommendationsTest(m_columnFilter, 1, PREDECESSORS);
    }

    /**
     * Tests node recommendations if no node is selected, yielding only source nodes
     *
     * @throws Exception
     */
    public void testNodeRecommendationsForSourceNodes() throws Exception {
        executeRecommendationsTest(null, null, null);
    }

    private void executeRecommendationsTest(final NodeIDEnt nodeId, final Integer portIdx, final String direction)
        throws Exception {
        var projectId = loadWorkflow(TestWorkflowCollection.GENERAL_WEB_UI);
        var workflowId = getRootID();
        var resultDefault = nrs().getNodeRecommendations(projectId, workflowId, nodeId, portIdx, null, direction, null);
        assertRecommendations(resultDefault, 12, false);
        // Max number of 13*2 recommendations received
        // (successor recommendation + backfilled 'most frequently used nodes')
        var resultMinimal26 =
            nrs().getNodeRecommendations(projectId, workflowId, nodeId, portIdx, 101, direction, false);
        assertRecommendations(resultMinimal26, 26, false);
        var resultFull7 = nrs().getNodeRecommendations(projectId, workflowId, nodeId, portIdx, 7, direction, true);
        assertRecommendations(resultFull7, 7, true);
        var resultPredecessors =
            nrs().getNodeRecommendations(projectId, workflowId, nodeId, portIdx, 1, direction, true);
        assertRecommendations(resultPredecessors, 7, true);
    }

    private static void assertRecommendations(final List<NodeTemplateEnt> recommendations, final int nodesLimit,
        final boolean fullTemplateInfo) {
        assertThat("Result size exceeds the expected number of nodes", recommendations.size(),
            lessThanOrEqualTo(nodesLimit));
        if (fullTemplateInfo) {
            recommendations
                .forEach(nt -> assertThat("This full template is incomplete", nt.getIcon(), is(IsNull.notNullValue())));
        } else {
            recommendations.forEach(
                nt -> assertThat("This minimal template is not minimal", nt.getIcon(), is(IsNull.nullValue())));
        }
    }

    /**
     * Tests cases where it should throw exceptions
     *
     * @throws Exception
     */
    public void testNodeRecommendationsThrowingExceptions() throws Exception {
        var projectId = loadWorkflow(TestWorkflowCollection.GENERAL_WEB_UI);
        var workflowId = getRootID();
        assertThrows("<nodeId> and <portIdx> must either be both null or not null", OperationNotAllowedException.class,
            () -> nrs().getNodeRecommendations(projectId, workflowId, m_datagenerator, null, null, null, null));
        assertThrows("<nodeId> and <portIdx> must either be both null or not null", OperationNotAllowedException.class,
            () -> nrs().getNodeRecommendations(projectId, workflowId, null, 1, null, null, null));
        assertThrows("<nodeId> and <direction> must either be both null or not null",
            OperationNotAllowedException.class,
            () -> nrs().getNodeRecommendations(projectId, workflowId, null, 1, 7, null, null));
        assertThrows("Cannot recommend nodes for non-existing port", OperationNotAllowedException.class,
            () -> nrs().getNodeRecommendations(projectId, workflowId, m_datagenerator, 4, null, SUCCESSORS, null));
        assertThrows("Cannot recommend nodes for non-existing port", OperationNotAllowedException.class,
            () -> nrs().getNodeRecommendations(projectId, workflowId, m_datagenerator, -1, null, SUCCESSORS, null));
    }

    /**
     * Tests the node recommendations for components and metanodes as provided by the
     * {@link TestNodeTripleProviderFactory}.
     *
     * @throws Exception
     */
    public void testNodeRecommendationsForComponentAndMetanode() throws Exception {
        var projectId = loadWorkflow(TestWorkflowCollection.GENERAL_WEB_UI);
        var workflowId = getRootID();
        // suggests the most frequently used node(s) - and that's only the 'String Manipulation'-node
        // since it's the only one compatible with the table port (see TestNodeTripleProviderFactory)
        var recommendations = nrs().getNodeRecommendations(projectId, workflowId, m_metanode, 1, 7, SUCCESSORS, true);
        assertThat(recommendations, hasSize(1));
        assertThat(recommendations.get(0).getName(), is("String Manipulation"));
        recommendations = nrs().getNodeRecommendations(projectId, workflowId, m_component, 1, 7, SUCCESSORS, true);
        assertThat(recommendations, hasSize(1));
        assertThat(recommendations.get(0).getName(), is("String Manipulation"));

        // Suggest most frequently used nodes - as predecessors compatible with table port
        recommendations =
            nrs().getNodeRecommendations(projectId, workflowId, m_metanode, 0, null, PREDECESSORS, true);
        assertThat(recommendations, hasSize(7));
        recommendations =
            nrs().getNodeRecommendations(projectId, workflowId, m_component, 1, null, PREDECESSORS, true);
        assertThat(recommendations, hasSize(7));
    }

}

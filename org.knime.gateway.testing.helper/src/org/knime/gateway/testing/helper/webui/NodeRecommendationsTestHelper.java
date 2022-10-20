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
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThrows;
import static org.knime.gateway.api.entity.NodeIDEnt.getRootID;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import org.eclipse.core.runtime.preferences.DefaultScope;
import org.hamcrest.core.IsNull;
import org.knime.core.node.KNIMEConstants;
import org.knime.gateway.api.entity.NodeIDEnt;
import org.knime.gateway.api.webui.entity.NodeRecommendationsEnt;
import org.knime.gateway.api.webui.service.util.ServiceExceptions.OperationNotAllowedException;
import org.knime.gateway.testing.helper.ResultChecker;
import org.knime.gateway.testing.helper.ServiceProvider;
import org.knime.gateway.testing.helper.TestWorkflowCollection;
import org.knime.gateway.testing.helper.WorkflowExecutor;
import org.knime.gateway.testing.helper.WorkflowLoader;

/**
 * Test node recommendations endpoint
 *
 * @author Kai Franze, KNIME GmbH
 */
public class NodeRecommendationsTestHelper extends WebUIGatewayServiceTestHelper {

    private static final String PREFERENCE_QUALIFIER = "org.knime.workbench.workflowcoach";

    private static final String PREFERENCE_KEY = "community_node_triple_provider";

    private static final String FILE_URL = "https://update.knime.com/community_recommendations.json";

    private static final String FILE_NAME = "community_recommendations.json";

    private static final Path FILE_PATH = Paths.get(KNIMEConstants.getKNIMEHomeDir(), FILE_NAME);

    /**
     * @param entityResultChecker
     * @param serviceProvider
     * @param workflowLoader
     * @param workflowExecutor
     */
    protected NodeRecommendationsTestHelper(final ResultChecker entityResultChecker, final ServiceProvider serviceProvider,
        final WorkflowLoader workflowLoader, final WorkflowExecutor workflowExecutor) {
        super(NodeServiceTestHelper.class, entityResultChecker, serviceProvider, workflowLoader, workflowExecutor);
    }

    /**
     * Helper method to setup the environment
     */
    @SuppressWarnings("resource")
    private static void beforeTest() throws IOException {
        DefaultScope.INSTANCE.getNode(PREFERENCE_QUALIFIER).putBoolean(PREFERENCE_KEY, true);
        InputStream in = new URL(FILE_URL).openStream();
        Files.copy(in, FILE_PATH, StandardCopyOption.REPLACE_EXISTING);
    }

    /**
     * Helper method to clean up
     */
    private static void afterTest() throws IOException {
        Files.delete(FILE_PATH);
    }

    /**
     * Tests node recommendations for nodes present on the workflow
     *
     * @throws Exception
     */
    public void testNodeRecommendations() throws Exception {
        executeRecommendationsTest(new NodeIDEnt(1), 1);
    }

    /**
     * Tests node recommendations if no node is selected, yielding only source nodes
     *
     * @throws Exception
     */
    public void testNodeRecommendationsForSourceNodes() throws Exception {
        executeRecommendationsTest(null, null);
    }

    private void executeRecommendationsTest(final NodeIDEnt nodeId, final Integer portIdx) throws Exception {
        // Setup
        beforeTest();

        // Perform test
        var projectId = loadWorkflow(TestWorkflowCollection.GENERAL_WEB_UI);
        var workflowId = getRootID();
        var resultDefault = nrs().getNodeRecommendations(projectId, workflowId, nodeId, portIdx, null, null);
        assertRecommendations(resultDefault, 12, false);
        var resultMinimal10 = nrs().getNodeRecommendations(projectId, workflowId, nodeId, portIdx, 10, false);
        assertRecommendations(resultMinimal10, 10, false);
        var resultFull20 = nrs().getNodeRecommendations(projectId, workflowId, nodeId, portIdx, 20, true);
        assertRecommendations(resultFull20, 20, true);

        // Clean up
        afterTest();
    }

    private static void assertRecommendations(final NodeRecommendationsEnt recommendations, final int nodesLimit,
        final boolean fullTemplateInfo) {
        var nodeTemplates = recommendations.getNodes();
        assertThat("Result size doesn't match", nodeTemplates.size(), is(nodesLimit));
        if (fullTemplateInfo) {
            nodeTemplates.forEach(nt -> assertThat("This full template is incomplete", nt.getIcon(), is(IsNull.notNullValue())));
        } else {
            nodeTemplates.forEach(nt -> assertThat("This minimal template is not minimal", nt.getIcon(), is(IsNull.nullValue())));
        }
    }

    /**
     * Tests cases where it should throw exceptions
     *
     * @throws Exception
     */
    public void testNodeRecommendationsThrowingExceptions() throws Exception {
        // Setup
        beforeTest();

        // Perform test
        var projectId = loadWorkflow(TestWorkflowCollection.GENERAL_WEB_UI);
        var workflowId = getRootID();
        assertThrows("<nodeId> and <portIdx> must either be both null or not null", OperationNotAllowedException.class,
            () -> nrs().getNodeRecommendations(projectId, workflowId, new NodeIDEnt(1), null, null, null));
        assertThrows("<nodeId> and <portIdx> must either be both null or not null", OperationNotAllowedException.class,
            () -> nrs().getNodeRecommendations(projectId, workflowId, null, 1, null, null));
        assertThrows("Cannot recommend nodes for non-existing port", OperationNotAllowedException.class,
            () -> nrs().getNodeRecommendations(projectId, workflowId, new NodeIDEnt(1), 4, null, null));
        assertThrows("Cannot recommend nodes for non-existing port", OperationNotAllowedException.class,
            () -> nrs().getNodeRecommendations(projectId, workflowId, new NodeIDEnt(1), -1, null, null));

        // TODO: Add test for container nodes exception

        // Clean up
        afterTest();
    }

}

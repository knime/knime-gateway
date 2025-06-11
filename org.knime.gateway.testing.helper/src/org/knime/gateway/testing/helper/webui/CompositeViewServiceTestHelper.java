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
 *   Jun 12, 2025 (tkampmann): created
 */
package org.knime.gateway.testing.helper.webui;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

import org.knime.gateway.api.entity.NodeIDEnt;
import org.knime.gateway.api.util.VersionId;
import org.knime.gateway.api.webui.service.CompositeViewService;
import org.knime.gateway.json.util.ObjectMapperUtil;
import org.knime.gateway.testing.helper.ResultChecker;
import org.knime.gateway.testing.helper.ServiceProvider;
import org.knime.gateway.testing.helper.TestWorkflow;
import org.knime.gateway.testing.helper.TestWorkflowCollection;
import org.knime.gateway.testing.helper.WorkflowExecutor;
import org.knime.gateway.testing.helper.WorkflowLoader;

/**
 * Test for the endpoints of the composite view service.
 *
 * @author Tobias Kampmann, TNG
 */
public class CompositeViewServiceTestHelper extends WebUIGatewayServiceTestHelper {

    /**
     * Creates a new test helper for the composite view service.
     *
     * @param entityResultChecker the result checker to use for checking the results of the service calls
     * @param serviceProvider the service provider to use for getting the service instances
     * @param workflowLoader the workflow loader to use for loading workflows
     * @param workflowExecutor the workflow executor to use for executing workflows
     */
    public CompositeViewServiceTestHelper(final ResultChecker entityResultChecker,
        final ServiceProvider serviceProvider, final WorkflowLoader workflowLoader,
        final WorkflowExecutor workflowExecutor) {
        super(ComponentServiceTestHelper.class, entityResultChecker, serviceProvider, workflowLoader, workflowExecutor);
    }

    /**
     * Tests {@link CompositeViewService#getCompositeViewPage(String, NodeIDEnt, String, NodeIDEnt)}.
     *
     * @throws Exception if the test fails
     */
    public void testCompositeViewPage() throws Exception {
        var projectId = loadWorkflow(TestWorkflowCollection.VIEW_NODES);
        var compositeViewPage =
            (String)cvs().getCompositeViewPage(projectId, NodeIDEnt.getRootID(), null, new NodeIDEnt("root:11"));
        var compositeViewPageTree = ObjectMapperUtil.getInstance().getObjectMapper().readTree(compositeViewPage);
        var nodeView = compositeViewPageTree.get("nodeViews").get("11:0:10");
        assertThat(nodeView.get("nodeInfo").get("nodeAnnotation").asText(), is("novel view-node"));
        var webNode = compositeViewPageTree.get("webNodes").get("11:0:9");
        assertThat(webNode.get("nodeInfo").get("nodeAnnotation").asText(), is("JS view-node"));
    }

    /**
     * Tests {@link CompositeViewService#getCompositeViewPage(String, NodeIDEnt, String, NodeIDEnt)} with versions.
     *
     * @throws Exception if the test fails
     */
    public void testCompositeViewPageWithVersions() throws Exception {
        var testWorkflowWithVersion = TestWorkflow.WithVersion.of( //
            TestWorkflowCollection.VERSIONS_EXTENDED_CURRENT_STATE, //
            TestWorkflowCollection.VERSIONS_EXTENDED_EARLIER_VERSION::getWorkflowDir //
        );
        var projectId = loadWorkflow(testWorkflowWithVersion);
        var nodeId = new NodeIDEnt("root:9"); // Component

        var currentStateCompositeViewPage = (String)cvs().getCompositeViewPage(projectId, NodeIDEnt.getRootID(),
            VersionId.currentState().toString(), nodeId);
        var currentStateCompositeViewNode =
            ObjectMapperUtil.getInstance().getObjectMapper().readTree(currentStateCompositeViewPage);

        var version = VersionId.parse("2");
        ws().getWorkflow(projectId, NodeIDEnt.getRootID(), version.toString(), false);

        var versionCompositeViewPage =
            (String)cvs().getCompositeViewPage(projectId, NodeIDEnt.getRootID(), version.toString(), nodeId);
        var versionCompositeViewNode =
            ObjectMapperUtil.getInstance().getObjectMapper().readTree(versionCompositeViewPage);

        assertThat(currentStateCompositeViewNode.get("nodeViews"), is(not(versionCompositeViewNode.get("nodeViews"))));
    }
}

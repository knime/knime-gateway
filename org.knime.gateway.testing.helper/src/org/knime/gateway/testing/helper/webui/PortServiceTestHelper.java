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
 *   Aug 17, 2022 (hornm): created
 */
package org.knime.gateway.testing.helper.webui;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.startsWith;
import static org.junit.Assert.assertThrows;
import static org.knime.gateway.api.entity.NodeIDEnt.getRootID;

import org.knime.core.webui.data.RpcDataService;
import org.knime.gateway.api.entity.NodeIDEnt;
import org.knime.gateway.api.util.VersionId;
import org.knime.gateway.api.webui.service.PortService;
import org.knime.gateway.api.webui.service.util.ServiceExceptions.InvalidRequestException;
import org.knime.gateway.impl.project.ProjectManager;
import org.knime.gateway.json.util.ObjectMapperUtil;
import org.knime.gateway.testing.helper.ResultChecker;
import org.knime.gateway.testing.helper.ServiceProvider;
import org.knime.gateway.testing.helper.TestWorkflow;
import org.knime.gateway.testing.helper.TestWorkflowCollection;
import org.knime.gateway.testing.helper.WorkflowExecutor;
import org.knime.gateway.testing.helper.WorkflowLoader;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * Test for the endpoints of the port service.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
@SuppressWarnings("javadoc")
public class PortServiceTestHelper extends WebUIGatewayServiceTestHelper {

    /**
     * @param entityResultChecker
     * @param serviceProvider
     * @param workflowLoader
     * @param workflowExecutor
     * @param projectManager
     */
    public PortServiceTestHelper(final ResultChecker entityResultChecker, final ServiceProvider serviceProvider,
        final WorkflowLoader workflowLoader, final WorkflowExecutor workflowExecutor,
        final ProjectManager projectManager) {
        super(NodeServiceTestHelper.class, entityResultChecker, serviceProvider, workflowLoader, workflowExecutor,
            projectManager);
    }

    /**
     * Tests {@link PortService#getPortView(String, NodeIDEnt, NodeIDEnt, Integer)}.
     *
     * @throws Exception
     */
    public void testGetPortView() throws Exception {
        var wfId = loadWorkflow(TestWorkflowCollection.GENERAL_WEB_UI);

        // get table port view (registered at index 1) for a non-executed node
        var message = assertThrows(InvalidRequestException.class,
            () -> ps().getPortView(wfId, getRootID(), VersionId.currentState().toString(), new NodeIDEnt(1), 1, 1))
                .getMessage();
        assertThat(message, containsString("No port view available"));

        // get flow variable 'spec' view 0
        var portView = ps().getPortView(wfId, getRootID(), VersionId.currentState().toString(), new NodeIDEnt(1), 0, 0);
        assertPortView(portView, "root:1", "flowvariableview", "SHADOW_APP");

        // get flow variable port view 1
        portView = ps().getPortView(wfId, getRootID(), VersionId.currentState().toString(), new NodeIDEnt(1), 0, 1);
        assertPortView(portView, "root:1", "flowvariableview", "SHADOW_APP");

        // check table spec view (registered at index 0)
        portView = ps().getPortView(wfId, getRootID(), VersionId.currentState().toString(), new NodeIDEnt(1), 1, 0);
        assertPortView(portView, "root:1", "tableview", "SHADOW_APP");

        executeWorkflow(wfId);

        // get table port view 1 now that node is executed
        portView = ps().getPortView(wfId, getRootID(), VersionId.currentState().toString(), new NodeIDEnt(1), 1, 1);
        assertPortView(portView, "root:1", "tableview", "SHADOW_APP");

        // get table statistics  now that node is executed
        portView = ps().getPortView(wfId, getRootID(), VersionId.currentState().toString(), new NodeIDEnt(1), 1, 2);
        assertPortView(portView, "root:1", "tableview", "SHADOW_APP");

        // get data for an inactive port
        message = assertThrows(InvalidRequestException.class,
            () -> ps().getPortView(wfId, getRootID(), VersionId.currentState().toString(), new NodeIDEnt(14), 1, 0))
                .getMessage();
        assertThat(message, containsString("No port view available"));

        // get data for a metanode port
        portView = ps().getPortView(wfId, getRootID(), VersionId.currentState().toString(), new NodeIDEnt(6), 0, 1);
        var portViewJsonNode = ObjectMapperUtil.getInstance().getObjectMapper().convertValue(portView, JsonNode.class);
        assertThat(portViewJsonNode.get("resourceInfo").get("id").textValue(), is("tableview"));

        // get data for a metanode port that is not executed
        message = assertThrows(InvalidRequestException.class,
            () -> ps().getPortView(wfId, getRootID(), VersionId.currentState().toString(), new NodeIDEnt(6), 2, 1))
                .getMessage();
        assertThat(message, containsString("No port view available"));
    }

    /**
     * Tests {@link PortService#getPortView(String, NodeIDEnt, NodeIDEnt, Integer)} with different versions.
     *
     * @throws Exception
     */
    public void testGetPortViewWithVersions() throws Exception {
        var testWorkflowWithVersion = TestWorkflow.WithVersion.of( //
            TestWorkflowCollection.VERSIONS_EXTENDED_CURRENT_STATE, //
            TestWorkflowCollection.VERSIONS_EXTENDED_EARLIER_VERSION::getWorkflowDir //
        );
        var projectId = loadWorkflow(testWorkflowWithVersion);
        var nodeId = new NodeIDEnt(2); // Row filter

        // Current state
        var currentStatePortView =
            ps().getPortView(projectId, NodeIDEnt.getRootID(), VersionId.currentState().toString(), nodeId, 1, 1);
        var currentStatePortViewNode =
            ObjectMapperUtil.getInstance().getObjectMapper().convertValue(currentStatePortView, JsonNode.class);

        var version = VersionId.parse("2");
        loadVersionAndSetActive(projectId, version);

        // Earlier version
        var earlierVersionPortView =
            ps().getPortView(projectId, NodeIDEnt.getRootID(), version.toString(), nodeId, 1, 1);
        var earlierVersionPortViewNode =
            ObjectMapperUtil.getInstance().getObjectMapper().convertValue(earlierVersionPortView, JsonNode.class);

        // "initial data" should be different
        assertThat(currentStatePortViewNode.get("initialData"), is(not(earlierVersionPortViewNode.get("initialData"))));
    }

    /**
     * Tests {@link PortService#getPortView(String, NodeIDEnt, NodeIDEnt, Integer)} for component- and metanode-ports.
     *
     * @throws Exception
     */
    public void testGetPortViewForComponentAndMetanode() throws Exception {
        var wfId = loadWorkflow(TestWorkflowCollection.GENERAL_WEB_UI);
        executeWorkflow(wfId);

        // get port view for a component
        var portView =
            ps().getPortView(wfId, getRootID(), VersionId.currentState().toString(), new NodeIDEnt(23), 1, 1);
        assertPortView(portView, "root:23", "tableview", "SHADOW_APP");

        // get port view for a metanode
        portView = ps().getPortView(wfId, getRootID(), VersionId.currentState().toString(), new NodeIDEnt(6), 0, 1);
        assertPortView(portView, "root:6", "tableview", "SHADOW_APP");
    }

    /**
     * Test {@link PortService#getPortView(String, NodeIDEnt, NodeIDEnt, Integer, Integer)} for nodes with flow-variable
     * output ports
     *
     * @throws Exception
     */
    public void testGetPortViewForFlowVariableOutPort() throws Exception {
        var wfId = loadWorkflow(TestWorkflowCollection.EXECUTION_STATES);
        var waitNodeIdx = 2;  // a non-executed wait node (has a flow variable outport)
        var waitNodeId = new NodeIDEnt(waitNodeIdx);

        var portView = ps().getPortView(wfId, getRootID(), VersionId.currentState().toString(), waitNodeId, 1, 0);
        assertPortView(portView, "root:" + waitNodeIdx, "flowvariableview", "SHADOW_APP",
            TestWorkflowCollection.EXECUTION_STATES);

        portView = ps().getPortView(wfId, getRootID(), VersionId.currentState().toString(), waitNodeId, 1, 1);
        assertPortView(portView, "root:" + waitNodeIdx, "flowvariableview", "SHADOW_APP",
            TestWorkflowCollection.EXECUTION_STATES);
    }

    private static void assertPortView(final Object portView, final String nodeId, final String expectedResourceId,
        final String expectedResourceType) {
        assertPortView(portView, nodeId, expectedResourceId, expectedResourceType,
            TestWorkflowCollection.GENERAL_WEB_UI);
    }

    private static void assertPortView(final Object portView, final String nodeId, final String expectedResourceId,
        final String expectedResourceType, final TestWorkflowCollection testWorkflow) {
        var portViewJsonNode = ObjectMapperUtil.getInstance().getObjectMapper().convertValue(portView, JsonNode.class);
        assertThat(portViewJsonNode.get("projectId").textValue(),
            containsString(testWorkflow.getName()));
        assertThat(portViewJsonNode.get("workflowId").textValue(), is("root"));
        assertThat(portViewJsonNode.get("nodeId").textValue(), is(nodeId));
        assertThat(portViewJsonNode.get("extensionType").textValue(), is("port"));
        assertThat(portViewJsonNode.get("initialData").textValue(), startsWith("{\"result\":"));
        var resourceInfo = portViewJsonNode.get("resourceInfo");
        assertThat(resourceInfo.get("id").textValue(), is(expectedResourceId));
        assertThat(resourceInfo.get("type").textValue(), is(expectedResourceType));
    }

    /**
     * Tests {@link PortService#callPortDataService(String, NodeIDEnt, NodeIDEnt, Integer, String, String)}.
     *
     * @throws Exception
     */
    public void testCallPortDataService() throws Exception {
        var wfId = loadWorkflow(TestWorkflowCollection.GENERAL_WEB_UI);
        executeWorkflow(wfId);

        // initialData
        var initialData = ps().callPortDataService(wfId, getRootID(), VersionId.currentState().toString(),
            new NodeIDEnt(1), 1, 1, "initial_data", "");
        var jsonNode = ObjectMapperUtil.getInstance().getObjectMapper().readTree(initialData);
        assertThat(jsonNode.get("result").get("table"), notNullValue());

        // data
        var jsonRpcRequest = RpcDataService.jsonRpcRequest("getTable", "Universe_0_0", "0", "2", null, "false", "true",
            "false", "false");
        var data = ps().callPortDataService(wfId, getRootID(), VersionId.currentState().toString(), new NodeIDEnt(1), 1,
            1, "data", jsonRpcRequest);
        jsonNode = ObjectMapperUtil.getInstance().getObjectMapper().readTree(data);
        assertThat(jsonNode.get("result").get("rows"), notNullValue());
        assertThat(jsonNode.get("id").intValue(), is(1));
    }

    /**
     * Tests {@link PortService#callPortDataService(String, NodeIDEnt, NodeIDEnt, Integer, String, String)} with
     * different versions.
     *
     * @throws Exception
     */
    public void testCallPortDataServiceWithVersions() throws Exception {
        var testWorkflowWithVersion = TestWorkflow.WithVersion.of( //
            TestWorkflowCollection.VERSIONS_EXTENDED_CURRENT_STATE, //
            TestWorkflowCollection.VERSIONS_EXTENDED_EARLIER_VERSION::getWorkflowDir //
        );
        var projectId = loadWorkflow(testWorkflowWithVersion);
        var nodeId = new NodeIDEnt(2); // Row filter

        // Current state
        var currentStateInitialData = ps().callPortDataService(projectId, getRootID(),
            VersionId.currentState().toString(), nodeId, 1, 1, "initial_data", "");
        var currentStateInitialDataNode =
            ObjectMapperUtil.getInstance().getObjectMapper().readTree(currentStateInitialData);

        var jsonRpcRequest = RpcDataService.jsonRpcRequest("getTable", "String column", "0", "1", null, "false",
            "false", "true", "false");
        var currentStateData = ps().callPortDataService(projectId, getRootID(), VersionId.currentState().toString(),
            nodeId, 1, 1, "data", jsonRpcRequest);
        var currentStateDataNode = ObjectMapperUtil.getInstance().getObjectMapper().readTree(currentStateData);

        var version = VersionId.parse("2");
        loadVersionAndSetActive(projectId, version);

        // Earlier version
        var earlierVersionInitialData =
            ps().callPortDataService(projectId, getRootID(), version.toString(), nodeId, 1, 1, "initial_data", "");
        var earlierVersionInitialDataNode =
            ObjectMapperUtil.getInstance().getObjectMapper().readTree(earlierVersionInitialData);

        var earlierVersionData =
            ps().callPortDataService(projectId, getRootID(), version.toString(), nodeId, 1, 1, "data", jsonRpcRequest);
        var earlierVersionDataNode = ObjectMapperUtil.getInstance().getObjectMapper().readTree(earlierVersionData);

        // "initial data" and "data" are different
        assertThat(currentStateInitialDataNode.get("result"), is(not(earlierVersionInitialDataNode.get("result"))));
        assertThat(currentStateDataNode.get("result"), is(not(earlierVersionDataNode.get("result"))));
    }

}

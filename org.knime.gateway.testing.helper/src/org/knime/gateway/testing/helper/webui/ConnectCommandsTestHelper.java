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
 *   Apr 11, 2024 (kai): created
 */
package org.knime.gateway.testing.helper.webui;

import static java.util.Collections.emptyList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThrows;
import static org.knime.gateway.api.entity.EntityBuilderManager.builder;
import static org.knime.gateway.api.entity.NodeIDEnt.getRootID;
import static org.knime.gateway.testing.helper.webui.DeleteCommandTestHelper.createDeleteCommandEnt;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.awaitility.Awaitility;
import org.knime.gateway.api.entity.ConnectionIDEnt;
import org.knime.gateway.api.entity.NodeIDEnt;
import org.knime.gateway.api.webui.entity.AutoConnectCommandEnt;
import org.knime.gateway.api.webui.entity.AutoConnectCommandEnt.AutoConnectCommandEntBuilder;
import org.knime.gateway.api.webui.entity.ConnectCommandEnt;
import org.knime.gateway.api.webui.entity.ConnectCommandEnt.ConnectCommandEntBuilder;
import org.knime.gateway.api.webui.entity.ConnectableEnt;
import org.knime.gateway.api.webui.entity.ConnectableEnt.ConnectableEntBuilder;
import org.knime.gateway.api.webui.entity.ConnectionEnt;
import org.knime.gateway.api.webui.entity.WorkflowCommandEnt.KindEnum;
import org.knime.gateway.api.webui.service.util.ServiceExceptions.OperationNotAllowedException;
import org.knime.gateway.testing.helper.ResultChecker;
import org.knime.gateway.testing.helper.ServiceProvider;
import org.knime.gateway.testing.helper.TestWorkflowCollection;
import org.knime.gateway.testing.helper.WorkflowExecutor;
import org.knime.gateway.testing.helper.WorkflowLoader;

/**
 * Helper class to test {@link ConnectCommandEnt} and {@link AutoConnectCommandEnt} workflow command implementations
 *
 * @author Kai Franze, KNIME GmbH, Germany
 */
public class ConnectCommandsTestHelper extends WebUIGatewayServiceTestHelper {

    /**
     * @param entityResultChecker
     * @param serviceProvider
     * @param workflowLoader
     * @param workflowExecutor
     */
    public ConnectCommandsTestHelper(final ResultChecker entityResultChecker, final ServiceProvider serviceProvider,
        final WorkflowLoader workflowLoader, final WorkflowExecutor workflowExecutor) {
        super(ConnectCommandsTestHelper.class, entityResultChecker, serviceProvider, workflowLoader, workflowExecutor);
    }

    /**
     * Tests {@link ConnectCommandEnt} replacing an existing command
     *
     * @throws Exception
     */
    public void testConnectReplaceExisting() throws Exception {
        var projectId = loadWorkflow(TestWorkflowCollection.GENERAL_WEB_UI);
        var workflowId = getRootID();
        Map<String, ConnectionEnt> connections =
            ws().getWorkflow(projectId, workflowId, false).getWorkflow().getConnections();
        var originalNumConnections = connections.size();
        assertThat(connections.get("root:10_1").getSourceNode().toString(), is("root:1"));

        // replace existing connection
        var command = buildConnectCommandEnt(new NodeIDEnt(27), 1, new NodeIDEnt(10), 1);
        ws().executeWorkflowCommand(projectId, workflowId, command);
        connections = ws().getWorkflow(projectId, workflowId, false).getWorkflow().getConnections();
        assertThat(connections.size(), is(originalNumConnections));
        assertThat(connections.get("root:10_1").getSourceNode().toString(), is("root:27"));

        // undo
        ws().undoWorkflowCommand(projectId, workflowId);
        connections = ws().getWorkflow(projectId, workflowId, false).getWorkflow().getConnections();
        assertThat(connections.size(), is(originalNumConnections));
        assertThat(connections.get("root:10_1").getSourceNode().toString(), is("root:1"));
    }

    /**
     * Tests {@link ConnectCommandEnt} creating a new connection
     *
     * @throws Exception
     */
    public void testConnectNewConnection() throws Exception {
        var projectId = loadWorkflow(TestWorkflowCollection.GENERAL_WEB_UI);
        var workflowId = getRootID();
        Map<String, ConnectionEnt> connections =
            ws().getWorkflow(projectId, workflowId, false).getWorkflow().getConnections();
        var originalNumConnections = connections.size();
        assertThat(connections.get("root:10_1").getSourceNode().toString(), is("root:1"));

        // new connection
        var command = buildConnectCommandEnt(new NodeIDEnt(27), 1, new NodeIDEnt(21), 2);
        ws().executeWorkflowCommand(projectId, workflowId, command);
        connections = ws().getWorkflow(projectId, workflowId, false).getWorkflow().getConnections();
        assertThat(connections.size(), is(originalNumConnections + 1));
        assertThat(connections.get("root:21_2").getSourceNode().toString(), is("root:27"));

        // undo
        ws().undoWorkflowCommand(projectId, workflowId);
        connections = ws().getWorkflow(projectId, workflowId, false).getWorkflow().getConnections();
        assertThat(connections.size(), is(originalNumConnections));
        assertNull(connections.get("root:21_2"));
    }

    /**
     * Tests {@link ConnectCommandEnt} doing nothing
     *
     * @throws Exception
     */
    public void testConnectNoop() throws Exception {
        var projectId = loadWorkflow(TestWorkflowCollection.GENERAL_WEB_UI);
        var workflowId = getRootID();
        Map<String, ConnectionEnt> connections =
            ws().getWorkflow(projectId, workflowId, false).getWorkflow().getConnections();
        assertThat(connections.get("root:10_1").getSourceNode().toString(), is("root:1"));

        // add already existing connection (command is not added to the undo stack)
        var command = buildConnectCommandEnt(new NodeIDEnt(1), 1, new NodeIDEnt(10), 1);
        ws().executeWorkflowCommand(projectId, workflowId, command);
        Exception exception =
            assertThrows(OperationNotAllowedException.class, () -> ws().undoWorkflowCommand(projectId, workflowId));
        assertThat(exception.getMessage(), is("No command to undo"));
    }

    /**
     * Tests {@link ConnectCommandEnt} throwing exceptions
     *
     * @throws Exception
     */
    public void testConnectExceptions() throws Exception {
        var projectId = loadWorkflow(TestWorkflowCollection.GENERAL_WEB_UI);
        var workflowId = getRootID();
        Map<String, ConnectionEnt> connections =
            ws().getWorkflow(projectId, workflowId, false).getWorkflow().getConnections();
        assertThat(connections.get("root:10_1").getSourceNode().toString(), is("root:1"));

        // add a connection to a node that doesn't exist
        var command2 = buildConnectCommandEnt(new NodeIDEnt(1), 1, new NodeIDEnt(9999999), 1);
        var exception = assertThrows(OperationNotAllowedException.class,
            () -> ws().executeWorkflowCommand(projectId, workflowId, command2));
        assertThat(exception.getMessage(),
            containsString("9999999\" not contained in workflow, nor it's the workflow itself"));

        // add a connection that can't be added (here: because it creates a cycle)
        var command3 = buildConnectCommandEnt(new NodeIDEnt(27), 0, new NodeIDEnt(1), 0);
        exception = assertThrows(OperationNotAllowedException.class,
            () -> ws().executeWorkflowCommand(projectId, workflowId, command3));
        assertThat(exception.getMessage(), containsString("Connection couldn't be created"));
    }

    /**
     * Tests {@link ConnectCommandEnt} within a sub workflow
     *
     * @throws Exception
     */
    public void testConnectSubWorkflow() throws Exception {
        var projectId = loadWorkflow(TestWorkflowCollection.GENERAL_WEB_UI);
        var workflowId = getRootID();
        Map<String, ConnectionEnt> connections =
            ws().getWorkflow(projectId, workflowId, false).getWorkflow().getConnections();
        assertThat(connections.get("root:10_1").getSourceNode().toString(), is("root:1"));

        // add a connection within a sub-workflow (e.g. within a component)
        var component23Id = new NodeIDEnt(23);
        var deleteCommand =
            createDeleteCommandEnt(emptyList(), List.of(new ConnectionIDEnt(new NodeIDEnt(23, 0, 9), 1)), emptyList());
        ws().executeWorkflowCommand(projectId, component23Id, deleteCommand);
        var component23ConnectionRemoved = ws().getWorkflow(projectId, component23Id, false);
        assertThat(component23ConnectionRemoved.getWorkflow().getConnections().size(), is(1));
        var command4 = buildConnectCommandEnt(new NodeIDEnt(23, 0, 10), 1, new NodeIDEnt(23, 0, 9), 1);
        ws().executeWorkflowCommand(projectId, component23Id, command4);
        Awaitility.await().atMost(2, TimeUnit.SECONDS).pollInterval(100, TimeUnit.MILLISECONDS).untilAsserted(() -> {
            var component23ConnectionAdded = ws().getWorkflow(projectId, component23Id, false);
            assertThat(component23ConnectionAdded.getWorkflow().getConnections().size(), is(2));
        });
    }

    private static ConnectCommandEnt buildConnectCommandEnt(final NodeIDEnt source, final Integer sourcePort,
        final NodeIDEnt dest, final Integer destPort) {
        return builder(ConnectCommandEntBuilder.class).setKind(KindEnum.CONNECT).setSourceNodeId(source)
            .setSourcePortIdx(sourcePort).setDestinationNodeId(dest).setDestinationPortIdx(destPort).build();
    }

    /**
     * Tests {@link AutoConnectCommandEnt} with unconnected native nodes
     *
     * @throws Exception
     */
    public void testAutoConnectUnconnectedNativeNodes() throws Exception {
        var projectId = loadWorkflow(TestWorkflowCollection.AUTO_CONNECT_NODES);
        var workflowId = getRootID();

        var c1 = buildConnectableEnt(new NodeIDEnt(1));
        var c2 = buildConnectableEnt(new NodeIDEnt(2));
        var c3 = buildConnectableEnt(new NodeIDEnt(3));
        var c4 = buildConnectableEnt(new NodeIDEnt(4));

        assertAutoConnect(projectId, workflowId, 3, 6, List.of(c1, c2, c3, c4));
    }

    /**
     * Tests {@link AutoConnectCommandEnt} with native nodes checking the resulting connections
     *
     * @throws Exception
     */
    public void testAutoConnectNativeNodesWithConnectionCheck1() throws Exception {
        var projectId = loadWorkflow(TestWorkflowCollection.AUTO_CONNECT_NODES);
        var workflowId = getRootID();

        var c23 = buildConnectableEnt(new NodeIDEnt(23));
        var c24 = buildConnectableEnt(new NodeIDEnt(24));
        var c20 = buildConnectableEnt(new NodeIDEnt(20));
        var c21 = buildConnectableEnt(new NodeIDEnt(21));
        var c22 = buildConnectableEnt(new NodeIDEnt(22));

        assertAutoConnect(projectId, workflowId, 3, 5, List.of(c23, c24, c20, c21, c22));

        var connections = ws().getWorkflow(projectId, workflowId, false).getWorkflow().getConnections().values();
        assertConnection(connections, new NodeIDEnt(23), new NodeIDEnt(24));
        assertConnection(connections, new NodeIDEnt(24), new NodeIDEnt(20));
        assertConnection(connections, new NodeIDEnt(22), new NodeIDEnt(21));
        assertConnection(connections, new NodeIDEnt(20), new NodeIDEnt(22));
    }

    /**
     * Tests {@link AutoConnectCommandEnt} with native nodes checking the resulting connections
     *
     * @throws Exception
     */
    public void testAutoConnectNativeNodesWithConnectionCheck2() throws Exception {
        var projectId = loadWorkflow(TestWorkflowCollection.AUTO_CONNECT_NODES);
        var workflowId = getRootID();

        var c26 = buildConnectableEnt(new NodeIDEnt(26)); // Node with 2 input ports
        var c28 = buildConnectableEnt(new NodeIDEnt(28)); // Node without output ports
        var c29 = buildConnectableEnt(new NodeIDEnt(29)); // Node without input ports
        var c30 = buildConnectableEnt(new NodeIDEnt(30)); // Node without input ports

        assertAutoConnect(projectId, workflowId, 3, 6, List.of(c26, c28, c29, c30));

        var connections = ws().getWorkflow(projectId, workflowId, false).getWorkflow().getConnections().values();
        assertConnection(connections, new NodeIDEnt(29), new NodeIDEnt(26));
        assertConnection(connections, new NodeIDEnt(30), new NodeIDEnt(26));
        assertConnection(connections, new NodeIDEnt(26), new NodeIDEnt(28));
    }

    /**
     * Tests {@link AutoConnectCommandEnt} with native nodes and a component
     *
     * @throws Exception
     */
    public void testAutoConnectNativeNodesAndComponent() throws Exception {
        var projectId = loadWorkflow(TestWorkflowCollection.AUTO_CONNECT_NODES);
        var workflowId = getRootID();

        // "14" is a component
        var c10 = buildConnectableEnt(new NodeIDEnt(10));
        var c11 = buildConnectableEnt(new NodeIDEnt(11));
        var c14 = buildConnectableEnt(new NodeIDEnt(14));

        assertAutoConnect(projectId, workflowId, 3, 5, List.of(c10, c11, c14));
    }

    /**
     * Tests {@link AutoConnectCommandEnt} with native nodes and a metanode
     *
     * @throws Exception
     */
    public void testAutoConnectNativeNodesAndMetanode() throws Exception {
        var projectId = loadWorkflow(TestWorkflowCollection.AUTO_CONNECT_NODES);
        var workflowId = getRootID();

        // "13" is a metanode
        var c5 = buildConnectableEnt(new NodeIDEnt(5));
        var c8 = buildConnectableEnt(new NodeIDEnt(8));
        var c13 = buildConnectableEnt(new NodeIDEnt(13));

        assertAutoConnect(projectId, workflowId, 3, 4, List.of(c5, c8, c13));
    }

    /**
     * Tests {@link AutoConnectCommandEnt} within a component
     *
     * @throws Exception
     */
    public void testAutoConnectWithinComponentWithVirtualNodes() throws Exception {
        var projectId = loadWorkflow(TestWorkflowCollection.AUTO_CONNECT_NODES);
        var workflowId = new NodeIDEnt(14); // A component with one table input and one table output port

        // "1" is a virtual node with one output port, "2" is a virtual node with one input port
        var c1 = buildConnectableEnt(new NodeIDEnt(14, 0, 1));
        var c2 = buildConnectableEnt(new NodeIDEnt(14, 0, 2));
        var c9 = buildConnectableEnt(new NodeIDEnt(14, 0, 9));
        var c12 = buildConnectableEnt(new NodeIDEnt(14, 0, 12));

        assertAutoConnect(projectId, workflowId, 0, 3, List.of(c1, c2, c9, c12));
    }

    /**
     * Tests {@link AutoConnectCommandEnt} within a metanode
     *
     * @throws Exception
     */
    public void testAutoConnectWithinMetaNodeWithMetaNodeBars() throws Exception {
        var projectId = loadWorkflow(TestWorkflowCollection.AUTO_CONNECT_NODES);
        var workflowId = new NodeIDEnt(13); // A metanode with one table input and one table output port

        // "1" is a virtual node with one output port, "2" is a virtual node with one input port
        var cIn = buildConnectableEnt(new NodeIDEnt(13), true, null); // Using 'null'
        var cOut = buildConnectableEnt(new NodeIDEnt(13), false, true); // Using 'false'
        var c6 = buildConnectableEnt(new NodeIDEnt(13, 6));
        var c7 = buildConnectableEnt(new NodeIDEnt(13, 7));

        assertAutoConnect(projectId, workflowId, 0, 3, List.of(cIn, c6, c7, cOut));
    }

    /**
     * Tests {@link AutoConnectCommandEnt} only connecting flow variable ports.
     *
     * @throws Exception
     */
    public void testAutoConnectFlowVariablesOnly() throws Exception {
        var projectId = loadWorkflow(TestWorkflowCollection.AUTO_CONNECT_NODES);
        var workflowId = getRootID();

        var c1 = buildConnectableEnt(new NodeIDEnt(1));
        var c2 = buildConnectableEnt(new NodeIDEnt(2));
        var c3 = buildConnectableEnt(new NodeIDEnt(3));
        var c4 = buildConnectableEnt(new NodeIDEnt(4));

        var connectables = List.of(c1, c2, c3, c4);
        var command = buildAutoConnectCommandEnt(connectables, true);
        var exception = assertThrows(OperationNotAllowedException.class,
            () -> ws().executeWorkflowCommand(projectId, workflowId, command));
        assertThat(exception.getMessage(),
            containsString("Automatically connecting all flow variables is not supported yet"));
    }

    private void assertAutoConnect(final String projectId, final NodeIDEnt workflowId, final int numConnectionsBefore,
        final int numConnectionsAfter, final List<ConnectableEnt> connectables) throws Exception {
        var connectionsBefore = ws().getWorkflow(projectId, workflowId, false).getWorkflow().getConnections();
        assertThat(connectionsBefore.size(), is(numConnectionsBefore));

        var command = buildAutoConnectCommandEnt(connectables, null);
        ws().executeWorkflowCommand(projectId, workflowId, command);

        var connectionsAfter = ws().getWorkflow(projectId, workflowId, false).getWorkflow().getConnections();
        assertThat(connectionsAfter.size(), is(numConnectionsAfter));

        ws().undoWorkflowCommand(projectId, workflowId);
        var connectionsUndo = ws().getWorkflow(projectId, workflowId, false).getWorkflow().getConnections();
        assertThat(connectionsUndo.size(), is(numConnectionsBefore));

        ws().redoWorkflowCommand(projectId, workflowId);
        var connectionsRedo = ws().getWorkflow(projectId, workflowId, false).getWorkflow().getConnections();
        assertThat(connectionsRedo.size(), is(numConnectionsAfter));
    }

    private static void assertConnection(final Collection<ConnectionEnt> connections, final NodeIDEnt sourceId,
        final NodeIDEnt destId) {
        var containsConnection = connections.stream().anyMatch(
            connection -> connection.getSourceNode().equals(sourceId) && connection.getDestNode().equals(destId));
        assertThat(containsConnection, is(true));
    }

    private static ConnectableEnt buildConnectableEnt(final NodeIDEnt nodeId) {
        return buildConnectableEnt(nodeId, null, null);
    }

    private static ConnectableEnt buildConnectableEnt(final NodeIDEnt nodeId, final Boolean isMetanodeInPortsBar,
        final Boolean isMetanodeOutPortsBar) {
        return builder(ConnectableEntBuilder.class)//
            .setNodeId(nodeId)//
            .setMetanodeInPortsBar(isMetanodeInPortsBar)//
            .setMetanodeOutPortsBar(isMetanodeOutPortsBar)//
            .build();
    }

    private static AutoConnectCommandEnt buildAutoConnectCommandEnt(final List<ConnectableEnt> connectables,
        final Boolean flowVariablesOnly) {
        return builder(AutoConnectCommandEntBuilder.class)//
            .setKind(KindEnum.AUTO_CONNECT)//
            .setConnectables(connectables)//
            .setFlowVariablesOnly(flowVariablesOnly)//
            .build();
    }

}

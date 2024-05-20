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
@SuppressWarnings("javadoc")
public class ConnectCommandsTestHelper extends WebUIGatewayServiceTestHelper {

    private static final String NODE_ID_DATA_GENERATOR = "root:1";

    private static final String NODE_ID_CONCATENATE = "root:27";

    private static final String CONNECTION_ID_DATA_GENERATOR_SRC = "root:10_1";

    private static final int NUMBER_OF_EXISTING_CONNECTIONS = 7;

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

    private static ConnectCommandEnt buildConnectCommandEnt(final NodeIDEnt source, final Integer sourcePort,
        final NodeIDEnt dest, final Integer destPort) {
        return builder(ConnectCommandEntBuilder.class).setKind(KindEnum.CONNECT).setSourceNodeId(source)
            .setSourcePortIdx(sourcePort).setDestinationNodeId(dest).setDestinationPortIdx(destPort).build();
    }

    private static void assertConnection(final Collection<ConnectionEnt> connections, final NodeIDEnt sourceId,
        final NodeIDEnt destId) {
        var containsConnection = connections.stream().anyMatch(
            connection -> connection.getSourceNode().equals(sourceId) && connection.getDestNode().equals(destId));
        assertThat("Connection <%s -> %s> doesn't exist".formatted(sourceId, destId), containsConnection, is(true));
    }

    private static AutoConnectCommandEnt buildAutoConnectCommandEnt(final List<NodeIDEnt> selectedNodes) {
        return buildAutoConnectCommandEnt(selectedNodes, false, false);
    }

    private static AutoConnectCommandEnt buildAutoConnectCommandEnt(final List<NodeIDEnt> selectedNodes,
        final boolean workflowInPortBarSelected, final boolean workflowOutPortBarSelected) {
        return builder(AutoConnectCommandEntBuilder.class)//
            .setKind(KindEnum.AUTO_CONNECT)//
            .setSelectedNodes(selectedNodes).setWorkflowInPortsBarSelected(workflowInPortBarSelected)
            .setWorkflowOutPortsBarSelected(workflowOutPortBarSelected).build();
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
        assertThat(connections.get(CONNECTION_ID_DATA_GENERATOR_SRC).getSourceNode().toString(),
            is(NODE_ID_DATA_GENERATOR));

        // replace existing connection
        var command = buildConnectCommandEnt(new NodeIDEnt(27), 1, new NodeIDEnt(10), 1);
        ws().executeWorkflowCommand(projectId, workflowId, command);
        connections = ws().getWorkflow(projectId, workflowId, false).getWorkflow().getConnections();
        assertThat(connections.size(), is(originalNumConnections));
        assertThat(connections.get(CONNECTION_ID_DATA_GENERATOR_SRC).getSourceNode().toString(),
            is(NODE_ID_CONCATENATE));

        // undo
        ws().undoWorkflowCommand(projectId, workflowId);
        connections = ws().getWorkflow(projectId, workflowId, false).getWorkflow().getConnections();
        assertThat(connections.size(), is(originalNumConnections));
        assertThat(connections.get(CONNECTION_ID_DATA_GENERATOR_SRC).getSourceNode().toString(),
            is(NODE_ID_DATA_GENERATOR));
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
        assertThat(connections.get(CONNECTION_ID_DATA_GENERATOR_SRC).getSourceNode().toString(),
            is(NODE_ID_DATA_GENERATOR));

        // new connection
        var command = buildConnectCommandEnt(new NodeIDEnt(27), 1, new NodeIDEnt(21), 2);
        ws().executeWorkflowCommand(projectId, workflowId, command);
        connections = ws().getWorkflow(projectId, workflowId, false).getWorkflow().getConnections();
        assertThat(connections.size(), is(originalNumConnections + 1));
        assertThat(connections.get("root:21_2").getSourceNode().toString(), is(NODE_ID_CONCATENATE));

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
        assertThat(connections.get(CONNECTION_ID_DATA_GENERATOR_SRC).getSourceNode().toString(),
            is(NODE_ID_DATA_GENERATOR));

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
        assertThat(connections.get(CONNECTION_ID_DATA_GENERATOR_SRC).getSourceNode().toString(),
            is(NODE_ID_DATA_GENERATOR));

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
        assertThat(connections.get(CONNECTION_ID_DATA_GENERATOR_SRC).getSourceNode().toString(),
            is(NODE_ID_DATA_GENERATOR));

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

    /**
     * Tests {@link AutoConnectCommandEnt} with native nodes and a component
     *
     * @throws Exception
     */
    public void testAutoConnectNativeNodesAndComponent() throws Exception {
        var projectId = loadWorkflow(TestWorkflowCollection.AUTO_CONNECT_NODES);
        var workflowId = getRootID();

        // "14" is a component
        var c10 = new NodeIDEnt(10);
        var c11 = new NodeIDEnt(11);
        var component = new NodeIDEnt(14);

        assertAutoConnect(projectId, workflowId, NUMBER_OF_EXISTING_CONNECTIONS, NUMBER_OF_EXISTING_CONNECTIONS + 2,
            buildAutoConnectCommandEnt(List.of(c10, c11, component)));
    }

    /**
     * Tests {@link AutoConnectCommandEnt} with native nodes and a metanode
     *
     * @throws Exception
     */
    public void testAutoConnectNativeNodesAndMetanode() throws Exception {
        var projectId = loadWorkflow(TestWorkflowCollection.AUTO_CONNECT_NODES);
        var workflowId = getRootID();

        var c5 = new NodeIDEnt(5);
        var c8 = new NodeIDEnt(8);
        var metanode = new NodeIDEnt(13);

        assertAutoConnect(projectId, workflowId, NUMBER_OF_EXISTING_CONNECTIONS, NUMBER_OF_EXISTING_CONNECTIONS + 1,
            buildAutoConnectCommandEnt(List.of(c5, c8, metanode)));
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
        var virtualNodeWithOutput = new NodeIDEnt(14, 0, 1);
        var virtualNodeWithInput = new NodeIDEnt(14, 0, 2);
        var c9 = new NodeIDEnt(14, 0, 9);
        var c12 = new NodeIDEnt(14, 0, 12);

        assertAutoConnect(projectId, workflowId, 0, 3,
            buildAutoConnectCommandEnt(List.of(virtualNodeWithOutput, virtualNodeWithInput, c9, c12)));
    }

    /**
     * Tests {@link AutoConnectCommandEnt} within a metanode
     *
     * @throws Exception
     */
    public void workflowInOutPortBarsAreConnected() throws Exception {
        var projectId = loadWorkflow(TestWorkflowCollection.AUTO_CONNECT_NODES);
        var parentWf = new NodeIDEnt(13); // A metanode with one table input and one table output port

        var childA = new NodeIDEnt(13, 6);
        var childB = new NodeIDEnt(13, 7);

        var command = buildAutoConnectCommandEnt(List.of(childA, childB), true, true);
        assertAutoConnect(projectId, parentWf, 0, 3, command);

        var connections = ws().getWorkflow(projectId, parentWf, false).getWorkflow().getConnections().values();
        assertConnection(connections, parentWf, childA);
        assertConnection(connections, childA, childB);
        assertConnection(connections, childB, parentWf);
    }

    /**
     * Tests {@link AutoConnectCommandEnt} with replacing an existing and executed connection
     *
     * @throws Exception
     */
    public void testAutoConnectWithReplacingExistingExecutedConnection() throws Exception {
        var projectId = loadWorkflow(TestWorkflowCollection.AUTO_CONNECT_NODES);
        var workflowId = getRootID();

        // both are executed and already connected
        var c42 = new NodeIDEnt(42);
        var c44 = new NodeIDEnt(44);

        // number of connections doesn't change, since one is replaced
        assertAutoConnect(projectId, workflowId, NUMBER_OF_EXISTING_CONNECTIONS, NUMBER_OF_EXISTING_CONNECTIONS,
            buildAutoConnectCommandEnt(List.of(c42, c44)));

        var connections = ws().getWorkflow(projectId, workflowId, false).getWorkflow().getConnections().values();
        assertConnection(connections, new NodeIDEnt(42), new NodeIDEnt(44));
    }

    public void autoConnectFlowVariablesOnly() throws Exception {
        var projectId = loadWorkflow(TestWorkflowCollection.AUTO_CONNECT_NODES);
        var workflowId = getRootID();

        var metanodeWithSingleFlowVarOut = new NodeIDEnt(23);
        var componentWithVisibleFlowVarIn = new NodeIDEnt(24);
        var nativeNode = new NodeIDEnt(20);

        var selectedNodes = List.of(metanodeWithSingleFlowVarOut, componentWithVisibleFlowVarIn, nativeNode);
        var command = builder(AutoConnectCommandEntBuilder.class)//
            .setKind(KindEnum.AUTO_CONNECT)//
            .setSelectedNodes(selectedNodes)//
            .setWorkflowInPortsBarSelected(false)//
            .setWorkflowOutPortsBarSelected(false)//
            .setFlowVariablePortsOnly(true) //
            .build();
        ws().executeWorkflowCommand(projectId, workflowId, command);
        var workflowAfter = ws().getWorkflow(projectId, workflowId, true).getWorkflow();
        var connectionsAfter = workflowAfter.getConnections().values();
        assertThat("Connection visible-to-hidden should be made even if visible destination is available", //
            connectionsAfter.stream().anyMatch(c -> //
            c.getSourceNode().equals(metanodeWithSingleFlowVarOut) //
                && c.getSourcePort() == 0 //
                && c.getDestNode().equals(componentWithVisibleFlowVarIn) //
                && c.getDestPort() == 0 //
            ) //
        );
        assertThat("Connection hidden-to-hidden should be made if no visible available", //
            connectionsAfter.stream().anyMatch(c -> //
            c.getSourceNode().equals(componentWithVisibleFlowVarIn) //
                && c.getSourcePort() == 0 //
                && c.getDestNode().equals(nativeNode) //
                && c.getDestPort() == 0 //
            ) //
        );
    }

    private void assertAutoConnect(final String projectId, final NodeIDEnt workflowId, final int numConnectionsBefore,
        final int numConnectionsAfter, final AutoConnectCommandEnt command) throws Exception { // NOSONAR
        var connectionsBefore = ws().getWorkflow(projectId, workflowId, false).getWorkflow().getConnections();
        assertThat("Unexpected number of connections before command execution", connectionsBefore.size(),
            is(numConnectionsBefore));

        ws().executeWorkflowCommand(projectId, workflowId, command);

        var workflowAfter = ws().getWorkflow(projectId, workflowId, true).getWorkflow();
        var connectionsAfter = workflowAfter.getConnections();
        assertThat("Unexpected number of connections after command execution", connectionsAfter.size(),
            is(numConnectionsAfter));

        if (Boolean.TRUE.equals(workflowAfter.getAllowedActions().isCanUndo())) { // Only undo if we can
            ws().undoWorkflowCommand(projectId, workflowId);
            var connectionsUndo = ws().getWorkflow(projectId, workflowId, false).getWorkflow().getConnections();
            assertThat("Unexpected number of connections after command undo", connectionsUndo.size(),
                is(numConnectionsBefore));

            ws().redoWorkflowCommand(projectId, workflowId);
            var connectionsRedo = ws().getWorkflow(projectId, workflowId, false).getWorkflow().getConnections();
            assertThat("Unexpected number of connections after command redo", connectionsRedo.size(),
                is(numConnectionsAfter));
        }
    }

}

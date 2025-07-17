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
 */

package org.knime.gateway.testing.helper.webui;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.knime.gateway.api.entity.EntityBuilderManager.builder;
import static org.knime.gateway.api.entity.NodeIDEnt.getRootID;

import java.util.List;
import java.util.Optional;

import org.knime.core.node.BufferedDataTable;
import org.knime.core.node.workflow.NodeContainer;
import org.knime.gateway.api.entity.NodeIDEnt;
import org.knime.gateway.api.service.GatewayException;
import org.knime.gateway.api.util.CoreUtil;
import org.knime.gateway.api.webui.entity.AddPortCommandEnt;
import org.knime.gateway.api.webui.entity.AddPortResultEnt;
import org.knime.gateway.api.webui.entity.ConnectCommandEnt;
import org.knime.gateway.api.webui.entity.NodePortEnt;
import org.knime.gateway.api.webui.entity.PortCommandEnt;
import org.knime.gateway.api.webui.entity.RemovePortCommandEnt;
import org.knime.gateway.api.webui.entity.WorkflowCommandEnt;
import org.knime.gateway.testing.helper.ResultChecker;
import org.knime.gateway.testing.helper.ServiceProvider;
import org.knime.gateway.testing.helper.TestWorkflowCollection;
import org.knime.gateway.testing.helper.WorkflowExecutor;
import org.knime.gateway.testing.helper.WorkflowLoader;
import org.knime.gateway.testing.helper.webui.node.NoOpDummyNodeFactory;
import org.knime.testing.util.WorkflowManagerUtil;

@SuppressWarnings({
        "MissingJavadoc", "javadoc", "java:S1176", // javadoc
        "java:S1192", // repeated string literals
        "java:S112" // generic exceptions
})
public class EditPortsTestHelper extends WebUIGatewayServiceTestHelper {
    public EditPortsTestHelper(final ResultChecker entityResultChecker, final ServiceProvider serviceProvider,
        final WorkflowLoader workflowLoader, final WorkflowExecutor workflowExecutor) {
        super(EditPortsTestHelper.class, entityResultChecker, serviceProvider, workflowLoader, workflowExecutor);
    }

    /**
     * This node features implicit flow variable ports.
     */
    static class NodeWithExtendablePortGroup extends NoOpDummyNodeFactory {

        static final String FIRST_INPUT_GROUP = "first input group";

        static final String OUTPUT_PORT_GROUP = "output group";

        @Override
        protected Optional<PortsConfigurationBuilder> createPortsConfigBuilder() {
            var builder = new PortsConfigurationBuilder();
            builder.addExtendableInputPortGroup(FIRST_INPUT_GROUP, BufferedDataTable.TYPE);
            builder.addExtendableOutputPortGroup(OUTPUT_PORT_GROUP, BufferedDataTable.TYPE);
            return Optional.of(builder);
        }
    }

    /**
     * Add ports to different port groups of native node, undo and redo.
     */
    public void testAddPortToNativeNode() throws Exception {
        final var wfId = loadWorkflow(TestWorkflowCollection.PORTS);
        var recursiveLoopEnd = new NodeIDEnt(190);

        var portType = BufferedDataTable.TYPE;

        var addToFirstGroupCommand = builder(AddPortCommandEnt.AddPortCommandEntBuilder.class) //
            .setNodeId(recursiveLoopEnd) //
            .setSide(PortCommandEnt.SideEnum.INPUT) //
            .setPortGroup("Collector") //
            .setPortTypeId(CoreUtil.getPortTypeId(portType)) //
            .setKind(WorkflowCommandEnt.KindEnum.ADD_PORT) //
            .build();

        var portResult = ws().executeWorkflowCommand(wfId, getRootID(), addToFirstGroupCommand);
        assertThat(portResult, instanceOf(AddPortResultEnt.class));
        assertThat(((AddPortResultEnt)portResult).getNewPortIdx(), is(3));

        var addToSecondGroupCommand = builder(AddPortCommandEnt.AddPortCommandEntBuilder.class) //
            .setNodeId(recursiveLoopEnd) //
            .setSide(PortCommandEnt.SideEnum.INPUT) //
            .setPortGroup("Recursion") //
            .setPortTypeId(CoreUtil.getPortTypeId(portType)) //
            .setKind(WorkflowCommandEnt.KindEnum.ADD_PORT) //
            .build();
        portResult = ws().executeWorkflowCommand(wfId, getRootID(), addToSecondGroupCommand);
        assertThat(portResult, instanceOf(AddPortResultEnt.class));
        assertThat(((AddPortResultEnt)portResult).getNewPortIdx(), is(6));
        var portsAfterExecute = getPortList(wfId, true, recursiveLoopEnd);

        ws().undoWorkflowCommand(wfId, getRootID());
        var portsAfterUndo = getPortList(wfId, true, recursiveLoopEnd);
        assertThat(portsAfterUndo.size(), is(portsAfterExecute.size() - 1));

        ws().redoWorkflowCommand(wfId, getRootID());
        var portsAfterRedo = getPortList(wfId, true, recursiveLoopEnd);
        assertThat(portsAfterRedo.size(), is(portsAfterExecute.size()));
    }

    /**
     * Remove ports from native node, undo and redo.
     */
    public void testRemovePortFromNativeNode() throws Exception {
        final var wfId = loadWorkflow(TestWorkflowCollection.PORTS);
        var recursiveLoopEnd = new NodeIDEnt(190);
        var removeFromFirstGroupCommand = builder(RemovePortCommandEnt.RemovePortCommandEntBuilder.class) //
            .setNodeId(recursiveLoopEnd) //
            .setSide(PortCommandEnt.SideEnum.INPUT) //
            .setKind(WorkflowCommandEnt.KindEnum.REMOVE_PORT) //
            .setPortIndex(2) //
            .build();

        ws().executeWorkflowCommand(wfId, getRootID(), removeFromFirstGroupCommand);
        var ports = getPortList(wfId, true, recursiveLoopEnd);
        assertThat(ports.size(), is(4));
        assertNull(ports.get(0).getPortGroupId());
        assertThat(ports.get(0).getName(), containsString("Variable Inport"));
        assertThat(ports.get(1).getPortGroupId(), is("Collector"));
        assertThat(ports.get(2).getPortGroupId(), is("Recursion"));
        assertThat(ports.get(3).getPortGroupId(), is("Recursion"));

        ws().undoWorkflowCommand(wfId, getRootID());
        ports = getPortList(wfId, true, recursiveLoopEnd);
        assertThat(ports.size(), is(5));
        assertNull(ports.get(0).getPortGroupId());
        assertThat(ports.get(0).getName(), containsString("Variable Inport"));
        assertThat(ports.get(1).getPortGroupId(), is("Collector"));
        assertThat(ports.get(2).getPortGroupId(), is("Collector"));
        assertThat(ports.get(3).getPortGroupId(), is("Recursion"));
        assertThat(ports.get(4).getPortGroupId(), is("Recursion"));

        ws().redoWorkflowCommand(wfId, getRootID());
        ports = getPortList(wfId, true, recursiveLoopEnd);
        assertThat(ports.size(), is(4));
        assertNull(ports.get(0).getPortGroupId());
        assertThat(ports.get(0).getName(), containsString("Variable Inport"));
        assertThat(ports.get(1).getPortGroupId(), is("Collector"));
        assertThat(ports.get(2).getPortGroupId(), is("Recursion"));
        assertThat(ports.get(3).getPortGroupId(), is("Recursion"));
    }

    public void testRemovePortFromSecondPortGroup() throws Exception {
        final var wfId = loadWorkflow(TestWorkflowCollection.PORTS);
        var recursiveLoopEnd = new NodeIDEnt(190);
        var removeFromSecondPortGroup = builder(RemovePortCommandEnt.RemovePortCommandEntBuilder.class) //
            .setNodeId(recursiveLoopEnd) //
            .setSide(PortCommandEnt.SideEnum.INPUT) //
            .setKind(WorkflowCommandEnt.KindEnum.REMOVE_PORT) //
            .setPortIndex(4).build();

        ws().executeWorkflowCommand(wfId, getRootID(), removeFromSecondPortGroup);
        var ports = getPortList(wfId, true, recursiveLoopEnd);
        assertThat(ports.size(), is(4));
        assertNull(ports.get(0).getPortGroupId());
        assertThat(ports.get(0).getName(), containsString("Variable Inport"));
        assertThat(ports.get(1).getPortGroupId(), is("Collector"));
        assertThat(ports.get(2).getPortGroupId(), is("Collector"));
        assertThat(ports.get(3).getPortGroupId(), is("Recursion"));
    }

    public void testCanRemovePortInPortGroups() throws Exception {
        final var wfId = loadWorkflow(TestWorkflowCollection.PORTS);
        var recursiveLoopEnd = new NodeIDEnt(190);
        var ports = getPortList(wfId, true, recursiveLoopEnd);
        assertFalse(ports.get(0).isCanRemove()); // flow variable port
        assertFalse(ports.get(1).isCanRemove()); // fixed port in 1st group
        assertTrue(ports.get(2).isCanRemove()); // dynamic port in 1st group
        assertFalse(ports.get(3).isCanRemove()); // fixed port in 2nd group
        assertTrue(ports.get(4).isCanRemove()); // dynamic port in 2nd group
    }

    /**
     * Test that a connection is removed with the port (and is not re-attached to the same index)
     *
     * @throws Exception
     */
    public void testConnectionIsRemovedWithPort() throws Exception {
        var pair = createEmptyWorkflow();
        var projectId = pair.getFirst();
        var wfm = pair.getSecond();

        var upstreamNode = WorkflowManagerUtil.createAndAddNode(wfm, new NodeWithExtendablePortGroup());
        addPort(projectId, upstreamNode, NodeWithExtendablePortGroup.OUTPUT_PORT_GROUP, PortCommandEnt.SideEnum.OUTPUT);

        var nodeUnderTest = WorkflowManagerUtil.createAndAddNode(wfm, new NodeWithExtendablePortGroup());
        addPort(projectId, nodeUnderTest, NodeWithExtendablePortGroup.FIRST_INPUT_GROUP, PortCommandEnt.SideEnum.INPUT); // total port index 1

        var totalIndexToRemove = 1;
        connect(upstreamNode, 1, nodeUnderTest, totalIndexToRemove, projectId);
        // provide another port that will be at the index after deletion that the connection could
        // erroneously be attached to
        addPort(projectId, nodeUnderTest, NodeWithExtendablePortGroup.FIRST_INPUT_GROUP, PortCommandEnt.SideEnum.INPUT); // will be at index 1 after removal
        removePort(projectId, nodeUnderTest, totalIndexToRemove); // removal

        var isConnected = !getPortList(projectId, true, new NodeIDEnt(nodeUnderTest.getID())).get(totalIndexToRemove)
            .getConnectedVia().isEmpty();
        assertThat("Port at index should not be connected", !isConnected);
    }

    /**
     * Test that a connection not connected to the removed port is not accidentally removed
     *
     * @throws Exception
     */
    public void testOtherConnectionRemains() throws Exception {
        var pair = createEmptyWorkflow();
        var projectId = pair.getFirst();
        var wfm = pair.getSecond();

        var upstreamNode = WorkflowManagerUtil.createAndAddNode(wfm, new NodeWithExtendablePortGroup());
        addPort(projectId, upstreamNode, NodeWithExtendablePortGroup.OUTPUT_PORT_GROUP, PortCommandEnt.SideEnum.OUTPUT);

        var nodeUnderTest = WorkflowManagerUtil.createAndAddNode(wfm, new NodeWithExtendablePortGroup());
        addPort(projectId, nodeUnderTest, NodeWithExtendablePortGroup.FIRST_INPUT_GROUP, PortCommandEnt.SideEnum.INPUT); // total port index 1

        addPort(projectId, nodeUnderTest, NodeWithExtendablePortGroup.FIRST_INPUT_GROUP, PortCommandEnt.SideEnum.INPUT); // total port index 2
        connect(upstreamNode, 1, nodeUnderTest, 2, projectId);

        removePort(projectId, nodeUnderTest, 1); // removal

        var isConnected =
            !getPortList(projectId, true, new NodeIDEnt(nodeUnderTest.getID())).get(1).getConnectedVia().isEmpty();
        assertThat("Port at index should be connected", isConnected);
    }

    private List<? extends NodePortEnt> getPortList(final String wfId, final boolean isInPort, final NodeIDEnt node)
        throws Exception {
        var wfEnt = ws().getWorkflow(wfId, getRootID(), null, true).getWorkflow();
        var nodeEnt = wfEnt.getNodes().get(node.toString());
        return isInPort ? nodeEnt.getInPorts() : nodeEnt.getOutPorts();
    }

    private void removePort(final String projectId, final NodeContainer node, final int indexToRemove)
        throws GatewayException {
        var command = builder(RemovePortCommandEnt.RemovePortCommandEntBuilder.class) //
            .setNodeId(new NodeIDEnt(node.getID())) //
            .setSide(PortCommandEnt.SideEnum.INPUT) //
            .setKind(WorkflowCommandEnt.KindEnum.REMOVE_PORT) //
            .setPortIndex(indexToRemove) //
            .build();
        ws().executeWorkflowCommand(projectId, getRootID(), command);
    }

    private void addPort(final String projectId, final NodeContainer node, final String portGroup,
        final PortCommandEnt.SideEnum side) throws GatewayException {
        var command = builder(AddPortCommandEnt.AddPortCommandEntBuilder.class) //
            .setNodeId(new NodeIDEnt(node.getID())) //
            .setSide(side) //
            .setPortGroup(portGroup) //
            .setPortTypeId(CoreUtil.getPortTypeId(BufferedDataTable.TYPE)) //
            .setKind(WorkflowCommandEnt.KindEnum.ADD_PORT) //
            .build();
        ws().executeWorkflowCommand(projectId, getRootID(), command);
    }

    private void connect(final NodeContainer source, final int sourcePort, final NodeContainer dest, final int destPort,
        final String projectId) throws GatewayException {
        var comamnd = builder(ConnectCommandEnt.ConnectCommandEntBuilder.class) //
            .setKind(WorkflowCommandEnt.KindEnum.CONNECT) //
            .setSourceNodeId(new NodeIDEnt(source.getID())) //
            .setSourcePortIdx(sourcePort) //
            .setDestinationNodeId(new NodeIDEnt(dest.getID())) //
            .setDestinationPortIdx(destPort).build();
        ws().executeWorkflowCommand(projectId, getRootID(), comamnd);
    }
}

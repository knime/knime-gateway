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
 */
package org.knime.gateway.testing.helper.webui;

import static org.knime.gateway.api.entity.EntityBuilderManager.builder;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;

import org.knime.gateway.api.entity.ConnectionIDEnt;
import org.knime.gateway.api.entity.NodeIDEnt;
import org.knime.gateway.api.webui.entity.AddBendpointCommandEnt;
import org.knime.gateway.api.webui.entity.DeleteCommandEnt;
import org.knime.gateway.api.webui.entity.DeleteCommandEnt.DeleteCommandEntBuilder;
import org.knime.gateway.api.webui.entity.WorkflowCommandEnt;
import org.knime.gateway.api.webui.entity.WorkflowEnt;
import org.knime.gateway.api.webui.entity.XYEnt;
import org.knime.gateway.testing.helper.ResultChecker;
import org.knime.gateway.testing.helper.ServiceProvider;
import org.knime.gateway.testing.helper.TestWorkflowCollection;
import org.knime.gateway.testing.helper.WorkflowExecutor;
import org.knime.gateway.testing.helper.WorkflowLoader;

/**
 * Tests bendpoint-related commands (e.g., the AddBendpoint-command and the Delete-command when being used to delete
 * bendpoints). Because updates to connections are processed asynchronously, the condition under test cannot be
 * evaluated right away, but we have to wait whether it eventually becomes true.
 *
 * @author Benjamin Moser, KNIME GmbH, Konstanz, Germany
 */
@SuppressWarnings({"javadoc", "restriction"})
public class BendpointsTestHelper extends WebUIGatewayServiceTestHelper {

    static final ConnectionIDEnt connectionWithTwoBendpoints = new ConnectionIDEnt(new NodeIDEnt(187), 1);

    static final ConnectionIDEnt connectionWithOneBendpoint = new ConnectionIDEnt(new NodeIDEnt(188), 1);

    static final ConnectionIDEnt connectionWithNoBendpointsEmptyUiInfo = new ConnectionIDEnt(new NodeIDEnt(190), 1);

    private static final ConnectionIDEnt connectionWithNoBendpointsNullUiInfo =
            new ConnectionIDEnt(new NodeIDEnt(191), 1);

    private static final XYEnt somePosition = builder(XYEnt.XYEntBuilder.class).setX(42).setY(17).build();

    public BendpointsTestHelper(final ResultChecker entityResultChecker, final ServiceProvider serviceProvider,
        final WorkflowLoader workflowLoader, final WorkflowExecutor workflowExecutor) {
        super(BendpointsTestHelper.class, entityResultChecker, serviceProvider, workflowLoader, workflowExecutor);
    }

    private static boolean bendpointPresentAt(final WorkflowEnt wfEnt, final String connection, final int index,
        final XYEnt position) {
        var bendpoints = wfEnt.getConnections().get(connection).getBendpoints();
        if (bendpoints == null) {
            return false;
        }
        var bendpointAccess = bendpoints.get(index);
        return (Objects.equals(position.getX(), bendpointAccess.getX()))
            && (Objects.equals(position.getY(), bendpointAccess.getY()));
    }

    public void testBendpointIsAddedOnConnectionWithEmptyUiInfo() throws Exception {
        var wfId = loadWorkflow(TestWorkflowCollection.BENDPOINTS);
        assertConnectionAdded(wfId, connectionWithNoBendpointsEmptyUiInfo);
    }

    public void testBendpointIsAddedOnConnectionWithNullUiInfo() throws Exception {
        var wfId = loadWorkflow(TestWorkflowCollection.BENDPOINTS);
        assertConnectionAdded(wfId, connectionWithNoBendpointsNullUiInfo);
    }

    private void assertConnectionAdded(final String wfId, final ConnectionIDEnt modifiedConnection) throws Exception {
        var insertionIndex = 0;
        var connection = modifiedConnection.toString();

        executeWorkflowCommand(addBendpoint(modifiedConnection, insertionIndex, somePosition), wfId);
        awaitBendpointPresentAt(wfId, connection, insertionIndex, somePosition);

        undoWorkflowCommand(wfId);
        awaitNoBendpointPresentAt(wfId, connection, insertionIndex, somePosition);

        redoWorkflowCommand(wfId);
        awaitNoBendpointPresentAt(wfId, connection, insertionIndex, somePosition);
    }

    public void testBendpointIsAddedBetweenExisting() throws Exception {
        var wfId = loadWorkflow(TestWorkflowCollection.BENDPOINTS);
        var insertionIndex = 1;
        executeWorkflowCommand(addBendpoint(connectionWithTwoBendpoints, insertionIndex, somePosition), wfId);
        awaitTrue(wfId, modifiedWorkflow -> bendpointPresentAt( //
            modifiedWorkflow, //
            connectionWithTwoBendpoints.toString(), //
            insertionIndex, //
            somePosition //
        ));
    }

    public void testBendpointIsRemovedWithRemaining() throws Exception {
        var wfId = loadWorkflow(TestWorkflowCollection.BENDPOINTS);
        var removalIndex = 0;
        var connection = connectionWithTwoBendpoints.toString();
        var removedBendpointPosition = ws().getWorkflow(wfId, NodeIDEnt.getRootID(), false).getWorkflow()
            .getConnections().get(connection).getBendpoints().get(removalIndex);
        executeWorkflowCommand( //
            deleteBendpoint(connectionWithTwoBendpoints, removalIndex), //
            wfId //
        );
        // a succeeding bendpoint will now be at that index, so all we can do is compare positions
        awaitNoBendpointPresentAt(wfId, connection, removalIndex, removedBendpointPosition);

        undoWorkflowCommand(wfId);
        awaitBendpointPresentAt(wfId, connection, removalIndex, removedBendpointPosition);

        redoWorkflowCommand(wfId);
        awaitNoBendpointPresentAt(wfId, connection, removalIndex, removedBendpointPosition);
    }

    public void testRemoveMultipleBendpoints() throws Exception {
        var wfId = loadWorkflow(TestWorkflowCollection.BENDPOINTS);
        executeWorkflowCommand(deleteBendpoint(connectionWithTwoBendpoints, 0, 1), wfId);
        awaitTrue(wfId, wf -> {
            var bendpoints = wf.getConnections().get(connectionWithTwoBendpoints.toString()).getBendpoints();
            return bendpoints == null;
        });
    }

    public void testBendpointIsRemovedWithNoneRemaining() throws Exception {
        var wfId = loadWorkflow(TestWorkflowCollection.BENDPOINTS);
        var removalIndex = 0;
        executeWorkflowCommand(deleteBendpoint(connectionWithOneBendpoint, removalIndex), wfId);
        awaitTrue(wfId, wf -> {
            var bendpoints = wf.getConnections().get(connectionWithOneBendpoint.toString()).getBendpoints();
            return bendpoints == null;
        });
    }

    private static AddBendpointCommandEnt addBendpoint(final ConnectionIDEnt connection,
        final int index, final XYEnt position) {
        return builder(AddBendpointCommandEnt.AddBendpointCommandEntBuilder.class)
            .setKind(WorkflowCommandEnt.KindEnum.ADD_BENDPOINT).setConnectionId(connection)
            .setIndex(BigDecimal.valueOf(index)).setPosition(position).build();
    }

    private static DeleteCommandEnt deleteBendpoint(final ConnectionIDEnt connectionIDEnt,
        final int... indices) {
        return builder(DeleteCommandEntBuilder.class).setKind(WorkflowCommandEnt.KindEnum.DELETE)
            .setConnectionBendpoints(
                Map.of(connectionIDEnt.toString(), Arrays.stream(indices).boxed().toList()))
            .build();
    }

    private void awaitBendpointPresentAt(String wfId, String connection, int insertionIndex, XYEnt position) {
        awaitTrue(wfId, wf -> bendpointPresentAt(wf, connection, insertionIndex, position));
    }

    private void awaitNoBendpointPresentAt(String wfId, String connection, int insertionIndex, XYEnt position) {
        awaitFalse(wfId, wf -> bendpointPresentAt(wf, connection, insertionIndex, position));
    }


}

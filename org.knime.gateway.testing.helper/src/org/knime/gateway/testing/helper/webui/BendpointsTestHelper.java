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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
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
 * Tests bendpoint-related commands (e.g. the AddBendpoint-command and the Delete-command when being used to delete
 * bendpoints).
 *
 * @author Benjamin Moser, KNIME GmbH, Konstanz, Germany
 */
@SuppressWarnings({"javadoc", "restriction"})
public class BendpointsTestHelper extends WebUIGatewayServiceTestHelper {

    static final ConnectionIDEnt connectionWithTwoBendpoints = new ConnectionIDEnt(new NodeIDEnt(187), 1);

    static final ConnectionIDEnt connectionWithOneBendpoint = new ConnectionIDEnt(new NodeIDEnt(188), 1);

    static final ConnectionIDEnt connectionWithNoBendpointsEmptyUiInfo = new ConnectionIDEnt(new NodeIDEnt(190), 1);

    private static final ConnectionIDEnt connectionWithNoBendpointsNullUiInfo = new ConnectionIDEnt(new NodeIDEnt(191), 1);

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
        var modifiedWf = executeWorkflowCommand(
            createAddBendpointCommandEnt(modifiedConnection, insertionIndex, somePosition), wfId);
        assertTrue(bendpointPresentAt(modifiedWf, modifiedConnection.toString(), insertionIndex, somePosition));
        var undoneWorkflow = undoWorkflowCommand(wfId);
        assertFalse(bendpointPresentAt(undoneWorkflow, modifiedConnection.toString(), insertionIndex, somePosition));
        var redoneWorkflow = redoWorkflowCommand(wfId);
        assertTrue(bendpointPresentAt(redoneWorkflow, modifiedConnection.toString(), insertionIndex, somePosition));
    }

    public void testBendpointIsAddedBetweenExisting() throws Exception {
        var wfId = loadWorkflow(TestWorkflowCollection.BENDPOINTS);
        var insertionIndex = 1;
        var modifiedWf = executeWorkflowCommand(
            createAddBendpointCommandEnt(connectionWithTwoBendpoints, insertionIndex, somePosition), wfId);
        assertTrue(bendpointPresentAt(modifiedWf, connectionWithTwoBendpoints.toString(), insertionIndex, somePosition));
    }

    public void testBendpointIsRemovedWithRemaining() throws Exception {
        var wfId = loadWorkflow(TestWorkflowCollection.BENDPOINTS);
        var removalIndex = 0;
        var removedBendpointPosition = ws().getWorkflow(wfId, NodeIDEnt.getRootID(), false).getWorkflow()
            .getConnections().get(connectionWithTwoBendpoints.toString()).getBendpoints().get(removalIndex);
        var modifiedWf = executeWorkflowCommand(createDeleteCommandToRemoveBendpoints(connectionWithTwoBendpoints, removalIndex), wfId);
        // a succeeding bendpoint will now be at that index, so all we can do is compare positions
        assertFalse(bendpointPresentAt(modifiedWf, connectionWithTwoBendpoints.toString(), removalIndex, removedBendpointPosition));
        var undoneWorkflow = undoWorkflowCommand(wfId);
        assertTrue(bendpointPresentAt(undoneWorkflow, connectionWithTwoBendpoints.toString(), removalIndex,
            removedBendpointPosition));
        var redoneWorkflow = redoWorkflowCommand(wfId);
        assertFalse(bendpointPresentAt(redoneWorkflow, connectionWithTwoBendpoints.toString(), removalIndex,
            removedBendpointPosition));
    }
    }

    public void testBendpointIsRemovedWithNoneRemaining() throws Exception {
        var wfId = loadWorkflow(TestWorkflowCollection.BENDPOINTS);
        var removalIndex = 0;
        var modifiedWf = executeWorkflowCommand(createDeleteCommandToRemoveBendpoints(connectionWithOneBendpoint, removalIndex), wfId);
        assertThat(modifiedWf.getConnections().get(connectionWithOneBendpoint.toString()).getBendpoints(), nullValue());
    }

    private static AddBendpointCommandEnt createAddBendpointCommandEnt(final ConnectionIDEnt connection,
        final int index, final XYEnt position) {
        return builder(AddBendpointCommandEnt.AddBendpointCommandEntBuilder.class)
            .setKind(WorkflowCommandEnt.KindEnum.ADD_BENDPOINT).setConnectionId(connection)
            .setIndex(BigDecimal.valueOf(index)).setPosition(position).build();
    }

    private static DeleteCommandEnt createDeleteCommandToRemoveBendpoints(final ConnectionIDEnt connectionIDEnt,
        final int... indices) {
        return builder(DeleteCommandEntBuilder.class).setKind(WorkflowCommandEnt.KindEnum.DELETE)
            .setConnectionBendpoints(
                Map.of(connectionIDEnt.toString(), Arrays.stream(indices).mapToObj(Integer::valueOf).toList()))
            .build();
    }

}

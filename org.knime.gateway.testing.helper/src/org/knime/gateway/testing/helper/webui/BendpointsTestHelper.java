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
import java.util.Objects;

import org.knime.gateway.api.entity.ConnectionIDEnt;
import org.knime.gateway.api.entity.NodeIDEnt;
import org.knime.gateway.api.webui.entity.AddBendpointCommandEnt;
import org.knime.gateway.api.webui.entity.RemoveBendpointCommandEnt;
import org.knime.gateway.api.webui.entity.WorkflowCommandEnt;
import org.knime.gateway.api.webui.entity.WorkflowEnt;
import org.knime.gateway.api.webui.entity.XYEnt;
import org.knime.gateway.testing.helper.ResultChecker;
import org.knime.gateway.testing.helper.ServiceProvider;
import org.knime.gateway.testing.helper.TestWorkflowCollection;
import org.knime.gateway.testing.helper.WorkflowExecutor;
import org.knime.gateway.testing.helper.WorkflowLoader;

@SuppressWarnings({"javadoc", "restriction"})
public class BendpointsTestHelper extends WebUIGatewayServiceTestHelper {

    public BendpointsTestHelper(final ResultChecker entityResultChecker, final ServiceProvider serviceProvider,
        final WorkflowLoader workflowLoader, final WorkflowExecutor workflowExecutor) {
        super(BendpointsTestHelper.class, entityResultChecker, serviceProvider, workflowLoader, workflowExecutor);
    }

    public static boolean bendpointPresentAt(final WorkflowEnt wfEnt, final String connection, final int index,
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
        var wf = loadBendpointsWorkflow();
        var modifiedConnection = TestWorkflowCollection.BendpointsWorkflow.noBendpointsEmptyUiInfo;
        assertConnectionAdded(wf, modifiedConnection);
    }

    public void testBendpointIsAddedOnConnectionWithNullUiInfo() throws Exception {
        var wf = loadBendpointsWorkflow();
        var modifiedConnection = TestWorkflowCollection.BendpointsWorkflow.noBendpointsNullUiInfo;
        assertConnectionAdded(wf, modifiedConnection);
    }

    private void assertConnectionAdded(TestWorkflowCollection.BendpointsWorkflow wf, ConnectionIDEnt modifiedConnection)
        throws Exception {
        var insertionIndex = 0;
        var insertionPosition = TestWorkflowCollection.BendpointsWorkflow.somePosition;
        var modifiedWf = executeWorkflowCommand(
            addBendpointCommandEnt(modifiedConnection, insertionIndex, insertionPosition), wf.id());
        assert bendpointPresentAt(modifiedWf, modifiedConnection.toString(), insertionIndex, insertionPosition);
        var undoneWorkflow = undoWorkflowCommand(wf.id());
        assert !bendpointPresentAt(undoneWorkflow, modifiedConnection.toString(), insertionIndex, insertionPosition);
        var redoneWorkflow = redoWorkflowCommand(wf.id());
        assert bendpointPresentAt(redoneWorkflow, modifiedConnection.toString(), insertionIndex, insertionPosition);
    }

    public void testBendpointIsAddedBetweenExisting() throws Exception {
        var wf = loadBendpointsWorkflow();
        var modifiedConnection = TestWorkflowCollection.BendpointsWorkflow.twoBendpoints;
        var insertionIndex = 1;
        var insertionPosition = TestWorkflowCollection.BendpointsWorkflow.somePosition;
        var modifiedWf = executeWorkflowCommand(
            addBendpointCommandEnt(modifiedConnection, insertionIndex, insertionPosition), wf.id());
        assert bendpointPresentAt(modifiedWf, modifiedConnection.toString(), insertionIndex, insertionPosition);
    }

    public void testBendpointIsRemovedWithRemaining() throws Exception {
        var wf = loadBendpointsWorkflow();
        var modifiedConnection = TestWorkflowCollection.BendpointsWorkflow.twoBendpoints;
        var removalIndex = 0;
        var removedBendpointPosition =
            wf.originalEnt().getConnections().get(modifiedConnection.toString()).getBendpoints().get(removalIndex);
        var modifiedWf = executeWorkflowCommand(removeBendpointCommand(modifiedConnection, removalIndex), wf.id());
        // a succeeding bendpoint will now be at that index, so all we can do is compare positions
        assert !bendpointPresentAt(modifiedWf, modifiedConnection.toString(), removalIndex, removedBendpointPosition);
        var undoneWorkflow = undoWorkflowCommand(wf.id());
        assert bendpointPresentAt(undoneWorkflow, modifiedConnection.toString(), removalIndex,
            removedBendpointPosition);
        var redoneWorkflow = redoWorkflowCommand(wf.id());
        assert !bendpointPresentAt(redoneWorkflow, modifiedConnection.toString(), removalIndex,
            removedBendpointPosition);
    }

    public void testBendpointIsRemovedWithNoneRemaining() throws Exception {
        var wf = loadBendpointsWorkflow();
        var modifiedConnection = TestWorkflowCollection.BendpointsWorkflow.oneBendpoint;
        var removalIndex = 0;
        var modifiedWf = executeWorkflowCommand(removeBendpointCommand(modifiedConnection, removalIndex), wf.id());
        assert modifiedWf.getConnections().get(modifiedConnection.toString()).getBendpoints() == null;
    }

    private static AddBendpointCommandEnt addBendpointCommandEnt(final ConnectionIDEnt connection, final int index,
        final XYEnt position) {
        return builder(AddBendpointCommandEnt.AddBendpointCommandEntBuilder.class)
            .setKind(WorkflowCommandEnt.KindEnum.ADD_BENDPOINT).setConnectionId(connection)
            .setIndex(BigDecimal.valueOf(index)).setPosition(position).build();
    }

    private static RemoveBendpointCommandEnt removeBendpointCommand(final ConnectionIDEnt connectionIDEnt,
        final int index) {
        return builder(RemoveBendpointCommandEnt.RemoveBendpointCommandEntBuilder.class)
            .setKind(WorkflowCommandEnt.KindEnum.REMOVE_BENDPOINT).setConnectionId(connectionIDEnt)
            .setIndex(BigDecimal.valueOf(index)).build();
    }

    private TestWorkflowCollection.BendpointsWorkflow loadBendpointsWorkflow() throws Exception {
        var wfId = loadWorkflow(TestWorkflowCollection.BENDPOINTS);
        WorkflowEnt originalWorkflow = ws().getWorkflow(wfId, NodeIDEnt.getRootID(), true).getWorkflow();
        return new TestWorkflowCollection.BendpointsWorkflow(wfId, originalWorkflow);
    }

}

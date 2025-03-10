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

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.knime.gateway.api.entity.EntityBuilderManager.builder;
import static org.knime.gateway.api.entity.NodeIDEnt.getRootID;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.awaitility.Awaitility;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.knime.gateway.api.entity.AnnotationIDEnt;
import org.knime.gateway.api.entity.ConnectionIDEnt;
import org.knime.gateway.api.entity.NodeIDEnt;
import org.knime.gateway.api.webui.entity.DeleteCommandEnt;
import org.knime.gateway.api.webui.entity.NativeNodeEnt;
import org.knime.gateway.api.webui.entity.NodeStateEnt;
import org.knime.gateway.api.webui.entity.WorkflowCommandEnt;
import org.knime.gateway.api.webui.entity.WorkflowEnt;
import org.knime.gateway.api.webui.service.WorkflowService;
import org.knime.gateway.api.webui.service.util.ServiceExceptions;
import org.knime.gateway.testing.helper.ResultChecker;
import org.knime.gateway.testing.helper.ServiceProvider;
import org.knime.gateway.testing.helper.TestWorkflowCollection;
import org.knime.gateway.testing.helper.WorkflowExecutor;
import org.knime.gateway.testing.helper.WorkflowLoader;

@SuppressWarnings("javadoc")
public class DeleteCommandTestHelper extends WebUIGatewayServiceTestHelper {

    public DeleteCommandTestHelper(final ResultChecker entityResultChecker, final ServiceProvider serviceProvider,
        final WorkflowLoader workflowLoader, final WorkflowExecutor workflowExecutor) {
        super(DeleteCommandTestHelper.class, entityResultChecker, serviceProvider, workflowLoader, workflowExecutor);
    }

    /**
     * Tests
     * {@link WorkflowService#executeWorkflowCommand(String, NodeIDEnt, org.knime.gateway.api.webui.entity.WorkflowCommandEnt)}
     * when called with {@link DeleteCommandEnt}.
     */
    public void testExecuteDeleteCommand() throws Exception {
        final String wfId = loadWorkflow(TestWorkflowCollection.GENERAL_WEB_UI);
        var command = createDeleteCommandEnt(asList(new NodeIDEnt(1), new NodeIDEnt(4)),
            asList(new ConnectionIDEnt(new NodeIDEnt(26), 1)), asList(new AnnotationIDEnt(getRootID(), 1)));
        ws().executeWorkflowCommand(wfId, getRootID(), command);
        WorkflowEnt workflow = ws().getWorkflow(wfId, getRootID(), Boolean.TRUE, null).getWorkflow();
        cr(workflow, "delete_command");
        assertThat(workflow.getNodes().keySet(), not(hasItems("root:1", "root:4")));
        assertThat(workflow.getWorkflowAnnotations().stream().map(a -> a.getId().toString()).toList(),
            not(hasItems("root_1")));
        assertThat(workflow.getWorkflowAnnotations().size(), is(6));
        assertThat(workflow.getConnections().keySet(), not(hasItems("root:26_1")));
    }

    public void canUndoDelete() throws Exception {
        final String wfId = loadWorkflow(TestWorkflowCollection.GENERAL_WEB_UI);
        var command = createDeleteCommandEnt(asList(new NodeIDEnt(1), new NodeIDEnt(4)),
            asList(new ConnectionIDEnt(new NodeIDEnt(26), 1)), asList(new AnnotationIDEnt(getRootID(), 1)));
        ws().executeWorkflowCommand(wfId, getRootID(), command);
        WorkflowEnt workflow = ws().getWorkflow(wfId, getRootID(), Boolean.TRUE, null).getWorkflow();
        assertThat(workflow.getNodes().keySet(), Matchers.not(hasItems("root:1", "root:4")));
        ws().undoWorkflowCommand(wfId, getRootID());
        workflow = ws().getWorkflow(wfId, getRootID(), Boolean.TRUE, null).getWorkflow();
        assertThat(workflow.getNodes().keySet(), hasItems("root:1", "root:4"));
        assertThat(workflow.getWorkflowAnnotations().stream().map(a -> a.getId().toString()).toList(),
            hasItems("root_1"));
        assertThat(workflow.getWorkflowAnnotations().size(), is(7));
        assertThat(workflow.getConnections().keySet(), hasItems("root:26_1"));
    }

    public void canDeleteWithinComponent() throws Exception {
        final String wfId = loadWorkflow(TestWorkflowCollection.GENERAL_WEB_UI);
        assertThat("node expected to be present",
            ws().getWorkflow(wfId, new NodeIDEnt(12), true, null).getWorkflow().getNodes().get("root:12:0:10"),
            is(notNullValue()));
        ws().executeWorkflowCommand(wfId, new NodeIDEnt(12),
            createDeleteCommandEnt(asList(new NodeIDEnt(12, 0, 10)), emptyList(), emptyList()));
        assertThat("node expected to be deleted",
            ws().getWorkflow(wfId, new NodeIDEnt(12), true, null).getWorkflow().getNodes().get("root:12:0:10"),
            is(nullValue()));
    }

    public void canDeleteConnectionLeavingMetanode() throws Exception {
        final String wfId = loadWorkflow(TestWorkflowCollection.GENERAL_WEB_UI);
        assertThat(ws().getWorkflow(wfId, new NodeIDEnt(6), false, null).getWorkflow().getConnections().get("root:6_1"),
            is(notNullValue()));
        ws().executeWorkflowCommand(wfId, new NodeIDEnt(6),
            createDeleteCommandEnt(emptyList(), asList(new ConnectionIDEnt("root:6_1")), emptyList()));
        assertThat(ws().getWorkflow(wfId, new NodeIDEnt(6), false, null).getWorkflow().getConnections().get("root:6_1"),
            is(nullValue()));
    }

    public void canDeleteConnectionInMetanode() throws Exception {
        final String wfId = loadWorkflow(TestWorkflowCollection.GENERAL_WEB_UI);
        assertThat("node expected to be present",
            ws().getWorkflow(wfId, new NodeIDEnt(6), true, null).getWorkflow().getNodes().get("root:6:3"),
            is(notNullValue()));
        ws().executeWorkflowCommand(wfId, new NodeIDEnt(6),
            createDeleteCommandEnt(asList(new NodeIDEnt(6, 3)), emptyList(), emptyList()));
        assertThat("node expected to be deleted",
            ws().getWorkflow(wfId, new NodeIDEnt(6), true, null).getWorkflow().getNodes().get("root:6:3"), is(nullValue()));
    }

    public void deletionFailsIfNodeDoesNotExist() throws Exception {
        final String wfId = loadWorkflow(TestWorkflowCollection.GENERAL_WEB_UI);
        var command2 = createDeleteCommandEnt(asList(new NodeIDEnt(99999999)), emptyList(), emptyList());
        var ex = Assert.assertThrows(ServiceExceptions.ServiceCallException.class,
            () -> ws().executeWorkflowCommand(wfId, getRootID(), command2));
        assertThat(ex.getMessage(), is("Some nodes can't be deleted or don't exist. Delete operation aborted."));
    }

    public void deletionFailsIfConnectionDoesNotExist() throws Exception {
        final String wfId = loadWorkflow(TestWorkflowCollection.GENERAL_WEB_UI);
        var command3 =
            createDeleteCommandEnt(emptyList(), asList(new ConnectionIDEnt(new NodeIDEnt(99999999), 0)), emptyList());
        var ex = Assert.assertThrows(ServiceExceptions.ServiceCallException.class,
            () -> ws().executeWorkflowCommand(wfId, getRootID(), command3));
        assertThat(ex.getMessage(), is("Some connections don't exist. Delete operation aborted."));
    }

    public void deletionFailsIfAnnotationDoesNotExist() throws Exception {
        final String wfId = loadWorkflow(TestWorkflowCollection.GENERAL_WEB_UI);
        var command4 =
            createDeleteCommandEnt(emptyList(), emptyList(), asList(new AnnotationIDEnt(getRootID(), 999999999)));
        var ex = Assert.assertThrows(ServiceExceptions.ServiceCallException.class,
            () -> ws().executeWorkflowCommand(wfId, getRootID(), command4));
        assertThat(ex.getMessage(), is("Some workflow annotations don't exist. Delete operation aborted."));
    }

    public void deletionFailsIfNodeUnderDeleteLock() throws Exception {
        final String wfId = loadWorkflow(TestWorkflowCollection.GENERAL_WEB_UI);
        var command5 = createDeleteCommandEnt(asList(new NodeIDEnt(23, 0, 8)), emptyList(), emptyList());
        var ex = Assert.assertThrows(ServiceExceptions.ServiceCallException.class,
            () -> ws().executeWorkflowCommand(wfId, new NodeIDEnt(23), command5));
        assertThat(ex.getMessage(), is("Some nodes can't be deleted or don't exist. Delete operation aborted."));
    }

    public void deleteInExecutingWorkflow() throws Exception {
        String wfId2 = loadWorkflow(TestWorkflowCollection.EXECUTION_STATES);
        executeWorkflowAsync(wfId2);
        Awaitility.await().atMost(5, TimeUnit.SECONDS).pollInterval(100, TimeUnit.MILLISECONDS).untilAsserted(() -> {
            WorkflowEnt w = ws().getWorkflow(wfId2, NodeIDEnt.getRootID(), Boolean.TRUE, null).getWorkflow();
            assertThat(((NativeNodeEnt)w.getNodes().get("root:4")).getState().getExecutionState(),
                is(NodeStateEnt.ExecutionStateEnum.EXECUTED));
        });
        cr(ws().getWorkflow(wfId2, getRootID(), Boolean.TRUE, null).getWorkflow(), "can_delete_executing");

        // deletion fails because of a node that cannot be deleted due to executing successors
        var command6 = createDeleteCommandEnt(asList(new NodeIDEnt(3)), emptyList(), emptyList());
        var ex = Assert.assertThrows(ServiceExceptions.ServiceCallException.class,
            () -> ws().executeWorkflowCommand(wfId2, getRootID(), command6));
        assertThat(ex.getMessage(), is("Some nodes can't be deleted or don't exist. Delete operation aborted."));

        // deletion of a connection fails because it's connected to an executing node
        var command7 =
            createDeleteCommandEnt(emptyList(), asList(new ConnectionIDEnt(new NodeIDEnt(7), 0)), emptyList());
        ex = Assert.assertThrows(ServiceExceptions.ServiceCallException.class,
            () -> ws().executeWorkflowCommand(wfId2, getRootID(), command7));
        assertThat(ex.getMessage(), is("Some connections can't be deleted. Delete operation aborted."));
    }

    static DeleteCommandEnt createDeleteCommandEnt(final List<NodeIDEnt> nodeIds,
        final List<ConnectionIDEnt> connectionIds, final List<AnnotationIDEnt> annotationIds) {
        return builder(DeleteCommandEnt.DeleteCommandEntBuilder.class).setKind(WorkflowCommandEnt.KindEnum.DELETE)//
            .setNodeIds(nodeIds)//
            .setConnectionIds(connectionIds)//
            .setAnnotationIds(annotationIds)//
            .build();

    }
}

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
 *   Aug 3, 2020 (hornm): created
 */
package org.knime.gateway.testing.helper.webui;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThrows;
import static org.knime.gateway.api.entity.EntityBuilderManager.builder;
import static org.knime.gateway.api.entity.NodeIDEnt.getRootID;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.awaitility.Awaitility;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.knime.core.util.Pair;
import org.knime.gateway.api.entity.AnnotationIDEnt;
import org.knime.gateway.api.entity.ConnectionIDEnt;
import org.knime.gateway.api.entity.NodeIDEnt;
import org.knime.gateway.api.webui.entity.ComponentNodeEnt;
import org.knime.gateway.api.webui.entity.DeleteCommandEnt;
import org.knime.gateway.api.webui.entity.DeleteCommandEnt.DeleteCommandEntBuilder;
import org.knime.gateway.api.webui.entity.NativeNodeEnt;
import org.knime.gateway.api.webui.entity.NodeEnt;
import org.knime.gateway.api.webui.entity.NodeStateEnt.ExecutionStateEnum;
import org.knime.gateway.api.webui.entity.TranslateCommandEnt;
import org.knime.gateway.api.webui.entity.TranslateCommandEnt.TranslateCommandEntBuilder;
import org.knime.gateway.api.webui.entity.WorkflowAnnotationEnt;
import org.knime.gateway.api.webui.entity.WorkflowCommandEnt.KindEnum;
import org.knime.gateway.api.webui.entity.WorkflowEnt;
import org.knime.gateway.api.webui.entity.XYEnt;
import org.knime.gateway.api.webui.entity.XYEnt.XYEntBuilder;
import org.knime.gateway.api.webui.service.WorkflowService;
import org.knime.gateway.api.webui.service.util.ServiceExceptions.NodeNotFoundException;
import org.knime.gateway.api.webui.service.util.ServiceExceptions.OperationNotAllowedException;
import org.knime.gateway.testing.helper.ResultChecker;
import org.knime.gateway.testing.helper.TestWorkflowCollection;
import org.knime.gateway.testing.helper.WorkflowExecutor;
import org.knime.gateway.testing.helper.WorkflowLoader;

/**
 * Test for the endpoints to view/render a workflow.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
public class WorkflowServiceTestHelper extends WebUIGatewayServiceTestHelper {

    /**
     * @param entityResultChecker
     * @param serviceProvider
     * @param workflowLoader
     * @param workflowExecutor
     */
    protected WorkflowServiceTestHelper(final ResultChecker entityResultChecker, final ServiceProvider serviceProvider,
        final WorkflowLoader workflowLoader, final WorkflowExecutor workflowExecutor) {
        super(WorkflowServiceTestHelper.class, entityResultChecker, serviceProvider, workflowLoader, workflowExecutor);
    }

    /**
     * Tests to get the workflow.
     *
     * @throws Exception
     */
    public void testGetWorkflow() throws Exception {
        String wfId = loadWorkflow(TestWorkflowCollection.GENERAL_WEB_UI);

        // check un-executed
        WorkflowEnt workflow = ws().getWorkflow(wfId, NodeIDEnt.getRootID(), Boolean.TRUE).getWorkflow();
        cr(workflow, "workflowent_root");

        // get a metanode's workflow
        workflow = ws().getWorkflow(wfId, new NodeIDEnt(6), Boolean.FALSE).getWorkflow();
        cr(workflow, "workflowent_6");

        // get a component's workflow
        workflow = ws().getWorkflow(wfId, new NodeIDEnt(23), Boolean.FALSE).getWorkflow();
        cr(workflow, "workflowent_23");

        // check executed
        executeWorkflow(wfId);
        workflow = ws().getWorkflow(wfId, NodeIDEnt.getRootID(), Boolean.TRUE).getWorkflow();
        cr(workflow, "workflowent_root_executed");

        // get a workflow of a linked component
        workflow = ws().getWorkflow(wfId, new NodeIDEnt(183), Boolean.FALSE).getWorkflow();
        cr(workflow, "workflowent_183_linked_component");
    }

    /**
     * Tests to get a workflow of a component workflow.
     *
     * @throws Exception
     */
    public void testGetComponentProjectWorkflow() throws Exception {
        String wfId = loadComponent(TestWorkflowCollection.COMPONENT_PROJECT);

        WorkflowEnt workflow = ws().getWorkflow(wfId, NodeIDEnt.getRootID(), Boolean.TRUE).getWorkflow();
        cr(workflow, "component_project");

        workflow = ws().getWorkflow(wfId, new NodeIDEnt(5), Boolean.TRUE).getWorkflow();
        cr(workflow, "component_in_component_project_l1");

        workflow = ws().getWorkflow(wfId, new NodeIDEnt(5,0,7), Boolean.TRUE).getWorkflow();
        cr(workflow, "component_in_component_project_l2");
    }

    /**
     * Tests the correct mapping of the node execution states.
     *
     * @throws Exception
     */
    public void testNodeExecutionStates() throws Exception {
        String wfId = loadWorkflow(TestWorkflowCollection.EXECUTION_STATES);

        WorkflowEnt workflow = ws().getWorkflow(wfId, NodeIDEnt.getRootID(), Boolean.FALSE).getWorkflow();
        cr(getNodeStates(workflow), "node_states");

        executeWorkflowAsync(wfId);
        Awaitility.await().atMost(5, TimeUnit.SECONDS).pollInterval(100, TimeUnit.MILLISECONDS).untilAsserted(() -> {
            WorkflowEnt w = ws().getWorkflow(wfId, NodeIDEnt.getRootID(), Boolean.FALSE).getWorkflow();
            assertThat(((NativeNodeEnt)w.getNodes().get("root:4")).getState().getExecutionState(),
                is(ExecutionStateEnum.EXECUTED));
        });
        workflow = ws().getWorkflow(wfId, NodeIDEnt.getRootID(), Boolean.FALSE).getWorkflow();
        cr(getNodeStates(workflow), "node_states_execution");
    }

    private static Map<String, ExecutionStateEnum> getNodeStates(final WorkflowEnt w) {
        return w.getNodes().entrySet().stream().map(e -> { // NOSONAR
            ExecutionStateEnum state = null;
            NodeEnt n = e.getValue();
            if (n instanceof NativeNodeEnt) {
                state = ((NativeNodeEnt)n).getState().getExecutionState();
            } else if (n instanceof ComponentNodeEnt) {
                state = ((ComponentNodeEnt)n).getState().getExecutionState();
            } else {
                //
            }
            return Pair.create(e.getKey(), state);
        }).collect(Collectors.toMap(Pair::getFirst, Pair::getSecond));
    }

    /**
     * Tests the {@link WorkflowEnt#getAllowedActions()} property in partcular.
     *
     * @throws Exception
     */
    public void testGetAllowedActionsInfo() throws Exception {
        String wfId = loadWorkflow(TestWorkflowCollection.GENERAL_WEB_UI);

        WorkflowEnt workflow = ws().getWorkflow(wfId, NodeIDEnt.getRootID(), Boolean.TRUE).getWorkflow();

        // check the allowed actions on the workflow itself
        cr(workflow.getAllowedActions(), "allowedactions_root");

        // check for component, node and metanode
        cr(workflow.getAllowedActions(), "allowedactions_8");
        cr(workflow.getAllowedActions(), "allowedactions_12");
        cr(workflow.getAllowedActions(), "allowedactions_6");
    }

    /**
     * Tests the metadata of the project workflow and components.
     *
     * @throws Exception
     */
    public void testWorkflowAndComponentMetadata() throws Exception {
        String wfId = loadWorkflow(TestWorkflowCollection.METADATA);

        // checks the metadata of the project workflow
        WorkflowEnt workflow = ws().getWorkflow(wfId, NodeIDEnt.getRootID(), Boolean.FALSE).getWorkflow();
        cr(workflow.getProjectMetadata(), "projectmetadataent");
        assertNull(workflow.getComponentMetadata());

        // checks the metadata of a component
        workflow = ws().getWorkflow(wfId, new NodeIDEnt(4), Boolean.FALSE).getWorkflow();
        cr(workflow.getComponentMetadata(), "componentmetadataent_4");
        assertNull(workflow.getProjectMetadata());

        // makes sure that no metadata is returned for a metanode
        workflow = ws().getWorkflow(wfId, new NodeIDEnt(2), Boolean.FALSE).getWorkflow();
        assertNull(workflow.getProjectMetadata());
        assertNull(workflow.getComponentMetadata());
    }

    /**
     * Tests
     * {@link WorkflowService#executeWorkflowCommand(String, NodeIDEnt, org.knime.gateway.api.webui.entity.WorkflowCommandEnt)}
     * when called with {@link TranslateCommandEnt}.
     *
     * @throws Exception
     */
    public void testExecuteTranslateCommand() throws Exception {
        String wfId = loadWorkflow(TestWorkflowCollection.GENERAL_WEB_UI);

        NodeIDEnt node15 = new NodeIDEnt(15);
        NodeIDEnt node16 = new NodeIDEnt(16);
        NodeIDEnt node18 = new NodeIDEnt(18);
        WorkflowEnt workflow = ws().getWorkflow(wfId, NodeIDEnt.getRootID(), true).getWorkflow();
        Map<String, NodeEnt> nodes = workflow.getNodes();
        XYEnt orgPosNode15 = nodes.get(node15.toString()).getPosition();
        XYEnt orgPosNode16 = nodes.get(node16.toString()).getPosition();
        XYEnt orgPosNode18 = nodes.get(node18.toString()).getPosition();

        assertThat(workflow.getAllowedActions().isCanUndo(), is(false));
        assertThat(workflow.getAllowedActions().isCanRedo(), is(false));

        // node and annotation translation
        AnnotationIDEnt anno3 = new AnnotationIDEnt("root_3");
        TranslateCommandEnt command = builder(TranslateCommandEntBuilder.class).setKind(KindEnum.TRANSLATE)
            .setNodeIds(asList(node15, node16, node18)).setAnnotationIds(singletonList(anno3))
            .setTranslation(builder(XYEntBuilder.class).setX(-224).setY(-763).build()).build();
        ws().executeWorkflowCommand(wfId, NodeIDEnt.getRootID(), command);
        workflow = ws().getWorkflow(wfId, NodeIDEnt.getRootID(), true).getWorkflow();
        nodes = workflow.getNodes();
        XYEnt pos = nodes.get(node15.toString()).getPosition();
        assertThat(pos.getX(), is(0));
        assertThat(pos.getY(), is(0));
        pos = nodes.get(node16.toString()).getPosition();
        assertThat(pos.getX(), is(120));
        assertThat(pos.getY(), is(0));
        pos = nodes.get(node18.toString()).getPosition();
        assertThat(pos.getX(), is(240));
        assertThat(pos.getY(), is(0));
        WorkflowAnnotationEnt wa =
            workflow.getWorkflowAnnotations().stream().filter(a -> a.getId().equals(anno3)).findFirst().orElse(null);
        assertThat(wa.getBounds().getX(), is(116)); // NOSONAR wa guaranteed to be non-null
        assertThat(wa.getBounds().getY(), is(123));
        assertThat(workflow.getAllowedActions().isCanUndo(), is(true));
        assertThat(workflow.getAllowedActions().isCanRedo(), is(false));

        // undo
        ws().undoWorkflowCommand(wfId, NodeIDEnt.getRootID());
        workflow = ws().getWorkflow(wfId, NodeIDEnt.getRootID(), true).getWorkflow();
        nodes = workflow.getNodes();
        pos = nodes.get(node15.toString()).getPosition();
        assertThat(pos.getX(), is(orgPosNode15.getX()));
        assertThat(pos.getY(), is(orgPosNode15.getY()));
        pos = nodes.get(node16.toString()).getPosition();
        assertThat(pos.getX(), is(orgPosNode16.getX()));
        assertThat(pos.getY(), is(orgPosNode16.getY()));
        pos = nodes.get(node18.toString()).getPosition();
        assertThat(pos.getX(), is(orgPosNode18.getX()));
        assertThat(pos.getY(), is(orgPosNode18.getY()));
        assertThat(workflow.getAllowedActions().isCanUndo(), is(false));
        assertThat(workflow.getAllowedActions().isCanRedo(), is(true));

        // redo
        ws().redoWorkflowCommand(wfId, NodeIDEnt.getRootID());
        workflow = ws().getWorkflow(wfId, NodeIDEnt.getRootID(), true).getWorkflow();
        nodes = workflow.getNodes();
        pos = nodes.get(node15.toString()).getPosition();
        assertThat(pos.getX(), is(0));
        assertThat(pos.getY(), is(0));
        pos = nodes.get(node16.toString()).getPosition();
        assertThat(pos.getX(), is(120));
        assertThat(pos.getY(), is(0));
        pos = nodes.get(node18.toString()).getPosition();
        assertThat(pos.getX(), is(240));
        assertThat(pos.getY(), is(0));
        assertThat(workflow.getAllowedActions().isCanUndo(), is(true));
        assertThat(workflow.getAllowedActions().isCanRedo(), is(false));

        // annotation translation alone
        AnnotationIDEnt anno1 = new AnnotationIDEnt("root_1");
        TranslateCommandEnt command2 =
            builder(TranslateCommandEntBuilder.class).setKind(KindEnum.TRANSLATE).setAnnotationIds(singletonList(anno1))
                .setTranslation(builder(XYEntBuilder.class).setX(-880).setY(-26).build()).build();
        ws().executeWorkflowCommand(wfId, NodeIDEnt.getRootID(), command2);
        workflow = ws().getWorkflow(wfId, NodeIDEnt.getRootID(), false).getWorkflow();
        wa = workflow.getWorkflowAnnotations().stream().filter(a -> a.getId().equals(anno1)).findFirst().orElse(null);
        assertThat(wa.getBounds().getX(), is(0)); // NOSONAR wa guaranteed to be non-null
        assertThat(wa.getBounds().getY(), is(0));

        // exceptions
        TranslateCommandEnt command3 = builder(TranslateCommandEntBuilder.class).setKind(KindEnum.TRANSLATE)
            .setNodeIds(singletonList(new NodeIDEnt(9999)))
            .setAnnotationIds(singletonList(new AnnotationIDEnt("root_12345")))
            .setTranslation(builder(XYEntBuilder.class).setX(0).setY(0).build()).build();
        assertThrows(NodeNotFoundException.class,
            () -> ws().executeWorkflowCommand(wfId, new NodeIDEnt(999999), command3));
        try {
            ws().executeWorkflowCommand(wfId, NodeIDEnt.getRootID(), command3);
        } catch (Exception e) { // NOSONAR
            assertThat("unexpected exception class", e, Matchers.instanceOf(OperationNotAllowedException.class));
            assertThat("unexpected exception message", e.getMessage(),
                is("Failed to execute command. Workflow parts not found: "
                    + "nodes (root:9999), workflow-annotations (root_12345)"));
        }

    }

    /**
     * Tests
     * {@link WorkflowService#executeWorkflowCommand(String, NodeIDEnt, org.knime.gateway.api.webui.entity.WorkflowCommandEnt)}
     * when called with {@link DeleteCommandEnt}.
     *
     * @throws Exception
     */
    public void testExecuteDeleteCommand() throws Exception {
        final String wfId = loadWorkflow(TestWorkflowCollection.GENERAL_WEB_UI);

        // successful deletion
        DeleteCommandEnt command = createDeleteCommandEnt(asList(new NodeIDEnt(1), new NodeIDEnt(4)),
            asList(new ConnectionIDEnt(new NodeIDEnt(26), 1)), asList(new AnnotationIDEnt(getRootID(), 1)));
        ws().executeWorkflowCommand(wfId, getRootID(), command);
        cr(ws().getWorkflow(wfId, getRootID(), true).getWorkflow(), "delete_command");

        // undo deletion
        ws().undoWorkflowCommand(wfId, getRootID());
        cr(ws().getWorkflow(wfId, getRootID(), true).getWorkflow(), "undo_delete_command");

        // deletion fails because a node doesn't exist
        DeleteCommandEnt command2 = createDeleteCommandEnt(asList(new NodeIDEnt(99999999)), emptyList(), emptyList());
        Exception ex = Assert.assertThrows(OperationNotAllowedException.class,
            () -> ws().executeWorkflowCommand(wfId, getRootID(), command2));
        assertThat(ex.getMessage(), is("Some nodes can't be deleted or don't exist. Delete operation aborted."));

        // deletion fails because a connection doesn't exist
        DeleteCommandEnt command3 =
            createDeleteCommandEnt(emptyList(), asList(new ConnectionIDEnt(new NodeIDEnt(99999999), 0)), emptyList());
        ex = Assert.assertThrows(OperationNotAllowedException.class,
            () -> ws().executeWorkflowCommand(wfId, getRootID(), command3));
        assertThat(ex.getMessage(), is("Some connections don't exist. Delete operation aborted."));

        // deletion fails because a workflow annotation doesn't exist
        DeleteCommandEnt command4 =
            createDeleteCommandEnt(emptyList(), emptyList(), asList(new AnnotationIDEnt(getRootID(), 999999999)));
        ex = Assert.assertThrows(OperationNotAllowedException.class,
            () -> ws().executeWorkflowCommand(wfId, getRootID(), command4));
        assertThat(ex.getMessage(), is("Some workflow annotations don't exist. Delete operation aborted."));

        // deletion fails because a node cannot be deleted due to a delete lock
        DeleteCommandEnt command5 = createDeleteCommandEnt(asList(new NodeIDEnt(23, 0, 8)), emptyList(), emptyList());
        ex = Assert.assertThrows(OperationNotAllowedException.class,
            () -> ws().executeWorkflowCommand(wfId, new NodeIDEnt(23), command5));
        assertThat(ex.getMessage(), is("Some nodes can't be deleted or don't exist. Delete operation aborted."));

        /* checks for a workflow in execution */
        String wfId2 = loadWorkflow(TestWorkflowCollection.EXECUTION_STATES);
        executeWorkflowAsync(wfId2);
        Awaitility.await().atMost(5, TimeUnit.SECONDS).pollInterval(100, TimeUnit.MILLISECONDS).untilAsserted(() -> {
            WorkflowEnt w = ws().getWorkflow(wfId2, NodeIDEnt.getRootID(), Boolean.FALSE).getWorkflow();
            assertThat(((NativeNodeEnt)w.getNodes().get("root:4")).getState().getExecutionState(),
                is(ExecutionStateEnum.EXECUTED));
        });
        cr(ws().getWorkflow(wfId2, getRootID(), true).getWorkflow(), "can_delete_executing");

        // deletion fails because of a node that cannot be deleted due to executing successors
        DeleteCommandEnt command6 = createDeleteCommandEnt(asList(new NodeIDEnt(3)), emptyList(), emptyList());
        ex = Assert.assertThrows(OperationNotAllowedException.class,
            () -> ws().executeWorkflowCommand(wfId2, getRootID(), command6));
        assertThat(ex.getMessage(), is("Some nodes can't be deleted or don't exist. Delete operation aborted."));

        // deletion of a connection fails because because it's connected to an executing node
        DeleteCommandEnt command7 =
            createDeleteCommandEnt(emptyList(), asList(new ConnectionIDEnt(new NodeIDEnt(7), 0)), emptyList());
        ex = Assert.assertThrows(OperationNotAllowedException.class,
            () -> ws().executeWorkflowCommand(wfId2, getRootID(), command7));
        assertThat(ex.getMessage(), is("Some connections can't be deleted. Delete operation aborted."));

    }

    private static DeleteCommandEnt createDeleteCommandEnt(final List<NodeIDEnt> nodeIds,
        final List<ConnectionIDEnt> connectionIds, final List<AnnotationIDEnt> annotationIds) {
        return builder(DeleteCommandEntBuilder.class).setKind(KindEnum.DELETE)//
            .setNodeIds(nodeIds)//
            .setConnectionIds(connectionIds)//
            .setAnnotationIds(annotationIds)//
            .build();
    }

}
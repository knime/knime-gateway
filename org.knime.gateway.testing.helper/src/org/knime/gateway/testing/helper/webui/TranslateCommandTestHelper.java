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

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.startsWith;
import static org.junit.Assert.assertThrows;
import static org.knime.gateway.api.entity.EntityBuilderManager.builder;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

import org.hamcrest.Matchers;
import org.knime.gateway.api.entity.AnnotationIDEnt;
import org.knime.gateway.api.entity.ConnectionIDEnt;
import org.knime.gateway.api.entity.NodeIDEnt;
import org.knime.gateway.api.webui.entity.NodeEnt;
import org.knime.gateway.api.webui.entity.TranslateCommandEnt;
import org.knime.gateway.api.webui.entity.WorkflowAnnotationEnt;
import org.knime.gateway.api.webui.entity.WorkflowCommandEnt;
import org.knime.gateway.api.webui.entity.WorkflowEnt;
import org.knime.gateway.api.webui.entity.XYEnt;
import org.knime.gateway.api.webui.service.util.ServiceExceptions;
import org.knime.gateway.api.webui.service.util.ServiceExceptions.ServiceCallException;
import org.knime.gateway.testing.helper.ResultChecker;
import org.knime.gateway.testing.helper.ServiceProvider;
import org.knime.gateway.testing.helper.TestWorkflowCollection;
import org.knime.gateway.testing.helper.WorkflowExecutor;
import org.knime.gateway.testing.helper.WorkflowLoader;

/**
 * Tests the implementation of {@link org.knime.gateway.impl.webui.service.commands.Translate}.
 */
@SuppressWarnings({"javadoc", "java:S1166", "java:S112", "java:1186", "java:1172"})
public class TranslateCommandTestHelper extends WebUIGatewayServiceTestHelper {

    private static final int[] delta = new int[]{-880, -20};

    /**
     * @param entityResultChecker
     * @param serviceProvider
     * @param workflowLoader
     * @param workflowExecutor
     */
    public TranslateCommandTestHelper(final ResultChecker entityResultChecker, final ServiceProvider serviceProvider,
        final WorkflowLoader workflowLoader, final WorkflowExecutor workflowExecutor) {
        super(TranslateCommandTestHelper.class, entityResultChecker, serviceProvider, workflowLoader, workflowExecutor);
    }

    private static TranslateCommandEnt translateCommand(final int[] delta, final List<NodeIDEnt> nodes,
        final Map<String, List<Integer>> bendpoints) {
        return builder(TranslateCommandEnt.TranslateCommandEntBuilder.class)
            .setKind(WorkflowCommandEnt.KindEnum.TRANSLATE) //
            .setNodeIds(nodes).setConnectionBendpoints(bendpoints)
            .setTranslation(builder(XYEnt.XYEntBuilder.class).setX(delta[0]).setY(delta[1]).build()).build();
    }


    private static boolean objectMoved(final Function<WorkflowEnt, XYEnt> accessor, final int[] delta,
        final WorkflowEnt originalWorkflow, final WorkflowEnt modifiedWorkflow) {
        var before = accessor.apply(originalWorkflow);
        var after = accessor.apply(modifiedWorkflow);
        return (before.getX() + delta[0] == after.getX()) && (before.getY() + delta[1] == after.getY());
    }

    private static Function<WorkflowEnt, XYEnt> bendpointAccessor(final String connectionId, final int bendpointIndex) {
        return wfEnt -> wfEnt.getConnections().get(connectionId).getBendpoints().get(bendpointIndex);
    }

    public void testTranslateBendpointsUndoRedo() throws Exception {
        var wfId = loadWorkflow(TestWorkflowCollection.BENDPOINTS);
        var originalWfEnt = ws().getWorkflow(wfId, NodeIDEnt.getRootID(), false, null).getWorkflow();
        var connection = BendpointsTestHelper.connectionWithTwoBendpoints.toString();
        var bendpointIndex = 1;
        Function<WorkflowEnt, XYEnt> bendpoint = bendpointAccessor(connection, bendpointIndex);
        var command = translateCommand(delta, List.of(), Map.of(connection, List.of(bendpointIndex)));
        executeWorkflowCommand(command, wfId);
        awaitMoved(bendpoint, delta, originalWfEnt, wfId);
        undoWorkflowCommand(wfId);
        awaitMoved(bendpoint, new int[]{0, 0}, originalWfEnt, wfId);
        redoWorkflowCommand(wfId);
        awaitMoved(bendpoint, delta, originalWfEnt, wfId);
    }

    public void testTranslateBendpointsOnlySelected() throws Exception {
        var wfId = loadWorkflow(TestWorkflowCollection.BENDPOINTS);
        var originalWfEnt = ws().getWorkflow(wfId, NodeIDEnt.getRootID(), false, null).getWorkflow();
        var connectionId = BendpointsTestHelper.connectionWithTwoBendpoints.toString();
        var sourceNode = new NodeIDEnt(189);
        var targetNode = BendpointsTestHelper.connectionWithTwoBendpoints.getDestNodeIDEnt();
        var selectedBendpointIndex = 1;
        Function<WorkflowEnt, XYEnt> bendpoint = bendpointAccessor(connectionId, selectedBendpointIndex);
        var command = translateCommand(delta, List.of(sourceNode, targetNode),
            Map.of(connectionId, List.of(selectedBendpointIndex)));
        executeWorkflowCommand(command, wfId);
        awaitMoved(bendpoint, delta, originalWfEnt, wfId);
    }

    public void testTranslateBendpointOfInvalidIndex() throws Exception {
        var wfId = loadWorkflow(TestWorkflowCollection.BENDPOINTS);
        String connectionId = BendpointsTestHelper.connectionWithOneBendpoint.toString();
        var bendpointIndex = 999;
        var translateInvalidBendpointIndex =
            translateCommand(delta, List.of(), Map.of(connectionId, List.of(bendpointIndex)));
        try {
            executeWorkflowCommand(translateInvalidBendpointIndex, wfId);
        } catch (Exception e) {
            assertThat("unexpected exception message", e.getMessage(),
                startsWith("Failed to execute command. Workflow parts not found: bendpoints (999 on connection STD"));
        }
    }

    public void testTranslateBendpointsOnConnectionWithNone() throws Exception {
        var wfId = loadWorkflow(TestWorkflowCollection.BENDPOINTS);
        var connectionId = BendpointsTestHelper.connectionWithNoBendpointsEmptyUiInfo.toString();
        var bendpointIndex = 999;
        var translateInvalidBendpointIndex =
            translateCommand(delta, List.of(), Map.of(connectionId, List.of(bendpointIndex)));
        try {
            executeWorkflowCommand(translateInvalidBendpointIndex, wfId);
        } catch (Exception e) {
            assertThat("unexpected exception message", e.getMessage(),
                startsWith("Failed to execute command. Workflow parts not found: bendpoints (999 on connection"));
        }
    }

    public void testTranslateBendpointsOnInexistentConnection() throws Exception {
        var wfId = loadWorkflow(TestWorkflowCollection.BENDPOINTS);
        var connection = new ConnectionIDEnt(new NodeIDEnt(999), 999).toString();
        var translateInvalidBendpointIndex = translateCommand(delta, List.of(), Map.of(connection, List.of(0)));
        try {
            executeWorkflowCommand(translateInvalidBendpointIndex, wfId);
        } catch (Exception e) {
            assertThat("unexpected exception message", e.getMessage(),
                startsWith("Failed to execute command. Workflow parts not found: " + "connections ([?"));
        }
    }

    public void testTranslateNodesAndAnnotations() throws Exception {
        String wfId = loadWorkflow(TestWorkflowCollection.GENERAL_WEB_UI);
        var node15 = new NodeIDEnt(15);
        var node16 = new NodeIDEnt(16);
        var node18 = new NodeIDEnt(18);
        WorkflowEnt workflow = ws().getWorkflow(wfId, NodeIDEnt.getRootID(), true, null).getWorkflow();
        Map<String, NodeEnt> nodes = workflow.getNodes();
        XYEnt orgPosNode15 = nodes.get(node15.toString()).getPosition();
        XYEnt orgPosNode16 = nodes.get(node16.toString()).getPosition();
        XYEnt orgPosNode18 = nodes.get(node18.toString()).getPosition();

        assertThat(workflow.getAllowedActions().isCanUndo(), is(false));
        assertThat(workflow.getAllowedActions().isCanRedo(), is(false));

        // node and annotation translation
        var anno3 = new AnnotationIDEnt("root_3");
        var deltaX = -224;
        var deltaY = -763;
        TranslateCommandEnt command = builder(TranslateCommandEnt.TranslateCommandEntBuilder.class)
            .setKind(WorkflowCommandEnt.KindEnum.TRANSLATE).setNodeIds(asList(node15, node16, node18)) //
            .setAnnotationIds(singletonList(anno3)) //
            .setTranslation(builder(XYEnt.XYEntBuilder.class).setX(deltaX).setY(deltaY).build()).build();
        ws().executeWorkflowCommand(wfId, NodeIDEnt.getRootID(), command);
        workflow = ws().getWorkflow(wfId, NodeIDEnt.getRootID(), true, null).getWorkflow();
        // assert node positions
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
        // assert annotation positions
        WorkflowAnnotationEnt wa =
            workflow.getWorkflowAnnotations().stream().filter(a -> a.getId().equals(anno3)).findFirst().orElse(null);
        assertThat(wa.getBounds().getX(), is(116)); // NOSONAR wa guaranteed to be non-null
        assertThat(wa.getBounds().getY(), is(117));
        assertThat(workflow.getAllowedActions().isCanUndo(), is(true));
        assertThat(workflow.getAllowedActions().isCanRedo(), is(false));

        // undo
        ws().undoWorkflowCommand(wfId, NodeIDEnt.getRootID());
        workflow = ws().getWorkflow(wfId, NodeIDEnt.getRootID(), true, null).getWorkflow();
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
        workflow = ws().getWorkflow(wfId, NodeIDEnt.getRootID(), true, null).getWorkflow();
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
        TranslateCommandEnt command2 = builder(TranslateCommandEnt.TranslateCommandEntBuilder.class)
            .setKind(WorkflowCommandEnt.KindEnum.TRANSLATE).setAnnotationIds(singletonList(anno1))
            .setTranslation(builder(XYEnt.XYEntBuilder.class).setX(-880).setY(-20).build()).build();
        ws().executeWorkflowCommand(wfId, NodeIDEnt.getRootID(), command2);
        workflow = ws().getWorkflow(wfId, NodeIDEnt.getRootID(), false, null).getWorkflow();
        wa = workflow.getWorkflowAnnotations().stream().filter(a -> a.getId().equals(anno1)).findFirst().orElse(null);
        assertThat(wa.getBounds().getX(), is(0)); // NOSONAR wa guaranteed to be non-null
        assertThat(wa.getBounds().getY(), is(0));

        // move a node within a component
        TranslateCommandEnt command3 =
            builder(TranslateCommandEnt.TranslateCommandEntBuilder.class).setKind(WorkflowCommandEnt.KindEnum.TRANSLATE)
                .setNodeIds(List.of(new NodeIDEnt(12, 0, 10))).setAnnotationIds(emptyList())
                .setTranslation(builder(XYEnt.XYEntBuilder.class).setX(10).setY(10).build()).build();
        NodeEnt nodeBefore =
            ws().getWorkflow(wfId, new NodeIDEnt(12), true, null).getWorkflow().getNodes().get("root:12:0:10");
        ws().executeWorkflowCommand(wfId, new NodeIDEnt(12), command3);
        NodeEnt nodeAfter =
            ws().getWorkflow(wfId, new NodeIDEnt(12), true, null).getWorkflow().getNodes().get("root:12:0:10");
        assertThat(nodeAfter.getPosition().getX(), is(nodeBefore.getPosition().getX() + 10));
        assertThat(nodeAfter.getPosition().getY(), is(nodeBefore.getPosition().getY() + 10));

        // move a node within a metanode
        TranslateCommandEnt command4 =
            builder(TranslateCommandEnt.TranslateCommandEntBuilder.class).setKind(WorkflowCommandEnt.KindEnum.TRANSLATE)
                .setNodeIds(List.of(new NodeIDEnt(6, 3))).setAnnotationIds(emptyList())
                .setTranslation(builder(XYEnt.XYEntBuilder.class).setX(10).setY(10).build()).build();
        nodeBefore = ws().getWorkflow(wfId, new NodeIDEnt(6), true, null).getWorkflow().getNodes().get("root:6:3");
        ws().executeWorkflowCommand(wfId, new NodeIDEnt(6), command4);
        nodeAfter = ws().getWorkflow(wfId, new NodeIDEnt(6), true, null).getWorkflow().getNodes().get("root:6:3");
        assertThat(nodeAfter.getPosition().getX(), is(nodeBefore.getPosition().getX() + 10));
        assertThat(nodeAfter.getPosition().getY(), is(nodeBefore.getPosition().getY() + 10));

        // exceptions
        TranslateCommandEnt command5 = builder(TranslateCommandEnt.TranslateCommandEntBuilder.class)
            .setKind(WorkflowCommandEnt.KindEnum.TRANSLATE).setNodeIds(singletonList(new NodeIDEnt(9999)))
            .setAnnotationIds(singletonList(new AnnotationIDEnt("root_12345")))
            .setTranslation(builder(XYEnt.XYEntBuilder.class).setX(0).setY(0).build()).build();
        assertThrows(ServiceExceptions.ServiceCallException.class,
            () -> ws().executeWorkflowCommand(wfId, new NodeIDEnt(999999), command5));
        try {
            ws().executeWorkflowCommand(wfId, NodeIDEnt.getRootID(), command5);
        } catch (Exception e) { // NOSONAR
            assertThat("unexpected exception class", e,
                Matchers.instanceOf(ServiceExceptions.ServiceCallException.class));
            assertThat("unexpected exception message", e.getMessage(),
                is("Failed to execute command. Workflow parts not found: "
                    + "nodes (0:9999), workflow-annotations (0:12345)"));
        }

    }

    public void testTranslateMetanodePortsBars() throws Exception {
        String wfId = loadWorkflow(TestWorkflowCollection.GENERAL_WEB_UI);
        var metanodeId = new NodeIDEnt(6);
        var outPortsBarBounds =
            ws().getWorkflow(wfId, metanodeId, Boolean.FALSE, null).getWorkflow().getMetaOutPorts().getBounds();

        var translateCommand = builder(TranslateCommandEnt.TranslateCommandEntBuilder.class)
            .setKind(WorkflowCommandEnt.KindEnum.TRANSLATE).setMetanodeOutPortsBar(Boolean.TRUE)
            .setTranslation(builder(XYEnt.XYEntBuilder.class).setX(10).setY(10).build()).build();
        ws().executeWorkflowCommand(wfId, metanodeId, translateCommand);

        var outPortsBarBoundsTranslated =
            ws().getWorkflow(wfId, metanodeId, Boolean.FALSE, null).getWorkflow().getMetaOutPorts().getBounds();
        assertThat(outPortsBarBoundsTranslated.getX(), is(outPortsBarBounds.getX() + 10));
        assertThat(outPortsBarBoundsTranslated.getY(), is(outPortsBarBounds.getY() + 10));

        // undo
        ws().undoWorkflowCommand(wfId, metanodeId);
        var outPortsBarBoundsTranslatedUndone =
            ws().getWorkflow(wfId, metanodeId, Boolean.FALSE, null).getWorkflow().getMetaOutPorts().getBounds();
        assertThat(outPortsBarBoundsTranslatedUndone.getX(), is(outPortsBarBounds.getX()));
        assertThat(outPortsBarBoundsTranslatedUndone.getY(), is(outPortsBarBounds.getY()));

        // try to execute command on a component
        var message = assertThrows(ServiceCallException.class,
            () -> ws().executeWorkflowCommand(wfId, new NodeIDEnt(12), translateCommand)).getMessage();
        assertThat(message, is("Components don't have metanode-ports-bars to be moved."));

        // try to translate ports bar without a fixed position
        var translateCommand2 = builder(TranslateCommandEnt.TranslateCommandEntBuilder.class)
            .setKind(WorkflowCommandEnt.KindEnum.TRANSLATE).setMetanodeInPortsBar(Boolean.TRUE)
            .setTranslation(builder(XYEnt.XYEntBuilder.class).setX(10).setY(10).build()).build();
        message = assertThrows(ServiceCallException.class,
            () -> ws().executeWorkflowCommand(wfId, metanodeId, translateCommand2)).getMessage();
        assertThat(message, is("Metanode in-ports-bar can't be moved. It doesn't have a position, yet."));
    }


    private void awaitMoved(final Function<WorkflowEnt, XYEnt> accessor, final int[] delta, final WorkflowEnt originalWorkflow, final String wfId) {
        awaitTrue(wfId, wf -> objectMoved(accessor, delta, originalWorkflow, wf));
    }

}

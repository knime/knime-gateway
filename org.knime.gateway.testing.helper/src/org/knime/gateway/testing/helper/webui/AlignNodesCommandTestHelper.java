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
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.knime.gateway.api.entity.EntityBuilderManager.builder;

import java.util.Map;

import org.knime.gateway.api.entity.NodeIDEnt;
import org.knime.gateway.api.webui.entity.AlignNodesCommandEnt;
import org.knime.gateway.api.webui.entity.NodeEnt;
import org.knime.gateway.api.webui.entity.WorkflowCommandEnt;
import org.knime.gateway.api.webui.entity.XYEnt;
import org.knime.gateway.api.webui.entity.XYEnt.XYEntBuilder;
import org.knime.gateway.testing.helper.ResultChecker;
import org.knime.gateway.testing.helper.ServiceProvider;
import org.knime.gateway.testing.helper.TestWorkflowCollection;
import org.knime.gateway.testing.helper.WorkflowExecutor;
import org.knime.gateway.testing.helper.WorkflowLoader;

/**
 * Tests the implementation of {@link org.knime.gateway.impl.webui.service.commands.AlignNodes}.
 */
@SuppressWarnings("javadoc")
public class AlignNodesCommandTestHelper extends WebUIGatewayServiceTestHelper {

    /**
     * @param entityResultChecker
     * @param serviceProvider
     * @param workflowLoader
     * @param workflowExecutor
     */
    public AlignNodesCommandTestHelper(final ResultChecker entityResultChecker, final ServiceProvider serviceProvider,
                                       final WorkflowLoader workflowLoader, final WorkflowExecutor workflowExecutor) {
        super(AlignNodesCommandTestHelper.class, entityResultChecker, serviceProvider, workflowLoader,
                workflowExecutor);
    }

    public void testAlignNodes() throws Exception {
        var wfId = loadWorkflow(TestWorkflowCollection.GENERAL_WEB_UI);
        var workflow = ws().getWorkflow(wfId, NodeIDEnt.getRootID(), true, null).getWorkflow();

        // selected some nodes which have pairwise different x and y from workflowent_root.snap
        var node21 = new NodeIDEnt(21);
        var node19 = new NodeIDEnt(19);
        var node183 = new NodeIDEnt(183);
        var nodeId21 = node21.toString();
        var nodeId19 = node19.toString();
        var nodeId183 = node183.toString();
        Map<String, NodeEnt> nodes = workflow.getNodes();
        var originalPos21 = nodes.get(nodeId21).getPosition();
        var originalPos19 = nodes.get(nodeId19).getPosition();
        var originalPos183 = nodes.get(nodeId183).getPosition();
        var minX = Math.min(Math.min(originalPos21.getX(), originalPos19.getX()), originalPos183.getX());
        var minY = Math.min(Math.min(originalPos21.getY(), originalPos19.getY()), originalPos183.getY());

        assertThat(workflow.getAllowedActions().isCanUndo(), is(false));
        assertThat(workflow.getAllowedActions().isCanRedo(), is(false));

        // 1. nodes alignment vertical
        var command = builder(AlignNodesCommandEnt.AlignNodesCommandEntBuilder.class)
                .setKind(WorkflowCommandEnt.KindEnum.ALIGN_NODES).setNodeIds(asList(node21, node19, node183)) //
                .setDirection(AlignNodesCommandEnt.DirectionEnum.VERTICAL)
                .build();
        ws().executeWorkflowCommand(wfId, NodeIDEnt.getRootID(), command);
        workflow = ws().getWorkflow(wfId, NodeIDEnt.getRootID(), true, null).getWorkflow();
        // assert node positions
        nodes = workflow.getNodes();
        var pos = nodes.get(nodeId21).getPosition();
        assertPos(pos, minX, originalPos21.getY());
        pos = nodes.get(nodeId19).getPosition();
        assertPos(pos, minX, originalPos19.getY());
        pos = nodes.get(nodeId183).getPosition();
        assertPos(pos, minX, originalPos183.getY());

        assertThat(workflow.getAllowedActions().isCanUndo(), is(true));
        assertThat(workflow.getAllowedActions().isCanRedo(), is(false));

        // undo
        ws().undoWorkflowCommand(wfId, NodeIDEnt.getRootID());
        workflow = ws().getWorkflow(wfId, NodeIDEnt.getRootID(), true, null).getWorkflow();
        nodes = workflow.getNodes();
        pos = nodes.get(nodeId21).getPosition();
        assertThat(pos, is(originalPos21));
        pos = nodes.get(nodeId19).getPosition();
        assertThat(pos, is(originalPos19));
        pos = nodes.get(nodeId183).getPosition();
        assertThat(pos, is(originalPos183));

        assertThat(workflow.getAllowedActions().isCanUndo(), is(false));
        assertThat(workflow.getAllowedActions().isCanRedo(), is(true));

        // redo
        ws().redoWorkflowCommand(wfId, NodeIDEnt.getRootID());
        workflow = ws().getWorkflow(wfId, NodeIDEnt.getRootID(), true, null).getWorkflow();
        nodes = workflow.getNodes();
        pos = nodes.get(nodeId21).getPosition();
        assertPos(pos, minX, originalPos21.getY());
        pos = nodes.get(nodeId19).getPosition();
        assertPos(pos, minX, originalPos19.getY());
        pos = nodes.get(nodeId183).getPosition();
        assertPos(pos, minX, originalPos183.getY());

        assertThat(workflow.getAllowedActions().isCanUndo(), is(true));
        assertThat(workflow.getAllowedActions().isCanRedo(), is(false));

        // 2. nodes alignment horizontal
        ws().undoWorkflowCommand(wfId, NodeIDEnt.getRootID());
        command = builder(AlignNodesCommandEnt.AlignNodesCommandEntBuilder.class)
                .setKind(WorkflowCommandEnt.KindEnum.ALIGN_NODES).setNodeIds(asList(node21, node19, node183)) //
                .setDirection(AlignNodesCommandEnt.DirectionEnum.HORIZONTAL)
                .build();
        ws().executeWorkflowCommand(wfId, NodeIDEnt.getRootID(), command);
        workflow = ws().getWorkflow(wfId, NodeIDEnt.getRootID(), true, null).getWorkflow();

        // assert node positions
        nodes = workflow.getNodes();
        pos = nodes.get(nodeId21).getPosition();
        assertPos(pos, originalPos21.getX(), minY);
        pos = nodes.get(nodeId19).getPosition();
        assertPos(pos, originalPos19.getX(), minY);
        pos = nodes.get(nodeId183).getPosition();
        assertPos(pos, originalPos183.getX(), minY);

        assertThat(workflow.getAllowedActions().isCanUndo(), is(true));
        assertThat(workflow.getAllowedActions().isCanRedo(), is(false));
    }

    private static void assertPos(final XYEnt foundPos, final Integer expectedX, final Integer expectedY) {
        var expectedPos = builder(XYEntBuilder.class).setX(expectedX).setY(expectedY).build();
        assertThat(foundPos, is(expectedPos));
    }
}

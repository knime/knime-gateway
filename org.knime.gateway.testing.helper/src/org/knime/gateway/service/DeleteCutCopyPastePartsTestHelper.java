/*
 * ------------------------------------------------------------------------
 *  Copyright by KNIME AG, Zurich, Switzerland
 *  Website: http://www.knime.com; Email: contact@knime.com
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
package org.knime.gateway.service;

import static java.util.Arrays.asList;
import static org.hamcrest.core.StringContains.containsString;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.knime.gateway.api.entity.EntityBuilderManager.builder;
import static org.knime.gateway.api.entity.NodeIDEnt.getRootID;
import static org.knime.gateway.api.util.EntityUtil.createAnnotationIDEntList;
import static org.knime.gateway.api.util.EntityUtil.createConnectionIDEntList;
import static org.knime.gateway.api.util.EntityUtil.createNodeIDEntList;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.knime.gateway.api.entity.AnnotationIDEnt;
import org.knime.gateway.api.entity.BoundsEnt;
import org.knime.gateway.api.entity.NodeIDEnt;
import org.knime.gateway.api.entity.WorkflowEnt;
import org.knime.gateway.api.entity.WorkflowPartsEnt;
import org.knime.gateway.api.entity.WorkflowPartsEnt.WorkflowPartsEntBuilder;
import org.knime.gateway.api.service.util.ServiceExceptions;
import org.knime.gateway.testing.helper.ResultChecker;
import org.knime.gateway.testing.helper.ServiceProvider;
import org.knime.gateway.testing.helper.WorkflowExecutor;
import org.knime.gateway.testing.helper.WorkflowLoader;

/**
 * Tests to cut, copy and paste workflow parts, i.e. connections, nodes and workflow annotatins.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
public class DeleteCutCopyPastePartsTestHelper extends AbstractGatewayServiceTestHelper {

    /**
     * See
     * {@link AbstractGatewayServiceTestHelper#AbstractGatewayServiceTestHelper(String, WorkflowLoader, ServiceProvider, ResultChecker)}.
     *
     * @param workflowLoader
     * @param workflowExecutor
     * @param serviceProvider
     * @param entityResultChecker
     */
    public DeleteCutCopyPastePartsTestHelper(final ServiceProvider serviceProvider,
        final ResultChecker entityResultChecker, final WorkflowLoader workflowLoader,
        final WorkflowExecutor workflowExecutor) {
        super("deletecutcopypasteparts", serviceProvider, entityResultChecker, workflowLoader, workflowExecutor);
    }

    /**
     * Tests deleting (and partly undoing) parts of a workflow.
     *
     * @throws Exception if an error occurs
     */
    public void testDeleteParts() throws Exception {
        UUID wfId = loadWorkflow(TestWorkflow.GENERAL);

        WorkflowEnt workflow = ws().getWorkflow(wfId, getRootID()).getWorkflow();

        //cut a selection of nodes and workflow annotations
        WorkflowPartsEnt parts = builder(WorkflowPartsEntBuilder.class)
                //delete nodes with many connections, a metanode that contains a workflow annotation
                //a node with a flow variable connection
                .setNodeIDs(createNodeIDEntList(new int[][] {{20}, {1}, {6}, {9}, {14}}))
                //root_1
                .setAnnotationIDs(createAnnotationIDEntList(new int[][] {{}}, 1))
                //"4_0", "22_1"
                .setConnectionIDs(createConnectionIDEntList(new int[][] {{4},{22}}, 0, 1))
                .build();
        int[] min = calcMinOffset(parts, workflow);
        UUID partsId = ws().deleteWorkflowParts(wfId, getRootID(), parts, true);
        workflow = ws().getWorkflow(wfId, getRootID()).getWorkflow();
        cr(workflow, "workflowent_root_parts_removed");

        //partly undo the delete (i.e. paste) - some former connection are expected to be not contained
        ws().pasteWorkflowParts(wfId, getRootID(), partsId, min[0], min[1]);
        workflow = ws().getWorkflow(wfId, getRootID()).getWorkflow();
        cr(workflow, "workflowent_root_parts_removed_undo");

        // delete parts without a copy
        parts = builder(WorkflowPartsEntBuilder.class)
                .setNodeIDs(createNodeIDEntList(new int[][] {{20}, {1}, {6}, {9}, {14}}))
                //root_2
                .setAnnotationIDs(createAnnotationIDEntList(new int[][] {{}}, 2))
                //"4_0", "22_1"
                .setConnectionIDs(createConnectionIDEntList(new int[][] {{4},{22}}, 0, 1))
                .build();
        UUID nullPartsId = ws().deleteWorkflowParts(wfId, getRootID(), parts, false);
        //since no copy is made, partsId is expected to be null
        assertNull("Parts id is expected to be null", nullPartsId);
        workflow = ws().getWorkflow(wfId, getRootID()).getWorkflow();
        cr(workflow, "workflowent_root_parts_removed");

        //restore parts
        ws().pasteWorkflowParts(wfId, getRootID(), partsId, min[0], min[1]);

        // delete parts in a sub-workflow
        parts = builder(WorkflowPartsEntBuilder.class)
                .setNodeIDs(createNodeIDEntList(new int[][] {{6, 3}}))
                //6_0
                .setAnnotationIDs(createAnnotationIDEntList(new int[][] {{6}}, 0))
                .build();
        ws().deleteWorkflowParts(wfId, new NodeIDEnt(6), parts, false);
        workflow = ws().getWorkflow(wfId, new NodeIDEnt(6)).getWorkflow();
        cr(workflow, "workflowent_6_parts_removed");
        workflow = ws().getWorkflow(wfId, getRootID()).getWorkflow();
        cr(workflow, "workflowent_root_parts_removed_from_6");

        //remove some non-existing parts - nothing should happen
        parts = builder(WorkflowPartsEntBuilder.class)
                .setNodeIDs(createNodeIDEntList(new int[][] {{99}}))
                //root_10
                .setAnnotationIDs(createAnnotationIDEntList(new int[][] {{}}, 10))
                //"99_4"
                .setConnectionIDs(createConnectionIDEntList(new int[][] {{99}}, 4))
                .build();
        ws().deleteWorkflowParts(wfId, getRootID(), parts, false);
        workflow = ws().getWorkflow(wfId, getRootID()).getWorkflow();
        cr(workflow, "workflowent_root_parts_removed_from_6");

        //remove parts with a non existing parent node id
        parts = builder(WorkflowPartsEntBuilder.class).build();
        try {
            ws().deleteWorkflowParts(wfId, new NodeIDEnt(99), parts, false);
            fail("Expected a ServiceException to be thrown");
        } catch (ServiceExceptions.NodeNotFoundException e) {
            assertThat("Unexpected exception message", e.getMessage(), containsString("No such node"));
        }

        //try removing a node with an executing successor
        UUID wfIdLongrunning = loadWorkflow(TestWorkflow.LONGRUNNING);
        executeWorkflowAsync(wfIdLongrunning);
        //delete node
        parts = builder(WorkflowPartsEntBuilder.class).setNodeIDs(createNodeIDEntList(new int[][] {{1}})).build();
        try {
            ws().deleteWorkflowParts(wfIdLongrunning, getRootID(), parts, false);
            fail("Expected a ServiceException to be thrown");
        } catch (ServiceExceptions.ActionNotAllowedException e) {
            assertThat("Unexpected exception message", e.getMessage(),
                containsString("There is an outgoing connection that cannot be removed"));
        }
    }

    /**
     * Tests copying and pasting parts of a workflow.
     *
     * @throws Exception if an error occurs
     */
    public void testCopyPasteParts() throws Exception {
        UUID wfId = loadWorkflow(TestWorkflow.GENERAL);

        WorkflowPartsEnt parts = builder(WorkflowPartsEntBuilder.class)
                .setNodeIDs(createNodeIDEntList(new int[][] {{20}, {1}, {6}, {9}, {14}}))
                .setAnnotationIDs(Arrays.asList(new AnnotationIDEnt(getRootID(), 1)))
                //copying isolated connections won't have an effect
                //"4_0", "22_1"
                .setConnectionIDs(createConnectionIDEntList(new int[][] {{4},{22}}, 0, 1))
                .build();
        UUID partsId = ws().createWorkflowCopy(wfId, getRootID(), parts);

        ws().pasteWorkflowParts(wfId, getRootID(), partsId, 0, 0);
        WorkflowEnt workflow = ws().getWorkflow(wfId, getRootID()).getWorkflow();
        cr(workflow, "workflowent_root_pasted");

        //paste the same parts into a sub-workflow
        ws().pasteWorkflowParts(wfId, new NodeIDEnt(6), partsId, 0, 0);
        workflow = ws().getWorkflow(wfId, new NodeIDEnt(6)).getWorkflow();
        cr(workflow, "workflowent_6_pasted");

        //try pasting parts with none-existing id
        try {
            ws().pasteWorkflowParts(wfId, getRootID(), UUID.randomUUID(), 0, 0);
            fail("Expected a ServiceException to be thrown");
        } catch (ServiceExceptions.NotFoundException e) {
            assertThat("Unexpected exception message", e.getMessage(),
                containsString("No workflow-part copy available for the given id"));
        }

        //try copying parts that don't exist
        //non-existing node
        parts = builder(WorkflowPartsEntBuilder.class)
                .setNodeIDs(createNodeIDEntList(new int[][] {{99}}))
                .build();
        try {
            ws().createWorkflowCopy(wfId, getRootID(), parts);
        } catch (ServiceExceptions.InvalidRequestException e) {
            assertThat("Unexpected exception message", e.getMessage(),
                containsString("Failed to copy parts: No such node ID"));
        }
        //non-existing annotation
        parts = builder(WorkflowPartsEntBuilder.class)
                .setAnnotationIDs(asList(new AnnotationIDEnt(getRootID(), 9)))
                .build();
        try {
            ws().createWorkflowCopy(wfId, getRootID(), parts);
        } catch (ServiceExceptions.InvalidRequestException e) {
            assertThat("Unexpected exception message", e.getMessage(),
                containsString("Failed to copy parts: No annotation with ID"));
        }
    }

    private static int[] calcMinOffset(final WorkflowPartsEnt parts, final WorkflowEnt workflow) {
        List<BoundsEnt> bounds = new ArrayList<BoundsEnt>();
        parts.getNodeIDs().stream().map(id -> workflow.getNodes().get(id.toString()).getUIInfo().getBounds())
            .forEach(bounds::add);
        parts.getAnnotationIDs().stream().map(id -> workflow.getWorkflowAnnotations().get(id.toString()).getBounds())
            .forEach(bounds::add);

        Optional<Integer> minX = bounds.stream().map(b -> b.getX()).min((x, y) -> Integer.compare(x, y));
        Optional<Integer> minY = bounds.stream().map(b -> b.getY()).min((x, y) -> Integer.compare(x, y));
        return new int[]{minX.get(), minY.get()};
    }
}

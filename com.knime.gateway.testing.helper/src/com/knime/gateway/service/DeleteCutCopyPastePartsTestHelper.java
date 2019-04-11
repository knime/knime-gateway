/* ------------------------------------------------------------------
 * This source code, its documentation and all appendant files
 * are protected by copyright law. All rights reserved.
 *
 * Copyright by KNIME AG, Zurich, Switzerland
 *
 * You may not modify, publish, transmit, transfer or sell, reproduce,
 * create derivative works from, distribute, perform, display, or in
 * any way exploit any of the content, in whole or in part, except as
 * otherwise expressly permitted in writing by the copyright owner or
 * as specified in the license file distributed with this product.
 *
 * If you have any questions please contact the copyright holder:
 * website: www.knime.com
 * email: contact@knime.com
 * ---------------------------------------------------------------------
 *
 */
package com.knime.gateway.service;

import static com.knime.gateway.entity.EntityBuilderManager.builder;
import static com.knime.gateway.entity.NodeIDEnt.getRootID;
import static com.knime.gateway.util.EntityUtil.createAnnotationIDEntList;
import static com.knime.gateway.util.EntityUtil.createConnectionIDEntList;
import static com.knime.gateway.util.EntityUtil.createNodeIDEntList;
import static java.util.Arrays.asList;
import static org.hamcrest.core.StringContains.containsString;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.knime.gateway.entity.AnnotationIDEnt;
import com.knime.gateway.entity.BoundsEnt;
import com.knime.gateway.entity.NodeIDEnt;
import com.knime.gateway.entity.WorkflowEnt;
import com.knime.gateway.entity.WorkflowPartsEnt;
import com.knime.gateway.entity.WorkflowPartsEnt.WorkflowPartsEntBuilder;
import com.knime.gateway.service.util.ServiceExceptions;
import com.knime.gateway.testing.helper.ResultChecker;
import com.knime.gateway.testing.helper.ServiceProvider;
import com.knime.gateway.testing.helper.WorkflowExecutor;
import com.knime.gateway.testing.helper.WorkflowLoader;

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
        UUID wfId = loadWorkflow(TestWorkflow.WORKFLOW);

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
        UUID wfIdLongrunning = loadWorkflow(TestWorkflow.WORKFLOW_LONGRUNNING);
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
        UUID wfId = loadWorkflow(TestWorkflow.WORKFLOW);

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

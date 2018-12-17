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
package com.knime.gateway.v0.service;

import static com.knime.gateway.entity.EntityBuilderManager.builder;
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

import com.knime.gateway.testing.helper.ResultChecker;
import com.knime.gateway.testing.helper.ServiceProvider;
import com.knime.gateway.testing.helper.WorkflowExecutor;
import com.knime.gateway.testing.helper.WorkflowLoader;
import com.knime.gateway.v0.entity.BoundsEnt;
import com.knime.gateway.v0.entity.WorkflowEnt;
import com.knime.gateway.v0.entity.WorkflowPartsEnt;
import com.knime.gateway.v0.entity.WorkflowPartsEnt.WorkflowPartsEntBuilder;
import com.knime.gateway.v0.service.util.ServiceExceptions;

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

        WorkflowEnt workflow = ws().getWorkflow(wfId).getWorkflow();

        //cut a selection of nodes and workflow annotations
        WorkflowPartsEnt parts = builder(WorkflowPartsEntBuilder.class)
                //delete nodes with many connections, a metanode that contains a workflow annotation
                //a node with a flow variable connection
                .setNodeIDs(Arrays.asList("20", "1", "6", "9", "14"))
                .setAnnotationIDs(Arrays.asList("root_1"))
                .setConnectionIDs(Arrays.asList("4_0", "22_1"))
                .setParentNodeID("root").build();
        int[] min = calcMinOffset(parts, workflow);
        UUID partsId = ws().deleteWorkflowParts(wfId, parts, true);
        workflow = ws().getWorkflow(wfId).getWorkflow();
        cr(workflow, "workflowent_root_parts_removed");

        //partly undo the delete (i.e. paste) - some former connection are expected to be not contained
        ws().pasteWorkflowParts(wfId, partsId, min[0], min[1], null);
        workflow = ws().getWorkflow(wfId).getWorkflow();
        cr(workflow, "workflowent_root_parts_removed_undo");

        // delete parts without a copy
        parts = builder(WorkflowPartsEntBuilder.class)
                .setNodeIDs(Arrays.asList("20", "1", "6", "9", "14"))
                .setAnnotationIDs(Arrays.asList("root_2"))
                .setConnectionIDs(Arrays.asList("4_0", "22_1"))
                .setParentNodeID("root").build();
        UUID nullPartsId = ws().deleteWorkflowParts(wfId, parts, false);
        //since no copy is made, partsId is expected to be null
        assertNull("Parts id is expected to be null", nullPartsId);
        workflow = ws().getWorkflow(wfId).getWorkflow();
        cr(workflow, "workflowent_root_parts_removed");

        //restore parts
        ws().pasteWorkflowParts(wfId, partsId, min[0], min[1], null);

        // delete parts in a sub-workflow
        parts = builder(WorkflowPartsEntBuilder.class)
                .setNodeIDs(Arrays.asList("6:3"))
                .setAnnotationIDs(Arrays.asList("6_0"))
                .setParentNodeID("6").build();
        ws().deleteWorkflowParts(wfId, parts, false);
        workflow = ws().getSubWorkflow(wfId, "6").getWorkflow();
        cr(workflow, "workflowent_6_parts_removed");
        workflow = ws().getWorkflow(wfId).getWorkflow();
        cr(workflow, "workflowent_root_parts_removed_from_6");

        //remove some non-existing parts - nothing should happen
        parts = builder(WorkflowPartsEntBuilder.class)
                .setNodeIDs(Arrays.asList("99"))
                .setAnnotationIDs(Arrays.asList("root_10"))
                .setConnectionIDs(Arrays.asList("99_4"))
                .setParentNodeID("root").build();
        ws().deleteWorkflowParts(wfId, parts, false);
        workflow = ws().getWorkflow(wfId).getWorkflow();
        cr(workflow, "workflowent_root_parts_removed_from_6");

        //remove parts with a non existing parent node id
        parts = builder(WorkflowPartsEntBuilder.class).setParentNodeID("99").build();
        try {
            ws().deleteWorkflowParts(wfId, parts, false);
            fail("Expected a ServiceException to be thrown");
        } catch (ServiceExceptions.NodeNotFoundException e) {
            assertThat("Unexpected exception message", e.getMessage(), containsString("No such node"));
        }

        //try removing a node with an executing successor
        UUID wfIdLongrunning = loadWorkflow(TestWorkflow.WORKFLOW_LONGRUNNING);
        executeWorkflowAsync(wfIdLongrunning);
        //delete node
        parts = builder(WorkflowPartsEntBuilder.class).setNodeIDs(Arrays.asList("1")).setParentNodeID("root").build();
        try {
            ws().deleteWorkflowParts(wfIdLongrunning, parts, false);
            fail("Expected a ServiceException to be thrown");
        } catch (ServiceExceptions.ActionNotAllowedException e) {
            assertThat("Unexpected exception message", e.getMessage(),
                containsString("There is an outgoing connection that cannot be removed"));
        }

        // job will be deleted by @After method
    }

    /**
     * Tests copying and pasting parts of a workflow.
     *
     * @throws Exception if an error occurs
     */
    public void testCopyPasteParts() throws Exception {
        UUID wfId = loadWorkflow(TestWorkflow.WORKFLOW);

        WorkflowPartsEnt parts = builder(WorkflowPartsEntBuilder.class)
                .setNodeIDs(Arrays.asList("20", "1", "6", "9", "14"))
                .setAnnotationIDs(Arrays.asList("root_1"))
                //copying isolated connections won't have an effect
                .setConnectionIDs(Arrays.asList("4_0", "22_1"))
                .setParentNodeID("root").build();
        UUID partsId = ws().createWorkflowCopy(wfId, parts);

        ws().pasteWorkflowParts(wfId, partsId, 0, 0, null);
        WorkflowEnt workflow = ws().getWorkflow(wfId).getWorkflow();
        cr(workflow, "workflowent_root_pasted");

        //paste the same parts into a sub-workflow
        ws().pasteWorkflowParts(wfId, partsId, 0, 0, "6");
        workflow = ws().getSubWorkflow(wfId, "6").getWorkflow();
        cr(workflow, "workflowent_6_pasted");

        //try pasting parts with none-existing id
        try {
            ws().pasteWorkflowParts(wfId, UUID.randomUUID(), 0, 0, null);
            fail("Expected a ServiceException to be thrown");
        } catch (ServiceExceptions.NotFoundException e) {
            assertThat("Unexpected exception message", e.getMessage(),
                containsString("No workflow-part copy available for the given id"));
        }

        //try copying parts that don't exist
        //non-existing node
        parts = builder(WorkflowPartsEntBuilder.class)
                .setNodeIDs(asList("99"))
                .setParentNodeID("root").build();
        try {
            ws().createWorkflowCopy(wfId, parts);
        } catch (ServiceExceptions.InvalidRequestException e) {
            assertThat("Unexpected exception message", e.getMessage(),
                containsString("Failed to copy parts: No such node ID"));
        }
        //non-existing annotation
        parts =
            builder(WorkflowPartsEntBuilder.class)
            .setAnnotationIDs(asList("root_9")).setParentNodeID("root").build();
        try {
            ws().createWorkflowCopy(wfId, parts);
        } catch (ServiceExceptions.InvalidRequestException e) {
            assertThat("Unexpected exception message", e.getMessage(),
                containsString("Failed to copy parts: No annotation with ID"));
        }
    }

    private static int[] calcMinOffset(final WorkflowPartsEnt parts, final WorkflowEnt workflow) {
        List<BoundsEnt> bounds = new ArrayList<BoundsEnt>();
        parts.getNodeIDs().stream().map(id -> workflow.getNodes().get(id).getUIInfo().getBounds())
            .forEach(bounds::add);
        parts.getAnnotationIDs().stream().map(id -> workflow.getWorkflowAnnotations().get(id).getBounds())
            .forEach(bounds::add);

        Optional<Integer> minX = bounds.stream().map(b -> b.getX()).min((x, y) -> Integer.compare(x, y));
        Optional<Integer> minY = bounds.stream().map(b -> b.getY()).min((x, y) -> Integer.compare(x, y));
        return new int[]{minX.get(), minY.get()};
    }
}

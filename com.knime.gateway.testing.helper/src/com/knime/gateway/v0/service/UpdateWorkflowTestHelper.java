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

import static org.hamcrest.core.StringContains.containsString;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import java.util.UUID;

import com.knime.gateway.testing.helper.ResultChecker;
import com.knime.gateway.testing.helper.ServiceProvider;
import com.knime.gateway.testing.helper.WorkflowExecutor;
import com.knime.gateway.testing.helper.WorkflowLoader;
import com.knime.gateway.v0.entity.PatchEnt;
import com.knime.gateway.v0.entity.WorkflowSnapshotEnt;
import com.knime.gateway.v0.service.util.ServiceExceptions;

/**
 * Tests updating a workflow, i.e. downloading the workflow diff (patch) and applying it.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
public class UpdateWorkflowTestHelper extends AbstractGatewayServiceTestHelper {

    /**
     * See
     * {@link AbstractGatewayServiceTestHelper#AbstractGatewayServiceTestHelper(String, WorkflowLoader, ServiceProvider, ResultChecker)}.
     *
     * @param workflowLoader
     * @param workflowExecutor
     * @param serviceProvider
     * @param entityResultChecker
     */
    public UpdateWorkflowTestHelper(final ServiceProvider serviceProvider, final ResultChecker entityResultChecker,
        final WorkflowLoader workflowLoader, final WorkflowExecutor workflowExecutor) {
        super("updateworkflow", serviceProvider, entityResultChecker, workflowLoader, workflowExecutor);
    }

    /**
     * Tests updating a workflow, i.e. downloading the workflow diff (patch) and applying it.
     *
     * @throws Exception if an error occurs
     */
    public void testUpdatebWorkflow() throws Exception {
    	UUID wfId = loadWorkflow(TestWorkflow.WORKFLOW);

        //download workflows un-executed
        WorkflowSnapshotEnt workflow = ws().getWorkflow(wfId);
        WorkflowSnapshotEnt subWorkflow = ws().getSubWorkflow(wfId, "12");

        executeWorkflow(wfId);

        //get workflow diff/patch, apply and check result
        PatchEnt workflowPatch =
            ws().getWorkflowDiff(wfId, workflow.getSnapshotID());
        cr(workflowPatch, "patchent_root_executed");



        //try updating a sub-workflow
        PatchEnt subWorkflowPatch =
            ws().getSubWorkflowDiff(wfId, "12", subWorkflow.getSnapshotID());
        cr(subWorkflowPatch, "patchent_12");

        // check the executor exceptions
        try {
            ws().getWorkflowDiff(wfId, UUID.randomUUID());
            fail("Expected a ServiceException to be thrown");
        } catch (ServiceExceptions.NotFoundException e) {
            assertThat("Unexpected exception message", e.getMessage(),
                containsString("No workflow found for snapshot with ID"));
        }

        try {
            ws().getSubWorkflowDiff(wfId, "12", UUID.randomUUID());
            fail("Expected a ServiceException to be thrown");
        } catch (ServiceExceptions.NotFoundException e) {
            assertThat("Unexpected exception message", e.getMessage(),
                containsString("No workflow found for snapshot with ID"));
        }

        try {
            ws().getWorkflowDiff(wfId, null);
            fail("Expected a ServiceException to be thrown");
        } catch (ServiceExceptions.NotFoundException e) {
            assertThat("Unexpected exception message", e.getMessage(), containsString("No snapshot id given!"));
        }
    }
}
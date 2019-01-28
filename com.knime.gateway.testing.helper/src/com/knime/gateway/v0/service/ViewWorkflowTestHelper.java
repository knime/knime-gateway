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

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.StringContains.containsString;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import java.util.UUID;

import com.knime.gateway.testing.helper.ResultChecker;
import com.knime.gateway.testing.helper.ServiceProvider;
import com.knime.gateway.testing.helper.WorkflowExecutor;
import com.knime.gateway.testing.helper.WorkflowLoader;
import com.knime.gateway.util.EntityUtil;
import com.knime.gateway.v0.entity.DataTableEnt;
import com.knime.gateway.v0.service.util.ServiceExceptions;
import com.knime.gateway.v0.service.util.ServiceExceptions.InvalidRequestException;

/**
 * Tests to get entities (such as nodes, workflows, settings etc.) via the job gateway. I.e. it mainly tests the
 * parts required for 'just' viewing a workflow via the gateway and related things (settings etc.).
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
public class ViewWorkflowTestHelper extends AbstractGatewayServiceTestHelper {

    /**
     * See
     * {@link AbstractGatewayServiceTestHelper#AbstractGatewayServiceTestHelper(String, WorkflowLoader, ServiceProvider, ResultChecker)}.
     *
     * @param workflowLoader
     * @param workflowExecutor
     * @param serviceProvider
     * @param entityResultChecker
     */
    public ViewWorkflowTestHelper(final ServiceProvider serviceProvider, final ResultChecker entityResultChecker,
        final WorkflowLoader workflowLoader, final WorkflowExecutor workflowExecutor) {
        super("viewworkflow", serviceProvider, entityResultChecker, workflowLoader, workflowExecutor);
    }

    /**
     * Tests to download all available parts a job workflow via the gateway API, i.e. nodes, (sub)workflows, port object
     * spec, flow variables, node settings etc.
     *
     * @throws Exception if an error occurs
     */
    public void testViewWorkflow() throws Exception {
    	UUID wfId = loadWorkflow(TestWorkflow.WORKFLOW);

        //download and check workflow un-executed
        //retrieve the workflow entity
        Object entity = ns().getRootNode(wfId);
        cr(entity, "workflownodeent_root");
        //this should be the same as above (root worklfow entity retrieved via "root" node id)
        entity = ns().getNode(wfId, EntityUtil.ROOT_NODE_ID);
        cr(entity, "workflownodeent_root");
        //retrieve the workflow
        entity = ws().getWorkflow(wfId).getWorkflow();
        cr(entity, "workflowent_root");
        //retrieve the workflow via the "root" node id (should return the same as above)
        entity = ws().getSubWorkflow(wfId, EntityUtil.ROOT_NODE_ID)
            .getWorkflow();
        cr(entity, "workflowent_root");

        //execute workflow
        executeWorkflow(wfId);

        //download and check executed workflow
        entity = ws().getWorkflow(wfId).getWorkflow();
        cr(entity, "workflowent_root_executed");

        //download and check contained metanode '6'
        entity = ws().getSubWorkflow(wfId, "6").getWorkflow();
        cr(entity, "workflowent_6");

        //download and check contained wrapped metanode '9'
        entity = ws().getSubWorkflow(wfId, "9").getWorkflow();
        cr(entity, "workflowent_9");

        //download and check contained wrapped metanode '12'
        entity = ws().getSubWorkflow(wfId, "12").getWorkflow();
        cr(entity, "workflowent_12");

        //download and check the native node '2'
        entity = ns().getNode(wfId, "2");
        cr(entity, "nodeent_2");

        //access a node within a metanode '6:3'
        entity = ns().getNode(wfId, "6:3");
        cr(entity, "nodeent_6_3");

        //download and check wrapped metanode '9'
        entity = ns().getNode(wfId, "9");
        cr(entity, "wrappedworkflownodeent_9");

        //wrapped metanode 23
        entity = ns().getNode(wfId, "23");
        cr(entity, "wrappedworkflownodeent_23");

        //try to download a workflow for an existing node that doesn't represent a workflow (i.e. an ordinary node)
        try {
            ws().getSubWorkflow(wfId, "1");
            fail("Expected a NotASubWorkflowException to be thrown");
        } catch (ServiceExceptions.NotASubWorkflowException e) {
            assertThat("Unexpected exception message", e.getMessage(),
                containsString("is neither a metanode nor a wrapped metanode"));
        }

        //try to download a sub-workflow for a node that doesn't exist
        try {
            ws().getSubWorkflow(wfId, "99");
            fail("Expected a NodeNotFoundException to be thrown");
        } catch (ServiceExceptions.NodeNotFoundException e) {
            assertThat("Unexpected exception message", e.getMessage(), containsString("No such node"));
        }

        //try to download a node for an id that doesn't exist
        try {
            ns().getNode(wfId, "99");
            fail("Expected a ServiceException to be thrown");
        } catch (ServiceExceptions.NodeNotFoundException e) {
            assertThat("Unexpected exception message", e.getMessage(), containsString("No such node"));
        }

        /* Test node settings endpoint */

        //download and check the node settings for a node
        String jsonString = ns().getNodeSettings(wfId, "10").getJsonContent();
        cr(jsonString, "nodesettings_10");

        //try get settings for a node that doesn't exist
        try {
            ns().getNodeSettings(wfId, "99");
            fail("Expected a ServiceException to be thrown");
        } catch (ServiceExceptions.NodeNotFoundException e) {
            assertThat("Unexpected exception message", e.getMessage(), containsString("No such node"));
        }

        /* Test flow variables endpoint */

        //test downloading the input flow variables for a node
        entity = ns().getInputFlowVariables(wfId, "18");
        cr(entity, "inputflowvariablesent_18");

        //test downloading the output flow variables for a node
        entity = ns().getOutputFlowVariables(wfId, "18");
        cr(entity, "outputflowvariablesent_18");

        /* Test input port object spec endpoint */

        //test downloading the port object specs for a node
        //(including different port types, i.e. data table, flow variables and simple port object specs)
        entity = ns().getInputPortSpecs(wfId, "20");
        cr(entity, "inportspecsent_20");

        entity = ns().getInputPortSpecs(wfId, "18");
        cr(entity, "inportspecsent_18");

        //test for a node contained in a subworkflow
        entity = ns().getInputPortSpecs(wfId, "6:3");
        cr(entity, "inportspecsent_6_3");

        //test a inactive port
        entity = ns().getInputPortSpecs(wfId, "14");
        cr(entity, "inportspecsent_14");

        //test optional port without connection
        entity = ns().getInputPortSpecs(wfId, "21");
        cr(entity, "inportspecsent_21");

        //test a 'generic' (i.e. not a simple) port object spec
        entity = ns().getInputPortSpecs(wfId, "22");
        cr(entity, "inportspecsent_22");

        //test another 'generic' (i.e. not a simple) port object spec
        entity = ns().getInputPortSpecs(wfId, "26");
        cr(entity, "inportspecsent_26");


        /* Test output port object spec endpoint */

        //test data table spec
        entity = ns().getOutputPortSpecs(wfId, "1");
        cr(entity, "outportspecsent_1");

        //test a simple port object spec
        entity = ns().getOutputPortSpecs(wfId, "19");
        cr(entity, "outportspecsent_19");

        //outspec of a node that is not configured (outspec is null then)
        entity = ns().getOutputPortSpecs(wfId, "5");
        cr(entity, "outportspecsent_5");

        //test a unsupported port object spec
        entity = ns().getOutputPortSpecs(wfId, "10");
        cr(entity, "outportspecsent_10");

        /* Try getting the output table */
        //at port 1
        entity = ns().getOutputDataTable(wfId, "1", 1, 2l, 4);
        cr(entity, "outportdataent_1_1");

        //at port 2
        entity = ns().getOutputDataTable(wfId, "1", 2, 2l, 10);
        cr(entity, "outportdataent_1_2");

        //call without 'from' and 'size' parameters - entire table is expected
        DataTableEnt tableEnt = ns().getOutputDataTable(wfId, "1", 1, null, null);
        assertThat("Unexpected number of rows", tableEnt.getRows().size(), is(8));

        //check exceptions
        try {
            ns().getOutputDataTable(wfId, "10", 1, 0l, 10);
            fail("Expected a ServiceException to be thrown");
        } catch (InvalidRequestException e) {
            assertThat("Unexpected exception message", e.getMessage(), containsString("Not a table at port index"));
        }

        try {
            ns().getOutputDataTable(wfId, "10", 2, 0l, 10);
            fail("Expected a ServiceException to be thrown");
        } catch (InvalidRequestException e) {
            assertThat("Unexpected exception message", e.getMessage(), containsString("No port at index"));
        }
    }
}

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

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.StringContains.containsString;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.knime.gateway.api.entity.NodeIDEnt.getRootID;

import java.util.UUID;

import org.knime.gateway.api.entity.DataTableEnt;
import org.knime.gateway.api.entity.NodeIDEnt;
import org.knime.gateway.api.service.util.ServiceExceptions;
import org.knime.gateway.api.service.util.ServiceExceptions.InvalidRequestException;
import org.knime.gateway.testing.helper.ResultChecker;
import org.knime.gateway.testing.helper.ServiceProvider;
import org.knime.gateway.testing.helper.WorkflowExecutor;
import org.knime.gateway.testing.helper.WorkflowLoader;

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
    	UUID wfId = loadWorkflow(TestWorkflow.GENERAL);

        //download and check workflow un-executed
        //retrieve the workflow entity
        Object entity = ns().getNode(wfId, getRootID());
        cr(entity, "workflownodeent_root");
        //retrieve the workflow
        entity = ws().getWorkflow(wfId, getRootID()).getWorkflow();
        cr(entity, "workflowent_root");

        //execute workflow
        executeWorkflow(wfId);

        //download and check executed workflow
        entity = ws().getWorkflow(wfId, getRootID()).getWorkflow();
        cr(entity, "workflowent_root_executed");

        //download and check contained metanode '6'
        entity = ws().getWorkflow(wfId, new NodeIDEnt(6)).getWorkflow();
        cr(entity, "workflowent_6");

        //download and check contained component '9'
        entity = ws().getWorkflow(wfId, new NodeIDEnt(9)).getWorkflow();
        cr(entity, "workflowent_9");

        //download and check contained component '12'
        entity = ws().getWorkflow(wfId, new NodeIDEnt(12)).getWorkflow();
        cr(entity, "workflowent_12");

        //download and check the native node '2'
        entity = ns().getNode(wfId, new NodeIDEnt(2));
        cr(entity, "nodeent_2");

        //access a node within a metanode '6:3'
        entity = ns().getNode(wfId, new NodeIDEnt(6, 3));
        cr(entity, "nodeent_6_3");

        //download and check component '9'
        entity = ns().getNode(wfId, new NodeIDEnt(9));
        cr(entity, "wrappedworkflownodeent_9");

        //component 23
        entity = ns().getNode(wfId, new NodeIDEnt(23));
        cr(entity, "wrappedworkflownodeent_23");

        //try to download a workflow for an existing node that doesn't represent a workflow (i.e. an ordinary node)
        try {
            ws().getWorkflow(wfId, new NodeIDEnt(1));
            fail("Expected a NotASubWorkflowException to be thrown");
        } catch (ServiceExceptions.NotASubWorkflowException e) {
            assertThat("Unexpected exception message", e.getMessage(),
                containsString("is neither a metanode nor a component"));
        }

        //try to download a sub-workflow for a node that doesn't exist
        try {
            ws().getWorkflow(wfId, new NodeIDEnt(99));
            fail("Expected a NodeNotFoundException to be thrown");
        } catch (ServiceExceptions.NodeNotFoundException e) {
            assertThat("Unexpected exception message", e.getMessage(), containsString("No such node"));
        }

        //try to download a node for an id that doesn't exist
        try {
            ns().getNode(wfId, new NodeIDEnt(99));
            fail("Expected a ServiceException to be thrown");
        } catch (ServiceExceptions.NodeNotFoundException e) {
            assertThat("Unexpected exception message", e.getMessage(), containsString("No such node"));
        }

        /* Test node settings endpoint */

        //download and check the node settings for a node
        String jsonString = ns().getNodeSettings(wfId, new NodeIDEnt(10)).getJsonContent();
        cr(jsonString, "nodesettings_10");

        //try get settings for a node that doesn't exist
        try {
            ns().getNodeSettings(wfId, new NodeIDEnt(99));
            fail("Expected a ServiceException to be thrown");
        } catch (ServiceExceptions.NodeNotFoundException e) {
            assertThat("Unexpected exception message", e.getMessage(), containsString("No such node"));
        }

        /* Test flow variables endpoint */

        //test downloading the input flow variables for a node
        entity = ns().getInputFlowVariables(wfId, new NodeIDEnt(18));
        cr(entity, "inputflowvariablesent_18");

        //test downloading the output flow variables for a node
        entity = ns().getOutputFlowVariables(wfId, new NodeIDEnt(18));
        cr(entity, "outputflowvariablesent_18");

        /* Test input port object spec endpoint */

        //test downloading the port object specs for a node
        //(including different port types, i.e. data table, flow variables and simple port object specs)
        entity = ns().getInputPortSpecs(wfId, new NodeIDEnt(20));
        cr(entity, "inportspecsent_20");

        entity = ns().getInputPortSpecs(wfId, new NodeIDEnt(18));
        cr(entity, "inportspecsent_18");

        //test for a node contained in a subworkflow
        entity = ns().getInputPortSpecs(wfId, new NodeIDEnt(6, 3));
        cr(entity, "inportspecsent_6_3");

        //test a inactive port
        entity = ns().getInputPortSpecs(wfId, new NodeIDEnt(14));
        cr(entity, "inportspecsent_14");

        //test optional port without connection
        entity = ns().getInputPortSpecs(wfId, new NodeIDEnt(21));
        cr(entity, "inportspecsent_21");

        //test a 'generic' (i.e. not a simple) port object spec
        entity = ns().getInputPortSpecs(wfId, new NodeIDEnt(22));
        cr(entity, "inportspecsent_22");

        //test another 'generic' (i.e. not a simple) port object spec
        entity = ns().getInputPortSpecs(wfId, new NodeIDEnt(26));
        cr(entity, "inportspecsent_26");


        /* Test output port object spec endpoint */

        //test data table spec
        entity = ns().getOutputPortSpecs(wfId, new NodeIDEnt(1));
        cr(entity, "outportspecsent_1");

        //test a simple port object spec
        entity = ns().getOutputPortSpecs(wfId, new NodeIDEnt(19));
        cr(entity, "outportspecsent_19");

        //outspec of a node that is not configured (outspec is null then)
        entity = ns().getOutputPortSpecs(wfId, new NodeIDEnt(5));
        cr(entity, "outportspecsent_5");

        //test a unsupported port object spec
        entity = ns().getOutputPortSpecs(wfId, new NodeIDEnt(10));
        cr(entity, "outportspecsent_10");

        /* Try getting the output table */
        //at port 1
        entity = ns().getOutputDataTable(wfId, new NodeIDEnt(1), 1, 2l, 4);
        cr(entity, "outportdataent_1_1");

        //at port 2
        entity = ns().getOutputDataTable(wfId, new NodeIDEnt(1), 2, 2l, 10);
        cr(entity, "outportdataent_1_2");

        //call without 'from' and 'size' parameters - entire table is expected
        DataTableEnt tableEnt = ns().getOutputDataTable(wfId, new NodeIDEnt(1), 1, null, null);
        assertThat("Unexpected number of rows", tableEnt.getRows().size(), is(8));

        //check exceptions
        try {
            ns().getOutputDataTable(wfId, new NodeIDEnt(10), 1, 0l, 10);
            fail("Expected a ServiceException to be thrown");
        } catch (InvalidRequestException e) {
            assertThat("Unexpected exception message", e.getMessage(), containsString("Not a table at port index"));
        }

        try {
            ns().getOutputDataTable(wfId, new NodeIDEnt(10), 2, 0l, 10);
            fail("Expected a ServiceException to be thrown");
        } catch (InvalidRequestException e) {
            assertThat("Unexpected exception message", e.getMessage(), containsString("No port at index"));
        }
    }
}

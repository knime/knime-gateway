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
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.StringContains.containsString;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.UUID;

import com.knime.gateway.entity.ConnectionEnt;
import com.knime.gateway.entity.ConnectionEnt.ConnectionEntBuilder;
import com.knime.gateway.entity.ConnectionEnt.TypeEnum;
import com.knime.gateway.entity.ConnectionIDEnt;
import com.knime.gateway.entity.NodeIDEnt;
import com.knime.gateway.entity.WorkflowEnt;
import com.knime.gateway.entity.WorkflowPartsEnt;
import com.knime.gateway.entity.WorkflowPartsEnt.WorkflowPartsEntBuilder;
import com.knime.gateway.service.util.ServiceExceptions;
import com.knime.gateway.testing.helper.ResultChecker;
import com.knime.gateway.testing.helper.ServiceProvider;
import com.knime.gateway.testing.helper.WorkflowLoader;

/**
 * Tests to add parts to a job workflow, i.e. connections, nodes and workflow annotations.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
public class AddPartsTestHelper extends AbstractGatewayServiceTestHelper {

    /**
     * See
     * {@link AbstractGatewayServiceTestHelper#AbstractGatewayServiceTestHelper(String, WorkflowLoader, ServiceProvider, ResultChecker)}.
     *
     * @param workflowLoader
     * @param serviceProvider
     * @param entityResultChecker
     */
    public AddPartsTestHelper(final ServiceProvider serviceProvider, final ResultChecker entityResultChecker,
        final WorkflowLoader workflowLoader) {
        super("addparts", serviceProvider, entityResultChecker, workflowLoader);
    }

    /**
     * Tests adding connections to a workflow.
     *
     * @throws Exception if an error occurs
     *
     */
    public void testAddConnections() throws Exception {
    	UUID wfId = loadWorkflow(TestWorkflow.WORKFLOW);

        ConnectionEnt connection = builder(ConnectionEntBuilder.class).setDest(new NodeIDEnt(21)).setDestPort(2)
            .setSource(new NodeIDEnt(1)).setSourcePort(1).setType(TypeEnum.STD).build();
        //returns connection id as json (i.e. a string in quotation marks)
        ConnectionIDEnt connId = ws().createConnection(wfId, connection);
        assertThat("Unexpected connection id", connId, is(new ConnectionIDEnt(new NodeIDEnt(21), 2)));
        WorkflowEnt workflow = ws().getWorkflow(wfId, getRootID()).getWorkflow();
        cr(workflow, "workflowent_root_connectionadded");

        //try adding a connection that already exists (nothing should happen)
        ws().createConnection(wfId, connection);
        workflow = ws().getWorkflow(wfId, getRootID()).getWorkflow();
        cr(workflow, "workflowent_root_connectionadded");

        //replace an existing connection (different source, but same dest)
        connection = builder(ConnectionEntBuilder.class).setSource(new NodeIDEnt(1)).setSourcePort(1)
            .setDest(new NodeIDEnt(5)).setDestPort(1).setType(TypeEnum.STD).build();
        ws().createConnection(wfId, connection);
        workflow = ws().getWorkflow(wfId, getRootID()).getWorkflow();
        cr(workflow, "workflowent_root_connectionreplaced");

        //add flow variable connection
        connection = builder(ConnectionEntBuilder.class).setSource(new NodeIDEnt(1)).setSourcePort(0)
            .setDest(new NodeIDEnt(14)).setDestPort(1).setType(TypeEnum.STD).build();
        ws().createConnection(wfId, connection);
        workflow = ws().getWorkflow(wfId, getRootID()).getWorkflow();
        cr(workflow, "workflowent_root_flowvariableconnection");

        //try adding a connection to a node that doesn't exist
        connection = builder(ConnectionEntBuilder.class).setSource(new NodeIDEnt(99)).setSourcePort(0)
            .setDest(new NodeIDEnt(14)).setDestPort(1).setType(TypeEnum.STD).build();
        try {
            ws().createConnection(wfId, connection);
            fail("Expected a ServiceException to be thrown");
        } catch (ServiceExceptions.ActionNotAllowedException e) {
            assertThat("Unexpected exception message", e.getMessage(), containsString("Not allowed"));
        }

        //try connecting incompatible ports
        connection = builder(ConnectionEntBuilder.class).setSource(new NodeIDEnt(0)).setSourcePort(1)
            .setDest(new NodeIDEnt(16)).setDestPort(1).setType(TypeEnum.STD).build();
        try {
            ws().createConnection(wfId, connection);
            fail("Expected a ServiceException to be thrown");
        } catch (ServiceExceptions.ActionNotAllowedException e) {
            assertThat("Unexpected exception message", e.getMessage(), containsString("Not allowed"));
        }

        //add a connection within in a metanode (from the metanode in-port)
        //check sub-workflow before removal
        cr(ws().getWorkflow(wfId, new NodeIDEnt(6)).getWorkflow(), "workflowent_6");
        //delete connection
        WorkflowPartsEnt parts =
            builder(WorkflowPartsEntBuilder.class)
                .setConnectionIDs(Arrays.asList(new ConnectionIDEnt(new NodeIDEnt(6), 1))).build();
        ws().deleteWorkflowParts(wfId, new NodeIDEnt(6), parts, false);
        connection = builder(ConnectionEntBuilder.class).setSource(new NodeIDEnt(6, 3)).setSourcePort(2)
            .setDest(new NodeIDEnt(6)).setDestPort(1).setType(TypeEnum.WFMOUT).build();
        ws().createConnection(wfId, connection);
        //check
        cr(ws().getWorkflow(wfId, new NodeIDEnt(6)).getWorkflow(), "workflowent_6");

        //add a connection in a component (to the metanode out-port)
        connection = builder(ConnectionEntBuilder.class).setSource(new NodeIDEnt(12, 0, 10)).setSourcePort(1)
            .setDest(new NodeIDEnt(12, 0, 9)).setDestPort(1).setType(TypeEnum.STD).build();
        ws().createConnection(wfId, connection);
        workflow = ws().getWorkflow(wfId, new NodeIDEnt(12)).getWorkflow();
        cr(workflow, "workflowent_12_connectionadded");
    }
}

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
import static org.knime.gateway.api.entity.EntityBuilderManager.builder;
import static org.knime.gateway.api.entity.NodeIDEnt.getRootID;

import java.util.Arrays;
import java.util.UUID;

import org.knime.gateway.api.entity.ConnectionEnt;
import org.knime.gateway.api.entity.ConnectionEnt.ConnectionEntBuilder;
import org.knime.gateway.api.entity.ConnectionEnt.TypeEnum;
import org.knime.gateway.api.entity.ConnectionIDEnt;
import org.knime.gateway.api.entity.NodeIDEnt;
import org.knime.gateway.api.entity.WorkflowEnt;
import org.knime.gateway.api.entity.WorkflowPartsEnt;
import org.knime.gateway.api.entity.WorkflowPartsEnt.WorkflowPartsEntBuilder;
import org.knime.gateway.api.service.util.ServiceExceptions;
import org.knime.gateway.testing.helper.ResultChecker;
import org.knime.gateway.testing.helper.ServiceProvider;
import org.knime.gateway.testing.helper.WorkflowLoader;

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
    	UUID wfId = loadWorkflow(TestWorkflow.GENERAL);

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

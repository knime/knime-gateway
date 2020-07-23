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

import static org.hamcrest.core.StringContains.containsString;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.knime.gateway.api.entity.NodeIDEnt.getRootID;

import java.util.UUID;

import org.knime.gateway.api.entity.NodeIDEnt;
import org.knime.gateway.api.entity.PatchEnt;
import org.knime.gateway.api.entity.WorkflowSnapshotEnt;
import org.knime.gateway.api.service.util.ServiceExceptions;
import org.knime.gateway.testing.helper.ResultChecker;
import org.knime.gateway.testing.helper.ServiceProvider;
import org.knime.gateway.testing.helper.WorkflowExecutor;
import org.knime.gateway.testing.helper.WorkflowLoader;

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
    	UUID wfId = loadWorkflow(TestWorkflow.GENERAL);

        //download workflows un-executed
        WorkflowSnapshotEnt workflow = ws().getWorkflow(wfId, getRootID());
        WorkflowSnapshotEnt subWorkflow = ws().getWorkflow(wfId, new NodeIDEnt(12));

        executeWorkflow(wfId);

        //get workflow diff/patch, apply and check result
        PatchEnt workflowPatch =
            ws().getWorkflowDiff(wfId, getRootID(), workflow.getSnapshotID());
        cr(workflowPatch, "patchent_root_executed");



        //try updating a sub-workflow
        PatchEnt subWorkflowPatch =
            ws().getWorkflowDiff(wfId, new NodeIDEnt(12), subWorkflow.getSnapshotID());
        cr(subWorkflowPatch, "patchent_12");

        // check the executor exceptions
        try {
            ws().getWorkflowDiff(wfId, getRootID(), UUID.randomUUID());
            fail("Expected a ServiceException to be thrown");
        } catch (ServiceExceptions.NotFoundException e) {
            assertThat("Unexpected exception message", e.getMessage(),
                containsString("No workflow found for snapshot with ID"));
        }

        try {
            ws().getWorkflowDiff(wfId, new NodeIDEnt(12), UUID.randomUUID());
            fail("Expected a ServiceException to be thrown");
        } catch (ServiceExceptions.NotFoundException e) {
            assertThat("Unexpected exception message", e.getMessage(),
                containsString("No workflow found for snapshot with ID"));
        }

        try {
            ws().getWorkflowDiff(wfId, getRootID(), null);
            fail("Expected a ServiceException to be thrown");
        } catch (ServiceExceptions.NotFoundException e) {
            assertThat("Unexpected exception message", e.getMessage(), containsString("No snapshot id given!"));
        }
    }
}

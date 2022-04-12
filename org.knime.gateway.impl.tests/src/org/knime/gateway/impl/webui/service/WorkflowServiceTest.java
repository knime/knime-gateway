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
 * History
 *   Mar 11, 2021 (hornm): created
 */
package org.knime.gateway.impl.webui.service;

import static org.junit.Assert.assertFalse;
import static org.knime.gateway.api.entity.NodeIDEnt.getRootID;

import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.knime.core.node.workflow.NodeUIInformation;
import org.knime.core.node.workflow.WorkflowManager;
import org.knime.gateway.api.webui.entity.WorkflowSnapshotEnt;
import org.knime.gateway.impl.project.WorkflowProjectManager;
import org.knime.gateway.impl.webui.WorkflowMiddleware;
import org.knime.gateway.testing.helper.TestWorkflowCollection;
import org.knime.gateway.testing.helper.webui.WorkflowServiceTestHelper;

/**
 * Test that can only be carried out directly on a {@link DefaultWorkflowService}instance (and can't be tested via the
 * {@link WorkflowServiceTestHelper}).
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
public class WorkflowServiceTest extends GatewayServiceTest {

    /**
     * Tests that returned workflow entities are NOT cached (and a different instance is returned) if the workflow
     * didn't change.
     *
     * (This test might seem a bit unnecessary. However, the once implemented caching caused some problems (see NXT-866)
     * that's why we now make sure it's not cached.)
     *
     * @throws Exception
     */
    @Test
    public void testThatWorkflowEntitiesAreNotCached() throws Exception {
        String wfId = "wf_id";
        WorkflowManager wfm = loadWorkflow(TestWorkflowCollection.GENERAL_WEB_UI, wfId);
        WorkflowSnapshotEnt ent = DefaultWorkflowService.getInstance().getWorkflow(wfId, getRootID(), false);
        WorkflowSnapshotEnt ent2 = DefaultWorkflowService.getInstance().getWorkflow(wfId, getRootID(), false);
        assertFalse(ent.getWorkflow() == ent2.getWorkflow());

        // change
        NodeUIInformation.moveNodeBy(wfm.getNodeContainers().iterator().next(), new int[]{10, 10});

        WorkflowSnapshotEnt ent3 = DefaultWorkflowService.getInstance().getWorkflow(wfId, getRootID(), false);
        assertFalse(ent2.getWorkflow() == ent3.getWorkflow());
    }

    /**
     * Checks the expected allowed action properties for nodes and components with a view of different kinds (contrasted
     * with nodes without a view). The main focus of this tests lies on the 'canOpenView'-property (and related).
     *
     * @throws Exception
     */
    @Test
    public void testAllowedNodeActionsForViewNodes() throws Exception { // NOSONAR 'cr' does the check
        var wfId = "wf_id";
        var wfm = loadWorkflow(TestWorkflowCollection.VIEW_NODES, "wf_id");
        var workflowService = DefaultWorkflowService.getInstance();

        var allowedActionsMap = workflowService.getWorkflow(wfId.toString(), getRootID(), true).getWorkflow().getNodes()
            .entrySet().stream().collect(Collectors.toMap(Entry::getKey, e -> e.getValue().getAllowedActions()));
        cr(allowedActionsMap, "allowed_actions_for_view_nodes");

        wfm.executeAllAndWaitUntilDone();

        var allowedActionsMapExecuted =
            workflowService.getWorkflow(wfId.toString(), getRootID(), true).getWorkflow().getNodes().entrySet().stream()
                .collect(Collectors.toMap(Entry::getKey, e -> e.getValue().getAllowedActions()));
        cr(allowedActionsMapExecuted, "allowed_actions_for_view_nodes_executed");
    }

    @SuppressWarnings("javadoc")
    @BeforeClass
    public static void setupServiceDependencies() {
        ServiceDependencies.setServiceDependency(WorkflowMiddleware.class,
            new WorkflowMiddleware(WorkflowProjectManager.getInstance()));
    }

    @SuppressWarnings("javadoc")
    @AfterClass
    public static void disposeServices() {
        ServiceInstances.disposeAllServicesInstancesAndDependencies();
    }

}

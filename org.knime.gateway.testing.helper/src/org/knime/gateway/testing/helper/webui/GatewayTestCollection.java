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
 */
package org.knime.gateway.testing.helper.webui;

import java.util.HashMap;
import java.util.Map;

/**
 * Gives programmatic access to all gateway test (helpers) that test the web-ui API services. Provided in that way such
 * that they can be re-used in different test settings (e.g. integration tests).
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
public final class GatewayTestCollection {

    private GatewayTestCollection() {
        //utility class
    }

    /**
     * Collects and initializes all available gateway tests.
     *
     * TODO could be automatically collected by annotating the respective classes and/or methods
     *
     * @return map from the individual test names to a function that allows one to run the test
     */
    public static Map<String, GatewayTestRunner> collectAllGatewayTests() {
        Map<String, GatewayTestRunner> res = new HashMap<>();
        res.put("testGetWorkflow",
            (rc, sp, wl, we, es) -> new WorkflowServiceTestHelper(rc, sp, wl, we).testGetWorkflow());
        res.put("testNodeExecutionStates",
            (rc, sp, wl, we, es) -> new WorkflowServiceTestHelper(rc, sp, wl, we).testNodeExecutionStates());
        res.put("testGetAllowedActionsInfo",
            (rc, sp, wl, we, es) -> new WorkflowServiceTestHelper(rc, sp, wl, we).testGetAllowedActionsInfo());
        res.put("testGetComponentProjectWorkflow",
            (rc, sp, wl, we, es) -> new WorkflowServiceTestHelper(rc, sp, wl, we).testGetComponentProjectWorkflow());
        res.put("testWorkflowAndComponentMetadata",
            (rc, sp, wl, we, es) -> new WorkflowServiceTestHelper(rc, sp, wl, we).testWorkflowAndComponentMetadata());
        res.put("testGetWorkflowWithAmbiguousPortTypes",
            (rc, sp, wl, we, es) -> new WorkflowServiceTestHelper(rc, sp, wl, we).testGetWorkflowWithAmbiguousPortTypes());
        res.put("testExecuteTranslateCommand",
            (rc, sp, wl, we, es) -> new WorkflowServiceTestHelper(rc, sp, wl, we).testExecuteTranslateCommand());
        res.put("testExecuteDeleteCommand",
            (rc, sp, wl, we, es) -> new WorkflowServiceTestHelper(rc, sp, wl, we).testExecuteDeleteCommand());
        res.put("testExecuteConnectCommand",
            (rc, sp, wl, we, es) -> new WorkflowServiceTestHelper(rc, sp, wl, we).testExecuteConnectCommand());
        res.put("testExecuteAddNodeCommand",
            (rc, sp, wl, we, es) -> new WorkflowServiceTestHelper(rc, sp, wl, we).testExecuteAddNodeCommand());

        res.put("testChangeNodeState",
            (rc, sp, wl, we, es) -> new NodeServiceTestHelper(rc, sp, wl, we).testChangeNodeState());
        res.put("testChangeNodeStateOfComponentProject",
            (rc, sp, wl, we, es) -> new NodeServiceTestHelper(rc, sp, wl, we).testChangeNodeStateOfComponentProject());
        res.put("testChangeNodeStateAllNodes",
            (rc, sp, wl, we, es) -> new NodeServiceTestHelper(rc, sp, wl, we).testChangeNodeStateAllNodes());
        res.put("testChangeLoopExecutionState",
            (rc, sp, wl, we, es) -> new NodeServiceTestHelper(rc, sp, wl, we).testChangeLoopExecutionState());
        res.put("testChangeLoopExecutionStateInSubWorkflow", (rc, sp, wl, we,
            es) -> new NodeServiceTestHelper(rc, sp, wl, we).testChangeLoopExecutionStateInSubWorkflow());
        res.put("testDoPortRpc", (rc, sp, wl, we, es) -> new NodeServiceTestHelper(rc, sp, wl, we).testDoPortRpc());

        res.put("testJobManagerProperty",
            (rc, sp, wl, we, es) -> new StreamingExecutionTestHelper(rc, sp, wl, we).testJobManagerProperty());
        res.put("testStreamedWorkflow",
            (rc, sp, wl, we, es) -> new StreamingExecutionTestHelper(rc, sp, wl, we).testStreamedWorkflow());

        res.put("testExecuteAndUndoDeleteCommandPatches", (rc, sp, wl, we,
            es) -> new EventServiceTestHelper(rc, sp, wl, we, es).testExecuteAndUndoDeleteCommandPatches());

        return res;
    }
}

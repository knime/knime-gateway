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
package com.knime.gateway.testing.helper;

import java.util.HashMap;
import java.util.Map;

import com.knime.gateway.service.AddNodeTestHelper;
import com.knime.gateway.service.AddPartsTestHelper;
import com.knime.gateway.service.ChangeNodeTestHelper;
import com.knime.gateway.service.ChangeWorkflowAnnotationTestHelper;
import com.knime.gateway.service.DeleteCutCopyPastePartsTestHelper;
import com.knime.gateway.service.JSViewTestHelper;
import com.knime.gateway.service.NodeDataTestHelper;
import com.knime.gateway.service.UpdateWorkflowTestHelper;
import com.knime.gateway.service.ViewWorkflowTestHelper;
import com.knime.gateway.service.WMetaNodeDialogTest;
import com.knime.gateway.service.WizardExecutionTestHelper;

/**
 * Gives programmatic access to all gateway test (helper) that mainly test the correct behavior of gateway services
 * (see, e.g., AbstractGatewayServiceTestHelper)
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
public class GatewayTestCollection {

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
        Map<String, GatewayTestRunner> res = new HashMap<String, GatewayTestRunner>();
        res.put("testAddNode",
            (sp, rc, wl, we) -> new AddNodeTestHelper(sp, rc, wl).testAddNode());
        res.put("testReplaceNode",
            (sp, rc, wl, we) -> new AddNodeTestHelper(sp, rc, wl).testReplaceNode());

        res.put("testAddConnections",
            (sp, rc, wl, we) -> new AddPartsTestHelper(sp, rc, wl).testAddConnections());

        res.put("testChangeNodeState",
            (sp, rc, wl, we) -> new ChangeNodeTestHelper(sp, rc, wl, we).testChangeNodeState());
        res.put("testChangeNodeSettings",
            (sp, rc, wl, we) -> new ChangeNodeTestHelper(sp, rc, wl, we).testChangeNodeSettings());
        res.put("testChangeNodeBounds",
            (sp, rc, wl, we) -> new ChangeNodeTestHelper(sp, rc, wl, we).testChangeNodeBounds());

        res.put("testChangeAnnotationBounds",
            (sp, rc, wl, we) -> new ChangeWorkflowAnnotationTestHelper(sp, rc, wl)
                .testChangeAnnotationBounds());

        res.put("testDeleteParts",
            (sp, rc, wl, we) -> new DeleteCutCopyPastePartsTestHelper(sp, rc, wl, we).testDeleteParts());
        res.put("testCopyPasteParts",
            (sp, rc, wl, we) -> new DeleteCutCopyPastePartsTestHelper(sp, rc, wl, we).testCopyPasteParts());

        res.put("testGetAndSetNodeViewsData",
            (sp, rc, wl, we) -> new JSViewTestHelper(sp, rc, wl, we).testGetAndSetNodeViewsData());

        res.put("testCompareNodeDataForDirectAccessTable",
            (sp, rc, wl, we) -> new NodeDataTestHelper(sp, rc, wl).testCompareNodeDataForDirectAccessTable());
        res.put("testCompareNodeDataForBufferedDataTable",
            (sp, rc, wl, we) -> new NodeDataTestHelper(sp, rc, wl).testCompareNodeDataForBufferedDataTable());
        res.put("testDirectAccessTableUnknownRowCount",
            (sp, rc, wl, we) -> new NodeDataTestHelper(sp, rc, wl).testDirectAccessTableUnknownRowCount());
        res.put("testDirectAccessTableExceedingTotalRowCount",
            (sp, rc, wl, we) -> new NodeDataTestHelper(sp, rc, wl).testDirectAccessTableExceedingTotalRowCount());

        res.put("testUpdateWorkflow",
            (sp, rc, wl, we) -> new UpdateWorkflowTestHelper(sp, rc, wl, we).testUpdatebWorkflow());

        res.put("testViewWorkflow",
            (sp, rc, wl, we) -> new ViewWorkflowTestHelper(sp, rc, wl, we).testViewWorkflow());

        res.put("testGetWMetaNodeDialog",
            (sp, rc, wl, we) -> new WMetaNodeDialogTest(sp, rc, wl).testGetWMetaNodeDialog());

        res.put("testGetCurrentPageAfterLoad",
            (sp, rc, wl, we) -> new WizardExecutionTestHelper(sp, rc, wl).testGetCurrentPageAfterLoad());
        res.put("testExecuteToFirstPage",
            (sp, rc, wl, we) -> new WizardExecutionTestHelper(sp, rc, wl).testExecuteToFirstPage());
        res.put("testExecuteToSecondPage",
            (sp, rc, wl, we) -> new WizardExecutionTestHelper(sp, rc, wl).testExecuteToSecondPage());
        res.put("testAsyncExecuteToNextPageAndGetCurrentPage", (sp, rc, wl,
            we) -> new WizardExecutionTestHelper(sp, rc, wl).testAsyncExecuteToNextPageAndGetCurrentPage());
        res.put("testExecuteToNextPageTimeout",
            (sp, rc, wl, we) -> new WizardExecutionTestHelper(sp, rc, wl).testExecuteToNextPageTimeout());
        res.put("testExecuteToSecondPageWithFailure",
            (sp, rc, wl, we) -> new WizardExecutionTestHelper(sp, rc, wl).testExecuteToSecondPageWithFailure());
        res.put("testGetCurrentPageWhileExecuting",
            (sp, rc, wl, we) -> new WizardExecutionTestHelper(sp, rc, wl).testGetCurrentPageWhileExecuting());
        res.put("testGetCurrentPageIfNotInWizardExecution",
            (sp, rc, wl, we) -> new WizardExecutionTestHelper(sp, rc, wl).testGetCurrentPageIfNotInWizardExecution());
        res.put("testExecuteToNextPageWithInvalidViewValues",
            (sp, rc, wl, we) -> new WizardExecutionTestHelper(sp, rc, wl).testExecuteToNextPageWithInvalidViewValues());
        res.put("testGetCurrentPageWhileReexecuting",
            (sp, rc, wl, we) -> new WizardExecutionTestHelper(sp, rc, wl).testGetCurrentPageWhileReexecuting());
        res.put("testResetToPreviousPageWhileReexecuting",
            (sp, rc, wl, we) -> new WizardExecutionTestHelper(sp, rc, wl).testResetToPreviousPageWhileReexecuting());
        res.put("testResetToZerothPage",
            (sp, rc, wl, we) -> new WizardExecutionTestHelper(sp, rc, wl).testResetToZerothPage());
        res.put("testResetToFirstPageAndExecuteToNextPageWithChangedInputs",
            (sp, rc, wl, we) -> new WizardExecutionTestHelper(sp, rc, wl)
                .testResetToFirstPageAndExecuteToNextPageWithChangedInputs());
        res.put("testResetToPreviousPageWhileWorkflowIsExecuting",
            (sp, rc, wl, we) -> new WizardExecutionTestHelper(sp, rc, wl)
                .testResetToPreviousPageWhileWorkflowIsExecuting());
        res.put("testExecuteToLastPageNode",
            (sp, rc, wl, we) -> new WizardExecutionTestHelper(sp, rc, wl).testExecuteToLastPageNode());
        res.put("testExecuteToLastPageNodeAndReport",
            (sp, rc, wl, we) -> new WizardExecutionTestHelper(sp, rc, wl).testExecuteToLastPageNodeAndReport());
        res.put("testExecuteToLastPageComponent",
            (sp, rc, wl, we) -> new WizardExecutionTestHelper(sp, rc, wl).testExecuteToLastPageComponent());
        res.put("testExecuteToLastPageComponentAndReport",
            (sp, rc, wl, we) -> new WizardExecutionTestHelper(sp, rc, wl).testExecuteToLastPageComponentAndReport());
        res.put("testExecuteToLastPageComponentAndFailureInParallelBranch",
            (sp, rc, wl, we) -> new WizardExecutionTestHelper(sp, rc, wl)
                .testExecuteToLastPageComponentAndFailureInParallelBranch());
        res.put("testListWebResources",
            (sp, rc, wl, we) -> new WizardExecutionTestHelper(sp, rc, wl).testListWebResources());
        res.put("testGetWebResource",
            (sp, rc, wl, we) -> new WizardExecutionTestHelper(sp, rc, wl).testGetWebResource());
        res.put("testGetWebResourceNotFound",
            (sp, rc, wl, we) -> new WizardExecutionTestHelper(sp, rc, wl).testGetWebResourceNotFound());
        res.put("testGetExecutionStatistics",
            (sp, rc, wl, we) -> new WizardExecutionTestHelper(sp, rc, wl).testGetExecutionStatistics());

        return res;
    }
}

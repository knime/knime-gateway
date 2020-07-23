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
package org.knime.gateway.testing.helper;

import java.util.HashMap;
import java.util.Map;

import org.knime.gateway.service.AddNodeTestHelper;
import org.knime.gateway.service.AddPartsTestHelper;
import org.knime.gateway.service.ChangeNodeTestHelper;
import org.knime.gateway.service.ChangeWorkflowAnnotationTestHelper;
import org.knime.gateway.service.DeleteCutCopyPastePartsTestHelper;
import org.knime.gateway.service.JSViewTestHelper;
import org.knime.gateway.service.NodeDataTestHelper;
import org.knime.gateway.service.UpdateWorkflowTestHelper;
import org.knime.gateway.service.ViewWorkflowTestHelper;
import org.knime.gateway.service.WMetaNodeDialogTest;
import org.knime.gateway.service.WizardExecutionTestHelper;

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

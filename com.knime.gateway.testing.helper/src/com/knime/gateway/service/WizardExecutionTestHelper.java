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

import static com.jayway.jsonpath.matchers.JsonPathMatchers.hasJsonPath;
import static com.knime.gateway.entity.EntityBuilderManager.builder;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.collection.IsEmptyCollection.empty;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsCollectionContaining.hasItems;
import static org.hamcrest.core.IsNot.not;
import static org.hamcrest.core.StringContains.containsString;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import com.knime.gateway.entity.WizardPageInputEnt;
import com.knime.gateway.entity.WizardPageInputEnt.WizardPageInputEntBuilder;
import com.knime.gateway.service.util.ServiceExceptions.InvalidSettingsException;
import com.knime.gateway.service.util.ServiceExceptions.NoWizardPageException;
import com.knime.gateway.service.util.ServiceExceptions.NotFoundException;
import com.knime.gateway.service.util.ServiceExceptions.TimeoutException;
import com.knime.gateway.testing.helper.ResultChecker;
import com.knime.gateway.testing.helper.ServiceProvider;
import com.knime.gateway.testing.helper.WorkflowLoader;

/**
 * Tests the wizard execution functionality of a workflow via the gateway API.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
public class WizardExecutionTestHelper extends AbstractGatewayServiceTestHelper {

    private static final long WF_EXECUTION_TIMEOUT = 600000;

	/**
     * See
     * {@link AbstractGatewayServiceTestHelper#AbstractGatewayServiceTestHelper(String, WorkflowLoader, ServiceProvider, ResultChecker)}.
     *
     * @param workflowLoader
     * @param serviceProvider
     * @param entityResultChecker
     */
    public WizardExecutionTestHelper(final ServiceProvider serviceProvider, final ResultChecker entityResultChecker,
        final WorkflowLoader workflowLoader) {
        super("wizardexecution", serviceProvider, entityResultChecker, workflowLoader);
    }

    /**
     * Checks if stepping to the first page works as expected.
     *
     * @throws Exception
     */
    public void testExecuteToFirstPage() throws Exception {
        UUID wfId = loadWorkflow(TestWorkflow.WORKFLOW_WIZARD_EXECUTION);
        byte[] pageContent = wes().executeToNextPage(wfId, false, WF_EXECUTION_TIMEOUT, emptyWizardPageInput());
        checkFirstPageContents(new String(pageContent));
    }

    /**
     * Checks if stepping to the second page works as expected.
     *
     * @throws Exception
     */
    public void testExecuteToSecondPage() throws Exception {
        UUID wfId = loadWorkflow(TestWorkflow.WORKFLOW_WIZARD_EXECUTION);
        wes().executeToNextPage(wfId, false, WF_EXECUTION_TIMEOUT, emptyWizardPageInput());
        int rowCount = (int)(5 * Math.random()) + 1;
        WizardPageInputEnt input = secondWizardPageInput(rowCount);
        byte[] pageContent = wes().executeToNextPage(wfId, false, WF_EXECUTION_TIMEOUT, input);
        checkSecondPageContents(new String(pageContent), rowCount);
    }

    /**
     * Checks to advance to the next page asynchronously and retrieve the content via 'current-page'.
     *
     * @throws Exception
     */
    public void testAsyncExecuteToNextPageAndGetCurrentPage() throws Exception {
        UUID wfId = loadWorkflow(TestWorkflow.WORKFLOW_WIZARD_EXECUTION);
        byte[] emptyPage = wes().executeToNextPage(wfId, true, WF_EXECUTION_TIMEOUT, emptyWizardPageInput());
        assertThat("Wizard page not empty", emptyPage, is(new byte[0]));
        //wait a bit for the wizard page to be available
        await().atMost(5, TimeUnit.SECONDS).pollInterval(100, TimeUnit.MILLISECONDS).untilAsserted(() -> {
            byte[] pageContent = wes().getCurrentPage(wfId);
            checkFirstPageContents(new String(pageContent));
        });
    }

    /**
     * Checks if stepping until the end with user input works as expected.
     *
     * @throws Exception
     */
    public void testExecuteToEnd() throws Exception {
        UUID wfId = loadWorkflow(TestWorkflow.WORKFLOW_WIZARD_EXECUTION);
        wes().executeToNextPage(wfId, false, WF_EXECUTION_TIMEOUT, emptyWizardPageInput());

        WizardPageInputEnt input = secondWizardPageInput((int)(5 * Math.random()) + 1);
        wes().executeToNextPage(wfId, false, WF_EXECUTION_TIMEOUT, input);

        try {
            wes().executeToNextPage(wfId, false, WF_EXECUTION_TIMEOUT, emptyWizardPageInput());
            fail("Exception expected");
        } catch (NoWizardPageException e) {
            assertThat("Unexpected exception message", e.getMessage(), is("No wizard page available"));
        }
    }

    /**
     * Checks that the correct exception is thrown when the passed view values for the next page are not valid (e.g.
     * exceeding the maximum value).
     *
     * @throws Exception
     */
    public void testExecuteToNextPageWithInvalidViewValues() throws Exception {
        UUID wfId = loadWorkflow(TestWorkflow.WORKFLOW_WIZARD_EXECUTION);
        wes().executeToNextPage(wfId, false, WF_EXECUTION_TIMEOUT, emptyWizardPageInput());

        // the the integer input parameter which controls a row filter
        Map<String, String> viewValues = Collections.singletonMap("5:0:1", "{\"integer\": " + 100 + "}");
        try {
            wes().executeToNextPage(wfId, false, WF_EXECUTION_TIMEOUT,
                builder(WizardPageInputEntBuilder.class).setViewValues(viewValues).build());
            fail("Expected exception");
        } catch (InvalidSettingsException e) {
            assertThat("Unexpected exception message", e.getMessage(),
                containsString("Validation of view parameters failed"));
        }
    }

    /**
     * Tests that the timeout works.
     *
     * @throws Exception
     */
    public void testExecuteToNextPageTimeout() throws Exception {
        UUID wfId = loadWorkflow(TestWorkflow.WORKFLOW_WIZARD_EXECUTION);
        try {
            wes().executeToNextPage(wfId, false, 1L, emptyWizardPageInput());
            fail("Exception expected");
        } catch (TimeoutException e) {
            assertThat("Unexpected exception message", e.getMessage(), is("Workflow didn't finish before timeout"));
        }
    }

    /**
     * Tests the {@link WizardExecutionService#getCurrentPage(UUID)} endpoint when no page is available.
     *
     * @throws Exception if an error occurs
     */
    public void testGetCurrentPageNoPage() throws Exception {
    	UUID wfId = loadWorkflow(TestWorkflow.WORKFLOW_WIZARD_EXECUTION);
    	try {
            wes().getCurrentPage(wfId);
            fail("Exception expected to be thrown");
        } catch (NoWizardPageException e) {
            assertThat("Unexpected exception method", e.getMessage(), is("No wizard page available"));
    	}
    }

    /**
     * Checks for the right exception thrown when execution is reseted to the zeroth page which doesn't exist.
     *
     * @throws Exception
     */
    public void testResetToZerothPage() throws Exception {
        UUID wfId = loadWorkflow(TestWorkflow.WORKFLOW_WIZARD_EXECUTION);
        wes().executeToNextPage(wfId, false, WF_EXECUTION_TIMEOUT, emptyWizardPageInput());

        try {
            wes().resetToPreviousPage(wfId);
            fail("Exception expected");
        } catch (NoWizardPageException e) {
            assertThat("Unexpected exception message", e.getMessage(), is("No previous wizard page"));
        }
    }

    /**
     * Executes to second page, resets to first page and re-executes to second page with changed inputs.
     *
     * @throws Exception
     */
    public void testResetToFirstPageAndExecuteToNextPageWithChangedInputs() throws Exception {
        UUID wfId = loadWorkflow(TestWorkflow.WORKFLOW_WIZARD_EXECUTION);
        wes().executeToNextPage(wfId, false, WF_EXECUTION_TIMEOUT, emptyWizardPageInput());
        int rowCount = (int)(5 * Math.random()) + 1;
        byte[] pageContent =
            wes().executeToNextPage(wfId, false, WF_EXECUTION_TIMEOUT, secondWizardPageInput(rowCount));
        checkSecondPageContents(new String(pageContent), rowCount);

        pageContent = wes().resetToPreviousPage(wfId);
        checkFirstPageContents(new String(pageContent));

        rowCount = (int)(5 * Math.random()) + 1;
        pageContent = wes().executeToNextPage(wfId, false, WF_EXECUTION_TIMEOUT, secondWizardPageInput(rowCount));
        checkSecondPageContents(new String(pageContent), rowCount);
    }

    private static void checkSecondPageContents(final String pageContents, final int expectedRowCount) {
        assertThat("Expected page element not found", pageContents,
            hasJsonPath("$.webNodes.9:0:7.nodeInfo.nodeName", is("Text Output")));
        assertThat("Unexpected output content", pageContents,
            hasJsonPath("$.webNodes.9:0:7.viewRepresentation.text", is(Integer.toString(expectedRowCount))));
    }

    private static void checkFirstPageContents(final String pageContents) {
        assertThat("Expected page element not found", pageContents,
            hasJsonPath("$.webNodePageConfiguration.layout.rows[*].columns[*].content", is(not(empty()))));
        assertThat("Expected page element not found", pageContents,
            hasJsonPath("$.webNodes.5:0:1.nodeInfo.nodeName", is("Integer Input")));
    }

    private static WizardPageInputEnt emptyWizardPageInput() {
        return builder(WizardPageInputEntBuilder.class).setViewValues(Collections.emptyMap()).build();
    }

    private static WizardPageInputEnt secondWizardPageInput(final int rowCount) {
        // the the integer input parameter which controls a row filter
        WizardPageInputEnt wizardPageInput = builder(WizardPageInputEntBuilder.class)
            .setViewValues(Collections.singletonMap("5:0:1", "{\"integer\": " + rowCount + "}")).build();
        return wizardPageInput;
    }

    /**
     * Checks if available web resources are retrieved correctly.
     *
     * @throws Exception if an error occurs
     */
    public void testListWebResources() throws Exception {
        UUID wfId = loadWorkflow(TestWorkflow.WORKFLOW_WIZARD_EXECUTION);

        List<String> resources = wes().listWebResources(wfId);

        String[] expectedResources = {
            "org/knime/js/base/node/quickform/selection/single/SingleSelection.js",
            "org/knime/js/base/node/quickform/input/listbox/ListBoxInput.js",
            "js-lib/jQueryUI/min/themes/base/jquery.ui.theme.min.css"};

        assertThat("No all expected resources found", resources, hasItems(expectedResources));
    }

    /**
     * Tests to request a single web resource via its id.
     *
     * @throws Exception
     */
    public void testGetWebResource() throws Exception {
        UUID wfId = loadWorkflow(TestWorkflow.WORKFLOW_WIZARD_EXECUTION);

        String webResource = new String(
            wes().getWebResource(wfId, "org/knime/js/base/node/quickform/selection/single/SingleSelection.js"));
        assertThat("Unexpected web resource", webResource, containsString("singleSelection.name"));
        assertThat("Unexpected web resource", webResource, containsString("return singleSelection"));
    }

    /**
     * Checks that exception is thrown when invalid resource id is passed.
     *
     * @throws Exception
     */
    public void testGetWebResourceNotFound() throws Exception {
        UUID wfId = loadWorkflow(TestWorkflow.WORKFLOW_WIZARD_EXECUTION);
        try {
            wes().getWebResource(wfId, "not_existing_resource_id");
            fail("Exception expected");
        } catch (NotFoundException e) {
            assertThat("Unexpected exception message", e.getMessage(), is("No resource for given id available"));
        }
    }
}

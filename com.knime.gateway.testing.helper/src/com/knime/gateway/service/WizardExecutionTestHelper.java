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

import static com.jayway.jsonassert.impl.matcher.IsEmptyCollection.empty;
import static com.jayway.jsonpath.matchers.JsonPathMatchers.hasJsonPath;
import static com.knime.gateway.entity.EntityBuilderManager.builder;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.lessThan;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.core.IsCollectionContaining.hasItem;
import static org.hamcrest.core.IsCollectionContaining.hasItems;
import static org.hamcrest.core.StringContains.containsString;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import com.fasterxml.jackson.databind.JsonNode;
import com.knime.gateway.entity.ConnectionEnt.ConnectionEntBuilder;
import com.knime.gateway.entity.ConnectionEnt.TypeEnum;
import com.knime.gateway.entity.ExecutionStatisticsEnt;
import com.knime.gateway.entity.NodeFactoryKeyEnt.NodeFactoryKeyEntBuilder;
import com.knime.gateway.entity.NodeIDEnt;
import com.knime.gateway.entity.NodeMessageEnt.NodeMessageEntBuilder;
import com.knime.gateway.entity.NodeSettingsEnt;
import com.knime.gateway.entity.NodeSettingsEnt.NodeSettingsEntBuilder;
import com.knime.gateway.entity.WizardPageEnt;
import com.knime.gateway.entity.WizardPageEnt.WizardExecutionStateEnum;
import com.knime.gateway.entity.WizardPageInputEnt;
import com.knime.gateway.entity.WizardPageInputEnt.WizardPageInputEntBuilder;
import com.knime.gateway.json.util.ObjectMapperUtil;
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
        UUID wfId = loadWorkflow(TestWorkflow.WIZARD_EXECUTION);
        WizardPageEnt wizardPage = wes().executeToNextPage(wfId, false, WF_EXECUTION_TIMEOUT, emptyWizardPageInput());
        checkFirstPageContents(wizardPage.getWizardPageContent());
        assertThat("unexpected wizard execution state", wizardPage.getWizardExecutionState(),
            is(WizardExecutionStateEnum.INTERACTION_REQUIRED));
        assertThat("previous page expected to be false", wizardPage.hasPreviousPage(), is(false));
        assertThat("no no messages expected", wizardPage.getNodeMessages(), nullValue());
        assertThat("no hasReport flag is set", wizardPage.hasReport(), nullValue());
    }

    /**
     * Checks if stepping to the second page works as expected.
     *
     * @throws Exception
     */
    public void testExecuteToSecondPage() throws Exception {
        UUID wfId = loadWorkflow(TestWorkflow.WIZARD_EXECUTION);
        wes().executeToNextPage(wfId, false, WF_EXECUTION_TIMEOUT, emptyWizardPageInput());
        int rowCount = (int)(5 * Math.random()) + 1;
        WizardPageInputEnt input = secondWizardPageInput(rowCount);
        WizardPageEnt wizardPage = wes().executeToNextPage(wfId, false, WF_EXECUTION_TIMEOUT, input);
        checkSecondPageContents(wizardPage.getWizardPageContent(), rowCount);
        assertThat("previous page expected to be true", wizardPage.hasPreviousPage(), is(true));
        assertThat("unexpected wizard execution state", wizardPage.getWizardExecutionState(),
            is(WizardExecutionStateEnum.INTERACTION_REQUIRED));
        assertThat("no node messages expected", wizardPage.getNodeMessages(), nullValue());
        assertThat("no hasReport flag is set", wizardPage.hasReport(), nullValue());
    }

    /**
     * Checks to advance to the next page asynchronously and retrieve the content via 'current-page'.
     *
     * @throws Exception
     */
    public void testAsyncExecuteToNextPageAndGetCurrentPage() throws Exception {
        UUID wfId = loadWorkflow(TestWorkflow.WIZARD_EXECUTION);
        WizardPageEnt emptyPage = wes().executeToNextPage(wfId, true, WF_EXECUTION_TIMEOUT, emptyWizardPageInput());
        assertNull("Wizard page content not empty", emptyPage.getWizardPageContent());
        assertNull("Wizard page execution state is set", emptyPage.getWizardExecutionState());

        //wait a bit for the wizard page to be available
        await().atMost(5, TimeUnit.SECONDS).pollInterval(100, TimeUnit.MILLISECONDS).untilAsserted(() -> {
            Object pageContent = wes().getCurrentPage(wfId).getWizardPageContent();
            checkFirstPageContents(pageContent);
        });
    }

    /**
     * Checks if stepping until the end with user input works as expected.
     *
     * @throws Exception
     */
    public void testExecuteToEnd() throws Exception {
        UUID wfId = loadWorkflow(TestWorkflow.WIZARD_EXECUTION);
        wes().executeToNextPage(wfId, false, WF_EXECUTION_TIMEOUT, emptyWizardPageInput());

        WizardPageInputEnt input = secondWizardPageInput((int)(5 * Math.random()) + 1);
        wes().executeToNextPage(wfId, false, WF_EXECUTION_TIMEOUT, input);

        WizardPageEnt wizardPage = wes().executeToNextPage(wfId, false, WF_EXECUTION_TIMEOUT, emptyWizardPageInput());
        assertThat("unexpected wizard execution state", wizardPage.getWizardExecutionState(),
            is(WizardExecutionStateEnum.EXECUTION_FINISHED));
        assertThat("previous page expected to be true", wizardPage.hasPreviousPage(), is(true));
        assertThat("empty list of node messages expected", wizardPage.getNodeMessages().isEmpty(), is(true));
        assertThat("has report flag is true", wizardPage.hasReport(), is(true));
    }

    /**
     * Checks what happens if workflow fails in wizard execution.
     *
     * @throws Exception
     */
    public void testExecuteToSecondPageWithFailure() throws Exception {
        UUID wfId = loadWorkflow(TestWorkflow.WIZARD_EXECUTION);

        //insert failing node between first and second page
        NodeIDEnt newNodeID = ns().createNode(wfId, NodeIDEnt.getRootID(), 10, 10,
            builder(NodeFactoryKeyEntBuilder.class)
                .setClassName("org.knime.testing.node.failing.FailingNodeFactory").build());
        ws().createConnection(wfId, builder(ConnectionEntBuilder.class).setSource(new NodeIDEnt(5)).setSourcePort(1)
            .setDest(newNodeID).setDestPort(1).setType(TypeEnum.STD).build());
        ws().createConnection(wfId, builder(ConnectionEntBuilder.class).setSource(newNodeID).setSourcePort(1)
            .setDest(new NodeIDEnt(9)).setDestPort(1).setType(TypeEnum.STD).build());

        //try to execute to second page
        wes().executeToNextPage(wfId, false, WF_EXECUTION_TIMEOUT, emptyWizardPageInput());
        WizardPageInputEnt input = secondWizardPageInput((int)(5 * Math.random()) + 1);
        WizardPageEnt wizardPage = wes().executeToNextPage(wfId, false, WF_EXECUTION_TIMEOUT, input);

        assertThat("unexpected wizard execution state", wizardPage.getWizardExecutionState(),
            is(WizardExecutionStateEnum.EXECUTION_FAILED));
        assertThat("previous page expected to be true", wizardPage.hasPreviousPage(), is(true));
        assertThat("node message key expected", wizardPage.getNodeMessages().keySet(),
            hasItem(containsString("Fail in execution")));
        assertThat("node messages expected", wizardPage.getNodeMessages().values(),
            hasItem(builder(NodeMessageEntBuilder.class)
                .setMessage("Execute failed: This node fails on each execution.").setType("ERROR").build()));
    }

    /**
     * Checks the wizard page status while workflow is in execution.
     *
     * @throws Exception
     */
    public void testGetCurrentPageWhileExecuting() throws Exception {
        UUID wfId = loadWorkflow(TestWorkflow.WIZARD_EXECUTION);

        //insert 'Wait ...' node to the beginning and configure
        NodeIDEnt newNodeID =
            ns().createNode(wfId, NodeIDEnt.getRootID(), 10, 10, builder(NodeFactoryKeyEntBuilder.class)
                .setClassName("org.knime.base.node.flowcontrol.sleep.SleepNodeFactory").build());
        ws().createConnection(wfId, builder(ConnectionEntBuilder.class).setSource(newNodeID).setSourcePort(1)
            .setDest(new NodeIDEnt(5)).setDestPort(0).setType(TypeEnum.STD).build());
        NodeSettingsEnt nodeSettings = ns().getNodeSettings(wfId, newNodeID);
        NodeSettingsEnt newNodeSettings =
            builder(NodeSettingsEntBuilder.class)
                .setJsonContent(nodeSettings.getJsonContent().replace(
                    "for_seconds\":{\"type\":\"int\",\"value\":0",
                    "for_seconds\":{\"type\":\"int\",\"value\":10"))
                .build();
        ns().setNodeSettings(wfId, newNodeID, newNodeSettings);

        //execute to first page
        WizardPageEnt wizardPage = wes().executeToNextPage(wfId, true, WF_EXECUTION_TIMEOUT, emptyWizardPageInput());
        assertNull(wizardPage.getWizardExecutionState());
        assertNull(wizardPage.getWizardPageContent());
        assertNull(wizardPage.getNodeMessages());
        assertNull(wizardPage.hasPreviousPage());

        //get current page
        wizardPage = wes().getCurrentPage(wfId);
        assertThat("unexpected wizard execution state", wizardPage.getWizardExecutionState(),
            is(WizardExecutionStateEnum.EXECUTING));
        assertThat("previous page expected to be false", wizardPage.hasPreviousPage(), is(false));
        assertNull(wizardPage.getNodeMessages());
    }

    /**
     * Makes sure that 'undefined' wizard execution state is returned when workflow is not executed in wizard mode.
     *
     * @throws Exception
     */
    public void testGetCurrentPageIfNotInWizardExecution() throws Exception {
        UUID wfId = loadWorkflow(TestWorkflow.WIZARD_EXECUTION);
        WizardPageEnt wizardPage = wes().getCurrentPage(wfId);
        assertThat("unexpected wizard execution state", wizardPage.getWizardExecutionState(),
            is(WizardExecutionStateEnum.UNDEFINED));
        assertNull(wizardPage.getWizardPageContent());
        assertNull(wizardPage.getNodeMessages());
        assertNull(wizardPage.hasPreviousPage());

        //execute entire workflow
        ns().changeAndGetNodeState(wfId, new NodeIDEnt(10), "execute");

        //check get current page
        await().atMost(2, TimeUnit.SECONDS).pollInterval(100, TimeUnit.MILLISECONDS).untilAsserted(() -> {
            WizardPageEnt wizardPage2 = wes().getCurrentPage(wfId);
            assertThat("unexpected wizard execution state", wizardPage2.getWizardExecutionState(),
                is(WizardExecutionStateEnum.EXECUTION_FINISHED));
            assertNull(wizardPage2.getWizardPageContent());
            assertNull(wizardPage2.hasPreviousPage());
        });
    }

    /**
     * Checks that the correct exception is thrown when the passed view values for the next page are not valid (e.g.
     * exceeding the maximum value).
     *
     * @throws Exception
     */
    public void testExecuteToNextPageWithInvalidViewValues() throws Exception {
        UUID wfId = loadWorkflow(TestWorkflow.WIZARD_EXECUTION);
        wes().executeToNextPage(wfId, false, WF_EXECUTION_TIMEOUT, emptyWizardPageInput());

        // the the integer input parameter which controls a row filter
        Map<String, String> viewValues = Collections.singletonMap("5:0:1", "{\"integer\": " + 100 + "}");
        try {
            wes().executeToNextPage(wfId, false, WF_EXECUTION_TIMEOUT,
                builder(WizardPageInputEntBuilder.class).setViewValues(viewValues).build());
            fail("Expected exception");
        } catch (InvalidSettingsException e) {
            assertThat("Unexpected exception message", e.getMessage(),
                containsString("The set integer 100 is bigger than the allowed maximum of 10"));
        }
    }

    /**
     * Tests that wizard execution state is not 'INTERACTION_REQUIRED' while a page is 're-executed' to apply view
     * values. Fixed with SRV-2777.
     *
     * @throws Exception
     */
    public void testGetCurrentPageWhileReexecuting() throws Exception {
        UUID wfIdAsync = loadWorkflow(TestWorkflow.WIZARD_EXECUTION_LONG_REEXECUTE);
        wes().executeToNextPage(wfIdAsync, false, WF_EXECUTION_TIMEOUT, emptyWizardPageInput());
        WizardPageInputEnt wizardPageInput = builder(WizardPageInputEntBuilder.class)
            .setViewValues(
                Collections.singletonMap("5:0:2", "{\"viewValues\":{\"5:0:2\":\"{\\\"string\\\":\\\"\\\"}\"}}"))
            .build();
        wes().executeToNextPage(wfIdAsync, true, WF_EXECUTION_TIMEOUT, wizardPageInput);
        AtomicReference<WizardPageEnt> currentPage = new AtomicReference<WizardPageEnt>();
        await().atMost(10, TimeUnit.SECONDS).pollInterval(100, TimeUnit.MILLISECONDS).untilAsserted(() -> {
            currentPage.set(wes().getCurrentPage(wfIdAsync));
            assertThat("unexpected wizard execution state", currentPage.get().getWizardExecutionState(),
                is(WizardExecutionStateEnum.INTERACTION_REQUIRED));
        });
        assertThat("hasReport flag set to false", currentPage.get().hasReport(), is(false));
        String pageContentString = ObjectMapperUtil.getInstance().getObjectMapper()
            .convertValue(currentPage.get().getWizardPageContent(), JsonNode.class).toString();
        assertThat("Expected page element not found - not the second page", pageContentString,
            hasJsonPath("$.webNodes.6:0:7.viewRepresentation.label", is("Second Page!")));
    }

    /**
     * Tests that the timeout works.
     *
     * @throws Exception
     */
    public void testExecuteToNextPageTimeout() throws Exception {
        UUID wfId = loadWorkflow(TestWorkflow.WIZARD_EXECUTION);
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
    public void testGetCurrentPageAfterLoad() throws Exception {
        UUID wfId = loadWorkflow(TestWorkflow.WIZARD_EXECUTION);
        WizardPageEnt wizardPage = wes().getCurrentPage(wfId);
        assertThat("unexpected wizard execution state", wizardPage.getWizardExecutionState(),
            is(WizardExecutionStateEnum.UNDEFINED));
    }

    /**
     * Checks for the right exception thrown when execution is reseted to the zeroth page which doesn't exist.
     *
     * @throws Exception
     */
    public void testResetToZerothPage() throws Exception {
        UUID wfId = loadWorkflow(TestWorkflow.WIZARD_EXECUTION);
        wes().executeToNextPage(wfId, false, WF_EXECUTION_TIMEOUT, emptyWizardPageInput());

        try {
            wes().resetToPreviousPage(wfId, 2000l);
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
        UUID wfId = loadWorkflow(TestWorkflow.WIZARD_EXECUTION);
        wes().executeToNextPage(wfId, false, WF_EXECUTION_TIMEOUT, emptyWizardPageInput());
        int rowCount = (int)(5 * Math.random()) + 1;
        WizardPageEnt wizardPage =
            wes().executeToNextPage(wfId, false, WF_EXECUTION_TIMEOUT, secondWizardPageInput(rowCount));
        checkSecondPageContents(wizardPage.getWizardPageContent(), rowCount);

        wizardPage = wes().resetToPreviousPage(wfId, 2000l);
        checkFirstPageContents(wizardPage.getWizardPageContent());

        rowCount = (int)(5 * Math.random()) + 1;
        wizardPage = wes().executeToNextPage(wfId, false, WF_EXECUTION_TIMEOUT, secondWizardPageInput(rowCount));
        checkSecondPageContents(wizardPage.getWizardPageContent(), rowCount);
    }

    /**
     * Method name says it all. Test for bugfix WEBP-277.
     *
     * @throws Exception
     */
    public void testResetToPreviousPageWhileWorkflowIsExecuting() throws Exception {
        final UUID wfId = loadWorkflow(TestWorkflow.WIZARD_EXECUTION_LONGRUNNING);
        wes().executeToNextPage(wfId, false, WF_EXECUTION_TIMEOUT, emptyWizardPageInput());
        wes().executeToNextPage(wfId, true, WF_EXECUTION_TIMEOUT, emptyWizardPageInput());
        await().atMost(5, TimeUnit.SECONDS).pollInterval(100, TimeUnit.MILLISECONDS).untilAsserted(() -> {
            assertThat(wes().getExecutionStatistics(wfId).getWizardExecutionState(),
                is(com.knime.gateway.entity.ExecutionStatisticsEnt.WizardExecutionStateEnum.EXECUTING));
        });

        WizardPageEnt page = wes().resetToPreviousPage(wfId, 2000l);
        //that we get the wizard page without failures is all we want to test
        assertThat(page.getWizardExecutionState(), is(WizardExecutionStateEnum.INTERACTION_REQUIRED));

        //should also test the timeout exception here - but couldn't come up with a workflow
        //that resists the cancellation
    }

    private static void checkSecondPageContents(final Object pageContents, final int expectedRowCount) {
        String pageContentString =
            ObjectMapperUtil.getInstance().getObjectMapper().convertValue(pageContents, JsonNode.class).toString();
        assertThat("Expected page element not found", pageContentString,
            hasJsonPath("$.webNodes.9:0:7.nodeInfo.nodeName", is("Text Output")));
        assertThat("Unexpected output content", pageContentString,
            hasJsonPath("$.webNodes.9:0:7.viewRepresentation.text", is(Integer.toString(expectedRowCount))));
    }

    private static void checkFirstPageContents(final Object pageContents) {
        String pageContentString =
            ObjectMapperUtil.getInstance().getObjectMapper().convertValue(pageContents, JsonNode.class).toString();
        assertThat("Expected page element not found", pageContentString,
            hasJsonPath("$.webNodePageConfiguration.layout.rows[*].columns[*].content", is(not(empty()))));
        assertThat("Expected page element not found", pageContentString,
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
        UUID wfId = loadWorkflow(TestWorkflow.WIZARD_EXECUTION);

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
        UUID wfId = loadWorkflow(TestWorkflow.WIZARD_EXECUTION);

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
        UUID wfId = loadWorkflow(TestWorkflow.WIZARD_EXECUTION);
        try {
            wes().getWebResource(wfId, "not_existing_resource_id");
            fail("Exception expected");
        } catch (NotFoundException e) {
            assertThat("Unexpected exception message", e.getMessage(), is("No resource for given id available"));
        }
    }

    /**
     * Checks the execution-statistics endpoint for a running workflow (started in wizard execution) and a workflow in
     * wizard execution.
     *
     * @throws Exception
     */
    public void testGetExecutionStatistics() throws Exception {
        long maxExecTime = 60000l;
        UUID wfIdAsync = loadWorkflow(TestWorkflow.LONGRUNNING);
        wes().executeToNextPage(wfIdAsync, true, WF_EXECUTION_TIMEOUT, emptyWizardPageInput());
        await().atMost(5, TimeUnit.SECONDS).pollInterval(100, TimeUnit.MILLISECONDS).untilAsserted(() -> {
            ExecutionStatisticsEnt executionStatistics = wes().getExecutionStatistics(wfIdAsync);
            assertThat("executing nodes expected", executionStatistics.getNodesExecuting(), not(empty()));
            assertThat("executed nodes expected", executionStatistics.getNodesExecuted(), not(empty()));
            assertThat("total execution time expected to be set",
                executionStatistics.getTotalExecutionDuration().longValue(), allOf(greaterThan(0l), lessThan(maxExecTime)));
            assertThat("wrong total node executions count", executionStatistics.getTotalNodeExecutionsCount(), is(2));
            assertThat("execution duration musst be set for executing nodes",
                executionStatistics.getNodesExecuting().get(0).getExecutionDuration().longValue(),
                allOf(greaterThan(0l), lessThan(maxExecTime)));
            assertThat("node annotation expected to be absent",
                executionStatistics.getNodesExecuted().get(0).getAnnotation(), nullValue());
            assertThat("node annotation expected to be present",
                executionStatistics.getNodesExecuting().get(0).getAnnotation(), is("executing node"));
            assertThat("execution state expected to be 'executing'", executionStatistics.getWizardExecutionState(),
                is(com.knime.gateway.entity.ExecutionStatisticsEnt.WizardExecutionStateEnum.EXECUTING));
        });

        UUID wfId = loadWorkflow(TestWorkflow.WIZARD_EXECUTION);
        wes().executeToNextPage(wfId, false, WF_EXECUTION_TIMEOUT, emptyWizardPageInput());
        ExecutionStatisticsEnt executionStatistics = wes().getExecutionStatistics(wfId);
        assertThat("no executing nodes expected", executionStatistics.getNodesExecuting(), empty());
        assertThat("executed nodes expected", executionStatistics.getNodesExecuted(), not(empty()));
        assertThat("executed nodes expected", executionStatistics.getNodesExecuted().size(), is(7));
        assertThat("total execution time expected to be set",
            executionStatistics.getTotalExecutionDuration().longValue(), allOf(greaterThan(0l), lessThan(maxExecTime)));
        assertThat("wrong total node executions count", executionStatistics.getTotalNodeExecutionsCount(), is(7));
        assertThat("execution state expected to be 'execution finished'", executionStatistics.getWizardExecutionState(),
            is(com.knime.gateway.entity.ExecutionStatisticsEnt.WizardExecutionStateEnum.INTERACTION_REQUIRED));

        int rowCount = (int)(5 * Math.random()) + 1;
        WizardPageInputEnt input = secondWizardPageInput(rowCount);
        WizardPageEnt wizardPage = wes().executeToNextPage(wfId, false, WF_EXECUTION_TIMEOUT, input);
        executionStatistics = wes().getExecutionStatistics(wfId);
        checkSecondPageContents(wizardPage.getWizardPageContent(), rowCount);
        assertThat("no executing nodes expected", executionStatistics.getNodesExecuting(), empty());
        assertThat("executed nodes expected", executionStatistics.getNodesExecuted(), not(empty()));
        assertThat("executed nodes expected", executionStatistics.getNodesExecuted().size(), is(14));
        assertThat("total execution time expected to be set",
            executionStatistics.getTotalExecutionDuration().longValue(), allOf(greaterThan(0l), lessThan(maxExecTime)));
        assertThat("wrong total node executions count", executionStatistics.getTotalNodeExecutionsCount(), is(14));
        assertThat("execution state expected to be 'interaction required'",
            executionStatistics.getWizardExecutionState(),
            is(com.knime.gateway.entity.ExecutionStatisticsEnt.WizardExecutionStateEnum.INTERACTION_REQUIRED));

        wfId = loadWorkflow(TestWorkflow.LOOP);
        wes().executeToNextPage(wfId, false, WF_EXECUTION_TIMEOUT, emptyWizardPageInput());
        executionStatistics = wes().getExecutionStatistics(wfId);
        assertThat("no executing nodes expected", executionStatistics.getNodesExecuting(), empty());
        assertThat("executed nodes expected", executionStatistics.getNodesExecuted(), not(empty()));
        assertThat("executed nodes expected", executionStatistics.getNodesExecuted().size(), is(4));
        assertThat("one run expected for first node",
            executionStatistics.getNodesExecuted().get(0).getRuns().intValue(), is(1));
        assertThat("multiple runs expected for node in loop",
            executionStatistics.getNodesExecuted().get(2).getRuns().intValue(), is(20));
        assertThat("total execution time expected to be set",
             executionStatistics.getTotalExecutionDuration().longValue(), allOf(greaterThan(0l), lessThan(maxExecTime)));
        assertThat("wrong total node executions count", executionStatistics.getTotalNodeExecutionsCount(), is(4));
        assertThat("execution state expected to be 'execution finished'", executionStatistics.getWizardExecutionState(),
            is(com.knime.gateway.entity.ExecutionStatisticsEnt.WizardExecutionStateEnum.EXECUTION_FINISHED));
    }
}

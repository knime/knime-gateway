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
package org.knime.gateway.impl.service;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.knime.core.node.CanceledExecutionException;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.workflow.UnsupportedWorkflowVersionException;
import org.knime.core.node.workflow.WorkflowManager;
import org.knime.core.util.LockFailedException;
import org.knime.gateway.impl.project.WorkflowProject;
import org.knime.gateway.impl.project.WorkflowProjectManager;
import org.knime.gateway.testing.helper.GatewayTestCollection;
import org.knime.gateway.testing.helper.GatewayTestRunner;
import org.knime.gateway.testing.helper.ResultChecker;
import org.knime.gateway.testing.helper.ServiceProvider;
import org.knime.gateway.testing.helper.TestUtil;
import org.knime.gateway.testing.helper.WorkflowExecutor;
import org.knime.gateway.testing.helper.WorkflowLoader;

/**
 * Runs all tests provided by {@link GatewayTestCollection} on the default service implementations, e.g.
 * {@link DefaultWorkflowService}.
 *
 * TODO consider using dynamic tests with JUnit 5.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
@RunWith(Parameterized.class)
public class GatewayDefaultServiceTests {

    private static final Map<String, GatewayTestRunner> GATEWAY_TESTS = GatewayTestCollection.collectAllGatewayTests();

    private final String m_gatewayTestName;

    private final WorkflowLoader m_workflowLoader;

    private final Set<UUID> m_loadedWorkflows = new HashSet<UUID>();

    private final WorkflowExecutor m_workflowExecutor;

    private final ServiceProvider m_serviceProvider;

    private static ResultChecker RESULT_CHECKER;

    /**
     * If manually set to true, the expected test results (i.e. the retrieved workflow etc.) will be updated and written
     * to the respective files. After that it need to be set to false again (otherwise the test will fail anyway).
     */
    private static final boolean REWRITE_TEST_RESULTS = false;

    /**
     * @return all names of the tests of {@link GatewayTestCollection}
     */
    @Parameters(name = "{0}")
    public static Iterable<String> testNames() {
        return GATEWAY_TESTS.keySet();
    }

    /**
     * @param gatewayTestName the test to run, the test names stemming from {@link #testNames()}
     * @throws InstantiationException
     * @throws IllegalAccessException
     * @throws URISyntaxException
     * @throws IOException
     */
    public GatewayDefaultServiceTests(final String gatewayTestName)
        throws InstantiationException, IllegalAccessException, URISyntaxException, IOException {
        m_workflowLoader = ((workflow) -> {

            final UUID uuid = UUID.randomUUID();
            WorkflowProjectManager.addWorkflowProject(uuid, new WorkflowProject() {

                @Override
                public WorkflowManager openProject() {
                    try {
                        WorkflowManager wfm = TestUtil.loadWorkflow(workflow.getUrlFolder());
                        wfm.setName(workflow.getName());
                        m_loadedWorkflows.add(uuid);
                        return wfm;
                    } catch (IOException | InvalidSettingsException | CanceledExecutionException
                            | UnsupportedWorkflowVersionException | LockFailedException e) {
                        throw new RuntimeException(e);
                    }
                }

                @Override
                public String getName() {
                    return workflow.getName();
                }

                @Override
                public String getID() {
                    return uuid.toString();
                }
            });
            return uuid;
        });
        m_workflowExecutor = new WorkflowExecutor() {

            @Override
            public void executeWorkflowAsync(final UUID wfId) throws Exception {
                WorkflowProjectManager.openAndCacheWorkflow(wfId).get().executeAll();
            }

            @Override
            public void executeWorkflow(final UUID wfId) throws Exception {
                WorkflowProjectManager.openAndCacheWorkflow(wfId).get().executeAllAndWaitUntilDone();
            }
        };
        m_serviceProvider = new GatewayDefaultServiceProvider();
        m_gatewayTestName = gatewayTestName;
    }

    /**
     * Runs the actual (parametrized) test.
     *
     * @throws Exception
     */
    @Test
    public void test() throws Exception {
        GATEWAY_TESTS.get(m_gatewayTestName).runGatewayTest(m_serviceProvider, RESULT_CHECKER, m_workflowLoader,
            m_workflowExecutor);
    }

    /**
     * Removes the project where necessary.
     */
    @After
    public void disposeWorkflows() {
        m_loadedWorkflows.forEach(uuid -> {
            WorkflowManager wfm = WorkflowProjectManager.openAndCacheWorkflow(uuid).get();
            TestUtil.cancelAndCloseLoadedWorkflow(wfm);
            WorkflowProjectManager.removeWorkflowProject(uuid);
        });
    }

    /**
     * A test that fails when {@link #REWRITE_TEST_RESULTS} is set to <code>true</code>.
     */
    @Test
    public void testTestResultsNotOverridden() {
        Assert.assertFalse("Result files have been rewritten. Set 'REWRITE_TEST_RESULTS' back to 'false'.",
            REWRITE_TEST_RESULTS);
    }

    /**
     * Initializes/instantiates the result checker.
     */
    @BeforeClass
    public static void initResultChecker() {
        RESULT_CHECKER = new ResultChecker(REWRITE_TEST_RESULTS);
    }

    /**
     * Finishes the result checker and writes collected test results to files (if configured).
     */
    @AfterClass
    public static void finishResultChecker() {
        if (REWRITE_TEST_RESULTS) {
            RESULT_CHECKER.writeTestResultsToFiles();
        }
    }
}

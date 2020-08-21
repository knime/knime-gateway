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
package org.knime.gateway.impl.webui.service;

import java.util.Map;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.knime.gateway.impl.project.WorkflowProjectManager;
import org.knime.gateway.testing.helper.LocalWorkflowLoader;
import org.knime.gateway.testing.helper.ResultChecker;
import org.knime.gateway.testing.helper.WorkflowExecutor;
import org.knime.gateway.testing.helper.webui.GatewayTestCollection;
import org.knime.gateway.testing.helper.webui.GatewayTestRunner;
import org.knime.gateway.testing.helper.webui.WebUIGatewayServiceTestHelper;

/**
 * Runs all tests provided by {@link GatewayTestCollection} on the default service implementations, e.g.
 * {@link DefaultWorkflowService}.
 *
 * TODO consider using dynamic tests with JUnit 5 //NOSONAR
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
@RunWith(Parameterized.class)
public class GatewayDefaultServiceTests {

    private static final Map<String, GatewayTestRunner> GATEWAY_TESTS = GatewayTestCollection.collectAllGatewayTests();

    private final String m_gatewayTestName;

    private final LocalWorkflowLoader m_workflowLoader;

    private final WorkflowExecutor m_workflowExecutor;

    private static ResultChecker resultChecker;

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
     */
    public GatewayDefaultServiceTests(final String gatewayTestName) {
        m_workflowLoader = new LocalWorkflowLoader();
        m_workflowExecutor = new WorkflowExecutor() {

            @Override
            public void executeWorkflowAsync(final String wfId) throws Exception {
                WorkflowProjectManager.openAndCacheWorkflow(wfId)
                    .orElseThrow(() -> new IllegalStateException("No workflow for id " + wfId)).executeAll();
            }

            @Override
            public void executeWorkflow(final String wfId) throws Exception {
                WorkflowProjectManager.openAndCacheWorkflow(wfId)
                    .orElseThrow(() -> new IllegalStateException("No workflow for id " + wfId))
                    .executeAllAndWaitUntilDone();
            }
        };
        m_gatewayTestName = gatewayTestName;
    }

    /**
     * Runs the actual (parametrized) test.
     *
     * @throws Exception
     */
    @Test
    public void test() throws Exception {
        GATEWAY_TESTS.get(m_gatewayTestName).runGatewayTest(resultChecker, m_workflowLoader, m_workflowExecutor);
    }

    /**
     * Removes the project where necessary.
     * @throws InterruptedException
     */
    @After
    public void disposeWorkflows() throws InterruptedException {
        m_workflowLoader.disposeWorkflows();
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
        resultChecker = WebUIGatewayServiceTestHelper.createResultChecker(REWRITE_TEST_RESULTS);
    }

    /**
     * Finishes the result checker and writes collected test results to files (if configured).
     */
    @AfterClass
    public static void finishResultChecker() {
        if (REWRITE_TEST_RESULTS) {
            resultChecker.writeTestResultsToFiles();
        }
    }
}

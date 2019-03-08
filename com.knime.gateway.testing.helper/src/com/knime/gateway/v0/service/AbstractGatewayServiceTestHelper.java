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
package com.knime.gateway.v0.service;

import static com.knime.gateway.testing.helper.TestUtil.resolveToURL;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.UUID;

import com.knime.gateway.service.GatewayService;
import com.knime.gateway.testing.helper.ResultChecker;
import com.knime.gateway.testing.helper.ServiceProvider;
import com.knime.gateway.testing.helper.TestUtil;
import com.knime.gateway.testing.helper.WorkflowExecutor;
import com.knime.gateway.testing.helper.WorkflowLoader;

/**
 * Tests for 'viewing' and manipulating workflows via the gateway API. Subclasses mainly test the correct behavior of
 * the provided {@link GatewayService} implementations given by the {@link ServiceProvider}.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
public abstract class AbstractGatewayServiceTestHelper {

    /**
     * Workflows used within the test helpers.
     */
    public enum TestWorkflow {
        /**
         * The main workflow the tests are based on.
         */
        WORKFLOW("/files/Test Gateway Workflow.knwf", "workflow"),

        /**
         * A longrunning workflow some tests are based on.
         */
        WORKFLOW_LONGRUNNING("/files/Test Gateway Workflow Longrunning.knwf", "workflow_longrunning"),

        /**
         * A workflow containing quickforms some tests are based on.
         */
        WORKFLOW_QUICKFORMS("/files/Test Gateway Workflow Quickforms.knwf", "workflow_quickforms"),

        /**
         * A workflow containing js views some tests are based on.
         */
        WORKFLOW_VIEWS("/files/Test Gateway Workflow Views.knwf", "workflow_views"),

        /**
         * A workflow to test nodes' data.
         */
        WORKFLOW_DATA("/files/Test Gateway Node Data.knwf", "workflow_data"),

        /**
         * A workflow to test large table output.
         */
        WORKFLOW_LARGE_TABLE("/files/Test Gateway Large Table.knwf", "workflow_large_table");

        private final String m_url;
        private final String m_name;

        /**
         * @param url the workflow url
         * @param name the workflow name
         */
        TestWorkflow(final String url, final String name) {
            m_url = url;
            m_name = name;
        }

        /**
         * @return url of the workflow file
         */
        public URL getUrlZipFile() {
            try {
                return resolveToURL(m_url);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }

        /**
         * @return the file of the workflow folder
         */
        public File getUrlFolder() {
            try {
                return TestUtil.resolveToFile(m_url.substring(0, m_url.length() - 5));
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }

        /**
         * @return name of the loaded workflow
         */
        public String getName() {
            return m_name;
        }
    }

    private final WorkflowLoader m_workflowLoader;

    private final WorkflowExecutor m_workflowExecutor;

    private final ServiceProvider m_serviceProvider;

    private final ResultChecker m_entityResultChecker;

    private final String m_testName;

    /**
     * Creates a new abstract service test.
     *
     * @param testName a unique name for this particular test
     * @param serviceProvider provides the service implementations that are actually the main objectives of the tests
     * @param entityResultChecker logic to check whether the returned entities are the expected ones
     * @param workflowLoader logic to load a workflow
     */
    public AbstractGatewayServiceTestHelper(final String testName, final ServiceProvider serviceProvider,
        final ResultChecker entityResultChecker, final WorkflowLoader workflowLoader) {
        this(testName, serviceProvider, entityResultChecker, workflowLoader, null);
    }

    /**
     * Creates a new abstract service test.
     *
     * @param testName a unique name for this particular test
     * @param serviceProvider provides the service implementations that are actually the main objectives of the tests
     * @param entityResultChecker logic to check whether the returned entities are the expected ones
     * @param workflowLoader logic to load a workflow
     * @param workflowExecutor logic to execute a workflow, can be <code>null</code> if not required by the test
     */
    public AbstractGatewayServiceTestHelper(final String testName, final ServiceProvider serviceProvider,
        final ResultChecker entityResultChecker, final WorkflowLoader workflowLoader,
        final WorkflowExecutor workflowExecutor) {
        m_testName = testName;
        m_workflowLoader = workflowLoader;
        m_workflowExecutor = workflowExecutor;
        m_serviceProvider = serviceProvider;
        m_entityResultChecker = entityResultChecker;
    }

    /**
     * See {@link WorkflowLoader#loadWorkflow(TestWorkflow)}.
     *
     * @param workflow the workflow to load
     * @return the workflow's id
     * @throws Exception if loading fails
     */
    protected final UUID loadWorkflow(final TestWorkflow workflow) throws Exception {
        return m_workflowLoader.loadWorkflow(workflow);
    }

    /**
     * See {@link WorkflowExecutor#executeWorkflow(UUID)}.
     *
     * @param wfId id of the workflow to execute
     * @throws Exception
     */
    protected final void executeWorkflow(final UUID wfId) throws Exception {
        m_workflowExecutor.executeWorkflow(wfId);
    }

    /**
     * See {@link WorkflowExecutor#executeWorkflowAsync(UUID)}.
     *
     * @param wfId
     * @throws Exception
     */
    protected final void executeWorkflowAsync(final UUID wfId) throws Exception {
        m_workflowExecutor.executeWorkflowAsync(wfId);
    }

    /**
     * Checks object by using the provided {@link ResultChecker}.
     *
     * @param obj the object to test
     * @param resultKey the key in the result map
     * @throws AssertionError if the result check failed (e.g. if the entity differs from the representation referenced
     *             by the given key)
     */
    protected final void cr(final Object obj, final String resultKey) {
        m_entityResultChecker.checkObject(m_testName, obj, resultKey);
    }

    /**
     * @return the supplied {@link WorkflowService} instance under test
     */
    protected final WorkflowService ws() {
        return m_serviceProvider.getWorkflowService();
    }

    /**
     * @return the supplied {@link NodeService} instance under test
     */
    protected final NodeService ns() {
        return m_serviceProvider.getNodeService();
    }

    /**
     * @return the supplied {@link AnnotationService} instance under test
     */
    protected final AnnotationService as() {
        return m_serviceProvider.getAnnotationService();
    }
}

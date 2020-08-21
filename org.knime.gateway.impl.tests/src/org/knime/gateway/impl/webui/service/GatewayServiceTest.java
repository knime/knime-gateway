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
 *   Aug 14, 2020 (hornm): created
 */
package org.knime.gateway.impl.webui.service;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.knime.core.node.CanceledExecutionException;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.workflow.UnsupportedWorkflowVersionException;
import org.knime.core.node.workflow.WorkflowManager;
import org.knime.core.util.LockFailedException;
import org.knime.core.util.Pair;
import org.knime.gateway.impl.project.WorkflowProject;
import org.knime.gateway.impl.project.WorkflowProjectManager;
import org.knime.gateway.testing.helper.GatewayServiceTestHelper;
import org.knime.gateway.testing.helper.ResultChecker;
import org.knime.gateway.testing.helper.TestWorkflow;
import org.knime.gateway.testing.helper.webui.WebUIGatewayServiceTestHelper;

/**
 * Super-class for tests of default gateway service implementations.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
public class GatewayServiceTest {

    private List<WorkflowManager> m_loadedWorkflows = new ArrayList<>();

    private static ResultChecker entityResultChecker;

    private String m_testName;

    private static final boolean REWRITE_TEST_RESULTS = false;

    /**
     * Initializes/instantiates the result checker.
     */
    @BeforeClass
    public static void initResultChecker() {
        entityResultChecker =
            WebUIGatewayServiceTestHelper.createResultChecker(REWRITE_TEST_RESULTS, GatewayServiceTest.class);
    }

    /**
     * Finishes the result checker and writes collected test results to files (if configured).
     */
    @AfterClass
    public static void finishResultChecker() {
        if (REWRITE_TEST_RESULTS) {
            entityResultChecker.writeTestResultsToFiles();
        }
    }

    /**
     * Creates a new abstract service test.
     *
     * @param testName a unique name for this particular test
     */
    protected GatewayServiceTest(final String testName) {
        m_testName = testName;
    }

    /**
     * Helper to load workflows for testing.
     *
     * @param wf the test workflow to load
     * @return the workflow id (to be provided to the default service implementations which in turn access the workflow
     *         via the {@link WorkflowProjectManager}) and the workflow manager instance.
     * @throws IOException
     * @throws InvalidSettingsException
     * @throws CanceledExecutionException
     * @throws UnsupportedWorkflowVersionException
     * @throws LockFailedException
     */
    protected Pair<UUID, WorkflowManager> loadWorkflow(final TestWorkflow wf) throws IOException,
        InvalidSettingsException, CanceledExecutionException, UnsupportedWorkflowVersionException, LockFailedException {
        UUID uuid = UUID.randomUUID();
        return Pair.create(uuid, loadWorkflow(wf, uuid.toString()));
    }


    /**
     * Helper to load workflows for testing.
     *
     * @param wf the test workflow to load
     * @param workflowProjectId a custom workflow project id of the loaded workflow
     * @return the workflow id (to be provided to the default service implementations which in turn access the workflow
     *         via the {@link WorkflowProjectManager}) and the workflow manager instance.
     * @throws IOException
     * @throws InvalidSettingsException
     * @throws CanceledExecutionException
     * @throws UnsupportedWorkflowVersionException
     * @throws LockFailedException
     */
    protected WorkflowManager loadWorkflow(final TestWorkflow wf, final String workflowProjectId) throws IOException,
        InvalidSettingsException, CanceledExecutionException, UnsupportedWorkflowVersionException, LockFailedException {
        WorkflowProject workflowProject = mock(WorkflowProject.class);
        WorkflowProjectManager.addWorkflowProject(workflowProjectId, workflowProject);
        WorkflowManager wfm = GatewayServiceTestHelper.loadWorkflow(wf.getUrlFolder());
        m_loadedWorkflows.add(wfm);
        when(workflowProject.openProject()).thenReturn(wfm);
        when(workflowProject.getID()).thenReturn(workflowProjectId);
        when(workflowProject.getName()).thenReturn(wfm.getName());
        return wfm;
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
        entityResultChecker.checkObject(m_testName, obj, resultKey);
    }

    /**
     * Cancels all workflow executions and disposes all workflows that have been loaded via
     * {@link #loadWorkflow(TestWorkflow)}.
     */
    @After
    public void disposeWorkflows() {
        for (WorkflowManager wfm : m_loadedWorkflows) {
            wfm.getParent().cancelExecution(wfm);
            if (wfm.getNodeContainerState().isExecutionInProgress()) {
                try {
                    wfm.waitWhileInExecution(5, TimeUnit.SECONDS);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            wfm.getParent().removeProject(wfm.getID());
        }
    }

}

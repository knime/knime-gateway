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

import org.knime.core.node.workflow.WorkflowManager;
import org.knime.core.util.Pair;
import org.knime.gateway.api.util.VersionId;
import org.knime.gateway.impl.project.ProjectManager;

/**
 * Helper to test service implementations. This is usually done by loading a workflow, optionally executing it and then
 * retrieving the entities under test and compare them to a stored snapshot (via {@link #cr(Object, String)}).
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
public class GatewayServiceTestHelper {

    private final WorkflowLoader m_workflowLoader;

    private final WorkflowExecutor m_workflowExecutor;

    private final ResultChecker m_entityResultChecker;

    private final Class<?> m_testClass;

    private final ProjectManager m_projectManager;

    /**
     * Creates a new abstract service test, only needed in 'knime-com-gateway', since {@link ProjectManager} is not
     * available there.
     *
     * @param testClass the test class carrying out the actual test
     * @param entityResultChecker logic to check whether the returned entities are the expected ones
     * @param workflowLoader logic to load a workflow
     * @param workflowExecutor logic to execute a workflow, can be <code>null</code> if not required by the test
     */
    protected GatewayServiceTestHelper(final Class<?> testClass, final ResultChecker entityResultChecker,
        final WorkflowLoader workflowLoader, final WorkflowExecutor workflowExecutor) {
        this(testClass, entityResultChecker, workflowLoader, workflowExecutor, null);
    }

    /**
     * Creates a new abstract service test.
     *
     * @param testClass the test class carrying out the actual test
     * @param entityResultChecker logic to check whether the returned entities are the expected ones
     * @param workflowLoader logic to load a workflow
     * @param workflowExecutor logic to execute a workflow, can be <code>null</code> if not required by the test
     * @param projectManager to enable project versions, can be <code>null</code> if not required by the test
     */
    protected GatewayServiceTestHelper(final Class<?> testClass, final ResultChecker entityResultChecker,
        final WorkflowLoader workflowLoader, final WorkflowExecutor workflowExecutor, final ProjectManager projectManager) {
        m_testClass = testClass;
        m_workflowLoader = workflowLoader;
        m_workflowExecutor = workflowExecutor;
        m_entityResultChecker = entityResultChecker;
        m_projectManager = projectManager;
    }

    /**
     * See {@link WorkflowLoader#loadWorkflow(TestWorkflow)}.
     *
     * @param workflow the workflow to load
     * @return the workflow's id
     * @throws Exception if loading fails
     */
    protected String loadWorkflow(final TestWorkflow workflow) throws Exception {
        return m_workflowLoader.loadWorkflow(workflow);
    }

    /**
     * Make sure a project version is set active
     *
     * @param projectId
     * @param versionId
     */
    protected void setVersionActive(final String projectId, final VersionId versionId) {
        m_projectManager.setProjectActive(projectId, versionId);
    }

    /**
     * Make sure a certain project version is loaded and set as active.
     *
     * @param projectId the project ID to load the version from
     * @param versionId the version to load
     * @throws Exception if loading fails
     */
    protected void loadVersionAndSetActive(final String projectId, final VersionId versionId) {
        m_projectManager.getProject(projectId) //
            .map(project -> project.getFromCacheOrLoadWorkflowManager(versionId)) //
            .orElseThrow(() -> new IllegalStateException(
                "Could not load project for id <%s> and version <%s>".formatted(projectId, versionId)));
        setVersionActive(projectId, versionId);
    }

    /**
     * @return The project ID
     * @throws Exception -
     * @see WorkflowLoader#createEmptyWorkflow()
     */
    protected Pair<String, WorkflowManager> createEmptyWorkflow() throws Exception {
        return m_workflowLoader.createEmptyWorkflow();
    }

    /**
     * See {@link WorkflowLoader#loadComponent(TestWorkflow)}.
     *
     * @param component the copmonent to load
     * @return the component's id
     * @throws Exception if loading fails
     */
    protected String loadComponent(final TestWorkflow component) throws Exception {
        return m_workflowLoader.loadComponent(component);
    }

    /**
     * See {@link WorkflowExecutor#executeWorkflow(String)}.
     *
     * @param wfId id of the workflow to execute
     * @throws Exception
     */
    protected final void executeWorkflow(final String wfId) throws Exception {
        m_workflowExecutor.executeWorkflow(wfId);
    }

    /**
     * See {@link WorkflowExecutor#executeWorkflowAsync(String)}.
     *
     * @param wfId
     * @throws Exception
     */
    protected final void executeWorkflowAsync(final String wfId) throws Exception {
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
        m_entityResultChecker.checkObject(m_testClass, resultKey, obj);
    }

}

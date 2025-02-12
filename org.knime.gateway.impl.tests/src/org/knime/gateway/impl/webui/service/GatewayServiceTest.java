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

import java.util.List;
import java.util.UUID;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.knime.core.node.workflow.WorkflowManager;
import org.knime.core.util.Pair;
import org.knime.gateway.impl.project.Project;
import org.knime.gateway.impl.project.ProjectManager;
import org.knime.gateway.impl.webui.AppStateUpdater;
import org.knime.gateway.impl.webui.NodeFactoryProvider;
import org.knime.gateway.impl.webui.PreferencesProvider;
import org.knime.gateway.impl.webui.WorkflowMiddleware;
import org.knime.gateway.impl.webui.service.events.EventConsumer;
import org.knime.gateway.impl.webui.spaces.SpaceProviderFactory;
import org.knime.gateway.impl.webui.spaces.SpaceProviders;
import org.knime.gateway.impl.webui.spaces.SpaceProvidersManager;
import org.knime.gateway.testing.helper.LocalWorkflowLoader;
import org.knime.gateway.testing.helper.ResultChecker;
import org.knime.gateway.testing.helper.TestWorkflow;
import org.knime.gateway.testing.helper.webui.WebUIGatewayServiceTestHelper;

/**
 * Super-class for tests of default gateway service implementations.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
public abstract class GatewayServiceTest {

    private static ResultChecker entityResultChecker;

    private final LocalWorkflowLoader m_workflowLoader = new LocalWorkflowLoader();

    /**
     * Initializes/instantiates the result checker.
     */
    @BeforeClass
    public static void initResultChecker() {
        entityResultChecker = WebUIGatewayServiceTestHelper.createResultChecker(GatewayServiceTest.class);
    }

    /**
     * Creates a new abstract service test.
     *
     */
    protected GatewayServiceTest() {
        //
    }

    @SuppressWarnings("javadoc")
    @Before
    public void setupServiceDependencies() {
        ServiceDependencies.setServiceDependency(AppStateUpdater.class, new AppStateUpdater());
        final var projectManager = ProjectManager.getInstance();
        final var spaceProvidersManager = createSpaceProvidersManager();
        ServiceDependencies.setServiceDependency(WorkflowMiddleware.class,
            new WorkflowMiddleware(projectManager, spaceProvidersManager));
        ServiceDependencies.setServiceDependency(EventConsumer.class, createEventConsumer());
        ServiceDependencies.setServiceDependency(ProjectManager.class, projectManager);
        ServiceDependencies.setServiceDependency(PreferencesProvider.class, mock(PreferencesProvider.class));
        ServiceDependencies.setServiceDependency(SpaceProvidersManager.class, spaceProvidersManager);
        ServiceDependencies.setServiceDependency(NodeFactoryProvider.class, createNodeFactoryProvider());
    }

    /**
     * @return the {@link SpaceProviders} service dependency
     */
    protected SpaceProvidersManager createSpaceProvidersManager() {
        var res = new SpaceProvidersManager(id -> {
        }, null, List.of(mock(SpaceProviderFactory.class)));
        res.update();
        return res;
    }

    /**
     * @return the {@link NodeFactoryProvider} service dependency
     */
    protected NodeFactoryProvider createNodeFactoryProvider() {
        return mock(NodeFactoryProvider.class);
    }

    /**
     * @return the {@link EventConsumer} service dependency
     */
    protected EventConsumer createEventConsumer() {
        return (name, event) -> {};
    }

    @SuppressWarnings("javadoc")
    @After
    public void disposeServices() {
        ServiceInstances.disposeAllServiceInstancesAndDependencies();
    }

    /**
     * Helper to load workflows for testing.
     *
     * @param wf the test workflow to load
     * @return the workflow id (to be provided to the default service implementations which in turn access the workflow
     *         via the {@link ProjectManager}) and the workflow manager instance.
     * @throws Exception
     */
    protected Pair<UUID, WorkflowManager> loadWorkflow(final TestWorkflow wf) throws Exception {
        UUID uuid = UUID.randomUUID();
        return Pair.create(uuid, loadWorkflow(wf, uuid.toString()));
    }

    /**
     * Helper to load workflows for testing.
     *
     * @param wf the test workflow to load
     * @param workflowProjectId a custom workflow project id of the loaded workflow
     * @return the loaded workflow manager instance
     * @throws Exception
     */
    protected WorkflowManager loadWorkflow(final TestWorkflow wf, final String workflowProjectId) throws Exception {
        m_workflowLoader.loadWorkflow(wf, workflowProjectId);
        Project project =
            ProjectManager.getInstance().getProject(workflowProjectId).orElse(null);
        return project == null ? null : project.loadWorkflowManager();
    }

    /**
     * Helper to load component projects for testing.
     *
     * @param wf the test component project to load
     * @param projectId a custom project id of the loaded component project
     * @return the loaded workflow manager instance
     * @throws Exception
     */
    protected WorkflowManager loadComponent(final TestWorkflow wf, final String projectId) throws Exception {
        m_workflowLoader.loadComponent(wf, projectId);
        return ProjectManager.getInstance().getProject(projectId) //
                .map(Project::getWorkflowManager) //
                .orElse(null);
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
        entityResultChecker.checkObject(getClass(), resultKey, obj);
    }

    /**
     * Cancels all workflow executions and disposes all workflows that have been loaded via
     * {@link #loadWorkflow(TestWorkflow)}.
     *
     * @throws InterruptedException
     */
    @After
    public void disposeWorkflows() throws InterruptedException {
        m_workflowLoader.disposeWorkflows();
    }

}

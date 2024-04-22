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

import static org.mockito.Mockito.mock;

import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.knime.gateway.api.webui.service.EventService;
import org.knime.gateway.api.webui.service.NodeRepositoryService;
import org.knime.gateway.api.webui.service.NodeService;
import org.knime.gateway.api.webui.service.PortService;
import org.knime.gateway.api.webui.service.SpaceService;
import org.knime.gateway.api.webui.service.WorkflowService;
import org.knime.gateway.impl.project.ProjectManager;
import org.knime.gateway.impl.webui.AppStateUpdater;
import org.knime.gateway.impl.webui.NodeRepository;
import org.knime.gateway.impl.webui.PreferencesProvider;
import org.knime.gateway.impl.webui.WorkflowMiddleware;
import org.knime.gateway.testing.helper.LocalWorkflowLoader;
import org.knime.gateway.testing.helper.ResultChecker;
import org.knime.gateway.testing.helper.ServiceProvider;
import org.knime.gateway.testing.helper.WorkflowExecutor;
import org.knime.gateway.testing.helper.webui.GatewayTestCollection;
import org.knime.gateway.testing.helper.webui.GatewayTestRunner;
import org.knime.gateway.testing.helper.webui.WebUIGatewayServiceTestHelper;
import org.knime.js.core.JSCorePlugin;

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

    private static ResultChecker resultChecker;

    private final String m_gatewayTestName;

    private final LocalWorkflowLoader m_workflowLoader;

    private final WorkflowExecutor m_workflowExecutor;

    private final ServiceProvider m_serviceProvider;


    /**
     * Makes sure the org.knime.js.core plugin is activated which in turn registers the
     * DefaultConfigurationLayoutCreator osgi-service registered which in turn is required to create the component
     * description which is used by tests (see SubNodeContainer#getDialogDescriptions and
     * ConfigurationLayoutUtil#getConfigurationOrder)
     */
    @BeforeClass
    public static void activateJsCore() {
        JSCorePlugin.class.getName();
    }

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
                ProjectManager.getInstance().openAndCacheProject(wfId)
                    .orElseThrow(() -> new IllegalStateException("No workflow for id " + wfId)).executeAll();
            }

            @Override
            public void executeWorkflow(final String wfId) throws Exception {
                ProjectManager.getInstance().openAndCacheProject(wfId)
                    .orElseThrow(() -> new IllegalStateException("No workflow for id " + wfId))
                    .executeAllAndWaitUntilDone();
            }
        };
        m_serviceProvider = new ServiceProvider() {

            @Override
            public WorkflowService getWorkflowService() {
                return DefaultWorkflowService.getInstance();
            }

            @Override
            public NodeService getNodeService() {
                return DefaultNodeService.getInstance();
            }

            @Override
            public PortService getPortService() {
                return DefaultPortService.getInstance();
            }

            @Override
            public EventService getEventService() {
                return DefaultEventService.getInstance();
            }

            @Override
            public NodeRepositoryService getNodeRepositoryService() {
                return DefaultNodeRepositoryService.getInstance();
            }

            @Override
            public SpaceService getSpaceService() {
                return DefaultSpaceService.getInstance();
            }
        };
        m_gatewayTestName = gatewayTestName;
    }

    /**
     * Runs the actual (parameterized) test.
     *
     * @throws Exception
     */
    @Test
    public void test() throws Exception {
        GATEWAY_TESTS.get(m_gatewayTestName).runGatewayTest(resultChecker, m_serviceProvider, m_workflowLoader,
            m_workflowExecutor);
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
     * Initializes/instantiates the result checker.
     */
    @BeforeClass
    public static void initResultChecker() {
        resultChecker = WebUIGatewayServiceTestHelper.createResultChecker();
    }

    @SuppressWarnings("javadoc")
    @Before
    public void setupServiceDependencies() {
        ServiceDependencies.setServiceDependency(ProjectManager.class, ProjectManager.getInstance());
        ServiceDependencies.setServiceDependency(WorkflowMiddleware.class,
            new WorkflowMiddleware(ProjectManager.getInstance(), null));
        ServiceDependencies.setServiceDependency(AppStateUpdater.class, null);
        ServiceDependencies.setServiceDependency(PreferencesProvider.class, mock(PreferencesProvider.class));
        ServiceDependencies.setServiceDependency(NodeRepository.class, new NodeRepository());
    }

    @SuppressWarnings("javadoc")
    @After
    public void disposeServices() {
        ServiceInstances.disposeAllServiceInstancesAndDependencies();
    }

}

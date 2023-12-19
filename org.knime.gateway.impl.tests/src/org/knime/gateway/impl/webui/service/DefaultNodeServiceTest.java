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
 *   Dec 7, 2023 (hornm): created
 */
package org.knime.gateway.impl.webui.service;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.port.PortObjectSpec;
import org.knime.core.node.workflow.NativeNodeContainer;
import org.knime.core.node.workflow.WorkflowManager;
import org.knime.core.webui.data.ApplyDataService;
import org.knime.core.webui.data.InitialDataService;
import org.knime.core.webui.data.RpcDataService;
import org.knime.core.webui.node.dialog.NodeAndVariableSettingsRO;
import org.knime.core.webui.node.dialog.NodeAndVariableSettingsWO;
import org.knime.core.webui.node.dialog.NodeDialog;
import org.knime.core.webui.node.dialog.NodeSettingsService;
import org.knime.core.webui.node.dialog.SettingsType;
import org.knime.core.webui.node.view.NodeView;
import org.knime.core.webui.page.Page;
import org.knime.gateway.api.entity.NodeIDEnt;
import org.knime.gateway.api.webui.service.NodeService;
import org.knime.gateway.impl.project.DefaultProject;
import org.knime.gateway.impl.project.ProjectManager;
import org.knime.gateway.testing.helper.webui.NodeServiceTestHelper;
import org.knime.testing.node.dialog.NodeDialogNodeFactory;
import org.knime.testing.node.view.NodeViewNodeFactory;
import org.knime.testing.util.WorkflowManagerUtil;

/**
 * Tests methods in {@link DefaultNodeService} which can't be covered by {@link NodeServiceTestHelper}.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
public class DefaultNodeServiceTest {

    private WorkflowManager m_wfm;
    private String m_projectId;

    @SuppressWarnings("javadoc")
    @Before
    public void createEmptyWorkflow() throws IOException {
        m_wfm = WorkflowManagerUtil.createEmptyWorkflow();
        var project = DefaultProject.builder(m_wfm).build();
        m_projectId = project.getID();
        ProjectManager.getInstance().addProject(project);
    }

    @SuppressWarnings("javadoc")
    @After
    public void tearDownWorkflow() {
        ProjectManager.getInstance().removeProject(m_projectId, WorkflowManagerUtil::disposeWorkflow);
    }

    @SuppressWarnings("javadoc")
    @After
    public void disposeServices() {
        ServiceInstances.disposeAllServiceInstancesAndDependencies();
    }

    /**
     * Tests
     * {@link NodeService#deactivateNodeDataServices(String, org.knime.gateway.api.entity.NodeIDEnt, org.knime.gateway.api.entity.NodeIDEnt, String)}
     * for a {@link NodeView}.
     *
     * @throws Exception
     */
    @Test
    public void testDeactivateNodeDataServicesForNodeView() throws Exception {
        var deactivateRunnablesCalled = new boolean[2];
        var nc = createNodeWithView(m_wfm, deactivateRunnablesCalled);
        var nodeIdEnt = new NodeIDEnt(nc.getID());
        m_wfm.executeAllAndWaitUntilDone();

        var nodeService = DefaultNodeService.getInstance();

        // 'use' the node view
        nodeService.getNodeView(m_projectId, NodeIDEnt.getRootID(), nodeIdEnt);
        nodeService.callNodeDataService(m_projectId, NodeIDEnt.getRootID(), nodeIdEnt, "view", "data", "foo");

        // the actual check
        nodeService.deactivateNodeDataServices(m_projectId, NodeIDEnt.getRootID(), nodeIdEnt, "view");
        assertThat(deactivateRunnablesCalled, is(new boolean[]{true, true}));
    }

    private static NativeNodeContainer createNodeWithView(final WorkflowManager wfm,
        final boolean[] deactivateRunnablesCalled) {
        var nc = WorkflowManagerUtil.createAndAddNode(wfm, new NodeViewNodeFactory(m -> {
            return new NodeView() {

                @Override
                public Page getPage() {
                    return Page.builder(() -> "blub", "index.html").build();
                }

                @Override
                public Optional<InitialDataService<String>> createInitialDataService() {
                    return Optional.of(InitialDataService.builder(() -> "initial data")
                        .onDeactivate(() -> deactivateRunnablesCalled[0] = true).build());
                }

                @Override
                public Optional<RpcDataService> createRpcDataService() {
                    return Optional.of(RpcDataService.<Supplier<String>> builder(() -> "bar")
                        .onDeactivate(() -> deactivateRunnablesCalled[1] = true).build());
                }

                @Override
                public Optional<ApplyDataService<String>> createApplyDataService() {
                    return Optional.empty();
                }

                @Override
                public void validateSettings(final NodeSettingsRO settings) throws InvalidSettingsException {
                    //
                }

                @Override
                public void loadValidatedSettingsFrom(final NodeSettingsRO settings) {
                    //
                }

            };
        }));
        return nc;
    }

    /**
     * Tests
     * {@link NodeService#deactivateNodeDataServices(String, org.knime.gateway.api.entity.NodeIDEnt, org.knime.gateway.api.entity.NodeIDEnt, String)}
     * for a {@link NodeDialog}.
     *
     * @throws Exception
     */
    @Test
    public void testDeactivateNodeDataServicesForNodeDialog() throws Exception {
        var deactivateRunnableCalled = new AtomicBoolean();

        var nc = createNodeWithDialog(m_wfm, deactivateRunnableCalled);
        var nodeIdEnt = new NodeIDEnt(nc.getID());

        var nodeService = DefaultNodeService.getInstance();

        // 'use' the node view
        nodeService.getNodeDialog(m_projectId, NodeIDEnt.getRootID(), nodeIdEnt);
        nodeService.callNodeDataService(m_projectId, NodeIDEnt.getRootID(), nodeIdEnt, "dialog", "data", "foo");

        // the actual check
        nodeService.deactivateNodeDataServices(m_projectId, NodeIDEnt.getRootID(), nodeIdEnt, "dialog");
        assertThat(deactivateRunnableCalled.get(), is(true));
    }

    private static NativeNodeContainer createNodeWithDialog(final WorkflowManager wfm,
        final AtomicBoolean deactivateRunnableCalled) {
        var nc = WorkflowManagerUtil.createAndAddNode(wfm, new NodeDialogNodeFactory(() -> {
            return new NodeDialog() {

                @Override
                public Page getPage() {
                    return Page.builder(() -> "blub", "index.html").build();
                }

                @Override
                public Optional<RpcDataService> createRpcDataService() {
                    return Optional.of(RpcDataService.<Supplier<String>> builder(() -> "bar")
                        .onDeactivate(() -> deactivateRunnableCalled.set(true)).build());
                }

                @Override
                public Set<SettingsType> getSettingsTypes() {
                    return Set.of(SettingsType.VIEW);
                }

                @Override
                public NodeSettingsService getNodeSettingsService() {
                    return new NodeSettingsService() {

                        @Override
                        public void toNodeSettings(final String textSettings,
                            final Map<SettingsType, NodeAndVariableSettingsWO> settings) {
                            //
                        }

                        @Override
                        public String fromNodeSettings(final Map<SettingsType, NodeAndVariableSettingsRO> settings,
                            final PortObjectSpec[] specs) {
                            return null;
                        }
                    };
                }

            };
        }));
        return nc;
    }

}

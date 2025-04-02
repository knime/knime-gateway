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
 *   Dec 8, 2023 (hornm): created
 */
package org.knime.gateway.impl.webui.service;

import static org.awaitility.Awaitility.await;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.knime.gateway.api.entity.EntityBuilderManager.builder;
import static org.knime.gateway.api.entity.NodeIDEnt.getRootID;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Supplier;

import org.junit.Test;
import org.knime.core.data.DataTableSpec;
import org.knime.core.node.BufferedDataTable;
import org.knime.core.node.CanceledExecutionException;
import org.knime.core.node.ExecutionContext;
import org.knime.core.node.ExecutionMonitor;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeDialogPane;
import org.knime.core.node.NodeFactory;
import org.knime.core.node.NodeModel;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.node.NodeView;
import org.knime.core.node.workflow.NativeNodeContainer;
import org.knime.core.node.workflow.WorkflowManager;
import org.knime.core.webui.data.InitialDataService;
import org.knime.core.webui.data.RpcDataService;
import org.knime.core.webui.node.port.PortSpecViewFactory;
import org.knime.core.webui.node.port.PortView;
import org.knime.core.webui.node.port.PortViewManager;
import org.knime.core.webui.node.port.PortViewManager.PortViewDescriptor;
import org.knime.core.webui.page.Page;
import org.knime.gateway.api.entity.DataValueViewEnt;
import org.knime.gateway.api.entity.NodeIDEnt;
import org.knime.gateway.api.entity.PortViewEnt;
import org.knime.gateway.api.util.VersionId;
import org.knime.gateway.api.webui.entity.SelectionEventEnt;
import org.knime.gateway.api.webui.entity.SelectionEventEnt.ModeEnum;
import org.knime.gateway.api.webui.entity.SelectionEventEnt.SelectionEventEntBuilder;
import org.knime.gateway.api.webui.service.PortService;
import org.knime.gateway.impl.project.Project;
import org.knime.gateway.impl.project.ProjectManager;
import org.knime.gateway.impl.webui.service.events.SelectionEventBus;
import org.knime.gateway.testing.helper.TestWorkflowCollection;
import org.knime.gateway.testing.helper.webui.PortServiceTestHelper;
import org.knime.testing.util.WorkflowManagerUtil;

/**
 * Tests methods in {@link DefaultPortService} which can't be covered by {@link PortServiceTestHelper}.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
public class DefaultPortServiceTest extends GatewayServiceTest {

    /**
     * Tests
     * {@link PortService#deactivatePortDataServices(String, org.knime.gateway.api.entity.NodeIDEnt, org.knime.gateway.api.entity.NodeIDEnt, Integer, Integer)}.
     *
     * @throws Exception
     */
    @Test
    public void testDeactivatePortDataService() throws Exception {
        var wfm = WorkflowManagerUtil.createEmptyWorkflow();
        var nc = createNodeWithTableOutputPort(wfm);
        var project = Project.builder().setWfm(wfm).onDispose(WorkflowManagerUtil::disposeWorkflow).build();
        var projectId = project.getID();
        var nodeIdEnt = new NodeIDEnt(nc.getID());
        ProjectManager.getInstance().addProject(project);

        var deactivateRunnablesCalled = new boolean[2];
        var portView = createPortView(deactivateRunnablesCalled);
        PortSpecViewFactory<DataTableSpec> portViewFactory = p -> portView;

        var originalPortViews = PortViewManager.getPortViews(BufferedDataTable.TYPE);
        PortViewManager.registerPortViews(BufferedDataTable.TYPE,
            List.of(new PortViewDescriptor("Test table view", portViewFactory)), List.of(0), List.of());

        var portService = DefaultPortService.getInstance();

        // 'use' port view
        portService.getPortView(projectId, NodeIDEnt.getRootID(), VersionId.currentState().toString(), nodeIdEnt, 1, 0);
        portService.callPortDataService(projectId, NodeIDEnt.getRootID(), VersionId.currentState().toString(), nodeIdEnt, 1, 0, "data", "foo");

        // the actual check
        portService.deactivatePortDataServices(projectId, NodeIDEnt.getRootID(), VersionId.currentState().toString(), nodeIdEnt, 1, 0);
        assertThat(deactivateRunnablesCalled, is(new boolean[]{true, true}));

        // clean up
        PortViewManager.registerPortViews(BufferedDataTable.TYPE, originalPortViews.viewDescriptors(),
            originalPortViews.configuredIndices(), originalPortViews.executedIndices());
        ProjectManager.getInstance().removeProject(project.getID());
        ServiceInstances.disposeAllServiceInstancesAndDependencies();
    }

    @SuppressWarnings({"java:S1188"})
    private static NativeNodeContainer createNodeWithTableOutputPort(final WorkflowManager wfm) {
        return WorkflowManagerUtil.createAndAddNode(wfm, new NodeFactory<>() {

            @Override
            public NodeModel createNodeModel() {
                return new NodeModel(0, 1) {

                    @Override
                    protected DataTableSpec[] configure(final DataTableSpec[] inSpecs) throws InvalidSettingsException {
                        return new DataTableSpec[]{new DataTableSpec()};
                    }

                    @Override
                    protected BufferedDataTable[] execute(final BufferedDataTable[] inData, final ExecutionContext exec)
                            throws Exception {
                        return null;
                    }

                    @Override
                    protected void validateSettings(final NodeSettingsRO settings) throws InvalidSettingsException {
                        //
                    }

                    @Override
                    protected void saveSettingsTo(final NodeSettingsWO settings) {
                        //
                    }

                    @Override
                    protected void saveInternals(final File nodeInternDir, final ExecutionMonitor exec)
                            throws IOException, CanceledExecutionException {
                        //
                    }

                    @Override
                    protected void reset() {
                        //
                    }

                    @Override
                    protected void loadValidatedSettingsFrom(final NodeSettingsRO settings)
                            throws InvalidSettingsException {
                        //
                    }

                    @Override
                    protected void loadInternals(final File nodeInternDir, final ExecutionMonitor exec)
                            throws IOException, CanceledExecutionException {
                        //
                    }
                };
            }

            @Override
            protected int getNrNodeViews() {
                return 0;
            }

            @Override
            public NodeView<NodeModel> createNodeView(final int viewIndex, final NodeModel nodeModel) {
                return null;
            }

            @Override
            protected boolean hasDialog() {
                return false;
            }

            @Override
            protected NodeDialogPane createNodeDialogPane() {
                return null;
            }
        });
    }

    private static PortView createPortView(final boolean[] deactivateRunnablesCalled) {
        return new PortView() {

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
        };
    }

    /**
     * Makes sure that {@link PortService#getPortView(String, NodeIDEnt, NodeIDEnt, Integer, Integer)} returns the
     * initial selection.
     *
     * @throws Exception
     */
    @Test
    public void getPortViewWithInitialSelection() throws Exception {
        var wfId = "wf_id";
        var wfm = loadWorkflow(TestWorkflowCollection.GENERAL_WEB_UI, wfId);
        wfm.executeAllAndWaitUntilDone();

        var ps = DefaultPortService.getInstance();
        ps.updateDataPointSelection(wfId, getRootID(), VersionId.currentState().toString(), new NodeIDEnt(2), 1, 1, "add", List.of("Row2", "Row5"));

        var portView =
            (PortViewEnt)ps.getPortView(wfId, getRootID(), VersionId.currentState().toString(), new NodeIDEnt(1), 1, 1);
        assertThat(portView.getInitialSelection(), containsInAnyOrder("Row2", "Row5"));
    }

    /**
     * Makes sure that {@link PortService#getPortView(String, NodeIDEnt, NodeIDEnt, Integer, Integer)} returns the
     * initial selection and subscribes to the {@link SelectionEventBus} if made available as a service dependency.
     *
     * @throws Exception
     */
    @Test
    public void testGetPortViewWithInitialSelectionAndSetUpSelectionEventBus() throws Exception {
        var wfId = "wf_id";
        var wfm = loadWorkflow(TestWorkflowCollection.GENERAL_WEB_UI, wfId);
        wfm.executeAllAndWaitUntilDone();

        var selectionEventBus = new SelectionEventBus();
        Consumer<SelectionEventEnt> selectionEventConsumer = mock(Consumer.class);
        selectionEventBus.addSelectionEventListener(selectionEventConsumer);
        ServiceDependencies.setServiceDependency(SelectionEventBus.class, selectionEventBus);

        var ps = DefaultPortService.getInstance();
        ps.updateDataPointSelection(wfId, getRootID(), VersionId.currentState().toString(), new NodeIDEnt(2), 1, 1,
            "add", List.of("Row2", "Row5"));

        var portView =
            (PortViewEnt)ps.getPortView(wfId, getRootID(), VersionId.currentState().toString(), new NodeIDEnt(1), 1, 1);
        assertThat(portView.getInitialSelection(), containsInAnyOrder("Row2", "Row5"));
        assertThat(selectionEventBus.getNumEventEmitters(), is(1));

        ps.updateDataPointSelection(wfId, getRootID(), VersionId.currentState().toString(), new NodeIDEnt(2), 1, 1,
            "add", List.of("Row3"));
        await().atMost(2, TimeUnit.SECONDS)
            .untilAsserted(() -> verify(selectionEventConsumer).accept(builder(SelectionEventEntBuilder.class)
                .setProjectId(wfId).setWorkflowId(getRootID()).setNodeId(new NodeIDEnt(1)).setPortIndex(1)
                .setMode(ModeEnum.ADD).setSelection(List.of("Row3")).build()));

        wfm.resetAndConfigureNode(wfm.getID().createChild(1));
        assertThat(selectionEventBus.getNumEventEmitters(), is(0));
    }

    @SuppressWarnings("javadoc")
    @Test
    public void testGetDataValueView() throws Exception {
        var wfId = "wf_id";
        var wfm = loadWorkflow(TestWorkflowCollection.GENERAL_WEB_UI, wfId);
        wfm.executeAllAndWaitUntilDone();
        final var dataValueView = DefaultPortService.getInstance().getDataValueView(wfId, getRootID(),
            VersionId.currentState().toString(), new NodeIDEnt(27), 1, 1, 4);

        assertThat(dataValueView, instanceOf(DataValueViewEnt.class));
        assertThat(((DataValueViewEnt)dataValueView).getResourceInfo().getPath(),
            is("uiext-data_value/org.knime.core.data.def.StringCell/StringCellView.html"));
    }

}

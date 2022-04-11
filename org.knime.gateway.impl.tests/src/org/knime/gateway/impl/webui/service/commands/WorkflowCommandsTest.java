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
 *   Jan 20, 2021 (hornm): created
 */
package org.knime.gateway.impl.webui.service.commands;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThrows;
import static org.knime.gateway.api.entity.EntityBuilderManager.builder;
import static org.mockito.Mockito.mock;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.awaitility.Awaitility;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeSettings;
import org.knime.core.node.workflow.FileNativeNodeContainerPersistor;
import org.knime.core.node.workflow.NodeID;
import org.knime.core.node.workflow.WorkflowContext;
import org.knime.core.node.workflow.WorkflowCreationHelper;
import org.knime.core.node.workflow.WorkflowManager;
import org.knime.core.node.workflow.WorkflowPersistor;
import org.knime.core.util.FileUtil;
import org.knime.gateway.api.entity.NodeIDEnt;
import org.knime.gateway.api.webui.entity.AddNodeCommandEnt.AddNodeCommandEntBuilder;
import org.knime.gateway.api.webui.entity.ConnectCommandEnt.ConnectCommandEntBuilder;
import org.knime.gateway.api.webui.entity.DeleteCommandEnt.DeleteCommandEntBuilder;
import org.knime.gateway.api.webui.entity.NodeFactoryKeyEnt.NodeFactoryKeyEntBuilder;
import org.knime.gateway.api.webui.entity.TranslateCommandEnt;
import org.knime.gateway.api.webui.entity.TranslateCommandEnt.TranslateCommandEntBuilder;
import org.knime.gateway.api.webui.entity.WorkflowChangedEventEnt;
import org.knime.gateway.api.webui.entity.WorkflowChangedEventTypeEnt.WorkflowChangedEventTypeEntBuilder;
import org.knime.gateway.api.webui.entity.WorkflowCommandEnt.KindEnum;
import org.knime.gateway.api.webui.entity.WorkflowCommandEnt.WorkflowCommandEntBuilder;
import org.knime.gateway.api.webui.entity.XYEnt.XYEntBuilder;
import org.knime.gateway.api.webui.service.util.ServiceExceptions.OperationNotAllowedException;
import org.knime.gateway.impl.project.WorkflowProject;
import org.knime.gateway.impl.project.WorkflowProjectManager;
import org.knime.gateway.impl.service.util.EventConsumer;
import org.knime.gateway.impl.webui.AppStateProvider;
import org.knime.gateway.impl.webui.WorkflowKey;
import org.knime.gateway.impl.webui.WorkflowMiddleware;
import org.knime.gateway.impl.webui.service.DefaultEventService;
import org.knime.gateway.impl.webui.service.DefaultWorkflowService;
import org.knime.gateway.impl.webui.service.GatewayServiceTest;
import org.knime.gateway.impl.webui.service.ServiceDependencies;
import org.knime.gateway.impl.webui.service.ServiceInstances;
import org.knime.gateway.testing.helper.TestWorkflowCollection;
import org.knime.testing.util.WorkflowManagerUtil;

/**
 * Tests {@link WorkflowCommands}.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
public class WorkflowCommandsTest extends GatewayServiceTest {

    /**
     * Mainly tests the expected sizes of the undo- and redo-stacks after calling apply, undo, redo or
     * disposeUndoAndRedoStacks.
     */
    @Test
    public void testUndoAndRedoStackSizes() throws Exception {
        WorkflowProject wp = createEmptyWorkflowProject();

        WorkflowCommands commands = new WorkflowCommands(5);
        TranslateCommandEnt commandEntity = builder(TranslateCommandEntBuilder.class).setKind(KindEnum.TRANSLATE)
            .setTranslation(builder(XYEntBuilder.class).setX(10).setY(10).build()).build();
        WorkflowKey wfKey = new WorkflowKey(wp.getID(), NodeIDEnt.getRootID());

        assertThrows(OperationNotAllowedException.class, () -> commands.undo(wfKey));
        assertThrows(OperationNotAllowedException.class, () -> commands.redo(wfKey));

        commands.execute(wfKey, commandEntity);
        commands.execute(wfKey, commandEntity);
        commands.execute(wfKey, commandEntity);
        commands.execute(wfKey, commandEntity);
        commands.execute(wfKey, commandEntity);
        commands.execute(wfKey, commandEntity);
        assertThat(commands.getUndoStackSize(wfKey), is(5));
        assertThat(commands.getRedoStackSize(wfKey), is(0));
        assertThat(commands.canUndo(wfKey), is(true));
        assertThat(commands.canRedo(wfKey), is(false));
        assertThrows(OperationNotAllowedException.class, () -> commands.redo(wfKey));

        commands.undo(wfKey);
        commands.undo(wfKey);
        assertThat(commands.getUndoStackSize(wfKey), is(3));
        assertThat(commands.getRedoStackSize(wfKey), is(2));
        assertThat(commands.canUndo(wfKey), is(true));
        assertThat(commands.canRedo(wfKey), is(true));

        commands.redo(wfKey);
        assertThat(commands.getUndoStackSize(wfKey), is(4));
        assertThat(commands.getRedoStackSize(wfKey), is(1));
        assertThat(commands.canUndo(wfKey), is(true));
        assertThat(commands.canRedo(wfKey), is(true));

        commands.undo(wfKey);
        commands.undo(wfKey);
        commands.undo(wfKey);
        commands.undo(wfKey);
        assertThat(commands.getUndoStackSize(wfKey), is(0));
        assertThat(commands.getRedoStackSize(wfKey), is(5));
        assertThat(commands.canUndo(wfKey), is(false));
        assertThat(commands.canRedo(wfKey), is(true));
        assertThrows(OperationNotAllowedException.class, () -> commands.undo(wfKey));

        assertThrows(OperationNotAllowedException.class,
            () -> commands.execute(null, builder(WorkflowCommandEntBuilder.class).setKind(KindEnum.TRANSLATE).build()));

        commands.redo(wfKey);
        assertThat(commands.getUndoStackSize(wfKey), is(1));
        assertThat(commands.getRedoStackSize(wfKey), is(4));
        commands.disposeUndoAndRedoStacks(wfKey.getProjectId());
        assertThat(commands.getUndoStackSize(wfKey), is(0));
        assertThat(commands.getRedoStackSize(wfKey), is(0));

        disposeWorkflowProject(wp);
    }

    /**
     * Tests that {@link WorkflowCommand#canUndo()} or {@link WorkflowCommand#canRedo()} returns {@code false} for some
     * commands if the workfow is executing (which, e.g., prohibits the deletion of nodes or connections).
     *
     * @throws Exception
     */
    @Test
    public void testUndoAndRedoWhileWorkflowIsExecuting() throws Exception {
        var wp = createEmptyWorkflowProject();
        var wfm = wp.openProject();
        var waitNodeID = WorkflowManagerUtil.createAndAddNode(wfm,
            FileNativeNodeContainerPersistor.loadNodeFactory("org.knime.base.node.flowcontrol.sleep.SleepNodeFactory"))
            .getID();
        configureWaitNode(wfm, waitNodeID);

        var addNodeCommand = new AddNode();
        addNodeCommand.execute(new WorkflowKey(wp.getID(), NodeIDEnt.getRootID()),
            builder(AddNodeCommandEntBuilder.class)
                .setNodeFactory(builder(NodeFactoryKeyEntBuilder.class)
                    .setClassName("org.knime.base.node.util.sampledata.SampleDataNodeFactory").build())
                .setPosition(builder(XYEntBuilder.class).setX(0).setY(0).build()).setKind(KindEnum.ADD_NODE).build());
        assertThat(wfm.getNodeContainers().size(), is(2));
        assertThat(addNodeCommand.canUndo(), is(true));

        var connectCommand = new Connect();
        connectCommand.execute(new WorkflowKey(wp.getID(), NodeIDEnt.getRootID()),
            builder(ConnectCommandEntBuilder.class).setSourceNodeId(new NodeIDEnt(2)).setSourcePortIdx(0)
                .setDestinationNodeId(new NodeIDEnt(1)).setDestinationPortIdx(0).setKind(KindEnum.CONNECT).build());
        assertThat(wfm.getConnectionContainers().size(), is(1));
        assertThat(connectCommand.canUndo(), is(true));

        var deleteCommand = new Delete();
        deleteCommand.execute(new WorkflowKey(wp.getID(), NodeIDEnt.getRootID()), builder(DeleteCommandEntBuilder.class)
            .setNodeIds(List.of(new NodeIDEnt(2))).setKind(KindEnum.DELETE).build());
        assertThat(wfm.getNodeContainers().size(), is(1));
        deleteCommand.undo();
        assertThat(wfm.getNodeContainers().size(), is(2));
        assertThat(deleteCommand.canRedo(), is(true));

        wfm.executeAll();
        assertThat(addNodeCommand.canUndo(), is(false));
        assertThat(connectCommand.canUndo(), is(false));
        assertThat(deleteCommand.canRedo(), is(false));

        wfm.getParent().cancelExecution(wfm);
        Awaitility.await().pollInterval(100, TimeUnit.MILLISECONDS).atMost(1, TimeUnit.MINUTES)
            .until(() -> !wfm.getNodeContainerState().isExecutionInProgress());
        disposeWorkflowProject(wp);
    }

    /**
     * Makes sure that the 'undo'-flag is correctly updated (through a workflow changed event) if a workflow is changed
     * (e.g. a node deleted).
     * There used to be a race condition where the respective event didn't contain the 'undo'-flag update (see NXT-965).
     *
     * @throws Exception
     */
    @Test
    public void testUndoFlagUpdateOnWorkflowChange() throws Exception {
        ServiceDependencies.setServiceDependency(AppStateProvider.class, new AppStateProvider(mock(Supplier.class)));
        ServiceDependencies.setServiceDependency(WorkflowMiddleware.class, WorkflowMiddleware.getInstance());
        Semaphore semaphore = new Semaphore(0);
        AtomicReference<Object> event = new AtomicReference<>();
        EventConsumer eventConsumer = (n, e) -> {
            event.set(e);
            semaphore.release();
        };
        ServiceDependencies.setServiceDependency(EventConsumer.class, eventConsumer);
        String projectId = loadWorkflow(TestWorkflowCollection.EXECUTION_STATES).getFirst().toString();
        var snapshotId =
            DefaultWorkflowService.getInstance().getWorkflow(projectId, NodeIDEnt.getRootID(), true).getSnapshotId();

        DefaultEventService.getInstance()
            .addEventListener(builder(WorkflowChangedEventTypeEntBuilder.class).setProjectId(projectId)
                .setWorkflowId(NodeIDEnt.getRootID()).setSnapshotId(snapshotId).setTypeId("WorkflowChangedEventType")
                .build());
        DefaultWorkflowService.getInstance().executeWorkflowCommand(projectId, NodeIDEnt.getRootID(),
            builder(DeleteCommandEntBuilder.class).setNodeIds(List.of(new NodeIDEnt(1))).setKind(KindEnum.DELETE)
                .build());

        semaphore.acquire();
        try {
            assertThat(((WorkflowChangedEventEnt)event.get()).getPatch().getOps().stream().map(op -> op.getPath())
                .collect(Collectors.toList()), Matchers.hasItem("/allowedActions/canUndo"));
        } finally {
            ServiceInstances.disposeAllServicesInstancesAndDependencies();
        }
    }

    private static void disposeWorkflowProject(final WorkflowProject wp) {
        WorkflowProjectManager.getInstance().removeWorkflowProject(wp.getID());
        WorkflowManager.ROOT.removeProject(wp.openProject().getID());
    }

    private static WorkflowProject createEmptyWorkflowProject() throws IOException {
        File dir = FileUtil.createTempDir("workflow");
        File workflowFile = new File(dir, WorkflowPersistor.WORKFLOW_FILE);
        if (workflowFile.createNewFile()) {
            WorkflowCreationHelper creationHelper = new WorkflowCreationHelper();
            WorkflowContext.Factory fac = new WorkflowContext.Factory(workflowFile.getParentFile());
            creationHelper.setWorkflowContext(fac.createContext());

            WorkflowManager wfm = WorkflowManager.ROOT.createAndAddProject("workflow", creationHelper);
            String id = "wfId";
            WorkflowProject workflowProject = new WorkflowProject() {

                @Override
                public String getName() {
                    return "workflow";
                }

                @Override
                public String getID() {
                    return id;
                }

                @Override
                public WorkflowManager openProject() {
                    return wfm;
                }

            };
            WorkflowProjectManager.getInstance().addWorkflowProject(id, workflowProject);
            return workflowProject;
        } else {
            throw new IllegalStateException("Creating empty workflow failed");
        }
    }

    private static void configureWaitNode(final WorkflowManager wfm, final NodeID waitNodeID)
        throws InvalidSettingsException {
        var ns = new NodeSettings("test");
        wfm.saveNodeSettings(waitNodeID, ns);
        var ms = ns.addNodeSettings("model");
        ms.addInt("wait_option", 0);
        ms.addInt("for_hours", 0);
        ms.addInt("for_minutes", 10);
        ms.addInt("for_seconds", 0);
        wfm.loadNodeSettings(waitNodeID, ns);
    }
}

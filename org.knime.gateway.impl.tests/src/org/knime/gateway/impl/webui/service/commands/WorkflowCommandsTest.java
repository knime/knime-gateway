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

import static org.awaitility.Awaitility.await;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThrows;
import static org.knime.gateway.api.entity.EntityBuilderManager.builder;
import static org.knime.gateway.api.entity.NodeIDEnt.getRootID;
import static org.mockito.Mockito.mock;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.Stack;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import org.awaitility.Awaitility;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeSettings;
import org.knime.core.node.workflow.AnnotationData;
import org.knime.core.node.workflow.FileNativeNodeContainerPersistor;
import org.knime.core.node.workflow.NodeID;
import org.knime.core.node.workflow.WorkflowAnnotationID;
import org.knime.core.node.workflow.WorkflowCreationHelper;
import org.knime.core.node.workflow.WorkflowManager;
import org.knime.core.node.workflow.WorkflowPersistor;
import org.knime.core.node.workflow.contextv2.WorkflowContextV2;
import org.knime.core.util.FileUtil;
import org.knime.gateway.api.entity.EntityBuilderManager;
import org.knime.gateway.api.entity.NodeIDEnt;
import org.knime.gateway.api.webui.entity.AddNodeCommandEnt.AddNodeCommandEntBuilder;
import org.knime.gateway.api.webui.entity.CollapseCommandEnt.CollapseCommandEntBuilder;
import org.knime.gateway.api.webui.entity.CollapseCommandEnt.ContainerTypeEnum;
import org.knime.gateway.api.webui.entity.CommandResultEnt;
import org.knime.gateway.api.webui.entity.CompositeEventEnt;
import org.knime.gateway.api.webui.entity.ConnectCommandEnt.ConnectCommandEntBuilder;
import org.knime.gateway.api.webui.entity.CopyCommandEnt.CopyCommandEntBuilder;
import org.knime.gateway.api.webui.entity.CopyResultEnt;
import org.knime.gateway.api.webui.entity.DeleteCommandEnt;
import org.knime.gateway.api.webui.entity.DeleteCommandEnt.DeleteCommandEntBuilder;
import org.knime.gateway.api.webui.entity.NodeFactoryKeyEnt.NodeFactoryKeyEntBuilder;
import org.knime.gateway.api.webui.entity.PasteCommandEnt.PasteCommandEntBuilder;
import org.knime.gateway.api.webui.entity.TranslateCommandEnt;
import org.knime.gateway.api.webui.entity.TranslateCommandEnt.TranslateCommandEntBuilder;
import org.knime.gateway.api.webui.entity.WorkflowChangedEventEnt;
import org.knime.gateway.api.webui.entity.WorkflowChangedEventTypeEnt.WorkflowChangedEventTypeEntBuilder;
import org.knime.gateway.api.webui.entity.WorkflowCommandEnt;
import org.knime.gateway.api.webui.entity.WorkflowCommandEnt.KindEnum;
import org.knime.gateway.api.webui.entity.WorkflowCommandEnt.WorkflowCommandEntBuilder;
import org.knime.gateway.api.webui.entity.XYEnt.XYEntBuilder;
import org.knime.gateway.api.webui.service.util.ServiceExceptions.OperationNotAllowedException;
import org.knime.gateway.impl.project.DefaultProject;
import org.knime.gateway.impl.project.Project;
import org.knime.gateway.impl.project.ProjectManager;
import org.knime.gateway.impl.service.events.EventConsumer;
import org.knime.gateway.impl.service.util.WorkflowChangesTracker.WorkflowChange;
import org.knime.gateway.impl.webui.AppStateUpdater;
import org.knime.gateway.impl.webui.PreferencesProvider;
import org.knime.gateway.impl.webui.WorkflowKey;
import org.knime.gateway.impl.webui.WorkflowMiddleware;
import org.knime.gateway.impl.webui.service.DefaultEventService;
import org.knime.gateway.impl.webui.service.DefaultWorkflowService;
import org.knime.gateway.impl.webui.service.GatewayServiceTest;
import org.knime.gateway.impl.webui.service.ServiceDependencies;
import org.knime.gateway.impl.webui.service.ServiceInstances;
import org.knime.gateway.impl.webui.spaces.SpaceProviders;
import org.knime.gateway.testing.helper.TestWorkflowCollection;
import org.knime.testing.util.WorkflowManagerUtil;

import junit.framework.AssertionFailedError;

/**
 * Tests {@link WorkflowCommands}.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
public class WorkflowCommandsTest extends GatewayServiceTest {

    @SuppressWarnings("javadoc")
    @Test
    public void testRedoCommandOrder() throws Exception {
        Project wp = createEmptyWorkflowProject();

        WorkflowCommands commands = new WorkflowCommands(5);
        WorkflowMiddleware workflowMiddleware = new WorkflowMiddleware(ProjectManager.getInstance());
        WorkflowKey wfKey = new WorkflowKey(wp.getID(), NodeIDEnt.getRootID());

        var wfm = wp.loadWorkflowManager();
        var sleepNodeClassname = "org.knime.base.node.flowcontrol.sleep.SleepNodeFactory";

        var n1 = addNodeDirectly(sleepNodeClassname, wfm);
        var n2 = addNodeDirectly(sleepNodeClassname, wfm);

        commands.execute(wfKey, buildDeleteCommandEnt(n2), workflowMiddleware, null, null);
        commands.execute(wfKey, buildDeleteCommandEnt(n1), workflowMiddleware, null, null);

        commands.undo(wfKey);
        commands.undo(wfKey);

        commands.redo(wfKey);
        assertFalse("Expect that most recent undo is re-done", wfm.containsNodeContainer(n2));

        commands.redo(wfKey);
        assertFalse("Expect that second redo corresponds to first undo", wfm.containsNodeContainer(n1));

        disposeWorkflowProject(wp);
    }

    /**
     * Mainly tests the expected sizes of the undo- and redo-stacks after calling apply, undo, redo or
     * disposeUndoAndRedoStacks.
     *
     * @throws Exception
     */
    @Test
    public void testUndoAndRedoStackSizes() throws Exception {
        Project wp = createEmptyWorkflowProject();

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
            () -> commands.execute(new WorkflowKey(wp.getID(), NodeIDEnt.getRootID()),
                builder(WorkflowCommandEntBuilder.class).setKind(KindEnum.TRANSLATE).build()));

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
        var wfm = wp.loadWorkflowManager();
        var waitNodeID = WorkflowManagerUtil.createAndAddNode(wfm,
            FileNativeNodeContainerPersistor.loadNodeFactory("org.knime.base.node.flowcontrol.sleep.SleepNodeFactory"))
            .getID();
        configureWaitNode(wfm, waitNodeID);
        var wfKey = new WorkflowKey(wp.getID(), NodeIDEnt.getRootID());

        var addCommandEnt = builder(AddNodeCommandEntBuilder.class)
            .setNodeFactory(builder(NodeFactoryKeyEntBuilder.class)
                .setClassName("org.knime.base.node.util.sampledata.SampleDataNodeFactory").build())
            .setPosition(builder(XYEntBuilder.class).setX(0).setY(0).build()).setKind(KindEnum.ADD_NODE).build();
        var addNodeCommand = new AddNode(addCommandEnt);
        addNodeCommand.execute(wfKey);
        assertThat(wfm.getNodeContainers().size(), is(2));
        assertThat(addNodeCommand.canUndo(), is(true));

        var connectCommandEnt =
            builder(ConnectCommandEntBuilder.class).setSourceNodeId(new NodeIDEnt(2)).setSourcePortIdx(0)
                .setDestinationNodeId(new NodeIDEnt(1)).setDestinationPortIdx(0).setKind(KindEnum.CONNECT).build();
        var connectCommand = new Connect(connectCommandEnt);
        connectCommand.execute(wfKey);

        assertThat(wfm.getConnectionContainers().size(), is(1));
        assertThat(connectCommand.canUndo(), is(true));

        var deleteCommandEnt = builder(DeleteCommandEntBuilder.class).setNodeIds(List.of(new NodeIDEnt(2)))
            .setKind(KindEnum.DELETE).build();
        var deleteCommand = new Delete(deleteCommandEnt);
        deleteCommand.execute(wfKey);
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
    @SuppressWarnings("unchecked")
    @Test
    public void testUndoFlagUpdateOnWorkflowChange() throws Exception {
        ServiceDependencies.setServiceDependency(AppStateUpdater.class, new AppStateUpdater());
        ServiceDependencies.setServiceDependency(WorkflowMiddleware.class,
            new WorkflowMiddleware(ProjectManager.getInstance()));
        ServiceDependencies.setServiceDependency(ProjectManager.class, ProjectManager.getInstance());
        ServiceDependencies.setServiceDependency(PreferencesProvider.class, mock(PreferencesProvider.class));
        ServiceDependencies.setServiceDependency(SpaceProviders.class, mock(SpaceProviders.class));

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
        assertThat(((WorkflowChangedEventEnt)((CompositeEventEnt)event.get()).getEvents().get(0)).getPatch().getOps()
            .stream().map(op -> op.getPath()).collect(Collectors.toList()),
            Matchers.hasItem("/allowedActions/canUndo"));
    }

    static void disposeWorkflowProject(final Project wp) {
        ProjectManager.getInstance().removeProject(wp.getID(), WorkflowManagerUtil::disposeWorkflow);
    }

    static Project createEmptyWorkflowProject() throws IOException {
        File dir = FileUtil.createTempDir("workflow");
        File workflowFile = new File(dir, WorkflowPersistor.WORKFLOW_FILE);
        if (workflowFile.createNewFile()) {
            WorkflowManager wfm = WorkflowManager.ROOT.createAndAddProject("workflow", new WorkflowCreationHelper(
                WorkflowContextV2.forTemporaryWorkflow(workflowFile.getParentFile().toPath(), null)));
            String id = "wfId";
            Project workflowProject = DefaultProject.builder(wfm).setId(id).setName("workflow").build();
            ProjectManager.getInstance().addProject(workflowProject);
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

    /**
     * Add a node to the given workflow manager directly via {@link WorkflowManager} API (not using commands).
     * @param nodeFactoryClassname The factory classname of the node to add
     * @param wfm The workflow manager to add the node to
     * @return The ID of the newly added node in the workflow manager
     * @throws Exception If anything goes wrong
     */
    private static NodeID addNodeDirectly(final String nodeFactoryClassname, final WorkflowManager wfm)
        throws Exception {
        return WorkflowManagerUtil
            .createAndAddNode(wfm, FileNativeNodeContainerPersistor.loadNodeFactory(nodeFactoryClassname)).getID();
    }

    private static DeleteCommandEnt buildDeleteCommandEnt(final NodeID nodeToDelete) {
        return buildDeleteCommandEnt(List.of(nodeToDelete));
    }

    private static DeleteCommandEnt buildDeleteCommandEnt(final List<NodeID> nodesToDelete) {
        return builder(DeleteCommandEntBuilder.class) //
            .setNodeIds(nodesToDelete.stream().map(NodeIDEnt::new).collect(Collectors.toList())) //
            .setKind(KindEnum.DELETE) //
            .build();
    }

    /**
     * Makes sure that {@link WorkflowCommands#canUndo(WorkflowKey)} and {@link WorkflowCommands#canRedo(WorkflowKey)}
     * are strictly called after
     * {@link WorkflowCommands#execute(WorkflowKey, org.knime.gateway.api.webui.entity.WorkflowCommandEnt)},
     * {@link WorkflowCommands#undo(WorkflowKey)}, or {@link WorkflowCommands#redo(WorkflowKey)} (and never while
     * 'command-execution' in progress).
     *
     * @throws Exception
     */
    @Test
    public void testCanUndoAndCanRedoCalledAfterExecuteUndoAndRedo() throws Exception {
        var wfId = loadWorkflow(TestWorkflowCollection.HOLLOW).getFirst().toString();

        ServiceDependencies.setServiceDependency(AppStateUpdater.class, new AppStateUpdater());
        ServiceDependencies.setServiceDependency(ProjectManager.class, ProjectManager.getInstance());
        ServiceDependencies.setServiceDependency(PreferencesProvider.class, mock(PreferencesProvider.class));
        var workflowMiddleware = new WorkflowMiddleware(ProjectManager.getInstance());
        var commands = workflowMiddleware.getCommands();
        var wfKey = new WorkflowKey(wfId, getRootID());
        AtomicInteger eventConsumerCalls = new AtomicInteger();
        EventConsumer eventConsumer = (n, e) -> {
            eventConsumerCalls.addAndGet(1);
            commands.canRedo(wfKey);
            commands.canUndo(wfKey);
        };
        ServiceDependencies.setServiceDependency(EventConsumer.class, eventConsumer);
        ServiceDependencies.setServiceDependency(WorkflowMiddleware.class, workflowMiddleware);
        ServiceDependencies.setServiceDependency(SpaceProviders.class, mock(SpaceProviders.class));

        var snapshotId = DefaultWorkflowService.getInstance()
            .getWorkflow(wfId, getRootID(), true).getSnapshotId();
        var eventType = EntityBuilderManager.builder(WorkflowChangedEventTypeEntBuilder.class)
            .setProjectId(wfId).setWorkflowId(getRootID()).setSnapshotId(snapshotId)
            .setTypeId("WorkflowChangedEventType").build();
        DefaultEventService.getInstance().addEventListener(eventType);

        var testCommand = new TestWorkflowCommand();
        commands.setCommandToExecute(testCommand);

        try {
            commands.execute(wfKey, null, workflowMiddleware, null, null);
            await().pollInterval(200, TimeUnit.MILLISECONDS).atMost(2, TimeUnit.SECONDS)
                .until(() -> eventConsumerCalls.get() == 1);

            testCommand.m_executeFinished = false;
            commands.undo(wfKey);
            await().pollInterval(200, TimeUnit.MILLISECONDS).atMost(2, TimeUnit.SECONDS)
                .until(() -> eventConsumerCalls.get() == 2);

            testCommand.m_executeFinished = false;
            commands.redo(wfKey);
            await().pollInterval(200, TimeUnit.MILLISECONDS).atMost(2, TimeUnit.SECONDS)
                .until(() -> eventConsumerCalls.get() == 3);
        } finally {
            ServiceInstances.disposeAllServiceInstancesAndDependencies();
        }
    }

    private class TestWorkflowCommand extends AbstractWorkflowCommand implements WithResult {

        private boolean m_executeFinished = false;

        private WorkflowAnnotationID m_annoID;

        @Override
        public boolean canRedo() {
            if (!m_executeFinished) {
                throw new AssertionFailedError("Unexpected 'canRedo'-call during command execution");
            }
            return true;
        }

        @Override
        public boolean canUndo() {
            if (!m_executeFinished) {
                throw new AssertionFailedError("Unexpected 'canUndo'-call during command execution");
            }
            return true;
        }

        @Override
        public void undo() throws OperationNotAllowedException {
            getWorkflowManager().removeAnnotation(m_annoID);
            m_annoID = null;
            sleep();
            m_executeFinished = true;
        }

        @Override
        public void redo() throws OperationNotAllowedException {
            executeWithLockedWorkflow();
        }

        @Override
        protected boolean executeWithLockedWorkflow() throws OperationNotAllowedException {
            // modify workflow to trigger an event (which is sent asynchronously)
            // which in turn creates a workflow patch
            // which in turn call the 'canUndo' and 'canRedo' methods of WorkflowCommands
            m_annoID = getWorkflowManager().addWorkflowAnnotation(new AnnotationData(), -1).getID();
            sleep();
            m_executeFinished = true;
            return true;
        }

        private void sleep() {
            try {
                Thread.sleep(200);
            } catch (InterruptedException ex) {
                //
            }
        }

        @Override
        public Set<WorkflowChange> getChangesToWaitFor() {
            return Set.of(WorkflowChange.ANNOTATION_ADDED, WorkflowChange.ANNOTATION_REMOVED);
        }

        @Override
        public CommandResultEnt buildEntity(final String snapshotId) {
            return null;
        }

    }

    /**
     * Test that commands yielding results return the latest snapshot id correctly, so synchronization works as expected
     *
     * @throws Exception
     */
    @Test
    public void testWaitForCommandResultReturnsLatestSnapshotId() throws Exception {
        ServiceDependencies.setServiceDependency(AppStateUpdater.class, new AppStateUpdater());
        ServiceDependencies.setServiceDependency(ProjectManager.class, ProjectManager.getInstance());
        ServiceDependencies.setServiceDependency(PreferencesProvider.class, mock(PreferencesProvider.class));
        ServiceDependencies.setServiceDependency(SpaceProviders.class, mock(SpaceProviders.class));

        Stack<CommandResultEnt> results = new Stack<>();
        Stack<WorkflowChangedEventEnt> events = new Stack<>();
        ServiceDependencies.setServiceDependency(EventConsumer.class,
            (n, e) -> events.push((WorkflowChangedEventEnt)((CompositeEventEnt)e).getEvents().get(0)));
        ServiceDependencies.setServiceDependency(WorkflowMiddleware.class,
            new WorkflowMiddleware(ProjectManager.getInstance()));

        var projectId = loadWorkflow(TestWorkflowCollection.GENERAL_WEB_UI).getFirst().toString();
        var snapshotId = DefaultWorkflowService.getInstance().getWorkflow(projectId, getRootID(), true).getSnapshotId();
        var eventType = EntityBuilderManager.builder(WorkflowChangedEventTypeEntBuilder.class).setProjectId(projectId)
            .setWorkflowId(getRootID()).setSnapshotId(snapshotId).setTypeId("WorkflowChangedEventType").build();
        DefaultEventService.getInstance().addEventListener(eventType);

        // Collapse command, changes the workflow, emits `WorkflowChangedEventEnt`
        var command1 = builder(CollapseCommandEntBuilder.class)//
            .setContainerType(ContainerTypeEnum.METANODE)//
            .setKind(KindEnum.COLLAPSE)//
            .setNodeIds(List.of(new NodeIDEnt(4)))//
            .build();
        executeCommandAndAssertSnapshotIdCorrect(command1, projectId, results, events);

        // Copy command, doesn't change the workflow, yields a result but doesn't emit a `WorkflowChangedEventEnt`
        var command2 = builder(CopyCommandEntBuilder.class)//
            .setKind(KindEnum.COPY)//
            .setNodeIds(List.of(new NodeIDEnt(1)))//
            .build();
        executeCommandAndAssertSnapshotIdCorrect(command2, projectId, results, events);

        // Paste command, changes the workflow, emits `WorkflowChangedEventEnt`
        var command3 = builder(PasteCommandEntBuilder.class)//
            .setKind(KindEnum.PASTE)//
            .setContent(((CopyResultEnt)results.peek()).getContent())//
            .build();
        executeCommandAndAssertSnapshotIdCorrect(command3, projectId, results, events);

        assertEquals("3 command results received", 3, results.size());
        assertEquals("Only 2 workflow changed events received", 2, events.size());
        ServiceInstances.disposeAllServiceInstancesAndDependencies();
    }

    private static void executeCommandAndAssertSnapshotIdCorrect(final WorkflowCommandEnt command,
        final String projectId, final Stack<CommandResultEnt> results, final Stack<WorkflowChangedEventEnt> events)
        throws Exception {
        var result =
            DefaultWorkflowService.getInstance().executeWorkflowCommand(projectId, NodeIDEnt.getRootID(), command);
        var snapshotId = result.getSnapshotId();
        if (snapshotId != null) {
            assertEquals("Snapshot id returned = top most snapshot id on the events stack", snapshotId,
                events.peek().getSnapshotId());
        }
        results.push(result);
    }

    @Override
    public void setupServiceDependencies() {
        // prevent service dependencies from being set
    }

}

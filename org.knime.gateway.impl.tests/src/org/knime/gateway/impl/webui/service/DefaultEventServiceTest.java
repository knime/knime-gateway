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

import static org.knime.gateway.api.entity.EntityBuilderManager.builder;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.awaitility.Awaitility;
import org.awaitility.Duration;
import org.junit.Test;
import org.knime.core.node.workflow.WorkflowManager;
import org.knime.core.util.Pair;
import org.knime.gateway.api.entity.NodeIDEnt;
import org.knime.gateway.api.webui.entity.AppStateChangedEventTypeEnt;
import org.knime.gateway.api.webui.entity.AppStateChangedEventTypeEnt.AppStateChangedEventTypeEntBuilder;
import org.knime.gateway.api.webui.entity.DeleteCommandEnt.DeleteCommandEntBuilder;
import org.knime.gateway.api.webui.entity.PatchEnt.PatchEntBuilder;
import org.knime.gateway.api.webui.entity.PatchOpEnt.OpEnum;
import org.knime.gateway.api.webui.entity.PatchOpEnt.PatchOpEntBuilder;
import org.knime.gateway.api.webui.entity.SpaceItemChangedEventEnt;
import org.knime.gateway.api.webui.entity.SpaceItemChangedEventEnt.SpaceItemChangedEventEntBuilder;
import org.knime.gateway.api.webui.entity.SpaceItemChangedEventTypeEnt;
import org.knime.gateway.api.webui.entity.SpaceItemChangedEventTypeEnt.SpaceItemChangedEventTypeEntBuilder;
import org.knime.gateway.api.webui.entity.SpaceProviderEnt.TypeEnum;
import org.knime.gateway.api.webui.entity.WorkflowChangedEventTypeEnt;
import org.knime.gateway.api.webui.entity.WorkflowChangedEventTypeEnt.WorkflowChangedEventTypeEntBuilder;
import org.knime.gateway.api.webui.entity.WorkflowCommandEnt.KindEnum;
import org.knime.gateway.api.webui.entity.WorkflowMonitorMessageEnt.WorkflowMonitorMessageEntBuilder;
import org.knime.gateway.api.webui.entity.WorkflowMonitorStateChangeEventEnt;
import org.knime.gateway.api.webui.entity.WorkflowMonitorStateChangeEventEnt.WorkflowMonitorStateChangeEventEntBuilder;
import org.knime.gateway.api.webui.entity.WorkflowMonitorStateChangeEventTypeEnt;
import org.knime.gateway.api.webui.entity.WorkflowMonitorStateChangeEventTypeEnt.WorkflowMonitorStateChangeEventTypeEntBuilder;
import org.knime.gateway.api.webui.entity.WorkflowSnapshotEnt;
import org.knime.gateway.api.webui.service.EventService;
import org.knime.gateway.api.webui.service.util.ServiceExceptions.InvalidRequestException;
import org.knime.gateway.impl.webui.service.events.EventConsumer;
import org.knime.gateway.impl.webui.spaces.SpaceProvider;
import org.knime.gateway.impl.webui.spaces.SpaceProviders;
import org.knime.gateway.testing.helper.TestWorkflowCollection;
import org.knime.gateway.testing.helper.WorkflowTransformations;
import org.knime.gateway.testing.helper.webui.SpaceServiceTestHelper;
import org.mockito.Mockito;

/**
 * Tests regarding the {@link DefaultEventService}.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
public class DefaultEventServiceTest extends GatewayServiceTest {

    private static final String PROVIDER_ID = "provider";

    private final EventConsumer m_testConsumer = mock(EventConsumer.class);

    private final DummyNotifier m_notifier = new DummyNotifier();

    @Override
    protected EventConsumer createEventConsumer() {
        return m_testConsumer;
    }

    /**
     * Tests that no more listeners are registered with the workflow once they have been removed.
     *
     * @throws Exception
     */
    @Test
    public void testWorkflowChangedEventsRemovedListeners() throws Exception {
        Pair<UUID, WorkflowManager> idAndWfm = loadWorkflow(TestWorkflowCollection.GENERAL_WEB_UI);
        WorkflowChangedEventTypeEnt eventType =
            registerWorkflowChangedEventListener(idAndWfm.getFirst().toString(), NodeIDEnt.getRootID());

        DefaultEventService es = DefaultEventService.getInstance();

        // remove event listener again
        es.removeEventListener(eventType);

        checkThatNoEventsAreSent(idAndWfm.getSecond());
    }

    /**
     * Tests {@link DefaultEventService#removeAllEventListeners()}.
     *
     * @throws Exception
     */
    @Test
    public void testRemoveAllEventListeners() throws Exception {
        Pair<UUID, WorkflowManager> idAndWfm = loadWorkflow(TestWorkflowCollection.GENERAL_WEB_UI);
        WorkflowChangedEventTypeEnt eventType =
            registerWorkflowChangedEventListener(idAndWfm.getFirst().toString(), NodeIDEnt.getRootID());

        DefaultEventService es = DefaultEventService.getInstance();

        // add one more event listener
        es.addEventListener(eventType);

        es.removeAllEventListeners();

        checkThatNoEventsAreSent(idAndWfm.getSecond());
    }

    static WorkflowChangedEventTypeEnt registerWorkflowChangedEventListener(final String projectId,
        final NodeIDEnt wfId) throws Exception {
        DefaultWorkflowService ws = DefaultWorkflowService.getInstance();
        DefaultEventService es = DefaultEventService.getInstance();

        // get the current workflow state and register event listener
        // (such that change events are send for that workflow)
        WorkflowSnapshotEnt wf = ws.getWorkflow(projectId, wfId, Boolean.TRUE);
        WorkflowChangedEventTypeEnt eventType = builder(WorkflowChangedEventTypeEntBuilder.class)
            .setProjectId(projectId).setWorkflowId(wfId).setSnapshotId(wf.getSnapshotId()).build();
        es.addEventListener(eventType);
        return eventType;
    }

    private void checkThatNoEventsAreSent(final WorkflowManager wfm) {
        // set empty callback for testing
        DefaultEventService es = DefaultEventService.getInstance();
        es.setPreEventCreationCallbackForTesting(null);

        // carry out the workflow changes
        WorkflowTransformations.createWorkflowTransformations(TestWorkflowCollection.GENERAL_WEB_UI)
            .forEach(t -> t.apply(wfm));

        // check that there weren't any events
        verify(m_testConsumer, times(0)).accept(any(), any());
    }

    /**
     * Makes sure that {@link EventService#addEventListener(org.knime.gateway.api.webui.entity.EventTypeEnt)} doesn't
     * cause an event to be emitted for event type {@link AppStateChangedEventTypeEnt}.
     *
     * @throws InvalidRequestException
     */
    @Test
    public void testNoEventsEmittedOnAddingAppStateChangedEventListener() throws InvalidRequestException {
        var es = DefaultEventService.getInstance();
        es.addEventListener(builder(AppStateChangedEventTypeEntBuilder.class).build());
        verify(m_testConsumer, never()).accept(any(), any());
    }

    /**
     * Tests {@link EventService#addEventListener(org.knime.gateway.api.webui.entity.EventTypeEnt)} for the
     * {@link WorkflowMonitorStateChangeEventTypeEnt}.
     *
     * @throws Exception
     */
    @Test
    public void testWorkflowMonitorStateChangedEventListener() throws Exception {
        var idAndWfm = loadWorkflow(TestWorkflowCollection.NODE_MESSAGE);
        var projectId = idAndWfm.getFirst().toString();

        var ws = DefaultWorkflowService.getInstance();
        var monitorStateSnapshot = ws.getWorkflowMonitorState(projectId);
        cr(monitorStateSnapshot.getState(), "initial_workflow_monitor_state");

        var es = DefaultEventService.getInstance();
        es.addEventListener(builder(WorkflowMonitorStateChangeEventTypeEntBuilder.class).setProjectId(projectId)
            .setSnapshotId(monitorStateSnapshot.getSnapshotId()).build());
        var ns = DefaultNodeService.getInstance();

        // error message added
        ns.changeNodeStates(projectId, NodeIDEnt.getRootID(), List.of(new NodeIDEnt(6)), "execute");
        var expectedEvent1 = buildEvent(OpEnum.ADD, "/errors/0", "Execute failed: This node fails on each execution.",
            "Fail in execution", NodeIDEnt.getRootID(), "org.knime.testing.node.failing.FailingNodeFactory",
            new NodeIDEnt(6));
        Awaitility.waitAtMost(Duration.FIVE_SECONDS).await()
            .untilAsserted(() -> verify(m_testConsumer).accept(eq("WorkflowMonitorStateChangeEvent"), argThat(e -> {
                return expectedEvent1.equals(e);
            }), eq(projectId)));

        // error message removed
        ns.changeNodeStates(projectId, NodeIDEnt.getRootID(), List.of(new NodeIDEnt(6)), "reset");
        var expectedEvent2 = buildEvent(OpEnum.REMOVE, "/errors/0", null, null, null, null, null);
        Awaitility.waitAtMost(Duration.FIVE_SECONDS).await()
            .untilAsserted(() -> verify(m_testConsumer).accept(eq("WorkflowMonitorStateChangeEvent"), argThat(e -> {
                return expectedEvent2.equals(e);
            }), eq(projectId)));

        // error message within component added
        ns.changeNodeStates(projectId, new NodeIDEnt(8), List.of(new NodeIDEnt(8, 0, 7)), "execute");
        var expectedEvent3 = buildEvent(OpEnum.ADD, "/errors/0", "Execute failed: This node fails on each execution.",
            "Fail in execution", new NodeIDEnt(8), "org.knime.testing.node.failing.FailingNodeFactory",
            new NodeIDEnt(8, 0, 7));
        Awaitility.waitAtMost(Duration.FIVE_SECONDS).await()
            .untilAsserted(() -> verify(m_testConsumer).accept(eq("WorkflowMonitorStateChangeEvent"), argThat(e -> {
                return expectedEvent3.equals(e);
            }), eq(projectId)));

        // warning message removed due to node removal
        DefaultWorkflowService.getInstance().executeWorkflowCommand(projectId, NodeIDEnt.getRootID(),
            builder(DeleteCommandEntBuilder.class).setKind(KindEnum.DELETE).setNodeIds(List.of(new NodeIDEnt(9)))
                .build());
        var expectedEvent4 = buildEvent(OpEnum.REMOVE, "/warnings/0", null, null, null, null, null);
        Awaitility.waitAtMost(Duration.FIVE_SECONDS).await()
            .untilAsserted(() -> verify(m_testConsumer).accept(eq("WorkflowMonitorStateChangeEvent"), argThat(e -> {
                return expectedEvent4.equals(e);
            }), eq(projectId)));

        // remove event listener
        es.removeEventListener(builder(WorkflowMonitorStateChangeEventTypeEntBuilder.class).setProjectId(projectId)
            .setSnapshotId("unused").build());
        Mockito.clearInvocations(m_testConsumer);
        ns.changeNodeStates(projectId, NodeIDEnt.getRootID(), Collections.emptyList(), "execute");
        Awaitility.await().until(() -> !ws.getWorkflowMonitorState(projectId).getState().getErrors().isEmpty());
        verify(m_testConsumer, times(0)).accept(any(), any(), any());
        verify(m_testConsumer, times(0)).accept(any(), any());
    }

    private static WorkflowMonitorStateChangeEventEnt buildEvent(final OpEnum opEnum, final String path,
        final String message, final String nodeName, final NodeIDEnt workflowId, final String templateId,
        final NodeIDEnt nodeId) {
        var value = message == null ? null : builder(WorkflowMonitorMessageEntBuilder.class).setMessage(message)
            .setName(nodeName).setNodeId(nodeId).setTemplateId(templateId).setWorkflowId(workflowId).build();
        var op = builder(PatchOpEntBuilder.class).setOp(opEnum).setPath(path).setValue(value).build();
        var ops = List.of(op);
        var patch = builder(PatchEntBuilder.class).setOps(ops).build();
        return builder(WorkflowMonitorStateChangeEventEntBuilder.class).setPatch(patch).build();
    }

    /**
     * Tests {@link EventService#addEventListener(org.knime.gateway.api.webui.entity.EventTypeEnt)} for the
     * {@link SpaceItemChangedEventTypeEnt}.
     */
    @Test
    public void testSpaceItemChangedEventListener() throws Exception {

        // No listener, no event
        m_notifier.notifyEventListeners();
        verify(m_testConsumer, times(0)).accept(any(), any());

        var eventService = DefaultEventService.getInstance();
        var eventType1 = buildEventTypeEnt(PROVIDER_ID, "spaceId1", "itemId1");
        eventService.addEventListener(eventType1);

        // One listener, one event
        m_notifier.notifyEventListeners();
        var expectedEvent1 = buildEventEnt(eventType1);
        verify(m_testConsumer, times(1)).accept("SpaceItemChangedEvent", expectedEvent1, null);

        var eventType2 = buildEventTypeEnt(PROVIDER_ID, "spaceId2", "itemId2");
        eventService.addEventListener(eventType2);
        Mockito.clearInvocations(m_testConsumer);

        // Two listeners, two events
        m_notifier.notifyEventListeners();
        verify(m_testConsumer, times(2)).accept(eq("SpaceItemChangedEvent"), any(), any());

        eventService.removeEventListener(eventType1);
        eventService.removeEventListener(eventType2);
        Mockito.clearInvocations(m_testConsumer);

        // No listener, no event
        m_notifier.notifyEventListeners();
        verify(m_testConsumer, times(0)).accept(any(), any(), any());
    }

    @Override
    protected SpaceProviders createSpaceProviders() {
        var provider = mock(SpaceProvider.class);
        when(provider.getId()).thenReturn(PROVIDER_ID);
        when(provider.getType()).thenReturn(TypeEnum.HUB);
        when(provider.getChangeNotifier()).thenReturn(Optional.of(m_notifier));
        return SpaceServiceTestHelper.createSpaceProviders(provider);
    }

    private static final class DummyNotifier implements SpaceProvider.SpaceItemChangeNotifier {

        private final Map<Pair<String, String>, Runnable> m_listeners = new HashMap<>();

        @Override
        public void subscribeToItem(final String space, final String item, final Runnable callback) {
            m_listeners.put(new Pair<>(space, item), callback);
        }

        @Override
        public void unsubscribe(final String spaceId, final String itemId) {
            m_listeners.remove(new Pair<>(spaceId, itemId));
        }

        @Override
        public void unsubscribeAll() {
            m_listeners.clear();
        }

        /**
         * Called from tests to simulate a change happening.
         */
        public void notifyEventListeners() {
            m_listeners.values().forEach(Runnable::run);
        }
    }

    private static SpaceItemChangedEventTypeEnt buildEventTypeEnt(final String providerId, final String spaceId,
        final String itemId) {
        return builder(SpaceItemChangedEventTypeEntBuilder.class) //
            .setProviderId(providerId) //
            .setSpaceId(spaceId) //
            .setItemId(itemId) //
            .build();
    }

    private static SpaceItemChangedEventEnt buildEventEnt(final SpaceItemChangedEventTypeEnt eventTypeEnt) {
        return builder(SpaceItemChangedEventEntBuilder.class) //
            .setProviderId(eventTypeEnt.getProviderId()) //
            .setSpaceId(eventTypeEnt.getSpaceId()) //
            .setItemId(eventTypeEnt.getItemId()) //
            .build();
    }

}

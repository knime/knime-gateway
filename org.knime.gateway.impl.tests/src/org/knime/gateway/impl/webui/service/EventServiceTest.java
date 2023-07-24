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

import java.util.List;
import java.util.UUID;

import org.awaitility.Awaitility;
import org.junit.Test;
import org.knime.core.node.workflow.WorkflowManager;
import org.knime.core.util.Pair;
import org.knime.gateway.api.entity.NodeIDEnt;
import org.knime.gateway.api.webui.entity.AppStateChangedEventTypeEnt;
import org.knime.gateway.api.webui.entity.AppStateChangedEventTypeEnt.AppStateChangedEventTypeEntBuilder;
import org.knime.gateway.api.webui.entity.SelectionEventTypeEnt;
import org.knime.gateway.api.webui.entity.SelectionEventTypeEnt.SelectionEventTypeEntBuilder;
import org.knime.gateway.api.webui.entity.WorkflowChangedEventTypeEnt;
import org.knime.gateway.api.webui.entity.WorkflowChangedEventTypeEnt.WorkflowChangedEventTypeEntBuilder;
import org.knime.gateway.api.webui.entity.WorkflowSnapshotEnt;
import org.knime.gateway.api.webui.service.EventService;
import org.knime.gateway.api.webui.service.util.ServiceExceptions.InvalidRequestException;
import org.knime.gateway.impl.service.events.EventConsumer;
import org.knime.gateway.impl.service.events.SelectionEvent;
import org.knime.gateway.impl.service.events.SelectionEventSource.SelectionEventMode;
import org.knime.gateway.testing.helper.TestWorkflowCollection;
import org.knime.gateway.testing.helper.WorkflowTransformations;

/**
 * Tests regarding the {@link DefaultEventService}.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
public class EventServiceTest extends GatewayServiceTest {

    private final EventConsumer m_testConsumer = mock(EventConsumer.class);

    @Override
    protected EventConsumer createEventConsumer() {
        return m_testConsumer;
    }

    /**
     * Tests that no more listeners are registered with the workflow once they have been removed.
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

    static WorkflowChangedEventTypeEnt registerWorkflowChangedEventListener(final String projectId, final NodeIDEnt wfId)
        throws Exception {
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
     * Tests {@link EventService#addEventListener(org.knime.gateway.api.webui.entity.EventTypeEnt)} for
     * {@link SelectionEventTypeEnt}s.
     *
     * @throws Exception
     */
    @Test
    public void testSelectionEventListener() throws Exception {
        var idAndWfm = loadWorkflow(TestWorkflowCollection.VIEW_NODES);
        var projectId = idAndWfm.getFirst().toString();
        var wfm = idAndWfm.getSecond();
        wfm.executeAllAndWaitUntilDone();

        DefaultEventService es = DefaultEventService.getInstance();
        DefaultNodeService ns = DefaultNodeService.getInstance();

        // start within some initial selection
        ns.updateDataPointSelection(projectId, NodeIDEnt.getRootID(), new NodeIDEnt(15),
            "add", List.of("Row2"));

        SelectionEventTypeEnt eventType = builder(SelectionEventTypeEntBuilder.class) //
            .setProjectId(projectId) //
            .setWorkflowId(NodeIDEnt.getRootID()) //
            .setNodeId(new NodeIDEnt(1)) //
            .build();
        es.addEventListener(eventType);

        // do a selection and check event consumer
        ns.updateDataPointSelection(projectId, NodeIDEnt.getRootID(), new NodeIDEnt(15), "add",
            List.of("Row0", "Row5"));
        verify(m_testConsumer).accept(eq("SelectionEvent"), argThat(e -> {
            var se = (SelectionEvent)e;
            return se.getNodeId().equals("root:1") && //
            se.getSelection().equals(List.of("Row2")) && //
            se.getMode() == SelectionEventMode.ADD;//
        }));
        Awaitility.await().untilAsserted(() -> verify(m_testConsumer).accept(eq("SelectionEvent"), argThat(e -> {
            var se = (SelectionEvent)e;
            return se.getNodeId().equals("root:1") && //
            se.getSelection().equals(List.of("Row0", "Row5")) && //
            se.getMode() == SelectionEventMode.ADD;//
        })));

        // remove selection event listener and check event consumer
        es.removeEventListener(eventType);
        ns.updateDataPointSelection(projectId, NodeIDEnt.getRootID(), new NodeIDEnt(15), "add", List.of("Row6"));
        verify(m_testConsumer, never()).accept(eq("SelectionEvent"),
            argThat(e -> ((SelectionEvent)e).getSelection().equals(List.of("Row6"))));
    }

}

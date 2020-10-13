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

import static org.awaitility.Awaitility.await;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.knime.gateway.api.entity.EntityBuilderManager.builder;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;

import org.junit.Test;
import org.knime.core.node.workflow.WorkflowAnnotation;
import org.knime.core.node.workflow.WorkflowListener;
import org.knime.core.node.workflow.WorkflowManager;
import org.knime.core.util.Pair;
import org.knime.gateway.api.entity.NodeIDEnt;
import org.knime.gateway.api.webui.entity.EventEnt;
import org.knime.gateway.api.webui.entity.WorkflowChangedEventEnt;
import org.knime.gateway.api.webui.entity.WorkflowChangedEventTypeEnt;
import org.knime.gateway.api.webui.entity.WorkflowChangedEventTypeEnt.WorkflowChangedEventTypeEntBuilder;
import org.knime.gateway.api.webui.entity.WorkflowSnapshotEnt;
import org.knime.gateway.testing.helper.TestWorkflowCollection;
import org.knime.gateway.testing.helper.WorkflowTransformations;
import org.knime.gateway.testing.helper.WorkflowTransformations.WorkflowTransformation;
import org.mockito.ArgumentCaptor;

/**
 * Tests for push events such as workflow changes.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
public class EventServiceTest extends GatewayServiceTest {

    /**
     * New test.
     */
    public EventServiceTest() {
        super("eventservice");
    }

    /**
     * Tests that the expected workflow change events are issued by the event service for certain changes to the
     * workflow manager.
     *
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    @Test
    public void testWorkflowChangedEvents() throws Exception {
        Pair<UUID, WorkflowManager> idAndWfm = loadWorkflow(TestWorkflowCollection.GENERAL_WEB_UI);

        DefaultWorkflowService ws = DefaultWorkflowService.getInstance();
        DefaultEventService es = DefaultEventService.getInstance();

        // get the current workflow state and register event listener
        // (such that change events are send for that workflow)
        WorkflowSnapshotEnt wf = ws.getWorkflow(idAndWfm.getFirst().toString(), NodeIDEnt.getRootID());
        WorkflowChangedEventTypeEnt eventType =
            builder(WorkflowChangedEventTypeEntBuilder.class).setProjectId(idAndWfm.getFirst().toString())
                .setWorkflowId(NodeIDEnt.getRootID()).setSnapshotId(wf.getSnapshotId()).build();
        es.addEventListener(eventType);

        // add event consumer to receive and check the change events
        BiConsumer<String, EventEnt> eventConsumerMock = mock(BiConsumer.class);
        es.addEventConsumer(eventConsumerMock);

        WorkflowManager wfm = idAndWfm.getSecond();
        checkWorkflowChangeEvents(wfm, eventConsumerMock, wf.getSnapshotId(),
            WorkflowTransformations.createWorkflowTransformations());

        // remove event listener and check successful removal
        reset(eventConsumerMock);
        es.removeEventListener(eventType);
        WorkflowListener wfListenerMock = mock(WorkflowListener.class);
        wfm.addListener(wfListenerMock); // listener in order to wait for the wf-events to be broadcasted
        wfm.addWorkflowAnnotation(new WorkflowAnnotation());
        await().atMost(2, TimeUnit.SECONDS).pollInterval(100, TimeUnit.MILLISECONDS)
            .untilAsserted(() -> verify(wfListenerMock, times(1)).workflowChanged(any()));
        verify(eventConsumerMock, times(0)).accept(any(), any());
    }

    /**
     * Tests that no more listeners are registered with the workflow once they have been removed.
     * @throws Exception
     */
    @Test
    public void testWorkflowChangedEventsRemovedListeners() throws Exception {
        Pair<UUID, WorkflowManager> idAndWfm = loadWorkflow(TestWorkflowCollection.GENERAL_WEB_UI);

        DefaultWorkflowService ws = DefaultWorkflowService.getInstance();
        DefaultEventService es = DefaultEventService.getInstance();

        // get the current workflow state
        WorkflowSnapshotEnt wf = ws.getWorkflow(idAndWfm.getFirst().toString(), NodeIDEnt.getRootID());
        WorkflowChangedEventTypeEnt eventType =
            builder(WorkflowChangedEventTypeEntBuilder.class).setProjectId(idAndWfm.getFirst().toString())
                .setWorkflowId(NodeIDEnt.getRootID()).setSnapshotId(wf.getSnapshotId()).build();

        // add and remove event listener again
        es.addEventListener(eventType);
        es.removeEventListener(eventType);

        // add event consumer to receive and check the change events
        @SuppressWarnings("unchecked")
        BiConsumer<String, EventEnt> eventConsumerMock = mock(BiConsumer.class);
        es.addEventConsumerForTesting(eventConsumerMock);

        WorkflowManager wfm = idAndWfm.getSecond();
        // carry out the workflow changes
        WorkflowTransformations.createWorkflowTransformations().forEach(t -> t.apply(wfm));

        // check that there weren't any events
        verify(eventConsumerMock, times(0)).accept(any(), any());
    }


    @SuppressWarnings("unchecked")
    private void checkWorkflowChangeEvents(final WorkflowManager wfm,
        final BiConsumer<String, EventEnt> eventConsumerMock, final String initialSnapshotId,
        final List<WorkflowTransformation> wfTransformations) throws InterruptedException {
        boolean isVeryFirstPatch = true;
        for (WorkflowTransformation workflowTransformation : wfTransformations) {
            workflowTransformation.apply(wfm);
            wfm.waitWhileInExecution(10, TimeUnit.SECONDS);

            int numPatches = workflowTransformation.getChangeNames().length;

            // wait for all events to arrive
            await().atMost(5, TimeUnit.SECONDS).pollInterval(100, TimeUnit.MILLISECONDS)
                .untilAsserted(() -> verify(eventConsumerMock, times(numPatches)).accept(any(), any()));

            // check the expected patches
            ArgumentCaptor<WorkflowChangedEventEnt> eventCaptor =
                ArgumentCaptor.forClass(WorkflowChangedEventEnt.class);
            verify(eventConsumerMock, times(numPatches)).accept(eq("WorkflowChangedEvent"), eventCaptor.capture());
            List<WorkflowChangedEventEnt> events = eventCaptor.getAllValues();
            for (int i = 0; i < numPatches; i++) {
                if (isVeryFirstPatch) {
                    assertThat("wrong previous snapshot id", events.get(0).getPreviousSnapshotId(),
                        is(initialSnapshotId));
                    isVeryFirstPatch = false;
                }
                if (workflowTransformation.getChangeNames()[i] != null) {
                    cr(events.get(numPatches - i - 1).getPatch(), workflowTransformation.getChangeNames()[i]);
                }
            }

            reset(eventConsumerMock);
        }
    }

}

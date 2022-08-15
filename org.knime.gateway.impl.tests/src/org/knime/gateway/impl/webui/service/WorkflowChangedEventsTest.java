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
 *   Dec 4, 2020 (hornm): created
 */
package org.knime.gateway.impl.webui.service;

import static org.awaitility.Awaitility.await;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Supplier;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.knime.core.node.workflow.AnnotationData;
import org.knime.core.node.workflow.WorkflowListener;
import org.knime.core.node.workflow.WorkflowManager;
import org.knime.core.util.Pair;
import org.knime.gateway.api.webui.entity.WorkflowChangedEventEnt;
import org.knime.gateway.api.webui.entity.WorkflowChangedEventTypeEnt;
import org.knime.gateway.impl.project.WorkflowProjectManager;
import org.knime.gateway.impl.service.util.EventConsumer;
import org.knime.gateway.impl.service.util.WorkflowChangesListener.CallbackState;
import org.knime.gateway.impl.webui.AppStateProvider;
import org.knime.gateway.impl.webui.WorkflowMiddleware;
import org.knime.gateway.impl.webui.entity.DefaultWorkflowChangedEventTypeEnt;
import org.knime.gateway.impl.webui.service.events.WorkflowChangedEventSource;
import org.knime.gateway.testing.helper.WorkflowTransformations;
import org.knime.gateway.testing.helper.WorkflowTransformations.WorkflowTransformation;
import org.knime.gateway.testing.helper.webui.GatewayTestCollection;

/**
 * Tests that the expected workflow change events are issued by the event service for certain changes to the workflow
 * manager.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
@RunWith(Parameterized.class)
public class WorkflowChangedEventsTest extends GatewayServiceTest {

    /**
     * @return all names of the tests of {@link GatewayTestCollection}
     */
    @Parameters(name = "{0}")
    public static Iterable<WorkflowTransformations> workflowTransformations() {
        return WorkflowTransformations.getAllWorkflowTransformations();
    }

    private WorkflowTransformations m_transformations;

    private final TestEventConsumer m_testEventConsumer = new TestEventConsumer();

    /**
     * @param transformations the workflow transformations (representing the workflow to be tested and the list of
     *            transformations to be carried out) to be tested
     */
    public WorkflowChangedEventsTest(final WorkflowTransformations transformations) {
        m_transformations = transformations;
    }

    @SuppressWarnings("javadoc")
    @Before
    public void setupServiceDependencies() {
        ServiceDependencies.setServiceDependency(AppStateProvider.class, new AppStateProvider(mock(Supplier.class)));
        ServiceDependencies.setServiceDependency(EventConsumer.class, m_testEventConsumer);
        ServiceDependencies.setServiceDependency(WorkflowMiddleware.class,
            new WorkflowMiddleware(WorkflowProjectManager.getInstance()));
        ServiceDependencies.setServiceDependency(WorkflowProjectManager.class, WorkflowProjectManager.getInstance());
    }

    @SuppressWarnings("javadoc")
    @After
    public void disposeServices() {
        ServiceInstances.disposeAllServiceInstancesAndDependencies();
    }

    /**
     * Tests that the expected workflow change events are issued by the event service for certain changes to the
     * workflow manager.
     *
     * @throws Exception
     */
    @Test
    public void testWorkflowChangedEvents() throws Exception {
        Pair<UUID, WorkflowManager> idAndWfm = loadWorkflow(m_transformations.getTestWorkflowProject());
        WorkflowChangedEventTypeEnt eventType =
            EventServiceTest.registerWorkflowChangedEventListener(idAndWfm.getFirst().toString(), m_transformations.getWorkflowId());

        // set callback for testing
        DefaultEventService es = DefaultEventService.getInstance();
        es.setPreEventCreationCallbackForTesting(() -> {
            // slightly delays the creation of the events to increase
            // the determinism of the event patches
            try {
                Thread.sleep(400); // 200 is not enough
            } catch (InterruptedException ex) { // NOSONAR
                // do nothing
            }
        });

        WorkflowManager wfm = idAndWfm.getSecond();
        checkWorkflowChangeEvents(wfm, m_testEventConsumer, m_transformations.getTransformations());

        // remove event listener and check successful removal
        m_testEventConsumer.getEvents().clear();
        es.removeEventListener(eventType);
        WorkflowListener wfListenerMock = mock(WorkflowListener.class);
        wfm.addListener(wfListenerMock); // listener in order to wait for the wf-events to be broadcasted
        wfm.addWorkflowAnnotation(new AnnotationData(), -1);
        await().atMost(2, TimeUnit.SECONDS).pollInterval(200, TimeUnit.MILLISECONDS)
            .untilAsserted(() -> verify(wfListenerMock, times(1)).workflowChanged(any()));
        assertThat(m_testEventConsumer.getEvents(), is(empty()));
    }

    private void checkWorkflowChangeEvents(final WorkflowManager wfm, final TestEventConsumer testEventConsumer,
        final List<WorkflowTransformation> wfTransformations) throws InterruptedException {
        for (WorkflowTransformation workflowTransformation : wfTransformations) {
            workflowTransformation.apply(wfm);
            workflowTransformation.waitForStableState(wfm);


            // The number of events that arrive at the event consumer are not always deterministic.
            // Thus, the TestEventConsumer blocks after it received the first event. And now that the workflow reached
            // a stable state (i.e. no more changes expected) we can unblock the event consumer and (possibly) receive
            // the second (and last) event
            testEventConsumer.unblock();

            // wait for the workflow events to arrive
            await().atMost(5, TimeUnit.SECONDS).pollInterval(200, TimeUnit.MILLISECONDS).untilAsserted(() -> {
                WorkflowChangedEventSource es = (WorkflowChangedEventSource)DefaultEventService.getInstance()
                    .getEventSource(DefaultWorkflowChangedEventTypeEnt.class);
                assertTrue(es.checkWorkflowChangesListenerCallbackState(CallbackState.IDLE));
            });

            // check the expected patches
            int numEvents = testEventConsumer.getEvents().size();
            for (int i = 0; i < numEvents; i++) {
                cr(testEventConsumer.getEvents().get(i).getPatch(),
                    workflowTransformation.getName() + (numEvents > 1 ? ("_" + i) : ""));
            }

            testEventConsumer.getEvents().clear();
            testEventConsumer.block();
        }
    }

    private static class TestEventConsumer implements EventConsumer {

        private List<WorkflowChangedEventEnt> m_events = new ArrayList<>();

        private Lock m_lock = new ReentrantLock();

        TestEventConsumer() {
            block();
        }

        @Override
        public void accept(final String t, final Object u) {
            m_events.add((WorkflowChangedEventEnt)u);
            m_lock.lock();
            m_lock.unlock();
        }

        final void block() {
            m_lock.tryLock(); // NOSONAR
        }

        void unblock() {
            m_lock.unlock();
        }

        List<WorkflowChangedEventEnt> getEvents() {
            return m_events;
        }

    }

    /**
     * Clean-up.
     */
    @After
    public void removeAllEventListener() {
        DefaultEventService.getInstance().removeAllEventListeners();
    }

}

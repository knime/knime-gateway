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
 *   Aug 12, 2020 (hornm): created
 */
package org.knime.gateway.impl.webui.service;

import static org.knime.gateway.api.entity.EntityBuilderManager.builder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import org.knime.core.node.NodeLogger;
import org.knime.core.node.workflow.WorkflowManager;
import org.knime.gateway.api.webui.entity.EventEnt;
import org.knime.gateway.api.webui.entity.EventTypeEnt;
import org.knime.gateway.api.webui.entity.PatchEnt;
import org.knime.gateway.api.webui.entity.PatchOpEnt;
import org.knime.gateway.api.webui.entity.PatchOpEnt.OpEnum;
import org.knime.gateway.api.webui.entity.WorkflowChangedEventEnt;
import org.knime.gateway.api.webui.entity.WorkflowChangedEventEnt.WorkflowChangedEventEntBuilder;
import org.knime.gateway.api.webui.entity.WorkflowChangedEventTypeEnt;
import org.knime.gateway.api.webui.service.EventService;
import org.knime.gateway.api.webui.service.util.ServiceExceptions.InvalidRequestException;
import org.knime.gateway.api.webui.service.util.ServiceExceptions.NodeNotFoundException;
import org.knime.gateway.api.webui.service.util.ServiceExceptions.NotASubWorkflowException;
import org.knime.gateway.impl.service.util.PatchCreator;
import org.knime.gateway.impl.service.util.WorkflowChangesListener;
import org.knime.gateway.impl.service.util.WorkflowChangesListener.CallbackState;
import org.knime.gateway.impl.webui.WorkflowKey;
import org.knime.gateway.impl.webui.WorkflowStatefulUtil;
import org.knime.gateway.impl.webui.WorkflowUtil;
import org.knime.gateway.impl.webui.entity.DefaultPatchEnt;
import org.knime.gateway.impl.webui.entity.DefaultPatchEnt.DefaultPatchEntBuilder;
import org.knime.gateway.impl.webui.entity.DefaultPatchOpEnt.DefaultPatchOpEntBuilder;

/**
 * Default implementation of the {@link EventService}-interface.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
public final class DefaultEventService implements EventService {

    private static final DefaultEventService INSTANCE = new DefaultEventService();

    private static final WorkflowStatefulUtil WF_UTIL = WorkflowStatefulUtil.getInstance();

    /**
     * Returns the singleton instance for this service.
     *
     * @return the singleton instance
     */
    public static DefaultEventService getInstance() {
        return INSTANCE;
    }

    private final List<BiConsumer<String, EventEnt>> m_eventConsumer = new ArrayList<>();

    private Map<WorkflowKey, Consumer<WorkflowManager>> m_workflowChangesCallbacks = new HashMap<>();

    /*
     * For testing purposes only.
     */
    private boolean m_callEventConsumerOnError = false;
    private Runnable m_preEventCreationCallback = null;

    private DefaultEventService() {
        // singleton
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("resource")
    @Override
    public void addEventListener(final EventTypeEnt eventTypeEnt) throws InvalidRequestException {
        if (eventTypeEnt instanceof WorkflowChangedEventTypeEnt) {
            WorkflowChangedEventTypeEnt wfEventType = (WorkflowChangedEventTypeEnt)eventTypeEnt;
            WorkflowKey key = new WorkflowKey(wfEventType.getProjectId(), wfEventType.getWorkflowId());
            try {
                WorkflowUtil.assertWorkflowExists(key);
            } catch (NodeNotFoundException | NotASubWorkflowException ex) {
                throw new InvalidRequestException(ex.getMessage(), ex);
            }

            // create very first changed event to be send first (and thus catch up with the most recent
            // workflow version)
            WorkflowChangedEventEnt workflowChangedEvent =
                WF_UTIL.buildWorkflowChangedEvent(key, new PatchEntCreator(null), wfEventType.getSnapshotId(), true);
            sendEvent(workflowChangedEvent);

            // add and keep track of callback added to the workflow changes listener (if not already)
            m_workflowChangesCallbacks.computeIfAbsent(key, k -> {
                String latestSnapshotId =
                    workflowChangedEvent == null ? wfEventType.getSnapshotId() : workflowChangedEvent.getSnapshotId();
                WorkflowChangesListener l = WF_UTIL.getWorkflowChangesListener(key);
                Consumer<WorkflowManager> callback =
                    createWorkflowChangesCallback(key, new PatchEntCreator(latestSnapshotId));
                l.addCallback(callback);
                return callback;
            });
        } else {
            throw new InvalidRequestException("Event type not supported: " + eventTypeEnt.getClass().getSimpleName());
        }
    }

    private Consumer<WorkflowManager> createWorkflowChangesCallback(final WorkflowKey wfKey,
        final PatchEntCreator patchEntCreator) {
        return wfm -> {
            if (m_preEventCreationCallback != null) {
                m_preEventCreationCallback.run();
            }
            patchEntCreator.clear();
            WorkflowChangedEventEnt event =
                WF_UTIL.buildWorkflowChangedEvent(wfKey, patchEntCreator, patchEntCreator.getLastSnapshotId(), true);
            sendEvent(event);
        };
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeEventListener(final EventTypeEnt eventTypeEnt) {
        if (eventTypeEnt instanceof WorkflowChangedEventTypeEnt) {
            WorkflowChangedEventTypeEnt wfEventType = (WorkflowChangedEventTypeEnt)eventTypeEnt;
            WorkflowKey wfKey = new WorkflowKey(wfEventType.getProjectId(), wfEventType.getWorkflowId());
            removeEventListener(wfKey);
        }
    }

    /**
     * Unregisters and removes all event listeners. After this method has been called, no events will arrive anymore at
     * the registered event consumers.
     */
    public void removeAllEventListeners() {
        new HashSet<>(m_workflowChangesCallbacks.keySet()).forEach(this::removeEventListener);
    }

    private void removeEventListener(final WorkflowKey wfKey) {
        Consumer<WorkflowManager> callback = m_workflowChangesCallbacks.remove(wfKey);
        WF_UTIL.getWorkflowChangesListener(wfKey).removeCallback(callback);
    }

    /**
     * Adds a new event consumer. The consumer takes the event name and the readily created event, i.e.
     * {@link EventEnt}s, and delivers it to the client/ui.
     *
     * @param eventConsumer the event consumer to add
     */
    public void addEventConsumer(final BiConsumer<String, EventEnt> eventConsumer) {
        m_eventConsumer.add(eventConsumer);
    }

    void setEventConsumerForTesting(final BiConsumer<String, EventEnt> eventConsumer,
        final Runnable preEventCreationCallback) {
        m_eventConsumer.clear();
        m_preEventCreationCallback = preEventCreationCallback;
        addEventConsumer(eventConsumer);
        m_callEventConsumerOnError = true;
    }

    /*
     * For testing purposes only!
     */
    boolean checkWorkflowChangesListenerCallbackState(final CallbackState state) {
        return m_workflowChangesCallbacks.keySet().stream()
            .anyMatch(k -> WF_UTIL.getWorkflowChangesListener(k).getCallbackState() == state);
    }

    /**
     * Removes a previously registered event consumer.
     *
     * @param eventConsumer the consumer to remove
     */
    public void removeEventConsumer(final BiConsumer<String, EventEnt> eventConsumer) {
        m_eventConsumer.remove(eventConsumer); // NOSONAR
    }

    private void sendEvent(final WorkflowChangedEventEnt event) {
        if (event != null) {
            sendEvent("WorkflowChangedEvent", event);
        }
    }

    private synchronized void sendEvent(final String name, final EventEnt event) {
        if (m_eventConsumer.isEmpty()) {
            String message =
                "Workflow change events available but no one is interested. Most likely an implementation error.";
            NodeLogger.getLogger(getClass()).error(message);
            if (m_callEventConsumerOnError) {
                m_eventConsumer.forEach(c -> c.accept(message, null));
            }
            throw new IllegalStateException(message);
        }
        m_eventConsumer.forEach(c -> c.accept(name, event));
    }

    /**
     * Creates {@link PatchEnt}s.
     *
     * Public scope for testing.
     */
    public static class PatchEntCreator implements PatchCreator<WorkflowChangedEventEnt> {

        private final List<PatchOpEnt> m_ops = new ArrayList<>();
        private String m_lastSnapshotId;

        /**
         * @param lastSnapshotId the latest snapshot id
         */
        public PatchEntCreator(final String lastSnapshotId) {
            m_lastSnapshotId = lastSnapshotId;
        }

        @Override
        public void replaced(final String path, final Object value) {
            m_ops.add(new DefaultPatchOpEntBuilder().setOp(OpEnum.REPLACE).setPath(path).setValue(value).build());
        }

        @Override
        public void removed(final String path) {
            m_ops.add(new DefaultPatchOpEntBuilder().setOp(OpEnum.REMOVE).setPath(path).build());
        }

        @Override
        public void added(final String path, final Object value) {
            m_ops.add(new DefaultPatchOpEntBuilder().setOp(OpEnum.ADD).setPath(path).setValue(value).build());
            if (value == null) {
                NodeLogger.getLogger(DefaultEventService.class).error(
                    "An 'ADD' patch operation has been created without a value. Most likely an implementation error.");
            }
        }

        @Override
        public WorkflowChangedEventEnt create(final String newSnapshotId) {
            m_lastSnapshotId = newSnapshotId;
            DefaultPatchEnt patch = new DefaultPatchEntBuilder().setOps(m_ops).build();
            return builder(WorkflowChangedEventEntBuilder.class).setPatch(patch).setSnapshotId(newSnapshotId).build();
        }

        void clear() {
            m_ops.clear();
        }

        String getLastSnapshotId() {
            return m_lastSnapshotId;
        }
    }
}
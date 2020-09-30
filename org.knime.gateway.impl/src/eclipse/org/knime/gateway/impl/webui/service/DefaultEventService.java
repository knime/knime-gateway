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
import static org.knime.gateway.api.webui.util.EntityBuilderUtil.buildWorkflowEnt;
import static org.knime.gateway.impl.service.util.DefaultServiceUtil.getWorkflowManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import org.knime.core.node.NodeLogger;
import org.knime.core.node.workflow.WorkflowManager;
import org.knime.gateway.api.entity.NodeIDEnt;
import org.knime.gateway.api.webui.entity.ConnectionEnt;
import org.knime.gateway.api.webui.entity.EventEnt;
import org.knime.gateway.api.webui.entity.EventTypeEnt;
import org.knime.gateway.api.webui.entity.NodeAnnotationEnt;
import org.knime.gateway.api.webui.entity.NodeEnt;
import org.knime.gateway.api.webui.entity.NodePortEnt;
import org.knime.gateway.api.webui.entity.PatchEnt;
import org.knime.gateway.api.webui.entity.PatchOpEnt;
import org.knime.gateway.api.webui.entity.PatchOpEnt.OpEnum;
import org.knime.gateway.api.webui.entity.StyleRangeEnt;
import org.knime.gateway.api.webui.entity.WorkflowAnnotationEnt;
import org.knime.gateway.api.webui.entity.WorkflowChangedEventEnt;
import org.knime.gateway.api.webui.entity.WorkflowChangedEventEnt.WorkflowChangedEventEntBuilder;
import org.knime.gateway.api.webui.entity.WorkflowChangedEventTypeEnt;
import org.knime.gateway.api.webui.entity.WorkflowEnt;
import org.knime.gateway.api.webui.service.EventService;
import org.knime.gateway.api.webui.service.util.ServiceExceptions.InvalidRequestException;
import org.knime.gateway.impl.service.util.PatchCreator;
import org.knime.gateway.impl.service.util.WorkflowChangesListener;
import org.knime.gateway.impl.webui.entity.DefaultPatchEnt.DefaultPatchEntBuilder;
import org.knime.gateway.impl.webui.entity.DefaultPatchOpEnt.DefaultPatchOpEntBuilder;

/**
 * Default implementation of the {@link EventService}-interface.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
public final class DefaultEventService implements EventService {

    private static final DefaultEventService INSTANCE = new DefaultEventService();

    /**
     * Returns the singleton instance for this service.
     *
     * @return the singleton instance
     */
    public static DefaultEventService getInstance() {
        return INSTANCE;
    }

    /*
     * Map from a workflow (represented by a WorkflowKey) to the WorkflowChangesListener.
     * Used to prevent multiple changes listeners for the same WorkflowManager.
     */
    private final Map<WorkflowKey, WorkflowChangesListener> m_workflowChangesListeners = new HashMap<>();

    /*
     * One PatchEntCreator per workflow.
     */
    private final Map<WorkflowKey, PatchEntCreator> m_patchEntCreators = new HashMap<>();

    private final List<BiConsumer<String, EventEnt>> m_eventConsumer = new ArrayList<>();

    /*
     * For testing purposes only.
     */
    private boolean m_callEventConsumerOnError = false;

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
            WorkflowManager wfm = getWorkflowManager(wfEventType.getProjectId(), wfEventType.getWorkflowId());

            // create very first changed event to be send first (and thus catch up with the most recent
            // workflow version)
            PatchEntCreator patchEntCreator = new PatchEntCreator(wfEventType.getSnapshotId());
            WorkflowChangedEventEnt workflowChangedEvent = createWorkflowChangedEvent(patchEntCreator, wfm);
            if (workflowChangedEvent != null) {
                sendEvent(workflowChangedEvent);
            }

            // initialize WorkflowChangesListener (if not already)
            m_workflowChangesListeners.computeIfAbsent(key, k -> {
                String latestSnapshotId =
                    workflowChangedEvent == null ? wfEventType.getSnapshotId() : workflowChangedEvent.getSnapshotId();
                m_patchEntCreators.put(key, new PatchEntCreator(latestSnapshotId));
                Consumer<WorkflowManager> callback = createWorkflowChangesCallback(key);
                return new WorkflowChangesListener(wfm, callback);
            });
        } else {
            throw new InvalidRequestException("Event type not supported: " + eventTypeEnt.getClass().getSimpleName());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeEventListener(final EventTypeEnt eventTypeEnt) {
        if (eventTypeEnt instanceof WorkflowChangedEventTypeEnt) {
            WorkflowChangedEventTypeEnt wfEventType = (WorkflowChangedEventTypeEnt)eventTypeEnt;
            WorkflowKey wfKey = new WorkflowKey(wfEventType.getProjectId(), wfEventType.getWorkflowId());
            @SuppressWarnings("resource")
            WorkflowChangesListener listener = m_workflowChangesListeners.remove(wfKey);
            listener.close();
            m_patchEntCreators.remove(wfKey);
        }
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

    void addEventConsumerForTesting(final BiConsumer<String, EventEnt> eventConsumer) {
        addEventConsumer(eventConsumer);
        m_callEventConsumerOnError = true;
    }

    /**
     * Removes a previously registered event consumer.
     *
     * @param eventConsumer the consumer to remove
     */
    public void removeEventConsumer(final BiConsumer<String, EventEnt> eventConsumer) {
        m_eventConsumer.remove(eventConsumer); // NOSONAR
    }

    private Consumer<WorkflowManager> createWorkflowChangesCallback(final WorkflowKey key) {
        return wfm -> {
            assertPatchEntCreatorAvaible(key);
            WorkflowChangedEventEnt event =
                createWorkflowChangedEvent(m_patchEntCreators.get(key), wfm);
            if (event != null) {
                sendEvent(event);
            }
        };
    }

    private void assertPatchEntCreatorAvaible(final WorkflowKey key) {
        if (m_patchEntCreators.get(key) == null) {
            String message =
                "Workflow change events available but no one is interested. Most likely an implementation error.";
            NodeLogger.getLogger(getClass()).error(message);
            if (m_callEventConsumerOnError) {
                sendEvent(message, null);
            }
            throw new IllegalStateException(message);
        }
    }

    private static WorkflowChangedEventEnt createWorkflowChangedEvent(final PatchEntCreator patchEntCreator,
        final WorkflowManager wfm) {
        // TODO parameterize the 'includeInteractionInfo'
        patchEntCreator.createPatch(buildWorkflowEnt(wfm, true));
        if (patchEntCreator.getPatch() == null) {
            return null;
        }
        return builder(WorkflowChangedEventEntBuilder.class).setPatch(patchEntCreator.getPatch())
            .setSnapshotId(patchEntCreator.getSnapshotId())
            .setPreviousSnapshotId(patchEntCreator.getPreviousSnapshotId()).build();
    }

    private void sendEvent(final WorkflowChangedEventEnt event) {
        sendEvent("WorkflowChangedEvent", event);
    }

    private void sendEvent(final String name, final EventEnt event) {
        m_eventConsumer.forEach(c -> c.accept(name, event));
    }

    /**
     * Creates {@link PatchEnt}s.
     */
    public static class PatchEntCreator implements PatchCreator<PatchEnt> {

        private String m_snapshotId;

        private String m_previousSnapshotId = null;

        private PatchEnt m_patch = null;

        private final List<PatchOpEnt> m_ops = new ArrayList<>();

        private String m_targetTypeID;

        /**
         * @param initialSnapshotId
         */
        public PatchEntCreator(final String initialSnapshotId) {
            m_snapshotId = initialSnapshotId;
        }

        private void createPatch(final WorkflowEnt ent) {
            m_ops.clear();
            m_targetTypeID = ent.getTypeID();
            m_patch = DefaultWorkflowService.getInstance().getEntityRepository()
                .getChangesAndCommit(m_snapshotId, ent, id -> {
                    m_previousSnapshotId = m_snapshotId;
                    m_snapshotId = id;
                    return this;
                }).orElse(null);
        }

        private PatchEnt getPatch() {
            return m_patch;
        }

        private String getSnapshotId() {
            return m_snapshotId;
        }

        private String getPreviousSnapshotId() {
            return m_previousSnapshotId;
        }

        @Override
        public void replaced(final String path, final Object value) {
            m_ops.add(new DefaultPatchOpEntBuilder().setOp(OpEnum.REPLACE).setPath(path).setValue(value).build());
        }

        @Override
        public boolean isNewCollectionObjectValid(final Object newObj) {
            return newObj instanceof NodeEnt //NOSONAR
                || newObj instanceof ConnectionEnt || newObj instanceof WorkflowAnnotationEnt
                || newObj instanceof NodePortEnt || newObj instanceof StyleRangeEnt;
        }

        @Override
        public boolean isNewObjectValid(final Object newObj) {
            return newObj instanceof NodeAnnotationEnt;
        }

        @Override
        public void removed(final String path) {
            m_ops.add(new DefaultPatchOpEntBuilder().setOp(OpEnum.REMOVE).setPath(path).build());
        }

        @Override
        public void added(final String path, final Object value) {
            m_ops.add(new DefaultPatchOpEntBuilder().setOp(OpEnum.ADD).setPath(path).setValue(value).build());
        }

        @Override
        public PatchEnt create() {
            return new DefaultPatchEntBuilder().setOps(m_ops).setTargetTypeId(m_targetTypeID).build();
        }
    }

    private static class WorkflowKey {

        private String m_projectId;

        private NodeIDEnt m_workfowId;

        public WorkflowKey(final String projectId, final NodeIDEnt workfowId) {
            m_projectId = projectId;
            m_workfowId = workfowId;
        }

        @Override
        public boolean equals(final Object o) { // NOSONAR
            if (o == this) {
                return true;
            }
            if (o == null) {
                return false;
            }
            if (this.getClass() == o.getClass()) {
                WorkflowKey k = (WorkflowKey)o;
                return Objects.equals(m_projectId, k.m_projectId) && Objects.equals(m_workfowId, k.m_workfowId);
            }
            return false;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + m_projectId.hashCode();
            result = prime * result + m_workfowId.hashCode();
            return result;
        }

    }

}
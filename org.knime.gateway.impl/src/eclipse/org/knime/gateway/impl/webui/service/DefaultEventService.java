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
import static org.knime.gateway.impl.service.util.DefaultServiceUtil.getWorkflowManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

import org.knime.core.node.NodeLogger;
import org.knime.core.node.workflow.DependentNodeProperties;
import org.knime.core.node.workflow.NodeSuccessors;
import org.knime.core.node.workflow.WorkflowLock;
import org.knime.core.node.workflow.WorkflowManager;
import org.knime.gateway.api.webui.entity.EventEnt;
import org.knime.gateway.api.webui.entity.EventTypeEnt;
import org.knime.gateway.api.webui.entity.PatchEnt;
import org.knime.gateway.api.webui.entity.PatchOpEnt;
import org.knime.gateway.api.webui.entity.PatchOpEnt.OpEnum;
import org.knime.gateway.api.webui.entity.WorkflowChangedEventEnt;
import org.knime.gateway.api.webui.entity.WorkflowChangedEventEnt.WorkflowChangedEventEntBuilder;
import org.knime.gateway.api.webui.entity.WorkflowChangedEventTypeEnt;
import org.knime.gateway.api.webui.entity.WorkflowEnt;
import org.knime.gateway.api.webui.service.EventService;
import org.knime.gateway.api.webui.service.util.ServiceExceptions.InvalidRequestException;
import org.knime.gateway.api.webui.util.EntityBuilderUtil;
import org.knime.gateway.api.webui.util.WorkflowBuildContext;
import org.knime.gateway.api.webui.util.WorkflowBuildContext.WorkflowBuildContextBuilder;
import org.knime.gateway.impl.service.util.PatchCreator;
import org.knime.gateway.impl.service.util.WorkflowChangesListener;
import org.knime.gateway.impl.service.util.WorkflowChangesListener.CallbackState;
import org.knime.gateway.impl.service.util.WorkflowChangesListener.WorkflowChanges;
import org.knime.gateway.impl.webui.entity.DefaultPatchEnt.DefaultPatchEntBuilder;
import org.knime.gateway.impl.webui.entity.DefaultPatchOpEnt.DefaultPatchOpEntBuilder;
import org.knime.gateway.impl.webui.service.commands.WorkflowCommands;

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

    private final Map<WorkflowKey, Workflow> m_workflows = new HashMap<>();

    private final List<BiConsumer<String, EventEnt>> m_eventConsumer = new ArrayList<>();

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
            WorkflowManager wfm = getWorkflowManager(wfEventType.getProjectId(), wfEventType.getWorkflowId());

            // create very first changed event to be send first (and thus catch up with the most recent
            // workflow version)
            PatchEntCreator patchEntCreator = new PatchEntCreator(wfEventType.getSnapshotId());
            WorkflowChangedEventEnt workflowChangedEvent = createWorkflowChangedEvent(key,
                new Workflow(wfm, null, patchEntCreator), WorkflowChanges.UNDEFINED_CHANGES);
            if (workflowChangedEvent != null) {
                sendEvent(workflowChangedEvent);
            }

            // initialize WorkflowChangesListener (if not already)
            m_workflows.computeIfAbsent(key, k -> {
                String latestSnapshotId =
                    workflowChangedEvent == null ? wfEventType.getSnapshotId() : workflowChangedEvent.getSnapshotId();
                BiConsumer<WorkflowManager, WorkflowChanges> callback = createWorkflowChangesCallback(key);
                return new Workflow(wfm, new WorkflowChangesListener(wfm, callback),
                    new PatchEntCreator(latestSnapshotId));
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
            removeEventListener(wfKey);
        }
    }

    /**
     * Unregisters and removes all event listeners. After this method has been called, no events will arrive anymore at
     * the registered event consumers.
     */
    public void removeAllEventListeners() {
        new HashSet<>(m_workflows.keySet()).forEach(this::removeEventListener);
    }

    private void removeEventListener(final WorkflowKey wfKey) {
        Workflow wf = m_workflows.remove(wfKey);
        if (wf != null && wf.workflowChangesListener() != null) {
            wf.workflowChangesListener().close();
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

    void setEventConsumerForTesting(final BiConsumer<String, EventEnt> eventConsumer,
        final Runnable preEventCreationCallback) {
        m_eventConsumer.clear();
        m_preEventCreationCallback = preEventCreationCallback;
        addEventConsumer(eventConsumer);
        m_callEventConsumerOnError = true;
    }

    boolean checkWorkflowChangesListenerCallbackState(final CallbackState state) {
        return m_workflows.values().stream().anyMatch(w -> w.workflowChangesListener().getCallbackState() == state);
    }

    /**
     * Removes a previously registered event consumer.
     *
     * @param eventConsumer the consumer to remove
     */
    public void removeEventConsumer(final BiConsumer<String, EventEnt> eventConsumer) {
        m_eventConsumer.remove(eventConsumer); // NOSONAR
    }

    private BiConsumer<WorkflowManager, WorkflowChanges> createWorkflowChangesCallback(final WorkflowKey key) {
        return (wfm, changes) -> {
            assertWorkflowAvailable(key);
            if (m_preEventCreationCallback != null) {
                m_preEventCreationCallback.run();
            }
            WorkflowChangedEventEnt event = createWorkflowChangedEvent(key, m_workflows.get(key), changes);
            if (event != null) {
                sendEvent(event);
            }
        };
    }

    private void assertWorkflowAvailable(final WorkflowKey key) {
        if (m_workflows.get(key) == null) {
            String message =
                "Workflow change events available but no one is interested. Most likely an implementation error.";
            NodeLogger.getLogger(getClass()).error(message);
            if (m_callEventConsumerOnError) {
                sendEvent(message, null);
            }
            throw new IllegalStateException(message);
        }
    }

    private static WorkflowChangedEventEnt createWorkflowChangedEvent(final WorkflowKey wfKey, final Workflow wf,
        final WorkflowChanges changes) {
        // TODO parameterize the 'includeInteractionInfo'
        WorkflowCommands commands = DefaultWorkflowService.getInstance().getWorkflowCommands();
        WorkflowBuildContextBuilder buildContextBuilder = WorkflowBuildContext.builder(wf.wfm())//
            .includeInteractionInfo(true)//
            .canUndo(commands.canUndo(wfKey))//
            .canRedo(commands.canRedo(wfKey))//
            .dependentNodeProperties(() -> wf.dependentNodeProperties(changes))//
            .nodeSuccessors(() -> wf.nodeSuccessors(changes));
        PatchEntCreator patchEntCreator = wf.patchEntCreator();
        WorkflowEnt wfEnt;
        try (WorkflowLock lock = wf.wfm().lock()) {
            wfEnt = EntityBuilderUtil.buildWorkflowEnt(buildContextBuilder);
            // The changes are used in the WorkflowEnt build-step to determine whether the re-calculation of some
            // properties (e.g. 'dependent node properties' or 'node successors') is necessary - if not
            // a cached instance is to reduce. Once done, the changes being tracked are reset which needs to happen
            // in the very same workflow-lock block to not miss any changes.
            changes.reset();
        }
        patchEntCreator.createPatch(wfEnt);
        if (patchEntCreator.getPatch() == null) {
            return null;
        }
        return builder(WorkflowChangedEventEntBuilder.class).setPatch(patchEntCreator.getPatch())
            .setSnapshotId(patchEntCreator.getSnapshotId()).build();
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

        private PatchEnt m_patch = null;

        private final List<PatchOpEnt> m_ops = new ArrayList<>();

        /**
         * @param initialSnapshotId
         */
        public PatchEntCreator(final String initialSnapshotId) {
            m_snapshotId = initialSnapshotId;
        }

        private void createPatch(final WorkflowEnt ent) {
            m_ops.clear();
            m_patch = DefaultWorkflowService.getInstance().getEntityRepository()
                .getChangesAndCommit(m_snapshotId, ent, id -> {
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
        public PatchEnt create() {
            return new DefaultPatchEntBuilder().setOps(m_ops).build();
        }
    }

    /**
     * Helper class that summarizes all the things that are associated with a single workflow.
     */
    private static class Workflow {

        private final WorkflowChangesListener m_workflowChangesListener;

        private final PatchEntCreator m_patchEntCreator;

        private DependentNodeProperties m_depNodeProperties;

        private NodeSuccessors m_nodeSuccessors;

        private final WorkflowManager m_wfm;

        Workflow(final WorkflowManager wfm, final WorkflowChangesListener l, final PatchEntCreator p) {
            m_wfm = wfm;
            m_workflowChangesListener = l;
            m_patchEntCreator = p;
        }

        WorkflowManager wfm() {
            return m_wfm;
        }

        WorkflowChangesListener workflowChangesListener() {
            return m_workflowChangesListener;
        }

        PatchEntCreator patchEntCreator() {
            return m_patchEntCreator;
        }

        DependentNodeProperties dependentNodeProperties(final WorkflowChanges changes) {
            // dependent node properties are only re-calculated if there are respective changes
            // otherwise a cached instance is used
            if (m_depNodeProperties == null || changes.nodeStateChanges() || changes.nodeOrConnectionAddedOrRemoved()) {
                m_depNodeProperties = m_wfm.determineDependentNodeProperties();
            }
            return m_depNodeProperties;
        }

        NodeSuccessors nodeSuccessors(final WorkflowChanges changes) {
            // the node successors are only re-calculated if there are respective changes
            // otherwise a cached instance is used
            if (m_nodeSuccessors == null || changes.nodeOrConnectionAddedOrRemoved()) {
                m_nodeSuccessors = m_wfm.determineNodeSuccessors();
            }
            return m_nodeSuccessors;
        }

    }

}
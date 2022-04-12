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
 *   Nov 25, 2021 (hornm): created
 */
package org.knime.gateway.impl.webui.service.events;

import static org.knime.gateway.api.entity.EntityBuilderManager.builder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import org.knime.core.node.NodeLogger;
import org.knime.core.node.workflow.WorkflowManager;
import org.knime.gateway.api.webui.entity.PatchEnt;
import org.knime.gateway.api.webui.entity.PatchOpEnt;
import org.knime.gateway.api.webui.entity.PatchOpEnt.OpEnum;
import org.knime.gateway.api.webui.entity.WorkflowChangedEventEnt;
import org.knime.gateway.api.webui.entity.WorkflowChangedEventEnt.WorkflowChangedEventEntBuilder;
import org.knime.gateway.api.webui.entity.WorkflowChangedEventTypeEnt;
import org.knime.gateway.api.webui.service.util.ServiceExceptions.NodeNotFoundException;
import org.knime.gateway.api.webui.service.util.ServiceExceptions.NotASubWorkflowException;
import org.knime.gateway.impl.service.events.EventSource;
import org.knime.gateway.impl.service.util.WorkflowChangesTracker;
import org.knime.gateway.impl.service.util.PatchCreator;
import org.knime.gateway.impl.service.util.WorkflowChangesListener;
import org.knime.gateway.impl.service.util.WorkflowChangesListener.CallbackState;
import org.knime.gateway.impl.webui.WorkflowKey;
import org.knime.gateway.impl.webui.WorkflowStatefulUtil;
import org.knime.gateway.impl.webui.WorkflowUtil;
import org.knime.gateway.impl.webui.entity.DefaultPatchEnt;
import org.knime.gateway.impl.webui.entity.DefaultPatchEnt.DefaultPatchEntBuilder;
import org.knime.gateway.impl.webui.entity.DefaultPatchOpEnt.DefaultPatchOpEntBuilder;
import org.knime.gateway.impl.webui.service.DefaultEventService;

/**
 * An event source that emits events whenever something changes in a particular workflow.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz
 */
public class WorkflowChangedEventSource extends EventSource<WorkflowChangedEventTypeEnt, WorkflowChangedEventEnt> {

    private static final WorkflowStatefulUtil WF_UTIL = WorkflowStatefulUtil.getInstance();

    private final Map<WorkflowKey, Consumer<WorkflowManager>> m_workflowChangesCallbacks = new HashMap<>();

    private final Map<WorkflowKey, WorkflowChangesTracker> m_trackers = new HashMap<>();

    /**
     * @param eventConsumer
     */
    public WorkflowChangedEventSource(final BiConsumer<String, Object> eventConsumer) {
        super(eventConsumer);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String getName() {
        return "WorkflowChangedEvent";
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("resource")
    @Override
    public Optional<WorkflowChangedEventEnt>
        addEventListenerAndGetInitialEventFor(final WorkflowChangedEventTypeEnt wfEventType) {
        var workflowKey = new WorkflowKey(wfEventType.getProjectId(), wfEventType.getWorkflowId());
        var workflowChangesListener = WF_UTIL.getWorkflowChangesListener(workflowKey);

        try {
            WorkflowUtil.assertWorkflowExists(workflowKey);
        } catch (NodeNotFoundException | NotASubWorkflowException ex) {
            throw new IllegalArgumentException(ex.getMessage(), ex);
        }

        var wfChangesTracker = m_trackers.computeIfAbsent(workflowKey, k -> {
            var tracker = new WorkflowChangesTracker(true);
            workflowChangesListener.registerWorkflowChangesTracker(tracker);
            return tracker;
        });

        // create very first changed event to be sent first (and thus catch up with the most recent
        // workflow version)
        var workflowChangedEvent =
                WF_UTIL.buildWorkflowChangedEvent(
                        workflowKey,
                        new PatchEntCreator(null),
                        wfEventType.getSnapshotId(),
                        true,
                        wfChangesTracker
                );

        // add and keep track of callback added to the workflow changes listener (if not already)
        m_workflowChangesCallbacks.computeIfAbsent(workflowKey, wfKey -> {
            String latestSnapshotId =
                workflowChangedEvent == null ? wfEventType.getSnapshotId() : workflowChangedEvent.getSnapshotId();
            Consumer<WorkflowManager> callback =
                createWorkflowChangesCallback(workflowKey, new PatchEntCreator(latestSnapshotId), wfChangesTracker);
            workflowChangesListener.addWorkflowChangeCallback(callback);
            return callback;
        });

        return Optional.ofNullable(workflowChangedEvent);
    }

    private Consumer<WorkflowManager> createWorkflowChangesCallback(final WorkflowKey wfKey,
        final PatchEntCreator patchEntCreator, final WorkflowChangesTracker tracker) {
        return wfm -> {
            preEventCreation();
            patchEntCreator.clear();
            WorkflowChangedEventEnt event =
                WF_UTIL.buildWorkflowChangedEvent(wfKey, patchEntCreator, patchEntCreator.getLastSnapshotId(), true, tracker);
            if (event != null) {
                sendEvent(event);
            }
        };
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeEventListener(final WorkflowChangedEventTypeEnt wfEventType) {
        var wfKey = new WorkflowKey(wfEventType.getProjectId(), wfEventType.getWorkflowId());
        removeEventListener(wfKey);
        removeTracker(wfKey);
    }

    @SuppressWarnings("resource")
    private void removeEventListener(final WorkflowKey wfKey) {
        Consumer<WorkflowManager> callback = m_workflowChangesCallbacks.remove(wfKey);
        if (callback != null && WF_UTIL.hasStateFor(wfKey)) {
            WF_UTIL.getWorkflowChangesListener(wfKey).removeCallback(callback);
        }
    }

    private void removeTracker(final WorkflowKey wfKey) {
        m_trackers.remove(wfKey);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeAllEventListeners() {
        new HashSet<>(m_workflowChangesCallbacks.keySet()).forEach(this::removeEventListener);
        new HashSet<>(m_trackers.keySet()).forEach(this::removeEventListener);
    }

    /**
     * For testing purposes only!
     *
     * @param state
     * @return <code>true</code> any of the {@link WorkflowChangesListener}s has the callback-state as given by the
     *         argument
     */
    public boolean checkWorkflowChangesListenerCallbackState(final CallbackState state) {
        return m_workflowChangesCallbacks.keySet().stream()
            .anyMatch(k -> WF_UTIL.getWorkflowChangesListener(k).getCallbackState() == state);
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

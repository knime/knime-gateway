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

import org.knime.core.node.NodeLogger;
import org.knime.core.node.workflow.WorkflowManager;
import org.knime.gateway.api.entity.EntityBuilderManager;
import org.knime.gateway.api.webui.entity.CompositeEventEnt;
import org.knime.gateway.api.webui.entity.CompositeEventEnt.CompositeEventEntBuilder;
import org.knime.gateway.api.webui.entity.PatchEnt;
import org.knime.gateway.api.webui.entity.PatchOpEnt;
import org.knime.gateway.api.webui.entity.PatchOpEnt.OpEnum;
import org.knime.gateway.api.webui.entity.ProjectDirtyStateEventEnt.ProjectDirtyStateEventEntBuilder;
import org.knime.gateway.api.webui.entity.WorkflowChangedEventEnt;
import org.knime.gateway.api.webui.entity.WorkflowChangedEventEnt.WorkflowChangedEventEntBuilder;
import org.knime.gateway.api.webui.entity.WorkflowChangedEventTypeEnt;
import org.knime.gateway.api.webui.service.util.ServiceExceptions.NodeNotFoundException;
import org.knime.gateway.api.webui.service.util.ServiceExceptions.NotASubWorkflowException;
import org.knime.gateway.impl.project.ProjectManager;
import org.knime.gateway.impl.service.util.CallThrottle.CallState;
import org.knime.gateway.impl.service.util.DefaultServiceUtil;
import org.knime.gateway.impl.service.util.PatchCreator;
import org.knime.gateway.impl.service.util.WorkflowChangesListener;
import org.knime.gateway.impl.service.util.WorkflowChangesTracker;
import org.knime.gateway.impl.webui.WorkflowKey;
import org.knime.gateway.impl.webui.WorkflowMiddleware;
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
public class WorkflowChangedEventSource extends EventSource<WorkflowChangedEventTypeEnt, CompositeEventEnt> {

    private final WorkflowMiddleware m_workflowMiddleware;

    private final Map<WorkflowKey, Runnable> m_workflowChangesCallbacks = new HashMap<>();

    private final Map<WorkflowKey, WorkflowChangesTracker> m_trackers = new HashMap<>();

    private final ProjectManager m_projectManager;

    /**
     * @param eventConsumer
     * @param workflowMiddleware
     * @param projectManager
     */
    public WorkflowChangedEventSource(final EventConsumer eventConsumer, final WorkflowMiddleware workflowMiddleware,
        final ProjectManager projectManager) {
        super(eventConsumer);
        m_workflowMiddleware = workflowMiddleware;
        m_projectManager = projectManager;
        m_projectManager.addProjectRemovedListener(projectId ->
        // remove listeners in case the FE doesn't explicitly do it,
        // e.g., in case the underlying job is swapped (AP in Hub)
        new HashSet<>(m_workflowChangesCallbacks.keySet()).stream() //
            .filter(wfKey -> wfKey.getProjectId().equals(projectId)) //
            .forEach(wfKey -> {
                removeEventListener(wfKey);
                removeTracker(wfKey);
            }));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String getName() {
        return "WorkflowChangedEvent:ProjectDirtyStateEvent";
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("resource")
    @Override
    public Optional<CompositeEventEnt>
        addEventListenerAndGetInitialEventFor(final WorkflowChangedEventTypeEnt wfEventType) {
        var workflowKey = new WorkflowKey(wfEventType.getProjectId(), wfEventType.getWorkflowId());
        var workflowChangesListener = m_workflowMiddleware.getWorkflowChangesListener(workflowKey);

        try {
            WorkflowUtil.assertWorkflowExists(workflowKey);
        } catch (NodeNotFoundException | NotASubWorkflowException ex) {
            throw new IllegalArgumentException(ex.getMessage(), ex);
        }

        var wfChangesTracker =
            m_trackers.computeIfAbsent(workflowKey, k -> workflowChangesListener.createWorkflowChangeTracker(true));

        // create very first changed event to be sent first (and thus catch up with the most recent
        // workflow version)
        var workflowChangedEvent = m_workflowMiddleware.buildWorkflowChangedEvent( //
            workflowKey, //
            new PatchEntCreator(null), //
            wfEventType.getSnapshotId(), //
            true, //
            wfChangesTracker //
        );

        // add and keep track of callback added to the workflow changes listener (if not already)
        m_workflowChangesCallbacks.computeIfAbsent(workflowKey, wfKey -> {
            String latestSnapshotId =
                workflowChangedEvent == null ? wfEventType.getSnapshotId() : workflowChangedEvent.getSnapshotId();
            Runnable callback =
                createWorkflowChangesCallback(workflowKey, new PatchEntCreator(latestSnapshotId), wfChangesTracker);
            workflowChangesListener.addWorkflowChangeCallback(callback);
            return callback;
        });

        if (workflowChangedEvent == null) {
            return Optional.empty();
        } else {
            var projectDirtyStateEvent = EntityBuilderManager.builder(ProjectDirtyStateEventEntBuilder.class)
                .setDirtyProjectsMap(m_projectManager.getDirtyProjectsMap()).build();

            return Optional.of(EntityBuilderManager.builder(CompositeEventEntBuilder.class)
                .setEvents(List.of(workflowChangedEvent, projectDirtyStateEvent)).build());
        }
    }

    private Runnable createWorkflowChangesCallback(final WorkflowKey wfKey, final PatchEntCreator patchEntCreator,
        final WorkflowChangesTracker tracker) {
        var wfm = DefaultServiceUtil.getWorkflowManager(wfKey.getProjectId(), wfKey.getWorkflowId());
        return () -> {
            preEventCreation();
            WorkflowChangedEventEnt workflowChangedEvent = m_workflowMiddleware.buildWorkflowChangedEvent(wfKey,
                patchEntCreator, patchEntCreator.getLastSnapshotId(), true, tracker);
            if (workflowChangedEvent != null) {
                var compositeEvent = createCompositeEvent(wfKey, wfm, workflowChangedEvent);
                sendEvent(compositeEvent, wfKey.getProjectId());
            }
        };
    }

    private static CompositeEventEnt createCompositeEvent(final WorkflowKey wfKey, final WorkflowManager wfm,
        final WorkflowChangedEventEnt workflowChangedEvent) {
        var projectDirtyStateEvent = EntityBuilderManager.builder(ProjectDirtyStateEventEntBuilder.class)
            .setDirtyProjectsMap(Map.of(wfKey.getProjectId(), wfm.isDirty())).build();
        return EntityBuilderManager.builder(CompositeEventEntBuilder.class)
            .setEvents(List.of(workflowChangedEvent, projectDirtyStateEvent)).build();
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
        var callback = m_workflowChangesCallbacks.remove(wfKey);
        if (callback != null && m_workflowMiddleware.hasStateFor(wfKey)) {
            m_workflowMiddleware.getWorkflowChangesListener(wfKey).removeCallback(callback);
        }
    }

    @SuppressWarnings("resource")
    private void removeTracker(final WorkflowKey wfKey) {
        var tracker = m_trackers.remove(wfKey);
        if (tracker != null && m_workflowMiddleware.hasStateFor(wfKey)) {
            m_workflowMiddleware.getWorkflowChangesListener(wfKey).removeWorkflowChangesTracker(tracker);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeAllEventListeners() {
        new HashSet<>(m_workflowChangesCallbacks.keySet()).forEach(this::removeEventListener);
        new HashSet<>(m_trackers.keySet()).forEach(this::removeTracker);
    }

    /**
     * For testing purposes only!
     *
     * @param state
     * @return <code>true</code> any of the {@link WorkflowChangesListener}s has the callback-state as given by the
     *         argument
     */
    public boolean checkWorkflowChangesListenerCallbackState(final CallState state) {
        return m_workflowChangesCallbacks.keySet().stream()
            .anyMatch(k -> m_workflowMiddleware.getWorkflowChangesListener(k).getCallState() == state);
    }

    /**
     * For testing purposes only.
     *
     * @return the number of registered event listeners
     */
    int getNumRegisteredListeners() {
        assert m_workflowChangesCallbacks.size() == m_trackers.size();
        return m_workflowChangesCallbacks.size();
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
            m_ops.clear();
            return builder(WorkflowChangedEventEntBuilder.class).setPatch(patch).setSnapshotId(newSnapshotId).build();

        }

        String getLastSnapshotId() {
            return m_lastSnapshotId;
        }
    }

}

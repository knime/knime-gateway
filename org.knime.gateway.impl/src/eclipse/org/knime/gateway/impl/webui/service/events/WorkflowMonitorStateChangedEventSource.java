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
 *   Apr 26, 2024 (hornm): created
 */
package org.knime.gateway.impl.webui.service.events;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;

import org.knime.gateway.api.entity.NodeIDEnt;
import org.knime.gateway.api.webui.entity.WorkflowMonitorStateChangeEventEnt;
import org.knime.gateway.api.webui.entity.WorkflowMonitorStateChangeEventTypeEnt;
import org.knime.gateway.impl.project.ProjectManager;
import org.knime.gateway.impl.service.util.PatchEntCreator;
import org.knime.gateway.impl.webui.WorkflowKey;
import org.knime.gateway.impl.webui.WorkflowMiddleware;

/**
 * An event source emitting {@link WorkflowMonitorStateChangeEventEnt}s.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
public final class WorkflowMonitorStateChangedEventSource
    extends EventSource<WorkflowMonitorStateChangeEventTypeEnt, WorkflowMonitorStateChangeEventEnt> {

    private final WorkflowMiddleware m_workflowMiddleware;

    // In a multi-user scenario we will need to keep track of the callbacks
    // per 'user/client' instead of per workflow - see NXT-2599
    private final Map<String, Runnable> m_workflowChangeCallbacks = new HashMap<>();

    /**
     * @param eventConsumer
     * @param projectManager
     * @param workflowMiddleware
     */
    public WorkflowMonitorStateChangedEventSource(final EventConsumer eventConsumer,
        final ProjectManager projectManager, final WorkflowMiddleware workflowMiddleware) {
        super(eventConsumer);
        m_workflowMiddleware = workflowMiddleware;
        projectManager.addProjectRemovedListener(m_workflowChangeCallbacks::remove);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<WorkflowMonitorStateChangeEventEnt>
        addEventListenerAndGetInitialEventFor(final WorkflowMonitorStateChangeEventTypeEnt eventTypeEnt,
            final String contextProjectId) {
        assertValidProjectId(contextProjectId, eventTypeEnt.getProjectId());
        var projectId = eventTypeEnt.getProjectId();
        var wfKey = new WorkflowKey(projectId, NodeIDEnt.getRootID());

        // initial event to catch-up with the most recent workflow version
        var patchEntCreator = new PatchEntCreator(eventTypeEnt.getSnapshotId());
        // no-sonar: initial event needs to be created before registering the callbacks
        // also because it might create a new snapshot id (relayed via the patchEntCreator)
        var initialEvent = m_workflowMiddleware.buildWorkflowMonitorStateChangeEventEnt(wfKey, // NOSONAR
            eventTypeEnt.getSnapshotId(), patchEntCreator);

        m_workflowChangeCallbacks.computeIfAbsent(projectId, id -> { // NOSONAR
            Runnable callback = () -> {
                var event = m_workflowMiddleware.buildWorkflowMonitorStateChangeEventEnt(wfKey,
                    patchEntCreator.getLastSnapshotId(), patchEntCreator);
                if (event != null) {
                    sendEvent(event, projectId);
                }
            };
            m_workflowMiddleware.getWorkflowChangesListenerForWorkflowMonitor(wfKey)
                .addWorkflowChangeCallback(callback);
            return callback;
        });

        return Optional.ofNullable(initialEvent);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeEventListener(final WorkflowMonitorStateChangeEventTypeEnt eventTypeEnt, final String projectId) {
        removeEventListener(eventTypeEnt.getProjectId());
    }

    private void removeEventListener(final String projectId) {
        var callback = m_workflowChangeCallbacks.remove(projectId);
        if (callback != null) {
            m_workflowMiddleware
                .getWorkflowChangesListenerForWorkflowMonitor(new WorkflowKey(projectId, NodeIDEnt.getRootID()))
                .removeCallback(callback);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeAllEventListeners() {
        new HashSet<>(m_workflowChangeCallbacks.keySet()).forEach(this::removeEventListener);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String getName() {
        return "WorkflowMonitorStateChangeEvent";
    }

}

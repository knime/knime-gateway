/*
 * ------------------------------------------------------------------------
 *
 *  Copyright by KNIME AG, Zurich, Switzerland
 *  Website: http://www.knime.com; Email: contact@knime.com
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
 */
package org.knime.gateway.impl.service.util;

import java.io.Closeable;
import java.util.BitSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

import org.knime.core.node.workflow.ConnectionContainer;
import org.knime.core.node.workflow.ConnectionID;
import org.knime.core.node.workflow.ConnectionProgressListener;
import org.knime.core.node.workflow.NodeContainer;
import org.knime.core.node.workflow.NodeID;
import org.knime.core.node.workflow.NodeMessageListener;
import org.knime.core.node.workflow.NodeProgressListener;
import org.knime.core.node.workflow.NodeStateChangeListener;
import org.knime.core.node.workflow.NodeUIInformationListener;
import org.knime.core.node.workflow.WorkflowAnnotation;
import org.knime.core.node.workflow.WorkflowAnnotationID;
import org.knime.core.node.workflow.WorkflowEvent;
import org.knime.core.node.workflow.WorkflowListener;
import org.knime.core.node.workflow.WorkflowManager;
import org.knime.gateway.api.util.CoreUtil;

/**
 * Summarizes all kind of workflow changes and allows one to register one single listener to all of them.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz
 */
public class WorkflowChangesListener implements Closeable {

    private final WorkflowManager m_wfm;

    private final WorkflowListener m_workflowListener;

    private final Map<NodeID, NodeStateChangeListener> m_nodeStateChangeListeners = new HashMap<>();

    private final Map<NodeID, NodeProgressListener> m_progressListeners = new HashMap<>();

    private final Map<NodeID, NodeUIInformationListener> m_nodeUIListeners = new HashMap<>();

    private final Map<NodeID, NodeMessageListener> m_nodeMessageListeners = new HashMap<>();

    private final Map<WorkflowAnnotationID, NodeUIInformationListener> m_workflowAnnotationListeners = new HashMap<>();

    private final Map<ConnectionID, ConnectionProgressListener> m_connectionListeners;

    private final Set<Consumer<WorkflowManager>> m_callbacks;

    private final ExecutorService m_executorService;

    private final AtomicCallbackState m_callbackState = new AtomicCallbackState();

    private final boolean m_isInStreamingMode;

    private final WorkflowChanges m_changes;

    /**
     * @param wfm the workflow manager to listen to
     */
    public WorkflowChangesListener(final WorkflowManager wfm) {
        m_wfm = wfm;
        m_executorService = Executors.newSingleThreadExecutor(r -> {
            Thread t = new Thread(r, "KNIME-Workflow-Changes-Listener (" + m_wfm.getName() + ")");
            t.setDaemon(true);
            return t;
        });
        m_callbacks = new HashSet<>();

        m_isInStreamingMode = CoreUtil.isInStreamingMode(m_wfm);
        m_connectionListeners = m_isInStreamingMode ? new HashMap<>() : null;

        m_changes = new WorkflowChanges(wfm, true);
        m_workflowListener = startListening();
    }

    /**
     * Adds a callback which is called as soon as the associated workflow changed.
     *
     * @param callback the callback to call if a change occurs in the workflow manager
     */
    public void addCallback(final Consumer<WorkflowManager> callback) {
        m_callbacks.add(callback);
    }

    /**
     * Removes a registered callback.
     *
     * @param callback
     */
    public void removeCallback(final Consumer<WorkflowManager> callback) {
        m_callbacks.remove(callback);
    }

    private WorkflowListener startListening() {
        WorkflowListener workflowListener = e -> {
            addOrRemoveListenersFromNodeOrWorkflowAnnotation(e);
            trackChange(e);
            callback();
        };
        m_wfm.addListener(workflowListener);
        m_wfm.getNodeContainers().forEach(this::addNodeListeners);
        m_wfm.getWorkflowAnnotations().forEach(this::addWorkflowAnnotationListener);
        if (m_isInStreamingMode) {
            m_wfm.getConnectionContainers().forEach(this::addConnectionListener);
        }
        return workflowListener;
    }

    private void trackChange(final WorkflowEvent e) {
        switch (e.getType()) {
            case NODE_ADDED:
            case NODE_REMOVED:
            case CONNECTION_ADDED:
            case CONNECTION_REMOVED:
                m_changes.trackChange(WorkflowChanges.NODE_OR_CONNECTION_ADDED_OR_REMOVED);
            default:
                //
        }
    }

    private void addOrRemoveListenersFromNodeOrWorkflowAnnotation(final WorkflowEvent e) {
        switch (e.getType()) {
            case NODE_ADDED:
                addNodeListeners(m_wfm.getNodeContainer(e.getID()));
                break;
            case NODE_REMOVED:
                removeNodeListeners((NodeContainer)e.getOldValue());
                break;
            case ANNOTATION_ADDED:
                addWorkflowAnnotationListener((WorkflowAnnotation)e.getNewValue());
                break;
            case ANNOTATION_REMOVED:
                removeWorkflowAnnotationListener((WorkflowAnnotation)e.getOldValue());
                break;
            case CONNECTION_ADDED:
                if (m_isInStreamingMode) {
                    addConnectionListener((ConnectionContainer)e.getNewValue());
                }
                break;
            case CONNECTION_REMOVED:
                if (m_isInStreamingMode) {
                    removeConnectionListener((ConnectionContainer)e.getOldValue());
                }
            default:
                //
        }
    }

    private void addNodeListeners(final NodeContainer nc) {
        NodeStateChangeListener sl = e -> {
            m_changes.trackChange(WorkflowChanges.NODE_STATE);
            callback();
        };
        m_nodeStateChangeListeners.put(nc.getID(), sl);
        nc.addNodeStateChangeListener(sl);
        NodeProgressListener pl = e -> callback();
        m_progressListeners.put(nc.getID(), pl);
        nc.getProgressMonitor().addProgressListener(pl);
        NodeUIInformationListener uil = e -> callback();
        m_nodeUIListeners.put(nc.getID(), uil);
        nc.addUIInformationListener(uil);
        nc.getNodeAnnotation().addUIInformationListener(uil);
        NodeMessageListener nml = e -> callback();
        m_nodeMessageListeners.put(nc.getID(), nml);
        nc.addNodeMessageListener(nml);
    }

    private void removeNodeListeners(final NodeContainer nc) {
        NodeID id = nc.getID();
        nc.removeNodeStateChangeListener(m_nodeStateChangeListeners.get(id));
        nc.getProgressMonitor().removeProgressListener(m_progressListeners.get(id));
        nc.removeUIInformationListener(m_nodeUIListeners.get(id));
        nc.getNodeAnnotation().removeUIInformationListener(m_nodeUIListeners.get(id));
        nc.removeNodeMessageListener(m_nodeMessageListeners.get(id));
        m_nodeStateChangeListeners.remove(id);
        m_progressListeners.remove(id);
        m_nodeUIListeners.remove(id);
        m_nodeMessageListeners.remove(id);
    }

    private void addConnectionListener(final ConnectionContainer cc) {
        ConnectionProgressListener l = e -> callback();
        cc.addProgressListener(l);
        m_connectionListeners.put(cc.getID(), l);
    }

    private void removeConnectionListener(final ConnectionContainer cc) {
        ConnectionID id = cc.getID();
        cc.removeProgressListener(m_connectionListeners.get(id));
        m_connectionListeners.remove(id);
    }

    private void addWorkflowAnnotationListener(final WorkflowAnnotation wa) {
        NodeUIInformationListener l = e -> callback();
        m_workflowAnnotationListeners.put(wa.getID(), l);
        wa.addUIInformationListener(l);
    }

    private void removeWorkflowAnnotationListener(final WorkflowAnnotation wa) {
        wa.removeUIInformationListener(m_workflowAnnotationListeners.get(wa.getID()));
        m_workflowAnnotationListeners.remove(wa.getID());
    }

    private void callback() {
        m_changes.trackChange();
        if (!m_callbackState.checkIsCallbackInProgressAndChangeState()) {
            m_executorService.execute(() -> {
                do {
                    m_callbacks.forEach(c -> c.accept(m_wfm));
                } while (m_callbackState.checkIsCallbackAwaitingAndChangeState());
            });
        }
    }

    private void stopListening() {
        m_wfm.removeListener(m_workflowListener);
        m_wfm.getNodeContainers().forEach(this::removeNodeListeners);
        m_wfm.getWorkflowAnnotations().forEach(this::removeWorkflowAnnotationListener);
        m_nodeMessageListeners.clear();
        m_nodeStateChangeListeners.clear();
        m_nodeUIListeners.clear();
        m_progressListeners.clear();
        m_workflowAnnotationListeners.clear();
        if (m_isInStreamingMode) {
            m_wfm.getConnectionContainers().forEach(this::removeConnectionListener);
            m_connectionListeners.clear();
        }
    }

    @Override
    public void close() {
        stopListening();
        m_executorService.shutdown();
    }

    /**
     * Gives the current callback state. Mainly for testing purposes.
     *
     * @return the current callback state
     */
    public CallbackState getCallbackState() {
        return m_callbackState.m_state;
    }

    /**
     * Gives access to the workflow changes that have been tracked.
     *
     * @return an object that allows one to access the workflow changes
     */
    public WorkflowChanges getChanges() {
        return m_changes;
    }

    /**
     * The state the 'callback process' is in.
     */
    public enum CallbackState {
            /**
             * No callback in progress nor is one awaiting
             */
            IDLE,

            /**
             * Callback in progress, none is awaiting
             */
            IN_PROGRESS,

            /**
             * Callback in progress, and another one awaiting
             */
            IN_PROGRESS_AND_AWAITING;
    }

    private static final class AtomicCallbackState {

        private CallbackState m_state = CallbackState.IDLE;

        /**
         * If the callback is in progress, state will change to 'in progress and one callback awaiting' (2); else to 'in
         * progress, no callback awaiting' (1).
         *
         * @return <code>true</code> if the callback is in progress, otherwise <code>false</code>
         */
        synchronized boolean checkIsCallbackInProgressAndChangeState() {
            if (m_state == CallbackState.IDLE) {
                m_state = CallbackState.IN_PROGRESS;
                return false;
            } else {
                m_state = CallbackState.IN_PROGRESS_AND_AWAITING;
                return true;
            }
        }

        /**
         * If a callback is awaiting (and another already in progress), the state will change to 'in progress' (1);
         * otherwise it will change to 'not in progress, none awaiting' (0)
         *
         * @return <code>true</code> if a callback is awaiting, otherwise <code>false</code>
         */
        synchronized boolean checkIsCallbackAwaitingAndChangeState() {
            if (m_state == CallbackState.IN_PROGRESS_AND_AWAITING) {
                m_state = CallbackState.IN_PROGRESS;
                return true;
            } else {
                m_state = CallbackState.IDLE;
                return false;
            }
        }

    }

    /**
     * Tracks the changes till the last {@link WorkflowChanges#reset()}. To avoid missing changes, all the methods must
     * always be called within the very same workflow-lock block.
     */
    public static final class WorkflowChanges {

        private final BitSet m_changes;

        private static final int NODE_STATE = 0;

        private static final int NODE_OR_CONNECTION_ADDED_OR_REMOVED = 1;

        private static final int ANY = 2;

        private WorkflowManager m_wfm;

        private WorkflowChanges(final WorkflowManager wfm, final boolean setAll) {
            m_wfm = wfm;
            m_changes = new BitSet(3);
            if (setAll) {
                m_changes.set(0, 3);
            }
        }

        /**
         * Resets all the tracked changes.
         *
         * Must be called with a workflow lock set (i.e. {@link WorkflowManager#lock()} on the associated workflow).
         */
        public void reset() {
            assert m_wfm == null || m_wfm.isLockedByCurrentThread();
            m_changes.clear();
        }

        /**
         * Whether there are node state changes since the last {@link #reset()}. Must be called with a workflow lock set
         * (i.e. {@link WorkflowManager#lock()} on the associated workflow).
         *
         * @return <code>true</code> if there were node state changes since the last reset
         */
        public boolean nodeStateChanges() {
            assert m_wfm == null || m_wfm.isLockedByCurrentThread();
            return m_changes.get(NODE_STATE);
        }

        /**
         * Whether there have been nodes or connection been removed or added since the last {@link #reset()}. Must be
         * called with a workflow lock set (i.e. {@link WorkflowManager#lock()} on the associated workflow).
         *
         * @return <code>true</code> if there are respective changes since the last reset
         */
        public boolean nodeOrConnectionAddedOrRemoved() {
            assert m_wfm == null || m_wfm.isLockedByCurrentThread();
            return m_changes.get(NODE_OR_CONNECTION_ADDED_OR_REMOVED);
        }

        /**
         * @return <code>true</code> if there has been any change to the workflow since the last {@link #reset()},
         *         otherwise <code>false</code>
         */
        public boolean anyChange() {
            assert m_wfm == null || m_wfm.isLockedByCurrentThread();
            return m_changes.get(ANY);
        }

        private synchronized void trackChange(final int change) {
            m_changes.set(change);
        }

        private synchronized void trackChange() {
            m_changes.set(ANY);
        }

    }

}
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
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import org.knime.core.node.workflow.ConnectionContainer;
import org.knime.core.node.workflow.ConnectionID;
import org.knime.core.node.workflow.ConnectionProgressListener;
import org.knime.core.node.workflow.LoopStatusChangeListener;
import org.knime.core.node.workflow.NativeNodeContainer;
import org.knime.core.node.workflow.NodeContainer;
import org.knime.core.node.workflow.NodeMessageListener;
import org.knime.core.node.workflow.NodeProgressListener;
import org.knime.core.node.workflow.NodePropertyChangedListener;
import org.knime.core.node.workflow.NodeStateChangeListener;
import org.knime.core.node.workflow.NodeUIInformationListener;
import org.knime.core.node.workflow.WorkflowAnnotation;
import org.knime.core.node.workflow.WorkflowAnnotationID;
import org.knime.core.node.workflow.WorkflowEvent;
import org.knime.core.node.workflow.WorkflowListener;
import org.knime.core.node.workflow.WorkflowManager;
import org.knime.gateway.api.util.CoreUtil;
import org.knime.gateway.impl.service.util.WorkflowChangesTracker.WorkflowChange;
import org.knime.gateway.impl.webui.WorkflowKey;

/**
 * Summarizes all kind of workflow changes and allows one to register one single listener to all of them.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz
 * @author Benjamin Moser, KNIME GmbH, Konstanz
 */
public class WorkflowChangesListener implements Closeable {

    private final WorkflowManager m_wfm;

    private final WorkflowListener m_workflowListener;

    private final NodeListenerMap<NodeStateChangeListener> m_nodeStateChangeListeners =
        new NodeListenerMap<>(NodeContainer::addNodeStateChangeListener, NodeContainer::removeNodeStateChangeListener);

    private final NodeListenerMap<NodeProgressListener> m_progressListeners =
        new NodeListenerMap<>((nc, l) -> nc.getProgressMonitor().addProgressListener(l),
            (nc, l) -> nc.getProgressMonitor().removeProgressListener(l));

    private final NodeListenerMap<NodeUIInformationListener> m_nodeUIListeners = new NodeListenerMap<>( //
        (nc, l) -> { //
            nc.addUIInformationListener(l);
            nc.getNodeAnnotation().addUIInformationListener(l);
        }, //
        (nc, l) -> { //
            nc.removeUIInformationListener(l);
            nc.getNodeAnnotation().removeUIInformationListener(l);
        });

    private final NodeListenerMap<NodeMessageListener> m_nodeMessageListeners =
        new NodeListenerMap<>(NodeContainer::addNodeMessageListener, NodeContainer::removeNodeMessageListener);

    private final NodeListenerMap<NodePropertyChangedListener> m_nodePropertyChangedListeners = new NodeListenerMap<>(
        NodeContainer::addNodePropertyChangedListener, NodeContainer::removeNodePropertyChangedListener);

    private final NodeListenerMap<LoopStatusChangeListener> m_loopStatusChangeListeners = new NodeListenerMap<>(
        (nc, l) -> getNNC(nc).flatMap(NativeNodeContainer::getLoopStatusChangeHandler)
            .ifPresent(h -> h.addLoopPausedListener(l)),
        (nc, l) -> getNNC(nc).flatMap(NativeNodeContainer::getLoopStatusChangeHandler)
            .ifPresent(h -> h.removeLoopPausedListener(l)));

    private final Map<WorkflowAnnotationID, NodeUIInformationListener> m_workflowAnnotationListeners = new HashMap<>();

    private final Map<ConnectionID, ConnectionProgressListener> m_connectionListeners;

    private final Set<Consumer<WorkflowManager>> m_workflowChangedCallbacks;

    private final Set<WorkflowChangesTracker> m_workflowChangesTrackers = Collections.synchronizedSet(new HashSet<>());

    private final Set<Runnable> m_postProcessCallbacks = new HashSet<>();

    private final ExecutorService m_executorService;

    private final AtomicCallbackState m_callbackState = new AtomicCallbackState();

    private final boolean m_isInStreamingMode;

    /**
     * @param wfm the workflow manager to listen to
     */
    public WorkflowChangesListener(final WorkflowManager wfm) {
        m_wfm = wfm;
        m_executorService = Executors.newSingleThreadExecutor(r -> {
            var t = new Thread(r, "KNIME-Workflow-Changes-Listener (" + m_wfm.getName() + ")");
            t.setDaemon(true);
            return t;
        });
        m_workflowChangedCallbacks = new HashSet<>();

        m_isInStreamingMode = CoreUtil.isInStreamingMode(m_wfm);
        m_connectionListeners = m_isInStreamingMode ? new HashMap<>() : null;

        m_workflowListener = startListening();
    }

    /**
     * Adds a callback which is called as soon as the associated workflow changed.
     *
     * @param callback the callback to call if a change occurs in the workflow manager
     */
    public void addWorkflowChangeCallback(final Consumer<WorkflowManager> callback) {
        m_workflowChangedCallbacks.add(callback);
    }

    /**
     * Removes a registered callback.
     *
     * @param callback
     */
    public void removeCallback(final Consumer<WorkflowManager> callback) {
        m_workflowChangedCallbacks.remove(callback);
    }

    /**
     * @param tracker The tracker to remove.
     */
    public void removeWorkflowChangesTracker(final WorkflowChangesTracker tracker) {
        m_workflowChangesTrackers.remove(tracker);
    }

    private void updateWorkflowChangesTrackers(final WorkflowChangesTracker.WorkflowChange workflowChange) {
        m_workflowChangesTrackers.forEach(t -> t.track(workflowChange));
    }

    /**
     * Initialise a waiter for workflow changes. The waiter is aware of changes since its creation.
     *
     * @param changesToWaitFor The changes to wait for
     * @return The created and initialised waiter.
     */
    public WorkflowChangeWaiter
        createWorkflowChangeWaiter(final WorkflowChangesTracker.WorkflowChange... changesToWaitFor) {
        return new WorkflowChangeWaiter(changesToWaitFor, this);
    }

    /**
     * Creates a new {@link WorkflowChangesTracker}-instance. Once a tracker is not needed anymore it should be
     * de-registered via {@link #removeWorkflowChangesTracker(WorkflowChangesTracker)}.
     *
     * @param setAllOccurred if {@code true}, set all possible changes to "have occurred" for the returned tracker
     * @return a new instance of a {@link WorkflowChangesTracker} for the workflow referenced by the {@link WorkflowKey}
     */
    public WorkflowChangesTracker createWorkflowChangeTracker(final boolean setAllOccurred) {
        var tracker = new WorkflowChangesTracker(setAllOccurred);
        m_workflowChangesTrackers.add(tracker);
        return tracker;
    }

    /**
     * Creates a new {@link WorkflowChangesTracker}-instance. Once a tracker is not needed anymore it should be
     * de-registered via {@link #removeWorkflowChangesTracker(WorkflowChangesTracker)}.
     *
     * @return a new instance of a {@link WorkflowChangesTracker} for the workflow referenced by the {@link WorkflowKey}
     */
    public WorkflowChangesTracker createWorkflowChangeTracker() {
        return createWorkflowChangeTracker(false);
    }

    /**
     * Register a callback to be called after workflow-change callbacks have been processed.
     *
     * @see this#m_workflowChangedCallbacks
     * @param listener The callback to add
     */
    void addPostProcessCallback(final Runnable listener) {
        m_postProcessCallbacks.add(listener);
    }

    /**
     * @param listener The callback to remove
     */
    void removePostProcessCallback(final Runnable listener) {
        m_postProcessCallbacks.remove(listener);
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
                updateWorkflowChangesTrackers(WorkflowChange.NODE_ADDED);
                break;
            case NODE_REMOVED:
                updateWorkflowChangesTrackers(WorkflowChange.NODE_REMOVED);
                break;
            case CONNECTION_ADDED:
                updateWorkflowChangesTrackers(WorkflowChange.CONNECTION_ADDED);
                break;
            case CONNECTION_REMOVED:
                updateWorkflowChangesTrackers(WorkflowChangesTracker.WorkflowChange.CONNECTION_REMOVED);
                break;
            case NODE_COLLAPSED:
                updateWorkflowChangesTrackers(WorkflowChangesTracker.WorkflowChange.NODES_COLLAPSED);
                break;
            case NODE_EXPANDED:
                updateWorkflowChangesTrackers(WorkflowChangesTracker.WorkflowChange.NODE_EXPANDED);
                break;
            case ANNOTATION_ADDED:
                updateWorkflowChangesTrackers(WorkflowChangesTracker.WorkflowChange.ANNOTATION_ADDED);
                break;
            case ANNOTATION_REMOVED:
                updateWorkflowChangesTrackers(WorkflowChangesTracker.WorkflowChange.ANNOTATION_ADDED);
                break;
            default:
                //
        }
    }

    private void addOrRemoveListenersFromNodeOrWorkflowAnnotation(final WorkflowEvent e) {
        switch (e.getType()) {
            case NODE_ADDED:
                if (m_wfm.containsNodeContainer(e.getID())) {
                    addNodeListeners(m_wfm.getNodeContainer(e.getID()));
                }
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
                break;
            default:
                //
        }
    }

    private void addNodeListeners(final NodeContainer nc) {
        m_nodeStateChangeListeners.add(nc, e -> {
            updateWorkflowChangesTrackers(WorkflowChangesTracker.WorkflowChange.NODE_STATE_UPDATED);
            callback();
        });
        m_progressListeners.add(nc, e -> callback());
        m_nodeUIListeners.add(nc, e -> callback());
        m_nodeMessageListeners.add(nc, e -> callback());
        m_loopStatusChangeListeners.add(nc, this::callback);
        m_nodePropertyChangedListeners.add(nc, e -> callback());
    }

    private void removeNodeListeners(final NodeContainer nc) {
        List.of( //
            m_nodeStateChangeListeners, //
            m_progressListeners, //
            m_nodeUIListeners, //
            m_nodeMessageListeners, //
            m_loopStatusChangeListeners, //
            m_nodePropertyChangedListeners //
        ).forEach(nlm -> nlm.remove(nc));
    }

    private static Optional<NativeNodeContainer> getNNC(final NodeContainer nc) {
        return (nc instanceof NativeNodeContainer) ? Optional.of((NativeNodeContainer)nc) : Optional.empty();
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
        updateWorkflowChangesTrackers(WorkflowChangesTracker.WorkflowChange.ANY);
        if (!m_callbackState.checkIsCallbackInProgressAndChangeState()) {
            m_executorService.execute(() -> {
                do {
                    m_workflowChangedCallbacks.forEach(c -> c.accept(m_wfm));
                    m_postProcessCallbacks.forEach(c -> c.run());
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
     * Wrapper around HashMap that uses the given NodeContainer's {@link System#identityHashCode(Object)} as keys. Only
     * performs update if key not yet present. {@link org.knime.core.node.workflow.NodeID} does not suffice because the
     * object associated with a node ID may change in the underlying workflow (e.g. conversion from metanode to
     * component). Dependency to {@link NodeContainer} and related classes is avoided for maintainability.
     *
     * @param <L> The type of event listener to attach / detach
     */
    private static final class NodeListenerMap<L> {

        private final Map<Integer, L> m_listeners = new HashMap<>();

        final BiConsumer<NodeContainer, L> m_attacher;

        final BiConsumer<NodeContainer, L> m_detacher;

        private NodeListenerMap(final BiConsumer<NodeContainer, L> attacher,
            final BiConsumer<NodeContainer, L> detacher) {
            m_attacher = attacher;
            m_detacher = detacher;
        }

        private static Integer getKey(final NodeContainer nc) {
            return System.identityHashCode(nc);
        }

        private void add(final NodeContainer nc, final L listener) {
            var ncKey = getKey(nc);

            if (!m_listeners.containsKey(ncKey)) {  // NOSONAR
                m_attacher.accept(nc, listener);
                m_listeners.put(ncKey, listener);
            }
        }

        private void remove(final NodeContainer nc) {
            var ncKey = getKey(nc);
            var listener = m_listeners.remove(ncKey);
            if (listener != null) {
                m_detacher.accept(nc, listener);
            }
        }

        private void clear() {
            m_listeners.clear();
        }
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
}
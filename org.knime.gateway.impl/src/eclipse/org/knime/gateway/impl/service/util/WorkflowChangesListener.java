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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiConsumer;

import org.knime.core.node.workflow.ConnectionContainer;
import org.knime.core.node.workflow.LoopStatusChangeListener;
import org.knime.core.node.workflow.NativeNodeContainer;
import org.knime.core.node.workflow.NodeContainer;
import org.knime.core.node.workflow.NodeProgressListener;
import org.knime.core.node.workflow.NodeUIInformationListener;
import org.knime.core.node.workflow.WorkflowAnnotation;
import org.knime.core.node.workflow.WorkflowEvent;
import org.knime.core.node.workflow.WorkflowEvent.Type;
import org.knime.core.node.workflow.WorkflowListener;
import org.knime.core.node.workflow.WorkflowManager;
import org.knime.gateway.api.util.CoreUtil;
import org.knime.gateway.impl.service.util.CallThrottle.CallState;
import org.knime.gateway.impl.service.util.WorkflowChangesTracker.WorkflowChange;
import org.knime.gateway.impl.webui.WorkflowKey;

/**
 * Summarizes all kind of workflow changes and allows one to register one single listener to all of them.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz
 * @author Benjamin Moser, KNIME GmbH, Konstanz
 * @author Kai Franze, KNIME GmbH
 */
public class WorkflowChangesListener implements Closeable {

    private final WorkflowManager m_wfm;

    private final Set<Runnable> m_workflowChangedCallbacks = new HashSet<>();

    private final Set<WorkflowChangesTracker> m_workflowChangesTrackers = Collections.synchronizedSet(new HashSet<>());

    private final Set<Runnable> m_postProcessCallbacks = new HashSet<>();

    private final CallThrottle m_callThrottle;

    private final WorkflowListener m_workflowListener;

    private final List<Listener<NodeContainer>> m_nodeListeners = new ArrayList<>();

    private Listener<WorkflowAnnotation> m_workflowAnnotationListener = new NoopListener<>();

    private Listener<ConnectionContainer> m_connectionUIInformationListener =
        new NoopListener<>();

    private Listener<ConnectionContainer> m_connectionProgressListener = new NoopListener<>();

    boolean m_isListening;

    private final boolean m_recurse;

    /**
     * Allows one to define what aspects of the workflow to listen to.
     */
    public enum Scope {
            /**
             * Listen to node message changes.
             */
            NODE_MESSAGES, //
            /**
             * Listen to everything that can be listened to.
             */
            EVERYTHING;
    }

    /**
     * @param wfm the workflow manager to listen to
     */
    public WorkflowChangesListener(final WorkflowManager wfm) {
        this(wfm, Set.of(Scope.EVERYTHING), false);
    }

    /**
     * @param wfm the workflow manager to listen to
     * @param scopes what workflow changes to listen to
     * @param recurse whether to recurse into sub-workflows to listen for respective changes there, too
     */
    @SuppressWarnings("java:S2293") // diamond operator: need to explicitly specify type params
    public WorkflowChangesListener(final WorkflowManager wfm, final Set<Scope> scopes, final boolean recurse) {
        m_wfm = wfm;
        m_recurse = recurse;

        var isInStreamingMode = CoreUtil.isInStreamingMode(m_wfm);
        if (isInStreamingMode) {
            m_connectionProgressListener = new ListenerImpl<>(ConnectionContainer::addProgressListener,
                ConnectionContainer::removeProgressListener, e -> callback());
        }

        m_callThrottle = new CallThrottle(() -> {
            m_workflowChangedCallbacks.forEach(Runnable::run);
            m_postProcessCallbacks.forEach(Runnable::run);
        }, "KNIME-Workflow-Changes-Listener (" + m_wfm.getName() + ")");

        m_workflowListener = e -> {
            addOrRemoveListenersFromNodeOrWorkflowAnnotation(e);
            trackChange(e);
            var type = e.getType();
            if (scopes.contains(Scope.EVERYTHING) || //
                (scopes.contains(Scope.NODE_MESSAGES) && (type == Type.NODE_ADDED || type == Type.NODE_REMOVED))) {
                callback();
            }
        };

        if (scopes.contains(Scope.NODE_MESSAGES) || scopes.contains(Scope.EVERYTHING)) {
            m_nodeListeners.add(new ListenerImpl<>(NodeContainer::addNodeMessageListener,
                NodeContainer::removeNodeMessageListener, e -> callback()));
        }
        if (scopes.contains(Scope.EVERYTHING)) {
            m_nodeListeners.add(new ListenerImpl<>(NodeContainer::addNodeStateChangeListener,
                NodeContainer::removeNodeStateChangeListener, e -> {
                    updateWorkflowChangesTrackers(WorkflowChangesTracker.WorkflowChange.NODE_STATE_UPDATED);
                    callback();
                }));
            m_nodeListeners.add(new ListenerImpl<NodeContainer, NodeProgressListener>(
                (nc, l) -> nc.getProgressMonitor().addProgressListener(l),
                (nc, l) -> nc.getProgressMonitor().removeProgressListener(l), e -> callback()));
            m_nodeListeners.add(new ListenerImpl<NodeContainer, NodeUIInformationListener>( //
                (nc, l) -> { //
                    nc.addUIInformationListener(l);
                    nc.getNodeAnnotation().addUIInformationListener(l);
                }, //
                (nc, l) -> { //
                    nc.removeUIInformationListener(l);
                    nc.getNodeAnnotation().removeUIInformationListener(l);
                }, e -> callback()));
            m_nodeListeners.add(new ListenerImpl<>(NodeContainer::addNodePropertyChangedListener,
                NodeContainer::removeNodePropertyChangedListener, e -> callback()));
            m_nodeListeners.add(new ListenerImpl<NodeContainer, LoopStatusChangeListener>(
                (nc, l) -> getNNC(nc).flatMap(NativeNodeContainer::getLoopStatusChangeHandler)
                    .ifPresent(h -> h.addLoopPausedListener(l)),
                (nc, l) -> getNNC(nc).flatMap(NativeNodeContainer::getLoopStatusChangeHandler)
                    .ifPresent(h -> h.removeLoopPausedListener(l)),
                this::callback));
            m_workflowAnnotationListener = new ListenerImpl<>(WorkflowAnnotation::addUIInformationListener,
                WorkflowAnnotation::removeUIInformationListener, e -> callback());
            m_connectionUIInformationListener = new ListenerImpl<>(ConnectionContainer::addUIInformationListener,
                ConnectionContainer::removeUIInformationListener, e -> {
                    updateWorkflowChangesTrackers(WorkflowChange.BENDPOINTS_MODIFIED);
                    callback();
                });
        }
    }

    /**
     * Adds a callback which is called as soon as the associated workflow changed.
     *
     * @param callback the callback to call if a change occurs in the workflow manager(s)
     */
    public void addWorkflowChangeCallback(final Runnable callback) {
        if (m_workflowChangedCallbacks.isEmpty()) {
            startListening();
        }
        m_workflowChangedCallbacks.add(callback);
    }

    /**
     * Removes a registered callback.
     *
     * @param callback
     */
    public void removeCallback(final Runnable callback) {
        m_workflowChangedCallbacks.remove(callback);
        if (m_workflowChangedCallbacks.isEmpty()) {
            stopListening();
        }
    }

    /**
     * @param tracker The tracker to remove.
     */
    public void removeWorkflowChangesTracker(final WorkflowChangesTracker tracker) {
        m_workflowChangesTrackers.remove(tracker);
        if (m_workflowChangesTrackers.isEmpty()) {
            stopListening();
        }
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
        if (m_workflowChangesTrackers.isEmpty()) {
            startListening();
        }
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

    private void startListening() {
        if (m_isListening) {
            // already listening
            return;
        }

        startListening(m_wfm);
        if (m_recurse) {
            for (var nc : m_wfm.getNodeContainers()) {
                CoreUtil.runOnNodeOrWfm(nc, null, this::startListening);
            }
        }

        m_isListening = true;
    }

    private void startListening(final WorkflowManager wfm) {
        wfm.addListener(m_workflowListener);
        wfm.getNodeContainers().forEach(this::addNodeListeners);
        m_workflowAnnotationListener.attachTo(wfm.getWorkflowAnnotations());
        var connectionContainers = wfm.getConnectionContainers();
        m_connectionProgressListener.attachTo(connectionContainers);
        m_connectionUIInformationListener.attachTo(connectionContainers);
    }

    @SuppressWarnings("java:S1541") // Complexity: Simple 1:1 matching
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
                updateWorkflowChangesTrackers(WorkflowChangesTracker.WorkflowChange.ANNOTATION_REMOVED);
                break;
            case NODE_PORTS_CHANGED:
                updateWorkflowChangesTrackers(WorkflowChangesTracker.WorkflowChange.NODE_PORTS_CHANGED);
                break;
            case PORTS_BAR_UI_INFO_CHANGED:
                updateWorkflowChangesTrackers(WorkflowChange.PORTS_BAR_MOVED);
                break;
            default:
                //
        }
    }

    private void addOrRemoveListenersFromNodeOrWorkflowAnnotation(final WorkflowEvent e) {
        switch (e.getType()) {
            case NODE_ADDED:
                var nc = (NodeContainer)e.getNewValue();
                addNodeListeners(nc);
                if (m_recurse) {
                    CoreUtil.runOnNodeOrWfm(nc, null, wfm -> wfm.addListener(m_workflowListener));
                }
                break;
            case NODE_REMOVED:
                nc = (NodeContainer)e.getOldValue();
                removeNodeListeners(nc);
                if (m_recurse) {
                    CoreUtil.runOnNodeOrWfm(nc, null, wfm -> wfm.removeListener(m_workflowListener));
                }
                break;
            case ANNOTATION_ADDED:
                m_workflowAnnotationListener.attachTo((WorkflowAnnotation)e.getNewValue());
                break;
            case ANNOTATION_REMOVED:
                m_workflowAnnotationListener.detachFrom((WorkflowAnnotation)e.getOldValue());
                break;
            case CONNECTION_ADDED:
                var cc = (ConnectionContainer)e.getNewValue();
                m_connectionUIInformationListener.attachTo(cc);
                m_connectionProgressListener.attachTo(cc);
                break;
            case CONNECTION_REMOVED:
                cc = (ConnectionContainer)e.getOldValue();
                m_connectionUIInformationListener.detachFrom(cc);
                m_connectionProgressListener.detachFrom(cc);
                break;
            default:
                //
        }
    }

    private void addNodeListeners(final NodeContainer nc) {
        m_nodeListeners.forEach(l -> l.attachTo(nc));
    }

    private void removeNodeListeners(final NodeContainer nc) {
        m_nodeListeners.forEach(l -> l.detachFrom(nc));
    }

    private static Optional<NativeNodeContainer> getNNC(final NodeContainer nc) {
        return (nc instanceof NativeNodeContainer nnc) ? Optional.of(nnc) : Optional.empty();
    }

    private void callback() {
        updateWorkflowChangesTrackers(WorkflowChangesTracker.WorkflowChange.ANY);
        m_callThrottle.invoke();
    }

    private void stopListening() {
        if (!m_isListening) {
            // already not listening
            return;
        }

        if (m_recurse) {
            for (var nc : m_wfm.getNodeContainers()) {
                CoreUtil.runOnNodeOrWfm(nc, null, this::stopListening);
            }
        }
        stopListening(m_wfm);

        m_isListening = false;
    }

    private void stopListening(final WorkflowManager wfm) {
        wfm.removeListener(m_workflowListener);
        wfm.getNodeContainers().forEach(this::removeNodeListeners);
        m_workflowAnnotationListener.detachFrom(wfm.getWorkflowAnnotations());
        var connectionContainers = wfm.getConnectionContainers();
        m_connectionProgressListener.detachFrom(connectionContainers);
        m_connectionUIInformationListener.detachFrom(connectionContainers);
    }

    @Override
    public void close() {
        stopListening();
        m_callThrottle.dispose();
        m_workflowChangedCallbacks.clear();
        m_workflowChangesTrackers.clear();
        m_postProcessCallbacks.clear();
    }

    /**
     * For testing purposes only!
     *
     * @return
     */
    public CallState getCallState() {
        return m_callThrottle.getCallState();
    }

    /**
     * Generalization of any listener that can be attached to aspects of a node/connection/annotation.
     *
     * @param <T> The target type to attach/detach the listener to/from
     * @param <L> The type of event listener to attach / detach
     */
    private interface Listener<T> {

        void attachTo(T target);

        void attachTo(final Collection<T> targets);

        void detachFrom(T target);

        void detachFrom(final Collection<T> targets);

    }


    /**
     * Generalization of any listener that can be attached to aspects of a node/connection/annotation.
     *
     * @param <L> The type of event listener to attach / detach
     * @param <T> The target type to attach/detach the listener to/from
     */
    private static final class ListenerImpl<T, L> implements Listener<T> {

        final BiConsumer<T, L> m_attacher;

        final BiConsumer<T, L> m_detacher;

        final L m_listener;

        private ListenerImpl(final BiConsumer<T, L> attacher,
            final BiConsumer<T, L> detacher, final L listener) {
            m_attacher = attacher;
            m_detacher = detacher;
            m_listener = listener;
        }

        @Override
        public void attachTo(final Collection<T> targets) {
            targets.forEach(this::attachTo);
        }

        @Override
        public void detachFrom(final Collection<T> targets) {
            targets.forEach(this::detachFrom);
        }

        @Override
        public void attachTo(final T target) {
            m_attacher.accept(target, m_listener);
        }

        @Override
        public void detachFrom(final T target) {
            m_detacher.accept(target, m_listener);
        }

    }

    private static final class NoopListener<T> implements Listener<T> {

        @Override
        public void attachTo(final Collection<T> targets) {
           //
        }

        @Override
        public void detachFrom(final Collection<T> targets) {
           //
        }

        @Override
        public void attachTo(final T target) {
            //
        }

        @Override
        public void detachFrom(final T target) {
            //
        }

    }

}

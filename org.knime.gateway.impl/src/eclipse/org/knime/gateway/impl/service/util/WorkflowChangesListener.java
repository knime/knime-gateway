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
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

import org.knime.core.node.workflow.NodeContainer;
import org.knime.core.node.workflow.NodeID;
import org.knime.core.node.workflow.NodeMessageListener;
import org.knime.core.node.workflow.NodeProgressListener;
import org.knime.core.node.workflow.NodeStateChangeListener;
import org.knime.core.node.workflow.NodeUIInformationListener;
import org.knime.core.node.workflow.WorkflowAnnotation;
import org.knime.core.node.workflow.WorkflowAnnotationID;
import org.knime.core.node.workflow.WorkflowEvent;
import org.knime.core.node.workflow.WorkflowEvent.Type;
import org.knime.core.node.workflow.WorkflowListener;
import org.knime.core.node.workflow.WorkflowManager;

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

    private final Consumer<WorkflowManager> m_callback;

    private final ExecutorService m_executorService;

    private final AtomicCallbackState m_callbackState = new AtomicCallbackState();

    /**
     * @param wfm the workflow manager to listen to
     * @param callback the callback to call if a change occurs in the workflow manager
     */
    public WorkflowChangesListener(final WorkflowManager wfm, final Consumer<WorkflowManager> callback) {
        m_wfm = wfm;
        m_executorService = Executors.newSingleThreadExecutor(r -> {
            Thread t = new Thread(r, "KNIME-Workflow-Changes-Listener (" + m_wfm.getName() + ")");
            t.setDaemon(true);
            return t;
        });
        m_callback = callback;
        m_workflowListener = startListening();
    }

    private WorkflowListener startListening() {
        WorkflowListener workflowListener = e -> {
            addOrRemoveListenersFromNodeOrWorkflowAnnotation(e);
            callback();
        };
        m_wfm.addListener(workflowListener);
        m_wfm.getNodeContainers().forEach(this::addNodeListeners);
        m_wfm.getWorkflowAnnotations().forEach(this::addWorkflowAnnotationListener);
        return workflowListener;
    }

    private void addOrRemoveListenersFromNodeOrWorkflowAnnotation(final WorkflowEvent e) {
        if (e.getType() == Type.NODE_ADDED) {
            addNodeListeners(m_wfm.getNodeContainer(e.getID()));
        } else if (e.getType() == Type.NODE_REMOVED) {
            removeNodeListeners((NodeContainer)e.getOldValue());
        } else if (e.getType() == Type.ANNOTATION_ADDED) {
            addWorkflowAnnotationListener((WorkflowAnnotation)e.getNewValue());
        } else if (e.getType() == Type.ANNOTATION_REMOVED) {
            removeWorkflowAnnotationListener((WorkflowAnnotation)e.getOldValue());
        } else {
            //
        }
    }

    private void addNodeListeners(final NodeContainer nc) {
        NodeStateChangeListener sl = e -> callback();
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
        if (!m_callbackState.checkIsCallbackInProgressAndChangeState()) {
            m_executorService.execute(() -> {
                do {
                    m_callback.accept(m_wfm);
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
    }

    @Override
    public void close() {
        stopListening();
        m_executorService.shutdown();
    }

    private static final class AtomicCallbackState {

        /*
         * The state the 'callback process' is in.
         */
        private enum CallbackState {
                /*
                 * No callback in progress nor is one awaiting
                 */
                IDLE,
                /*
                 * Callback in progress, none is awaiting
                 */
                IN_PROGRESS,
                /*
                 * Callback in progress, and another one awaiting
                 */
                IN_PROGRESS_AND_AWAITING;
        }

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
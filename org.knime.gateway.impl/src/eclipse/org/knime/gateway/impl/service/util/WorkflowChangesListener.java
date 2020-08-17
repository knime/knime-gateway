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
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

import org.knime.core.node.workflow.NodeContainer;
import org.knime.core.node.workflow.NodeID;
import org.knime.core.node.workflow.NodeMessageListener;
import org.knime.core.node.workflow.NodeProgressListener;
import org.knime.core.node.workflow.NodeStateChangeListener;
import org.knime.core.node.workflow.NodeUIInformationListener;
import org.knime.core.node.workflow.WorkflowAnnotation;
import org.knime.core.node.workflow.WorkflowAnnotationID;
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

    private final AtomicBoolean m_isCallbackWaiting = new AtomicBoolean(false);

    private final AtomicBoolean m_isCallbackInProgress = new AtomicBoolean(false);

    private final Consumer<WorkflowManager> m_callback;

    private final ExecutorService m_executorService;

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
            //TODO filter out workflow dirty event?
            if (e.getType() == Type.NODE_ADDED) {
                registerNode(m_wfm.getNodeContainer(e.getID()));
            } else if (e.getType() == Type.ANNOTATION_ADDED) {
                registerWorkflowAnnotation((WorkflowAnnotation)e.getNewValue());
            } else {
                //
            }
            callback();
        };
        m_wfm.addListener(workflowListener);
        m_wfm.getNodeContainers().forEach(this::registerNode);
        m_wfm.getWorkflowAnnotations().forEach(this::registerWorkflowAnnotation);
        return workflowListener;
    }

    private void registerNode(final NodeContainer nc) {
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

    private void registerWorkflowAnnotation(final WorkflowAnnotation wa) {
        NodeUIInformationListener l = e -> callback();
        m_workflowAnnotationListeners.put(wa.getID(), l);
        wa.addUIInformationListener(l);
    }

    private void deregisterNode(final NodeContainer nc) {
        nc.removeNodeStateChangeListener(m_nodeStateChangeListeners.get(nc.getID()));
        nc.getProgressMonitor().removeProgressListener(m_progressListeners.get(nc.getID()));
        nc.removeUIInformationListener(m_nodeUIListeners.get(nc.getID()));
        nc.getNodeAnnotation().removeUIInformationListener(m_nodeUIListeners.get(nc.getID()));
        nc.removeNodeMessageListener(m_nodeMessageListeners.get(nc.getID()));
    }

    private synchronized void callback() {
        if (m_isCallbackInProgress.get()) {
            m_isCallbackWaiting.set(true);
        } else {
            m_isCallbackInProgress.set(true);
            m_executorService.execute(() -> {
                try {
                    do {
                        m_isCallbackWaiting.set(false);
                        m_callback.accept(m_wfm);
                    } while (m_isCallbackWaiting.get());
                } finally {
                    m_isCallbackInProgress.set(false);
                }
            });
        }
    }

    private void stopListening() {
        m_wfm.removeListener(m_workflowListener);
        m_wfm.getNodeContainers().forEach(this::deregisterNode);
        m_wfm.getWorkflowAnnotations()
            .forEach(wa -> wa.removeUIInformationListener(m_workflowAnnotationListeners.get(wa.getID())));
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

}
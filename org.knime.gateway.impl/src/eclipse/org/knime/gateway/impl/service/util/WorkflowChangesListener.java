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
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

import org.knime.core.node.workflow.NodeContainer;
import org.knime.core.node.workflow.NodeID;
import org.knime.core.node.workflow.NodeProgressListener;
import org.knime.core.node.workflow.NodeStateChangeListener;
import org.knime.core.node.workflow.NodeUIInformationListener;
import org.knime.core.node.workflow.WorkflowEvent.Type;
import org.knime.core.node.workflow.WorkflowListener;
import org.knime.core.node.workflow.WorkflowManager;

/**
 * A summarizes all kind of workflow changes and allows one to register one single listener to all of them.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz
 */
public class WorkflowChangesListener implements Closeable {

    private WorkflowManager m_wfm;

    private WorkflowListener m_workflowListener;

    private Map<NodeID, NodeStateChangeListener> m_nodeStateChangeListeners = new HashMap<>();

    private Map<NodeID, NodeProgressListener> m_progressListeners = new HashMap<>();

    private Map<NodeID, NodeUIInformationListener> m_nodeUIListeners = new HashMap<>();

    private AtomicBoolean m_isCallbackWaiting = new AtomicBoolean(false);

    private AtomicBoolean m_isCallbackInProgress = new AtomicBoolean(false);

    private Consumer<WorkflowManager> m_callback;

    /**
     * @param wfm the workflow manager to listen to
     * @param callback the callback to call if a change occurs in the workflow manager
     */
    public WorkflowChangesListener(final WorkflowManager wfm, final Consumer<WorkflowManager> callback) {
        m_wfm = wfm;
        m_callback = callback;
        startListening();
    }

    private void startListening() {
        m_workflowListener = e -> {
            //TODO filter out workflow dirty event?
            if (e.getType() == Type.NODE_ADDED) {
                registerNode(m_wfm.getNodeContainer(e.getID()));
            }
            //TODO unregister node?
            callback();
        };
        m_wfm.addListener(m_workflowListener);
        m_wfm.getNodeContainers().forEach(nc -> {
            registerNode(nc);
        });
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
        //TODO node message listener?
    }

    private void deregisterNode(final NodeContainer nc) {
        nc.removeNodeStateChangeListener(m_nodeStateChangeListeners.get(nc.getID()));
        nc.getProgressMonitor().removeProgressListener(m_progressListeners.get(nc.getID()));
        nc.removeUIInformationListener(m_nodeUIListeners.get(nc.getID()));
    }

    private synchronized void callback() {
        if (m_isCallbackInProgress.get()) {
            m_isCallbackWaiting.set(true);
        } else {
            m_isCallbackInProgress.set(true);
            //TODO
            new Thread(() -> {
                try {
                    do {
                        m_isCallbackWaiting.set(false);
                        m_callback.accept(m_wfm);
                    } while (m_isCallbackWaiting.get());
                } finally {
                    m_isCallbackInProgress.set(false);
                }
            }).start();
        }
    }

    private void stopListening() {
        m_wfm.removeListener(m_workflowListener);
        m_workflowListener = null;
        m_wfm.getNodeContainers().forEach(this::deregisterNode);
    }

    @Override
    public void close() {
        stopListening();
        m_progressListeners = null;
        m_nodeStateChangeListeners = null;
    }

}
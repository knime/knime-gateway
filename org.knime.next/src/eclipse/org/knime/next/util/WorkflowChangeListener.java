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
 * History
 *   May 7, 2020 (hornm): created
 */
package org.knime.next.util;

import java.io.Closeable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BiConsumer;

import org.knime.core.node.workflow.NodeContainer;
import org.knime.core.node.workflow.NodeID;
import org.knime.core.node.workflow.NodeProgressListener;
import org.knime.core.node.workflow.NodeStateChangeListener;
import org.knime.core.node.workflow.NodeUIInformationListener;
import org.knime.core.node.workflow.WorkflowEvent.Type;
import org.knime.core.node.workflow.WorkflowListener;
import org.knime.core.node.workflow.WorkflowManager;

import com.knime.gateway.entity.NodeIDEnt;
import com.knime.gateway.entity.PatchEnt;
import com.knime.gateway.remote.service.DefaultWorkflowService;
import com.knime.gateway.remote.service.util.DefaultServiceUtil;
import com.knime.gateway.service.util.ServiceExceptions.NodeNotFoundException;
import com.knime.gateway.service.util.ServiceExceptions.NotASubWorkflowException;
import com.knime.gateway.service.util.ServiceExceptions.NotFoundException;

/**
 *
 * @author Martin Horn, KNIME GmbH, Konstanz
 */
public class WorkflowChangeListener implements Closeable {

    private WorkflowManager m_wfm;

    private WorkflowListener m_workflowListener;

    private Map<NodeID, NodeStateChangeListener> m_nodeStateChangeListeners = new HashMap<>();

    private Map<NodeID, NodeProgressListener> m_progressListeners = new HashMap<>();

    private Map<NodeID, NodeUIInformationListener> m_nodeUIListeners = new HashMap<>();

    private UUID m_rootWorkflowID;

    private NodeIDEnt m_workflowID;

    private AtomicBoolean m_isCallbackWaiting = new AtomicBoolean(false);

    private AtomicBoolean m_isCallbackInProgress = new AtomicBoolean(false);

    // consumer consumes patch and the 'old' snapshot id
    private Map<UUID, List<BiConsumer<PatchEnt, UUID>>> m_callbacks = new HashMap<>();

    /**
     *
     */
    public WorkflowChangeListener(final UUID rootWorkflowID, final NodeIDEnt workflowID)
        throws NotASubWorkflowException, NodeNotFoundException {
        m_rootWorkflowID = rootWorkflowID;
        m_workflowID = workflowID;
        m_wfm = DefaultServiceUtil.getWorkflowManager(rootWorkflowID, workflowID);
    }

    public void registerCallback(final UUID initSnapshotID, final BiConsumer<PatchEnt, UUID> callback) {
        registerCallback(initSnapshotID, callback, false);
    }

    public void registerCallback(final UUID initSnapshotID, final BiConsumer<PatchEnt, UUID> callback,
        final boolean replaceAll) {
        if (replaceAll) {
            List<BiConsumer<PatchEnt, UUID>> list = m_callbacks.get(initSnapshotID);
            if (list != null && !list.isEmpty()) {
                list.clear();
            }
        }
        m_callbacks.computeIfAbsent(initSnapshotID, k -> new ArrayList<>()).add(callback);
        if (m_workflowListener == null) {
            startListening();
        }
    }

    public void deregisterCallbacks(final UUID snapshotID) {
        m_callbacks.remove(snapshotID);
        if (m_callbacks.isEmpty()) {
            stopListening();
        }
    }

    public void deregisterCallback(final BiConsumer<PatchEnt, UUID> callbackToRemove) {
        for (Entry<UUID, List<BiConsumer<PatchEnt, UUID>>> entry : m_callbacks.entrySet()) {
            List<BiConsumer<PatchEnt, UUID>> list = entry.getValue();
            boolean removed = false;
            for (BiConsumer<PatchEnt, UUID> callback : list) {
                if (callback == callbackToRemove) {
                    list.remove(callbackToRemove);
                    removed = true;
                    if (list.isEmpty()) {
                        m_callbacks.remove(entry.getKey());
                    }
                    break;
                }
            }
            if (m_callbacks.isEmpty()) {
                stopListening();
            }
            if (removed) {
                return;
            }
        }
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

    public synchronized void callback() {
        if (m_isCallbackInProgress.get()) {
            m_isCallbackWaiting.set(true);
        } else {
            m_isCallbackInProgress.set(true);
            //TODO
            new Thread(() -> {
                try {
                    do {
                        m_isCallbackWaiting.set(false);
                        doCallback();
                    } while (m_isCallbackWaiting.get());
                } finally {
                    m_isCallbackInProgress.set(false);
                }
            }).start();
        }
    }

    private void doCallback() {
        Map<UUID, List<BiConsumer<PatchEnt, UUID>>> callbacksWithNewSnapshotIDs = new HashMap<>();
        m_callbacks.keySet().forEach(k -> {
            //TODO extract logic and use wfm directly
            PatchEnt patch = createPatch(k);
            if (patch.getOps().isEmpty()) {
                callbacksWithNewSnapshotIDs.put(k, m_callbacks.get(k));
            } else {
                List<BiConsumer<PatchEnt, UUID>> callbacks = m_callbacks.get(k);
                callbacks.forEach(c -> c.accept(patch, k));
                // update snapshot id for callbacks
                callbacksWithNewSnapshotIDs.put(patch.getSnapshotID(), callbacks);
            }
        });
        m_callbacks = callbacksWithNewSnapshotIDs;
    }

    private PatchEnt createPatch(final UUID snapshotID) {
        try {
            return DefaultWorkflowService.getInstance().getWorkflowDiff(m_rootWorkflowID, m_workflowID, snapshotID);
        } catch (NotASubWorkflowException | NotFoundException e) {
            //TODO
            throw new RuntimeException(e);
        }
    }

    void stopListening() {
        m_wfm.removeListener(m_workflowListener);
        m_workflowListener = null;
        m_wfm.getNodeContainers().forEach(nc -> {
            deregisterNode(nc);
        });
    }

    @Override
    public void close() {
        stopListening();
        m_progressListeners = null;
        m_nodeStateChangeListeners = null;
    }

}

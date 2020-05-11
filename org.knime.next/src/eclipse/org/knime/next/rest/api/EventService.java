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
 *   Apr 29, 2020 (hornm): created
 */
package org.knime.next.rest.api;

import java.io.Closeable;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.sse.OutboundSseEvent;
import javax.ws.rs.sse.Sse;
import javax.ws.rs.sse.SseBroadcaster;
import javax.ws.rs.sse.SseEventSink;

import org.knime.core.util.Pair;
import org.knime.next.util.WorkflowChangeListener;

import com.knime.gateway.entity.NodeIDEnt;
import com.knime.gateway.entity.PatchEnt;
import com.knime.gateway.service.util.ServiceExceptions.NodeNotFoundException;
import com.knime.gateway.service.util.ServiceExceptions.NotASubWorkflowException;

/**
 * TODO make singleton?
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
@Path("/workflows/{job-id}")
public class EventService {

    private Map<Pair<UUID, NodeIDEnt>, UpdateListener> m_updateListeners = new HashMap<>();

    @GET
    @Path("/events/{workflow-id}")
    @Produces(MediaType.SERVER_SENT_EVENTS)
    public synchronized void getServerSentEvents(@PathParam("job-id") final UUID rootWorkflowID,
        @PathParam("workflow-id") final NodeIDEnt workflowId, @QueryParam("snapshot-id") final UUID snapshotId,
        @Context final SseEventSink eventSink, @Context final Sse sse) throws Exception {
        AtomicReference<Exception> exception = new AtomicReference<>();
        UpdateListener updateListener =
            m_updateListeners.computeIfAbsent(Pair.create(rootWorkflowID, workflowId), k -> {
                try {
                    return new UpdateListener(rootWorkflowID, workflowId, sse);
                } catch (NotASubWorkflowException | NodeNotFoundException e) {
                    exception.set(e);
                    return null;
                }
            });
        if (exception.get() instanceof NotASubWorkflowException) {
            throw (NotASubWorkflowException)exception.get();
        }
        if (exception.get() instanceof NodeNotFoundException) {
            throw (NodeNotFoundException)exception.get();
        }
        updateListener.registerSseEventSinkAndTriggerEvent(eventSink, snapshotId);
    }

    private class UpdateListener implements Closeable {

        Map<UUID, SseBroadcaster> m_broadcasters = new HashMap<>();

        Map<UUID, Integer> m_sinkCount = new HashMap<>();

        private Sse m_sse;

        private WorkflowChangeListener m_workflowListener;

        UpdateListener(final UUID rootWorkflowID, final NodeIDEnt workflowID, final Sse sse)
            throws NotASubWorkflowException, NodeNotFoundException {
            m_workflowListener = new WorkflowChangeListener(rootWorkflowID, workflowID);
            m_sse = sse;
        }

        void registerSseEventSinkAndTriggerEvent(final SseEventSink sseEventSink, final UUID snapshotID) {
            getBroadcaster(snapshotID).register(sseEventSink);
            m_sinkCount.replace(snapshotID, m_sinkCount.computeIfAbsent(snapshotID, k -> 0) + 1);
            m_workflowListener.registerCallback(snapshotID, this::broadcast, true);
            // trigger first event storm right away
            m_workflowListener.callback();
        }

        private SseBroadcaster getBroadcaster(final UUID snapshotID) {
            return m_broadcasters.computeIfAbsent(snapshotID, k -> {
                m_sinkCount.put(snapshotID, 0);
                SseBroadcaster b = m_sse.newBroadcaster();
                b.onClose(e -> {
                    int count = m_sinkCount.get(snapshotID) - 1;
                    if (count == 0) {
                        m_workflowListener.deregisterCallbacks(snapshotID);
                    }
                    m_sinkCount.replace(snapshotID, count);
                });
                b.onError((e, t) -> {
                    //TODO
                });
                return b;
            });
        }

        private void broadcast(final PatchEnt patch, final UUID oldSnapshotID) {
            OutboundSseEvent sseEvent = m_sse.newEventBuilder().id(patch.getSnapshotID().toString())
                .mediaType(MediaType.APPLICATION_JSON_TYPE).data(patch).reconnectDelay(3000).build();
            SseBroadcaster broadcaster = m_broadcasters.get(oldSnapshotID);
            broadcaster.broadcast(sseEvent);
            m_broadcasters.remove(oldSnapshotID);
            m_broadcasters.put(patch.getSnapshotID(), broadcaster);
            Integer sinkCount = m_sinkCount.remove(oldSnapshotID);
            m_sinkCount.put(patch.getSnapshotID(), sinkCount);
        }

        //TODO never be called so far
        @Override
        public void close() {
            m_broadcasters.values().forEach(SseBroadcaster::close);
            m_broadcasters = null;
            m_broadcasters.keySet().forEach(k -> m_workflowListener.deregisterCallbacks(k));
        }

    }

}

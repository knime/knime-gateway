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
 *   May 30, 2020 (hornm): created
 */
package org.knime.next.jsonrpc;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

import org.knime.next.util.WorkflowChangeListener;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.googlecode.jsonrpc4j.JsonRpcMethod;
import com.googlecode.jsonrpc4j.JsonRpcParam;
import com.googlecode.jsonrpc4j.JsonRpcService;
import com.knime.gateway.entity.NodeIDEnt;
import com.knime.gateway.json.util.ObjectMapperUtil;
import com.knime.gateway.service.util.ServiceExceptions.NodeNotFoundException;
import com.knime.gateway.service.util.ServiceExceptions.NotASubWorkflowException;

/**
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
@JsonRpcService(value = "EventService")
public class EventService {

    public static Consumer<String> MESSAGE_CONSUMER;

    private final Map<UUID, WorkflowChangeListener> m_workflowChangeListeners = new HashMap<>();

    @JsonRpcMethod(value = "register")
    public void register(@JsonRpcParam(value = "rootWorkflowID") final UUID rootWorkflowID,
        @JsonRpcParam(value = "workflowID") final NodeIDEnt workflowID,
        @JsonRpcParam(value = "snapshotID") final UUID snapshotID) {
        AtomicReference<Exception> exception = new AtomicReference<>();
        m_workflowChangeListeners.computeIfAbsent(rootWorkflowID, wfID -> {
            try {
                return new WorkflowChangeListener(rootWorkflowID, workflowID);
            } catch (NotASubWorkflowException | NodeNotFoundException e) {
                exception.set(e);
                return null;
            }
        }).registerCallback(snapshotID, (patch, oldSnapshotID) -> {
            try {
                sendMessageAsJson(patch);
            } catch (JsonProcessingException e) {
                exception.set(e);
            }
        }, true);
        if (exception.get() != null) {
            sendErrorMessage(exception.get());
        }
    }

    private void sendMessageAsJson(final Object obj) throws JsonProcessingException {
        sendMessage(ObjectMapperUtil.getInstance().getObjectMapper().writeValueAsString(obj));
    }

    private void sendMessage(final String message) {
        if(MESSAGE_CONSUMER != null) {
            MESSAGE_CONSUMER.accept(message);
        }
    }

    private void sendErrorMessage(final Exception e) {
        sendMessage("error: " + e.getStackTrace());
    }


}

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
 *   Aug 12, 2020 (hornm): created
 */
package org.knime.gateway.impl.webui.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

import org.knime.core.node.NodeLogger;
import org.knime.gateway.api.webui.entity.EventEnt;
import org.knime.gateway.api.webui.entity.EventTypeEnt;
import org.knime.gateway.api.webui.entity.WorkflowChangedEventTypeEnt;
import org.knime.gateway.api.webui.service.EventService;
import org.knime.gateway.api.webui.service.util.ServiceExceptions.InvalidRequestException;
import org.knime.gateway.impl.webui.service.events.EventSource;
import org.knime.gateway.impl.webui.service.events.WorkflowChangedEventSource;

/**
 * Default implementation of the {@link EventService}-interface.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
public final class DefaultEventService implements EventService {

    private static final DefaultEventService INSTANCE = new DefaultEventService();

    /**
     * Returns the singleton instance for this service.
     *
     * @return the singleton instance
     */
    public static DefaultEventService getInstance() {
        return INSTANCE;
    }

    private final List<BiConsumer<String, EventEnt>> m_eventConsumer = new ArrayList<>();

    private final Map<Class<? extends EventTypeEnt>, EventSource<? extends EventTypeEnt>> m_eventSources =
        new HashMap<>();

    /*
     * For testing purposes only.
     */
    private boolean m_callEventConsumerOnError = false;

    private DefaultEventService() {
        // singleton
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings({"unchecked"})
    @Override
    public void addEventListener(final EventTypeEnt eventTypeEnt) throws InvalidRequestException {
        @SuppressWarnings("rawtypes")
        EventSource eventSource;
        if (eventTypeEnt instanceof WorkflowChangedEventTypeEnt) {
            eventSource = m_eventSources.computeIfAbsent(eventTypeEnt.getClass(),
                t -> new WorkflowChangedEventSource(this::sendEvent));
        } else {
            throw new InvalidRequestException("Event type not supported: " + eventTypeEnt.getClass().getSimpleName());
        }
        eventSource.addEventListener(eventTypeEnt);
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @Override
    public void removeEventListener(final EventTypeEnt eventTypeEnt) {
        @SuppressWarnings({"rawtypes"})
        EventSource eventSource = m_eventSources.get(eventTypeEnt.getClass()); // NOSONAR
        eventSource.removeEventListener(eventTypeEnt);
    }

    /**
     * Unregisters and removes all event listeners. After this method has been called, no events will arrive anymore at
     * the registered event consumers.
     */
    public void removeAllEventListeners() {
        m_eventSources.values().forEach(EventSource::removeAllEventListeners);
    }

    /**
     * Adds a new event consumer. The consumer takes the event name and the readily created event, i.e.
     * {@link EventEnt}s, and delivers it to the client/ui.
     *
     * @param eventConsumer the event consumer to add
     */
    public void addEventConsumer(final BiConsumer<String, EventEnt> eventConsumer) {
        m_eventConsumer.add(eventConsumer);
    }

    void setEventConsumerForTesting(final BiConsumer<String, EventEnt> eventConsumer,
        final Runnable preEventCreationCallback) {
        m_eventConsumer.clear();
        m_eventSources.values().forEach(s -> s.setPreEventCreationCallback(preEventCreationCallback));
        addEventConsumer(eventConsumer);
        m_callEventConsumerOnError = true;
    }

    /**
     * Removes a previously registered event consumer.
     *
     * @param eventConsumer the consumer to remove
     */
    public void removeEventConsumer(final BiConsumer<String, EventEnt> eventConsumer) {
        m_eventConsumer.remove(eventConsumer); // NOSONAR
    }

    private synchronized void sendEvent(final String name, final EventEnt event) {
        if (m_eventConsumer.isEmpty()) {
            var message = "Events available but no one is interested. Most likely an implementation error.";
            NodeLogger.getLogger(getClass()).error(message);
            if (m_callEventConsumerOnError) {
                m_eventConsumer.forEach(c -> c.accept(message, null));
            }
            throw new IllegalStateException(message);
        }
        m_eventConsumer.forEach(c -> c.accept(name, event));
    }

    /*
     * For testing purposes only!
     */
    EventSource<? extends EventTypeEnt> getEventSource(final Class<? extends EventTypeEnt> eventTypeEnt) {
        return m_eventSources.get(eventTypeEnt);
    }

}
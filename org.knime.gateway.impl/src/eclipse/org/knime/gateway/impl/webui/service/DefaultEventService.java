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

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.knime.gateway.api.webui.entity.AppStateChangedEventTypeEnt;
import org.knime.gateway.api.webui.entity.EventTypeEnt;
import org.knime.gateway.api.webui.entity.SelectionEventTypeEnt;
import org.knime.gateway.api.webui.entity.WorkflowChangedEventTypeEnt;
import org.knime.gateway.api.webui.service.EventService;
import org.knime.gateway.api.webui.service.util.ServiceExceptions.InvalidRequestException;
import org.knime.gateway.impl.project.WorkflowProjectManager;
import org.knime.gateway.impl.service.events.EventSource;
import org.knime.gateway.impl.service.util.EventConsumer;
import org.knime.gateway.impl.webui.AppStateProvider;
import org.knime.gateway.impl.webui.WorkflowMiddleware;
import org.knime.gateway.impl.webui.service.events.AppStateChangedEventSource;
import org.knime.gateway.impl.webui.service.events.SelectionEventSourceDelegator;
import org.knime.gateway.impl.webui.service.events.WorkflowChangedEventSource;

/**
 * Default implementation of the {@link EventService}-interface.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
public final class DefaultEventService implements EventService {

    private final EventConsumer m_eventConsumer = ServiceDependencies.getServiceDependency(EventConsumer.class, true);

    private final Map<Class<? extends EventTypeEnt>, EventSource<? extends EventTypeEnt, ?>> m_eventSources =
        Collections.synchronizedMap(new HashMap<>());

    private final AppStateProvider m_appStateProvider =
        ServiceDependencies.getServiceDependency(AppStateProvider.class, true);

    private final WorkflowMiddleware m_workflowMiddleware =
        ServiceDependencies.getServiceDependency(WorkflowMiddleware.class, true);

    private final WorkflowProjectManager m_workflowProjectManager =
        ServiceDependencies.getServiceDependency(WorkflowProjectManager.class, true);

    /**
     * Returns the singleton instance for this service.
     *
     * @return the singleton instance
     */
    public static DefaultEventService getInstance() {
        return ServiceInstances.getDefaultServiceInstance(DefaultEventService.class);
    }

    DefaultEventService() {
        // singleton
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addEventListener(final EventTypeEnt eventTypeEnt) throws InvalidRequestException {
        @SuppressWarnings("rawtypes")
        EventSource eventSource;
        if (eventTypeEnt instanceof WorkflowChangedEventTypeEnt) {
            eventSource = m_eventSources.computeIfAbsent(eventTypeEnt.getClass(),
                t -> new WorkflowChangedEventSource(m_eventConsumer, m_workflowMiddleware));
        } else if (eventTypeEnt instanceof AppStateChangedEventTypeEnt) {
            eventSource = m_eventSources.computeIfAbsent(eventTypeEnt.getClass(),
                t -> new AppStateChangedEventSource(m_eventConsumer, m_appStateProvider, m_workflowProjectManager,
                    m_workflowMiddleware));
        } else if (eventTypeEnt instanceof SelectionEventTypeEnt) {
            eventSource = m_eventSources.computeIfAbsent(eventTypeEnt.getClass(),
                t -> new SelectionEventSourceDelegator(m_eventConsumer));
        } else {
            throw new InvalidRequestException("Event type not supported: " + eventTypeEnt.getClass().getSimpleName());
        }
        try {
            eventSource.addEventListenerFor(eventTypeEnt);
        } catch (IllegalArgumentException e) {
            throw new InvalidRequestException(e.getMessage(), e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @Override
    public void removeEventListener(final EventTypeEnt eventTypeEnt) {
        @SuppressWarnings({"rawtypes"})
        EventSource eventSource = m_eventSources.get(eventTypeEnt.getClass()); // NOSONAR
        if (eventSource != null) {
            eventSource.removeEventListener(eventTypeEnt);
        }
    }

    /**
     * Unregisters and removes all event listeners. After this method has been called, no events will arrive anymore at
     * the registered event consumers.
     */
    public void removeAllEventListeners() {
        m_eventSources.values().forEach(EventSource::removeAllEventListeners);
    }

    /**
     * For testing purposes only!
     */
    void setPreEventCreationCallbackForTesting(final Runnable preEventCreationCallback) {
        m_eventSources.values().forEach(s -> s.setPreEventCreationCallback(preEventCreationCallback));
    }

    /*
     * For testing purposes only!
     */
    EventSource<?, ?> getEventSource(final Class<? extends EventTypeEnt> eventTypeEnt) {
        return m_eventSources.get(eventTypeEnt);
    }

    @Override
    public void dispose() {
        removeAllEventListeners();
    }

}
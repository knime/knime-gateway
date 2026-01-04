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

import org.knime.core.node.NodeLogger;
import org.knime.gateway.api.webui.entity.AppStateChangedEventTypeEnt;
import org.knime.gateway.api.webui.entity.EventTypeEnt;
import org.knime.gateway.api.webui.entity.NodeRepositoryLoadingProgressEventTypeEnt;
import org.knime.gateway.api.webui.entity.ProjectDisposedEventTypeEnt;
import org.knime.gateway.api.webui.entity.SpaceItemChangedEventTypeEnt;
import org.knime.gateway.api.webui.entity.UpdateAvailableEventTypeEnt;
import org.knime.gateway.api.webui.entity.WorkflowChangedEventTypeEnt;
import org.knime.gateway.api.webui.entity.WorkflowMonitorStateChangeEventTypeEnt;
import org.knime.gateway.api.webui.service.EventService;
import org.knime.gateway.api.webui.service.util.ServiceExceptions.InvalidRequestException;
import org.knime.gateway.impl.project.ProjectManager;
import org.knime.gateway.impl.webui.AppStateUpdater;
import org.knime.gateway.impl.webui.NodeFactoryProvider;
import org.knime.gateway.impl.webui.PreferencesProvider;
import org.knime.gateway.impl.webui.UpdateStateProvider;
import org.knime.gateway.impl.webui.WorkflowMiddleware;
import org.knime.gateway.impl.webui.entity.AppStateEntityFactory;
import org.knime.gateway.impl.webui.kai.KaiHandler;
import org.knime.gateway.impl.webui.repo.NodeCollections;
import org.knime.gateway.impl.webui.service.events.AppStateChangedEventSource;
import org.knime.gateway.impl.webui.service.events.EventConsumer;
import org.knime.gateway.impl.webui.service.events.EventSource;
import org.knime.gateway.impl.webui.service.events.NodeRepositoryLoadingProgressEventSource;
import org.knime.gateway.impl.webui.service.events.ProjectDisposedEventSource;
import org.knime.gateway.impl.webui.service.events.SpaceItemChangedEventSource;
import org.knime.gateway.impl.webui.service.events.UpdateAvailableEventSource;
import org.knime.gateway.impl.webui.service.events.WorkflowChangedEventSource;
import org.knime.gateway.impl.webui.service.events.WorkflowMonitorStateChangedEventSource;
import org.knime.gateway.impl.webui.spaces.SpaceProvidersManager;
import org.knime.gateway.impl.webui.syncing.WorkflowSyncer;

/**
 * Default implementation of the {@link EventService}-interface.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 * @author Kai Franze, KNIME GmbH
 */
public final class DefaultEventService implements EventService {

    private final EventConsumer m_eventConsumer = ServiceDependencies.getServiceDependency(EventConsumer.class, true);

    private final Map<Class<? extends EventTypeEnt>, EventSource<? extends EventTypeEnt, ?>> m_eventSources =
        Collections.synchronizedMap(new HashMap<>());

    private final AppStateUpdater m_appStateUpdater =
        ServiceDependencies.getServiceDependency(AppStateUpdater.class, false);

    private final WorkflowMiddleware m_workflowMiddleware =
        ServiceDependencies.getServiceDependency(WorkflowMiddleware.class, true);

    private final ProjectManager m_projectManager =
        ServiceDependencies.getServiceDependency(ProjectManager.class, true);

    private final UpdateStateProvider m_updateStateProvider =
        ServiceDependencies.getServiceDependency(UpdateStateProvider.class, false);

    private final PreferencesProvider m_preferencesProvider =
        ServiceDependencies.getServiceDependency(PreferencesProvider.class, true);

    private final SpaceProvidersManager m_spaceProvidersManager =
        ServiceDependencies.getServiceDependency(SpaceProvidersManager.class, true);

    private final NodeFactoryProvider m_nodeFactoryProvider =
        ServiceDependencies.getServiceDependency(NodeFactoryProvider.class, false);

    private final NodeCollections m_nodeCollections =
        ServiceDependencies.getServiceDependency(NodeCollections.class, false);

    private final KaiHandler m_kaiHandler = ServiceDependencies.getServiceDependency(KaiHandler.class, false);

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

    @Override
    @SuppressWarnings({ "unchecked", "java:S5612" })
    public void addEventListener(final EventTypeEnt eventTypeEnt) throws InvalidRequestException {
        @SuppressWarnings("rawtypes")
        EventSource eventSource;

        // Set the event source depending on the event type
        if (eventTypeEnt instanceof WorkflowChangedEventTypeEnt) {
            eventSource = m_eventSources.computeIfAbsent(eventTypeEnt.getClass(),
                t -> new WorkflowChangedEventSource(m_eventConsumer, m_workflowMiddleware, m_projectManager));
        } else if (eventTypeEnt instanceof AppStateChangedEventTypeEnt) {
            if (m_appStateUpdater != null) {
                eventSource = m_eventSources.computeIfAbsent(eventTypeEnt.getClass(), t -> {
                    var projectId = DefaultServiceContext.getProjectId();
                    var key = projectId.map(SpaceProvidersManager.Key::of) //
                        .orElse(SpaceProvidersManager.Key.defaultKey());
                    var spaceProviders = m_spaceProvidersManager.getSpaceProviders(key);
                    var workflowSyncer = m_projectManager.getProject(projectId.orElseThrow()).orElseThrow() //
                            .getWorkflowManagerIfLoaded().orElseThrow() //
                            .getWorkflowResourceCache().getFromCache(WorkflowSyncer.WorkflowSyncerResource.class) //
                            .map(res -> res.get()) //
                            .orElse(null);
                    var dependencies = new AppStateEntityFactory.ServiceDependencies( //
                        m_projectManager, //
                        m_preferencesProvider, //
                        spaceProviders, //
                        m_nodeFactoryProvider, //
                        m_nodeCollections, //
                        m_kaiHandler, //
                        workflowSyncer //
                    );
                    return new AppStateChangedEventSource(m_eventConsumer, m_appStateUpdater, dependencies);
                });
            } else {
                NodeLogger.getLogger(getClass()).warn(
                    "Listener for 'app state changed' event type can't be attached. No app state updater available.");
                return;
            }
        } else if (eventTypeEnt instanceof UpdateAvailableEventTypeEnt) {
            if (m_updateStateProvider == null) {
                return;
            }
            eventSource = m_eventSources.computeIfAbsent(eventTypeEnt.getClass(),
                t -> new UpdateAvailableEventSource(m_eventConsumer, m_updateStateProvider));
        } else if (eventTypeEnt instanceof NodeRepositoryLoadingProgressEventTypeEnt) {
            eventSource = m_eventSources.computeIfAbsent(eventTypeEnt.getClass(),
                t -> new NodeRepositoryLoadingProgressEventSource(m_eventConsumer));
        } else if (eventTypeEnt instanceof ProjectDisposedEventTypeEnt) {
            eventSource = m_eventSources.computeIfAbsent(eventTypeEnt.getClass(),
                t -> new ProjectDisposedEventSource(m_eventConsumer, m_projectManager));
        } else if (eventTypeEnt instanceof WorkflowMonitorStateChangeEventTypeEnt) {
            eventSource = m_eventSources.computeIfAbsent(eventTypeEnt.getClass(),
                t -> new WorkflowMonitorStateChangedEventSource(m_eventConsumer, m_projectManager,
                    m_workflowMiddleware));
        } else if (eventTypeEnt instanceof SpaceItemChangedEventTypeEnt) {
            eventSource = m_eventSources.computeIfAbsent(eventTypeEnt.getClass(),
                t -> new SpaceItemChangedEventSource(m_eventConsumer, m_spaceProvidersManager));
        } else {
            throw InvalidRequestException.builder() //
                .withTitle("Event type not supported") //
                .withDetails("Unexpected event type: " + eventTypeEnt.getClass().getSimpleName()) //
                .canCopy(true) //
                .build();
        }

        // After setting the event source, try to add an event listener for the event type
        try {
            eventSource.addEventListenerFor(eventTypeEnt, projectId());
        } catch (IllegalArgumentException e) {
            throw InvalidRequestException.builder() //
                .withTitle("Failed to add event listener") //
                .withDetails(e.getMessage()) //
                .canCopy(true) //
                .withCause(e) //
                .build();
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public void removeEventListener(final EventTypeEnt eventTypeEnt) {
        @SuppressWarnings({"rawtypes"})
        EventSource eventSource = m_eventSources.get(eventTypeEnt.getClass()); // NOSONAR
        if (eventSource != null) {
            eventSource.removeEventListener(eventTypeEnt, projectId());
        }
    }

    private static String projectId() {
        return DefaultServiceContext.getProjectId().orElse(null);
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
    @SuppressWarnings("java:S1452")
    EventSource<?, ?> getEventSource(final Class<? extends EventTypeEnt> eventTypeEnt) {
        return m_eventSources.get(eventTypeEnt);
    }

    @Override
    public void dispose() {
        removeAllEventListeners();
    }

}

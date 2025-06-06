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
package org.knime.gateway.impl.webui.service.events;

import static java.util.Arrays.asList;
import static org.knime.gateway.api.entity.EntityBuilderManager.builder;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

import org.knime.core.node.extension.NodeSpecCollectionProvider;
import org.knime.core.node.extension.NodeSpecCollectionProvider.Progress.ProgressEvent;
import org.knime.gateway.api.entity.EntityBuilderManager;
import org.knime.gateway.api.webui.entity.AppStateChangedEventEnt;
import org.knime.gateway.api.webui.entity.AppStateChangedEventEnt.AppStateChangedEventEntBuilder;
import org.knime.gateway.api.webui.entity.AppStateChangedEventTypeEnt;
import org.knime.gateway.api.webui.entity.AppStateEnt;
import org.knime.gateway.api.webui.entity.AppStateEnt.AppStateEntBuilder;
import org.knime.gateway.api.webui.entity.CompositeEventEnt;
import org.knime.gateway.api.webui.entity.CompositeEventEnt.CompositeEventEntBuilder;
import org.knime.gateway.api.webui.entity.EventEnt;
import org.knime.gateway.api.webui.entity.ProjectDirtyStateEventEnt.ProjectDirtyStateEventEntBuilder;
import org.knime.gateway.impl.project.ProjectManager;
import org.knime.gateway.impl.webui.AppStateUpdater;
import org.knime.gateway.impl.webui.entity.AppStateEntityFactory;
import org.knime.gateway.impl.webui.entity.AppStateEntityFactory.ProjectFilter;
import org.knime.gateway.impl.webui.entity.AppStateEntityFactory.ServiceDependencies;

/**
 * Event source that emits events whenever the cached application state changes.
 *
 * @author Benjamin Moser, KNIME GmbH, Konstanz, Germany
 * @author Kai Franze, KNIME GmbH
 */
public class AppStateChangedEventSource extends EventSource<AppStateChangedEventTypeEnt, CompositeEventEnt> {

    private static final AppStateChangedEventEnt EMPTY_APP_STATE_CHANGED_EVENT =
        builder(AppStateChangedEventEntBuilder.class).setAppState(builder(AppStateEntBuilder.class).build()).build();

    private final AppStateUpdater m_appStateUpdater;

    private final ProjectManager m_projectManager;

    private final ServiceDependencies m_dependencies;

    private Runnable m_removeAppStateChangedListener;

    /**
     * @param eventConsumer consumes the emitted events
     * @param appStateUpdater
     * @param dependencies
     */
    public AppStateChangedEventSource(final EventConsumer eventConsumer, final AppStateUpdater appStateUpdater,
        final AppStateEntityFactory.ServiceDependencies dependencies) {
        super(eventConsumer);
        m_appStateUpdater = appStateUpdater;
        m_dependencies = dependencies;
        m_projectManager = dependencies.projectManager();
    }

    @Override
    public Optional<CompositeEventEnt>
        addEventListenerAndGetInitialEventFor(final AppStateChangedEventTypeEnt eventTypeEnt, final String projectId) {
        Runnable appStateChangedListener = this::checkForAppStateChangeAndSendEvent;
        m_appStateUpdater.addAppStateChangedListener(appStateChangedListener);
        m_removeAppStateChangedListener =
            () -> m_appStateUpdater.removeAppStateChangedListener(appStateChangedListener);

        if (!NodeSpecCollectionProvider.Progress.isDone()) {
            NodeSpecCollectionProvider.Progress.addListener(this::handleProgressEvent);
        }

        if (!m_appStateUpdater.filterProjectSpecificInfosFromEvents()
            && !m_projectManager.getDirtyProjectsMap().isEmpty()) {
            var projectDirtyStateEvent = EntityBuilderManager.builder(ProjectDirtyStateEventEntBuilder.class)
                .setDirtyProjectsMap(m_projectManager.getDirtyProjectsMap()) //
                .setShouldReplace(true) //
                .build();
            var compositeEvent = EntityBuilderManager.builder(CompositeEventEntBuilder.class)
                .setEvents(asList(null, projectDirtyStateEvent)).build();
            return Optional.of(compositeEvent);
        } else {
            return Optional.empty();
        }
    }

    private void handleProgressEvent(final ProgressEvent progressEvent) {
        if (progressEvent.isDone()) {
            checkForAppStateChangeAndSendEvent();
        }
    }

    private static AppStateChangedEventEnt buildEventEnt(final AppStateEnt newAppStateEnt) {
        return EntityBuilderManager.builder(AppStateChangedEventEnt.AppStateChangedEventEntBuilder.class)
            .setAppState(newAppStateEnt).build();
    }

    @Override
    public void removeEventListener(final AppStateChangedEventTypeEnt eventTypeEnt, final String projectId) {
        m_removeAppStateChangedListener.run();
    }

    @Override
    public void removeAllEventListeners() {
        removeEventListener(null, null);
    }

    @Override
    protected String getName() {
        return "AppStateChangedEvent:ProjectDirtyStateEvent";
    }

    private void checkForAppStateChangeAndSendEvent() {
        var appStateChangedEvent = buildAppStateChangedEvent();
        List<EventEnt> events = null;
        if (m_appStateUpdater.filterProjectSpecificInfosFromEvents()) {
            if (!EMPTY_APP_STATE_CHANGED_EVENT.equals(appStateChangedEvent)) {
                events = asList(appStateChangedEvent, null);
            }
        } else {
            var projectDirtyStateEvent = EntityBuilderManager.builder(ProjectDirtyStateEventEntBuilder.class)
                .setDirtyProjectsMap(m_dependencies.projectManager().getDirtyProjectsMap()) //
                .setShouldReplace(true) //
                .build();
            events = List.of(appStateChangedEvent, projectDirtyStateEvent);
        }

        if (events != null) {
            sendEvent(EntityBuilderManager.builder(CompositeEventEntBuilder.class).setEvents(events).build(), null);
        }
    }

    private AppStateChangedEventEnt buildAppStateChangedEvent() {
        var previousAppState = m_appStateUpdater.getLastAppState().orElse(null);
        var filterProjectSpecificInfosFromEvents = m_appStateUpdater.filterProjectSpecificInfosFromEvents();
        var projectFilter = filterProjectSpecificInfosFromEvents ? ProjectFilter.none() : ProjectFilter.all();
        Predicate<String> isActiveProject = filterProjectSpecificInfosFromEvents ? id -> false : null;
        var appState = AppStateEntityFactory.buildAppStateEnt(projectFilter, isActiveProject, m_dependencies);
        m_appStateUpdater.setLastAppState(appState);
        return buildEventEnt(AppStateEntityFactory.buildAppStateEntDiff(previousAppState, appState,
            !filterProjectSpecificInfosFromEvents));
    }
}

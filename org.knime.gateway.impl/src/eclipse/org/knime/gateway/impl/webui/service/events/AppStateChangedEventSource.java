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

import java.util.List;
import java.util.Optional;

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
import org.knime.gateway.api.webui.entity.ProjectDirtyStateEventEnt.ProjectDirtyStateEventEntBuilder;
import org.knime.gateway.impl.project.ProjectManager;
import org.knime.gateway.impl.webui.AppStateUpdater;
import org.knime.gateway.impl.webui.entity.AppStateEntityFactory;

/**
 * Event source that emits events whenever the cached application state changes.
 *
 * @author Benjamin Moser, KNIME GmbH, Konstanz, Germany
 * @author Kai Franze, KNIME GmbH
 */
public class AppStateChangedEventSource extends EventSource<AppStateChangedEventTypeEnt, CompositeEventEnt> {

    private final Runnable m_callback;

    private final AppStateUpdater m_appStateUpdater;

    private final ProjectManager m_workflowProjectManager;

    /**
     * @param eventConsumer consumes the emitted events
     * @param appStateUpdater
     * @param dependencies
     */
    public AppStateChangedEventSource(final EventConsumer eventConsumer, final AppStateUpdater appStateUpdater,
        final AppStateEntityFactory.ServiceDependencies dependencies) {
        super(eventConsumer);
        m_appStateUpdater = appStateUpdater;
        m_workflowProjectManager = dependencies.projectManager();
        m_callback = () -> {
            var previousAppState = appStateUpdater.getLastAppState().orElse(null);
            var appState = AppStateEntityFactory.buildAppStateEnt( //
                previousAppState, //
                null, //
                null, //
                dependencies //
            );
            appStateUpdater.setLastAppState(appState);
            var appStateEvent = buildEventEnt(AppStateEntityFactory.buildAppStateEntDiff(previousAppState, appState));
            var projectDirtyStateEvent = EntityBuilderManager.builder(ProjectDirtyStateEventEntBuilder.class)
                .setDirtyProjectsMap(dependencies.projectManager().getDirtyProjectsMap()) //
                .setShouldReplace(true) //
                .build();

            sendEvent( //
                EntityBuilderManager.builder(CompositeEventEntBuilder.class) //
                    .setEvents(List.of(appStateEvent, projectDirtyStateEvent)).build() //
            );
        };
    }

    @Override
    public Optional<CompositeEventEnt>
        addEventListenerAndGetInitialEventFor(final AppStateChangedEventTypeEnt eventTypeEnt) {
        m_appStateUpdater.addAppStateChangedListener(m_callback);

        if (!NodeSpecCollectionProvider.Progress.isDone()) {
            NodeSpecCollectionProvider.Progress.addListener(this::handleProgressEvent);
        }

        if (!m_workflowProjectManager.getDirtyProjectsMap().isEmpty()) {
            var appStateEvent = EntityBuilderManager.builder(AppStateChangedEventEntBuilder.class)
                .setAppState(EntityBuilderManager.builder(AppStateEntBuilder.class).build()).build();
            var projectDirtyStateEvent = EntityBuilderManager.builder(ProjectDirtyStateEventEntBuilder.class)
                .setDirtyProjectsMap(m_workflowProjectManager.getDirtyProjectsMap()).setShouldReplace(true).build();
            var compositeEvent = EntityBuilderManager.builder(CompositeEventEntBuilder.class)
                .setEvents(List.of(appStateEvent, projectDirtyStateEvent)).build();
            return Optional.of(compositeEvent);
        } else {
            return Optional.empty();
        }
    }

    private void handleProgressEvent(final ProgressEvent progressEvent) {
        if (progressEvent.isDone()) {
            m_callback.run();
        }
    }

    private static AppStateChangedEventEnt buildEventEnt(final AppStateEnt newAppStateEnt) {
        return EntityBuilderManager.builder(AppStateChangedEventEnt.AppStateChangedEventEntBuilder.class)
            .setAppState(newAppStateEnt).build();
    }

    @Override
    public void removeEventListener(final AppStateChangedEventTypeEnt eventTypeEnt) {
        m_appStateUpdater.removeAppStateChangedListener(m_callback);
    }

    @Override
    public void removeAllEventListeners() {
        removeEventListener(null);
    }

    @Override
    protected String getName() {
        return "AppStateChangedEvent:ProjectDirtyStateEvent";
    }
}

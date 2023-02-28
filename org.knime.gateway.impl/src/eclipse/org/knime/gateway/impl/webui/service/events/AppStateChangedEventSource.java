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

import java.util.Optional;

import org.knime.gateway.api.entity.EntityBuilderManager;
import org.knime.gateway.api.webui.entity.AppStateChangedEventEnt;
import org.knime.gateway.api.webui.entity.AppStateChangedEventTypeEnt;
import org.knime.gateway.api.webui.entity.AppStateEnt;
import org.knime.gateway.impl.project.WorkflowProjectManager;
import org.knime.gateway.impl.service.events.EventSource;
import org.knime.gateway.impl.service.util.EventConsumer;
import org.knime.gateway.impl.webui.AppStateUpdater;
import org.knime.gateway.impl.webui.NodeFactoryProvider;
import org.knime.gateway.impl.webui.PreferencesProvider;
import org.knime.gateway.impl.webui.entity.AppStateEntityFactory;
import org.knime.gateway.impl.webui.spaces.SpaceProviders;

/**
 * Event source that emits events whenever the cached application state changes.
 *
 * @author Benjamin Moser, KNIME GmbH, Konstanz, Germany
 * @author Kai Franze, KNIME GmbH
 */
public class AppStateChangedEventSource extends EventSource<AppStateChangedEventTypeEnt, AppStateChangedEventEnt> {

    private final Runnable m_callback;

    private final AppStateUpdater m_appStateUpdater;

    /**
     * @param eventConsumer consumes the emitted events
     * @param appStateUpdater
     * @param workflowProjectManager
     * @param preferenceProvider
     * @param spaceProviders
     */
    public AppStateChangedEventSource(final EventConsumer eventConsumer, final AppStateUpdater appStateUpdater,
        final WorkflowProjectManager workflowProjectManager, final PreferencesProvider preferenceProvider,
        final SpaceProviders spaceProviders, final NodeFactoryProvider nodeFactoryProvider ) {
        super(eventConsumer);
        m_appStateUpdater = appStateUpdater;
        m_callback = () -> {
            var lastAppState = appStateUpdater.getLastAppState().orElse(null);
            var appState = AppStateEntityFactory.buildAppStateEnt(lastAppState, workflowProjectManager,
                preferenceProvider, null, spaceProviders, nodeFactoryProvider);
            appStateUpdater.setLastAppState(appState);
            sendEvent(buildEventEnt(AppStateEntityFactory.buildAppStateEntDiff(lastAppState, appState)));
        };
    }

    @Override
    public Optional<AppStateChangedEventEnt>
        addEventListenerAndGetInitialEventFor(final AppStateChangedEventTypeEnt eventTypeEnt) {
        m_appStateUpdater.addAppStateChangedListener(m_callback);
        return Optional.empty();
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
        return "AppStateChangedEvent";
    }
}

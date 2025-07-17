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
 *   Feb 18, 2022 (hornm): created
 */
package org.knime.gateway.impl.webui;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import org.knime.gateway.api.webui.entity.AppStateChangedEventEnt;
import org.knime.gateway.api.webui.entity.AppStateEnt;
import org.knime.gateway.impl.webui.service.events.AppStateChangedEventSource;

/**
 * Essentially connects two 'sides':<br>
 * * one that listens for app state changes (e.g. {@link AppStateChangedEventSource})<br>
 * * one that informs about an app state change (e.g. the OpenWorkflowBrowserFunction)
 *
 * And keeps the app-state history.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 * @author Benjamin Moser, KNIME GmbH, Konstanz, Germany
 * @author Kai Franze, KNIME GmbH
 */
public final class AppStateUpdater {

    private final Set<Runnable> m_listeners = new HashSet<>();

    private AppStateEnt m_lastAppState;

    private final boolean m_filterProjectSpecificInfosFromEvents;

    @SuppressWarnings("javadoc")
    public AppStateUpdater() {
        this(false);
    }

    /**
     * @param filterProjectSpecificInfosFromEvents see {@link #filterProjectSpecificInfosFromEvents()}
     */
    public AppStateUpdater(final boolean filterProjectSpecificInfosFromEvents) {
        m_filterProjectSpecificInfosFromEvents = filterProjectSpecificInfosFromEvents;
    }

    /**
     * Informs all the registered listeners that the app state has changed.
     */
    public void updateAppState() {
        for (final var listener : m_listeners) {
            listener.run();
        }
    }

    /**
     * Subscribe a listener to updates of the application state. Do nothing if the listener is already registered.
     *
     * @param callback The callback to be called with the new application state
     * @since 5.6
     */
    public void addAppStateChangedListener(final Runnable callback) {
        m_listeners.add(callback);
    }

    /**
     * Remove a subscribed listener
     *
     * @param callback The callback to be removed from the set of listeners.
     * @since 5.6
     */
    public void removeAppStateChangedListener(final Runnable callback) {
        m_listeners.remove(callback);
    }

    /**
     * @return the last app state that has been provided via {@link #setLastAppState(AppStateEnt)}.
     */
    public Optional<AppStateEnt> getLastAppState() {
        return Optional.ofNullable(m_lastAppState);
    }

    /**
     * Updates the last app state.
     *
     * @param lastAppState
     */
    public void setLastAppState(final AppStateEnt lastAppState) {
        m_lastAppState = lastAppState;
    }

    /**
     * Workaround to optionally filter any project-specific infos from the app-state, such as
     * {@link AppStateEnt#getOpenProjects()}, before broadcasting the {@link AppStateChangedEventEnt} to the connected
     * clients (only relevant for AP in Hub). To be made obsolete when refactoring the app-state with NXT-1442.
     *
     * @return the filterProjectSpecificInfosFromEvents if {@code true}, project-specific infos will be filtered from
     *         the app-state
     */
    public boolean filterProjectSpecificInfosFromEvents() {
        return m_filterProjectSpecificInfosFromEvents;
    }

}

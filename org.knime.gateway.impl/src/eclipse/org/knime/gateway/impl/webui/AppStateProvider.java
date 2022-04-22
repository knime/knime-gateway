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
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Supplier;

import org.knime.core.node.port.PortType;

/**
 * Provides information about the state of the application. The application state is modelled by a reference to a
 * supplier.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 * @author Benjamin Moser, KNIME GmbH, Konstanz, Germany
 */
public final class AppStateProvider {

    private final Supplier<AppState> m_appStateSupplier;

    private final Set<Consumer<AppState>> m_listeners = new HashSet<>();

    /**
     * Create a new instance.
     *
     * @param supplier A callable supplying the application state
     */
    public AppStateProvider(final Supplier<AppState> supplier) {
        m_appStateSupplier = supplier;
    }

    /**
     * @return The current application state.
     */
    public AppState getAppState() {
        return m_appStateSupplier.get();
    }

    /**
     * Obtain the current application state and pass it to any listeners, regardless of whether the state has changed.
     */
    public void updateAppState() {
        var newAppState = m_appStateSupplier.get();
        m_listeners.forEach(l -> l.accept(newAppState));
    }

    /**
     * Subscribe a listener to updates of the application state. Do nothing if the listener is already registered.
     *
     * @param consumer The callback to be called with the new application state
     */
    public void addAppStateChangedListener(final Consumer<AppState> consumer) {
        m_listeners.add(consumer);
    }

    /**
     * Remove a subscribed listener
     *
     * @param consumer The callback to be removed from the set of listeners.
     */
    public void removeAppStateChangedListener(final Consumer<AppState> consumer) {
        m_listeners.remove(consumer);
    }

    /**
     * Describes the state of the application, such as e.g. currently open workflows.
     */
    public interface AppState {

        /**
         * @return list of all currently open workflows.
         */
        List<OpenedWorkflow> getOpenedWorkflows();

        /**
         * @return All port types available in the current extension
         */
        Set<PortType> getAvailablePortTypes();

        /**
         * @return List of recommended port types
         */
        List<PortType> getRecommendedPortTypes();

        /**
         * Represents an opened workflow.
         */
        interface OpenedWorkflow {

            /**
             * @return the workflow project id
             */
            String getProjectId();

            /**
             * @return the id representing the actual (sub-)workflow that is opened
             */
            String getWorkflowId();

            /**
             * @return <code>true</code> if the workflow is visible, otherwise <code>false</code>
             */
            boolean isVisible();

        }

    }

}

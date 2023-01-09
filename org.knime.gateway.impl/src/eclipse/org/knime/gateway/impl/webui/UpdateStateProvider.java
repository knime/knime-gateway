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
 *   Dec 15, 2022 (kai): created
 */
package org.knime.gateway.impl.webui;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Supplier;

import org.knime.core.eclipseUtil.UpdateChecker.UpdateInfo;
import org.knime.core.node.KNIMEConstants;
import org.knime.gateway.api.webui.entity.UpdateAvailableEventEnt;

/**
 * Source of truth regarding available application updates
 *
 * @author Kai Franze, KNIME GmbH
 */
public class UpdateStateProvider {

    private final Supplier<UpdateState> m_updateStateSupplier;

    private final Set<Consumer<UpdateState>> m_listeners = new HashSet<>();

    /**
     * Create a new instance.
     *
     * @param supplier A callable supplying the update state
     */
    public UpdateStateProvider(final Supplier<UpdateState> supplier) {
        m_updateStateSupplier = supplier;
    }

    /**
     * Check for updates and notify listeners
     */
    public void checkForUpdates() {
        KNIMEConstants.GLOBAL_THREAD_POOL.enqueue(() -> {
            var updateState = m_updateStateSupplier.get();
            if (updateState != null
                && (!updateState.getNewReleases().isEmpty() || !updateState.getBugfixes().isEmpty())) {
                m_listeners.forEach(listener -> listener.accept(updateState));
            }
        });
    }

    /**
     * Triggers and {@link UpdateAvailableEventEnt} for testing.
     *
     * @param newReleases The new release update information to emit
     * @param bugfixes The bugfix update information to emit
     */
    public void emitUpdateNotificationsForTesting(final List<UpdateInfo> newReleases, final List<String> bugfixes) {
        var updateState = new UpdateState() {
            @Override
            public List<UpdateInfo> getNewReleases() {
                return newReleases;
            }

            @Override
            public List<String> getBugfixes() {
                return bugfixes;
            }
        };
        m_listeners.forEach(listener -> listener.accept(updateState));
    }

    /**
     * @param consumer The event listener to register
     */
    public void addUpdateStateChangedListener(final Consumer<UpdateState> consumer) {
        m_listeners.add(consumer);
    }

    /**
     * @param consumer The event listener to unregister
     */
    public void removeUpdateStateChangedListener(final Consumer<UpdateState> consumer) {
        m_listeners.remove(consumer);
    }

    /**
     * Internal update state representation
     */
    public interface UpdateState {

        /**
         * @return A list of new releases
         */
        List<UpdateInfo> getNewReleases();

        /**
         * @return A list of bugfixes
         */
        List<String> getBugfixes();

    }

}

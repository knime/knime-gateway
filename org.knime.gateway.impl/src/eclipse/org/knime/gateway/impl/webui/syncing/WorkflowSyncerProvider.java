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
 *   Nov 18, 2025 (motacilla): created
 */
package org.knime.gateway.impl.webui.syncing;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.knime.gateway.api.util.DataSize;
import org.knime.gateway.impl.webui.AppStateUpdater;
import org.knime.gateway.impl.webui.spaces.SpaceProvider;
import org.knime.gateway.impl.webui.spaces.SpaceProvidersManager;
import org.knime.gateway.impl.webui.spaces.SpaceProvidersManager.Key;
import org.knime.gateway.impl.webui.syncing.WorkflowSyncer.DefaultWorkflowSyncer;
import org.knime.gateway.impl.webui.syncing.WorkflowSyncer.NoOpWorkflowSyncer;

/**
 * Provides the project specific {@link WorkflowSyncer}.
 *
 * @author Kai Franze, KNIME GmbH, Germany
 * @since 5.10
 */
public final class WorkflowSyncerProvider {

    private static final WorkflowSyncerProvider NO_OP = new WorkflowSyncerProvider(null, null, Duration.ZERO, DataSize.ZERO);

    private final Map<Key, WorkflowSyncer> m_workflowSyncers = new ConcurrentHashMap<>();

    private final AppStateUpdater m_appStateUpdater;

    private final SpaceProvidersManager m_spaceProvidersManager;

    private final Duration m_debounceInterval;

    private final DataSize m_syncThreshold;

    /**
     * Creates a new {link WorkflowSyncProvider}.
     *
     * @param appStateUpdater -
     * @param spaceProvidersManager -
     * @param syncDelay -
     * @param syncThresholdMB -
     */
    public WorkflowSyncerProvider(final AppStateUpdater appStateUpdater,
        final SpaceProvidersManager spaceProvidersManager, final Duration syncDelay, final DataSize syncThresholdMB) {
        m_appStateUpdater = appStateUpdater;
        m_spaceProvidersManager = spaceProvidersManager;
        m_debounceInterval = syncDelay;
        m_syncThreshold = syncThresholdMB;
    }

    /**
     * Creates a disabled {link WorkflowSyncProvider} that always returns {link NoOpWorkflowSyncer}.
     *
     * @return A disabled {@link WorkflowSyncerProvider}
     */
    public static WorkflowSyncerProvider disabled() {
        return NO_OP;
    }

    private boolean isEnabled() {
        return m_debounceInterval.isPositive() //
            && m_syncThreshold.bytes() >= 0 //
            && m_appStateUpdater != null //
            && m_spaceProvidersManager != null;
    }

    /**
     * @throws IllegalStateException if no {@link SpaceProvider} is available for the given key
     */
    private static SpaceProvider getSpaceProvider(final SpaceProvidersManager spaceProvidersManager,
        final Key key) {
        return spaceProvidersManager.getSpaceProviders(key) //
            .getAllSpaceProviders() //
            .stream() //
            .findFirst() //
            .orElseThrow(() -> new IllegalStateException("No SpaceProvider available for key: " + key));
    }

    /**
     * Get the {@link WorkflowSyncer} for the given project ID.
     *
     * @param key
     * @return The {@link WorkflowSyncer} associated with the {@link Key}
     */
    public WorkflowSyncer getWorkflowSyncer(final Key key) {
        return m_workflowSyncers.computeIfAbsent(key, k -> {
            if (!isEnabled()) {
                return new NoOpWorkflowSyncer();
            }

            final var spaceProvider = getSpaceProvider(m_spaceProvidersManager, k);
            final var projectId = key.toString();
            return new DefaultWorkflowSyncer(m_appStateUpdater, spaceProvider, m_debounceInterval, m_syncThreshold,
                projectId);
        });
    }
}

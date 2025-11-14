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
 *   Nov 14, 2025 (motacilla): created
 */
package org.knime.gateway.impl.webui;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.knime.core.node.NodeLogger;
import org.knime.gateway.impl.util.Debouncer;

/**
 * Automatically sync the currently open project
 *
 * @author Kai Franze, KNIME GmbH, Germany
 * @since 5.9
 */
public interface WorkflowSyncer {

    /**
     * Notify that the workflow has changed and needs to be synced.
     *
     * @param projectId the ID of the project that has changed
     */
    void notifyWorkflowChanged(final String projectId);

    /**
     * Get the current sync status.
     *
     * @return ...
     */
    SyncStatus getSyncStatus();

    /**
     * Status of the workflow sync
     */
    enum SyncStatus {
        SYNCED,
        SYNCING,
        OUT_OF_SYNC // TODO: When would we need this?
    }

    /**
     * Default implementation of {@link WorkflowSyncer} that does nothing.
     */
    static final class DefaultWorkflowSyncer implements WorkflowSyncer {

        private static final NodeLogger LOGGER = NodeLogger.getLogger(WorkflowSyncer.class);

        private final Debouncer m_debouncer; // Since sync can throw IOException

        private final Map<String, SyncStatus> m_statusMap = new ConcurrentHashMap<>();

        public DefaultWorkflowSyncer(final int delaySeconds, final AppStateUpdater appStateUpdater) {
            m_debouncer = new Debouncer(delaySeconds, (id) -> {
                m_statusMap.put(id, SyncStatus.SYNCING);
                appStateUpdater.updateAppState();

                // TODO: Implement actual sync logic here
                try {
                    Thread.sleep(2000); // Simulate sync time
                } catch (final InterruptedException e) {
                    Thread.currentThread().interrupt();
                }

                m_statusMap.put(id, SyncStatus.SYNCED);
                appStateUpdater.updateAppState();
            });
        }

        @Override
        public void notifyWorkflowChanged(final String projectId) {
            LOGGER.warn("Workflow change detected for project ID: " + projectId);
            m_debouncer.call(projectId);
        }

        @Override
        public SyncStatus getSyncStatus() {
            // TODO: We don't use the project ID here...
            return m_statusMap.entrySet().stream().findFirst().map(Map.Entry::getValue).orElse(SyncStatus.SYNCED);
        }
    }
}

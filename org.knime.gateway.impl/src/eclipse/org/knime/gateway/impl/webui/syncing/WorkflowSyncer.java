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
package org.knime.gateway.impl.webui.syncing;

import org.knime.core.node.NodeLogger;
import org.knime.core.node.workflow.WorkflowListener;
import org.knime.core.node.workflow.WorkflowManager;
import org.knime.gateway.impl.util.Debouncer;
import org.knime.gateway.impl.webui.AppStateUpdater;
import org.knime.gateway.impl.webui.spaces.SpaceProvidersManager;
import org.knime.gateway.impl.webui.spaces.SpaceProvidersManager.Key;

/**
 * Automatically sync the currently open project
 *
 * @author Kai Franze, KNIME GmbH, Germany
 * @since 5.9
 */
public interface WorkflowSyncer {

    /**
     * Status of the workflow sync
     */
    enum SyncStatus {
        SYNCED,
        SYNCING,
        OUT_OF_SYNC // TODO: When would we need this?
    }

    /**
     * Get the current sync status.
     *
     * @return ...
     */
    SyncStatus getSyncStatus();

    /**
     * Notify that the workflow has changed and needs to be synced.
     */
    void notifyWorkflowChanged();

    /**
     * The callback to be invoked when a workflow is loaded
     */
    void onLoadCallback(final WorkflowManager wfm);

    /**
     * The callback to be invoked when a workflow is disposed.
     */
    void onDisposeCallback(final WorkflowManager wfm);

    /**
     * Default implementation of {@link WorkflowSyncer} that does nothing.
     */
    static final class DefaultWorkflowSyncer implements WorkflowSyncer {

        private static final NodeLogger LOGGER = NodeLogger.getLogger(WorkflowSyncer.class);

        private final WorkflowListener m_listener = new SyncingListener(this::notifyWorkflowChanged);

        private final Debouncer m_debouncer;

        private SyncStatus m_syncStatus = SyncStatus.SYNCED; // To provide initial status

        DefaultWorkflowSyncer(final int delaySeconds, final AppStateUpdater appStateUpdater,
            final SpaceProvidersManager spaceProvidersManager, final Key key) {
            m_debouncer = new Debouncer(delaySeconds, () -> {
                m_syncStatus = SyncStatus.SYNCING;
                appStateUpdater.updateAppState();

                // TODO: Improve implementation
                final var context = LocalSaver.saveWorkflowToLocalDisk(key.toString()); // The actual "projectId" is in here
                context.ifPresent(ctx -> HubUploader.uploadToHub(ctx, spaceProvidersManager, key));

                m_syncStatus = SyncStatus.SYNCED;
                appStateUpdater.updateAppState();
            });
        }

        @Override
        public SyncStatus getSyncStatus() {
            return m_syncStatus;
        }

        @Override
        public void notifyWorkflowChanged() {
            LOGGER.warn("Workflow change detected");
            m_debouncer.call();
        }

        @Override
        public void onLoadCallback(final WorkflowManager wfm) {
            LOGGER.warn("'onLoadCallback' called for worklfow <%s>".formatted(wfm.getName()));
            wfm.addListener(m_listener);
        }

        @Override
        public void onDisposeCallback(final WorkflowManager wfm) {
            LOGGER.warn("'onLoadCallback' called for worklfow <%s>".formatted(wfm.getName()));
            wfm.addListener(m_listener);
        }
    }

    /**
     * No-op implementation of {@link WorkflowSyncer}.
     */
    static final class NoOpWorkflowSyncer implements WorkflowSyncer {

        @Override
        public SyncStatus getSyncStatus() {
            return SyncStatus.SYNCED;
        }

        @Override
        public void notifyWorkflowChanged() {
            // No-op
        }

        @Override
        public void onLoadCallback(final WorkflowManager wfm) {
            // No-op
        }

        @Override
        public void onDisposeCallback(final WorkflowManager wfm) {
            // No-op
        }
    }
}

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

import java.awt.GraphicsEnvironment;
import java.io.IOException;
import java.time.Duration;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;

import org.knime.core.node.NodeLogger;
import org.knime.core.node.workflow.WorkflowEvent;
import org.knime.core.node.workflow.WorkflowListener;
import org.knime.core.node.workflow.WorkflowManager;
import org.knime.core.node.workflow.WorkflowResourceCache.WorkflowResource;
import org.knime.gateway.api.util.DataSize;
import org.knime.gateway.api.webui.entity.SyncStateEnt;
import org.knime.gateway.api.webui.service.util.ServiceExceptions.LoggedOutException;
import org.knime.gateway.api.webui.service.util.ServiceExceptions.NetworkException;
import org.knime.gateway.api.webui.service.util.ServiceExceptions.ServiceCallException;
import org.knime.gateway.impl.util.Debouncer;
import org.knime.gateway.impl.webui.modes.WebUIMode;
import org.knime.gateway.impl.webui.spaces.SpaceProvider;
import org.knime.gateway.impl.webui.syncing.HubUploader.SyncThresholdException;

/**
 * Automatically sync the currently open project
 *
 * @author Kai Franze, KNIME GmbH, Germany
 * @since 5.10
 */
public interface WorkflowSyncer extends WorkflowResource {

    /**
     * @param config
     * @return {@code true} if the syncer can (and should) be enabled, false otherwise
     */
    static boolean canBeEnabled(final SyncerConfig config) {
        return WebUIMode.getMode() == WebUIMode.DEFAULT && //
            GraphicsEnvironment.isHeadless() && //
            config.sizeThreshold().bytes() >= 0;
    }

    /**
     * @return the current project sync state
     */
    SyncStateEnt getSyncState();

    /**
     * To manually synchronize the project
     *
     * @throws ServiceCallException -
     * @throws LoggedOutException -
     * @throws NetworkException -
     */
    void syncProjectNow() throws ServiceCallException, LoggedOutException, NetworkException;

    /**
     * @param listener listener to be called when the sync state changes
     */
    void addOnStateChangeListener(Runnable listener);

    /**
     * @param listener the listener to be removed
     */
    void removeOnStateChangeListener(Runnable listener);

    /**
     * To unregister listeners on the workflow when it is disposed.
     */
    @Override
    void dispose();

    /**
     * Default implementation of {@link WorkflowSyncer}.
     */
    final class DefaultWorkflowSyncer implements WorkflowSyncer {

        private static final NodeLogger LOGGER = NodeLogger.getLogger(DefaultWorkflowSyncer.class);

        private SyncStateStore m_syncStateStore;

        private final WorkflowListener m_workflowListener;

        private final LocalSaver m_localSaver;

        private final HubUploader m_hubUploader;

        private final Debouncer m_debouncedProjectSync;

        private WorkflowManager m_wfm;

        /**
         * Protects against reentrant calls (remaining in-flight wf change callbacks or multiple dispose calls).
         */
        private final AtomicBoolean m_disposed = new AtomicBoolean(false);

        /**
         * Creates a workflow syncer wired with default services.
         */
        public DefaultWorkflowSyncer(final WorkflowManager targetWfm, final SyncerConfig config,
            final SpaceProvider spaceProvider) {
            this( //
                targetWfm, //
                config, //
                new SyncStateStore(), //
                SyncingListener::new, //
                new LocalSaver(), //
                new HubUploader(spaceProvider), //
                task -> new Debouncer(config.debounceInterval(), task) //
            );
        }

        DefaultWorkflowSyncer(final WorkflowManager targetWfm, //
            final SyncerConfig config, //
            final SyncStateStore syncStateStore, //
            final Function<Runnable, WorkflowListener> listenerFactory, //
            final LocalSaver localSaver, //
            final HubUploader hubUploader, //
            final Function<Runnable, Debouncer> debouncerFactory //
        ) {
            m_syncStateStore = syncStateStore;
            m_workflowListener = listenerFactory.apply(this::notifyWorkflowChanged);
            m_localSaver = localSaver;
            m_hubUploader = hubUploader;
            m_debouncedProjectSync = debouncerFactory.apply(() -> syncProjectAutomatically(config.sizeThreshold()));
            LOGGER.info("'attach' called for workflow <%s>".formatted(targetWfm.getName()));
            m_wfm = targetWfm;
            targetWfm.addListener(m_workflowListener);
        }

        @Override
        public SyncStateEnt getSyncState() {
            return m_syncStateStore.buildSyncStateEnt();
        }

        private void notifyWorkflowChanged() {
            if (m_disposed.get()) {
                return;
            }
            m_syncStateStore.changeStateDeferrable(SyncStateEnt.StateEnum.DIRTY);
            m_debouncedProjectSync.call();
        }

        /**
         * Synchronizes the project via the auto-sync mechanism
         */
        void syncProjectAutomatically(final DataSize syncThreshold) {
            if (!m_syncStateStore.isAutoSyncEnabled()) {
                return;
            }

            m_syncStateStore.changeState(SyncStateEnt.StateEnum.WRITING);
            try {
                m_localSaver.saveProject(m_wfm);
            } catch (IOException e) {
                m_syncStateStore.changeState(SyncStateEnt.StateEnum.ERROR, new SyncStateStore.Error(e));
                return;
            }

            m_syncStateStore.changeState(SyncStateEnt.StateEnum.UPLOAD);
            m_syncStateStore.deferStateChanges(); // defer latest deferrable update
            try {
                m_hubUploader.uploadProjectWithThreshold(m_wfm, syncThreshold);
                m_syncStateStore.changeState(SyncStateEnt.StateEnum.SYNCED);
            } catch (IOException e) {
                m_syncStateStore.changeState( //
                    SyncStateEnt.StateEnum.ERROR, //
                    new SyncStateStore.Error(e));
            } catch (SyncThresholdException e) {
                m_syncStateStore.changeState( //
                    SyncStateEnt.StateEnum.DIRTY, //
                    new SyncStateStore.Error(e), //
                    false);
                m_debouncedProjectSync.shutdown();
            } finally {
                m_syncStateStore.allowStateChanges(); // apply deferred state change
            }
        }

        @Override
        public void syncProjectNow() throws ServiceCallException, LoggedOutException, NetworkException {
            assertIsNotSyncing(m_syncStateStore.state());

            m_syncStateStore.changeState(SyncStateEnt.StateEnum.WRITING);
            try {
                m_localSaver.saveProject(m_wfm);
            } catch (IOException e) {
                handleSyncIOException(e);
                return;
            }

            m_syncStateStore.changeState(SyncStateEnt.StateEnum.UPLOAD);
            m_syncStateStore.deferStateChanges(); // defer latest deferrable update
            try {
                m_hubUploader.uploadProject(m_wfm);
                m_syncStateStore.changeState(SyncStateEnt.StateEnum.SYNCED);
            } catch (LoggedOutException | NetworkException | ServiceCallException e) {
                LOGGER.error("Error during manual project sync: %s".formatted(e.getMessage()), e);
                m_syncStateStore.changeState(SyncStateEnt.StateEnum.ERROR);
                throw e;
            } finally {
                m_syncStateStore.allowStateChanges(); // apply deferred state change
            }
        }

        private static void assertIsNotSyncing(final SyncStateEnt.StateEnum state) throws ServiceCallException {
            if (state == SyncStateEnt.StateEnum.WRITING || state == SyncStateEnt.StateEnum.UPLOAD) {
                throw ServiceCallException.builder() //
                    .withTitle("Workflow is currently syncing") //
                    .withDetails("Please wait until the current sync operation is finished.") //
                    .canCopy(false) //
                    .build();
            }
        }

        private void handleSyncIOException(final IOException e) throws ServiceCallException {
            LOGGER.error("Error during manual project sync: %s".formatted(e.getMessage()), e);
            m_syncStateStore.changeState(SyncStateEnt.StateEnum.ERROR);
            throw ServiceCallException.builder() //
                .withTitle("Failed to sync workflow") //
                .withDetails(e.getClass().getSimpleName() + ": " + e.getMessage()) //
                .canCopy(true) //
                .withCause(e) //
                .build();
        }

        @Override
        public void dispose() {
            if (!m_disposed.compareAndSet(false, true)) {
                return;
            }
            LOGGER.info("'dispose' called for workflow <%s>".formatted(m_wfm.getName()));
            m_wfm.removeListener(m_workflowListener);
            m_debouncedProjectSync.shutdown();
        }

        @Override
        public void addOnStateChangeListener(final Runnable listener) {
            m_syncStateStore.addOnStateChangeListener(listener);
        }

        @Override
        public void removeOnStateChangeListener(final Runnable listener) {
            m_syncStateStore.removeOnStateChangeListener(listener);
        }

        private static final class SyncingListener implements WorkflowListener {

            private final Runnable m_onChange;

            SyncingListener(final Runnable callback) {
                m_onChange = callback;
            }

            @Override
            public void workflowChanged(final WorkflowEvent event) {
                // ignore
            }

            @Override
            public void workflowChanged() {
                m_onChange.run();
            }
        }
    }

    /**
     * Sync configuration values controlling debounce timing and upload thresholds.
     *
     * @param debounceInterval duration between automatic sync attempts
     * @param sizeThreshold data size threshold for auto-save
     */
    record SyncerConfig(Duration debounceInterval, DataSize sizeThreshold) {

        /** Defines the auto-save interval for syncing workflows in seconds */
        private static final String SYNC_AUTO_SAVE_INTERVAL_PROP = "com.knime.gateway.executor.sync.autoSaveInterval";

        /** Defines the auto-save threshold for syncing workflows in Kibibytes. If negative, disables auto-save. */
        private static final String SYNC_AUTO_SAVE_THRESHOLD_PROP = "com.knime.gateway.executor.sync.autoSaveThreshold";

        private static final Duration SYNC_AUTO_SAVE_INTERVAL_DEFAULT = Duration.ofSeconds(15);

        private static final DataSize SYNC_AUTO_SAVE_THRESHOLD_DEFAULT = DataSize.ofKibiBytes(10 * 1024l);

        /**
         * Parse debounce interval (seconds) and size threshold (KiB) into a config with defaults.
         *
         * @return parsed {@link SyncerConfig} with non-negative values and sensible defaults or empty invalid
         */
        public static SyncerConfig fromSystemProperties() {
            var debounceIntervalString = System.getProperty(SYNC_AUTO_SAVE_INTERVAL_PROP);
            var sizeThresholdString = System.getProperty(SYNC_AUTO_SAVE_THRESHOLD_PROP);
            final var debounceInterval = parseInteger(debounceIntervalString) //
                .map(Duration::ofSeconds) //
                .orElse(SYNC_AUTO_SAVE_INTERVAL_DEFAULT);
            final var sizeThreshold = parseInteger(sizeThresholdString)//
                .map(DataSize::ofKibiBytes) //
                .orElse(SYNC_AUTO_SAVE_THRESHOLD_DEFAULT);
            return new SyncerConfig(debounceInterval, sizeThreshold);
        }

        private static Optional<Integer> parseInteger(final String value) {
            if (value == null || value.isBlank()) {
                return Optional.empty();
            }
            try {
                return Optional.of(Integer.parseInt(value.trim()));
            } catch (NumberFormatException e) {
                return Optional.empty();
            }
        }
    }

    /**
     * A no-operation {@link WorkflowSyncer} implementation.
     */
    WorkflowSyncer DISABLED_SYNCER = new WorkflowSyncer() {

        @Override
        public SyncStateEnt getSyncState() {
            return null;
        }

        @Override
        public void syncProjectNow() {
            // since this syncer-instance effectively disables the syncing functionality altogether (by returning null at 'getSyncState')
            // we would never expect this method to be called
            throw new UnsupportedOperationException();
        }

        @Override
        public void addOnStateChangeListener(final Runnable listener) {
            // no-op
        }

        @Override
        public void removeOnStateChangeListener(final Runnable listener) {
            // no-op
        }

        @Override
        public void dispose() {
            // no-op
        }
    };

}

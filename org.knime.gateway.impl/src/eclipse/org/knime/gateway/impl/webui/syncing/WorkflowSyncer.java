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

import java.io.IOException;
import java.time.Duration;
import java.util.function.Function;

import org.knime.core.node.NodeLogger;
import org.knime.core.node.workflow.WorkflowListener;
import org.knime.core.node.workflow.WorkflowManager;
import org.knime.core.node.workflow.WorkflowResourceCache;
import org.knime.gateway.api.util.DataSize;
import org.knime.gateway.api.webui.entity.ProjectSyncStateEnt;
import org.knime.gateway.api.webui.service.util.ServiceExceptions.LoggedOutException;
import org.knime.gateway.api.webui.service.util.ServiceExceptions.NetworkException;
import org.knime.gateway.api.webui.service.util.ServiceExceptions.ServiceCallException;
import org.knime.gateway.impl.util.Debouncer;
import org.knime.gateway.impl.webui.AppStateUpdater;
import org.knime.gateway.impl.webui.spaces.SpaceProvider;
import org.knime.gateway.impl.webui.syncing.HubUploader.SyncThresholdException;
import org.knime.gateway.impl.webui.syncing.LocalSaver.SyncWhileWorkflowExecutingException;

/**
 * Automatically sync the currently open project
 *
 * @author Kai Franze, KNIME GmbH, Germany
 * @since 5.10
 */
public interface WorkflowSyncer {

    /** Defines the auto-save interval for syncing workflows in seconds */
    String SYNC_AUTO_SAVE_INTERVAL_PROP = "com.knime.gateway.executor.sync.autoSaveInterval";

    /** Defines the auto-save threshold for syncing workflows in megabytes */
    String SYNC_AUTO_SAVE_THRESHOLD_PROP = "com.knime.gateway.executor.sync.autoSaveThreshold";

    String SYNC_AUTO_SAVE_DISABLED = "com.knime.gateway.executor.sync.disabled";

    /**
     * @return the current project sync state
     */
    ProjectSyncStateEnt getProjectSyncState();

    /**
     * To manually synchronize the project
     *
     * @throws ServiceCallException -
     * @throws LoggedOutException -
     * @throws NetworkException -
     */
    void syncProjectNow() throws ServiceCallException, LoggedOutException, NetworkException;

    /**
     * To unregister listeners on the workflow when it is disposed.
     */
    void dispose();

    /**
     * Default implementation of {@link WorkflowSyncer}.
     */
    final class DefaultWorkflowSyncer implements WorkflowSyncer {

        private static final NodeLogger LOGGER = NodeLogger.getLogger(WorkflowSyncer.class);

        private SyncStateStore m_syncStateStore;

        private final WorkflowListener m_workflowListener;

        private final LocalSaver m_localSaver;

        private final HubUploader m_hubUploader;

        private final Debouncer m_debouncedProjectSync;

        private WorkflowManager m_wfm;

        /**
         * Bundles collaborators needed by the default syncer.
         */
        public record Dependencies(AppStateUpdater appStateUpdater, SpaceProvider provider) {

        }

        /**
         * Creates a workflow syncer wired with default services.
         */
        public DefaultWorkflowSyncer(final WorkflowManager targetWfm, final SyncerConfig config,
            final Dependencies dependencies) {
            this( //
                targetWfm, //
                config, //
                new SyncStateStore(() -> dependencies.appStateUpdater().updateAppState()), //
                SyncingListener::new, //
                new LocalSaver(), //
                new HubUploader(dependencies.provider()), //
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
        public ProjectSyncStateEnt getProjectSyncState() {
            return m_syncStateStore.buildSyncStateEnt();
        }

        private void notifyWorkflowChanged() {
            m_syncStateStore.changeStateDeferrable(ProjectSyncStateEnt.StateEnum.DIRTY);
            m_debouncedProjectSync.call();
        }

        /**
         * Synchronizes the project via the auto-sync mechanism
         */
        void syncProjectAutomatically(final DataSize syncThreshold) {
            if (!m_syncStateStore.isAutoSyncEnabled()) {
                return;
            }

            m_syncStateStore.changeState(ProjectSyncStateEnt.StateEnum.WRITING);
            try {
                m_localSaver.saveProject(m_wfm);
            } catch (IOException e) {
                m_syncStateStore.changeState(ProjectSyncStateEnt.StateEnum.ERROR, new SyncStateStore.Details(e));
                return;
            } catch (SyncWhileWorkflowExecutingException e) {
                m_syncStateStore.changeState(ProjectSyncStateEnt.StateEnum.DIRTY, new SyncStateStore.Details(e));
                return;
            }

            m_syncStateStore.changeState(ProjectSyncStateEnt.StateEnum.UPLOAD);
            m_syncStateStore.deferStateChanges(); // defer latest deferrable update
            try {
                m_hubUploader.uploadProjectWithThreshold(m_wfm, syncThreshold);
                m_syncStateStore.changeState(ProjectSyncStateEnt.StateEnum.SYNCED);
            } catch (IOException e) {
                m_syncStateStore.changeState( //
                    ProjectSyncStateEnt.StateEnum.ERROR, //
                    new SyncStateStore.Details(e));
            } catch (SyncThresholdException e) {
                m_syncStateStore.changeState( //
                    ProjectSyncStateEnt.StateEnum.DIRTY, //
                    new SyncStateStore.Details(e), //
                    false);
            } finally {
                m_syncStateStore.allowStateChanges(); // apply deferred state change
            }
        }

        @Override
        public void syncProjectNow() throws ServiceCallException, LoggedOutException, NetworkException {
            assertIsNotSyncing(m_syncStateStore.state());

            m_syncStateStore.changeState(ProjectSyncStateEnt.StateEnum.WRITING);
            try {
                m_localSaver.saveProject(m_wfm);
            } catch (IOException e) {
                handleSyncIOException(e);
                return;
            } catch (SyncWhileWorkflowExecutingException e) {
                handleSyncWhileExecutingException(e);
                return;
            }

            m_syncStateStore.changeState(ProjectSyncStateEnt.StateEnum.UPLOAD);
            m_syncStateStore.deferStateChanges(); // defer latest deferrable update
            try {
                m_hubUploader.uploadProject(m_wfm);
                m_syncStateStore.changeState(ProjectSyncStateEnt.StateEnum.SYNCED);
            } catch (LoggedOutException | NetworkException | ServiceCallException e) {
                LOGGER.error("Error during manual project sync: %s".formatted(e.getMessage()), e);
                m_syncStateStore.changeState(ProjectSyncStateEnt.StateEnum.ERROR);
                throw e;
            } finally {
                m_syncStateStore.allowStateChanges(); // apply deferred state change
            }
        }

        private static void assertIsNotSyncing(final ProjectSyncStateEnt.StateEnum state) throws ServiceCallException {
            if (state == ProjectSyncStateEnt.StateEnum.WRITING || state == ProjectSyncStateEnt.StateEnum.UPLOAD) {
                throw ServiceCallException.builder() //
                    .withTitle("Workflow is currently syncing") //
                    .withDetails("Please wait until the current sync operation is finished.") //
                    .canCopy(false) //
                    .build();
            }
        }

        private void handleSyncIOException(final IOException e) throws ServiceCallException {
            LOGGER.error("Error during manual project sync: %s".formatted(e.getMessage()), e);
            m_syncStateStore.changeState(ProjectSyncStateEnt.StateEnum.ERROR);
            throw ServiceCallException.builder() //
                .withTitle("Failed to save workflow") //
                .withDetails(e.getClass().getSimpleName() + ": " + e.getMessage()) //
                .canCopy(true) //
                .withCause(e) //
                .build();
        }

        private void handleSyncWhileExecutingException(final SyncWhileWorkflowExecutingException e)
            throws ServiceCallException {
            LOGGER.error("Project sync skipped because workflow is executing: %s".formatted(e.getMessage()), e);
            m_syncStateStore.changeState(ProjectSyncStateEnt.StateEnum.ERROR);
            throw ServiceCallException.builder() //
                .withTitle("Workflow is currently executing") //
                .withDetails("Cannot save workflow while it is executing.") //
                .canCopy(false) //
                .withCause(e) //
                .build();
        }

        @Override
        public void dispose() {
            LOGGER.info("'dispose' called for workflow <%s>".formatted(m_wfm.getName()));
            m_wfm.removeListener(m_workflowListener);
            m_debouncedProjectSync.shutdown();
        }
    }

    /**
     * WorkflowResource wrapper that disposes the underlying syncer with cache eviction.
     */
    class WorkflowSyncerResource implements WorkflowResourceCache.WorkflowResource {

        private final WorkflowSyncer m_syncer;

        public WorkflowSyncerResource(final WorkflowSyncer syncer) {
            m_syncer = syncer;
        }

        public WorkflowSyncer get() {
            return m_syncer;
        }

        @Override
        public void dispose() {
            m_syncer.dispose();
        }
    }

    /**
     * Sync configuration values controlling debounce timing and upload thresholds.
     */
    record SyncerConfig(Duration debounceInterval, DataSize sizeThreshold) {
        private static final Duration SYNC_AUTO_SAVE_INTERVAL_DEFAULT = Duration.ofSeconds(15);

        private static final DataSize SYNC_AUTO_SAVE_THRESHOLD_DEFAULT = DataSize.ofKibiBytes(10 * 1024);

        /**
         * Parse debounce interval (seconds) and size threshold (KiB) into a config with defaults.
         *
         * @param debounceIntervalString seconds between automatic sync attempts (blank -> default 15s)
         * @param sizeThresholdString size threshold in KiB for auto-save (blank -> default 10 MiB)
         * @return parsed {@link SyncerConfig} with non-negative values and sensible defaults
         */
        public static SyncerConfig of(String debounceIntervalString, String sizeThresholdString) {
            final var debounceIntervalSeconds = parseNonNegativeInt( //
                debounceIntervalString, //
                (int)SYNC_AUTO_SAVE_INTERVAL_DEFAULT.getSeconds() //
            );
            final var sizeThresholdKibibytes = parseNonNegativeInt( //
                sizeThresholdString, //
                (int)(SYNC_AUTO_SAVE_THRESHOLD_DEFAULT.bytes() / 1024) //
            );

            final var debounceInterval = Duration.ofSeconds(debounceIntervalSeconds);
            final var sizeThreshold = DataSize.ofKibiBytes(sizeThresholdKibibytes);

            return new SyncerConfig(debounceInterval, sizeThreshold);
        }

        private static int parseNonNegativeInt(final String value, final int defaultValue) {
            if (value == null || value.isBlank()) {
                return defaultValue;
            }
            try {
                final var parsed = Integer.parseInt(value.trim());
                return parsed >= 0 ? parsed : defaultValue;
            } catch (NumberFormatException e) {
                return defaultValue;
            }
        }
    }
}

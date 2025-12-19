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

import org.knime.core.node.NodeLogger;
import org.knime.core.node.workflow.WorkflowListener;
import org.knime.core.node.workflow.WorkflowManager;
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

    /**
     * @return the current project sync state
     */
    ProjectSyncStateEnt getProjectSyncState();

    /**
     * Resets the project sync state.
     */
    void resetProjectSyncState();

    /**
     * To manually synchronize the project
     *
     * @param projectId -
     * @throws ServiceCallException -
     * @throws LoggedOutException -
     * @throws NetworkException -
     */
    void syncProjectNow(final String projectId) throws ServiceCallException, LoggedOutException, NetworkException;

    /**
     * To register listeners on the workflow when it is loaded.
     *
     * @param wfm -
     */
    void onWfmLoad(final WorkflowManager wfm);

    /**
     * To unregister listeners on the workflow when it is disposed.
     *
     * @param wfm -
     */
    void onWfmDispose(final WorkflowManager wfm);

    /**
     * Default implementation of {@link WorkflowSyncer}.
     */
    static final class DefaultWorkflowSyncer implements WorkflowSyncer {

        private static final NodeLogger LOGGER = NodeLogger.getLogger(WorkflowSyncer.class);

        private SyncStateStore m_syncStateStore;

        private final WorkflowListener m_workflowListener;

        private final LocalSaver m_localSaver;

        private final HubUploader m_hubUploader;

        private final Debouncer m_debouncedProjectSync;

        DefaultWorkflowSyncer(final AppStateUpdater appStateUpdater, final SpaceProvider spaceProvider,
            final Duration syncDelay, final int syncThresholdMB, final String projectId) {
            m_syncStateStore = new SyncStateStore(appStateUpdater::updateAppState);
            m_workflowListener = new SyncingListener(this::notifyWorkflowChanged);
            m_localSaver = new LocalSaver();
            m_hubUploader = new HubUploader(spaceProvider);
            m_debouncedProjectSync = new Debouncer(syncDelay, //
                () -> syncProjectAutomatically(projectId, syncThresholdMB));
        }

        @Override
        public ProjectSyncStateEnt getProjectSyncState() {
            return m_syncStateStore.buildSyncStateEnt();
        }

        @Override
        public void resetProjectSyncState() {
            m_syncStateStore.reset();
        }

        private void notifyWorkflowChanged() {
            m_syncStateStore.changeStateDeferrable(ProjectSyncStateEnt.StateEnum.DIRTY);
            m_debouncedProjectSync.call();
        }

        /**
         * Synchronizes the project via the auto-sync mechanism
         */
        private void syncProjectAutomatically(final String projectId, final int syncThresholdMB) {
            if (!m_syncStateStore.isAutoSyncEnabled()) {
                return;
            }

            m_syncStateStore.changeState(ProjectSyncStateEnt.StateEnum.WRITING);
            try {
                m_localSaver.saveProject(projectId);
            } catch (IOException e) {
                m_syncStateStore.changeState(ProjectSyncStateEnt.StateEnum.ERROR, new SyncStateStore.Details(e));
                return;
            } catch (SyncWhileWorkflowExecutingException e) {
                m_syncStateStore.changeState(ProjectSyncStateEnt.StateEnum.DIRTY, new SyncStateStore.Details(e));
                return;
            }

            m_syncStateStore.changeState(ProjectSyncStateEnt.StateEnum.UPLOAD);
            m_syncStateStore.deferStateChanges(); // We deferStateChanges the sync state store to defer the latest deferrable update
            try {
                m_hubUploader.uploadProjectWithThreshold(projectId, syncThresholdMB);
                m_syncStateStore.changeState(ProjectSyncStateEnt.StateEnum.SYNCED);
            } catch (IOException e) {
                m_syncStateStore.changeState(ProjectSyncStateEnt.StateEnum.ERROR, new SyncStateStore.Details(e));
            } catch (SyncThresholdException e) {
                m_syncStateStore.changeState(ProjectSyncStateEnt.StateEnum.DIRTY, new SyncStateStore.Details(e), false);
            } finally {
                m_syncStateStore.allowStateChanges(); // We allowStateChanges the sync state store and apply the latest deferrable state change
            }
        }

        @Override
        public void syncProjectNow(final String projectId)
            throws ServiceCallException, LoggedOutException, NetworkException {
            assertIsNotSyncing(m_syncStateStore.state());

            m_syncStateStore.changeState(ProjectSyncStateEnt.StateEnum.WRITING);
            try {
                m_localSaver.saveProject(projectId);
            } catch (IOException e) {
                handleSyncIOException(e);
                return;
            } catch (SyncWhileWorkflowExecutingException e) {
                handleSyncWhileExecutingException(e);
                return;
            }

            m_syncStateStore.changeState(ProjectSyncStateEnt.StateEnum.UPLOAD);
            m_syncStateStore.deferStateChanges(); // We deferStateChanges the sync state store to defer the latest deferrable update
            try {
                m_hubUploader.uploadProject(projectId);
                m_syncStateStore.changeState(ProjectSyncStateEnt.StateEnum.SYNCED);
            } catch (LoggedOutException | NetworkException | ServiceCallException e) {
                LOGGER.error("Error during manual project sync: %s".formatted(e.getMessage()), e);
                m_syncStateStore.changeState(ProjectSyncStateEnt.StateEnum.ERROR);
                throw e;
            } finally {
                m_syncStateStore.allowStateChanges(); // We allowStateChanges the sync state store and apply the latest deferrable state change
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
        public void onWfmLoad(final WorkflowManager wfm) {
            LOGGER.info("'onWfmLoad' called for workflow <%s>".formatted(wfm.getName()));
            wfm.addListener(m_workflowListener);
        }

        @Override
        public void onWfmDispose(final WorkflowManager wfm) {
            LOGGER.info("'onWfmDispose' called for workflow <%s>".formatted(wfm.getName()));
            wfm.removeListener(m_workflowListener);
            m_debouncedProjectSync.shutdown();
        }
    }

    /**
     * No-op implementation of {@link WorkflowSyncer}.
     */
    static final class NoOpWorkflowSyncer implements WorkflowSyncer {

        static final WorkflowSyncer INSTANCE = new NoOpWorkflowSyncer();

        private static final ProjectSyncStateEnt SYNCED_STATE = new SyncStateStore().buildSyncStateEnt();

        @Override
        public ProjectSyncStateEnt getProjectSyncState() {
            return SYNCED_STATE;
        }

        @Override
        public void resetProjectSyncState() {
            // No-op
        }

        @Override
        public void syncProjectNow(final String projectId) throws ServiceCallException {
            // No-op
        }

        @Override
        public void onWfmLoad(final WorkflowManager wfm) {
            // No-op
        }

        @Override
        public void onWfmDispose(final WorkflowManager wfm) {
            // No-op
        }
    }
}

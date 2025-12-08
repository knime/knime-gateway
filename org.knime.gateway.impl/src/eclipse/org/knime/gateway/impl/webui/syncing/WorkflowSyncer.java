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

import org.knime.core.node.NodeLogger;
import org.knime.core.node.workflow.WorkflowListener;
import org.knime.core.node.workflow.WorkflowManager;
import org.knime.core.node.workflow.contextv2.WorkflowContextV2;
import org.knime.gateway.api.webui.entity.ProjectSyncStateEnt;
import org.knime.gateway.api.webui.service.util.ServiceExceptions.LoggedOutException;
import org.knime.gateway.api.webui.service.util.ServiceExceptions.NetworkException;
import org.knime.gateway.api.webui.service.util.ServiceExceptions.ServiceCallException;
import org.knime.gateway.impl.service.util.WorkflowManagerResolver;
import org.knime.gateway.impl.util.Debouncer;
import org.knime.gateway.impl.webui.AppStateUpdater;
import org.knime.gateway.impl.webui.spaces.SpaceProvider;
import org.knime.gateway.impl.webui.spaces.SpaceProvidersManager;
import org.knime.gateway.impl.webui.spaces.SpaceProvidersManager.Key;
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

        private final HubUploader m_hubUploader;

        private final Debouncer m_debouncedProjectSync;

        DefaultWorkflowSyncer(final AppStateUpdater appStateUpdater, final SpaceProvidersManager spaceProvidersManager,
            final int syncDelaySeconds, final int syncThresholdMB, final Key key) {
            m_syncStateStore = new SyncStateStore(appStateUpdater::updateAppState);
            m_workflowListener = new SyncingListener(this::notifyWorkflowChanged);
            m_hubUploader = new HubUploader(getSpaceProvider(spaceProvidersManager, key));
            m_debouncedProjectSync = new Debouncer(syncDelaySeconds, //
                () -> syncProjectAutomatically(key.toString(), syncThresholdMB));
        }

        @Override
        public ProjectSyncStateEnt getProjectSyncState() {
            return m_syncStateStore.buildSyncStateEnt();
        }

        private void notifyWorkflowChanged() {
            m_syncStateStore.deferrableUpdate(ProjectSyncStateEnt.StateEnum.DIRTY);
            m_debouncedProjectSync.call();
        }

        private static SpaceProvider getSpaceProvider(final SpaceProvidersManager spaceProvidersManager,
            final Key key) {
            return spaceProvidersManager.getSpaceProviders(key) //
                .getAllSpaceProviders() //
                .stream() //
                .findFirst() //
                .orElseThrow();
        }

        /**
         * Synchronizes the project via the auto-sync mechanism
         */
        private void syncProjectAutomatically(final String projectId, final int syncThresholdMB) {
            if (!m_syncStateStore.isAutoSyncEnabled()) {
                return;
            }

            m_syncStateStore.update(ProjectSyncStateEnt.StateEnum.BLOCKED);
            try {
                LocalSaver.saveProject(projectId);
            } catch (IOException e) {
                m_syncStateStore.update(ProjectSyncStateEnt.StateEnum.ERROR, new SyncStateStore.Details(e));
                return;
            } catch (SyncWhileWorkflowExecutingException e) {
                m_syncStateStore.update(ProjectSyncStateEnt.StateEnum.DIRTY, new SyncStateStore.Details(e));
                return;
            }

            m_syncStateStore.update(ProjectSyncStateEnt.StateEnum.UPLOAD);
            m_syncStateStore.lock(); // We lock the sync state store to defer the latest deferrable update
            try {
                m_hubUploader.uploadProjectWithThreshold(projectId, syncThresholdMB);
                m_syncStateStore.update(ProjectSyncStateEnt.StateEnum.SYNCED);
            } catch (IOException e) {
                m_syncStateStore.update(ProjectSyncStateEnt.StateEnum.ERROR, new SyncStateStore.Details(e));
            } catch (SyncThresholdException e) {
                m_syncStateStore.update(ProjectSyncStateEnt.StateEnum.DIRTY, new SyncStateStore.Details(e), false);
            } finally {
                m_syncStateStore.unlock(); // We unlock the sync state store and apply the latest deferrable update
            }
        }

        @Override
        public void syncProjectNow(final String projectId)
            throws ServiceCallException, LoggedOutException, NetworkException {
            assertIsNotSyncing(m_syncStateStore.state());

            m_syncStateStore.update(ProjectSyncStateEnt.StateEnum.BLOCKED);
            try {
                LocalSaver.saveProject(projectId);
            } catch (IOException e) {
                handleSyncIOException(e);
                return;
            } catch (SyncWhileWorkflowExecutingException e) {
                handleSyncWhileExecutingException(e);
                return;
            }

            m_syncStateStore.update(ProjectSyncStateEnt.StateEnum.UPLOAD);
            m_syncStateStore.lock(); // We lock the sync state store to defer the latest deferrable update
            try {
                m_hubUploader.uploadProject(projectId);
                m_syncStateStore.update(ProjectSyncStateEnt.StateEnum.SYNCED);
            } catch (IOException e) {
                handleSyncIOException(e);
            } finally {
                m_syncStateStore.unlock(); // We unlock the sync state store and apply the latest deferrable update
            }
        }

        private static void assertIsNotSyncing(final ProjectSyncStateEnt.StateEnum state) throws ServiceCallException {
            if (state == ProjectSyncStateEnt.StateEnum.BLOCKED || state == ProjectSyncStateEnt.StateEnum.UPLOAD) {
                throw ServiceCallException.builder() //
                    .withTitle("Workflow is currently syncing") //
                    .withDetails("Please wait until the current sync operation is finished.") //
                    .canCopy(false) //
                    .build();
            }
        }

        private void handleSyncIOException(final IOException e) throws ServiceCallException {
            LOGGER.error("Error during manual project sync: %s".formatted(e.getMessage()), e);
            m_syncStateStore.update(ProjectSyncStateEnt.StateEnum.ERROR);
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
            m_syncStateStore.update(ProjectSyncStateEnt.StateEnum.ERROR);
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
            m_syncStateStore.reset();
        }
    }

    /**
     * No-op implementation of {@link WorkflowSyncer}.
     */
    static final class NoOpWorkflowSyncer implements WorkflowSyncer {

        private static final ProjectSyncStateEnt SYNCED_STATE = new SyncStateStore().buildSyncStateEnt();

        @Override
        public ProjectSyncStateEnt getProjectSyncState() {
            return SYNCED_STATE;
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

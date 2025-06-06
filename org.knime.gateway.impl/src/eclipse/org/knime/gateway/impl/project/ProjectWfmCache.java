package org.knime.gateway.impl.project;

import org.knime.core.node.NodeLogger;
import org.knime.core.node.workflow.WorkflowManager;
import org.knime.core.util.LRUCache;
import org.knime.gateway.api.util.CoreUtil;
import org.knime.gateway.api.util.VersionId;
import org.knime.gateway.impl.util.Lazy;

import java.util.Objects;
import java.util.Optional;

/**
 * Caches loaded {@link WorkflowManager} instances associated with a project.
 *
 * @see Project
 */
interface ProjectWfmCache {

    /**
     * TODO
     *
     * @param version -
     * @return -
     */
    WorkflowManager getWorkflowManager(VersionId version);

    void dispose();

    void dispose(VersionId version);

    boolean contains(VersionId version);

    class CurrentStateOnly implements ProjectWfmCache {

        private WorkflowManager m_currentState;

        CurrentStateOnly(final WorkflowManager wfm) {
            m_currentState = Objects.requireNonNull(wfm, "wfm must not be null");
        }

        @Override
        public WorkflowManager getWorkflowManager(final VersionId version) {
            if (version.isCurrentState()) {
                if (m_currentState != null) {
                    return m_currentState;
                } else {
                    throw new IllegalStateException("Already disposed");
                }
            } else {
                throw new IllegalArgumentException(
                    "Cannot get workflow manager for non-current state version: " + version);
            }
        }

        @Override
        public void dispose() {
            m_currentState = null;
        }

        @Override
        public void dispose(final VersionId version) {
            m_currentState = null;
        }

        @Override
        public boolean contains(final VersionId version) {
            return version.isCurrentState() && m_currentState != null;
        }
    }

    /**
     * For fixed versions, we want an LRU cache. The current-state a.k.a. working area should be kept indefinitely.
     */
    class FromLoader implements ProjectWfmCache {

        private final WorkflowManagerLoader m_wfmLoader;

        private final Lazy.Init<WorkflowManager> m_currentState;

        private static final int VERSION_WFM_CACHE_MAX_SIZE = 5;

        private final LRUCache<VersionId.Fixed, WorkflowManager> m_fixedVersions;

        FromLoader(final WorkflowManagerLoader wfmLoader) {
            this(wfmLoader, new Lazy.Init<>(() -> wfmLoader.load(VersionId.currentState())));
        }

        FromLoader(final WorkflowManager wfm, final WorkflowManagerLoader wfmLoader) {
            this(wfmLoader, new Lazy.Init<>(wfm, () -> wfmLoader.load(VersionId.currentState())));
        }

        private FromLoader(final WorkflowManagerLoader wfmLoader, final Lazy.Init<WorkflowManager> currentState) {
            m_wfmLoader = Objects.requireNonNull(wfmLoader);
            m_currentState = Objects.requireNonNull(currentState);
            m_fixedVersions = new LRUCache<>( //
                VERSION_WFM_CACHE_MAX_SIZE, //
                (removedVersion, removedWfm) -> disposeWorkflowManager(removedWfm));
        }

        @Override
        public WorkflowManager getWorkflowManager(final VersionId version) {
            if (version instanceof VersionId.Fixed fixedVersion) {
                if (m_wfmLoader == null) {
                    throw new IllegalArgumentException(
                        "No workflow loader set for this project (required for dynamically loading some version)");
                }
                return m_fixedVersions.computeIfAbsent(fixedVersion, m_wfmLoader::load);
            } else {
                return m_currentState.get();
            }
        }

        @Override
        public void dispose() {
            this.dispose(VersionId.currentState());
            m_fixedVersions.keySet().stream().toList().forEach(this::dispose);
        }

        /**
         * Dispose the workflow manager instance corresponding to the given version, if present.
         *
         * @param version -
         */
        @Override
        public void dispose(final VersionId version) {
            if (version.isCurrentState()) {
                m_currentState.ifPresent(FromLoader::disposeWorkflowManager);
                m_currentState.clear();
            } else {
                Optional.ofNullable(m_fixedVersions.remove(version)) //
                    .ifPresent(FromLoader::disposeWorkflowManager);
            }
        }

        /**
         * -
         *
         * @param version -
         * @return Whether a workflow manager instance corresponding to the given version is already loaded
         */
        public boolean contains(final VersionId version) {
            if (version instanceof VersionId.Fixed fixedVersion) {
                return m_fixedVersions.containsKey(fixedVersion);
            } else if (version.isCurrentState()) {
                return m_currentState.isInitialized();
            } else {
                return false;
            }
        }

        private static void disposeWorkflowManager(final WorkflowManager wfm) {
            if (wfm == null) {
                return;
            }
            try {
                CoreUtil.cancelAndCloseLoadedWorkflow(wfm);
            } catch (InterruptedException e) { // NOSONAR
                NodeLogger.getLogger(FromLoader.class).error(e);
            }
        }
    }
}

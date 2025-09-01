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
 */

package org.knime.gateway.impl.project;

import java.util.Objects;
import java.util.Optional;

import org.knime.core.node.NodeLogger;
import org.knime.core.node.workflow.WorkflowManager;
import org.knime.core.util.LRUCache;
import org.knime.gateway.api.service.GatewayException;
import org.knime.gateway.api.util.CoreUtil;
import org.knime.gateway.api.util.VersionId;
import org.knime.gateway.impl.util.Lazy;

/**
 * For fixed versions, we want an LRU cache. The current-state a.k.a. working area should be kept indefinitely.
 */
class ProjectWfmCache {

    private final WorkflowManagerLoader m_wfmLoader;

    private final Lazy.Init<WorkflowManager, GatewayException> m_currentState;

    private static final int VERSION_WFM_CACHE_MAX_SIZE = 5;

    private final LRUCache<VersionId.Fixed, WorkflowManager> m_fixedVersions;

    ProjectWfmCache(final WorkflowManagerLoader wfmLoader) {
        this(wfmLoader, new Lazy.Init<>(() -> wfmLoader.load(VersionId.currentState())));
    }

    ProjectWfmCache(final WorkflowManager wfm, final WorkflowManagerLoader wfmLoader) {
        this(wfmLoader, new Lazy.Init<>(wfm, () -> wfmLoader.load(VersionId.currentState())));
    }

    private ProjectWfmCache(final WorkflowManagerLoader wfmLoader,
        final Lazy.Init<WorkflowManager, GatewayException> currentState) {
        m_wfmLoader = Objects.requireNonNull(wfmLoader);
        m_currentState = Objects.requireNonNull(currentState);
        m_fixedVersions = new LRUCache<>( //
            VERSION_WFM_CACHE_MAX_SIZE, //
            (removedVersion, removedWfm) -> disposeWorkflowManager(removedWfm));
    }

    Optional<WorkflowManager> getWorkflowManagerIfLoaded(final VersionId version) {
        if (version instanceof VersionId.Fixed fixedVersion) {
            return Optional.ofNullable(m_fixedVersions.get(fixedVersion));
        } else if (version.isCurrentState()) {
            try {
                return Optional.ofNullable(m_currentState.isInitialized() ? m_currentState.get() : null);
            } catch (final GatewayException ex) {
                // should never happen
                throw new IllegalStateException(ex);
            }
        } else {
            return Optional.empty();
        }
    }

    WorkflowManager getWorkflowManager(final VersionId version) {
        if (version instanceof VersionId.Fixed fixedVersion) {
            if (m_wfmLoader == null) {
                throw new IllegalArgumentException(
                    "No workflow loader set for this project (required for dynamically loading some version)");
            }

            final var existing = m_fixedVersions.get(fixedVersion);
            if (existing != null) {
                return existing;
            }

            final var loaded = m_wfmLoader.load(fixedVersion);
            m_fixedVersions.put(fixedVersion, loaded);
            return loaded;
        } else {
            try {
                return m_currentState.get();
            } catch (GatewayException e) {
                // TODO NXT-3938
                throw new RuntimeException(e);
            }
        }
    }

    void dispose() {
        this.dispose(VersionId.currentState());
        m_fixedVersions.keySet().stream().toList().forEach(this::dispose);
    }

    /**
     * Dispose the workflow manager instance corresponding to the given version, if present.
     *
     * @param version -
     */
    void dispose(final VersionId version) {
        if (version.isCurrentState()) {
            m_currentState.ifPresent(ProjectWfmCache::disposeWorkflowManager);
            m_currentState.clear();
        } else {
            Optional.ofNullable(m_fixedVersions.remove(version)) //
                .ifPresent(ProjectWfmCache::disposeWorkflowManager);
        }
    }

    /**
     * -
     *
     * @param version -
     * @return Whether a workflow manager instance corresponding to the given version is already loaded
     */
    boolean contains(final VersionId version) {
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
            NodeLogger.getLogger(ProjectWfmCache.class).error(e);
        }
    }
}

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

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;

import org.knime.core.util.Pair;
import org.knime.gateway.api.util.VersionId;
import org.knime.gateway.impl.util.Listeners;
import org.knime.gateway.impl.webui.WorkflowKey;

/**
 * Only need cache when project is created from loader -- and this is only OpenProject and AppStatePersistor
 * <p>
 * In shared-user setting, a user-specific instance can be obtained.
 */
public class WorkflowManagerCache {

    /**
     * Keys are project ids as used by {@link ProjectManager}
     */
    private final Map<String, ProjectWfmCache> m_projectCaches = new HashMap<>();

    private final Listeners<Predicate<WorkflowKey>> m_disposeListeners = new Listeners<>();

    /**
     * Return an already known project cache, or obtain a new project cache instance that is configured to notify this
     * instance when a WorkflowManager is disposed
     * 
     * @param projectId -
     * @param wfmLoader -
     * @return -
     */
    public ProjectWfmCache computeIfAbsent(final String projectId, final WorkflowManagerLoader wfmLoader) {
        return m_projectCaches.computeIfAbsent(projectId, id -> {
            final var projectCache = new ProjectWfmCache(projectId, wfmLoader);
            projectCache.getWfmDisposeListeners().add(this::onWfmDispose);
            projectCache.getInstanceDisposeListeners().add(m_projectCaches::remove);
            return projectCache;
        });
    }

    private void onWfmDispose(final Pair<String, VersionId> disposed) {
        this.getDisposeListeners().notify(wfKey -> wfKey.getProjectId().equals(disposed.getFirst())
            && wfKey.getVersionId().equals(disposed.getSecond()));
    }

    /**
     * -
     * 
     * @param projectId -
     * @return A project cache instance, if known.
     */
    public Optional<ProjectWfmCache> getProjectCache(final String projectId) {
        return Optional.ofNullable(m_projectCaches.get(projectId));
    }

    /**
     * -
     * 
     * @return -
     * @see this#m_disposeListeners
     */
    public Listeners<Predicate<WorkflowKey>> getDisposeListeners() {
        return m_disposeListeners;
    }

}

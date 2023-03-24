/*
 * ------------------------------------------------------------------------
 *  Copyright by KNIME AG, Zurich, Switzerland
 *  Website: http://www.knime.com; Email: contact@knime.com
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
 */
package org.knime.gateway.impl.project;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.apache.commons.lang3.concurrent.ConcurrentException;
import org.apache.commons.lang3.concurrent.LazyInitializer;
import org.knime.core.node.workflow.WorkflowManager;
import org.knime.core.util.Pair;

/**
 * Manages workflow projects that are eventually used by the service implementations.
 *
 * @author Martin Horn, University of Konstanz
 * @noreference This class is not intended to be referenced by clients.
 */
public final class WorkflowProjectManager {

    private static final LazyInitializer<WorkflowProjectManager> INITIALIZER = new LazyInitializer<>() {

        @Override
        protected WorkflowProjectManager initialize() throws ConcurrentException {
            return new WorkflowProjectManager();
        }

    };

    private final Map<String, WorkflowProject> m_workflowProjectMap = new LinkedHashMap<>();

    private final List<Consumer<String>> m_workflowRemovedListeners = new ArrayList<>();

    /**
     * Maps of already opened/loaded workflow projects.
     */
    private final Map<String, WorkflowManager> m_chachedWorkflowsMap = new HashMap<>();

    private String m_activeProjectId;

    private WorkflowProjectManager() {
        // singleton
    }

    /**
     * @return the singleton instance
     */
    public static WorkflowProjectManager getInstance() {
        try {
            return INITIALIZER.get();
        } catch (ConcurrentException ex) {
            throw new IllegalStateException(ex);
        }
    }

    /**
     * Marks the given project as being active (e.g., meaning that it's visible to the user in an opened tab).
     * All other opened projects are considered <b>not</b> active after this call.
     *
     * @param projectId the id to add
     * @throws NoSuchElementException if there is no workflow open/loaded for the given project id
     */
    public void setWorkflowProjectActive(final String projectId) {
        if (getCachedWorkflow(projectId).isEmpty()) {
            throw new NoSuchElementException("No loaded workflow for id " + projectId);
        }
        m_activeProjectId = projectId;
    }

    /**
     * @param projectId the id of the project to check
     * @return whether the project for the given id is active (e.g., meaning that it's to the user in an opened tab).
     */
    public boolean isActiveWorkflowProject(final String projectId) {
        return m_activeProjectId != null && m_activeProjectId.equals(projectId);
    }

    /**
     * @return the IDs of all registered workflow projects
     */
    public Set<String> getWorkflowProjectsIds() {
        return new LinkedHashSet<>(m_workflowProjectMap.keySet());
    }

    /**
     * Adds the workflow project with the id to the manager. If a workflow project with the given id already exists, it
     * will be replaced.
     *
     * @param workflowProjectID id of the project to be added
     * @param project the actual workflow project to be added
     */
    public void addWorkflowProject(final String workflowProjectID, final WorkflowProject project) {
        m_workflowProjectMap.put(workflowProjectID, project);
    }

    /**
     * If a workflow project for the given id exists it will be removed
     *
     * @param workflowProjectID id of the project to be removed
     */
    public void removeWorkflowProject(final String workflowProjectID) {
        m_workflowProjectMap.remove(workflowProjectID);
        m_chachedWorkflowsMap.remove(workflowProjectID);
        m_workflowRemovedListeners.stream().forEach(l -> l.accept(workflowProjectID));
        if (m_activeProjectId != null && m_activeProjectId.equals(workflowProjectID)) {
            m_activeProjectId = null;
        }
    }

    /**
     * @param workflowProjectID
     * @return the workflow project for the given id or an empty optional if doesn't exist
     */
    public Optional<WorkflowProject> getWorkflowProject(final String workflowProjectID) {
        return Optional.ofNullable(m_workflowProjectMap.get(workflowProjectID));
    }

    /**
     * Opens and caches the workflow with the given workflow project ID. If the workflow has already been opened, it
     * will be returned instead.
     *
     * @param workflowProjectID
     * @return the opened workflow or an empty optional if there is no workflow project with the given id or the project
     *         couldn't be opened
     */
    public Optional<WorkflowManager> openAndCacheWorkflow(final String workflowProjectID) {
        WorkflowManager wfm = getCachedWorkflow(workflowProjectID).orElse(null);
        if (wfm == null) {
            WorkflowProject wp = getWorkflowProject(workflowProjectID).orElse(null);
            if (wp == null) {
                return Optional.empty();
            }
            wfm = wp.openProject();
            if (wfm == null) {
                return Optional.empty();
            }
            cacheWorkflow(workflowProjectID, wfm);
        }
        return Optional.of(wfm);
    }

    /**
     * Callback when a workflow project with a certain ID has been removed.
     *
     * @param listener the listener to be called
     */
    public void addWorkflowProjectRemovedListener(final Consumer<String> listener) {
        m_workflowRemovedListeners.add(listener);
    }

    /**
     * @param workflowProjectID
     * @return the cached workflow or an empty optional if none has been found for the given workflow project ID.
     */
    public Optional<WorkflowManager> getCachedWorkflow(final String workflowProjectID) {
        return Optional.ofNullable(m_chachedWorkflowsMap.get(workflowProjectID));
    }

    /**
     * Determines whether the open projects are dirty ore not.
     *
     * @return map from project ids to the dirty flag of the workflows
     */
    public Map<String, Boolean> getProjectIdsToDirtyMap() {
        return getWorkflowProjectsIds().stream().map(projectId -> {
            var cachedWorkflow = getCachedWorkflow(projectId);
            if (cachedWorkflow.isEmpty()) {
                return Pair.create(projectId, Boolean.FALSE);
            }
            var isDirty = cachedWorkflow.get().isDirty();
            return Pair.create(projectId, isDirty);
        }).collect(Collectors.toMap(Pair::getFirst, Pair::getSecond));
    }

    /**
     * Caches the given workflow manager and therewith makes it available to other plugins etc. (e.g. the
     * ConnectionContainerEditPart-class in the org.knime.workbench.editor-plugin)
     *
     * @param wfm
     */
    private void cacheWorkflow(final String workflowProjectID, final WorkflowManager wfm) {
        m_chachedWorkflowsMap.put(workflowProjectID, wfm);
    }
}

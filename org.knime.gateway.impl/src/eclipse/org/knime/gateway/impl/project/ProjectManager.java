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
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.apache.commons.lang3.concurrent.ConcurrentException;
import org.apache.commons.lang3.concurrent.LazyInitializer;
import org.knime.core.node.workflow.WorkflowManager;
import org.knime.core.util.Pair;
import org.osgi.annotation.versioning.ConsumerType;

/**
 * Manages projects that are eventually used by the service implementations.
 *
 * @author Martin Horn, University of Konstanz
 * @noreference This class is not intended to be referenced by clients.
 */
public final class ProjectManager {

    private static final LazyInitializer<ProjectManager> INITIALIZER = new LazyInitializer<>() {

        @Override
        protected ProjectManager initialize() throws ConcurrentException {
            return new ProjectManager();
        }

    };

    private final Map<String, ProjectInternal> m_projectsMap = new LinkedHashMap<>();


    private final List<Consumer<String>> m_projectRemovedListeners = new ArrayList<>();

    /**
     * Maps of already opened/loaded projects.
     */
    private final Map<String, WorkflowManager> m_cachedProjectsMap = new HashMap<>();

    private String m_activeProjectId;

    private ProjectManager() {
        // singleton
    }

    /**
     * @return the singleton instance
     */
    public static ProjectManager getInstance() {
        try {
            return INITIALIZER.get();
        } catch (ConcurrentException ex) {
            throw new IllegalStateException(ex);
        }
    }

    /**
     * Marks the given project as being active (e.g., meaning that it's visible to the user in an opened tab). All other
     * opened projects are considered <b>not</b> active after this call.
     *
     * @param projectId the id to add
     * @throws NoSuchElementException if there is no project open/loaded for the given project id
     */
    public void setProjectActive(final String projectId) {
        if (getCachedProject(projectId).isEmpty()) {
            throw new NoSuchElementException("No loaded project for id " + projectId);
        }
        if (!m_projectsMap.get(projectId).hasUIConsumer) {
            throw new IllegalStateException("Projects hidden from the user can't be set active.");
        }
        m_activeProjectId = projectId;
    }

    /**
     * @param projectId the id of the project to check
     * @return whether the project for the given id is active (e.g., meaning that it's to the user in an opened tab).
     */
    public boolean isActiveProject(final String projectId) {
        return m_activeProjectId != null && m_activeProjectId.equals(projectId);
    }

    /**
     * @return the IDs of all registered projects
     */
    public List<String> getProjectIds() {
        return getProjectIds(ProjectConsumerType.UI);
    }

    /**
     * @param consumerType the {@link ProjectConsumerType} to get the project ids for
     * @return the ids of the registered projects
     */
    List<String> getProjectIds(final ProjectConsumerType consumerType) {
        return m_projectsMap.entrySet().stream()
            .filter(e -> consumerType.isUI() ? e.getValue().hasUIConsumer : (e.getValue().numNonUIConsumer > 0))
            .map(Entry::getKey).toList();
    }

    /**
     * Adds the project with the id to the manager. If a project with the given id already exists, it will be replaced.
     *
     * @param project the actual project to be added
     */
    public void addProject(final Project project) {
        addProject(project, ProjectConsumerType.UI, true);
    }

    /**
     * Adds the project with the id to the manager.
     *
     * @param project the actual project to be added
     * @param consumerType the {@link ProjectConsumerType} this project is being added for
     * @param replace whether to replace the project or not in case there is another project with the same id already
     */
    void addProject(final Project project, final ProjectConsumerType consumerType, final boolean replace) {
        boolean hasUIConsumer = consumerType.isUI();
        int numNonUIConsumer = consumerType.isUI() ? 0 : 1;
        var projectInternal = m_projectsMap.get(project.getID());
        if (projectInternal != null) {
            hasUIConsumer = hasUIConsumer || projectInternal.hasUIConsumer;
            numNonUIConsumer =
                consumerType.isUI() ? projectInternal.numNonUIConsumer : (projectInternal.numNonUIConsumer + 1);
        }

        if (projectInternal == null || replace) {
            projectInternal = new ProjectInternal(project, hasUIConsumer, numNonUIConsumer);
            project.getWorkflowManager().ifPresent(wfm -> cacheProject(project.getID(), wfm));
        } else {
            projectInternal = new ProjectInternal(projectInternal.project, hasUIConsumer, numNonUIConsumer);
        }

        m_projectsMap.put(project.getID(), projectInternal);
    }

    /**
     * If a project for the given id exists it will be removed
     *
     * @param projectId id of the project to be removed
     * @param disposeWfm the logic to also dispose the {@link WorkflowManager} which has been removed from the project
     *            manager
     */
    public void removeProject(final String projectId, final Consumer<WorkflowManager> disposeWfm) {
        removeProject(projectId, ProjectConsumerType.UI, disposeWfm);
    }

    /**
     * Removes the project for the given id. Note: the project might not be removed in case there are still other
     * consumers that rely on this project.
     *
     * @param projectId the project id to remove
     * @param consumerType the {@link ProjectConsumerType} to remove this project for
     * @param disposeWfm the logic to also dispose the {@link WorkflowManager} which has been removed from the project
     *            manager
     */
    void removeProject(final String projectId, final ProjectConsumerType consumerType,
        final Consumer<WorkflowManager> disposeWfm) {
        var projectInternal = m_projectsMap.get(projectId);
        if (projectInternal == null) {
            return;
        }

        if (consumerType.isUI()) {
            projectInternal = new ProjectInternal(projectInternal.project, false, projectInternal.numNonUIConsumer);
        } else {
            projectInternal = new ProjectInternal(projectInternal.project, projectInternal.hasUIConsumer,
                projectInternal.numNonUIConsumer - 1);
        }

        if (!projectInternal.hasUIConsumer && projectInternal.numNonUIConsumer == 0) {
            m_projectsMap.remove(projectId);
            var wfm = m_cachedProjectsMap.remove(projectId);
            if (wfm != null) {
                disposeWfm.accept(wfm);
            }
            m_projectRemovedListeners.stream().forEach(l -> l.accept(projectId));
            if (m_activeProjectId != null && m_activeProjectId.equals(projectId)) {
                m_activeProjectId = null;
            }
        } else {
            m_projectsMap.put(projectId, projectInternal);
        }
    }

    /**
     * @param projectId
     * @return the project for the given id or an empty optional if it doesn't exist
     */
    public Optional<Project> getProject(final String projectId) {
        return Optional.ofNullable(m_projectsMap.get(projectId)).map(ProjectInternal::project);
    }

    /**
     * Opens and caches the project with the given project ID. If the project has already been opened, it
     * will be returned instead.
     *
     * @param projectId
     * @return the opened project or an empty optional if there is no project with the given id or the project
     *         couldn't be opened
     */
    public Optional<WorkflowManager> openAndCacheProject(final String projectId) {
        WorkflowManager wfm = getCachedProject(projectId).orElse(null);
        if (wfm == null) {
            Project wp = getProject(projectId).orElse(null);
            if (wp == null) {
                return Optional.empty();
            }
            wfm = wp.loadWorkflowManager();
            if (wfm == null) {
                return Optional.empty();
            }
            cacheProject(projectId, wfm);
        }
        return Optional.of(wfm);
    }

    /**
     * Callback when a project with a certain ID has been removed.
     *
     * @param listener the listener to be called
     */
    public void addProjectRemovedListener(final Consumer<String> listener) {
        m_projectRemovedListeners.add(listener);
    }

    /**
     * @param projectId
     * @return the cached project or an empty optional if none has been found for the given project ID
     */
    public Optional<WorkflowManager> getCachedProject(final String projectId) {
        return Optional.ofNullable(m_cachedProjectsMap.get(projectId));
    }

    /**
     * Determines whether the open projects are dirty or not.
     *
     * @return map from project IDs to the dirty flag of the projects
     */
    public Map<String, Boolean> getDirtyProjectsMap() {
        return getProjectIds().stream().map(projectId -> {
            var cachedProject = getCachedProject(projectId);
            if (cachedProject.isEmpty()) {
                return Pair.create(projectId, Boolean.FALSE);
            }
            var isDirty = cachedProject.get().isDirty();
            return Pair.create(projectId, isDirty);
        }).collect(Collectors.toMap(Pair::getFirst, Pair::getSecond));
    }

    /**
     * Caches the given workflow manager and therewith makes it available to other plugins etc. (e.g. the
     * ConnectionContainerEditPart-class in the org.knime.workbench.editor-plugin)
     *
     * @param wfm
     */
    private void cacheProject(final String projectId, final WorkflowManager wfm) {
        m_cachedProjectsMap.put(projectId, wfm);
    }

    /**
     * Clears the entire state. For testing purposes only.
     */
    void clearState() {
        m_activeProjectId = null;
        m_projectsMap.clear();
        m_cachedProjectsMap.clear();
        m_projectRemovedListeners.clear();
    }

    /**
     * Wrapper around {@link Project} to additional track the {@link ConsumerType}s it is associated with.
     */
    private record ProjectInternal(Project project, boolean hasUIConsumer, int numNonUIConsumer) {
        //
    }

    /**
     * A project can have multiple consumers of certain type. Essentially controls whether a project can be removed for
     * real via {@link ProjectManager#removeProject(String, Consumer)}.
     */
    enum ProjectConsumerType {

            /**
             * If a project is consumed by the UI. There can only be one UI-consumer.
             */
            UI,
            /**
             * If a project is consumed in order to execute a workflow called by a workflow service node.
             */
            WORKFLOW_SERVICE;

        private boolean isUI() {
            return this == UI;
        }
    }

}

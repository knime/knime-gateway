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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.concurrent.ConcurrentException;
import org.apache.commons.lang3.concurrent.LazyInitializer;
import org.knime.core.node.ExecutionMonitor;
import org.knime.core.util.Pair;
import org.knime.gateway.api.webui.entity.SpaceItemReferenceEnt;
import org.knime.gateway.impl.webui.spaces.Space;
import org.knime.gateway.impl.webui.spaces.local.LocalSpace;

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

    private final ResortableMap<String, ProjectInternal> m_projectsMap = new ResortableMap<>();

    private final List<Consumer<String>> m_projectRemovedListeners = new ArrayList<>();

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
     * Checks whether there is already a workflow project loaded which is used as a workflow service (i.e. a workflow
     * executed by another workflow). If so, the respective project is updated to add the project-origin and mark it as
     * being used by the UI, too.
     *
     * @return the project and workflow-manager or null if none
     */
    public Optional<Project> getAndUpdateWorkflowServiceProject(final Space space, final String spaceProviderId,
        final String spaceId, final String itemId, final SpaceItemReferenceEnt.ProjectTypeEnum projectType) {
        if (!(space instanceof LocalSpace localSpace)) {
            return Optional.empty();
        }
        return localSpace.toLocalAbsolutePath(new ExecutionMonitor(), itemId) //
            .flatMap(WorkflowServiceProjects::getProjectIdAt) //
            .flatMap(this::getProject) //
            .flatMap(originalProject -> updateProject(originalProject, spaceProviderId, spaceId, itemId, projectType));
    }

    private Optional<Project> updateProject(final Project originalProject, final String spaceProviderId,
        final String spaceId, final String itemId, final SpaceItemReferenceEnt.ProjectTypeEnum projectType) {
        return originalProject.getWorkflowManagerIfLoaded().map(wfm -> {
            var updatedProject = Project.builder() //
                .setWfm(wfm) //
                .setId(originalProject.getID()) //
                .setOrigin(new Origin(spaceProviderId, spaceId, itemId, projectType)) //
                .build();
            this.addProject(updatedProject);
            return updatedProject;
        });
    }

    /**
     * Marks the given project as being active (e.g., meaning that it's visible to the user in an opened tab). All other
     * opened projects are considered <b>not</b> active after this call.
     *
     * @param projectId the id to add or {@code null} to unset the active project
     */
    public void setProjectActive(final String projectId) {
        var project = m_projectsMap.get(projectId);
        if (project != null && !project.hasUIConsumer) {
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
     * @param consumerType The {@link ProjectConsumerType} to get the project IDs for
     * @return The IDs of the registered projects. Since it's a 'LinkedHashMap', the order of the entries is guaranteed
     *         to be the insertion order.
     */
    List<String> getProjectIds(final ProjectConsumerType consumerType) {
        return m_projectsMap.entrySet().stream()
            .filter(e -> consumerType.isUI() ? e.getValue().hasUIConsumer : (e.getValue().numNonUIConsumer > 0)) //
            .map(Entry::getKey) //
            .toList();
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
        var hasUIConsumer = consumerType.isUI();
        var numNonUIConsumer = consumerType.isUI() ? 0 : 1;
        var projectInternal = m_projectsMap.get(project.getID());
        if (projectInternal != null) {
            hasUIConsumer = hasUIConsumer || projectInternal.hasUIConsumer;
            numNonUIConsumer =
                consumerType.isUI() ? projectInternal.numNonUIConsumer : (projectInternal.numNonUIConsumer + 1);
        }

        if (projectInternal == null || replace) {
            projectInternal = new ProjectInternal(project, hasUIConsumer, numNonUIConsumer);
        } else {
            projectInternal = new ProjectInternal(projectInternal.project, hasUIConsumer, numNonUIConsumer);
        }

        m_projectsMap.put(project.getID(), projectInternal);
    }

    /**
     * If a project for the given id exists it will be removed
     *
     * @param projectId id of the project to be removed
     */
    public void removeProject(final String projectId) {
        removeProject(projectId, ProjectConsumerType.UI);
    }

    /**
     * Remove the project for the given ID and dispose it. The project might not be removed in case there are still
     * other consumers that rely on this project.
     *
     * @param projectId the project id to remove
     * @param consumerType the {@link ProjectConsumerType} to remove this project for
     */
    void removeProject(final String projectId, final ProjectConsumerType consumerType) {
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
            var removedProject = m_projectsMap.remove(projectId);
            if (removedProject != null) {
                removedProject.project().dispose();
            }
            m_projectRemovedListeners.forEach(l -> l.accept(projectId));
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
     * Get a {@link Project} if one is currently open that matches the given ID triplet in its {@link Origin}.
     *
     * @param providerId
     * @param spaceId
     * @param itemId
     * @return A currently open project matching the given IDs in its {@link Origin}.
     */
    public Optional<Project> getProject(final String providerId, final String spaceId, final String itemId) {
        return projects().filter(project -> project.getOrigin() //
            .filter(origin -> origin.providerId().equals(providerId)) //
            .filter(origin -> origin.spaceId().equals(spaceId)) //
            .filter(origin -> origin.itemId().equals(itemId)) //
            .isPresent()) //
            .findFirst();
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
     * Determines whether the open projects are dirty or not.
     *
     * @return map from project IDs to the dirty flag of the projects
     */
    @SuppressWarnings("java:S1602")
    public Map<String, Boolean> getDirtyProjectsMap() {
        return projects().map(project -> {
            return project.getWorkflowManagerIfLoaded() //
                .map(wfm -> Pair.create(project.getID(), wfm.isDirty())) //
                .orElseGet(() -> Pair.create(project.getID(), false));
        }).collect(Collectors.toMap(Pair::getFirst, Pair::getSecond));
    }

    /**
     * The contained projects
     */
    public Stream<Project> projects() {
        return m_projectsMap.values().stream().map(ProjectInternal::project);
    }

    /**
     * Dispose all projects
     */
    public void disposeAll() {
        getProjectIds().forEach(id -> {
            getProject(id).ifPresent(Project::dispose);
            removeProject(id);
        });
    }

    /**
     * Updates the list of project IDs to reflect the new order.
     *
     * @param projectIds
     */
    public void updateOpenProjectsOrder(final List<String> projectIds) {
        m_projectsMap.resortKeys(projectIds);
    }

    /**
     * Clears the entire state. For testing purposes only.
     */
    void clearState() {
        m_activeProjectId = null;
        m_projectsMap.clear();
        m_projectRemovedListeners.clear();
    }

    /**
     * Wrapper around {@link Project} to additional track the {@link ProjectConsumerType}s it is associated with.
     */
    private record ProjectInternal(Project project, boolean hasUIConsumer, int numNonUIConsumer) {
        //
    }

    /**
     * A project can have multiple consumers of certain type. Essentially controls whether a project can be removed for
     * real via {@link #removeProject(String)}.
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

    /**
     * A re-sortable map.
     */
    @SuppressWarnings("serial")
    private static final class ResortableMap<K, V> extends LinkedHashMap<K, V> {

        void resortKeys(final List<K> keys) {
            var entriesInNewOrder = keys.stream() //
                .filter(this::containsKey) //
                .map(key -> Map.entry(key, this.get(key))) //
                .toList(); // To force evaluation, otherwise 'this.clear()' might be called before 'this.get(key)'
            this.clear();
            entriesInNewOrder.forEach(entry -> this.put(entry.getKey(), entry.getValue()));
        }

    }

}

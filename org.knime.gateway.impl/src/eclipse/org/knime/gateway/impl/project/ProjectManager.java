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
import org.knime.core.util.Pair;
import org.knime.gateway.api.util.VersionId;
import org.knime.gateway.api.webui.entity.SpaceItemReferenceEnt;
import org.knime.gateway.impl.webui.spaces.Space;
import org.knime.gateway.impl.webui.spaces.local.LocalSpace;

/**
 * Manages projects that are eventually used by the service implementations.
 *
 * @see Project
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

    // Note: We want to support multiple active projects in the future.
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
     * @param space -
     * @param spaceProviderId -
     * @param spaceId -
     * @param itemId -
     * @param projectType -
     * @return the project and workflow-manager or null if none
     * @since 5.5
     */
    public Optional<Project> getAndUpdateWorkflowServiceProject(final Space space, final String spaceProviderId,
        final String spaceId, final String itemId, final SpaceItemReferenceEnt.ProjectTypeEnum projectType) {
        if (!(space instanceof LocalSpace localSpace)) {
            return Optional.empty();
        }
        return localSpace.toLocalAbsolutePath(itemId) //
            .flatMap(WorkflowServiceProjects::getProjectIdAt) //
            .flatMap(this::getProject) //
            .flatMap(originalProject -> originalProject.getWorkflowManagerIfLoaded().map(wfm -> {
                var newOrigin = new Origin(spaceProviderId, spaceId, itemId, projectType);
                var updatedProject = Project.of(originalProject, newOrigin);
                this.addProject(updatedProject);
                return updatedProject;
            }));
    }

    /**
     * Marks the given project as being active (e.g., meaning that it's visible to the user in an opened tab). All other
     * opened projects are considered <b>not</b> active after this call.
     *
     * @param projectId the id to add or {@code null} to unset the active project
     */
    public void setProjectActive(final String projectId) {
        setProjectActive(projectId, VersionId.currentState());
    }

    /**
     * Marks the given project as being active (e.g., meaning that it's visible to the user in an opened tab). All other
     * opened projects are considered <b>not</b> active after this call.
     *
     * @param projectId the id to add or {@code null} to unset the active project
     * @param versionId the version id to set as active
     * @since 5.5
     */
    public void setProjectActive(final String projectId, final VersionId versionId) {
        var projectInternal = m_projectsMap.get(projectId);
        if (projectInternal == null) {
            m_activeProjectId = null;
            return; // No project active
        }

        if (!projectInternal.hasUIConsumer) {
            throw new IllegalStateException("Projects hidden from the user can't be set active.");
        }

        addProject(projectInternal.project, versionId);
        m_activeProjectId = projectId;
    }

    /**
     * Checks whether the project for the given id is active (e.g., meaning that it's to the user in an opened tab).
     *
     * @param projectId the id of the project to check
     * @return whether the project for the given id is active (e.g., meaning that it's to the user in an opened tab).
     */
    public boolean isActiveProject(final String projectId) {
        // Note: We want to support multiple active projects in the future.
        if (m_activeProjectId == null) {
            return false;
        }
        return m_activeProjectId.equals(projectId);
    }

    /**
     * Checks whether the project for the given id is active (e.g., meaning that it's to the user in an opened tab) and
     * the version is active.
     *
     * @param projectId the id of the project to check
     * @param versionId the version id to check
     * @return Whether the project is active and the version is the active version.
     * @since 5.5
     */
    public boolean isActiveProjectVersion(final String projectId, final VersionId versionId) {
        // If no active project is set, we assume the active version wasn't set. In that case we return 'true', since
        // we are most likely in the browser context, where the concept of an active project doesn't exist.
        if (m_activeProjectId == null) {
            return true;
        }

        var thisProject = m_activeProjectId;
        var thisVersion = Optional.ofNullable(m_projectsMap.get(m_activeProjectId)) //
            .map(ProjectInternal::activeVersion) //
            .orElse(VersionId.currentState());

        return thisProject.equals(projectId) && thisVersion.equals(versionId);
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
        addProject(project, VersionId.currentState());
    }

    private void addProject(final Project project, final VersionId activeVersion) {
        addProject(project, ProjectConsumerType.UI, true, activeVersion);
    }

    /**
     * Adds the project with the id to the manager.
     *
     * @param project the actual project to be added
     * @param consumerType the {@link ProjectConsumerType} this project is being added for
     * @param replace whether to replace the project or not in case there is another project with the same id already
     */
    void addProject(final Project project, final ProjectConsumerType consumerType, final boolean replace) {
        addProject(project, consumerType, replace, VersionId.currentState());
    }

    private void addProject(final Project project, final ProjectConsumerType consumerType, final boolean replace,
        final VersionId activeVersion) {
        var hasUIConsumer = consumerType.isUI();
        var numNonUIConsumer = consumerType.isUI() ? 0 : 1;
        var projectInternal = m_projectsMap.get(project.getID());
        if (projectInternal != null) {
            hasUIConsumer = hasUIConsumer || projectInternal.hasUIConsumer;
            numNonUIConsumer =
                consumerType.isUI() ? projectInternal.numNonUIConsumer : (projectInternal.numNonUIConsumer + 1);
        }

        if (projectInternal == null || replace) {
            projectInternal = new ProjectInternal(project, hasUIConsumer, numNonUIConsumer, activeVersion);
        } else {
            projectInternal =
                new ProjectInternal(projectInternal.project, hasUIConsumer, numNonUIConsumer, activeVersion);
        }

        m_projectsMap.put(project.getID(), projectInternal);
    }

    /**
     * If a project for the given id exists it will be removed
     *
     * @param projectId id of the project to be removed
     * @since 5.5
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

        var updatedProjectInternal = consumerType.isUI() //
            ? projectInternal.updateHasUIConsumer(false) //
            : projectInternal.updateNumNonUIConsumer(projectInternal.numNonUIConsumer - 1);

        if (!updatedProjectInternal.hasUIConsumer && updatedProjectInternal.numNonUIConsumer == 0) {
            var removedProject = m_projectsMap.remove(projectId);
            if (removedProject != null) {
                removedProject.project().dispose();
            }
            m_projectRemovedListeners.forEach(l -> l.accept(projectId));

            // Note: We want to support multiple active projects in the future.
            if (m_activeProjectId != null && m_activeProjectId.equals(projectId)) {
                m_activeProjectId = null; // To indicate there's no active project anymore
            }
        } else {
            m_projectsMap.put(projectId, updatedProjectInternal);
        }
    }

    /**
     * Get a project by ID.
     *
     * @param projectId -
     * @return the project for the given id or an empty optional if it doesn't exist
     */
    public Optional<Project> getProject(final String projectId) {
        return Optional.ofNullable(m_projectsMap.get(projectId)).map(ProjectInternal::project);
    }

    /**
     * Get the active version by project ID.
     *
     * @param projectId -
     * @return the active version of the project or an empty optional if it doesn't exist
     * @since 5.5
     */
    public Optional<VersionId> getActiveVersionForProject(final String projectId) {
        return Optional.ofNullable(m_projectsMap.get(projectId)).map(ProjectInternal::activeVersion);
    }

    /**
     * Get a {@link Project} if one is currently open that matches the given ID triplet in its {@link Origin}.
     *
     * @param providerId -
     * @param spaceId -
     * @param itemId -
     * @return A currently open project matching the given IDs in its {@link Origin}.
     * @since 5.5
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
     * Add a listener to be notified when a project is removed. The ID of the removed project is provided to the
     * listener.
     *
     * @param listener the listener to be added
     * @see #removeProject(String)
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
     *
     * @return -
     * @since 5.5
     */
    public Stream<Project> projects() {
        return m_projectsMap.values().stream().map(ProjectInternal::project);
    }

    /**
     * Dispose all projects
     * @since 5.5
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
     * @param projectIds Assumed to contain all currently open projects. The order of this list defines the new
     *            ordering.
     * @since 5.5
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
    private record ProjectInternal(Project project, boolean hasUIConsumer, int numNonUIConsumer,
        VersionId activeVersion) {

        ProjectInternal updateHasUIConsumer(final boolean updatedHasUIConsumer) {
            return new ProjectInternal(project, updatedHasUIConsumer, numNonUIConsumer, activeVersion);
        }

        ProjectInternal updateNumNonUIConsumer(final int updatedNumNonUIConsumer) {
            return new ProjectInternal(project, hasUIConsumer, updatedNumNonUIConsumer, activeVersion);
        }

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
            WORKFLOW_SERVICE,

            /**
             * If a project is used as a virtual workflow, e.g., for tool execution in an agent node.
             */
            VIRTUAL_WORKFLOW;

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

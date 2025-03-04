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
 *   Nov 21, 2023 (hornm): created
 */
package org.knime.gateway.impl.project;

import java.nio.file.Path;
import java.util.Optional;

import org.knime.core.node.workflow.WorkflowManager;
import org.knime.gateway.impl.project.ProjectManager.ProjectConsumerType;

/**
 * Utility methods to manage workflow projects loaded in order to locally execute workflows from other workflows (call
 * workflow/workflow service).
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
public final class WorkflowServiceProjects {

    private static Runnable removeAllProjectsCallback;

    private WorkflowServiceProjects() {
        // utility
    }

    /**
     * Registers a new project.
     *
     * @param wfm the workflow manager representing the project
     */
    public static void registerProject(final WorkflowManager wfm) {
        ProjectManager.getInstance().addProject( //
            Project.builder().setWfm(wfm).build(), //
            ProjectConsumerType.WORKFLOW_SERVICE, //
            false //
        );
    }

    /**
     * @param absolutePath the absolute path to the workflow within the local workspace
     * @return the project id or an empty optional if there is no project for the given path
     */
    @SuppressWarnings({"java:S1602"}) // braces around 1-expression lambda
    public static Optional<String> getProjectIdAt(final Path absolutePath) {
        var projectManager = ProjectManager.getInstance();
        return projectManager.getProjectIds(ProjectConsumerType.WORKFLOW_SERVICE).stream() //
            .filter(projectId -> { //
                return projectManager.getProject(projectId) //
                    .flatMap(Project::getWorkflowManagerIfLoaded).filter(wfm -> isAtPath(wfm, absolutePath)) //
                    .isPresent(); //
            }) //
            .findFirst();
    }

    private static boolean isAtPath(final WorkflowManager wfm, final Path absolutePath) {
        return Optional.ofNullable(wfm.getContextV2()) //
            .map(context -> context.getExecutorInfo().getLocalWorkflowPath()) //
            .map(wfPath -> wfPath.equals(absolutePath)) //
            .orElse(false);
    }

    /**
     * Unregisters the project and disposes the associated {@link WorkflowManager}.
     *
     * @param absolutePath the absolute path to the workflow within the local workspace
     */
    @SuppressWarnings("java:S1602")
    public static void removeProject(final Path absolutePath) {
        getProjectIdAt(absolutePath).ifPresent(projectId -> {
            ProjectManager.getInstance().removeProject( //
                projectId, //
                ProjectConsumerType.WORKFLOW_SERVICE //
            //
            );
        });
    }

    /**
     * Removes all workflow service projects from memory.
     */
    public static void removeAllProjects() {
        if (removeAllProjectsCallback != null) {
            removeAllProjectsCallback.run();
        }
    }

    /**
     * Callback registered by the LocalWorkflowBackend such that it can be informed when all workflow service projects
     * are to be removed from memory.
     *
     * @param callback
     */
    public static void setOnRemoveAllProjectsCallback(final Runnable callback) {
        if (removeAllProjectsCallback != null) {
            throw new IllegalStateException(
                "A WorkflowServiceProjects 'onRemoveAllProjects'-callback is already registered.");
        }
        removeAllProjectsCallback = callback;
    }

}

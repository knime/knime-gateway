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

import org.knime.core.node.NodeLogger;
import org.knime.core.node.workflow.WorkflowManager;
import org.knime.gateway.api.util.CoreUtil;
import org.knime.gateway.impl.project.ProjectManager.ProjectConsumer;

/**
 * Utility methods to manage workflow projects loaded in order to locally execute workflows from other workflows (call
 * workflow/workflow service).
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
public class WorkflowServiceProjects {

    /**
     * Registers a new project.
     *
     * @param wfm the workflow manager representing the project
     */
    public static void registerProject(final WorkflowManager wfm) {
        ProjectManager.getInstance().addProject(DefaultProject.builder(wfm).build(), ProjectConsumer.WORKFLOW_SERVICE,
            false);
    }

    /**
     * @param absolutePath the absolute path to the workflow within the local workspace
     * @return the project id or an empty optional if there is no project for the given path
     */
    public static Optional<String> getProject(final Path absolutePath) {
        var pm = ProjectManager.getInstance();
        for (var projectId : pm.getProjectIds(ProjectConsumer.WORKFLOW_SERVICE)) {
            var wfm = pm.getCachedProject(projectId).orElse(null);
            if (wfm != null && Optional.ofNullable(wfm.getContextV2()) //
                .map(context -> context.getExecutorInfo().getLocalWorkflowPath()) //
                .map(wfPath -> wfPath.equals(absolutePath)).orElse(Boolean.FALSE)) {
                return Optional.of(projectId);
            }
        }
        return Optional.empty();
    }

    /**
     * Unregisters the project and disposes the associated {@link WorkflowManager}.
     *
     * @param absolutePath the absolute path to the workflow within the local workspace
     */
    public static void removeProject(final Path absolutePath) {
        var pm = ProjectManager.getInstance();
        var projectId = getProject(absolutePath).orElse(null);
        if (projectId != null) {
            pm.removeProject(projectId, ProjectConsumer.WORKFLOW_SERVICE, wfm -> {
                try {
                    CoreUtil.cancelAndCloseLoadedWorkflow(wfm);
                } catch (InterruptedException e) { // NOSONAR
                    NodeLogger.getLogger(WorkflowServiceProjects.class).error(e);
                }
            });
        }
    }

}

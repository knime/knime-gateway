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
 *   Nov 28, 2016 (hornm): created
 */
package com.knime.gateway.remote.endpoint;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;

import org.knime.core.node.NodeLogger;
import org.knime.core.node.workflow.WorkflowManager;

import com.knime.gateway.remote.service.DefaultWorkflowService;

/**
 * Manages workflow projects that are eventually used by the default service implementations (e.g.
 * {@link DefaultWorkflowService}).
 *
 * @author Martin Horn, University of Konstanz
 * @noreference This class is not intended to be referenced by clients.
 */
public final class WorkflowProjectManager {

    private static final NodeLogger LOGGER = NodeLogger.getLogger(WorkflowProjectManager.class);

    private static Map<UUID, WorkflowProject> WORKFLOW_PROJECT_MAP = new HashMap<UUID, WorkflowProject>();

    private static List<Consumer<UUID>> WORKFLOW_REMOVED_LISTENERS = new ArrayList<Consumer<UUID>>();

    /**
     * Maps of already opened/loaded workflow projects.
     */
    private static Map<UUID, WorkflowManager> CACHED_WORKFLOWS_MAP = new HashMap<UUID, WorkflowManager>();

    private WorkflowProjectManager() {
        //~ static utility class
    }

    /**
     * @return all registered workflow projects
     */
    public static Collection<WorkflowProject> getWorkflowProjects() {
        return WORKFLOW_PROJECT_MAP.values();
    }

    /**
     * Adds the workflow project with the id to the manager. If a workflow project with the given id already exists, it
     * will be replaced.
     *
     * @param worklfowProjectID id of the project to be added
     * @param project the actual workflow project to be added
     */
    public static void addWorkflowProject(final UUID worklfowProjectID, final WorkflowProject project) {
        WORKFLOW_PROJECT_MAP.put(worklfowProjectID, project);
    }

    /**
     * If a workflow project for the given id exists it will be removed
     *
     * @param workflowProjectID id of the project to be removed
     */
    public static void removeWorkflowProject(final UUID workflowProjectID) {
         WORKFLOW_PROJECT_MAP.remove(workflowProjectID);
         CACHED_WORKFLOWS_MAP.remove(workflowProjectID);
         WORKFLOW_REMOVED_LISTENERS.stream().forEach(l -> l.accept(workflowProjectID));
    }

    /**
     * @param workflowProjectID
     * @return the workflow project for the given id or an empty optional if doesn't exist
     */
    public static Optional<WorkflowProject> getWorkflowProject(final UUID workflowProjectID) {
        return Optional.ofNullable(WORKFLOW_PROJECT_MAP.get(workflowProjectID));
    }

    /**
     * Opens and caches the workflow with the given workflow project ID. If the workflow has already been opened, it
     * will be returned instead.
     *
     * @param workflowProjectID
     * @return the opened workflow or an empty optional if there is no workflow project with the given id
     */
    public static Optional<WorkflowManager> openAndCacheWorkflow(final UUID workflowProjectID) {
        WorkflowManager iwfm = getCachedWorkflow(workflowProjectID).orElse(null);
        if (iwfm == null) {
            WorkflowProject wp = getWorkflowProject(workflowProjectID).orElse(null);
            if (wp == null) {
                return Optional.empty();
            }
            iwfm = wp.openProject();
            cacheWorkflow(workflowProjectID, iwfm);
        }
        return Optional.of(iwfm);
    }

    /**
     * Callback when a workflow project with a certain UUID has been removed.
     *
     * @param listener the listener to be called
     */
    public static void addWorkflowProjectRemovedListener(final Consumer<UUID> listener) {
        WORKFLOW_REMOVED_LISTENERS.add(listener);
    }

    /**
     * @param workflowProjectID
     * @return the cached workflow or an empty optional if none has been found for the given workflow project ID.
     */
    private static Optional<WorkflowManager> getCachedWorkflow(final UUID workflowProjectID) {
        return Optional.ofNullable(CACHED_WORKFLOWS_MAP.get(workflowProjectID));
    }

    /**
     * Caches the given workflow manager and therewith makes it available to other plugins etc. (e.g. the
     * ConnectionContainerEditPart-class in the org.knime.workbench.editor-plugin)
     *
     * @param wfm
     */
    private static void cacheWorkflow(final UUID workflowProjectID, final WorkflowManager wfm) {
        CACHED_WORKFLOWS_MAP.put(workflowProjectID, wfm);
    }
}

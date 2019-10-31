/* ------------------------------------------------------------------
 * This source code, its documentation and all appendant files
 * are protected by copyright law. All rights reserved.
 *
 * Copyright by KNIME AG, Zurich, Switzerland
 *
 * You may not modify, publish, transmit, transfer or sell, reproduce,
 * create derivative works from, distribute, perform, display, or in
 * any way exploit any of the content, in whole or in part, except as
 * otherwise expressly permitted in writing by the copyright owner or
 * as specified in the license file distributed with this product.
 *
 * If you have any questions please contact the copyright holder:
 * website: www.knime.com
 * email: contact@knime.com
 * ---------------------------------------------------------------------
 *
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
     * @param workflowProjectID id of the project to be added
     * @param project the actual workflow project to be added
     */
    public static void addWorkflowProject(final UUID workflowProjectID, final WorkflowProject project) {
        WORKFLOW_PROJECT_MAP.put(workflowProjectID, project);
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

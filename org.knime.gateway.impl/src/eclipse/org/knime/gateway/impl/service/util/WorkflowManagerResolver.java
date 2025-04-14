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
 *   Mar 28, 2025 (kai): created
 */
package org.knime.gateway.impl.service.util;

import java.util.NoSuchElementException;

import org.knime.core.node.workflow.NodeContainer;
import org.knime.core.node.workflow.SubNodeContainer;
import org.knime.core.node.workflow.WorkflowManager;
import org.knime.gateway.api.entity.NodeIDEnt;
import org.knime.gateway.api.util.VersionId;
import org.knime.gateway.impl.project.ProjectManager;

/**
 * Helps to resolve and retrieve the correct {@link WorkflowManager} instance.
 *
 * @author Kai Franze, KNIME GmbH, Germany
 */
public final class WorkflowManagerResolver {

    private WorkflowManagerResolver() {
        // Utility class
    }

    /**
     * Loads the {@link WorkflowManager} for the given project id.
     *
     * @param projectId the root workflow id
     * @return The {@link WorkflowManager}-instance
     * @throws NoSuchElementException
     */
    public static WorkflowManager load(final String projectId) {
        return load(projectId, VersionId.currentState());
    }

    /**
     * Loads the {@link WorkflowManager} for the given project id and version id
     *
     * @param projectId the root workflow id
     * @param versionId the version id
     * @return The {@link WorkflowManager}-instance
     * @throws NoSuchElementException
     */
    public static WorkflowManager load(final String projectId, final VersionId versionId) {
        return load(projectId, NodeIDEnt.getRootID(), versionId);
    }

    /**
     * Loads the {@link WorkflowManager} for the given project id and workflow id.
     *
     * @param projectId the root workflow id
     * @param workflowId the subnode's or metanode's node id. May be {@link NodeIDEnt#getRootID()}
     * @return The {@link WorkflowManager}-instance
     * @throws NoSuchElementException
     */
    public static WorkflowManager load(final String projectId, final NodeIDEnt workflowId) {
        return load(projectId, workflowId, VersionId.currentState());
    }

    /**
     * Loads the {@link WorkflowManager} for the given project id and workflow id and version id.
     *
     * @param projectId the root workflow id
     * @param workflowId the subnode's or metanode's node id. May be {@link NodeIDEnt#getRootID()}
     * @param versionId the version id
     * @return The {@link WorkflowManager}-instance
     * @throws NoSuchElementException
     */
    public static WorkflowManager load(final String projectId, final NodeIDEnt workflowId, final VersionId versionId) {
        return parseWfm(findNodeContainer(loadProjectWfm(projectId, versionId), workflowId));
    }

    /**
     * Retrieves the {@link WorkflowManager} for the given project id.
     *
     * @param projectId the root workflow id
     * @return The {@link WorkflowManager}-instance
     * @throws NoSuchElementException
     */
    public static WorkflowManager get(final String projectId) {
        return get(projectId, VersionId.currentState());
    }

    /**
     * Retrieves the {@link WorkflowManager} for the given project id and version id.
     *
     * @param projectId the root workflow id
     * @param versionId the version id
     * @return The {@link WorkflowManager}-instance
     * @throws NoSuchElementException
     */
    public static WorkflowManager get(final String projectId, final VersionId versionId) {
        return get(projectId, NodeIDEnt.getRootID(), versionId);
    }

    /**
     * Gets the (sub-)workflow manager for the given root workflow id and node id.
     *
     * @param projectId the root workflow id
     * @param workflowId the subnode's or metanode's node id. May be {@link NodeIDEnt#getRootID()}
     * @return the {@link WorkflowManager}-instance
     * @throws NoSuchElementException if there is no root workflow for the given root workflow id
     * @throws IllegalArgumentException if there is no node for the given node id
     * @throws IllegalStateException if the given node id doesn't reference a sub workflow (i.e. component or metanode)
     *             or the workflow is encrypted
     */
    public static WorkflowManager get(final String projectId, final NodeIDEnt workflowId) {
        return get(projectId, workflowId, VersionId.currentState());
    }

    /**
     * Retrieves the (sub-)workflow manager for the given root workflow id and node id and version id.
     *
     * @param projectId the root workflow id
     * @param workflowId the subnode's or metanode's node id. May be {@link NodeIDEnt#getRootID()}
     * @param versionId the version id
     * @return The {@link WorkflowManager}-instance
     */
    public static WorkflowManager get(final String projectId, final NodeIDEnt workflowId,
        final VersionId versionId) {
        return parseWfm(findNodeContainer(getProjectWfm(projectId, versionId), workflowId));
    }

    static NodeContainer findNodeContainer(final WorkflowManager parent, final NodeIDEnt child) {
        if (child.equals(NodeIDEnt.getRootID())) {
            return parent;
        }
        return parent.findNodeContainer(child.toNodeID(parent));
    }

    static WorkflowManager parseWfm(final NodeContainer nc) {
        WorkflowManager wfm;
        if (nc instanceof SubNodeContainer subNodeContainer) {
            wfm = subNodeContainer.getWorkflowManager();
        } else if (nc instanceof WorkflowManager metanodeWfm) {
            wfm = metanodeWfm;
        } else {
            throw new IllegalStateException("The node id '" + nc.getID() + "' doesn't reference a sub workflow.");
        }
        if (wfm.isEncrypted() && !wfm.isUnlocked()) {
            throw new IllegalStateException("Workflow is locked and cannot be accessed.");
        }
        return wfm;
    }

    static WorkflowManager loadProjectWfm(final String projectId, final VersionId versionId) {
        return ProjectManager.getInstance().getProject(projectId)
            .orElseThrow(() -> new NoSuchElementException("Project for ID \"" + projectId + "\" not found."))
            .getFromCacheOrLoadWorkflowManager(versionId) //
            .orElseThrow(() -> new NoSuchElementException("Workflow not found."));
    }

    static WorkflowManager getProjectWfm(final String projectId) {
        return getProjectWfm(projectId, VersionId.currentState());
    }

    static WorkflowManager getProjectWfm(final String projectId, final VersionId versionId) {
        return ProjectManager.getInstance().getProject(projectId)
            .orElseThrow(() -> new NoSuchElementException("Project for ID \"" + projectId + "\" not found."))
            .getFromCacheOrLoadWorkflowManager(versionId) //
            .orElseThrow(() -> new NoSuchElementException("Workflow not found."));
    }

}

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
 *   Jan 20, 2021 (hornm): created
 */
package org.knime.gateway.impl.webui;

import org.knime.core.node.util.CheckUtils;
import org.knime.gateway.api.entity.NodeIDEnt;
import org.knime.gateway.api.util.VersionId;

/**
 * Uniquely identifies a workflow.
 * 
 * @param projectId The ID of the project the workflow is associated with. See
 *            {@link org.knime.gateway.impl.project.ProjectManager}.
 * @param workflowId The ID of a node within the project's root workflow. That ID may correspond to a container node
 *            holding a sub-workflow. If {@link NodeIDEnt#getRootID()}, this points to the root workflow.
 * @param version The identifier of a version.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
public record WorkflowKey(String projectId, NodeIDEnt workflowId, VersionId version) {

    public WorkflowKey(final String projectId, final NodeIDEnt workflowId, final VersionId version) {
        this.projectId = CheckUtils.checkArgumentNotNull(projectId);
        this.workflowId = CheckUtils.checkArgumentNotNull(workflowId);
        this.version = CheckUtils.checkArgumentNotNull(version);
    }

    /**
     * @see WorkflowKey
     */
    public WorkflowKey(final String projectId, final NodeIDEnt workflowId) {
        this(projectId, workflowId, VersionId.currentState());
    }

    /**
     * @see WorkflowKey
     */
    public WorkflowKey(final String projectId, final String nodeId) {
        this(projectId, new NodeIDEnt(nodeId));
    }

    /**
     * @see WorkflowKey
     */
    public WorkflowKey(final String projectId) {
        this(projectId, NodeIDEnt.getRootID());
    }

    /**
     * @return the workflow project id
     */
    public String getProjectId() {
        return projectId;
    }

    /**
     * @return the node-id of the sub-workflow (component or metanode) or 'root' if it refers to the top-level workflow
     */
    public NodeIDEnt getWorkflowId() {
        return workflowId;
    }

    /**
     * @return the ID of the version
     */
    public VersionId getVersionId() {
        return version;
    }

}

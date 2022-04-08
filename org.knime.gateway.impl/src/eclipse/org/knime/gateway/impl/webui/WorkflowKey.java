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

import java.util.Objects;

import org.knime.core.node.workflow.NodeID;
import org.knime.gateway.api.entity.NodeIDEnt;

/**
 * Uniquely identifies a workflow by its project-id and the node-id in case its a sub-workflow (the node-id is 'root' if
 * it's the top-level workflow).
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
public final class WorkflowKey {

    private final String m_projectId;

    private final NodeIDEnt m_workfowId;

    /**
     * Creates a new key instance.
     *
     * @param projectId the workflow project id
     * @param workflowId the node-id of the sub-workflow (component or metanode) or 'root' if it refers to the top-level
     *            workflow
     */
    public WorkflowKey(final String projectId, final NodeIDEnt workflowId) {
        m_projectId = projectId;
        m_workfowId = workflowId;
    }

    public WorkflowKey(final String projectId, final NodeID workflowId) {
        this(projectId, new NodeIDEnt(workflowId));
    }

    /**
     * @return the workflow project id
     */
    public String getProjectId() {
        return m_projectId;
    }

    /**
     * @return the node-id of the sub-workflow (component or metanode) or 'root' if it refers to the top-level workflow
     */
    public NodeIDEnt getWorkflowId() {
        return m_workfowId;
    }

    @Override
    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        }
        if (o == null) {
            return false;
        }
        if (this.getClass() == o.getClass()) {
            WorkflowKey k = (WorkflowKey)o;
            return Objects.equals(m_projectId, k.m_projectId) && Objects.equals(m_workfowId, k.m_workfowId);
        }
        return false;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + m_projectId.hashCode();
        result = prime * result + m_workfowId.hashCode();
        return result;
    }

}
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
 *   Jun 26, 2025 (hornm): created
 */
package org.knime.gateway.impl.project;

import org.knime.core.node.workflow.WorkflowManager;
import org.knime.core.node.workflow.virtual.parchunk.FlowVirtualScopeContext;
import org.knime.gateway.impl.project.ProjectManager.ProjectConsumerType;

/**
 * Allows to keep track of 'virtual projects'. A virtual project only exist for a temporary period of time and is never
 * persisted, such as a project used for tool-execution for an 'agent'-node. See also {@link FlowVirtualScopeContext}.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 * @since 5.6
 */
public final class VirtualWorkflowProjects {

    private VirtualWorkflowProjects() {
        // utility class
    }

    /**
     * Registers a workflow manager as virtual project. As a result, the gateway API can be used to access the workflow
     * manager (i.e. in order to access a node view and it's data services).
     *
     * @param wfm the workflow manager to register
     * @return the new project ID
     */
    public static String registerProject(final WorkflowManager wfm) {
        var proj = //
            Project.builder() //
                .setWfm(wfm) //
                .build();
        ProjectManager.getInstance().addProject(proj, ProjectConsumerType.VIRTUAL_WORKFLOW, false);
        return proj.getID();
    }

    /**
     * Removes the project for the given id and disposes the workflow manager associated with it.
     *
     * @param id the id of the project to remove
     */
    public static void removeProject(final String id) {
        ProjectManager.getInstance().removeProject(id, ProjectConsumerType.VIRTUAL_WORKFLOW);
    }

}

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
 *   Aug 3, 2020 (hornm): created
 */
package org.knime.gateway.testing.helper;

import java.io.File;
import java.net.URI;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.knime.core.node.ExecutionMonitor;
import org.knime.core.node.workflow.SubNodeContainer;
import org.knime.core.node.workflow.TemplateNodeContainerPersistor;
import org.knime.core.node.workflow.WorkflowLoadHelper;
import org.knime.core.node.workflow.WorkflowManager;
import org.knime.core.node.workflow.WorkflowPersistor.MetaNodeLinkUpdateResult;
import org.knime.core.node.workflow.contextv2.WorkflowContextV2;
import org.knime.gateway.api.util.CoreUtil;
import org.knime.gateway.api.webui.entity.SpaceItemReferenceEnt.ProjectTypeEnum;
import org.knime.gateway.impl.project.DefaultProject;
import org.knime.gateway.impl.project.Project;
import org.knime.gateway.impl.project.ProjectManager;
import org.knime.testing.util.WorkflowManagerUtil;

/**
 * Loads a workflow locally.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
public class LocalWorkflowLoader implements WorkflowLoader {

    private final Set<String> m_loadedWorkflows = new HashSet<>();

    /**
     * {@inheritDoc}
     */
    @Override
    public String loadWorkflow(final TestWorkflow workflow) throws Exception {
        final String projectId = UUID.randomUUID().toString();
        loadWorkflow(workflow, projectId);
        return projectId;
    }

    /**
     * Same as {@link #loadWorkflow(TestWorkflow)} with the additional possibility to set a custom project id (instead
     * of a randomly generated one).
     *
     * @param workflow
     * @param projectId
     * @throws Exception
     */
    public void loadWorkflow(final TestWorkflow workflow, final String projectId) throws Exception {
        WorkflowManager wfm = WorkflowManagerUtil.loadWorkflow(workflow.getWorkflowDir());
        addToProjectManager(wfm, workflow.getName(), projectId);
    }

    private void addToProjectManager(final WorkflowManager wfm, final String name, final String projectId) {
        wfm.setName(name);
        // wfm.setName marks the workflow dirty
        wfm.getNodeContainerDirectory().setDirty(false);
        ProjectManager.getInstance()
            .addProject(DefaultProject.builder(wfm).setId(projectId).setOrigin(createOriginForTesting()).build());
        m_loadedWorkflows.add(projectId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String loadComponent(final TestWorkflow component) throws Exception {
        String projectId = UUID.randomUUID().toString();
        loadComponent(component, projectId);
        return projectId;
    }

    /**
     * Same as {@link #loadComponent(TestWorkflow)} with the additional possibility to set a custom project id (instead
     * of a randomly generated one).
     *
     * @param component
     * @param projectId
     * @throws Exception
     */
    public void loadComponent(final TestWorkflow component, final String projectId) throws Exception {
        URI componentURI = component.getWorkflowDir().toURI();
        WorkflowLoadHelper loadHelper = new WorkflowLoadHelper(true, true,
            WorkflowContextV2.forTemporaryWorkflow(new File("").toPath(), null));
        TemplateNodeContainerPersistor loadPersistor =
            loadHelper.createTemplateLoadPersistor(component.getWorkflowDir(), componentURI);
        MetaNodeLinkUpdateResult loadResult =
            new MetaNodeLinkUpdateResult("Shared instance from \"" + componentURI + "\"");
        WorkflowManager.ROOT.load(loadPersistor, loadResult, new ExecutionMonitor(), false);
        SubNodeContainer snc = (SubNodeContainer)loadResult.getLoadedInstance();
        addToProjectManager(snc.getWorkflowManager(), component.getName(), projectId);
    }

    /**
     * Disposes all loaded workflows.
     *
     * @throws InterruptedException
     */
    public void disposeWorkflows() throws InterruptedException {
        for (String projectId : m_loadedWorkflows) {
            WorkflowManager wfm = ProjectManager.getInstance().openAndCacheProject(projectId).orElse(null);
            if (wfm != null) {
                CoreUtil.cancelAndCloseLoadedWorkflow(wfm);
            }
            ProjectManager.getInstance().removeProject(projectId, w -> {
                //
            });
        }
    }

    private static Project.Origin createOriginForTesting() {
        return new Project.Origin() {
            @Override
            public String getProviderId() {
                return "Provider ID for testing";
            }

            @Override
            public String getSpaceId() {
                return "Space ID for testing";
            }

            @Override
            public String getItemId() {
                return "Item ID for testing";
            }

            @Override
            public ProjectTypeEnum getProjectType() {
                return ProjectTypeEnum.WORKFLOW;
            }
        };
    }
}

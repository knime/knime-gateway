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
 *   Oct 4, 2023 (hornm): created
 */
package org.knime.gateway.impl.webui;

import static org.awaitility.Awaitility.await;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

import org.junit.BeforeClass;
import org.junit.Test;
import org.knime.core.node.extension.InvalidNodeFactoryExtensionException;
import org.knime.core.node.workflow.NodeID;
import org.knime.core.node.workflow.WorkflowAnnotationID;
import org.knime.core.node.workflow.WorkflowManager;
import org.knime.gateway.api.entity.NodeIDEnt;
import org.knime.gateway.api.webui.entity.WorkflowInfoEnt.ContainerTypeEnum;
import org.knime.gateway.api.webui.util.WorkflowBuildContext;
import org.knime.gateway.impl.project.Project;
import org.knime.gateway.impl.project.ProjectManager;
import org.knime.js.core.JSCorePlugin;
import org.knime.testing.util.WorkflowManagerUtil;

/**
 * Tests for {@link WorkflowMiddleware}.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
public class WorkflowMiddlewareTest {

    /**
     * Makes sure the org.knime.js.core plugin is activated which in turn registers the
     * DefaultConfigurationLayoutCreator osgi-service registered which in turn is required to create the component
     * description which is used by tests (see SubNodeContainer#getDialogDescriptions and
     * ConfigurationLayoutUtil#getConfigurationOrder)
     */
    @BeforeClass
    public static void activateJsCore() {
        JSCorePlugin.class.getName();
    }

    /**
     * Tests that the {@link WorkflowMiddleware}s workflow state cache is properly cleared on sub-workflow removal
     * (metanode and components).
     *
     * @throws Exception
     */
    @Test
    public void testWorkflowStateCacheCleanUpOnSubWorkflowRemoval() throws Exception {
        var wfm = WorkflowManagerUtil.createEmptyWorkflow();
        var projectManager = ProjectManager.getInstance();
        var projectId = wfm.getNameWithID();
        projectManager.addProject(
            Project.builder().setWfm(wfm).setId(projectId).build());
        var middleware = new WorkflowMiddleware(projectManager, null);
        createWorkflowSnapshotEnts(projectId, middleware, ContainerTypeEnum.PROJECT, wfm.getID());

        var metanodeIDs = createNestedMetanodesOrComponents(wfm, false);
        createWorkflowSnapshotEnts(projectId, middleware, ContainerTypeEnum.METANODE, metanodeIDs);
        assertThat(middleware.m_workflowStateCache.size(), is(3));

        wfm.removeNode(metanodeIDs[0]);
        await().untilAsserted(() -> assertThat(middleware.m_workflowStateCache.size(), is(1)));

        var componentIDs = createNestedMetanodesOrComponents(wfm, true);
        createWorkflowSnapshotEnts(projectId, middleware, ContainerTypeEnum.COMPONENT, componentIDs);
        assertThat(middleware.m_workflowStateCache.size(), is(3));

        wfm.removeNode(componentIDs[0]);
        await().untilAsserted(() -> assertThat(middleware.m_workflowStateCache.size(), is(1)));

        ProjectManager.getInstance().removeProject(projectId);
    }

    private static NodeID[] createNestedMetanodesOrComponents(final WorkflowManager wfm, final boolean createComponents)
        throws InstantiationException, IllegalAccessException, InvalidNodeFactoryExtensionException {
        var nodeId = WorkflowManagerUtil.createAndAddNode(wfm,
            org.knime.core.node.extension.NodeFactoryProvider.getInstance() //
                .getNodeFactory("org.knime.base.node.preproc.filter.column.DataColumnSpecFilterNodeFactory")
                .orElseThrow())
            .getID();
        var metanodeID = wfm.collapseIntoMetaNode(new NodeID[]{nodeId}, new WorkflowAnnotationID[0], "Metanode")
            .getCollapsedMetanodeID();
        var metanode = (WorkflowManager)wfm.getNodeContainer(metanodeID);
        var nestedMetanodeID =
            metanode.collapseIntoMetaNode(new NodeID[]{metanode.getNodeContainers().iterator().next().getID()},
                new WorkflowAnnotationID[0], "Nested Metanode").getCollapsedMetanodeID();
        if (createComponents) {
            var nestedComponentID = metanode.convertMetaNodeToSubNode(nestedMetanodeID).getConvertedNodeID();
            var componentID = wfm.convertMetaNodeToSubNode(metanodeID).getConvertedNodeID();
            return new NodeID[]{componentID, nestedComponentID};
        } else {
            return new NodeID[]{metanodeID, nestedMetanodeID};
        }
    }

    private static void createWorkflowSnapshotEnts(final String projectId, final WorkflowMiddleware middleware,
        final ContainerTypeEnum expectedContainerType, final NodeID... nodeIds) {
        for (var nodeId : nodeIds) {
            var snapshot = middleware.buildWorkflowSnapshotEnt(new WorkflowKey(projectId, new NodeIDEnt(nodeId)),
                WorkflowBuildContext::builder);
            assertThat(snapshot.getWorkflow().getInfo().getContainerType(), is(expectedContainerType));
        }
    }

}

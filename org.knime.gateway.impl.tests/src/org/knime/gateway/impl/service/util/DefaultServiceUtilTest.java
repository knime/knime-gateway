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
 *   Sep 18, 2020 (hornm): created
 */
package org.knime.gateway.impl.service.util;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertTrue;
import static org.knime.gateway.api.entity.NodeIDEnt.getRootID;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.clearInvocations;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.Optional;
import java.util.UUID;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.knime.core.node.workflow.NodeContainer;
import org.knime.core.node.workflow.NodeID;
import org.knime.core.node.workflow.WorkflowManager;
import org.knime.gateway.api.entity.NodeIDEnt;
import org.knime.gateway.impl.project.DefaultProject;
import org.knime.gateway.impl.project.ProjectManager;
import org.knime.testing.util.WorkflowManagerUtil;
import org.mockito.Mockito;

/**
 * Tests methods in {@link DefaultServiceUtil}.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
public class DefaultServiceUtilTest {

    private WorkflowManager m_wfm;

    private WorkflowManager m_rootWfm;

    private final ProjectManager m_wpm = ProjectManager.getInstance();

    private NodeID m_nodeID;

    private String m_wfId;

    private NodeContainer m_nc;

    /**
     * Initializes the mocks.
     */
    @Before
    public void createMocks() {
        m_wfm = createWorkflowManagerMock();
        m_nc = mock(NodeContainer.class);

        m_nodeID = NodeID.fromString("0:1");
        doReturn(new NodeID(0)).when(m_wfm).getID();
        doReturn(m_wfm).when(m_nc).getParent();
        doReturn(m_nc).when(m_wfm).findNodeContainer(eq(m_nodeID));
        m_rootWfm = createWorkflowManagerMock();
        doReturn(m_rootWfm).when(m_wfm).getParent();
        doReturn(Optional.empty()).when(m_wfm).getProjectComponent();
        doReturn(m_rootWfm).when(m_wfm).getProjectWFM();
        doReturn(new NodeID(0)).when(m_rootWfm).getID();
        doReturn(m_rootWfm).when(m_wfm).getDirectNCParent();

        m_wfId = addWorkflowProject(m_wfm);
    }

    /**
     * Tests {@link DefaultServiceUtil#changeNodeStates(String, NodeIDEnt, String, NodeIDEnt...)}
     */
    @Test
    public void testChangeNodeStates() {

        // action for a node
        NodeIDEnt nodeIDEnt = new NodeIDEnt(1);
        DefaultServiceUtil.changeNodeStates(m_wfId, getRootID(), "execute", nodeIDEnt);
        verify(m_wfm).executeUpToHere(m_nodeID);
        DefaultServiceUtil.changeNodeStates(m_wfId, getRootID(), "reset", nodeIDEnt);
        verify(m_wfm).resetAndConfigureNode(m_nodeID);
        DefaultServiceUtil.changeNodeStates(m_wfId, getRootID(), "cancel", nodeIDEnt);
        verify(m_wfm).cancelExecution(any());
        verify(m_wfm, never()).findNodeContainer(m_nodeID);
        verify(m_nc, never()).getParent();
        verify(m_wfm, never()).getParent();

        // action for a node within a workflow
        WorkflowManager subWfm = createWorkflowManagerMock();
        doReturn(subWfm).when(m_wfm).findNodeContainer(eq(NodeID.fromString("0:2")));
        doReturn(m_wfm).when(subWfm).getProjectWFM();
        doReturn(Optional.empty()).when(subWfm).getProjectComponent();
        doReturn(m_nc).when(subWfm).findNodeContainer(eq(m_nodeID));
        DefaultServiceUtil.changeNodeStates(m_wfId, new NodeIDEnt(2), "execute", nodeIDEnt);
        verify(subWfm).executeUpToHere(m_nodeID);
        verify(m_wfm, times(1)).findNodeContainer(NodeID.fromString("0:2"));

        // no action given
        clearInvocations(m_wfm);
        DefaultServiceUtil.changeNodeStates(m_wfId, getRootID(), "", nodeIDEnt);
        verify(m_wfm, never()).findNodeContainer(m_nodeID);
        verify(m_wfm, never()).executeUpToHere(m_nodeID);
        verify(m_wfm, never()).resetAndConfigureNode(m_nodeID);
        verify(m_wfm, never()).cancelExecution(any());

        // execute the root workflow
        clearInvocations(m_wfm, m_nc);
        DefaultServiceUtil.changeNodeStates(m_wfId, getRootID(), "execute");
        verify(m_wfm, never()).executeUpToHere(eq(new NodeID(0)));
        verify(m_wfm).getParent();
        verify(m_wfm, never()).findNodeContainer(any());
        verify(m_rootWfm).executeUpToHere(m_wfm.getID());

        // check exception if action is nonsense
        Assert.assertThrows("exception expected", IllegalStateException.class,
            () -> DefaultServiceUtil.changeNodeStates(m_wfId, getRootID(), "blub", new NodeIDEnt(1)));

        // check exception if node id's don't refer to nodes on the same workflow level
        Exception ex =
            Assert.assertThrows("exception expected", IllegalArgumentException.class, () -> DefaultServiceUtil
                .changeNodeStates(m_wfId, getRootID(), "execute", new NodeIDEnt(1, 2), new NodeIDEnt(1)));
        assertThat("unexpected exception message", ex.getMessage(), is("Node ids don't have the same prefix."));

    }

    /**
     * Tests {@link DefaultServiceUtil#changeNodeState(String, NodeIDEnt, String)}.
     */
    public void testChangeNodeState() {
        // action for a node
        NodeIDEnt nodeIDEnt = new NodeIDEnt(1);
        NodeContainer nc = DefaultServiceUtil.changeNodeState(m_wfId, nodeIDEnt, "execute");
        assertTrue(nc == m_nc);
        verify(m_wfm).executeUpToHere(m_nodeID);
        DefaultServiceUtil.changeNodeStates(m_wfId, getRootID(), "reset", nodeIDEnt);
        verify(m_wfm).resetAndConfigureNode(m_nodeID);
        DefaultServiceUtil.changeNodeStates(m_wfId, getRootID(), "cancel", nodeIDEnt);
        verify(m_wfm).cancelExecution(any());
        verify(m_wfm, times(3)).findNodeContainer(m_nodeID);
        verify(m_nc, times(3)).getParent();
        verify(m_wfm, times(3)).getID();
        verify(m_wfm, never()).getParent();

        // no action given
        clearInvocations(m_wfm);
        nc = DefaultServiceUtil.changeNodeState(m_wfId, nodeIDEnt, "");
        assertTrue(nc == m_nc);
        verify(m_wfm).findNodeContainer(m_nodeID);
        verify(m_wfm, never()).executeUpToHere(m_nodeID);
        verify(m_wfm, never()).resetAndConfigureNode(m_nodeID);
        verify(m_wfm, never()).cancelExecution(any());

        // execute the root workflow
        clearInvocations(m_wfm, m_nc);
        nc = DefaultServiceUtil.changeNodeState(m_wfId, getRootID(), "execute");
        assertTrue(nc == m_wfm);
        verify(m_wfm, never()).executeUpToHere(eq(new NodeID(0)));
        verify(m_wfm, never()).getParent();
        verify(m_wfm, never()).findNodeContainer(any());
        verify(m_wfm).getID();
        verify(m_wfm).executeAll();
    }

    /**
     * Remove workflow projects.
     */
    @After
    public void removeWorkflowProjects() {
        m_wpm.getProjectIds().stream().forEach(id -> m_wpm.removeProject(id, WorkflowManagerUtil::disposeWorkflow));
    }

    /*
     * Mocks the WorkflowManager (which is final, thus a bit more elaborate).
     */
    private WorkflowManager createWorkflowManagerMock() {
        final ClassLoader loader = Thread.currentThread().getContextClassLoader();
        Thread.currentThread()
            .setContextClassLoader(new ClassLoaderFinalClassMock(loader, getClass().getClassLoader()));
        WorkflowManager wfm = Mockito.mock(WorkflowManager.class);
        Thread.currentThread().setContextClassLoader(loader);
        return wfm;
    }

    private static String addWorkflowProject(final WorkflowManager wfm) {
        String wfId = UUID.randomUUID().toString();
        ProjectManager.getInstance().addProject(DefaultProject.builder(wfm).setId(wfId).build());
        return wfId;
    }

}

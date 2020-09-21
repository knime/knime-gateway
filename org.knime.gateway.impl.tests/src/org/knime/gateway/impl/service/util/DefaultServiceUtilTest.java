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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.clearInvocations;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.Assert;
import org.junit.Test;
import org.knime.core.node.workflow.NodeContainer;
import org.knime.core.node.workflow.NodeID;
import org.knime.core.node.workflow.WorkflowManager;
import org.knime.gateway.api.entity.NodeIDEnt;
import org.mockito.Mockito;

/**
 * Tests methods in {@link DefaultServiceUtil}.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
public class DefaultServiceUtilTest {

    /**
     * Tests
     * {@link DefaultServiceUtil#changeNodeStates(WorkflowManager, String, NodeIDEnt...)}
     */
    @Test
    public void testChangeNodeState() {
        WorkflowManager wfm = createWorkflowManagerMock();
        WorkflowManager parent = createWorkflowManagerMock();
        NodeContainer nc = mock(NodeContainer.class);

        NodeID nodeID = NodeID.fromString("0:1");
        doReturn(new NodeID(0)).when(wfm).getID();
        doReturn(parent).when(wfm).getParent();
        doReturn(parent).when(nc).getParent();
        doReturn(nc).when(wfm).findNodeContainer(eq(nodeID));

        // action for a node
        NodeIDEnt nodeIDEnt = new NodeIDEnt(1);
        NodeID resNodeID = DefaultServiceUtil.changeNodeStates(wfm, "execute", nodeIDEnt)[0];
        assertThat(resNodeID, is(nodeID));
        verify(parent).executeUpToHere(nodeID);
        DefaultServiceUtil.changeNodeStates(wfm, "reset", nodeIDEnt);
        verify(parent).resetAndConfigureNode(nodeID);
        DefaultServiceUtil.changeNodeStates(wfm, "cancel", nodeIDEnt);
        verify(parent).cancelExecution(any());
        verify(wfm, times(3)).findNodeContainer(nodeID);
        verify(nc, times(3)).getParent();
        verify(wfm, times(3)).getID();
        verify(wfm, never()).getParent();

        // no action given
        clearInvocations(wfm, parent);
        DefaultServiceUtil.changeNodeStates(wfm, "", nodeIDEnt);
        verify(wfm).findNodeContainer(nodeID);
        verify(parent, never()).executeUpToHere(nodeID);
        verify(parent, never()).resetAndConfigureNode(nodeID);
        verify(parent, never()).cancelExecution(any());

        // execute the root workflow
        clearInvocations(wfm, parent, nc);
        nodeIDEnt = NodeIDEnt.getRootID();
        resNodeID = DefaultServiceUtil.changeNodeStates(wfm, "execute", nodeIDEnt)[0];
        assertThat(resNodeID, is(new NodeID(0)));
        verify(parent).executeUpToHere(eq(new NodeID(0)));
        verify(wfm).getParent();
        verify(wfm).getID();
        verify(wfm, never()).findNodeContainer(any());

        // check exception if action is nonsense
        Assert.assertThrows("exception expected", IllegalStateException.class,
            () -> DefaultServiceUtil.changeNodeStates(wfm, "blub", new NodeIDEnt(1)));

        // check exception if node id's don't refere to nodes on the same workflow level
        Assert.assertThrows("exception expected", IllegalArgumentException.class,
            () -> DefaultServiceUtil.changeNodeStates(wfm, "execute", new NodeIDEnt(1, 2), new NodeIDEnt(1)));

        // no ids in no ids out
        assertThat(DefaultServiceUtil.changeNodeStates(wfm, "execute").length, is(0));

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

}

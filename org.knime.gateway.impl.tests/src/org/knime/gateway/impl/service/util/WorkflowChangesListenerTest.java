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
 *   Oct 6, 2023 (hornm): created
 */
package org.knime.gateway.impl.service.util;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.knime.testing.util.WorkflowManagerUtil.createEmptyWorkflow;
import static org.knime.testing.util.WorkflowManagerUtil.disposeWorkflow;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;

import java.io.IOException;
import java.util.Set;
import java.util.function.Predicate;

import org.awaitility.Awaitility;
import org.awaitility.Duration;
import org.junit.Test;
import org.knime.core.node.port.PortType;
import org.knime.core.node.workflow.AnnotationData;
import org.knime.core.node.workflow.NodeMessage;
import org.knime.core.node.workflow.NodeMessage.Type;
import org.knime.core.node.workflow.NodeUIInformation;
import org.knime.core.node.workflow.SubNodeContainer;
import org.knime.core.node.workflow.WorkflowListener;
import org.knime.core.node.workflow.WorkflowManager;
import org.knime.gateway.impl.service.util.WorkflowChangesListener.Scope;
import org.knime.testing.node.SourceNodeTestFactory;
import org.knime.testing.util.WorkflowManagerUtil;
import org.mockito.Mockito;

/**
 * Tests for {@link WorkflowChangesListener}.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
public class WorkflowChangesListenerTest {

    /**
     * Tests that the workflow changes listener is 'listening' (i.e. listeners are registered with the underlying
     * workflow manager etc.) depending on whether callbacks are registered etc.
     *
     * @throws IOException
     */
    @Test
    public void testIsListening() throws IOException {
        var wfm = createEmptyWorkflow();
        var workflowChangesListener = new WorkflowChangesListener(wfm);

        assertThat(workflowChangesListener.m_isListening, is(false));

        Runnable callback = () -> {
        };
        workflowChangesListener.addWorkflowChangeCallback(callback);
        assertThat(workflowChangesListener.m_isListening, is(true));
        Runnable callback2 = () -> {
        };
        workflowChangesListener.addWorkflowChangeCallback(callback2);
        assertThat(workflowChangesListener.m_isListening, is(true));

        workflowChangesListener.removeCallback(callback);
        assertThat(workflowChangesListener.m_isListening, is(true));
        workflowChangesListener.removeCallback(callback2);
        assertThat(workflowChangesListener.m_isListening, is(false));

        var tracker = workflowChangesListener.createWorkflowChangeTracker();
        assertThat(workflowChangesListener.m_isListening, is(true));

        workflowChangesListener.removeWorkflowChangesTracker(tracker);
        assertThat(workflowChangesListener.m_isListening, is(false));

        workflowChangesListener.addWorkflowChangeCallback(callback);
        assertThat(workflowChangesListener.m_isListening, is(true));
        workflowChangesListener.close();
        assertThat(workflowChangesListener.m_isListening, is(false));

        disposeWorkflow(wfm);
    }

    /**
     * Checks that the workflow-changes-listener listens to any workflow modification.
     *
     * @throws IOException
     */
    @Test
    public void testListenToEverything() throws IOException {
        var wfm = createEmptyWorkflow();

        var workflowChangesListener = new WorkflowChangesListener(wfm, Set.of(Scope.EVERYTHING), false);
        var callback = mock(Runnable.class);
        workflowChangesListener.addWorkflowChangeCallback(callback);

        modifyWorkflowAndVerifyCallback(wfm, callback, mod -> true);

        disposeWorkflow(wfm);
    }

    /**
     * Checks that the workflow-changes-listener selectively listens to node-message change events (and related events
     * such as node added, node removed).
     *
     * @throws IOException
     */
    @Test
    public void testListenToNodeMessagesOnly() throws IOException {
        var wfm = createEmptyWorkflow();

        var workflowChangesListener = new WorkflowChangesListener(wfm, Set.of(Scope.NODE_MESSAGES), false);
        var callback = mock(Runnable.class);
        workflowChangesListener.addWorkflowChangeCallback(callback);

        modifyWorkflowAndVerifyCallback(wfm, callback, mod -> {
            return switch (mod) {
                // adding/removing a node is expected to result in a callback (since it could add/remove a node message)
                case ADD_NODE, REMOVE_NODE, UPDATE_NODE_MESSAGE -> true;
                default -> false;
            };
        });

        disposeWorkflow(wfm);
    }

    private static void modifyWorkflowAndVerifyCallback(final WorkflowManager wfm,
        final Runnable callbackMock, final Predicate<Modification> callbackExpected) {
        var annoId = wfm.addWorkflowAnnotation(new AnnotationData(), 0).getID();
        verify(callbackMock, callbackExpected.test(Modification.OTHER));
        wfm.removeAnnotation(annoId);
        verify(callbackMock, callbackExpected.test(Modification.OTHER));
        var nc1 = WorkflowManagerUtil.createAndAddNode(wfm, new SourceNodeTestFactory());
        verify(callbackMock, callbackExpected.test(Modification.ADD_NODE));
        nc1.setNodeMessage(new NodeMessage(Type.ERROR, "blub"));
        verify(callbackMock, callbackExpected.test(Modification.UPDATE_NODE_MESSAGE));
        nc1.setNodeMessage(new NodeMessage(Type.RESET, "blub"));
        verify(callbackMock, callbackExpected.test(Modification.UPDATE_NODE_MESSAGE));
        var nc2 = WorkflowManagerUtil.createAndAddNode(wfm, new SourceNodeTestFactory());
        verify(callbackMock, callbackExpected.test(Modification.ADD_NODE));
        var connId = wfm.addConnection(nc1.getID(), 0, nc2.getID(), 0);
        verify(callbackMock, callbackExpected.test(Modification.OTHER));
        wfm.removeConnection(connId);
        verify(callbackMock, callbackExpected.test(Modification.OTHER));
        nc1.setUIInformation(NodeUIInformation.builder().build());
        verify(callbackMock, callbackExpected.test(Modification.OTHER));
        var nodeAnno = nc1.getNodeAnnotation();
        var annoData = nodeAnno.getData();
        annoData.setText("blub");
        nodeAnno.copyFrom(annoData, false);
        verify(callbackMock, callbackExpected.test(Modification.OTHER));
        var metanode = wfm.createAndAddSubWorkflow(new PortType[0], new PortType[0], "metanode");
        verify(callbackMock, callbackExpected.test(Modification.ADD_NODE));
        metanode.setName("new metanode name");
        verify(callbackMock, callbackExpected.test(Modification.OTHER));
        wfm.removeNode(nc1.getID());
        verify(callbackMock, callbackExpected.test(Modification.REMOVE_NODE));
    }

    private static void verify(final Runnable callbackMock, final boolean callbackExpected) {
        Awaitility.await().atMost(Duration.FIVE_SECONDS)
            .untilAsserted(() -> Mockito.verify(callbackMock, callbackExpected ? atLeast(1) : times(0)).run());
        Mockito.clearInvocations(callbackMock);
    }

    private enum Modification {
            ADD_NODE, REMOVE_NODE, UPDATE_NODE_MESSAGE, OTHER;
    }

    /**
     * Tests that the workflow-changes-listener also listens to event in sub-workflows.
     *
     * @throws IOException
     */
    @Test
    public void testRecursive() throws IOException {
        var wfm = createEmptyWorkflow();
        var workflowListener = mock(WorkflowListener.class);
        wfm.addListener(workflowListener);
        var metanode = wfm.createAndAddSubWorkflow(new PortType[0], new PortType[0], "metanode");
        // add a node already to make sure the metanode-state doesn't change later on
        // (which is propagated to the parent)
        WorkflowManagerUtil.createAndAddNode(metanode, new SourceNodeTestFactory());
        var component = wfm.convertMetaNodeToSubNode(
            wfm.createAndAddSubWorkflow(new PortType[0], new PortType[0], "component").getID());
        var componentWfm =
            wfm.getNodeContainer(component.getConvertedNodeID(), SubNodeContainer.class, false).getWorkflowManager();

        var workflowChangesListenerRecursive = new WorkflowChangesListener(wfm, Set.of(Scope.EVERYTHING), true);
        var workflowChangesListenerNonRecursive = new WorkflowChangesListener(wfm, Set.of(Scope.EVERYTHING), false);
        var callbackRecursive = mock(Runnable.class);
        var callbackNonRecursive = mock(Runnable.class);
        workflowChangesListenerRecursive.addWorkflowChangeCallback(callbackRecursive);
        workflowChangesListenerNonRecursive.addWorkflowChangeCallback(callbackNonRecursive);

        modifyWorkflowAndVerifyCallback(metanode, callbackNonRecursive, mod -> false);
        modifyWorkflowAndVerifyCallback(metanode, callbackRecursive, mod -> true);

        modifyWorkflowAndVerifyCallback(componentWfm, callbackNonRecursive, mod -> false);
        modifyWorkflowAndVerifyCallback(componentWfm, callbackRecursive, mod -> true);

        WorkflowManagerUtil.disposeWorkflow(wfm);
    }

    @Test
    public void testListenToParentOfMetanode() throws IOException {
        var parent = createEmptyWorkflow();
        var childMetanode = parent.createAndAddSubWorkflow(new PortType[0], new PortType[0], "metanode");
        var listener = new WorkflowChangesListener(childMetanode, true);
        var callback = mock(Runnable.class);
        listener.addWorkflowChangeCallback(callback);
        modifyWorkflowAndVerifyCallback(parent, callback, mod -> true);

        WorkflowManagerUtil.disposeWorkflow(parent);
    }


    @Test
    public void testListenToParentOfComponent() throws IOException {
        var parent = createEmptyWorkflow();
        var childComponent = parent.convertMetaNodeToSubNode(
            parent.createAndAddSubWorkflow(new PortType[0], new PortType[0], "component").getID());
        var childComponentWfm =
                parent.getNodeContainer(childComponent.getConvertedNodeID(), SubNodeContainer.class, false).getWorkflowManager();
        var listener = new WorkflowChangesListener(childComponentWfm, true);
        var callback = mock(Runnable.class);
        listener.addWorkflowChangeCallback(callback);
        modifyWorkflowAndVerifyCallback(parent, callback, mod -> true);

        WorkflowManagerUtil.disposeWorkflow(parent);
    }

    @Test
    public void testDoesNotListenToParentByDefault() throws IOException {
        var parent = createEmptyWorkflow();
        var childMetanode = parent.createAndAddSubWorkflow(new PortType[0], new PortType[0], "metanode");
        var listener = new WorkflowChangesListener(childMetanode);
        var callback = mock(Runnable.class);
        listener.addWorkflowChangeCallback(callback);
        modifyWorkflowAndVerifyCallback(parent, callback, mod -> false);

        WorkflowManagerUtil.disposeWorkflow(parent);
    }



}

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
 *   Mar 11, 2023 (hornm): created
 */
package org.knime.gateway.impl.webui.service.commands;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.NoSuchElementException;

import org.knime.core.node.NodeFactory;
import org.knime.core.node.NodeModel;
import org.knime.core.node.port.flowvariable.FlowVariablePortObject;
import org.knime.core.node.workflow.ConnectionContainer;
import org.knime.core.node.workflow.ConnectionUIInformation;
import org.knime.core.node.workflow.NativeNodeContainer;
import org.knime.core.node.workflow.NodeContainer;
import org.knime.core.node.workflow.NodeID;
import org.knime.core.node.workflow.NodeUIInformation;
import org.knime.core.node.workflow.WorkflowCopyContent;
import org.knime.core.node.workflow.WorkflowManager;
import org.knime.gateway.api.util.CoreUtil;
import org.knime.gateway.api.webui.entity.NodeFactoryKeyEnt;
import org.knime.gateway.api.webui.entity.ReplaceNodeCommandEnt;
import org.knime.gateway.api.webui.service.util.ServiceExceptions.ServiceCallException;

/**
 * Workflow command to replace a node.
 *
 * @author Juan Baquero
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
final class ReplaceNode extends AbstractWorkflowCommand {

    private InternalReplaceNodeResult m_result;

    private final ReplaceNodeCommandEnt m_commandEnt;

    ReplaceNode(final ReplaceNodeCommandEnt commandEnt) {
        m_commandEnt = commandEnt;
    }

    @Override
    protected boolean executeWithWorkflowLockAndContext() throws ServiceCallException {
        var wfm = getWorkflowManager();
        var targetNodeId = m_commandEnt.getTargetNodeId().toNodeID(wfm);

        if (!wfm.canRemoveNode(targetNodeId)) {
            throw new ServiceCallException(
                "Unable to delete the targeted node. Replace operation aborted. Please check for any execution on progress.");
        }

        var nodeFactoryEnt = m_commandEnt.getNodeFactory();
        var replacementNodeEnt = m_commandEnt.getReplacementNodeId();

        if (nodeFactoryEnt == null ^ replacementNodeEnt == null) { // xor
            var targetNodeContainer = wfm.getNodeContainer(targetNodeId);
            if (nodeFactoryEnt != null && targetNodeContainer instanceof NativeNodeContainer) {
                m_result = replaceNativeNodeWithNewNode(targetNodeId, nodeFactoryEnt, wfm);
            } else {
                var replacementNodeId = replacementNodeEnt == null ? null
                    : replacementNodeEnt.toNodeID(wfm);
                m_result = replaceNode(nodeFactoryEnt, replacementNodeId, targetNodeContainer, wfm);
            }
        } else {
            throw new UnsupportedOperationException(
                "Either a node-factory or a replacement-node-id needs to be provided. But never both or none.");
        }
        return true;
    }

    private static InternalReplaceNodeResult replaceNode(final NodeFactoryKeyEnt nodeFactoryEntOrNull,
        final NodeID replacementNodeIdOrNull, final NodeContainer targetNodeContainer, final WorkflowManager wfm)
        throws ServiceCallException {

        final NodeContainer replacementNodeContainer;
        NodeID[] nodesToRestoreOnUndo;
        var targetNodeId = targetNodeContainer.getID();
        if (nodeFactoryEntOrNull != null) {
            try {
                replacementNodeContainer = wfm.getNodeContainer(wfm.addNodeAndApplyContext(
                    CoreUtil.getNodeFactory(nodeFactoryEntOrNull.getClassName(), nodeFactoryEntOrNull.getSettings()),
                    null, -1));
            } catch (NoSuchElementException | IOException ex) {
                throw new ServiceCallException(ex.getMessage(), ex);
            }
            nodesToRestoreOnUndo = new NodeID[]{targetNodeId};
        } else {
            nodesToRestoreOnUndo = new NodeID[]{targetNodeId, replacementNodeIdOrNull};
            replacementNodeContainer = wfm.getNodeContainer(replacementNodeIdOrNull);
        }

        final var previousConnections = new HashSet<ConnectionContainer>();
        previousConnections.addAll(wfm.getIncomingConnectionsFor(targetNodeId));
        previousConnections.addAll(wfm.getOutgoingConnectionsFor(targetNodeId));

        final var previousNodesPersistor =
            wfm.copy(true, WorkflowCopyContent.builder().setNodeIDs(nodesToRestoreOnUndo).build());
        setUIInformation(targetNodeContainer.getUIInformation(), replacementNodeContainer);
        reconnect(targetNodeContainer, replacementNodeContainer, wfm);
        wfm.removeNode(targetNodeId);

        return new InternalReplaceNodeResult() {

            @Override
            public boolean canUndo() {
                return wfm.canRemoveNode(replacementNodeContainer.getID());
            }

            @Override
            public void undo() {
                wfm.removeNode(replacementNodeContainer.getID());
                wfm.paste(previousNodesPersistor);
                for (ConnectionContainer cc : previousConnections) {
                    var newConnection =
                        wfm.addConnection(cc.getSource(), cc.getSourcePort(), cc.getDest(), cc.getDestPort());
                    setConnectionUIInfo(cc, newConnection);
                }
            }

        };
    }

    private static InternalReplaceNodeResult replaceNativeNodeWithNewNode(final NodeID targetNodeId,
        final NodeFactoryKeyEnt nodeFactoryEnt, final WorkflowManager wfm) throws ServiceCallException {
        NodeFactory<NodeModel> nodeFactory;
        try {
            nodeFactory = CoreUtil.getNodeFactory(nodeFactoryEnt.getClassName(), nodeFactoryEnt.getSettings());
        } catch (NoSuchElementException | IOException ex) {
            throw new ServiceCallException(ex.getMessage(), ex);
        }
        var result = wfm.replaceNode(targetNodeId, null, nodeFactory, false, null);
        return new InternalReplaceNodeResult() {

            @Override
            public void undo() {
                result.undo();
            }

            @Override
            public boolean canUndo() {
                return result.canUndo();
            }
        };
    }

    /**
     * Connects new node with connection of the old node.
     *
     * The original implementation of this method comes from ReplaceHelper.java from knime-workbench
     *
     * @param targetNode old node
     * @param replacementNode new node container
     * @param wfm
     */
    private static void reconnect(final NodeContainer targetNode, final NodeContainer replacementNode,
        final WorkflowManager wfm) {

        final var targetNodeId = targetNode.getID();
        var incomingConnections = new ArrayList<>(wfm.getIncomingConnectionsFor(targetNodeId));
        var outgoingConnections = new ArrayList<>(wfm.getOutgoingConnectionsFor(targetNodeId));

        int inShift;
        int outShift;

        if ((targetNode instanceof WorkflowManager) && !(replacementNode instanceof WorkflowManager)) {
            inShift = 0;
            // replacing a metanode (no opt. flow var ports) with a "normal" node (that has optional flow var ports)
            if ((targetNode.getNrInPorts() > 0) && (replacementNode.getNrInPorts() > 1)) {
                // shift ports one index - unless we need to use the invisible optional flow var port of new node
                if (!targetNode.getInPort(0).getPortType().equals(FlowVariablePortObject.TYPE)) {
                    inShift = 1;
                } else if (replacementNode.getInPort(1).getPortType().equals(FlowVariablePortObject.TYPE)) {
                    inShift = 1;
                }
            }

            outShift = 0;
            if ((targetNode.getNrOutPorts() > 0) && (replacementNode.getNrOutPorts() > 1)) {
                if (!targetNode.getOutPort(0).getPortType().equals(FlowVariablePortObject.TYPE)) {
                    outShift = 1;
                } else if (replacementNode.getOutPort(1).getPortType().equals(FlowVariablePortObject.TYPE)) {
                    outShift = 1;
                }
            }
        } else if (!(targetNode instanceof WorkflowManager) && (replacementNode instanceof WorkflowManager)) {
            // replacing a "normal" node with a metanode
            inShift = -1;
            for (final ConnectionContainer cc : incomingConnections) {
                if (cc.getDestPort() == 0) {
                    inShift = 0;
                    break;
                }
            }

            outShift = -1;
            for (final ConnectionContainer cc : outgoingConnections) {
                if (cc.getSourcePort() == 0) {
                    outShift = 0;
                    break;
                }
            }
        } else {
            inShift = 0;
            outShift = 0;
        }

        // set incoming connections
        final NodeID newId = replacementNode.getID();
        for (final ConnectionContainer c : incomingConnections) {
            if (wfm.canAddConnection(c.getSource(), c.getSourcePort(), newId, c.getDestPort() + inShift)) {
                var newConnection =
                    wfm.addConnection(c.getSource(), c.getSourcePort(), newId, c.getDestPort() + inShift);
                setConnectionUIInfo(c, newConnection);
            } else {
                break;
            }
        }

        // set outgoing connections
        for (final ConnectionContainer c : outgoingConnections) {
            if (wfm.canAddConnection(newId, c.getSourcePort() + outShift, c.getDest(), c.getDestPort())) {
                var newConnection =
                    wfm.addConnection(newId, c.getSourcePort() + outShift, c.getDest(), c.getDestPort());
                setConnectionUIInfo(c, newConnection);
            } else {
                break;
            }
        }
    }

    /**
     * Sets the connection ui info including bendpoints
     *
     * @param removedConnection the connection container carrying the information
     * @param newConnection the connection container to add the information to
     */
    private static void setConnectionUIInfo(final ConnectionContainer removedConnection,
        final ConnectionContainer newConnection) {
        if (removedConnection.getUIInfo() != null) {
            ConnectionUIInformation newInfo = ConnectionUIInformation.builder(removedConnection.getUIInfo()).build();
            newConnection.setUIInfo(newInfo);
        }
    }

    /**
     * Copies the UI information from the old to the new container.
     *
     * The original implementation of this method comes from ReplaceHelper.java from knime-workbench
     *
     * @param uiInfo
     * @param replacement the new node container
     */
    private static void setUIInformation(final NodeUIInformation uiInfo, final NodeContainer replacement) {
        final int[] bounds = uiInfo.getBounds();
        final NodeUIInformation info = NodeUIInformation.builder().setNodeLocation(bounds[0], bounds[1], -1, -1)
            .setHasAbsoluteCoordinates(true).setSnapToGrid(uiInfo.getSnapToGrid()).setIsDropLocation(false).build();
        replacement.setUIInformation(info);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean canUndo() {
        return m_result != null && m_result.canUndo();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void undo() throws ServiceCallException {
        if (m_result != null) {
            m_result.undo();
            m_result = null;
        }
    }

    private interface InternalReplaceNodeResult {
        boolean canUndo();

        void undo();
    }
}

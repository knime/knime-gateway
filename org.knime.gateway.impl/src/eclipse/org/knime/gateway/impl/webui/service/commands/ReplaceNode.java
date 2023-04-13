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

import static org.knime.gateway.impl.service.util.DefaultServiceUtil.entityToNodeID;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

import org.knime.core.node.NodeFactory;
import org.knime.core.node.NodeModel;
import org.knime.core.node.context.ModifiableNodeCreationConfiguration;
import org.knime.core.node.port.flowvariable.FlowVariablePortObject;
import org.knime.core.node.workflow.ConnectionContainer;
import org.knime.core.node.workflow.NativeNodeContainer;
import org.knime.core.node.workflow.NodeContainer;
import org.knime.core.node.workflow.NodeID;
import org.knime.core.node.workflow.NodeUIInformation;
import org.knime.core.node.workflow.SubNodeContainer;
import org.knime.core.node.workflow.WorkflowCopyContent;
import org.knime.core.node.workflow.WorkflowManager;
import org.knime.core.node.workflow.WorkflowPersistor;
import org.knime.core.node.workflow.action.ReplaceNodeResult;
import org.knime.core.util.Pair;
import org.knime.gateway.api.entity.NodeIDEnt;
import org.knime.gateway.api.util.CoreUtil;
import org.knime.gateway.api.webui.entity.NodeFactoryKeyEnt;
import org.knime.gateway.api.webui.entity.ReplaceNodeCommandEnt;
import org.knime.gateway.api.webui.service.util.ServiceExceptions.OperationNotAllowedException;

/**
 * Workflow command to replace a node.
 *
 * @author Juan Baquero
 */
final class ReplaceNode extends AbstractWorkflowCommand {

    private ReplaceNodeResult m_result;

    private final ReplaceNodeCommandEnt m_commandEnt;

    private boolean m_isNativeNodeReplace;

    private NodeContainer m_replacementNodeContainer;

    private WorkflowPersistor m_oldStatePersistor;

    private Set<ConnectionContainer> m_oldStateConnections;

    ReplaceNode(final ReplaceNodeCommandEnt commandEnt) {
        m_commandEnt = commandEnt;
    }

    @Override
    protected boolean executeWithLockedWorkflow() throws OperationNotAllowedException {
        var wfm = getWorkflowManager();
        var targetNode = entityToNodeID(getWorkflowKey().getProjectId(), m_commandEnt.getTargetNodeId());

        if (!wfm.canRemoveNode(targetNode)) {
            throw new OperationNotAllowedException(
                "Unable to delete the targeted node. Replace operation aborted. Please check for any execution on progress.");
        }

        var replacementNodeEnt = m_commandEnt.getReplacementNodeId();
        m_replacementNodeContainer = replacementNodeEnt != null
            ? wfm.getNodeContainer(entityToNodeID(getWorkflowKey().getProjectId(), replacementNodeEnt)) : null;
        var targetNodeContainer = wfm.getNodeContainer(targetNode);
        var nodeFactoryEnt = m_commandEnt.getNodeFactory();
        // either node is a SubWorkflow
        if (isSubWorkflow(targetNodeContainer) || isSubWorkflow(m_replacementNodeContainer)) {
            replaceSubWorkflowNode(nodeFactoryEnt, targetNode, targetNodeContainer, wfm);
        } else { // both are native nodes
            m_isNativeNodeReplace = true;
            replaceNativeNode(targetNode, nodeFactoryEnt, replacementNodeEnt, wfm);
        }
        return true;
    }

    private static boolean isSubWorkflow(final NodeContainer nodeContainer) {
        return nodeContainer instanceof SubNodeContainer || nodeContainer instanceof WorkflowManager;
    }

    /**
     * @param nodeFactoryEnt
     * @param targetNode
     * @param targetNodeContainer
     * @param wfm
     * @throws OperationNotAllowedException
     */
    private void replaceSubWorkflowNode(final NodeFactoryKeyEnt nodeFactoryEnt, final NodeID targetNode,
        final NodeContainer targetNodeContainer, final WorkflowManager wfm) throws OperationNotAllowedException {

        var nodesToPersist = m_replacementNodeContainer == null ? List.of(targetNode)
            : List.of(targetNode, m_replacementNodeContainer.getID());
        m_oldStateConnections = wfm.getIncomingConnectionsFor(targetNode);
        m_oldStateConnections.addAll(wfm.getOutgoingConnectionsFor(targetNode));

        if (nodeFactoryEnt != null) { // create replacement
            try {
                m_replacementNodeContainer = wfm.getNodeContainer(wfm.addNodeAndApplyContext(
                    CoreUtil.getNodeFactory(nodeFactoryEnt.getClassName(), nodeFactoryEnt.getSettings()), null, -1));
            } catch (NoSuchElementException | IOException ex) {
                throw new OperationNotAllowedException(ex.getMessage(), ex);
            }
        } else if (m_replacementNodeContainer == null) {
            throw new OperationNotAllowedException("Provide one of nodeId and nodeFactoryId.");
        }

        m_oldStatePersistor = wfm.copy(true, WorkflowCopyContent.builder()
            .setNodeIDs(nodesToPersist.toArray(new NodeID[nodesToPersist.size()])).build());
        setUIInformation(targetNodeContainer.getUIInformation(), m_replacementNodeContainer);
        reconnect(targetNodeContainer, m_replacementNodeContainer, wfm);
        wfm.removeNode(targetNode);
    }

    /**
     * @param targetNode
     * @param nodeFactoryEnt
     * @param replacementNodeEnt
     * @param wfm
     * @throws OperationNotAllowedException
     */
    private void replaceNativeNode(final NodeID targetNode, final NodeFactoryKeyEnt nodeFactoryEnt,
        final NodeIDEnt replacementNodeEnt, final WorkflowManager wfm) throws OperationNotAllowedException {
        var nodeSetup = getNodeSetup(nodeFactoryEnt, replacementNodeEnt, wfm);
        m_result = wfm.replaceNode(targetNode, nodeSetup.getSecond(), nodeSetup.getFirst());
        if (m_replacementNodeContainer != null) { // remove copy if node already exists
            m_oldStatePersistor =
                wfm.copy(true, WorkflowCopyContent.builder().setNodeIDs(m_replacementNodeContainer.getID()).build());
            wfm.removeNode(m_replacementNodeContainer.getID());
        }
    }

    private Pair<NodeFactory<NodeModel>, ModifiableNodeCreationConfiguration> getNodeSetup(
        final NodeFactoryKeyEnt nodeFactoryEnt, final NodeIDEnt replacementNodeEnt, final WorkflowManager wfm)
        throws OperationNotAllowedException {

        if (nodeFactoryEnt != null) {
            try {
                return new Pair<>(CoreUtil.getNodeFactory(nodeFactoryEnt.getClassName(), nodeFactoryEnt.getSettings()),
                    null);
            } catch (NoSuchElementException | IOException ex) {
                throw new OperationNotAllowedException(ex.getMessage(), ex);
            }
        } else if (replacementNodeEnt != null) {
            var replacementNode = entityToNodeID(getWorkflowKey().getProjectId(), m_commandEnt.getReplacementNodeId());
            return new Pair<>(((NativeNodeContainer)wfm.getNodeContainer(replacementNode)).getNode().getFactory(),
                CoreUtil.getCopyOfCreationConfig(wfm, replacementNode).orElse(null));
        } else {
            throw new OperationNotAllowedException(
                "Both replacemenetNodeId and nodeFactoryId are not defined. Provide one of them.");
        }
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
    public static void reconnect(final NodeContainer targetNode, final NodeContainer replacementNode,
        final WorkflowManager wfm) {

        final NodeID targetNodeId = targetNode.getID();
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
                wfm.addConnection(c.getSource(), c.getSourcePort(), newId, c.getDestPort() + inShift);
            } else {
                break;
            }
        }

        // set outgoing connections
        for (final ConnectionContainer c : outgoingConnections) {
            if (wfm.canAddConnection(newId, c.getSourcePort() + outShift, c.getDest(), c.getDestPort())) {
                wfm.addConnection(newId, c.getSourcePort() + outShift, c.getDest(), c.getDestPort());
            } else {
                break;
            }
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
    protected static void setUIInformation(final NodeUIInformation uiInfo, final NodeContainer replacement) {
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
        var wfm = getWorkflowManager();
        if (m_isNativeNodeReplace) {
            return m_result.canUndo();
        } else {
            return m_replacementNodeContainer != null && wfm.canRemoveNode(m_replacementNodeContainer.getID());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void undo() throws OperationNotAllowedException {
        var wfm = getWorkflowManager();

        if (m_replacementNodeContainer != null) { // remove replacement
            wfm.removeNode(m_replacementNodeContainer.getID());
        }

        if (m_oldStatePersistor != null) {
            wfm.paste(m_oldStatePersistor); // Restore old node
        }

        if (m_isNativeNodeReplace) {
            m_result.undo();
            m_result = null;
        } else {
            for (ConnectionContainer cc : m_oldStateConnections) {
                wfm.addConnection(cc.getSource(), cc.getSourcePort(), cc.getDest(), cc.getDestPort());
            }
            m_oldStateConnections = null;
        }
        m_replacementNodeContainer = null;
        m_oldStatePersistor = null;
    }
}

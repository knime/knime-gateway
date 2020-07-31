/* ------------------------------------------------------------------
 * This source code, its documentation and all appendant files
 * are protected by copyright law. All rights reserved.
 *
 * Copyright by KNIME AG, Zurich, Switzerland
 *
 * You may not modify, publish, transmit, transfer or sell, reproduce,
 * create derivative works from, distribute, perform, display, or in
 * any way exploit any of the content, in whole or in part, except as
 * otherwise expressly permitted in writing by the copyright owner or
 * as specified in the license file distributed with this product.
 *
 * If you have any questions please contact the copyright holder:
 * website: www.knime.com
 * email: contact@knime.com
 * ---------------------------------------------------------------------
 *
 */
package org.knime.gateway.impl.webui.entity.util;

import static org.knime.gateway.api.entity.EntityBuilderManager.builder;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.knime.core.node.port.PortType;
import org.knime.core.node.workflow.ConnectionContainer;
import org.knime.core.node.workflow.ConnectionContainer.ConnectionType;
import org.knime.core.node.workflow.NativeNodeContainer;
import org.knime.core.node.workflow.NodeAnnotation;
import org.knime.core.node.workflow.NodeContainer;
import org.knime.core.node.workflow.NodeContainerState;
import org.knime.core.node.workflow.NodeID;
import org.knime.core.node.workflow.NodeInPort;
import org.knime.core.node.workflow.NodeMessage;
import org.knime.core.node.workflow.NodeOutPort;
import org.knime.core.node.workflow.SubNodeContainer;
import org.knime.core.node.workflow.WorkflowManager;
import org.knime.gateway.api.entity.ConnectionIDEnt;
import org.knime.gateway.api.entity.NodeIDEnt;
import org.knime.gateway.api.webui.entity.ComponentNodeEnt;
import org.knime.gateway.api.webui.entity.ComponentNodeEnt.ComponentNodeEntBuilder;
import org.knime.gateway.api.webui.entity.ConnectionEnt;
import org.knime.gateway.api.webui.entity.ConnectionEnt.ConnectionEntBuilder;
import org.knime.gateway.api.webui.entity.NativeNodeEnt;
import org.knime.gateway.api.webui.entity.NativeNodeEnt.NativeNodeEntBuilder;
import org.knime.gateway.api.webui.entity.NodeAnnotationEnt;
import org.knime.gateway.api.webui.entity.NodeAnnotationEnt.NodeAnnotationEntBuilder;
import org.knime.gateway.api.webui.entity.NodeEnt;
import org.knime.gateway.api.webui.entity.NodeEnt.NodeTypeEnum;
import org.knime.gateway.api.webui.entity.NodeInPortEnt;
import org.knime.gateway.api.webui.entity.NodeInPortEnt.NodeInPortEntBuilder;
import org.knime.gateway.api.webui.entity.NodeMessageEnt;
import org.knime.gateway.api.webui.entity.NodeMessageEnt.NodeMessageEntBuilder;
import org.knime.gateway.api.webui.entity.NodeOutPortEnt;
import org.knime.gateway.api.webui.entity.NodeOutPortEnt.NodeOutPortEntBuilder;
import org.knime.gateway.api.webui.entity.NodeProgressEnt;
import org.knime.gateway.api.webui.entity.NodeProgressEnt.NodeProgressEntBuilder;
import org.knime.gateway.api.webui.entity.NodeStateEnt;
import org.knime.gateway.api.webui.entity.NodeStateEnt.NodeStateEntBuilder;
import org.knime.gateway.api.webui.entity.PortTypeEnt;
import org.knime.gateway.api.webui.entity.PortTypeEnt.PortTypeEntBuilder;
import org.knime.gateway.api.webui.entity.WorkflowEnt;
import org.knime.gateway.api.webui.entity.WorkflowEnt.WorkflowEntBuilder;
import org.knime.gateway.api.webui.entity.WorkflowNodeEnt;
import org.knime.gateway.api.webui.entity.WorkflowNodeEnt.WorkflowNodeEntBuilder;

/**
 * Collects helper methods to build entity instances basically from core.api-classes (e.g. WorkflowManager etc.).
 *
 * @author Martin Horn, University of Konstanz
 */
public final class EntityBuilderUtil {

    private EntityBuilderUtil() {
        //utility class
    }

    /**
     * Builds a new {@link WorkflowEnt}.
     *
     * @param wfm the workflow manager to create the entity from
     * @param rootWorkflowID the workflow ID of the root workflow
     * @return the newly created entity
     */
    public static WorkflowEnt buildWorkflowEnt(final WorkflowManager wfm, final UUID rootWorkflowID) {
        Collection<NodeContainer> nodeContainers = wfm.getNodeContainers();
        Map<String, NodeEnt> nodes = nodeContainers.stream()
                .map(nc -> buildNodeEnt(nc, rootWorkflowID))
                .collect(Collectors.toMap(n -> n.getNodeID().toString(), n -> n));
        Map<String, ConnectionEnt> connections =
            wfm.getConnectionContainers().stream().map(cc -> buildConnectionEnt(cc)).collect(
                Collectors.toMap(c -> new ConnectionIDEnt(c.getDest(), c.getDestPort()).toString(), c -> c));
        return builder(WorkflowEntBuilder.class)
            .setNodes(nodes)
            .setConnections(connections)
            .build();
    }

    /**
     * Builds a new {@link NodeEnt}.
     * Depending on the node container implementation a different subclass is returned.
     *
     * @param nc the node container to create the entity from
     * @param rootWorkflowID must be present, if nc is of type {@link WorkflowManager}
     * @return the newly created entity
     */
    public static NodeEnt buildNodeEnt(final NodeContainer nc, final UUID rootWorkflowID) {
        if (nc instanceof NativeNodeContainer) {
            return buildNativeNodeEnt((NativeNodeContainer) nc, rootWorkflowID);
        } else if (nc instanceof WorkflowManager) {
            return buildWorkflowNodeEnt((WorkflowManager) nc, rootWorkflowID);
        } else if (nc instanceof SubNodeContainer) {
            return buildComponentNodeEnt((SubNodeContainer) nc, rootWorkflowID);
        } else {
            throw new IllegalArgumentException("Node container " + nc.getClass().getCanonicalName()
                + " cannot be mapped to a node entity.");
        }
    }

    /**
     * Builds a new {@link WorkflowNodeEnt}.
     *
     * @param wm the workflow manager to build the node entity from
     * @param rootWorkflowID the workflow ID of the root workflow
     * @return the newly created entity
     */
    public static WorkflowNodeEnt buildWorkflowNodeEnt(final WorkflowManager wm, final UUID rootWorkflowID) {
        NodeIDEnt parentNodeID;
        if (wm.getParent() == null || wm.getParent() == WorkflowManager.ROOT) {
            parentNodeID = null;
        } else {
            parentNodeID = new NodeIDEnt(wm.getParent().getID());
        }

        //retrieve states of nodes connected to the workflow outports
        List<NodeStateEnt> outNodeStates = new ArrayList<>();
        for (int i = 0; i < wm.getNrOutPorts(); i++) {
            outNodeStates.add(buildNodeStateEnt(wm.getOutPort(i).getNodeContainerState().toString()));
        }

        return builder(WorkflowNodeEntBuilder.class).setName(wm.getName())
                .setNodeID(new NodeIDEnt(wm.getID()))
                .setNodeMessage(buildNodeMessageEnt(wm))
                .setNodeType(NodeTypeEnum.valueOf(wm.getType().toString().toUpperCase()))
                .setNodeState(buildNodeStateEnt(wm.getNodeContainerState().toString()))
                .setOutPorts(buildNodeOutPortEnts(wm))
                .setParentNodeID(parentNodeID)
                .setNodeAnnotation(buildNodeAnnotationEnt(wm))
                .setInPorts(buildNodeInPortEnts(wm))
                .setRootWorkflowID(rootWorkflowID)
                .setWorkflowOutgoingPortNodeStates(outNodeStates)
                .setType("WorkflowNode").build();
    }

    /**
     * Builds a new {@link ComponentNodeEnt}.
     *
     * @param subNode the subnode container to create the node entity from
     * @param rootWorkflowID the workflow ID of the root workflow
     * @return the newly created entity
     */
    public static ComponentNodeEnt buildComponentNodeEnt(final SubNodeContainer subNode,
        final UUID rootWorkflowID) {
        return builder(ComponentNodeEntBuilder.class).setName(subNode.getName())
                .setNodeID(new NodeIDEnt(subNode.getID()))
                .setNodeMessage(buildNodeMessageEnt(subNode))
                .setNodeType(NodeTypeEnum.valueOf(subNode.getType().toString().toUpperCase()))
                .setNodeState(buildNodeStateEnt((subNode.getNodeContainerState().toString())))
                .setOutPorts(buildNodeOutPortEnts(subNode))
                .setParentNodeID(
                    subNode.getParent() == WorkflowManager.ROOT ? null : new NodeIDEnt(subNode.getParent().getID()))
                .setNodeAnnotation(buildNodeAnnotationEnt(subNode))
                .setInPorts(buildNodeInPortEnts(subNode))
                .setRootWorkflowID(rootWorkflowID)
                .setType("ComponentNode").build();
    }

    private static PortTypeEnt buildPortTypeEnt(final PortType portType) {
        return builder(PortTypeEntBuilder.class)
            .setOptional(portType.isOptional())
            .setPortObjectClassName(portType.getPortObjectClass().getCanonicalName()).build();
    }

    private static List<NodeInPortEnt> buildNodeInPortEnts(final NodeContainer nc) {
        List<NodeInPortEnt> inPorts = new ArrayList<>(nc.getNrInPorts());
        for (int i = 0; i < nc.getNrInPorts(); i++) {
            inPorts.add(buildNodeInPortEnt(nc.getInPort(i)));
        }
        return inPorts;
    }

    private static NodeInPortEnt buildNodeInPortEnt(final NodeInPort inPort) {
        PortTypeEnt pType = buildPortTypeEnt(inPort.getPortType());
        return builder(NodeInPortEntBuilder.class)
                .setPortIndex(inPort.getPortIndex())
                .setPortName(inPort.getPortName())
                .setPortType(pType)
                .setType("NodeInPort").build();
    }

    private static List<NodeOutPortEnt> buildNodeOutPortEnts(final NodeContainer nc) {
        List<NodeOutPortEnt> outPorts = new ArrayList<>(nc.getNrOutPorts());
        for (int i = 0; i < nc.getNrOutPorts(); i++) {
            outPorts.add(buildNodeOutPortEnt(nc.getOutPort(i)));
        }
        return outPorts;
    }

    private static NodeOutPortEnt buildNodeOutPortEnt(final NodeOutPort outPort) {
        PortTypeEnt pType = buildPortTypeEnt(outPort.getPortType());
        return builder(NodeOutPortEntBuilder.class)
                .setPortIndex(outPort.getPortIndex())
                .setPortName(outPort.getPortName())
                .setPortType(pType)
                .setInactive(outPort.isInactive())
                .setSummary(outPort.getPortSummary())
                .setType("NodeOutPort").build();
    }

    private static NodeAnnotationEnt buildNodeAnnotationEnt(final NodeContainer nc) {
        NodeAnnotation na = nc.getNodeAnnotation();
        return builder(NodeAnnotationEntBuilder.class)
            .setText(na.getText())
            .setType("NodeAnnotation").build();
    }

    /**
     * Extracts the node messages from a workflow manager and returns them as {@link NodeMessageEnt} instance.
     *
     * @param wfm the workflow manager to extract the node messages from
     * @return the new node message entity instance
     */
    public static Map<String, NodeMessageEnt> buildNodeMessageEntMap(final WorkflowManager wfm) {
        return wfm.getNodeMessages(NodeMessage.Type.ERROR, NodeMessage.Type.WARNING).stream()
            .collect(Collectors.toMap(nm -> nm.getFirst(), nm -> {
                return builder(NodeMessageEntBuilder.class).setMessage(nm.getSecond().getMessage())
                    .setType(nm.getSecond().getMessageType().toString()).build();
            }));
    }

    private static NodeMessageEnt buildNodeMessageEnt(final NodeContainer nc) {
        return builder(NodeMessageEntBuilder.class).setMessage(nc.getNodeMessage().getMessage())
            .setType(nc.getNodeMessage().getMessageType().toString()).build();
    }

    private static NativeNodeEnt buildNativeNodeEnt(final NativeNodeContainer nc, final UUID rootWorkflowID) {
        return builder(NativeNodeEntBuilder.class).setName(nc.getName())
            .setNodeID(new NodeIDEnt(nc.getID()))
            .setNodeMessage(buildNodeMessageEnt(nc))
            .setNodeType(NodeTypeEnum.valueOf(nc.getType().toString().toUpperCase()))
            .setNodeState(buildNodeStateEnt(nc.getNodeContainerState().toString()))
            .setProgress(
                buildNodeProgressEnt(nc.getProgressMonitor().getProgress(),
                    nc.getProgressMonitor().getMessage(),
                    nc.getNodeContainerState()))
            .setOutPorts(buildNodeOutPortEnts(nc))
            .setParentNodeID(nc.getParent() == WorkflowManager.ROOT ? null : new NodeIDEnt(nc.getParent().getID()))
            .setRootWorkflowID(rootWorkflowID)
            .setNodeAnnotation(buildNodeAnnotationEnt(nc))
            .setInPorts(buildNodeInPortEnts(nc))
            .setInactive(nc.isInactive())
            .setType("NativeNode").build();
    }

    private static NodeProgressEnt buildNodeProgressEnt(final Double progress, final String message,
        final NodeContainerState state) {
        if (state.isExecutionInProgress()) {
            return builder(NodeProgressEntBuilder.class)
                .setProgress(progress == null ? null : BigDecimal.valueOf(progress)).setMessage(message).build();
        } else {
            return builder(NodeProgressEntBuilder.class).setProgress(null).setMessage(null).build();
        }
    }

    private static ConnectionEnt buildConnectionEnt(final ConnectionContainer cc) {
        return builder(ConnectionEntBuilder.class).setDest(new NodeIDEnt(cc.getDest()))
            .setDestPort(cc.getDestPort())
            .setSource(new NodeIDEnt(cc.getSource())).setSourcePort(cc.getSourcePort())
            .build();
    }

    /**
     * Creates a new connection entity from the given parameters. All other connection-properties remain empty.
     *
     * @param source
     * @param sourcePort
     * @param dest
     * @param destPort
     * @param type
     * @param bendpoints
     * @return a new connection entity
     */
    public static ConnectionEnt buildConnectionEnt(final NodeID source, final int sourcePort, final NodeID dest,
        final int destPort, final ConnectionType type, final int[][] bendpoints) {
        return builder(ConnectionEntBuilder.class)
                .setSource(new NodeIDEnt(source))
                .setSourcePort(sourcePort)
                .setDest(new NodeIDEnt(dest))
                .setDestPort(destPort).build();
    }

    private static NodeStateEnt buildNodeStateEnt(final String state) {
        return builder(NodeStateEntBuilder.class)
                .setState(NodeStateEnt.StateEnum.valueOf(state))
                .build();
    }
}

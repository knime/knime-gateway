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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.knime.core.node.port.PortType;
import org.knime.core.node.workflow.ConnectionContainer;
import org.knime.core.node.workflow.NativeNodeContainer;
import org.knime.core.node.workflow.NodeAnnotation;
import org.knime.core.node.workflow.NodeContainer;
import org.knime.core.node.workflow.NodeID;
import org.knime.core.node.workflow.NodeInPort;
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
import org.knime.gateway.api.webui.entity.NativeNodeEnt.TypeEnum;
import org.knime.gateway.api.webui.entity.NodeAnnotationEnt;
import org.knime.gateway.api.webui.entity.NodeAnnotationEnt.NodeAnnotationEntBuilder;
import org.knime.gateway.api.webui.entity.NodeEnt;
import org.knime.gateway.api.webui.entity.NodeInPortEnt;
import org.knime.gateway.api.webui.entity.NodeInPortEnt.NodeInPortEntBuilder;
import org.knime.gateway.api.webui.entity.NodeOutPortEnt;
import org.knime.gateway.api.webui.entity.NodeOutPortEnt.NodeOutPortEntBuilder;
import org.knime.gateway.api.webui.entity.NodeStateEnt;
import org.knime.gateway.api.webui.entity.NodeStateEnt.NodeStateEntBuilder;
import org.knime.gateway.api.webui.entity.PortTypeEnt;
import org.knime.gateway.api.webui.entity.PortTypeEnt.PortTypeEntBuilder;
import org.knime.gateway.api.webui.entity.WorkflowEnt;
import org.knime.gateway.api.webui.entity.WorkflowEnt.WorkflowEntBuilder;
import org.knime.gateway.api.webui.entity.WorkflowNodeEnt;
import org.knime.gateway.api.webui.entity.WorkflowNodeEnt.WorkflowNodeEntBuilder;
import org.knime.gateway.api.webui.entity.XYEnt;
import org.knime.gateway.api.webui.entity.XYEnt.XYEntBuilder;

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
     * @return the newly created entity
     */
    public static WorkflowEnt buildWorkflowEnt(final WorkflowManager wfm) {
        Collection<NodeContainer> nodeContainers = wfm.getNodeContainers();
        Map<String, NodeEnt> nodes = nodeContainers.stream()
                .map(EntityBuilderUtil::buildNodeEnt)
                .collect(Collectors.toMap(n -> n.getId().toString(), n -> n));
        Map<String, ConnectionEnt> connections =
            wfm.getConnectionContainers().stream().map(EntityBuilderUtil::buildConnectionEnt).collect(
                Collectors.toMap(c -> new ConnectionIDEnt(c.getDest(), c.getDestPort()).toString(), c -> c));
        return builder(WorkflowEntBuilder.class)
            .setName(wfm.getName())
            .setNodes(nodes)
            .setConnections(connections)
            .build();
    }

    /**
     * Builds a new {@link NodeEnt}.
     * Depending on the node container implementation a different subclass is returned.
     *
     * @param nc the node container to create the entity from
     * @return the newly created entity
     */
    public static NodeEnt buildNodeEnt(final NodeContainer nc) {
        if (nc instanceof NativeNodeContainer) {
            return buildNativeNodeEnt((NativeNodeContainer) nc);
        } else if (nc instanceof WorkflowManager) {
            return buildWorkflowNodeEnt((WorkflowManager) nc);
        } else if (nc instanceof SubNodeContainer) {
            return buildComponentNodeEnt((SubNodeContainer) nc);
        } else {
            throw new IllegalArgumentException("Node container " + nc.getClass().getCanonicalName()
                + " cannot be mapped to a node entity.");
        }
    }

    /**
     * Builds a new {@link WorkflowNodeEnt}.
     *
     * @param wm the workflow manager to build the node entity from
     * @return the newly created entity
     */
    public static WorkflowNodeEnt buildWorkflowNodeEnt(final WorkflowManager wm) {
        //retrieve states of nodes connected to the workflow outports
        List<NodeStateEnt> outNodeStates = new ArrayList<>();
        for (int i = 0; i < wm.getNrOutPorts(); i++) {
            outNodeStates.add(buildNodeStateEnt(wm.getOutPort(i).getNodeContainerState().toString()));
        }

        return builder(WorkflowNodeEntBuilder.class).setName(wm.getName())
                .setId(new NodeIDEnt(wm.getID()))
                .setState(buildNodeStateEnt(wm.getNodeContainerState().toString()))
                .setOutPorts(buildNodeOutPortEnts(wm))
                .setAnnotation(buildNodeAnnotationEnt(wm))
                .setInPorts(buildNodeInPortEnts(wm))
                .setWorkflowOutgoingPortNodeStates(outNodeStates)
                .setPosition(buildXYEnt(wm.getUIInformation().getBounds()[0], wm.getUIInformation().getBounds()[1]))
                .setObjectType("WorkflowNode").build();
    }

    /**
     * Builds a new {@link ComponentNodeEnt}.
     *
     * @param nc the subnode container to create the node entity from
     * @return the newly created entity
     */
    public static ComponentNodeEnt buildComponentNodeEnt(final SubNodeContainer nc) {
        String type = nc.getType().toString().toUpperCase();
        return builder(ComponentNodeEntBuilder.class).setName(nc.getName())
                .setId(new NodeIDEnt(nc.getID()))
                .setType(org.knime.gateway.api.webui.entity.ComponentNodeEnt.TypeEnum.valueOf(type))
                .setState(buildNodeStateEnt((nc.getNodeContainerState().toString())))
                .setOutPorts(buildNodeOutPortEnts(nc))
                .setAnnotation(buildNodeAnnotationEnt(nc))
                .setInPorts(buildNodeInPortEnts(nc))
                .setPosition(buildXYEnt(nc.getUIInformation().getBounds()[0], nc.getUIInformation().getBounds()[1]))
                .setObjectType("ComponentNode").build();
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
                .setObjectType("NodeInPort").build();
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
                .setObjectType("NodeOutPort").build();
    }

    private static NodeAnnotationEnt buildNodeAnnotationEnt(final NodeContainer nc) {
        NodeAnnotation na = nc.getNodeAnnotation();
        return builder(NodeAnnotationEntBuilder.class)
            .setText(na.getText())
            .setObjectType("NodeAnnotation").build();
    }

    private static NativeNodeEnt buildNativeNodeEnt(final NativeNodeContainer nc) {
        return builder(NativeNodeEntBuilder.class).setName(nc.getName())
            .setId(new NodeIDEnt(nc.getID()))
            .setType(TypeEnum.valueOf(nc.getType().toString().toUpperCase()))
            .setState(buildNodeStateEnt(nc.getNodeContainerState().toString()))
            .setOutPorts(buildNodeOutPortEnts(nc))
            .setAnnotation(buildNodeAnnotationEnt(nc))
            .setInPorts(buildNodeInPortEnts(nc))
            .setPosition(buildXYEnt(nc.getUIInformation().getBounds()[0], nc.getUIInformation().getBounds()[1]))
            .setObjectType("NativeNode").build();
    }

    private static XYEnt buildXYEnt(final int x, final int y) {
        return builder(XYEntBuilder.class).setX(x).setY(y).build();
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
     * @return a new connection entity
     */
    public static ConnectionEnt buildConnectionEnt(final NodeID source, final int sourcePort, final NodeID dest,
        final int destPort) {
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

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
package org.knime.gateway.api.webui.util;

import static org.knime.gateway.api.entity.EntityBuilderManager.builder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.knime.core.node.BufferedDataTable;
import org.knime.core.node.port.PortType;
import org.knime.core.node.port.flowvariable.FlowVariablePortObject;
import org.knime.core.node.workflow.ConnectionContainer;
import org.knime.core.node.workflow.NativeNodeContainer;
import org.knime.core.node.workflow.NodeAnnotation;
import org.knime.core.node.workflow.NodeContainer;
import org.knime.core.node.workflow.NodeInPort;
import org.knime.core.node.workflow.NodeOutPort;
import org.knime.core.node.workflow.SubNodeContainer;
import org.knime.core.node.workflow.WorkflowAnnotation;
import org.knime.core.node.workflow.WorkflowAnnotationID;
import org.knime.core.node.workflow.WorkflowManager;
import org.knime.core.util.Pair;
import org.knime.gateway.api.entity.AnnotationIDEnt;
import org.knime.gateway.api.entity.ConnectionIDEnt;
import org.knime.gateway.api.entity.NodeIDEnt;
import org.knime.gateway.api.webui.entity.BoundsEnt;
import org.knime.gateway.api.webui.entity.BoundsEnt.BoundsEntBuilder;
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
import org.knime.gateway.api.webui.entity.NodeEnt.PropertyClassEnum;
import org.knime.gateway.api.webui.entity.NodePortEnt;
import org.knime.gateway.api.webui.entity.NodePortEnt.NodePortEntBuilder;
import org.knime.gateway.api.webui.entity.WorkflowAnnotationEnt;
import org.knime.gateway.api.webui.entity.WorkflowAnnotationEnt.TextAlignEnum;
import org.knime.gateway.api.webui.entity.WorkflowAnnotationEnt.WorkflowAnnotationEntBuilder;
import org.knime.gateway.api.webui.entity.WorkflowEnt;
import org.knime.gateway.api.webui.entity.WorkflowEnt.WorkflowEntBuilder;
import org.knime.gateway.api.webui.entity.WorkflowNodeEnt;
import org.knime.gateway.api.webui.entity.WorkflowNodeEnt.WorkflowNodeEntBuilder;
import org.knime.gateway.api.webui.entity.XYEnt;
import org.knime.gateway.api.webui.entity.XYEnt.XYEntBuilder;

/**
 * Collects helper methods to build entity instances basically from core.api-classes (e.g. WorkflowManager etc.).
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
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
                Collectors.toMap(c -> new ConnectionIDEnt(c.getDestNode(), c.getDestPort()).toString(), c -> c));
        Map<String, WorkflowAnnotationEnt> annotations =
            wfm.getWorkflowAnnotations().stream().map(EntityBuilderUtil::buildWorkflowAnnotationEnt)
                .collect(Collectors.toMap(wa -> new AnnotationIDEnt(wa.getFirst()).toString(), Pair::getSecond));
        return builder(WorkflowEntBuilder.class)
            .setName(wfm.getName())
            .setNodes(nodes)
            .setConnections(connections)
            .setWorkflowAnnotations(annotations)
            .build();
    }

    private static Pair<WorkflowAnnotationID, WorkflowAnnotationEnt>
        buildWorkflowAnnotationEnt(final WorkflowAnnotation wa) {
        BoundsEnt bounds = builder(BoundsEntBuilder.class)
                .setX(wa.getX())
                .setY(wa.getY())
                .setWidth(wa.getWidth())
                .setHeight(wa.getHeight())
                .build();
        TextAlignEnum textAlign;
        switch (wa.getAlignment()) {
            case LEFT:
                textAlign = TextAlignEnum.LEFT;
                break;
            case CENTER:
                textAlign = TextAlignEnum.CENTER;
                break;
            case RIGHT:
            default:
                textAlign = TextAlignEnum.RIGHT;
                break;
        }
        return Pair.create(wa.getID(), builder(WorkflowAnnotationEntBuilder.class)
                .setTextAlign(textAlign)
                .setBackgroundColor(hexStringColor(wa.getBgColor()))
                .setBorderColor(hexStringColor(wa.getBorderColor()))
                .setBorderWidth(wa.getBorderSize())
                .setDefaultFontSize(wa.getDefaultFontSize())
                .setBounds(bounds)
                .setText(wa.getText())
                .build());
    }

    private static String hexStringColor(final int color) {
        return "#" + Integer.toHexString(color).substring(0, 6);
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
        return builder(WorkflowNodeEntBuilder.class).setName(wm.getName())
                .setId(new NodeIDEnt(wm.getID()))
                .setOutPorts(buildNodePortEnts(wm, false))
                .setAnnotation(buildNodeAnnotationEnt(wm))
                .setInPorts(buildNodePortEnts(wm, true))
                .setPosition(buildXYEnt(wm.getUIInformation().getBounds()[0], wm.getUIInformation().getBounds()[1]))
                .setPropertyClass(PropertyClassEnum.METANODE).build();
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
                .setOutPorts(buildNodePortEnts(nc, false))
                .setAnnotation(buildNodeAnnotationEnt(nc))
                .setInPorts(buildNodePortEnts(nc, true))
                .setPosition(buildXYEnt(nc.getUIInformation().getBounds()[0], nc.getUIInformation().getBounds()[1]))
                .setPropertyClass(PropertyClassEnum.COMPONENT).build();
    }

    private static List<NodePortEnt> buildNodePortEnts(final NodeContainer nc, final boolean inPorts) {
        List<NodePortEnt> res = new ArrayList<>();
        if (inPorts) {
            for (int i = 0; i < nc.getNrInPorts(); i++) {
                ConnectionContainer connection = nc.getParent().getIncomingConnectionFor(nc.getID(), i);
                NodeInPort inPort = nc.getInPort(i);
                res.add(buildNodePortEnt(inPort.getPortType(), i, inPort.getPortType().isOptional(), null,
                    connection == null ? Collections.emptyList() : Arrays.asList(connection)));
            }
        } else {
            for (int i = 0; i < nc.getNrOutPorts(); i++) {
                Set<ConnectionContainer> connections = nc.getParent().getOutgoingConnectionsFor(nc.getID(), i);
                NodeOutPort outPort = nc.getOutPort(i);
                res.add(buildNodePortEnt(outPort.getPortType(), i, null,
                    outPort.isInactive() ? outPort.isInactive() : null, connections));
            }
        }
        return res;
    }

    private static NodePortEnt buildNodePortEnt(final PortType ptype, final int portIdx, final Boolean isOptional,
        final Boolean isInactive, final Collection<ConnectionContainer> connections) {
        org.knime.gateway.api.webui.entity.NodePortEnt.TypeEnum type;
        String color;
        if (BufferedDataTable.TYPE.equals(ptype)) {
            type = org.knime.gateway.api.webui.entity.NodePortEnt.TypeEnum.TABLE;
            color = null;
        } else if (FlowVariablePortObject.TYPE.equals(ptype)) {
            type = org.knime.gateway.api.webui.entity.NodePortEnt.TypeEnum.FLOWVARIABLE;
            color = null;
        } else {
            type = org.knime.gateway.api.webui.entity.NodePortEnt.TypeEnum.OTHER;
            color = hexStringColor(ptype.getColor());
        }
        return builder(NodePortEntBuilder.class)
                .setIndex(portIdx)
                .setOptional(isOptional)
                .setInactive(isInactive)
                .setColor(color)
                .setConnectedVia(connections.stream().map(EntityBuilderUtil::buildConnectionIDEnt)
                    .collect(Collectors.toList()))
                .setType(type).build();
    }

    private static ConnectionIDEnt buildConnectionIDEnt(final ConnectionContainer c) {
        return new ConnectionIDEnt(new NodeIDEnt(c.getDest()), c.getDestPort());
    }

    private static NodeAnnotationEnt buildNodeAnnotationEnt(final NodeContainer nc) {
        NodeAnnotation na = nc.getNodeAnnotation();
        if (na.getData().isDefault()) {
            return null;
        }
        return builder(NodeAnnotationEntBuilder.class)
            .setText(na.getText()).build();
    }

    private static NativeNodeEnt buildNativeNodeEnt(final NativeNodeContainer nc) {
        return builder(NativeNodeEntBuilder.class).setName(nc.getName())
            .setId(new NodeIDEnt(nc.getID()))
            .setType(TypeEnum.valueOf(nc.getType().toString().toUpperCase()))
            .setOutPorts(buildNodePortEnts(nc, false))
            .setAnnotation(buildNodeAnnotationEnt(nc))
            .setInPorts(buildNodePortEnts(nc, true))
            .setPosition(buildXYEnt(nc.getUIInformation().getBounds()[0], nc.getUIInformation().getBounds()[1]))
            .setPropertyClass(NodeEnt.PropertyClassEnum.NODE).build();
    }

    private static XYEnt buildXYEnt(final int x, final int y) {
        return builder(XYEntBuilder.class).setX(x).setY(y).build();
    }

    private static ConnectionEnt buildConnectionEnt(final ConnectionContainer cc) {
        return builder(ConnectionEntBuilder.class).setDestNode(new NodeIDEnt(cc.getDest()))
            .setDestPort(cc.getDestPort())
            .setSourceNode(new NodeIDEnt(cc.getSource())).setSourcePort(cc.getSourcePort())
            .setFlowVariableConnection(
                cc.isFlowVariablePortConnection() ? cc.isFlowVariablePortConnection() : null)
            .build();
    }
}

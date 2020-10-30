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

import static java.util.Collections.emptyList;
import static java.util.Collections.singleton;
import static java.util.stream.Collectors.toList;
import static org.knime.gateway.api.entity.EntityBuilderManager.builder;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.knime.core.node.BufferedDataTable;
import org.knime.core.node.DynamicNodeFactory;
import org.knime.core.node.NodeFactory;
import org.knime.core.node.NodeFactory.NodeType;
import org.knime.core.node.NodeLogger;
import org.knime.core.node.NodeModel;
import org.knime.core.node.NodeProgressMonitor;
import org.knime.core.node.NodeSettings;
import org.knime.core.node.missing.MissingNodeFactory;
import org.knime.core.node.port.PortType;
import org.knime.core.node.port.flowvariable.FlowVariablePortObject;
import org.knime.core.node.workflow.AnnotationData.StyleRange;
import org.knime.core.node.workflow.ComponentMetadata.ComponentNodeType;
import org.knime.core.node.workflow.ConnectionContainer;
import org.knime.core.node.workflow.DependentNodeProperties;
import org.knime.core.node.workflow.NativeNodeContainer;
import org.knime.core.node.workflow.NodeAnnotation;
import org.knime.core.node.workflow.NodeContainer;
import org.knime.core.node.workflow.NodeContainerParent;
import org.knime.core.node.workflow.NodeContainerState;
import org.knime.core.node.workflow.NodeContainerTemplate;
import org.knime.core.node.workflow.NodeID;
import org.knime.core.node.workflow.NodeInPort;
import org.knime.core.node.workflow.NodeMessage;
import org.knime.core.node.workflow.NodeMessage.Type;
import org.knime.core.node.workflow.NodeOutPort;
import org.knime.core.node.workflow.NodeUIInformation;
import org.knime.core.node.workflow.SingleNodeContainer;
import org.knime.core.node.workflow.SubNodeContainer;
import org.knime.core.node.workflow.WorkflowAnnotation;
import org.knime.core.node.workflow.WorkflowLock;
import org.knime.core.node.workflow.WorkflowManager;
import org.knime.core.util.ConfigUtils;
import org.knime.core.util.workflowalizer.NodeAndBundleInformation;
import org.knime.gateway.api.entity.AnnotationIDEnt;
import org.knime.gateway.api.entity.ConnectionIDEnt;
import org.knime.gateway.api.entity.NodeIDEnt;
import org.knime.gateway.api.webui.entity.AllowedActionsEnt;
import org.knime.gateway.api.webui.entity.AllowedActionsEnt.AllowedActionsEntBuilder;
import org.knime.gateway.api.webui.entity.AnnotationEnt.TextAlignEnum;
import org.knime.gateway.api.webui.entity.BoundsEnt;
import org.knime.gateway.api.webui.entity.BoundsEnt.BoundsEntBuilder;
import org.knime.gateway.api.webui.entity.ComponentNodeEnt;
import org.knime.gateway.api.webui.entity.ComponentNodeEnt.ComponentNodeEntBuilder;
import org.knime.gateway.api.webui.entity.ConnectionEnt;
import org.knime.gateway.api.webui.entity.ConnectionEnt.ConnectionEntBuilder;
import org.knime.gateway.api.webui.entity.MetaNodeEnt;
import org.knime.gateway.api.webui.entity.MetaNodeEnt.MetaNodeEntBuilder;
import org.knime.gateway.api.webui.entity.MetaNodePortEnt;
import org.knime.gateway.api.webui.entity.MetaNodePortEnt.MetaNodePortEntBuilder;
import org.knime.gateway.api.webui.entity.MetaNodePortEnt.NodeStateEnum;
import org.knime.gateway.api.webui.entity.MetaNodeStateEnt;
import org.knime.gateway.api.webui.entity.MetaNodeStateEnt.MetaNodeStateEntBuilder;
import org.knime.gateway.api.webui.entity.MetaPortsEnt;
import org.knime.gateway.api.webui.entity.MetaPortsEnt.MetaPortsEntBuilder;
import org.knime.gateway.api.webui.entity.NativeNodeEnt;
import org.knime.gateway.api.webui.entity.NativeNodeEnt.NativeNodeEntBuilder;
import org.knime.gateway.api.webui.entity.NodeAnnotationEnt;
import org.knime.gateway.api.webui.entity.NodeAnnotationEnt.NodeAnnotationEntBuilder;
import org.knime.gateway.api.webui.entity.NodeEnt;
import org.knime.gateway.api.webui.entity.NodeEnt.KindEnum;
import org.knime.gateway.api.webui.entity.NodePortEnt;
import org.knime.gateway.api.webui.entity.NodePortEnt.NodePortEntBuilder;
import org.knime.gateway.api.webui.entity.NodeStateEnt;
import org.knime.gateway.api.webui.entity.NodeStateEnt.ExecutionStateEnum;
import org.knime.gateway.api.webui.entity.NodeStateEnt.NodeStateEntBuilder;
import org.knime.gateway.api.webui.entity.NodeTemplateEnt;
import org.knime.gateway.api.webui.entity.NodeTemplateEnt.NodeTemplateEntBuilder;
import org.knime.gateway.api.webui.entity.NodeTemplateEnt.TypeEnum;
import org.knime.gateway.api.webui.entity.StyleRangeEnt;
import org.knime.gateway.api.webui.entity.StyleRangeEnt.StyleRangeEntBuilder;
import org.knime.gateway.api.webui.entity.WorkflowAnnotationEnt;
import org.knime.gateway.api.webui.entity.WorkflowAnnotationEnt.WorkflowAnnotationEntBuilder;
import org.knime.gateway.api.webui.entity.WorkflowEnt;
import org.knime.gateway.api.webui.entity.WorkflowEnt.WorkflowEntBuilder;
import org.knime.gateway.api.webui.entity.WorkflowInfoEnt;
import org.knime.gateway.api.webui.entity.WorkflowInfoEnt.ContainerTypeEnum;
import org.knime.gateway.api.webui.entity.WorkflowInfoEnt.WorkflowInfoEntBuilder;
import org.knime.gateway.api.webui.entity.XYEnt;
import org.knime.gateway.api.webui.entity.XYEnt.XYEntBuilder;

/**
 * Collects helper methods to build entity instances basically from core.api-classes (e.g. WorkflowManager etc.).
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
public final class EntityBuilderUtil {

    /*
     * The node position in the java-ui refers to the upper left corner of the 'node figure' which also includes
     * the (not always visible) implicit flow variables ports. I.e. the position does NOT match with the upper left
     * corner of the node background image. However, this is used as reference point in the web-ui. Thus, we need
     * to correct the position in y direction by some pixels.
     * (the value is chosen according to org.knime.workbench.editor2.figures.AbstractPortFigure.getPortSizeNode())
     *
     * NOTE: the current value has been 'experimentally' determined
     */
    private static final int NODE_Y_POS_CORRECTION = 6;

    /*
     * The default background color for node annotations which usually translates to opaque.
     */
    private static final int DEFAULT_NODE_ANNOTATION_BG_COLOR = 0xFFFFFF;

    private EntityBuilderUtil() {
        //utility class
    }

    /**
     * Builds a new {@link WorkflowEnt}.
     *
     * @param wfm the workflow manager to create the entity from
     * @param includeInfoOnAllowedActions whether to include information on the allowed actions on nodes and the entire
     *            workflow (such as execute, cancel or reset)
     * @return the newly created entity
     */
    public static WorkflowEnt buildWorkflowEnt(final WorkflowManager wfm, final boolean includeInfoOnAllowedActions) { // NOSONAR
        try (WorkflowLock lock = wfm.lock()) {
            Collection<NodeContainer> nodeContainers = wfm.getNodeContainers();

            Map<String, NodeEnt> nodes = new HashMap<>();
            Map<String, NodeTemplateEnt> templates = new HashMap<>();

            DependentNodeProperties depNodeProps = null;
            if (includeInfoOnAllowedActions) {
                depNodeProps = wfm.determineDependentNodeProperties();
            }
            boolean hasComponentProjectParent = wfm.getProjectComponent().isPresent();
            for (NodeContainer nc : nodeContainers) {
                NodeIDEnt id = new NodeIDEnt(nc.getID(), hasComponentProjectParent);
                buildAndAddNodeEnt(id, nc, nodes, templates, depNodeProps);
            }
            Map<String, ConnectionEnt> connections = wfm.getConnectionContainers().stream()
                .map(cc -> buildConnectionEnt(cc, hasComponentProjectParent)).collect(
                    Collectors.toMap(c -> new ConnectionIDEnt(c.getDestNode(), c.getDestPort()).toString(), c -> c)); // NOSONAR
            List<WorkflowAnnotationEnt> annotations =
                wfm.getWorkflowAnnotations().stream().map(EntityBuilderUtil::buildWorkflowAnnotationEnt)
                    .collect(Collectors.toList());
            return builder(WorkflowEntBuilder.class)
                .setInfo(buildWorkflowInfoEnt(wfm))
                .setNodes(nodes)
                .setNodeTemplates(templates)
                .setConnections(connections)
                .setWorkflowAnnotations(annotations)
                .setParents(buildParentWorkflowInfoEnts(wfm))
                .setMetaInPorts(buildMetaPortsEnt(wfm, true))
                .setMetaOutPorts(buildMetaPortsEnt(wfm, false))
                .setAllowedActions(includeInfoOnAllowedActions ? buildAllowedActionsEnt(wfm) : null)
                .build();
        }
    }

    private static MetaPortsEnt buildMetaPortsEnt(final WorkflowManager wfm, final boolean incoming) {
        if (wfm.isProject() || wfm.getDirectNCParent() instanceof SubNodeContainer) {
            // no meta ports for workflow projects and component workflows
            return null;
        }
        MetaPortsEntBuilder builder = builder(MetaPortsEntBuilder.class);

        List<NodePortEnt> ports = new ArrayList<>();
        if (incoming) {
            int nrPorts = wfm.getNrWorkflowIncomingPorts();
            for (int i = 0; i < nrPorts; i++) {
                Set<ConnectionContainer> connections = wfm.getOutgoingConnectionsFor(wfm.getID(), i);
                NodeOutPort port = wfm.getWorkflowIncomingPort(i);
                ports.add(buildNodePortEnt(port.getPortType(), port.getPortName(), port.getPortSummary(), i, null,
                    port.isInactive() ? Boolean.TRUE : null, connections));
            }
        } else {
            int nrPorts = wfm.getNrWorkflowOutgoingPorts();
            for (int i = 0; i < nrPorts; i++) {
                ConnectionContainer connection = wfm.getIncomingConnectionFor(wfm.getID(), i);
                NodeInPort port = wfm.getWorkflowOutgoingPort(i);
                ports.add(buildNodePortEnt(port.getPortType(), port.getPortName(), null, i, null, null,
                    connection != null ? singleton(connection) : emptyList()));
            }
        }
        builder.setPorts(ports);
        NodeUIInformation barUIInfo = incoming ? wfm.getInPortsBarUIInfo() : wfm.getOutPortsBarUIInfo();
        if (barUIInfo != null) {
            builder.setXPos(barUIInfo.getBounds()[0]);
        }
        return builder.build();
    }

    private static WorkflowInfoEnt buildWorkflowInfoEnt(final WorkflowManager wfm) {
        NodeContainerTemplate template;
        if (wfm.getDirectNCParent() instanceof SubNodeContainer) {
            template = (SubNodeContainer)wfm.getDirectNCParent();
        } else {
            template = wfm;
        }
        return builder(WorkflowInfoEntBuilder.class)//
            .setName(wfm.getName())//
            .setContainerId(getContainerId(wfm))//
            .setContainerType(getContainerType(wfm))//
            .setLinked(getTemplateLink(template) == null ? null : Boolean.TRUE)//
            .build();
    }

    private static String getTemplateLink(final NodeContainerTemplate nct) {
        URI sourceURI = nct.getTemplateInformation().getSourceURI();
        return sourceURI == null ? null : sourceURI.toString();
    }

    private static NodeIDEnt getContainerId(final WorkflowManager wfm) {
        if (wfm.isProject()) {
            return null;
        }

        NodeContainerParent ncParent = wfm.getDirectNCParent();
        boolean hasComponentProjectParent = wfm.getProjectComponent().isPresent();
        if (ncParent instanceof SubNodeContainer) {
            // it's a component's workflow
            return new NodeIDEnt(((SubNodeContainer)ncParent).getID(), hasComponentProjectParent);
        } else {
            return new NodeIDEnt(wfm.getID(), hasComponentProjectParent);
        }
    }

    private static ContainerTypeEnum getContainerType(final WorkflowManager wfm) {
        if (wfm.isProject() || wfm.isComponentProjectWFM()) {
            return ContainerTypeEnum.PROJECT;
        }
        NodeContainerParent parent = wfm.getDirectNCParent();
        if (parent instanceof SubNodeContainer) {
            return ContainerTypeEnum.COMPONENT;
        } else if (parent instanceof WorkflowManager) {
            return ContainerTypeEnum.METANODE;
        } else {
            throw new IllegalStateException();
        }
    }

    private static List<WorkflowInfoEnt> buildParentWorkflowInfoEnts(final WorkflowManager wfm) {
        if (wfm.isProject() || wfm.isComponentProjectWFM()) {
            return null; // NOSONAR
        }
        List<WorkflowInfoEnt> parents = new ArrayList<>();
        WorkflowManager parent = wfm;
        do {
            parent = getWorkflowParent(parent);
            parents.add(buildWorkflowInfoEnt(parent));
        } while (!parent.isProject() && !parent.isComponentProjectWFM());
        Collections.reverse(parents);
        return parents;
    }

    private static WorkflowManager getWorkflowParent(final WorkflowManager wfm) {
        NodeContainerParent parent = wfm.getDirectNCParent();
        if (parent instanceof SubNodeContainer) {
            return ((SubNodeContainer)parent).getParent();
        } else {
            return (WorkflowManager)parent;
        }
    }

    private static WorkflowAnnotationEnt buildWorkflowAnnotationEnt(final WorkflowAnnotation wa) {
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

        List<StyleRangeEnt> styleRanges =
            Arrays.stream(wa.getStyleRanges()).map(EntityBuilderUtil::buildStyleRangeEnt).collect(Collectors.toList());
        return builder(WorkflowAnnotationEntBuilder.class)
                .setId(new AnnotationIDEnt(wa.getID()))
                .setTextAlign(textAlign)
                .setBackgroundColor(hexStringColor(wa.getBgColor()))
                .setBorderColor(hexStringColor(wa.getBorderColor()))
                .setBorderWidth(wa.getBorderSize())
                .setBounds(bounds)
                .setText(wa.getText())
                .setStyleRanges(styleRanges)
                .setDefaultFontSize(wa.getDefaultFontSize() > 0 ? convertFromPtToPx(wa.getDefaultFontSize()) : null)
                .build();
    }

    private static Integer convertFromPtToPx(final int size) {
        return (int) Math.round(size + size / 3.0);
    }

    private static StyleRangeEnt buildStyleRangeEnt(final StyleRange sr) {
        StyleRangeEntBuilder builder = builder(StyleRangeEntBuilder.class)
                .setFontSize(convertFromPtToPx(sr.getFontSize()))
                .setColor(hexStringColor(sr.getFgColor()))
                .setLength(sr.getLength())
                .setStart(sr.getStart());
        if ((sr.getFontStyle() & StyleRange.BOLD) != 0) {
            builder.setBold(Boolean.TRUE);
        }
        if ((sr.getFontStyle() & StyleRange.ITALIC) != 0) {
            builder.setItalic(Boolean.TRUE);
        }
        return builder.build();
    }

    private static String hexStringColor(final int color) {
        return String.format("#%06X", (0xFFFFFF & color));
    }

    private static void buildAndAddNodeEnt(final NodeIDEnt id, final NodeContainer nc, final Map<String, NodeEnt> nodes,
        final Map<String, NodeTemplateEnt> templates, final DependentNodeProperties depNodeProps) {
        NodeEnt nodeEnt =
            depNodeProps != null ? buildNodeEntWithInfoOnAllowedActions(id, nc, depNodeProps) : buildNodeEnt(id, nc);
        nodes.put(nodeEnt.getId().toString(), nodeEnt);
        if (nc instanceof NativeNodeContainer) {
            String templateId = ((NativeNodeEnt)nodeEnt).getTemplateId();
            templates.computeIfAbsent(templateId, tid -> buildNodeTemplateEnt((NativeNodeContainer)nc));
        }
    }

    private static NodeEnt buildNodeEnt(final NodeIDEnt id, final NodeContainer nc) {
        return buildNodeEnt(id, nc, null);
    }

    private static NodeEnt buildNodeEntWithInfoOnAllowedActions(final NodeIDEnt id, final NodeContainer nc,
        final DependentNodeProperties depNodeProps) {
        return buildNodeEnt(id, nc, buildAllowedActionsEnt(nc, depNodeProps));
    }

    private static NodeEnt buildNodeEnt(final NodeIDEnt id, final NodeContainer nc,
        final AllowedActionsEnt allowedActions) {
        if (nc instanceof NativeNodeContainer) {
            return buildNativeNodeEnt(id, (NativeNodeContainer)nc, allowedActions);
        } else if (nc instanceof WorkflowManager) {
            return buildMetaNodeEnt(id, (WorkflowManager)nc, allowedActions);
        } else if (nc instanceof SubNodeContainer) {
            return buildComponentNodeEnt(id, (SubNodeContainer)nc, allowedActions);
        } else {
            throw new IllegalArgumentException(
                "Node container " + nc.getClass().getCanonicalName() + " cannot be mapped to a node entity.");
        }
    }

    private static AllowedActionsEnt buildAllowedActionsEnt(final NodeContainer nc,
        final DependentNodeProperties depNodeProps) {
        WorkflowManager parent = nc.getParent();
        NodeID id = nc.getID();
        return builder(AllowedActionsEntBuilder.class)
                .setCanExecute(depNodeProps.canExecuteNode(id))
                .setCanReset(depNodeProps.canResetNode(id))
                .setCanCancel(parent.canCancelNode(id))
                .build();
    }

    private static AllowedActionsEnt buildAllowedActionsEnt(final WorkflowManager wfm) {
        return builder(AllowedActionsEntBuilder.class)//
            .setCanReset(wfm.canResetAll())//
            .setCanExecute(wfm.canExecuteAll())//
            .setCanCancel(wfm.canCancelAll()).build();
    }

    private static MetaNodeEnt buildMetaNodeEnt(final NodeIDEnt id, final WorkflowManager wm,
        final AllowedActionsEnt allowedActions) {
        return builder(MetaNodeEntBuilder.class).setName(wm.getName()).setId(id)//
            .setOutPorts(buildMetaNodePortEnts(wm, false))//
            .setAnnotation(buildNodeAnnotationEnt(wm.getNodeAnnotation()))//
            .setInPorts(buildMetaNodePortEnts(wm, true))//
            .setPosition(buildXYEnt(wm.getUIInformation()))//
            .setState(buildMetaNodeStateEnt(wm.getNodeContainerState()))//
            .setKind(KindEnum.METANODE)//
            .setLink(getTemplateLink(wm))//
            .setAllowedActions(allowedActions).build();
    }

    private static MetaNodeStateEnt buildMetaNodeStateEnt(final NodeContainerState state) {
        org.knime.gateway.api.webui.entity.MetaNodeStateEnt.ExecutionStateEnum executionState;
        if (state.isExecutingRemotely() || state.isExecutionInProgress()) {
            executionState = org.knime.gateway.api.webui.entity.MetaNodeStateEnt.ExecutionStateEnum.EXECUTING;
        } else if (state.isExecuted()) {
            executionState = org.knime.gateway.api.webui.entity.MetaNodeStateEnt.ExecutionStateEnum.EXECUTED;
        } else {
            executionState = org.knime.gateway.api.webui.entity.MetaNodeStateEnt.ExecutionStateEnum.IDLE;
        }
        return builder(MetaNodeStateEntBuilder.class).setExecutionState(executionState).build();
    }

    private static List<MetaNodePortEnt> buildMetaNodePortEnts(final WorkflowManager wm, final boolean inPorts) {
        return buildNodePortEnts(wm, inPorts).stream().map(np -> { // NOSONAR
            NodeStateEnum nodeState;
            if (!inPorts) {
                nodeState = getNodeStateEnumForMetaNodePort(wm.getOutPort(np.getIndex()).getNodeState());
            } else {
                nodeState = null;
            }
            return builder(MetaNodePortEntBuilder.class)
                .setColor(np.getColor())
                .setConnectedVia(np.getConnectedVia())
                .setInactive(np.isInactive())
                .setIndex(np.getIndex())
                .setInfo(np.getInfo())
                .setName(np.getName())
                .setOptional(np.isOptional())
                .setType(np.getType())
                .setNodeState(nodeState)
                .build();
        }).collect(toList());
    }

    private static ComponentNodeEnt buildComponentNodeEnt(final NodeIDEnt id, final SubNodeContainer nc,
        final AllowedActionsEnt allowedActions) {
        String type = nc.getMetadata().getNodeType().map(ComponentNodeType::toString).orElse(null);
        return builder(ComponentNodeEntBuilder.class).setName(nc.getName())//
            .setId(id)//
            .setType(type == null ? null : org.knime.gateway.api.webui.entity.ComponentNodeEnt.TypeEnum.valueOf(type))//
            .setOutPorts(buildNodePortEnts(nc, false))//
            .setAnnotation(buildNodeAnnotationEnt(nc.getNodeAnnotation()))//
            .setInPorts(buildNodePortEnts(nc, true))//
            .setPosition(buildXYEnt(nc.getUIInformation()))//
            .setState(buildNodeStateEnt(nc))//
            .setIcon(createIconDataURL(nc.getMetadata().getIcon().orElse(null)))//
            .setKind(KindEnum.COMPONENT)//
            .setLink(getTemplateLink(nc))//
            .setAllowedActions(allowedActions).build();
    }

    private static List<NodePortEnt> buildNodePortEnts(final NodeContainer nc, final boolean inPorts) {
        List<NodePortEnt> res = new ArrayList<>();
        if (inPorts) {
            for (int i = 0; i < nc.getNrInPorts(); i++) {
                ConnectionContainer connection = nc.getParent().getIncomingConnectionFor(nc.getID(), i);
                NodeInPort inPort = nc.getInPort(i);
                res.add(buildNodePortEnt(inPort.getPortType(), inPort.getPortName(), null, i,
                    inPort.getPortType().isOptional(), null,
                    connection == null ? Collections.emptyList() : Collections.singletonList(connection)));
            }
        } else {
            for (int i = 0; i < nc.getNrOutPorts(); i++) {
                Set<ConnectionContainer> connections = nc.getParent().getOutgoingConnectionsFor(nc.getID(), i);
                NodeOutPort outPort = nc.getOutPort(i);
                res.add(buildNodePortEnt(outPort.getPortType(), outPort.getPortName(), outPort.getPortSummary(), i,
                    null, outPort.isInactive() ? outPort.isInactive() : null, connections));
            }
        }
        return res;
    }

    private static NodePortEnt buildNodePortEnt(final PortType ptype, final String name, final String info,
        final int portIdx, final Boolean isOptional, final Boolean isInactive,
        final Collection<ConnectionContainer> connections) {
        NodePortEntBuilder builder = builder(NodePortEntBuilder.class)
                .setIndex(portIdx)
                .setOptional(isOptional)
                .setInactive(isInactive)
                .setConnectedVia(connections.stream().map(EntityBuilderUtil::buildConnectionIDEnt)
                    .collect(Collectors.toList()))
                .setName(name)
                .setInfo(info);
        if (BufferedDataTable.TYPE.equals(ptype)) {
            builder.setType(org.knime.gateway.api.webui.entity.NodePortEnt.TypeEnum.TABLE);
        } else if (FlowVariablePortObject.TYPE.equals(ptype)) {
            builder.setType(org.knime.gateway.api.webui.entity.NodePortEnt.TypeEnum.FLOWVARIABLE);
        } else {
            builder.setType(org.knime.gateway.api.webui.entity.NodePortEnt.TypeEnum.OTHER);
            builder.setColor(hexStringColor(ptype.getColor()));
        }
        return builder.build();
    }

    private static ConnectionIDEnt buildConnectionIDEnt(final ConnectionContainer c) {
        return new ConnectionIDEnt(new NodeIDEnt(c.getDest()), c.getDestPort());
    }

    private static NodeAnnotationEnt buildNodeAnnotationEnt(final NodeAnnotation na) {
        if (na.getData().isDefault()) {
            return null;
        }
        TextAlignEnum textAlign;
        switch (na.getAlignment()) {
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

        List<StyleRangeEnt> styleRanges =
            Arrays.stream(na.getStyleRanges()).map(EntityBuilderUtil::buildStyleRangeEnt).collect(Collectors.toList());
        return builder(NodeAnnotationEntBuilder.class)
                .setTextAlign(textAlign)
                .setBackgroundColor(na.getBgColor() == DEFAULT_NODE_ANNOTATION_BG_COLOR ? null : hexStringColor(na
                    .getBgColor()))
                .setText(na.getText())
                .setStyleRanges(styleRanges)
                .setDefaultFontSize(na.getDefaultFontSize() > 0 ? convertFromPtToPx(na.getDefaultFontSize()) : null)
                .build();
    }

    private static NativeNodeEnt buildNativeNodeEnt(final NodeIDEnt id, final NativeNodeContainer nc,
        final AllowedActionsEnt allowedActions) {
        return builder(NativeNodeEntBuilder.class)//
            .setId(id)//
            .setOutPorts(buildNodePortEnts(nc, false))//
            .setAnnotation(buildNodeAnnotationEnt(nc.getNodeAnnotation()))//
            .setInPorts(buildNodePortEnts(nc, true))//
            .setPosition(buildXYEnt(nc.getUIInformation()))//
            .setKind(KindEnum.NODE)//
            .setState(buildNodeStateEnt(nc))//
            .setTemplateId(createTemplateId(nc.getNode().getFactory()))//
            .setAllowedActions(allowedActions).build();
    }

    private static NodeStateEnt buildNodeStateEnt(final SingleNodeContainer nc) {
        if (nc.isInactive()) {
            return null;
        }
        NodeContainerState ncState = nc.getNodeContainerState();
        NodeStateEntBuilder builder =
            builder(NodeStateEntBuilder.class).setExecutionState(getNodeExecutionStateEnum(ncState));
        NodeMessage nodeMessage = nc.getNodeMessage();
        if (nodeMessage.getMessageType() == Type.ERROR) {
            builder.setError(nodeMessage.getMessage());
        } else if (nodeMessage.getMessageType() == Type.WARNING) {
            builder.setWarning(nodeMessage.getMessage());
        } else {
            //
        }
        if (ncState.isExecutionInProgress()) {
            NodeProgressMonitor progressMonitor = nc.getProgressMonitor();
            Double progress = progressMonitor.getProgress();
            builder.setProgress(progress == null ? null : BigDecimal.valueOf(progress));
            builder.setProgressMessage(progressMonitor.getMessage());
        }
        return builder.build();
    }



    private static ExecutionStateEnum getNodeExecutionStateEnum(final NodeContainerState ncState) { // NOSONAR
        if (ncState.isConfigured()) {
            return ExecutionStateEnum.CONFIGURED;
        } else if (ncState.isExecuted()) {
            return ExecutionStateEnum.EXECUTED;
        } else if (isExecuting(ncState)) {
            return ExecutionStateEnum.EXECUTING;
        } else if (ncState.isIdle()) {
            return ExecutionStateEnum.IDLE;
        } else if (ncState.isWaitingToBeExecuted()) {
            return ExecutionStateEnum.QUEUED;
        } else if (ncState.isHalted()) {
            return ExecutionStateEnum.HALTED;
        } else {
            throw new IllegalStateException("Node container state cannot be mapped!");
        }
    }

    private static NodeStateEnum getNodeStateEnumForMetaNodePort(final NodeContainerState ncState) { // NOSONAR
        if (ncState.isConfigured()) {
            return NodeStateEnum.CONFIGURED;
        } else if (ncState.isExecuted()) {
            return NodeStateEnum.EXECUTED;
        } else if (isExecuting(ncState)) {
            return NodeStateEnum.EXECUTING;
        } else if (ncState.isIdle()) {
            return NodeStateEnum.IDLE;
        } else if (ncState.isWaitingToBeExecuted()) {
            return NodeStateEnum.QUEUED;
        } else if (ncState.isHalted()) {
            return NodeStateEnum.HALTED;
        } else {
            throw new IllegalStateException("Node container state cannot be mapped!");
        }
    }

    private static boolean isExecuting(final NodeContainerState ncState) {
        return (ncState.isExecutionInProgress() && !ncState.isWaitingToBeExecuted()) || ncState.isExecutingRemotely();
    }

    private static NodeTemplateEnt buildNodeTemplateEnt(final NativeNodeContainer nc) {
        NodeTemplateEntBuilder builder = builder(NodeTemplateEntBuilder.class)
            .setType(TypeEnum.valueOf(nc.getType().toString().toUpperCase()));
        if(nc.getType() != NodeType.Missing) {
            builder.setName(nc.getName())
                .setIcon(createIconDataURL(nc.getNode().getFactory()));
        } else {
            NodeFactory<? extends NodeModel> factory = nc.getNode().getFactory();
            NodeAndBundleInformation nodeInfo = ((MissingNodeFactory)factory).getNodeAndBundleInfo();
            builder.setName(nodeInfo.getNodeName().orElse("Unknown Name (MISSING)"));
        }
        return builder.build();
    }

    private static String createTemplateId(final NodeFactory<? extends NodeModel> nodeFactory) {
        String configHash = "";
        if (nodeFactory instanceof DynamicNodeFactory) {
            final NodeSettings settings = new NodeSettings("");
            nodeFactory.saveAdditionalFactorySettings(settings);
            configHash = ConfigUtils.contentBasedHashString(settings);
        }
        if (nodeFactory instanceof MissingNodeFactory) {
            NodeAndBundleInformation nodeInfo = ((MissingNodeFactory)nodeFactory).getNodeAndBundleInfo();
            return nodeInfo.getFactoryClass().orElse("unknown_missing_node_factory_" + UUID.randomUUID()) + configHash;
        } else {
            return nodeFactory.getClass().getCanonicalName() + configHash;
        }
    }

    private static String createIconDataURL(final NodeFactory<NodeModel> nodeFactory) {
        URL url = nodeFactory.getIcon();
        if (url != null) {
            try (InputStream in = url.openStream()) {
                return createIconDataURL(IOUtils.toByteArray(in));
            } catch (IOException ex) {
                NodeLogger.getLogger(EntityBuilderUtil.class)
                    .error(String.format("Icon for node '%s' couldn't be read", nodeFactory.getNodeName()), ex);
                return null;
            }
        } else {
            return null;
        }
    }

    private static String createIconDataURL(final byte[] iconData) {
        if (iconData != null) {
            String dataUrlPrefix = "data:image/png;base64,";
            return dataUrlPrefix + new String(Base64.encodeBase64(iconData), StandardCharsets.UTF_8);
        } else {
            return null;
        }
    }

    private static XYEnt buildXYEnt(final NodeUIInformation uiInfo) {
        int[] bounds = uiInfo.getBounds();
        return builder(XYEntBuilder.class).setX(bounds[0]).setY(bounds[1] + NODE_Y_POS_CORRECTION).build();
    }

    private static ConnectionEnt buildConnectionEnt(final ConnectionContainer cc,
        final boolean hasComponentProjectParent) {
        return builder(ConnectionEntBuilder.class).setDestNode(new NodeIDEnt(cc.getDest(), hasComponentProjectParent))//
            .setDestPort(cc.getDestPort())//
            .setSourceNode(new NodeIDEnt(cc.getSource(), hasComponentProjectParent)).setSourcePort(cc.getSourcePort())//
            .setFlowVariableConnection(cc.isFlowVariablePortConnection() ? cc.isFlowVariablePortConnection() : null)//
            .build();
    }
}

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

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
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
import java.util.stream.Collectors;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.knime.core.node.BufferedDataTable;
import org.knime.core.node.DynamicNodeFactory;
import org.knime.core.node.NodeFactory;
import org.knime.core.node.NodeLogger;
import org.knime.core.node.NodeModel;
import org.knime.core.node.NodeProgressMonitor;
import org.knime.core.node.NodeSettings;
import org.knime.core.node.port.PortType;
import org.knime.core.node.port.flowvariable.FlowVariablePortObject;
import org.knime.core.node.workflow.AnnotationData.StyleRange;
import org.knime.core.node.workflow.ComponentMetadata.ComponentNodeType;
import org.knime.core.node.workflow.ConnectionContainer;
import org.knime.core.node.workflow.NativeNodeContainer;
import org.knime.core.node.workflow.NodeAnnotation;
import org.knime.core.node.workflow.NodeContainer;
import org.knime.core.node.workflow.NodeContainerState;
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
import org.knime.gateway.api.entity.ConnectionIDEnt;
import org.knime.gateway.api.entity.NodeIDEnt;
import org.knime.gateway.api.webui.entity.AnnotationEnt.TextAlignEnum;
import org.knime.gateway.api.webui.entity.BoundsEnt;
import org.knime.gateway.api.webui.entity.BoundsEnt.BoundsEntBuilder;
import org.knime.gateway.api.webui.entity.ComponentNodeEnt;
import org.knime.gateway.api.webui.entity.ComponentNodeEnt.ComponentNodeEntBuilder;
import org.knime.gateway.api.webui.entity.ConnectionEnt;
import org.knime.gateway.api.webui.entity.ConnectionEnt.ConnectionEntBuilder;
import org.knime.gateway.api.webui.entity.NativeNodeEnt;
import org.knime.gateway.api.webui.entity.NativeNodeEnt.NativeNodeEntBuilder;
import org.knime.gateway.api.webui.entity.NodeAnnotationEnt;
import org.knime.gateway.api.webui.entity.NodeAnnotationEnt.NodeAnnotationEntBuilder;
import org.knime.gateway.api.webui.entity.NodeEnt;
import org.knime.gateway.api.webui.entity.NodeEnt.KindEnum;
import org.knime.gateway.api.webui.entity.NodeExecutionStateEnt;
import org.knime.gateway.api.webui.entity.NodeExecutionStateEnt.NodeExecutionStateEntBuilder;
import org.knime.gateway.api.webui.entity.NodeExecutionStateEnt.StateEnum;
import org.knime.gateway.api.webui.entity.NodePortEnt;
import org.knime.gateway.api.webui.entity.NodePortEnt.NodePortEntBuilder;
import org.knime.gateway.api.webui.entity.NodeTemplateEnt;
import org.knime.gateway.api.webui.entity.NodeTemplateEnt.NodeTemplateEntBuilder;
import org.knime.gateway.api.webui.entity.NodeTemplateEnt.TypeEnum;
import org.knime.gateway.api.webui.entity.StyleRangeEnt;
import org.knime.gateway.api.webui.entity.StyleRangeEnt.StyleRangeEntBuilder;
import org.knime.gateway.api.webui.entity.WorkflowAnnotationEnt;
import org.knime.gateway.api.webui.entity.WorkflowAnnotationEnt.WorkflowAnnotationEntBuilder;
import org.knime.gateway.api.webui.entity.WorkflowEnt;
import org.knime.gateway.api.webui.entity.WorkflowEnt.WorkflowEntBuilder;
import org.knime.gateway.api.webui.entity.WorkflowNodeEnt;
import org.knime.gateway.api.webui.entity.WorkflowNodeEnt.WorkflowNodeEntBuilder;
import org.knime.gateway.api.webui.entity.XYEnt;
import org.knime.gateway.api.webui.entity.XYEnt.XYEntBuilder;
import org.osgi.framework.FrameworkUtil;

/**
 * Collects helper methods to build entity instances basically from core.api-classes (e.g. WorkflowManager etc.).
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
public final class EntityBuilderUtil {

    /*
     * The node position in the java-ui refers to the upper left corner of the 'node figure' which also includes
     * the (not always visble) implicit flow variables ports. I.e. the position does NOT match with the upper left
     * corner of the node background image. However, this is used as reference point in the web-ui. Thus, we need
     * to correct the position in y direction by some pixels.
     * (the value is chosen according to org.knime.workbench.editor2.figures.AbstractPortFigure.getPortSizeNode())
     */
    private static final int NODE_Y_POS_CORRECTION = 9;

    private EntityBuilderUtil() {
        //utility class
    }

    /**
     * Builds a new {@link WorkflowEnt}.
     *
     * @param wfm the workflow manager to create the entity from
     * @param projectId the project id of the workflow (if its the top-level project workflow) or <code>null</code>
     * @return the newly created entity
     */
    public static WorkflowEnt buildWorkflowEnt(final WorkflowManager wfm, final String projectId) {
        try (WorkflowLock lock = wfm.lock()) {
            Collection<NodeContainer> nodeContainers = wfm.getNodeContainers();

            Map<String, NodeEnt> nodes = new HashMap<>();
            Map<String, NodeTemplateEnt> templates = new HashMap<>();
            for (NodeContainer nc : nodeContainers) {
                NodeEnt nodeEnt = buildNodeEnt(nc);
                nodes.put(nodeEnt.getId().toString(), nodeEnt);
                if (nc instanceof NativeNodeContainer) {
                    String templateId = ((NativeNodeEnt)nodeEnt).getTemplateId();
                    templates.computeIfAbsent(templateId, id -> buildNodeTemplateEnt((NativeNodeContainer)nc));
                }
            }
            Map<String, ConnectionEnt> connections =
                wfm.getConnectionContainers().stream().map(EntityBuilderUtil::buildConnectionEnt).collect(
                    Collectors.toMap(c -> new ConnectionIDEnt(c.getDestNode(), c.getDestPort()).toString(), c -> c)); // NOSONAR
            List<WorkflowAnnotationEnt> annotations =
                wfm.getWorkflowAnnotations().stream().map(EntityBuilderUtil::buildWorkflowAnnotationEnt)
                    .collect(Collectors.toList());
            return builder(WorkflowEntBuilder.class)
                .setName(wfm.getName())
                .setNodes(nodes)
                .setNodeTemplates(templates)
                .setConnections(connections)
                .setWorkflowAnnotations(annotations)
                .setProjectId(wfm.isProject() ? projectId : null)
                .build();
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
                .setTextAlign(textAlign)
                .setBackgroundColor(hexStringColor(wa.getBgColor()))
                .setBorderColor(hexStringColor(wa.getBorderColor()))
                .setBorderWidth(wa.getBorderSize())
                .setBounds(bounds)
                .setText(wa.getText())
                .setStyleRanges(styleRanges)
                .setDefaultFontSize(wa.getDefaultFontSize() > 0 ? wa.getDefaultFontSize() : null)
                .build();
    }

    private static StyleRangeEnt buildStyleRangeEnt(final StyleRange sr) {
        StyleRangeEntBuilder builder = builder(StyleRangeEntBuilder.class)
                .setFontSize(sr.getFontSize())
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
                .setAnnotation(buildNodeAnnotationEnt(wm.getNodeAnnotation()))
                .setInPorts(buildNodePortEnts(wm, true))
                .setPosition(buildXYEnt(wm.getUIInformation()))
                .setKind(KindEnum.METANODE).build();
    }

    /**
     * Builds a new {@link ComponentNodeEnt}.
     *
     * @param nc the subnode container to create the node entity from
     * @return the newly created entity
     */
    public static ComponentNodeEnt buildComponentNodeEnt(final SubNodeContainer nc) {
        String type = nc.getMetadata().getNodeType().map(ComponentNodeType::toString).orElse(null);
        return builder(ComponentNodeEntBuilder.class).setName(nc.getName())
                .setId(new NodeIDEnt(nc.getID()))
                .setType(type == null ? null : org.knime.gateway.api.webui.entity.ComponentNodeEnt.TypeEnum.valueOf(
                    type))
                .setOutPorts(buildNodePortEnts(nc, false))
                .setAnnotation(buildNodeAnnotationEnt(nc.getNodeAnnotation()))
                .setInPorts(buildNodePortEnts(nc, true))
                .setPosition(buildXYEnt(nc.getUIInformation()))
                .setState(buildNodeExecutionStateEnt(nc))
                .setIcon(createIconDataURL(nc.getMetadata().getIcon().orElse(null)))
                .setKind(KindEnum.COMPONENT).build();
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
                .setBackgroundColor(hexStringColor(na.getBgColor()))
                .setBorderColor(hexStringColor(na.getBorderColor()))
                .setBorderWidth(na.getBorderSize())
                .setText(na.getText())
                .setStyleRanges(styleRanges)
                .setDefaultFontSize(na.getDefaultFontSize() > 0 ? na.getDefaultFontSize() : null)
                .build();
    }

    private static NativeNodeEnt buildNativeNodeEnt(final NativeNodeContainer nc) {
        return builder(NativeNodeEntBuilder.class)
            .setId(new NodeIDEnt(nc.getID()))
            .setOutPorts(buildNodePortEnts(nc, false))
            .setAnnotation(buildNodeAnnotationEnt(nc.getNodeAnnotation()))
            .setInPorts(buildNodePortEnts(nc, true))
            .setPosition(buildXYEnt(nc.getUIInformation()))
            .setKind(KindEnum.NODE)
            .setState(buildNodeExecutionStateEnt(nc))
            .setTemplateId(createTemplateId(nc.getNode().getFactory())).build();
    }

    private static NodeExecutionStateEnt buildNodeExecutionStateEnt(final SingleNodeContainer nc) {
        if (nc.isInactive()) {
            return null;
        }
        NodeContainerState ncState = nc.getNodeContainerState();
        NodeExecutionStateEntBuilder builder =
            builder(NodeExecutionStateEntBuilder.class).setState(getStateEnum(ncState));
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

    private static StateEnum getStateEnum(final NodeContainerState ncState) { // NOSONAR
        if (ncState.isConfigured()) {
            return StateEnum.CONFIGURED;
        } else if (ncState.isExecuted()) {
            return StateEnum.EXECUTED;
        } else if (ncState.isExecutionInProgress() || ncState.isExecutingRemotely()) {
            return StateEnum.EXECUTING;
        } else if (ncState.isIdle()) {
            return StateEnum.IDLE;
        } else if (ncState.isWaitingToBeExecuted()) {
            return StateEnum.QUEUED;
        } else if (ncState.isHalted()) {
            return StateEnum.HALTED;
        } else {
            throw new IllegalStateException("Node container state cannot be mapped!");
        }
    }

    private static NodeTemplateEnt buildNodeTemplateEnt(final NativeNodeContainer nc) {
        return builder(NodeTemplateEntBuilder.class)
                .setName(nc.getName())
                .setType(TypeEnum.valueOf(nc.getType().toString().toUpperCase()))
                .setIcon(createIconDataURL(nc.getNode().getFactory()))
                .build();
    }

    private static String createTemplateId(final NodeFactory<NodeModel> nodeFactory) {
        String configHash = "";
        if (nodeFactory instanceof DynamicNodeFactory) {
            final NodeSettings settings = new NodeSettings("");
            nodeFactory.saveAdditionalFactorySettings(settings);
            configHash = ConfigUtils.contentBasedHashString(settings);
        }
        return nodeFactory.getClass().getCanonicalName() + configHash;
    }

    private static String createIconDataURL(final NodeFactory<NodeModel> nodeFactory) {
        URL url = nodeFactory.getIcon();
        if (url != null) {
            ensureBundleURL(nodeFactory.getClass(), url);
            try (InputStream in = url.openStream()) { // NOSONAR - URL checked above to avoid abuse
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

    /*
     * Makes sure that the provided url points to a file in the bundle the factory class is part of.
     * Otherwise an exception is thrown.
     */
    private static void ensureBundleURL(final Class<? extends NodeFactory> factoryClass, final URL url) {
        String bundleRoot = FrameworkUtil.getBundle(factoryClass).getResource("/").toExternalForm();
        if (!url.toExternalForm().startsWith(bundleRoot.substring(0, bundleRoot.length() - 1))) {
            throw new IllegalStateException("An icon URL references a file outside of the node's bundle. Icon URL: "
                + url + "; bundle root URL: " + bundleRoot);
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

    private static ConnectionEnt buildConnectionEnt(final ConnectionContainer cc) {
        return builder(ConnectionEntBuilder.class).setDestNode(new NodeIDEnt(cc.getDest()))
            .setDestPort(cc.getDestPort())
            .setSourceNode(new NodeIDEnt(cc.getSource())).setSourcePort(cc.getSourcePort())
            .setFlowVariableConnection(
                cc.isFlowVariablePortConnection() ? cc.isFlowVariablePortConnection() : null)
            .build();
    }
}

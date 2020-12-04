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
import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.knime.gateway.api.entity.EntityBuilderManager.builder;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.knime.core.internal.ReferencedFile;
import org.knime.core.node.BufferedDataTable;
import org.knime.core.node.DynamicNodeFactory;
import org.knime.core.node.NodeFactory;
import org.knime.core.node.NodeFactory.NodeType;
import org.knime.core.node.NodeLogger;
import org.knime.core.node.NodeModel;
import org.knime.core.node.NodeProgressMonitor;
import org.knime.core.node.NodeSettings;
import org.knime.core.node.dialog.DialogNodeValue;
import org.knime.core.node.dialog.SubNodeDescriptionProvider;
import org.knime.core.node.exec.ThreadNodeExecutionJobManager;
import org.knime.core.node.missing.MissingNodeFactory;
import org.knime.core.node.port.PortType;
import org.knime.core.node.port.flowvariable.FlowVariablePortObject;
import org.knime.core.node.util.NodeExecutionJobManagerPool;
import org.knime.core.node.workflow.AnnotationData.StyleRange;
import org.knime.core.node.workflow.ComponentMetadata;
import org.knime.core.node.workflow.ComponentMetadata.ComponentNodeType;
import org.knime.core.node.workflow.ConnectionContainer;
import org.knime.core.node.workflow.DependentNodeProperties;
import org.knime.core.node.workflow.NativeNodeContainer;
import org.knime.core.node.workflow.NodeAnnotation;
import org.knime.core.node.workflow.NodeContainer;
import org.knime.core.node.workflow.NodeContainerParent;
import org.knime.core.node.workflow.NodeContainerState;
import org.knime.core.node.workflow.NodeContainerTemplate;
import org.knime.core.node.workflow.NodeExecutionJobManager;
import org.knime.core.node.workflow.NodeExecutionJobManagerFactory;
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
import org.knime.core.node.workflow.WorkflowPersistor;
import org.knime.core.node.workflow.action.InteractiveWebViewsResult;
import org.knime.core.node.workflow.action.InteractiveWebViewsResult.SingleInteractiveWebViewResult;
import org.knime.core.util.ConfigUtils;
import org.knime.core.util.workflowalizer.NodeAndBundleInformation;
import org.knime.core.util.workflowalizer.WorkflowGroupMetadata;
import org.knime.core.util.workflowalizer.WorkflowSetMeta.Link;
import org.knime.core.util.workflowalizer.Workflowalizer;
import org.knime.gateway.api.entity.AnnotationIDEnt;
import org.knime.gateway.api.entity.ConnectionIDEnt;
import org.knime.gateway.api.entity.NodeIDEnt;
import org.knime.gateway.api.webui.entity.AllowedActionsEnt;
import org.knime.gateway.api.webui.entity.AllowedActionsEnt.AllowedActionsEntBuilder;
import org.knime.gateway.api.webui.entity.AnnotationEnt.TextAlignEnum;
import org.knime.gateway.api.webui.entity.BoundsEnt;
import org.knime.gateway.api.webui.entity.BoundsEnt.BoundsEntBuilder;
import org.knime.gateway.api.webui.entity.ComponentNodeAndTemplateEnt;
import org.knime.gateway.api.webui.entity.ComponentNodeEnt;
import org.knime.gateway.api.webui.entity.ComponentNodeEnt.ComponentNodeEntBuilder;
import org.knime.gateway.api.webui.entity.ComponentNodeTemplateEnt;
import org.knime.gateway.api.webui.entity.ComponentNodeTemplateEnt.ComponentNodeTemplateEntBuilder;
import org.knime.gateway.api.webui.entity.ConnectionEnt;
import org.knime.gateway.api.webui.entity.ConnectionEnt.ConnectionEntBuilder;
import org.knime.gateway.api.webui.entity.CustomJobManagerEnt;
import org.knime.gateway.api.webui.entity.CustomJobManagerEnt.CustomJobManagerEntBuilder;
import org.knime.gateway.api.webui.entity.JobManagerEnt;
import org.knime.gateway.api.webui.entity.JobManagerEnt.JobManagerEntBuilder;
import org.knime.gateway.api.webui.entity.LinkEnt;
import org.knime.gateway.api.webui.entity.LinkEnt.LinkEntBuilder;
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
import org.knime.gateway.api.webui.entity.NativeNodeTemplateEnt;
import org.knime.gateway.api.webui.entity.NativeNodeTemplateEnt.NativeNodeTemplateEntBuilder;
import org.knime.gateway.api.webui.entity.NativeNodeTemplateEnt.TypeEnum;
import org.knime.gateway.api.webui.entity.NodeAnnotationEnt;
import org.knime.gateway.api.webui.entity.NodeAnnotationEnt.NodeAnnotationEntBuilder;
import org.knime.gateway.api.webui.entity.NodeDialogOptionsEnt;
import org.knime.gateway.api.webui.entity.NodeDialogOptionsEnt.NodeDialogOptionsEntBuilder;
import org.knime.gateway.api.webui.entity.NodeDialogOptions_fieldsEnt;
import org.knime.gateway.api.webui.entity.NodeDialogOptions_fieldsEnt.NodeDialogOptions_fieldsEntBuilder;
import org.knime.gateway.api.webui.entity.NodeEnt;
import org.knime.gateway.api.webui.entity.NodeEnt.KindEnum;
import org.knime.gateway.api.webui.entity.NodePortAndTemplateEnt;
import org.knime.gateway.api.webui.entity.NodePortEnt;
import org.knime.gateway.api.webui.entity.NodePortEnt.NodePortEntBuilder;
import org.knime.gateway.api.webui.entity.NodePortTemplateEnt;
import org.knime.gateway.api.webui.entity.NodePortTemplateEnt.NodePortTemplateEntBuilder;
import org.knime.gateway.api.webui.entity.NodeStateEnt;
import org.knime.gateway.api.webui.entity.NodeStateEnt.ExecutionStateEnum;
import org.knime.gateway.api.webui.entity.NodeStateEnt.NodeStateEntBuilder;
import org.knime.gateway.api.webui.entity.NodeViewDescriptionEnt;
import org.knime.gateway.api.webui.entity.NodeViewDescriptionEnt.NodeViewDescriptionEntBuilder;
import org.knime.gateway.api.webui.entity.NodeViewEnt;
import org.knime.gateway.api.webui.entity.NodeViewEnt.NodeViewEntBuilder;
import org.knime.gateway.api.webui.entity.PortViewEnt;
import org.knime.gateway.api.webui.entity.PortViewEnt.PortViewEntBuilder;
import org.knime.gateway.api.webui.entity.ProjectMetadataEnt;
import org.knime.gateway.api.webui.entity.ProjectMetadataEnt.ProjectMetadataEntBuilder;
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
import org.xml.sax.SAXException;

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

    private static final String STREAMING_JOB_MANAGER_ID =
        "org.knime.core.streaming.SimpleStreamerNodeExecutionJobManagerFactory";

    private static final Map<String, NativeNodeTemplateEnt> m_nativeNodeTemplateCache = new HashMap<>();

    private static final Map<String, NodePortTemplateEnt> m_nodePortTemplateCache = new HashMap<>();

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
            Map<String, NativeNodeTemplateEnt> templates = new HashMap<>();

            DependentNodeProperties depNodeProps = null;
            if (includeInfoOnAllowedActions) {
                depNodeProps = wfm.determineDependentNodeProperties();
            }
            final boolean hasComponentProjectParent = wfm.getProjectComponent().isPresent();
            Function<NodeID, NodeIDEnt> buildNodeIDEnt = id -> new NodeIDEnt(id, hasComponentProjectParent);
            for (NodeContainer nc : nodeContainers) {
                buildAndAddNodeEnt(buildNodeIDEnt.apply(nc.getID()), nc, nodes, templates, depNodeProps,
                    buildNodeIDEnt);
            }
            Map<String, ConnectionEnt> connections = wfm.getConnectionContainers().stream()
                .map(cc -> buildConnectionEnt(cc, buildNodeIDEnt)).collect(
                    Collectors.toMap(c -> new ConnectionIDEnt(c.getDestNode(), c.getDestPort()).toString(), c -> c)); // NOSONAR
            List<WorkflowAnnotationEnt> annotations =
                wfm.getWorkflowAnnotations().stream().map(EntityBuilderUtil::buildWorkflowAnnotationEnt)
                    .collect(Collectors.toList());
            WorkflowInfoEnt info = buildWorkflowInfoEnt(wfm, buildNodeIDEnt);
            return builder(WorkflowEntBuilder.class)
                .setInfo(info)//
                .setNodes(nodes)//
                .setNodeTemplates(templates)//
                .setConnections(connections)//
                .setWorkflowAnnotations(annotations)//
                .setAllowedActions(includeInfoOnAllowedActions ? buildAllowedActionsEnt(wfm) : null)//
                .setParents(buildParentWorkflowInfoEnts(wfm, buildNodeIDEnt))//
                .setMetaInPorts(buildMetaPortsEnt(wfm, true, buildNodeIDEnt))//
                .setMetaOutPorts(buildMetaPortsEnt(wfm, false, buildNodeIDEnt))//
                .setProjectMetadata(wfm.isProject() ? buildProjectMetadataEnt(wfm) : null)//
                .setComponentMetadata(isComponentWFM(wfm) ? buildComponentNodeTemplateEnt(getParentComponent(wfm))
                    : null)//
                .build();
        }
    }

    private static boolean isComponentWFM(final WorkflowManager wfm) {
        return wfm.getDirectNCParent() instanceof SubNodeContainer;
    }

    private static MetaPortsEnt buildMetaPortsEnt(final WorkflowManager wfm, final boolean incoming,
        final Function<NodeID, NodeIDEnt> buildNodeIDEnt) {
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
                    port.isInactive() ? Boolean.TRUE : null, connections, buildNodeIDEnt));
            }
        } else {
            int nrPorts = wfm.getNrWorkflowOutgoingPorts();
            for (int i = 0; i < nrPorts; i++) {
                ConnectionContainer connection = wfm.getIncomingConnectionFor(wfm.getID(), i);
                NodeInPort port = wfm.getWorkflowOutgoingPort(i);
                ports.add(buildNodePortEnt(port.getPortType(), port.getPortName(), null, i, null, null,
                    connection != null ? singleton(connection) : emptyList(), buildNodeIDEnt));
            }
        }
        builder.setPorts(ports);
        NodeUIInformation barUIInfo = incoming ? wfm.getInPortsBarUIInfo() : wfm.getOutPortsBarUIInfo();
        if (barUIInfo != null) {
            builder.setXPos(barUIInfo.getBounds()[0]);
        }
        return builder.build();
    }

    private static WorkflowInfoEnt buildWorkflowInfoEnt(final WorkflowManager wfm,
        final Function<NodeID, NodeIDEnt> buildNodeIDEnt) {
        NodeContainerTemplate template;
        if (wfm.getDirectNCParent() instanceof SubNodeContainer) {
            template = (SubNodeContainer)wfm.getDirectNCParent();
        } else {
            template = wfm;
        }
        return builder(WorkflowInfoEntBuilder.class)//
            .setName(wfm.getName())//
            .setContainerId(getContainerId(wfm, buildNodeIDEnt))//
            .setContainerType(getContainerType(wfm))//
            .setLinked(getTemplateLink(template) == null ? null : Boolean.TRUE)//
            .build();
    }

    private static String getTemplateLink(final NodeContainerTemplate nct) {
        if (nct instanceof SubNodeContainer && ((SubNodeContainer)nct).isProject()) {
            return null;
        }
        URI sourceURI = nct.getTemplateInformation().getSourceURI();
        return sourceURI == null ? null : sourceURI.toString();
    }

    private static NodeIDEnt getContainerId(final WorkflowManager wfm,
        final Function<NodeID, NodeIDEnt> buildNodeIDEnt) {
        if (wfm.isProject()) {
            return null;
        }

        NodeContainerParent ncParent = wfm.getDirectNCParent();
        if (ncParent instanceof SubNodeContainer) {
            // it's a component's workflow
            return buildNodeIDEnt.apply(((SubNodeContainer)ncParent).getID());
        } else {
            return buildNodeIDEnt.apply(wfm.getID());
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

    private static List<WorkflowInfoEnt> buildParentWorkflowInfoEnts(final WorkflowManager wfm,
        final Function<NodeID, NodeIDEnt> buildNodeIDEnt) {
        if (wfm.isProject() || wfm.isComponentProjectWFM()) {
            return null; // NOSONAR
        }
        List<WorkflowInfoEnt> parents = new ArrayList<>();
        WorkflowManager parent = wfm;
        do {
            parent = getWorkflowParent(parent);
            parents.add(buildWorkflowInfoEnt(parent, buildNodeIDEnt));
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

    private static ProjectMetadataEnt buildProjectMetadataEnt(final WorkflowManager wfm) {
        assert wfm.isProject();
        final ReferencedFile rf = wfm.getWorkingDir();
        File metadataFile = new File(rf.getFile(), WorkflowPersistor.METAINFO_FILE);
        if (metadataFile.exists()) {
            WorkflowGroupMetadata metadata;
            try {
                metadata = Workflowalizer.readWorkflowGroup(metadataFile.toPath());
            } catch (XPathExpressionException | ParserConfigurationException | SAXException | IOException ex) {
                NodeLogger.getLogger(EntityBuilderUtil.class).error("Workflow metadata could not be read", ex);
                return null;
            }
            return builder(ProjectMetadataEntBuilder.class)
                    .setDescription(metadata.getDescription().orElse(null))
                    .setLastEdit(wfm.getAuthorInformation().getLastEditDate()
                        // the Date class doesn't support time zones. We just assume UTC here to create an OffsetDateTime
                        .map(date -> date.toInstant().atOffset(ZoneOffset.UTC)).orElse(null))
                    .setLinks(metadata.getLinks().map(EntityBuilderUtil::buildLinkEnts).orElse(null))
                    .setTags(metadata.getTags().orElse(null))
                    .setTitle(metadata.getTitle().orElse(null)).build();
        } else {
            return null;
        }
    }

    private static List<LinkEnt> buildLinkEnts(final List<Link> links) {
        return links.stream()
            .map(link -> builder(LinkEntBuilder.class).setUrl(link.getUrl()).setText(link.getText()).build())
            .collect(Collectors.toList());
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
        final Map<String, NativeNodeTemplateEnt> templates, final DependentNodeProperties depNodeProps,
        final Function<NodeID, NodeIDEnt> buildNodeIDEnt) {
        NodeEnt nodeEnt =
            depNodeProps != null ? buildNodeEntWithInfoOnAllowedActions(id, nc, depNodeProps, buildNodeIDEnt)
                : buildNodeEnt(id, nc, buildNodeIDEnt);
        nodes.put(nodeEnt.getId().toString(), nodeEnt);
        if (nc instanceof NativeNodeContainer) {
            String templateId = ((NativeNodeEnt)nodeEnt).getTemplateId();
            templates.computeIfAbsent(templateId,
                tid -> buildOrGetFromCacheNativeNodeTemplateEnt(templateId, (NativeNodeContainer)nc));
        }
    }

    private static NodeEnt buildNodeEnt(final NodeIDEnt id, final NodeContainer nc,
        final Function<NodeID, NodeIDEnt> buildNodeIDEnt) {
        return buildNodeEnt(id, nc, null, buildNodeIDEnt);
    }

    private static NodeEnt buildNodeEntWithInfoOnAllowedActions(final NodeIDEnt id, final NodeContainer nc,
        final DependentNodeProperties depNodeProps, final Function<NodeID, NodeIDEnt> buildNodeIDEnt) {
        return buildNodeEnt(id, nc, buildAllowedActionsEnt(nc, depNodeProps), buildNodeIDEnt);
    }

    private static NodeEnt buildNodeEnt(final NodeIDEnt id, final NodeContainer nc,
        final AllowedActionsEnt allowedActions, final Function<NodeID, NodeIDEnt> buildNodeIDEnt) {
        if (nc instanceof NativeNodeContainer) {
            return buildNativeNodeEnt(id, (NativeNodeContainer)nc, allowedActions, buildNodeIDEnt);
        } else if (nc instanceof WorkflowManager) {
            return buildMetaNodeEnt(id, (WorkflowManager)nc, allowedActions, buildNodeIDEnt);
        } else if (nc instanceof SubNodeContainer) {
            return buildComponentNodeEnt(id, (SubNodeContainer)nc, allowedActions, buildNodeIDEnt);
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
        final AllowedActionsEnt allowedActions, final Function<NodeID, NodeIDEnt> buildNodeIDEnt) {
        return builder(MetaNodeEntBuilder.class).setName(wm.getName()).setId(id)//
            .setOutPorts(buildMetaNodePortEnts(wm, false, buildNodeIDEnt))//
            .setAnnotation(buildNodeAnnotationEnt(wm.getNodeAnnotation()))//
            .setInPorts(buildMetaNodePortEnts(wm, true, buildNodeIDEnt))//
            .setPosition(buildXYEnt(wm.getUIInformation()))//
            .setState(buildMetaNodeStateEnt(wm.getNodeContainerState()))//
            .setKind(KindEnum.METANODE)//
            .setLink(getTemplateLink(wm))//
            .setAllowedActions(allowedActions)//
            .setDialog(wm.hasDialog() ? Boolean.TRUE : null)//
            .setJobManager(buildJobManagerEnt(wm)).build();
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

    private static List<MetaNodePortEnt> buildMetaNodePortEnts(final WorkflowManager wm, final boolean inPorts,
        final Function<NodeID, NodeIDEnt> buildNodeIDEnt) {
        return buildNodePortEnts(wm, inPorts, buildNodeIDEnt).stream().map(np -> { // NOSONAR
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
        final AllowedActionsEnt allowedActions, final Function<NodeID, NodeIDEnt> buildNodeIDEnt) {
        ComponentMetadata metadata = nc.getMetadata();
        String type = metadata.getNodeType().map(ComponentNodeType::toString).orElse(null);
        return builder(ComponentNodeEntBuilder.class).setName(nc.getName())//
            .setId(id)//
            .setType(type == null ? null : org.knime.gateway.api.webui.entity.ComponentNodeEnt.TypeEnum.valueOf(type))//
            .setOutPorts(buildNodePortEnts(nc, false, buildNodeIDEnt))//
            .setAnnotation(buildNodeAnnotationEnt(nc.getNodeAnnotation()))//
            .setInPorts(buildNodePortEnts(nc, true, buildNodeIDEnt))//
            .setPosition(buildXYEnt(nc.getUIInformation()))//
            .setState(buildNodeStateEnt(nc))//
            .setIcon(createIconDataURL(nc.getMetadata().getIcon().orElse(null)))//
            .setKind(KindEnum.COMPONENT)//
            .setLink(getTemplateLink(nc))//
            .setView(buildNodeViewEnt(nc))//
            .setAllowedActions(allowedActions)//
            .setDialog(nc.hasDialog() ? Boolean.TRUE : null)//
            .setJobManager(buildJobManagerEnt(nc)).build();
    }

    private static NodeViewEnt buildNodeViewEnt(final SubNodeContainer nc) {
        if (nc.getInteractiveWebViews().size() > 0) {
            return buildNodeViewEnt("Interactive View: " + nc.getName(),
                nc.getNodeContainerState().isExecuted());
        } else {
            return null;
        }
    }

    private static NodeViewEnt buildNodeViewEnt(final NativeNodeContainer nc) {
        InteractiveWebViewsResult interactiveWebViews = nc.getInteractiveWebViews();
        if (interactiveWebViews.size() == 1) {
            return buildNodeViewEnt(interactiveWebViews.get(0).getViewName(), nc.getNodeContainerState().isExecuted());
        } else {
            return null;
        }
    }

    private static NodeViewEnt buildNodeViewEnt(final String name, final boolean isAvailable) {
        return builder(NodeViewEntBuilder.class).setName(name).setAvailable(isAvailable).build();
    }

    private static List<NodePortEnt> buildNodePortEnts(final NodeContainer nc, final boolean inPorts,
        final Function<NodeID, NodeIDEnt> buildNodeIDEnt) {
        List<NodePortEnt> res = new ArrayList<>();
        if (inPorts) {
            for (int i = 0; i < nc.getNrInPorts(); i++) {
                ConnectionContainer connection = nc.getParent().getIncomingConnectionFor(nc.getID(), i);
                NodeInPort inPort = nc.getInPort(i);
                res.add(buildNodePortEnt(inPort.getPortType(), inPort.getPortName(), null, i,
                    inPort.getPortType().isOptional(), null,
                    connection == null ? Collections.emptyList() : Collections.singletonList(connection),
                    buildNodeIDEnt));
            }
        } else {
            for (int i = 0; i < nc.getNrOutPorts(); i++) {
                Set<ConnectionContainer> connections = nc.getParent().getOutgoingConnectionsFor(nc.getID(), i);
                NodeOutPort outPort = nc.getOutPort(i);
                res.add(buildNodePortEnt(outPort.getPortType(), outPort.getPortName(), outPort.getPortSummary(), i,
                    null, outPort.isInactive() ? outPort.isInactive() : null, connections, buildNodeIDEnt));
            }
        }
        return res;
    }

    private static NodePortEnt buildNodePortEnt(final PortType ptype, final String name, final String info,
        final int portIdx, final Boolean isOptional, final Boolean isInactive,
        final Collection<ConnectionContainer> connections, final Function<NodeID, NodeIDEnt> buildNodeIDEnt) {
        NodePortAndTemplateEnt.TypeEnum resPortType = getNodePortTemplateType(ptype);
        return builder(NodePortEntBuilder.class).setIndex(portIdx)//
            .setOptional(isOptional)//
            .setInactive(isInactive)//
            .setConnectedVia(connections.stream().map(cc -> buildConnectionIDEnt(cc, buildNodeIDEnt))
                .collect(Collectors.toList()))//
            .setName(name)//
            .setInfo(info)//
            .setType(resPortType)//
            .setColor(resPortType == NodePortAndTemplateEnt.TypeEnum.OTHER ? hexStringColor(ptype.getColor()) : null)//
            .setView(buildPortViewEnt(ptype))//
            .build();
    }

    private static PortViewEnt buildPortViewEnt(final PortType ptype) {
        BuildInWebPortViewType portViewType = BuildInWebPortViewType.getPortViewTypeFor(ptype).orElse(null);
        if (portViewType != null) {
            return builder(PortViewEntBuilder.class).setType(PortViewEnt.TypeEnum.valueOf(portViewType.toString()))
                .build();
        } else {
            return null;
        }
    }

    private static NodePortAndTemplateEnt.TypeEnum getNodePortTemplateType(final PortType ptype) {
        if (BufferedDataTable.TYPE.equals(ptype)) {
            return NodePortAndTemplateEnt.TypeEnum.TABLE;
        } else if (FlowVariablePortObject.TYPE.equals(ptype)) {
            return NodePortAndTemplateEnt.TypeEnum.FLOWVARIABLE;
        } else {
            return NodePortAndTemplateEnt.TypeEnum.OTHER;
        }
    }

    private static ConnectionIDEnt buildConnectionIDEnt(final ConnectionContainer c,
        final Function<NodeID, NodeIDEnt> buildNodeIDEnt) {
        return new ConnectionIDEnt(buildNodeIDEnt.apply(c.getDest()), c.getDestPort());
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
        final AllowedActionsEnt allowedActions, final Function<NodeID, NodeIDEnt> buildNodeIDEnt) {
        return builder(NativeNodeEntBuilder.class)//
            .setId(id)//
            .setOutPorts(buildNodePortEnts(nc, false, buildNodeIDEnt))//
            .setAnnotation(buildNodeAnnotationEnt(nc.getNodeAnnotation()))//
            .setInPorts(buildNodePortEnts(nc, true, buildNodeIDEnt))//
            .setPosition(buildXYEnt(nc.getUIInformation()))//
            .setKind(KindEnum.NODE)//
            .setState(buildNodeStateEnt(nc))//
            .setTemplateId(createTemplateId(nc.getNode().getFactory()))//
            .setAllowedActions(allowedActions)//
            .setView(buildNodeViewEnt(nc))//
            .setDialog(nc.hasDialog() ? Boolean.TRUE : null)//
            .setJobManager(buildJobManagerEnt(nc))//
            .build();
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


    private static NativeNodeTemplateEnt buildOrGetFromCacheNativeNodeTemplateEnt(final String templateId,
        final NativeNodeContainer nc) {
        return m_nativeNodeTemplateCache.computeIfAbsent(templateId, id -> buildNativeNodeTemplateEnt(nc));
    }

    private static NativeNodeTemplateEnt buildNativeNodeTemplateEnt(final NativeNodeContainer nc) {
        NativeNodeTemplateEntBuilder builder = builder(NativeNodeTemplateEntBuilder.class)
            .setType(TypeEnum.valueOf(nc.getType().toString().toUpperCase()));
        if (nc.getType() != NodeType.Missing) {
            builder.setName(nc.getName()).setIcon(createIconDataURL(nc.getNode().getFactory()));
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

    private static ComponentNodeTemplateEnt buildComponentNodeTemplateEnt(final SubNodeContainer snc) {
        if (snc != null) {
            ComponentMetadata metadata = snc.getMetadata();
            String type = metadata.getNodeType().map(ComponentNodeType::toString).orElse(null);
            return builder(ComponentNodeTemplateEntBuilder.class)//
                .setName(snc.getName())//
                .setIcon(createIconDataURL(metadata.getIcon().orElse(null)))//
                .setType(type == null ? null : ComponentNodeAndTemplateEnt.TypeEnum.valueOf(type))//
                .setDescription(metadata.getDescription().orElse(null))//
                .setOptions(buildNodeDialogOptionsEnts(snc))//
                .setViews(buildNodeViewDescriptionEnts(snc))//
                .setInPorts(buildComponentInNodePortTemplateEnts(metadata, snc))//
                .setOutPorts(buildComponentOutNodePortTemplateEnts(metadata, snc))//
                .build();
        }
        return null;
    }

    private static SubNodeContainer getParentComponent(final WorkflowManager wfm) {
        NodeContainerParent ncParent = wfm.getDirectNCParent();
        return ncParent instanceof SubNodeContainer ? (SubNodeContainer)ncParent : null;
    }

    private static List<NodeDialogOptionsEnt> buildNodeDialogOptionsEnts(final SubNodeContainer snc) {
        List<SubNodeDescriptionProvider<? extends DialogNodeValue>> descs = snc.getDialogDescriptions();
        if (!descs.isEmpty()) {
            List<NodeDialogOptions_fieldsEnt> fields = descs.stream()
                .map(d -> builder(NodeDialogOptions_fieldsEntBuilder.class).setName(d.getLabel())
                    .setDescription(d.getDescription()).build())//
                .collect(toList());
            return singletonList(builder(NodeDialogOptionsEntBuilder.class).setFields(fields).build());
        } else {
            return null; // NOSONAR
        }
    }

    private static List<NodeViewDescriptionEnt> buildNodeViewDescriptionEnts(final SubNodeContainer snc) {
        InteractiveWebViewsResult interactiveWebViews = snc.getInteractiveWebViews();
        List<NodeViewDescriptionEnt> res = new ArrayList<>();
        if (interactiveWebViews.size() > 0) {
            for (int i = 0; i < interactiveWebViews.size(); i++) {
                SingleInteractiveWebViewResult siwvr = interactiveWebViews.get(i);
                res.add(builder(NodeViewDescriptionEntBuilder.class).setName(siwvr.getViewName()).build());
            }
            return res;
        } else {
            return null; // NOSONAR
        }
    }

    private static List<NodePortTemplateEnt> buildComponentInNodePortTemplateEnts(final ComponentMetadata metadata,
        final SubNodeContainer snc) {
        if(snc.getNrInPorts() == 1) {
            return null; // NOSONAR
        }
        List<NodePortTemplateEnt> res = new ArrayList<>();
        String[] names = metadata.getInPortNames().orElse(null);
        String[] descs = metadata.getInPortDescriptions().orElse(null);
        for (int i = 1; i < snc.getNrInPorts(); i++) {
            res.add(buildOrGetFromCacheNodePortTemplateEnt(snc.getInPort(i).getPortType(),
                names == null ? null : names[i - 1], descs == null ? null : descs[i - 1]));
        }
        return res;
    }

    private static List<NodePortTemplateEnt> buildComponentOutNodePortTemplateEnts(final ComponentMetadata metadata,
        final SubNodeContainer snc) {
        if(snc.getNrOutPorts() == 1) {
            return null; // NOSONAR
        }
        List<NodePortTemplateEnt> res = new ArrayList<>();
        String[] names = metadata.getOutPortNames().orElse(null);
        String[] descs = metadata.getOutPortDescriptions().orElse(null);
        for (int i = 1; i < snc.getNrOutPorts(); i++) {
            res.add(buildOrGetFromCacheNodePortTemplateEnt(snc.getOutPort(i).getPortType(),
                names == null ? null : names[i - 1], descs == null ? null : descs[i - 1]));
        }
        return res;
    }

    private static NodePortTemplateEnt buildOrGetFromCacheNodePortTemplateEnt(final PortType ptype, final String name,
        final String description) {
        return m_nodePortTemplateCache.computeIfAbsent(ptype.getPortObjectClass().getCanonicalName(),
            k -> buildNodePortTemplateEnt(ptype, name, description));
    }

    private static NodePortTemplateEnt buildNodePortTemplateEnt(final PortType ptype, final String name,
        final String description) {
        NodePortAndTemplateEnt.TypeEnum resPortType = getNodePortTemplateType(ptype);
        return builder(NodePortTemplateEntBuilder.class)//
            .setName(isBlank(name) ? null : name)//
            .setDescription(isBlank(description) ? null : description)//
            .setType(resPortType)//
            .setTypeName(ptype.getName())//
            .setColor(resPortType == NodePortAndTemplateEnt.TypeEnum.OTHER ? hexStringColor(ptype.getColor()) : null)//
            .setOptional(ptype.isOptional())//
            .build();
    }

    private static JobManagerEnt buildJobManagerEnt(final NodeContainer nc) {
        NodeExecutionJobManager jobManager = nc.getJobManager();
        if (jobManager == null || jobManager instanceof ThreadNodeExecutionJobManager) {
            return null;
        } else if (jobManager.getID().equals(STREAMING_JOB_MANAGER_ID)) {
            return builder(JobManagerEntBuilder.class)
                .setType(org.knime.gateway.api.webui.entity.JobManagerEnt.TypeEnum.STREAMING).build();
        } else {
            return builder(JobManagerEntBuilder.class)
                .setType(org.knime.gateway.api.webui.entity.JobManagerEnt.TypeEnum.OTHER)
                .setCustom(buildCustomJobManagerEnt(jobManager)).build();
        }
    }

    private static CustomJobManagerEnt buildCustomJobManagerEnt(final NodeExecutionJobManager jobManager) {
        NodeExecutionJobManagerFactory factory = NodeExecutionJobManagerPool.getJobManagerFactory(jobManager.getID());
        String name = factory == null ? jobManager.getID() : factory.getLabel();
        String icon = null;
        try {
            icon = createIconDataURL(jobManager.getIcon());
        } catch (IOException ex) {
            NodeLogger.getLogger(EntityBuilderUtil.class)
                .error(String.format("Icon for job manager '%s' couldn't be read", name), ex);
        }
        return builder(CustomJobManagerEntBuilder.class).setName(name).setIcon(icon).build();
    }

    private static String createIconDataURL(final NodeFactory<NodeModel> nodeFactory) {
        try {
            return createIconDataURL(nodeFactory.getIcon());
        } catch (IOException ex) {
            NodeLogger.getLogger(EntityBuilderUtil.class)
                .error(String.format("Icon for node '%s' couldn't be read", nodeFactory.getNodeName()), ex);
            return null;
        }
    }

    private static String createIconDataURL(final URL url) throws IOException {
        if (url != null) {
            try (InputStream in = url.openStream()) {
                return createIconDataURL(IOUtils.toByteArray(in));
            }
        } else {
            return null;
        }
    }

    private static String createIconDataURL(final byte[] iconData) {
        if (iconData != null) {
            final String dataUrlPrefix = "data:image/png;base64,";
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
        final Function<NodeID, NodeIDEnt> buildNodeIDEnt) {
        return builder(ConnectionEntBuilder.class).setDestNode(buildNodeIDEnt.apply(cc.getDest()))//
            .setDestPort(cc.getDestPort())//
            .setSourceNode(buildNodeIDEnt.apply(cc.getSource())).setSourcePort(cc.getSourcePort())//
            .setFlowVariableConnection(cc.isFlowVariablePortConnection() ? cc.isFlowVariablePortConnection() : null)//
            .build();
    }
}

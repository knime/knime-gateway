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
package com.knime.gateway.remote.util;

import static com.knime.gateway.entity.EntityBuilderManager.builder;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.knime.core.data.DataTableSpec;
import org.knime.core.node.DynamicNodeFactory;
import org.knime.core.node.ModelContent;
import org.knime.core.node.NodeFactory;
import org.knime.core.node.NodeModel;
import org.knime.core.node.NodeSettings;
import org.knime.core.node.config.base.JSONConfig;
import org.knime.core.node.config.base.JSONConfig.WriterConfig;
import org.knime.core.node.port.AbstractSimplePortObjectSpec;
import org.knime.core.node.port.AbstractSimplePortObjectSpec.AbstractSimplePortObjectSpecSerializer;
import org.knime.core.node.port.MetaPortInfo;
import org.knime.core.node.port.PortObjectSpec;
import org.knime.core.node.port.PortType;
import org.knime.core.node.port.flowvariable.FlowVariablePortObjectSpec;
import org.knime.core.node.port.inactive.InactiveBranchPortObjectSpec;
import org.knime.core.node.util.NodeExecutionJobManagerPool;
import org.knime.core.node.web.WebViewContent;
import org.knime.core.node.wizard.WizardNode;
import org.knime.core.node.workflow.AnnotationData.StyleRange;
import org.knime.core.node.workflow.ConnectionContainer;
import org.knime.core.node.workflow.EditorUIInformation;
import org.knime.core.node.workflow.FlowVariable;
import org.knime.core.node.workflow.NativeNodeContainer;
import org.knime.core.node.workflow.NodeAnnotation;
import org.knime.core.node.workflow.NodeContainer;
import org.knime.core.node.workflow.NodeContainerState;
import org.knime.core.node.workflow.NodeExecutionJobManager;
import org.knime.core.node.workflow.NodeID;
import org.knime.core.node.workflow.NodeInPort;
import org.knime.core.node.workflow.NodeOutPort;
import org.knime.core.node.workflow.NodeUIInformation;
import org.knime.core.node.workflow.SubNodeContainer;
import org.knime.core.node.workflow.WorkflowAnnotation;
import org.knime.core.node.workflow.WorkflowManager;
import org.knime.core.node.workflow.action.InteractiveWebViewsResult;

import com.knime.gateway.util.DefaultEntUtil;
import com.knime.gateway.v0.entity.BoundsEnt;
import com.knime.gateway.v0.entity.BoundsEnt.BoundsEntBuilder;
import com.knime.gateway.v0.entity.ConnectionEnt;
import com.knime.gateway.v0.entity.ConnectionEnt.ConnectionEntBuilder;
import com.knime.gateway.v0.entity.ConnectionEnt.TypeEnum;
import com.knime.gateway.v0.entity.FlowVariableEnt;
import com.knime.gateway.v0.entity.FlowVariableEnt.FlowVariableEntBuilder;
import com.knime.gateway.v0.entity.JobManagerEnt;
import com.knime.gateway.v0.entity.JobManagerEnt.JobManagerEntBuilder;
import com.knime.gateway.v0.entity.MetaPortInfoEnt;
import com.knime.gateway.v0.entity.MetaPortInfoEnt.MetaPortInfoEntBuilder;
import com.knime.gateway.v0.entity.NativeNodeEnt;
import com.knime.gateway.v0.entity.NativeNodeEnt.NativeNodeEntBuilder;
import com.knime.gateway.v0.entity.NodeAnnotationEnt;
import com.knime.gateway.v0.entity.NodeAnnotationEnt.NodeAnnotationEntBuilder;
import com.knime.gateway.v0.entity.NodeEnt;
import com.knime.gateway.v0.entity.NodeEnt.NodeTypeEnum;
import com.knime.gateway.v0.entity.NodeFactoryKeyEnt.NodeFactoryKeyEntBuilder;
import com.knime.gateway.v0.entity.NodeInPortEnt;
import com.knime.gateway.v0.entity.NodeInPortEnt.NodeInPortEntBuilder;
import com.knime.gateway.v0.entity.NodeMessageEnt;
import com.knime.gateway.v0.entity.NodeMessageEnt.NodeMessageEntBuilder;
import com.knime.gateway.v0.entity.NodeOutPortEnt;
import com.knime.gateway.v0.entity.NodeOutPortEnt.NodeOutPortEntBuilder;
import com.knime.gateway.v0.entity.NodeProgressEnt;
import com.knime.gateway.v0.entity.NodeProgressEnt.NodeProgressEntBuilder;
import com.knime.gateway.v0.entity.NodeStateEnt;
import com.knime.gateway.v0.entity.NodeStateEnt.NodeStateEntBuilder;
import com.knime.gateway.v0.entity.NodeUIInfoEnt;
import com.knime.gateway.v0.entity.NodeUIInfoEnt.NodeUIInfoEntBuilder;
import com.knime.gateway.v0.entity.PortObjectSpecEnt;
import com.knime.gateway.v0.entity.PortObjectSpecEnt.PortObjectSpecEntBuilder;
import com.knime.gateway.v0.entity.PortTypeEnt;
import com.knime.gateway.v0.entity.PortTypeEnt.PortTypeEntBuilder;
import com.knime.gateway.v0.entity.StyleRangeEnt;
import com.knime.gateway.v0.entity.StyleRangeEnt.FontStyleEnum;
import com.knime.gateway.v0.entity.StyleRangeEnt.StyleRangeEntBuilder;
import com.knime.gateway.v0.entity.WebViewEnt;
import com.knime.gateway.v0.entity.WebViewEnt.WebViewEntBuilder;
import com.knime.gateway.v0.entity.WebView_viewRepresentationEnt.WebView_viewRepresentationEntBuilder;
import com.knime.gateway.v0.entity.WebView_viewValueEnt.WebView_viewValueEntBuilder;
import com.knime.gateway.v0.entity.WorkflowAnnotationEnt;
import com.knime.gateway.v0.entity.WorkflowAnnotationEnt.WorkflowAnnotationEntBuilder;
import com.knime.gateway.v0.entity.WorkflowEnt;
import com.knime.gateway.v0.entity.WorkflowEnt.WorkflowEntBuilder;
import com.knime.gateway.v0.entity.WorkflowNodeEnt;
import com.knime.gateway.v0.entity.WorkflowNodeEnt.WorkflowNodeEntBuilder;
import com.knime.gateway.v0.entity.WorkflowUIInfoEnt;
import com.knime.gateway.v0.entity.WorkflowUIInfoEnt.WorkflowUIInfoEntBuilder;
import com.knime.gateway.v0.entity.WrappedWorkflowNodeEnt;
import com.knime.gateway.v0.entity.WrappedWorkflowNodeEnt.WrappedWorkflowNodeEntBuilder;
import com.knime.gateway.v0.entity.XYEnt;
import com.knime.gateway.v0.entity.XYEnt.XYEntBuilder;

/**
 * Collects helper methods to build entity instances basically from core.api-classes (e.g. WorkflowManager etc.).
 *
 * @author Martin Horn, University of Konstanz
 */
public class EntityBuilderUtil {

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
                .collect(Collectors.toMap(n -> n.getNodeID(), n -> n));
        Collection<ConnectionContainer> connectionContainers = wfm.getConnectionContainers();
        List<ConnectionEnt> connections = connectionContainers.stream()
                .map(cc -> buildConnectionEnt(cc))
                .collect(Collectors.toList());
        return builder(WorkflowEntBuilder.class)
            .setNodes(nodes)
            .setConnections(connections)
            .setMetaInPortInfos(buildMetaInPortInfoEnts(wfm))
            .setMetaOutPortInfos(buildMetaOutPortInfoEnts(wfm))
            .setWorkflowAnnotations(wfm.getWorkflowAnnotations().stream()
                .map(wa -> buildWorkflowAnnotationEnt(wa)).collect(Collectors.toList()))
            .setWorkflowUIInfo(buildWorkflowUIInfoEnt(wfm.getEditorUIInformation()))
            .setHasCredentials(!wfm.getCredentialsStore().listNames().isEmpty())
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
            return buildWrappedWorkflowNodeEnt((SubNodeContainer) nc, rootWorkflowID);
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
        NodeExecutionJobManager jobManager;
        if (wm.getParent() == WorkflowManager.ROOT) {
            //TODO somehow get the default job manager from the workflow manager itself!!
            jobManager =
                NodeExecutionJobManagerPool.getDefaultJobManagerFactory().getInstance();
        } else {
            jobManager = wm.getJobManager();
        }
        String parentNodeID;
        if (wm.getParent() == null || wm.getParent() == WorkflowManager.ROOT) {
            parentNodeID = null;
        } else {
            parentNodeID = nodeIdAsString(wm.getParent().getID());
        }

        //retrieve states of nodes connected to the workflow outports
        List<NodeStateEnt> outNodeStates = new ArrayList<>();
        for (int i = 0; i < wm.getNrOutPorts(); i++) {
            outNodeStates.add(buildNodeStateEnt(wm.getOutPort(i).getNodeContainerState().toString()));
        }

        return builder(WorkflowNodeEntBuilder.class).setName(wm.getName()).setNodeID(nodeIdAsString(wm.getID()))
                .setNodeMessage(buildNodeMessageEnt(wm))
                .setNodeType(NodeTypeEnum.valueOf(wm.getType().toString().toUpperCase()))
                .setUIInfo(buildNodeUIInfoEnt(wm.getUIInformation()))
                .setDeletable(wm.isDeletable())
                .setNodeState(buildNodeStateEnt(wm.getNodeContainerState().toString()))
                .setOutPorts(buildNodeOutPortEnts(wm))
                .setParentNodeID(parentNodeID)
                .setDeletable(wm.isDeletable())
                .setResetable(true)
                .setJobManager(buildJobManagerEnt(jobManager))
                .setNodeAnnotation(buildNodeAnnotationEnt(wm))
                .setInPorts(buildNodeInPortEnts(wm))
                .setHasDialog(wm.hasDialog())
                .setWorkflowIncomingPorts(buildWorkflowIncomingPortEnts(wm))
                .setWorkflowOutgoingPorts(buildWorkflowOutgoingPortEnts(wm))
                .setRootWorkflowID(rootWorkflowID)
                .setEncrypted(wm.isEncrypted())
                .setWorkflowOutgoingPortNodeStates(outNodeStates)
                .setType("WorkflowNode").build();
    }

    /**
     * Builds a new {@link WrappedWorkflowNodeEnt}.
     *
     * @param subNode the subnode container to create the node entity from
     * @param rootWorkflowID the workflow ID of the root workflow
     * @return the newly created entity
     */
    public static WrappedWorkflowNodeEnt buildWrappedWorkflowNodeEnt(final SubNodeContainer subNode,
        final UUID rootWorkflowID) {
        NodeExecutionJobManager jobManager;
        if (subNode.getParent() == WorkflowManager.ROOT) {
            //TODO somehow get the default job manager from the workflow manager itself!!
            jobManager = NodeExecutionJobManagerPool.getDefaultJobManagerFactory().getInstance();
        } else {
            jobManager = subNode.getJobManager();
        }
        return builder(WrappedWorkflowNodeEntBuilder.class).setName(subNode.getName())
                .setNodeID(nodeIdAsString(subNode.getID()))
                .setNodeMessage(buildNodeMessageEnt(subNode))
                .setNodeType(NodeTypeEnum.valueOf(subNode.getType().toString().toUpperCase()))
                .setUIInfo(buildNodeUIInfoEnt(subNode.getUIInformation()))
                .setDeletable(subNode.isDeletable())
                .setResetable(subNode.isResetable())
                .setNodeState(buildNodeStateEnt((subNode.getNodeContainerState().toString())))
                .setOutPorts(buildNodeOutPortEnts(subNode))
                .setParentNodeID(
                    subNode.getParent() == WorkflowManager.ROOT ? null : nodeIdAsString(subNode.getParent().getID()))
                .setJobManager(buildJobManagerEnt(jobManager))
                .setNodeAnnotation(buildNodeAnnotationEnt(subNode))
                .setInPorts(buildNodeInPortEnts(subNode))
                .setHasDialog(subNode.hasDialog())
                .setWorkflowIncomingPorts(buildWorkflowIncomingPortEnts(subNode.getWorkflowManager()))
                .setWorkflowOutgoingPorts(buildWorkflowOutgoingPortEnts(subNode.getWorkflowManager()))
                .setRootWorkflowID(rootWorkflowID)
                .setEncrypted(subNode.getWorkflowManager().isEncrypted())
                .setVirtualInNodeID(nodeIdAsString(subNode.getVirtualInNodeID()))
                .setVirtualOutNodeID(nodeIdAsString(subNode.getVirtualOutNodeID()))
                .setInactive(subNode.isInactive())
                .setType("WrappedWorkflowNode").build();
    }

    /**
     * Newly creates a {@link PortObjectSpecEnt}.
     *
     * @param type port type
     * @param spec the actual spec to create the entity from
     * @return the entity or <code>null</code> if spec/port type is not supported
     * @throws IllegalArgumentException if the port type and the port object spec are not compatible
     */
    public static PortObjectSpecEnt buildPortObjectSpecEnt(final PortType type, final PortObjectSpec spec) {
        if (!type.acceptsPortObjectSpec(spec)) {
            throw new IllegalArgumentException("The port type and port object spec are not compatible.");
        }
        ModelContent model = null;
        PortObjectSpecEntBuilder builder = builder(PortObjectSpecEntBuilder.class).setInactive(false);
        if (spec instanceof DataTableSpec) {
            model = new ModelContent("model");
            ((DataTableSpec)spec).save(model);
        } else if (spec instanceof FlowVariablePortObjectSpec) {
            //flow variable port spec doesn't have any content
        } else if (spec instanceof AbstractSimplePortObjectSpec) {
            model = new ModelContent("model");
            AbstractSimplePortObjectSpecSerializer.savePortObjectSpecToModelSettings((AbstractSimplePortObjectSpec)spec,
                model);
        } else if (spec instanceof InactiveBranchPortObjectSpec) {
            builder.setInactive(true);
        } else {
            //port type/spec not supported, yet
            return null;
        }

        builder.setType(buildPortTypeEnt(type));
        if (model != null) {
            builder.setRepresentation(JSONConfig.toJSONString(model, WriterConfig.PRETTY));
        }
        return builder.build();
    }

    /**
     * Builds a new {@link FlowVariableEnt}.
     *
     * @param flowVar the flow variable to create the entity from
     * @return the newly created entity
     */
    public static FlowVariableEnt buildFlowVariableEnt(final FlowVariable flowVar) {
        return builder(FlowVariableEntBuilder.class)
                .setName(flowVar.getName())
                .setType(FlowVariableEnt.TypeEnum.valueOf(flowVar.getType().toString()))
                .setValue(flowVar.getValueAsString())
                .build();
    }

    /**
     * Builds a new {@link WebViewEnt}.
     *
     * @param webViewsResult the interactive web view result to extract the web view from
     * @param index  the index of the web view to build
     * @return the newly created entity
     */
    public static WebViewEnt buildWebViewEnt(final InteractiveWebViewsResult webViewsResult, final int index) {
        WizardNode<?, ?> wnode = (WizardNode<?, ?>)webViewsResult.get(index).getNativeNodeContainer().getNodeModel();
        String viewRepresentation = toJsonString(wnode.getViewRepresentation());
        String viewValue = toJsonString(wnode.getViewValue());
        return builder(WebViewEntBuilder.class)
                .setJavascriptObjectID(wnode.getJavascriptObjectID())
                .setViewRepresentation(
                    builder(WebView_viewRepresentationEntBuilder.class)
                    .setClassname(wnode.getViewRepresentation().getClass().getCanonicalName())
                    .setContent(viewRepresentation)
                    .build())
                 .setViewValue(
                    builder(WebView_viewValueEntBuilder.class)
                    .setClassname(wnode.getViewValue().getClass().getCanonicalName())
                    .setContent(viewValue)
                    .build())
                .setViewHTMLPath(wnode.getViewHTMLPath())
                .setHideInWizard(wnode.isHideInWizard()).build();
    }

    /**
     * Turns a webview content into a json string via node settings.
     */
    private static String toJsonString(final WebViewContent webViewContent) {
        NodeSettings settings = new NodeSettings("settings");
        webViewContent.saveToNodeSettings(settings);
        return JSONConfig.toJSONString(settings, WriterConfig.DEFAULT);
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

    private static List<NodeInPortEnt> buildWorkflowOutgoingPortEnts(final WorkflowManager wm) {
        List<NodeInPortEnt> inPorts = new ArrayList<>(wm.getNrWorkflowOutgoingPorts());
        for (int i = 0; i < wm.getNrWorkflowOutgoingPorts(); i++) {
            inPorts.add(buildNodeInPortEnt(wm.getWorkflowOutgoingPort(i)));
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

    private static List<NodeOutPortEnt> buildWorkflowIncomingPortEnts(final WorkflowManager wm) {
        List<NodeOutPortEnt> outPorts = new ArrayList<>(wm.getNrWorkflowIncomingPorts());
        for (int i = 0; i < wm.getNrWorkflowIncomingPorts(); i++) {
            outPorts.add(buildNodeOutPortEnt(wm.getWorkflowIncomingPort(i)));
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
        BoundsEnt bounds = builder(BoundsEntBuilder.class)
                .setX(na.getX())
                .setY(na.getY())
                .setWidth(na.getWidth())
                .setHeight(na.getHeight())
                .build();
        List<StyleRangeEnt> styleRanges =
            Arrays.stream(na.getStyleRanges()).map(sr -> buildStyleRangeEnt(sr)).collect(Collectors.toList());
        return builder(NodeAnnotationEntBuilder.class).setBackgroundColor(na.getBgColor())
            .setBorderColor(na.getBorderColor()).setBorderSize(na.getBorderSize())
            .setDefaultFontSize(na.getDefaultFontSize()).setBounds(bounds).setText(na.getText())
            .setTextAlignment(na.getAlignment().toString()).setVersion(na.getVersion())
            .setDefault(na.getData().isDefault()).setStyleRanges(styleRanges)
            .setType("NodeAnnotation").build();
    }

    private static JobManagerEnt buildJobManagerEnt(final NodeExecutionJobManager jobManager) {
        if(jobManager != null) {
            return builder(JobManagerEntBuilder.class)
                    .setId(jobManager.getID())
                    .build();
        } else {
            return null;
        }
    }

    //TODO!!
    private static MetaPortInfoEnt buildMetaPortInfoEnt(final MetaPortInfo info) {
        PortType pt = info.getType();
        PortTypeEnt portType = builder(PortTypeEntBuilder.class)
                .setOptional(pt.isOptional())
                .setPortObjectClassName(pt.getPortObjectClass().getCanonicalName()).build();
        return builder(MetaPortInfoEntBuilder.class)
            .setConnected(info.isConnected())
            .setMessage(info.getMessage())
            .setNewIndex(info.getNewIndex())
            .setOldIndex(info.getNewIndex())
            .setPortType(portType).build();
    }

    private static List<MetaPortInfoEnt> buildMetaInPortInfoEnts(final WorkflowManager wm) {
        if (wm.getNrWorkflowIncomingPorts() == 0) {
            return Collections.emptyList();
        } else {
            return Arrays.stream(wm.getParent().getMetanodeInputPortInfo(wm.getID())).map(i -> buildMetaPortInfoEnt(i))
                .collect(Collectors.toList());
        }
    }

    private static List<MetaPortInfoEnt> buildMetaOutPortInfoEnts(final WorkflowManager wm) {
        if (wm.getNrWorkflowOutgoingPorts() == 0) {
            return Collections.emptyList();
        } else {
            return Arrays.stream(wm.getParent().getMetanodeOutputPortInfo(wm.getID())).map(i -> buildMetaPortInfoEnt(i))
                .collect(Collectors.toList());
        }
    }

    private static NodeMessageEnt buildNodeMessageEnt(final NodeContainer nc) {
        return builder(NodeMessageEntBuilder.class).setMessage(nc.getNodeMessage().getMessage())
            .setType(nc.getNodeMessage().getMessageType().toString()).build();
    }

    private static NativeNodeEnt buildNativeNodeEnt(final NativeNodeContainer nc, final UUID rootWorkflowID) {
        NodeFactory<NodeModel> factory = nc.getNode().getFactory();
        NodeFactoryKeyEntBuilder nodeFactoryKeyBuilder = builder(NodeFactoryKeyEntBuilder.class)
                .setClassName(factory.getClass().getCanonicalName());
        //only set settings in case of a dynamic node factory
        if (DynamicNodeFactory.class.isAssignableFrom(factory.getClass())) {
            NodeSettings settings = new NodeSettings("settings");
            nc.getNode().getFactory().saveAdditionalFactorySettings(settings);
            nodeFactoryKeyBuilder.setSettings(JSONConfig.toJSONString(settings, WriterConfig.PRETTY));
        }
        InteractiveWebViewsResult webViews = nc.getInteractiveWebViews();
        return builder(NativeNodeEntBuilder.class).setName(nc.getName()).setNodeID(nodeIdAsString(nc.getID()))
            .setNodeMessage(buildNodeMessageEnt(nc))
            .setNodeType(NodeTypeEnum.valueOf(nc.getType().toString().toUpperCase()))
            .setUIInfo(buildNodeUIInfoEnt(nc.getUIInformation()))
            .setDeletable(nc.isDeletable())
            .setResetable(nc.isResetable())
            .setNodeState(buildNodeStateEnt(nc.getNodeContainerState().toString()))
            .setProgress(
                buildNodeProgressEnt(nc.getProgressMonitor().getProgress(),
                    nc.getProgressMonitor().getMessage(),
                    nc.getNodeContainerState()))
            .setOutPorts(buildNodeOutPortEnts(nc))
            .setParentNodeID(nc.getParent() == WorkflowManager.ROOT ? null : nodeIdAsString(nc.getParent().getID()))
            .setRootWorkflowID(rootWorkflowID)
            .setJobManager(buildJobManagerEnt(nc.getJobManager()))
            .setNodeAnnotation(buildNodeAnnotationEnt(nc))
            .setInPorts(buildNodeInPortEnts(nc))
            .setHasDialog(nc.hasDialog())
            .setNodeFactoryKey(nodeFactoryKeyBuilder.build())
            .setInactive(nc.isInactive())
            .setWebViewNames(IntStream.range(0, webViews.size())
                .mapToObj(i -> webViews.get(i).getViewName()).collect(Collectors.toList()))
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

    private static NodeUIInfoEnt buildNodeUIInfoEnt(final NodeUIInformation uiInfo) {
        if(uiInfo == null) {
            return null;
        } else {
            int[] b = uiInfo.getBounds();
            BoundsEnt bounds = builder(BoundsEntBuilder.class)
                    .setX(b[0])
                    .setY(b[1])
                    .setWidth(b[2])
                    .setHeight(b[3]).build();
            return builder(NodeUIInfoEntBuilder.class)
                    .setBounds(bounds)
                    .setHasAbsoluteCoordinates(uiInfo.hasAbsoluteCoordinates())
                    .setDropLocation(uiInfo.isDropLocation())
                    .setSnapToGrid(uiInfo.getSnapToGrid())
                    .setSymbolRelative(uiInfo.isSymbolRelative()).build();
        }
    }

    /**
     * @param nodeId
     * @return the node id as string with the very first id (root) removed. If the nodeId only consist of the root id,
     *         it will return 'root'.
     */
    private static String nodeIdAsString(final NodeID nodeId) {
        String s = nodeId.toString();
        int firstIdx = s.indexOf(":");
        return (firstIdx >= 0) ? s.substring(firstIdx + 1) : DefaultEntUtil.ROOT_NODE_ID;
    }

    private static ConnectionEnt buildConnectionEnt(final ConnectionContainer cc) {
        List<XYEnt> bendpoints;
        if (cc.getUIInfo() != null) {
            int[][] allBendpoints = cc.getUIInfo().getAllBendpoints();
            bendpoints = Arrays.stream(allBendpoints).map(a -> {
                return builder(XYEntBuilder.class).setX(a[0]).setY(a[1]).build();
            }).collect(Collectors.toList());
        } else {
            bendpoints = Collections.emptyList();
        }
        return builder(ConnectionEntBuilder.class).setDest(nodeIdAsString(cc.getDest())).setDestPort(cc.getDestPort())
            .setSource(nodeIdAsString(cc.getSource())).setSourcePort(cc.getSourcePort())
            .setDeletable(cc.isDeletable()).setType(TypeEnum.valueOf(cc.getType().toString().toUpperCase()))
            .setBendPoints(bendpoints)
            .setFlowVariablePortConnection(cc.isFlowVariablePortConnection()).build();
    }

    private static WorkflowAnnotationEnt buildWorkflowAnnotationEnt(final WorkflowAnnotation wa) {
        BoundsEnt bounds = builder(BoundsEntBuilder.class)
                .setX(wa.getX())
                .setY(wa.getY())
                .setWidth(wa.getWidth())
                .setHeight(wa.getHeight())
                .build();
        List<StyleRangeEnt> styleRanges =
            Arrays.stream(wa.getStyleRanges()).map(sr -> buildStyleRangeEnt(sr)).collect(Collectors.toList());
        return builder(WorkflowAnnotationEntBuilder.class)
                .setTextAlignment(wa.getAlignment().toString())
                .setBackgroundColor(wa.getBgColor())
                .setBorderColor(wa.getBorderColor())
                .setBorderSize(wa.getBorderSize())
                .setDefaultFontSize(wa.getDefaultFontSize())
                .setBounds(bounds)
                .setText(wa.getText())
                .setStyleRanges(styleRanges)
                .setType("WorkflowAnnotation")
                .build();
    }

    private static StyleRangeEnt buildStyleRangeEnt(final StyleRange sr) {
        return builder(StyleRangeEntBuilder.class)
                .setFontName(sr.getFontName())
                .setFontSize(sr.getFontSize())
                .setForegroundColor(sr.getFgColor())
                .setLength(sr.getLength())
                .setStart(sr.getStart())
                .setFontStyle(FontStyleEnum.valueOf(getFontStyleString(sr.getFontStyle())))
                .build();
    }

    private static NodeStateEnt buildNodeStateEnt(final String state) {
        return builder(NodeStateEntBuilder.class)
                .setState(NodeStateEnt.StateEnum.valueOf(state))
                .build();
    }

    private static String getFontStyleString(final int fontStyle) {
        switch (fontStyle) {
            case 0:
                return "NORMAL";
            case 1:
                return "BOLD";
            case 2:
                return "ITALIC";
            default:
                return "NORMAL";
        }
    }

    private static WorkflowUIInfoEnt buildWorkflowUIInfoEnt(final EditorUIInformation editorUIInfo) {
        return builder(WorkflowUIInfoEntBuilder.class)
                .setGridX(editorUIInfo.getGridX())
                .setGridY(editorUIInfo.getGridY())
                .setSnapToGrid(editorUIInfo.getSnapToGrid())
                .setShowGrid(editorUIInfo.getShowGrid())
                .setZoomLevel(BigDecimal.valueOf(editorUIInfo.getZoomLevel()))
                .setHasCurvedConnection(editorUIInfo.getHasCurvedConnections())
                .setConnectionLineWidth(editorUIInfo.getConnectionLineWidth())
                .build();
    }

}

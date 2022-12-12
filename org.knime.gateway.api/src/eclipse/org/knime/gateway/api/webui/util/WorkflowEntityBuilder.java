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
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.knime.gateway.api.entity.EntityBuilderManager.builder;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import javax.xml.xpath.XPathExpressionException;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.knime.core.internal.ReferencedFile;
import org.knime.core.node.DynamicNodeFactory;
import org.knime.core.node.NodeFactory;
import org.knime.core.node.NodeFactory.NodeType;
import org.knime.core.node.NodeLogger;
import org.knime.core.node.NodeModel;
import org.knime.core.node.NodeProgressMonitor;
import org.knime.core.node.NodeSettings;
import org.knime.core.node.config.base.JSONConfig;
import org.knime.core.node.config.base.JSONConfig.WriterConfig;
import org.knime.core.node.context.ports.ExtendablePortGroup;
import org.knime.core.node.dialog.DialogNodeValue;
import org.knime.core.node.dialog.SubNodeDescriptionProvider;
import org.knime.core.node.missing.MissingNodeFactory;
import org.knime.core.node.port.MetaPortInfo;
import org.knime.core.node.port.PortType;
import org.knime.core.node.port.flowvariable.FlowVariablePortObject;
import org.knime.core.node.util.NodeExecutionJobManagerPool;
import org.knime.core.node.wizard.page.WizardPageUtil;
import org.knime.core.node.workflow.AbstractNodeExecutionJobManager;
import org.knime.core.node.workflow.AnnotationData.StyleRange;
import org.knime.core.node.workflow.ComponentMetadata;
import org.knime.core.node.workflow.ComponentMetadata.ComponentNodeType;
import org.knime.core.node.workflow.ConnectionContainer;
import org.knime.core.node.workflow.FlowScopeContext;
import org.knime.core.node.workflow.FlowVariable;
import org.knime.core.node.workflow.LoopEndNode;
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
import org.knime.core.util.workflowalizer.NodeAndBundleInformation;
import org.knime.core.util.workflowalizer.WorkflowGroupMetadata;
import org.knime.core.util.workflowalizer.WorkflowSetMeta.Link;
import org.knime.core.util.workflowalizer.Workflowalizer;
import org.knime.core.webui.node.dialog.NodeDialogManager;
import org.knime.core.webui.node.view.NodeViewManager;
import org.knime.gateway.api.entity.AnnotationIDEnt;
import org.knime.gateway.api.entity.ConnectionIDEnt;
import org.knime.gateway.api.entity.NodeIDEnt;
import org.knime.gateway.api.util.CoreUtil;
import org.knime.gateway.api.util.DependentNodeProperties;
import org.knime.gateway.api.webui.entity.AllowedConnectionActionsEnt;
import org.knime.gateway.api.webui.entity.AllowedConnectionActionsEnt.AllowedConnectionActionsEntBuilder;
import org.knime.gateway.api.webui.entity.AllowedLoopActionsEnt;
import org.knime.gateway.api.webui.entity.AllowedLoopActionsEnt.AllowedLoopActionsEntBuilder;
import org.knime.gateway.api.webui.entity.AllowedNodeActionsEnt;
import org.knime.gateway.api.webui.entity.AllowedNodeActionsEnt.AllowedNodeActionsEntBuilder;
import org.knime.gateway.api.webui.entity.AllowedWorkflowActionsEnt;
import org.knime.gateway.api.webui.entity.AllowedWorkflowActionsEnt.AllowedWorkflowActionsEntBuilder;
import org.knime.gateway.api.webui.entity.AnnotationEnt.TextAlignEnum;
import org.knime.gateway.api.webui.entity.BoundsEnt;
import org.knime.gateway.api.webui.entity.BoundsEnt.BoundsEntBuilder;
import org.knime.gateway.api.webui.entity.ComponentNodeAndDescriptionEnt;
import org.knime.gateway.api.webui.entity.ComponentNodeDescriptionEnt;
import org.knime.gateway.api.webui.entity.ComponentNodeDescriptionEnt.ComponentNodeDescriptionEntBuilder;
import org.knime.gateway.api.webui.entity.ComponentNodeEnt;
import org.knime.gateway.api.webui.entity.ComponentNodeEnt.ComponentNodeEntBuilder;
import org.knime.gateway.api.webui.entity.ConnectionEnt;
import org.knime.gateway.api.webui.entity.ConnectionEnt.ConnectionEntBuilder;
import org.knime.gateway.api.webui.entity.CustomJobManagerEnt;
import org.knime.gateway.api.webui.entity.CustomJobManagerEnt.CustomJobManagerEntBuilder;
import org.knime.gateway.api.webui.entity.JobManagerEnt;
import org.knime.gateway.api.webui.entity.JobManagerEnt.JobManagerEntBuilder;
import org.knime.gateway.api.webui.entity.LinkEnt;
import org.knime.gateway.api.webui.entity.LinkEnt.LinkEntBuilder;
import org.knime.gateway.api.webui.entity.LoopInfoEnt;
import org.knime.gateway.api.webui.entity.LoopInfoEnt.LoopInfoEntBuilder;
import org.knime.gateway.api.webui.entity.LoopInfoEnt.StatusEnum;
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
import org.knime.gateway.api.webui.entity.NativeNodeInvariantsEnt;
import org.knime.gateway.api.webui.entity.NativeNodeInvariantsEnt.NativeNodeInvariantsEntBuilder;
import org.knime.gateway.api.webui.entity.NativeNodeInvariantsEnt.TypeEnum;
import org.knime.gateway.api.webui.entity.NodeAnnotationEnt;
import org.knime.gateway.api.webui.entity.NodeAnnotationEnt.NodeAnnotationEntBuilder;
import org.knime.gateway.api.webui.entity.NodeDialogOptionDescriptionEnt;
import org.knime.gateway.api.webui.entity.NodeDialogOptionGroupEnt;
import org.knime.gateway.api.webui.entity.NodeEnt;
import org.knime.gateway.api.webui.entity.NodeEnt.KindEnum;
import org.knime.gateway.api.webui.entity.NodeExecutionInfoEnt;
import org.knime.gateway.api.webui.entity.NodeExecutionInfoEnt.NodeExecutionInfoEntBuilder;
import org.knime.gateway.api.webui.entity.NodeFactoryKeyEnt;
import org.knime.gateway.api.webui.entity.NodeFactoryKeyEnt.NodeFactoryKeyEntBuilder;
import org.knime.gateway.api.webui.entity.NodePortDescriptionEnt;
import org.knime.gateway.api.webui.entity.NodePortDescriptionEnt.NodePortDescriptionEntBuilder;
import org.knime.gateway.api.webui.entity.NodePortEnt;
import org.knime.gateway.api.webui.entity.NodePortEnt.NodePortEntBuilder;
import org.knime.gateway.api.webui.entity.NodeStateEnt;
import org.knime.gateway.api.webui.entity.NodeStateEnt.ExecutionStateEnum;
import org.knime.gateway.api.webui.entity.NodeStateEnt.NodeStateEntBuilder;
import org.knime.gateway.api.webui.entity.NodeViewDescriptionEnt;
import org.knime.gateway.api.webui.entity.NodeViewDescriptionEnt.NodeViewDescriptionEntBuilder;
import org.knime.gateway.api.webui.entity.PortGroupEnt;
import org.knime.gateway.api.webui.entity.PortGroupEnt.PortGroupEntBuilder;
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
import org.knime.gateway.api.webui.util.WorkflowBuildContext.WorkflowBuildContextBuilder;
import org.xml.sax.SAXException;

/**
 * See {@link EntityBuilderUtil}.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
@SuppressWarnings("static-method")
public final class WorkflowEntityBuilder {

    /*
     * The default background color for node annotations which usually translates to opaque.
     */
    private final int DEFAULT_NODE_ANNOTATION_BG_COLOR = 0xFFFFFF;

    private final Map<Class<?>, Boolean> IS_STREAMABLE = new ConcurrentHashMap<>(0);

    /**
     * Characterization of loop state for determining allowed actions.
     * This is not part of the API, see {@link StatusEnum} instead.
     */
    private static enum LoopState {

        /** Initial state, no loop iteration has been performed yet */
        READY,
        /** Loop is currently performing "full" execution (not step) */
        RUNNING,
        /** Loop is currently executing and will be paused after this iteration (e.g. due to step execution, or pause action) */
        PAUSE_PENDING,
        /** Loop is currently executing and is currently in paused state */
        PAUSED,
        /** Loop is fully executed */
        DONE,
        /** Status could not be determined (e.g. if there is no valid loop structure) */
        NONE;

        /**
         * @param tail The node to consider
         * @param buildContext The workflow build context
         * @return The state of the given node.
         */
        @SuppressWarnings("java:S1142")  // number of method returns
        static LoopState get(final NativeNodeContainer tail, final WorkflowBuildContext buildContext) {
            boolean hasLoopHead = CoreUtil.getLoopContext(tail)
                    .map(FlowScopeContext::getHeadNode)
                    // When a loop head node is removed, the loop context may point to a node ID that is no longer
                    //  present in the workflow.
                    .map(head -> buildContext.wfm().containsNodeContainer(head))
                    .orElse(false);
            boolean canExecuteDirectly = tail.getParent().canExecuteNodeDirectly(tail.getID());
            var loopStatus = tail.getLoopStatus();
            // Resume and step should not be enabled if nodes in the loop body are currently executing (this includes
            // outgoing dangling branches) ...
            boolean hasExecutingLoopBody = buildContext.dependentNodeProperties().hasExecutingLoopBody(tail);
            // ... and not if the tail node is currently waiting due to other reasons, such as... (cf. AP-18329)
            //      - a node upstream of the corresponding head is currently executing
            //      - a tail node of a nested loop is currently paused
            // It suffices to check only the direct predecessor since the "waiting" node state is propagated downstream.
            // We only need to check predecessors in the current workflow: Since scopes cannot leave workflows, for any
            //  validly constructed loop, both head and tail have to be in the workflow and the tail has to be reachable
            //  from the head. Consequently, the direct predecessor of a tail cannot be outside the current workflow.
            boolean hasWaitingPredecessor = CoreUtil.hasWaitingPredecessor(tail.getID(), buildContext.wfm());
            boolean loopBodyActive = hasExecutingLoopBody || hasWaitingPredecessor;

            if (!hasLoopHead) {
                return NONE;
            } else if (canExecuteDirectly) {
                return READY;
            } else if(loopStatus == NativeNodeContainer.LoopStatus.RUNNING) {
                return RUNNING;
            } else if (loopStatus == NativeNodeContainer.LoopStatus.PAUSED) {
                if (loopBodyActive) {
                    return PAUSE_PENDING;
                } else {
                    return PAUSED;
                }
            } else {
                return DONE;
            }
        }

        /**
         * Determine the loop state of the given node, and based on it determine the allowed actions.
         * @param tail The node to consider
         * @param buildContext The current dependent node properties
         * @return The allowed actions for the given node
         */
        static AllowedLoopActionsEnt getAllowedActions(final NativeNodeContainer tail, final WorkflowBuildContext buildContext) {
            var loopState = LoopState.get(tail, buildContext);
            boolean canPause;
            boolean canResume;
            boolean canStep;

           switch (loopState) {
               // Comments indicate state transitions triggered by the corresponding actions (cf. NXT-848).
               case READY:
                   // "execute" action is set by `buildAllowedNodeActionsEnt` -> RUNNING
                   canPause = false;
                   canResume = false;
                   canStep = true;   // -> PAUSE_PENDING
                   break;
               case RUNNING:
                   canPause = true;  // -> PAUSE_PENDING
                   canStep = false;
                   canResume = false;
                   break;
               case PAUSE_PENDING:
                   // backend: -> PAUSED  or  -> DONE
                   canPause = false;
                   canStep = false;
                   canResume = false;
                   break;
               case PAUSED:
                   canPause = false;
                   canStep = true;   // -> PAUSE_PENDING
                   canResume = true; // -> RUNNING
                   break;
               default:  // NOSONAR: duplicate code block for readability
                   // DONE or NONE
                   canPause = false;
                   canStep = false;
                   canResume = false;
                   break;
           }

            return builder(AllowedLoopActionsEntBuilder.class)
                    .setCanPause(canPause)
                    .setCanResume(canResume)
                    .setCanStep(canStep)
                    .build();
        }
    }

    /**
     * The node position in the java-ui refers to the upper left corner of the 'node figure' which also includes
     * the (not always visible) implicit flow variables ports. I.e. the position does NOT match with the upper left
     * corner of the node background image. However, this is used as reference point in the web-ui. Thus, we need
     * to correct the position in y direction by some pixels.
     * (the value is chosen according to org.knime.workbench.editor2.figures.AbstractPortFigure.getPortSizeNode())
     *
     * NOTE: the current value has been 'experimentally' determined
     */
    public final int NODE_Y_POS_CORRECTION = 6;

    private final Map<String, NativeNodeInvariantsEnt> m_nativeNodeInvariantsCache = new ConcurrentHashMap<>();

    private final Map<String, NodePortDescriptionEntBuilder> m_nodePortBuilderCache = new ConcurrentHashMap<>();

    WorkflowEntityBuilder() {
        //
    }

    /**
     * Builds a new {@link WorkflowEnt} instance.
     *
     * @param wfm the workflow manager to build the workflow entity for
     *
     * @param buildContextBuilder contextual information required to build the {@link WorkflowEnt} instance
     * @return the newly created entity
     */
    public WorkflowEnt buildWorkflowEnt(final WorkflowManager wfm,
        final WorkflowBuildContextBuilder buildContextBuilder) { // NOSONAR
        try (WorkflowLock lock = wfm.lock()) {
            WorkflowBuildContext buildContext = buildContextBuilder.build(wfm);
            Collection<NodeContainer> nodeContainers = wfm.getNodeContainers();
            // linked hash map to retain iteration order!
            Map<String, NodeEnt> nodes = new LinkedHashMap<>();
            Map<String, NativeNodeInvariantsEnt> invariants = new HashMap<>();
            for (NodeContainer nc : nodeContainers) {
                buildAndAddNodeEnt(buildContext.buildNodeIDEnt(nc.getID()), nc, nodes, invariants, buildContext);
            }
            Map<String, ConnectionEnt> connections = wfm.getConnectionContainers().stream()
                .map(cc -> buildConnectionEnt(buildConnectionIDEnt(cc, buildContext), cc, buildContext))
                .collect(Collectors.toMap(c -> c.getId().toString(), c -> c)); // NOSONAR
            List<WorkflowAnnotationEnt> annotations = wfm.getWorkflowAnnotations().stream()
                .map(this::buildWorkflowAnnotationEnt).collect(Collectors.toList());
            var info = buildWorkflowInfoEnt(wfm, buildContext);
            return builder(WorkflowEntBuilder.class).setInfo(info)//
                .setNodes(nodes)//
                .setNodeTemplates(invariants)//
                .setConnections(connections)//
                .setWorkflowAnnotations(annotations)//
                .setAllowedActions(
                    buildContext.includeInteractionInfo() ? buildAllowedWorkflowActionsEnt(wfm, buildContext) : null)//
                .setParents(buildParentWorkflowInfoEnts(wfm, buildContext))//
                .setMetaInPorts(buildMetaPortsEntForWorkflow(wfm, true, buildContext))//
                .setMetaOutPorts(buildMetaPortsEntForWorkflow(wfm, false, buildContext))//
                .setProjectMetadata(wfm.isProject() ? buildProjectMetadataEnt(wfm) : null)//
                .setComponentMetadata(
                    CoreUtil.isComponentWFM(wfm) ? buildComponentNodeDescriptionEnt(getParentComponent(wfm)) : null)//
                .setDirty(wfm.isDirty()).build();
        }
    }

    /**
     * Add the port ranges for a single Map.Entry<String, PortGroupEntBuilder> if the port is used at all. Returns the
     * unmodified builder instead.
     *
     * @param entry The input builder
     * @param portEnts The list of all input or output ports used
     * @param isInputPort Whether this is an input port or not
     * @return The updated builder
     */
    private Map.Entry<String, PortGroupEntBuilder> addPortRangeToPortGroupEntBuilder(
        final Map.Entry<String, PortGroupEntBuilder> entry, final List<NodePortEnt> portEnts,
        final boolean isInputPort) {
        var id = entry.getKey();
        var builder = entry.getValue();
        var ids = portEnts.stream().map(NodePortEnt::getPortGroupId).collect(Collectors.toList());
        var minIdx = ids.indexOf(id);
        var maxIdx = ids.lastIndexOf(id);
        if (minIdx > -1 && maxIdx > -1) {
            return Map.entry(id, isInputPort ? builder.setInputRange(List.of(minIdx, maxIdx))
                : builder.setOutputRange(List.of(minIdx, maxIdx)));
        }
        return entry;
    }

    private AllowedConnectionActionsEnt buildAllowedConnectionActionsEnt(final ConnectionContainer cc,
        final WorkflowBuildContext buildContext) {
        boolean canDelete;
        if (!cc.isDeletable()) {
            canDelete = false;
        } else {
            WorkflowManager wfm = buildContext.wfm();
            if (cc.getDest().equals(wfm.getID())) {
                canDelete = wfm.canRemoveConnection(cc);
            } else {
                var nc = wfm.getNodeContainer(cc.getDest());
                canDelete = isNodeResetOrCanBeReset(nc.getNodeContainerState(), nc.getID(),
                    buildContext.dependentNodeProperties());
            }
        }
        return builder(AllowedConnectionActionsEntBuilder.class).setCanDelete(canDelete).build();
    }

    private AllowedNodeActionsEnt buildAllowedNodeActionsEnt(final NodeContainer nc,
        final WorkflowBuildContext buildContext) {
        var depNodeProps = buildContext.dependentNodeProperties();
        WorkflowManager parent = nc.getParent();
        NodeID id = nc.getID();
        boolean hasNodeDialog = NodeDialogManager.hasNodeDialog(nc);
        boolean hasLegacyNodeDialog = nc.hasDialog();
        return builder(AllowedNodeActionsEntBuilder.class)//
            .setCanExecute(depNodeProps.canExecuteNode(id))//
            .setCanReset(depNodeProps.canResetNode(id))//
            .setCanCancel(parent.canCancelNode(id))//
            .setCanOpenDialog((hasLegacyNodeDialog || hasNodeDialog) ? Boolean.TRUE : null)//
            .setCanOpenLegacyFlowVariableDialog(hasNodeDialog ? Boolean.TRUE : null)//
            .setCanOpenView(hasAndCanOpenNodeView(nc))//
            .setCanDelete(canDeleteNode(nc, id, depNodeProps))//
            .setCanCollapse(canCollapseNode(id, buildContext))//
            .setCanExpand(canExpandNode(nc, id, buildContext))//
            .build();
    }

    private AllowedWorkflowActionsEnt buildAllowedWorkflowActionsEnt(final WorkflowManager wfm,
            final WorkflowBuildContext buildContext) {
        return builder(AllowedWorkflowActionsEntBuilder.class)//
            .setCanReset(buildContext.dependentNodeProperties().canResetAny())
            .setCanExecute(wfm.canExecuteAll())//
            .setCanCancel(wfm.canCancelAll())//
            .setCanUndo(buildContext.canUndo())//
            .setCanRedo(buildContext.canRedo()).build();
    }

    private void buildAndAddNodeEnt(final NodeIDEnt id, final NodeContainer nc, final Map<String, NodeEnt> nodes,
        final Map<String, NativeNodeInvariantsEnt> invariants, final WorkflowBuildContext buildContext) {
        var nodeEnt = buildNodeEnt(id, nc, buildContext);
        nodes.put(nodeEnt.getId().toString(), nodeEnt);
        if (nc instanceof NativeNodeContainer) {
            String templateId = ((NativeNodeEnt)nodeEnt).getTemplateId();
            invariants.computeIfAbsent(templateId,
                tid -> buildOrGetFromCacheNativeNodeInvariantsEnt(templateId, (NativeNodeContainer)nc));
        }
    }

    private List<NodeDialogOptionDescriptionEnt> buildComponentDialogOptionsEnts(final SubNodeContainer snc) {
        List<SubNodeDescriptionProvider<? extends DialogNodeValue>> descs = snc.getDialogDescriptions();
        if (!descs.isEmpty()) {
            return descs.stream().map(d ->
                    builder(NodeDialogOptionDescriptionEnt.NodeDialogOptionDescriptionEntBuilder.class)
                        .setName(d.getLabel())
                        .setDescription(d.getDescription())
                        .build()
            ).collect(toList());
        } else {
            return null; // NOSONAR
        }
    }

    private List<NodePortDescriptionEnt>
        buildComponentInNodePortDescriptionEnts(final ComponentMetadata metadata, final SubNodeContainer snc) {
        if (snc.getNrInPorts() == 1) {
            return null; // NOSONAR
        }
        List<NodePortDescriptionEnt> res = new ArrayList<>();
        String[] names = metadata.getInPortNames().orElse(null);
        String[] descs = metadata.getInPortDescriptions().orElse(null);
        for (int i = 1; i < snc.getNrInPorts(); i++) {
            res.add(buildOrGetFromCacheNodePortDescriptionEnt(snc.getInPort(i).getPortType(),
                names == null ? null : names[i - 1], descs == null ? null : descs[i - 1]));
        }
        return res;
    }

    private ComponentNodeDescriptionEnt buildComponentNodeDescriptionEnt(final SubNodeContainer snc) {
        if (snc != null) {
            ComponentMetadata metadata = snc.getMetadata();
            String type = metadata.getNodeType().map(ComponentNodeType::toString).orElse(null);
            return builder(ComponentNodeDescriptionEntBuilder.class)//
                .setName(snc.getName())//
                .setIcon(createIconDataURL(metadata.getIcon().orElse(null)))//
                .setType(type == null ? null : ComponentNodeAndDescriptionEnt.TypeEnum.valueOf(type))//
                .setDescription(metadata.getDescription().orElse(null))//
                .setOptions(buildUngroupedDialogOptionGroupEnt(buildComponentDialogOptionsEnts(snc))) //
                .setViews(buildComponentViewDescriptionEnts(snc))//
                .setInPorts(buildComponentInNodePortDescriptionEnts(metadata, snc))//
                .setOutPorts(buildComponentOutNodePortDescriptionEnts(metadata, snc))//
                .build();
        }
        return null;
    }

    private ComponentNodeEnt buildComponentNodeEnt(final NodeIDEnt id, final SubNodeContainer nc,
        final AllowedNodeActionsEnt allowedActions, final WorkflowBuildContext buildContext) {
        ComponentMetadata metadata = nc.getMetadata();
        String type = metadata.getNodeType().map(ComponentNodeType::toString).orElse(null);
        var hasDialog = NodeDialogManager.hasNodeDialog(nc) ? Boolean.TRUE : null;
        return builder(ComponentNodeEntBuilder.class).setName(nc.getName())//
            .setId(id)//
            .setType(type == null ? null : ComponentNodeAndDescriptionEnt.TypeEnum.valueOf(type))//
            .setOutPorts(buildNodePortEnts(nc, false, buildContext))//
            .setAnnotation(buildNodeAnnotationEnt(nc.getNodeAnnotation()))//
            .setInPorts(buildNodePortEnts(nc, true, buildContext))//
            .setPosition(buildXYEnt(nc.getUIInformation()))//
            .setState(buildNodeStateEnt(nc))//
            .setIcon(createIconDataURL(nc.getMetadata().getIcon().orElse(null)))//
            .setKind(KindEnum.COMPONENT)//
            .setLink(getTemplateLink(nc))//
            .setHasDialog(hasDialog)//
            .setAllowedActions(allowedActions)//
            .setExecutionInfo(buildNodeExecutionInfoEnt(nc)) //
            .build();
    }

    private List<NodePortDescriptionEnt>
        buildComponentOutNodePortDescriptionEnts(final ComponentMetadata metadata, final SubNodeContainer snc) {
        if (snc.getNrOutPorts() == 1) {
            return null; // NOSONAR
        }
        List<NodePortDescriptionEnt> res = new ArrayList<>();
        String[] names = metadata.getOutPortNames().orElse(null);
        String[] descs = metadata.getOutPortDescriptions().orElse(null);
        for (int i = 1; i < snc.getNrOutPorts(); i++) {
            res.add(buildOrGetFromCacheNodePortDescriptionEnt(snc.getOutPort(i).getPortType(),
                names == null ? null : names[i - 1], descs == null ? null : descs[i - 1]));
        }
        return res;
    }

    private List<NodeViewDescriptionEnt> buildComponentViewDescriptionEnts(final SubNodeContainer snc) {
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

    private ConnectionEnt buildConnectionEnt(final ConnectionIDEnt id, final ConnectionContainer cc,
        final WorkflowBuildContext buildContext) {
        ConnectionEntBuilder builder = builder(ConnectionEntBuilder.class)//
            .setId(id)//
            .setDestNode(id.getDestNodeIDEnt())//
            .setDestPort(cc.getDestPort())//
            .setSourceNode(buildContext.buildNodeIDEnt(cc.getSource())).setSourcePort(cc.getSourcePort())//
            .setFlowVariableConnection(cc.isFlowVariablePortConnection() ? cc.isFlowVariablePortConnection() : null);
        if (buildContext.isInStreamingMode()) {
            var connectionProgress = cc.getConnectionProgress().orElse(null);
            if (connectionProgress != null) {
                builder.setLabel(connectionProgress.getMessage()).setStreaming(connectionProgress.inProgress());
            }
        }
        if (buildContext.includeInteractionInfo()) {
            builder.setAllowedActions(buildAllowedConnectionActionsEnt(cc, buildContext));
        }
        return builder.build();
    }

    private ConnectionIDEnt buildConnectionIDEnt(final ConnectionContainer c, final WorkflowBuildContext buildContext) {
        return new ConnectionIDEnt(buildContext.buildNodeIDEnt(c.getDest()), c.getDestPort());
    }

    private CustomJobManagerEnt buildCustomJobManagerEnt(final NodeExecutionJobManager jobManager) {
        NodeExecutionJobManagerFactory factory = NodeExecutionJobManagerPool.getJobManagerFactory(jobManager.getID());
        String name = factory == null ? jobManager.getID() : factory.getLabel();
        String icon = null;
        String iconForWorkflow = null;
        try {
            icon = createIconDataURL(jobManager.getIcon());
        } catch (IOException ex) {
            NodeLogger.getLogger(WorkflowEntityBuilder.class)
                .error(String.format("Icon for job manager '%s' couldn't be read", name), ex);
        }
        if (jobManager instanceof AbstractNodeExecutionJobManager) {
            try {
                iconForWorkflow = createIconDataURL(((AbstractNodeExecutionJobManager)jobManager).getIconForWorkflow());
            } catch (IOException ex) {
                NodeLogger.getLogger(WorkflowEntityBuilder.class)
                    .error(String.format("Workflow icon for job manager '%s' couldn't be read", name), ex);
            }
        }
        return builder(CustomJobManagerEntBuilder.class).setName(name).setIcon(icon).setWorkflowIcon(iconForWorkflow)
            .build();
    }



    private JobManagerEnt buildJobManagerEnt(final NodeExecutionJobManager jobManager) {
        if (CoreUtil.isDefaultOrNullJobManager(jobManager)) {
            return null;
        } else if (CoreUtil.isStreamingJobManager(jobManager)) {
            return builder(JobManagerEntBuilder.class)
                .setType(org.knime.gateway.api.webui.entity.JobManagerEnt.TypeEnum.STREAMING).build();
        } else {
            return builder(JobManagerEntBuilder.class)
                .setType(org.knime.gateway.api.webui.entity.JobManagerEnt.TypeEnum.OTHER)
                .setCustom(buildCustomJobManagerEnt(jobManager)).build();
        }
    }

    private List<LinkEnt> buildLinkEnts(final List<Link> links) {
        return links.stream()
            .map(link -> builder(LinkEntBuilder.class).setUrl(link.getUrl()).setText(link.getText()).build())
            .collect(Collectors.toList());
    }

    private LoopInfoEnt buildLoopInfoEnt(final NativeNodeContainer nc, final WorkflowBuildContext buildContext) {
        if (!nc.isModelCompatibleTo(LoopEndNode.class)) {
            return null;
        }
        var status = StatusEnum.valueOf(nc.getLoopStatus().name());
        AllowedLoopActionsEnt allowedActions = null;
        if (buildContext.includeInteractionInfo()) {
            allowedActions = LoopState.getAllowedActions(nc, buildContext);
        }
        return builder(LoopInfoEntBuilder.class).setStatus(status).setAllowedActions(allowedActions).build();
    }

    private MetaNodeEnt buildMetaNodeEnt(final NodeIDEnt id, final WorkflowManager wm,
        final AllowedNodeActionsEnt allowedActions, final WorkflowBuildContext buildContext) {
        return builder(MetaNodeEntBuilder.class).setName(wm.getName()).setId(id)//
            .setOutPorts(buildMetaNodePortEnts(wm, false, buildContext))//
            .setAnnotation(buildNodeAnnotationEnt(wm.getNodeAnnotation()))//
            .setInPorts(buildMetaNodePortEnts(wm, true, buildContext))//
            .setPosition(buildXYEnt(wm.getUIInformation()))//
            .setState(buildMetaNodeStateEnt(wm.getNodeContainerState()))//
            .setKind(KindEnum.METANODE)//
            .setLink(getTemplateLink(wm))//
            .setAllowedActions(allowedActions)//
            .setExecutionInfo(buildNodeExecutionInfoEnt(wm))//
            .build();
    }

    private List<MetaNodePortEnt> buildMetaNodePortEnts(final WorkflowManager wm, final boolean inPorts,
        final WorkflowBuildContext buildContext) {
        return buildNodePortEnts(wm, inPorts, buildContext).stream().map(np -> { // NOSONAR
            NodeStateEnum nodeState;
            if (!inPorts) {
                nodeState = getNodeStateEnumForMetaNodePort(wm.getOutPort(np.getIndex()).getNodeState());
            } else {
                nodeState = null;
            }
            return builder(MetaNodePortEntBuilder.class)//
                .setTypeId(np.getTypeId())//
                .setConnectedVia(np.getConnectedVia())//
                .setInactive(np.isInactive())//
                .setIndex(np.getIndex())//
                .setInfo(np.getInfo())//
                .setName(np.getName())//
                .setOptional(np.isOptional())//
                .setPortObjectVersion(np.getPortObjectVersion())//
                .setNodeState(nodeState)//
                .setCanRemove(np.isCanRemove())//
                .build();
        }).collect(toList());
    }

    private MetaNodeStateEnt buildMetaNodeStateEnt(final NodeContainerState state) {
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

    private MetaPortsEnt buildMetaPortsEntForWorkflow(final WorkflowManager wfm, final boolean incoming,
        final WorkflowBuildContext buildContext) {
        if (wfm.isProject() || wfm.getDirectNCParent() instanceof SubNodeContainer) {
            // no meta ports for workflow projects and component workflows
            return null;
        }
        List<NodePortEnt> ports = buildNodePortEntsForWorkflow(wfm, incoming, buildContext);
        MetaPortsEntBuilder builder = builder(MetaPortsEntBuilder.class);
        builder.setPorts(ports);

        NodeUIInformation barUIInfo = incoming ? wfm.getInPortsBarUIInfo() : wfm.getOutPortsBarUIInfo();
        if (barUIInfo != null) {
            builder.setXPos(barUIInfo.getBounds()[0]);
        }
        return builder.build();
    }

    /**
     * TODO: NXT-1189 Gateway API: Add `ExchangeablePortGroup` port types to `buildNativeNodeEnt()` method
     */
    private NativeNodeEnt buildNativeNodeEnt(final NodeIDEnt id, final NativeNodeContainer nnc,
        final AllowedNodeActionsEnt allowedActions, final WorkflowBuildContext buildContext) {
        var inPorts = buildNodePortEnts(nnc, true, buildContext);
        var outPorts = buildNodePortEnts(nnc, false, buildContext);
        var portGroups = buildPortGroupEntsMapOptional(nnc, inPorts, outPorts, buildContext).orElse(null);
        var hasDialog = NodeDialogManager.hasNodeDialog(nnc) ? Boolean.TRUE : null;
        var hasView = NodeViewManager.hasNodeView(nnc) ? Boolean.TRUE : null;
        return builder(NativeNodeEntBuilder.class)//
            .setId(id)//
            .setKind(KindEnum.NODE)//
            .setInPorts(inPorts)//
            .setOutPorts(outPorts)//
            .setPortGroups(portGroups)//
            .setAnnotation(buildNodeAnnotationEnt(nnc.getNodeAnnotation()))//
            .setPosition(buildXYEnt(nnc.getUIInformation()))//
            .setState(buildNodeStateEnt(nnc))//
            .setTemplateId(EntityBuilderUtil.NodeTemplateAndDescription.createTemplateId(nnc.getNode().getFactory()))//
            .setAllowedActions(allowedActions)//
            .setExecutionInfo(buildNodeExecutionInfoEnt(nnc))//
            .setLoopInfo(buildLoopInfoEnt(nnc, buildContext))//
            .setHasDialog(hasDialog)
            .setHasView(hasView)//
            .build();
    }

    private NativeNodeInvariantsEnt buildNativeNodeInvariantsEnt(final NativeNodeContainer nc) {
        NativeNodeInvariantsEntBuilder builder = builder(NativeNodeInvariantsEntBuilder.class)
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

    private NodeAnnotationEnt buildNodeAnnotationEnt(final NodeAnnotation na) {
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
            Arrays.stream(na.getStyleRanges()).map(this::buildStyleRangeEnt).collect(Collectors.toList());
        return builder(NodeAnnotationEntBuilder.class)
                .setTextAlign(textAlign)//
                .setBackgroundColor(na.getBgColor() == DEFAULT_NODE_ANNOTATION_BG_COLOR ? null : hexStringColor(na
                    .getBgColor()))//
                .setText(na.getText())//
                .setStyleRanges(styleRanges)//
                .setDefaultFontSize(na.getDefaultFontSize() > 0 ? na.getDefaultFontSize() : null)//
                .build();
    }

    private NodeEnt buildNodeEnt(final NodeIDEnt id, final NodeContainer nc,
        final AllowedNodeActionsEnt allowedActions, final WorkflowBuildContext buildContext) {
        if (nc instanceof NativeNodeContainer) {
            return buildNativeNodeEnt(id, (NativeNodeContainer)nc, allowedActions, buildContext);
        } else if (nc instanceof WorkflowManager) {
            return buildMetaNodeEnt(id, (WorkflowManager)nc, allowedActions, buildContext);
        } else if (nc instanceof SubNodeContainer) {
            return buildComponentNodeEnt(id, (SubNodeContainer)nc, allowedActions, buildContext);
        } else {
            throw new IllegalArgumentException(
                "Node container " + nc.getClass().getName() + " cannot be mapped to a node entity.");
        }
    }

    private NodeEnt buildNodeEnt(final NodeIDEnt id, final NodeContainer nc,
        final WorkflowBuildContext buildContext) {
        AllowedNodeActionsEnt allowedActions =
            buildContext.includeInteractionInfo() ? buildAllowedNodeActionsEnt(nc, buildContext) : null;
        return buildNodeEnt(id, nc, allowedActions, buildContext);
    }

    private NodeExecutionInfoEnt buildNodeExecutionInfoEnt(final NodeContainer nc) {
        var jobManagerEnt = buildJobManagerEnt(nc.getJobManager());
        if (jobManagerEnt != null) {
            return builder(NodeExecutionInfoEntBuilder.class).setJobManager(jobManagerEnt).build();
        } else {
            NodeExecutionJobManager parentJobManager = nc.getParent().findJobManager();
            if (!CoreUtil.isDefaultOrNullJobManager(parentJobManager)) {
                return buildNodeExecutionInfoEntFromParentJobManager(parentJobManager, nc);
            }
        }
        return null;
    }

    private NodeExecutionInfoEnt buildNodeExecutionInfoEntFromParentJobManager(
        final NodeExecutionJobManager parentJobManager, final NodeContainer nc) {
        if (CoreUtil.isStreamingJobManager(parentJobManager)) {
            if (nc instanceof NativeNodeContainer) {
                return builder(NodeExecutionInfoEntBuilder.class).setStreamable(isStreamable((NativeNodeContainer)nc))
                    .build();
            } else {
                return null;
            }
        } else if (parentJobManager instanceof AbstractNodeExecutionJobManager) {
            try {
                return builder(NodeExecutionInfoEntBuilder.class)
                    .setIcon(createIconDataURL(((AbstractNodeExecutionJobManager)parentJobManager).getIconForChild(nc)))
                    .build();
            } catch (IOException ex) {
                NodeLogger.getLogger(WorkflowEntityBuilder.class).error(String.format(
                    "Problem reading icon for job manager '%s' and node '%s'.", parentJobManager.getID(), nc), ex);
                return null;
            }
        } else {
            return null;
        }
    }

    NodeFactoryKeyEnt buildNodeFactoryKeyEnt(final NodeFactory<? extends NodeModel> factory) {
        NodeFactoryKeyEntBuilder nodeFactoryKeyBuilder = builder(NodeFactoryKeyEntBuilder.class);
        if (factory != null) {
            nodeFactoryKeyBuilder.setClassName(factory.getClass().getName());
            //only set settings in case of a dynamic node factory
            if (DynamicNodeFactory.class.isAssignableFrom(factory.getClass())) {
                var settings = new NodeSettings("settings");
                factory.saveAdditionalFactorySettings(settings);
                nodeFactoryKeyBuilder.setSettings(JSONConfig.toJSONString(settings, WriterConfig.DEFAULT));
            }
        } else {
            nodeFactoryKeyBuilder.setClassName("");
        }
        return nodeFactoryKeyBuilder.build();
    }

    private NodePortDescriptionEntBuilder buildNodePortDescriptionEntBuilder(final PortType ptype) {
        return builder(NodePortDescriptionEntBuilder.class)//
            .setTypeId(CoreUtil.getPortTypeId(ptype))//
            .setTypeName(ptype.getName())//
            .setOptional(ptype.isOptional());
    }

    @SuppressWarnings("java:S107") // it's a 'builder'-method, so many parameters are ok
    private NodePortEnt buildNodePortEnt(final PortType ptype, final String name, final String info,
        final int portIdx, final Boolean isOptional, final Boolean isInactive, final Boolean canRemovePort,
        final Collection<ConnectionContainer> connections, final Integer portObjectVersion, final String portGroupId,
        final WorkflowBuildContext buildContext) {
        return builder(NodePortEntBuilder.class) //
            .setIndex(portIdx)//
            .setOptional(isOptional)//
            .setInactive(isInactive)//
            .setConnectedVia(
                connections.stream().map(cc -> buildConnectionIDEnt(cc, buildContext)).collect(Collectors.toList()))//
            .setName(name)//
            .setInfo(info)//
            .setTypeId(CoreUtil.getPortTypeId(ptype))//
            .setPortObjectVersion(portObjectVersion)//
            .setPortGroupId(portGroupId)//
            .setCanRemove(canRemovePort)//
            .build();
    }

    private List<NodePortEnt> buildNodePortEnts(final NodeContainer nc, final boolean isInputPorts,
        final WorkflowBuildContext buildContext) {
        List<NodePortEnt> res = new ArrayList<>();
        if (isInputPorts) {
            for (var i = 0; i < nc.getNrInPorts(); i++) {
                var canRemovePort = canRemovePort(nc, i, isInputPorts, buildContext);
                ConnectionContainer connection = nc.getParent().getIncomingConnectionFor(nc.getID(), i);
                List<ConnectionContainer> connections =
                    connection == null ? emptyList() : Collections.singletonList(connection);
                NodeInPort inPort = nc.getInPort(i);
                var portGroupId = getPortGroupNameForDynamicNativeNodePort(nc, i, true, buildContext);
                var pt = inPort.getPortType();
                res.add(buildNodePortEnt(pt, inPort.getPortName(), null, i, pt.isOptional(), null, canRemovePort,
                    connections, null, portGroupId, buildContext));
            }
        } else {
            for (var i = 0; i < nc.getNrOutPorts(); i++) {
                var canRemovePort = canRemovePort(nc, i, isInputPorts, buildContext);
                Set<ConnectionContainer> connections = nc.getParent().getOutgoingConnectionsFor(nc.getID(), i);
                NodeOutPort outPort = nc.getOutPort(i);
                var portGroupId = getPortGroupNameForDynamicNativeNodePort(nc, i, false, buildContext);
                var pt = outPort.getPortType();
                res.add(buildNodePortEnt(pt, outPort.getPortName(), outPort.getPortSummary(), i, null,
                    outPort.isInactive() ? outPort.isInactive() : null, canRemovePort, connections,
                    getPortObjectVersion(outPort, buildContext), portGroupId, buildContext));
            }
        }
        return res;
    }

    private List<NodePortEnt> buildNodePortEntsForWorkflow(final WorkflowManager wfm, final boolean incoming,
        final WorkflowBuildContext buildContext) {
        List<NodePortEnt> ports = new ArrayList<>();
        if (incoming) {
            int nrPorts = wfm.getNrWorkflowIncomingPorts();
            for (var i = 0; i < nrPorts; i++) {
                var canRemovePort = canRemoveContainerNodePort(wfm, i, incoming);
                Set<ConnectionContainer> connections = wfm.getOutgoingConnectionsFor(wfm.getID(), i);
                NodeOutPort port = wfm.getWorkflowIncomingPort(i);
                var isInactive = port.isInactive() ? Boolean.TRUE : null;
                var portObjectVersion = getPortObjectVersion(port, buildContext);
                ports.add(buildNodePortEnt(port.getPortType(), port.getPortName(), port.getPortSummary(), i, null,
                    isInactive, canRemovePort, connections, portObjectVersion, null, buildContext));
            }
        } else {
            int nrPorts = wfm.getNrWorkflowOutgoingPorts();
            for (var i = 0; i < nrPorts; i++) {
                var canRemovePort = canRemoveContainerNodePort(wfm, i, incoming);
                ConnectionContainer connection = wfm.getIncomingConnectionFor(wfm.getID(), i);
                Collection<ConnectionContainer> connections = connection != null ? singleton(connection) : emptyList();
                NodeInPort port = wfm.getWorkflowOutgoingPort(i);
                ports.add(buildNodePortEnt(port.getPortType(), port.getPortName(), null, i, null, null, canRemovePort,
                    connections, null, null, buildContext));
            }
        }
        return ports;
    }

    private NodeStateEnt buildNodeStateEnt(final SingleNodeContainer nc) {
        if (nc.isInactive()) {
            return null;
        }
        var ncState = nc.getNodeContainerState();
        NodeStateEntBuilder builder =
            builder(NodeStateEntBuilder.class).setExecutionState(getNodeExecutionStateEnum(ncState));
        var nodeMessage = nc.getNodeMessage();
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

    private NativeNodeInvariantsEnt buildOrGetFromCacheNativeNodeInvariantsEnt(final String templateId,
        final NativeNodeContainer nc) {
        return m_nativeNodeInvariantsCache.computeIfAbsent(templateId, id -> buildNativeNodeInvariantsEnt(nc));
    }

    private NodePortDescriptionEnt buildOrGetFromCacheNodePortDescriptionEnt(final PortType ptype,
        final String name, final String description) {
        NodePortDescriptionEntBuilder builder = m_nodePortBuilderCache.computeIfAbsent(
            ptype.getPortObjectClass().getName(), k -> buildNodePortDescriptionEntBuilder(ptype));
        builder.setName(isBlank(name) ? null : name);
        builder.setDescription(isBlank(description) ? null : description);
        return builder.build();
    }

    private List<WorkflowInfoEnt> buildParentWorkflowInfoEnts(final WorkflowManager wfm,
        final WorkflowBuildContext buildContext) {
        if (wfm.isProject() || wfm.isComponentProjectWFM()) {
            return null; // NOSONAR
        }
        List<WorkflowInfoEnt> parents = new ArrayList<>();
        WorkflowManager parent = wfm;
        do {
            parent = getWorkflowParent(parent);
            parents.add(buildWorkflowInfoEnt(parent, buildContext));
        } while (!parent.isProject() && !parent.isComponentProjectWFM());
        Collections.reverse(parents);
        return parents;
    }

    private Map<String, PortGroupEnt> buildPortGroupEntsMap(final Map<String, ExtendablePortGroup> portGroups,
        final List<NodePortEnt> inPorts, final List<NodePortEnt> outPorts, final Predicate<String> canEditPorts) {
        return portGroups.entrySet().stream()//
            .map(entry -> initializePortGroupEntBuilder(entry, canEditPorts.test(entry.getKey())))//
            .map(entry -> addPortRangeToPortGroupEntBuilder(entry, inPorts, true))//
            .map(entry -> addPortRangeToPortGroupEntBuilder(entry, outPorts, false))//
            .collect(Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue().build()));
    }

    /**
     * TODO: NXT-1189 Gateway API: Add `ExchangeablePortGroup` port types to `buildNativeNodeEnt()` method
     *
     * Build a map of port group entities mapped by their port group id. If that doesn't apply, we just return an empty
     * optional.
     *
     * @param nnc The native node container the result is for
     * @param inPorts List of input ports present for the node
     * @param outPorts List of output ports present for the node
     * @param buildContext The build context
     * @return A map of {@link PortGroupEnt} entities mapped by their port group id
     */
    private Optional<Map<String, PortGroupEnt>> buildPortGroupEntsMapOptional(final NativeNodeContainer nnc,
        final List<NodePortEnt> inPorts, final List<NodePortEnt> outPorts, final WorkflowBuildContext buildContext) {
        if (!buildContext.includeInteractionInfo()) {
            return Optional.empty();
        }
        var canRemoveIncomingConnections =
            buildContext.dependentNodeProperties().canRemoveIncomingConnections(nnc.getID());
        return buildContext.getPortsConfiguration(nnc).map(portsConfig -> {
            var portGroups = portsConfig.getExtendablePorts(); // Only supports `ExtendablePortGroup` for now
            Predicate<String> canEditPorts =
                portGroupId -> canRemoveIncomingConnections && portsConfig.isInteractive(portGroupId); // Port group must be interactive
            return buildPortGroupEntsMap(portGroups, inPorts, outPorts, canEditPorts);
        });
    }

    private ProjectMetadataEnt buildProjectMetadataEnt(final WorkflowManager wfm) {
        assert wfm.isProject();
        final ReferencedFile rf = wfm.getWorkingDir();
        var metadataFile = new File(rf.getFile(), WorkflowPersistor.METAINFO_FILE);
        if (metadataFile.exists()) {
            WorkflowGroupMetadata metadata;
            try {
                metadata = Workflowalizer.readWorkflowGroup(metadataFile.toPath());
            } catch (XPathExpressionException | SAXException | IOException ex) {
                NodeLogger.getLogger(WorkflowEntityBuilder.class).error("Workflow metadata could not be read", ex);
                return null;
            }
            return builder(ProjectMetadataEntBuilder.class)
                    .setDescription(metadata.getDescription().orElse(null))
                    .setLastEdit(wfm.getAuthorInformation().getLastEditDate()
                        // the Date class doesn't support time zones. We just assume UTC here to create an OffsetDateTime
                        .map(date -> date.toInstant().atOffset(ZoneOffset.UTC)).orElse(null))
                    .setLinks(metadata.getLinks().map(this::buildLinkEnts).orElse(null))
                    .setTags(metadata.getTags().orElse(null))
                    .setTitle(metadata.getTitle().orElse(null)).build();
        } else {
            return null;
        }
    }

    private StyleRangeEnt buildStyleRangeEnt(final StyleRange sr) {
        StyleRangeEntBuilder builder = builder(StyleRangeEntBuilder.class)
                .setFontSize(sr.getFontSize())//
                .setColor(hexStringColor(sr.getFgColor()))//
                .setLength(sr.getLength())//
                .setStart(sr.getStart());
        if ((sr.getFontStyle() & StyleRange.BOLD) != 0) {
            builder.setBold(Boolean.TRUE);
        }
        if ((sr.getFontStyle() & StyleRange.ITALIC) != 0) {
            builder.setItalic(Boolean.TRUE);
        }
        return builder.build();
    }

    private List<NodeDialogOptionGroupEnt> buildUngroupedDialogOptionGroupEnt(final List<NodeDialogOptionDescriptionEnt> ungroupedOptionEnts) {
        if (Objects.isNull(ungroupedOptionEnts) || ungroupedOptionEnts.isEmpty()) {
            return null;  // NOSONAR: returning null is useful here
        }
        return Collections.singletonList(
                builder(NodeDialogOptionGroupEnt.NodeDialogOptionGroupEntBuilder.class) //
                        .setSectionName(null) //
                        .setSectionDescription(null) //
                        .setFields(ungroupedOptionEnts) //
                        .build()
        );
    }

    private WorkflowAnnotationEnt buildWorkflowAnnotationEnt(final WorkflowAnnotation wa) {
        BoundsEnt bounds = builder(BoundsEntBuilder.class)
                .setX(wa.getX())//
                .setY(wa.getY())//
                .setWidth(wa.getWidth())//
                .setHeight(wa.getHeight())//
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
            Arrays.stream(wa.getStyleRanges()).map(this::buildStyleRangeEnt).collect(Collectors.toList());
        return builder(WorkflowAnnotationEntBuilder.class)
                .setId(new AnnotationIDEnt(wa.getID()))//
                .setTextAlign(textAlign)//
                .setBackgroundColor(hexStringColor(wa.getBgColor()))//
                .setBorderColor(hexStringColor(wa.getBorderColor()))//
                .setBorderWidth(wa.getBorderSize())//
                .setBounds(bounds)//
                .setText(wa.getText())//
                .setStyleRanges(styleRanges)//
                .setDefaultFontSize(wa.getDefaultFontSize() > 0 ? wa.getDefaultFontSize() : null)//
                .build();
    }

    private WorkflowInfoEnt buildWorkflowInfoEnt(final WorkflowManager wfm, final WorkflowBuildContext buildContext) {
        NodeContainerTemplate template;
        if (wfm.getDirectNCParent() instanceof SubNodeContainer) {
            template = (NodeContainerTemplate)wfm.getDirectNCParent();
        } else {
            template = wfm;
        }
        return builder(WorkflowInfoEntBuilder.class)//
            .setName(wfm.getName())//
            .setContainerId(getContainerId(wfm, buildContext))//
            .setContainerType(getContainerType(wfm))//
            .setLinked(getTemplateLink(template) == null ? null : Boolean.TRUE)//
            .setJobManager(buildJobManagerEnt(wfm.findJobManager())).build();
    }

    private XYEnt buildXYEnt(final NodeUIInformation uiInfo) {
        int[] bounds = uiInfo.getBounds();
        return builder(XYEntBuilder.class).setX(bounds[0]).setY(bounds[1] + NODE_Y_POS_CORRECTION).build();
    }

    private AllowedNodeActionsEnt.CanCollapseEnum canCollapseNode(final NodeID id,
        final WorkflowBuildContext buildContext) {
        var isResettable = buildContext.dependentNodeProperties().canResetNode(id);
        if (isResettable) {
            return AllowedNodeActionsEnt.CanCollapseEnum.RESETREQUIRED;
        }

        var hasExcecutingSuccessors = buildContext.dependentNodeProperties().hasExecutingSuccessor(id);
        if (hasExcecutingSuccessors) {
            return AllowedNodeActionsEnt.CanCollapseEnum.FALSE;
        }

        return AllowedNodeActionsEnt.CanCollapseEnum.TRUE;
    }

    private Boolean canDeleteNode(final NodeContainer nc, final NodeID nodeId,
        final DependentNodeProperties depNodeProps) {
        if (!nc.isDeletable()) {
            return Boolean.FALSE;
        } else {
            return isNodeResetOrCanBeReset(nc.getNodeContainerState(), nodeId, depNodeProps);
        }
    }

    private AllowedNodeActionsEnt.CanExpandEnum canExpandNode(final NodeContainer nc, final NodeID id,
        final WorkflowBuildContext buildContext) {
        if (!(nc instanceof WorkflowManager || nc instanceof SubNodeContainer)) {
            return null;
        }

        var isResettable = buildContext.dependentNodeProperties().canResetNode(id);
        if (isResettable) {
            return AllowedNodeActionsEnt.CanExpandEnum.RESETREQUIRED;
        }

        if (!canExpandNodeContainer(id, nc, buildContext.wfm())) {
            return AllowedNodeActionsEnt.CanExpandEnum.FALSE;
        }

        return AllowedNodeActionsEnt.CanExpandEnum.TRUE;
    }

    private boolean canExpandNodeContainer(final NodeID id, final NodeContainer nc, final WorkflowManager wfm) {
        String cannotExpandReason = null;
        if (nc instanceof SubNodeContainer) {
            cannotExpandReason = wfm.canExpandSubNode(id);
        } else if (nc instanceof WorkflowManager) {
            cannotExpandReason = wfm.canExpandMetaNode(id);
        }
        return cannotExpandReason == null;
    }

    /**
     * Determine whether a given port can be removed from a container node.
     *
     * @param nc The node the port is attached to. Assumed to be a container node
     * @param portIndex The index of the queried port.
     * @param isInputPort Whether the queried is an input or an output port
     * @throws IllegalArgumentException If the given node is not a container node.
     * @apiNote For containers, the port at index 0 is always the fixed flow variable port. Metanodes do not have a
     *          fixed flow variable port.
     * @return Whether the queried port can currently be removed from the node.
     */
    @SuppressWarnings("java:S2301") // boolean parameter is reasonable.
    private boolean canRemoveContainerNodePort(final NodeContainer nc, final int portIndex,
        final boolean isInputPort) {
        var isContainerNode = (nc instanceof SubNodeContainer) || (nc instanceof WorkflowManager);
        if (!isContainerNode) {
            throw new IllegalArgumentException("Not a container node");
        }
        if (nc instanceof SubNodeContainer && portIndex == 0) {
            // First input/output port of component nodes is always a fixed flow variable port.
            return false;
        }
        var metaPortInfo = getContainerMetaPortInfo(nc, isInputPort);
        return !metaPortInfo[portIndex].isConnected(); // Port can only be removed if not connected.
    }

    /**
     * Determine whether a given port can be removed from a native node.
     *
     * @param nnc The node the port is attached to.
     * @param portIndex The index of the port. The enumeration begins with 0 at the fixed flow variable port.
     * @param isInputPort Whether the queried port is an input port. Assumed to be an output port if false.
     * @param buildContext context required to be able to re-use pre-calculated infos (in particular
     *            {@link WorkflowBuildContext#getPortIndexToPortGroupMap(NativeNodeContainer, boolean)}
     * @return Whether the queried port can currently be removed from the node.
     */
    private boolean canRemoveNativeNodePort(final NativeNodeContainer nnc, final int portIndex,
        final boolean isInputPort, final WorkflowBuildContext buildContext) {
        if (portIndex == 0) {
            return false; // Flow variable ports can never be removed
        }

        return buildContext.getPortIndexToPortGroupMap(nnc, isInputPort)//
            // There must be a port group found for the port
            .filter(portIndexToPortGroupMap -> portIndexToPortGroupMap[portIndex - 1] != null)
            // Check if port can be removed
            .map(portIndexToPortGroupMap -> {
                var portGroupName = portIndexToPortGroupMap[portIndex - 1];
                var isLastInGroup = portIndex == portIndexToPortGroupMap.length
                    || !portGroupName.equals(portIndexToPortGroupMap[portIndex]);
                return isLastInGroup && hasPortsAddedToPortGroup(nnc, buildContext, portGroupName);
            })//
            // False in all other cases
            .orElse(false);
    }

    private boolean hasPortsAddedToPortGroup(final NativeNodeContainer nnc,
        final WorkflowBuildContext buildContext, final String portGroupName) {
        return buildContext.getPortsConfiguration(nnc)//
            // Map to `ExtendablePortGroup` if there is any
            .map(portsConfig -> portsConfig.getExtendablePorts().get(portGroupName))//
            // Check if port group has at least one added port
            .map(ExtendablePortGroup::hasConfiguredPorts)//
            // False otherwise
            .orElse(false);
    }

    private Boolean canRemovePort(final NodeContainer nc, final int portIndex, final boolean isInputPort,
        final WorkflowBuildContext buildContext) {
        if (!buildContext.includeInteractionInfo()) {
            return null; // NOSONAR
        }
        if (nc instanceof NativeNodeContainer) {
            var nnc = (NativeNodeContainer)nc;
            return canRemoveNativeNodePort(nnc, portIndex, isInputPort, buildContext);
        } else {
            return canRemoveContainerNodePort(nc, portIndex, isInputPort);
        }

    }

    private String createIconDataURL(final byte[] iconData) {
        if (iconData != null) {
            final var dataUrlPrefix = "data:image/png;base64,";
            return dataUrlPrefix + new String(Base64.encodeBase64(iconData), StandardCharsets.UTF_8);
        } else {
            return null;
        }
    }

    String createIconDataURL(final NodeFactory<?> nodeFactory) {
        try {
            return createIconDataURL(nodeFactory.getIcon());
        } catch (IOException ex) {
            NodeLogger.getLogger(WorkflowEntityBuilder.class)
                .error(String.format("Icon for node '%s' couldn't be read", nodeFactory.getNodeName()), ex);
            return null;
        }
    }

    private String createIconDataURL(final URL url) throws IOException {
        if (url != null) {
            try (InputStream in = url.openStream()) {
                return createIconDataURL(IOUtils.toByteArray(in));
            }
        } else {
            return null;
        }
    }

    private NodeIDEnt getContainerId(final WorkflowManager wfm, final WorkflowBuildContext buildContext) {
        if (!wfm.isProject()) {
            NodeContainerParent ncParent = wfm.getDirectNCParent();
            if (ncParent instanceof SubNodeContainer) {
                // it's a component's workflow
                return buildContext.buildNodeIDEnt(((SubNodeContainer)ncParent).getID());
            }
        }
        // it's a project's or a metanode's workflow
        return buildContext.buildNodeIDEnt(wfm.getID());
    }

    private MetaPortInfo[] getContainerMetaPortInfo(final NodeContainer nc, final boolean inPorts) {
        var parentWfm = nc.getParent();
        if (nc instanceof WorkflowManager) {
            if (inPorts) {
                return parentWfm.getMetanodeInputPortInfo(nc.getID());
            } else {
                return parentWfm.getMetanodeOutputPortInfo(nc.getID());
            }
        } else if (nc instanceof SubNodeContainer) {
            if (inPorts) {
                return parentWfm.getSubnodeInputPortInfo(nc.getID());
            } else {
                return parentWfm.getSubnodeOutputPortInfo(nc.getID());
            }
        } else {
            throw new IllegalStateException("Queried node is not a container");
        }
    }

    private ContainerTypeEnum getContainerType(final WorkflowManager wfm) {
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

    private ExecutionStateEnum getNodeExecutionStateEnum(final NodeContainerState ncState) { // NOSONAR
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

    private NodeStateEnum getNodeStateEnumForMetaNodePort(final NodeContainerState ncState) { // NOSONAR
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

    private SubNodeContainer getParentComponent(final WorkflowManager wfm) {
        NodeContainerParent ncParent = wfm.getDirectNCParent();
        return ncParent instanceof SubNodeContainer ? (SubNodeContainer)ncParent : null;
    }

    /**
     * TODO: NXT-1189 Gateway API: Add `ExchangeablePortGroup` port types to `buildNativeNodeEnt()` method
     */
    private String getPortGroupNameForDynamicNativeNodePort(final NodeContainer nc, final int portIndex,
        final boolean isInputPort, final WorkflowBuildContext buildContext) {
        if (nc instanceof NativeNodeContainer) { // We are only interested in native nodes
            var nnc = (NativeNodeContainer)nc;
            if (portIndex == 0) {
                return null; // Flow variable ports are not interesting
            }
            return buildContext.getPortsConfiguration(nnc)//
                .flatMap(portsConfig -> buildContext.getPortIndexToPortGroupMap(nnc, isInputPort)//
                    // Map to port group name
                    .map(portIndexToPortGroupMap -> portIndexToPortGroupMap[portIndex - 1])//
                    // Only continue if port group is `ExtendablePortGroup`
                    .filter(portGroupName -> portsConfig.getGroup(portGroupName) instanceof ExtendablePortGroup))//
                .orElse(null);
        }
        return null;
    }

    private Integer getPortObjectVersion(final NodeOutPort outPort, final WorkflowBuildContext buildContext) {
        if (!buildContext.includeInteractionInfo()) {
            return null;
        }
        if (outPort.getPortType().equals(FlowVariablePortObject.TYPE)) {
            var hashCodeBuilder = new HashCodeBuilder();
            var flowObjectStack = outPort.getFlowObjectStack();
            if (flowObjectStack != null) {
                for (FlowVariable v : flowObjectStack.getAllAvailableFlowVariables().values()) {
                    hashCodeBuilder.append(v.getName());
                    hashCodeBuilder.append(v.getValue(v.getVariableType()).hashCode());
                }
            }
            return hashCodeBuilder.build();
        } else {
            var po = outPort.getPortObject();
            return po == null ? null : System.identityHashCode(po);
        }
    }

    private String getTemplateLink(final NodeContainerTemplate nct) {
        if (nct instanceof SubNodeContainer && ((SubNodeContainer)nct).isProject()) {
            return null;
        }
        var sourceURI = nct.getTemplateInformation().getSourceURI();
        return sourceURI == null ? null : sourceURI.toString();
    }

    private WorkflowManager getWorkflowParent(final WorkflowManager wfm) {
        NodeContainerParent parent = wfm.getDirectNCParent();
        if (parent instanceof SubNodeContainer) {
            return ((SubNodeContainer)parent).getParent();
        } else {
            return (WorkflowManager)parent;
        }
    }

    /*
     * Returns null if the node has no node view; false, if there is a node view but there is nothing to display,
     * true, if there is a node view which also has something to display.
     */
    private Boolean hasAndCanOpenNodeView(final NodeContainer nc) {
        var hasNodeView = NodeViewManager.hasNodeView(nc);
        var hasCompositeView =
            nc instanceof SubNodeContainer && WizardPageUtil.isWizardPage(nc.getParent(), nc.getID());
        var hasLegacyJSNodeView = nc instanceof NativeNodeContainer && nc.getInteractiveWebViews().size() > 0;
        if (hasNodeView || hasCompositeView || hasLegacyJSNodeView) {
            return nc.getNodeContainerState().isExecuted();
        }
        return null; // NOSONAR
    }

    String hexStringColor(final int color) {
        return String.format("#%06X", (0xFFFFFF & color));
    }

    /**
     * Maps a Map.Entry<String, ExtendablePortGroup> to a Map.Entry<String, PortGroupEntBuilder>. This function is used
     * to initialize the creation of the {@link PortGroupEnt} returned by `buildPortGroupEnts()`. As a side effect, it
     * also sets the supported port types and the `canAddInputPort` and `canAddOutputPort` properties.
     *
     * @param entry A single entry of the initial Map<String, ExtendablePortGroup>
     * @param canEditPorts Can ports be edited for the parent node
     * @return A single Map.Entry<String, PortGroupEntBuilder>
     */
    private Map.Entry<String, PortGroupEntBuilder> initializePortGroupEntBuilder(
        final Map.Entry<String, ExtendablePortGroup> entry, final boolean canEditPorts) {
        var portGroupId = entry.getKey();
        var portGroup = entry.getValue();
        var isInputPort = portGroup.definesInputPorts();
        var supportedTypeIds = Arrays.stream(portGroup.getSupportedPortTypes())//
            .map(CoreUtil::getPortTypeId)//
            .collect(toList());
        var canAddPort = canEditPorts && portGroup.canAddPort();
        return Map.entry(portGroupId, builder(PortGroupEntBuilder.class)//
            .setSupportedPortTypeIds(supportedTypeIds)//
            .setCanAddInPort(isInputPort ? canAddPort : null)//
            .setCanAddOutPort(!isInputPort ? canAddPort : null)//
            .setInputRange(List.of())//
            .setOutputRange(List.of()));
    }

    private boolean isExecuting(final NodeContainerState ncState) {
        return (ncState.isExecutionInProgress() && !ncState.isWaitingToBeExecuted()) || ncState.isExecutingRemotely();
    }

    private Boolean isNodeResetOrCanBeReset(final NodeContainerState state, final NodeID nodeId,
        final DependentNodeProperties depNodeProps) {
        if (state.isExecutionInProgress()) {
            return Boolean.FALSE;
        } else if (state.isExecuted()) {
            return depNodeProps.canResetNode(nodeId);
        } else {
            return Boolean.TRUE;
        }
    }

    private Boolean isStreamable(final NativeNodeContainer nc) {
        final Class<?> nodeModelClass = nc.getNode().getNodeModel().getClass();
        return IS_STREAMABLE.computeIfAbsent(nodeModelClass, CoreUtil::isStreamable);
    }

}

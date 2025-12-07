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
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.knime.gateway.api.entity.EntityBuilderManager.builder;
import static org.knime.gateway.api.util.EntityUtil.toLinkEnts;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.knime.core.node.NodeFactory;
import org.knime.core.node.NodeFactory.NodeType;
import org.knime.core.node.NodeLogger;
import org.knime.core.node.NodeModel;
import org.knime.core.node.config.base.JSONConfig;
import org.knime.core.node.config.base.JSONConfig.WriterConfig;
import org.knime.core.node.context.ports.ExtendablePortGroup;
import org.knime.core.node.extension.NodeSpec;
import org.knime.core.node.interactive.ReExecutable;
import org.knime.core.node.missing.MissingNodeFactory;
import org.knime.core.node.port.MetaPortInfo;
import org.knime.core.node.port.PortType;
import org.knime.core.node.util.NodeExecutionJobManagerPool;
import org.knime.core.node.wizard.WizardNodeFactoryExtension;
import org.knime.core.node.wizard.page.WizardPageUtil;
import org.knime.core.node.workflow.AbstractNodeExecutionJobManager;
import org.knime.core.node.workflow.AnnotationData.StyleRange;
import org.knime.core.node.workflow.AnnotationData.TextAlignment;
import org.knime.core.node.workflow.ComponentMetadata;
import org.knime.core.node.workflow.ConnectionContainer;
import org.knime.core.node.workflow.FlowScopeContext;
import org.knime.core.node.workflow.LoopEndNode;
import org.knime.core.node.workflow.MetaNodeTemplateInformation.Role;
import org.knime.core.node.workflow.NativeNodeContainer;
import org.knime.core.node.workflow.NodeAnnotation;
import org.knime.core.node.workflow.NodeContainer;
import org.knime.core.node.workflow.NodeContainerState;
import org.knime.core.node.workflow.NodeContainerTemplate;
import org.knime.core.node.workflow.NodeExecutionJobManager;
import org.knime.core.node.workflow.NodeID;
import org.knime.core.node.workflow.NodeMessage.Type;
import org.knime.core.node.workflow.NodeUIInformation;
import org.knime.core.node.workflow.SingleNodeContainer;
import org.knime.core.node.workflow.SubNodeContainer;
import org.knime.core.node.workflow.WorkflowAnnotation;
import org.knime.core.node.workflow.WorkflowManager;
import org.knime.core.node.workflow.contextv2.WorkflowContextV2;
import org.knime.core.node.workflow.contextv2.WorkflowContextV2.LocationType;
import org.knime.core.util.KnimeUrlType;
import org.knime.core.webui.node.dialog.NodeDialogManager;
import org.knime.core.webui.node.dialog.SubNodeContainerDialogFactory;
import org.knime.core.webui.node.view.NodeViewManager;
import org.knime.gateway.api.entity.AnnotationIDEnt;
import org.knime.gateway.api.entity.ConnectionIDEnt;
import org.knime.gateway.api.entity.NodeIDEnt;
import org.knime.gateway.api.util.CoreUtil;
import org.knime.gateway.api.util.DependentNodeProperties;
import org.knime.gateway.api.util.EntityUtil;
import org.knime.gateway.api.util.KnimeUrls;
import org.knime.gateway.api.util.VersionId;
import org.knime.gateway.api.webui.entity.AllowedConnectionActionsEnt;
import org.knime.gateway.api.webui.entity.AllowedConnectionActionsEnt.AllowedConnectionActionsEntBuilder;
import org.knime.gateway.api.webui.entity.AllowedLoopActionsEnt;
import org.knime.gateway.api.webui.entity.AllowedLoopActionsEnt.AllowedLoopActionsEntBuilder;
import org.knime.gateway.api.webui.entity.AllowedNodeActionsEnt;
import org.knime.gateway.api.webui.entity.AllowedNodeActionsEnt.AllowedNodeActionsEntBuilder;
import org.knime.gateway.api.webui.entity.AllowedWorkflowActionsEnt;
import org.knime.gateway.api.webui.entity.AllowedWorkflowActionsEnt.AllowedWorkflowActionsEntBuilder;
import org.knime.gateway.api.webui.entity.AnnotationEnt.TextAlignEnum;
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
import org.knime.gateway.api.webui.entity.EditableMetadataEnt;
import org.knime.gateway.api.webui.entity.EditableMetadataEnt.MetadataTypeEnum;
import org.knime.gateway.api.webui.entity.JobManagerEnt;
import org.knime.gateway.api.webui.entity.JobManagerEnt.JobManagerEntBuilder;
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
import org.knime.gateway.api.webui.entity.NodeEnt.DialogTypeEnum;
import org.knime.gateway.api.webui.entity.NodeEnt.KindEnum;
import org.knime.gateway.api.webui.entity.NodeExecutionInfoEnt;
import org.knime.gateway.api.webui.entity.NodeExecutionInfoEnt.NodeExecutionInfoEntBuilder;
import org.knime.gateway.api.webui.entity.NodeFactoryKeyEnt;
import org.knime.gateway.api.webui.entity.NodeFactoryKeyEnt.NodeFactoryKeyEntBuilder;
import org.knime.gateway.api.webui.entity.NodeIdAndIsExecutedEnt;
import org.knime.gateway.api.webui.entity.NodeIdAndIsExecutedEnt.NodeIdAndIsExecutedEntBuilder;
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
import org.knime.gateway.api.webui.entity.SpaceProviderEnt;
import org.knime.gateway.api.webui.entity.StyleRangeEnt;
import org.knime.gateway.api.webui.entity.StyleRangeEnt.StyleRangeEntBuilder;
import org.knime.gateway.api.webui.entity.TemplateLinkEnt;
import org.knime.gateway.api.webui.entity.TemplateLinkEnt.TemplateLinkEntBuilder;
import org.knime.gateway.api.webui.entity.TemplateLinkEnt.UpdateStatusEnum;
import org.knime.gateway.api.webui.entity.TypedTextEnt.ContentTypeEnum;
import org.knime.gateway.api.webui.entity.WorkflowAnnotationEnt;
import org.knime.gateway.api.webui.entity.WorkflowAnnotationEnt.WorkflowAnnotationEntBuilder;
import org.knime.gateway.api.webui.entity.WorkflowEnt;
import org.knime.gateway.api.webui.entity.WorkflowEnt.WorkflowEntBuilder;
import org.knime.gateway.api.webui.entity.WorkflowInfoEnt;
import org.knime.gateway.api.webui.entity.WorkflowInfoEnt.ContainerTypeEnum;
import org.knime.gateway.api.webui.entity.WorkflowInfoEnt.ProviderTypeEnum;
import org.knime.gateway.api.webui.entity.WorkflowInfoEnt.WorkflowInfoEntBuilder;
import org.knime.gateway.api.webui.entity.XYEnt;
import org.knime.gateway.api.webui.entity.XYEnt.XYEntBuilder;
import org.knime.gateway.api.webui.util.WorkflowBuildContext.WorkflowBuildContextBuilder;

/**
 * See {@link EntityFactory}.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
@SuppressWarnings("static-method")
public final class WorkflowEntityFactory {

    private static final NodeLogger LOGGER = NodeLogger.getLogger(WorkflowEntityFactory.class);

    /*
     * The default background color for node annotations which usually translates to opaque.
     */
    private static final int DEFAULT_NODE_ANNOTATION_BG_COLOR = 0xFFFFFF;

    private static final Map<Class<?>, Boolean> IS_STREAMABLE = new ConcurrentHashMap<>(0);

    private static final String ICON_DATA_URL_PREFIX = "data:image/png;base64,";

    /**
     * Characterization of loop state for determining allowed actions. This is not part of the API, see
     * {@link StatusEnum} instead.
     */
    private enum LoopState {

            /** Initial state, no loop iteration has been performed yet */
            READY,
            /** Loop is currently performing "full" execution (not step) */
            RUNNING,
            /**
             * Loop is currently executing and will be paused after this iteration (e.g. due to step execution, or pause
             * action)
             */
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
        @SuppressWarnings("java:S1142") // number of method returns
        static LoopState get(final NativeNodeContainer tail, final WorkflowBuildContext buildContext) {
            boolean hasLoopHead = CoreUtil.getLoopContext(tail).map(FlowScopeContext::getHeadNode)
                // When a loop head node is removed, the loop context may point to a node ID that is no longer
                //  present in the workflow.
                .map(head -> buildContext.wfm().containsNodeContainer(head)).orElse(false);
            var canExecuteDirectly = tail.getParent().canExecuteNodeDirectly(tail.getID());
            var loopStatus = tail.getLoopStatus();
            // Resume and step should not be enabled if nodes in the loop body are currently executing (this includes
            // outgoing dangling branches) ...
            var hasExecutingLoopBody = buildContext.dependentNodeProperties().hasExecutingLoopBody(tail);
            // ... and not if the tail node is currently waiting due to other reasons, such as... (cf. AP-18329)
            //      - a node upstream of the corresponding head is currently executing
            //      - a tail node of a nested loop is currently paused
            // It suffices to check only the direct predecessor since the "waiting" node state is propagated downstream.
            // We only need to check predecessors in the current workflow: Since scopes cannot leave workflows, for any
            //  validly constructed loop, both head and tail have to be in the workflow and the tail has to be reachable
            //  from the head. Consequently, the direct predecessor of a tail cannot be outside the current workflow.
            var hasWaitingPredecessor = CoreUtil.hasWaitingPredecessor(tail.getID(), buildContext.wfm());
            var loopBodyActive = hasExecutingLoopBody || hasWaitingPredecessor;

            if (!hasLoopHead) {
                return NONE;
            } else if (canExecuteDirectly) {
                return READY;
            } else if (loopStatus == NativeNodeContainer.LoopStatus.RUNNING) {
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
         *
         * @param tail The node to consider
         * @param buildContext The current dependent node properties
         * @return The allowed actions for the given node
         */
        static AllowedLoopActionsEnt getAllowedActions(final NativeNodeContainer tail,
            final WorkflowBuildContext buildContext) {
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
                    canStep = true; // -> PAUSE_PENDING
                    break;
                case RUNNING:
                    canPause = true; // -> PAUSE_PENDING
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
                    canStep = true; // -> PAUSE_PENDING
                    canResume = true; // -> RUNNING
                    break;
                default: // NOSONAR: duplicate code block for readability
                    // DONE or NONE
                    canPause = false;
                    canStep = false;
                    canResume = false;
                    break;
            }

            return builder(AllowedLoopActionsEntBuilder.class)//
                .setCanPause(canPause)//
                .setCanResume(canResume)//
                .setCanStep(canStep).build();
        }
    }

    /**
     * The node position in the java-ui refers to the upper left corner of the 'node figure' which also includes the
     * (not always visible) implicit flow variables ports. I.e. the position does NOT match with the upper left corner
     * of the node background image. However, this is used as reference point in the web-ui. Thus, we need to correct
     * the position in y direction by some pixels. (the value is chosen according to
     * org.knime.workbench.editor2.figures.AbstractPortFigure.getPortSizeNode())
     *
     * NOTE: the current value has been 'experimentally' determined
     */
    public static final int NODE_Y_POS_CORRECTION = 6;

    private final Map<String, NativeNodeInvariantsEnt> m_nativeNodeInvariantsCache = new ConcurrentHashMap<>();

    private final Map<String, NodePortDescriptionEntBuilder> m_nodePortBuilderCache = new ConcurrentHashMap<>();

    WorkflowEntityFactory() {
        //
    }

    private EditableMetadataEnt getMetadata(final WorkflowManager wfm) {
        var providingWfm = CoreUtil.getNonMetanodeSelfOrParent(wfm);
        if (providingWfm.isProject()) {
            return buildProjectMetadataEnt(providingWfm);
        }
        if (CoreUtil.isComponentWFM(providingWfm)) {
            return buildComponentNodeDescriptionEnt(getParentComponent(providingWfm));
        }
        return null;
    }

    /**
     * Builds a new {@link WorkflowEnt} instance.
     *
     * @param wfm the workflow manager to build the workflow entity for
     * @param buildContextBuilder contextual information required to build the {@link WorkflowEnt} instance
     * @return the newly created entity
     */
    public WorkflowEnt buildWorkflowEnt(final WorkflowManager wfm,
        final WorkflowBuildContextBuilder buildContextBuilder) { // NOSONAR
        try (var lock = wfm.lock()) {
            var buildContext = buildContextBuilder.build(wfm);
            var nodeContainers = wfm.getNodeContainers();
            // linked hash map to retain iteration order!
            Map<String, NodeEnt> nodes = new LinkedHashMap<>();
            Map<String, NativeNodeInvariantsEnt> invariants = new HashMap<>();
            for (var nc : nodeContainers) {
                if (nc instanceof WorkflowManager metanode && metanode.isHiddenInUI()) {
                    continue;
                }
                buildAndAddNodeEnt(buildContext.buildNodeIDEnt(nc.getID()), nc, nodes, invariants, buildContext);
            }

            var connections = wfm.getConnectionContainers().stream()
                .map(cc -> buildConnectionEnt(buildConnectionIDEnt(cc, buildContext), cc, buildContext))
                .collect(Collectors.toMap(c -> c.getId().toString(), c -> c)); // NOSONAR
            var annotations =
                wfm.getWorkflowAnnotations().stream().map(wa -> buildWorkflowAnnotationEnt(wa, buildContext)).toList();
            var metadata = getMetadata(wfm);
            var componentPlaceholders = buildContext.getComponentPlaceholders().stream().toList();
            return builder(WorkflowEntBuilder.class) //
                .setInfo(buildWorkflowInfoEnt(wfm, buildContext))//
                .setNodes(nodes)//
                .setNodeTemplates(invariants)//
                .setConnections(connections)//
                .setWorkflowAnnotations(annotations)//
                .setAllowedActions(
                    buildContext.includeInteractionInfo() ? buildAllowedWorkflowActionsEnt(wfm, buildContext) : null)//
                .setParents(buildParentWorkflowInfoEnts(wfm, buildContext))//
                .setMetaInPorts(buildMetaPortsEntForWorkflow(wfm, true, buildContext))//
                .setMetaOutPorts(buildMetaPortsEntForWorkflow(wfm, false, buildContext))//
                .setMetadata(metadata)//
                .setComponentPlaceholders(componentPlaceholders.isEmpty() ? null : componentPlaceholders)//
                .setDirty(CoreUtil.isWorkflowDirtyOrHasDirtyParent(wfm)).build();
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
    private static Map.Entry<String, PortGroupEntBuilder> addPortRangeToPortGroupEntBuilder(
        final Map.Entry<String, PortGroupEntBuilder> entry, final List<NodePortEnt> portEnts,
        final boolean isInputPort) {
        var id = entry.getKey();
        var builder = entry.getValue();
        var ids = portEnts.stream().map(NodePortEnt::getPortGroupId).toList();
        var minIdx = ids.indexOf(id);
        var maxIdx = ids.lastIndexOf(id);
        if (minIdx > -1 && maxIdx > -1) {
            return Map.entry(id, isInputPort ? builder.setInputRange(List.of(minIdx, maxIdx))
                : builder.setOutputRange(List.of(minIdx, maxIdx)));
        }
        return entry;
    }

    private static AllowedConnectionActionsEnt buildAllowedConnectionActionsEnt(final ConnectionContainer cc,
        final WorkflowBuildContext buildContext) {
        boolean canDelete;
        if (!cc.isDeletable()) {
            canDelete = false;
        } else {
            var wfm = buildContext.wfm();
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

    private static AllowedNodeActionsEnt buildAllowedNodeActionsEnt(final NodeContainer nc,
        final WorkflowBuildContext buildContext) {
        var depNodeProps = buildContext.dependentNodeProperties();
        var parent = nc.getParent();
        var id = nc.getID();
        var hasNodeDialog = NodeDialogManager.hasNodeDialog(nc);
        return builder(AllowedNodeActionsEntBuilder.class)//
            .setCanExecute(depNodeProps.canExecuteNode(id))//
            .setCanReset(depNodeProps.canResetNode(id))//
            .setCanCancel(parent.canCancelNode(id))//
            .setCanOpenLegacyFlowVariableDialog(
                hasNodeDialog && !(nc instanceof SubNodeContainer) ? Boolean.TRUE : null)//
            .setCanOpenView(hasAndCanOpenNodeView(nc))//
            .setCanDelete(canDeleteNode(nc, id, depNodeProps))//
            .setCanCollapse(canCollapseNode(id, buildContext))//
            .setCanExpand(canExpandNode(nc, id, buildContext))//
            .build();
    }

    private static AllowedWorkflowActionsEnt buildAllowedWorkflowActionsEnt(final WorkflowManager wfm,
        final WorkflowBuildContext buildContext) {
        return builder(AllowedWorkflowActionsEntBuilder.class)//
            .setCanReset(buildContext.dependentNodeProperties().canResetAny()) //
            .setCanExecute(wfm.canExecuteAll())//
            .setCanCancel(wfm.canCancelAll())//
            .setCanUndo(buildContext.canUndo())//
            .setCanRedo(buildContext.canRedo()).build();
    }

    private void buildAndAddNodeEnt(final NodeIDEnt id, final NodeContainer nc, final Map<String, NodeEnt> nodes,
        final Map<String, NativeNodeInvariantsEnt> invariants, final WorkflowBuildContext buildContext) {
        var nodeEnt = buildNodeEnt(id, nc, buildContext);
        nodes.put(nodeEnt.getId().toString(), nodeEnt);
        if (nc instanceof NativeNodeContainer nnc) {
            var templateId = ((NativeNodeEnt)nodeEnt).getTemplateId();
            invariants.computeIfAbsent(templateId, tid -> buildOrGetFromCacheNativeNodeInvariantsEnt(templateId, nnc));
        }
    }

    private static List<NodeDialogOptionDescriptionEnt> buildComponentDialogOptionsEnts(final SubNodeContainer snc) {
        var descs = snc.getDialogDescriptions();
        if (!descs.isEmpty()) {
            return descs.stream()
                .map(d -> builder(NodeDialogOptionDescriptionEnt.NodeDialogOptionDescriptionEntBuilder.class)
                    .setName(d.getLabel()) //
                    .setDescription(d.getDescription()) //
                    .build() //
                ).toList();
        } else {
            return null; // NOSONAR
        }
    }

    private List<NodePortDescriptionEnt> buildComponentInNodePortDescriptionEnts(final ComponentMetadata metadata,
        final SubNodeContainer snc) {
        if (snc.getNrInPorts() == 1) {
            return null; // NOSONAR
        }
        List<NodePortDescriptionEnt> res = new ArrayList<>();
        var names = metadata.getInPortNames().orElse(null);
        var descs = metadata.getInPortDescriptions().orElse(null);
        for (var i = 1; i < snc.getNrInPorts(); i++) {
            res.add(buildOrGetFromCacheNodePortDescriptionEnt(snc.getInPort(i).getPortType(),
                names == null ? null : names[i - 1], descs == null ? null : descs[i - 1]));
        }
        return res;
    }

    /**
     * Builds a new {@link ComponentNodeDescriptionEnt} instance.
     *
     * @param snc The sub node container to get the description for
     * @return The newly created entity
     */
    @SuppressWarnings("java:S3516") // "Methods returns should not be invariant" -- false positive
    public ComponentNodeDescriptionEnt buildComponentNodeDescriptionEnt(final SubNodeContainer snc) {
        if (snc == null) {
            return null;
        }
        final var metadata = snc.getMetadata();
        final var description = metadata.getDescription().isEmpty() ? null
            : EntityUtil.toTypedTextEnt(metadata.getDescription().orElse(null), metadata.getContentType());
        final var links = metadata.getLinks().isEmpty() ? null : toLinkEnts(metadata.getLinks());
        final var tags = metadata.getTags().isEmpty() ? null : metadata.getTags();
        return builder(ComponentNodeDescriptionEntBuilder.class)//
            .setName(snc.getName())//
            .setIcon(buildComponentIconEnt(snc))//
            .setType(buildComponentTypeEnt(snc))//
            .setDescription(description)//
            .setLinks(links)//
            .setTags(tags)//
            .setOptions(buildUngroupedDialogOptionGroupEnt(buildComponentDialogOptionsEnts(snc)))//
            .setViews(buildComponentViewDescriptionEnts(snc))//
            .setInPorts(buildComponentInNodePortDescriptionEnts(metadata, snc))//
            .setOutPorts(buildComponentOutNodePortDescriptionEnts(metadata, snc))//
            .setMetadataType(MetadataTypeEnum.COMPONENT)//
            .build();
    }

    static ComponentNodeAndDescriptionEnt.TypeEnum buildComponentTypeEnt(final SubNodeContainer snc) {
        return snc.getMetadata() //
            .getNodeType() //
            .map(t -> ComponentNodeAndDescriptionEnt.TypeEnum.valueOf(t.name())) //
            .orElse(null);
    }

    static String buildComponentIconEnt(final SubNodeContainer snc) {
        return createIconDataURL(snc.getMetadata().getIcon().orElse(null));
    }

    private static ComponentNodeEnt buildComponentNodeEnt(final NodeIDEnt id, final SubNodeContainer snc,
        final AllowedNodeActionsEnt allowedActions, final WorkflowBuildContext buildContext) {
        var metadata = snc.getMetadata();
        var type =
            metadata.getNodeType().map(t -> ComponentNodeAndDescriptionEnt.TypeEnum.valueOf(t.name())).orElse(null);
        var wantContentVersions = buildContext.includeInteractionInfo() && NodeDialogManager.hasNodeDialog(snc);
        var inputContentVersion = wantContentVersions ? ContentVersions.getInputContentVersion(snc) : null;
        var modelSettingsContentVersion =
            wantContentVersions ? ContentVersions.getModelSettingsContentVersion(snc) : null;
        return builder(ComponentNodeEntBuilder.class).setName(snc.getName())//
            .setId(id)//
            .setType(type) //
            .setOutPorts(buildNodePortEnts(snc, false, buildContext))//
            .setAnnotation(buildNodeAnnotationEnt(snc.getNodeAnnotation()))//
            .setInPorts(buildNodePortEnts(snc, true, buildContext))//
            .setPosition(buildXYEnt(snc.getUIInformation()))//
            .setState(buildNodeStateEnt(snc))//
            .setIcon(createIconDataURL(snc.getMetadata().getIcon().orElse(null)))//
            .setKind(KindEnum.COMPONENT)//
            .setLink(buildTemplateLinkEnt(snc, buildContext))//
            .setDialogType(getDialogType(snc)) //
            .setAllowedActions(allowedActions)//
            .setExecutionInfo(buildNodeExecutionInfoEnt(snc)) //
            .setIsLocked(CoreUtil.isLocked(snc).orElse(null)) //
            .setInputContentVersion(inputContentVersion) //
            .setModelSettingsContentVersion(modelSettingsContentVersion) //
            .setHasView(hasNodeView(snc, 0)) //
            .build();
    }

    /**
     * Determines if a component node has a view to show
     *
     * @param snc The component node container for which it should be determined if it has a view to show
     * @param currentDepth current depth of recursion while trying to determine if the component node has a view
     * @return <code>true</code> if the component node has a view, <code>false</code> if the component node has no view
     *         or max recursion depth has been reached.
     */
    private static boolean hasNodeView(final SubNodeContainer snc, final int currentDepth) {
        var maxDepth = 50; // TODO: Is there a more direct way to determine 'hasView', s.t. we can avoid recursion?
        if (currentDepth > maxDepth) {
            LOGGER.info("Could not determine if component node has a view, as max recursion depth has been reached. "
                + "Potential reasons: deeply nested components or cyclical dependency of nested components.");
            return false;
        }
        var childNcs = snc.getWorkflowManager().getNodeContainers();
        return childNcs.stream().anyMatch(childNc -> hasNodeView(childNc, currentDepth));
    }

    /**
     * Determines if a node has a view to show, is called recursively for component nodes
     *
     * @param nc the node container for which it should be determined if it has a view to show
     * @param currentDepth current depth of recursion while trying to determine if component node has a view
     * @return <code>true</code> if the node has a view, <code>false</code> if the node has no view or max recursion
     *         depth has been reached for a component node.
     */
    private static boolean hasNodeView(final NodeContainer nc, final int currentDepth) {
        if (nc instanceof NativeNodeContainer nnc) {
            return NodeViewManager.hasNodeView(nnc) || nnc.getNode().getFactory() instanceof WizardNodeFactoryExtension;
        } else if (nc instanceof WorkflowManager) {
            return false;
        } else if (nc instanceof SubNodeContainer snc) {
            return hasNodeView(snc, currentDepth + 1);
        } else {
            throw new IllegalArgumentException(
                "Could not determine if node container " + nc.getClass().getName() + " has a view.");
        }
    }

    private static DialogTypeEnum getDialogType(final NodeContainer nc) {
        if (NodeDialogManager.hasNodeDialog(nc)) {
            return DialogTypeEnum.WEB;
        } else if (nc.hasDialog()) {
            return DialogTypeEnum.SWING;
        } else {
            return null;
        }
    }

    private static DialogTypeEnum getDialogType(final SubNodeContainer snc) {
        if (SubNodeContainerDialogFactory.isSubNodeContainerNodeDialogEnabled()) {
            return new SubNodeContainerDialogFactory(snc).hasNodeDialog() ? DialogTypeEnum.WEB : null;
        } else {
            return snc.hasDialog() ? DialogTypeEnum.SWING : null;
        }
    }

    private List<NodePortDescriptionEnt> buildComponentOutNodePortDescriptionEnts(final ComponentMetadata metadata,
        final SubNodeContainer snc) {
        if (snc.getNrOutPorts() == 1) {
            return null; // NOSONAR
        }
        List<NodePortDescriptionEnt> res = new ArrayList<>();
        var names = metadata.getOutPortNames().orElse(null);
        var descs = metadata.getOutPortDescriptions().orElse(null);
        for (var i = 1; i < snc.getNrOutPorts(); i++) {
            res.add(buildOrGetFromCacheNodePortDescriptionEnt(snc.getOutPort(i).getPortType(),
                names == null ? null : names[i - 1], descs == null ? null : descs[i - 1]));
        }
        return res;
    }

    private static List<NodeViewDescriptionEnt> buildComponentViewDescriptionEnts(final SubNodeContainer snc) {
        var interactiveWebViews = snc.getInteractiveWebViews();
        List<NodeViewDescriptionEnt> res = new ArrayList<>();
        if (interactiveWebViews.size() > 0) {
            for (var i = 0; i < interactiveWebViews.size(); i++) {
                var siwvr = interactiveWebViews.get(i);
                res.add(builder(NodeViewDescriptionEntBuilder.class).setName(siwvr.getViewName()).build());
            }
            return res;
        } else {
            return null; // NOSONAR
        }
    }

    private static ConnectionEnt buildConnectionEnt(final ConnectionIDEnt id, final ConnectionContainer cc,
        final WorkflowBuildContext buildContext) {
        var builder = builder(ConnectionEntBuilder.class)//
            .setId(id)//
            .setDestNode(id.getDestNodeIDEnt())//
            .setDestPort(cc.getDestPort())//
            .setSourceNode(buildContext.buildNodeIDEnt(cc.getSource())).setSourcePort(cc.getSourcePort())//
            .setFlowVariableConnection(cc.isFlowVariablePortConnection() ? cc.isFlowVariablePortConnection() : null)//
            .setBendpoints(buildBendpoints(cc));
        if (buildContext.isInStreamingMode()) {
            cc.getConnectionProgress().ifPresent(connectionProgress -> builder.setLabel(connectionProgress.getMessage())
                .setStreaming(connectionProgress.inProgress()));
        }
        if (buildContext.includeInteractionInfo()) {
            builder.setAllowedActions(buildAllowedConnectionActionsEnt(cc, buildContext));
        }
        return builder.build();
    }

    private static List<XYEnt> buildBendpoints(final ConnectionContainer cc) {
        if (cc.getUIInfo() != null) {
            var allBendpoints = cc.getUIInfo().getAllBendpoints();
            if (allBendpoints.length == 0) {
                return null; // NOSONAR
            }
            return Arrays.stream(allBendpoints).map(a -> builder(XYEntBuilder.class).setX(a[0]).setY(a[1]).build())
                .toList();
        } else {
            return null; // NOSONAR
        }
    }

    private static ConnectionIDEnt buildConnectionIDEnt(final ConnectionContainer c,
        final WorkflowBuildContext buildContext) {
        return new ConnectionIDEnt(buildContext.buildNodeIDEnt(c.getDest()), c.getDestPort());
    }

    private static CustomJobManagerEnt buildCustomJobManagerEnt(final NodeExecutionJobManager jobManager) {
        var factory = NodeExecutionJobManagerPool.getJobManagerFactory(jobManager.getID());
        var name = factory == null ? jobManager.getID() : factory.getLabel();
        String iconForWorkflow = null;
        var icon = createIconDataURL(jobManager.getIcon());
        if (jobManager instanceof AbstractNodeExecutionJobManager nodeExecJobManager) {
            iconForWorkflow = createIconDataURL(nodeExecJobManager.getIconForWorkflow());
        }
        return builder(CustomJobManagerEntBuilder.class).setName(name).setIcon(icon).setWorkflowIcon(iconForWorkflow)
            .build();
    }

    private static JobManagerEnt buildJobManagerEnt(final NodeExecutionJobManager jobManager) {
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

    private static LoopInfoEnt buildLoopInfoEnt(final NativeNodeContainer nc, final WorkflowBuildContext buildContext) {
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

    private static MetaNodeEnt buildMetaNodeEnt(final NodeIDEnt id, final WorkflowManager wm,
        final AllowedNodeActionsEnt allowedActions, final WorkflowBuildContext buildContext) {
        return builder(MetaNodeEntBuilder.class).setName(wm.getName()).setId(id)//
            .setOutPorts(buildMetaNodePortEnts(wm, false, buildContext))//
            .setAnnotation(buildNodeAnnotationEnt(wm.getNodeAnnotation()))//
            .setInPorts(buildMetaNodePortEnts(wm, true, buildContext))//
            .setPosition(buildXYEnt(wm.getUIInformation()))//
            .setState(buildMetaNodeStateEnt(wm.getNodeContainerState()))//
            .setKind(KindEnum.METANODE)//
            .setLink(buildTemplateLinkEnt(wm, buildContext))//
            .setAllowedActions(allowedActions)//
            .setExecutionInfo(buildNodeExecutionInfoEnt(wm))//
            .setIsLocked(CoreUtil.isLocked(wm).orElse(null))//
            .setDialogType(null)//
            .setHasView(false)//
            .build();
    }

    private static List<MetaNodePortEnt> buildMetaNodePortEnts(final WorkflowManager wm, final boolean inPorts,
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
                .setPortContentVersion(np.getPortContentVersion())//
                .setNodeState(nodeState)//
                .setCanRemove(np.isCanRemove())//
                .build();
        }).toList();
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

    private static MetaPortsEnt buildMetaPortsEntForWorkflow(final WorkflowManager wfm, final boolean incoming,
        final WorkflowBuildContext buildContext) {
        if (wfm.isProject() || wfm.getDirectNCParent() instanceof SubNodeContainer) {
            // no meta ports for workflow projects and component workflows
            return null;
        }
        var ports = buildNodePortEntsForWorkflow(wfm, incoming, buildContext);
        var builder = builder(MetaPortsEntBuilder.class);
        builder.setPorts(ports);

        var barUIInfo = incoming ? wfm.getInPortsBarUIInfo() : wfm.getOutPortsBarUIInfo();
        if (barUIInfo != null) {
            var bounds = barUIInfo.getBounds();
            builder.setBounds(builder(BoundsEntBuilder.class).setX(bounds[0]).setY(bounds[1]).setWidth(bounds[2])
                .setHeight(bounds[3]).build());
        }
        return builder.build();
    }

    /**
     * TODO: NXT-1189 Gateway API: Add `ExchangeablePortGroup` port types to `buildNativeNodeEnt()` method
     */
    private static NativeNodeEnt buildNativeNodeEnt(final NodeIDEnt id, final NativeNodeContainer nnc,
        final AllowedNodeActionsEnt allowedActions, final WorkflowBuildContext buildContext) {
        var inPorts = buildNodePortEnts(nnc, true, buildContext);
        var outPorts = buildNodePortEnts(nnc, false, buildContext);
        var portGroups = buildPortGroupEntsMapOptional(nnc, inPorts, outPorts, buildContext).orElse(null);
        var hasView = NodeViewManager.hasNodeView(nnc);
        Boolean isReexecutable = null;
        if (nnc.getNodeModel() instanceof ReExecutable) {
            isReexecutable = ((ReExecutable<?>)nnc.getNodeModel()).canTriggerReExecution();
        }
        var wantContentVersions = buildContext.includeInteractionInfo() && NodeDialogManager.hasNodeDialog(nnc);
        var inputContentVersion = wantContentVersions ? ContentVersions.getInputContentVersion(nnc) : null;
        var modelSettingsContentVersion =
            wantContentVersions ? ContentVersions.getModelSettingsContentVersion(nnc) : null;
        return builder(NativeNodeEntBuilder.class)//
            .setId(id)//
            .setKind(KindEnum.NODE)//
            .setInPorts(inPorts)//
            .setOutPorts(outPorts)//
            .setPortGroups(portGroups)//
            .setAnnotation(buildNodeAnnotationEnt(nnc.getNodeAnnotation()))//
            .setPosition(buildXYEnt(nnc.getUIInformation()))//
            .setState(buildNodeStateEnt(nnc))//
            .setTemplateId(nnc.getNode().getFactory().getFactoryId())//
            .setAllowedActions(allowedActions)//
            .setExecutionInfo(buildNodeExecutionInfoEnt(nnc))//
            .setLoopInfo(buildLoopInfoEnt(nnc, buildContext))//
            .setDialogType(getDialogType(nnc)) //
            .setHasView(hasView)//
            .setIsReexecutable(isReexecutable)//
            .setInputContentVersion(inputContentVersion) //
            .setModelSettingsContentVersion(modelSettingsContentVersion) //
            .build();
    }

    static NativeNodeInvariantsEnt buildNativeNodeInvariantsEnt(final NativeNodeContainer nnc) {
        var builder = builder(NativeNodeInvariantsEntBuilder.class);
        NodeFactory<? extends NodeModel> factory = nnc.getNode().getFactory();
        if (nnc.getType() == NodeType.Missing) {
            var nodeInfo = ((MissingNodeFactory)factory).getNodeAndBundleInfo();
            builder.setName(nodeInfo.getNodeName().orElse("Unknown Name (MISSING)")) //
                .setType(getMissingNodeType(nnc));
        } else {
            builder.setName(nnc.getName()) //
                .setType(TypeEnum.valueOf(nnc.getType().toString().toUpperCase(Locale.ROOT))) //
                .setNodeFactory(buildNodeFactoryKeyEnt(NodeSpec.Factory.of(factory))) //
                .setIcon(createIconDataURL(factory));
        }
        return builder.build();
    }

    private static TypeEnum getMissingNodeType(final NativeNodeContainer nnc) {
        NodeFactory<? extends NodeModel> factory = nnc.getNode().getFactory();
        if (!(factory instanceof MissingNodeFactory missingNodeFactory)) {
            return null;
        }
        return switch (missingNodeFactory.getReason()) {
            case GOVERNANCE_FORBIDDEN -> TypeEnum.FORBIDDEN;
            case MISSING_EXTENSION -> TypeEnum.MISSING;
        };
    }

    private static NodeAnnotationEnt buildNodeAnnotationEnt(final NodeAnnotation na) {
        if (na.getData().isDefault()) {
            return null;
        }
        var text = EntityUtil.toTypedTextEnt(na.getText(), na.getContentType());
        var textAlignSupplier = getTextAlignSupplier(na.getAlignment());
        var styleRangesSupplier = getStyleRangesSupplier(na.getStyleRanges());
        var bgColor = na.getBgColor();
        return builder(NodeAnnotationEntBuilder.class)//
            .setText(text)//
            .setTextAlign(text.getContentType() == ContentTypeEnum.PLAIN ? textAlignSupplier.get() : null)//
            .setStyleRanges(text.getContentType() == ContentTypeEnum.PLAIN ? styleRangesSupplier.get() : null)//
            .setBackgroundColor(bgColor == DEFAULT_NODE_ANNOTATION_BG_COLOR ? null : hexStringColor(bgColor))//
            .setDefaultFontSize(na.getDefaultFontSize() > 0 ? na.getDefaultFontSize() : null)//
            .build();
    }

    private static Supplier<TextAlignEnum> getTextAlignSupplier(final TextAlignment alignment) {
        return () -> switch (alignment) {
            case LEFT -> TextAlignEnum.LEFT;
            case CENTER -> TextAlignEnum.CENTER;
            case RIGHT -> TextAlignEnum.RIGHT;
        };
    }

    private static Supplier<List<StyleRangeEnt>> getStyleRangesSupplier(final StyleRange[] styleRanges) {
        return () -> Arrays.stream(styleRanges)//
            .map(WorkflowEntityFactory::buildStyleRangeEnt)//
            .toList();
    }

    private NodeEnt buildNodeEnt(final NodeIDEnt id, final NodeContainer nc, final AllowedNodeActionsEnt allowedActions,
        final WorkflowBuildContext buildContext) {
        if (nc instanceof NativeNodeContainer nnc) {
            return buildNativeNodeEnt(id, nnc, allowedActions, buildContext);
        } else if (nc instanceof WorkflowManager wfm) {
            return buildMetaNodeEnt(id, wfm, allowedActions, buildContext);
        } else if (nc instanceof SubNodeContainer snc) {
            return buildComponentNodeEnt(id, snc, allowedActions, buildContext);
        } else {
            throw new IllegalArgumentException(
                "Node container " + nc.getClass().getName() + " cannot be mapped to a node entity.");
        }
    }

    private NodeEnt buildNodeEnt(final NodeIDEnt id, final NodeContainer nc, final WorkflowBuildContext buildContext) {
        var allowedActions =
            buildContext.includeInteractionInfo() ? buildAllowedNodeActionsEnt(nc, buildContext) : null;
        return buildNodeEnt(id, nc, allowedActions, buildContext);
    }

    private static NodeExecutionInfoEnt buildNodeExecutionInfoEnt(final NodeContainer nc) {
        var jobManagerEnt = buildJobManagerEnt(nc.getJobManager());
        if (jobManagerEnt != null) {
            return builder(NodeExecutionInfoEntBuilder.class).setJobManager(jobManagerEnt).build();
        } else {
            var parentJobManager = nc.getParent().findJobManager();
            if (!CoreUtil.isDefaultOrNullJobManager(parentJobManager)) {
                return buildNodeExecutionInfoEntFromParentJobManager(parentJobManager, nc);
            }
        }
        return null;
    }

    private static NodeExecutionInfoEnt buildNodeExecutionInfoEntFromParentJobManager(
        final NodeExecutionJobManager parentJobManager, final NodeContainer nc) {
        if (CoreUtil.isStreamingJobManager(parentJobManager)) {
            if (nc instanceof NativeNodeContainer nnc) {
                return builder(NodeExecutionInfoEntBuilder.class).setStreamable(isStreamable(nnc)).build();
            } else {
                return null;
            }
        } else if (parentJobManager instanceof AbstractNodeExecutionJobManager aneJobManager) {
            return builder(NodeExecutionInfoEntBuilder.class)
                .setIcon(createIconDataURL(aneJobManager.getIconForChild(nc))).build();
        } else {
            return null;
        }
    }

    static NodeFactoryKeyEnt buildNodeFactoryKeyEnt(final NodeSpec.Factory factorySpec) {
        var nodeFactoryKeyBuilder = builder(NodeFactoryKeyEntBuilder.class);
        if (factorySpec != null) {
            nodeFactoryKeyBuilder.setClassName(factorySpec.className());
            // only set settings in case of a dynamic node factory
            if (factorySpec.factorySettings() != null) {
                nodeFactoryKeyBuilder
                    .setSettings(JSONConfig.toJSONString(factorySpec.factorySettings(), WriterConfig.DEFAULT));
            }
        } else {
            nodeFactoryKeyBuilder.setClassName("");
        }
        return nodeFactoryKeyBuilder.build();
    }

    private static NodePortDescriptionEntBuilder buildNodePortDescriptionEntBuilder(final PortType ptype) {
        return builder(NodePortDescriptionEntBuilder.class)//
            .setTypeId(CoreUtil.getPortTypeId(ptype))//
            .setTypeName(ptype.getName())//
            .setOptional(ptype.isOptional());
    }

    @SuppressWarnings("java:S107") // it's a 'builder'-method, so many parameters are ok
    private static NodePortEnt buildNodePortEnt(final PortType ptype, final String name, final String info,
        final int portIdx, final Boolean isOptional, final Boolean isInactive, final Boolean canRemovePort,
        final Boolean isReportPort, final Collection<ConnectionContainer> connections, final Integer portContentVersion,
        final String portGroupId, final WorkflowBuildContext buildContext) {
        return builder(NodePortEntBuilder.class) //
            .setIndex(portIdx)//
            .setOptional(isOptional)//
            .setInactive(isInactive)//
            .setConnectedVia(connections.stream().map(cc -> buildConnectionIDEnt(cc, buildContext)).toList())//
            .setName(name)//
            .setInfo(info)//
            .setTypeId(CoreUtil.getPortTypeId(ptype))//
            .setPortContentVersion(portContentVersion)//
            .setPortGroupId(portGroupId)//
            .setCanRemove(canRemovePort)//
            .setIsComponentReportPort(isReportPort)//
            .build();
    }

    private static List<NodePortEnt> buildNodePortEnts(final NodeContainer nc, final boolean isInputPorts,
        final WorkflowBuildContext buildContext) {
        List<NodePortEnt> res = new ArrayList<>();
        if (isInputPorts) {
            for (var i = 0; i < nc.getNrInPorts(); i++) {
                var canRemovePort = canRemovePort(nc, i, isInputPorts, buildContext);
                var connection = nc.getParent().getIncomingConnectionFor(nc.getID(), i);
                List<ConnectionContainer> connections =
                    connection == null ? emptyList() : Collections.singletonList(connection);
                var inPort = nc.getInPort(i);
                var portGroupId = getPortGroupNameForDynamicNativeNodePort(nc, i, true, buildContext);
                var pt = inPort.getPortType();
                final var isReportPort = isComponentReportPort(nc, i, isInputPorts);
                res.add(buildNodePortEnt(pt, inPort.getPortName(), null, i, pt.isOptional(), null, canRemovePort,
                    isReportPort, connections, null, portGroupId, buildContext));
            }
        } else {
            for (var i = 0; i < nc.getNrOutPorts(); i++) {
                var canRemovePort = canRemovePort(nc, i, isInputPorts, buildContext);
                var connections = nc.getParent().getOutgoingConnectionsFor(nc.getID(), i);
                var outPort = nc.getOutPort(i);
                var portGroupId = getPortGroupNameForDynamicNativeNodePort(nc, i, false, buildContext);
                var pt = outPort.getPortType();
                final var isReportPort = isComponentReportPort(nc, i, isInputPorts);
                var portContentVersion = ContentVersions.getPortContentVersion(outPort);
                res.add(buildNodePortEnt(pt, outPort.getPortName(), outPort.getPortSummary(), i, null,
                    outPort.isInactive() ? outPort.isInactive() : null, canRemovePort, isReportPort, connections,
                    portContentVersion, portGroupId, buildContext));
            }
        }
        return res;
    }

    private static List<NodePortEnt> buildNodePortEntsForWorkflow(final WorkflowManager wfm, final boolean incoming,
        final WorkflowBuildContext buildContext) {
        List<NodePortEnt> ports = new ArrayList<>();
        if (incoming) {
            var nrPorts = wfm.getNrWorkflowIncomingPorts();
            for (var i = 0; i < nrPorts; i++) {
                var canRemovePort = canRemoveContainerNodePort(wfm, i, incoming);
                var connections = wfm.getOutgoingConnectionsFor(wfm.getID(), i);
                var port = wfm.getWorkflowIncomingPort(i);
                var isInactive = port.isInactive() ? Boolean.TRUE : null;
                var portContentVersion = ContentVersions.getPortContentVersion(port);
                ports.add(buildNodePortEnt(port.getPortType(), port.getPortName(), port.getPortSummary(), i, null,
                    isInactive, canRemovePort, null, connections, portContentVersion, null, buildContext));
            }
        } else {
            var nrPorts = wfm.getNrWorkflowOutgoingPorts();
            for (var i = 0; i < nrPorts; i++) {
                var canRemovePort = canRemoveContainerNodePort(wfm, i, incoming);
                var connection = wfm.getIncomingConnectionFor(wfm.getID(), i);
                Collection<ConnectionContainer> connections = connection != null ? singleton(connection) : emptyList();
                var port = wfm.getWorkflowOutgoingPort(i);
                ports.add(buildNodePortEnt(port.getPortType(), port.getPortName(), null, i, null, null, canRemovePort,
                    null, connections, null, null, buildContext));
            }
        }
        return ports;
    }

    private static NodeStateEnt buildNodeStateEnt(final SingleNodeContainer nc) {
        var ncState = nc.getNodeContainerState();
        var builder = builder(NodeStateEntBuilder.class);
        if (!nc.isInactive()) {
            builder.setExecutionState(getNodeExecutionStateEnum(ncState));
        }
        var nodeMessage = nc.getNodeMessage();
        if (nodeMessage.getMessageType() == Type.ERROR) {
            builder.setError(nodeMessage.getMessage());
        } else if (nodeMessage.getMessageType() == Type.WARNING) {
            builder.setWarning(nodeMessage.getMessage());
        }

        if (nodeMessage.getMessageType() != Type.RESET) {
            builder.setIssue(nodeMessage.getIssue().orElse(null));
            builder.setResolutions(nodeMessage.getResolutions());
        }

        if (ncState.isExecutionInProgress()) {
            var progressMonitor = nc.getProgressMonitor();
            var progress = progressMonitor.getProgress();
            builder.setProgress(progress == null ? null : BigDecimal.valueOf(progress));
            final var messages = progressMonitor.getMessages();
            if (!messages.isEmpty()) {
                builder.setProgressMessages(messages);
            }
        }

        var state = builder.build();
        return emptyState.equals(state) ? null : state;
    }

    private static final NodeStateEnt emptyState = builder(NodeStateEntBuilder.class).build();

    private NativeNodeInvariantsEnt buildOrGetFromCacheNativeNodeInvariantsEnt(final String templateId,
        final NativeNodeContainer nc) {
        return m_nativeNodeInvariantsCache.computeIfAbsent(templateId, id -> buildNativeNodeInvariantsEnt(nc));
    }

    private NodePortDescriptionEnt buildOrGetFromCacheNodePortDescriptionEnt(final PortType ptype, final String name,
        final String description) {
        var builder = m_nodePortBuilderCache.computeIfAbsent(ptype.getPortObjectClass().getName(),
            k -> buildNodePortDescriptionEntBuilder(ptype));
        builder.setName(isBlank(name) ? null : name);
        builder.setDescription(isBlank(description) ? null : description);
        return builder.build();
    }

    private static List<WorkflowInfoEnt> buildParentWorkflowInfoEnts(final WorkflowManager wfm,
        final WorkflowBuildContext buildContext) {
        if (wfm.isProject() || wfm.isComponentProjectWFM()) {
            return null; // NOSONAR
        }
        List<WorkflowInfoEnt> parents = new ArrayList<>();
        var parent = wfm;
        do {
            parent = CoreUtil.getWorkflowParent(parent);
            parents.add(buildWorkflowInfoEnt(parent, buildContext));
        } while (!parent.isProject() && !parent.isComponentProjectWFM());
        Collections.reverse(parents);
        return parents;
    }

    private static Map<String, PortGroupEnt> buildPortGroupEntsMap(final Map<String, ExtendablePortGroup> portGroups,
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
    private static Optional<Map<String, PortGroupEnt>> buildPortGroupEntsMapOptional(final NativeNodeContainer nnc,
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

    private static ProjectMetadataEnt buildProjectMetadataEnt(final WorkflowManager wfm) {
        assert wfm.isProject();
        final var metadata = wfm.getMetadata();
        final var description =
            EntityUtil.toTypedTextEnt(metadata.getDescription().orElse(""), metadata.getContentType());
        final var links = EntityUtil.toLinkEnts(metadata.getLinks());
        return builder(ProjectMetadataEntBuilder.class)//
            .setDescription(description)//
            .setLinks(links)//
            .setTags(metadata.getTags())//
            .setLastEdit(metadata.getLastModified().toOffsetDateTime())//
            .setMetadataType(MetadataTypeEnum.PROJECT)//
            .build();
    }

    private static StyleRangeEnt buildStyleRangeEnt(final StyleRange sr) {
        var builder = builder(StyleRangeEntBuilder.class).setFontSize(sr.getFontSize())//
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

    private static List<NodeDialogOptionGroupEnt>
        buildUngroupedDialogOptionGroupEnt(final List<NodeDialogOptionDescriptionEnt> ungroupedOptionEnts) {
        if (Objects.isNull(ungroupedOptionEnts) || ungroupedOptionEnts.isEmpty()) {
            return null; // NOSONAR: returning null is useful here
        }
        return Collections.singletonList(builder(NodeDialogOptionGroupEnt.NodeDialogOptionGroupEntBuilder.class) //
            .setSectionName(null) //
            .setSectionDescription(null) //
            .setFields(ungroupedOptionEnts) //
            .build());
    }

    private static WorkflowAnnotationEnt buildWorkflowAnnotationEnt(final WorkflowAnnotation wa,
        final WorkflowBuildContext buildContext) {
        var bounds = builder(BoundsEntBuilder.class).setX(wa.getX())//
            .setY(wa.getY())//
            .setWidth(wa.getWidth())//
            .setHeight(wa.getHeight())//
            .build();
        final var text = EntityUtil.toTypedTextEnt(wa.getText(), wa.getContentType());
        final var textAlignSupplier = getTextAlignSupplier(wa.getAlignment());
        final var styleRangesSupplier = getStyleRangesSupplier(wa.getStyleRanges());
        return builder(WorkflowAnnotationEntBuilder.class) //
            .setId(buildContext.buildAnnotationIDEnt(wa.getID()))//
            .setText(text)//
            .setTextAlign(text.getContentType() == ContentTypeEnum.PLAIN ? textAlignSupplier.get() : null)//
            .setStyleRanges(text.getContentType() == ContentTypeEnum.PLAIN ? styleRangesSupplier.get() : null)//
            .setBackgroundColor(hexStringColor(wa.getBgColor()))//
            .setBorderColor(hexStringColor(wa.getBorderColor()))//
            .setBorderWidth(wa.getBorderSize())//
            .setBounds(bounds)//
            .setDefaultFontSize(wa.getDefaultFontSize() > 0 ? wa.getDefaultFontSize() : null)//
            .build();
    }

    /**
     * Builds an {@code AnnotationIDEnt} considering the {@code WorkflowBuildContext}.
     *
     * @param wa -
     * @param buildContextBuilder -
     * @param wfm -
     * @return The new add annotation ID entity
     */
    public static AnnotationIDEnt buildAnnotationIDEnt(final WorkflowAnnotation wa,
        final WorkflowBuildContextBuilder buildContextBuilder, final WorkflowManager wfm) {
        try (var lock = wfm.lock()) {
            var buildContext = buildContextBuilder.build(wfm);
            return buildContext.buildAnnotationIDEnt(wa.getID());
        }
    }

    private static WorkflowInfoEnt buildWorkflowInfoEnt(final WorkflowManager wfm,
        final WorkflowBuildContext buildContext) {
        final var template = wfm.getDirectNCParent() instanceof SubNodeContainer//
            ? (NodeContainerTemplate)wfm.getDirectNCParent()//
            : wfm;
        final var locationType = Optional.ofNullable(wfm.getContextV2())//
            .map(WorkflowContextV2::getLocationType)//
            .orElse(LocationType.LOCAL);
        final var version = Optional.ofNullable(buildContext.getVersion()) //
            .filter(v -> !VersionId.currentState().equals(v)) //
            .map(VersionId::toString) //
            .orElse(null);
        return builder(WorkflowInfoEntBuilder.class)//
            .setName(wfm.getName())//
            .setContainerId(getContainerId(wfm, buildContext))//
            .setContainerType(getContainerType(wfm))//
            .setLinked(getTemplateLink(template) != null ? Boolean.TRUE : null)//
            .setContainsLinkedComponents(getContainsLinkedComponents(wfm)) //
            .setProviderType(switch (locationType) {
                case LOCAL -> ProviderTypeEnum.LOCAL;
                case HUB_SPACE -> ProviderTypeEnum.HUB;
                case SERVER_REPOSITORY -> ProviderTypeEnum.SERVER;
            })//
            .setJobManager(buildJobManagerEnt(wfm.findJobManager())) //
            .setVersion(version) //
            .build();
    }

    private static Boolean getContainsLinkedComponents(final WorkflowManager wfm) {
        final var linkedComponents = CoreUtil.getLinkedComponentToStateMap(wfm);
        return linkedComponents.isEmpty() ? null : true;
    }

    private static XYEnt buildXYEnt(final NodeUIInformation uiInfo) {
        if (uiInfo == null) {
            // This can happen when, e.g., components are added. In that case the component is added to the workflow
            // first without any uiInfo (which already triggers workflow changed event). And then the uiInfo is set
            // via `NodeContainer.setUIInformation` (which triggers another workflow changed event).
            return builder(XYEntBuilder.class).setX(0).setY(0).build();
        }
        var bounds = uiInfo.getBounds();
        return buildXYEnt(bounds[0], bounds[1] + NODE_Y_POS_CORRECTION);
    }

    private static XYEnt buildXYEnt(final int x, final int y) {
        return builder(XYEntBuilder.class).setX(x).setY(y).build();
    }

    private static AllowedNodeActionsEnt.CanCollapseEnum canCollapseNode(final NodeID id,
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

    /**
     * Builds a new {@link NodeIdAndIsExecutedEnt} instance.
     *
     * @param nodeId The node ID to build the entity for
     * @param ncState The node container state
     * @return Combination of node ID and execution state
     */
    public NodeIdAndIsExecutedEnt buildNodeIdAndIsExecutedEnt(final NodeID nodeId, final NodeContainerState ncState) {
        final var ncStateEnum = getNodeExecutionStateEnum(ncState);
        final var isExecuted =
            ncStateEnum == ExecutionStateEnum.EXECUTED || ncStateEnum == ExecutionStateEnum.EXECUTING;
        return builder(NodeIdAndIsExecutedEntBuilder.class)//
            .setId(new NodeIDEnt(nodeId))//
            .setIsExecuted(isExecuted)//
            .build();
    }

    private static Boolean canDeleteNode(final NodeContainer nc, final NodeID nodeId,
        final DependentNodeProperties depNodeProps) {
        if (!nc.isDeletable()) {
            return Boolean.FALSE;
        } else {
            return isNodeResetOrCanBeReset(nc.getNodeContainerState(), nodeId, depNodeProps);
        }
    }

    private static AllowedNodeActionsEnt.CanExpandEnum canExpandNode(final NodeContainer nc, final NodeID id,
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

    private static boolean canExpandNodeContainer(final NodeID id, final NodeContainer nc, final WorkflowManager wfm) {
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
    private static boolean canRemoveContainerNodePort(final NodeContainer nc, final int portIndex,
        final boolean isInputPort) {
        var isContainerNode = (nc instanceof SubNodeContainer) || (nc instanceof WorkflowManager);
        if (!isContainerNode) {
            throw new IllegalArgumentException("Not a container node");
        }
        // First input/output port of component nodes is always a fixed flow variable port - last port might be report
        if (nc instanceof SubNodeContainer snc && (portIndex == 0 || snc.isReportPort(portIndex, isInputPort))) {
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
    private static boolean canRemoveNativeNodePort(final NativeNodeContainer nnc, final int portIndex,
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
                var portIndexWithinGroup = CoreUtil.getPortIndexWithinGroup(portIndexToPortGroupMap, portIndex);
                return hasPortsAddedToPortGroup(nnc, buildContext, portGroupName)
                    && !isFixedPort(nnc, buildContext, portGroupName, portIndexWithinGroup);
            })//
            // False in all other cases
            .orElse(false);
    }

    private static boolean isFixedPort(final NativeNodeContainer nnc, final WorkflowBuildContext buildContext,
        final String portGroupName, final int portIndex) {
        return buildContext.getPortsConfiguration(nnc)//
            // Map to `ExtendablePortGroup` if there is any
            .map(portsConfig -> portsConfig.getExtendablePorts().get(portGroupName))//
            //Get the number of fixed ports
            .map(portGroup -> portGroup.getFixedPorts().length)
            //Check if port index is within range of fixed ports
            .map(fixedPortLen -> fixedPortLen > portIndex).orElse(false);
    }

    private static boolean hasPortsAddedToPortGroup(final NativeNodeContainer nnc,
        final WorkflowBuildContext buildContext, final String portGroupName) {
        return buildContext.getPortsConfiguration(nnc)//
            // Map to `ExtendablePortGroup` if there is any
            .map(portsConfig -> portsConfig.getExtendablePorts().get(portGroupName))//
            // Check if port group has at least one added port
            .map(ExtendablePortGroup::hasConfiguredPorts)//
            // False otherwise
            .orElse(false);
    }

    private static Boolean canRemovePort(final NodeContainer nc, final int portIndex, final boolean isInputPort,
        final WorkflowBuildContext buildContext) {
        if (!buildContext.includeInteractionInfo()) {
            return null; // NOSONAR
        }
        if (nc instanceof NativeNodeContainer nnc) {
            return canRemoveNativeNodePort(nnc, portIndex, isInputPort, buildContext);
        } else {
            return canRemoveContainerNodePort(nc, portIndex, isInputPort);
        }
    }

    private static Boolean isComponentReportPort(final NodeContainer nc, final int portIndex,
        final boolean isInputPort) {
        return nc instanceof SubNodeContainer snc && snc.isReportPort(portIndex, isInputPort) ? Boolean.TRUE : null;
    }

    /**
     * Decode bytes from a given String
     *
     * @param dataUrl A string containing a PNG encoded in a base64-data-url
     * @return The decoded bytes
     */
    public static byte[] decodeIconDataURL(final String dataUrl) {
        var withoutPrefix = StringUtils.removeStart(dataUrl, ICON_DATA_URL_PREFIX);
        return Base64.decodeBase64(withoutPrefix.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Encode bytes into a data-url-string
     *
     * @param iconData The data to encode
     * @return The encoded string
     */
    public static String createIconDataURL(final byte[] iconData) {
        if (iconData != null) {
            return ICON_DATA_URL_PREFIX + new String(Base64.encodeBase64(iconData), StandardCharsets.UTF_8);
        } else {
            return null;
        }
    }

    static String createIconDataURL(final NodeFactory<?> nodeFactory) {
        return createIconDataURL(nodeFactory.getIcon());
    }

    static String createIconDataURL(final URL url) {
        if (url == null) {
            return null;
        }
        ensureFileOrBundleURL(url);
        try (var in = url.openStream()) {
            if (in == null) {
                throw new IOException("Could not open stream for URL: " + url);
            }
            return createIconDataURL(IOUtils.toByteArray(in));
        } catch (IOException ex) {
            NodeLogger.getLogger(WorkflowEntityFactory.class).error("Icon for node couldn't be read", ex);
            return null;
        }
    }

    /*
     * Makes sure that the provided url points to a file or bundle. Otherwise an exception is thrown.
     */
    private static void ensureFileOrBundleURL(final URL url) {
        var protocol = url.getProtocol();
        if (protocol == null || (!protocol.equals("file") && !protocol.equals("bundleresource"))) {
            throw new IllegalStateException("An icon URL doesn't reference a file. Icon URL: " + url);
        }
    }

    private static NodeIDEnt getContainerId(final WorkflowManager wfm, final WorkflowBuildContext buildContext) {
        if (!wfm.isProject()) {
            var ncParent = wfm.getDirectNCParent();
            if (ncParent instanceof SubNodeContainer snc) {
                // it's a component's workflow
                return buildContext.buildNodeIDEnt(snc.getID());
            }
        }
        // it's a project's or a metanode's workflow
        return buildContext.buildNodeIDEnt(wfm.getID());
    }

    private static MetaPortInfo[] getContainerMetaPortInfo(final NodeContainer nc, final boolean inPorts) {
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

    private static ContainerTypeEnum getContainerType(final WorkflowManager wfm) {
        if (wfm.isProject() || wfm.isComponentProjectWFM()) {
            return ContainerTypeEnum.PROJECT;
        }
        var parent = wfm.getDirectNCParent();
        if (parent instanceof SubNodeContainer) {
            return ContainerTypeEnum.COMPONENT;
        } else if (parent instanceof WorkflowManager) {
            return ContainerTypeEnum.METANODE;
        } else {
            throw new IllegalStateException();
        }
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

    private static SubNodeContainer getParentComponent(final WorkflowManager wfm) {
        return wfm.getDirectNCParent() instanceof SubNodeContainer snc ? snc : null;
    }

    /**
     * TODO: NXT-1189 Gateway API: Add `ExchangeablePortGroup` port types to `buildNativeNodeEnt()` method
     */
    private static String getPortGroupNameForDynamicNativeNodePort(final NodeContainer nc, final int portIndex,
        final boolean isInputPort, final WorkflowBuildContext buildContext) {
        if (nc instanceof NativeNodeContainer nnc) { // We are only interested in native nodes
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

    private static String getTemplateLink(final NodeContainerTemplate nct) {
        if (nct instanceof SubNodeContainer snc && snc.isProject()) {
            return null;
        }
        var sourceURI = nct.getTemplateInformation().getSourceURI();
        return sourceURI == null ? null : sourceURI.toString();
    }

    private static TemplateLinkEnt buildTemplateLinkEnt(final NodeContainerTemplate nct,
        final WorkflowBuildContext buildContext) {
        final var templateInfo = nct.getTemplateInformation();
        if (templateInfo.getRole() != Role.Link) { // Only works for linked components and metanodes
            return null;
        }

        var updateStatus = switch (templateInfo.getUpdateStatus()) {
            case UpToDate -> UpdateStatusEnum.UP_TO_DATE;
            case HasUpdate -> UpdateStatusEnum.HAS_UPDATE;
            case Error -> UpdateStatusEnum.ERROR;
        };

        final var linkUri = templateInfo.getSourceURI();

        return builder(TemplateLinkEntBuilder.class) //
            .setUrl(getTemplateLink(nct))//
            .setUpdateStatus(updateStatus) //
            .setIsLinkVariantChangeable( //
                KnimeUrls.isLinkTypeChangeable( //
                    linkUri, //
                    CoreUtil.getProjectWorkflow(nct.getParent()).getContextV2(), //
                    buildContext::getSpaceProviderType) //
            ) //
            .setIsHubItemVersionChangeable(isHubItemVersionChangeable(linkUri, buildContext)) //
            .setCurrentLinkVariant(KnimeUrls.getLinkVariant(linkUri)) //
            .build();
    }

    /**
     * The version of a KNIME URL can be changed if it is an absolute URL to a Hub repository item.
     *
     * @param uri KNIME URL to check
     * @param buildContext build context to determine whether the URL points to a Hub
     * @return {@code true} if changing Hub versions is possible, {@code false} otherwise
     */
    private static boolean isHubItemVersionChangeable(final URI uri, final WorkflowBuildContext buildContext) {
        return KnimeUrlType.getType(uri).orElse(null) == KnimeUrlType.MOUNTPOINT_ABSOLUTE
            && buildContext.getSpaceProviderType(uri.getAuthority()).orElse(null) == SpaceProviderEnt.TypeEnum.HUB;
    }

    /**
     * Returns null if the node has no node view; false, if there is a node view but there is nothing to display, true,
     * if there is a node view which also has something to display.
     * <p>
     * See org.knime.ui.java.api.NodeAPI#executeNodeAndOpenView
     */
    private static Boolean hasAndCanOpenNodeView(final NodeContainer nc) {
        var hasNodeView = NodeViewManager.hasNodeView(nc);
        var hasCompositeView =
            nc instanceof SubNodeContainer && WizardPageUtil.isWizardPage(nc.getParent(), nc.getID());
        var hasLegacyJSNodeView = nc instanceof NativeNodeContainer && nc.getInteractiveWebViews().size() > 0;
        var hasSwingNodeView = nc.getNrNodeViews() > 0;
        if (hasNodeView || hasCompositeView || hasLegacyJSNodeView || hasSwingNodeView) {
            return nc.getNodeContainerState().isExecuted() && !nc.isInactive();
        }
        return null; // NOSONAR
    }

    static String hexStringColor(final int color) {
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
    private static Map.Entry<String, PortGroupEntBuilder>
        initializePortGroupEntBuilder(final Map.Entry<String, ExtendablePortGroup> entry, final boolean canEditPorts) {
        var portGroupId = entry.getKey();
        var portGroup = entry.getValue();
        var isInputPort = portGroup.definesInputPorts();
        var supportedTypeIds = Arrays.stream(portGroup.getSupportedPortTypes())//
            .map(CoreUtil::getPortTypeId)//
            .toList();
        var canAddPort = canEditPorts && portGroup.canAddPort();
        return Map.entry(portGroupId, builder(PortGroupEntBuilder.class)//
            .setSupportedPortTypeIds(supportedTypeIds)//
            .setCanAddInPort(isInputPort ? canAddPort : null)//
            .setCanAddOutPort(!isInputPort ? canAddPort : null)//
            .setInputRange(List.of())//
            .setOutputRange(List.of()));
    }

    private static boolean isExecuting(final NodeContainerState ncState) {
        return (ncState.isExecutionInProgress() && !ncState.isWaitingToBeExecuted()) || ncState.isExecutingRemotely();
    }

    private static Boolean isNodeResetOrCanBeReset(final NodeContainerState state, final NodeID nodeId,
        final DependentNodeProperties depNodeProps) {
        if (state.isExecutionInProgress()) {
            return Boolean.FALSE;
        } else if (state.isExecuted()) {
            return depNodeProps.canResetNode(nodeId);
        } else {
            return Boolean.TRUE;
        }
    }

    private static Boolean isStreamable(final NativeNodeContainer nc) {
        final Class<?> nodeModelClass = nc.getNode().getNodeModel().getClass();
        return IS_STREAMABLE.computeIfAbsent(nodeModelClass, CoreUtil::isStreamable);
    }

}

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
import java.net.URI;
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
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.knime.core.internal.ReferencedFile;
import org.knime.core.node.BufferedDataTable;
import org.knime.core.node.DynamicNodeFactory;
import org.knime.core.node.NoDescriptionProxy;
import org.knime.core.node.Node;
import org.knime.core.node.NodeDescription;
import org.knime.core.node.NodeFactory;
import org.knime.core.node.NodeFactory.NodeType;
import org.knime.core.node.NodeLogger;
import org.knime.core.node.NodeModel;
import org.knime.core.node.NodeProgressMonitor;
import org.knime.core.node.NodeSettings;
import org.knime.core.node.config.base.JSONConfig;
import org.knime.core.node.config.base.JSONConfig.WriterConfig;
import org.knime.core.node.context.ModifiableNodeCreationConfiguration;
import org.knime.core.node.context.ports.ConfigurablePortGroup;
import org.knime.core.node.context.ports.ModifiablePortsConfiguration;
import org.knime.core.node.context.ports.PortGroupConfiguration;
import org.knime.core.node.dialog.DialogNodeValue;
import org.knime.core.node.dialog.SubNodeDescriptionProvider;
import org.knime.core.node.missing.MissingNodeFactory;
import org.knime.core.node.port.PortObject;
import org.knime.core.node.port.PortType;
import org.knime.core.node.port.flowvariable.FlowVariablePortObject;
import org.knime.core.node.util.NodeExecutionJobManagerPool;
import org.knime.core.node.wizard.page.WizardPageUtil;
import org.knime.core.node.workflow.AbstractNodeExecutionJobManager;
import org.knime.core.node.workflow.AnnotationData.StyleRange;
import org.knime.core.node.workflow.ComponentMetadata;
import org.knime.core.node.workflow.ComponentMetadata.ComponentNodeType;
import org.knime.core.node.workflow.ConnectionContainer;
import org.knime.core.node.workflow.ConnectionProgress;
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
import org.knime.gateway.api.webui.entity.DynamicPortGroupDescriptionEnt;
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
import org.knime.gateway.api.webui.entity.NativeNodeDescriptionEnt;
import org.knime.gateway.api.webui.entity.NativeNodeDescriptionEnt.NativeNodeDescriptionEntBuilder;
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
import org.knime.gateway.api.webui.entity.NodePortTemplateEnt;
import org.knime.gateway.api.webui.entity.NodePortTemplateEnt.NodePortTemplateEntBuilder;
import org.knime.gateway.api.webui.entity.NodeStateEnt;
import org.knime.gateway.api.webui.entity.NodeStateEnt.ExecutionStateEnum;
import org.knime.gateway.api.webui.entity.NodeStateEnt.NodeStateEntBuilder;
import org.knime.gateway.api.webui.entity.NodeTemplateEnt;
import org.knime.gateway.api.webui.entity.NodeTemplateEnt.NodeTemplateEntBuilder;
import org.knime.gateway.api.webui.entity.NodeViewDescriptionEnt;
import org.knime.gateway.api.webui.entity.NodeViewDescriptionEnt.NodeViewDescriptionEntBuilder;
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
import org.knime.gateway.api.webui.service.util.ServiceExceptions;
import org.knime.gateway.api.webui.util.WorkflowBuildContext.WorkflowBuildContextBuilder;
import org.xml.sax.SAXException;

/**
 * Collects helper methods to build entity instances basically from core.api-classes (e.g. WorkflowManager etc.).
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
public final class EntityBuilderUtil {

    /**
     * The node position in the java-ui refers to the upper left corner of the 'node figure' which also includes
     * the (not always visible) implicit flow variables ports. I.e. the position does NOT match with the upper left
     * corner of the node background image. However, this is used as reference point in the web-ui. Thus, we need
     * to correct the position in y direction by some pixels.
     * (the value is chosen according to org.knime.workbench.editor2.figures.AbstractPortFigure.getPortSizeNode())
     *
     * NOTE: the current value has been 'experimentally' determined
     */
    public static final int NODE_Y_POS_CORRECTION = 6;

    /*
     * The default background color for node annotations which usually translates to opaque.
     */
    private static final int DEFAULT_NODE_ANNOTATION_BG_COLOR = 0xFFFFFF;

    private static final Map<String, NativeNodeInvariantsEnt> m_nativeNodeInvariantsCache = new ConcurrentHashMap<>();

    private static final Map<String, NodePortDescriptionEntBuilder> m_nodePortBuilderCache = new ConcurrentHashMap<>();

    private static final Map<Class<?>, Boolean> IS_STREAMABLE = new ConcurrentHashMap<>(0);

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
        return buildWorkflowEnt(wfm, WorkflowBuildContext.builder());
    }

    /**
     * Builds a new {@link WorkflowEnt} instance.
     *
     * @param wfm the workflow manager to build the workflow entity for
     *
     * @param buildContextBuilder contextual information required to build the {@link WorkflowEnt} instance
     * @return the newly created entity
     */
    public static WorkflowEnt buildWorkflowEnt(final WorkflowManager wfm, final WorkflowBuildContextBuilder buildContextBuilder) { // NOSONAR
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
                .map(EntityBuilderUtil::buildWorkflowAnnotationEnt).collect(Collectors.toList());
            WorkflowInfoEnt info = buildWorkflowInfoEnt(wfm, buildContext);
            return builder(WorkflowEntBuilder.class).setInfo(info)//
                .setNodes(nodes)//
                .setNodeTemplates(invariants)//
                .setConnections(connections)//
                .setWorkflowAnnotations(annotations)//
                .setAllowedActions(buildContext.includeInteractionInfo()
                    ? buildAllowedWorkflowActionsEnt(wfm, buildContext) : null)//
                .setParents(buildParentWorkflowInfoEnts(wfm, buildContext))//
                .setMetaInPorts(buildMetaPortsEntForWorkflow(wfm, true, buildContext))//
                .setMetaOutPorts(buildMetaPortsEntForWorkflow(wfm, false, buildContext))//
                .setProjectMetadata(wfm.isProject() ? buildProjectMetadataEnt(wfm) : null)//
                .setComponentMetadata(
                    CoreUtil.isComponentWFM(wfm) ? buildComponentNodeDescriptionEnt(getParentComponent(wfm)) : null)//
                .setAmbiguousPortTypes(buildAmbiguousPortTypesMap(buildContext))//
                .setDirty(wfm.isDirty())
                .build();
        }
    }

    private static Map<String, List<Integer>> buildAmbiguousPortTypesMap(final WorkflowBuildContext buildContext) {
        if (buildContext.inPortTypes() == null || buildContext.outPortTypes() == null) {
            return null;
        }
        Map<String, List<Integer>> res = null;
        for (PortType source : buildContext.outPortTypes()) {
            List<Integer> destList = new ArrayList<>();
            for (PortType dest : buildContext.inPortTypes()) {
                if (dest.equals(source)) {
                    continue;
                }
                if (source.getPortObjectClass().isAssignableFrom(dest.getPortObjectClass())
                    || dest.getPortObjectClass().isAssignableFrom(source.getPortObjectClass())) {
                    destList.add(dest.getPortObjectClass().getName().hashCode());
                }
            }
            if (!destList.isEmpty()) {
                // the source port type can be additionally connected to at least one other port type than itself
                if (res == null) {
                    res = new HashMap<>();
                }
                res.put(String.valueOf(source.getPortObjectClass().getName().hashCode()), destList);
            }
        }
        return res;
    }

    /**
     * Builds {@link NodeTemplateEnt}-instance that only has a minimal set of properties set. I.e. omitting some
     * properties such as icon and port infos.
     *
     * @param factory the node factory to create the template entity from
     * @return the new {@link NodeTemplateEnt}-instance
     */
    public static NodeTemplateEnt buildMinimalNodeTemplateEnt(final NodeFactory<? extends NodeModel> factory) {
        NodeTemplateEntBuilder builder = builder(NodeTemplateEntBuilder.class)//
            .setId(createTemplateId(factory))//
            .setName(factory.getNodeName())//
            .setComponent(false)//
            .setType(TypeEnum.valueOf(factory.getType().toString().toUpperCase()));
        return builder.build();
    }

    /**
     * Builds a {@link NodeTemplateEnt}-instance.
     *
     * @param factory the node factory to create the template entity from
     * @return the new {@link NodeTemplateEnt}-instance
     */
    public static NodeTemplateEnt buildNodeTemplateEnt(final NodeFactory<? extends NodeModel> factory) {
        Node node = new Node((NodeFactory<NodeModel>)factory);
        return builder(NodeTemplateEntBuilder.class)//
            .setId(createTemplateId(factory))//
            .setName(factory.getNodeName())//
            .setComponent(false)//
            .setType(TypeEnum.valueOf(factory.getType().toString().toUpperCase()))//
            .setInPorts(
                buildNodePortTemplateEnts(IntStream.range(1, node.getNrInPorts()).mapToObj(node::getInputType)))//
            .setOutPorts(
                buildNodePortTemplateEnts(IntStream.range(1, node.getNrOutPorts()).mapToObj(node::getOutputType)))//
            .setIcon(createIconDataURL(factory))//
            .setNodeFactory(buildNodeFactoryKeyEnt(factory)).build();
    }

    /**
     * Construct an entity representing the node description. The node description is potentially dynamically generated.
     * Information about ports is based on an instance of {@link org.knime.core.node.Node}.
     * @param coreNode The node instance to obtain information from.
     * @return an entity representing the node description.
     * @throws ServiceExceptions.NodeDescriptionNotAvailableException if node description could not be obtained.
     */
    public static NativeNodeDescriptionEnt buildNativeNodeDescriptionEnt(final Node coreNode)
            throws ServiceExceptions.NodeDescriptionNotAvailableException {

        NodeDescription nodeDescription = coreNode.invokeGetNodeDescription();
        if (nodeDescription instanceof NoDescriptionProxy) {
            // This will be the case when node description could not be read, cf. NodeDescription#init
            throw new ServiceExceptions.NodeDescriptionNotAvailableException("Could not read node description");
        }

        // intro and short description
        NativeNodeDescriptionEntBuilder builder = builder(NativeNodeDescriptionEntBuilder.class) //
                .setDescription(nodeDescription.getIntro().orElse(null)) //
                .setShortDescription(nodeDescription.getShortDescription().orElse(null));

        // dialog options
        builder.setOptions(buildDialogOptionGroupEnts(nodeDescription.getDialogOptionGroups()));

        // static/simple ports
        // Node#getInputType, #getOutputType, index 0 is the flow variable port, hence +1
        // Node#getNrInPorts adds 1 for flow variable port, hence -1
        // NodeDescription#getInportDescription is 0-indexed and does not contain description for flow variable port, hence +1
        builder.setInPorts(
                buildNativeNodePortDescriptionEnts(coreNode.getNrInPorts() - 1, nodeDescription::getInportName,
                        nodeDescription::getInportDescription, i -> coreNode.getInputType(i + 1))
        );
        builder.setOutPorts( //
            buildNativeNodePortDescriptionEnts(coreNode.getNrOutPorts() - 1, nodeDescription::getOutportName,
                nodeDescription::getOutportDescription, i -> coreNode.getOutputType(i + 1)) //
        );

        // dynamic port group descriptions (not the dynamically generated individual port descriptions)
        final Optional<ModifiablePortsConfiguration> portConfigs =
                coreNode.getCopyOfCreationConfig().flatMap(ModifiableNodeCreationConfiguration::getPortConfig);
        builder.setDynamicInPortGroupDescriptions( //
            buildDynamicPortGroupDescriptions(nodeDescription.getDynamicInPortGroups(), portConfigs) //
        );
        builder.setDynamicOutPortGroupDescriptions( //
            buildDynamicPortGroupDescriptions(nodeDescription.getDynamicOutPortGroups(), portConfigs) //
        );

        // view descriptions
        builder.setViews(buildNodeViewDescriptionEnts(coreNode.getNrViews(), nodeDescription));

        // interactive view description
        if (nodeDescription.getInteractiveViewName() != null) {
            builder.setInteractiveView( //
                builder(NodeViewDescriptionEntBuilder.class) //
                    .setName(nodeDescription.getInteractiveViewName()) //
                    .setDescription(nodeDescription.getInteractiveViewDescription().orElse(null)) //
                    .build() //
            );
        }

        // links
        builder.setLinks(buildNodeDescriptionLinkEnts(nodeDescription.getLinks()));

        return builder.build();
    }

    private static List<LinkEnt> buildNodeDescriptionLinkEnts(final List<NodeDescription.DescriptionLink> links) {
        return listMapOrNull( //
            links, //
            el -> builder(LinkEntBuilder.class).setText(el.getText()).setUrl(el.getTarget()).build() //
        );
    }

    private static List<NodeViewDescriptionEnt> buildNodeViewDescriptionEnts(final int nrViews,
        final NodeDescription nodeDescription) {
        if (nrViews < 1) {
            return null;  // NOSONAR: returning null is useful here
        }
        return IntStream.range(0, nrViews).mapToObj(index -> //
        builder(NodeViewDescriptionEntBuilder.class) //
            .setName(nodeDescription.getViewName(index)) //
            .setDescription(nodeDescription.getViewDescription(index)) //
            .build()) //
            .collect(toList());
    }

    private static List<DynamicPortGroupDescriptionEnt> buildDynamicPortGroupDescriptions(
        final List<NodeDescription.DynamicPortGroupDescription> portGroupDescriptions,
        final Optional<ModifiablePortsConfiguration> portConfigs) { // NOSONAR
        return listMapOrNull(portGroupDescriptions, pgd -> { // NOSONAR
            List<NodePortTemplateEnt> supportedPortTypes = portConfigs.map(pc -> {
                PortGroupConfiguration group = pc.getGroup(pgd.getGroupIdentifier());
                if (group instanceof ConfigurablePortGroup) {
                    ConfigurablePortGroup configurableGroupConfig = (ConfigurablePortGroup)group;
                    return buildNodePortTemplateEnts(Arrays.stream(configurableGroupConfig.getSupportedPortTypes()));
                } else {
                    return null; // map yields empty optional
                }
            }).orElse(null);

            return builder(DynamicPortGroupDescriptionEnt.DynamicPortGroupDescriptionEntBuilder.class) //
                .setName(pgd.getGroupName()) //
                .setDescription(pgd.getGroupDescription()) //
                .setIdentifier(pgd.getGroupIdentifier()) //
                .setSupportedPortTypes(supportedPortTypes) //
                .build();
        });
    }

    private static List<NodePortDescriptionEnt> buildNativeNodePortDescriptionEnts(final int nrPorts,
        final IntFunction<String> nameGetter, final IntFunction<String> descGetter,
        final IntFunction<PortType> typeGetter) {
        return listMapOrNull(IntStream.range(0, nrPorts).boxed().collect(toList()), //
            index -> builder(NodePortDescriptionEntBuilder.class) //
                .setName(nameGetter.apply(index)) //
                .setDescription(descGetter.apply(index)) //
                .setType(getNodePortTemplateEntType(typeGetter.apply(index))) //
                .setOptional(typeGetter.apply(index).isOptional()) //
                .build());
    }

    private static List<NodeDialogOptionGroupEnt> buildUngroupedDialogOptionGroupEnt(final List<NodeDialogOptionDescriptionEnt> ungroupedOptionEnts) {
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

    private static List<NodeDialogOptionGroupEnt> buildDialogOptionGroupEnts(final List<NodeDescription.DialogOptionGroup> groups) {
        return listMapOrNull(groups, g -> //
            builder(NodeDialogOptionGroupEnt.NodeDialogOptionGroupEntBuilder.class) //
                .setSectionName(g.getName().orElse(null)) //
                .setSectionDescription(g.getDescription().orElse(null)) //
                .setFields( //
                    buildDialogOptionDescriptionEnts(g.getOptions()) //
                ).build()
        );
    }

    private static List<NodeDialogOptionDescriptionEnt> buildDialogOptionDescriptionEnts(final List<NodeDescription.DialogOption> opts) {
        return listMapOrNull(opts, o -> //
            builder(NodeDialogOptionDescriptionEnt.NodeDialogOptionDescriptionEntBuilder.class) //
                .setName(o.getName()) //
                .setDescription(o.getDescription()) //
                .setOptional(o.isOptional()) //
                .build()
        );
    }

    /**
     * Map operation over a list with special handling of null values: If the input list is null or empty, the method
     * returns null.
     *
     * @param input List of elements to be transformed
     * @param transformation Transformation to apply to each element
     * @param <I> Type of input elements
     * @param <O> Type of output elemens
     * @return Transformed list
     */
    private static <I, O> List<O> listMapOrNull(final List<I> input, final Function<I, O> transformation) {
        if (input == null || input.isEmpty()) {
            return null;  // NOSONAR: returning null is useful here
        }
        return input.stream().map(transformation).collect(toList());
    }

    private static NodeFactoryKeyEnt buildNodeFactoryKeyEnt(final NodeFactory<? extends NodeModel> factory) {
        org.knime.gateway.api.webui.entity.NodeFactoryKeyEnt.NodeFactoryKeyEntBuilder nodeFactoryKeyBuilder =
            builder(NodeFactoryKeyEntBuilder.class);
        if (factory != null) {
            nodeFactoryKeyBuilder.setClassName(factory.getClass().getCanonicalName());
            //only set settings in case of a dynamic node factory
            if (DynamicNodeFactory.class.isAssignableFrom(factory.getClass())) {
                NodeSettings settings = new NodeSettings("settings");
                factory.saveAdditionalFactorySettings(settings);
                nodeFactoryKeyBuilder.setSettings(JSONConfig.toJSONString(settings, WriterConfig.DEFAULT));
            }
        } else {
            nodeFactoryKeyBuilder.setClassName("");
        }
        return nodeFactoryKeyBuilder.build();
    }

    private static List<NodePortTemplateEnt> buildNodePortTemplateEnts(final Stream<PortType> ptypes) {
        return ptypes.map(ptype -> {
            NodePortTemplateEnt.TypeEnum typeEnt = getNodePortTemplateEntType(ptype);
            return builder(NodePortTemplateEntBuilder.class) //
                .setName(null) //
                .setType(typeEnt) //
                .setOtherTypeId(getOtherPortTypeId(ptype, typeEnt, false)) //
                .setColor(getPortTypeColor(typeEnt, ptype)) //
                .setOptional(ptype.isOptional()).build();
        }).collect(Collectors.toList());
    }

    private static MetaPortsEnt buildMetaPortsEntForWorkflow(final WorkflowManager wfm, final boolean incoming,
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

    private static List<NodePortEnt> buildNodePortEntsForWorkflow(final WorkflowManager wfm, final boolean incoming,
        final WorkflowBuildContext buildContext) {
        List<NodePortEnt> ports = new ArrayList<>();
        if (incoming) {
            int nrPorts = wfm.getNrWorkflowIncomingPorts();
            for (int i = 0; i < nrPorts; i++) {
                Set<ConnectionContainer> connections = wfm.getOutgoingConnectionsFor(wfm.getID(), i);
                NodeOutPort port = wfm.getWorkflowIncomingPort(i);
                ports.add(buildNodePortEnt(port.getPortType(), port.getPortName(), port.getPortSummary(), i, null,
                    port.isInactive() ? Boolean.TRUE : null, connections, getPortObjectVersion(port, buildContext),
                    buildContext));
            }
        } else {
            int nrPorts = wfm.getNrWorkflowOutgoingPorts();
            for (int i = 0; i < nrPorts; i++) {
                ConnectionContainer connection = wfm.getIncomingConnectionFor(wfm.getID(), i);
                NodeInPort port = wfm.getWorkflowOutgoingPort(i);
                ports.add(buildNodePortEnt(port.getPortType(), port.getPortName(), null, i, null, null,
                    connection != null ? singleton(connection) : emptyList(), null, buildContext));
            }
        }
        return ports;
    }

    private static WorkflowInfoEnt buildWorkflowInfoEnt(final WorkflowManager wfm, final WorkflowBuildContext buildContext) {
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

    private static String getTemplateLink(final NodeContainerTemplate nct) {
        if (nct instanceof SubNodeContainer && ((SubNodeContainer)nct).isProject()) {
            return null;
        }
        URI sourceURI = nct.getTemplateInformation().getSourceURI();
        return sourceURI == null ? null : sourceURI.toString();
    }

    private static NodeIDEnt getContainerId(final WorkflowManager wfm, final WorkflowBuildContext buildContext) {
        if (wfm.isProject()) {
            return null;
        }

        NodeContainerParent ncParent = wfm.getDirectNCParent();
        if (ncParent instanceof SubNodeContainer) {
            // it's a component's workflow
            return buildContext.buildNodeIDEnt(((SubNodeContainer)ncParent).getID());
        } else {
            return buildContext.buildNodeIDEnt(wfm.getID());
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
            Arrays.stream(wa.getStyleRanges()).map(EntityBuilderUtil::buildStyleRangeEnt).collect(Collectors.toList());
        return builder(WorkflowAnnotationEntBuilder.class)
                .setId(new AnnotationIDEnt(wa.getID()))//
                .setTextAlign(textAlign)//
                .setBackgroundColor(hexStringColor(wa.getBgColor()))//
                .setBorderColor(hexStringColor(wa.getBorderColor()))//
                .setBorderWidth(wa.getBorderSize())//
                .setBounds(bounds)//
                .setText(wa.getText())//
                .setStyleRanges(styleRanges)//
                .setDefaultFontSize(wa.getDefaultFontSize() > 0 ? convertFromPtToPx(wa.getDefaultFontSize()) : null)//
                .build();
    }

    private static Integer convertFromPtToPx(final int size) {
        return (int)Math.round(size + size / 3.0);
    }

    private static StyleRangeEnt buildStyleRangeEnt(final StyleRange sr) {
        StyleRangeEntBuilder builder = builder(StyleRangeEntBuilder.class)
                .setFontSize(convertFromPtToPx(sr.getFontSize()))//
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

    private static String hexStringColor(final int color) {
        return String.format("#%06X", (0xFFFFFF & color));
    }

    private static void buildAndAddNodeEnt(final NodeIDEnt id, final NodeContainer nc, final Map<String, NodeEnt> nodes,
        final Map<String, NativeNodeInvariantsEnt> invariants, final WorkflowBuildContext buildContext) {
        NodeEnt nodeEnt = buildNodeEnt(id, nc, buildContext);
        nodes.put(nodeEnt.getId().toString(), nodeEnt);
        if (nc instanceof NativeNodeContainer) {
            String templateId = ((NativeNodeEnt)nodeEnt).getTemplateId();
            invariants.computeIfAbsent(templateId,
                tid -> buildOrGetFromCacheNativeNodeInvariantsEnt(templateId, (NativeNodeContainer)nc));
        }
    }

    private static NodeEnt buildNodeEnt(final NodeIDEnt id, final NodeContainer nc,
        final WorkflowBuildContext buildContext) {
        return buildNodeEnt(id, nc, buildContext.includeInteractionInfo()
            ? buildAllowedNodeActionsEnt(nc, buildContext.dependentNodeProperties()) : null, buildContext);
    }

    private static NodeEnt buildNodeEnt(final NodeIDEnt id, final NodeContainer nc,
        final AllowedNodeActionsEnt allowedActions, final WorkflowBuildContext buildContext) {
        if (nc instanceof NativeNodeContainer) {
            return buildNativeNodeEnt(id, (NativeNodeContainer)nc, allowedActions, buildContext);
        } else if (nc instanceof WorkflowManager) {
            return buildMetaNodeEnt(id, (WorkflowManager)nc, allowedActions, buildContext);
        } else if (nc instanceof SubNodeContainer) {
            return buildComponentNodeEnt(id, (SubNodeContainer)nc, allowedActions, buildContext);
        } else {
            throw new IllegalArgumentException(
                "Node container " + nc.getClass().getCanonicalName() + " cannot be mapped to a node entity.");
        }
    }

    private static AllowedNodeActionsEnt buildAllowedNodeActionsEnt(final NodeContainer nc,
        final DependentNodeProperties depNodeProps) {
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

    private static AllowedWorkflowActionsEnt buildAllowedWorkflowActionsEnt(final WorkflowManager wfm,
            final WorkflowBuildContext buildContext) {
        return builder(AllowedWorkflowActionsEntBuilder.class)//
            .setCanReset(buildContext.dependentNodeProperties().canResetAny())
            .setCanExecute(wfm.canExecuteAll())//
            .setCanCancel(wfm.canCancelAll())//
            .setCanUndo(buildContext.canUndo())//
            .setCanRedo(buildContext.canRedo()).build();
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
            .setLink(getTemplateLink(wm))//
            .setAllowedActions(allowedActions)//
            .setExecutionInfo(buildNodeExecutionInfoEnt(wm))//
            .build();
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
        final WorkflowBuildContext buildContext) {
        return buildNodePortEnts(wm, inPorts, buildContext).stream().map(np -> { // NOSONAR
            NodeStateEnum nodeState;
            if (!inPorts) {
                nodeState = getNodeStateEnumForMetaNodePort(wm.getOutPort(np.getIndex()).getNodeState());
            } else {
                nodeState = null;
            }
            return builder(MetaNodePortEntBuilder.class)
                .setColor(np.getColor())//
                .setConnectedVia(np.getConnectedVia())//
                .setInactive(np.isInactive())//
                .setIndex(np.getIndex())//
                .setInfo(np.getInfo())//
                .setName(np.getName())//
                .setOptional(np.isOptional())//
                .setType(np.getType())//
                .setNodeState(nodeState)//
                .build();
        }).collect(toList());
    }

    private static ComponentNodeEnt buildComponentNodeEnt(final NodeIDEnt id, final SubNodeContainer nc,
        final AllowedNodeActionsEnt allowedActions, final WorkflowBuildContext buildContext) {
        ComponentMetadata metadata = nc.getMetadata();
        String type = metadata.getNodeType().map(ComponentNodeType::toString).orElse(null);
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
            .setAllowedActions(allowedActions)//
            .setExecutionInfo(buildNodeExecutionInfoEnt(nc)).build();
    }

    /*
     * Returns null if the node has no node view; false, if there is a node view but there is nothing to display,
     * true, if there is a node view which also has something to display.
     */
    private static Boolean hasAndCanOpenNodeView(final NodeContainer nc) {
        var hasNodeView = NodeViewManager.hasNodeView(nc);
        var hasCompositeView =
            nc instanceof SubNodeContainer && WizardPageUtil.isWizardPage(nc.getParent(), nc.getID());
        var hasLegacyJSNodeView = nc instanceof NativeNodeContainer && nc.getInteractiveWebViews().size() > 0;
        if (hasNodeView || hasCompositeView || hasLegacyJSNodeView) {
            return nc.getNodeContainerState().isExecuted();
        }
        return null; // NOSONAR
    }

    private static List<NodePortEnt> buildNodePortEnts(final NodeContainer nc, final boolean inPorts,
        final WorkflowBuildContext buildContext) {
        List<NodePortEnt> res = new ArrayList<>();
        if (inPorts) {
            for (int i = 0; i < nc.getNrInPorts(); i++) {
                ConnectionContainer connection = nc.getParent().getIncomingConnectionFor(nc.getID(), i);
                NodeInPort inPort = nc.getInPort(i);
                PortType pt = inPort.getPortType();
                buildContext.updatePortTypes(pt, true);
                res.add(buildNodePortEnt(pt, inPort.getPortName(), null, i, pt.isOptional(), null,
                    connection == null ? Collections.emptyList() : Collections.singletonList(connection), null,
                    buildContext));
            }
        } else {
            for (int i = 0; i < nc.getNrOutPorts(); i++) {
                Set<ConnectionContainer> connections = nc.getParent().getOutgoingConnectionsFor(nc.getID(), i);
                NodeOutPort outPort = nc.getOutPort(i);
                PortType pt = outPort.getPortType();
                buildContext.updatePortTypes(pt, false);
                res.add(buildNodePortEnt(pt, outPort.getPortName(), outPort.getPortSummary(), i, null,
                    outPort.isInactive() ? outPort.isInactive() : null, connections,
                    getPortObjectVersion(outPort, buildContext), buildContext));
            }
        }
        return res;
    }

    private static Integer getPortObjectVersion(final NodeOutPort outPort, final WorkflowBuildContext buildContext) {
        if (!buildContext.includeInteractionInfo()) {
            return null;
        }
        if (outPort.getPortType().equals(FlowVariablePortObject.TYPE)) {
            HashCodeBuilder hashCodeBuilder = new HashCodeBuilder();
            var flowObjectStack = outPort.getFlowObjectStack();
            if (flowObjectStack != null) {
                for (FlowVariable v : flowObjectStack.getAllAvailableFlowVariables().values()) {
                    hashCodeBuilder.append(v.getName());
                    hashCodeBuilder.append(v.getValue(v.getVariableType()).hashCode());
                }
            }
            return hashCodeBuilder.build();
        } else {
            PortObject po = outPort.getPortObject();
            return po == null ? null : System.identityHashCode(po);
        }
    }

    @SuppressWarnings("java:S107") // it's a 'builder'-method, so many parameters are ok
    private static NodePortEnt buildNodePortEnt(final PortType ptype, final String name, final String info,
        final int portIdx, final Boolean isOptional, final Boolean isInactive,
        final Collection<ConnectionContainer> connections, final Integer portObjectVersion,
        final WorkflowBuildContext buildContext) {
        NodePortTemplateEnt.TypeEnum resPortType = getNodePortTemplateEntType(ptype);
        return builder(NodePortEntBuilder.class).setIndex(portIdx)//
            .setOptional(isOptional)//
            .setInactive(isInactive)//
            .setConnectedVia(
                connections.stream().map(cc -> buildConnectionIDEnt(cc, buildContext)).collect(Collectors.toList()))//
            .setName(name)//
            .setInfo(info)//
            .setType(resPortType)//
            .setOtherTypeId(getOtherPortTypeId(ptype, resPortType, buildContext.includeInteractionInfo()))//
            .setColor(getPortTypeColor(resPortType, ptype))//
            .setView(buildPortViewEnt(ptype))//
            .setPortObjectVersion(portObjectVersion)//
            .build();
    }

    private static PortViewEnt buildPortViewEnt(final PortType ptype) {
        BuildInWebPortViewType portViewType = BuildInWebPortViewType.getPortViewTypeFor(ptype).orElse(null);
        if (portViewType != null && portViewType != BuildInWebPortViewType.FLOWVARIABLE) {
            return builder(PortViewEntBuilder.class).setType(PortViewEnt.TypeEnum.valueOf(portViewType.toString()))
                .build();
        } else {
            return null;
        }
    }

    private static Integer getOtherPortTypeId(final PortType ptype, final NodePortTemplateEnt.TypeEnum portTypeEnt,
        final boolean includeInteractionInfo) {
        return includeInteractionInfo && NodePortTemplateEnt.TypeEnum.OTHER == portTypeEnt
            ? ptype.getPortObjectClass().getName().hashCode() : null;
    }

    private static String getPortTypeColor(final NodePortTemplateEnt.TypeEnum entType, final PortType ptype) {
        return entType == NodePortTemplateEnt.TypeEnum.OTHER ? hexStringColor(ptype.getColor()) : null;
    }

    private static NodePortTemplateEnt.TypeEnum getNodePortTemplateEntType(final PortType ptype) {
        if (BufferedDataTable.TYPE.equals(ptype)) {
            return NodePortTemplateEnt.TypeEnum.TABLE;
        } else if (FlowVariablePortObject.TYPE.equals(ptype)) {
            return NodePortTemplateEnt.TypeEnum.FLOWVARIABLE;
        } else if (PortObject.TYPE.equals(ptype)) {
            return NodePortTemplateEnt.TypeEnum.GENERIC;
        } else {
            return NodePortTemplateEnt.TypeEnum.OTHER;
        }
    }

    private static ConnectionIDEnt buildConnectionIDEnt(final ConnectionContainer c, final WorkflowBuildContext buildContext) {
        return new ConnectionIDEnt(buildContext.buildNodeIDEnt(c.getDest()), c.getDestPort());
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
                .setTextAlign(textAlign)//
                .setBackgroundColor(na.getBgColor() == DEFAULT_NODE_ANNOTATION_BG_COLOR ? null : hexStringColor(na
                    .getBgColor()))//
                .setText(na.getText())//
                .setStyleRanges(styleRanges)//
                .setDefaultFontSize(na.getDefaultFontSize() > 0 ? convertFromPtToPx(na.getDefaultFontSize()) : null)//
                .build();
    }

    private static NativeNodeEnt buildNativeNodeEnt(final NodeIDEnt id, final NativeNodeContainer nc,
        final AllowedNodeActionsEnt allowedActions, final WorkflowBuildContext buildContext) {
        return builder(NativeNodeEntBuilder.class)//
            .setId(id)//
            .setOutPorts(buildNodePortEnts(nc, false, buildContext))//
            .setAnnotation(buildNodeAnnotationEnt(nc.getNodeAnnotation()))//
            .setInPorts(buildNodePortEnts(nc, true, buildContext))//
            .setPosition(buildXYEnt(nc.getUIInformation()))//
            .setKind(KindEnum.NODE)//
            .setState(buildNodeStateEnt(nc))//
            .setTemplateId(createTemplateId(nc.getNode().getFactory()))//
            .setAllowedActions(allowedActions)//
            .setExecutionInfo(buildNodeExecutionInfoEnt(nc))//
            .setLoopInfo(buildLoopInfoEnt(nc, buildContext))//
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

    private static LoopInfoEnt buildLoopInfoEnt(final NativeNodeContainer nc, final WorkflowBuildContext buildContext) {
        if (!nc.isModelCompatibleTo(LoopEndNode.class)) {
            return null;
        }
        StatusEnum status = StatusEnum.valueOf(nc.getLoopStatus().name());
        AllowedLoopActionsEnt allowedActions = null;
        if (buildContext.includeInteractionInfo()) {
            allowedActions = LoopState.getAllowedActions(nc, buildContext);
        }
        return builder(LoopInfoEntBuilder.class).setStatus(status).setAllowedActions(allowedActions).build();
    }

    /**
     * Characterization of loop state for determining allowed actions.
     * This is not part of the API, see {@link StatusEnum} instead.
     */
    private enum LoopState {

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

    private static NativeNodeInvariantsEnt buildOrGetFromCacheNativeNodeInvariantsEnt(final String templateId,
        final NativeNodeContainer nc) {
        return m_nativeNodeInvariantsCache.computeIfAbsent(templateId, id -> buildNativeNodeInvariantsEnt(nc));
    }

    private static NativeNodeInvariantsEnt buildNativeNodeInvariantsEnt(final NativeNodeContainer nc) {
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

    /**
     * Creates an id the uniquely represents a node factory (node matter if it's a normal one or a dynamic node
     * factory).
     *
     * @param nodeFactory the factory to create the id for
     * @return the new template id
     */
    public static String createTemplateId(final NodeFactory<? extends NodeModel> nodeFactory) {
        String configHash = "";
        if (nodeFactory instanceof DynamicNodeFactory) {
            final NodeSettings settings = new NodeSettings("");
            nodeFactory.saveAdditionalFactorySettings(settings);
            configHash = ConfigUtils.contentBasedHashString(settings);
        }
        if (nodeFactory instanceof MissingNodeFactory) {
            NodeAndBundleInformation nodeInfo = ((MissingNodeFactory)nodeFactory).getNodeAndBundleInfo();
            return nodeInfo.getFactoryClass().orElseGet(() -> "unknown_missing_node_factory_" + UUID.randomUUID())
                + configHash;
        } else {
            return nodeFactory.getClass().getCanonicalName() + configHash;
        }
    }

    private static ComponentNodeDescriptionEnt buildComponentNodeDescriptionEnt(final SubNodeContainer snc) {
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

    private static SubNodeContainer getParentComponent(final WorkflowManager wfm) {
        NodeContainerParent ncParent = wfm.getDirectNCParent();
        return ncParent instanceof SubNodeContainer ? (SubNodeContainer)ncParent : null;
    }

    private static List<NodeDialogOptionDescriptionEnt> buildComponentDialogOptionsEnts(final SubNodeContainer snc) {
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

    private static List<NodeViewDescriptionEnt> buildComponentViewDescriptionEnts(final SubNodeContainer snc) {
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

    private static List<NodePortDescriptionEnt>
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

    private static List<NodePortDescriptionEnt>
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

    private static NodePortDescriptionEnt buildOrGetFromCacheNodePortDescriptionEnt(final PortType ptype,
        final String name, final String description) {
        NodePortDescriptionEntBuilder builder = m_nodePortBuilderCache.computeIfAbsent(
            ptype.getPortObjectClass().getCanonicalName(), k -> buildNodePortDescriptionEntBuilder(ptype));
        builder.setName(isBlank(name) ? null : name);
        builder.setDescription(isBlank(description) ? null : description);
        return builder.build();
    }

    private static NodePortDescriptionEntBuilder buildNodePortDescriptionEntBuilder(final PortType ptype) {
        NodePortTemplateEnt.TypeEnum resPortType = getNodePortTemplateEntType(ptype);
        return builder(NodePortDescriptionEntBuilder.class)//
            .setType(resPortType)//
            .setTypeName(ptype.getName())//
            .setColor(getPortTypeColor(resPortType, ptype))//
            .setOptional(ptype.isOptional());
    }

    private static NodeExecutionInfoEnt buildNodeExecutionInfoEnt(final NodeContainer nc) {
        JobManagerEnt jobManagerEnt = buildJobManagerEnt(nc.getJobManager());
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

    private static NodeExecutionInfoEnt buildNodeExecutionInfoEntFromParentJobManager(
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
                NodeLogger.getLogger(EntityBuilderUtil.class).error(String.format(
                    "Problem reading icon for job manager '%s' and node '%s'.", parentJobManager.getID(), nc), ex);
                return null;
            }
        } else {
            return null;
        }
    }

    private static Boolean isStreamable(final NativeNodeContainer nc) {
        final Class<?> nodeModelClass = nc.getNode().getNodeModel().getClass();
        return IS_STREAMABLE.computeIfAbsent(nodeModelClass, CoreUtil::isStreamable);
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

    private static CustomJobManagerEnt buildCustomJobManagerEnt(final NodeExecutionJobManager jobManager) {
        NodeExecutionJobManagerFactory factory = NodeExecutionJobManagerPool.getJobManagerFactory(jobManager.getID());
        String name = factory == null ? jobManager.getID() : factory.getLabel();
        String icon = null;
        String iconForWorkflow = null;
        try {
            icon = createIconDataURL(jobManager.getIcon());
        } catch (IOException ex) {
            NodeLogger.getLogger(EntityBuilderUtil.class)
                .error(String.format("Icon for job manager '%s' couldn't be read", name), ex);
        }
        if (jobManager instanceof AbstractNodeExecutionJobManager) {
            try {
                iconForWorkflow = createIconDataURL(((AbstractNodeExecutionJobManager)jobManager).getIconForWorkflow());
            } catch (IOException ex) {
                NodeLogger.getLogger(EntityBuilderUtil.class)
                    .error(String.format("Workflow icon for job manager '%s' couldn't be read", name), ex);
            }
        }
        return builder(CustomJobManagerEntBuilder.class).setName(name).setIcon(icon).setWorkflowIcon(iconForWorkflow)
            .build();
    }

    private static String createIconDataURL(final NodeFactory<?> nodeFactory) {
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

    private static ConnectionEnt buildConnectionEnt(final ConnectionIDEnt id, final ConnectionContainer cc,
        final WorkflowBuildContext buildContext) {
        ConnectionEntBuilder builder = builder(ConnectionEntBuilder.class)//
            .setId(id)//
            .setDestNode(id.getDestNodeIDEnt())//
            .setDestPort(cc.getDestPort())//
            .setSourceNode(buildContext.buildNodeIDEnt(cc.getSource())).setSourcePort(cc.getSourcePort())//
            .setFlowVariableConnection(cc.isFlowVariablePortConnection() ? cc.isFlowVariablePortConnection() : null);
        if (buildContext.isInStreamingMode()) {
            ConnectionProgress connectionProgress = cc.getConnectionProgress().orElse(null);
            if (connectionProgress != null) {
                builder.setLabel(connectionProgress.getMessage()).setStreaming(connectionProgress.inProgress());
            }
        }
        if (buildContext.includeInteractionInfo()) {
            builder.setAllowedActions(buildAllowedConnectionActionsEnt(cc, buildContext));
        }
        return builder.build();
    }

    private static AllowedConnectionActionsEnt buildAllowedConnectionActionsEnt(final ConnectionContainer cc,
        final WorkflowBuildContext buildContext) {
        boolean canDelete;
        if (!cc.isDeletable()) {
            canDelete = false;
        } else {
            WorkflowManager wfm = buildContext.wfm();
            if (cc.getDest().equals(wfm.getID())) {
                canDelete = wfm.canRemoveConnection(cc);
            } else {
                NodeContainer nc = wfm.getNodeContainer(cc.getDest());
                canDelete = isNodeResetOrCanBeReset(nc.getNodeContainerState(), nc.getID(),
                    buildContext.dependentNodeProperties());
            }
        }
        return builder(AllowedConnectionActionsEntBuilder.class).setCanDelete(canDelete).build();
    }

}

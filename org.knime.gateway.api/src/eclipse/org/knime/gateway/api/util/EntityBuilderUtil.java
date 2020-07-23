/*
 * ------------------------------------------------------------------------
 *  Copyright by KNIME AG, Zurich, Switzerland
 *  Website: http://www.knime.com; Email: contact@knime.com
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License, Version 3, as
 *  published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but
 *  WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, see <http://www.gnu.org/licenses>.
 *
 *  Additional permission under GNU GPL version 3 section 7:
 *
 *  KNIME interoperates with ECLIPSE solely via ECLIPSE's plug-in APIs.
 *  Hence, KNIME and ECLIPSE are both independent programs and are not
 *  derived from each other. Should, however, the interpretation of the
 *  GNU GPL Version 3 ("License") under any applicable laws result in
 *  KNIME and ECLIPSE being a combined program, KNIME AG herewith grants
 *  you the additional permission to use and propagate KNIME together with
 *  ECLIPSE with only the license terms in place for ECLIPSE applying to
 *  ECLIPSE and the GNU GPL Version 3 applying for KNIME, provided the
 *  license terms of ECLIPSE themselves allow for the respective use and
 *  propagation of ECLIPSE together with KNIME.
 *
 *  Additional permission relating to nodes for KNIME that extend the Node
 *  Extension (and in particular that are based on subclasses of NodeModel,
 *  NodeDialog, and NodeView) and that only interoperate with KNIME through
 *  standard APIs ("Nodes"):
 *  Nodes are deemed to be separate and independent programs and to not be
 *  covered works.  Notwithstanding anything to the contrary in the
 *  License, the License does not apply to Nodes, you are not required to
 *  license Nodes under the License, and you are granted a license to
 *  prepare and propagate Nodes, in each case even if such Nodes are
 *  propagated with or for interoperation with KNIME.  The owner of a Node
 *  may freely choose the license terms applicable to such Node, including
 *  when such Node is propagated with or for interoperation with KNIME.
 * ---------------------------------------------------------------------
 *
 */
package org.knime.gateway.api.util;

import static org.knime.gateway.api.entity.EntityBuilderManager.builder;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.apache.commons.codec.binary.Base64;
import org.knime.core.data.DataCell;
import org.knime.core.data.DataCellDataOutput;
import org.knime.core.data.DataCellSerializer;
import org.knime.core.data.DataRow;
import org.knime.core.data.DataTableSpec;
import org.knime.core.data.DataType;
import org.knime.core.data.DataTypeRegistry;
import org.knime.core.data.DirectAccessTable;
import org.knime.core.data.DirectAccessTable.UnknownRowCountException;
import org.knime.core.data.MissingValue;
import org.knime.core.data.container.CloseableRowIterator;
import org.knime.core.data.def.BooleanCell;
import org.knime.core.data.def.DoubleCell;
import org.knime.core.data.def.IntCell;
import org.knime.core.data.def.LongCell;
import org.knime.core.data.def.StringCell;
import org.knime.core.data.filestore.FileStoreCell;
import org.knime.core.node.BufferedDataTable;
import org.knime.core.node.CanceledExecutionException;
import org.knime.core.node.DynamicNodeFactory;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.ModelContent;
import org.knime.core.node.NodeFactory;
import org.knime.core.node.NodeModel;
import org.knime.core.node.NodeSettings;
import org.knime.core.node.config.base.JSONConfig;
import org.knime.core.node.config.base.JSONConfig.WriterConfig;
import org.knime.core.node.context.ModifiableNodeCreationConfiguration;
import org.knime.core.node.exec.dataexchange.PortObjectRepository;
import org.knime.core.node.port.AbstractSimplePortObjectSpec;
import org.knime.core.node.port.AbstractSimplePortObjectSpec.AbstractSimplePortObjectSpecSerializer;
import org.knime.core.node.port.MetaPortInfo;
import org.knime.core.node.port.PortObjectSpec;
import org.knime.core.node.port.PortObjectSpec.PortObjectSpecSerializer;
import org.knime.core.node.port.PortObjectSpecZipOutputStream;
import org.knime.core.node.port.PortType;
import org.knime.core.node.port.PortTypeRegistry;
import org.knime.core.node.port.PortUtil;
import org.knime.core.node.port.flowvariable.FlowVariablePortObjectSpec;
import org.knime.core.node.port.inactive.InactiveBranchPortObjectSpec;
import org.knime.core.node.util.NodeExecutionJobManagerPool;
import org.knime.core.node.web.WebViewContent;
import org.knime.core.node.wizard.WizardNode;
import org.knime.core.node.workflow.AnnotationData.StyleRange;
import org.knime.core.node.workflow.ConnectionContainer;
import org.knime.core.node.workflow.ConnectionContainer.ConnectionType;
import org.knime.core.node.workflow.ConnectionID;
import org.knime.core.node.workflow.EditorUIInformation;
import org.knime.core.node.workflow.FlowVariable;
import org.knime.core.node.workflow.NativeNodeContainer;
import org.knime.core.node.workflow.NodeAnnotation;
import org.knime.core.node.workflow.NodeContainer;
import org.knime.core.node.workflow.NodeContainerState;
import org.knime.core.node.workflow.NodeExecutionJobManager;
import org.knime.core.node.workflow.NodeID;
import org.knime.core.node.workflow.NodeInPort;
import org.knime.core.node.workflow.NodeMessage;
import org.knime.core.node.workflow.NodeOutPort;
import org.knime.core.node.workflow.NodeUIInformation;
import org.knime.core.node.workflow.SubNodeContainer;
import org.knime.core.node.workflow.WorkflowAnnotation;
import org.knime.core.node.workflow.WorkflowAnnotationID;
import org.knime.core.node.workflow.WorkflowCopyContent;
import org.knime.core.node.workflow.WorkflowManager;
import org.knime.core.node.workflow.WorkflowManager.NodeModelFilter;
import org.knime.core.node.workflow.action.InteractiveWebViewsResult;
import org.knime.core.wizard.SinglePageManager;
import org.knime.gateway.api.entity.AnnotationIDEnt;
import org.knime.gateway.api.entity.BoundsEnt;
import org.knime.gateway.api.entity.BoundsEnt.BoundsEntBuilder;
import org.knime.gateway.api.entity.ConnectionEnt;
import org.knime.gateway.api.entity.ConnectionEnt.ConnectionEntBuilder;
import org.knime.gateway.api.entity.ConnectionEnt.TypeEnum;
import org.knime.gateway.api.entity.ConnectionIDEnt;
import org.knime.gateway.api.entity.DataCellEnt;
import org.knime.gateway.api.entity.DataCellEnt.DataCellEntBuilder;
import org.knime.gateway.api.entity.DataRowEnt;
import org.knime.gateway.api.entity.DataRowEnt.DataRowEntBuilder;
import org.knime.gateway.api.entity.DataTableEnt;
import org.knime.gateway.api.entity.DataTableEnt.DataTableEntBuilder;
import org.knime.gateway.api.entity.FlowVariableEnt;
import org.knime.gateway.api.entity.FlowVariableEnt.FlowVariableEntBuilder;
import org.knime.gateway.api.entity.JavaObjectEnt;
import org.knime.gateway.api.entity.JavaObjectEnt.JavaObjectEntBuilder;
import org.knime.gateway.api.entity.JobManagerEnt;
import org.knime.gateway.api.entity.JobManagerEnt.JobManagerEntBuilder;
import org.knime.gateway.api.entity.MetaNodeDialogCompEnt;
import org.knime.gateway.api.entity.MetaNodeDialogCompEnt.MetaNodeDialogCompEntBuilder;
import org.knime.gateway.api.entity.MetaNodeDialogEnt;
import org.knime.gateway.api.entity.MetaNodeDialogEnt.MetaNodeDialogEntBuilder;
import org.knime.gateway.api.entity.MetaPortInfoEnt;
import org.knime.gateway.api.entity.MetaPortInfoEnt.MetaPortInfoEntBuilder;
import org.knime.gateway.api.entity.NativeNodeEnt;
import org.knime.gateway.api.entity.NativeNodeEnt.NativeNodeEntBuilder;
import org.knime.gateway.api.entity.NodeAnnotationEnt;
import org.knime.gateway.api.entity.NodeAnnotationEnt.NodeAnnotationEntBuilder;
import org.knime.gateway.api.entity.NodeEnt;
import org.knime.gateway.api.entity.NodeEnt.NodeTypeEnum;
import org.knime.gateway.api.entity.NodeFactoryKeyEnt;
import org.knime.gateway.api.entity.NodeFactoryKeyEnt.NodeFactoryKeyEntBuilder;
import org.knime.gateway.api.entity.NodeIDEnt;
import org.knime.gateway.api.entity.NodeInPortEnt;
import org.knime.gateway.api.entity.NodeInPortEnt.NodeInPortEntBuilder;
import org.knime.gateway.api.entity.NodeMessageEnt;
import org.knime.gateway.api.entity.NodeMessageEnt.NodeMessageEntBuilder;
import org.knime.gateway.api.entity.NodeOutPortEnt;
import org.knime.gateway.api.entity.NodeOutPortEnt.NodeOutPortEntBuilder;
import org.knime.gateway.api.entity.NodeProgressEnt;
import org.knime.gateway.api.entity.NodeProgressEnt.NodeProgressEntBuilder;
import org.knime.gateway.api.entity.NodeStateEnt;
import org.knime.gateway.api.entity.NodeStateEnt.NodeStateEntBuilder;
import org.knime.gateway.api.entity.NodeUIInfoEnt;
import org.knime.gateway.api.entity.NodeUIInfoEnt.NodeUIInfoEntBuilder;
import org.knime.gateway.api.entity.PortObjectSpecEnt;
import org.knime.gateway.api.entity.PortObjectSpecEnt.PortObjectSpecEntBuilder;
import org.knime.gateway.api.entity.PortTypeEnt;
import org.knime.gateway.api.entity.PortTypeEnt.PortTypeEntBuilder;
import org.knime.gateway.api.entity.StyleRangeEnt;
import org.knime.gateway.api.entity.StyleRangeEnt.FontStyleEnum;
import org.knime.gateway.api.entity.StyleRangeEnt.StyleRangeEntBuilder;
import org.knime.gateway.api.entity.ViewDataEnt;
import org.knime.gateway.api.entity.ViewDataEnt.ViewDataEntBuilder;
import org.knime.gateway.api.entity.WorkflowAnnotationEnt;
import org.knime.gateway.api.entity.WorkflowAnnotationEnt.WorkflowAnnotationEntBuilder;
import org.knime.gateway.api.entity.WorkflowEnt;
import org.knime.gateway.api.entity.WorkflowEnt.WorkflowEntBuilder;
import org.knime.gateway.api.entity.WorkflowNodeEnt;
import org.knime.gateway.api.entity.WorkflowNodeEnt.WorkflowNodeEntBuilder;
import org.knime.gateway.api.entity.WorkflowPartsEnt;
import org.knime.gateway.api.entity.WorkflowPartsEnt.WorkflowPartsEntBuilder;
import org.knime.gateway.api.entity.WorkflowUIInfoEnt;
import org.knime.gateway.api.entity.WorkflowUIInfoEnt.WorkflowUIInfoEntBuilder;
import org.knime.gateway.api.entity.WrappedWorkflowNodeEnt;
import org.knime.gateway.api.entity.WrappedWorkflowNodeEnt.WrappedWorkflowNodeEntBuilder;
import org.knime.gateway.api.entity.XYEnt;
import org.knime.gateway.api.entity.XYEnt.XYEntBuilder;
import org.knime.js.base.node.quickform.QuickFormNodeModel;
import org.knime.js.base.node.quickform.QuickFormRepresentationImpl;

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
                .collect(Collectors.toMap(n -> n.getNodeID().toString(), n -> n));
        Map<String, ConnectionEnt> connections =
            wfm.getConnectionContainers().stream().map(cc -> buildConnectionEnt(cc)).collect(
                Collectors.toMap(c -> new ConnectionIDEnt(c.getDest(), c.getDestPort()).toString(), c -> c));
        Map<String, WorkflowAnnotationEnt> annotations =
            wfm.getWorkflowAnnotations().stream().map(wa -> buildWorkflowAnnotationEnt(wa))
                .collect(Collectors.toMap(wa -> wa.getAnnotationID().toString(), wa -> wa));
        return builder(WorkflowEntBuilder.class)
            .setNodes(nodes)
            .setConnections(connections)
            .setMetaInPortInfos(buildMetaInPortInfoEnts(wfm))
            .setMetaOutPortInfos(buildMetaOutPortInfoEnts(wfm))
            .setWorkflowAnnotations(annotations)
            .setWorkflowUIInfo(buildWorkflowUIInfoEnt(wfm.getEditorUIInformation()))
            .setHasCredentials(!wfm.getCredentialsStore().listNames().isEmpty())
            .setInWizardExecution(wfm.isInWizardExecution())
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
     * Builds a new {@link NodeUIInfoEnt}.
     *
     * @param uiInfo the ui-infor to build the entity from
     * @return the newly created entity
     */
    public static NodeUIInfoEnt buildNodeUIInfoEnt(final NodeUIInformation uiInfo) {
        if(uiInfo == null) {
            return null;
        } else {
            int[] b = uiInfo.getBounds();
            BoundsEnt bounds = builder(BoundsEntBuilder.class)
                    .setX(b[0])
                    .setY(b[1])
                    .setWidth(b[2])
                    .setHeight(b[3]).build();
            assert uiInfo.hasAbsoluteCoordinates();
            return builder(NodeUIInfoEntBuilder.class)
                    .setBounds(bounds)
                    .setDropLocation(uiInfo.isDropLocation())
                    .setSnapToGrid(uiInfo.getSnapToGrid())
                    .setSymbolRelative(uiInfo.isSymbolRelative()).build();
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
        boolean hasWizardPage = SinglePageManager.of(subNode.getParent()).hasWizardPage(subNode.getID());
        return builder(WrappedWorkflowNodeEntBuilder.class).setName(subNode.getName())
                .setNodeID(new NodeIDEnt(subNode.getID()))
                .setNodeMessage(buildNodeMessageEnt(subNode))
                .setNodeType(NodeTypeEnum.valueOf(subNode.getType().toString().toUpperCase()))
                .setUIInfo(buildNodeUIInfoEnt(subNode.getUIInformation()))
                .setDeletable(subNode.isDeletable())
                .setResetable(subNode.isResetable())
                .setNodeState(buildNodeStateEnt((subNode.getNodeContainerState().toString())))
                .setOutPorts(buildNodeOutPortEnts(subNode))
                .setParentNodeID(
                    subNode.getParent() == WorkflowManager.ROOT ? null : new NodeIDEnt(subNode.getParent().getID()))
                .setJobManager(buildJobManagerEnt(jobManager))
                .setNodeAnnotation(buildNodeAnnotationEnt(subNode))
                .setInPorts(buildNodeInPortEnts(subNode))
                .setHasDialog(subNode.hasDialog())
                .setWorkflowIncomingPorts(buildWorkflowIncomingPortEnts(subNode.getWorkflowManager()))
                .setWorkflowOutgoingPorts(buildWorkflowOutgoingPortEnts(subNode.getWorkflowManager()))
                .setRootWorkflowID(rootWorkflowID)
                .setEncrypted(subNode.getWorkflowManager().isEncrypted())
                .setVirtualInNodeID(new NodeIDEnt(subNode.getVirtualInNodeID()))
                .setVirtualOutNodeID(new NodeIDEnt(subNode.getVirtualOutNodeID()))
                .setInactive(subNode.isInactive())
                .setWebViewNames(hasWizardPage ? Arrays.asList(subNode.getName()) : Collections.emptyList())
                .setHasWizardPage(hasWizardPage)
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
        if (!(spec instanceof InactiveBranchPortObjectSpec) && !type.acceptsPortObjectSpec(spec)) {
            throw new IllegalArgumentException("The port type and port object spec are not compatible.");
        }
        ModelContent model = null;
        PortObjectSpecEntBuilder builder = builder(PortObjectSpecEntBuilder.class).setInactive(false).setProblem(false);
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
            Optional<PortObjectSpecSerializer<PortObjectSpec>> specSerializer =
                PortTypeRegistry.getInstance().getSpecSerializer(spec.getClass());
            if (specSerializer.isPresent()) {
                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                try (PortObjectSpecZipOutputStream out = PortUtil.getPortObjectSpecZipOutputStream(bytes)) {
                    specSerializer.get().savePortObjectSpec(spec, out);
                    out.flush();
                    out.close();
                    byte[] encodeBase64 = Base64.encodeBase64(bytes.toByteArray());
                    builder.setRepresentation(new String(encodeBase64));
                } catch (IOException ex) {
                    builder.setProblem(true);
                    builder.setRepresentation("Problem serializing spec: " + ex.getMessage());
                }
            } else {
                builder.setProblem(true);
                builder.setRepresentation("not supported");
            }
        }

        builder.setPortType(buildPortTypeEnt(type));
        builder.setClassName(spec.getClass().getCanonicalName());
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
     * Builds a new {@link ViewDataEnt}.
     *
     * @param wnode the node to get the view data for
     * @return the newly created entity
     * @throws IOException
     * @throws UnsupportedEncodingException
     */
    public static ViewDataEnt buildViewDataEnt(final WizardNode<?, ?> wnode)
        throws UnsupportedEncodingException, IOException {
        return builder(ViewDataEntBuilder.class)
                .setJavascriptObjectID(wnode.getJavascriptObjectID())
                .setViewRepresentation(buildJavaObjectEntFromViewContent(wnode.getViewRepresentation()))
                .setViewValue(buildJavaObjectEntFromViewContent(wnode.getViewValue()))
                .setHideInWizard(wnode.isHideInWizard()).build();
    }

    /**
     * Builds a new {@link MetaNodeDialogEnt}-object from a {@link SubNodeContainer}.
     *
     * @param snc the instance to build from
     * @return a new {@link MetaNodeDialogEnt}-object
     * @throws UnsupportedEncodingException
     * @throws IOException
     * @throws InvalidSettingsException
     */
    @SuppressWarnings("rawtypes")
    public static MetaNodeDialogEnt buildMetaNodeDialogEnt(final SubNodeContainer snc)
        throws UnsupportedEncodingException, IOException, InvalidSettingsException {
        Map<NodeID, QuickFormNodeModel> nodes =
            snc.getWorkflowManager().findNodes(QuickFormNodeModel.class, new NodeModelFilter<QuickFormNodeModel>() {
                @SuppressWarnings("cast")
                @Override
                public boolean include(final QuickFormNodeModel nodeModel) {
                    return nodeModel instanceof QuickFormNodeModel && !nodeModel.isHideInDialog();
                }
            }, false);
        List<MetaNodeDialogCompEnt> comps = new ArrayList<MetaNodeDialogCompEnt>();
        for(Entry<NodeID, QuickFormNodeModel> e : nodes.entrySet()) {
            QuickFormRepresentationImpl rep = e.getValue().getDialogRepresentation();
            JavaObjectEnt representationEnt = buildJavaObjectEntFromViewContent(rep);
            comps.add(builder(MetaNodeDialogCompEntBuilder.class)
                    .setNodeID(new NodeIDEnt(e.getKey()))
                    .setIsHideInDialog(e.getValue().isHideInDialog())
                    .setRepresentation(representationEnt)
                    .setParamName(e.getValue().getParameterName())
                .build());
        }
        return builder(MetaNodeDialogEntBuilder.class)
                .setComponents(comps)
                .build();
    }

    /**
     * Helper to build a {@link WorkflowPartsEnt}-object from a {@link WorkflowCopyContent}-object.
     *
     * @param cc the object to build from
     * @return a new {@link WorkflowPartsEnt}-object
     */
    public static WorkflowPartsEnt buildWorkflowPartsEnt(final WorkflowCopyContent cc) {
        return buildWorkflowPartsEnt(cc.getNodeIDs(), new ConnectionID[0], cc.getAnnotationIDs());
    }

    /**
     * Helper to build a {@link WorkflowPartsEnt}-object from individual id-arrays.
     *
     * @param nodeIDs the node ids to be included
     * @param connectionIDs the connection ids to be included
     * @param annotationIDs the annotation ids to be included
     * @return a new {@link WorkflowPartsEnt}-object
     */
    public static WorkflowPartsEnt buildWorkflowPartsEnt(final NodeID[] nodeIDs, final ConnectionID[] connectionIDs,
        final WorkflowAnnotationID[] annotationIDs) {
        return builder(WorkflowPartsEntBuilder.class)
            .setNodeIDs(
                nodeIDs != null ? Arrays.stream(nodeIDs).map(id -> new NodeIDEnt(id)).collect(Collectors.toList())
                    : Collections.emptyList())
            .setConnectionIDs(connectionIDs != null ? Arrays.stream(connectionIDs)
                .map(id -> new ConnectionIDEnt(id)).collect(Collectors.toList()) : Collections.emptyList())
            .setAnnotationIDs(annotationIDs != null ? Arrays.stream(annotationIDs)
                .map(id -> new AnnotationIDEnt(id)).collect(Collectors.toList()) : Collections.emptyList())
            .build();
    }

    private static JavaObjectEnt buildJavaObjectEntFromViewContent(final WebViewContent vc)
        throws UnsupportedEncodingException, IOException {
        return builder(JavaObjectEntBuilder.class)
                .setClassname(vc.getClass().getCanonicalName())
                .setJsonContent(viewContentToJsonString(vc)).build();
    }

    /**
     * Builds a new {@link DataTableEnt}.
     *
     * @param table the table to create the entity from
     * @param from row index to start from, if larger then the size of the table, it will return an empty table without
     *            any rows, if <code>null</code> it will be set to 0
     * @param size max number of rows to include (if less rows are available, resulting table will contain less), if
     *            <code>null</code> size will the maximum (i.e. the table end)
     * @return a newly created entity
     */
    public static DataTableEnt buildDataTableEnt(final BufferedDataTable table, final Long from, final Integer size) {
        List<String> colNames = Arrays.asList(table.getSpec().getColumnNames());
        List<DataRowEnt> rows;
        long f = from == null ? 0 : from;
        int s = size == null ? (int)table.size() : size;
        if (f >= table.size()) {
            rows = Collections.emptyList();
        } else {
            rows = new ArrayList<DataRowEnt>(s);
            try (CloseableRowIterator it = table.iteratorFailProve()) {
                for (int i = 0; i < f && it.hasNext(); i++) {
                    it.next();
                }
                for (int i = 0; i < s && it.hasNext(); i++) {
                    rows.add(buildDataRowEnt(it.next(), table.getDataTableSpec()));
                }
            }
        }
        return builder(DataTableEntBuilder.class)
                .setNumTotalRows(table.size())
                .setColumnNames(colNames)
                .setRows(rows)
                .build();
    }

    /**
     * Creates a new {@link DataTableEnt} from a {@link DirectAccessTable}-object.
     *
     * @param daTable the direct access table to create the entity from
     * @param from row index to start from, if larger then the size of the table, it will return an empty table without
     *            any rows, if <code>null</code> it will be set to 0
     * @param size max number of rows to include (if less rows are available, resulting table will contain less), if
     *            <code>null</code> size will the maximum (i.e. the table end)
     * @return a newly created entity
     */
    public static DataTableEnt buildDataTableEnt(final DirectAccessTable daTable, final Long from, final Integer size) {
        DataTableSpec spec = daTable.getDataTableSpec();
        List<String> colNames = Arrays.asList(spec.getColumnNames());
        long f = from == null || from < 0 ? 0 : from;
        int s = size == null ? Integer.MAX_VALUE : size;
        long totalRowCount = -1;
        try {
            totalRowCount = daTable.getRowCount();
        } catch (UnknownRowCountException ex1) {
            //
        }

        List<DataRowEnt> rows;
        try {
            rows = daTable.getRows(f, s, null).stream().map(row -> buildDataRowEnt(row, spec))
                .collect(Collectors.toList());
        } catch (IndexOutOfBoundsException | CanceledExecutionException ex) {
            //return an empty table
            rows = Collections.emptyList();
        }
        return builder(DataTableEntBuilder.class).setNumTotalRows(totalRowCount).setColumnNames(colNames).setRows(rows)
            .build();
    }

    private static DataRowEnt buildDataRowEnt(final DataRow row, final DataTableSpec spec) {
        List<DataCellEnt> columns = new ArrayList<DataCellEnt>(row.getNumCells());
        for (int i = 0; i < row.getNumCells(); i++) {
            columns.add(buildDataCellEnt(row.getCell(i), spec.getColumnSpec(i).getType()));
        }
        return builder(DataRowEntBuilder.class)
               .setRowID(row.getKey().getString())
               .setColumns(columns)
               .build();
    }

    private static final Set<DataType> basicTypes =
        new HashSet<DataType>(
            Arrays.asList(new DataType[]{
                DoubleCell.TYPE,
                IntCell.TYPE,
                LongCell.TYPE,
                StringCell.TYPE,
                BooleanCell.TYPE}));

    /**
     * @param cell the cell to build the entity from
     * @param dataTypeFromSpec the datatype as given by the data table spec
     * @return the data cell entity
     */
    public static DataCellEnt buildDataCellEnt(final DataCell cell, final DataType dataTypeFromSpec) {
        if(cell.isMissing()) {
            return builder(DataCellEntBuilder.class)
                    .setMissing(true)
                    .setValueAsString(((MissingValue) cell).getError())
                    .build();
        }
        //cell type is only transfered if it's not the same as the type as given by the data table spec
        String cellType = cell.getType().equals(dataTypeFromSpec) ? null : cell.getClass().getCanonicalName();
        if (basicTypes.contains(cell.getType())) {
            return builder(DataCellEntBuilder.class).setValueAsString(cell.toString()).setType(cellType).build();
        } else if (cell instanceof FileStoreCell) {
            return buildProblemDataCell("FileStoreCells are not supported, yet");
        } else {
            Optional<DataCellSerializer<DataCell>> serializer =
                DataTypeRegistry.getInstance().getSerializer(cell.getClass());
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            try (DataCellObjectOutputStream out = new DataCellObjectOutputStream(bytes)) {
                if(!serializer.isPresent()) {
                    return buildProblemDataCell(
                        "No serializer available for cell of type '" + cell.getType().getName() + "'");
                }
                serializer.get().serialize(cell, out);
                out.flush();
                out.close();
                byte[] encodeBase64 = Base64.encodeBase64(bytes.toByteArray());
                return builder(DataCellEntBuilder.class).setValueAsString(new String(encodeBase64)).setBinary(true)
                    .setType(cellType).build();
            } catch (IOException ex) {
                return buildProblemDataCell("Problem occured while serializing the cell: " + ex.getClass().getName()
                    + " (" + ex.getMessage() + ")");
            }
        }
    }

    private static DataCellEnt buildProblemDataCell(final String problem) {
        return builder(DataCellEntBuilder.class).setProblem(true).setValueAsString(problem).build();
    }

    /**
     * Output stream used for serializing a data cell. Mainly copied from {@link PortObjectRepository}.
     */
    private static final class DataCellObjectOutputStream
        extends ObjectOutputStream implements DataCellDataOutput {

        /** Call super.
         * @param out To delegate
         * @throws IOException If super throws it.
         *
         */
        DataCellObjectOutputStream(
                final OutputStream out) throws IOException {
            super(out);
        }

        /** {@inheritDoc} */
        @Override
        public void writeDataCell(final DataCell cell) throws IOException {
            writeUTF(cell.getClass().getName());
            Optional<DataCellSerializer<DataCell>> cellSerializer =
                    DataTypeRegistry.getInstance().getSerializer(cell.getClass());
            if (cellSerializer.isPresent()) {
                cellSerializer.get().serialize(cell, this);
            } else {
                writeObject(cell);
            }
        }
    }

    /**
     * Turns a webview content into a json string.
     */
    private static String viewContentToJsonString(final WebViewContent webViewContent)
        throws UnsupportedEncodingException, IOException {
        //very ugly, but it's done the same way at other places, too
        //TODO: WebViewContent should have a 'saveToStream(OutputStream)'-method
        //TODO: right now the returning will be json, but what if not anymore?
        return ((ByteArrayOutputStream)webViewContent.saveToStream()).toString("UTF-8");
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
        InteractiveWebViewsResult webViews = nc.getInteractiveWebViews();
        return builder(NativeNodeEntBuilder.class).setName(nc.getName())
            .setNodeID(new NodeIDEnt(nc.getID()))
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
            .setParentNodeID(nc.getParent() == WorkflowManager.ROOT ? null : new NodeIDEnt(nc.getParent().getID()))
            .setRootWorkflowID(rootWorkflowID)
            .setJobManager(buildJobManagerEnt(nc.getJobManager()))
            .setNodeAnnotation(buildNodeAnnotationEnt(nc))
            .setInPorts(buildNodeInPortEnts(nc))
            .setHasDialog(nc.hasDialog())
            .setNodeFactoryKey(buildNodeFactoryKeyEnt(nc))
            .setInactive(nc.isInactive())
            .setWebViewNames(IntStream.range(0, webViews.size())
                .mapToObj(i -> webViews.get(i).getViewName()).collect(Collectors.toList()))
            .setType("NativeNode").build();
    }

    private static NodeFactoryKeyEnt buildNodeFactoryKeyEnt(final NativeNodeContainer nnc) {
        return buildNodeFactoryKeyEnt(nnc.getNode().getFactory(), nnc.getNode().getCopyOfCreationConfig().orElse(null));
    }

    /**
     * Creates a new {@link NodeFactoryKeyEnt} instance from a {@link NativeNodeContainer}.
     *
     * @param factory
     * @param config
     * @return the node factory entity
     */
    public static NodeFactoryKeyEnt buildNodeFactoryKeyEnt(final NodeFactory<NodeModel> factory,
        final ModifiableNodeCreationConfiguration config) {
        NodeFactoryKeyEntBuilder nodeFactoryKeyBuilder = builder(NodeFactoryKeyEntBuilder.class);
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
        nodeFactoryKeyBuilder.setNodeCreationConfigSettings(getNodeCreationConfigSettingsString(config));
        return nodeFactoryKeyBuilder.build();
    }

    private static String getNodeCreationConfigSettingsString(final ModifiableNodeCreationConfiguration config) {
        if (config == null) {
            return null;
        }
        NodeSettings settings = new NodeSettings("node creation config");
        StringWriter sw = new StringWriter();
        config.saveSettingsTo(settings);
        try {
            JSONConfig.writeJSON(settings, sw, WriterConfig.DEFAULT);
        } catch (IOException ex) {
            // should never happen
            throw new RuntimeException("Problem serializing node creation configuration settings as json", ex);
        }
        return sw.toString();
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
        List<XYEnt> bendpoints;
        if (cc.getUIInfo() != null) {
            int[][] allBendpoints = cc.getUIInfo().getAllBendpoints();
            bendpoints = Arrays.stream(allBendpoints).map(a -> {
                return builder(XYEntBuilder.class).setX(a[0]).setY(a[1]).build();
            }).collect(Collectors.toList());
        } else {
            bendpoints = Collections.emptyList();
        }
        return builder(ConnectionEntBuilder.class).setDest(new NodeIDEnt(cc.getDest()))
            .setDestPort(cc.getDestPort())
            .setSource(new NodeIDEnt(cc.getSource())).setSourcePort(cc.getSourcePort())
            .setDeletable(cc.isDeletable()).setType(TypeEnum.valueOf(cc.getType().toString().toUpperCase()))
            .setBendPoints(bendpoints)
            .setFlowVariablePortConnection(cc.isFlowVariablePortConnection()).build();
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
        List<XYEnt> bendpointsEnts;
        if (bendpoints != null) {
            bendpointsEnts = Arrays.stream(bendpoints).map(a -> {
                return builder(XYEntBuilder.class).setX(a[0]).setY(a[1]).build();
            }).collect(Collectors.toList());
        } else {
            bendpointsEnts = Collections.emptyList();
        }
        return builder(ConnectionEntBuilder.class)
                .setSource(new NodeIDEnt(source))
                .setSourcePort(sourcePort)
                .setDest(new NodeIDEnt(dest))
                .setDestPort(destPort)
                .setType(TypeEnum.valueOf(type.toString()))
                .setDeletable(true) //deletable by default
                .setBendPoints(bendpointsEnts).build();
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
                .setAnnotationID(new AnnotationIDEnt(wa.getID()))
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
        if(editorUIInfo == null) {
            return null;
        }
        return builder(WorkflowUIInfoEntBuilder.class)
                .setGridX(editorUIInfo.getGridX())
                .setGridY(editorUIInfo.getGridY())
                .setSnapToGrid(editorUIInfo.getSnapToGrid())
                .setShowGrid(editorUIInfo.getShowGrid())
                .setZoomLevel(new BigDecimal(editorUIInfo.getZoomLevel()))
                .setHasCurvedConnection(editorUIInfo.getHasCurvedConnections())
                .setConnectionLineWidth(editorUIInfo.getConnectionLineWidth())
                .build();
    }
}

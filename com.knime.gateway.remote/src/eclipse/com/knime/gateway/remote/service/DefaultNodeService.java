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
package com.knime.gateway.remote.service;

import static com.knime.gateway.entity.EntityBuilderManager.builder;
import static com.knime.gateway.remote.service.util.DefaultServiceUtil.getNodeContainer;
import static com.knime.gateway.remote.service.util.DefaultServiceUtil.getRootWfmAndNc;
import static com.knime.gateway.remote.service.util.DefaultServiceUtil.getWorkflowManager;
import static com.knime.gateway.remote.service.util.WorkflowUndoStack.getUndoStack;
import static com.knime.gateway.util.EntityBuilderUtil.buildNodeEnt;

import java.io.IOException;
import java.io.StringReader;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.apache.commons.io.IOUtils;
import org.knime.core.data.DirectAccessTable;
import org.knime.core.node.BufferedDataTable;
import org.knime.core.node.DynamicNodeFactory;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeFactory;
import org.knime.core.node.NodeModel;
import org.knime.core.node.NodeSettings;
import org.knime.core.node.config.base.JSONConfig;
import org.knime.core.node.config.base.JSONConfig.WriterConfig;
import org.knime.core.node.extension.InvalidNodeFactoryExtensionException;
import org.knime.core.node.interactive.DefaultReexecutionCallback;
import org.knime.core.node.interactive.ViewContent;
import org.knime.core.node.port.DataTableSpecProvider;
import org.knime.core.node.port.PortObject;
import org.knime.core.node.port.PortObjectSpec;
import org.knime.core.node.port.PortType;
import org.knime.core.node.web.WebViewContent;
import org.knime.core.node.wizard.WizardNode;
import org.knime.core.node.workflow.ConnectionContainer;
import org.knime.core.node.workflow.FlowObjectStack;
import org.knime.core.node.workflow.FlowVariable.Scope;
import org.knime.core.node.workflow.NativeNodeContainer;
import org.knime.core.node.workflow.NodeContainer;
import org.knime.core.node.workflow.NodeID;
import org.knime.core.node.workflow.NodeOutPort;
import org.knime.core.node.workflow.NodeUIInformation;
import org.knime.core.node.workflow.SingleNodeContainer;
import org.knime.core.node.workflow.SubNodeContainer;
import org.knime.core.node.workflow.WorkflowManager;
import org.knime.core.util.Pair;
import org.knime.core.wizard.SubnodeViewableModel;
import org.knime.workbench.repository.RepositoryManager;

import com.knime.gateway.entity.BoundsEnt;
import com.knime.gateway.entity.DataTableEnt;
import com.knime.gateway.entity.FlowVariableEnt;
import com.knime.gateway.entity.JavaObjectEnt;
import com.knime.gateway.entity.MetaNodeDialogEnt;
import com.knime.gateway.entity.NodeEnt;
import com.knime.gateway.entity.NodeFactoryKeyEnt;
import com.knime.gateway.entity.NodeIDEnt;
import com.knime.gateway.entity.NodeSettingsEnt;
import com.knime.gateway.entity.NodeSettingsEnt.NodeSettingsEntBuilder;
import com.knime.gateway.entity.PortObjectSpecEnt;
import com.knime.gateway.entity.ViewDataEnt;
import com.knime.gateway.remote.endpoint.WorkflowProjectManager;
import com.knime.gateway.service.NodeService;
import com.knime.gateway.service.util.ServiceExceptions;
import com.knime.gateway.service.util.ServiceExceptions.ActionNotAllowedException;
import com.knime.gateway.service.util.ServiceExceptions.InvalidRequestException;
import com.knime.gateway.service.util.ServiceExceptions.NodeNotFoundException;
import com.knime.gateway.service.util.ServiceExceptions.NotASubWorkflowException;
import com.knime.gateway.util.EntityBuilderUtil;

/**
 * Default implementation of {@link NodeService} that delegates the operations to knime.core (e.g.
 * {@link WorkflowManager} etc.).
 *
 * @author Martin Horn, University of Konstanz
 */
public class DefaultNodeService implements NodeService {
    private static final DefaultNodeService INSTANCE = new DefaultNodeService();

    private DefaultNodeService() {
        //private constructor since it's a singleton
    }

    /**
     * Returns the singleton instance for this service.
     *
     * @return the singleton instance
     */
    public static DefaultNodeService getInstance() {
        return INSTANCE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setNodeBounds(final UUID rootWorkflowID, final NodeIDEnt nodeID, final BoundsEnt bounds)
        throws NodeNotFoundException {
        Pair<WorkflowManager, NodeContainer> rootWfmAndNc = getRootWfmAndNc(rootWorkflowID, nodeID);
        NodeUIInformation orgInfo = rootWfmAndNc.getSecond().getUIInformation();
        NodeUIInformation information = NodeUIInformation.builder()
            .setNodeLocation(bounds.getX(), bounds.getY(), bounds.getWidth(), bounds.getHeight()).build();
        // undo stack id: workflow root id + workflow id
        //TODO better only keep ids in the closure instead of workflow manager?
        getUndoStack(rootWorkflowID, new NodeIDEnt(nodeID.toNodeID(rootWfmAndNc.getFirst().getID()).getPrefix()))
            .addAndRunOperation(p -> {
                rootWfmAndNc.getSecond().setUIInformation(information);
                rootWfmAndNc.getFirst().setDirty();
            }, p -> {
                rootWfmAndNc.getSecond().setUIInformation(orgInfo);
                rootWfmAndNc.getFirst().setDirty();
            }, rootWfmAndNc);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public NodeSettingsEnt getNodeSettings(final UUID rootWorkflowID, final NodeIDEnt nodeID)
        throws NodeNotFoundException {
        NodeSettings settings = getNodeContainer(rootWorkflowID, nodeID).getNodeSettings();
        return builder(NodeSettingsEntBuilder.class)
            .setJsonContent(JSONConfig.toJSONString(settings, WriterConfig.DEFAULT)).build();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setNodeSettings(final UUID rootWorkflowID, final NodeIDEnt nodeID, final NodeSettingsEnt nodeSettings)
        throws NodeNotFoundException, ServiceExceptions.InvalidSettingsException,
        ServiceExceptions.IllegalStateException {
        NodeContainer nc = getNodeContainer(rootWorkflowID, nodeID);
        WorkflowManager parent = nc.getParent();
        NodeSettings settings = new NodeSettings("settings");
        try {
            JSONConfig.readJSON(settings, new StringReader(nodeSettings.getJsonContent()));
            parent.loadNodeSettings(nc.getID(), settings);
        } catch (InvalidSettingsException | IOException ex) {
            throw new ServiceExceptions.InvalidSettingsException(ex.getMessage());
        } catch (IllegalStateException ex) {
            throw new ServiceExceptions.IllegalStateException(ex.getMessage());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public NodeIDEnt createNode(final UUID rootWorkflowID, final NodeIDEnt parentNodeID, final Integer x,
        final Integer y, final NodeFactoryKeyEnt nodeFactoryKey)
        throws NotASubWorkflowException, NodeNotFoundException, InvalidRequestException {
        WorkflowManager wfm = getWorkflowManager(rootWorkflowID, parentNodeID);
        NodeFactory<NodeModel> nodeFactory;
        try {
            nodeFactory = RepositoryManager.loadNodeFactory(nodeFactoryKey.getClassName());
        } catch (InstantiationException | IllegalAccessException | InvalidNodeFactoryExtensionException
                | InvalidSettingsException ex) {
            throw new NodeNotFoundException("No node found for factory key " + nodeFactoryKey);
        }
        if (nodeFactoryKey.getSettings() != null && !nodeFactoryKey.getSettings().isEmpty()) {
            try {
                NodeSettings settings =
                    JSONConfig.readJSON(new NodeSettings("settings"), new StringReader(nodeFactoryKey.getSettings()));
                nodeFactory.loadAdditionalFactorySettings(settings);
            } catch (IOException | InvalidSettingsException ex) {
                throw new InvalidRequestException("Problem reading factory settings while trying to create node from '"
                    + nodeFactoryKey.getClassName() + "'", ex);
            }
        } else if (nodeFactory instanceof DynamicNodeFactory) {
            //no settings stored with a dynamic node factory (which is the, e.g., with the spark nodes)
            //at least init the node factory in order to have the node description available
            nodeFactory.init();
        }

        return new NodeIDEnt(getUndoStack(rootWorkflowID, parentNodeID).addAndRunOperation(w -> {
            NodeID nodeID = wfm.createAndAddNode(nodeFactory);
            NodeUIInformation info =
                NodeUIInformation.builder().setNodeLocation(x, y, -1, -1).setIsDropLocation(true).build();
            wfm.getNodeContainer(nodeID).setUIInformation(info);
            return nodeID;
        }, (w, r) -> {
            w.removeNode(r);
        }, wfm));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public NodeEnt getNode(final UUID rootWorkflowID, final NodeIDEnt nodeID) throws NodeNotFoundException {
        NodeContainer node = getNodeContainer(rootWorkflowID, nodeID);
        return buildNodeEnt(node, rootWorkflowID);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String changeAndGetNodeState(final UUID rootWorkflowID, final NodeIDEnt nodeId, final String action)
        throws NodeNotFoundException, ActionNotAllowedException {
        WorkflowManager rootWfm = WorkflowProjectManager.openAndCacheWorkflow(rootWorkflowID).orElseThrow(
            () -> new NoSuchElementException("Workflow project for ID \"" + rootWorkflowID + "\" not found."));
        NodeID nodeID;
        WorkflowManager wfm;
        if (nodeId.equals(NodeIDEnt.getRootID())) {
            nodeID = rootWfm.getID();
            wfm = rootWfm.getParent();
        } else {
            nodeID = nodeId.toNodeID(rootWfm.getID());
            try {
                NodeContainer nc = rootWfm.findNodeContainer(nodeID);
                wfm = nc.getParent();
            } catch (IllegalArgumentException e) {
                throw new ServiceExceptions.NodeNotFoundException(e.getMessage(), e);
            }
        }

        if (action == null || action.isEmpty()) {
            //if there is no action (null or empty), do nothing and just return the node's state
        } else if (action.equals("reset")) {
            try {
                wfm.resetAndConfigureNode(nodeID);
            } catch (IllegalStateException e) {
                //thrown when, e.g., there are executing successors
                throw new ServiceExceptions.ActionNotAllowedException(e.getMessage(), e);
            }
        } else if (action.equals("cancel")) {
            wfm.cancelExecution(wfm.getNodeContainer(nodeID));
        } else if (action.equals("execute")) {
            wfm.executeUpToHere(nodeID);
        } else {
            throw new ServiceExceptions.ActionNotAllowedException("Unknown action '" + action + "'");
        }

        //return the node's state
        try {
            NodeContainer nc = wfm.findNodeContainer(nodeID);
            return nc.getNodeContainerState().toString();
        } catch (IllegalArgumentException e) {
            throw new ServiceExceptions.NodeNotFoundException(e.getMessage());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<PortObjectSpecEnt> getInputPortSpecs(final UUID rootWorkflowID, final NodeIDEnt nodeID)
        throws NodeNotFoundException {
        Pair<WorkflowManager, NodeContainer> rootWfmAndNc = getRootWfmAndNc(rootWorkflowID, nodeID);
        WorkflowManager wfm = rootWfmAndNc.getFirst();
        NodeContainer nc = rootWfmAndNc.getSecond();
        List<NodeOutPort> outPorts = IntStream.range(0, nc.getNrInPorts()).mapToObj(i -> {
            ConnectionContainer conn = nc.getParent().getIncomingConnectionFor(nc.getID(), i);
            if (conn != null) {
                NodeOutPort outPort;
                switch (conn.getType()) {
                    case WFMIN:
                        outPort = ((WorkflowManager)wfm.findNodeContainer(conn.getSource()))
                            .getWorkflowIncomingPort(conn.getSourcePort());
                        break;
                    default:
                        outPort = wfm.findNodeContainer(conn.getSource()).getOutPort(conn.getSourcePort());
                        break;
                }
                return outPort;
            } else {
                return null;
            }
        }).collect(Collectors.toList());
        return getPortObjectSpecsAsEntityList(outPorts);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<PortObjectSpecEnt> getOutputPortSpecs(final UUID rootWorkflowID, final NodeIDEnt nodeID)
        throws NodeNotFoundException {
        NodeContainer nodeContainer = getNodeContainer(rootWorkflowID, nodeID);
        List<NodeOutPort> outPorts = IntStream.range(0, nodeContainer.getNrOutPorts())
            .mapToObj(i -> nodeContainer.getOutPort(i)).collect(Collectors.toList());
        return getPortObjectSpecsAsEntityList(outPorts);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DataTableEnt getOutputDataTable(final UUID rootWorkflowID, final NodeIDEnt nodeID, final Integer portIdx,
        final Long from, final Integer size) throws NodeNotFoundException, InvalidRequestException {
        NodeContainer nc = getNodeContainer(rootWorkflowID, nodeID);
        if (portIdx >= nc.getNrOutPorts()) {
            throw new InvalidRequestException("No port at index " + portIdx);
        }
        PortObject portObject = nc.getOutPort(portIdx).getPortObject();
        if (portObject instanceof BufferedDataTable) {
            return EntityBuilderUtil.buildDataTableEnt((BufferedDataTable)portObject, from, size);
        } else if (portObject instanceof DirectAccessTable && portObject.getSpec() instanceof DataTableSpecProvider) {
            return EntityBuilderUtil.buildDataTableEnt((DirectAccessTable)portObject, from, size);
        } else {
            throw new InvalidRequestException("Not a table at port index " + portIdx);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<FlowVariableEnt> getInputFlowVariables(final UUID rootWorkflowID, final NodeIDEnt nodeID)
        throws NodeNotFoundException {
        NodeContainer nodeContainer = getNodeContainer(rootWorkflowID, nodeID);
        return getFlowVariableEntListFromFlowObjectStack(nodeContainer.getFlowObjectStack());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<FlowVariableEnt> getOutputFlowVariables(final UUID rootWorkflowID, final NodeIDEnt nodeID)
        throws NodeNotFoundException {
        NodeContainer nodeContainer = getNodeContainer(rootWorkflowID, nodeID);
        if (nodeContainer instanceof SingleNodeContainer) {
            return getFlowVariableEntListFromFlowObjectStack(
                ((SingleNodeContainer)nodeContainer).createOutFlowObjectStack());
        } else {
            return getFlowVariableEntListFromFlowObjectStack(nodeContainer.getFlowObjectStack());
        }
    }

    private static final List<FlowVariableEnt> getFlowVariableEntListFromFlowObjectStack(final FlowObjectStack stack) {
        if (stack == null) {
            return Collections.emptyList();
        }
        return stack.getAvailableFlowVariables().values().stream().filter(fv -> fv.getScope().equals(Scope.Flow))
            .map(fv -> EntityBuilderUtil.buildFlowVariableEnt(fv)).collect(Collectors.toList());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ViewDataEnt getViewData(final UUID rootWorkflowID, final NodeIDEnt nodeID)
        throws NodeNotFoundException, InvalidRequestException {
        NodeContainer nc = getNodeContainer(rootWorkflowID, nodeID);
        if (nc instanceof NativeNodeContainer && ((NativeNodeContainer)nc).getNodeModel() instanceof WizardNode) {
            NativeNodeContainer nnc = (NativeNodeContainer)nc;
            try {
                return EntityBuilderUtil.buildViewDataEnt((WizardNode<?, ?>)nnc.getNodeModel());
            } catch (IOException ex) {
                //should not happen, that's why it's just a runtime exception
                throw new IllegalStateException("Views data cannot be accessed.", ex);
            }
        } else if (nc instanceof SubNodeContainer) {
            try {
                return EntityBuilderUtil.buildViewDataEnt(new SubnodeViewableModel((SubNodeContainer)nc, nc.getName()));
            } catch (IOException ex) {
                //should not happen, that's why it's just a runtime exception
                throw new IllegalStateException("Views data cannot be accessed.", ex);
            }
        } else {
            throw new InvalidRequestException("Node doesn't provide view data.");
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setViewValue(final UUID rootWorkflowID, final NodeIDEnt nodeID, final Boolean useAsDefault,
        final JavaObjectEnt viewValue) throws NodeNotFoundException, InvalidRequestException {
        Pair<WorkflowManager, NodeContainer> rootWfmAndNc = getRootWfmAndNc(rootWorkflowID, nodeID);
        NodeContainer nc = rootWfmAndNc.getSecond();
        if (nc instanceof NativeNodeContainer && ((NativeNodeContainer)nc).getNodeModel() instanceof WizardNode) {
            NativeNodeContainer nnc = (NativeNodeContainer)nc;
            WizardNode<?, ?> wn = (WizardNode<?, ?>)nnc.getNodeModel();
            ViewContent vc = readWebViewContentFromJsonString(viewValue.getJsonContent(), wn.createEmptyViewValue());
            nnc.getParent().reExecuteNode(nnc.getID(), vc, useAsDefault, new DefaultReexecutionCallback());
        } else {
            throw new InvalidRequestException("Node doesn't provide a view.");
        }
    }

    private static final WebViewContent readWebViewContentFromJsonString(final String s,
        final WebViewContent webViewContent) {
        try {
            webViewContent.loadFromStream(IOUtils.toInputStream(s, Charset.forName("UTF-8")));
        } catch (IOException ex) {
            //should not happen
            throw new IllegalStateException("Problem serializing web view.", ex);
        }
        return webViewContent;
    }

    private static List<PortObjectSpecEnt> getPortObjectSpecsAsEntityList(final List<NodeOutPort> outPorts) {
        return outPorts.stream().map(port -> {
            if (port == null) {
                //can happen in case of an optional port
                return null;
            }
            PortType type = port.getPortType();
            PortObjectSpec spec = port.getPortObjectSpec();
            if (spec == null) {
                //can happen when spec is not known, yet
                return null;
            }
            return EntityBuilderUtil.buildPortObjectSpecEnt(type, spec);
        }).collect(Collectors.toList());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public MetaNodeDialogEnt getWMetaNodeDialog(final UUID rootWorkflowID, final NodeIDEnt nodeID)
        throws NodeNotFoundException, InvalidRequestException {
        NodeContainer nc = getNodeContainer(rootWorkflowID, nodeID);
        if (nc instanceof SubNodeContainer) {
            SubNodeContainer snc = (SubNodeContainer)nc;
            try {
                return EntityBuilderUtil.buildMetaNodeDialogEnt(snc);
            } catch (IOException | InvalidSettingsException ex) {
                throw new IllegalStateException("Data for metanode dialog cannot be accessed.", ex);
            }
        } else {
            throw new InvalidRequestException("The node the dialog is requested for is not a component!");
        }
    }
}

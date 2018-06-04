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
import static com.knime.gateway.remote.util.EntityBuilderUtil.buildNodeEnt;

import java.io.IOException;
import java.io.StringReader;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeSettings;
import org.knime.core.node.config.base.JSONConfig;
import org.knime.core.node.config.base.JSONConfig.WriterConfig;
import org.knime.core.node.port.PortObjectSpec;
import org.knime.core.node.port.PortType;
import org.knime.core.node.workflow.ConnectionContainer;
import org.knime.core.node.workflow.FlowVariable;
import org.knime.core.node.workflow.FlowVariable.Scope;
import org.knime.core.node.workflow.NodeContainer;
import org.knime.core.node.workflow.NodeID;
import org.knime.core.node.workflow.NodeID.NodeIDSuffix;
import org.knime.core.node.workflow.NodeOutPort;
import org.knime.core.node.workflow.WorkflowManager;
import org.knime.core.util.Pair;

import com.knime.gateway.remote.endpoint.WorkflowProjectManager;
import com.knime.gateway.remote.util.EntityBuilderUtil;
import com.knime.gateway.util.DefaultEntUtil;
import com.knime.gateway.v0.entity.FlowVariableEnt;
import com.knime.gateway.v0.entity.NodeEnt;
import com.knime.gateway.v0.entity.NodeSettingsEnt;
import com.knime.gateway.v0.entity.NodeSettingsEnt.NodeSettingsEntBuilder;
import com.knime.gateway.v0.entity.PortObjectSpecEnt;
import com.knime.gateway.v0.entity.WebViewEnt;
import com.knime.gateway.v0.service.NodeService;
import com.knime.gateway.v0.service.util.ServiceExceptions;
import com.knime.gateway.v0.service.util.ServiceExceptions.ActionNotAllowedException;
import com.knime.gateway.v0.service.util.ServiceExceptions.NodeNotFoundException;
import com.knime.gateway.v0.service.util.ServiceExceptions.NotSupportedException;

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
    public NodeSettingsEnt getNodeSettings(final UUID rootWorkflowID, final String nodeID)
        throws NodeNotFoundException {
        NodeSettings settings = getNodeContainer(rootWorkflowID, nodeID).getNodeSettings();
        return builder(NodeSettingsEntBuilder.class).setContent(JSONConfig.toJSONString(settings, WriterConfig.PRETTY))
            .build();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setNodeSettings(final UUID rootWorkflowID, final String nodeID, final NodeSettingsEnt nodeSettings)
        throws NodeNotFoundException, ServiceExceptions.InvalidSettingsException,
        ServiceExceptions.IllegalStateException {
        NodeContainer nc = getNodeContainer(rootWorkflowID, nodeID);
        WorkflowManager parent = nc.getParent();
        NodeSettings settings = new NodeSettings("settings");
        try {
            JSONConfig.readJSON(settings, new StringReader(nodeSettings.getContent()));
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
    public NodeEnt getNode(final UUID rootWorkflowID, final String nodeID) throws NodeNotFoundException {
        NodeContainer node = getNodeContainer(rootWorkflowID, nodeID);
        return buildNodeEnt(node, rootWorkflowID);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public NodeEnt getRootNode(final UUID rootWorkflowID) {
        return buildNodeEnt(getRootWorkflowManager(rootWorkflowID), rootWorkflowID);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String changeAndGetNodeState(final UUID rootWorkflowID, final String nodeId, final String action)
        throws NodeNotFoundException, ActionNotAllowedException {
        WorkflowManager rootWfm = WorkflowProjectManager.openAndCacheWorkflow(rootWorkflowID).orElseThrow(
            () -> new NoSuchElementException("Workflow project for ID \"" + rootWorkflowID + "\" not found."));
        NodeID nodeID;
        WorkflowManager wfm;
        if (nodeId.equals(DefaultEntUtil.ROOT_NODE_ID)) {
            nodeID = rootWfm.getID();
            wfm = rootWfm.getParent();
        } else {
            nodeID = NodeIDSuffix.fromString(nodeId).prependParent(rootWfm.getID());
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
    public List<PortObjectSpecEnt> getInputPortSpecs(final UUID rootWorkflowID, final String nodeID)
        throws NodeNotFoundException, NotSupportedException {
        Pair<WorkflowManager, NodeContainer> rootWfmAndNc = getRootWfmAndNc(rootWorkflowID, nodeID);
        WorkflowManager wfm = rootWfmAndNc.getFirst();
        NodeContainer nc = rootWfmAndNc.getSecond();
        return getPortObjectSpecsAsEntityList(IntStream.range(0, nc.getNrInPorts()).mapToObj(i -> {
            ConnectionContainer conn = nc.getParent().getIncomingConnectionFor(nc.getID(), i);
            if (conn != null) {
                NodeOutPort outPort = wfm.findNodeContainer(conn.getSource()).getOutPort(conn.getSourcePort());
                return Pair.create(outPort.getPortType(), outPort.getPortObjectSpec());
            } else {
                return null;
            }
        }));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<PortObjectSpecEnt> getOutputPortSpecs(final UUID rootWorkflowID, final String nodeID)
        throws NodeNotFoundException, NotSupportedException {
        NodeContainer nodeContainer = getNodeContainer(rootWorkflowID, nodeID);
        return getPortObjectSpecsAsEntityList(IntStream.range(0, nodeContainer.getNrOutPorts()).mapToObj(i -> {
            return Pair.create(nodeContainer.getOutPort(i).getPortType(),
                nodeContainer.getOutPort(i).getPortObjectSpec());
        }));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<FlowVariableEnt> getInputFlowVariables(final UUID rootWorkflowID, final String nodeID)
        throws NodeNotFoundException {
        NodeContainer nodeContainer = getNodeContainer(rootWorkflowID, nodeID);
        Map<String, FlowVariable> flowObjectStack = nodeContainer.getFlowObjectStack().getAvailableFlowVariables();
        return flowObjectStack.values().stream().filter(fv -> fv.getScope().equals(Scope.Flow))
            .map(fv -> EntityBuilderUtil.buildFlowVariableEnt(fv)).collect(Collectors.toList());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public WebViewEnt getWebView(final UUID rootWorkflowID, final String nodeID, final Integer index) throws NodeNotFoundException {
        NodeContainer nc = getNodeContainer(rootWorkflowID, nodeID);
        return EntityBuilderUtil.buildWebViewEnt(nc.getInteractiveWebViews(), index);
    }

    private static List<PortObjectSpecEnt> getPortObjectSpecsAsEntityList(
        final Stream<Pair<PortType, PortObjectSpec>> specs) throws NotSupportedException {
        AtomicReference<NotSupportedException> exception = new AtomicReference<NotSupportedException>();
        List<PortObjectSpecEnt> res = specs.map(port -> {
            if (port == null) {
                //can happen in case of an optional port
                return null;
            }
            if (exception.get() != null) {
                return null;
            }
            PortType type = port.getFirst();
            PortObjectSpec spec = port.getSecond();
            if (spec == null) {
                //can happen when spec is not known, yet
                return null;
            }
            PortObjectSpecEnt ent = EntityBuilderUtil.buildPortObjectSpecEnt(type, spec);
            if (ent == null) {
                exception.set(new ServiceExceptions.NotSupportedException(
                    "Port object spec of type '" + type.getName() + "' not supported in remote view."));
                return null;
            } else {
                return ent;

            }
        }).collect(Collectors.toList());
        if (exception.get() == null) {
            return res;
        } else {
            //couldn't think of a better way to transfer a thrown exception from within a lambda-expression
            throw exception.get();
        }
    }

    private static NodeContainer getNodeContainer(final UUID rootWorkflowID, final String nodeID)
        throws NodeNotFoundException {
        WorkflowManager wfm = getRootWorkflowManager(rootWorkflowID);
        if (nodeID.equals(DefaultEntUtil.ROOT_NODE_ID)) {
            return wfm;
        } else {
            try {
                return wfm.findNodeContainer(NodeIDSuffix.fromString(nodeID).prependParent(wfm.getID()));
            } catch (IllegalArgumentException e) {
                throw new ServiceExceptions.NodeNotFoundException(e.getMessage());
            }
        }
    }

    private static WorkflowManager getRootWorkflowManager(final UUID rootWorkflowID) {
        return WorkflowProjectManager.openAndCacheWorkflow(rootWorkflowID).orElseThrow(
            () -> new NoSuchElementException("Workflow project for ID \"" + rootWorkflowID + "\" not found."));
    }

    private static Pair<WorkflowManager, NodeContainer> getRootWfmAndNc(final UUID rootWorkflowID, final String nodeID)
        throws NodeNotFoundException {
        return Pair.create(getRootWorkflowManager(rootWorkflowID), getNodeContainer(rootWorkflowID, nodeID));
    }
}

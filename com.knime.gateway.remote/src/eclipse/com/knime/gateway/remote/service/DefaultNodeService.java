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

import static com.knime.gateway.remote.util.EntityBuilderUtil.buildNodeEnt;

import java.util.NoSuchElementException;
import java.util.UUID;

import org.knime.core.node.NodeSettings;
import org.knime.core.node.config.base.JSONConfig;
import org.knime.core.node.config.base.JSONConfig.WriterConfig;
import org.knime.core.node.workflow.NodeContainer;
import org.knime.core.node.workflow.NodeContainerState;
import org.knime.core.node.workflow.NodeID;
import org.knime.core.node.workflow.NodeID.NodeIDSuffix;
import org.knime.core.node.workflow.WorkflowManager;

import com.knime.gateway.remote.endpoint.WorkflowProjectManager;
import com.knime.gateway.service.ServiceException;
import com.knime.gateway.util.DefaultEntUtil;
import com.knime.gateway.v0.entity.NodeEnt;
import com.knime.gateway.v0.service.NodeService;
import com.knime.gateway.v0.service.util.ServiceExceptions;
import com.knime.gateway.v0.service.util.ServiceExceptions.NodeNotFoundException;
import com.knime.gateway.v0.service.util.ServiceExceptions.NotAllowedException;

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
    public String getNodeSettings(final UUID rootWorkflowID, final String nodeID) throws NodeNotFoundException {
        WorkflowManager wfm = WorkflowProjectManager.openAndCacheWorkflow(rootWorkflowID).orElseThrow(
            () -> new NoSuchElementException("Workflow project for ID \"" + rootWorkflowID + "\" not found."));
        NodeContainer nodeContainer;
        try{
            nodeContainer = wfm.findNodeContainer(NodeIDSuffix.fromString(nodeID).prependParent(wfm.getID()));
        } catch(IllegalArgumentException e) {
            throw new ServiceExceptions.NodeNotFoundException(e.getMessage());
        }
        NodeSettings settings = nodeContainer.getNodeSettings();
        return JSONConfig.toJSONString(settings, WriterConfig.PRETTY);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public NodeEnt getNode(final UUID rootWorkflowID, final String nodeID) throws NodeNotFoundException {
        //get the right IWorkflowManager for the given id and create a WorkflowEnt from it
        WorkflowManager wfm = WorkflowProjectManager.openAndCacheWorkflow(rootWorkflowID).orElseThrow(
            () -> new NoSuchElementException("Workflow project for ID \"" + rootWorkflowID + "\" not found."));
        NodeContainer node;
        try {
            node = wfm.findNodeContainer(NodeIDSuffix.fromString(nodeID).prependParent(wfm.getID()));
        } catch (IllegalArgumentException e) {
            throw new ServiceExceptions.NodeNotFoundException(e.getMessage());
        }
        return buildNodeEnt(node, rootWorkflowID);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public NodeEnt getRootNode(final UUID rootWorkflowID) {
        return buildNodeEnt(
            WorkflowProjectManager.openAndCacheWorkflow(rootWorkflowID).orElseThrow(
                () -> new NoSuchElementException("Workflow project for ID \"" + rootWorkflowID + "\" not found.")),
            rootWorkflowID);
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public String changeAndGetNodeState(final UUID rootWorkflowID, final String nodeId)
        throws NodeNotFoundException, NotAllowedException {
        WorkflowManager rootWfm = WorkflowProjectManager.openAndCacheWorkflow(rootWorkflowID).orElseThrow(
            () -> new NoSuchElementException("Workflow project for ID \"" + rootWorkflowID + "\" not found."));
        WorkflowManager wfm;
        NodeContainer nc;
        if (nodeId.equals(DefaultEntUtil.ROOT_NODE_ID)) {
            nc = rootWfm;
            wfm = rootWfm.getParent();
        } else {
            NodeID nodeID = NodeIDSuffix.fromString(nodeId).prependParent(rootWfm.getID());
            try {
                nc = rootWfm.findNodeContainer(nodeID);
                wfm = nc.getParent();
            } catch (IllegalArgumentException e) {
                throw new ServiceExceptions.NodeNotFoundException(e.getMessage(), e);
            }
        }

        NodeContainerState ncs = nc.getNodeContainerState();
        if (ncs.isExecuted()) {
            try {
                wfm.resetAndConfigureNode(nc.getID());
            } catch (IllegalStateException e) {
                //thrown when, e.g., there are executing successors
                throw new ServiceExceptions.NotAllowedException(e.getMessage(), e);
            }
        } else if (ncs.isExecutionInProgress()) {
            wfm.cancelExecution(wfm.getNodeContainer(nc.getID()));
        } else if (ncs.isConfigured() || ncs.isIdle()) {
            wfm.executeUpToHere(nc.getID());
        } else {
            //should not happen!!
            throw new ServiceException("Invalid node state!");
        }

        //return the node's state
        try {
            return ncs.toString();
        } catch (IllegalArgumentException e) {
            throw new ServiceExceptions.NodeNotFoundException(e.getMessage());
        }
    }
}

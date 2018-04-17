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
import org.knime.core.node.workflow.NodeID.NodeIDSuffix;
import org.knime.core.node.workflow.WorkflowManager;

import com.knime.gateway.remote.endpoint.WorkflowProjectManager;
import com.knime.gateway.v0.entity.NodeEnt;
import com.knime.gateway.v0.service.NodeService;
import com.knime.gateway.v0.service.util.ServiceExceptions;
import com.knime.gateway.v0.service.util.ServiceExceptions.NodeNotFoundException;

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

}

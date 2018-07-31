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
package com.knime.gateway.remote.service.util;

import java.util.NoSuchElementException;
import java.util.UUID;

import org.knime.core.node.workflow.NodeContainer;
import org.knime.core.node.workflow.NodeID.NodeIDSuffix;
import org.knime.core.node.workflow.SubNodeContainer;
import org.knime.core.node.workflow.WorkflowManager;
import org.knime.core.util.Pair;

import com.knime.gateway.remote.endpoint.WorkflowProjectManager;
import com.knime.gateway.util.DefaultEntUtil;
import com.knime.gateway.v0.service.util.ServiceExceptions;
import com.knime.gateway.v0.service.util.ServiceExceptions.NodeNotFoundException;
import com.knime.gateway.v0.service.util.ServiceExceptions.NotASubWorkflowException;

/**
 * Helper methods useful for the default service implementations.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
public class DefaultServiceUtil {

    private DefaultServiceUtil() {
        //utility class
    }

    /**
     * Gets the node container (including (sub-)workflows) for the id-pair of root workflow- and node ID.
     *
     * @param rootWorkflowID id of the root workflow
     * @param nodeID the node id to get the node/workflow for - if {@link DefaultEntUtil#ROOT_NODE_ID} the root workflow
     *            itself will be returned
     * @return the {@link NodeContainer} instance
     * @throws NodeNotFoundException if there is no node for the given node id
     * @throws NoSuchElementException if there is no root workflow for the given root workflow id
     */
    public static NodeContainer getNodeContainer(final UUID rootWorkflowID, final String nodeID)
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

    /**
     * Gets the workflow manager from the {@link WorkflowProjectManager} for a corresponding root workflow id.
     *
     * @param rootWorkflowID the id to get the wfm for
     * @return the {@link WorkflowManager} instance
     * @throws NoSuchElementException if there is no workflow manager for the id registered
     */
    public static WorkflowManager getRootWorkflowManager(final UUID rootWorkflowID) {
        return WorkflowProjectManager.openAndCacheWorkflow(rootWorkflowID).orElseThrow(
            () -> new NoSuchElementException("Workflow project for ID \"" + rootWorkflowID + "\" not found."));
    }

    /**
     * Gets the (sub-)workflow manager for the given root workflow id and node id.
     *
     * @param rootWorkflowID the root workflow id
     * @param nodeID the subnode's or metanode's node id
     * @return the {@link WorkflowManager}-instance
     * @throws NotASubWorkflowException if the node id doesn't reference a workflow (i.e. a sub- or metanode)
     * @throws NodeNotFoundException if there is no node for the given node id
     */
    public static WorkflowManager getSubWorkflowManager(final UUID rootWorkflowID, final String nodeID)
        throws NotASubWorkflowException, NodeNotFoundException {
        NodeContainer nodeContainer;
        if (nodeID == null) {
            nodeContainer = getRootWorkflowManager(rootWorkflowID);
        } else {
            nodeContainer = getNodeContainer(rootWorkflowID, nodeID);
        }
        if (nodeContainer instanceof SubNodeContainer) {
            return ((SubNodeContainer)nodeContainer).getWorkflowManager();
        } else if (nodeContainer instanceof WorkflowManager) {
            return (WorkflowManager)nodeContainer;
        } else {
            throw new ServiceExceptions.NotASubWorkflowException(
                "The node id '" + nodeID + "' doesn't reference a sub workflow.");
        }
   }

    /**
     * Gets the root workflow manager and the contained node container at the same time (see
     * {@link #getNodeContainer(UUID, String)} and {@link #getRootWorkflowManager(UUID)}).
     *
     * @param rootWorkflowID the id of the root workflow
     * @param nodeID the id of the node requested
     * @return a pair of {@link WorkflowManager} and {@link NodeContainer} instances
     * @throws NodeNotFoundException if there is no node for the given node id
     * @throws NoSuchElementException if there is no root workflow for the given root workflow id
     */
    public static Pair<WorkflowManager, NodeContainer> getRootWfmAndNc(final UUID rootWorkflowID, final String nodeID)
        throws NodeNotFoundException {
        return Pair.create(getRootWorkflowManager(rootWorkflowID), getNodeContainer(rootWorkflowID, nodeID));
    }
}

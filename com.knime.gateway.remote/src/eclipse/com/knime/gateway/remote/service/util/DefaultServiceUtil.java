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

import org.knime.core.node.workflow.ConnectionID;
import org.knime.core.node.workflow.NodeContainer;
import org.knime.core.node.workflow.NodeID;
import org.knime.core.node.workflow.NodeID.NodeIDSuffix;
import org.knime.core.node.workflow.SubNodeContainer;
import org.knime.core.node.workflow.WorkflowAnnotationID;
import org.knime.core.node.workflow.WorkflowManager;
import org.knime.core.util.Pair;

import com.knime.gateway.remote.endpoint.WorkflowProject;
import com.knime.gateway.remote.endpoint.WorkflowProjectManager;
import com.knime.gateway.service.util.ServiceExceptions;
import com.knime.gateway.service.util.ServiceExceptions.NodeNotFoundException;
import com.knime.gateway.service.util.ServiceExceptions.NotASubWorkflowException;
import com.knime.gateway.util.EntityUtil;

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
     * @param nodeID the node id to get the node/workflow for - if {@link EntityUtil#ROOT_NODE_ID} the root workflow
     *            itself will be returned
     * @return the {@link NodeContainer} instance
     * @throws NodeNotFoundException if there is no node for the given node id
     * @throws NoSuchElementException if there is no root workflow for the given root workflow id
     */
    public static NodeContainer getNodeContainer(final UUID rootWorkflowID, final String nodeID)
        throws NodeNotFoundException {
        WorkflowManager wfm = getRootWorkflowManager(rootWorkflowID);
        if (nodeID.equals(EntityUtil.ROOT_NODE_ID)) {
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
    public static WorkflowManager getRootWorkflowManager(final UUID rootWorkflowID) throws NoSuchElementException {
        return WorkflowProjectManager.openAndCacheWorkflow(rootWorkflowID).orElseThrow(
            () -> new NoSuchElementException("Workflow project for ID \"" + rootWorkflowID + "\" not found."));
    }

    /**
     * Gets the {@link WorkflowProject} from the {@link WorkflowProjectManager} for a corresponding workflow project id.
     *
     * @param workflowProjectID the id to get the project for
     * @return the {@link WorkflowProject} instance
     * @throws NoSuchElementException if there is no workflow project for the id registered
     */
    public static WorkflowProject getWorkflowProject(final UUID workflowProjectID) throws NoSuchElementException {
        return WorkflowProjectManager.getWorkflowProject(workflowProjectID).orElseThrow(
            () -> new NoSuchElementException("Workflow project for ID \"" + workflowProjectID + "\" not found."));
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
        if (nodeID == null || nodeID.equals(EntityUtil.ROOT_NODE_ID)) {
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

    /**
     * Converts a string representation of a node id (as provided by gateway entities) to a {@link NodeID} instance.
     *
     * @param rootWorkflowID id of the workflow the node belongs to
     * @param nodeID the node id (without the root workflow node id)
     *
     * @return the {@link NodeID} instance
     */
    public static NodeID stringToNodeID(final UUID rootWorkflowID, final String nodeID) {
        return EntityUtil.stringToNodeID(getRootWorkflowManager(rootWorkflowID).getID().toString(), nodeID);
    }

    /**
     * Converts/parses a string representation of a annotation id (as provided by gateway entities) to a
     * {@link WorkflowAnnotationID}-instance.
     *
     * @param rootWorkflowID id of the root(!) workflow the annotations belongs to
     * @param annotationID the annotation id to parse (without the root workflow node id)
     * @return the {@link WorkflowAnnotationID} instance
     */
    public static WorkflowAnnotationID stringToAnnotationID(final UUID rootWorkflowID, final String annotationID) {
        String[] split = annotationID.split("_");
        NodeID nodeID = stringToNodeID(rootWorkflowID, split[0]);
        return new WorkflowAnnotationID(nodeID, Integer.valueOf(split[1]));
    }

    /**
     * Converts a string representation of a connection id (as provided by gateway entities) to a {@link ConnectionID}
     * instance.
     *
     * @param rootWorkflowID id of the workflow the connection belongs to
     *
     * @param s the string representation to convert
     * @return the {@link ConnectionID} instance
     */
    public static ConnectionID stringToConnectionID(final UUID rootWorkflowID, final String s) {
        if (!s.contains("_")) {
            throw new IllegalArgumentException("Unable to parse connection id from string.");
        }
        String[] split = s.split("_");
        return new ConnectionID(stringToNodeID(rootWorkflowID, split[0]), Integer.valueOf(split[1]));
    }
}

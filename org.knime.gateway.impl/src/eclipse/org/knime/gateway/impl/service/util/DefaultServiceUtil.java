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
package org.knime.gateway.impl.service.util;

import java.util.Arrays;
import java.util.NoSuchElementException;

import org.apache.commons.lang3.StringUtils;
import org.knime.core.node.workflow.ConnectionID;
import org.knime.core.node.workflow.NodeContainer;
import org.knime.core.node.workflow.NodeID;
import org.knime.core.node.workflow.SubNodeContainer;
import org.knime.core.node.workflow.WorkflowAnnotationID;
import org.knime.core.node.workflow.WorkflowManager;
import org.knime.core.util.Pair;
import org.knime.gateway.api.entity.AnnotationIDEnt;
import org.knime.gateway.api.entity.ConnectionIDEnt;
import org.knime.gateway.api.entity.NodeIDEnt;
import org.knime.gateway.impl.project.WorkflowProject;
import org.knime.gateway.impl.project.WorkflowProjectManager;

/**
 * Helper methods useful for the default service implementations (shared between different api implementations, i.e.
 * java-ui, web-ui and webportal).
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
public final class DefaultServiceUtil {

    private DefaultServiceUtil() {
        //utility class
    }

    /**
     * Gets the node container (including (sub-)workflows) for the id-pair of root workflow- and node ID.
     *
     * @param rootWorkflowID id of the root workflow
     * @param nodeID the node id to get the node/workflow for - if {@link NodeIDEnt#getRootID()} the root workflow
     *            itself will be returned
     * @return the {@link NodeContainer} instance
     * @throws IllegalArgumentException if there is no node for the given node id
     * @throws NoSuchElementException if there is no root workflow for the given root workflow id
     */
    public static NodeContainer getNodeContainer(final String rootWorkflowID, final NodeIDEnt nodeID) {
        WorkflowManager wfm = getRootWorkflowManager(rootWorkflowID);
        if (nodeID.equals(NodeIDEnt.getRootID())) {
            return wfm;
        } else {
            return wfm.findNodeContainer(nodeID.toNodeID(wfm.getID()));
        }
    }

    /**
     * Gets the workflow manager from the {@link WorkflowProjectManager} for a corresponding root workflow id.
     *
     * @param rootWorkflowID the id to get the wfm for
     * @return the {@link WorkflowManager} instance
     * @throws NoSuchElementException if there is no workflow manager for the id registered
     */
    public static WorkflowManager getRootWorkflowManager(final String rootWorkflowID) {
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
    public static WorkflowProject getWorkflowProject(final String workflowProjectID) {
        return WorkflowProjectManager.getWorkflowProject(workflowProjectID).orElseThrow(
            () -> new NoSuchElementException("Workflow project for ID \"" + workflowProjectID + "\" not found."));
    }

    /**
     * Gets the (sub-)workflow manager for the given root workflow id and node id.
     *
     * @param rootWorkflowID the root workflow id
     * @param nodeID the subnode's or metanode's node id
     * @return the {@link WorkflowManager}-instance
     * @throws NoSuchElementException if there is no root workflow for the given root workflow id
     * @throws IllegalArgumentException if there is no node for the given node id
     * @throws IllegalStateException if the given node id doesn't reference a sub workflow (i.e. component or metanode)
     *             or the workflow is encrypted
     */
    public static WorkflowManager getWorkflowManager(final String rootWorkflowID, final NodeIDEnt nodeID) {
        NodeContainer nodeContainer;
        if (nodeID == null || nodeID.equals(NodeIDEnt.getRootID())) {
            nodeContainer = getRootWorkflowManager(rootWorkflowID);
        } else {
            nodeContainer = getNodeContainer(rootWorkflowID, nodeID);
        }
        WorkflowManager wfm;
        if (nodeContainer instanceof SubNodeContainer) {
            wfm = ((SubNodeContainer)nodeContainer).getWorkflowManager();
        } else if (nodeContainer instanceof WorkflowManager) {
            wfm = (WorkflowManager)nodeContainer;
        } else {
            throw new IllegalStateException("The node id '" + nodeID + "' doesn't reference a sub workflow.");
        }
        if (wfm.isEncrypted()) {
            throw new IllegalStateException("Workflow is encrypted and cannot be accessed.");
        }
        return wfm;
    }

    /**
     * Gets the root workflow manager and the contained node container at the same time (see
     * {@link #getNodeContainer(String, NodeIDEnt)} and {@link #getRootWorkflowManager(String)}).
     *
     * @param rootWorkflowID the id of the root workflow
     * @param nodeID the id of the node requested
     * @return a pair of {@link WorkflowManager} and {@link NodeContainer} instances
     * @throws IllegalArgumentException if there is no node for the given node id
     * @throws NoSuchElementException if there is no root workflow for the given root workflow id
     */
    public static Pair<WorkflowManager, NodeContainer> getRootWfmAndNc(final String rootWorkflowID,
        final NodeIDEnt nodeID) {
        return Pair.create(getRootWorkflowManager(rootWorkflowID), getNodeContainer(rootWorkflowID, nodeID));
    }

    /**
     * Converts a node id entity (as provided by gateway entities) to a {@link NodeID} instance.
     *
     * @param rootWorkflowID id of the workflow the node belongs to
     * @param nodeID the node id entity
     *
     * @return the {@link NodeID} instance
     */
    public static NodeID entityToNodeID(final String rootWorkflowID, final NodeIDEnt nodeID) {
        return nodeID.toNodeID(getRootWorkflowManager(rootWorkflowID).getID());
    }

    /**
     * Converts annotation id entity (as provided by gateway entities) to a
     * {@link WorkflowAnnotationID}-instance.
     *
     * @param rootWorkflowID id of the root(!) workflow the annotations belongs to
     * @param annotationID the annotation id entity to convert
     * @return the {@link WorkflowAnnotationID} instance
     */
    public static WorkflowAnnotationID entityToAnnotationID(final String rootWorkflowID,
        final AnnotationIDEnt annotationID) {
        NodeID nodeID = entityToNodeID(rootWorkflowID, annotationID.getNodeIDEnt());
        return new WorkflowAnnotationID(nodeID, annotationID.getIndex());
    }

    /**
     * Converts a connection id entity (as provided by gateway entities) to a {@link ConnectionID} instance.
     *
     * @param rootWorkflowID id of the workflow the connection belongs to
     * @param connectionID the id entity to convert
     * @return the {@link ConnectionID} instance
     */
    public static ConnectionID entityToConnectionID(final String rootWorkflowID, final ConnectionIDEnt connectionID) {
        return new ConnectionID(entityToNodeID(rootWorkflowID, connectionID.getDestNodeIDEnt()),
            connectionID.getDestPortIdx());
    }

    /**
     * Executes, resets or cancels a node.
     *
     * @param rootWfm the root/project workflow which contains the node whose state shall be changed
     * @param action the action to change the node state; 'reset', 'cancel' or 'execute'
     * @param nodeIdEnts the ids of the nodes to change the state for; possibly {@link NodeIDEnt#getRootID()} to change
     *            the state of the root workflow. All ids must reference nodes on the same workflow level (i.e. all must
     *            have the same prefix).
     *
     * @return the {@link NodeID}s of the nodes the status was supposed to be changed
     *
     * @throws NoSuchElementException if there is no workflow for the given root workflow id
     * @throws IllegalArgumentException if the is no node for one of the given node ids or the given node ids don't
     *             refer to the same workflow level (i.e. don't have the exact same prefix)
     * @throws IllegalStateException if the state transition is not possible, e.g., because there are executing
     *             successors or the provided action is unknown
     */
    public static NodeID[] changeNodeStates(final WorkflowManager rootWfm, final String action,
        final NodeIDEnt... nodeIdEnts) {
        if (nodeIdEnts.length == 0) {
            return new NodeID[0];
        }
        NodeID[] nodeIDs;
        WorkflowManager wfm;
        NodeID rootID = rootWfm.getID();
        if (Arrays.stream(nodeIdEnts).anyMatch(nodeId -> nodeId.equals(NodeIDEnt.getRootID()))) {
            nodeIDs = new NodeID[]{rootID};
            wfm = rootWfm.getParent();
        } else {
            nodeIDs = new NodeID[nodeIdEnts.length];
            nodeIDs[0] = nodeIdEnts[0].toNodeID(rootID);
            NodeID prefix = nodeIDs[0].getPrefix();
            for (int i = 1; i < nodeIDs.length; i++) {
                nodeIDs[i] = nodeIdEnts[i].toNodeID(rootID);
                if (!nodeIDs[i].hasSamePrefix(prefix)) {
                    throw new IllegalArgumentException("Node ids don't have the same prefix.");
                }
            }
            NodeContainer nc = rootWfm.findNodeContainer(nodeIDs[0]);
            wfm = nc.getParent();
        }

        doChangeNodeState(wfm, action, nodeIDs);

        return nodeIDs;
    }

    private static void doChangeNodeState(final WorkflowManager wfm, final String action, final NodeID... nodeIDs) {
        if (StringUtils.isBlank(action)) {
            //if there is no action (null or empty)
        } else if (action.equals("reset")) {
            for (NodeID nodeID : nodeIDs) {
                wfm.resetAndConfigureNode(nodeID);
            }
        } else if (action.equals("cancel")) {
            for (NodeID nodeID : nodeIDs) {
                wfm.cancelExecution(wfm.getNodeContainer(nodeID));
            }
        } else if (action.equals("execute")) {
            wfm.executeUpToHere(nodeIDs);
        } else {
            throw new IllegalStateException("Unknown action '" + action + "'");
        }
    }

}

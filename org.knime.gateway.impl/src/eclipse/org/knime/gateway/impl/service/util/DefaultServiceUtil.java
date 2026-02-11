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

import static org.knime.gateway.impl.service.util.WorkflowManagerResolver.findNodeContainer;
import static org.knime.gateway.impl.service.util.WorkflowManagerResolver.getProjectWfm;

import java.util.Arrays;
import java.util.NoSuchElementException;
import java.util.stream.Stream;

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
import org.knime.gateway.api.util.CoreUtil;
import org.knime.gateway.api.util.VersionId;
import org.knime.gateway.impl.project.Project;
import org.knime.gateway.impl.project.ProjectManager;
import org.knime.gateway.impl.webui.WorkflowKey;

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
     * Obtain the {@link NodeContainer} with the given id in the root workflow manager of the given {@link Project}.
     *
     * @param projectId id of the root workflow (project)
     * @param nodeId the id of the actual node
     * @return the node container
     * @throws IllegalArgumentException if there is no node for the given node id
     * @throws NoSuchElementException if there is no project for the id registered
     */
    public static NodeContainer getNodeContainer(final String projectId, final NodeIDEnt nodeId) {
        return findNodeContainer(getProjectWfm(projectId), nodeId);
    }

    /**
     * Obtain the {@link NodeContainer} with the given id in the root workflow manager of the given {@link Project}.
     *
     * @param projectId id of the root workflow (project)
     * @param versionId the version of the project
     * @param nodeId the id of the actual node
     * @return the node container
     * @throws IllegalArgumentException if there is no node for the given node id
     * @throws NoSuchElementException if there is no project for the id registered
     * @since 5.5
     */
    public static NodeContainer getNodeContainer(final String projectId, final VersionId versionId,
        final NodeIDEnt nodeId) {
        var parent = WorkflowManagerResolver.get(projectId, versionId);
        return findNodeContainer(parent, nodeId);
    }

    /**
     * Gets the workflow manager from the {@link ProjectManager} for a corresponding root workflow id. Obtain the root
     * workflow manager of the given {@link Project} and find the workflow manager of the container node identified by
     * {@code subWorkflowId}. In the sub-workflow, find the {@code nodeInSubWorkflow}.
     *
     * @param projectId the id to get the wfm for
     * @param subWorkflowId the id of the sub workflow
     * @param nodeInSubWorkflow the id of the node in the sub workflow
     * @return the node container, never null
     * @throws NoSuchElementException if there is no workflow manager for the id registered
     * @throws IllegalArgumentException if there is no node for the given node id
     * @throws IllegalStateException if the given node id doesn't reference a sub workflow (i.e. component or metanode)
     *             or the workflow is encrypted
     *
     */
    public static NodeContainer getNodeContainer(final String projectId, final NodeIDEnt subWorkflowId,
        final NodeIDEnt nodeInSubWorkflow) {
        var subWorkflow = WorkflowManagerResolver.get(projectId, subWorkflowId);
        return findNodeContainer(subWorkflow, nodeInSubWorkflow);
    }

    /**
     * Gets the (sub-)workflow manager for the given project and node id.
     *
     * @param projectId the id to get the wfm for
     * @param subWorkflowId the id of the sub workflow
     * @param versionId the version of the project
     * @param nodeInSubWorkflow the id of the node in the sub workflow
     * @return the node container, never null
     * @throws NoSuchElementException if there is no workflow manager for the id registered
     * @throws IllegalArgumentException if there is no node for the given node id
     * @throws IllegalStateException if the given node id doesn't reference a sub workflow (i.e. component or metanode)
     *             or the workflow is encrypted
     */
    public static NodeContainer getNodeContainer(final String projectId, final NodeIDEnt subWorkflowId,
        final VersionId versionId, final NodeIDEnt nodeInSubWorkflow) {
        var subWorkflow = WorkflowManagerResolver.get(projectId, subWorkflowId, versionId);
        return findNodeContainer(subWorkflow, nodeInSubWorkflow);
    }

    /**
     * Gets the (sub-)workflow manager for the project id and node id. If the WorkflowManager instance is not yet
     * loaded, load it.
     *
     * @param projectId -
     * @param workflowId the subnode's or metanode's node id. May be {@link NodeIDEnt#getRootID()}
     * @return -
     */
    public static WorkflowManager getWorkflowManager(final String projectId, final NodeIDEnt workflowId) {
        return WorkflowManagerResolver.load(projectId, workflowId, VersionId.currentState());
    }

    /**
     * Gets the (sub-)workflow manager for the project id and node id. If the WorkflowManager instance is not yet
     * loaded, load it.
     *
     * @param wfKey identifying the {@link WorkflowManager} instance
     * @return -
     * @since 5.5
     */
    public static WorkflowManager getWorkflowManager(final WorkflowKey wfKey) {
        return WorkflowManagerResolver.load(wfKey.getProjectId(), wfKey.workflowId(), wfKey.getVersionId());
    }

    /**
     * Gets the root workflow manager and the contained node container at the same time (see
     * {@link #getNodeContainer(String, NodeIDEnt)} and {@link WorkflowManagerResolver#getProjectWfm(String)}).
     *
     * @param rootWorkflowID the id of the root workflow
     * @param nodeID the id of the node requested
     * @return a pair of {@link WorkflowManager} and {@link NodeContainer} instances
     * @throws IllegalArgumentException if there is no node for the given node id
     * @throws NoSuchElementException if there is no root workflow for the given root workflow id
     */
    @SuppressWarnings("javadoc")
    public static Pair<WorkflowManager, NodeContainer> getRootWfmAndNc(final String rootWorkflowID,
        final NodeIDEnt nodeID) {
        return Pair.create( //
            getProjectWfm(rootWorkflowID), //
            getNodeContainer(rootWorkflowID, nodeID) //
        );
    }

    /**
     * Converts a node id entity (as provided by gateway entities) to a {@link NodeID} instance.
     *
     * @param projectId id of the workflow the node belongs to
     * @param nodeID the node id entity
     *
     * @return the {@link NodeID} instance
     */
    public static NodeID entityToNodeID(final String projectId, final NodeIDEnt nodeID) {
        return nodeID.toNodeID(getProjectWfm(projectId));
    }

    /**
     * Converts annotation id entity (as provided by gateway entities) to a {@link WorkflowAnnotationID}-instance.
     *
     * @param rootWorkflowID id of the root(!) workflow the annotations belongs to
     * @param annotationID the annotation id entity to convert
     * @return the {@link WorkflowAnnotationID} instance
     */
    public static WorkflowAnnotationID entityToAnnotationID(final String rootWorkflowID,
        final AnnotationIDEnt annotationID) {
        var nodeID = entityToNodeID(rootWorkflowID, annotationID.getNodeIDEnt());
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
     * Executes, resets or cancels a set of nodes.
     *
     * @param projectId the id of the workflow project
     * @param workflowId the id of the sub-workflow or 'root'
     * @param action the action to change the node state; 'reset', 'cancel' or 'execute'
     * @param nodeIdEnts the ids of the nodes to change the state for. All ids must reference nodes on the same workflow
     *            level (i.e. all must have the same prefix). If no node ids are given, the state of the workflow itself
     *            (referenced by projectId and workflowId) is changed.
     *
     * @throws NoSuchElementException if there is no workflow for the given root workflow id
     * @throws IllegalArgumentException if the is no node for one of the given node ids or the given node ids don't
     *             refer to the same workflow level (i.e. don't have the exact same prefix)
     * @throws IllegalStateException if the state transition is not possible, e.g., because there are executing
     *             successors or the provided action is unknown
     */
    public static void changeNodeStates(final String projectId, final NodeIDEnt workflowId, final String action,
        final NodeIDEnt... nodeIdEnts) {
        NodeID[] nodeIDs = null;
        var wfm = WorkflowManagerResolver.get(projectId, workflowId); // No version is needed.
        if (nodeIdEnts != null && nodeIdEnts.length != 0) {
            nodeIDs = new NodeID[nodeIdEnts.length];
            nodeIDs[0] = nodeIdEnts[0].toNodeID(wfm);
            var prefix = nodeIDs[0].getPrefix();
            for (var i = 1; i < nodeIDs.length; i++) {
                nodeIDs[i] = nodeIdEnts[i].toNodeID(wfm);
                if (!nodeIDs[i].hasSamePrefix(prefix)) {
                    throw new IllegalArgumentException("Node ids don't have the same prefix.");
                }
            }
        }

        doChangeNodeStates(wfm, action, nodeIDs);
    }

    /**
     * Executes, cancels or resets a node.
     *
     * @param projectId the id of the workflow project
     * @param nodeId the id of the node to change the state for
     * @param action the action to change the node state; 'reset', 'cancel' or 'execute'
     * @return the node the state has been changed for
     */
    public static NodeContainer changeNodeState(final String projectId, final NodeIDEnt nodeId, final String action) {
        var nc = getNodeContainer(projectId, nodeId);
        if (nc instanceof SubNodeContainer subNodeContainer) {
            doChangeNodeStates(subNodeContainer.getWorkflowManager(), action);
        } else if (nc instanceof WorkflowManager metanodeWfm) {
            doChangeNodeStates(metanodeWfm, action);
        } else {
            doChangeNodeStates(nc.getParent(), action, nc.getID());
        }
        return nc;
    }

    private static void doChangeNodeStates(final WorkflowManager wfm, final String action, final NodeID... nodeIDs) {
        try (var l = wfm.lock()) {
            if (StringUtils.isBlank(action)) {
                // if there is no action (null or empty)
            } else if (action.equals("reset")) {
                reset(wfm, nodeIDs);
            } else if (action.equals("cancel")) {
                cancel(wfm, nodeIDs);
            } else if (action.equals("execute")) {
                execute(wfm, nodeIDs);
            } else {
                throw new IllegalStateException("Unknown action '" + action + "'");
            }
        }
    }

    private static void reset(final WorkflowManager wfm, final NodeID... nodeIDs) {
        if (nodeIDs != null && nodeIDs.length != 0) {
            // Reset given nodes
            var toReset = Arrays.stream(nodeIDs);
            toReset.forEach(wfm::resetAndConfigureNode);
        } else {
            // Reset all nodes that can be reset.
            // Only need to call on source nodes since `resetAndConfigure` will reset and configure all successors, too.
            // But: make sure to always explicitly reset metanodes because they can contain nodes that aren't connected
            // to the inner metanode inports (NXT-4489)
            var toReset = getSourceNodesAndMetanodes(wfm).map(NodeContainer::getID).filter(wfm::canResetNode);
            toReset.forEach(id -> wfm.resetAndConfigureNode(id, true));
            // In case a selection is given, we do not need to filter because if not all nodes can be reset, the
            //    action is not available in the frontend.
        }
    }

    /**
     * @param wfm The workflow manager to search in
     * @return The set of nodes in the given {@code wfm} that satisfy at least one of these conditions:
     *         <ul>
     *         <li>The node is a metanode</li>
     *         <li>The node does not have any incoming connections</li>
     *         <li>All incoming connections are from a metanode inport</li>
     *         </ul>
     */
    private static Stream<NodeContainer> getSourceNodesAndMetanodes(final WorkflowManager wfm) {
        return wfm.getNodeContainers().stream().filter(nc -> {
            if (nc instanceof WorkflowManager) {
                return true;
            }
            var incoming = wfm.getIncomingConnectionsFor(nc.getID());
            return incoming.isEmpty() || incoming.stream().allMatch(cc -> cc.getSource().equals(wfm.getID()));
        });
    }

    private static void cancel(final WorkflowManager wfm, final NodeID... nodeIDs) {
        if (nodeIDs == null || nodeIDs.length == 0) {
            // Cancel the execution of the containing workflow manager -- required for properly handling components.
            CoreUtil.cancel(wfm);
        } else {
            for (final var nodeID : nodeIDs) {
                wfm.cancelExecution(wfm.getNodeContainer(nodeID));
            }
        }
    }

    private static void execute(final WorkflowManager wfm, final NodeID... nodeIDs) {
        if (nodeIDs == null || nodeIDs.length == 0) {
            // Trigger execution via the containing workflow manager -- required for properly handling components.
            CoreUtil.execute(wfm);
        } else {
            wfm.executeUpToHere(nodeIDs);
        }
    }

    /**
     * Asserts that the given project version is active.
     *
     * @param projectId the id of the project
     * @param versionId the version id to check
     * @throws IllegalStateException if no project for the given ID could be found
     * @throws ProjectVersionException if the active project version is not the given version
     */
    public static void assertProjectVersion(final String projectId, final VersionId versionId) {
        if (!ProjectManager.getInstance().isActiveProjectVersion(projectId, versionId)) {
            throw new ProjectVersionException("Project version \"" + versionId + "\" is not active");
        }
    }

    /**
     * Exception thrown when the project version is not the expected one.
     */
    @SuppressWarnings("serial")
    public static class ProjectVersionException extends RuntimeException {

        ProjectVersionException(final String message) {
            super(message);
        }
    }

}

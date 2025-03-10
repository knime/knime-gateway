/*
 * ------------------------------------------------------------------------
 *
 *  Copyright by KNIME AG, Zurich, Switzerland
 *  Website: http://www.knime.org; Email: contact@knime.org
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
 * History
 *   Feb 24, 2021 (hornm): created
 */
package org.knime.gateway.impl.webui.service.commands;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.knime.core.node.workflow.ConnectionContainer;
import org.knime.core.node.workflow.ConnectionID;
import org.knime.core.node.workflow.ConnectionUIInformation;
import org.knime.core.node.workflow.NodeID;
import org.knime.core.node.workflow.WorkflowAnnotation;
import org.knime.core.node.workflow.WorkflowAnnotationID;
import org.knime.core.node.workflow.WorkflowCopyContent;
import org.knime.core.node.workflow.WorkflowManager;
import org.knime.core.node.workflow.WorkflowPersistor;
import org.knime.gateway.api.entity.AnnotationIDEnt;
import org.knime.gateway.api.entity.ConnectionIDEnt;
import org.knime.gateway.api.entity.NodeIDEnt;
import org.knime.gateway.api.util.CoreUtil;
import org.knime.gateway.api.webui.entity.DeleteCommandEnt;
import org.knime.gateway.api.webui.service.util.ServiceExceptions.ServiceCallException;
import org.knime.gateway.impl.service.util.DefaultServiceUtil;

/**
 * Workflow command to delete nodes, connections or workflow annotations.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
final class Delete extends AbstractWorkflowCommand {

    private final List<ConnectionIDEnt> m_connectionIdsQueried;

    private final List<AnnotationIDEnt> m_annotationIdsQueried;

    private final List<NodeIDEnt> m_nodeIdsQueried;

    private final Map<String, List<Integer>> m_bendpointsIndicesQueried;

    private WorkflowPersistor m_copy;

    /*
     * Set of the connection that have been deleted. Both explicitly selected ones and those that are not part of the
     * persistor (persistor only covers connections whose source and destination are part of the persistor too).
     */
    private Set<ConnectionContainer> m_connectionsDeleted;

    private Map<ConnectionID, ConnectionUIInformation> m_connectionsWithBendpointsRemoved;

    Delete(final DeleteCommandEnt commandEnt) {
        m_nodeIdsQueried = commandEnt.getNodeIds();
        m_annotationIdsQueried = commandEnt.getAnnotationIds();
        m_connectionIdsQueried = commandEnt.getConnectionIds();
        m_bendpointsIndicesQueried = commandEnt.getConnectionBendpoints();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void undo() throws ServiceCallException {
        var wfm = getWorkflowManager();
        wfm.paste(m_copy);
        for (ConnectionContainer originalConnection : m_connectionsDeleted) {
            // restore connection as well as bendpoints on that connection
            var reAddedConnection = wfm.addConnection(originalConnection.getSource(),
                originalConnection.getSourcePort(), originalConnection.getDest(), originalConnection.getDestPort());
            reAddedConnection.setUIInfo(originalConnection.getUIInfo());
        }
        m_connectionsWithBendpointsRemoved.forEach((key, uiInfo) -> {
            // restore individually deleted bendpoints
            var cc = wfm.getConnection(key);
            if (cc != null && uiInfo != null) {
                cc.setUIInfo(uiInfo);
            }
        });
        wfm.setDirty();
    }

    @Override
    public boolean canRedo() {
        var wfm = getWorkflowManager();
        // we only need to check the connections here because every node removal will also require a
        // connection removal - and if the connection can't be removed so can't the node
        return m_connectionsDeleted != null && m_connectionsDeleted.stream().allMatch(wfm::canRemoveConnection);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean executeWithLockedWorkflow() throws ServiceCallException {
        var wfm = getWorkflowManager();
        String projectId = getWorkflowKey().getProjectId();
        Set<NodeID> nodesToDelete = m_nodeIdsQueried.stream()
            .map(id -> id.toNodeID(wfm)).collect(Collectors.toSet());
        if (!canRemoveAllNodes(wfm, nodesToDelete)) {
            throw new ServiceCallException(
                "Some nodes can't be deleted or don't exist. Delete operation aborted.");
        }

        // Connections are identified by their destination node ID and port. Connections to metanode outputs are
        //   represented by pointing to the node ID of the parent metanode.
        // Filter connections from the command s.t. all connections are guaranteed to exist in this workflow manager.
        m_connectionsDeleted = m_connectionIdsQueried.stream()
            .map(connectionId -> new ConnectionID(connectionId.getDestNodeIDEnt().toNodeID(wfm),
                connectionId.getDestPortIdx()))
            .filter(
                id -> wfm.containsNodeContainer(id.getDestinationNode()) || wfm.getID().equals(id.getDestinationNode()))
            .map(wfm::getConnection).collect(Collectors.toCollection(HashSet::new));

        if (m_connectionsDeleted.size() != m_connectionIdsQueried.size()) {
            throw new ServiceCallException("Some connections don't exist. Delete operation aborted.");
        }

        // add all connections that have a to-be-deleted-node as source _or_ destination (but _not_ both)
        for (NodeID id : nodesToDelete) {
            addIfConnectedToJustOneNode(wfm.getIncomingConnectionsFor(id), m_connectionsDeleted, nodesToDelete);
            addIfConnectedToJustOneNode(wfm.getOutgoingConnectionsFor(id), m_connectionsDeleted, nodesToDelete);
        }

        if (!CoreUtil.canRemoveConnections(m_connectionsDeleted, wfm)) {
            throw new ServiceCallException("Some connections can't be deleted. Delete operation aborted.");
        }

        var annotationIDsToDelete =
            m_annotationIdsQueried.stream().map(id -> new WorkflowAnnotationID(wfm.getID(), id.getIndex()))
                .toArray(size -> new WorkflowAnnotationID[size]);
        WorkflowCopyContent content = createWorkflowCopyContent(nodesToDelete, annotationIDsToDelete);
        if (!checkThatAllWorkflowAnnotationsExist(wfm, content.getAnnotationIDs())) {
            throw new ServiceCallException("Some workflow annotations don't exist. Delete operation aborted.");
        }

        m_copy = wfm.copy(true, content);
        WorkflowAnnotationID[] annoIds = content.getAnnotationIDs();

        var bendpointsToDelete = m_bendpointsIndicesQueried == null ? Collections.<ConnectionID, int[]> emptyMap()
            : m_bendpointsIndicesQueried.entrySet().stream().filter(e -> !e.getValue().isEmpty())
                .collect(Collectors.toMap(
                    e -> DefaultServiceUtil.entityToConnectionID(projectId, new ConnectionIDEnt(e.getKey())),
                    e -> e.getValue().stream().mapToInt(Integer::intValue).toArray()));
        m_connectionsWithBendpointsRemoved = bendpointsToDelete.keySet().stream()
            .collect(Collectors.toMap(id -> id, id -> wfm.getConnection(id).getUIInfo()));
        remove(wfm, nodesToDelete, m_connectionsDeleted, annoIds, bendpointsToDelete);
        return !nodesToDelete.isEmpty() || !m_connectionsDeleted.isEmpty() || (annoIds != null && annoIds.length != 0)
            || !bendpointsToDelete.isEmpty();
    }

    private static void addIfConnectedToJustOneNode(final Set<ConnectionContainer> connectionsToAdd,
        final Set<ConnectionContainer> connections, final Set<NodeID> nodes) {
        for (ConnectionContainer cc : connectionsToAdd) {
            if (!(nodes.contains(cc.getSource()) && nodes.contains(cc.getDest()))) {
                connections.add(cc);
            }
        }
    }

    private static void remove(final WorkflowManager wfm, final Set<NodeID> nodeIDs,
        final Set<ConnectionContainer> connections, final WorkflowAnnotationID[] annotationIDs,
        final Map<ConnectionID, int[]> bendpoints) {
        if (nodeIDs != null) {
            for (NodeID id : nodeIDs) {
                wfm.removeNode(id);
            }
        }
        if (connections != null) {
            for (ConnectionContainer cc : connections) {
                wfm.removeConnection(cc);
            }
        }
        Arrays.stream(annotationIDs).forEach(wfm::removeAnnotation);
        bendpoints.forEach((key, value) -> { //
            ConnectionContainer connection = null;
            try {
                connection = wfm.getConnection(key);
            } catch (IllegalArgumentException ex) {
                //
            }
            if (connection != null) {
                // connection might have already been deleted (explicitly, or implicitly by removal of source/target node.)
                CoreUtil.removeBendpoints(connection, value);
                wfm.setDirty();
            }
        });
    }

    private static boolean canRemoveAllNodes(final WorkflowManager wfm, final Set<NodeID> nodeIDs) {
        for (NodeID id : nodeIDs) {
            if (!wfm.canRemoveNode(id)) {
                return false;
            }
        }
        return true;
    }

    private static boolean checkThatAllWorkflowAnnotationsExist(final WorkflowManager wfm,
        final WorkflowAnnotationID[] ids) {
        WorkflowAnnotation[] annos = wfm.getWorkflowAnnotations(ids);
        return Arrays.stream(annos).noneMatch(Objects::isNull);
    }

    private static WorkflowCopyContent createWorkflowCopyContent(final Set<NodeID> nodeIDs,
        final WorkflowAnnotationID[] annotationIds) {
        return WorkflowCopyContent.builder().setNodeIDs(nodeIDs.toArray(new NodeID[nodeIDs.size()]))
            .setAnnotationIDs(annotationIds).build();
    }

}

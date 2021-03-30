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

import static org.knime.gateway.impl.service.util.DefaultServiceUtil.entityToNodeID;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.knime.core.node.workflow.ConnectionContainer;
import org.knime.core.node.workflow.ConnectionID;
import org.knime.core.node.workflow.NodeID;
import org.knime.core.node.workflow.WorkflowAnnotation;
import org.knime.core.node.workflow.WorkflowAnnotationID;
import org.knime.core.node.workflow.WorkflowCopyContent;
import org.knime.core.node.workflow.WorkflowManager;
import org.knime.core.node.workflow.WorkflowPersistor;
import org.knime.gateway.api.entity.AnnotationIDEnt;
import org.knime.gateway.api.webui.entity.DeleteCommandEnt;
import org.knime.gateway.api.webui.service.util.ServiceExceptions.OperationNotAllowedException;

/**
 * Workflow command to delete nodes, connections or workflow annotations.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
final class Delete extends AbstractWorkflowCommand<DeleteCommandEnt> {

    private WorkflowPersistor m_copy;

    /*
     * Set of the connection that have been deleted. Both explicitly selected ones and those that are not part of the
     * persistor (persistor only covers connections whose source and destination are part of the persistor too).
     */
    private Set<ConnectionContainer> m_connections;

    /**
     * {@inheritDoc}
     */
    @Override
    public void undo() throws OperationNotAllowedException {
        WorkflowManager wfm = getWorkflowManager();
        wfm.paste(m_copy);
        for (ConnectionContainer cc : m_connections) {
            wfm.addConnection(cc.getSource(), cc.getSourcePort(), cc.getDest(), cc.getDestPort());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void execute() throws OperationNotAllowedException {
        WorkflowManager wfm = getWorkflowManager();
        DeleteCommandEnt ent = getCommandEntity();
        String projectId = getWorkflowKey().getProjectId();
        Set<NodeID> nodesToDelete = ent.getNodeIds().stream()
            .map(id -> entityToNodeID(projectId, id)).collect(Collectors.toSet());
        if (!canRemoveAllNodes(wfm, nodesToDelete)) {
            throw new OperationNotAllowedException(
                "Some nodes can't be deleted or don't exist. Delete operation aborted.");
        }

        m_connections = ent.getConnectionIds().stream()
            .map(id -> new ConnectionID(entityToNodeID(projectId, id.getDestNodeIDEnt()), id.getDestPortIdx()))
            .filter(id -> wfm.containsNodeContainer(id.getDestinationNode())).map(wfm::getConnection)
            .collect(Collectors.toCollection(HashSet::new));

        if (m_connections.size() != ent.getConnectionIds().size()) {
            throw new OperationNotAllowedException("Some connections don't exist. Delete operation aborted.");
        }

        // add all connections that have a to be deleted node as source _or_ destination (but _not_ both)
        for (NodeID id : nodesToDelete) {
            addIfConnectedToJustOneNode(wfm.getIncomingConnectionsFor(id), m_connections, nodesToDelete);
            addIfConnectedToJustOneNode(wfm.getOutgoingConnectionsFor(id), m_connections, nodesToDelete);
        }

        if (!canRemoveAllConnections(wfm, m_connections)) {
            throw new OperationNotAllowedException("Some connections can't be deleted. Delete operation aborted.");
        }

        WorkflowCopyContent content = createWorkflowCopyContent(wfm.getID(), nodesToDelete, ent.getAnnotationIds());
        if (!checkThatAllWorkflowAnnotationsExist(wfm, content.getAnnotationIDs())) {
            throw new OperationNotAllowedException("Some workflow annotations don't exist. Delete operation aborted.");
        }

        m_copy = wfm.copy(true, content);
        remove(wfm, nodesToDelete, m_connections, content.getAnnotationIDs());
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
        final Set<ConnectionContainer> connections, final WorkflowAnnotationID[] annotationIDs) {
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
        if (annotationIDs != null) {
            for (WorkflowAnnotation wa : wfm.getWorkflowAnnotations(annotationIDs)) {
                wfm.removeAnnotation(wa);
            }
        }
    }

    private static boolean canRemoveAllNodes(final WorkflowManager wfm, final Set<NodeID> nodeIDs) {
        for (NodeID id : nodeIDs) {
            if (!wfm.canRemoveNode(id)) {
                return false;
            }
        }
        return true;
    }

    private static boolean canRemoveAllConnections(final WorkflowManager wfm,
        final Set<ConnectionContainer> connections) {
        for (ConnectionContainer cc : connections) {
            if (!wfm.canRemoveConnection(cc)) {
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

    private static WorkflowCopyContent createWorkflowCopyContent(final NodeID wfmId, final Set<NodeID> nodeIDs,
        final List<AnnotationIDEnt> annotationIds) {
        return WorkflowCopyContent.builder().setNodeIDs(nodeIDs.toArray(new NodeID[nodeIDs.size()]))
            .setAnnotationIDs(annotationIds.stream()
                .map(id -> new WorkflowAnnotationID(id.getNodeIDEnt().toNodeID(wfmId), id.getIndex()))
                .toArray(size -> new WorkflowAnnotationID[size]))
            .build();
    }

}

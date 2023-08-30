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
 */
package org.knime.gateway.impl.webui.service.commands;

import static java.util.Arrays.stream;
import static org.knime.gateway.api.util.CoreUtil.getConnection;
import static org.knime.gateway.impl.service.util.DefaultServiceUtil.entityToConnectionID;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.knime.core.node.workflow.ConnectionContainer;
import org.knime.core.node.workflow.ConnectionID;
import org.knime.core.node.workflow.NodeContainer;
import org.knime.core.node.workflow.NodeID;
import org.knime.core.node.workflow.WorkflowAnnotation;
import org.knime.core.node.workflow.WorkflowAnnotationID;
import org.knime.gateway.api.entity.ConnectionIDEnt;
import org.knime.gateway.api.util.CoreUtil;
import org.knime.gateway.api.webui.entity.PartBasedCommandEnt;
import org.knime.gateway.api.webui.service.util.ServiceExceptions;
import org.knime.gateway.api.webui.service.util.ServiceExceptions.OperationNotAllowedException;
import org.knime.gateway.impl.service.util.DefaultServiceUtil;

/**
 * Workflow command based on workflow parts (i.e. nodes and annotations).
 *
 * @author Benjamin Moser, KNIME GmbH, Konstanz, Germany
 */
abstract class AbstractPartBasedWorkflowCommand extends AbstractWorkflowCommand {

    private final PartBasedCommandEnt m_commandEnt;

    private NodeID[] m_nodesQueried;

    private WorkflowAnnotationID[] m_annotationsQueried;

    private Map<ConnectionID, List<Integer>> m_bendpointsQueried;

    private boolean m_partsChecked = false;


    protected AbstractPartBasedWorkflowCommand(final PartBasedCommandEnt commandEnt) {
        m_commandEnt = commandEnt;
    }

    /**
     * Check whether the workflow parts affected by this command are available (i.e. part of the workflow).
     * To avoid already performing modifications to the workflow and only then realising some workflow part is not
     * present, you may call this method at the beginning of {@link AbstractPartBasedWorkflowCommand#execute()} and,
     * going forward, assume that all workflow parts are available.
     *
     * @throws ServiceExceptions.OperationNotAllowedException If a workflow part is not available
     */
    private void checkPartsPresentElseThrow() throws ServiceExceptions.OperationNotAllowedException {
        if (m_partsChecked) {
            return;
        }

        var wfm = getWorkflowManager();
        Set<NodeID> nodesNotFound = stream(getNodeIDs()) //
            .filter(id -> CoreUtil.getNodeContainer(id, wfm).isEmpty()) //
            .collect(Collectors.toSet());

        var annotationsNotFound = stream(getAnnotationIDs()) //
            .filter(id -> CoreUtil.getAnnotation(id, wfm).isEmpty()) //
            .collect(Collectors.toSet());

        var connectionsNotFound = new HashSet<ConnectionID>();
        var bendpointsNotFound = new HashMap<ConnectionContainer, List<Integer>>();
        getBendpoints().forEach((connectionId, requestedBendpointIndices) -> { // NOSONAR
            if (getConnection(connectionId, wfm).isEmpty()) {
                connectionsNotFound.add(connectionId);
                return; // do not try to check bendpoints on non-existent connections
            }
            var connection = wfm.getConnection(connectionId);
            if (!CoreUtil.hasBendpoints(connection)) {
                bendpointsNotFound.put(connection, requestedBendpointIndices);
            } else {
                var existingBendpoints = connection.getUIInfo().getAllBendpoints();
                var nonExistingBendpoints = requestedBendpointIndices.stream()
                    .filter(requestedIndex -> requestedIndex + 1 > existingBendpoints.length).toList();
                if (!nonExistingBendpoints.isEmpty()) {
                    bendpointsNotFound.put(connection, nonExistingBendpoints);
                }
            }
        });

        if (!(nodesNotFound.isEmpty() && annotationsNotFound.isEmpty() && connectionsNotFound.isEmpty()
            && bendpointsNotFound.isEmpty())) {
            String message =
                printMissingParts(nodesNotFound, annotationsNotFound, connectionsNotFound, bendpointsNotFound);
            throw new ServiceExceptions.OperationNotAllowedException(message);
        }

        m_partsChecked = true;
    }

    /**
     * Assumed to be only called if one collection is nonempty
     *
     * @param nodesNotFound
     * @param annotationsNotFound
     * @param connectionsNotFound
     * @param bendpointsNotFound
     * @return
     */
    private static String printMissingParts(final Set<NodeID> nodesNotFound,
        final Set<WorkflowAnnotationID> annotationsNotFound, final Set<ConnectionID> connectionsNotFound,
        final HashMap<ConnectionContainer, List<Integer>> bendpointsNotFound) {

        // assumes that connection exists
        Function<Map.Entry<ConnectionContainer, List<Integer>>, String> printBendpoints =
            entry -> entry.getValue().stream() //
                .map(i -> Integer.toString(i)) // NOSONAR
                .collect(Collectors.joining(",")) //
                + " on connection " //
                + entry.getKey().toString();

        // this is all we know about nonexistent connections
        Function<ConnectionID, String> printConnectionId = connectionID -> "[? -> "
            + connectionID.getDestinationNode().toString() + "(" + connectionID.getDestinationPort() + ")]";

        return "Failed to execute command. Workflow parts not found: " + Stream
            .of(listParts("nodes", nodesNotFound, NodeID::toString),
                listParts("workflow-annotations", annotationsNotFound, WorkflowAnnotationID::toString),
                listParts("connections", connectionsNotFound, printConnectionId),
                listParts("bendpoints", bendpointsNotFound.entrySet(), printBendpoints))
            .filter(Objects::nonNull).collect(Collectors.joining(","));
    }

    private static <O> String listParts(final String label, final Collection<O> parts,
        final Function<O, String> toString) {
        if (parts.isEmpty()) {
            return null;
        }
        return label + " " + "(" + parts.stream().map(toString).collect(Collectors.joining(",")) + ")";
    }

    /**
     * @throws java.util.NoSuchElementException If a node container is not available.
     * @return The node containers for the node ids affected by this command.
     * @throws OperationNotAllowedException if a workflow part is not available
     */
    protected final Set<NodeContainer> getNodeContainers() throws OperationNotAllowedException {
        checkPartsPresentElseThrow();
        return stream(getNodeIDs()) //
            .map(id -> CoreUtil.getNodeContainer(id, getWorkflowManager()).orElseThrow()) //
            .collect(Collectors.toSet());
    }

    /**
     * @throws java.util.NoSuchElementException If an annotation is not available.
     * @return The annotation objects for the annotation ids affected by this command.
     * @throws OperationNotAllowedException if a workflow part is not available
     */
    protected final Set<WorkflowAnnotation> getAnnotations() throws OperationNotAllowedException {
        checkPartsPresentElseThrow();
        return getAnnotations(getAnnotationIDs());
    }

    /**
     * @param annotationIDs workflow annotation ids to retrieve the actual {@link WorkflowAnnotation
     *            WorkflowAnnotations} for
     * @return the workflow annotations for the given annotation ids; except the ones that couldn't be found
     */
    protected final Set<WorkflowAnnotation> getAnnotations(final WorkflowAnnotationID... annotationIDs) {
        var wfm = getWorkflowManager();
        return stream(annotationIDs) //
            .map(id -> CoreUtil.getAnnotation(id, wfm).orElse(null)) //
            .filter(Objects::nonNull) //
            .collect(Collectors.toSet());
    }

    protected final NodeID[] getNodeIDs() {
        if (m_nodesQueried == null) {
            m_nodesQueried = m_commandEnt.getNodeIds().stream() //
                .map(id -> DefaultServiceUtil.entityToNodeID(getWorkflowKey().getProjectId(), id)) //
                .toArray(NodeID[]::new);
        }
        return m_nodesQueried;
    }

    protected final WorkflowAnnotationID[] getAnnotationIDs() {
        if (m_annotationsQueried == null) {
            m_annotationsQueried = m_commandEnt.getAnnotationIds().stream() //
                .map(id -> DefaultServiceUtil.entityToAnnotationID(getWorkflowKey().getProjectId(), id)) //
                .toArray(WorkflowAnnotationID[]::new);
        }
        return m_annotationsQueried;
    }

    protected final Map<ConnectionID, List<Integer>> getBendpoints() {
        if (m_bendpointsQueried == null) {
            if (m_commandEnt.getConnectionBendpoints() == null) {
                m_bendpointsQueried = Map.of();
            } else {
                var projId = getWorkflowKey().getProjectId();
                m_bendpointsQueried = m_commandEnt.getConnectionBendpoints().entrySet().stream().collect(Collectors
                    .toMap(e -> entityToConnectionID(projId, new ConnectionIDEnt(e.getKey())), Map.Entry::getValue));
            }
        }
        return m_bendpointsQueried;
    }


}

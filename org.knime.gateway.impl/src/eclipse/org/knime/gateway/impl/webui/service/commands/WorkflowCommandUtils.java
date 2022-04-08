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

/**
 * @author Benjamin Moser, KNIME GmbH, Konstanz, Germany
 */

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.tuple.Pair;
import org.knime.core.node.workflow.NodeContainer;
import org.knime.core.node.workflow.NodeID;
import org.knime.core.node.workflow.SubNodeContainer;
import org.knime.core.node.workflow.WorkflowAnnotation;
import org.knime.core.node.workflow.WorkflowAnnotationID;
import org.knime.core.node.workflow.WorkflowManager;
import org.knime.gateway.api.entity.AnnotationIDEnt;
import org.knime.gateway.api.entity.NodeIDEnt;
import org.knime.gateway.api.webui.service.util.ServiceExceptions;
import org.knime.gateway.impl.webui.WorkflowKey;
import org.knime.gateway.impl.webui.WorkflowStatefulUtil;
import org.knime.gateway.impl.webui.WorkflowUtil;

/**
 * Utility functions for implementing workflow commands
 */
class WorkflowCommandUtils {

    /**
     * Find a node with given id in the given workflow manager.
     * @param id The node to query
     * @param wfm The workflow manager to search in
     * @return The node container corresponding to the given ID, or an empty optional if not available.
     */
    static Optional<NodeContainer> getNodeContainer(final NodeID id, final WorkflowManager wfm) {
            try {
                var nc = wfm.getNodeContainer(id);
                return Optional.of(nc);
            } catch (IllegalArgumentException e) {  // NOSONAR
                return Optional.empty();
            }
    }

    /**
     * Find a workflow annotation with given id in the given workflow manager.
     * @param id The workflow annotation to look for.
     * @param wfm The workflow manager to search in.
     * @return The workflow annotation object corresponding to the given ID, or an empty optional if not available.
     */
    static Optional<WorkflowAnnotation> getAnnotation(final WorkflowAnnotationID id, final WorkflowManager wfm) {
        var annos = wfm.getWorkflowAnnotations(id);
        if (annos.length == 0 || annos[0] == null) {
            return Optional.empty();
        } else {
            return Optional.of(annos[0]);
        }
    }

    /**
     * If any of the given optionals is empty, throw an exception and report which exactly are missing.
     * @param nodes The nodes to check. A pair of node ID and, optionally, the node container of the corresponding node.
     * @param annotations Workflow annotations to check. A pair of annotation ID and, optionally, the annotation object.
     */
    static void checkPartsPresentElseThrow(final Set<Pair<NodeID, Optional<NodeContainer>>> nodes,
            final Set<Pair<WorkflowAnnotationID, Optional<WorkflowAnnotation>>> annotations)
            throws ServiceExceptions.OperationNotAllowedException {
        var nodesNotFound = nodes.stream()
                .filter(p -> p.getRight().isEmpty())
                .map(Pair::getLeft)
                .collect(Collectors.toSet());
        var annotsNotFound = annotations.stream()
                .filter(p -> p.getRight().isEmpty())
                .map(Pair::getLeft)
                .collect(Collectors.toSet());
        boolean nodesMissing = !nodesNotFound.isEmpty();
        boolean annotsMissing = !annotsNotFound.isEmpty();
        if (nodesMissing || annotsMissing) {
            StringBuilder message = new StringBuilder("Failed to execute command. Workflow parts not found: ");
            if (nodesMissing) {
                message.append("nodes (").append(nodesNotFound.stream().map(NodeID::toString).collect(Collectors.joining(","))).append(")");
            }
            if (nodesMissing && annotsMissing) {
                message.append(", ");
            }
            if (annotsMissing) {
                message.append("workflow-annotations (").append(annotsNotFound.stream().map(WorkflowAnnotationID::toString).collect(Collectors.joining(","))).append(")");
            }
            throw new ServiceExceptions.OperationNotAllowedException(message.toString());
        }
    }

    static void resetNodesOrThrow(final AbstractWorkflowCommand command, final Set<NodeID> nodes, final boolean allowReset)
            throws ServiceExceptions.OperationNotAllowedException {
        var wfm = command.getWorkflowManager();
        var wfKey = command.getWorkflowKey();
        boolean someResettable = nodes.stream().anyMatch(wfm::canResetNode);
        if (someResettable && !allowReset) {
            throw new ServiceExceptions.OperationNotAllowedException("Resettable nodes in selection but " +
                    "explicit confirmation not given");
        }

        var dependentNodeProperties = WorkflowStatefulUtil.getInstance().getDependentNodeProperties(wfKey);
        nodes.stream().filter(dependentNodeProperties::canResetNode).forEach(wfm::resetAndConfigureNode);
    }

    enum ContainerType {
        METANODE, COMPONENT
    }


    static Optional<ContainerType> getContainerType(final WorkflowKey parent, final NodeIDEnt child)
            throws ServiceExceptions.NodeNotFoundException, ServiceExceptions.NotASubWorkflowException {
        var wfm = WorkflowUtil.getWorkflowManager(parent);
        var nodeId = child.toNodeID(NodeID.ROOTID.createChild(wfm.getProjectWFM().getID().getIndex()));
        var nodeContainer = getNodeContainer(nodeId, wfm);
        return nodeContainer.map(nc -> {
            if (nc instanceof WorkflowManager) {
                return ContainerType.METANODE;
            } else if (nc instanceof SubNodeContainer) {
                return ContainerType.COMPONENT;
            } else {
                return null;
            }
        });
    }
}

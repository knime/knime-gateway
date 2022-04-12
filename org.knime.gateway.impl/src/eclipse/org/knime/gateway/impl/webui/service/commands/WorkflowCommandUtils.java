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

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.knime.core.node.workflow.NodeContainer;
import org.knime.core.node.workflow.NodeID;
import org.knime.core.node.workflow.SubNodeContainer;
import org.knime.core.node.workflow.WorkflowAnnotation;
import org.knime.core.node.workflow.WorkflowAnnotationID;
import org.knime.core.node.workflow.WorkflowManager;
import org.knime.gateway.api.entity.NodeIDEnt;
import org.knime.gateway.api.webui.service.util.ServiceExceptions;

/**
 * Utility functions for implementing workflow commands
 * @author Benjamin Moser, KNIME GmbH, Konstanz, Germany
 */
final class WorkflowCommandUtils {

    private WorkflowCommandUtils() {

    }

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

    static void resetNodesOrThrow(final AbstractWorkflowCommand command, final Set<NodeID> nodes, final boolean allowReset)
            throws ServiceExceptions.OperationNotAllowedException {
        var wfm = command.getWorkflowManager();
        boolean someResettable = nodes.stream().anyMatch(wfm::canResetNode);
        if (someResettable && !allowReset) {
            throw new ServiceExceptions.OperationNotAllowedException("Resettable nodes in selection but " +
                    "explicit confirmation not given");
        }
        nodes.stream().filter(wfm::canResetNode).forEach(wfm::resetAndConfigureNode);
    }

    enum ContainerType {
        METANODE, COMPONENT
    }

    static Optional<ContainerType> getContainerType(final WorkflowManager parentWfm, final NodeIDEnt child) {
        var nodeId = child.toNodeID(NodeID.ROOTID.createChild(parentWfm.getProjectWFM().getID().getIndex()));
        var nodeContainer = getNodeContainer(nodeId, parentWfm);
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

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
 *   Jan 20, 2021 (hornm): created
 */
package org.knime.gateway.impl.webui.service.commands;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.knime.core.node.workflow.ConnectionContainer;
import org.knime.core.node.workflow.ConnectionID;
import org.knime.core.node.workflow.NodeContainer;
import org.knime.core.node.workflow.NodeUIInformation;
import org.knime.core.node.workflow.WorkflowAnnotation;
import org.knime.core.node.workflow.WorkflowManager;
import org.knime.gateway.api.webui.entity.TranslateCommandEnt;
import org.knime.gateway.api.webui.service.util.ServiceExceptions.OperationNotAllowedException;
import org.knime.gateway.impl.webui.service.commands.util.EditBendpoints;
import org.knime.gateway.impl.webui.service.commands.util.Geometry;
import org.knime.gateway.impl.webui.service.commands.util.Geometry.Delta;

/**
 * Workflow command to translate (i.e. change the position) of nodes and workflow annotations.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
final class Translate extends AbstractPartBasedWorkflowCommand {

    private final Delta m_delta;

    Translate(final TranslateCommandEnt commandEnt) {
        super(commandEnt);
        m_delta = Delta.of(commandEnt.getTranslation());
    }

    @Override
    public boolean executeWithLockedWorkflow() throws OperationNotAllowedException {
        if (m_delta.isZero()) {
            return false;
        }
        performTranslation(getWorkflowManager(), getNodeContainers(), getAnnotations(), getBendpoints(), m_delta);
        return true;
    }

    @Override
    public void undo() throws OperationNotAllowedException {
        performTranslation(getWorkflowManager(), getNodeContainers(), getAnnotations(), getBendpoints(),
            m_delta.invert());
    }

    /**
     * Translate the given elements
     *
     * @param wfm The workflow manager to operate in
     * @param nodes The nodes to move
     * @param annotations The annotations to move
     * @param bendpoints The connection bendpoints to move. Mapping from connection ID to sequence indices of bendpoints
     *            on connections
     * @param delta The 2D translation vector
     */
    static void performTranslation(final WorkflowManager wfm, final Set<NodeContainer> nodes,
        final Set<WorkflowAnnotation> annotations, final Map<ConnectionID, List<Integer>> bendpoints,
        final Delta delta) {
        translateNodes(nodes, delta);
        translateAnnotations(wfm, annotations, delta);
        translateSomeBendpoints(wfm, bendpoints, delta);
    }

    private static void translateSomeBendpoints(final WorkflowManager wfm, final Map<ConnectionID, List<Integer>> bendpoints,
        final Geometry.Delta delta) {
        var modifiedConnections = bendpoints.entrySet().stream() //
            .filter(e -> !e.getValue().isEmpty()).collect(Collectors.toSet());
        modifiedConnections //
            .forEach(e -> { //
                var connection = wfm.getConnection(e.getKey());
                var bendpointIndices = e.getValue();
                EditBendpoints.translateSomeBendpoints(connection, bendpointIndices, delta);
                wfm.setDirty();
            });
        if (!modifiedConnections.isEmpty()) {
            wfm.setDirty();
        }
    }

    /**
     * Translate the given elements, implicitly including all connection bendpoints between given nodes
     *
     * @param wfm The workflow manager to operate in
     * @param nodes The nodes to move
     * @param annotations The annotations to move
     * @param delta The 2D translation vector
     */
    static void performTranslation(final WorkflowManager wfm, final Set<NodeContainer> nodes,
        final Set<WorkflowAnnotation> annotations, final Delta delta) {
        translateNodes(nodes, delta);
        translateAnnotations(wfm, annotations, delta);
        translateAllBendpoints(wfm, nodes, delta);
    }

    private static void translateAllBendpoints(final WorkflowManager wfm, final Set<NodeContainer> nodes, final Delta delta) {
        Set<ConnectionContainer> modifiedConnections = EditBendpoints.inducedConnections(nodes, wfm);
        modifiedConnections.forEach(connectionInSet -> EditBendpoints.translateAllBendpoints(connectionInSet, delta));
        if (!modifiedConnections.isEmpty()) {
            wfm.setDirty();
        }
    }

    private static void translateAnnotations(final WorkflowManager wfm,
        final Set<WorkflowAnnotation> selectedAnnotations, final Delta delta) {
        for (WorkflowAnnotation wa : selectedAnnotations) {
            wa.shiftPosition(delta.x(), delta.y());
        }
        if (!selectedAnnotations.isEmpty()) {
            wfm.setDirty();
        }
    }

    static void translateNodes(final Set<NodeContainer> selectedNodes, final Delta delta) {
        for (NodeContainer nc : selectedNodes) {
            NodeUIInformation.moveNodeBy(nc, delta.toArray());
            nc.setDirty(); // will propagate upwards
        }
    }

}

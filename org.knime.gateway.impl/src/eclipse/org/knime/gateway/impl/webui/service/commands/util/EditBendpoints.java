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
package org.knime.gateway.impl.webui.service.commands.util;

import java.util.List;
import java.util.Set;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

import org.knime.core.node.workflow.ConnectionContainer;
import org.knime.core.node.workflow.ConnectionUIInformation;
import org.knime.core.node.workflow.NodeContainer;
import org.knime.core.node.workflow.WorkflowManager;

/**
 * Utility methods for editing connection bendpoints.
 */
public final class EditBendpoints {

    private EditBendpoints() {

    }

    public static boolean hasBendpoints(final ConnectionContainer connection) {
        return connection.getUIInfo() != null && connection.getUIInfo().getAllBendpoints().length > 0;
    }

    /**
     * Translate bendpoints on the workflow canvas by {@code delta}. Assumes all connections and bendpoints are present.
     * 
     * @param connection The connection on which the bendpoints are
     * @param bendpointIndices Indices identifying the bendpoints to move
     * @param delta The translation shift. First component is X-coordinate, second is Y-coordinate.
     */
    public static void translateSomeBendpoints(final ConnectionContainer connection,
        final List<Integer> bendpointIndices, final Geometry.Delta delta) {
        var indices = bendpointIndices.stream().mapToInt(i -> i).toArray();
        editConnectionUIInformation(connection, b -> b.translate(delta.toArray(), indices));
    }

    public static void translateAllBendpoints(final ConnectionContainer connection, final Geometry.Delta delta) {
        editConnectionUIInformation(connection, b -> b.translate(delta.toArray()));
    }

    private static void editConnectionUIInformation(final ConnectionContainer connection,
        final UnaryOperator<ConnectionUIInformation.Builder> transformation) {
        var builder = ConnectionUIInformation.builder().copyFrom(connection.getUIInfo());
        connection.setUIInfo(transformation.apply(builder).build()); // need to explicitly set to notify listeners
    }

    /**
     *
     * @param nodes
     * @return The set of all connections between nodes in the given set
     */
    public static Set<ConnectionContainer> inducedConnections(final Set<NodeContainer> nodes,
        final WorkflowManager wfm) {
        var nodeIds = nodes.stream().map(NodeContainer::getID).collect(Collectors.toSet());
        return nodes.stream() //
            .flatMap(pastedNode -> wfm.getOutgoingConnectionsFor(pastedNode.getID()).stream()) //
            .filter(connectionStartingInSet -> nodeIds.contains(connectionStartingInSet.getDest()))
            .collect(Collectors.toSet());
    }

}

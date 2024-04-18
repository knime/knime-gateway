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
 *   Apr 10, 2024 (kai): created
 */
package org.knime.gateway.impl.webui.service.commands.util;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.knime.core.node.port.PortType;
import org.knime.core.node.workflow.ConnectionContainer;
import org.knime.core.node.workflow.NodeContainer;
import org.knime.core.node.workflow.NodeID;
import org.knime.core.node.workflow.WorkflowManager;
import org.knime.gateway.api.webui.entity.ConnectableEnt;

/**
 * Package internal representation of a selected node used for port matching and automatically drawing connections
 *
 * @author Kai Franze, KNIME GmbH, Germany
 */
record Connectable(NodeContainer nc, boolean isContainedInWfm, Connectable.Type type, boolean isExecuted,
    List<PortType> inPorts, int numInPorts, List<PortType> outPorts, int numOutPorts, int firstDataPortIdx,
    Collection<ConnectionContainer> incomingConnections, Collection<ConnectionContainer> outgoingConnections) {

    // TODO: Do we really need to save all these values, or can we just get them from their source at every access?
    //  e.g. instead of saving `inPorts`, add a getter that invokes MatchingPortsUtil#...

    /**
     * A {@link Connectable} precedes another in this ordering iff its left top corner is north-west of the other.
     */
    static final Comparator<Connectable> NORTH_WEST_ORDERING = Comparator.comparing(node -> node.bounds().leftTop());

    enum Type {
            DEFAULT, //
            METANODE_INPUT_BAR, //
            METANODE_OUTPUT_BAR
    }

    /**
     * Factory method
     *
     * @param nodeId The entity representing the selected connectable on the workflow canvas
     * @param wfm The workflow manager to use
     * @return The internal connectable record giving access to all properties needed
     */
    static Connectable of(final ConnectableEnt connectableEnt, final WorkflowManager wfm) {
        final var nodeId = connectableEnt.getNodeId().toNodeID(wfm);
        final var nc = NodeConnector.getNodeContainerOrSelf(nodeId, wfm);
        final var isContainedInWfm = wfm.containsNodeContainer(nodeId);

        final Type type;
        final int firstDataPortIdx;
        if (nc instanceof WorkflowManager subWfm) {
            type = getType(connectableEnt, isContainedInWfm, wfm, subWfm);
            firstDataPortIdx = 0;
        } else {
            type = Type.DEFAULT;
            firstDataPortIdx = 1;
        }

        final var isMetaNodeBar = type == Type.METANODE_INPUT_BAR || type == Type.METANODE_OUTPUT_BAR;

        final var inPorts = MatchingPortsUtil.getAllPortTypesOfSide(nc, true, isMetaNodeBar);
        final var numInPorts = inPorts.size();
        final var outPorts = MatchingPortsUtil.getAllPortTypesOfSide(nc, false, isMetaNodeBar);
        final var numOutPorts = outPorts.size();

        final var isExecuted = nc.getNodeContainerState().isExecuted();
        final var incomingConnections = getConnectionsForConnectable(nodeId, false, wfm, type);
        final var outgoingConnections = getConnectionsForConnectable(nodeId, true, wfm, type);

        return new Connectable(nc, isContainedInWfm, type, isExecuted, inPorts, numInPorts, outPorts, numOutPorts,
            firstDataPortIdx, incomingConnections, outgoingConnections);
    }

    private static Type getType(final ConnectableEnt connectableEnt, final boolean isContainedInWfm,
        final WorkflowManager wfm, final WorkflowManager subWfm) {
        final var nodeId = connectableEnt.getNodeId().toNodeID(wfm);
        final boolean isUsableMetaNode =
            !isContainedInWfm && subWfm.equals(wfm) && subWfm.getParent().containsNodeContainer(nodeId);
        if (isUsableMetaNode && Boolean.TRUE.equals(connectableEnt.isMetanodeInPortsBar())) {
            return Type.METANODE_INPUT_BAR;
        }
        if (isUsableMetaNode && Boolean.TRUE.equals(connectableEnt.isMetanodeOutPortsBar())) {
            return Type.METANODE_OUTPUT_BAR;
        }
        return Type.DEFAULT;
    }

    private static Set<ConnectionContainer> getConnectionsForConnectable(final NodeID node, final boolean isInPort,
        final WorkflowManager wfm, final Type type) {
        return switch (type) {
            case METANODE_INPUT_BAR -> isInPort //
                ? Collections.emptySet() //
                : wfm.getParent().getIncomingConnectionsFor(node);
            case METANODE_OUTPUT_BAR -> isInPort //
                ? wfm.getParent().getOutgoingConnectionsFor(node) //
                : Collections.emptySet();
            case DEFAULT -> isInPort ? wfm.getOutgoingConnectionsFor(node) : wfm.getIncomingConnectionsFor(node);
        };
    }

    Geometry.Bounds bounds() {
        return Geometry.Bounds.of(this.nc, this.type);
    }

    NodeID nodeId() {
        return this.nc.getID();
    }

    boolean isValidLeft() {
        final var isConnectableMetaNodeOutPortsBar = this.type == Type.METANODE_OUTPUT_BAR;
        final var isConnectableNativeNode = this.numOutPorts > this.firstDataPortIdx;
        return isConnectableMetaNodeOutPortsBar || isConnectableNativeNode;
    }

    boolean isValidRight() {
        final var isConnectableMetaNodeInPortsBar = this.type == Type.METANODE_INPUT_BAR;
        final var isConnectableNativeNode = this.numInPorts() > this.firstDataPortIdx;
        return isConnectableMetaNodeInPortsBar || isConnectableNativeNode;
    }

    boolean hasEnoughInputPorts() {
        return numInPorts > firstDataPortIdx;
    }

    boolean hasEnoughOutputPorts() {
        return numOutPorts > firstDataPortIdx;
    }

    boolean intersects(final Connectable connectable) {
        return bounds().xRange().intersects(connectable.bounds().xRange());
    }

    boolean isLeftTo(final Connectable connectable) {
        return bounds().xRange().start() < connectable.bounds().xRange().start();
    }

    boolean isRightTo(final Connectable connectable) {
        return bounds().xRange().start() > connectable.bounds().xRange().start();
    }

    boolean hasIncomingConnectionFrom(final Collection<Connectable> connectables) {
        return incomingConnections().stream()//
            .anyMatch(connection -> connectables.stream()//
                .map(Connectable::nodeId)//
                // True if any incoming connection starts from any node within the collection,
                // false otherwise. Also false if the stream is empty.
                .anyMatch(nodeId -> connection.getSource().equals(nodeId)));
    }

    Optional<ConnectionContainer> incomingConnection(final int portIdx) {
        return incomingConnections.stream()//
            .filter(cc -> cc.getDestPort() == portIdx)//
            .findFirst();
    }
}

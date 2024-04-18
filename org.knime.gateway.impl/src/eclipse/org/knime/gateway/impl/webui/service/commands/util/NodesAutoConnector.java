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
 *   Apr 4, 2024 (kai): created
 */
package org.knime.gateway.impl.webui.service.commands.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.IntPredicate;
import java.util.function.Predicate;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.knime.core.node.NodeLogger;
import org.knime.core.node.workflow.ConnectionContainer;
import org.knime.core.node.workflow.WorkflowManager;
import org.knime.core.util.Pair;
import org.knime.gateway.api.util.CoreUtil;
import org.knime.gateway.api.webui.entity.ConnectableEnt;
import org.knime.gateway.impl.webui.service.commands.util.Connectable.Type;


/**
 * Helper to automatically connect an arbitrary amount of nodes.
 *
 * @author Kai Franze, KNIME GmbH, Germany
 */
public final class NodesAutoConnector {

    private static final NodeLogger LOGGER = NodeLogger.getLogger(NodesAutoConnector.class);

    private final WorkflowManager m_wfm;

    private final List<ConnectableEnt> m_connectableEnts;

    private final boolean m_isFlowVariablesOnly;

    /**
     * Instantiates the {@link NodesAutoConnector}
     *
     * @param wfm The workflow manager
     * @param connectableEnts The list of nodes to connect
     * @param isFlowVariablesOnly Whether to only connect the flow variable ports or not
     */
    public NodesAutoConnector(final WorkflowManager wfm, final List<ConnectableEnt> connectableEnts, final boolean isFlowVariablesOnly) {
        m_wfm = wfm;
        m_connectableEnts = connectableEnts;
        m_isFlowVariablesOnly = isFlowVariablesOnly;
    }

    /**
     * Connects the nodes containing
     *
     * @return A list of (1) all added connections and (2) all removed connections
     */
    public Pair<List<ConnectionContainer>, List<ConnectionContainer>> connect() {
        final List<Connectable> connectables = m_connectableEnts.stream()//
            .map(connectableEnt -> Connectable.of(connectableEnt, m_wfm))//
            .toList();

        if (m_isFlowVariablesOnly) {
            LOGGER.warn("Only connect flow variable ports. Not implemented yet");
            // TODO:
            //   1. Spatially order all the nodes
            //   2. From left to right: Identify the flow variable ports and create a planned connection
            //   3. Create all the connections
            //   4. Return all the added and removed connections
            return Pair.create(Collections.emptyList(), Collections.emptyList());
        }

        final ScreenedSelectionSet selection = determineWhetherSetIsConnectable(connectables);

        // Should never be the case since this method is only ever called as part of run, which can't
        // execute unless we're enabled, the crux of which already checks this condition.
        if (!selection.setIsConnectable()) {
            return Pair.create(Collections.emptyList(), Collections.emptyList());
        }

        final List<PlannedConnection> plannedConnections = computeConnectionPlan(selection);
        final AutoConnectResult result = executeCompletePlan(plannedConnections, m_wfm);

        LOGGER.info("%s connections were added and %s connections were removed"
            .formatted(result.addedConnections.size(), result.removedConnections.size()));

        return Pair.create(result.addedConnections(), result.removedConnections());
    }

    /**
     * Determines whether the set of selected nodes is connectable. It checks for a potential leftmost and rightmost
     * node and creates a spatially ordered list of nodes.
     *
     * @param connectables The set of potentially connectable nodes
     * @return The screened selection set
     */
    private static ScreenedSelectionSet determineWhetherSetIsConnectable(final Collection<Connectable> connectables) {
        final var emptySelectionSet = new ScreenedSelectionSet(Collections.emptyList(), null, null);

        final var validLeft = getValidLeft(connectables);
        final var validRight = getValidRight(connectables);

        if (validLeft.isEmpty() || validRight.isEmpty()) {
            return emptySelectionSet; // Since either no sources or no destinations are available
        }

        final var leftMost = validLeft.get(0);
        final var rightMost = validRight.get(validRight.size() - 1);

        // TODO: Find edge cases for this and test this
        final var leftToDiscard = getLeftToDiscard(validLeft, validRight.get(validRight.size() - 1));
        validLeft.removeAll(leftToDiscard);
        final var rightToDiscard = getRightToDiscard(validRight, validLeft.get(0));
        validRight.removeAll(rightToDiscard);

        final var hasAtLeastOnLegalConnection = validLeft.stream()//
            .anyMatch(source -> MatchingPortsUtil.checkForAtLeastOnePairOfCompatiblePorts(source, validRight));

        if (!hasAtLeastOnLegalConnection) {
            return emptySelectionSet; // Since not a single new connection could be created
        }

        final var orderedConnectables = Stream.concat(validLeft.stream(), validRight.stream())//
            .distinct()//
            .sorted(Connectable.NORTH_WEST_ORDERING)//
            .toList();
        return new ScreenedSelectionSet(orderedConnectables, leftMost, rightMost);
    }

    private static List<Connectable> getValidLeft(final Collection<Connectable> connectables) {
        return getValidConnectablesForPredicate(connectables, Connectable::isValidLeft);
    }

    private static List<Connectable> getValidRight(final Collection<Connectable> connectables) {
        return getValidConnectablesForPredicate(connectables, Connectable::isValidRight);
    }

    private static List<Connectable> getValidConnectablesForPredicate(final Collection<Connectable> connectables,
        final Predicate<Connectable> isValid) {
        final var validNodesForSide = connectables.stream()//
            .filter(connectable -> connectable.isContainedInWfm() || connectable.type() == Type.METANODE_INPUT_BAR
                || connectable.type() == Type.METANODE_OUTPUT_BAR)//
            .filter(isValid)//
            .sorted(Connectable.NORTH_WEST_ORDERING)//
            .toList();
        return new ArrayList<>(validNodesForSide); // To return a mutable list
    }

    private static Collection<Connectable> getLeftToDiscard(final List<Connectable> validLeft,
        final Connectable rightMost) {
        return getConnectablesToDiscardForPredicate(validLeft, rightMost, c -> !c.isValidLeft());
    }

    private static Collection<Connectable> getRightToDiscard(final List<Connectable> validRight,
        final Connectable leftMost) {
        return getConnectablesToDiscardForPredicate(validRight, leftMost, c -> !c.isValidRight());
    }

    private static Collection<Connectable> getConnectablesToDiscardForPredicate(
        final List<Connectable> validFromSide, final Connectable connectableAtChainEnd,
        final Predicate<Connectable> isNotConnectable) {
        final Set<Connectable> discards = new HashSet<>();
        for (int i = (validFromSide.size() - 1); i >= 0; i--) { // Iterate in backward order
            final var connectable = validFromSide.get(i);
            if (!connectable.equals(connectableAtChainEnd)) {
                if (connectable.isLeftTo(connectableAtChainEnd)) { // We don't need to look more to the left
                    break;
                }
                // It's not clear whether there may ever be a node which doesn't have a flow output, <= to be sure
                if (isNotConnectable.test(connectable)) {
                    discards.add(connectable);
                }
            }
        }
        return discards;
    }

    /**
     * Computes a connection plan, that is an ordered list of {@link PlannedConnection} containing at least one element.
     *
     * @param selection The screened selection set of connectables
     * @return a list of planned connections
     */
    private static List<PlannedConnection> computeConnectionPlan(final ScreenedSelectionSet selection) {
        final var orderedConnectables = selection.connectables();
        final List<PlannedConnection> plannedConnections = new ArrayList<>();
        final Set<Connectable> plannedDestinations = new HashSet<>();
        final Predicate<Connectable> hasNoIncoming = dest -> !dest.hasIncomingConnectionFrom(plannedDestinations);

        // Step one:
        // Iterate the ordered nodes and try to connect from left to right. If they don't overlap in the X domain and
        // the destination node doesn't have any incoming connection from within the selection yet.
        for (var i = 0; i < (orderedConnectables.size() - 1); i++) { // Iterate all potential sources
            final var source = orderedConnectables.get(i);
            final var offset = i + 1; // To pick the second node in the list as destination

            if (source.hasEnoughOutputPorts()) {
                final var destinations = orderedConnectables.stream().skip(offset);
                getPlannedConnection(source, destinations, plannedConnections, hasNoIncoming).ifPresent(pc -> {
                    plannedDestinations.add(pc.destination());
                    plannedConnections.add(pc);
                });
            }
        }

        // Step two:
        // Now, find any with input ports that are not already connected in the plan and that are not the first node
        // or have the same X location as the first node and connect to the first previous node which has an output
        // port of the appropriate port type. Such situations may arise due certain spatial configurations.
        final var nodeAtChainStart = orderedConnectables.get(0);
        for (var i = 1; i < orderedConnectables.size(); i++) { // Iterate all potential destinations
            final var destination = orderedConnectables.get(i);
            final var length = i; // To pick all the nodes left to the destination as potential sources

            if (!plannedDestinations.contains(destination) && destination.isRightTo(nodeAtChainStart)
                && destination.hasEnoughInputPorts()) {
                final var sources = getHeadAsReverseStream(orderedConnectables, length);
                getPlannedConnection(destination, sources, plannedConnections).ifPresent(pc -> {
                    plannedDestinations.add(pc.destination());
                    plannedConnections.add(pc);
                });
            }
        }

        return plannedConnections;
    }

    private static Stream<Connectable> getHeadAsReverseStream(final List<Connectable> orderedConnectables,
        final int length) {
        final List<Connectable> mutableHeadOfList =
            new ArrayList<>(orderedConnectables.stream().limit(length).toList());
        Collections.reverse(mutableHeadOfList);
        return mutableHeadOfList.stream();
    }

    /**
     * Try to find a planned connection from the source to to any of the destination nodes, searching from left to
     * right. Returns an empty {@link Optional} if no connection is possible.
     *
     * @param source The source node to find a connection from
     * @param destinations The ordered stream of destination nodes to find a connection to
     * @param existingPlan The already existing list of planned connections
     * @param hasNoIncoming Checks if potential destination has existing incoming connections
     * @return The optional planned connection if one could be found
     */
    private static Optional<PlannedConnection> getPlannedConnection(final Connectable source,
        final Stream<Connectable> destinations, final List<PlannedConnection> existingPlan,
        final Predicate<Connectable> hasNoIncoming) {
        return destinations//
            .filter(Connectable::hasEnoughInputPorts)//
            .filter(destination -> !destination.intersects(source))//
            .filter(hasNoIncoming)//
            .map(destination -> getPlannedConnection(source, destination, existingPlan))//
            .flatMap(Optional::stream)//
            .findFirst();
    }

    /**
     * Try to find a planned connection to the destination from any of the source nodes, searching from right to left.
     * Returns an empty {@link Optional} if no connection is possible.
     *
     * @param destination The destination node to find a connection to
     * @param sources The ordered stream of source nodes to find a connection from
     * @param existingPlan The already existing list of planned connections
     * @return The optional planned connection if one could be found
     */
    private static Optional<PlannedConnection> getPlannedConnection(final Connectable destination,
        final Stream<Connectable> sources, final List<PlannedConnection> existingPlan) {
        return sources//
            .filter(Connectable::hasEnoughOutputPorts)//
            .map(source -> getPlannedConnection(source, destination, existingPlan))//
            .flatMap(Optional::stream)//
            .findFirst();
    }

    /**
     * Try to find a planned connection from the source to the destination by performing a depth first search. The outer
     * loop iterates the source output ports while every inner loop iterates the destination input ports from top to
     * bottom.
     *
     * @return An instance of {@link PlannedConnection} as an {@link Optional}, by request; this takes into account port
     *         types. The priority is the first (in natural ordering) unused and unplanned port on source and
     *         destination side of matching port types; if no such connection exists already, then the first unplanned
     *         port on each side of matching port types. If no connection plan could be determined under these rules, an
     *         empty {@link Optional} is returned.
     */
    private static Optional<PlannedConnection> getPlannedConnection(final Connectable source,
        final Connectable destination, final List<PlannedConnection> existingPlan) {
        // Step 1: Taking existing connections for source and destination into account
        var plannedConnection = findFirstMatchPairOfCompatiblePorts(source, destination, //
            portIdx -> !outputPortAlreadyHasConnection(source, portIdx, existingPlan, source.outgoingConnections()), //
            portIdx -> !inputPortAlreadyHasConnection(destination, portIdx, existingPlan,
                destination.incomingConnections()), //
            portIdx -> false);
        if (plannedConnection.isPresent()) {
            return plannedConnection;
        }

        // Step 2: If we've made it to here, nothing was found in, taking existing connections on source and
        // destination into account. Try again ignoring existing connections on source.
        plannedConnection = findFirstMatchPairOfCompatiblePorts(source, destination, //
            portIdx -> !outputPortAlreadyHasConnection(source, portIdx, existingPlan, Collections.emptySet()), //
            portIdx -> !inputPortAlreadyHasConnection(destination, portIdx, existingPlan,
                destination.incomingConnections()), //
            portIdx -> false);
        if (plannedConnection.isPresent()) {
            return plannedConnection;
        }

        // Step 3: If we've made it to here, nothing was found in, taking existing connections on source and
        // destination into account, and also ignoring existing connections on source but not destination.
        // Now just try only taking the existing plan into affect and allowing for multiple output port assignments
        // as a last ditch effort.
        plannedConnection = findFirstMatchPairOfCompatiblePorts(source, destination, //
            portIdx -> true, //
            portIdx -> !inputPortAlreadyHasConnection(destination, portIdx, existingPlan, Collections.emptySet()), //
            portIdx -> inputPortAlreadyHasConnection(destination, portIdx, Collections.emptyList(),
                destination.incomingConnections()));

        return plannedConnection; // Return optional, no matter if empty or not
    }

    /**
     * Finds first matching pair of compatible ports iterating all the source output and destination input ports.
     *
     * @param source The source node
     * @param destination The destination node
     * @param isSourceUsable Predicate to filter for usable source output ports
     * @param isDestinationUsable Predicate to filter for usable destination input ports
     * @param mustDetach Predicate to determine whether an existing connection needs to be removed first
     * @return The optional planned connection
     */
    private static Optional<PlannedConnection> findFirstMatchPairOfCompatiblePorts(final Connectable source,
        final Connectable destination, final IntPredicate isSourceUsable, final IntPredicate isDestinationUsable,
        final IntPredicate mustDetach) {
        if (!source.hasEnoughOutputPorts()) {
            return Optional.empty();
        }

        return IntStream.range(source.firstDataPortIdx(), source.numOutPorts())//
            .filter(isSourceUsable)//
            .mapToObj(sourcePortIdx -> findFirstMatchingPairOfCompatiblePorts(source, sourcePortIdx, destination,
                isDestinationUsable, mustDetach))
            .flatMap(Optional::stream)//
            .findFirst();
    }

    /**
     * Finds first matching pair of compatible ports for a given source output port iterating iterating all destination
     * input ports.
     *
     * @param source The source node
     * @param sourcePortIdx The source output port index
     * @param destination The destination node
     * @param isDestinationUsable Predicate to filter for usable destination input ports
     * @param mustDetach Predicate to determine whether an existing connection needs to be removed first
     * @return The optional planned connection
     */
    private static Optional<PlannedConnection> findFirstMatchingPairOfCompatiblePorts(final Connectable source,
        final int sourcePortIdx, final Connectable destination, final IntPredicate isDestinationUsable,
        final IntPredicate mustDetach) {
        if (!destination.hasEnoughInputPorts()) {
            return Optional.empty();
        }

        final var sourcePortType = source.outPorts().get(sourcePortIdx);
        return IntStream.range(destination.firstDataPortIdx(), destination.numInPorts())//
            .filter(isDestinationUsable)//
            .filter(destinationPortIdx -> {
                final var destinationPortType = destination.inPorts().get(destinationPortIdx);
                return CoreUtil.arePortTypesCompatible(sourcePortType, destinationPortType);
            })//
            .mapToObj(destinationPortIdx -> new PlannedConnection(source, sourcePortIdx, destination,
                destinationPortIdx, mustDetach.test(destinationPortIdx)))//
            .findFirst();
    }

    /**
     * Creates all the planned connections on the workflow.
     *
     * @param plannedConnections The collection of planned connections
     * @param wfm The workflow manager being the workflow to manipulate
     * @return A list of all the added and all the remove connections
     */
    private static AutoConnectResult executeCompletePlan(final Collection<PlannedConnection> plannedConnections,
        final WorkflowManager wfm) {
        if (willReplaceExecutedIncoming(plannedConnections)) {
            LOGGER.warn("This will replace at least one already executed incoming connection");
        }

        final List<ConnectionContainer> removedConnections = new ArrayList<>();
        final List<ConnectionContainer> addedConnections = plannedConnections.stream()//
            .map(plannedConnection -> createNewConnection(plannedConnection, wfm, removedConnections::add))//
            .flatMap(Optional::stream)//
            .toList();

        return new AutoConnectResult(addedConnections, removedConnections);
    }

    private static Optional<ConnectionContainer> createNewConnection(final PlannedConnection plannedConnection,
        final WorkflowManager wfm, final Consumer<ConnectionContainer> reporter) {
        final var source = plannedConnection.source();
        final var sourcePortIdx = plannedConnection.sourcePortIdx();
        final var destination = plannedConnection.destination();
        final var destinationPortIdx = plannedConnection.destinationPortIdx();

        if (plannedConnection.destinationRequiresDetachEvent()) {
            final var connectionToRemove = destination.incomingConnection(destinationPortIdx).orElse(null);
            if (connectionToRemove != null) {
                try {
                    wfm.removeConnection(connectionToRemove);
                    reporter.accept(connectionToRemove);
                } catch (Exception e) { // TODO: Can we improve this?
                    LOGGER.error("Could not delete existing inport connection for " + destination.nodeId() + ":"
                        + destinationPortIdx + "; skipping new connection task from " + source.nodeId() + ":"
                        + sourcePortIdx + " to " + destination.nodeId() + ":" + destinationPortIdx + " due to: "
                        + e.getMessage(), e);
                    return Optional.empty();
                }
            }
        }

        final var addedConnection =
            NodeConnector.connect(wfm, source.nodeId(), sourcePortIdx, destination.nodeId(), destinationPortIdx, true);

        if (addedConnection == null) {
            LOGGER.error("Could not create the connection <%s>:<%s> -> <%s>:<%s>".formatted(source.nodeId(),
                sourcePortIdx, destination.nodeId(), destinationPortIdx));
        }

        return Optional.ofNullable(addedConnection);
    }

    private static boolean willReplaceExecutedIncoming(final Collection<PlannedConnection> plannedConnections) {
        return plannedConnections.stream()//
            .map(PlannedConnection::destination)//
            .anyMatch(Connectable::isExecuted);
    }

    private static boolean outputPortAlreadyHasConnection(final Connectable source, final int sourcePortIdx,
        final List<PlannedConnection> existingPlan, final Collection<ConnectionContainer> outgoingConnections) {
        final var connectionExists = outgoingConnections.stream()//
            .anyMatch(cc -> cc.getSourcePort() == sourcePortIdx);
        final var connectionPlanned = existingPlan.stream()//
            .anyMatch(pc -> pc.source().nodeId().equals(source.nodeId()) && pc.sourcePortIdx() == sourcePortIdx);
        return connectionExists || connectionPlanned;
    }

    private static boolean inputPortAlreadyHasConnection(final Connectable destination, final int destinationPortIdx,
        final List<PlannedConnection> existingPlan, final Collection<ConnectionContainer> incomingConnections) {
        final var connectionExists = incomingConnections.stream()//
            .anyMatch(cc -> cc.getDestPort() == destinationPortIdx);
        final var connectionPlanned = existingPlan.stream()//
            .anyMatch(pc -> pc.destination().nodeId().equals(destination.nodeId())
                && pc.destinationPortIdx() == destinationPortIdx);
        return connectionExists || connectionPlanned;
    }

    private static record ScreenedSelectionSet(List<Connectable> connectables, Connectable left, Connectable right) {

        boolean setIsConnectable() {
            return ((left != null) && (right != null) && (left != right) && !connectables.isEmpty());
        }

    }

    private static record PlannedConnection(Connectable source, int sourcePortIdx, Connectable destination,
        int destinationPortIdx, boolean destinationRequiresDetachEvent) {
        //
    }

    private static record AutoConnectResult(List<ConnectionContainer> addedConnections,
        List<ConnectionContainer> removedConnections) {
        //
    }

}

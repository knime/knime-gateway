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
import java.util.function.Function;
import java.util.function.IntPredicate;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.knime.core.node.NodeLogger;
import org.knime.core.node.workflow.ConnectionContainer;
import org.knime.core.node.workflow.WorkflowManager;
import org.knime.gateway.api.webui.entity.ConnectableEnt;
import org.knime.gateway.impl.webui.service.commands.util.Connectable.Destination;
import org.knime.gateway.impl.webui.service.commands.util.Connectable.Source;


/**
 * Helper to automatically connect an arbitrary amount of nodes.
 *
 * @author Kai Franze, KNIME GmbH, Germany
 */
public final class NodesAutoConnector {

    private static final NodeLogger LOGGER = NodeLogger.getLogger(NodesAutoConnector.class);

    private final WorkflowManager m_wfm;

    private final List<ConnectableEnt> m_connectableEnts;

    private boolean m_isFlowVariablesOnly;

    private Collection<ConnectionContainer> m_addedConnections = Collections.emptyList();

    private Collection<ConnectionContainer> m_removedConnections = Collections.emptyList();

    /**
     * Instantiates the {@link NodesAutoConnector}
     *
     * @param wfm The workflow manager
     * @param connectableEnts The list of nodes to connect
     */
    public NodesAutoConnector(final WorkflowManager wfm, final List<ConnectableEnt> connectableEnts) {
        m_wfm = wfm;
        m_connectableEnts = connectableEnts;
    }

    /**
     * Activates the 'Only connect flow variables' option
     */
    public void onlyConnectFlowVariables() {
        m_isFlowVariablesOnly = true;
    }

    /**
     * @return The collection of added connections (after execution)
     */
    public Collection<ConnectionContainer> getAddedConnections() {
        return m_addedConnections;
    }

    /**
     * @return The collection of removed connections (after execution)
     */
    public Collection<ConnectionContainer> getRemovedConnections() {
        return m_removedConnections;
    }

    /**
     * Connects the nodes containing
     */
    public void connect() {
        final List<Connectable> connectables = m_connectableEnts.stream()//
            .map(connectableEnt -> Connectable.of(connectableEnt, m_wfm))//
            .toList();

        if (m_isFlowVariablesOnly) {

            // TODO (NXT-2595): Add auto connect for flow variable ports
            //   1. Spatially order all the nodes
            //   2. From left to right: Identify the flow variable ports and create a planned connection
            //   3. Create all the connections
            //   4. Return all the added and removed connections

        }

        final List<Connectable> selection = determineWhetherSetIsConnectable(connectables);

        // Should never be the case since this method is only ever called as part of run, which can't
        // execute unless we're enabled, the crux of which already checks this condition.
        if (selection.isEmpty()) {
            return;
        }

        final List<PlannedConnection> plannedConnections = computeConnectionPlan(selection);
        final AutoConnectResult result = executeCompletePlan(plannedConnections, m_wfm);

        LOGGER.info("%s connections were added and %s connections were removed"
            .formatted(result.addedConnections.size(), result.removedConnections.size()));

        m_addedConnections = result.addedConnections();
        m_removedConnections = result.removedConnections();
    }

    /**
     * Determines whether the set of selected nodes is connectable. It checks for a potential leftmost and rightmost
     * node and creates a spatially ordered list of nodes.
     *
     * @param connectables The set of potentially connectable nodes
     * @return The screened selection set
     */
    private static List<Connectable> determineWhetherSetIsConnectable(final Collection<Connectable> connectables) {
        final var sources = getConnectablesMappedTo(connectables, Source::of);
        final var destinations = getConnectablesMappedTo(connectables, Destination::of);

        if (sources.isEmpty() || destinations.isEmpty()) {
            LOGGER.info("Either no valid source or destination nodes could be identified");
            return Collections.emptyList(); // Since either no sources or no destinations are available
        }

        final var hasAtLeastOnLegalConnection = sources.stream()//
            .anyMatch(source -> MatchingPortsUtil.checkForAtLeastOneMatchingPairOfPorts(source, destinations));

        if (!hasAtLeastOnLegalConnection) {
            LOGGER.info("Could not find any legal connection to create");
            return Collections.emptyList(); // Since not a single new connection could be created
        }

        return mergeAndOrderConnectables(sources, destinations);
    }

    private static <T extends Connectable> List<T> getConnectablesMappedTo(final Collection<Connectable> connectables,
        final Function<Connectable, Optional<T>> mapper) {
        return connectables.stream()//
            .map(mapper)//
            .flatMap(Optional::stream)//
            .sorted(Connectable.NORTH_WEST_ORDERING)//
            .toList();
    }

    private static List<Connectable> mergeAndOrderConnectables(final List<Source> sources,
        final List<Destination> destinations) {
        return Stream.concat(sources.stream(), destinations.stream())//
            .distinct()//
            .sorted(Connectable.NORTH_WEST_ORDERING)//
            .toList();
    }

    /**
     * Computes a connection plan, that is an ordered list of {@link PlannedConnection} containing at least one element.
     *
     * @param selection The screened selection set of connectables
     * @return a list of planned connections
     */
    private static List<PlannedConnection> computeConnectionPlan(final List<Connectable> selection) {
        final List<PlannedConnection> plannedConnections = new ArrayList<>();
        final Set<Destination> plannedDestinations = new HashSet<>();
        final Predicate<Destination> hasNoIncoming = dest -> !dest.hasIncomingConnectionFrom(plannedDestinations);

        // Step one:
        // Iterate the ordered nodes and try to connect from left to right. If they don't overlap in the X domain and
        // the destination node doesn't have any incoming connection from within the selection yet.
        for (var i = 0; i < (selection.size() - 1); i++) { // Iterate all potential sources
            final var source = Source.of(selection.get(i)).orElse(null);
            final var offset = i + 1; // To pick the second node in the list as destination

            if (source != null && source.hasEnoughPorts()) {
                final var destinations = getTailOfDestinationStream(selection, offset);
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
        final var nodeAtChainStart = selection.get(0);
        for (var i = 1; i < selection.size(); i++) { // Iterate all potential destinations
            final var destination = Destination.of(selection.get(i)).orElse(null);
            final var length = i; // To pick all the nodes left to the destination as potential sources

            if (destination != null && !plannedDestinations.contains(destination)
                && destination.isRightTo(nodeAtChainStart) && destination.hasEnoughPorts()) {
                final var sources = getHeadOfSourceStreamReversed(selection, length);
                getPlannedConnection(destination, sources, plannedConnections).ifPresent(pc -> {
                    plannedDestinations.add(pc.destination());
                    plannedConnections.add(pc);
                });
            }
        }

        return plannedConnections;
    }

    private static Stream<Destination> getTailOfDestinationStream(final List<Connectable> orderedConnectables,
        final int offset) {
        return orderedConnectables.stream()//
            .skip(offset)//
            .map(Destination::of)//
            .flatMap(Optional::stream);
    }

    private static Stream<Source> getHeadOfSourceStreamReversed(final List<Connectable> orderedConnectables,
        final int length) {
        final var mutableSources = orderedConnectables.stream()//
            .limit(length)//
            .map(Source::of)//
            .flatMap(Optional::stream)//
            .collect(Collectors.toCollection(ArrayList::new)); // Makes it a mutable list
        Collections.reverse(mutableSources);
        return mutableSources.stream();
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
    private static Optional<PlannedConnection> getPlannedConnection(final Source source,
        final Stream<Destination> destinations, final List<PlannedConnection> existingPlan,
        final Predicate<Destination> hasNoIncoming) {
        return destinations//
            .filter(Destination::hasEnoughPorts)//
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
    private static Optional<PlannedConnection> getPlannedConnection(final Destination destination,
        final Stream<Source> sources, final List<PlannedConnection> existingPlan) {
        return sources//
            .filter(Source::hasEnoughPorts)//
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
    private static Optional<PlannedConnection> getPlannedConnection(final Source source, final Destination destination,
        final List<PlannedConnection> existingPlan) {
        // Step 1: Taking existing connections for source and destination into account
        IntPredicate isSourceUsable =
            idx -> !source.isPortConnected(idx) && existingPlan.stream().noneMatch(pc -> pc.isPlannedFrom(source, idx));
        IntPredicate isDestinationUsable = idx -> !destination.isPortConnected(idx)
            && existingPlan.stream().noneMatch(pc -> pc.isPlannedTo(destination, idx));
        IntPredicate mustDetach = idx -> false;

        var plannedConnection = MatchingPortsUtil.findFirstMatchingPairOfPorts(source, destination, isSourceUsable,
            isDestinationUsable, mustDetach);
        if (plannedConnection.isPresent()) {
            return plannedConnection;
        }

        // Step 2: If we've made it to here, nothing was found in, taking existing connections on source and
        // destination into account. Try again ignoring existing connections on source.
        isSourceUsable = idx -> existingPlan.stream().noneMatch(pc -> pc.isPlannedFrom(source, idx));

        plannedConnection = MatchingPortsUtil.findFirstMatchingPairOfPorts(source, destination, isSourceUsable,
            isDestinationUsable, mustDetach);
        if (plannedConnection.isPresent()) {
            return plannedConnection;
        }

        // Step 3: If we've made it to here, nothing was found in, taking existing connections on source and
        // destination into account, and also ignoring existing connections on source but not destination.
        // Now just try only taking the existing plan into affect and allowing for multiple output port assignments
        // as a last ditch effort.
        isSourceUsable = idx -> true;
        isDestinationUsable = idx -> existingPlan.stream().noneMatch(pc -> pc.isPlannedTo(destination, idx));
        mustDetach = destination::isPortConnected;

        plannedConnection = MatchingPortsUtil.findFirstMatchingPairOfPorts(source, destination, isSourceUsable,
            isDestinationUsable, mustDetach);
        return plannedConnection; // Return optional, no matter if empty or not
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
        final var sourceId = plannedConnection.source().getNodeId();
        final var sourcePortIdx = plannedConnection.sourcePortIdx();
        final var destination = plannedConnection.destination();
        final var destinationId = destination.getNodeId();
        final var destinationPortIdx = plannedConnection.destinationPortIdx();

        if (plannedConnection.destinationRequiresDetachEvent()) {
            final var connectionToRemove = destination.getConnection(destinationPortIdx).orElse(null);
            if (connectionToRemove != null) {
                try {
                    wfm.removeConnection(connectionToRemove);
                    reporter.accept(connectionToRemove);
                } catch (RuntimeException e) {
                    LOGGER.error("""
                            Could not delete existing input port connection for <%s:%s>;
                            Skipping new connection task from <%s:%s> to <%s:%s> due to <%s>
                            """.formatted(destinationId, destinationPortIdx, sourceId, sourcePortIdx, destinationId,
                        destinationPortIdx), e);
                    return Optional.empty();
                }
            }
        }

        final var addedConnection =
            NodeConnector.connect(wfm, sourceId, sourcePortIdx, destinationId, destinationPortIdx, true);

        if (addedConnection == null) {
            LOGGER.error("Could not create the connection <%s:%s> -> <%s:%s>".formatted(sourceId, sourcePortIdx,
                destinationId, destinationPortIdx));
        }

        return Optional.ofNullable(addedConnection);
    }

    private static boolean willReplaceExecutedIncoming(final Collection<PlannedConnection> plannedConnections) {
        return plannedConnections.stream()//
            .map(PlannedConnection::destination)//
            .anyMatch(Destination::isExecuted);
    }

    static record PlannedConnection(Source source, int sourcePortIdx, Destination destination, int destinationPortIdx,
        boolean destinationRequiresDetachEvent) {

        boolean isPlannedFrom(final Source src, final int idx) {
            return source.equals(src) && sourcePortIdx == idx;
        }

        boolean isPlannedTo(final Destination dest, final int idx) {
            return destination.equals(dest) && destinationPortIdx == idx;
        }

    }

    private static record AutoConnectResult(List<ConnectionContainer> addedConnections,
        List<ConnectionContainer> removedConnections) {
        //
    }

}

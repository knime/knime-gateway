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
 */
package org.knime.gateway.impl.webui.service.commands.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.knime.core.node.NodeLogger;
import org.knime.core.node.workflow.ConnectionContainer;
import org.knime.core.node.workflow.WorkflowManager;
import org.knime.gateway.impl.webui.service.commands.AutoConnect;

/**
 * Heuristic to connect a given set of connectable workflow parts.
 *
 * @author Benjamin Moser, KNIME GmbH
 * @author Kai Franze, KNIME GmbH
 */
public class ConnectionPlan {

    private static final NodeLogger LOGGER = NodeLogger.getLogger(ConnectionPlan.class);

    final List<PlannedConnection> m_plannedConnections = new ArrayList<>();

    /**
     * Establish a plan to connect the selected workflow parts. Algorithm based on LinkNodesAction
     *
     * @param selection The selected parts.
     */
    @SuppressWarnings("java:S1602")  // curly braces in lambda for readability
    public ConnectionPlan(final AutoConnect.OrderedSelection selection) {
        // Forward pass: Iterate sources and try to find a fitting destination
        for (var candidateSourceIndex = 0; candidateSourceIndex < (selection.size() - 1); candidateSourceIndex++) {
            if (selection.get(candidateSourceIndex) instanceof Connectable.Source candidateSource) {
                var possibleConnections = selection.destinationsAfter(candidateSourceIndex) //
                    .filter(dest -> !dest.getBounds().xRange().intersects(candidateSource.getBounds().xRange())) //
                    .filter(dest -> {
                        // Do not plan forward connections to destinations that are in the selection.
                        // This is to enable these destinations to be connected via the backwards pass later
                        return dest.getIncomingConnections().stream().noneMatch(incoming -> {
                            var sourceIds = selection.sources().stream().map(Connectable::getNodeId)
                                .collect(Collectors.toUnmodifiableSet());
                            return sourceIds.contains(incoming.getSource());
                        });
                    }) //
                    .map(dest -> matchPorts(candidateSource, dest));
                addFirst(possibleConnections);
            }
        }
        // Backward pass: Iterate destinations and try to find a fitting source
        for (var candidateDestinationIndex = 1; candidateDestinationIndex < selection
            .size(); candidateDestinationIndex++) {
            if (selection.get(candidateDestinationIndex) instanceof Connectable.Destination candidateDestination) {
                // connections planned from first pass
                var destinationsFromForwardPass =
                    m_plannedConnections.stream().map(pc -> pc.destinationPort().destination());
                var alreadyPlanned = destinationsFromForwardPass.anyMatch(d -> d.equals(candidateDestination));
                var onLeftBoundary =
                    candidateDestination.getBounds().xRange().start() == selection.get(0).getBounds().xRange().start();
                if (alreadyPlanned || onLeftBoundary) {
                    continue;
                }
                var possibleConnections = selection.sourcesBefore(candidateDestinationIndex) //
                    .map(src -> matchPorts(src, candidateDestination));
                addFirst(possibleConnections);
            }
        }

    }

    private void add(final PlannedConnection plannedConnection) {
        m_plannedConnections.add(plannedConnection);
    }

    private void addFirst(final Stream<Optional<PlannedConnection>> plannedConnections) {
        plannedConnections.flatMap(Optional::stream).findFirst().ifPresent(this::add);
    }

    boolean willReplaceExecutedIncoming() {
        return m_plannedConnections.stream()//
            .map(PlannedConnection::destinationPort)//
            .anyMatch(dp -> dp.destination().isExecuted());
    }

    public AutoConnectResult execute(final WorkflowManager wfm) {
        if (this.willReplaceExecutedIncoming()) {
            LOGGER.warn("This will replace at least one already executed incoming connection");
        }
        final List<ConnectionContainer> removedConnections = new ArrayList<>();
        final List<ConnectionContainer> addedConnections = m_plannedConnections.stream()//
            .map(plannedConnection -> ConnectionPlan.createNewConnection(plannedConnection, wfm,
                removedConnections::add))//
            .flatMap(Optional::stream)//
            .toList();
        return new AutoConnectResult(addedConnections, removedConnections);
    }

    private static Optional<ConnectionContainer> createNewConnection(
        final ConnectionPlan.PlannedConnection plannedConnection, final WorkflowManager wfm,
        final Consumer<ConnectionContainer> removedConnectionConsumer) {
        var sourcePort = plannedConnection.sourcePort();
        var destinationPort = plannedConnection.destinationPort();
        var previousIncomingConnection = plannedConnection.destinationPort().getIncomingConnection();
        previousIncomingConnection.ifPresent(cc -> {
            try {
                wfm.removeConnection(cc);
                removedConnectionConsumer.accept(cc);
            } catch (RuntimeException e) {
                LOGGER.error("""
                        Could not delete existing input port connection for %s;
                        Skipping new connection task from %s to %s
                        """.formatted(destinationPort, sourcePort, destinationPort), e);
            }
        });
        final var addedConnection = NodeConnector.connect(wfm, sourcePort, destinationPort, true);
        if (addedConnection.isEmpty()) {
            LOGGER.error("Could not create the connection from %s to %s".formatted(sourcePort, destinationPort));
        }
        return addedConnection;
    }

    /**
     * Finds a pair of output port of {@code source} and input port of{@code destination}, taking into account already
     * planned connections.
     *
     * @param source
     * @param destination
     * @return
     */
    private Optional<PlannedConnection> matchPorts(final Connectable.Source source,
        final Connectable.Destination destination) {
        Predicate<Connectable.Source.SourcePort> sourcePortFreeInWf =
            sourcePort -> sourcePort.getOutgoingConnection().isEmpty();
        Predicate<Connectable.Destination.DestinationPort> destPortFreeInWf =
            destPort -> destPort.getIncomingConnection().isEmpty();
        Predicate<Connectable.Source.SourcePort> sourcePortFreeInPlan =
            sourcePort -> m_plannedConnections.stream().noneMatch(pc -> pc.sourcePort().equals(sourcePort));
        Predicate<Connectable.Destination.DestinationPort> destPortFreeInPlan =
            destPort -> m_plannedConnections.stream().noneMatch(pc -> pc.destinationPort().equals(destPort));
        var searchStages = Stream.of( //
            MatchingPortsUtil.findFirstMatchingPairOfPorts( //
                source, destination, //
                sourcePortFreeInWf.and(sourcePortFreeInPlan), //
                destPortFreeInWf.and(destPortFreeInPlan) //
            ), MatchingPortsUtil.findFirstMatchingPairOfPorts( //
                source, destination, //
                sourcePortFreeInPlan, //
                destPortFreeInWf.and(destPortFreeInPlan) //
            ), MatchingPortsUtil.findFirstMatchingPairOfPorts( //
                source, destination, //
                sourcePort -> true, //
                destPortFreeInPlan //
            ) //
        );
        return searchStages.flatMap(Optional::stream).findFirst();

    }

    static record PlannedConnection(Connectable.Source.SourcePort sourcePort,
            Connectable.Destination.DestinationPort destinationPort) {

        @Override
        public String toString() {
            return "%s/%s -> %s/%s".formatted(sourcePort().source().getNodeId(), sourcePort().index(),
                destinationPort().destination().getNodeId(), destinationPort().index());
        }
    }

    public static record AutoConnectResult(List<ConnectionContainer> addedConnections,
            List<ConnectionContainer> removedConnections) {
    }
}

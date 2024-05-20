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
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.knime.core.node.NodeLogger;
import org.knime.core.node.workflow.ConnectionContainer;
import org.knime.core.node.workflow.WorkflowManager;
import org.knime.gateway.api.webui.entity.ConnectableSelectionEnt;

/**
 * Heuristic to connect a given set of connectable workflow parts.
 *
 * @author Benjamin Moser, KNIME GmbH
 * @author Kai Franze, KNIME GmbH
 */
public final class AutoConnectUtil {

    private static final NodeLogger LOGGER = NodeLogger.getLogger(AutoConnectUtil.class);

    private AutoConnectUtil() {
        // utility
    }

    /**
     * Connect the provided connectables.
     *
     * @param wfm the workflow containing the connectables
     * @param connectables
     * @return the auto-connect result
     */
    public static AutoConnectChanges autoConnect(final WorkflowManager wfm, final Connectables connectables) {
        var plannedConnections = plan(connectables.sorted());
        return execute(wfm, plannedConnections);
    }

    /**
     * Plans connections among the provided spatially sorted connectables. We search in two passes: A forward pass that
     * iterates over potential sources and tries to find destinations; and a backward pass that iterates over potential
     * destinations and tries to find sources.
     * <p>
     * Algorithm based on {@code LinkNodesAction}.
     *
     * @param orderedConnectables A collection of connectables sorted by
     *            {@link org.knime.gateway.impl.webui.service.commands.util.Geometry.Rectangle#NORTH_WEST_ORDERING}.
     * @return A collection of planned connections that connect the given connectables.
     */
    @SuppressWarnings("java:S135") // more than one `continue` statement
    static List<PlannedConnection> plan(final OrderedConnectables orderedConnectables) {
        var plannedConnections = new ArrayList<PlannedConnection>();

        for (var candidateSourceIndex =
            0; candidateSourceIndex < (orderedConnectables.size() - 1); candidateSourceIndex++) {
            if (!(orderedConnectables.get(candidateSourceIndex) instanceof Connectable.Source candidateSource)) {
                continue;
            }
            var possibleConnections = orderedConnectables.destinationsAfter(candidateSourceIndex) //
                .filter(dest -> !dest.getBounds().xRange().intersects(candidateSource.getBounds().xRange())) //
                .filter(dest -> { // NOSONAR: curly braces for one-line-lambda
                    // Do not plan forward connections to destinations that are in the selection.
                    // This is to enable these destinations to be connected via the backwards pass later
                    return dest.getDestinationPorts().stream().flatMap(dp -> dp.getIncomingConnection().stream())
                        .noneMatch(incoming -> {
                            var sourceIds = orderedConnectables.sources().stream().map(Connectable::getNodeId)
                                .collect(Collectors.toUnmodifiableSet());
                            return sourceIds.contains(incoming.getSource());
                        });
                }) //
                .map(dest -> matchPorts(candidateSource, dest, plannedConnections));
            addFirst(possibleConnections, plannedConnections);
        }

        for (var candidateDestinationIndex = 1; candidateDestinationIndex < orderedConnectables
            .size(); candidateDestinationIndex++) {
            if ((!(orderedConnectables
                .get(candidateDestinationIndex) instanceof Connectable.Destination candidateDestination))) {
                continue;
            }
            var destinationsFromForwardPass = plannedConnections.stream().map(pc -> pc.destinationPort().owner());
            var alreadyPlanned = destinationsFromForwardPass.anyMatch(d -> d.equals(candidateDestination));
            var onLeftBoundary = candidateDestination.getBounds().xRange().start() == orderedConnectables.get(0)
                .getBounds().xRange().start();
            if (alreadyPlanned || onLeftBoundary) {
                continue;
            }
            var possibleConnections = orderedConnectables.sourcesBefore(candidateDestinationIndex) //
                .map(src -> matchPorts(src, candidateDestination, plannedConnections));
            addFirst(possibleConnections, plannedConnections);
        }

        return plannedConnections;
    }

    private static AutoConnectChanges execute(final WorkflowManager wfm,
        final List<PlannedConnection> plannedConnections) {
        final List<ConnectionContainer> removedConnections = new ArrayList<>();
        final List<ConnectionContainer> addedConnections = plannedConnections.stream()//
            .map(plannedConnection -> AutoConnectUtil.createNewConnection(plannedConnection, wfm,
                removedConnections::add))//
            .flatMap(Optional::stream)//
            .toList();
        return new AutoConnectChanges(addedConnections, removedConnections);
    }

    private static void addFirst(final Stream<Optional<PlannedConnection>> possibleConnections,
        final List<PlannedConnection> plannedConnections) {
        possibleConnections.flatMap(Optional::stream).findFirst().ifPresent(plannedConnections::add);
    }

    private static Optional<ConnectionContainer> createNewConnection(
        final AutoConnectUtil.PlannedConnection plannedConnection, final WorkflowManager wfm,
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
        final var addedConnection = NodeConnector.connect(wfm, sourcePort, destinationPort, false);
        if (addedConnection.isEmpty()) {
            LOGGER.error("Could not create the connection from %s to %s".formatted(sourcePort, destinationPort));
        }
        return addedConnection;
    }

    /**
     * Finds a pair of output port of {@code source} and input port of{@code destination}, taking into account
     * connections already in the workflow and already planned connections.
     *
     * @param source The candidate source connectable
     * @param destination the candidate destination connectable
     * @return A planned connection connecting {@code source} and {@code destination} via matching ports.
     */
    @SuppressWarnings("java:S3252") // false positive: no member access
    private static Optional<PlannedConnection> matchPorts(final Connectable.Source source,
        final Connectable.Destination destination, final List<PlannedConnection> plannedConnections) {
        Predicate<Connectable.Source.SourcePort<?>> sourcePortFreeInWf =
            sourcePort -> sourcePort.getOutgoingConnections().isEmpty();
        Predicate<Connectable.Destination.DestinationPort<?>> destPortFreeInWf =
            destPort -> destPort.getIncomingConnection().isEmpty();
        Predicate<Connectable.Source.SourcePort<?>> sourcePortFreeInPlan =
            sourcePort -> plannedConnections.stream().noneMatch(pc -> pc.sourcePort().equals(sourcePort));
        Predicate<Connectable.Destination.DestinationPort<?>> destPortFreeInPlan =
            destPort -> plannedConnections.stream().noneMatch(pc -> pc.destinationPort().equals(destPort));
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


    public static boolean canAddConnections(final Collection<ConnectionContainer> connections,
            final WorkflowManager wfm) {
        return connections.stream()
                .allMatch(cc -> wfm.canAddConnection(cc.getSource(), cc.getSourcePort(), cc.getDest(), cc.getDestPort()));
    }

    public static boolean canRemoveConnections(final Collection<ConnectionContainer> connections,
            final WorkflowManager wfm) {
        return connections.stream().allMatch(wfm::canRemoveConnection);
    }

    static record PlannedConnection(Connectable.SourcePort<? extends Connectable.Source> sourcePort,
            Connectable.DestinationPort<? extends Connectable.Destination> destinationPort) {

        @Override
        public String toString() {
            return "%s/%s -> %s/%s".formatted(sourcePort().owner().getNodeId(), sourcePort().index(),
                destinationPort().owner().getNodeId(), destinationPort().index());
        }
    }

    /**
     * The result of the auto-connection process.
     *
     * @param addedConnections connections that have been added in the process
     * @param removedConnections connections that have been removed in the process
     */
    public static record AutoConnectChanges(List<ConnectionContainer> addedConnections,
            List<ConnectionContainer> removedConnections) {
    }

    /**
     * An immutable list of {@link Connectable}s providing specific accessors.
     */
    public static class Connectables {

        List<Connectable> m_connectables;

        public Connectables(final List<Connectable> connectables) {
            m_connectables = connectables;
        }

        public Connectables(final ConnectableSelectionEnt selection, final WorkflowManager wfm) {
            var isFlowVariablePortsOnly = Boolean.TRUE.equals(selection.isFlowVariablePortsOnly());
            var selectedNodes = selection.getSelectedNodes().stream() //
                .map(nodeId -> {  // NOSONAR
                    return isFlowVariablePortsOnly ? //
                        new Connectable.NodeFlow(nodeId.toNodeID(wfm), wfm) : //
                        new Connectable.NodeData(nodeId.toNodeID(wfm), wfm);
                })//
                .toList();
            List<Connectable> connectables = new ArrayList<>(selectedNodes);
            if (Boolean.TRUE.equals(selection.isWorkflowInPortsBarSelected())) {
                connectables.add( //
                    isFlowVariablePortsOnly ? //
                        new Connectable.InPortsBarFlow(wfm) : //
                        new Connectable.InPortsBarData(wfm));
            }
            if (Boolean.TRUE.equals(selection.isWorkflowOutPortsBarSelected())) {
                connectables.add( //
                    isFlowVariablePortsOnly ? //
                        new Connectable.OutPortsBarFlow(wfm) : //
                        new Connectable.OutPortsBarData(wfm));
            }
            m_connectables = connectables;
        }

        private static <E> Stream<E> filter(final Stream<?> stream, final Class<E> targetClass) {
            return stream.filter(targetClass::isInstance).map(targetClass::cast);
        }

        public List<Connectable.Source> sources() {
            return filter(this.stream(), Connectable.Source.class).toList();
        }

        public List<Connectable.Destination> destinations() {
            return filter(this.stream(), Connectable.Destination.class).toList();
        }

        private Stream<Connectable> reversedFrom(final int index) {
            var els = new ArrayList<>(m_connectables);
            Collections.reverse(els);
            return els.stream().skip((long)this.size() - index);
        }

        Stream<Connectable.Source> sourcesBefore(final int index) {
            return filter(reversedFrom(index), Connectable.Source.class);
        }

        public Stream<Connectable.Destination> destinationsAfter(final int index) {
            return filter( //
                this.stream().skip((long)index + 1), //
                Connectable.Destination.class //
            );
        }

        private Stream<Connectable> stream() {
            return m_connectables.stream();
        }

        int size() {
            return m_connectables.size();
        }

        Connectable get(final int index) {
            return m_connectables.get(index);
        }

        OrderedConnectables sorted() {
            var sorted = this.stream()//
                .sorted(Comparator.comparing(Connectable::getBounds, Geometry.Rectangle.NORTH_WEST_ORDERING))//
                .toList();
            return new OrderedConnectables(sorted);
        }
    }

    /**
     * A list of {@link Connectable}s ordered by their bounds under north-west-ordering.
     *
     * @see Connectables#sorted()
     */
    static final class OrderedConnectables extends Connectables {
        private OrderedConnectables(final List<Connectable> connectables) {
            super(connectables);
        }
    }
}

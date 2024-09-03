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
 *   Apr 3, 2023 (hornm): created
 */
package org.knime.gateway.impl.webui.service.commands.util;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.TreeMap;
import java.util.function.Predicate;
import java.util.stream.IntStream;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.knime.core.node.context.ModifiableNodeCreationConfiguration;
import org.knime.core.node.context.ports.ConfigurablePortGroup;
import org.knime.core.node.context.ports.ExchangeablePortGroup;
import org.knime.core.node.context.ports.ExtendablePortGroup;
import org.knime.core.node.context.ports.PortGroupConfiguration;
import org.knime.core.node.port.PortType;
import org.knime.core.node.port.flowvariable.FlowVariablePortObject;
import org.knime.core.node.workflow.NodeContainer;
import org.knime.core.node.workflow.NodeID;
import org.knime.core.node.workflow.WorkflowManager;
import org.knime.gateway.api.util.CoreUtil;
import org.knime.gateway.impl.webui.service.commands.util.AutoDisConnectUtil.PlannedConnection;
import org.knime.gateway.impl.webui.service.commands.util.Connectable.Destination;
import org.knime.gateway.impl.webui.service.commands.util.Connectable.Source;

/**
 * Utility methods to identify matching port pairs for nodes.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
final class MatchingPortsUtil {

    private MatchingPortsUtil() {
        // utility
    }

    /**
     * Get matching ports for a given pair of source node and destination node. As of today, the FE only calls this
     * function when it also provides a source port index to connect from. The case where no source port index is
     * provided (a.k.a. auto-connecting two existing nodes present in the workflow) is not used yet.
     *
     * @param sourceNode source node container
     * @param destNode destination node
     * @param sourcePortIdx optional source port, if <code>null</code> it will be automatically determined
     * @param destPortIdx optional dest port, if <code>null</code> it will be automatically determined
     * @param wfm workflow manager
     *
     * @return The patching pairs of ports; the destination port index can be {@code -1} if the port index couldn't be
     *         determined
     */
    static Map<Integer, Integer> getMatchingPorts(final NodeContainer sourceNode, final NodeContainer destNode,
        final Integer sourcePortIdx, final Integer destPortIdx, final WorkflowManager wfm) {
        if (sourcePortIdx != null && destPortIdx != null) { // Check if both ports are compatible
            var sourcePortType = sourceNode.getOutPort(sourcePortIdx).getPortType();
            var destPortType = destNode.getInPort(destPortIdx).getPortType();
            if (CoreUtil.arePortTypesCompatible(sourcePortType, destPortType)) {
                return Map.of(sourcePortIdx, destPortIdx);
            } else {
                return Map.of();
            }
        }

        if (sourcePortIdx != null) { // Looks for matching destPort, supports dynamic nodes and flow variables
            var destPort = getDestPortIdxFromSourcePortIdx(sourceNode, sourcePortIdx, destNode, wfm);
            return Map.of(sourcePortIdx, destPort);
        }

        if (destPortIdx != null) { // Looks for matching sourcePort, supports dynamic nodes and flow variables
            var sourcePort = getSourcePortIdxFromDestPortIdx(sourceNode, destNode, destPortIdx, wfm);
            return Map.of(sourcePort, destPortIdx);
        }

        // Looks for first matching pair
        // ignores dynamic nodes and flow variables. Might be added later.
        return getFirstMatchingSourcePortsForDestPorts(sourceNode, destNode, wfm);
    }

    /**
     * Get the destination port that best matches a given source port. In case of dynamic nodes, this port might first
     * be added. This is a side effect.
     *
     * @return Port index of best matching destination port or {@code -1} if there is none
     */
    private static Integer getDestPortIdxFromSourcePortIdx(final NodeContainer sourceNode, final Integer sourcePortIdx,
        final NodeContainer destNode, final WorkflowManager wfm) {
        PortType sourcePortType;
        if(sourceNode == wfm) { // We are inside a metanode, the connection source is an 'in' port of the metanode
            sourcePortType = sourceNode.getInPort(sourcePortIdx).getPortType();
        } else {
            sourcePortType = sourceNode.getOutPort(sourcePortIdx).getPortType();
        }

        // First try to find an existing matching port
        var destPortFirst = (destNode instanceof WorkflowManager) ? 0 : 1;
        for (var destPortIdx = destPortFirst; destPortIdx < destNode.getNrInPorts(); destPortIdx++) {
            var destPortType = destNode.getInPort(destPortIdx).getPortType();
            if (CoreUtil.arePortTypesCompatible(sourcePortType, destPortType)) {
                return destPortIdx;
            }
        }

        try {
            return createAndGetDestPortIdx(sourcePortType, destNode.getID(), wfm).orElseThrow();
        } catch (IllegalArgumentException | NoSuchElementException e) { // NOSONAR

            // Third, consider the default flow variable port if compatible
            if (CoreUtil.arePortTypesCompatible(sourcePortType, FlowVariablePortObject.TYPE)) {
                return 0;
            }
            return -1;
        }
    }

    /**
     * Get the source port that best matches a given source port.
     *
     * @return Port index of best matching destination port or {@code -1} if there is none
     */
    private static Integer getSourcePortIdxFromDestPortIdx(final NodeContainer sourceNode, final NodeContainer destNode,
        final Integer destPortIdx, final WorkflowManager wfm) {
        PortType destPortType;
        if(destNode == wfm) { // We are inside a metanode, the connection destination is an 'out' port of the metanode
            destPortType = destNode.getOutPort(destPortIdx).getPortType();
        } else {
            destPortType = destNode.getInPort(destPortIdx).getPortType();
        }

        var sourcePortFirst = (sourceNode instanceof WorkflowManager) ? 0 : 1;
        for (var sourcePortIdx = sourcePortFirst; sourcePortIdx < sourceNode.getNrOutPorts(); sourcePortIdx++) {
            var sourcePortType = sourceNode.getOutPort(sourcePortIdx).getPortType();
            if (CoreUtil.arePortTypesCompatible(sourcePortType, destPortType)) {
                return sourcePortIdx;
            }
        }

        // check default flow variable if compatible with destination port
        if (CoreUtil.arePortTypesCompatible(FlowVariablePortObject.TYPE, destPortType)) {
            return 0;
        }

        return -1;
    }

    /**
     * Optionally creates the best matching port and returns it's index
     */
    private static Optional<Integer> createAndGetDestPortIdx(final PortType sourcePortType, final NodeID destNodeId,
        final WorkflowManager wfm) throws IllegalArgumentException, NoSuchElementException {

        var creationConfig = CoreUtil.getCopyOfCreationConfig(wfm, destNodeId).orElseThrow();
        var portsConfig = creationConfig.getPortConfig().orElseThrow();
        var portGroupIds = portsConfig.getPortGroupNames();

        var inPortGroups = portGroupIds.stream()//
            .filter(portsConfig::isInteractive) //
            .map(portsConfig::getGroup)//
            .filter(PortGroupConfiguration::definesInputPorts)//
            .toList();

        var portGroupToCompatibleTypes = inPortGroups.stream()//
            .filter(ConfigurablePortGroup.class::isInstance)//
            .map(ConfigurablePortGroup.class::cast)//
            .flatMap(cpg -> Arrays.stream(cpg.getSupportedPortTypes())//
                .filter(pt -> CoreUtil.arePortTypesCompatible(sourcePortType, pt))//
                .map(pt -> ImmutablePair.of(cpg, pt)))//
            .toList();

        // If there are no compatible port types, we are done
        if (portGroupToCompatibleTypes.isEmpty()) {
            throw new NoSuchElementException(); // Exception will be handled by `getDestPortIdxFromSourcePortIdx(...)`
        }

        // Determine which port group to use
        var portGroup = portGroupToCompatibleTypes.stream()//
            .filter(pair -> sourcePortType.equals(pair.getValue()))//
            .map(ImmutablePair::getKey).findFirst() // If there is an equal port type, use its port group
            .orElse(portGroupToCompatibleTypes.get(0).getKey()); // Otherwise use the first compatible port group

        // Create port and return index
        var destPortIdx =
            doCreatePortAndReturnPortIdx(portGroup, sourcePortType, destNodeId, creationConfig, inPortGroups, wfm);
        return Optional.of(destPortIdx);
    }

    private static int doCreatePortAndReturnPortIdx(final ConfigurablePortGroup portGroup, final PortType destPortType,
        final NodeID destNodeId, final ModifiableNodeCreationConfiguration creationConfig,
        final List<PortGroupConfiguration> inPortGroups, final WorkflowManager wfm) {
        if (portGroup instanceof ExtendablePortGroup extendablePortGroup) {
            extendablePortGroup.addPort(destPortType);
        } else {
            ((ExchangeablePortGroup)portGroup).setSelectedPortType(destPortType);
        }
        wfm.replaceNode(destNodeId, creationConfig);
        return IntStream.range(0, inPortGroups.indexOf(portGroup) + 1)//
            .mapToObj(inPortGroups::get)//
            .mapToInt(group -> group.getInputPorts().length)//
            .sum();
    }

    private static Map<Integer, Integer> getFirstMatchingSourcePortsForDestPorts(final NodeContainer sourceNode,
        final NodeContainer destNode, final WorkflowManager wfm) {
        var sourcePortFirst = determineFirstNonFlowVariablePort(sourceNode);
        var destPortFirst = determineFirstNonFlowVariablePort(destNode);
        var matchingPorts = new TreeMap<Integer, Integer>();
        for (var destPortIdx = destPortFirst; destPortIdx < destNode.getNrInPorts(); destPortIdx++) {
            var destPortType = destNode.getInPort(destPortIdx).getPortType();
            for (var sourcePortIdx = sourcePortFirst; sourcePortIdx < sourceNode.getNrOutPorts(); sourcePortIdx++) {
                var sourcePortType = sourceNode.getOutPort(sourcePortIdx).getPortType();
                var arePortsCompatible = CoreUtil.arePortTypesCompatible(sourcePortType, destPortType);
                var isSourcePortUnconnected =
                    wfm.getOutgoingConnectionsFor(sourceNode.getID(), sourcePortIdx).isEmpty();
                var arePortsAbsentInMap =
                    !matchingPorts.containsKey(sourcePortIdx) && !matchingPorts.containsValue(destPortIdx);
                if (arePortsCompatible && isSourcePortUnconnected && arePortsAbsentInMap) {
                    matchingPorts.put(sourcePortIdx, destPortIdx);
                }
            }
        }
        return matchingPorts;
    }

    private static int determineFirstNonFlowVariablePort(final NodeContainer nodeContainer) {
        return (nodeContainer instanceof WorkflowManager) ? 0 : 1;
    }

    /**
     * @param source A candidate source to connect
     * @param destination A candidate destination to connect
     * @param sourcePortUsable Predicate whether a source port can be connected
     * @param destinationPortUsable Predicate whether a destination port can be connected
     * @return A planned connection between the first matching ports of {@code source} and {@code destination}.
     */
    static Optional<PlannedConnection> findFirstMatchingPairOfPorts(final Source source,
            final Destination destination, final Predicate<Connectable.SourcePort<?>> sourcePortUsable,
            final Predicate<Connectable.DestinationPort<?>> destinationPortUsable) {
        return source.getSourcePorts().stream()
                .filter(sourcePortUsable)
                .map(sp -> findFirstMatchingPairOfPorts(sp, destination, destinationPortUsable))
                .flatMap(Optional::stream)
                .findFirst();
    }

    private static Optional<PlannedConnection> findFirstMatchingPairOfPorts(
        final Connectable.SourcePort<?> sourcePort,
            final Destination destination,
            final Predicate<Connectable.DestinationPort<?>> destinationPortUsable) {
        return destination.getDestinationPorts().stream() //
                .filter(destinationPortUsable) //
                .filter(sourcePort::isCompatibleWith)
            .map(destPort -> new PlannedConnection(sourcePort, destPort)) //
                .findFirst(); //
    }

}

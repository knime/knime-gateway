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
import java.util.stream.Collectors;
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
import org.knime.gateway.api.webui.service.util.ServiceExceptions.OperationNotAllowedException;

/**
 * Utility methods to identify matching port pairs for nodes.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
public final class MatchingPortsUtil {

    private MatchingPortsUtil() {
        // utility
    }

    /**
     * Get matching ports for a given pair of source node and destination node. As of today, the FE only calls this
     * function when it also provides a source port index to connect from. The case where no source port index is
     * provided (a.k.a. auto-connecting two existing nodes present in the workflow) is not used yet.
     *
     * @param sourceNodeId source node
     * @param destNodeId destination node
     * @param sourcePortIdx optional source port, if <code>null</code> it will be automatically determined
     * @param wfm workflow manager
     *
     * @return The patching pairs of ports
     * @throws OperationNotAllowedException Is thrown when no matching ports where found
     */
    public static Map<Integer, Integer> getMatchingPorts(final NodeID sourceNodeId, final NodeID destNodeId,
        final Integer sourcePortIdx, final WorkflowManager wfm) throws OperationNotAllowedException {
        if (sourcePortIdx != null) { // Currently in use by the FE, supports dynamic nodes and flow variables
            var destPortIdx = getDestPortIdxFromSourcePortIdx(sourceNodeId, sourcePortIdx, destNodeId, wfm);
            return Map.of(sourcePortIdx, destPortIdx);
        } else { // Currently not used by the FE, ignores dynamic nodes and flow variables. Might be added later.
            var sourceNode = wfm.getNodeContainer(sourceNodeId);
            var destNode = wfm.getNodeContainer(destNodeId);
            return getFirstMatchingSourcePortsForDestPorts(sourceNode, destNode, wfm);
        }
    }

    /**
     * Get the destination port that best matches a given source port. In case of dynamic nodes, this port might first
     * be added. This is a side effect.
     *
     * @return Port index of best matching destination port
     */
    private static Integer getDestPortIdxFromSourcePortIdx(final NodeID sourceNodeId, final Integer sourcePortIdx,
        final NodeID destNodeId, final WorkflowManager wfm) throws OperationNotAllowedException {
        var sourceNode = wfm.getNodeContainer(sourceNodeId);
        var sourcePortType = sourceNode.getOutPort(sourcePortIdx).getPortType();
        var destNode = wfm.getNodeContainer(destNodeId);

        // First try to find an existing matching port
        var destPortFirst = (destNode instanceof WorkflowManager) ? 0 : 1;
        for (var destPortIdx = destPortFirst; destPortIdx < destNode.getNrInPorts(); destPortIdx++) {
            var destPortType = destNode.getInPort(destPortIdx).getPortType();
            if (CoreUtil.arePortTypesCompatible(sourcePortType, destPortType)) {
                return destPortIdx;
            }
        }

        // Second try to create a matching port for dynamic nodes
        try {
            return createAndGetDestPortIdx(sourcePortType, destNodeId, wfm).orElseThrow();
        } catch (IllegalArgumentException | NoSuchElementException e) {

            // Third, consider the default flow variable port if compatible
            if (CoreUtil.arePortTypesCompatible(sourcePortType, FlowVariablePortObject.TYPE)) {
                return 0;
            }
            throw new OperationNotAllowedException("Destination port index could not be inferred", e);
        }
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
            .map(portsConfig::getGroup)//
            .filter(PortGroupConfiguration::definesInputPorts)//
            .collect(Collectors.toList());

        var portGroupToCompatibleTypes = inPortGroups.stream()//
            .filter(ConfigurablePortGroup.class::isInstance)//
            .map(ConfigurablePortGroup.class::cast)//
            .flatMap(cpg -> Arrays.stream(cpg.getSupportedPortTypes())//
                .filter(pt -> CoreUtil.arePortTypesCompatible(sourcePortType, pt))//
                .map(pt -> ImmutablePair.of(cpg, pt)))//
            .collect(Collectors.toList());

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
        if (portGroup instanceof ExtendablePortGroup) {
            ((ExtendablePortGroup)portGroup).addPort(destPortType);
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
        var sourcePortFirst = (sourceNode instanceof WorkflowManager) ? 0 : 1; // Don't connect to default flow variable ports
        var destPortFirst = (destNode instanceof WorkflowManager) ? 0 : 1; // Don't connect to default flow variable ports
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

}
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
 *   May 11, 2021 (hornm): created
 */
package org.knime.gateway.impl.webui.service.commands;

import static org.knime.gateway.api.entity.EntityBuilderManager.builder;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.apache.commons.lang3.tuple.Pair;
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
import org.knime.gateway.api.entity.NodeIDEnt;
import org.knime.gateway.api.util.CoreUtil;
import org.knime.gateway.api.webui.entity.AddNodeCommandEnt;
import org.knime.gateway.api.webui.entity.AddNodeResultEnt;
import org.knime.gateway.api.webui.entity.AddNodeResultEnt.AddNodeResultEntBuilder;
import org.knime.gateway.api.webui.entity.CommandResultEnt.KindEnum;
import org.knime.gateway.api.webui.service.util.ServiceExceptions.OperationNotAllowedException;
import org.knime.gateway.api.webui.util.EntityBuilderUtil;
import org.knime.gateway.impl.service.util.DefaultServiceUtil;
import org.knime.gateway.impl.service.util.WorkflowChangesTracker.WorkflowChange;

/**
 * Workflow command to add a native node.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 * @author Kai Franze, KNIME GmbH
 */
final class AddNode extends AbstractWorkflowCommand implements WithResult {

    private NodeID m_addedNode;

    private AddNodeCommandEnt m_commandEnt;

    AddNode(final AddNodeCommandEnt commandEnt) {
        m_commandEnt = commandEnt;
    }

    @Override
    protected boolean executeWithLockedWorkflow() throws OperationNotAllowedException {
        var wfm = getWorkflowManager();
        // Add node
        var positionEnt = m_commandEnt.getPosition();
        var factoryKeyEnt = m_commandEnt.getNodeFactory();
        var targetPosition = new int[]{positionEnt.getX(), positionEnt.getY()};
        try {
            m_addedNode = DefaultServiceUtil.createAndAddNode(factoryKeyEnt.getClassName(), factoryKeyEnt.getSettings(),
                targetPosition[0], targetPosition[1] - EntityBuilderUtil.NODE_Y_POS_CORRECTION, wfm, false);
        } catch (IOException | NoSuchElementException e) {
            throw new OperationNotAllowedException(e.getMessage(), e);
        }
        // Optionally connect node
        try {
            if (m_commandEnt.getSourceNodeId() != null) {
                var sourceNodeId = m_commandEnt.getSourceNodeId().toNodeID(wfm.getProjectWFM().getID());
                var destNodeId = m_addedNode;
                var matchingPorts = getMatchingPorts(sourceNodeId, destNodeId);
                for (var entry : matchingPorts.entrySet()) {
                    Integer sourcePortIdx = entry.getKey();
                    Integer destPortIdx = entry.getValue();
                    Connect.addNewConnection(wfm, sourceNodeId, sourcePortIdx, destNodeId, destPortIdx);
                }
            }
        } catch (OperationNotAllowedException e) {
            undo(); // No side effect if exception is thrown
            throw e;
        }
        return true; // Workflow changed if no exceptions were thrown
    }

    private Map<Integer, Integer> getMatchingPorts(final NodeID sourceNodeId, final NodeID destNodeId)
        throws OperationNotAllowedException {
        if (m_commandEnt.getSourcePortIdx() != null) { // Supports dynamic nodes and flow variables
            var sourcePortIdx = m_commandEnt.getSourcePortIdx();
            var destPortIdx = getDestPortIdxFromSourcePortIdx(sourceNodeId, sourcePortIdx, destNodeId);
            return Map.of(sourcePortIdx, destPortIdx);
        } else { // Currently unreachable, ignores dynamic nodes and flow variables
            var wfm = getWorkflowManager();
            var sourceNode = wfm.getNodeContainer(sourceNodeId);
            var destNode = wfm.getNodeContainer(destNodeId);
            var sourcePortFirst = (sourceNode instanceof WorkflowManager) ? 0 : 1; // Don't connect to default flow variable ports
            var destPortFirst = (destNode instanceof WorkflowManager) ? 0 : 1; // Don't connect to default flow variable ports
            var matchingPorts = new TreeMap<Integer, Integer>();
            for (var destPortIdx = destPortFirst; destPortIdx < destNode.getNrInPorts(); destPortIdx++) {
                addFirstMatchingSourcePortForDestPortIdx(destPortIdx, sourcePortFirst, sourceNode, destNode,
                    matchingPorts);
            }
            return matchingPorts;
        }
    }

    /**
     * Get the destination port that best matches a given source port. In case of dynamic nodes, this port might first
     * be added. This is a side effect.
     *
     * @return Port index of best matching destination port
     */
    private Integer getDestPortIdxFromSourcePortIdx(final NodeID sourceNodeId, final Integer sourcePortIdx,
        final NodeID destNodeId) throws OperationNotAllowedException {
        var wfm = getWorkflowManager();
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
            return createAndGetDestPortIdx(sourcePortType, destNodeId).orElseThrow();
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
    private Optional<Integer> createAndGetDestPortIdx(final PortType sourcePortType, final NodeID destNodeId)
        throws IllegalArgumentException, NoSuchElementException {
        var wfm = getWorkflowManager();
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
                .map(pt -> Pair.of(cpg, pt)))//
            .collect(Collectors.toList());

        // If there are no compatible port types, we are done
        if (portGroupToCompatibleTypes.isEmpty()) {
            return Optional.empty();
        }

        // If there is a port type that is equal to the source port, use it
        for (var pair : portGroupToCompatibleTypes) {
            var portGroup = pair.getKey();
            var destPortType = pair.getValue();
            if (sourcePortType.equals(destPortType)) {
                var destPortIdx =
                    doCreatePortAndReturnPortIdx(portGroup, sourcePortType, destNodeId, creationConfig, inPortGroups);
                return Optional.of(destPortIdx);
            }
        }

        // Else use the first one found
        var portGroup = portGroupToCompatibleTypes.get(0).getKey();
        var destPortIdx =
            doCreatePortAndReturnPortIdx(portGroup, sourcePortType, destNodeId, creationConfig, inPortGroups);
        return Optional.of(destPortIdx);
    }

    private int doCreatePortAndReturnPortIdx(final ConfigurablePortGroup portGroup, final PortType destPortType,
        final NodeID destNodeId, final ModifiableNodeCreationConfiguration creationConfig,
        final List<PortGroupConfiguration> inPortGroups) {
        var wfm = getWorkflowManager();
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

    /**
     * Given a destination port index, it adds a compatible source port index if there is one. This ignores dynamic
     * nodes for now.
     *
     * @param matchingPorts If a matching port is found, this is updated as a side effect
     */
    private void addFirstMatchingSourcePortForDestPortIdx(final Integer destPortIdx, final Integer sourcePortFirst,
        final NodeContainer sourceNode, final NodeContainer destNode, final Map<Integer, Integer> matchingPorts) {
        var wfm = getWorkflowManager();
        var destPortType = destNode.getInPort(destPortIdx).getPortType();
        for (var sourcePortIdx = sourcePortFirst; sourcePortIdx < sourceNode.getNrOutPorts(); sourcePortIdx++) {
            var sourcePortType = sourceNode.getOutPort(sourcePortIdx).getPortType();
            if (CoreUtil.arePortTypesCompatible(sourcePortType, destPortType) // Port types match
                && wfm.getOutgoingConnectionsFor(sourceNode.getID(), sourcePortIdx).isEmpty() // Source port is not already connected
                && !matchingPorts.containsKey(sourcePortIdx) && !matchingPorts.containsValue(destPortIdx)) { // Ports weren't already assigned
                matchingPorts.put(sourcePortIdx, destPortIdx);
            }
        }
    }

    @Override
    public boolean canUndo() {
        return getWorkflowManager().canRemoveNode(m_addedNode);
    }

    @Override
    public void undo() throws OperationNotAllowedException {
        getWorkflowManager().removeNode(m_addedNode);
        m_addedNode = null;
    }

    @Override
    public AddNodeResultEnt buildEntity(final String snapshotId) {
        return builder(AddNodeResultEntBuilder.class)//
            .setKind(KindEnum.ADDNODERESULT)//
            .setNewNodeId(new NodeIDEnt(m_addedNode))//
            .setSnapshotId(snapshotId)//
            .build();
    }

    @Override
    public Set<WorkflowChange> getChangesToWaitFor() {
        return Collections.singleton(WorkflowChange.NODE_ADDED);
    }

}

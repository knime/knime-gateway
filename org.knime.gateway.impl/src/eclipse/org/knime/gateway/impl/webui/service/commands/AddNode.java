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
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
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

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.knime.core.node.NodeLogger;
import org.knime.core.node.context.ModifiableNodeCreationConfiguration;
import org.knime.core.node.context.ports.ConfigurablePortGroup;
import org.knime.core.node.context.ports.ExchangeablePortGroup;
import org.knime.core.node.context.ports.ExtendablePortGroup;
import org.knime.core.node.context.ports.PortGroupConfiguration;
import org.knime.core.node.port.PortType;
import org.knime.core.node.port.flowvariable.FlowVariablePortObject;
import org.knime.core.node.workflow.NodeContainer;
import org.knime.core.node.workflow.NodeID;
import org.knime.core.node.workflow.NodeTimer;
import org.knime.core.node.workflow.NodeTimer.GlobalNodeStats.NodeCreationType;
import org.knime.core.node.workflow.WorkflowManager;
import org.knime.gateway.api.entity.NodeIDEnt;
import org.knime.gateway.api.util.CoreUtil;
import org.knime.gateway.api.webui.entity.AddNodeCommandEnt;
import org.knime.gateway.api.webui.entity.AddNodeResultEnt;
import org.knime.gateway.api.webui.entity.AddNodeResultEnt.AddNodeResultEntBuilder;
import org.knime.gateway.api.webui.entity.CommandResultEnt.KindEnum;
import org.knime.gateway.api.webui.entity.NodeFactoryKeyEnt;
import org.knime.gateway.api.webui.entity.NodeFactoryKeyEnt.NodeFactoryKeyEntBuilder;
import org.knime.gateway.api.webui.entity.SpaceItemReferenceEnt;
import org.knime.gateway.api.webui.service.util.ServiceExceptions.OperationNotAllowedException;
import org.knime.gateway.api.webui.util.WorkflowEntityFactory;
import org.knime.gateway.impl.service.util.DefaultServiceUtil;
import org.knime.gateway.impl.service.util.WorkflowChangesTracker.WorkflowChange;
import org.knime.gateway.impl.webui.NodeFactoryProvider;
import org.knime.gateway.impl.webui.spaces.SpaceProviders;

/**
 * Workflow command to add a native node.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 * @author Kai Franze, KNIME GmbH
 */
final class AddNode extends AbstractWorkflowCommand implements WithResult {

    private NodeID m_addedNode;

    private final AddNodeCommandEnt m_commandEnt;

    private final NodeFactoryProvider m_nodeFactoryProvider;

    private final SpaceProviders m_spaceProviders;

    AddNode(final AddNodeCommandEnt commandEnt) {
        this(commandEnt, null, null);
    }

    AddNode(final AddNodeCommandEnt commandEnt, final NodeFactoryProvider nodeFactoryProvider,
        final SpaceProviders spaceProviders) {
        m_commandEnt = commandEnt;
        m_nodeFactoryProvider = nodeFactoryProvider;
        m_spaceProviders = spaceProviders;
    }

    @Override
    protected boolean executeWithLockedWorkflow() throws OperationNotAllowedException {
        var wfm = getWorkflowManager();
        // Add node
        var positionEnt = m_commandEnt.getPosition();
        var factoryKeyEnt = m_commandEnt.getNodeFactory();
        var url = parseURL(m_commandEnt.getUrl());
        if (factoryKeyEnt == null && url != null && m_nodeFactoryProvider != null) {
            factoryKeyEnt = getNodeFactoryKeyFromUrl(m_commandEnt.getUrl());
        }
        if (factoryKeyEnt == null && m_commandEnt.getSpaceItemReference() != null && m_nodeFactoryProvider != null
            && m_spaceProviders != null) {
            var spaceItemIdResult = getNodeFactoryKeyAndUrlFromSpaceItemReference(m_commandEnt.getSpaceItemReference());
            url = spaceItemIdResult.getValue();
            factoryKeyEnt = spaceItemIdResult.getKey();
        }
        if (factoryKeyEnt == null) {
            throw new OperationNotAllowedException("No node factory class given");
        }
        var targetPosition = new int[]{positionEnt.getX(), positionEnt.getY()};
        try {
            m_addedNode = DefaultServiceUtil.createAndAddNode(factoryKeyEnt.getClassName(), factoryKeyEnt.getSettings(),
                url, targetPosition[0], targetPosition[1] - WorkflowEntityFactory.NODE_Y_POS_CORRECTION, wfm, false);
            var nc = wfm.getNodeContainer(m_addedNode);
            trackNodeCreation(nc, m_commandEnt);
        } catch (IOException | NoSuchElementException e) {
            throw new OperationNotAllowedException(e.getMessage(), e);
        }
        // Optionally connect node
        try {
            if (m_commandEnt.getSourceNodeId() != null) {
                var sourceNodeId = m_commandEnt.getSourceNodeId().toNodeID(wfm.getProjectWFM().getID());
                var destNodeId = m_addedNode;
                var matchingPorts = getMatchingPorts(sourceNodeId, destNodeId, m_commandEnt.getSourcePortIdx(), wfm);
                for (var entry : matchingPorts.entrySet()) {
                    Integer sourcePortIdx = entry.getKey();
                    Integer destPortIdx = entry.getValue();
                    Connect.addNewConnection(wfm, sourceNodeId, sourcePortIdx, destNodeId, destPortIdx);
                    // TODO as soon as we extended the quick insertion feature with search,
                    // we need to track connection creations here, too! (but only for the nodes added via the search!)
                    // NodeTimer.GLOBAL_TIMER.addConnectionCreation(wfm.getNodeContainer(sourceNodeId),
                    //    wfm.getNodeContainer(destNodeId));

                }
            }
        } catch (OperationNotAllowedException e) {
            undo(); // No side effect if exception is thrown
            throw e;
        }
        return true; // Workflow changed if no exceptions were thrown
    }

    private static void trackNodeCreation(final NodeContainer nc, final AddNodeCommandEnt commandEnt) {
        NodeTimer.GLOBAL_TIMER.addNodeCreation(nc);
        NodeTimer.GLOBAL_TIMER.incNodeCreatedVia(NodeCreationType.WEB_UI);
        if (isNodeAddedViaQuickNodeInsertion(commandEnt)) {
            NodeTimer.GLOBAL_TIMER.incNodeCreatedVia(NodeCreationType.WEB_UI_QUICK_INSERTION_RECOMMENDED);
        }
    }

    private static boolean isNodeAddedViaQuickNodeInsertion(final AddNodeCommandEnt commandEnt) {
        // at the moment nodes are added _and_ connected to a source node only via the quick node insertion feature
        return commandEnt.getSourceNodeId() != null && commandEnt.getSourcePortIdx() != null;
    }

    private NodeFactoryKeyEnt getNodeFactoryKeyFromUrl(final String url) {
        var factory = m_nodeFactoryProvider.fromFileExtension(url);
        if (factory == null) {
            return null;
        }
        return builder(NodeFactoryKeyEntBuilder.class).setClassName(factory.getName()).build();
    }

    private static URL parseURL(final String urlString) {
        if (urlString == null) {
            return null;
        }
        try {
            return new URI(urlString).toURL();
        } catch (MalformedURLException | URISyntaxException ex) {
            NodeLogger.getLogger(AddNode.class).warn("Failed to parse url: " + urlString, ex);
            return null;
        }
    }

    private ImmutablePair<NodeFactoryKeyEnt, URL>
        getNodeFactoryKeyAndUrlFromSpaceItemReference(final SpaceItemReferenceEnt spaceItemId) {
        final var spaceProviderId = spaceItemId.getProviderId();
        final var spaceId = spaceItemId.getSpaceId();
        final var itemId = spaceItemId.getItemId();
        try {
            var space = SpaceProviders.getSpace(m_spaceProviders, spaceProviderId, spaceId);
            var url = space.toKnimeUrl(itemId).toURL();
            var itemName = space.getItemName(itemId);
            var factory = m_nodeFactoryProvider.fromFileExtension(itemName);
            if (factory != null) {
                var factoryKeyEnt = builder(NodeFactoryKeyEntBuilder.class).setClassName(factory.getName()).build();
                return new ImmutablePair<>(factoryKeyEnt, url);
            }
            return new ImmutablePair<>(null, url);
        } catch (MalformedURLException ex) {
            return new ImmutablePair<>(null, null);
        }
    }

    /**
     * Get matching ports for a given pair of source node and destination node. As of today, the FE only calls this
     * function when it also provides a source port index to connect from. The case where no source port index is
     * provided (a.k.a. auto-connecting two existing nodes present in the workflow) is not used yet.
     *
     * @return The patching pairs of ports
     * @throws OperationNotAllowedException Is thrown when no matching ports where found
     */
    private static Map<Integer, Integer> getMatchingPorts(final NodeID sourceNodeId, final NodeID destNodeId,
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

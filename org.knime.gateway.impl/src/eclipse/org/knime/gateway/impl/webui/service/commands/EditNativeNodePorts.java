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
 *   Sep 27, 2022 (Kai Franze, KNIME GmbH): created
 */
package org.knime.gateway.impl.webui.service.commands;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.function.UnaryOperator;
import java.util.stream.IntStream;

import org.knime.core.node.context.ModifiableNodeCreationConfiguration;
import org.knime.core.node.context.ports.ExtendablePortGroup;
import org.knime.core.node.context.ports.ModifiablePortsConfiguration;
import org.knime.core.node.workflow.NodeContainer;
import org.knime.core.node.workflow.NodeID;
import org.knime.core.node.workflow.WorkflowManager;
import org.knime.core.node.workflow.action.ReplaceNodeResult;
import org.knime.core.util.Pair;
import org.knime.gateway.api.util.CoreUtil;
import org.knime.gateway.api.webui.entity.AddPortCommandEnt;
import org.knime.gateway.api.webui.entity.PortCommandEnt;
import org.knime.gateway.api.webui.entity.PortCommandEnt.SideEnum;
import org.knime.gateway.api.webui.entity.RemovePortCommandEnt;
import org.knime.gateway.api.webui.service.util.ServiceExceptions.ServiceCallException;

/**
 * Helper class to edit native node ports
 *
 * @author Benjamin Moser, KNIME GmbH, Konstanz, Germany
 * @author Kai Franze, KNIME GmbH
 */
final class EditNativeNodePorts implements EditPorts {

    private final WorkflowManager m_wfm;

    private final PortCommandEnt m_portCommandEnt;

    private ReplaceNodeResult m_replaceNodeResult;

    EditNativeNodePorts(final WorkflowManager wfm, final PortCommandEnt portCommandEnt) {
        m_wfm = wfm;
        m_portCommandEnt = portCommandEnt;
        m_replaceNodeResult = null;
    }

    @Override
    public int addPort(final AddPortCommandEnt addPortCommandEnt) throws ServiceCallException {
        var newPortType = CoreUtil.getPortType(addPortCommandEnt.getPortTypeId())
            .orElseThrow(() -> new ServiceCallException("Unknown port type"));
        var groupName = addPortCommandEnt.getPortGroup();
        var creationConfigCopy = CoreUtil.getCopyOfCreationConfig(m_wfm, getNodeId()).orElseThrow();
        getExtendablePortGroup(creationConfigCopy, groupName).addPort(newPortType);
        executeAddPort(creationConfigCopy);
        return findNewPortIdx(addPortCommandEnt);
    }

    @Override
    public void removePort(final RemovePortCommandEnt removePortCommandEnt) {
        // instance that will be modified and used for creating the replacing node
        var newCreationConfig = CoreUtil.getCopyOfCreationConfig(m_wfm, getNodeId()).orElseThrow();
        var isInputSide = removePortCommandEnt.getSide() == SideEnum.INPUT;
        var portsConfig = newCreationConfig.getPortConfig().orElseThrow();
        var totalPortIndexToRemove = removePortCommandEnt.getPortIndex();
        var portGroupName = getPortGroupsPerIndex(isInputSide, portsConfig)[totalPortIndexToRemove - 1];
        var portGroup = (ExtendablePortGroup)portsConfig.getGroup(portGroupName);
        // it is possible that a port group defines input and output ports
        var removeInputPort = portGroup.definesInputPorts();
        var removeOutputPort = portGroup.definesOutputPorts();
        var indexInGroupToRemove = getPortIndexWithinGroup(totalPortIndexToRemove, isInputSide, portsConfig);
        portGroup.removePort(indexInGroupToRemove);
        executeRemovePort(newCreationConfig, totalPortIndexToRemove, removeInputPort, removeOutputPort);
    }

    @Override
    public void undo() {
        m_replaceNodeResult.undo();
    }

    @Override
    public boolean canUndo() {
        return m_replaceNodeResult.canUndo();
    }

    /**
     * Finds the new port index by adding up the length of all preceding port groups on the particular side
     */
    private int findNewPortIdx(final AddPortCommandEnt addPortCommandEnt) throws ServiceCallException {
        var portGroupId = addPortCommandEnt.getPortGroup();
        var isPortGroupInput = addPortCommandEnt.getSide() == SideEnum.INPUT;
        var portConfig = CoreUtil.getCopyOfCreationConfig(m_wfm, getNodeId()).orElseThrow().getPortConfig()
            .orElseThrow(() -> new ServiceCallException("Could not retrieve port config"));
        var portGroupIds = portConfig.getPortGroupNames();
        return IntStream.range(0, portGroupIds.indexOf(portGroupId) + 1)//
            .mapToObj(idx -> portConfig.getGroup(portGroupIds.get(idx)))//
            .filter(group -> isPortGroupInput ? group.definesInputPorts() : group.definesOutputPorts())//
            .mapToInt(group -> isPortGroupInput ? group.getInputPorts().length : group.getOutputPorts().length)//
            .sum();
    }

    private NodeID getNodeId() {
        return m_portCommandEnt.getNodeId().toNodeID(m_wfm);
    }

    private void executeAddPort(final ModifiableNodeCreationConfiguration newCreationConfig) {
        m_replaceNodeResult = m_wfm.replaceNode(getNodeId(), newCreationConfig);
    }

    private void executeRemovePort(final ModifiableNodeCreationConfiguration creationConfigCopy,
        final int totalPortIndexToRemove, final boolean removeInputPort, final boolean removeOutputPort) {
        var portMappings = getPortMappingForPortRemoval(totalPortIndexToRemove, removeInputPort, removeOutputPort,
            CoreUtil.getNodeContainer(getNodeId(), m_wfm).orElseThrow());
        m_replaceNodeResult = m_wfm.replaceNode( //
            getNodeId(), //
            creationConfigCopy, //
            portMappings //
        );
    }

    /**
     * @param totalPortIndexToRemove Index into the total number of ports on the node, counting implicit flow variable
     *            port on native nodes.
     * @param removeInputPort whether port is removed on the input- or output-side
     * @param node the node being edited
     * @return mappings for input- and output side
     */
    private static Pair<Map<Integer, Integer>, Map<Integer, Integer>> getPortMappingForPortRemoval(
        final int totalPortIndexToRemove, final boolean removeInputPort, final boolean removeOutputPort,
        final NodeContainer node) {
        var pairOfMappings = new Pair<>( //
            // Use NodeContainer#getNrInPorts / #getNrOutPorts to also count implicit flow variable port if present
            identityPortMapping(node.getNrInPorts()), //
            identityPortMapping(node.getNrOutPorts()) //
        );
        return applyRemoval( //
            pairOfMappings, //
            removeInputPort, //
            removeOutputPort, //
            mapping -> removeIndex(mapping, totalPortIndexToRemove) //
        );
    }

    private static <X> Pair<X, X> applyRemoval(final Pair<X, X> pair, final boolean leftSide,
        final boolean rightSide, final UnaryOperator<X> mapper) {
        if (!leftSide) {
            pair.map(e -> e, mapper);
        } else if (!rightSide) {
            pair.map(mapper, e -> e);
        } else {
            pair.map(mapper, mapper);
        }
        return pair;
    }

    private static ExtendablePortGroup getExtendablePortGroup(final ModifiableNodeCreationConfiguration creationConfig,
        final String groupName) {
        var portsConfig = creationConfig.getPortConfig().orElseThrow();
        return (ExtendablePortGroup)portsConfig.getGroup(groupName);
    }


    /**
     * returned array does not include position for implicit flow variable port
     *
     * @param isInputSide Whether to consider input or output ports
     * @return Port group names/IDs repeated such that lookup at index {@code i} yields the port group name/ID that the
     *         {@code i}-th port belongs to. Does not include position for implicit flow variable port.
     */
    private static String[] getPortGroupsPerIndex(final boolean isInputSide,
        final ModifiablePortsConfiguration portsConfig) {
        var locations = isInputSide ? portsConfig.getInputPortLocation() : portsConfig.getOutputPortLocation();
        var totalNumberOfPorts =
            isInputSide ? (portsConfig.getInputPorts().length) : (portsConfig.getOutputPorts().length);
        var portGroupNamePerIndex = new String[totalNumberOfPorts];
        locations.forEach((portGroupName, indicesInPortGroup) -> Arrays.stream(indicesInPortGroup)
            .forEach(i -> portGroupNamePerIndex[i] = portGroupName));
        return portGroupNamePerIndex;
    }

    /**
     * @see #getPortIndexWithinGroup(String[], int)
     */
    private static int getPortIndexWithinGroup(final int totalPortIndex, final boolean isInputSide,
        final ModifiablePortsConfiguration portsConfig) {
        var portIndexToPortGroupMap = getPortGroupsPerIndex(isInputSide, portsConfig);
        return CoreUtil.getPortIndexWithinGroup(portIndexToPortGroupMap, totalPortIndex);
    }

    private static Map<Integer, Integer> identityPortMapping(final int totalNumberOfPorts) {
        var map = new HashMap<Integer, Integer>();
        IntStream.range(0, totalNumberOfPorts).forEach(i -> map.put(i, i));
        return map;
    }

    /**
     * Set an index to removed (map to {@code -1}) and shift all succeeding indices.
     *
     * @param indexToRemove The index to set to removed
     * @return the modified mapping
     */
    private static Map<Integer, Integer> removeIndex(final Map<Integer, Integer> portMapping, final int indexToRemove) {
        portMapping.put(indexToRemove, -1);
        IntStream.range(indexToRemove + 1, portMapping.size()).forEach(i -> portMapping.put(i, i - 1));
        return portMapping;
    }

}

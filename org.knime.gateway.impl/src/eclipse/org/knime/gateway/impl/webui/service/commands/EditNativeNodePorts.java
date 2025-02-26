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

import java.util.HashMap;
import java.util.Map;
import java.util.stream.IntStream;

import org.knime.core.node.context.ModifiableNodeCreationConfiguration;
import org.knime.core.node.context.ports.ExtendablePortGroup;
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
        //offset to account for flow variable port index
        var offset = 1;
        var creationConfigCopy = CoreUtil.getCopyOfCreationConfig(m_wfm, getNodeId()).orElseThrow();
        var groupName = removePortCommandEnt.getPortGroup();
        var portIndex = removePortCommandEnt.getPortIndex();
        var portGroup = getExtendablePortGroup(creationConfigCopy, groupName);
        var removeInputPort = portGroup.definesInputPorts();
        var removeOutputPort = portGroup.definesOutputPorts();
        var portGroupLength = portGroup.getFixedPorts().length + portGroup.getConfiguredPorts().length + offset;
        portGroup.removePort(portIndex - offset);
        executeRemovePort(creationConfigCopy, portIndex, portGroupLength, removeInputPort, removeOutputPort);
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

    private final NodeID getNodeId() {
        return m_portCommandEnt.getNodeId().toNodeID(m_wfm);
    }

    private void executeAddPort(final ModifiableNodeCreationConfiguration creationConfigCopy) {
        m_replaceNodeResult = m_wfm.replaceNode(getNodeId(), creationConfigCopy);
    }

    private void executeRemovePort(final ModifiableNodeCreationConfiguration creationConfigCopy, final int portIndex,
        final int portGroupLength, final boolean inputPort, final boolean outputPort) {
        var portMappings =
            getPortMappingForPortRemoval(creationConfigCopy, portIndex, portGroupLength, inputPort, outputPort);
        m_replaceNodeResult = m_wfm.replaceNode(getNodeId(), creationConfigCopy, portMappings);
    }

    /*
     * maps input port indices before removal to indices after removal
     */
    private static Map<Integer, Integer> getInputPortMapping(
        final ModifiableNodeCreationConfiguration creationConfigCopy, final int portIndex, final int portGroupLength,
        final boolean inputPortRemoved) {
        Map<Integer, Integer> inputPortMapping = new HashMap<>();
        //no input port has been removed, all input port indices remain the same
        if (!inputPortRemoved) {
            var portConfig = creationConfigCopy.getPortConfig();
            var inputLength = portConfig.isPresent() ? (portConfig.get().getInputPorts().length + 1) : 0;
            for (int i = 0; i < inputLength; i++) {
                inputPortMapping.put(i, i);
            }
        } else { //removed input port at index portIndex, map portIndex to -1 and all following indices to index -1
            for (int i = 0; i < portGroupLength; i++) {
                if (i < portIndex) {
                    inputPortMapping.put(i, i);
                } else if (i == portIndex) {
                    inputPortMapping.put(i, -1);
                } else {
                    inputPortMapping.put(i, (i - 1));
                }
            }
        }
        return inputPortMapping;
    }

    /*
     * maps output port indices before removal to indices after removal
     */
    private static Map<Integer, Integer> getOutputPortMapping(
        final ModifiableNodeCreationConfiguration creationConfigCopy, final int portIndex, final int portGroupLength,
        final boolean outputPortRemoved) {
        Map<Integer, Integer> outputPortMapping = new HashMap<>();
        //no output port has been removed, all output port indices remain the same
        if (!outputPortRemoved) {
            var portConfig = creationConfigCopy.getPortConfig();
            var inputLength = portConfig.isPresent() ? (portConfig.get().getInputPorts().length + 1) : 0;
            for (int i = 0; i < inputLength; i++) {
                outputPortMapping.put(i, i);
            }
        } else { //removed input port at index portIndex, map portIndex to -1 and all following indices to index -1
            for (int i = 0; i < portGroupLength; i++) {
                if (i < portIndex) {
                    outputPortMapping.put(i, i);
                } else if (i == portIndex) {
                    outputPortMapping.put(i, -1);
                } else {
                    outputPortMapping.put(i, (i - 1));
                }
            }
        }
        return outputPortMapping;
    }

    /*
     * maps port indices before removal to indices after removal, required to update connections on ports correctly
     *  index of removed port is mapped to -1
     */
    private static Pair<Map<Integer, Integer>, Map<Integer, Integer>> getPortMappingForPortRemoval(
        final ModifiableNodeCreationConfiguration creationConfigCopy, final int portIndex, final int portGroupLength,
        final boolean inputPort, final boolean outputPort) {
        Map<Integer, Integer> inputPortMapping =
            getInputPortMapping(creationConfigCopy, portIndex, portGroupLength, inputPort);
        Map<Integer, Integer> outputPortMapping =
            getOutputPortMapping(creationConfigCopy, portIndex, portGroupLength, outputPort);
        return new Pair<>(inputPortMapping, outputPortMapping);
    }

    private static ExtendablePortGroup getExtendablePortGroup(final ModifiableNodeCreationConfiguration creationConfig,
        final String groupName) {
        var portsConfig = creationConfig.getPortConfig().orElseThrow();
        return (ExtendablePortGroup)portsConfig.getGroup(groupName);
    }

}

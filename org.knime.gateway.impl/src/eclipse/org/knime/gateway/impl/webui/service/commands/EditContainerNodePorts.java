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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.IntStream;

import org.knime.core.node.port.MetaPortInfo;
import org.knime.core.node.workflow.NodeID;
import org.knime.core.node.workflow.WorkflowManager;
import org.knime.gateway.api.util.CoreUtil;
import org.knime.gateway.api.webui.entity.AddPortCommandEnt;
import org.knime.gateway.api.webui.entity.PortCommandEnt;
import org.knime.gateway.api.webui.entity.RemovePortCommandEnt;
import org.knime.gateway.api.webui.service.util.ServiceExceptions;
import org.knime.gateway.api.webui.service.util.ServiceExceptions.OperationNotAllowedException;

/**
 * Helper class to edit container node ports
 *
 * @author Benjamin Moser, KNIME GmbH, Konstanz, Germany
 * @author Kai Franze, KNIME GmbH
 */
final class EditContainerNodePorts implements EditPorts {

    private final WorkflowManager m_wfm;

    private final PortCommandEnt m_portCommandEnt;

    private MetaPortInfo[] m_reverseInfos;

    private MetaPortInfo[] m_newPortInfos;

    EditContainerNodePorts(final WorkflowManager wfm, final PortCommandEnt portCommandEnt) {
        m_wfm = wfm;
        m_portCommandEnt = portCommandEnt;
        m_reverseInfos = null;
    }

    @Override
    public int addPort(final AddPortCommandEnt addPortCommandEnt) throws OperationNotAllowedException {
        var currentPortInfos = getCurrentPortInfos();
        List<MetaPortInfo> newPortInfos = new ArrayList<>(Arrays.asList(currentPortInfos));
        var newPortType = CoreUtil.getPortType(addPortCommandEnt.getPortTypeId())
            .orElseThrow(() -> new OperationNotAllowedException("Unknown port type"));
        var newMetaPortInfo = MetaPortInfo.builder() //
            .setPortType(newPortType) //
            .setNewIndex(currentPortInfos.length) //
            .build();
        newPortInfos.add(newMetaPortInfo);
        m_newPortInfos = newPortInfos.toArray(MetaPortInfo[]::new);
        executeChanges(m_newPortInfos);
        return findNewPortIdx();
    }

    @Override
    public void removePort(final RemovePortCommandEnt removePortCommandEnt) throws OperationNotAllowedException {
        var newPortInfos = new ArrayList<>(Arrays.asList(getCurrentPortInfos()));
        int indexToRemove = removePortCommandEnt.getPortIndex();
        if (getContainerType() == CoreUtil.ContainerType.COMPONENT && indexToRemove == 0) {
            throw new ServiceExceptions.OperationNotAllowedException(
                "Can not remove fixed flow variable port at index 0");
        }
        try {
            newPortInfos.remove(indexToRemove);
        } catch (IndexOutOfBoundsException e) { // NOSONAR: Exception is thrown
            throw new ServiceExceptions.OperationNotAllowedException(e.getMessage());
        }
        executeChanges(newPortInfos.toArray(MetaPortInfo[]::new));
    }

    @Override
    public void undo() {
        updatePorts(m_reverseInfos);
        m_reverseInfos = null;
        m_newPortInfos = null;
    }

    @Override
    public boolean canUndo() {
        return true;
    }

    private Integer findNewPortIdx() throws NoSuchElementException {
        if (m_newPortInfos == null) {
            throw new NoSuchElementException("`m_newPortInfos` is not set");
        }
        return Arrays.stream(m_newPortInfos)//
            .filter(pt -> pt.getOldIndex() == -1)//
            .map(MetaPortInfo::getNewIndex)//
            .findFirst()//
            .orElseThrow(() -> new NoSuchElementException("Can't find new port index"));
    }

    private NodeID getNodeId() {
        return m_portCommandEnt.getNodeId().toNodeID(CoreUtil.getProjectWorkflowNodeID(m_wfm));
    }

    private void executeChanges(final MetaPortInfo[] newPortInfos) {
        setNewIndices(newPortInfos);
        m_reverseInfos = createReverseInfos(getCurrentPortInfos(), newPortInfos);
        updatePorts(newPortInfos);
    }

    /**
     * Update the port list based on the given information.
     *
     * @param newPortInfos The list of new port infos to apply. This parameter describes changes, and its semantics
     *            depends on previous state (more like a patch).
     */
    private void updatePorts(final MetaPortInfo[] newPortInfos) {
        if (m_portCommandEnt.getSide() == PortCommandEnt.SideEnum.INPUT) {
            updateInputPorts(newPortInfos);
        } else {
            updateOutputPorts(newPortInfos);
        }
    }

    /**
     * MetaPortInfo serves as both info-representing data structure and as patch (newIndex, oldIndex)
     *
     * @return New instances of {@link MetaPortInfo} describing the port configuration
     */
    private MetaPortInfo[] getCurrentPortInfos() {
        if (m_portCommandEnt.getSide() == PortCommandEnt.SideEnum.INPUT) {
            return getInputPortInfo();
        } else {
            return getOutputPortInfo();
        }
    }

    private CoreUtil.ContainerType getContainerType() {
        return CoreUtil.getContainerType(getNodeId(), m_wfm)
            .orElseThrow(() -> new UnsupportedOperationException("Node could not be found or is not a container"));
    }

    private MetaPortInfo[] getInputPortInfo() {
        if (getContainerType() == CoreUtil.ContainerType.METANODE) {
            return m_wfm.getMetanodeInputPortInfo(getNodeId());
        } else {
            return m_wfm.getSubnodeInputPortInfo(getNodeId());
        }
    }

    private MetaPortInfo[] getOutputPortInfo() {
        if (getContainerType() == CoreUtil.ContainerType.METANODE) {
            return m_wfm.getMetanodeOutputPortInfo(getNodeId());
        } else {
            return m_wfm.getSubnodeOutputPortInfo(getNodeId());
        }
    }

    /**
     * Replace all input ports of the target node with the given ports.
     *
     * @param newInPorts The new port list
     */
    private void updateInputPorts(final MetaPortInfo[] newInPorts) {
        if (getContainerType() == CoreUtil.ContainerType.METANODE) {
            m_wfm.changeMetaNodeInputPorts(getNodeId(), newInPorts);
        } else {
            m_wfm.changeSubNodeInputPorts(getNodeId(), newInPorts);
        }
    }

    /**
     * Replace all output ports of the target node with the given ports
     *
     * @param newOutPorts The new port list
     */
    private void updateOutputPorts(final MetaPortInfo[] newOutPorts) {
        if (getContainerType() == CoreUtil.ContainerType.METANODE) {
            m_wfm.changeMetaNodeOutputPorts(getNodeId(), newOutPorts);
        } else {
            m_wfm.changeSubNodeOutputPorts(getNodeId(), newOutPorts);
        }
    }

    /**
     * Create the port info patch describing the reverse operation between {@code originalInfos} and {@code newInfos}.
     *
     * @param originalInfos The original port info list.
     * @param newInfos The port info list describing the new state to be applied.
     * @return A port info list describing the reverse operation.
     */
    private static MetaPortInfo[] createReverseInfos(final MetaPortInfo[] originalInfos,
        final MetaPortInfo[] newInfos) {
        var reverse = Arrays.asList(new MetaPortInfo[originalInfos.length]);
        Arrays.stream(newInfos)
            // original index not present => port was added just now => don't need to include in undo list
            .filter(newInfo -> newInfo.getOldIndex() >= 0)
            // each other port is added to undo list with same info but index patch (newIndex, oldIndex) reversed
            .forEach(newInfo -> {
                var undoInfo = buildReverseInfo(originalInfos[newInfo.getOldIndex()], newInfo);
                // undo port infos will be at position before change
                reverse.set(newInfo.getOldIndex(), undoInfo);
            });
        // A port is removed by updating the ports with an info list that does not contain that port.
        //    So, we may have |new| < |original|. In that case, at this point, we have null elements in `reverse`.
        //    To undo, we need to re-introduce the port info element from `original` at that index.
        IntStream.range(0, reverse.size()) //
            .filter(i -> reverse.get(i) == null) //
            .forEach(i -> {
                var removedInfo = originalInfos[i];
                // reintroduced info is same as original info, only with no old index (since it is being (re-)introduced)
                var reintroducedInfo = buildReintroducedInfo(removedInfo, i);
                reverse.set(i, reintroducedInfo);
            });
        return reverse.toArray(MetaPortInfo[]::new);
    }

    private static MetaPortInfo buildReverseInfo(final MetaPortInfo originalInfo, final MetaPortInfo newInfo) {
        return MetaPortInfo.builder() //
            .setPortType(originalInfo.getType()) //
            .setIsConnected(originalInfo.isConnected()) //
            // "original"/old index of undoInfo is newIndex of change patch "newInfos"
            .setOldIndex(newInfo.getNewIndex()) //
            .setNewIndex(newInfo.getOldIndex()) //
            .build();
    }

    private static MetaPortInfo buildReintroducedInfo(final MetaPortInfo removedInfo, final int index) {
        return MetaPortInfo.builder(removedInfo)
            // connected ports cannot be removed, thus any re-introduced port can not be connected
            .setIsConnected(false) //
            .setMessage(null) //
            .setOldIndex(-1) //
            .setNewIndex(index) //
            .build();
    }

    private static void setNewIndices(final MetaPortInfo[] newPortList) {
        for (var portIndex = 0; portIndex < newPortList.length; portIndex++) {
            var updatedInfo = MetaPortInfo.builder(newPortList[portIndex]) //
                .setNewIndex(portIndex) //
                .build();
            newPortList[portIndex] = updatedInfo;
        }
    }

}

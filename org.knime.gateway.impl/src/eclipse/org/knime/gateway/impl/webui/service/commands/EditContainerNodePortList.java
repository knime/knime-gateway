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
 */
package org.knime.gateway.impl.webui.service.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.IntStream;

import org.knime.core.node.port.MetaPortInfo;
import org.knime.core.node.port.PortType;
import org.knime.gateway.api.util.CoreUtil;
import org.knime.gateway.api.webui.entity.AddPortCommandEnt;
import org.knime.gateway.api.webui.entity.PortCommandEnt;
import org.knime.gateway.api.webui.entity.RemovePortCommandEnt;
import org.knime.gateway.api.webui.service.util.ServiceExceptions;

/**
 * Implementations for modifying ports of a container node.
 *
 * @author Benjamin Moser, KNIME GmbH, Konstanz, Germany
 */
public class EditContainerNodePortList extends AbstractEditPortList {

    private List<MetaPortInfo> m_reverseInfos;

    public EditContainerNodePortList(final PortCommandEnt portCommandEnt) {
        super(portCommandEnt);
    }

    @Override
    public void undo() throws ServiceExceptions.OperationNotAllowedException {
        Objects.requireNonNull(m_reverseInfos, "Execute assumed to be called before undo");
        updatePortList(m_reverseInfos);
        m_reverseInfos = null;
    }

    @Override
    protected void addPort(final AddPortCommandEnt addPortCommandEnt) {
        List<MetaPortInfo> newPortInfos = new ArrayList<>(getCurrentPortInfos());
        var newPortType = CoreUtil.getPortType(addPortCommandEnt.getPortTypeId())
            .orElseThrow(() -> new UnsupportedOperationException("Unknown port type"));
        var newMetaPortInfo = MetaPortInfo.builder().setPortType(newPortType).build();
        newPortInfos.add(newMetaPortInfo);
        updatePortList(newPortInfos);
    }

    @Override
    protected void removePort(final RemovePortCommandEnt removePortCommandEnt)
        throws ServiceExceptions.OperationNotAllowedException {
        List<MetaPortInfo> newPortInfos = new ArrayList<>(getCurrentPortInfos());
        int indexToRemove = removePortCommandEnt.getPortIndex();
        if (getContainerType() == WorkflowCommandUtils.ContainerType.COMPONENT && indexToRemove == 0) {
            throw new ServiceExceptions.OperationNotAllowedException(
                "Can not remove fixed flow variable port at index 0");
        }
        try {
            newPortInfos.remove(indexToRemove);
        } catch (IndexOutOfBoundsException e) { // NOSONAR: Exception is thrown
            throw new ServiceExceptions.OperationNotAllowedException(e.getMessage());
        }
        updatePortList(newPortInfos);
    }

    /**
     * Update the port list based on the given information.
     *
     * @param newPortInfos The list of new port infos to apply. This parameter describes changes, and its semantics
     *            depends on previous state (more like a patch).
     */
    protected void updatePortList(final List<MetaPortInfo> newPortInfos) {
        reEnumerateNewIndices(newPortInfos);
        m_reverseInfos = createReverseInfos(getCurrentPortInfos(), newPortInfos);
        if (getPortCommandEnt().getSide() == PortCommandEnt.SideEnum.INPUT) {
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
    private List<MetaPortInfo> getCurrentPortInfos() {
        if (getPortCommandEnt().getSide() == PortCommandEnt.SideEnum.INPUT) {
            return getInputPortInfo();
        } else {
            return getOutputPortInfo();
        }
    }

    protected WorkflowCommandUtils.ContainerType getContainerType() {
        return WorkflowCommandUtils.getContainerType(getWorkflowManager(), getPortCommandEnt().getNodeId())
            .orElseThrow(() -> new UnsupportedOperationException("Node could not be found or is not a container"));
    }

    protected List<MetaPortInfo> getInputPortInfo() {
        if (getContainerType() == WorkflowCommandUtils.ContainerType.METANODE) {
            return Arrays.asList(getWorkflowManager().getMetanodeInputPortInfo(getNodeId()));
        } else {
            return Arrays.asList(getWorkflowManager().getSubnodeInputPortInfo(getNodeId()));
        }
    }

    protected List<MetaPortInfo> getOutputPortInfo() {
        if (getContainerType() == WorkflowCommandUtils.ContainerType.METANODE) {
            return Arrays.asList(getWorkflowManager().getMetanodeOutputPortInfo(getNodeId()));
        } else {
            return Arrays.asList(getWorkflowManager().getSubnodeOutputPortInfo(getNodeId()));
        }
    }

    /**
     * Replace all input ports of the target node with the given ports.
     *
     * @param newInPorts The new port list
     */
    protected void updateInputPorts(final List<MetaPortInfo> newInPorts) {
        var newInPortsArr = newInPorts.toArray(MetaPortInfo[]::new);
        if (getContainerType() == WorkflowCommandUtils.ContainerType.METANODE) {
            getWorkflowManager().changeMetaNodeInputPorts(getNodeId(), newInPortsArr);
        } else {
            getWorkflowManager().changeSubNodeInputPorts(getNodeId(), newInPortsArr);
        }
    }

    /**
     * Replace all output ports of the target node with the given ports
     *
     * @param newOutPorts The new port list
     */
    protected void updateOutputPorts(final List<MetaPortInfo> newOutPorts) {
        var newOutPortsArr = newOutPorts.toArray(MetaPortInfo[]::new);
        if (getContainerType() == WorkflowCommandUtils.ContainerType.METANODE) {
            getWorkflowManager().changeMetaNodeOutputPorts(getNodeId(), newOutPortsArr);
        } else {
            getWorkflowManager().changeSubNodeOutputPorts(getNodeId(), newOutPortsArr);
        }
    }

    /**
     * Create the port info patch describing the reverse operation between {@code originalInfos} and {@code newInfos}.
     *
     * @param originalInfos The original port info list.
     * @param newInfos The port info list describing the new state to be applied.
     * @return A port info list describing the reverse operation.
     */
    private static List<MetaPortInfo> createReverseInfos(final List<MetaPortInfo> originalInfos,
            final List<MetaPortInfo> newInfos) {
        var reverse = Arrays.asList(new MetaPortInfo[originalInfos.size()]);
        newInfos.stream()
            // original index not present => port was added just now => don't need to include in undo list
            .filter(newInfo -> newInfo.getOldIndex() >= 0)
            // each other port is added to undo list with same info but index patch (newIndex, oldIndex) reversed
            .forEach(newInfo -> {
                var undoInfo = buildReverseInfo(originalInfos.get(newInfo.getOldIndex()), newInfo);
                // undo port infos will be at position before change
                reverse.set(newInfo.getOldIndex(), undoInfo);
            });
        // A port is removed by updating the ports with an info list that does not contain that port.
        //    So, we may have |new| < |original|. In that case, at this point, we have null elements in `reverse`.
        //    To undo, we need to re-introduce the port info element from `original` at that index.
        IntStream.range(0, reverse.size()) //
            .filter(i -> reverse.get(i) == null) //
            .forEach(i -> {
                var removedInfo = originalInfos.get(i);
                // reintroduced info is same as original info, only with no old index (since it is being (re-)introduced)
                var reintroducedInfo = buildReintroducedInfo(removedInfo, i);
                reverse.set(i, reintroducedInfo);
            });
        return reverse;
    }

    private static MetaPortInfo buildReverseInfo(final MetaPortInfo originalInfo, MetaPortInfo newInfo) {
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

    private static void reEnumerateNewIndices(final List<MetaPortInfo> newPortList) {
        for (int portIndex = 0; portIndex < newPortList.size(); portIndex++) {
            var updatedInfo = MetaPortInfo.builder(newPortList.get(portIndex)) //
                .setNewIndex(portIndex) //
                .build();
            newPortList.set(portIndex, updatedInfo);
        }
    }

}

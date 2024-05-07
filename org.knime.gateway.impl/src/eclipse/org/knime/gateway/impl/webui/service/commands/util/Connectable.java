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
 *   Apr 23, 2024 (kai): created
 */
package org.knime.gateway.impl.webui.service.commands.util;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

import org.knime.core.node.port.PortType;
import org.knime.core.node.workflow.ConnectionContainer;
import org.knime.core.node.workflow.NodeContainer;
import org.knime.core.node.workflow.NodeID;
import org.knime.core.node.workflow.NodeUIInformation;
import org.knime.core.node.workflow.WorkflowManager;
import org.knime.gateway.impl.webui.service.commands.util.Geometry.Bounds;

/**
 * A workflow part (native node, metanode, container, workflow-in-bar, workflow-out-bar, ...) that can be connected via
 * workflow connections.
 * 
 * @author Kai Franze, KNIME GmbH, Germany
 * @author Benjamin Moser, KNIME GmbH, Germany
 */
public interface Connectable {

    NodeID getNodeId();

    Bounds getBounds();

    /**
     * A workflow part that can have outgoing connections.
     * <p>
     * A more precise name would be "Origin" but naming is chosen for consistency with existing code. Unlike in the
     * graph-theoretical sense, here the name "source" does <i>not</i> imply that there are no incoming connections.
     */
    interface Source extends Connectable {

        /**
         * @return All to-be-considered outgoing ports from this {@code Connectable}.
         */
        List<SourcePort> getSourcePorts();

        Collection<ConnectionContainer> getOutgoingConnections();

        /**
         * Name intentionally chosen to not be "out port". An instance corresponds to an out-port but not all of a
         * {@link Connectable}'s out-ports are {@code SourcePort}s in the sense of {@link Source#getSourcePorts()}.
         *
         * @param source The owner of this port
         * @param index Original port index (considering all ports, including implicit/hidden flow-variable port)
         * @param type The port type
         */
        record SourcePort(Source source, int index, PortType type) {
            Optional<ConnectionContainer> getOutgoingConnection() {
                return source().getOutgoingConnections().stream().filter(cc -> cc.getSourcePort() == this.index())
                    .findFirst();
            }
        }
    }

    /**
     * A workflow part that can have incoming connections.
     */
    interface Destination extends Connectable {

        /**
         * @return All to-be-considered incoming ports to this {@code Connectable}.
         */
        List<DestinationPort> getDestinationPorts();

        Collection<ConnectionContainer> getIncomingConnections();

        boolean isExecuted();

        /**
         * Name intentionally chosen to not be "in-port". An instance corresponds to an in-port but not all of a
         * {@link Connectable}'s in-ports are {@code DestinationPort}s in the sense of {@link Destination#getDestinationPorts()}.
         *
         * @param destination The owner of this port
         * @param index Original port index (considering all ports, including implicit/hidden flow-variable port)
         * @param type The port type
         */
        record DestinationPort(Destination destination, int index, PortType type) {

            Optional<ConnectionContainer> getIncomingConnection() {
                return destination().getIncomingConnections().stream().filter(cc -> cc.getDestPort() == this.index())
                    .findFirst();
            }
        }
    }

    /**
     * A node, but considering only its data ports, omitting the implicit flow variable port on native nodes.
     */
    record NodeDataPorts(NodeContainer nc, WorkflowManager parentWfm) implements Source, Destination {

        public NodeDataPorts(final NodeID nodeID, final WorkflowManager parentWfm) {
            this(parentWfm.getNodeContainer(nodeID), parentWfm);
            if (!parentWfm.containsNodeContainer(nodeID)) {
                throw new IllegalArgumentException("Given node ID %s not in workflow %s".formatted(nodeID, parentWfm));
            }
        }


        @Override
        public NodeID getNodeId() {
            return nc().getID();
        }

        @Override
        public Bounds getBounds() {
            return new Bounds( //
                Optional.ofNullable(nc().getUIInformation()) //
                    .map(NodeUIInformation::getBounds) //
                    .orElseThrow() //
            );
        }


        @Override
        public List<DestinationPort> getDestinationPorts() {
            return IntStream.range(getFirstDataPortIndex(), nc().getNrInPorts())//
                .mapToObj(nc()::getInPort)//
                .map(nodeInPort -> new DestinationPort(this, nodeInPort.getPortIndex(), nodeInPort.getPortType()))
                .toList();
        }

        @Override
        public List<SourcePort> getSourcePorts() {
            return IntStream.range(getFirstDataPortIndex(), nc().getNrOutPorts())//
                .mapToObj(nc()::getOutPort)//
                .map(nodeOutPort -> new SourcePort(this, nodeOutPort.getPortIndex(), nodeOutPort.getPortType()))
                .toList();
        }

        private int getFirstDataPortIndex() {
            return nc() instanceof WorkflowManager ? 0 : 1;
        }

        @Override
        public Collection<ConnectionContainer> getIncomingConnections() {
            return parentWfm().getIncomingConnectionsFor(getNodeId());
        }

        @Override
        public Collection<ConnectionContainer> getOutgoingConnections() {
            return parentWfm().getOutgoingConnectionsFor(getNodeId());
        }

        @Override
        public boolean isExecuted() {
            return nc().getNodeContainerState().isExecuted();
        }

    }

    /**
     * The workflow-out ports bar of a metanode workflow.
     * The input ports of the Workflow <i>out</i> ports-bar correspond to the ports <i>leaving</i> the workflow i.e.,
     * the workflow <i>out</i> ports.
     */
    record WorkflowOutPortsBar(WorkflowManager wfm) implements Destination {

        @Override
        public List<DestinationPort> getDestinationPorts() {
            return IntStream.range(0, wfm().getNrWorkflowOutgoingPorts())//
                .mapToObj(wfm()::getInPort)//
                .map(nodeInPort -> new DestinationPort(this, nodeInPort.getPortIndex(), nodeInPort.getPortType()))
                .toList();
        }

        @Override
        public Collection<ConnectionContainer> getIncomingConnections() {
            return wfm().getParent().getOutgoingConnectionsFor(getNodeId());
        }

        @Override
        public boolean isExecuted() {
            return wfm().getNodeContainerState().isExecuted();
        }

        @Override
        public NodeID getNodeId() {
            return wfm().getID();
        }

        @Override
        public Bounds getBounds() {
            return Optional.ofNullable(wfm().getOutPortsBarUIInfo())//
                .map(NodeUIInformation::getBounds)//
                .map(Bounds::new) //
                .orElse(new Bounds(Geometry.Point.MAX_VALUE, 0, 0));
        }
    }

    /**
     * The workflow-in ports bar of a metanode workflow.
     * The output ports of the Workflow <i>in</i> ports-bar correspond to the ports <i>entering</i> the workflow i.e., the
     * workflow <i>in</i> ports.
     */
    record WorkflowInPortsBar(WorkflowManager wfm) implements Source {

        @Override
        public List<SourcePort> getSourcePorts() {
            return IntStream.range(0, wfm().getNrWorkflowIncomingPorts())//
                .mapToObj(wfm()::getOutPort)//
                .map(nodeOutPort -> new SourcePort(this, nodeOutPort.getPortIndex(), nodeOutPort.getPortType()))
                .toList();
        }

        @Override
        public Collection<ConnectionContainer> getOutgoingConnections() {
            return wfm().getParent().getIncomingConnectionsFor(getNodeId());
        }

        @Override
        public NodeID getNodeId() {
            return wfm().getID();
        }

        @Override
        public Bounds getBounds() {
            return Optional.ofNullable(wfm().getInPortsBarUIInfo())//
                .map(NodeUIInformation::getBounds)//
                .map(Bounds::new) //
                .orElse(new Bounds(Geometry.Point.MIN_VALUE, 0, 0));
        }

    }
}

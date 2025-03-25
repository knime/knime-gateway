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

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.knime.core.node.port.PortType;
import org.knime.core.node.port.flowvariable.FlowVariablePortObject;
import org.knime.core.node.workflow.ConnectionContainer;
import org.knime.core.node.workflow.NodeContainer;
import org.knime.core.node.workflow.NodeID;
import org.knime.core.node.workflow.NodeInPort;
import org.knime.core.node.workflow.NodeOutPort;
import org.knime.core.node.workflow.WorkflowManager;
import org.knime.gateway.api.util.CoreUtil;
import org.knime.gateway.impl.webui.service.commands.util.Geometry.Bounds;

/**
 * Data structures for backing logic to automatically connect workflow parts.
 * <p>
 * A Connectable is a workflow part (native node, metanode, container, workflow-in-bar, workflow-out-bar, ...) that can
 * be connected via workflow connections.
 * <p>
 * Implementations of this interface provide views on workflow parts from different perspectives.
 *
 * @author Kai Franze, KNIME GmbH, Germany
 * @author Benjamin Moser, KNIME GmbH, Germany
 */
public interface Connectable {

    /**
     * @return the id of the node or workflow (in case of metanode port bars) to connect
     */
    NodeID getNodeId();

    /**
     * @return the bounds of the connectable's visual representation
     */
    Bounds getBounds();

    /**
     * @return The containing workflow manager
     */
    WorkflowManager wfm();

    /**
     * A {@link Source} is a connectable that can have out-ports. The {@link Port}s provided through this interface
     * represent be a subset of the {@link NodeOutPort}s the connectable has.
     * <p>
     * A more precise name would be "Origin" but naming is chosen for consistency with existing code. Unlike in the
     * graph-theoretical sense, here the name "source" does <i>not</i> imply that there are no incoming connections.
     */
    interface Source extends Connectable {

        /**
         * @return All to-be-considered outgoing ports from this {@code Connectable}.
         */
        @SuppressWarnings("java:S1452") // wildcard types are narrowed by implementing classes
        List<? extends SourcePort<? extends Source>> getSourcePorts();

    }

    /**
     * A {@link Source} of {@link FlowSourcePort}s.
     */
    interface FlowSource extends Source {
        @Override
        List<FlowSourcePort> getSourcePorts();
    }

    /**
     * @see Source
     */
    interface Destination extends Connectable {

        /**
         * @return All to-be-considered incoming ports to this {@code Connectable}.
         */
        @SuppressWarnings("java:S1452") // wildcard types are narrowed by implementing classes
        List<? extends DestinationPort<? extends Destination>> getDestinationPorts();

    }

    /**
     * @see FlowSource
     */
    interface FlowDestination extends Destination {
        @Override
        List<FlowDestinationPort> getDestinationPorts();
    }

    /**
     * Identifies a port on a {@link Connectable}
     *
     * @param <O> The type of the owner of this port.
     */
    @SuppressWarnings("java:S2974") // should only be subclassed but not instantiated itself.
    // Not abstract because this causes another linting issue since no abstract methods are declared.
    class Port<O extends Connectable> {
        final int m_index;

        final PortType m_type;

        final O m_owner;

        private Port(final O owner, final int index, final PortType type) {
            this.m_index = index;
            this.m_type = type;
            this.m_owner = owner;
        }

        O owner() {
            return this.m_owner;
        }

        int index() {
            return this.m_index;
        }

        PortType type() {
            return this.m_type;
        }

        @Override
        public boolean equals(final Object otherObject) {
            if (!(otherObject instanceof Port<?> other)) {
                return false;
            }
            return this.m_index == other.m_index && this.m_type.equals(other.m_type)
                && this.m_owner.equals(other.m_owner);
        }

        @Override
        public int hashCode() {
            return new HashCodeBuilder().append(this.index()).append(this.type()).append(this.owner()).build();
        }

        @Override
        public String toString() {
            return "%s/%s (%s)".formatted(owner(), index(), type());
        }
    }

    /**
     * @apiNote Name intentionally chosen to not be "out port". An instance corresponds to an out-port but not all of a
     *          {@link Connectable}'s out-ports are {@code SourcePort}s in the sense of an implementation of
     *          {@link Source#getSourcePorts()}.
     */
    class SourcePort<O extends Source> extends Port<O> {

        SourcePort(final O owner, final NodeOutPort outPort) {
            this(owner, outPort.getPortIndex(), outPort.getPortType());
        }

        public SourcePort(final O owner, final int index, final PortType type) {
            super(owner, index, type);
        }

        public Set<ConnectionContainer> getOutgoingConnections() {
            return m_owner.wfm().getOutgoingConnectionsFor(m_owner.getNodeId(), this.m_index);
        }

        /**
         * @apiNote This property is symmetric.
         * @param destinationPort
         * @return
         */
        boolean isCompatibleWith(final DestinationPort<?> destinationPort) {
            return CoreUtil.arePortTypesCompatible(this.type(), destinationPort.type());
        }
    }

    /**
     * A {@link SourcePort} with the additional property of being visible or hidden.
     */
    @SuppressWarnings("java:S2160") // Do not need to override equals.
    // #isVisible is a derived property and is not needed for establishing equality.
    // If two objects differ in #isVisisble, they will necessarily also differ in other properties.
    class FlowSourcePort extends SourcePort<FlowSource> {
        private final boolean m_isVisible;

        FlowSourcePort(final FlowSource owner, final int index, final PortType type, final boolean isVisible) {
            super(owner, index, type);
            m_isVisible = isVisible;
        }

        FlowSourcePort(final FlowSource owner, final NodeOutPort outPort, final boolean isVisible) {
            super(owner, outPort);
            m_isVisible = isVisible;
        }

        boolean isVisible() {
            return m_isVisible;
        }

        @Override
        boolean isCompatibleWith(final DestinationPort<?> destinationPort) {
            if (!(destinationPort instanceof FlowDestinationPort flowDestinationPort)) {
                return false;
            }
            if (!super.isCompatibleWith(destinationPort)) {
                return false;
            }
            var sourceHasVisible = this.owner().getSourcePorts().stream().anyMatch(FlowSourcePort::isVisible);
            var destinationHasVisible =
                flowDestinationPort.owner().getDestinationPorts().stream().anyMatch(FlowDestinationPort::isVisible);
            var hasOnlyHiddenOrOnlyVisible = sourceHasVisible == destinationHasVisible;
            if (hasOnlyHiddenOrOnlyVisible) {
                // connecting visible-to-visible is also covered by vanilla connect command, but it would be expected
                // that this is connected also when considering only flow variables
                return true;
            }
            // otherwise, connect to/from hidden
            var sourceVisible = this.isVisible();
            var destinationVisible = flowDestinationPort.isVisible();
            return sourceVisible ^ destinationVisible;
        }
    }

    /**
     * @see SourcePort
     */
    class DestinationPort<O extends Destination> extends Port<O> {

        /**
         * @param destination The owner of this port
         * @param index Original port index (considering all ports, including implicit/hidden flow-variable port)
         * @param type The port type
         */
        public DestinationPort(final O destination, final int index, final PortType type) {
            super(destination, index, type);
        }

        DestinationPort(final O destination, final NodeInPort inPort) {
            this(destination, inPort.getPortIndex(), inPort.getPortType());
        }

        Optional<ConnectionContainer> getIncomingConnection() {
            return Optional.ofNullable(m_owner.wfm().getIncomingConnectionFor(m_owner.getNodeId(), this.m_index));
        }
    }

    /**
     * @see FlowSourcePort
     */
    @SuppressWarnings("java:S2160") // Do not need to override equals.
    // #isVisible is a derived property and is not needed for establishing equality.
    // If two objects differ in #isVisisble, they will necessarily also differ in other properties.
    class FlowDestinationPort extends DestinationPort<FlowDestination> {

        private final boolean m_isHidden;

        FlowDestinationPort(final FlowDestination destination, final int index, final PortType type,
            final boolean isHidden) {
            super(destination, index, type);
            m_isHidden = isHidden;
        }

        FlowDestinationPort(final FlowDestination destination, final NodeInPort inPort, final boolean isHidden) {
            super(destination, inPort);
            m_isHidden = isHidden;
        }

        boolean isVisible() {
            return !m_isHidden;
        }
    }

    abstract class Node implements Source, Destination {

        final NodeContainer m_nc;

        private final WorkflowManager m_parentWfm;

        protected Node(final NodeContainer nc, final WorkflowManager parentWfm) {
            this.m_nc = nc;
            this.m_parentWfm = parentWfm;
        }

        protected Node(final NodeID nodeID, final WorkflowManager parentWfm) {
            this(parentWfm.getNodeContainer(nodeID), parentWfm);
            if (!parentWfm.containsNodeContainer(nodeID)) {
                throw new IllegalArgumentException("Given node ID %s not in workflow %s".formatted(nodeID, parentWfm));
            }
        }

        @Override
        public WorkflowManager wfm() {
            return m_parentWfm;
        }

        @Override
        public NodeID getNodeId() {
            return m_nc.getID();
        }

        @Override
        public Bounds getBounds() {
            return Optional.ofNullable(m_nc.getUIInformation()) //
                .flatMap(Bounds::of).orElseThrow();
        }

        @Override
        public int hashCode() {
            return this.getNodeId().hashCode();
        }

        @Override
        public boolean equals(final Object otherObject) {
            if (!(otherObject instanceof Node other)) {
                return false;
            }
            return this.getNodeId().equals(other.getNodeId());
        }
    }

    /**
     * A view on a node that reveals only its visible ports, omitting the hidden flow variable port on native nodes
     * (still includes visible flow variable ports).
     */
    final class NodeData extends Node {

        public NodeData(final NodeID nodeID, final WorkflowManager parentWfm) {
            super(nodeID, parentWfm);
        }

        @Override
        public List<DestinationPort<NodeData>> getDestinationPorts() {
            return IntStream.range(getFirstDataPortIndex(), m_nc.getNrInPorts())//
                .mapToObj(m_nc::getInPort)//
                .map(nodeInPort -> new DestinationPort<>(this, nodeInPort.getPortIndex(), nodeInPort.getPortType()))
                .toList();
        }

        @Override
        public List<SourcePort<NodeData>> getSourcePorts() {
            return IntStream.range(getFirstDataPortIndex(), m_nc.getNrOutPorts())//
                .mapToObj(m_nc::getOutPort)//
                .map(nodeOutPort -> new SourcePort<>(this, nodeOutPort))
                .toList();
        }

        private int getFirstDataPortIndex() {
            return m_nc instanceof WorkflowManager ? 0 : 1;
        }
    }

    /**
     * A view on a node that reveals only flow variable ports (visible and hidden)
     */
    final class NodeFlow extends Node implements FlowSource, FlowDestination {

        public NodeFlow(final NodeID nodeID, final WorkflowManager parentWfm) {
            super(nodeID, parentWfm);
        }

        private boolean hasHiddenPort() {
            // Only metanodes do not have hidden ports
            return !(m_nc instanceof WorkflowManager);
        }

        private int dataPortsStartIndex() {
            return hasHiddenPort() ? 1 : 0;
        }

        @Override
        public List<FlowSourcePort> getSourcePorts() {
            Stream<FlowSourcePort> hiddenPorts = !this.hasHiddenPort() //
                ? Stream.empty() //
                : Stream.of(new FlowSourcePort(this, m_nc.getOutPort(0), false));
            var visiblePorts = IntStream.range(dataPortsStartIndex(), m_nc.getNrOutPorts())//
                .mapToObj(m_nc::getOutPort)//
                .filter(outPort -> outPort.getPortType().equals(FlowVariablePortObject.TYPE)) //
                .map(nodeOutPort -> new FlowSourcePort(this, nodeOutPort, true));
            return Stream.concat(hiddenPorts, visiblePorts).toList();
        }

        @Override
        public List<FlowDestinationPort> getDestinationPorts() {
            Stream<FlowDestinationPort> hiddenPorts = !this.hasHiddenPort() //
                ? Stream.empty() //
                : Stream.of(new FlowDestinationPort(this, m_nc.getInPort(0), true));
            var visiblePorts = IntStream.range(dataPortsStartIndex(), m_nc.getNrInPorts())//
                .mapToObj(m_nc::getInPort)//
                .filter(inPort -> inPort.getPortType().equals(FlowVariablePortObject.TYPE)) //
                .map(inPort -> new FlowDestinationPort(this, inPort, false));
            return Stream.concat(hiddenPorts, visiblePorts).toList();
        }
    }

    abstract class PortsBar implements Connectable {

        private final WorkflowManager m_wfm;

        protected PortsBar(final WorkflowManager mWfm) {
            m_wfm = mWfm;
        }

        @Override
        public WorkflowManager wfm() {
            return m_wfm;
        }

    }

    abstract class InPortsBar extends PortsBar implements Source {

        protected InPortsBar(final WorkflowManager mWfm) {
            super(mWfm);
        }

        static <T extends PortsBar> Stream<NodeOutPort> getSourcePorts(final T bar) {
            return IntStream.range(0, bar.wfm().getNrWorkflowIncomingPorts())//
                .mapToObj(bar.wfm()::getWorkflowIncomingPort);//
        }

        @Override
        public NodeID getNodeId() {
            return super.wfm().getID();
        }

        @Override
        public Bounds getBounds() {
            return Optional.ofNullable(super.wfm().getInPortsBarUIInfo())//
                .flatMap(Bounds::of) //
                .orElse(Bounds.MIN_VALUE);
        }

        @Override
        public boolean equals(final Object otherObject) {
            return (otherObject instanceof InPortsBar other) && this.getNodeId().equals(other.getNodeId());
        }

        @Override
        public int hashCode() {
            return new HashCodeBuilder().append(this.getNodeId()).append(true).build();
        }
    }

    /**
     * A view on a workflow-in ports bar of a metanode workflow that reveals only its visible ports.
     * <p>
     * The output ports of the Workflow <i>in</i> ports-bar correspond to the ports <i>entering</i> the workflow i.e.,
     * the workflow <i>in</i> ports.
     */
    final class InPortsBarData extends InPortsBar {
        public InPortsBarData(final WorkflowManager wfm) {
            super(wfm);
        }

        @Override
        public List<SourcePort<InPortsBarData>> getSourcePorts() {
            return getSourcePorts(this).map(nodeOutPort -> new SourcePort<>(this, nodeOutPort)).toList();
        }

    }

    /**
     * A view on a workflow-in ports bar of a metanode workflow that reveals only flow-variable ports, including hidden
     * ones.
     */
    class InPortsBarFlow extends InPortsBar implements FlowSource {

        public InPortsBarFlow(final WorkflowManager wfm) {
            super(wfm);
        }

        @Override
        public List<FlowSourcePort> getSourcePorts() {
            var isVisible = true; // ports bars never have hidden ports
            return getSourcePorts(this) //
                .filter(nodeInPort -> nodeInPort.getPortType().equals(FlowVariablePortObject.TYPE)) //
                .map(nodeOutPort -> new FlowSourcePort(this, nodeOutPort, isVisible)).toList();
        }

    }

    /**
     * @see InPortsBar
     */
    abstract class OutPortsBar extends PortsBar implements Destination {

        protected OutPortsBar(final WorkflowManager mWfm) {
            super(mWfm);
        }

        static <T extends PortsBar> Stream<NodeInPort> getDestinationPorts(final T bar) {
            return IntStream.range(0, bar.wfm().getNrWorkflowOutgoingPorts())//
                .mapToObj(bar.wfm()::getWorkflowOutgoingPort);//
        }

        @Override
        public NodeID getNodeId() {
            return super.wfm().getID();
        }

        @Override
        public Bounds getBounds() {
            return Optional.ofNullable(super.wfm().getOutPortsBarUIInfo())//
                .flatMap(Bounds::of) //
                .orElse(Bounds.MAX_VALUE);
        }

        @Override
        public boolean equals(final Object otherObject) {
            return (otherObject instanceof OutPortsBar other) && this.getNodeId().equals(other.getNodeId());
        }

        @Override
        public int hashCode() {
            return new HashCodeBuilder().append(this.getNodeId()).append(false).build();
        }

    }

    /**
     * @see InPortsBarData
     */
    final class OutPortsBarData extends OutPortsBar {

        public OutPortsBarData(final WorkflowManager wfm) {
            super(wfm);
        }

        @Override
        public List<DestinationPort<OutPortsBarData>> getDestinationPorts() {
            return getDestinationPorts(this).map(nodeInPort -> new DestinationPort<>(this, nodeInPort)).toList();
        }
    }

    /**
     * @see InPortsBarFlow
     */
    class OutPortsBarFlow extends OutPortsBar implements FlowDestination {

        public OutPortsBarFlow(final WorkflowManager wfm) {
            super(wfm);
        }

        @Override
        public List<FlowDestinationPort> getDestinationPorts() {
            return getDestinationPorts(this) //
                .filter(nodeInPort -> nodeInPort.getPortType().equals(FlowVariablePortObject.TYPE)) //
                .map(nodeInPort -> new FlowDestinationPort(this, nodeInPort, false)).toList();
        }
    }

}

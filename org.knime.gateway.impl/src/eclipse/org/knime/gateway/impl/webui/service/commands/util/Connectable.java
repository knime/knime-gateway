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
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.knime.core.node.port.PortType;
import org.knime.core.node.workflow.ConnectionContainer;
import org.knime.core.node.workflow.NodeContainer;
import org.knime.core.node.workflow.NodeID;
import org.knime.core.node.workflow.NodeInPort;
import org.knime.core.node.workflow.NodeOutPort;
import org.knime.core.node.workflow.NodeUIInformation;
import org.knime.core.node.workflow.WorkflowManager;
import org.knime.gateway.api.webui.entity.ConnectableEnt;
import org.knime.gateway.impl.webui.service.commands.util.Geometry.Bounds;

/**
 * A connectable represents a node that could be automatically connected. The following interfaces and implementations
 * were introduced to isolate those concerns and make the whole thing more readable and understandable.
 *
 * @author Kai Franze, KNIME GmbH, Germany
 */
interface Connectable {

    Comparator<Connectable> NORTH_WEST_ORDERING = Comparator.comparing(s -> s.getBounds().leftTop());

    NodeID getNodeId();

    ConnectableEnt getConnectableEnt();

    Bounds getBounds();

    default boolean isLeftTo(final Connectable connectable) {
        return getBounds().xRange().start() < connectable.getBounds().xRange().start();
    }

    default boolean isRightTo(final Connectable connectable) {
        return getBounds().xRange().start() > connectable.getBounds().xRange().start();
    }

    /**
     * @param connectableEnt
     * @param wfm
     * @return A connectable derived from a given connectable entity
     */
    static Connectable of(final ConnectableEnt connectableEnt, final WorkflowManager wfm) {
        final var isMetanodeInPortsBar = Boolean.TRUE.equals(connectableEnt.isMetanodeInPortsBar());
        final var isMetanodeOutPortsBar = Boolean.TRUE.equals(connectableEnt.isMetanodeOutPortsBar());
        return (isMetanodeInPortsBar || isMetanodeOutPortsBar) //
            ? new MetanodePortsBarConnectable(connectableEnt, wfm, isMetanodeInPortsBar, isMetanodeOutPortsBar) //
            : new DefaultConnectable(connectableEnt, wfm);
    }

    /**
     * For everything that is not source / destination specific and not a metanode ports bar
     */
    class DefaultConnectable implements Connectable {

        protected final ConnectableEnt m_connectableEnt;

        protected final WorkflowManager m_wfm;

        protected final NodeID m_nodeId;

        protected final NodeContainer m_nc;

        protected final int m_firstDataPortIdx;

        private DefaultConnectable(final ConnectableEnt connectableEnt, final WorkflowManager wfm) {
            m_connectableEnt = connectableEnt;
            m_wfm = wfm;
            m_nodeId = connectableEnt.getNodeId().toNodeID(wfm);
            m_nc = NodeConnector.getNodeContainerOrSelf(m_nodeId, wfm);
            m_firstDataPortIdx = m_nc instanceof WorkflowManager ? 0 : 1;
        }

        @Override
        public NodeID getNodeId() {
            return m_nodeId;
        }

        @Override
        public ConnectableEnt getConnectableEnt() {
            return m_connectableEnt;
        }

        @Override
        public Bounds getBounds() {
            final var uiInfo = Optional.ofNullable(m_nc.getUIInformation()).orElseThrow();
            final var bounds = Optional.ofNullable(uiInfo.getBounds()).orElseThrow();
            return new Bounds(bounds);
        }

        @Override
        public boolean equals(final Object other) {
            if (this == other) {
                return true;
            }

            if (!(other instanceof Connectable)) {
                return false;
            }

            final var otherConnectable = (Connectable)other;
            final var otherConnectableEnt = otherConnectable.getConnectableEnt();
            return new EqualsBuilder()//
                .append(m_nodeId, otherConnectable.getNodeId())//
                .append(m_connectableEnt.isMetanodeInPortsBar(), otherConnectableEnt.isMetanodeInPortsBar())//
                .append(m_connectableEnt.isMetanodeOutPortsBar(), otherConnectableEnt.isMetanodeOutPortsBar())//
                .build();
        }

        @Override
        public int hashCode() {
            return new HashCodeBuilder()//
                .append(m_nodeId)//
                .append(m_connectableEnt.isMetanodeInPortsBar())//
                .append(m_connectableEnt.isMetanodeOutPortsBar())//
                .build();
        }

    }

    /**
     * For everything that is not source / destination specific but a metanode ports bar
     */
    class MetanodePortsBarConnectable implements Connectable {

        protected final ConnectableEnt m_connectableEnt;

        protected final WorkflowManager m_wfm;

        protected final NodeID m_nodeId;

        protected final boolean m_isMetanodeInPortsBar;

        protected final boolean m_isMetanodeOutPortsBar;

        protected final NodeContainer m_nc;

        protected final int m_firstDataPortIdx;

        private MetanodePortsBarConnectable(final ConnectableEnt connectableEnt, final WorkflowManager wfm,
            final boolean isMetanodeInPortsBar, final boolean isMetanodeOutPortsBar) {
            m_connectableEnt = connectableEnt;
            m_wfm = wfm;
            m_nodeId = connectableEnt.getNodeId().toNodeID(wfm);
            m_isMetanodeInPortsBar = isMetanodeInPortsBar;
            m_isMetanodeOutPortsBar = isMetanodeOutPortsBar;
            m_nc = NodeConnector.getNodeContainerOrSelf(m_nodeId, wfm);
            m_firstDataPortIdx = 0;
        }

        @Override
        public NodeID getNodeId() {
            return m_nodeId;
        }

        @Override
        public ConnectableEnt getConnectableEnt() {
            return m_connectableEnt;
        }

        @Override
        public Bounds getBounds() {
            return m_isMetanodeInPortsBar //
                ? MetanodeInPortsBarDestination.getBounds(m_wfm) //
                : MetanodeOutPortsBarSource.getBounds(m_wfm);
        }

        @Override
        public boolean equals(final Object other) {
            if (this == other) {
                return true;
            }

            if (!(other instanceof Connectable)) {
                return false;
            }

            final var otherConnectable = (Connectable)other;
            final var otherConnectableEnt = otherConnectable.getConnectableEnt();
            return new EqualsBuilder()//
                .append(m_nodeId, otherConnectable.getNodeId())//
                .append(m_connectableEnt.isMetanodeInPortsBar(), otherConnectableEnt.isMetanodeInPortsBar())//
                .append(m_connectableEnt.isMetanodeOutPortsBar(), otherConnectableEnt.isMetanodeOutPortsBar())//
                .build();
        }

        @Override
        public int hashCode() {
            return new HashCodeBuilder()//
                .append(m_nodeId)//
                .append(m_connectableEnt.isMetanodeInPortsBar())//
                .append(m_connectableEnt.isMetanodeOutPortsBar())//
                .build();
        }

    }

    /**
     * For source nodes
     *
     * @author Kai Franze, KNIME GmbH, Germany
     */
    interface Source extends Connectable {

        List<PortType> getPorts();

        int getFirstDataPortIdx();

        default boolean hasEnoughPorts() {
            return getPorts().size() > getFirstDataPortIdx();
        }

        Collection<ConnectionContainer> getConnections();

        default boolean isPortConnected(final int sourcePortIdx) {
            return getConnections().stream().anyMatch(cc -> cc.getSourcePort() == sourcePortIdx);
        }

        /**
         * @param connectable
         * @return A source derived from a given connectable
         */
        static Optional<Source> of(final Connectable connectable) {
            if (connectable instanceof DefaultConnectable dc
                && DefaultSource.isValid(dc.m_wfm, dc.m_nc, dc.m_firstDataPortIdx)) {
                return Optional.of(new DefaultSource(dc.m_connectableEnt, dc.m_wfm));
            }

            if (connectable instanceof MetanodePortsBarConnectable mpbc
                && MetanodeOutPortsBarSource.isValid(mpbc.m_wfm, mpbc.m_nc, mpbc.m_isMetanodeOutPortsBar)) {
                return Optional.of(new MetanodeOutPortsBarSource(mpbc.m_connectableEnt, mpbc.m_wfm,
                    mpbc.m_isMetanodeInPortsBar, mpbc.m_isMetanodeOutPortsBar));
            }

            return Optional.empty();
        }

    }

    /**
     * For source nodes that are not metanode output port bars
     */
    final class DefaultSource extends DefaultConnectable implements Source {

        private DefaultSource(final ConnectableEnt connectableEnt, final WorkflowManager wfm) {
            super(connectableEnt, wfm);
        }

        @Override
        public List<PortType> getPorts() {
            final var portCount = m_nc.getNrOutPorts();
            if (portCount == 0) {
                return Collections.emptyList();
            }
            return IntStream.range(0, portCount)//
                .mapToObj(m_nc::getOutPort)//
                .map(NodeOutPort::getPortType)//
                .toList();
        }

        @Override
        public int getFirstDataPortIdx() {
            return m_firstDataPortIdx;
        }

        @Override
        public Collection<ConnectionContainer> getConnections() {
            return m_wfm.getOutgoingConnectionsFor(m_nodeId);
        }

        private static boolean isValid(final WorkflowManager wfm, final NodeContainer nc, final int firstDataPortIdx) {
            return wfm.containsNodeContainer(nc.getID()) && (nc.getNrOutPorts() > firstDataPortIdx);
        }

    }

    /**
     * For source nodes that are metanode output ports bars
     */
    final class MetanodeOutPortsBarSource extends MetanodePortsBarConnectable implements Source {

        private MetanodeOutPortsBarSource(final ConnectableEnt connectableEnt, final WorkflowManager wfm,
            final boolean isMetanodeInPortsBar, final boolean isMetanodeOutPortsBar) {
            super(connectableEnt, wfm, isMetanodeInPortsBar, isMetanodeOutPortsBar);
        }

        @Override
        public List<PortType> getPorts() {
            if (m_nc instanceof WorkflowManager subWfm) {
                final var portCount = subWfm.getNrWorkflowIncomingPorts();
                return IntStream.range(0, portCount)//
                    .mapToObj(m_nc::getInPort)//
                    .map(NodeInPort::getPortType)//
                    .toList();
            }
            return Collections.emptyList(); // Should never happen
        }

        @Override
        public int getFirstDataPortIdx() {
            return m_firstDataPortIdx;
        }

        @Override
        public Collection<ConnectionContainer> getConnections() {
            return m_wfm.getParent().getIncomingConnectionsFor(m_nodeId);
        }

        private static boolean isValid(final WorkflowManager wfm, final NodeContainer nc,
            final boolean isMetanodeOutPortsBar) {
            if (nc instanceof WorkflowManager subWfm) {
                final var wfmContainsNc = wfm.containsNodeContainer(nc.getID());
                final var subWfmEqualsWfm = subWfm.equals(wfm);
                final var parentWfmContainsNc = subWfm.getParent().containsNodeContainer(nc.getID());
                return !wfmContainsNc && subWfmEqualsWfm && parentWfmContainsNc && isMetanodeOutPortsBar;
            }
            return false;
        }

        private static Bounds getBounds(final WorkflowManager wfm) {
            return Optional.ofNullable(wfm.getOutPortsBarUIInfo())//
                .map(NodeUIInformation::getBounds)//
                .map(Bounds::new)//
                .orElse(new Bounds(Integer.MIN_VALUE + 1, 0, 1, 1)); // Max to the right without integer overflow
        }

    }

    /**
     * For destination nodes
     *
     * @author Kai Franze, KNIME GmbH, Germany
     */
    interface Destination extends Connectable {

        List<PortType> getPorts();

        int getFirstDataPortIdx();

        default boolean hasEnoughPorts() {
            return getPorts().size() > getFirstDataPortIdx();
        }

        Collection<ConnectionContainer> getConnections();

        default Optional<ConnectionContainer> getConnection(final int destinationPortIdx) {
            return getConnections().stream()//
                .filter(cc -> cc.getDestPort() == destinationPortIdx)//
                .findFirst();
        }

        default boolean isPortConnected(final int destinationPortIdx) {
            return getConnection(destinationPortIdx).isPresent();
        }

        default boolean hasIncomingConnectionFrom(final Collection<Destination> plannedDestinations) {
            return getConnections().stream()//
                .anyMatch(connection -> plannedDestinations.stream()//
                    .map(Destination::getNodeId)//
                    // True if any incoming connection starts from any node within the collection,
                    // false otherwise. Also false if the stream is empty.
                    .anyMatch(nodeId -> connection.getSource().equals(nodeId)));

        }

        default boolean intersects(final Connectable connectable) {
            return getBounds().xRange().intersects(connectable.getBounds().xRange());
        }

        boolean isExecuted();

        static Optional<Destination> of(final Connectable connectable) {
            if (connectable instanceof DefaultConnectable dc
                && DefaultDestination.isValid(dc.m_wfm, dc.m_nc, dc.m_firstDataPortIdx)) {
                return Optional.of(new DefaultDestination(dc.m_connectableEnt, dc.m_wfm));
            }

            if (connectable instanceof MetanodePortsBarConnectable mpbc
                && MetanodeInPortsBarDestination.isValid(mpbc.m_wfm, mpbc.m_nc, mpbc.m_isMetanodeInPortsBar)) {
                return Optional.of(new MetanodeInPortsBarDestination(mpbc.m_connectableEnt, mpbc.m_wfm,
                    mpbc.m_isMetanodeInPortsBar, mpbc.m_isMetanodeOutPortsBar));
            }

            return Optional.empty();
        }
    }

    /**
     * For regular destination nodes
     */
    final class DefaultDestination extends DefaultConnectable implements Destination {

        private DefaultDestination(final ConnectableEnt connectableEnt, final WorkflowManager wfm) {
            super(connectableEnt, wfm);
        }

        @Override
        public List<PortType> getPorts() {
            final var portCount = m_nc.getNrInPorts();
            if (portCount == 0) {
                return Collections.emptyList();
            }
            return IntStream.range(0, portCount)//
                .mapToObj(m_nc::getInPort)//
                .map(NodeInPort::getPortType)//
                .toList();
        }

        @Override
        public int getFirstDataPortIdx() {
            return m_firstDataPortIdx;
        }

        @Override
        public Collection<ConnectionContainer> getConnections() {
            return m_wfm.getIncomingConnectionsFor(m_nodeId);
        }

        @Override
        public boolean isExecuted() {
            return m_nc.getNodeContainerState().isExecuted();
        }

        private static boolean isValid(final WorkflowManager wfm, final NodeContainer nc, final int firstDataPortIdx) {
            return wfm.containsNodeContainer(nc.getID()) && (nc.getNrInPorts() > firstDataPortIdx);
        }

    }

    /**
     * For destination nodes that are metanode input ports bars
     */
    final class MetanodeInPortsBarDestination extends MetanodePortsBarConnectable implements Destination {

        private MetanodeInPortsBarDestination(final ConnectableEnt connectableEnt, final WorkflowManager wfm,
            final boolean isMetanodeInPortsBar, final boolean isMetanodeOutPortsBar) {
            super(connectableEnt, wfm, isMetanodeInPortsBar, isMetanodeOutPortsBar);
        }

        @Override
        public List<PortType> getPorts() {
            if (m_nc instanceof WorkflowManager subWfm) {
                final var portCount = subWfm.getNrWorkflowOutgoingPorts();
                return IntStream.range(0, portCount)//
                    .mapToObj(m_nc::getOutPort)//
                    .map(NodeOutPort::getPortType)//
                    .toList();
            }
            return Collections.emptyList(); // Should never happen
        }

        @Override
        public int getFirstDataPortIdx() {
            return m_firstDataPortIdx;
        }

        @Override
        public Collection<ConnectionContainer> getConnections() {
            return m_wfm.getParent().getOutgoingConnectionsFor(m_nodeId);
        }

        @Override
        public boolean isExecuted() {
            return m_nc.getNodeContainerState().isExecuted();
        }

        private static boolean isValid(final WorkflowManager wfm, final NodeContainer nc,
            final boolean isMetanodeInPortsBar) {
            if (nc instanceof WorkflowManager subWfm) {
                final var wfmContainsNc = wfm.containsNodeContainer(nc.getID());
                final var subWfmEqualsWfm = subWfm.equals(wfm);
                final var parentWfmContainsNc = subWfm.getParent().containsNodeContainer(nc.getID());
                return !wfmContainsNc && subWfmEqualsWfm && parentWfmContainsNc && isMetanodeInPortsBar;
            }
            return false;
        }

        private static Bounds getBounds(final WorkflowManager wfm) {
            return Optional.ofNullable(wfm.getInPortsBarUIInfo())//
                .map(NodeUIInformation::getBounds)//
                .map(Bounds::new)//
                .orElse(new Bounds(Integer.MAX_VALUE - 1, 0, 1, 1)); // Max to the left without integer overflow
        }

    }

}

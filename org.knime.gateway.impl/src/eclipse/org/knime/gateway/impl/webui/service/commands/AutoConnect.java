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
 *   Apr 4, 2024 (kai): created
 */
package org.knime.gateway.impl.webui.service.commands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.knime.core.node.NodeLogger;
import org.knime.gateway.api.webui.entity.AutoConnectCommandEnt;
import org.knime.gateway.api.webui.service.util.ServiceExceptions.OperationNotAllowedException;
import org.knime.gateway.impl.webui.service.commands.util.Connectable;
import org.knime.gateway.impl.webui.service.commands.util.ConnectionPlan;
import org.knime.gateway.impl.webui.service.commands.util.Geometry;

/**
 * Connect multiple notes according to an automatically determined plan.
 *
 * @author Benjamin Moser, KNIME GmbH, Germany
 * @author Kai Franze, KNIME GmbH, Germany
 */
public final class AutoConnect extends AbstractWorkflowCommand {

    private final AutoConnectCommandEnt m_commandEnt;

    private ConnectionPlan.AutoConnectResult m_connectResult;

    AutoConnect(final AutoConnectCommandEnt commandEnt) {
        m_commandEnt = commandEnt;
    }

    @Override
    protected boolean executeWithLockedWorkflow() throws OperationNotAllowedException {
        var selection = new AutoConnect.OrderedSelection(getConnectables());
        final var plan = new ConnectionPlan(selection);
        final var result = plan.execute(getWorkflowManager());
        NodeLogger.getLogger(this.getClass()).info("%s connections were added and %s connections were removed"
            .formatted(result.addedConnections().size(), result.removedConnections().size()));
        m_connectResult = result;
        return !result.addedConnections().isEmpty();
    }


    private Set<Connectable> getConnectables() {
        var wfm = getWorkflowManager();
        var selectedNodes = m_commandEnt.getSelectedNodes().stream() //
            .map(nodeId -> new Connectable.NodeDataPorts(nodeId.toNodeID(wfm), wfm))//
            .collect(Collectors.toUnmodifiableSet());
        Set<Connectable> connectables = new HashSet<>(selectedNodes);
        if (Boolean.TRUE.equals(m_commandEnt.isWorkflowInPortsBarSelected())) {
            connectables.add(new Connectable.WorkflowInPortsBar(wfm));
        }
        if (Boolean.TRUE.equals(m_commandEnt.isWorkflowOutPortsBarSelected())) {
            connectables.add(new Connectable.WorkflowOutPortsBar(wfm));
        }
        return connectables;
    }

    @Override
    public boolean canUndo() {
        if (m_connectResult == null) {
            return false;
        }
        return m_connectResult.addedConnections().stream().allMatch(getWorkflowManager()::canRemoveConnection);
    }

    @Override
    public void undo() throws OperationNotAllowedException {
        final var wfm = getWorkflowManager();
        m_connectResult.addedConnections().forEach(wfm::removeConnection);
        m_connectResult.removedConnections()
            .forEach(cc -> wfm.addConnection(cc.getSource(), cc.getSourcePort(), cc.getDest(), cc.getDestPort()));
        m_connectResult = null;
    }

    /**
     * An immutable list of {@link Connectable}s ordered by their bounds by north-west ordering.
     */
    public static class OrderedSelection {

        private final List<Connectable> m_list;

        OrderedSelection(final Set<Connectable> connectables) {
            m_list = connectables.stream()//
                .sorted(Comparator.comparing(Connectable::getBounds, Geometry.Rectangle.NORTH_WEST_ORDERING))//
                .toList();
        }

        private static <E> Stream<E> filter(final Stream<?> stream, final Class<E> targetClass) {
            return stream.filter(targetClass::isInstance).map(targetClass::cast);
        }

        public List<Connectable.Source> sources() {
            return filter(this.stream(), Connectable.Source.class).toList();
        }

        private Stream<Connectable> reversedFrom(final int index) {
            var els = new ArrayList<>(m_list);
            Collections.reverse(els);
            return els.stream().skip((long)this.size() - index);
        }

        public Stream<Connectable.Source> sourcesBefore(final int index) {
            return filter(reversedFrom(index), Connectable.Source.class);
        }

        public Stream<Connectable.Destination> destinationsAfter(final int index) {
            return filter( //
                this.stream().skip((long)index + 1), //
                Connectable.Destination.class //
            );
        }

        public Stream<Connectable> stream() {
            return m_list.stream();
        }

        public int size() {
            return m_list.size();
        }

        public Connectable get(int index) {
            return m_list.get(index);
        }
    }
}

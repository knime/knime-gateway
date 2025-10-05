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
 *   19 Mar 2025 (jtk): created
 */
package org.knime.gateway.impl.webui.service.commands;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.knime.core.node.workflow.NodeContainer;
import org.knime.core.node.workflow.NodeID;
import org.knime.core.node.workflow.NodeUIInformation;
import org.knime.core.node.workflow.WorkflowManager;
import org.knime.gateway.api.entity.NodeIDEnt;
import org.knime.gateway.api.webui.entity.AlignNodesCommandEnt;
import org.knime.gateway.api.webui.entity.AlignNodesCommandEnt.DirectionEnum;
import org.knime.gateway.api.webui.service.util.ServiceExceptions.ServiceCallException;
import org.knime.gateway.impl.webui.service.commands.util.Geometry;

/**
 * Workflow command to align nodes horizontally or vertically
 *
 * @author Jan-Timm Kuhr, TNG Technology Consulting GmbH
 * @since 5.5
 */
final class AlignNodes extends AbstractWorkflowCommand {
    private final DirectionEnum m_direction;

    private Map<NodeID, Geometry.Point> m_originalPositions = new HashMap<>();

    private final List<NodeIDEnt> m_nodeIds;

    private final NodeIDEnt m_referenceNodeIDEnt;

    private NodeID m_referenceNodeID;

    AlignNodes(final AlignNodesCommandEnt commandEnt) {
        m_direction = commandEnt.getDirection();
        m_nodeIds = commandEnt.getNodeIds();
        m_referenceNodeIDEnt = commandEnt.getReferenceNodeId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void undo() throws ServiceCallException {
        resetToPreviousPositions(getWorkflowManager());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean executeWithWorkflowLockAndContext() throws ServiceCallException {
        return alignNodes(getWorkflowManager());
    }

    /**
     * Align selected nodes
     *
     * @param wfm The workflow manager to operate in
     * @return <code>true</code> if the command changed the position of nodes, <code>false</code> if the successful
     *         execution of the command did not change the position of nodes
     */
    private boolean alignNodes(final WorkflowManager wfm) {
        if (m_nodeIds.isEmpty()) {
            return false;
        }

        m_originalPositions = m_nodeIds.stream().map(id -> wfm.getNodeContainer(id.toNodeID(wfm)))
            .collect(Collectors.toUnmodifiableMap(nc -> nc.getID(), AlignNodes::getPosition));

        if (areAlreadyAligned()) {
            return false;
        }

        if (m_referenceNodeIDEnt == null) {
            this.alignByBoundary(wfm);
        } else {
            this.alignByReference(wfm);
        }

        wfm.setDirty();
        return true;
    }

    private boolean areAlreadyAligned() {
        var somePosition = m_originalPositions.values().iterator().next();
        switch (m_direction) { // NOSONAR switch over enum is acceptable
            case LEFT, RIGHT, HORIZONTAL_CENTER -> {
                return m_originalPositions.values().stream().map(Geometry.Point::y)
                    .allMatch(x -> x == somePosition.x());
            }

            case TOP, BOTTOM, VERTICAL_CENTER -> {
                return m_originalPositions.values().stream().map(Geometry.Point::y)
                    .allMatch(y -> y == somePosition.y());
            }
        }
        return false;
    }

    private void alignByReference(final WorkflowManager wfm) {
        m_referenceNodeID = m_referenceNodeIDEnt.toNodeID(wfm);

        switch (m_direction) {
            case LEFT, RIGHT, HORIZONTAL_CENTER -> {
                var nextX = m_originalPositions.get(this.m_referenceNodeID).x();

                m_originalPositions.forEach((nodeID, originalPosition) -> {
                    var newPosition = new Geometry.Point(nextX, originalPosition.y());
                    setPosition(wfm.getNodeContainer(nodeID), newPosition);
                });
            }

            case TOP, BOTTOM, VERTICAL_CENTER -> {
                var nextY = m_originalPositions.get(this.m_referenceNodeID).y();

                m_originalPositions.forEach((nodeID, originalPosition) -> {
                    var newPosition = new Geometry.Point(originalPosition.x(), nextY);
                    setPosition(wfm.getNodeContainer(nodeID), newPosition);
                });
            }
        }
    }

    private void alignByBoundary(final WorkflowManager wfm) {
        switch (m_direction) {
            case LEFT -> {
                var nextX = this.getMinX();

                m_originalPositions.forEach((nodeID, originalPosition) -> {
                    var newPosition = new Geometry.Point(nextX, originalPosition.y());
                    setPosition(wfm.getNodeContainer(nodeID), newPosition);
                });
            }

            case RIGHT -> {
                var nextX = this.getMaxX();

                m_originalPositions.forEach((nodeID, originalPosition) -> {
                    var newPosition = new Geometry.Point(nextX, originalPosition.y());
                    setPosition(wfm.getNodeContainer(nodeID), newPosition);
                });
            }

            case TOP -> {
                var nextY = this.getMinY();

                m_originalPositions.forEach((nodeID, originalPosition) -> {
                    var newPosition = new Geometry.Point(originalPosition.x(), nextY);
                    setPosition(wfm.getNodeContainer(nodeID), newPosition);
                });
            }

            case BOTTOM -> {
                var nextY = this.getMaxY();

                m_originalPositions.forEach((nodeID, originalPosition) -> {
                    var newPosition = new Geometry.Point(originalPosition.x(), nextY);
                    setPosition(wfm.getNodeContainer(nodeID), newPosition);
                });
            }

            case VERTICAL_CENTER -> {
                var minY = this.getMinY();
                var maxY = this.getMaxY();

                var centerY = (maxY - minY) / 2 + minY;

                m_originalPositions.forEach((nodeID, originalPosition) -> {
                    var newPosition = new Geometry.Point(originalPosition.x(), centerY);
                    setPosition(wfm.getNodeContainer(nodeID), newPosition);
                });
            }

            case HORIZONTAL_CENTER -> {
                var minX = this.getMinX();
                var maxX = this.getMaxX();

                var centerX = (maxX - minX) / 2 + minX;

                m_originalPositions.forEach((nodeID, originalPosition) -> {
                    var newPosition = new Geometry.Point(centerX, originalPosition.y());
                    setPosition(wfm.getNodeContainer(nodeID), newPosition);
                });
            }
        }
    }

    private int getMinX() {
        return m_originalPositions.values().stream().min(Comparator.comparingInt(Geometry.Point::x)).orElseThrow().x();
    }

    private int getMaxX() {
        return m_originalPositions.values().stream().max(Comparator.comparingInt(Geometry.Point::x)).orElseThrow().x();
    }

    private int getMinY() {
        return m_originalPositions.values().stream().min(Comparator.comparingInt(Geometry.Point::y)).orElseThrow().y();
    }

    private int getMaxY() {
        return m_originalPositions.values().stream().max(Comparator.comparingInt(Geometry.Point::y)).orElseThrow().y();
    }

    private static Geometry.Point getPosition(final NodeContainer node) {
        return Geometry.Bounds.of(node.getUIInformation()).orElseThrow().leftTop();
    }

    private static void setPosition(final NodeContainer node, final Geometry.Point newLocation) {
        var oldBounds = node.getUIInformation().getBounds();
        var newBounds = Arrays.copyOf(oldBounds, oldBounds.length);
        newBounds[0] = newLocation.x();
        newBounds[1] = newLocation.y();
        node.setUIInformation(NodeUIInformation.builder()
            .setNodeLocation(newBounds[0], newBounds[1], newBounds[2], newBounds[3]).build());
    }

    /**
     * Reset node positions to state prior to align
     *
     * @param wfm The workflow manager to operate in
     */
    private void resetToPreviousPositions(final WorkflowManager wfm) {
        if (m_originalPositions.isEmpty()) {
            return;
        }

        m_originalPositions
            .forEach((nodeID, originalPosition) -> setPosition(wfm.getNodeContainer(nodeID), originalPosition));

        wfm.setDirty();
    }
}

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
 *   18 Jul 2025 (albrecht): created
 */
package org.knime.gateway.impl.webui.preview.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.knime.gateway.api.webui.entity.ConnectionEnt;
import org.knime.gateway.api.webui.entity.NodeEnt;
import org.knime.gateway.api.webui.entity.XYEnt;
import org.knime.gateway.impl.webui.preview.util.ShapeConstants.ShapeKey;

/**
 * Utility functions for rendering connections on workflow previews
 *
 * @author Christian Albrecht, KNIME GmbH, Konstanz, Germany
 * @since 5.6
 */
@SuppressWarnings({"javadoc"})
public final class ConnectorUtils {

    private static final double PORT_SIZE = ShapeConstants.get(ShapeKey.PORT_SIZE);

    private static final double DELTA_X1 = PORT_SIZE / 2 - 0.5;
    private static final double DELTA_X2 = PORT_SIZE / 2 - 0.5;

    public record Point(double x, double y) {
        public Point(final XYEnt xy) {
            this(xy.getX(), xy.getY());
        }
    }

    public record PathSegment (Point start, Point end, boolean isStart, boolean isEnd) {}

    public record BezierPoints(Point start, Point control1, Point control2, Point end) {}

    private final Map<String, NodeEnt> m_nodes;

    public ConnectorUtils(final Map<String, NodeEnt> nodes) {
        m_nodes = nodes;
    }

    public boolean isFlowVariableConnection(final ConnectionEnt connection) {
        var sourceNode = m_nodes.get(connection.getSourceNode().toString());
        if (sourceNode != null) {
            try {
                var sourcePort = sourceNode.getOutPorts().get(connection.getSourcePort());
                return sourcePort.getTypeId().equals(PortUtils.FLOW_VARIABLE_TYPE_ID);
            } catch (Exception e) { /* do nothing, return false */ }
        }
        return false;
    }

    public List<PathSegment> getPathSegments(final ConnectionEnt connection) {
        var sourceNode = m_nodes.get(connection.getSourceNode().toString());
        var targetNode = m_nodes.get(connection.getDestNode().toString());
        var startPosition = getRegularNodePortPosition(connection.getSourcePort(), true, sourceNode);
        var endPosition = getRegularNodePortPosition(connection.getDestPort(), false, targetNode);
        var bendpoints = connection.getBendpoints();

        // simple case just connect two nodes
        if (bendpoints == null || bendpoints.isEmpty()) {
            return List.of(new PathSegment(startPosition, endPosition, true, true));
        }

        // otherwise create a segment per bendpoint including start and end positions
        var segments = new ArrayList<PathSegment>();
        var previous = startPosition;
        for (int i = 0; i < bendpoints.size(); i++) {
            var current = new Point(bendpoints.get(i));
            segments.add(new PathSegment(previous, current, i == 0, false));
            previous = current;
        }
        segments.add(new PathSegment(previous, endPosition, false, true));
        return segments;
    }

    private static Point getRegularNodePortPosition(final int portIndex, final boolean isSourceNode, final NodeEnt node) {
        if (node == null) {
            return new Point(0, 0);
        }
        var allPorts = isSourceNode ? node.getOutPorts().size() : node.getInPorts().size();
        var shift = PortUtils.portShift(portIndex, allPorts, node.getKind(), isSourceNode);
        var nodePosition = node.getPosition();
        return new Point(nodePosition.getX() + shift[0], nodePosition.getY() + shift[1]);
    }

    private static BezierPoints getBezier(
        double x1,
        final double y1,
        double x2,
        final double y2,
        final boolean offsetStart,
        final boolean offsetEnd
    ) {
        x1 += DELTA_X1;
        x2 -= DELTA_X2;

        double width = Math.abs(x1 - x2) / 4;
        double height = Math.abs(y1 - y2) / 4;

        double xOffsetStart = offsetStart ? 4 : 0;
        double xOffsetEnd = offsetEnd ? 4 : 0;

        Point start = new Point(x1 - xOffsetStart, y1);
        Point control1 = new Point(x1 + width + height, y1);
        Point control2 = new Point(x2 - width - height, y2);
        Point end = new Point(x2 + xOffsetEnd, y2);

        return new BezierPoints(start, control1, control2, end);
    }

    public static String getBezierPathString(final PathSegment segment) {
        return getBezierPathString(segment.start.x, segment.start.y, segment.end.x, segment.end.y,
            !segment.isStart, !segment.isEnd);
    }

    private static String getBezierPathString(
        final double x1,
        final double y1,
        final double x2,
        final double y2,
        final boolean offsetStart,
        final boolean offsetEnd
    ) {
        BezierPoints bp = getBezier(x1, y1, x2, y2, offsetStart, offsetEnd);

        return String.format(
            Locale.US,
            "M%.2f,%.2f C%.2f,%.2f %.2f,%.2f %.2f,%.2f",
            bp.start.x, bp.start.y,
            bp.control1.x, bp.control1.y,
            bp.control2.x, bp.control2.y,
            bp.end.x, bp.end.y
        );
    }

}

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
import java.util.stream.Collectors;

import org.knime.core.node.BufferedDataTable;
import org.knime.core.node.port.PortTypeRegistry;
import org.knime.core.node.port.flowvariable.FlowVariablePortObject;
import org.knime.gateway.api.util.CoreUtil;
import org.knime.gateway.api.webui.entity.MetaNodePortEnt;
import org.knime.gateway.api.webui.entity.NodeEnt;
import org.knime.gateway.api.webui.entity.NodeEnt.KindEnum;
import org.knime.gateway.api.webui.entity.NodePortEnt;
import org.knime.gateway.api.webui.entity.PortTypeEnt;
import org.knime.gateway.api.webui.util.EntityFactory;
import org.knime.gateway.impl.webui.entity.DefaultPortTypeEnt.DefaultPortTypeEntBuilder;
import org.knime.gateway.impl.webui.preview.util.ShapeConstants.ShapeKey;

/**
 * Utility functions for rendering node ports on workflow previews
 *
 * @author Christian Albrecht, KNIME GmbH, Konstanz, Germany
 * @since 5.6
 */
@SuppressWarnings("javadoc")
public final class PortUtils {

    private static final double NODE_SIZE = ShapeConstants.get(ShapeKey.NODE_SIZE);
    private static final double PORT_SIZE = ShapeConstants.get(ShapeKey.PORT_SIZE);

    static final String TABLE_PORT_TYPE_ID = BufferedDataTable.class.getCanonicalName();
    private static final PortTypeEnt TABLE_PORT_TYPE;
    static final String FLOW_VARIABLE_TYPE_ID = FlowVariablePortObject.class.getCanonicalName();
    private static final PortTypeEnt FLOW_VARIABLE_PORT_TYPE;

    static {
        /* DEFAULT PORT TYPES */
        var tablePortBuilder = new DefaultPortTypeEntBuilder();
        tablePortBuilder.setKind(PortTypeEnt.KindEnum.TABLE);
        tablePortBuilder.setName("Data Table");
        tablePortBuilder.setColor(ColorConstants.PORT_COLORS.get("table"));
        TABLE_PORT_TYPE = tablePortBuilder.build();
        var flowVariablePortBuilder = new DefaultPortTypeEntBuilder();
        flowVariablePortBuilder.setKind(PortTypeEnt.KindEnum.FLOWVARIABLE);
        flowVariablePortBuilder.setName("Flow Variable");
        flowVariablePortBuilder.setColor(ColorConstants.PORT_COLORS.get("flowVariable"));
        FLOW_VARIABLE_PORT_TYPE = flowVariablePortBuilder.build();
    }

    private static String TRIANGLE_PATH = null;
    private static String INACTIVE_PATH = null;

    private static Map<String, PortTypeEnt> PORT_TEMPLATES = null;

    public PortUtils() {
        if (PORT_TEMPLATES == null) {
            // initialize port templates only once, but lazily
            var availablePortTypes = PortTypeRegistry.getInstance().availablePortTypes();
            PORT_TEMPLATES = availablePortTypes.stream().distinct()
                .collect(Collectors.toMap(
                    CoreUtil::getPortTypeId,
                    pt -> EntityFactory.PortType.buildPortTypeEnt(pt, availablePortTypes, false)
                ));
            PORT_TEMPLATES.put(TABLE_PORT_TYPE_ID, TABLE_PORT_TYPE);
            PORT_TEMPLATES.put(FLOW_VARIABLE_TYPE_ID, FLOW_VARIABLE_PORT_TYPE);
        }
    }

    private static boolean isMetanode(final NodeEnt.KindEnum nodeKind) {
        return nodeKind == KindEnum.METANODE;
    }

    /**
     * Calculates the position of the center of a port on a node depending on its index and the total number of ports on
     * the same side of the node.
     *
     * Returns the offset in regard to the upper left corner of the node.
     *
     * @param portIndex Index of the port
     * @param portCount Total number of ports on the same side of the node
     * @param isMetanode true if this port is attached to a metanode
     * @param isOutPort true for an output port, false for an input port
     * @return double[] with [x-shift, y-shift]
     */
    static double[] portShift(int portIndex, int portCount, final NodeEnt.KindEnum nodeKind, final boolean isOutPort) {
        double x = isOutPort ? NODE_SIZE + PORT_SIZE / 2 : -PORT_SIZE / 2;

        if (isMetanode(nodeKind)) {
            portIndex++;
            portCount++;
        }

        if (portIndex == 0) {
            // Default flow-variable port at the top
            return new double[]{x + ((isOutPort ? -1 : 1) * PORT_SIZE) / 2, -PORT_SIZE / 2};
        }

        double middleY = NODE_SIZE / 2;
        if (portCount == 2) {
            return new double[]{x, middleY};
        }

        int middleIndex = 2;
        double portMargin = 1.5;

        if (portCount == 3 && portIndex == 2) {
            portIndex = 3;
        }

        double dy = middleY + (portIndex - middleIndex) * (PORT_SIZE + portMargin);
        return new double[]{x, dy};
    }

    /**
     * Returns a list of [x, y] positions for all ports.
     *
     * @param portCount Number of ports
     * @param nodeKind The kind of node (native, component, metanode)
     * @param isOutports Whether the ports are output ports
     * @return List of double arrays, each representing a port position [x, y]
     */
    public static List<double[]> portPositions(final int portCount, final NodeEnt.KindEnum nodeKind, final boolean isOutports) {
        List<double[]> positions = new ArrayList<>();
        for (int i = 0; i < portCount; i++) {
            positions.add(portShift(i, portCount, nodeKind, isOutports));
        }
        return positions;
    }

    public static boolean showPort(final NodePortEnt port, final NodeEnt.KindEnum nodeKind) {
        var isMickeyMousePort = !isMetanode(nodeKind) && port.getIndex() == 0;
        return !isMickeyMousePort || port.getConnectedVia().size() > 0; // don't display unconnected flow variable ports
    }

    @SuppressWarnings("static-method") // should not be static to ensure m_portTemplates was initialized by constructor
    public PortTypeEnt.KindEnum getPortType(final NodePortEnt port) {
        if (PORT_TEMPLATES.containsKey(port.getTypeId())) {
            return PORT_TEMPLATES.get(port.getTypeId()).getKind();
        }
        return PortTypeEnt.KindEnum.OTHER;
    }

    @SuppressWarnings("static-method") // should not be static to ensure m_portTemplates was initialized by constructor
    public String getPortColor(final NodePortEnt port) {
        if (PORT_TEMPLATES.containsKey(port.getTypeId())) {
            return PORT_TEMPLATES.get(port.getTypeId()).getColor();
        } else {
            return ColorConstants.PORT_COLORS.get("generic");
        }
    }

    public boolean shouldBeFilled(final NodePortEnt port) {
        if (getPortType(port) == PortTypeEnt.KindEnum.FLOWVARIABLE && port.getIndex() == 0) {
            // Mickey Mouse ears are always rendered filled, even though they may technically be optional
            return true;
        }
        return !Boolean.TRUE.equals(port.isOptional());
    }

    public static String getTrafficLightColor(final NodePortEnt port) {
        if (port == null || !(port instanceof MetaNodePortEnt)) {
            return null;
        }
        var mPort = (MetaNodePortEnt)port;
        if (mPort.getNodeState() == null) {
            return null;
        }

        switch (mPort.getNodeState()) {
            case IDLE:
                return "red";
            case CONFIGURED:
            case QUEUED:
                return "yellow";
            case EXECUTING:
                return "blue";
            case HALTED:
            case EXECUTED:
                return "green";
            default:
                return null;
        }
    }

    public static String getTrianglePath() {
        // Lazy initialization
        if (TRIANGLE_PATH == null) {
            double strokeWidth = ShapeConstants.get(ShapeKey.PORT_STROKE_WIDTH);
            double halfSize = PORT_SIZE / 2;

            // Define triangle points relative to the center
            double x1 = -halfSize;
            double y1 = -halfSize;
            double x2 = halfSize;
            double y3 = halfSize;

            // Constants to keep triangle within bounds including stroke
            double d = Math.sqrt(5) / 2;
            double y = d / 2 + 0.25;

            // Adjust points for stroke width
            x1 += strokeWidth / 2;
            x2 -= strokeWidth * d;
            y1 += strokeWidth * y;
            y3 -= strokeWidth * y;

            // Build the path string (clockwise)
            TRIANGLE_PATH = String.format(Locale.US, "%.3f,%.3f %.3f,0 %.3f,%.3f", x1, y1, x2, x1, y3);
        }
        return TRIANGLE_PATH;
    }

    public static String getInactivePath() {
        if (INACTIVE_PATH == null) {
            double halfSize = PORT_SIZE / 2;
            // This draws an x as large as the port size
            INACTIVE_PATH = String.format(
                Locale.US,
                "M-%.1f,-%.1f l%.1f,%.1f m-%.1f,0 l%.1f,-%.1f",
                halfSize, halfSize,
                PORT_SIZE, PORT_SIZE,
                PORT_SIZE,
                PORT_SIZE, PORT_SIZE
            );
        }
        return INACTIVE_PATH;
    }
}

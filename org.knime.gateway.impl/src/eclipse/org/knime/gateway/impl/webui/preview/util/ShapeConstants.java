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
 *   15 Jul 2025 (albrecht): created
 */
package org.knime.gateway.impl.webui.preview.util;

import java.util.EnumMap;
import java.util.Map;
import java.util.stream.Collectors;


/**
 * Size constants for shapes drawn on the workflow canvas. Derived from component/workflow/util/shapes.ts
 *
 * @author Christian Albrecht, KNIME GmbH, Konstanz, Germany
 * @since 5.6
 */
@SuppressWarnings("javadoc")
public final class ShapeConstants {

    public static enum ShapeKey {
        NODE_SIZE,
        PORT_SIZE,
        PORT_STROKE_WIDTH,
        NODE_TORSO_RADIUS,
        NODE_STATUS_HEIGHT,
        NODE_STATUS_MARGIN_TOP,
        NODE_NAME_MARGIN,
        NODE_NAME_LINE_HEIGHT,
        NODE_NAME_FONT_SIZE,
        NODE_NAME_MAX_LINES,
        NODE_NAME_WIDTH,
        METANODE_LABEL_OFFSET_Y,
        NODE_LABEL_MARGIN_TOP,
        NODE_LABEL_WIDTH,
        NODE_LABEL_OFFSET_Y,
        NODE_LABEL_LINE_HEIGHT,
        NODE_LABEL_MAX_LINES,
        WORKFLOW_ANNOTATION_PADDING,
        LEGACY_ANNOTATIONS_FONT_SIZE_POINT_TO_PIXEL_FACTOR
    }

    /* Size values are made available as a map, because static fields are not easily accessible for ThymeLeaf */

    private static final EnumMap<ShapeKey, Double> VALUES = new EnumMap<>(ShapeKey.class);
    public static final Map<String, Object> SHAPES;

    public static final Map<String, String> NODE_TORSO_PATHS = Map.ofEntries(
        Map.entry("default", "M0,29.2L0,2.8C0,1.3,1.3,0,2.8,0l26.3,0C30.7,0,32,1.3,32,2.8v26.3c0,1.6-1.3,2.8-2.8,2.8H2.8C1.3,32,0,30.7,0,29.2z"),
        Map.entry("LoopEnd", "M32,2.8v26.3c0,1.6-1.3,2.8-2.8,2.8H4L0,16.1L4,0l25.2,0C30.7,0,32,1.3,32,2.8z"),
        Map.entry("LoopStart", "M0,29.2L0,2.8C0,1.3,1.3,0,2.8,0L32,0l-4,15.9L32,32H2.8C1.3,32,0,30.7,0,29.2z"),
        Map.entry("ScopeEnd", "M32,2.8v26.3c0,1.6-1.3,2.8-2.8,2.8H4L0,16.1L4,0l25.2,0C30.7,0,32,1.3,32,2.8z"),
        Map.entry("ScopeStart", "M0,29.2L0,2.8C0,1.3,1.3,0,2.8,0L32,0l-4,15.9L32,32H2.8C1.3,32,0,30.7,0,29.2z"),
        Map.entry("VirtualIn", "M32,2.8v26.3c0,1.6-1.3,2.8-2.8,2.8H6.5L0,25.9l5.2-10L0.7,7.2L6.5,0l22.7,0C30.7,0,32,1.3,32,2.8z"),
        Map.entry("VirtualOut", "M0,29.2L0,2.8C0,1.3,1.3,0,2.8,0L32,0l-5.8,7.2l4.5,8.7l-5.2,10L32,32H2.8C1.3,32,0,30.7,0,29.2z")
    );

    static {
        VALUES.put(ShapeKey.NODE_SIZE, 32.0);
        VALUES.put(ShapeKey.PORT_SIZE, 9.0);
        VALUES.put(ShapeKey.PORT_STROKE_WIDTH, 1.4);
        VALUES.put(ShapeKey.NODE_TORSO_RADIUS, 2.8);
        VALUES.put(ShapeKey.NODE_STATUS_HEIGHT, 12.0);
        VALUES.put(ShapeKey.NODE_STATUS_MARGIN_TOP, 8.0);
        VALUES.put(ShapeKey.NODE_NAME_MARGIN, 7.0);
        VALUES.put(ShapeKey.NODE_NAME_FONT_SIZE, 12.0);
        VALUES.put(ShapeKey.NODE_NAME_LINE_HEIGHT, 14.0);
        VALUES.put(ShapeKey.NODE_NAME_MAX_LINES, 3.0);
        VALUES.put(ShapeKey.NODE_NAME_WIDTH, 120.0);
        VALUES.put(ShapeKey.METANODE_LABEL_OFFSET_Y, 32.0 + 8.0); // NODE_SIZE + NODE_LABEL_MARGIN_TOP
        VALUES.put(ShapeKey.NODE_LABEL_MARGIN_TOP, 8.0);
        VALUES.put(ShapeKey.NODE_LABEL_OFFSET_Y, 32.0 + 8.0 + 12.0 + 8.0); // NODE_SIZE + NODE_LABEL_MARGIN_TOP + NODE_STATUS_HEIGHT + NODE_STATUS_MARGIN_TOP
        VALUES.put(ShapeKey.NODE_LABEL_LINE_HEIGHT, 14.0);
        VALUES.put(ShapeKey.NODE_LABEL_MAX_LINES, 8.0);
        VALUES.put(ShapeKey.NODE_LABEL_WIDTH, 150.0);
        VALUES.put(ShapeKey.WORKFLOW_ANNOTATION_PADDING, 3.0);
        VALUES.put(ShapeKey.LEGACY_ANNOTATIONS_FONT_SIZE_POINT_TO_PIXEL_FACTOR, 1.3333);

        SHAPES = VALUES.entrySet().stream().collect(Collectors.toUnmodifiableMap(
            entry -> entry.getKey().name(),
            Map.Entry::getValue
        ));
    }

    /* get method for type safe java access */
    public static double get(final ShapeKey key) {
        return VALUES.get(key);
    }

    private ShapeConstants() {}
}

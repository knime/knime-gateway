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

/**
 * Size constants for shapes drawn on the workflow canvas. Derived from component/workflow/util/shapes.ts
 *
 * @author Christian Albrecht, KNIME GmbH, Konstanz, Germany
 */
public class ShapeConstants {
    public static final double NODE_SIZE = 32;

    public static final double PORT_SIZE = 9;

    public static final double NODE_TORSO_RADIUS = 2.8;

    public static final double NODE_STATUS_HEIGHT = 12;

    public static final double NODE_STATUS_MARGIN_TOP = 8;

    public static final double NODE_ANNOTATION_MARGIN_TOP = 8;

    public static final double NODE_NAME_MARGIN = 7;

    public static final double NODE_NAME_LINE_HEIGHT = 14;

    public static final double NODE_NAME_FONT_SIZE = 12;

    public static final int NODE_NAME_MAX_LINES = 2;

    public static final double MAX_NODE_NAME_WIDTH = NODE_SIZE * 1.7; // 54.4

    public static final double METANODE_LABEL_OFFSET_Y = NODE_SIZE + NODE_ANNOTATION_MARGIN_TOP;

    public static final double NODE_LABEL_OFFSET_Y =
        NODE_SIZE + NODE_ANNOTATION_MARGIN_TOP + NODE_STATUS_HEIGHT + NODE_STATUS_MARGIN_TOP;

    public static final double WORKFLOW_ANNOTATION_PADDING = 3;

    public static final double ANNOTATIONS_FONT_SIZE_POINT_TO_PIXEL_FACTOR = 1.3333;

    private ShapeConstants() {
    }
}

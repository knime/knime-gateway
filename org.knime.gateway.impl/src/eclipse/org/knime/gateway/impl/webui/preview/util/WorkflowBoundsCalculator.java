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

import static org.knime.gateway.impl.webui.preview.util.ShapeConstants.ShapeKey.NODE_LABEL_LINE_HEIGHT;
import static org.knime.gateway.impl.webui.preview.util.ShapeConstants.ShapeKey.NODE_LABEL_MARGIN_TOP;
import static org.knime.gateway.impl.webui.preview.util.ShapeConstants.ShapeKey.NODE_LABEL_MAX_LINES;
import static org.knime.gateway.impl.webui.preview.util.ShapeConstants.ShapeKey.NODE_LABEL_WIDTH;
import static org.knime.gateway.impl.webui.preview.util.ShapeConstants.ShapeKey.NODE_NAME_LINE_HEIGHT;
import static org.knime.gateway.impl.webui.preview.util.ShapeConstants.ShapeKey.NODE_NAME_MARGIN;
import static org.knime.gateway.impl.webui.preview.util.ShapeConstants.ShapeKey.NODE_NAME_MAX_LINES;
import static org.knime.gateway.impl.webui.preview.util.ShapeConstants.ShapeKey.NODE_NAME_WIDTH;
import static org.knime.gateway.impl.webui.preview.util.ShapeConstants.ShapeKey.NODE_SIZE;
import static org.knime.gateway.impl.webui.preview.util.ShapeConstants.ShapeKey.NODE_STATUS_HEIGHT;
import static org.knime.gateway.impl.webui.preview.util.ShapeConstants.ShapeKey.NODE_STATUS_MARGIN_TOP;

import org.knime.gateway.api.webui.entity.BoundsEnt;
import org.knime.gateway.api.webui.entity.ConnectionEnt;
import org.knime.gateway.api.webui.entity.NodeEnt;
import org.knime.gateway.api.webui.entity.NodeEnt.KindEnum;
import org.knime.gateway.api.webui.entity.WorkflowAnnotationEnt;
import org.knime.gateway.api.webui.entity.WorkflowEnt;
import org.knime.gateway.api.webui.entity.XYEnt;

/**
 * Utility class that given a set of nodes, connections and annotations calculates the effective bounds of a workflow
 *
 * @author Christian Albrecht, KNIME GmbH, Konstanz, Germany
 * @since 5.6
 */
@SuppressWarnings("javadoc")
public final class WorkflowBoundsCalculator {

    private static final int WORKFLOW_PADDING = 10;

    // Derived constants
    private static final double NODE_NAME_HEIGHT = ShapeConstants.get(NODE_NAME_LINE_HEIGHT) * ShapeConstants.get(NODE_NAME_MAX_LINES);

    private static final double NODE_LABEL_HEIGHT = ShapeConstants.get(NODE_LABEL_LINE_HEIGHT) * ShapeConstants.get(NODE_LABEL_MAX_LINES);

    private static final double NODE_HEIGHT =
            NODE_NAME_HEIGHT + ShapeConstants.get(NODE_NAME_MARGIN) + ShapeConstants.get(NODE_SIZE);

    public record BoundingBox(int minX, int minY, int width, int height) {}

    private static class Extents {
        double minX = Double.POSITIVE_INFINITY;

        double minY = Double.POSITIVE_INFINITY;

        double maxX = Double.NEGATIVE_INFINITY;

        double maxY = Double.NEGATIVE_INFINITY;
    }

    private static Extents getNodeExtents(final NodeEnt node) {
        double x = node.getPosition().getX();
        double y = node.getPosition().getY();

        double width = 0;
        double height = NODE_HEIGHT;

        if (node.getKind() != KindEnum.METANODE) {
            height += ShapeConstants.get(NODE_STATUS_HEIGHT) + ShapeConstants.get(NODE_STATUS_MARGIN_TOP);
        }

        if (node.getAnnotation() != null && node.getAnnotation().getText() != null
            && node.getAnnotation().getText().getValue() != null
            && !node.getAnnotation().getText().getValue().isEmpty()) {
            width = Math.max(ShapeConstants.get(NODE_NAME_WIDTH), ShapeConstants.get(NODE_LABEL_WIDTH));
            height += NODE_LABEL_HEIGHT + ShapeConstants.get(NODE_LABEL_MARGIN_TOP);
        } else {
            width = ShapeConstants.get(NODE_NAME_WIDTH);
        }

        double minX = x + (ShapeConstants.get(NODE_SIZE) - width) / 2;
        double minY = y - NODE_NAME_HEIGHT - ShapeConstants.get(NODE_NAME_MARGIN);

        Extents extents = new Extents();

        extents.minX = minX;
        extents.minY = minY;
        extents.maxX = minX + width;
        extents.maxY = minY + height;

        return extents;
    }

    private static Extents getConnectionExtents(final ConnectionEnt connection) {
        if (connection.getBendpoints() == null || connection.getBendpoints().isEmpty()) {
            return null;
        }

        Extents extents = new Extents();

        for (XYEnt point : connection.getBendpoints()) {
            extents.minX = Math.min(extents.minX, point.getX());
            extents.minY = Math.min(extents.minY, point.getY());
            extents.maxX = Math.max(extents.maxX, point.getX());
            extents.maxY = Math.max(extents.maxY, point.getY());
        }

        if (extents.minX == Double.POSITIVE_INFINITY || extents.minY == Double.POSITIVE_INFINITY) {
            return null;
        }

        return extents;
    }

    private static Extents getAnnotationExtents(final WorkflowAnnotationEnt annotation) {
        if (annotation.getBounds() == null) {
            return null;
        }

        BoundsEnt bounds = annotation.getBounds();
        Extents extents = new Extents();
        extents.minX = bounds.getX();
        extents.minY = bounds.getY();
        extents.maxX = bounds.getX() + bounds.getWidth();
        extents.maxY = bounds.getY() + bounds.getHeight();
        return extents;
    }

    private static void updateWorkflowExtents(final Extents workflow, final Extents element) {
        workflow.minX = Math.min(workflow.minX, element.minX);
        workflow.minY = Math.min(workflow.minY, element.minY);
        workflow.maxX = Math.max(workflow.maxX, element.maxX);
        workflow.maxY = Math.max(workflow.maxY, element.maxY);
    }

    public static BoundingBox getWorkflowBoundingBox(final WorkflowEnt workflow) {
        Extents workflowExtents = new Extents();

        // Nodes
        for (NodeEnt node : workflow.getNodes().values()) {
            Extents nodeExtents = getNodeExtents(node);
            updateWorkflowExtents(workflowExtents, nodeExtents);
        }

        // Connections
        for (ConnectionEnt connection : workflow.getConnections().values()) {
            Extents connectionExtents = getConnectionExtents(connection);
            if (connectionExtents != null) {
                updateWorkflowExtents(workflowExtents, connectionExtents);
            }
        }

        // Annotations
        for (WorkflowAnnotationEnt annotation : workflow.getWorkflowAnnotations()) {
            Extents annotationExtents = getAnnotationExtents(annotation);
            if (annotationExtents != null) {
                updateWorkflowExtents(workflowExtents, annotationExtents);
            }
        }

        // Fallback if nothing found
        if (workflowExtents.minX == Double.POSITIVE_INFINITY || workflowExtents.minY == Double.POSITIVE_INFINITY) {
            workflowExtents.minX = 0;
            workflowExtents.minY = 0;
            workflowExtents.maxX = 0;
            workflowExtents.maxY = 0;
        }

        int minX = (int)(Math.floor(workflowExtents.minX) - WORKFLOW_PADDING);
        int minY = (int)(Math.floor(workflowExtents.minY) - WORKFLOW_PADDING);

        int width = (int)Math.ceil(workflowExtents.maxX) - minX + 2 * WORKFLOW_PADDING;
        int height = (int)Math.ceil(workflowExtents.maxY) - minY + 2 * WORKFLOW_PADDING;


        return new BoundingBox(minX, minY, width, height);
    }

    private WorkflowBoundsCalculator() {}

}

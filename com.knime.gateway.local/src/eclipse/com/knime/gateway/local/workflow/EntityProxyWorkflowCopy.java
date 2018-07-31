/* ------------------------------------------------------------------
 * This source code, its documentation and all appendant files
 * are protected by copyright law. All rights reserved.
 *
 * Copyright by KNIME AG, Zurich, Switzerland
 *
 * You may not modify, publish, transmit, transfer or sell, reproduce,
 * create derivative works from, distribute, perform, display, or in
 * any way exploit any of the content, in whole or in part, except as
 * otherwise expressly permitted in writing by the copyright owner or
 * as specified in the license file distributed with this product.
 *
 * If you have any questions please contact the copyright holder:
 * website: www.knime.com
 * email: contact@knime.com
 * ---------------------------------------------------------------------
 *
 */
package com.knime.gateway.local.workflow;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.knime.core.node.workflow.NodeID;
import org.knime.core.node.workflow.NodeUIInformation;
import org.knime.core.node.workflow.WorkflowAnnotation;
import org.knime.core.node.workflow.WorkflowCopyContent;
import org.knime.core.ui.node.workflow.NodeContainerUI;
import org.knime.core.ui.node.workflow.WorkflowCopyUI;
import org.knime.core.ui.node.workflow.WorkflowCopyWithOffsetUI;
import org.knime.core.ui.node.workflow.WorkflowManagerUI;

/**
 * {@link WorkflowCopyUI} implementation that just wraps a parts-id representing that copy on the server.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
final class EntityProxyWorkflowCopy implements WorkflowCopyWithOffsetUI {

    private UUID m_partsID;
    private int m_x;
    private int m_y;

    private int m_xShift = 0;
    private int m_yShift = 0;

    EntityProxyWorkflowCopy(final UUID partsID, final int x, final int y) {
        m_partsID = partsID;
        m_x = x;
        m_y = y;
    }

    public UUID getPartsID() {
        return m_partsID;
    }

    @Override
    public int getX() {
        return m_x;
    }

    @Override
    public int getY() {
        return m_y;
    }

    @Override
    public void setXShift(final int shift) {
        m_xShift = shift;
    }

    @Override
    public void setYShift(final int shift) {
        m_yShift = shift;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getXShift() {
        return m_xShift;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getYShift() {
        return m_yShift;
    }

    static int[] calcOffset(final WorkflowCopyContent wcc, final WorkflowManagerUI wfm) {
        NodeID[] nodes = wcc.getNodeIDs();
        List<int[]> insertedElementBounds = new ArrayList<int[]>();
        for (NodeID i : nodes) {
            NodeContainerUI nc = wfm.getNodeContainer(i);
            NodeUIInformation ui = nc.getUIInformation();
            int[] bounds = ui.getBounds();
            insertedElementBounds.add(bounds);
        }

        WorkflowAnnotation[] annos = wfm.getWorkflowAnnotations(wcc.getAnnotationIDs());
        for (WorkflowAnnotation a : annos) {
            int[] bounds =
                new int[] {a.getX(), a.getY(), a.getWidth(), a.getHeight()};
            insertedElementBounds.add(bounds);
        }
        int smallestX = Integer.MAX_VALUE;
        int smallestY = Integer.MAX_VALUE;
        for (int[] bounds : insertedElementBounds) {
            int currentX = bounds[0];
            int currentY = bounds[1];
            if (currentX < smallestX) {
                smallestX = currentX;
            }
            if (currentY < smallestY) {
                smallestY = currentY;
            }
        }
        return new int[]{smallestX, smallestY};
    }
}

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

import org.knime.core.ui.node.workflow.WorkflowManagerUI;

import com.knime.gateway.v0.entity.WorkflowNodeEnt;

/**
 * {@link WorkflowManagerUI} implementation that wraps (and therewith retrieves its information) from a
 * {@link WorkflowNodeEnt} most likely received remotely.
 *
 * @author Martin Horn, University of Konstanz
 */
public final class EntityProxyWorkflowManager extends AbstractEntityProxyWorkflowManager<WorkflowNodeEnt> {

    /**
     * @param workflowNodeEnt
     */
    EntityProxyWorkflowManager(final WorkflowNodeEnt workflowNodeEnt, final EntityProxyAccess access) {
        super(workflowNodeEnt, access);
    }
}

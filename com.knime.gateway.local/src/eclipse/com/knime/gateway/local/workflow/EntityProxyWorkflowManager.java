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

import com.knime.gateway.v0.entity.NodeStateEnt.StateEnum;
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

    @Override
    boolean canExecute() {
        //a meta node can be executed, if it contains at least one configured node. However, here we don't
        //want to check that every time (we would need to 'download' the sub-workflow) and thus allow a meta node
        //to be executed when it's in idle state, too. A metanode is in idle state, e.g., when there is one of
        //its out ports is not connected to an inner node.
        return super.canExecute() || getEntity().getNodeState().getState().equals(StateEnum.IDLE);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    boolean canReset() {
        // since we cannot check easily (unless we 'download' the contained workflow or ask the server directly)
        // whether there is at least one executed node within this meta node, we for now allow a meta node
        // even if its not executed
        return super.canReset() || getEntity().getNodeState().getState().equals(StateEnum.CONFIGURED);
    }
}

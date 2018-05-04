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

import org.knime.core.node.workflow.NodeContainerState;

/**
 * Mainly COPIED from org.knime.core.node.workflow.InternalNodeContainerState!
 *
 * @author Martin Horn, University of Konstanz
 */
enum EntityProxyNodeContainerState implements NodeContainerState {

    IDLE,
    CONFIGURED,
    UNCONFIGURED_MARKEDFOREXEC,
    CONFIGURED_MARKEDFOREXEC,
    EXECUTED_MARKEDFOREXEC,
    CONFIGURED_QUEUED,
    EXECUTED_QUEUED,
    PREEXECUTE,
    EXECUTING,
    EXECUTINGREMOTELY,
    POSTEXECUTE,
    EXECUTED;


    /** {@inheritDoc} */
    @Override
    public boolean isIdle() {
        return IDLE.equals(this);
    }

    /** {@inheritDoc} */
    @Override
    public boolean isConfigured() {
        return CONFIGURED.equals(this);
    }

    /** {@inheritDoc} */
    @Override
    public boolean isExecuted() {
        return EXECUTED.equals(this);
    }

    /** {@inheritDoc} */
    @Override
    public boolean isExecutionInProgress() {
        switch (this) {
            case IDLE:
            case EXECUTED:
            case CONFIGURED: return false;
            default: return true;
        }
    }

    /** {@inheritDoc} */
    @Override
    public boolean isWaitingToBeExecuted() {
        switch (this) {
            case UNCONFIGURED_MARKEDFOREXEC:
            case CONFIGURED_MARKEDFOREXEC:
            case EXECUTED_MARKEDFOREXEC:
            case CONFIGURED_QUEUED:
            case EXECUTED_QUEUED:
                return true;
            default:
                return false;
        }
    }

    /** {@inheritDoc} */
    @Override
    public boolean isHalted() {
        switch (this) {
            case CONFIGURED_QUEUED:
            case EXECUTED_QUEUED:
            case EXECUTING:
            case EXECUTINGREMOTELY:
            case POSTEXECUTE:
            case PREEXECUTE:
                return false;
            default:
                return true;
        }
    }

    /** {@inheritDoc} */
    @Override
    public boolean isExecutingRemotely() {
        return EXECUTINGREMOTELY.equals(this);
    }

}

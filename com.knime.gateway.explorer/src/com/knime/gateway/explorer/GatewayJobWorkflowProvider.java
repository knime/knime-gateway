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
package com.knime.gateway.explorer;

import java.util.UUID;

import org.knime.core.ui.node.workflow.RemoteWorkflowContext;
import org.knime.core.ui.node.workflow.WorkflowManagerUI;
import org.knime.core.util.Version;

import com.knime.explorer.server.internal.view.actions.jobworkflow.JobWorkflowProvider;
import com.knime.gateway.local.service.ServerServiceConfig;
import com.knime.gateway.local.workflow.EntityProxyAccess;

/**
 * Implementation of the {@link JobWorkflowProvider} to provide the actual workflow to the server explorer retrieved via
 * the gateway API.
 *
 * @author Martin Horn, University of Konstanz
 */
public class GatewayJobWorkflowProvider implements JobWorkflowProvider {
    /**
     * {@inheritDoc}
     */
    @Override
    public WorkflowManagerUI getWorkflowForJob(final UUID jobId, final RemoteWorkflowContext workflowContext,
        final Version serverVersion) {
        return EntityProxyAccess.createWorkflowManager(new ServerServiceConfig(workflowContext.getRepositoryAddress(),
            workflowContext.getServerAuthToken(), serverVersion), jobId, workflowContext);
    }
}

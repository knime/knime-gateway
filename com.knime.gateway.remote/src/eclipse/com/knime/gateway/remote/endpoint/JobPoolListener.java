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
package com.knime.gateway.remote.endpoint;

import java.util.UUID;

import org.knime.core.node.workflow.WorkflowManager;

/**
 * It keeps track of created and discarded jobs at the executor and adds/removes the them to/from the
 * {@link WorkflowProjectManager}.
 *
 *
 * @author Martin Horn, University of Konstanz
 */
public class JobPoolListener implements com.knime.enterprise.executor.JobPoolListener {

    /**
     * {@inheritDoc}
     */
    @Override
    public void jobLoaded(final UUID id, final WorkflowManager wfm) {
        WorkflowProjectManager.addWorkflowProject(id, new WorkflowProject() {

            @Override
            public WorkflowManager openProject() {
                return wfm;
            }

            @Override
            public String getName() {
                return wfm.getName();
            }

            @Override
            public String getID() {
                return id.toString();
            }
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void jobDiscarded(final UUID id) {
        WorkflowProjectManager.removeWorkflowProject(id);
    }
}

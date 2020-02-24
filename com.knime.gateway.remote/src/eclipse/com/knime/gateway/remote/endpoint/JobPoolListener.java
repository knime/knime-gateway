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

import java.io.IOException;

import org.eclipse.birt.report.engine.api.EngineException;
import org.knime.core.node.workflow.WorkflowManager;
import org.knime.core.util.report.ReportingConstants;
import org.knime.core.util.report.ReportingConstants.RptOutputFormat;
import org.knime.core.util.report.ReportingConstants.RptOutputOptions;

import com.knime.enterprise.executor.ExecutorUtil;
import com.knime.enterprise.executor.JobPool;
import com.knime.enterprise.executor.WorkflowJob;

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
    public void jobLoaded(final WorkflowJob job) {
        WorkflowProjectManager.addWorkflowProject(job.getId(), new WorkflowProject() {

            @Override
            public WorkflowManager openProject() {
                return job.getWorkflowManager();
            }

            @Override
            public String getName() {
                return job.getWorkflowManager().getName();
            }

            @Override
            public String getID() {
                return job.getId().toString();
            }

            @Override
            public void clearReport() {
                ExecutorUtil.clearReport(job);
            }

            @Override
            public byte[] generateReport(final String format) {
                RptOutputFormat reportFormat;
                reportFormat = RptOutputFormat.valueOf(format);

                RptOutputOptions options = ReportingConstants.getDefaultReportOptions(reportFormat);

                try {
                    return JobPool.generateReport(job, reportFormat, options);
                } catch (EngineException | IOException ex) {
                    throw new IllegalStateException(ex);
                }
            }

        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void jobDiscarded(final WorkflowJob job) {
        WorkflowProjectManager.removeWorkflowProject(job.getId());
    }
}

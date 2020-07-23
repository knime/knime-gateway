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
package org.knime.gateway.impl.project;

import org.eclipse.birt.report.model.api.IllegalOperationException;
import org.knime.core.node.workflow.WorkflowManager;

/**
 * Represents a workflow project.
 *
 * @author Martin Horn, University of Konstanz
 * @noreference This interface is not intended to be referenced by clients.
 */
public interface WorkflowProject {

    /**
     * @return the name of the workflow
     */
    String getName();

    /**
     * @return an id of the workflow
     */
    String getID();

    /**
     * Opens/loads the actual workflow represented by this workflow project.
     * If the workflow has already been opened before it will be opened/loaded again.
     *
     * @return the newly loaded workflow
     */
    WorkflowManager openProject();

    /**
     * Clears the report directory of the workflow project (if there is any).
     */
    default void clearReport() {}

    /**
     * Generates a report. See
     * <code>com.knime.enterprise.executor.JobPool#generateReport(com.knime.enterprise.executor.WorkflowJob, org.knime.core.util.report.ReportingConstants.RptOutputFormat, org.knime.core.util.report.ReportingConstants.RptOutputOptions)</code>
     *
     * @param format the report format
     * @return the report directory or an empty optional
     * @throws IllegalArgumentException if the format is not supported or invalid
     * @throws IllegalStateException if report generation failed for some reason
     */
    default byte[] generateReport(final String format) {
        throw new IllegalOperationException("Report generation not supported");
    }
}
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
 */
package org.knime.gateway.testing.helper;

import java.util.UUID;

/**
 * Executes workflows identified by {@link UUID}s.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
public interface WorkflowExecutor {

    /**
     * Executes the workflow and waits until finished.
     *
     * @param wfId the id of the workflow to execute
     * @throws Exception
     */
	void executeWorkflow(String wfId) throws Exception;

	/**
	 * Triggers the workflow execution and returns immediately.
	 *
	 * @param wfId the id of the workflow to execute
	 * @throws Exception
	 */
	void executeWorkflowAsync(String wfId) throws Exception;

}

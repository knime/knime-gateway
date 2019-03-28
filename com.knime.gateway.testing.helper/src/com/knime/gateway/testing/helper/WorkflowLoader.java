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
package com.knime.gateway.testing.helper;

import java.util.UUID;

import com.knime.gateway.service.AbstractGatewayServiceTestHelper.TestWorkflow;

/**
 * Loads workflows into memory that are later (directly or indirectly) accessed from the gateway's
 * WorkflowProjectManager.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
@FunctionalInterface
public interface WorkflowLoader {

	/**
	 * Loads the workflow from the given URL. The
	 * resource behind the URL must be a ZIP archive containing a workflow.
	 *
	 * @param workflow workflow to load
	 * @return the unique identifier of the loaded workflow
	 * @throws Exception if the loading fails
	 */
	UUID loadWorkflow(TestWorkflow workflow) throws Exception;

}

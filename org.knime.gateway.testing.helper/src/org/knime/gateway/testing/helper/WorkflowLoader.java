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

/**
 * Loads workflows into memory that are later (directly or indirectly) accessed from the gateway's
 * WorkflowProjectManager.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
public interface WorkflowLoader {

	/**
	 * Loads a workflow.
	 *
	 * @param workflow workflow to load
	 * @return the unique identifier of the loaded workflow
	 * @throws Exception if the loading fails
	 */
	String loadWorkflow(TestWorkflow workflow) throws Exception;

    /**
     * Loads a component.
     *
     * @param component component to load
     * @return the unique identifier of the loaded workflow
     * @throws Exception if the loading fails
     */
    String loadComponent(TestWorkflow component) throws Exception;

}

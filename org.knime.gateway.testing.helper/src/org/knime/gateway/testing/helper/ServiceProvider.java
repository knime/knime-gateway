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
package org.knime.gateway.testing.helper;

import org.knime.gateway.api.service.AnnotationService;
import org.knime.gateway.api.service.NodeService;
import org.knime.gateway.api.service.WizardExecutionService;
import org.knime.gateway.api.service.WorkflowService;

/**
 * Provides implementations of all gateway services.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
public interface ServiceProvider {

    /**
     * @return workflow service implementatin
     */
	WorkflowService getWorkflowService();

	/**
	 * @return node service implementation
	 */
	NodeService getNodeService();

	/**
	 * @return annotation service implementation
	 */
	AnnotationService getAnnotationService();

	/**
	 * @return service implementation
	 */
	WizardExecutionService getWizardExecutionService();

}

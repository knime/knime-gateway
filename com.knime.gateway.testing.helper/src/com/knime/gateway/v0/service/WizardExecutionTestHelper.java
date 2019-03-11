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
package com.knime.gateway.v0.service;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import java.util.UUID;

import com.knime.gateway.testing.helper.ResultChecker;
import com.knime.gateway.testing.helper.ServiceProvider;
import com.knime.gateway.testing.helper.WorkflowLoader;
import com.knime.gateway.v0.service.util.ServiceExceptions.NoWizardPageException;

/**
 * Tests the wizard execution functionality of a workflow via the gateway API.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
public class WizardExecutionTestHelper extends AbstractGatewayServiceTestHelper {

	/**
     * See
     * {@link AbstractGatewayServiceTestHelper#AbstractGatewayServiceTestHelper(String, WorkflowLoader, ServiceProvider, ResultChecker)}.
     *
     * @param workflowLoader
     * @param serviceProvider
     * @param entityResultChecker
     */
    public WizardExecutionTestHelper(final ServiceProvider serviceProvider, final ResultChecker entityResultChecker,
        final WorkflowLoader workflowLoader) {
        super("wizardexecution", serviceProvider, entityResultChecker, workflowLoader);
    }

    /**
     * Tests the {@link WizardExecutionService#getCurrentPage(UUID)} endpoint when no page is available.
     *
     * @throws Exception if an error occurs
     */
    public void testGetCurrentPageNoPage() throws Exception {
    	UUID wfId = loadWorkflow(TestWorkflow.WORKFLOW_WIZARD_EXECUTION);
    	try {
            wes().getCurrentPage(wfId);
            fail("Exception expected to be thrown");
        } catch (NoWizardPageException e) {
            assertThat("Unexpected exception method", e.getMessage(), is("No current wizard page"));
    	}
    }
}

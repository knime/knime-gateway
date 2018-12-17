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

import static org.hamcrest.core.StringContains.containsString;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import java.util.UUID;

import com.knime.gateway.entity.GatewayEntity;
import com.knime.gateway.testing.helper.ResultChecker;
import com.knime.gateway.testing.helper.ServiceProvider;
import com.knime.gateway.testing.helper.WorkflowLoader;
import com.knime.gateway.v0.service.util.ServiceExceptions;

/**
 * Tests the necessary parts for the configuration of wrapped metanodes.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
public class WMetaNodeDialogTest extends AbstractGatewayServiceTestHelper {

	/**
     * See
     * {@link AbstractGatewayServiceTestHelper#AbstractGatewayServiceTestHelper(String, WorkflowLoader, ServiceProvider, ResultChecker)}.
     *
     * @param workflowLoader
     * @param serviceProvider
     * @param entityResultChecker
     */
    public WMetaNodeDialogTest(final ServiceProvider serviceProvider, final ResultChecker entityResultChecker,
        final WorkflowLoader workflowLoader) {
        super("wmetanodedialog", serviceProvider, entityResultChecker, workflowLoader);
    }

    /**
     * Tests essentially the {@link NodeService#getWMetaNodeDialog(java.util.UUID, String)} endpoint.
     *
     * @throws Exception if an error occurs
     */
    public void testGetWMetaNodeDialog() throws Exception {
    	UUID wfId = loadWorkflow(TestWorkflow.WORKFLOW_QUICKFORMS);

        GatewayEntity entity = ns().getWMetaNodeDialog(wfId, "19");
        cr(entity, "wmetanodedialog_19");

        //what if the node to get the meta node dialog for is not a wrapped metanode?
        try {
            ns().getWMetaNodeDialog(wfId, "1");
            fail("Expected a ServiceException to be thrown");
        } catch (ServiceExceptions.InvalidRequestException e) {
            assertThat("Unexpected exception message", e.getMessage(),
                containsString("The node the dialog is requested for is not a wrapped metanode"));
        }
    }
}

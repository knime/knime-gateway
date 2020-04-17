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
package com.knime.gateway.service;

import static org.hamcrest.core.StringContains.containsString;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import java.util.UUID;

import com.knime.gateway.entity.GatewayEntity;
import com.knime.gateway.entity.NodeIDEnt;
import com.knime.gateway.service.util.ServiceExceptions;
import com.knime.gateway.testing.helper.ResultChecker;
import com.knime.gateway.testing.helper.ServiceProvider;
import com.knime.gateway.testing.helper.WorkflowLoader;

/**
 * Tests the necessary parts for the configuration of components.
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
    	UUID wfId = loadWorkflow(TestWorkflow.QUICKFORMS);

        GatewayEntity entity = ns().getWMetaNodeDialog(wfId, new NodeIDEnt(19));
        cr(entity, "wmetanodedialog_19");

        //what if the node to get the meta node dialog for is not a component?
        try {
            ns().getWMetaNodeDialog(wfId, new NodeIDEnt(1));
            fail("Expected a ServiceException to be thrown");
        } catch (ServiceExceptions.InvalidRequestException e) {
            assertThat("Unexpected exception message", e.getMessage(),
                containsString("The node the dialog is requested for is not a component"));
        }
    }
}

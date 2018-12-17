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

import com.knime.gateway.v0.service.AbstractGatewayServiceTestHelper;

/**
 * Runs a gateway tests, e.g. those derived from {@link AbstractGatewayServiceTestHelper}.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
@FunctionalInterface
public interface GatewayTestRunner {

    /**
     * @param serviceProvider provides the service implementations that are actually the objectives of the test
     * @param entityResultChecker for result comparison
     * @param workflowLoader provides the workflows to run the test
     * @param workflowExecutor executes workflows if desired during the test run
     * @throws Exception
     */
    void runGatewayTest(final ServiceProvider serviceProvider, final ResultChecker entityResultChecker,
        final WorkflowLoader workflowLoader, final WorkflowExecutor workflowExecutor) throws Exception;

}

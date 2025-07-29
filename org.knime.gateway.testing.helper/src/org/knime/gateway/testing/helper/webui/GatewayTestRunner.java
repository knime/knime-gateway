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
package org.knime.gateway.testing.helper.webui;

import org.knime.gateway.impl.project.ProjectManager;
import org.knime.gateway.testing.helper.ResultChecker;
import org.knime.gateway.testing.helper.ServiceProvider;
import org.knime.gateway.testing.helper.WorkflowExecutor;
import org.knime.gateway.testing.helper.WorkflowLoader;

/**
 * Runs a gateway test.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
@FunctionalInterface
public interface GatewayTestRunner {

    /**
     * @param entityResultChecker for result comparison
     * @param serviceProvider
     * @param workflowLoader provides the workflows to run the test
     * @param workflowExecutor executes workflows if desired during the test run
     * @param projectManager manages active projects
     * @throws Exception
     */
    void runGatewayTest(final ResultChecker entityResultChecker, ServiceProvider serviceProvider,
        final WorkflowLoader workflowLoader, final WorkflowExecutor workflowExecutor,
        final ProjectManager projectManager) throws Exception; //NOSONAR

}

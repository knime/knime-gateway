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

import java.util.HashMap;
import java.util.Map;

/**
 * Gives programmatic access to all gateway test (helpers) that test the web-ui API services. Provided in that way such
 * that they can be re-used in different test settings (e.g. integration tests).
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
public class GatewayTestCollection {

    private GatewayTestCollection() {
        //utility class
    }

    /**
     * Collects and initializes all available gateway tests.
     *
     * TODO could be automatically collected by annotating the respective classes and/or methods
     *
     * @return map from the individual test names to a function that allows one to run the test
     */
    public static Map<String, GatewayTestRunner> collectAllGatewayTests() {
        Map<String, GatewayTestRunner> res = new HashMap<>();
        res.put("testGetWorkflow",
            (rc, wl, we) -> new ViewWorkflowTestHelper(rc, wl).testGetWorkflow());
        return res;
    }
}
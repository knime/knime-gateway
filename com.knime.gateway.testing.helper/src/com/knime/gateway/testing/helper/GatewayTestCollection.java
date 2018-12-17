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

import java.util.HashMap;
import java.util.Map;

import com.knime.gateway.v0.service.AddNodeTestHelper;
import com.knime.gateway.v0.service.AddPartsTestHelper;
import com.knime.gateway.v0.service.ChangeNodeTestHelper;
import com.knime.gateway.v0.service.ChangeWorkflowAnnotationTestHelper;
import com.knime.gateway.v0.service.DeleteCutCopyPastePartsTestHelper;
import com.knime.gateway.v0.service.JSViewTestHelper;
import com.knime.gateway.v0.service.NodeDataTestHelper;
import com.knime.gateway.v0.service.UpdateWorkflowTestHelper;
import com.knime.gateway.v0.service.ViewWorkflowTestHelper;
import com.knime.gateway.v0.service.WMetaNodeDialogTest;

/**
 * Gives programmatic access to all gateway test (helper) that mainly test the correct behavior of gateway services
 * (see, e.g., AbstractGatewayServiceTestHelper)
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
        Map<String, GatewayTestRunner> res = new HashMap<String, GatewayTestRunner>();
        res.put("testAddNode",
            (sp, rc, wl, we) -> new AddNodeTestHelper(sp, rc, wl).testAddNode());

        res.put("testAddConnections",
            (sp, rc, wl, we) -> new AddPartsTestHelper(sp, rc, wl).testAddConnections());

        res.put("testChangeNodeState",
            (sp, rc, wl, we) -> new ChangeNodeTestHelper(sp, rc, wl, we).testChangeNodeState());
        res.put("testChangeNodeSettings",
            (sp, rc, wl, we) -> new ChangeNodeTestHelper(sp, rc, wl, we).testChangeNodeSettings());
        res.put("testChangeNodeBounds",
            (sp, rc, wl, we) -> new ChangeNodeTestHelper(sp, rc, wl, we).testChangeNodeBounds());

        res.put("testChangeAnnotationBounds",
            (sp, rc, wl, we) -> new ChangeWorkflowAnnotationTestHelper(sp, rc, wl)
                .testChangeAnnotationBounds());

        res.put("testDeleteParts",
            (sp, rc, wl, we) -> new DeleteCutCopyPastePartsTestHelper(sp, rc, wl, we).testDeleteParts());
        res.put("testCopyPasteParts",
            (sp, rc, wl, we) -> new DeleteCutCopyPastePartsTestHelper(sp, rc, wl, we).testCopyPasteParts());

        res.put("testGetAndSetNodeViewsData",
            (sp, rc, wl, we) -> new JSViewTestHelper(sp, rc, wl, we).testGetAndSetNodeViewsData());

        res.put("testCompareNodeData",
            (sp, rc, wl, we) -> new NodeDataTestHelper(sp, rc, wl).testCompareNodeData());

        res.put("testUpdateWorkflow",
            (sp, rc, wl, we) -> new UpdateWorkflowTestHelper(sp, rc, wl, we).testUpdatebWorkflow());

        res.put("testViewWorkflow",
            (sp, rc, wl, we) -> new ViewWorkflowTestHelper(sp, rc, wl, we).testViewWorkflow());

        res.put("testGetWMetaNodeDialog",
            (sp, rc, wl, we) -> new WMetaNodeDialogTest(sp, rc, wl).testGetWMetaNodeDialog());

        return res;
    }
}

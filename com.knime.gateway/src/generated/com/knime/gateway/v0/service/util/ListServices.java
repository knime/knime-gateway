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
package com.knime.gateway.v0.service.util;

import com.knime.gateway.v0.service.WizardExecutionService;
import com.knime.gateway.v0.service.NodeService;
import com.knime.gateway.v0.service.WorkflowService;
import com.knime.gateway.v0.service.AnnotationService;

import java.util.ArrayList;
import java.util.List;


/**
 * Lists all gateway services of package <code>com.knime.gateway.v0.service</code>.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
@javax.annotation.Generated(value = "org.knime.gateway.codegen.GatewayCodegen")
public class ListServices {

    private ListServices() {
        //utility class
    }

    /**
     * Lists all gateway service classes of package <code>com.knime.gateway.v0.service</code>.
     * @return the class list
     */
    public static List<Class<?>> listServiceInterfaces() {
        List<Class<?>> res = new ArrayList<>();
        res.add(WizardExecutionService.class);
        res.add(NodeService.class);
        res.add(WorkflowService.class);
        res.add(AnnotationService.class);
        return res;
    }
}

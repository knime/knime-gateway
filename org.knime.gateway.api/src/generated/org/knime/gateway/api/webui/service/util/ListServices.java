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
package org.knime.gateway.api.webui.service.util;

import org.knime.gateway.api.webui.service.WorkflowService;

import java.util.ArrayList;
import java.util.List;


/**
 * Lists all gateway services of package <code>com.knime.gateway.service</code>.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
@javax.annotation.Generated(value = {"com.knime.gateway.codegen.GatewayCodegen", "src-gen/api/web-ui/configs/org.knime.gateway.api-config.json"})
public class ListServices {

    private ListServices() {
        //utility class
    }

    /**
     * Lists all gateway service classes of package <code>com.knime.gateway.service</code>.
     * @return the class list
     */
    public static List<Class<?>> listServiceInterfaces() {
        List<Class<?>> res = new ArrayList<>();
        res.add(WorkflowService.class);
        return res;
    }
}

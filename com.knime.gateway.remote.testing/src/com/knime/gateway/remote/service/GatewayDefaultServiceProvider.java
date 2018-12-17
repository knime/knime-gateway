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
package com.knime.gateway.remote.service;

import com.knime.gateway.testing.helper.ServiceProvider;
import com.knime.gateway.v0.service.AnnotationService;
import com.knime.gateway.v0.service.NodeService;
import com.knime.gateway.v0.service.WorkflowService;

/**
 * Provides the default service implementations such as {@link DefaultWorkflowService}.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
public class GatewayDefaultServiceProvider implements ServiceProvider {

    @Override
    public WorkflowService getWorkflowService() {
        return DefaultWorkflowService.getInstance();
    }

    @Override
    public NodeService getNodeService() {
        return DefaultNodeService.getInstance();
    }

    @Override
    public AnnotationService getAnnotationService() {
        return DefaultAnnotationService.getInstance();
    }

}

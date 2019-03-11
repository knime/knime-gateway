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

import com.knime.gateway.service.GatewayService;
import com.knime.gateway.v0.service.util.ServiceExceptions;


/**
 * 
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
@javax.annotation.Generated(value = "org.knime.gateway.codegen.GatewayCodegen")
public interface WizardExecutionService extends GatewayService {

    /**
     * Returns the output for a workflow&#39;s current page. This is identical to the response returned by executing a workflow to the next page however it can be retrieved again at a later time. 
     *
     * @param jobId ID of the job the workflow is requested for.
     *
     * @return the result
     * @throws ServiceExceptions.NoWizardPageException If a wizard page is not available.
     */
    String getCurrentPage(java.util.UUID jobId)  throws ServiceExceptions.NoWizardPageException;
        
}

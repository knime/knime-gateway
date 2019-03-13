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
package com.knime.gateway.jsonrpc.local.service;

import com.knime.gateway.v0.entity.WizardPageInputEnt;

import com.knime.gateway.v0.service.util.ServiceExceptions;

import com.googlecode.jsonrpc4j.JsonRpcMethod;

import com.knime.gateway.v0.service.WizardExecutionService;

/**
 * Interface that adds json rpc annotations. 
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
@javax.annotation.Generated(value = "org.knime.gateway.codegen.GatewayCodegen")
public interface JsonRpcWizardExecutionService extends WizardExecutionService {

	/**
     * {@inheritDoc}
     */
    @Override
    @JsonRpcMethod(value = "WizardExecutionService.executeToNextPage")
    String executeToNextPage(java.util.UUID jobId, Boolean async, Long timeout, WizardPageInputEnt wizardPageInputEnt)  throws ServiceExceptions.InvalidSettingsException, ServiceExceptions.NoWizardPageException, ServiceExceptions.TimeoutException;

	/**
     * {@inheritDoc}
     */
    @Override
    @JsonRpcMethod(value = "WizardExecutionService.getCurrentPage")
    String getCurrentPage(java.util.UUID jobId)  throws ServiceExceptions.NoWizardPageException;

}

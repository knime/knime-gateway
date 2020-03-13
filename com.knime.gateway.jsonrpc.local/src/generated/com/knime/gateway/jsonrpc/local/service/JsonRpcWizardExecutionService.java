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

import com.knime.gateway.entity.ExecutionStatisticsEnt;
import com.knime.gateway.entity.WizardPageEnt;
import com.knime.gateway.entity.WizardPageInputEnt;

import com.knime.gateway.service.util.ServiceExceptions;

import com.googlecode.jsonrpc4j.JsonRpcMethod;

import com.knime.gateway.service.WizardExecutionService;

/**
 * Interface that adds json rpc annotations. 
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
@javax.annotation.Generated(value = {"org.knime.gateway.codegen.GatewayCodegen", "src-gen/com.knime.gateway.jsonrpc.local-config.json"})
public interface JsonRpcWizardExecutionService extends WizardExecutionService {

	/**
     * {@inheritDoc}
     */
    @Override
    @JsonRpcMethod(value = "WizardExecutionService.executeToNextPage")
    WizardPageEnt executeToNextPage(java.util.UUID jobId, Boolean async, Long timeout, WizardPageInputEnt wizardPageInputEnt)  throws ServiceExceptions.InvalidSettingsException, ServiceExceptions.NoWizardPageException, ServiceExceptions.TimeoutException;

	/**
     * {@inheritDoc}
     */
    @Override
    @JsonRpcMethod(value = "WizardExecutionService.getCurrentPage")
    WizardPageEnt getCurrentPage(java.util.UUID jobId) ;

	/**
     * {@inheritDoc}
     */
    @Override
    @JsonRpcMethod(value = "WizardExecutionService.getExecutionStatistics")
    ExecutionStatisticsEnt getExecutionStatistics(java.util.UUID jobId)  throws ServiceExceptions.NotFoundException;

	/**
     * {@inheritDoc}
     */
    @Override
    @JsonRpcMethod(value = "WizardExecutionService.getWebResource")
    byte[] getWebResource(java.util.UUID jobId, String resourceId)  throws ServiceExceptions.NotFoundException;

	/**
     * {@inheritDoc}
     */
    @Override
    @JsonRpcMethod(value = "WizardExecutionService.listWebResources")
    java.util.List<String> listWebResources(java.util.UUID jobId) ;

	/**
     * {@inheritDoc}
     */
    @Override
    @JsonRpcMethod(value = "WizardExecutionService.renderReport")
    byte[] renderReport(java.util.UUID jobId, String format)  throws ServiceExceptions.TimeoutException, ServiceExceptions.InvalidRequestException;

	/**
     * {@inheritDoc}
     */
    @Override
    @JsonRpcMethod(value = "WizardExecutionService.resetToPreviousPage")
    WizardPageEnt resetToPreviousPage(java.util.UUID jobId, Long timeout)  throws ServiceExceptions.NoWizardPageException, ServiceExceptions.TimeoutException;

}

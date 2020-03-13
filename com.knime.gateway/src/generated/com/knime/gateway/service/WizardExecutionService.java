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
package com.knime.gateway.service;

import com.knime.gateway.service.GatewayService;
import com.knime.gateway.service.util.ServiceExceptions;

import com.knime.gateway.entity.ExecutionStatisticsEnt;
import com.knime.gateway.entity.WizardPageEnt;
import com.knime.gateway.entity.WizardPageInputEnt;

/**
 * 
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
@javax.annotation.Generated(value = {"org.knime.gateway.codegen.GatewayCodegen", "src-gen/com.knime.gateway-interfaces-config.json"})
public interface WizardExecutionService extends GatewayService {

    /**
     * Executes the workflow to the next page. If no data is sent the job is executed with the current view values (if there are any), otherwise the view values are set according to the JSON map provided.
     *
     * @param jobId ID of the job the workflow is requested for.
     * @param async If true the call will return immediately after input validation and starting the execution. If false the call will wait until execution has finished (which is the default if no request parameter is specified)
     * @param timeout Timeout in milliseconds when synchronous execution is requested. The request will return with a timeout-response if the workflow doesn&#39;t finish execution (step) before the timeout
     * @param wizardPageInputEnt The input parameters for the next wizard page.
     *
     * @return the result
     * @throws ServiceExceptions.InvalidSettingsException If settings couldn&#39;t be applied.
     * @throws ServiceExceptions.NoWizardPageException If a wizard page is not available.
     * @throws ServiceExceptions.TimeoutException If the executor got a timeout, e.g., because a workflow didn&#39;t finish execution before the timeout.
     */
    WizardPageEnt executeToNextPage(java.util.UUID jobId, Boolean async, Long timeout, WizardPageInputEnt wizardPageInputEnt)  throws ServiceExceptions.InvalidSettingsException, ServiceExceptions.NoWizardPageException, ServiceExceptions.TimeoutException;
        
    /**
     * Returns the output for a workflow&#39;s current page. This is identical to the response returned by executing a workflow to the next page however it can be retrieved again at a later time. 
     *
     * @param jobId ID of the job the workflow is requested for.
     *
     * @return the result
     */
    WizardPageEnt getCurrentPage(java.util.UUID jobId) ;
        
    /**
     * Provides statistics on the node execution between wizard pages.
     *
     * @param jobId ID of the job the workflow is requested for.
     *
     * @return the result
     * @throws ServiceExceptions.NotFoundException A resource couldn&#39;t be found.
     */
    ExecutionStatisticsEnt getExecutionStatistics(java.util.UUID jobId)  throws ServiceExceptions.NotFoundException;
        
    /**
     * Returns a list of web resources needed for this job to handle wizard execution.
     *
     * @param jobId ID of the job the workflow is requested for.
     * @param resourceId The id (usually a relative path) of a single web resource (e.g. js, css, png, ...).
     *
     * @return the result
     * @throws ServiceExceptions.NotFoundException A resource couldn&#39;t be found.
     */
    byte[] getWebResource(java.util.UUID jobId, String resourceId)  throws ServiceExceptions.NotFoundException;
        
    /**
     * Returns a list of web resources needed for this job to handle wizard execution.
     *
     * @param jobId ID of the job the workflow is requested for.
     *
     * @return the result
     */
    java.util.List<String> listWebResources(java.util.UUID jobId) ;
        
    /**
     * Renders the report of an executed workflow into a certain format. 
     *
     * @param jobId ID of the job the workflow is requested for.
     * @param format the requested report format
     *
     * @return the result
     * @throws ServiceExceptions.TimeoutException If the executor got a timeout, e.g., because a workflow didn&#39;t finish execution before the timeout.
     * @throws ServiceExceptions.InvalidRequestException If the request is invalid for a reason.
     */
    byte[] renderReport(java.util.UUID jobId, String format)  throws ServiceExceptions.TimeoutException, ServiceExceptions.InvalidRequestException;
        
    /**
     * Resets a workflow to a previously executed page.
     *
     * @param jobId ID of the job the workflow is requested for.
     * @param timeout Time (in milliseconds) to wait for the workflow to be cancelled. The request will return with a timeout-response if  the cancellation didn&#39;t succeed before the timeout
     *
     * @return the result
     * @throws ServiceExceptions.NoWizardPageException If a wizard page is not available.
     * @throws ServiceExceptions.TimeoutException If the executor got a timeout, e.g., because a workflow didn&#39;t finish execution before the timeout.
     */
    WizardPageEnt resetToPreviousPage(java.util.UUID jobId, Long timeout)  throws ServiceExceptions.NoWizardPageException, ServiceExceptions.TimeoutException;
        
}

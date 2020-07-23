/*
 * ------------------------------------------------------------------------
 *  Copyright by KNIME AG, Zurich, Switzerland
 *  Website: http://www.knime.com; Email: contact@knime.com
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License, Version 3, as
 *  published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but
 *  WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, see <http://www.gnu.org/licenses>.
 *
 *  Additional permission under GNU GPL version 3 section 7:
 *
 *  KNIME interoperates with ECLIPSE solely via ECLIPSE's plug-in APIs.
 *  Hence, KNIME and ECLIPSE are both independent programs and are not
 *  derived from each other. Should, however, the interpretation of the
 *  GNU GPL Version 3 ("License") under any applicable laws result in
 *  KNIME and ECLIPSE being a combined program, KNIME AG herewith grants
 *  you the additional permission to use and propagate KNIME together with
 *  ECLIPSE with only the license terms in place for ECLIPSE applying to
 *  ECLIPSE and the GNU GPL Version 3 applying for KNIME, provided the
 *  license terms of ECLIPSE themselves allow for the respective use and
 *  propagation of ECLIPSE together with KNIME.
 *
 *  Additional permission relating to nodes for KNIME that extend the Node
 *  Extension (and in particular that are based on subclasses of NodeModel,
 *  NodeDialog, and NodeView) and that only interoperate with KNIME through
 *  standard APIs ("Nodes"):
 *  Nodes are deemed to be separate and independent programs and to not be
 *  covered works.  Notwithstanding anything to the contrary in the
 *  License, the License does not apply to Nodes, you are not required to
 *  license Nodes under the License, and you are granted a license to
 *  prepare and propagate Nodes, in each case even if such Nodes are
 *  propagated with or for interoperation with KNIME.  The owner of a Node
 *  may freely choose the license terms applicable to such Node, including
 *  when such Node is propagated with or for interoperation with KNIME.
 * ------------------------------------------------------------------------
 */
package org.knime.gateway.api.service;

import org.knime.gateway.api.service.GatewayService;
import org.knime.gateway.api.service.util.ServiceExceptions;

import org.knime.gateway.api.entity.ExecutionStatisticsEnt;
import org.knime.gateway.api.entity.WizardPageEnt;
import org.knime.gateway.api.entity.WizardPageInputEnt;

/**
 * 
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
@javax.annotation.Generated(value = {"com.knime.gateway.codegen.GatewayCodegen", "src-gen/org.knime.gateway.api-config.json"})
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

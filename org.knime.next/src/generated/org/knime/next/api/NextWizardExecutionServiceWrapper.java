/*
 * TODO license header
 */
package org.knime.next.api;

import com.knime.gateway.entity.ExecutionStatisticsEnt;
import com.knime.gateway.entity.WizardPageEnt;
import com.knime.gateway.entity.WizardPageInputEnt;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;

import org.knime.next.rest.api.AbstractServiceWrapper;

import com.knime.gateway.service.util.ServiceExceptions;

import com.knime.gateway.service.WizardExecutionService;

/**
 * KNIME Gateway API
 *
 * <p>Gateway operations on KNIME workflows.
 *
 * <p> 
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
@Path("/workflows/{job-id}")
@javax.annotation.Generated(value = {"org.knime.gateway.codegen.GatewayCodegen", "src-gen/org.knime.next.gateway-config.json"})
public class NextWizardExecutionServiceWrapper extends AbstractServiceWrapper {

    private final WizardExecutionService m_service;
    
    public NextWizardExecutionServiceWrapper(WizardExecutionService service) {
        m_service = service;
    }


    /**
     * Executes the workflow to the next page. If no data is sent the job is executed with the current view values (if there are any), otherwise the view values are set according to the JSON map provided.
     *
     * @param jobId ID of the job the workflow is requested for.
     * @param async If true the call will return immediately after input validation and starting the execution. If false the call will wait until execution has finished (which is the default if no request parameter is specified)
     * @param timeout Timeout in milliseconds when synchronous execution is requested. The request will return with a timeout-response if the workflow doesn&#39;t finish execution (step) before the timeout
     * @param wizardPageInputEnt The input parameters for the next wizard page.
     * @return the response
     * @throws ServiceExceptions.InvalidSettingsException If settings couldn&#39;t be applied.
     * @throws ServiceExceptions.NoWizardPageException If a wizard page is not available.
     * @throws ServiceExceptions.TimeoutException If the executor got a timeout, e.g., because a workflow didn&#39;t finish execution before the timeout.
     */
    @POST
    @Path("/workflow/wizard/next-page")
    @Consumes({ "application/json" })
    @Produces({ "application/json" })
    public Response executeToNextPage(@PathParam("job-id") java.util.UUID jobId, @QueryParam("async") Boolean async, @QueryParam("timeout") Long timeout, WizardPageInputEnt wizardPageInputEnt)
      throws ServiceExceptions.InvalidSettingsException, ServiceExceptions.NoWizardPageException, ServiceExceptions.TimeoutException {
    try {
        WizardPageEnt entity = m_service.executeToNextPage(jobId, async, timeout, wizardPageInputEnt);    
        return createResponse(entity);    
            
    }
	catch(ServiceExceptions.InvalidSettingsException e) {
	    //TODO
	    return null;
	}
	catch(ServiceExceptions.NoWizardPageException e) {
	    //TODO
	    return null;
	}
	catch(ServiceExceptions.TimeoutException e) {
	    //TODO
	    return null;
	}
	finally {
		//TODO
	}
   
   }

    /**
     * Returns the output for a workflow&#39;s current page. This is identical to the response returned by executing a workflow to the next page however it can be retrieved again at a later time. 
     *
     * @param jobId ID of the job the workflow is requested for.
     * @return the response
     */
    @GET
    @Path("/workflow/wizard/current-page")
    @Produces({ "application/json" })
    public Response getCurrentPage(@PathParam("job-id") java.util.UUID jobId)
      {
    try {
        WizardPageEnt entity = m_service.getCurrentPage(jobId);    
        return createResponse(entity);    
            
    }
	finally {
		//TODO
	}
   
   }

    /**
     * Provides statistics on the node execution between wizard pages.
     *
     * @param jobId ID of the job the workflow is requested for.
     * @return the response
     * @throws ServiceExceptions.NotFoundException A resource couldn&#39;t be found.
     */
    @GET
    @Path("/workflow/wizard/execution-statistics")
    @Produces({ "application/json" })
    public Response getExecutionStatistics(@PathParam("job-id") java.util.UUID jobId)
      throws ServiceExceptions.NotFoundException {
    try {
        ExecutionStatisticsEnt entity = m_service.getExecutionStatistics(jobId);    
        return createResponse(entity);    
            
    }
	catch(ServiceExceptions.NotFoundException e) {
	    //TODO
	    return null;
	}
	finally {
		//TODO
	}
   
   }

    /**
     * Returns a list of web resources needed for this job to handle wizard execution.
     *
     * @param jobId ID of the job the workflow is requested for.
     * @param resourceId The id (usually a relative path) of a single web resource (e.g. js, css, png, ...).
     * @return the response
     * @throws ServiceExceptions.NotFoundException A resource couldn&#39;t be found.
     */
    @GET
    @Path("/workflow/wizard/web-resources/{resource-id:[^:]*}")
    @Produces({ "*/*", "application/json" })
    public Response getWebResource(@PathParam("job-id") java.util.UUID jobId, @PathParam("resource-id") String resourceId)
      throws ServiceExceptions.NotFoundException {
    try {
        byte[] entity = m_service.getWebResource(jobId, resourceId);    
        return createResponse(entity);    
            
    }
	catch(ServiceExceptions.NotFoundException e) {
	    //TODO
	    return null;
	}
	finally {
		//TODO
	}
   
   }

    /**
     * Returns a list of web resources needed for this job to handle wizard execution.
     *
     * @param jobId ID of the job the workflow is requested for.
     * @return the response
     */
    @GET
    @Path("/workflow/wizard/web-resources")
    @Produces({ "application/json" })
    public Response listWebResources(@PathParam("job-id") java.util.UUID jobId)
      {
    try {
        java.util.List<String> entity = m_service.listWebResources(jobId);    
        return createResponse(entity);    
            
    }
	finally {
		//TODO
	}
   
   }

    /**
     * Renders the report of an executed workflow into a certain format. 
     *
     * @param jobId ID of the job the workflow is requested for.
     * @param format the requested report format
     * @return the response
     * @throws ServiceExceptions.TimeoutException If the executor got a timeout, e.g., because a workflow didn&#39;t finish execution before the timeout.
     * @throws ServiceExceptions.InvalidRequestException If the request is invalid for a reason.
     */
    @GET
    @Path("/workflow/wizard/report")
    @Produces({ "*/*", "application/json" })
    public Response renderReport(@PathParam("job-id") java.util.UUID jobId, @QueryParam("format") String format)
      throws ServiceExceptions.TimeoutException, ServiceExceptions.InvalidRequestException {
    try {
        byte[] entity = m_service.renderReport(jobId, format);    
        return createResponse(entity);    
            
    }
	catch(ServiceExceptions.TimeoutException e) {
	    //TODO
	    return null;
	}
	catch(ServiceExceptions.InvalidRequestException e) {
	    //TODO
	    return null;
	}
	finally {
		//TODO
	}
   
   }

    /**
     * Resets a workflow to a previously executed page.
     *
     * @param jobId ID of the job the workflow is requested for.
     * @param timeout Time (in milliseconds) to wait for the workflow to be cancelled. The request will return with a timeout-response if  the cancellation didn&#39;t succeed before the timeout
     * @return the response
     * @throws ServiceExceptions.NoWizardPageException If a wizard page is not available.
     * @throws ServiceExceptions.TimeoutException If the executor got a timeout, e.g., because a workflow didn&#39;t finish execution before the timeout.
     */
    @POST
    @Path("/workflow/wizard/previous-page")
    @Produces({ "application/json" })
    public Response resetToPreviousPage(@PathParam("job-id") java.util.UUID jobId, @QueryParam("timeout") Long timeout)
      throws ServiceExceptions.NoWizardPageException, ServiceExceptions.TimeoutException {
    try {
        WizardPageEnt entity = m_service.resetToPreviousPage(jobId, timeout);    
        return createResponse(entity);    
            
    }
	catch(ServiceExceptions.NoWizardPageException e) {
	    //TODO
	    return null;
	}
	catch(ServiceExceptions.TimeoutException e) {
	    //TODO
	    return null;
	}
	finally {
		//TODO
	}
   
   }
}


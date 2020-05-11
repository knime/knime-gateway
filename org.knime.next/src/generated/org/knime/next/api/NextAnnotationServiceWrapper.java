/*
 * TODO license header
 */
package org.knime.next.api;

import com.knime.gateway.entity.BoundsEnt;

import org.knime.next.rest.api.AbstractServiceWrapper;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;

import com.knime.gateway.service.util.ServiceExceptions;

import com.knime.gateway.service.AnnotationService;

/**
 * KNIME Gateway API
 *
 * <p>Gateway operations on KNIME workflows.
 *
 * <p> Operations on single workflow annotations.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
@Path("/workflows/{job-id}")
@javax.annotation.Generated(value = {"org.knime.gateway.codegen.GatewayCodegen", "src-gen/org.knime.next.gateway-config.json"})
public class NextAnnotationServiceWrapper extends AbstractServiceWrapper {

    private final AnnotationService m_service;
    
    public NextAnnotationServiceWrapper(AnnotationService service) {
        m_service = service;
    }


    /**
     * Changes the bounds (x,y,width,height) of a workflow annotation in a sub-workflow.
     *
     * @param jobId ID of the job the workflow is requested for.
     * @param annoId 
     * @param boundsEnt 
     * @return the response
     * @throws ServiceExceptions.NotASubWorkflowException The requested node is not a sub-workflow (i.e. a meta- or sub-node), but is required to be.
     * @throws ServiceExceptions.NotFoundException A resource couldn&#39;t be found.
     */
    @PUT
    @Path("/workflow/annotation/{anno-id}/bounds")
    @Consumes({ "application/json" })
    @Produces({ "application/json" })
    public Response setAnnotationBounds(@PathParam("job-id") java.util.UUID jobId, @PathParam("anno-id") com.knime.gateway.entity.AnnotationIDEnt annoId, BoundsEnt boundsEnt)
      throws ServiceExceptions.NotASubWorkflowException, ServiceExceptions.NotFoundException {
    try {
        m_service.setAnnotationBounds(jobId, annoId, boundsEnt);    
            
        return createResponse();    
    }
	catch(ServiceExceptions.NotASubWorkflowException e) {
	    //TODO
	    return null;
	}
	catch(ServiceExceptions.NotFoundException e) {
	    //TODO
	    return null;
	}
	finally {
		//TODO
	}
   
   }
}


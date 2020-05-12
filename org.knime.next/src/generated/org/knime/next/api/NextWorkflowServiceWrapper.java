/*
 * TODO license header
 */
package org.knime.next.api;

import com.knime.gateway.entity.ConnectionEnt;
import com.knime.gateway.entity.PatchEnt;
import com.knime.gateway.entity.WorkflowPartsEnt;
import com.knime.gateway.entity.WorkflowSnapshotEnt;

import org.knime.next.rest.api.AbstractServiceWrapper;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;

import com.knime.gateway.service.util.ServiceExceptions;

import com.knime.gateway.service.WorkflowService;

/**
 * KNIME Gateway API
 *
 * <p>Gateway operations on KNIME workflows.
 *
 * <p> Operations on workflows.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
@Path("/workflows/{job-id}")
@javax.annotation.Generated(value = {"org.knime.gateway.codegen.GatewayCodegen", "src-gen/org.knime.next.gateway-config.json"})
public class NextWorkflowServiceWrapper extends AbstractServiceWrapper {

    private final WorkflowService m_service;
    
    public NextWorkflowServiceWrapper(WorkflowService service) {
        m_service = service;
    }


    /**
     * Creates a new connection between two nodes. Note: replaces/removes existing connections if destination port is already in use.
     *
     * @param jobId ID of the job the workflow is requested for.
     * @param connectionEnt The connection.
     * @return the response
     * @throws ServiceExceptions.ActionNotAllowedException If the an action is not allowed because it&#39;s not applicable or it doesn&#39;t exist.
     */
    @POST
    @Path("/workflow/connection")
    @Consumes({ "application/json" })
    @Produces({ "application/json" })
    public Response createConnection(@PathParam("job-id") java.util.UUID jobId, ConnectionEnt connectionEnt)
      throws ServiceExceptions.ActionNotAllowedException {
    try {
        com.knime.gateway.entity.ConnectionIDEnt entity = m_service.createConnection(jobId, connectionEnt);    
        return createResponse(entity);    
            
    }
	catch(ServiceExceptions.ActionNotAllowedException e) {
	    //TODO
	    return null;
	}
	finally {
		//TODO
	}
   
   }

    /**
     * Selects and essentially copies the specified part of the workflow. It will still be available even if (sub)parts are deleted. The parts are referenced by a part-id.  Note: connections will be ignored and _not_ copied!
     *
     * @param jobId ID of the job the workflow is requested for.
     * @param workflowId The ID of a worklow which has the same format as a node-id.
     * @param workflowPartsEnt The actual part selection.
     * @return the response
     * @throws ServiceExceptions.NotASubWorkflowException The requested node is not a sub-workflow (i.e. a meta- or sub-node), but is required to be.
     * @throws ServiceExceptions.NodeNotFoundException The requested node was not found.
     * @throws ServiceExceptions.InvalidRequestException If the request is invalid for a reason.
     */
    @POST
    @Path("/workflow/{workflow-id}/parts")
    @Consumes({ "application/json" })
    @Produces({ "application/json" })
    public Response createWorkflowCopy(@PathParam("job-id") java.util.UUID jobId, @PathParam("workflow-id") com.knime.gateway.entity.NodeIDEnt workflowId, WorkflowPartsEnt workflowPartsEnt)
      throws ServiceExceptions.NotASubWorkflowException, ServiceExceptions.NodeNotFoundException, ServiceExceptions.InvalidRequestException {
    try {
        java.util.UUID entity = m_service.createWorkflowCopy(jobId, workflowId, workflowPartsEnt);    
        return createResponse(entity);    
            
    }
	catch(ServiceExceptions.NotASubWorkflowException e) {
	    //TODO
	    return null;
	}
	catch(ServiceExceptions.NodeNotFoundException e) {
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
     * Deletes the given workflow parts. Cannot be undone unless a copy has been made before.
     *
     * @param jobId ID of the job the workflow is requested for.
     * @param workflowId The ID of a worklow which has the same format as a node-id.
     * @param workflowPartsEnt The parts to be deleted.
     * @param copy If a copy should be created before removal. False by default. Please note that the copy will _only_ include the connections that are entirely enclosed by the parts to be removed (i.e. connections that are connecting two removed nodes - all others won&#39;t be kept)
     * @return the response
     * @throws ServiceExceptions.NotASubWorkflowException The requested node is not a sub-workflow (i.e. a meta- or sub-node), but is required to be.
     * @throws ServiceExceptions.NodeNotFoundException The requested node was not found.
     * @throws ServiceExceptions.ActionNotAllowedException If the an action is not allowed because it&#39;s not applicable or it doesn&#39;t exist.
     */
    @DELETE
    @Path("/workflow/{workflow-id}/parts")
    @Consumes({ "application/json" })
    @Produces({ "application/json" })
    public Response deleteWorkflowParts(@PathParam("job-id") java.util.UUID jobId, @PathParam("workflow-id") com.knime.gateway.entity.NodeIDEnt workflowId, WorkflowPartsEnt workflowPartsEnt, @QueryParam("copy") Boolean copy)
      throws ServiceExceptions.NotASubWorkflowException, ServiceExceptions.NodeNotFoundException, ServiceExceptions.ActionNotAllowedException {
    try {
        java.util.UUID entity = m_service.deleteWorkflowParts(jobId, workflowId, workflowPartsEnt, copy);    
        return createResponse(entity);    
            
    }
	catch(ServiceExceptions.NotASubWorkflowException e) {
	    //TODO
	    return null;
	}
	catch(ServiceExceptions.NodeNotFoundException e) {
	    //TODO
	    return null;
	}
	catch(ServiceExceptions.ActionNotAllowedException e) {
	    //TODO
	    return null;
	}
	finally {
		//TODO
	}
   
   }

    /**
     * Retrieves the complete structure (nodes, connections, annotations) of (sub-)workflows.
     *
     * @param jobId ID of the job the workflow is requested for.
     * @param workflowId The ID of a worklow which has the same format as a node-id.
     * @return the response
     * @throws ServiceExceptions.NotASubWorkflowException The requested node is not a sub-workflow (i.e. a meta- or sub-node), but is required to be.
     * @throws ServiceExceptions.NodeNotFoundException The requested node was not found.
     */
    @GET
    @Path("/workflow/{workflow-id}")
    @Produces({ "application/json" })
    public Response getWorkflow(@PathParam("job-id") java.util.UUID jobId, @PathParam("workflow-id") com.knime.gateway.entity.NodeIDEnt workflowId)
      throws ServiceExceptions.NotASubWorkflowException, ServiceExceptions.NodeNotFoundException {
    try {
        WorkflowSnapshotEnt entity = m_service.getWorkflow(jobId, workflowId);    
        return createResponse(entity);    
            
    }
	catch(ServiceExceptions.NotASubWorkflowException e) {
	    //TODO
	    return null;
	}
	catch(ServiceExceptions.NodeNotFoundException e) {
	    //TODO
	    return null;
	}
	finally {
		//TODO
	}
   
   }

    /**
     * Gives the changes of the sub-workflow as a patch.
     *
     * @param jobId ID of the job the workflow is requested for.
     * @param workflowId The ID of a worklow which has the same format as a node-id.
     * @param snapshotId The id of the workflow snapshot already retrieved.
     * @return the response
     * @throws ServiceExceptions.NotASubWorkflowException The requested node is not a sub-workflow (i.e. a meta- or sub-node), but is required to be.
     * @throws ServiceExceptions.NotFoundException A resource couldn&#39;t be found.
     */
    @GET
    @Path("/workflowdiff/{workflow-id}")
    @Produces({ "application/json" })
    public Response getWorkflowDiff(@PathParam("job-id") java.util.UUID jobId, @PathParam("workflow-id") com.knime.gateway.entity.NodeIDEnt workflowId, @QueryParam("snapshot-id") java.util.UUID snapshotId)
      throws ServiceExceptions.NotASubWorkflowException, ServiceExceptions.NotFoundException {
    try {
        PatchEnt entity = m_service.getWorkflowDiff(jobId, workflowId, snapshotId);    
        return createResponse(entity);    
            
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

    /**
     * Pastes the referenced parts into the referenced (sub-)workflow.
     *
     * @param jobId ID of the job the workflow is requested for.
     * @param workflowId The ID of a worklow which has the same format as a node-id.
     * @param partsId The id referencing the parts to paste.
     * @param x The x position to paste the parts.
     * @param y The y position to paste the parts.
     * @return the response
     * @throws ServiceExceptions.NotASubWorkflowException The requested node is not a sub-workflow (i.e. a meta- or sub-node), but is required to be.
     * @throws ServiceExceptions.NotFoundException A resource couldn&#39;t be found.
     */
    @PUT
    @Path("/workflow/{workflow-id}/parts/{parts-id}")
    @Produces({ "application/json" })
    public Response pasteWorkflowParts(@PathParam("job-id") java.util.UUID jobId, @PathParam("workflow-id") com.knime.gateway.entity.NodeIDEnt workflowId, @PathParam("parts-id") java.util.UUID partsId, @QueryParam("x") Integer x, @QueryParam("y") Integer y)
      throws ServiceExceptions.NotASubWorkflowException, ServiceExceptions.NotFoundException {
    try {
        WorkflowPartsEnt entity = m_service.pasteWorkflowParts(jobId, workflowId, partsId, x, y);    
        return createResponse(entity);    
            
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

    /**
     * TODO
     *
     * @param jobId ID of the job the workflow is requested for.
     * @param workflowId The ID of a worklow which has the same format as a node-id.
     * @return the response
     * @throws ServiceExceptions.NotASubWorkflowException The requested node is not a sub-workflow (i.e. a meta- or sub-node), but is required to be.
     * @throws ServiceExceptions.NotFoundException A resource couldn&#39;t be found.
     */
    @POST
    @Path("/workflow/{workflow-id}/redo")
    @Produces({ "application/json" })
    public Response redo(@PathParam("job-id") java.util.UUID jobId, @PathParam("workflow-id") com.knime.gateway.entity.NodeIDEnt workflowId)
      throws ServiceExceptions.NotASubWorkflowException, ServiceExceptions.NotFoundException {
    try {
        Boolean entity = m_service.redo(jobId, workflowId);    
        return createResponse(entity);    
            
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

    /**
     * TODO
     *
     * @param jobId ID of the job the workflow is requested for.
     * @param workflowId The ID of a worklow which has the same format as a node-id.
     * @return the response
     * @throws ServiceExceptions.NotASubWorkflowException The requested node is not a sub-workflow (i.e. a meta- or sub-node), but is required to be.
     * @throws ServiceExceptions.NotFoundException A resource couldn&#39;t be found.
     */
    @POST
    @Path("/workflow/{workflow-id}/undo")
    @Produces({ "application/json" })
    public Response undo(@PathParam("job-id") java.util.UUID jobId, @PathParam("workflow-id") com.knime.gateway.entity.NodeIDEnt workflowId)
      throws ServiceExceptions.NotASubWorkflowException, ServiceExceptions.NotFoundException {
    try {
        Boolean entity = m_service.undo(jobId, workflowId);    
        return createResponse(entity);    
            
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


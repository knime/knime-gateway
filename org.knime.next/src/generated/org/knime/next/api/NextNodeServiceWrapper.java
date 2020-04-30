/*
 * TODO license header
 */
package org.knime.next.api;

import com.knime.gateway.entity.BoundsEnt;
import com.knime.gateway.entity.DataTableEnt;
import com.knime.gateway.entity.FlowVariableEnt;
import com.knime.gateway.entity.JavaObjectEnt;
import com.knime.gateway.entity.MetaNodeDialogEnt;
import com.knime.gateway.entity.NodeEnt;
import com.knime.gateway.entity.NodeFactoryKeyEnt;
import com.knime.gateway.entity.NodeSettingsEnt;
import com.knime.gateway.entity.PortObjectSpecEnt;
import com.knime.gateway.entity.ViewDataEnt;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;

import com.knime.gateway.service.util.ServiceExceptions;

import com.knime.gateway.service.NodeService;

/**
 * KNIME Gateway API
 *
 * <p>Gateway operations on KNIME workflows.
 *
 * <p> Operations on single nodes.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
@Path("/workflows/{job-id}")
@javax.annotation.Generated(value = {"org.knime.gateway.codegen.GatewayCodegen", "src-gen/org.knime.next.gateway-config.json"})
public class NextNodeServiceWrapper extends AbstractServiceWrapper {

    private final NodeService m_service;
    
    public NextNodeServiceWrapper(NodeService service) {
        m_service = service;
    }


    /**
     * Retrieves and manipulates the node&#39;s state for the given node-id.
     *
     * @param jobId ID of the job the workflow is requested for.
     * @param nodeId The ID of a node. The node-id format: Node IDs always start with &#39;root&#39; and optionally followed by numbers separated by &#39;:&#39; refering to nested nodes/subworkflows,e.g. root:3:6:4. Nodes within components require an additional trailing &#39;0&#39;, e.g. &#39;root:3:6:0:4&#39; (if &#39;root:3:6&#39; is a component).
     * @param action The action (reset, cancel, execute) to be performed in order to change the node&#39;s state.
     * @return the response
     * @throws ServiceExceptions.NodeNotFoundException The requested node was not found.
     * @throws ServiceExceptions.ActionNotAllowedException If the an action is not allowed because it&#39;s not applicable or it doesn&#39;t exist.
     */
    @POST
    @Path("/workflow/node/{node-id}/state")
    @Produces({ "application/json" })
    public Response changeAndGetNodeState(@PathParam("job-id") java.util.UUID jobId, @PathParam("node-id") com.knime.gateway.entity.NodeIDEnt nodeId, @QueryParam("action") String action)
      throws ServiceExceptions.NodeNotFoundException, ServiceExceptions.ActionNotAllowedException {
    try {
        String entity = m_service.changeAndGetNodeState(jobId, nodeId, action);    
        return createResponse(entity);    
            
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
     * Creates and adds a new native node to the workflow.
     *
     * @param jobId ID of the job the workflow is requested for.
     * @param workflowId The ID of a worklow which has the same format as a node-id.
     * @param x the x coordinate to place the new node
     * @param y the y coordinate to place the new node
     * @param nodeFactoryKeyEnt The key representing the native node to be added to the workflow.
     * @return the response
     * @throws ServiceExceptions.NotASubWorkflowException The requested node is not a sub-workflow (i.e. a meta- or sub-node), but is required to be.
     * @throws ServiceExceptions.NodeNotFoundException The requested node was not found.
     * @throws ServiceExceptions.InvalidRequestException If the request is invalid for a reason.
     */
    @POST
    @Path("/workflow/{workflow-id}")
    @Consumes({ "application/json" })
    @Produces({ "application/json" })
    public Response createNode(@PathParam("job-id") java.util.UUID jobId, @PathParam("workflow-id") com.knime.gateway.entity.NodeIDEnt workflowId, @QueryParam("x") Integer x, @QueryParam("y") Integer y, NodeFactoryKeyEnt nodeFactoryKeyEnt)
      throws ServiceExceptions.NotASubWorkflowException, ServiceExceptions.NodeNotFoundException, ServiceExceptions.InvalidRequestException {
    try {
        com.knime.gateway.entity.NodeIDEnt entity = m_service.createNode(jobId, workflowId, x, y, nodeFactoryKeyEnt);    
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
     * Provides the node&#39;s flow variables available for the node with the given node-id.
     *
     * @param jobId ID of the job the workflow is requested for.
     * @param nodeId The ID of a node. The node-id format: Node IDs always start with &#39;root&#39; and optionally followed by numbers separated by &#39;:&#39; refering to nested nodes/subworkflows,e.g. root:3:6:4. Nodes within components require an additional trailing &#39;0&#39;, e.g. &#39;root:3:6:0:4&#39; (if &#39;root:3:6&#39; is a component).
     * @return the response
     * @throws ServiceExceptions.NodeNotFoundException The requested node was not found.
     */
    @GET
    @Path("/workflow/node/{node-id}/input/flowvariables")
    @Produces({ "application/json" })
    public Response getInputFlowVariables(@PathParam("job-id") java.util.UUID jobId, @PathParam("node-id") com.knime.gateway.entity.NodeIDEnt nodeId)
      throws ServiceExceptions.NodeNotFoundException {
    try {
        java.util.List<FlowVariableEnt> entity = m_service.getInputFlowVariables(jobId, nodeId);    
        return createResponse(entity);    
            
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
     * Provides the node&#39;s input port specifications for the given node-id. I.e. all output port specs of the output ports connected to the input ports of this node.
     *
     * @param jobId ID of the job the workflow is requested for.
     * @param nodeId The ID of a node. The node-id format: Node IDs always start with &#39;root&#39; and optionally followed by numbers separated by &#39;:&#39; refering to nested nodes/subworkflows,e.g. root:3:6:4. Nodes within components require an additional trailing &#39;0&#39;, e.g. &#39;root:3:6:0:4&#39; (if &#39;root:3:6&#39; is a component).
     * @return the response
     * @throws ServiceExceptions.NodeNotFoundException The requested node was not found.
     */
    @GET
    @Path("/workflow/node/{node-id}/input/specs")
    @Produces({ "application/json" })
    public Response getInputPortSpecs(@PathParam("job-id") java.util.UUID jobId, @PathParam("node-id") com.knime.gateway.entity.NodeIDEnt nodeId)
      throws ServiceExceptions.NodeNotFoundException {
    try {
        java.util.List<PortObjectSpecEnt> entity = m_service.getInputPortSpecs(jobId, nodeId);    
        return createResponse(entity);    
            
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
     * Retrieves the node for the given node-id.
     *
     * @param jobId ID of the job the workflow is requested for.
     * @param nodeId The ID of a node. The node-id format: Node IDs always start with &#39;root&#39; and optionally followed by numbers separated by &#39;:&#39; refering to nested nodes/subworkflows,e.g. root:3:6:4. Nodes within components require an additional trailing &#39;0&#39;, e.g. &#39;root:3:6:0:4&#39; (if &#39;root:3:6&#39; is a component).
     * @return the response
     * @throws ServiceExceptions.NodeNotFoundException The requested node was not found.
     */
    @GET
    @Path("/workflow/node/{node-id}")
    @Produces({ "application/json" })
    public Response getNode(@PathParam("job-id") java.util.UUID jobId, @PathParam("node-id") com.knime.gateway.entity.NodeIDEnt nodeId)
      throws ServiceExceptions.NodeNotFoundException {
    try {
        NodeEnt entity = m_service.getNode(jobId, nodeId);    
        return createResponse(entity);    
            
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
     * Retrieves the node&#39;s settings for the given node-id.
     *
     * @param jobId ID of the job the workflow is requested for.
     * @param nodeId The ID of a node. The node-id format: Node IDs always start with &#39;root&#39; and optionally followed by numbers separated by &#39;:&#39; refering to nested nodes/subworkflows,e.g. root:3:6:4. Nodes within components require an additional trailing &#39;0&#39;, e.g. &#39;root:3:6:0:4&#39; (if &#39;root:3:6&#39; is a component).
     * @return the response
     * @throws ServiceExceptions.NodeNotFoundException The requested node was not found.
     */
    @GET
    @Path("/workflow/node/{node-id}/settings")
    @Produces({ "application/json" })
    public Response getNodeSettings(@PathParam("job-id") java.util.UUID jobId, @PathParam("node-id") com.knime.gateway.entity.NodeIDEnt nodeId)
      throws ServiceExceptions.NodeNotFoundException {
    try {
        NodeSettingsEnt entity = m_service.getNodeSettings(jobId, nodeId);    
        return createResponse(entity);    
            
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
     * Gives access to the table at the a certain port index (if it&#39;s a table). Otherwise will return &#39;not supported&#39;.
     *
     * @param jobId ID of the job the workflow is requested for.
     * @param nodeId The ID of a node. The node-id format: Node IDs always start with &#39;root&#39; and optionally followed by numbers separated by &#39;:&#39; refering to nested nodes/subworkflows,e.g. root:3:6:4. Nodes within components require an additional trailing &#39;0&#39;, e.g. &#39;root:3:6:0:4&#39; (if &#39;root:3:6&#39; is a component).
     * @param portIdx The port index to get the table for.
     * @param from Row index to start returning the rows. Rows from the beginning (i.e. index 0) will be returned.
     * @param size Number of rows to retrieve. If not given, all rows to the end of the table are returned.
     * @return the response
     * @throws ServiceExceptions.NodeNotFoundException The requested node was not found.
     * @throws ServiceExceptions.InvalidRequestException If the request is invalid for a reason.
     */
    @GET
    @Path("/workflow/node/{node-id}/output/{port-idx}/table")
    @Produces({ "application/json" })
    public Response getOutputDataTable(@PathParam("job-id") java.util.UUID jobId, @PathParam("node-id") com.knime.gateway.entity.NodeIDEnt nodeId, @PathParam("port-idx") Integer portIdx, @QueryParam("from") Long from, @QueryParam("size") Integer size)
      throws ServiceExceptions.NodeNotFoundException, ServiceExceptions.InvalidRequestException {
    try {
        DataTableEnt entity = m_service.getOutputDataTable(jobId, nodeId, portIdx, from, size);    
        return createResponse(entity);    
            
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
     * Provides the node&#39;s flow variables available for the node&#39;s output with the given node-id. For metanodes (not components) the same variables are returned as with &#39;... For metanodes (not components) the same variables are returned as with &#39;.../input/flowvariables&#39;.
     *
     * @param jobId ID of the job the workflow is requested for.
     * @param nodeId The ID of a node. The node-id format: Node IDs always start with &#39;root&#39; and optionally followed by numbers separated by &#39;:&#39; refering to nested nodes/subworkflows,e.g. root:3:6:4. Nodes within components require an additional trailing &#39;0&#39;, e.g. &#39;root:3:6:0:4&#39; (if &#39;root:3:6&#39; is a component).
     * @return the response
     * @throws ServiceExceptions.NodeNotFoundException The requested node was not found.
     */
    @GET
    @Path("/workflow/node/{node-id}/output/flowvariables")
    @Produces({ "application/json" })
    public Response getOutputFlowVariables(@PathParam("job-id") java.util.UUID jobId, @PathParam("node-id") com.knime.gateway.entity.NodeIDEnt nodeId)
      throws ServiceExceptions.NodeNotFoundException {
    try {
        java.util.List<FlowVariableEnt> entity = m_service.getOutputFlowVariables(jobId, nodeId);    
        return createResponse(entity);    
            
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
     * Provides the node&#39;s output port specifications for the given node-id.
     *
     * @param jobId ID of the job the workflow is requested for.
     * @param nodeId The ID of a node. The node-id format: Node IDs always start with &#39;root&#39; and optionally followed by numbers separated by &#39;:&#39; refering to nested nodes/subworkflows,e.g. root:3:6:4. Nodes within components require an additional trailing &#39;0&#39;, e.g. &#39;root:3:6:0:4&#39; (if &#39;root:3:6&#39; is a component).
     * @return the response
     * @throws ServiceExceptions.NodeNotFoundException The requested node was not found.
     */
    @GET
    @Path("/workflow/node/{node-id}/output/specs")
    @Produces({ "application/json" })
    public Response getOutputPortSpecs(@PathParam("job-id") java.util.UUID jobId, @PathParam("node-id") com.knime.gateway.entity.NodeIDEnt nodeId)
      throws ServiceExceptions.NodeNotFoundException {
    try {
        java.util.List<PortObjectSpecEnt> entity = m_service.getOutputPortSpecs(jobId, nodeId);    
        return createResponse(entity);    
            
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
     * Provides the data for the node&#39;s view, if the node supports views. The data includes the view&#39;s and the representation.
     *
     * @param jobId ID of the job the workflow is requested for.
     * @param nodeId The ID of a node. The node-id format: Node IDs always start with &#39;root&#39; and optionally followed by numbers separated by &#39;:&#39; refering to nested nodes/subworkflows,e.g. root:3:6:4. Nodes within components require an additional trailing &#39;0&#39;, e.g. &#39;root:3:6:0:4&#39; (if &#39;root:3:6&#39; is a component).
     * @return the response
     * @throws ServiceExceptions.NodeNotFoundException The requested node was not found.
     * @throws ServiceExceptions.InvalidRequestException If the request is invalid for a reason.
     */
    @GET
    @Path("/workflow/node/{node-id}/view/data")
    @Produces({ "application/json" })
    public Response getViewData(@PathParam("job-id") java.util.UUID jobId, @PathParam("node-id") com.knime.gateway.entity.NodeIDEnt nodeId)
      throws ServiceExceptions.NodeNotFoundException, ServiceExceptions.InvalidRequestException {
    try {
        ViewDataEnt entity = m_service.getViewData(jobId, nodeId);    
        return createResponse(entity);    
            
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
     * Gives access to the dialog representations, values and configs of a component.
     *
     * @param jobId ID of the job the workflow is requested for.
     * @param nodeId The ID of a node. The node-id format: Node IDs always start with &#39;root&#39; and optionally followed by numbers separated by &#39;:&#39; refering to nested nodes/subworkflows,e.g. root:3:6:4. Nodes within components require an additional trailing &#39;0&#39;, e.g. &#39;root:3:6:0:4&#39; (if &#39;root:3:6&#39; is a component).
     * @return the response
     * @throws ServiceExceptions.NodeNotFoundException The requested node was not found.
     * @throws ServiceExceptions.InvalidRequestException If the request is invalid for a reason.
     */
    @GET
    @Path("/workflow/wmetanode/{node-id}/dialog")
    @Produces({ "application/json" })
    public Response getWMetaNodeDialog(@PathParam("job-id") java.util.UUID jobId, @PathParam("node-id") com.knime.gateway.entity.NodeIDEnt nodeId)
      throws ServiceExceptions.NodeNotFoundException, ServiceExceptions.InvalidRequestException {
    try {
        MetaNodeDialogEnt entity = m_service.getWMetaNodeDialog(jobId, nodeId);    
        return createResponse(entity);    
            
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
     * Sets the node&#39;s bounds for the given node-id.
     *
     * @param jobId ID of the job the workflow is requested for.
     * @param nodeId The ID of a node. The node-id format: Node IDs always start with &#39;root&#39; and optionally followed by numbers separated by &#39;:&#39; refering to nested nodes/subworkflows,e.g. root:3:6:4. Nodes within components require an additional trailing &#39;0&#39;, e.g. &#39;root:3:6:0:4&#39; (if &#39;root:3:6&#39; is a component).
     * @param boundsEnt The node bounds to set.
     * @return the response
     * @throws ServiceExceptions.NodeNotFoundException The requested node was not found.
     */
    @PUT
    @Path("/workflow/node/{node-id}/bounds")
    @Consumes({ "application/json" })
    @Produces({ "application/json" })
    public Response setNodeBounds(@PathParam("job-id") java.util.UUID jobId, @PathParam("node-id") com.knime.gateway.entity.NodeIDEnt nodeId, BoundsEnt boundsEnt)
      throws ServiceExceptions.NodeNotFoundException {
    try {
        m_service.setNodeBounds(jobId, nodeId, boundsEnt);    
            
        return createResponse();    
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
     * Sets the node&#39;s settings for the given node-id.
     *
     * @param jobId ID of the job the workflow is requested for.
     * @param nodeId The ID of a node. The node-id format: Node IDs always start with &#39;root&#39; and optionally followed by numbers separated by &#39;:&#39; refering to nested nodes/subworkflows,e.g. root:3:6:4. Nodes within components require an additional trailing &#39;0&#39;, e.g. &#39;root:3:6:0:4&#39; (if &#39;root:3:6&#39; is a component).
     * @param nodeSettingsEnt The node settings to set.
     * @return the response
     * @throws ServiceExceptions.NodeNotFoundException The requested node was not found.
     * @throws ServiceExceptions.InvalidSettingsException If settings couldn&#39;t be applied.
     * @throws ServiceExceptions.IllegalStateException If node is not in the right state to apply the settings.
     */
    @PUT
    @Path("/workflow/node/{node-id}/settings")
    @Consumes({ "application/json" })
    @Produces({ "application/json" })
    public Response setNodeSettings(@PathParam("job-id") java.util.UUID jobId, @PathParam("node-id") com.knime.gateway.entity.NodeIDEnt nodeId, NodeSettingsEnt nodeSettingsEnt)
      throws ServiceExceptions.NodeNotFoundException, ServiceExceptions.InvalidSettingsException, ServiceExceptions.IllegalStateException {
    try {
        m_service.setNodeSettings(jobId, nodeId, nodeSettingsEnt);    
            
        return createResponse();    
    }
	catch(ServiceExceptions.NodeNotFoundException e) {
	    //TODO
	    return null;
	}
	catch(ServiceExceptions.InvalidSettingsException e) {
	    //TODO
	    return null;
	}
	catch(ServiceExceptions.IllegalStateException e) {
	    //TODO
	    return null;
	}
	finally {
		//TODO
	}
   
   }

    /**
     * Saves back the view&#39;s value (e.g. title etc.) to the server.
     *
     * @param jobId ID of the job the workflow is requested for.
     * @param nodeId The ID of a node. The node-id format: Node IDs always start with &#39;root&#39; and optionally followed by numbers separated by &#39;:&#39; refering to nested nodes/subworkflows,e.g. root:3:6:4. Nodes within components require an additional trailing &#39;0&#39;, e.g. &#39;root:3:6:0:4&#39; (if &#39;root:3:6&#39; is a component).
     * @param useAsDefault True if node settings are to be updated by view content.
     * @param javaObjectEnt The view value to set.
     * @return the response
     * @throws ServiceExceptions.NodeNotFoundException The requested node was not found.
     * @throws ServiceExceptions.InvalidRequestException If the request is invalid for a reason.
     */
    @PUT
    @Path("/workflow/node/{node-id}/view/data/value")
    @Consumes({ "application/json" })
    @Produces({ "application/json" })
    public Response setViewValue(@PathParam("job-id") java.util.UUID jobId, @PathParam("node-id") com.knime.gateway.entity.NodeIDEnt nodeId, @QueryParam("use-as-default") Boolean useAsDefault, JavaObjectEnt javaObjectEnt)
      throws ServiceExceptions.NodeNotFoundException, ServiceExceptions.InvalidRequestException {
    try {
        m_service.setViewValue(jobId, nodeId, useAsDefault, javaObjectEnt);    
            
        return createResponse();    
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
}


package com.knime.gateway.jsonrpc.remote.service;

import org.knime.gateway.v0.entity.WorkflowEnt;

import com.googlecode.jsonrpc4j.JsonRpcError;
import com.googlecode.jsonrpc4j.JsonRpcErrors;
import com.googlecode.jsonrpc4j.JsonRpcMethod;
import com.googlecode.jsonrpc4j.JsonRpcParam;
import com.googlecode.jsonrpc4j.JsonRpcService;

import org.knime.gateway.v0.service.util.ServiceExceptions;

import org.knime.gateway.v0.service.WorkflowService;

/**
 * Json rpc annotated class that wraps another service and delegates the method calls. 
 *
 * @author Martin Horn, University of Konstanz
 */
@JsonRpcService(value = "WorkflowService")
@javax.annotation.Generated(value = "org.knime.gateway.codegen.GatewayCodegen", date = "2018-01-10T17:43:16.417+01:00")
public class JsonRpcWorkflowServiceWrapper implements WorkflowService {

    private final WorkflowService m_service;
    
    public JsonRpcWorkflowServiceWrapper(WorkflowService service) {
        m_service = service;
    }

	/**
     * {@inheritDoc}
     */
    @Override
    @JsonRpcMethod(value = "getWorkflow")
    public WorkflowEnt getWorkflow(@JsonRpcParam(value="jobId") java.util.UUID jobId, String nodeId) {
        return m_service.getWorkflow(jobId, nodeId);    
    }

}

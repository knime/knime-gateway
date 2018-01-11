package com.knime.gateway.jsonrpc.remote.service;

import org.knime.gateway.v0.entity.NodeEnt;

import com.googlecode.jsonrpc4j.JsonRpcError;
import com.googlecode.jsonrpc4j.JsonRpcErrors;
import com.googlecode.jsonrpc4j.JsonRpcMethod;
import com.googlecode.jsonrpc4j.JsonRpcParam;
import com.googlecode.jsonrpc4j.JsonRpcService;

import org.knime.gateway.v0.service.util.ServiceExceptions;

import org.knime.gateway.v0.service.NodeService;

/**
 * Json rpc annotated class that wraps another service and delegates the method calls. 
 *
 * @author Martin Horn, University of Konstanz
 */
@JsonRpcService(value = "NodeService")
@javax.annotation.Generated(value = "org.knime.gateway.codegen.GatewayCodegen", date = "2018-01-10T17:43:16.417+01:00")
public class JsonRpcNodeServiceWrapper implements NodeService {

    private final NodeService m_service;
    
    public JsonRpcNodeServiceWrapper(NodeService service) {
        m_service = service;
    }

	/**
     * {@inheritDoc}
     */
    @Override
    @JsonRpcMethod(value = "getNode")
    @JsonRpcErrors(value = {
        @JsonRpcError(exception = ServiceExceptions.NodeNotFoundException.class, code = -32600,
            data = "404" /*per convention the data property contains the status code*/)
    })
    public NodeEnt getNode(@JsonRpcParam(value="jobId") java.util.UUID jobId, @JsonRpcParam(value="nodeId") String nodeId) throws ServiceExceptions.NodeNotFoundException {
        return m_service.getNode(jobId, nodeId);    
    }

	/**
     * {@inheritDoc}
     */
    @Override
    @JsonRpcMethod(value = "getNodeSettings")
    @JsonRpcErrors(value = {
        @JsonRpcError(exception = ServiceExceptions.NodeNotFoundException.class, code = -32600,
            data = "404" /*per convention the data property contains the status code*/)
    })
    public String getNodeSettings(@JsonRpcParam(value="jobId") java.util.UUID jobId, @JsonRpcParam(value="nodeId") String nodeId) throws ServiceExceptions.NodeNotFoundException {
        return m_service.getNodeSettings(jobId, nodeId);    
    }

}

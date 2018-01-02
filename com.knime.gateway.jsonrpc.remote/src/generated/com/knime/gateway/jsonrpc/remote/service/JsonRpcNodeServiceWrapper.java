package com.knime.gateway.jsonrpc.remote.service;

import org.knime.gateway.v0.entity.NodeEnt;

import com.googlecode.jsonrpc4j.JsonRpcMethod;
import com.googlecode.jsonrpc4j.JsonRpcParam;
import com.googlecode.jsonrpc4j.JsonRpcService;

import org.knime.gateway.v0.service.NodeService;

/**
 * Json rpc annotated class that wraps another service and delegates the method calls. 
 *
 * @author Martin Horn, University of Konstanz
 */
@JsonRpcService(value = "NodeService")
@javax.annotation.Generated(value = "org.knime.gateway.codegen.GatewayCodegen", date = "2018-01-02T16:29:34.776+01:00")
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
    public NodeEnt getNode(@JsonRpcParam(value="jobId") String jobId, String nodeId) {
        return m_service.getNode(jobId, nodeId);    
    }

	/**
     * {@inheritDoc}
     */
    @Override
    @JsonRpcMethod(value = "getNodeSettings")
    public String getNodeSettings(@JsonRpcParam(value="jobId") String jobId, String nodeId) {
        return m_service.getNodeSettings(jobId, nodeId);    
    }

}

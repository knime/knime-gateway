# KNIME Server Gateway

## Brief summary of contained projects

### _com.knime.gateway.remote_: 
* to be shipped with a KNIME executor on the server-side
* contains the default implementations of the service interfaces (from _org.knime.gateway_) that essentially delegate/transform the service calls into _org.knime.core_-method-calls (e.g. to the WorkflowManager)
* results are accordingly translated into entities again (see EntityBuilderUtil)

### _com.knime.gateway.jsonrpc.remote_:
* to be shipped with the KNIME executor on the server-side
* receives [json-rpc 2.0](www.jsonrpc.org) messages from the server (e.g. via MQ or RMI) and turns them into gateway API service calls that in turn uses the default service implementations from _com.knime.gateway.remote_
* the json-rpc service implementations (that wrap the default service implementations) are auto-generated (see _org.knime.gateway.codegen_)

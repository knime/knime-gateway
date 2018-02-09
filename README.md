# KNIME Gateway API definition and related projects

## Brief summary of contained projects

### _com.knime.gateway_: 
* contains all the entity, entity builder and service interfaces (e.g. WorkflowEnt, NodeEnt, WorkflowService, ExecutionService etc.)
* contains auto-generated interfaces and classes; generation logic and the Swagger specification file is located in 'knime-shared/org.knime.gateway.codegen'
* some 'API'-remarks
  * there are entities that are the messages to be send in between two computers consisting either of other entities or primitives (or list/maps of those)
  * entity interfaces have all a trailing 'Ent'
  * entities are generated with a corresponding entity builder ("EntBuilder"-suffix)
  * service interfaces all have a trailing "Service" and represent a collection of methods that either have entities or primitives as parameters/return types - a service-method corresponds to a resource-endpoint in the swagger specification

### _com.knime.gateway.local_:
* essentially an implementation of _org.knime.core.ui_ that turns the respective UI-commands, -actions, -requests etc. into gateway API service calls (as defined in _org.knime.core.gateway_) usually communicated to a KNIME server (another local-only implementation of _org.knime.core.ui_ is _org.knime.core_)
* the actual service implementations, e.g. defining how to forward the actual service calls to the server, are injected via the respective extension point 'ServiceFactory'

### _com.knime.gateway.explorer_:
* establishes the connection between the gateway client (that communicates with the KNIME-server, e.g. via REST) and the KNIME explorer's (remote) job workflow view

### _com.knime.gateway.json_:
* mainly contains json-based implementations of the entity and entity builder interfaces (i.e. the entities are de-/serialized from/to json via jackson)
* entity and entity builder implementations are auto-generated

### _com.knime.gateway.jsonrpc.local_:
* an actual implementation of the gateway services that forward the service calls to a specific rest-endpoint at the KNIME-server (extends the 'ServiceFactory'-extension point of _org.knime.gateway.local_)
* the service calls are translated into [json-rpc 2.0](http://www.jsonrpc.org/) requests and responses in order to be transfered
* the service implementations are auto-generated

### _com.knime.gateway.rest.local_:
* an implementation of the gateway services that forward the service calls to rest-endpoints at the KNIME-server (extends the 'ServiceFactory'-extension point of _org.knime.gateway.local_)

### _com.knime.gateway.remote_: 
* to be shipped with a KNIME executor on the server-side
* contains the default implementations of the service interfaces (from _org.knime.gateway_) that essentially delegate/transform the service calls into _org.knime.core_-method-calls (e.g. to the WorkflowManager)
* results are accordingly translated into entities again (see EntityBuilderUtil)

### _com.knime.gateway.jsonrpc.remote_:
* to be shipped with the KNIME executor on the server-side
* receives [json-rpc 2.0](www.jsonrpc.org) messages from the server (e.g. via MQ or RMI) and turns them into gateway API service calls that in turn uses the default service implementations from _com.knime.gateway.remote_
* the json-rpc service implementations (that wrap the default service implementations) are auto-generated (see _knime-shared/org.knime.gateway.codegen_)

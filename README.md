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

### _com.knime.gateway.jsonrpc_:
* mainly contains json-based implementations of the entity and entity builder interfaces (i.e. the entities are de-/serialized from/to json via jackson)
* entity and entity builder implementations are auto-generated

### _com.knime.gateway.jsonrpc.local_:
* an actual implementation of the gateway services that forward the service calls to a specific rest-endpoint at the KNIME-server (extends the 'ServiceFactory'-extension point of _org.knime.gateway.local_)
* the service calls are translated into [json-rpc 2.0](http://www.jsonrpc.org/) requests and responses in order to be transfered
* the service implementations are auto-generated



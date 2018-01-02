# KNIME Gateway API definition and related projects

## Brief summary of contained projects

### _org.knime.gateway_: 
* contains all the entity, entity builder and service interfaces (e.g. WorkflowEnt, NodeEnt, WorkflowService, ExecutionService etc.)
* interfaces (among others) are auto-generated from a Swagger 2.0 specification (a yaml-file) - see 'api/gateway.yaml'
* contains also the (auto-generated) default implementations of the entity interfaces
* some 'API'-remarks
  * there are entities that are the messages to be send in between two computers consisting either of other entities or primitives (or list/maps of those)
  * entity interfaces have all a trailing 'Ent'
  * entities are generated with a corresponding entity builder ("EntBuilder"-suffix)
  * service interfaces all have a trailing "Service" and represent a collection of methods that either have entities or primitives as parameters/return types - a service-method corresponds to a resource-endpoint in the swagger specification

### _org.knime.gateway.codegen_:
* contains the code to auto-generate code from the api definition (in org.knime.gateway/api)
* it auto-generates code for various projects and assumes their static existence relative to this project
* the 'src-gen/*-config.json'-file determine the what is generated where
* for auto-generation the swagger-codegen tools are used and extended (which in turn uses mustache templates, see 'src-gen/templates')
* for code generation run org.knime.gateway.codegen.Generate
* only needed for development, never deployed with the actual product

### _org.knime.gateway.local_:
* essentially an implementation of _org.knime.core.ui_ that turns the respective UI-commands, -actions, -requests etc. into gateway API service calls (as defined in _org.knime.core.gateway_) usually communicated to a KNIME server (another local-only implementation of _org.knime.core.ui_ is _org.knime.core_)
* the actual service implementations, e.g. defining how to forward the actual service calls to the server, are injected via the respective extension point 'ServiceFactory'

### _org.knime.gateway.jsonrpc_:
* mainly contains json-based implementations of the entity and entity builder interfaces (i.e. the entities are de-/serialized from/to json via jackson)
* entity and entity builder implementations are auto-generated

### _org.knime.gateway.jsonrpc.local_:
* an actual implementation of the gateway services that forward the service calls to a specific rest-endpoint at the KNIME-server (extends the 'ServiceFactory'-extension point of _org.knime.gateway.local_)
* the service calls are translated into [json-rpc 2.0](http://www.jsonrpc.org/) requests and responses in order to be transfered
* the service implementations are auto-generated



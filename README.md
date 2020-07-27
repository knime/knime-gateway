# KNIME Gateway API definition and related projects

## Brief summary of contained projects

*Note*: some projects contain auto-generated code. The code-generation tool etc. is located at 'knime-com-shared/com.knime.gateway.codegen'.

### _org.knime.gateway.api_: 
* contains all the entity, entity builder and service interfaces (e.g. WorkflowEnt, NodeEnt, WorkflowService, ExecutionService etc.)
* contains auto-generated interfaces and classes
* some 'API'-remarks
  * _entities_ are the messages to be send in between two computers consisting either of other entities or primitives (or list/maps of those)
  * entity interfaces have all a trailing 'Ent'
  * entities are generated with a corresponding entity builder ("EntBuilder"-suffix)
  * service interfaces all have a trailing "Service" and represent a collection of methods that either have entities or primitives as parameters/return types - a service-method corresponds to a resource-endpoint in the swagger specification

### _org.knime.gateway.impl_:
* contains the default implementations of the service interfaces (from _org.knime.gateway.api_) that essentially delegate/transform the service calls into _org.knime.core_-method-calls (e.g. to the WorkflowManager)
* contains the (auto-generated) default implementations of the entities and entity-builders

### _org.knime.gateway.json_:
* mainly contains json-based implementations of the entity and entity builder interfaces (i.e. the entities are de-/serialized from/to json via jackson)
* entity and entity builder implementations are auto-generated

### _org.knime.gateway.impl.jsonrpc_:
* utilities and service implementations that transform a [json-rpc 2.0](http://www.jsonrpc.org/) request into an actual service call to the service's default implementation (_org.knime.gateway.impl_) and returns a jsonrpc-response

### _org.knime.gateway.testing.helper_:
* contain the testing logic to test all service-implementations; moved into a 'helper'-project for re-usability; e.g. used by the 'server-integration-tests' _and_ 'org.knime.gateway.impl.tests'
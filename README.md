# KNIME Gateway API definition and related projects

**IMPORTANT NOTE: though it's called API none of the interfaces or classes of these projects are meant to be public API. They can change any time.**

## Brief summary of contained projects

*Note*: some projects contain auto-generated code. The code-generation tool etc. is located at 'knime-com-shared/com.knime.gateway.codegen'.

### _org.knime.gateway.api_: 
* contains all the entity, entity builder and service interfaces for the web-ui API (generated)
* some 'API'-remarks
  * _entities_ are the messages to be send in between two computers consisting either of other entities or primitives (or list/maps of those)
  * entity interfaces have all a trailing 'Ent'
  * entities are generated with a corresponding entity builder ("EntBuilder"-suffix)
  * service interfaces all have a trailing "Service" and represent a collection of methods that either have entities or primitives as parameters/return types - a service-method corresponds to a resource-endpoint in the swagger specification

### _org.knime.gateway.impl_:
* contains the default implementations of the service interfaces (from _org.knime.gateway.api_) that essentially delegate/transform the service calls into _org.knime.core_-method-calls (e.g. to the WorkflowManager)
* contains the (generated) default implementations of the entities and entity-builders
* service implementations for the web-ui API

### _org.knime.gateway.json_:
* generated mixins interfaces for the entities for json-de-/serialization via the jackson lib
* only for the web-ui API

### _org.knime.gateway.impl.jsonrpc_:
* utilities and service implementations that transform a [json-rpc 2.0](http://www.jsonrpc.org/) request into an actual service call to the service's default implementation (_org.knime.gateway.impl_) and returns a jsonrpc-response
* only for the web-ui API

### _org.knime.gateway.testing.helper_:
* contain the testing logic to test all service-implementations for the web-ui API
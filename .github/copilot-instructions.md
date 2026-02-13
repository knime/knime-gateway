# Code Style and Conventions
## ServiceCallException vs runtime exceptions
- We raise user-facing (or user-caused) error cases via a checked ServiceCallException / MutableServiceCallException.
- Exceptions which are "unexpected" in the sense that they are not an anticipated product error case are thrown as
  runtime exceptions by conventions -- these are still handled by the frontend. In these cases, throwing a runtime
  exception is acceptable and we do not need to construct a ServiceCallException.

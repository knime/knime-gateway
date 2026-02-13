# Code Style and Conventions

## Generated code
- We check in generated code to version control. Generated code files usually have `generated` in their path.
- Do not point out code style issues in generated code files.
- If javadoc, symbol names or descriptions are problematic, please request to fix in the source spec used to generate
  from.

## ServiceCallException vs runtime exceptions
- We raise user-facing (or user-caused) error cases via a checked ServiceCallException / MutableServiceCallException.
- Exceptions which are "unexpected" in the sense that they are not an anticipated product error case are thrown as
  runtime exceptions by conventions -- these are still handled by the frontend. In these cases, throwing a runtime
  exception is acceptable and we do not need to construct a ServiceCallException.

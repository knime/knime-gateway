# JSON-RPC for Java classpath fix

This fragment is required because "JSON-RPC for Java" doesn't import
`javax.jws` via the new Jakarta packages, leading to a logged exception
at each startup of AP.

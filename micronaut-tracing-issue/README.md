# micronaut-tracing-issue

## Running

`mvn clean install exec:exec`

## Testing

### Baseline with 1.2.8

`curl -sSv -w '\n' http://localhost:8080/v1/serviceA/foobar`

This should return HTTP/200 with a payload of `1`

### Baseline with 1.3.0.M2

Modify `pom.xml` to change the `micronaut.version` from 1.2.8 to 1.3.0.M2

Restart the service and run `curl` again which will return HTTP/500 and a payload of `{"message":"Internal Server Error: Internal Server Error: expected: <bar> but was: <null>"}`

`ServiceA.java` has some `Assertions` that can be uncommented to show that the `SpanContext` does not appear to be propagated between the various stages
package micronaut.tracing.issue;

import io.micronaut.http.HttpHeaders;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.MediaType;
import io.micronaut.http.MutableHttpHeaders;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.client.RxHttpClient;
import io.micronaut.http.client.annotation.Client;
import io.opentracing.Scope;
import io.opentracing.Span;
import io.opentracing.SpanContext;
import io.opentracing.Tracer;
import io.reactivex.Single;
import org.junit.jupiter.api.Assertions;

import javax.inject.Inject;
import java.nio.ByteBuffer;
import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.stream.StreamSupport;


@Controller("/v1/serviceA")
public class ServiceA {
    @Inject
    @Client("http://localhost:8080")
    private RxHttpClient client;

    @Inject
    private Tracer tracer;

    @Get(value = "/{string}", produces = MediaType.TEXT_PLAIN)
    public Single<Integer> foo(final String string) {
        Assertions.assertTrue(tracer.activeSpan().toString().endsWith("GET /v1/serviceA/{string}"), tracer.activeSpan().toString());
        Assertions.assertNull(tracer.activeSpan().getBaggageItem("foo"));

        // Add baggage to this Span
        tracer.activeSpan().setBaggageItem("foo", "bar");
        Assertions.assertEquals("bar", tracer.activeSpan().getBaggageItem("foo"));

        final Single<Integer> serviceBResponse =
            client.retrieve(HttpRequest.GET("/v1/serviceB/"), Integer.class).singleOrError();
        final Single<Integer> serviceCResponse =
            serviceBResponse.doOnSuccess(System.out::println)  // print out response
//                            .doOnSuccess(i -> Assertions.assertNotNull(tracer.activeSpan()))
//                            .doOnSuccess(i -> Assertions.assertTrue(tracer.activeSpan().toString().endsWith("GET /v1/serviceA/{string}")))
//                            .doOnSuccess(i -> Assertions.assertEquals("bar", tracer.activeSpan().getBaggageItem("foo")))
                            .flatMap(i -> client.retrieve(HttpRequest.GET("/v1/serviceC/" + string + "/" + i), Integer.class).singleOrError());
        return serviceCResponse;
    }
}

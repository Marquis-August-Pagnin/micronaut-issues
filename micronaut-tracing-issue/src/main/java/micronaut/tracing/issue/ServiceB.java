package micronaut.tracing.issue;

import io.micronaut.http.HttpRequest;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.opentracing.Tracer;
import org.junit.jupiter.api.Assertions;

import javax.inject.Inject;
import java.util.Random;


@Controller("/v1/serviceB")
public class ServiceB {
    private final Random random = new Random();

    @Inject
    private Tracer tracer;

    @Get(value = "/", produces = MediaType.TEXT_PLAIN)
    int foo(final HttpRequest<?> httpRequest) {
        Assertions.assertEquals("bar", tracer.activeSpan().getBaggageItem("foo"));
        return random.nextInt();
    }
}

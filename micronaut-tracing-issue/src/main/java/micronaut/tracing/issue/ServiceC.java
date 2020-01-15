package micronaut.tracing.issue;

import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.opentracing.Tracer;
import io.reactivex.Single;
import org.junit.jupiter.api.Assertions;

import javax.inject.Inject;


@Controller("/v1/serviceC")
public class ServiceC {
    @Inject
    private Tracer tracer;

    @Get(value = "/{string}/{number}", produces = MediaType.TEXT_PLAIN)
    Single<Integer> foo(final String string, final int number) {
        Assertions.assertEquals("bar", tracer.activeSpan().getBaggageItem("foo"));
        return Single.just(1);
    }
}

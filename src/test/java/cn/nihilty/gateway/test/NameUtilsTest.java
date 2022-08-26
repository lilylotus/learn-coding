package cn.nihilty.gateway.test;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.cloud.gateway.handler.predicate.CookieRoutePredicateFactory;
import org.springframework.cloud.gateway.support.NameUtils;
import reactor.core.publisher.Mono;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

class NameUtilsTest {

    @Test
    void testPredicateName() {
        String name = NameUtils.normalizeRoutePredicateNameAsProperty(CookieRoutePredicateFactory.class);
        Assertions.assertEquals("cookie", name);
    }

    @Test
    void testMono() {
        /*Mono<String> ok = Mono.just("ok");
        Mono<Object> mono = Mono.defer(() -> Mono.fromRunnable(() -> System.out.println("Hello")));
        ok.doFinally(signalType -> System.out.println("doFinally [" + signalType.name() + "]"));
        ok.then(Mono.create(monoSink -> System.out.println("then")));

        ok.subscribe();*/

        Mono.create((t) -> t.success("ok"))
                .then(Mono.create(t -> t.success("success")))
                .subscribe(System.out::println);

    }

}

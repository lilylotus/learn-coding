package cn.nihility.cloud.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

/**
 * @author daffodil
 * @date 2020-10-02 15:21:36
 */
@RestController
public class GatewayController {

    @RequestMapping("/countriesfallback")
    public Mono<String> countries() {
        return Mono.just("Countries API is taking too long to respond or is down. Please try again later");
    }
    @RequestMapping("/jokefallback")
    public Mono<String> joke() {
        return Mono.just("Joke API is taking too long to respond or is down. Please try again later");
    }

    @RequestMapping("/gatewayFallback")
    public Mono<String> gateway() {
        return Mono.just("Gateway taking too long to respond or is down. Please try again later");
    }

}

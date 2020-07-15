package cn.nihility.cloud.controller;

import org.springframework.cloud.client.loadbalancer.reactive.ReactorLoadBalancerExchangeFilterFunction;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

/**
 * BalanceController
 *
 * @author dandelion
 * @date 2020-04-10 23:55
 */
@RestController
public class BalanceController {

    private final WebClient.Builder loadBalancedWebClientBuilder;
    private final ReactorLoadBalancerExchangeFilterFunction lbFunction;

    public BalanceController(WebClient.Builder loadBalancedWebClientBuilder,
                             ReactorLoadBalancerExchangeFilterFunction lbFunction) {
        this.loadBalancedWebClientBuilder = loadBalancedWebClientBuilder;
        this.lbFunction = lbFunction;
    }

    @RequestMapping("/hi")
    public Mono<String> hi(@RequestParam(value = "name", defaultValue = "Mary") String name) {
        return loadBalancedWebClientBuilder.build().get().uri("http://cloud-load-balancer-client/greeting")
                .retrieve().bodyToMono(String.class)
                .map(v -> String.format("%s, %s!", v, name));
    }

    @RequestMapping("/hello")
    public Mono<String> hello(@RequestParam(value = "name", defaultValue = "John") String name) {
        return WebClient.builder()
                .filter(lbFunction)
                .build().get().uri("http://cloud-load-balancer-client/greeting")
                .retrieve().bodyToMono(String.class)
                .map(v -> String.format("%s, %s!", v, name));
    }

}

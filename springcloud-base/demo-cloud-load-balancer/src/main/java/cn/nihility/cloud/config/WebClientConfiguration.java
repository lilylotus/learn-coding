package cn.nihility.cloud.config;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.loadbalancer.annotation.LoadBalancerClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * WebClientConfiguration
 *
 * @author dandelion
 * @date 2020-04-11 00:21
 */
@Configuration
@LoadBalancerClient(name = "load-balancer-client", configuration = BalanceConfiguration.class)
public class WebClientConfiguration {


    @LoadBalanced
    @Bean
    public WebClient.Builder webClientBuilder() {
        return WebClient.builder();
    }

}

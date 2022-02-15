package cn.nihility.cloud.openfeign.client;

import org.springframework.context.annotation.Configuration;

/**
 * disable Spring Cloud CircuitBreaker support on a per-client
 * basis create a vanilla Feign.Builder with the "prototype" scope,
 */
@Configuration
public class ServiceProviderClientConfiguration {

    /* To disable Hystrix support on a per-client basis
    create a vanilla Feign.Builder with the "prototype" scope
     */
    /*@Bean
    @Scope("prototype")
    public Feign.Builder feignBuilder() {
        return Feign.builder();
    }*/

}

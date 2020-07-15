package cn.nihility.cloud.gateway.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.cloud.netflix.hystrix.EnableHystrix;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * GatewayConfig
 *
 * @author dandelion
 * @date 2020-04-14 10:26
 */
@Configuration
@EnableHystrix
public class GatewayConfig {

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                .route(p -> p.path("/v1/joke")
                        .filters(f -> f.addRequestHeader("x-rapidapi-host", "joke3.p.rapidapi.com")
                                .addRequestHeader("x-rapidapi-key", "e6cde8f529msh1fbb615261eba03p1fbc05jsnd0d98175cbdc")
                                .hystrix(config -> config.setName("joke-service")
                                .setFallbackUri("forward:/jokefallback")))
                        .uri("https://joke3.p.rapidapi.com"))
                .route(p -> p.path("/all")
                        .filters(f -> f.addRequestHeader("x-rapidapi-host", "nanosdk-countries-v1.p.rapidapi.com")
                                .addRequestHeader("x-rapidapi-key", "e6cde8f529msh1fbb615261eba03p1fbc05jsnd0d98175cbdc")
                                .hystrix(config -> config.setName("countries-service")
                                .setFallbackUri("forward:/countriesfallback")))
                        .uri("https://nanosdk-countries-v1.p.rapidapi.com"))
                .build();
    }

   /* @Bean
    public ServerCodecConfigurer serverCodecConfigurer() {
        return ServerCodecConfigurer.create();
    }*/

}

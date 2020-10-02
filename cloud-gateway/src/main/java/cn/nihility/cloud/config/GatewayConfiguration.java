package cn.nihility.cloud.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayConfiguration {

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                // https://joke3.p.rapidapi.com/v1/joke
                .route(p -> p.path("/v1/joke")
                        .filters(f -> f.addRequestHeader("x-rapidapi-host", "joke3.p.rapidapi.com")
                                .addRequestHeader("x-rapidapi-key", "dde78fbf59msh63a1bb7a3776b45p136996jsn9d5d45c3c96f")
                                .addRequestHeader("useQueryString", "true")
                                .hystrix(config -> config.setName("joke-service")
                                        .setFallbackUri("forward:/jokefallback")))
                        .uri("https://joke3.p.rapidapi.com"))
                .route(p -> p.path("/rest/v1/all")
                        .filters(f -> f.addRequestHeader("x-rapidapi-host", "ajayakv-rest-countries-v1.p.rapidapi.com")
                                .addRequestHeader("x-rapidapi-key", "dde78fbf59msh63a1bb7a3776b45p136996jsn9d5d45c3c96f")
                                .addRequestHeader("useQueryString", "true")
                                .hystrix(config -> config.setName("countries-service")
                                        .setFallbackUri("forward:/countriesfallback")))
                        .uri("https://ajayakv-rest-countries-v1.p.rapidapi.com"))
                .build();
    }

   /* @Bean
    public ServerCodecConfigurer serverCodecConfigurer() {
        return ServerCodecConfigurer.create();
    }*/

}

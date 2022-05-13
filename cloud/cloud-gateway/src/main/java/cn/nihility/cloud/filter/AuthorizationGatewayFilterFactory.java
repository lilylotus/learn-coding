package cn.nihility.cloud.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.style.ToStringCreator;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Component
public class AuthorizationGatewayFilterFactory
    extends AbstractGatewayFilterFactory<AuthorizationGatewayFilterFactory.Config> {

    private static final Logger logger = LoggerFactory.getLogger(AuthorizationGatewayFilterFactory.class);

    private static final String AUTHORIZATION_HEADER_TAG = "Authorization";

    public AuthorizationGatewayFilterFactory() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return new GatewayFilter() {
            @Override
            public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
                String authorization = Optional.ofNullable(exchange.getRequest().getHeaders().getFirst(AUTHORIZATION_HEADER_TAG))
                    .orElse(exchange.getRequest().getQueryParams().getFirst(AUTHORIZATION_HEADER_TAG));
                logger.info("Authorization = {}", authorization);

                if (StringUtils.hasLength(authorization) && authorization.startsWith(config.getPrefix())) {
                    final ServerHttpResponse response = exchange.getResponse();
                    response.getHeaders().set("Authorization-Status", "Success");
                    //response.setComplete();
                }

                return chain.filter(exchange);
            }
        };
    }

    @Override
    public List<String> shortcutFieldOrder() {
        return Collections.singletonList("prefix");
    }

    public static class Config {

        private String prefix;

        public String getPrefix() {
            return prefix;
        }

        public void setPrefix(String prefix) {
            this.prefix = prefix;
        }

        @Override
        public String toString() {
            return new ToStringCreator(this).append("prefix", prefix).toString();
        }
    }

}

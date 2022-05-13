package cn.nihility.cloud.predicate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.handler.predicate.AbstractRoutePredicateFactory;
import org.springframework.cloud.gateway.handler.predicate.GatewayPredicate;
import org.springframework.core.style.ToStringCreator;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.server.ServerWebExchange;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

@Component
public class AuthorizationRoutePredicateFactory extends AbstractRoutePredicateFactory<AuthorizationRoutePredicateFactory.Config> {

    private static final Logger logger = LoggerFactory.getLogger(AuthorizationRoutePredicateFactory.class);

    private static final String AUTHORIZATION_HEADER_TAG = "Authorization";

    public AuthorizationRoutePredicateFactory() {
        super(Config.class);
    }

    @Override
    public List<String> shortcutFieldOrder() {
        return Collections.singletonList("prefix");
    }

    @Override
    public Predicate<ServerWebExchange> apply(Config config) {
        return new GatewayPredicate() {
            @Override
            public boolean test(ServerWebExchange exchange) {
                String authorization = Optional.ofNullable(exchange.getRequest().getHeaders().getFirst(AUTHORIZATION_HEADER_TAG))
                    .orElse(exchange.getRequest().getQueryParams().getFirst(AUTHORIZATION_HEADER_TAG));
                logger.info("Authorization = {}", authorization);
                return StringUtils.hasLength(authorization) && authorization.startsWith(config.getPrefix());
            }
        };
    }

    @Validated
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

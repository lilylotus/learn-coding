package cn.nihilty.gateway.fliter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.config.conditional.ConditionalOnEnabledFilter;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.cloud.gateway.support.GatewayToStringStyler;
import org.springframework.cloud.gateway.support.ServerWebExchangeUtils;
import org.springframework.core.style.ToStringCreator;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import javax.validation.constraints.NotEmpty;
import java.util.Arrays;
import java.util.List;

@Component
@ConditionalOnEnabledFilter(CustomerGatewayFilterFactory.class)
public class CustomerGatewayFilterFactory extends AbstractGatewayFilterFactory<CustomerGatewayFilterFactory.Config> {

    private static final Logger log = LoggerFactory.getLogger(CustomerGatewayFilterFactory.class);
    /**
     * Name key.
     */
    private static final String NAME_KEY = "name";
    /**
     * Value key.
     */
    private static final String VALUE_KEY = "value";

    public CustomerGatewayFilterFactory() {
        super(Config.class);
    }

    /**
     * RouteDefinitionRouteLocator#convertToRoute
     * RouteDefinitionRouteLocator#combinePredicates
     * RouteDefinitionRouteLocator#lookup
     * RoutePredicateFactory#applyAsync(C)
     *
     * @param config 参数配置
     * @return 自定义过滤器
     */
    @Override
    public GatewayFilter apply(Config config) {
        return new GatewayFilter() {
            @Override
            public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
                final String value = ServerWebExchangeUtils.expand(exchange, config.getValue());
                ServerHttpRequest request = exchange.getRequest().mutate()
                        .headers(httpHeaders -> httpHeaders.add(config.getName(), value)).build();
                ServerWebExchange webExchange = exchange.mutate().request(request).build();
                if (log.isDebugEnabled()) {
                    log.debug("Customer Filter Add Header {}:{}", config.getName(), value);
                }
                return chain.filter(webExchange);
            }

            @Override
            public String toString() {
                return GatewayToStringStyler.filterToStringCreator(CustomerGatewayFilterFactory.this)
                        .append(config.getName(), config.getValue()).toString();
            }
        };
    }

    @Override
    public List<String> shortcutFieldOrder() {
        return Arrays.asList(NAME_KEY, VALUE_KEY);
    }

    @Validated
    public static class Config {

        @NotEmpty
        protected String name;

        @NotEmpty
        protected String value;

        public String getName() {
            return name;
        }

        public Config setName(String name) {
            this.name = name;
            return this;
        }

        public String getValue() {
            return value;
        }

        public Config setValue(String value) {
            this.value = value;
            return this;
        }

        @Override
        public String toString() {
            return new ToStringCreator(this).append("name", name).append("value", value).toString();
        }

    }
}

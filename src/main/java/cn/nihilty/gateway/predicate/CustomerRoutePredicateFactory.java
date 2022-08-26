package cn.nihilty.gateway.predicate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.config.conditional.ConditionalOnEnabledPredicate;
import org.springframework.cloud.gateway.handler.predicate.AbstractRoutePredicateFactory;
import org.springframework.cloud.gateway.handler.predicate.GatewayPredicate;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.server.ServerWebExchange;

import javax.validation.constraints.NotEmpty;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;

@Component
@ConditionalOnEnabledPredicate(CustomerRoutePredicateFactory.class)
public class CustomerRoutePredicateFactory extends AbstractRoutePredicateFactory<CustomerRoutePredicateFactory.Config> {

    private static final Logger log = LoggerFactory.getLogger(CustomerRoutePredicateFactory.class);

    /**
     * Header key.
     */
    public static final String HEADER_KEY = "header";

    /**
     * Regexp key.
     */
    public static final String REGEXP_KEY = "regexp";

    public CustomerRoutePredicateFactory() {
        super(Config.class);
    }

    @Override
    public List<String> shortcutFieldOrder() {
        return Arrays.asList(HEADER_KEY, REGEXP_KEY);
    }

    @Override
    public Predicate<ServerWebExchange> apply(Config config) {
        //final boolean regexEnabled = !Objects.isNull(config.regexp);
        return new GatewayPredicate() {
            @Override
            public boolean test(ServerWebExchange serverWebExchange) {
                List<String> values = serverWebExchange.getRequest()
                        .getHeaders().getOrDefault(config.header, Collections.emptyList());
                log.info("Customer Route: {}:{}", config.header, values);
                return true;
                /*if (values.isEmpty()) {
                    return false;
                }
                if (regexEnabled) {
                    for (String h : values) {
                        if (h.matches(config.regexp)) {
                            return true;
                        }
                    }
                    return false;
                }
                return true;*/
            }

            @Override
            public Object getConfig() {
                return config;
            }

            @Override
            public String toString() {
                return String.format("Customer: header=%s regexp=%s", config.header, config.regexp);
            }
        };
    }

    @Validated
    public static class Config {

        @NotEmpty
        private String header;

        private String regexp;

        public String getHeader() {
            return header;
        }

        public Config setHeader(String header) {
            this.header = header;
            return this;
        }

        public String getRegexp() {
            return regexp;
        }

        public Config setRegexp(String regexp) {
            this.regexp = regexp;
            return this;
        }

    }

}

package cn.nihilty.gateway.fliter;

import cn.nihilty.gateway.constant.Constant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.cloud.gateway.config.conditional.ConditionalOnEnabledGlobalFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Component
@ConditionalOnEnabledGlobalFilter(CustomerGlobalFilter.class)
public class CustomerGlobalFilter implements GlobalFilter, Ordered {

    private static final Logger log = LoggerFactory.getLogger(CustomerGlobalFilter.class);

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String traceId = request.getHeaders().getFirst(Constant.TRACE_ID);
        if (!StringUtils.hasText(traceId)) {
            traceId = MDC.get(Constant.TRACE_ID);
        }
        if (!StringUtils.hasText(traceId)) {
            traceId = UUID.randomUUID().toString().replace("-", "");
        }
        MDC.put(Constant.TRACE_ID, traceId);
        final String path = request.getPath().toString();
        final long startTimeMillis = System.currentTimeMillis();
        log.info("Customer Global Filter {} start", path);

        return chain.filter(exchange).then(Mono.fromSupplier(() -> {
            log.info("Customer Global Filter {} end, status {}, duration {}", path,
                    exchange.getResponse().getRawStatusCode(), (System.currentTimeMillis() - startTimeMillis));
            MDC.remove(Constant.TRACE_ID);
            return null;
        }));

    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }

}

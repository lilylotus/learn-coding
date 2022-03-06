package cn.nihility.plugin.log4j2.filter;

import cn.nihility.plugin.log4j2.constant.Constant;
import org.slf4j.MDC;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.UUID;

/**
 * @author sakura
 * @date 2022-03-06 16:36
 */
@Component
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.REACTIVE)
public class ReactiveTraceIdFilter implements WebFilter {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {

        MDC.put(Constant.TRACE_ID, UUID.randomUUID().toString().replace("-", ""));

        return chain.filter(exchange).doFinally(signalType -> MDC.remove(Constant.TRACE_ID));

    }

}

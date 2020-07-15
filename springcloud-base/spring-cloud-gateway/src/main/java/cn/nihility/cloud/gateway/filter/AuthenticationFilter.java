package cn.nihility.cloud.gateway.filter;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * AuthenticationFilter
 * 自定义全局过滤器
 * @author dandelion
 * @date 2020-04-27 11:34
 */
@Component
public class AuthenticationFilter implements GlobalFilter, Ordered {
    private static final Logger log = LoggerFactory.getLogger(AuthenticationFilter.class);
    /* 执行过滤器业务逻辑,返回值 chain.filter(exchange) 才会继续执行 */
    /* ServerWebExchange 中可以获取 web 环境的上下文  */
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        log.info("Gateway 全局过滤器 AuthenticationFilter -> filter()");

        // 1. 获取请求参数 token
        final ServerHttpRequest request = exchange.getRequest();
        final String token = request.getQueryParams().getFirst("token");
        // 2. 判断 token 的信息
        if (StringUtils.isEmpty(token)) {
            // 2.1 token 认证失败
            log.error("认证失败， token 不可为空");
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete(); // 请求结束
        }
        // 3. 认证通过
        return chain.filter(exchange);
    }

    /* 指定过滤器执行顺序，数值越小优先级越高 */
    @Override
    public int getOrder() {
        return 0;
    }
}

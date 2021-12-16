package cn.nihility.cloud.openfeign.service;

import cn.nihility.cloud.openfeign.exception.CircuitBreakerExceptionA;
import cn.nihility.cloud.openfeign.exception.CircuitBreakerExceptionB;
import cn.nihility.cloud.openfeign.util.CircuitBreakerUtil;
import cn.nihility.common.util.UuidUtil;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.concurrent.atomic.AtomicInteger;

@Service
public class RemoteCallService {

    private static final Logger logger = LoggerFactory.getLogger(RemoteCallService.class);

    private static AtomicInteger count = new AtomicInteger(0);

    private CircuitBreakerRegistry circuitBreakerRegistry;

    public RemoteCallService(CircuitBreakerRegistry circuitBreakerRegistry) {
        this.circuitBreakerRegistry = circuitBreakerRegistry;
    }

    private void call(String tag) {
        int num = count.getAndIncrement();
        logger.info("[{}] count = [{}]", tag, num);
        if (num % 4 == 1) {
            throw new CircuitBreakerExceptionA("异常A，不需要被记录");
        }
        if (num % 4 == 2 || num % 4 == 3) {
            throw new CircuitBreakerExceptionB("异常B，需要被记录");
        }
        logger.info("服务正常运行，执行业务逻辑");
    }

    public String echo(String msg) {
        call("echo");
        return msg;
    }

    public String uuidEcho() {
        call("uuidEcho");
        return UuidUtil.jdkUUID();
    }

    @CircuitBreaker(name = "backendA", fallbackMethod = "fallBack")
    public String randomEcho() {
        call("randomEcho");
        return UuidUtil.jdkUUID();
    }

    private String fallBack(Throwable throwable) {
        logger.info("方法被降级了~~ fallBack [{}]", throwable.getLocalizedMessage());
        CircuitBreakerUtil.getCircuitBreakerStatus("降级 fallBack 方法中:",
            circuitBreakerRegistry.circuitBreaker("backendA"));
        return "方法被降级了~~ fallBack";
    }

    private String fallBack(IllegalArgumentException e) {
        logger.info("熔断器已经打开，拒绝访问被保护方法~ fallBack");
        CircuitBreakerUtil.getCircuitBreakerStatus("熔断器 fallBack 打开中:",
            circuitBreakerRegistry.circuitBreaker("backendA"));
        return "熔断器已经打开，拒绝访问被保护方法~ fallBack";
    }

}

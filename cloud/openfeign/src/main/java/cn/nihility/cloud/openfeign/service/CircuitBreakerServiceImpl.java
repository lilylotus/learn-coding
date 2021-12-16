package cn.nihility.cloud.openfeign.service;

import cn.nihility.cloud.openfeign.util.CircuitBreakerUtil;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.vavr.CheckedFunction0;
import io.vavr.control.Try;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * 在 CircuitService 中先注入注册器，然后用注册器通过熔断器名称获取熔断器。
 * 如果不需要使用降级函数，可以直接调用熔断器的 executeSupplier 方法或 executeCheckedSupplier 方法
 * <p>
 * 同时也可以看出白名单所谓的忽略，是指不计入缓冲区中（即不算成功也不算失败），
 * 有降级方法会调用降级方法，没有降级方法会抛出异常，和其他异常无异。
 * <p>
 * 当环形缓冲区大小被填满时会计算失败率，这时请求会被拒绝获取不到 count 的值，且 notPermittedCalls 会增加。
 */
@Service
public class CircuitBreakerServiceImpl {

    private static final Logger logger = LoggerFactory.getLogger(CircuitBreakerServiceImpl.class);

    private CircuitBreakerRegistry circuitBreakerRegistry;
    private RemoteCallService remoteCallService;

    public CircuitBreakerServiceImpl(CircuitBreakerRegistry circuitBreakerRegistry,
                                     RemoteCallService remoteCallService) {
        this.circuitBreakerRegistry = circuitBreakerRegistry;
        this.remoteCallService = remoteCallService;
    }

    public String circuitBreakerNoAOP() throws Throwable {
        CircuitBreaker circuitBreaker = circuitBreakerRegistry.circuitBreaker("backendA");
        CircuitBreakerUtil.getCircuitBreakerStatus("执行开始前：", circuitBreaker);
        return circuitBreaker.executeCheckedSupplier(remoteCallService::uuidEcho);
    }

    public String circuitBreakerNoAOPWithDescend() {
        // 通过注册器获取熔断器的实例
        CircuitBreaker circuitBreaker = circuitBreakerRegistry.circuitBreaker("backendA");
        CircuitBreakerUtil.getCircuitBreakerStatus("执行开始前：", circuitBreaker);
        // 使用熔断器包装连接器的方法
        CheckedFunction0<String> checkedSupplier =
            CircuitBreaker.decorateCheckedSupplier(circuitBreaker, remoteCallService::uuidEcho);
        // 使用Try.of().recover()调用并进行降级处理
        Try<String> result = Try.of(checkedSupplier).
            recover(CallNotPermittedException.class, throwable -> {
                logger.info("熔断器已经打开，拒绝访问被保护方法~");
                CircuitBreakerUtil.getCircuitBreakerStatus("熔断器打开中:", circuitBreaker);
                return "熔断器已经打开，拒绝访问被保护方法~";
            })
            .recover(throwable -> {
                logger.info("方法被降级了~~ [{}]", throwable.getLocalizedMessage());
                CircuitBreakerUtil.getCircuitBreakerStatus("降级方法中:", circuitBreaker);
                return "方法被降级了~~";
            });
        CircuitBreakerUtil.getCircuitBreakerStatus("执行结束后：", circuitBreaker);
        return result.get();
    }

    public String circuitBreakerAOP() {
        CircuitBreakerUtil
            .getCircuitBreakerStatus("执行开始前：", circuitBreakerRegistry.circuitBreaker("backendA"));
        String result = remoteCallService.uuidEcho();
        CircuitBreakerUtil
            .getCircuitBreakerStatus("执行结束后：", circuitBreakerRegistry.circuitBreaker("backendA"));
        return result;
    }

}

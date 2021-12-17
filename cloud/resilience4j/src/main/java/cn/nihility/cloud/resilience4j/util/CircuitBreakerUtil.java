package cn.nihility.cloud.resilience4j.util;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CircuitBreakerUtil {

    private static final Logger log = LoggerFactory.getLogger(CircuitBreakerUtil.class);

    private CircuitBreakerUtil() {
    }

    public static void threadSleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
        }
    }

    /**
     * 获取熔断器的状态
     */
    public static void getCircuitBreakerStatus(String time, CircuitBreaker circuitBreaker) {
        CircuitBreaker.Metrics metrics = circuitBreaker.getMetrics();
        // Returns the failure rate in percentage.
        float failureRate = metrics.getFailureRate();
        // Returns the current number of buffered calls.
        int bufferedCalls = metrics.getNumberOfBufferedCalls();
        // Returns the current number of failed calls.
        int failedCalls = metrics.getNumberOfFailedCalls();
        // Returns the current number of successed calls.
        int successCalls = metrics.getNumberOfSuccessfulCalls();
        // Returns the max number of buffered calls.
        int maxBufferCalls = metrics.getNumberOfBufferedCalls();
        // Returns the current number of not permitted calls.
        long notPermittedCalls = metrics.getNumberOfNotPermittedCalls();

        log.info("time [{}] state=[{}], metrics[ failureRate=[{}], bufferedCalls=[{}], " +
                "failedCalls=[{}], successCalls=[{}], maxBufferCalls=[{}], notPermittedCalls=[{}]]",
            time, circuitBreaker.getState(), failureRate, bufferedCalls,
            failedCalls, successCalls, maxBufferCalls, notPermittedCalls);
    }

    /**
     * 监听熔断器事件
     */
    public static void addCircuitBreakerListener(CircuitBreaker circuitBreaker) {
        circuitBreaker.getEventPublisher()
            .onSuccess(event -> log.info("服务调用成功：[{}]", event))
            .onError(event -> log.info("服务调用失败：[{}]", event))
            .onIgnoredError(event -> log.info("服务调用失败，但异常被忽略：[{}]", event))
            .onReset(event -> log.info("熔断器重置：[{}]", event))
            .onStateTransition(event -> log.info("熔断器状态改变：[{}]", event))
            .onCallNotPermitted(event -> log.info(" 熔断器已经打开：[{}]", event));
    }

}

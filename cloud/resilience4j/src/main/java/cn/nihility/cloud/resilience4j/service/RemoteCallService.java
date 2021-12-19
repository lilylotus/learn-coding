package cn.nihility.cloud.resilience4j.service;

import cn.nihility.cloud.resilience4j.constant.Constant;
import cn.nihility.cloud.resilience4j.exception.CircuitBreakerExceptionA;
import cn.nihility.cloud.resilience4j.exception.CircuitBreakerExceptionB;
import cn.nihility.cloud.resilience4j.util.BulkhdadUtil;
import cn.nihility.cloud.resilience4j.util.CircuitBreakerUtil;
import cn.nihility.cloud.resilience4j.util.RetryUtil;
import cn.nihility.common.util.UuidUtil;
import io.github.resilience4j.bulkhead.BulkheadFullException;
import io.github.resilience4j.bulkhead.BulkheadRegistry;
import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.retry.RetryRegistry;
import io.github.resilience4j.retry.annotation.Retry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.concurrent.atomic.AtomicInteger;

@Service
public class RemoteCallService {

    private static final Logger logger = LoggerFactory.getLogger(RemoteCallService.class);

    private static AtomicInteger count = new AtomicInteger(0);

    private CircuitBreakerRegistry circuitBreakerRegistry;
    private RetryRegistry retryRegistry;
    private BulkheadRegistry bulkheadRegistry;

    public RemoteCallService(CircuitBreakerRegistry circuitBreakerRegistry,
                             RetryRegistry retryRegistry,
                             BulkheadRegistry bulkheadRegistry) {
        this.circuitBreakerRegistry = circuitBreakerRegistry;
        this.retryRegistry = retryRegistry;
        this.bulkheadRegistry = bulkheadRegistry;
    }

    private void call(int num, String tag) {
        logger.info("[{}] count = [{}]", tag, num);
        if (num % 4 == 0) {
            throw new CircuitBreakerExceptionA("异常A，不需要被记录 [" + num + "]");
        }
        if (num % 4 == 1) {
            throw new CircuitBreakerExceptionB("异常B，需要被记录 [" + num + "]");
        }
        logger.info("[{}] 服务正常运行，执行业务逻辑", num);
        CircuitBreakerUtil.threadSleep(200L);
    }

    private void call(String tag) {
        call(count.getAndIncrement(), tag);
    }

    public ResponseEntity<String> echo(String msg) {
        call("echo");
        return ResponseEntity.ok(msg);
    }

    public ResponseEntity<String> uuidEcho() {
        call("uuidEcho");
        return ResponseEntity.ok(UuidUtil.jdkUUID());
    }

    public ResponseEntity<String> uuidEchoWithId(int num) {
        call(num, "uuidEchoWithId");
        return ResponseEntity.ok(UuidUtil.jdkUUID());
    }

    public ResponseEntity<String> uuidEchoTimeLimiter(int num) {
        call(num, "uuidEchoTimeLimiter");
        if (num % 4 == 3) {
            CircuitBreakerUtil.threadSleep(5000L);
        }
        return ResponseEntity.ok(UuidUtil.jdkUUID());
    }

    public ResponseEntity<String> uuidEchoRetry(int num) {
        int index = count.getAndIncrement();
        call(index, "uuidEchoRetry [" + num + "]");
        return index % 4 == 3 ? null : ResponseEntity.ok(UuidUtil.jdkUUID());
    }

    public ResponseEntity<String> uuidBulkhead(int num) {
        //call(num, "uuidBulkhead");
        CircuitBreakerUtil.threadSleep(500L);
        return ResponseEntity.ok(UuidUtil.jdkUUID());
    }

    public ResponseEntity<String> uuidRateLimiter(int num) {
        //call(num, "uuidBulkhead");
        CircuitBreakerUtil.threadSleep(500L);
        return ResponseEntity.ok(UuidUtil.jdkUUID() + ":" + num);
    }

    @CircuitBreaker(name = Constant.CIRCUIT_BREAKER_A, fallbackMethod = "fallBack")
    public ResponseEntity<String> echoWithCircuitBreaker() {
        call("echoWithCircuitBreaker");
        return ResponseEntity.ok(UuidUtil.jdkUUID());
    }

    @CircuitBreaker(name = Constant.CIRCUIT_BREAKER_A, fallbackMethod = "fallBack")
    @Retry(name = Constant.RETRY_B, fallbackMethod = "retryFallBack")
    public ResponseEntity<String> echoWithCircuitBreakerAndRetry(int num) {
        int index = count.getAndIncrement();
        call(index, "echoWithCircuitBreakerAndRetry [" + num + "]");
        return index % 4 == 3 ? null : ResponseEntity.ok(UuidUtil.jdkUUID());
    }

    /**
     * NOTE : ThreadPool bulkhead is only applicable for completable futures
     * 注意： ThreadPool 线程池模式，仅支持返回异步返回值的方法 Future
     * <p>
     * Retry、CircuitBreaker、Bulkhead 同时注解在方法上
     * 默认的顺序是 Retry>CircuitBreaker>Bulkhead
     * 即先控制并发再熔断最后重试，之后直接调用方法
     */
    @Bulkhead(name = Constant.BULKHEAD_B, fallbackMethod = "bulkheadFallBack", type = Bulkhead.Type.SEMAPHORE)
    public ResponseEntity<String> bulkheadAop(int num) {
        //int index = count.getAndIncrement();
        //call(index, "bulkheadAop [" + num + "]");
        CircuitBreakerUtil.threadSleep(1000L);
        return ResponseEntity.ok(UuidUtil.jdkUUID() + ":" + num);
    }

    /**
     * 如果 Retry、CircuitBreaker、Bulkhead、RateLimiter 同时注解在方法上
     * 默认的顺序是 Retry>CircuitBreaker>RateLimiter>Bulkhead
     * 即先控制并发再限流然后熔断最后重试
     */
    @RateLimiter(name = Constant.RATELIMITER_B, fallbackMethod = "rateLimiterFallBack")
    public ResponseEntity<String> rateLimiterAop(int num) {
        //int index = count.getAndIncrement();
        //call(index, "bulkheadAop [" + num + "]");
        //CircuitBreakerUtil.threadSleep(1000L);
        return ResponseEntity.ok(UuidUtil.jdkUUID() + ":" + num);
    }

    private ResponseEntity<String> bulkheadFallBack(BulkheadFullException throwable) {
        logger.error("方法被降级了~~ bulkheadFallBack [{}]", throwable.getLocalizedMessage());
        BulkhdadUtil.getBulkheadStatus("降级 bulkheadFallBack 方法中:", bulkheadRegistry.bulkhead(Constant.BULKHEAD_B));
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("方法被降级了~~ bulkheadFallBack");
    }

    private ResponseEntity<String> rateLimiterFallBack(Throwable throwable) {
        logger.error("方法被降级了~~ rateLimiterFallBack [{}]", throwable.getLocalizedMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("方法被降级了~~ rateLimiterFallBack");
    }

    private ResponseEntity<String> bulkheadFallBack(Throwable throwable) {
        logger.error("方法被降级了~~ bulkheadFallBack [{}]", throwable.getLocalizedMessage());
        BulkhdadUtil.getBulkheadStatus("降级 bulkheadFallBack 方法中:", bulkheadRegistry.bulkhead(Constant.BULKHEAD_B));
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("方法被降级了~~ bulkheadFallBack");
    }

    private ResponseEntity<String> retryFallBack(Throwable throwable) {
        logger.error("方法被降级了~~ retryFallBack [{}]", throwable.getLocalizedMessage());
        RetryUtil.getRetryStatus("降级 retryFallBack 方法中:",
            retryRegistry.retry(Constant.RETRY_B));
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body("方法被降级了~~ retryFallBack");
    }

    private ResponseEntity<String> fallBack(Throwable throwable) {
        logger.error("方法被降级了~~ fallBack [{}]", throwable.getLocalizedMessage());
        CircuitBreakerUtil.getCircuitBreakerStatus("降级 fallBack 方法中:",
            circuitBreakerRegistry.circuitBreaker(Constant.CIRCUIT_BREAKER_A));
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body("方法被降级了~~ fallBack");
    }

    private ResponseEntity<String> fallBack(CallNotPermittedException e) {
        logger.error("熔断器已经打开，拒绝访问被保护方法~ fallBack, [{}]", e.getMessage());
        CircuitBreakerUtil.getCircuitBreakerStatus("熔断器 fallBack 打开中:",
            circuitBreakerRegistry.circuitBreaker(Constant.CIRCUIT_BREAKER_A));
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body("熔断器已经打开，拒绝访问被保护方法~ fallBack");
    }

}

package cn.nihility.cloud.resilience4j.service;

import cn.nihility.cloud.resilience4j.constant.Constant;
import cn.nihility.cloud.resilience4j.util.BulkhdadUtil;
import cn.nihility.cloud.resilience4j.util.CircuitBreakerUtil;
import cn.nihility.cloud.resilience4j.util.RateLimiterUtil;
import cn.nihility.cloud.resilience4j.util.RetryUtil;
import io.github.resilience4j.bulkhead.Bulkhead;
import io.github.resilience4j.bulkhead.BulkheadFullException;
import io.github.resilience4j.bulkhead.BulkheadRegistry;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.ratelimiter.RateLimiter;
import io.github.resilience4j.ratelimiter.RateLimiterRegistry;
import io.github.resilience4j.ratelimiter.RequestNotPermitted;
import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryRegistry;
import io.github.resilience4j.timelimiter.TimeLimiter;
import io.github.resilience4j.timelimiter.TimeLimiterRegistry;
import io.vavr.CheckedFunction0;
import io.vavr.CheckedFunction1;
import io.vavr.control.Try;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

/**
 * {@code @CircuitBreaker} 和 {@code @Retry} 需要 spring-boot-starter-aop 支持
 * <p>
 * 在 CircuitService 中先注入注册器，然后用注册器通过熔断器名称获取熔断器。
 * 如果不需要使用降级函数，可以直接调用熔断器的 executeSupplier 方法或 executeCheckedSupplier 方法
 * <p>
 * 同时也可以看出白名单所谓的忽略，是指不计入缓冲区中（即不算成功也不算失败），
 * 有降级方法会调用降级方法，没有降级方法会抛出异常，和其他异常无异。
 * {@link io.github.resilience4j.circuitbreaker.internal.CircuitBreakerStateMachine#handleThrowable(long, java.util.concurrent.TimeUnit, java.lang.Throwable)}
 * 来处理一次的黑白名单
 * <p>
 * 当环形缓冲区大小被填满时会计算失败率，这时请求会被拒绝获取不到 count 的值，且 notPermittedCalls 会增加。
 */
@Service
public class CircuitBreakerServiceImpl {

    private static final Logger logger = LoggerFactory.getLogger(CircuitBreakerServiceImpl.class);

    private static AtomicInteger count = new AtomicInteger(1);
    private static AtomicInteger timeLimiterCount = new AtomicInteger(1);
    private static AtomicInteger retryCount = new AtomicInteger(1);
    private static AtomicInteger bulkheadCount = new AtomicInteger(1);
    private static AtomicInteger rateLimiterCount = new AtomicInteger(1);
    private static ExecutorService executorService = Executors.newFixedThreadPool(10);

    private CircuitBreakerRegistry circuitBreakerRegistry;
    private TimeLimiterRegistry timeLimiterRegistry;
    private RetryRegistry retryRegistry;
    private BulkheadRegistry bulkheadRegistry;
    private RateLimiterRegistry rateLimiterRegistry;
    private RemoteCallService remoteCallService;

    public CircuitBreakerServiceImpl(CircuitBreakerRegistry circuitBreakerRegistry,
                                     TimeLimiterRegistry timeLimiterRegistry,
                                     RetryRegistry retryRegistry,
                                     BulkheadRegistry bulkheadRegistry,
                                     RateLimiterRegistry rateLimiterRegistry,
                                     RemoteCallService remoteCallService) {
        this.circuitBreakerRegistry = circuitBreakerRegistry;
        this.timeLimiterRegistry = timeLimiterRegistry;
        this.retryRegistry = retryRegistry;
        this.bulkheadRegistry = bulkheadRegistry;
        this.rateLimiterRegistry = rateLimiterRegistry;
        this.remoteCallService = remoteCallService;
    }

    public ResponseEntity<String> circuitBreakerNoAOP() throws Throwable {
        CircuitBreaker circuitBreaker = circuitBreakerRegistry.circuitBreaker(Constant.CIRCUIT_BREAKER_A);
        CircuitBreakerUtil.getCircuitBreakerStatus("执行开始前：", circuitBreaker);
        return circuitBreaker.executeCheckedSupplier(remoteCallService::uuidEcho);
    }

    public ResponseEntity<String> circuitBreakerNoAOPWithDescend() {
        final int currentIndex = count.getAndIncrement();

        // 通过注册器获取熔断器的实例
        CircuitBreaker circuitBreaker = circuitBreakerRegistry.circuitBreaker(Constant.CIRCUIT_BREAKER_A);
        CircuitBreakerUtil.getCircuitBreakerStatus(currentIndex + ":执行开始前：", circuitBreaker);
        // 使用熔断器包装连接器的方法
        CheckedFunction1<Integer, ResponseEntity<String>> checkedFunction =
            CircuitBreaker.decorateCheckedFunction(circuitBreaker, remoteCallService::uuidEchoWithId);
        // 使用Try.of().recover()调用并进行降级处理
        ResponseEntity<String> result;
        Try<ResponseEntity<String>> tryResult = Try.of((CheckedFunction0<ResponseEntity<String>>) () -> checkedFunction.apply(currentIndex))
            .recover(CallNotPermittedException.class, throwable -> {
                logger.error("[{}]熔断器已经打开，拒绝访问被保护方法~ [{}]", currentIndex, throwable.getLocalizedMessage());
                CircuitBreakerUtil.getCircuitBreakerStatus(currentIndex + ":熔断器打开中:", circuitBreaker);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(currentIndex + ":熔断器已经打开，拒绝访问被保护方法~");
            })
            .recover(throwable -> {
                logger.error("[{}]方法被降级了~~ [{}]", currentIndex, throwable.getLocalizedMessage());
                CircuitBreakerUtil.getCircuitBreakerStatus(currentIndex + ":降级方法中:", circuitBreaker);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(currentIndex + ":方法被降级了~~");
            });
        result = tryResult.get();

        /*try {
            result = checkedFunction.apply(currentIndex);
        } catch (CallNotPermittedException ex) {
            logger.info("[{}] 熔断器已经打开，拒绝访问被保护方法~ [{}]", currentIndex, ex.getLocalizedMessage());
            CircuitBreakerUtil.getCircuitBreakerStatus(currentIndex + " :熔断器打开中:", circuitBreaker);
            result = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(currentIndex + ": 熔断器已经打开，拒绝访问被保护方法~");
        } catch (Throwable throwable) {
            logger.info("[{}] 方法被降级了~~ [{}]", currentIndex, throwable.getLocalizedMessage());
            CircuitBreakerUtil.getCircuitBreakerStatus(currentIndex + ":降级方法中:", circuitBreaker);
            result = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(currentIndex + ":方法被降级了~~");
        }*/


        /*CheckedFunction0<ResponseEntity<String>> checkedSupplier =
            CircuitBreaker.decorateCheckedSupplier(circuitBreaker, remoteCallService::uuidEcho);
        Try<ResponseEntity<String>> tryResult = Try.of(checkedSupplier).
            recover(CallNotPermittedException.class, throwable -> {
                logger.info("熔断器已经打开，拒绝访问被保护方法~ [{}]", throwable.getLocalizedMessage());
                CircuitBreakerUtil.getCircuitBreakerStatus("熔断器打开中:", circuitBreaker);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("熔断器已经打开，拒绝访问被保护方法~");
            })
            .recover(throwable -> {
                logger.info("方法被降级了~~ [{}]", throwable.getLocalizedMessage());
                CircuitBreakerUtil.getCircuitBreakerStatus("降级方法中:", circuitBreaker);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("方法被降级了~~");
            });
          result = tryResult.get();
         */

        CircuitBreakerUtil.getCircuitBreakerStatus(currentIndex + ":执行结束后：", circuitBreaker);
        return result;
    }

    public ResponseEntity<String> circuitBreakerWithTimeLimiter() {
        final int currentIndex = timeLimiterCount.getAndIncrement();

        CircuitBreaker circuitBreaker = circuitBreakerRegistry.circuitBreaker(Constant.CIRCUIT_BREAKER_A);
        CircuitBreakerUtil.getCircuitBreakerStatus(currentIndex + ":执行开始前：", circuitBreaker);

        Supplier<Future<ResponseEntity<String>>> supplier = () -> executorService.submit(() -> remoteCallService.uuidEchoTimeLimiter(currentIndex));
        TimeLimiter timeLimiter = timeLimiterRegistry.timeLimiter(Constant.TIME_LIMITER_A);
        Callable<ResponseEntity<String>> timelimiterDecorate = timeLimiter.decorateFutureSupplier(supplier);
        Callable<ResponseEntity<String>> circuitBreakerDecorate = CircuitBreaker.decorateCallable(circuitBreaker, timelimiterDecorate);

        ResponseEntity<String> result;
        Try<ResponseEntity<String>> tryResult = Try.of(circuitBreakerDecorate::call)
            .recover(CallNotPermittedException.class, throwable -> {
                logger.error("[{}]熔断器已经打开，拒绝访问被保护方法~ [{}]", currentIndex, throwable.getLocalizedMessage());
                CircuitBreakerUtil.getCircuitBreakerStatus(currentIndex + ":熔断器打开中:", circuitBreaker);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(currentIndex + ":熔断器已经打开，拒绝访问被保护方法~");
            })
            .recover(TimeoutException.class, throwable -> {
                logger.error("[{}]请求超时，方法被降级了~ [{}]", currentIndex, throwable.getLocalizedMessage());
                CircuitBreakerUtil.getCircuitBreakerStatus(currentIndex + ":请求超时，熔断器打开中:", circuitBreaker);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(currentIndex + ":请求超时，方法被降级了~");
            })
            .recover(throwable -> {
                logger.error("[{}]方法被降级了~~ [{}]", currentIndex, throwable.getLocalizedMessage());
                CircuitBreakerUtil.getCircuitBreakerStatus(currentIndex + ":降级方法中:", circuitBreaker);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(currentIndex + ":方法被降级了~~");
            });
        result = tryResult.get();

        CircuitBreakerUtil.getCircuitBreakerStatus(currentIndex + ":执行结束后：", circuitBreaker);
        return result;
    }

    public ResponseEntity<String> circuitBreakerWithRetry() {
        final int currentIndex = retryCount.getAndIncrement();

        CircuitBreaker circuitBreaker = circuitBreakerRegistry.circuitBreaker(Constant.CIRCUIT_BREAKER_A);
        Retry retry = retryRegistry.retry(Constant.RETRY_A);
        CircuitBreakerUtil.getCircuitBreakerStatus(currentIndex + ":执行开始前：", circuitBreaker);

        CheckedFunction1<Integer, ResponseEntity<String>> retryDecorate = Retry.decorateCheckedFunction(retry,
            new CheckedFunction1<Integer, ResponseEntity<String>>() {
                @Override
                public ResponseEntity<String> apply(Integer integer) throws Throwable {
                    return remoteCallService.uuidEchoRetry(currentIndex);
                }
            });
        CheckedFunction1<Integer, ResponseEntity<String>> circuitBreakerDecorate =
            CircuitBreaker.decorateCheckedFunction(circuitBreaker, retryDecorate);

        ResponseEntity<String> result;
        Try<ResponseEntity<String>> tryResult = Try.of(new CheckedFunction0<ResponseEntity<String>>() {
            @Override
            public ResponseEntity<String> apply() throws Throwable {
                return circuitBreakerDecorate.apply(currentIndex);
            }
        }).recover(CallNotPermittedException.class, throwable -> {
            logger.error("[{}]熔断器已经打开，拒绝访问被保护方法~ [{}]", currentIndex, throwable.getLocalizedMessage());
            CircuitBreakerUtil.getCircuitBreakerStatus(currentIndex + ":熔断器打开中:", circuitBreaker);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(currentIndex + ":熔断器已经打开，拒绝访问被保护方法~");
        }).recover(TimeoutException.class, throwable -> {
            logger.error("[{}]请求超时，方法被降级了~ [{}]", currentIndex, throwable.getLocalizedMessage());
            CircuitBreakerUtil.getCircuitBreakerStatus(currentIndex + ":请求超时，熔断器打开中:", circuitBreaker);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(currentIndex + ":请求超时，方法被降级了~");
        }).recover(throwable -> {
            logger.error("[{}]方法被降级了~~ [{}]", currentIndex, throwable.getLocalizedMessage());
            CircuitBreakerUtil.getCircuitBreakerStatus(currentIndex + ":降级方法中:", circuitBreaker);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(currentIndex + ":方法被降级了~~");
        });
        result = tryResult.get();

        RetryUtil.getRetryStatus(currentIndex + ":执行结束: ", retry);
        CircuitBreakerUtil.getCircuitBreakerStatus(currentIndex + ":执行结束后：", circuitBreaker);

        return result;
    }

    public ResponseEntity<String> bulkhead() {
        final int currentIndex = bulkheadCount.getAndIncrement();

        Bulkhead bulkhead = bulkheadRegistry.bulkhead(Constant.BULKHEAD_A);
        BulkhdadUtil.getBulkheadStatus(currentIndex + ":执行开始前：", bulkhead);

        CheckedFunction1<Integer, ResponseEntity<String>> bulkheadFunc = Bulkhead.decorateCheckedFunction(bulkhead,
            new CheckedFunction1<Integer, ResponseEntity<String>>() {
                @Override
                public ResponseEntity<String> apply(Integer integer) throws Throwable {
                    return remoteCallService.uuidBulkhead(currentIndex);
                }
            });

        Try<ResponseEntity<String>> tryResult = Try.of(new CheckedFunction0<ResponseEntity<String>>() {
            @Override
            public ResponseEntity<String> apply() throws Throwable {
                return bulkheadFunc.apply(currentIndex);
            }
        }).recover(BulkheadFullException.class, throwable -> {
            logger.error("[{}]服务调用失败 BulkheadFullException，返回垫底数据 [{}]", currentIndex, throwable.getLocalizedMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(currentIndex + ":服务调用失败 BulkheadFullException，返回垫底数据");
        }).recover(throwable -> {
            logger.error("[{}]服务调用失败 throwable，返回垫底数据 [{}]", currentIndex, throwable.getLocalizedMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(currentIndex + ":服务调用失败 throwable，返回垫底数据");
        });

        BulkhdadUtil.getBulkheadStatus(currentIndex + ":执行结束：", bulkhead);

        return tryResult.get();
    }

    public ResponseEntity<String> rateLimiter() {
        final int currentIndex = rateLimiterCount.getAndIncrement();

        RateLimiter rateLimiter = rateLimiterRegistry.rateLimiter(Constant.RATELIMITER_A);
        RateLimiterUtil.getRateLimiterStatus(currentIndex + ":执行开始前：", rateLimiter);

        CheckedFunction1<Integer, ResponseEntity<String>> limiterFunc = RateLimiter.decorateCheckedFunction(rateLimiter,
            new CheckedFunction1<Integer, ResponseEntity<String>>() {
                @Override
                public ResponseEntity<String> apply(Integer integer) throws Throwable {
                    return remoteCallService.uuidRateLimiter(currentIndex);
                }
            });

        Try<ResponseEntity<String>> tryResult = Try.of(new CheckedFunction0<ResponseEntity<String>>() {
            @Override
            public ResponseEntity<String> apply() throws Throwable {
                return limiterFunc.apply(currentIndex);
            }
        }).recover(RequestNotPermitted.class, throwable -> {
            logger.error("[{}]服务调用失败 RequestNotPermitted，返回垫底数据 [{}]", currentIndex, throwable.getLocalizedMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(currentIndex + ":服务调用失败 RequestNotPermitted，返回垫底数据");
        }).recover(throwable -> {
            logger.error("[{}]服务调用失败 throwable，返回垫底数据 [{}]", currentIndex, throwable.getLocalizedMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(currentIndex + ":服务调用失败 throwable，返回垫底数据");
        });

        RateLimiterUtil.getRateLimiterStatus(currentIndex + ":执行结束：", rateLimiter);

        return tryResult.get();
    }

    public ResponseEntity<String> circuitBreakerAOP() {
        CircuitBreakerUtil.getCircuitBreakerStatus("执行开始前：",
            circuitBreakerRegistry.circuitBreaker(Constant.CIRCUIT_BREAKER_A));
        ResponseEntity<String> result = remoteCallService.echoWithCircuitBreaker();
        CircuitBreakerUtil.getCircuitBreakerStatus("执行结束后：",
            circuitBreakerRegistry.circuitBreaker(Constant.CIRCUIT_BREAKER_A));
        return result;
    }

    public ResponseEntity<String> circuitBreakerWithRetryAOP() {
        final int index = retryCount.getAndIncrement();
        CircuitBreakerUtil.getCircuitBreakerStatus(index + ":执行开始前：",
            circuitBreakerRegistry.circuitBreaker(Constant.CIRCUIT_BREAKER_A));
        ResponseEntity<String> result = remoteCallService.echoWithCircuitBreakerAndRetry(index);
        RetryUtil.getRetryStatus(index + ":执行结束后：", retryRegistry.retry(Constant.RETRY_B));
        CircuitBreakerUtil.getCircuitBreakerStatus(index + ":执行结束后：",
            circuitBreakerRegistry.circuitBreaker(Constant.CIRCUIT_BREAKER_A));
        return result;
    }

    public ResponseEntity<String> bulkheadAop() {
        final int index = bulkheadCount.getAndIncrement();
        Bulkhead bulkhead = bulkheadRegistry.bulkhead(Constant.BULKHEAD_B);
        ResponseEntity<String> result = remoteCallService.bulkheadAop(index);
        BulkhdadUtil.getBulkheadStatus(index + ":执行结束：", bulkhead);
        return result;
    }

    public ResponseEntity<String> rateLimiterAop() {
        final int index = rateLimiterCount.getAndIncrement();
        RateLimiter rateLimiter = rateLimiterRegistry.rateLimiter(Constant.RATELIMITER_B);
        ResponseEntity<String> result = remoteCallService.rateLimiterAop(index);
        RateLimiterUtil.getRateLimiterStatus(index + ":执行结束：", rateLimiter);
        return result;
    }

}

package cn.nihility.cloud.resilience4j.util;

import io.github.resilience4j.ratelimiter.RateLimiter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RateLimiterUtil {

    private static final Logger log = LoggerFactory.getLogger(RateLimiterUtil.class);

    private RateLimiterUtil() {
    }


    /**
     * 获取rateLimiter的状态
     */
    public static void getRateLimiterStatus(String time, RateLimiter rateLimiter) {
        RateLimiter.Metrics metrics = rateLimiter.getMetrics();
        // Returns the number of availablePermissions in this duration.
        int availablePermissions = metrics.getAvailablePermissions();
        // Returns the number of WaitingThreads
        int numberOfWaitingThreads = metrics.getNumberOfWaitingThreads();

        log.info("[{}]，metrics[ availablePermissions=[{}], numberOfWaitingThreads=[{}]]",
            time, availablePermissions, numberOfWaitingThreads);
    }

    /**
     * 监听rateLimiter事件
     */
    public static void addRateLimiterListener(RateLimiter rateLimiter) {
        rateLimiter.getEventPublisher()
            .onSuccess(event -> log.info("onSuccess [{}]", event))
            .onFailure(event -> log.info("onFailure [{}]", event));
    }

}

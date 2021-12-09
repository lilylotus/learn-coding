package cn.nihility.cloud.openfeign.service;

import io.github.resilience4j.bulkhead.ThreadPoolBulkhead.Metrics;
import io.github.resilience4j.bulkhead.ThreadPoolBulkheadRegistry;
import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
public class Resilience4jThreadPoolBulkheadService {

    private static final Logger logger = LoggerFactory.getLogger(Resilience4jThreadPoolBulkheadService.class);

    private ThreadPoolBulkheadRegistry threadPoolBulkheadRegistry;

    public Resilience4jThreadPoolBulkheadService(ThreadPoolBulkheadRegistry threadPoolBulkheadRegistry) {
        this.threadPoolBulkheadRegistry = threadPoolBulkheadRegistry;
    }

    /**
     * FixedThreadPoolBulkhead 只对 CompletableFuture 方法有效，所以我们必创建返回 CompletableFuture
     */
    @Bulkhead(name = "DefaultThreadPoolBulkhead",
        fallbackMethod = "defaultThreadPoolBulkheadFallback",
        type = Bulkhead.Type.THREADPOOL)
    public CompletableFuture<String> defaultThreadPoolBulkhead() {
        Metrics metrics = threadPoolBulkheadRegistry.bulkhead("DefaultThreadPoolBulkhead").getMetrics();
        logger.info("now defaultThreadPoolBulkhead enter the method!!!,{}", metrics.getRemainingQueueCapacity());
        try {
            Thread.sleep(1000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return CompletableFuture.supplyAsync(() -> "[DefaultThreadPoolBulkhead]");
    }

    @Bulkhead(name = "SpecificThreadPoolBulkhead",
        fallbackMethod = "specificConfigThreadPoolBulkheadFallback",
        type = Bulkhead.Type.THREADPOOL)
    public CompletableFuture<String> specificThreadPoolBulkhead() {
        Metrics metrics = threadPoolBulkheadRegistry.bulkhead("SpecificThreadPoolBulkhead").getMetrics();
        logger.info("now specificThreadPoolBulkhead enter the method!!!,{}", metrics.getRemainingQueueCapacity());
        try {
            Thread.sleep(1000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return CompletableFuture.supplyAsync(() -> "[SpecificThreadPoolBulkhead]");
    }

    @Bulkhead(name = "SpecificConfigThreadPoolBulkhead",
        fallbackMethod = "specificThreadPoolBulkheadFallback",
        type = Bulkhead.Type.THREADPOOL)
    public CompletableFuture<String> specificConfigThreadPoolBulkhead() {
        Metrics metrics = threadPoolBulkheadRegistry.bulkhead("SpecificConfigThreadPoolBulkhead").getMetrics();
        logger.info("now specificConfigThreadPoolBulkhead enter the method!!!,{}", metrics.getRemainingQueueCapacity());
        try {
            Thread.sleep(1000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return CompletableFuture.supplyAsync(() -> "[SpecificConfigThreadPoolBulkhead]");
    }

    public CompletableFuture<String> defaultThreadPoolBulkheadFallback(Exception ex) {
        logger.error("Invoke defaultThreadPoolBulkheadFallback Method", ex);
        return CompletableFuture.supplyAsync(() -> "defaultThreadPoolBulkheadFallback [" + ex.getMessage() + "]");
    }

    public CompletableFuture<String> specificThreadPoolBulkheadFallback(Exception ex) {
        logger.error("Invoke specificThreadPoolBulkheadFallback Method", ex);
        return CompletableFuture.supplyAsync(() -> "specificThreadPoolBulkheadFallback - [" + ex.getMessage() + "]");
    }

    public CompletableFuture<String> specificConfigThreadPoolBulkheadFallback(Exception ex) {
        logger.error("Invoke specificConfigThreadPoolBulkheadFallback Method", ex);
        return CompletableFuture.supplyAsync(() -> "specificConfigThreadPoolBulkheadFallback [" + ex.getMessage() + "]");
    }

}

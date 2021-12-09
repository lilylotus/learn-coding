package cn.nihility.cloud.openfeign.service;

import io.github.resilience4j.bulkhead.Bulkhead.Metrics;
import io.github.resilience4j.bulkhead.BulkheadRegistry;
import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class Resilience4jBulkheadService {

    private static final Logger logger = LoggerFactory.getLogger(Resilience4jBulkheadService.class);

    private BulkheadRegistry bulkheadRegistry;

    public Resilience4jBulkheadService(BulkheadRegistry bulkheadRegistry) {
        this.bulkheadRegistry = bulkheadRegistry;
    }

    @Bulkhead(name = "DefaultBulkhead", fallbackMethod = "defaultBulkheadFallback")
    public String defaultBulkhead() {
        Metrics metrics = bulkheadRegistry.bulkhead("DefaultBulkhead").getMetrics();
        logger.info("now defaultBulkhead enter the method!!!,{}<<<<<<{}",
            metrics.getAvailableConcurrentCalls(), metrics.getMaxAllowedConcurrentCalls());
        try {
            Thread.sleep(1000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        logger.info("now defaultBulkhead exist the method!!!");
        return "[defaultBulkhead]";
    }

    @Bulkhead(name = "SpecificBulkhead", fallbackMethod = "specificBulkheadFallback")
    public String specificBulkhead(String id) {
        Metrics metrics = bulkheadRegistry.bulkhead("SpecificBulkhead").getMetrics();
        logger.info("now specificBulkhead [{}] enter the method!!!,{} <<<<<< {}",
            id, metrics.getAvailableConcurrentCalls(), metrics.getMaxAllowedConcurrentCalls());
        try {
            Thread.sleep(1000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        logger.info("now specificBulkhead [{}] exist the method!!!", id);
        return "[specificBulkhead]";
    }

    @Bulkhead(name = "SpecificConfigBulkhead", fallbackMethod = "specificConfigBulkheadFallback")
    public String specificConfigBulkhead() {
        Metrics metrics = bulkheadRegistry.bulkhead("SpecificConfigBulkhead").getMetrics();
        logger.info("now specificConfigBulkhead enter the method!!!,{} <<<<<< {}",
            metrics.getAvailableConcurrentCalls(), metrics.getMaxAllowedConcurrentCalls());
        try {
            Thread.sleep(1000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        logger.info("now specificConfigBulkhead exist the method!!!");
        return "[specificConfigBulkhead]";
    }

    public String defaultBulkheadFallback(Exception ex) {
        logger.error("Invoke defaultBulkheadFallback Method", ex);
        return "defaultBulkheadFallback [" + ex.getMessage() + "]";
    }

    public String specificBulkheadFallback(String id, Exception ex) {
        logger.error("Invoke specificBulkheadFallback [{}] Method", id, ex);
        return "specificBulkheadFallback [" + id + "] - [" + ex.getMessage() + "]";
    }

    public String specificConfigBulkheadFallback(Exception ex) {
        logger.error("Invoke specificConfigBulkheadFallback Method", ex);
        return "specificConfigBulkheadFallback [" + ex.getMessage() + "]";
    }

}

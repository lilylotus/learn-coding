package cn.nihility.cloud.resilience4j.util;

import io.github.resilience4j.bulkhead.Bulkhead;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BulkhdadUtil {

    private static final Logger log = LoggerFactory.getLogger(BulkhdadUtil.class);

    private BulkhdadUtil() {
    }

    /**
     * 获取 bulkhead 的状态
     */
    public static void getBulkheadStatus(String time, Bulkhead bulkhead) {
        Bulkhead.Metrics metrics = bulkhead.getMetrics();
        // Returns the number of parallel executions this bulkhead can support at this point in time.
        int availableConcurrentCalls = metrics.getAvailableConcurrentCalls();
        // Returns the configured max amount of concurrent calls
        int maxAllowedConcurrentCalls = metrics.getMaxAllowedConcurrentCalls();

        log.info("[{}], metrics[availableConcurrentCalls=[{}], maxAllowedConcurrentCalls=[{}]]",
            time, availableConcurrentCalls, maxAllowedConcurrentCalls);
    }

    /**
     * 监听 bulkhead 事件
     */
    public static void addBulkheadListener(Bulkhead bulkhead) {
        bulkhead.getEventPublisher()
            .onCallFinished(event -> log.info("CallFinished [{}]", event))
            .onCallPermitted(event -> log.info("CallPermitted [{}]", event))
            .onCallRejected(event -> log.info("CallRejected [{}]", event));
    }

}

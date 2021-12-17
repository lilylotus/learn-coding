package cn.nihility.cloud.resilience4j.util;

import io.github.resilience4j.retry.Retry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RetryUtil {

    private static final Logger log = LoggerFactory.getLogger(RetryUtil.class);

    private RetryUtil() {
    }

    /**
     * 获取重试的状态
     */
    public static void getRetryStatus(String time, Retry retry) {
        Retry.Metrics metrics = retry.getMetrics();
        long failedRetryNum = metrics.getNumberOfFailedCallsWithRetryAttempt();
        long failedNotRetryNum = metrics.getNumberOfFailedCallsWithoutRetryAttempt();
        long successfulRetryNum = metrics.getNumberOfSuccessfulCallsWithRetryAttempt();
        long successfulNotRetryNum = metrics.getNumberOfSuccessfulCallsWithoutRetryAttempt();

        log.info("[{}] state=[] metrics[failedRetryNum=[{}], failedNotRetryNum=[{}]," +
                " successfulRetryNum=[{}], successfulNotRetryNum=[{}]]",
            time, failedRetryNum, failedNotRetryNum, successfulRetryNum, successfulNotRetryNum);
    }

    /**
     * 监听重试事件
     */
    public static void addRetryListener(Retry retry) {
        retry.getEventPublisher()
            .onSuccess(event -> log.info("服务调用成功：[{}]", event))
            .onError(event -> log.info("服务调用失败：[{}]", event))
            .onIgnoredError(event -> log.info("服务调用失败，但异常被忽略：[{}]", event))
            .onRetry(event -> log.info("重试：第 [{}] 次", event.getNumberOfRetryAttempts()))
        ;
    }

}

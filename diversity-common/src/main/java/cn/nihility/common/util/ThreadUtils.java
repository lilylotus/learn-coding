package cn.nihility.common.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author nihility
 * @date 2022/02/16 17:38
 */
public final class ThreadUtils {

    private static final Logger log = LoggerFactory.getLogger(ThreadUtils.class);

    private ThreadUtils() {
    }

    public static void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            log.error("Thread [{}] sleep 发生中断异常", Thread.currentThread().getName(), e);
            Thread.currentThread().interrupt();
        }
    }

}

package cn.nihility.common.util;

import org.apache.http.conn.HttpClientConnectionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * A daemon thread used to periodically check connection pools for idle connections.
 */
public final class IdleConnectionReaper {

    private static final Logger log = LoggerFactory.getLogger(IdleConnectionReaper.class);

    private static final int REAP_INTERVAL_MILLISECONDS = 30000;

    private static final int REAP_INTERVAL_DELAY_MILLISECONDS = 5000;

    private static final Map<String, HttpClientConnectionManager> CONNECTION_MANAGER = new HashMap<>(8);

    private static ScheduledExecutorService idleReaperService =
        new ScheduledThreadPoolExecutor(1, new IdleReaperThreadFactory());

    /**
     * true - not running, false - running
     */
    private static AtomicBoolean scheduledTaskStatus = new AtomicBoolean(true);

    private IdleConnectionReaper() {
    }

    public static synchronized void registerConnectionManager(HttpClientConnectionManager cm) {
        if (null != cm) {
            CONNECTION_MANAGER.put(Integer.toString(cm.hashCode()), cm);
            if (scheduledTaskStatus.get()) {
                if (idleReaperService.isShutdown()) {
                    idleReaperService = new ScheduledThreadPoolExecutor(1, new IdleReaperThreadFactory());
                }
                idleReaperService.scheduleWithFixedDelay(() -> {

                    final List<HttpClientConnectionManager> copy = new ArrayList<>(CONNECTION_MANAGER.values());
                    for (HttpClientConnectionManager ccm : copy) {
                        try {
                            ccm.closeExpiredConnections();
                            ccm.closeIdleConnections(HttpClientUtils.DEFAULT_IDLE_CONNECTION_TIME, TimeUnit.MILLISECONDS);
                        } catch (Exception ex) {
                            log.error("Unable to close idle connections", ex);
                        }
                    }

                }, REAP_INTERVAL_DELAY_MILLISECONDS, REAP_INTERVAL_MILLISECONDS, TimeUnit.MILLISECONDS);
                scheduledTaskStatus.compareAndSet(true, false);
            }
        }
    }

    public static synchronized void removeConnectionManager(HttpClientConnectionManager cm) {
        if (null != cm) {
            CONNECTION_MANAGER.remove(Integer.toString(cm.hashCode()));
        }
        if (CONNECTION_MANAGER.isEmpty()) {
            shutdown();
        }
    }

    public static synchronized void shutdown() {
        CONNECTION_MANAGER.clear();
        idleReaperService.shutdown();
        scheduledTaskStatus.compareAndSet(false, true);
        log.info("IdleConnectionReaper Shutdown");
    }

    public static synchronized int size() {
        return CONNECTION_MANAGER.size();
    }

    static class IdleReaperThreadFactory implements ThreadFactory {

        private static final AtomicInteger IDX = new AtomicInteger(1);
        private final AtomicInteger threadNumber = new AtomicInteger(1);
        private final String namePrefix;

        public IdleReaperThreadFactory() {
            namePrefix = "IdleReaper-" + IDX.getAndIncrement() + "-";
        }

        @Override
        public Thread newThread(Runnable r) {
            Thread t = new Thread(r, namePrefix + threadNumber.getAndIncrement());
            t.setDaemon(true);
            if (t.getPriority() != Thread.NORM_PRIORITY) {
                t.setPriority(Thread.NORM_PRIORITY);
            }
            return t;
        }
    }

}

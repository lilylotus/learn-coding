package cn.nihility.common.util;

import org.apache.http.conn.HttpClientConnectionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * A daemon thread used to periodically check connection pools for idle connections.
 */
public final class IdleConnectionReaper extends Thread {

    private static final Logger log = LoggerFactory.getLogger(IdleConnectionReaper.class);

    private static final int REAP_INTERVAL_MILLISECONDS = 5 * 1000;
    private static final ArrayList<HttpClientConnectionManager> connectionManagers = new ArrayList<>();

    private static IdleConnectionReaper instance;

    private static long idleConnectionTime = 60 * 1000L;

    private volatile boolean shuttingDown;

    private IdleConnectionReaper() {
        super("idle_connection_reaper");
        setDaemon(true);
    }

    public static synchronized void registerConnectionManager(HttpClientConnectionManager connectionManager) {
        if (instance == null) {
            instance = new IdleConnectionReaper();
            instance.start();
        }
        if (null != connectionManager) {
            connectionManagers.add(connectionManager);
        }
    }

    public static synchronized void removeConnectionManager(HttpClientConnectionManager connectionManager) {
        if (null != connectionManager) {
            connectionManagers.remove(connectionManager);
        }
        if (connectionManagers.isEmpty()) {
            shutdown();
        }
    }

    private void markShuttingDown() {
        shuttingDown = true;
    }

    @Override
    public void run() {
        while (true) {
            if (shuttingDown) {
                log.debug("Shutting down reaper thread.");
                break;
            }

            try {
                Thread.sleep(REAP_INTERVAL_MILLISECONDS);
            } catch (InterruptedException ex) {
                log.warn("中断异常", ex);
                Thread.currentThread().interrupt();
            }

            synchronized (IdleConnectionReaper.class) {
                final List<HttpClientConnectionManager> copy = new ArrayList<>(connectionManagers);
                for (HttpClientConnectionManager cm : copy) {
                    try {
                        cm.closeExpiredConnections();
                        cm.closeIdleConnections(idleConnectionTime, TimeUnit.MILLISECONDS);
                    } catch (Exception ex) {
                        log.warn("Unable to close idle connections", ex);
                    }
                }
            }
        }
    }

    public static synchronized void shutdown() {
        if (instance != null) {
            instance.markShuttingDown();
            instance.interrupt();
            connectionManagers.clear();
            instance = null;
        }
    }

    public static synchronized int size() {
        return connectionManagers.size();
    }

    static synchronized void setIdleConnectionTime(long idleTime) {
        idleConnectionTime = idleTime;
    }

}

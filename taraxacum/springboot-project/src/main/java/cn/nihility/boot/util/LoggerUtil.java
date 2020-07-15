package cn.nihility.boot.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * @author dandelion
 * @date 2020:06:27 00:08
 */
public class LoggerUtil {

    private final static Map<String, Logger> logCache = new HashMap<>(64);

    public static void log(Class<?> clazz, LogLevel level, String msg, Object ... params) {
        if (clazz == null) { return; }

        Logger log;
        String clazzName = clazz.getName();
        if (logCache.containsKey(clazzName)) {
            log = logCache.get(clazzName);
        } else {
            log = LoggerFactory.getLogger(clazz);
            logCache.put(clazzName, log);
        }

        switch (level) {
            case TRACE:
                log.trace(msg, params); break;
            case DEBUG:
                log.debug(msg, params); break;
            case INFO:
                log.info(msg, params); break;
            case ERROR:
                log.error(msg, params); break;
            case WARN:
                log.warn(msg, params); break;
            default:
                log.debug(msg, params);
        }
    }

}

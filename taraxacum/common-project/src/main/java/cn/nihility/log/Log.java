package cn.nihility.log;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author dandelion
 * @date 2020-06-25 22:32
 */
public class Log {

    // logback
    //private final static Logger log = LoggerFactory.getLogger(Log.class);
    // log4j2
    // private final static Logger log = LogManager.getLogger(Log.class);
    // log4j2 to slf4j
    private final static Logger log = LoggerFactory.getLogger(Log.class);


    public static void main(String[] args) {
        String msg = "Logger test, msg [{}]";
        Object[] params = new Object[] {"logger param"};

        int loop = 10000;
        for (int i = 0; i < loop; i++) {
            log(LoggerLevel.TRACE, msg, params);
            log(LoggerLevel.DEBUG, msg, params);
            log(LoggerLevel.INFO, msg, params);
            log(LoggerLevel.ERROR, msg, params);
            log(LoggerLevel.WARN, msg, params);
        }
    }


    public static void log(LoggerLevel level, String msg, Object ... params) {
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

    enum LoggerLevel {
        TRACE, DEBUG, INFO, WARN, ERROR
    }

}

package cn.nihility.plugins.log4j2.util;

import cn.nihility.plugins.log4j2.constant.Constant;
import org.slf4j.MDC;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Callable;

public final class ThreadMDCUtil {

    private ThreadMDCUtil() {
    }

    public static String generateTraceId() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 10);
    }

    public static void setTraceIdIfAbsent() {
        if (MDC.get(Constant.TRACE_ID) == null) {
            MDC.put(Constant.TRACE_ID, generateTraceId());
        }
    }

    public static Runnable wrapper(final Runnable runnable, final Map<String, String> context) {
        return () -> {
            if (context == null) {
                MDC.clear();
            } else {
                MDC.setContextMap(context);
            }
            setTraceIdIfAbsent();
            try {
                runnable.run();
            } finally {
                MDC.clear();
            }
        };
    }

    public static <T> Callable<T> wrapper(final Callable<T> callable, final Map<String, String> context) {
        return () -> {
            if (null == context) {
                MDC.clear();
            } else {
                MDC.setContextMap(context);
            }
            setTraceIdIfAbsent();
            try {
                return callable.call();
            } finally {
                MDC.clear();
            }
        };
    }

}

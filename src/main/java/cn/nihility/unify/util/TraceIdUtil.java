package cn.nihility.unify.util;

import java.util.UUID;

/**
 * Trace ID 生成工具类
 */
public class TraceIdUtil {

    public static String generateTraceId() {
        return UUID.randomUUID().toString().replace("-", "").toUpperCase();
    }

}

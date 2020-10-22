package cn.nihility.unify.util;

/**
 * Trace ID 生成工具类
 */
public class TraceIdUtil {

    public static String generateTraceId() {
        //return UUID.randomUUID().toString().replace("-", "").toUpperCase();
        return SnowflakeIdWorker.nextLongStringId();
    }

}

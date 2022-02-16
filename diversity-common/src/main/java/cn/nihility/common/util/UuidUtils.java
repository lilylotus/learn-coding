package cn.nihility.common.util;

import java.util.UUID;

public class UuidUtils {

    private static SnowflakeIdWorker snowflakeIdWorker;

    private UuidUtils() {
    }

    private static SnowflakeIdWorker snowflakeIdWorker() {
        SnowflakeIdWorker idWorker = snowflakeIdWorker;
        if (null == idWorker) {
            synchronized (UuidUtils.class) {
                idWorker = snowflakeIdWorker;
                if (null == idWorker) {
                    snowflakeIdWorker = SnowflakeIdWorker.createSnowflakeIdWorker();
                    idWorker = snowflakeIdWorker;
                }
            }
        }
        return idWorker;
    }

    public static String jdkUUID() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    public static String jdkUUID(int len) {
        return (len <= 0 || len >= 32) ? jdkUUID() : jdkUUID().substring(0, len);
    }

    public static String snowflakeId() {
        return Long.toString(snowflakeIdWorker().nextId());
    }

}

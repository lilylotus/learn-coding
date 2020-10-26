package cn.nihility.unify.util;

import cn.nihility.unify.distribute.DistributedLock;

import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

/**
 * 分布式锁 redisson 工具类
 */
public class RedissonUtil {

    private static DistributedLock distributedLock;

    public static void setDistributedLock(DistributedLock distributedLock) {
        RedissonUtil.distributedLock = distributedLock;
    }

    public static <T> T lock(String key, int waitTime, int leaseTime, Supplier<T> success, Supplier<T> fail) {
        return distributedLock.lock(key, waitTime, leaseTime, success, fail);
    }

    public static <T> T lock(String key, int leaseTime, Supplier<T> success, Supplier<T> fail) {
        return distributedLock.lock(key, leaseTime, success, fail);
    }

    public static <T> T lock(String key, int leaseTime, TimeUnit timeUnit, Supplier<T> success, Supplier<T> fail) {
        return distributedLock.lock(key, leaseTime, timeUnit, success, fail);
    }

    public static void lock(String key, int waitTime, int leaseTime, Runnable success, Runnable fail) {
        distributedLock.lock(key, waitTime, leaseTime, success, fail);
    }

    public static void lock(String key, int leaseTime, Runnable success, Runnable fail) {
        distributedLock.lock(key, leaseTime, success, fail);
    }

    public static void lock(String key, int leaseTime, TimeUnit timeUnit, Runnable success, Runnable fail) {
        distributedLock.lock(key, leaseTime, timeUnit, success, fail);
    }

    public static void lock(String key) {
        distributedLock.lock(key);
    }

    public static void releaseLock(String key) {
        distributedLock.releaseLock(key);
    }
}

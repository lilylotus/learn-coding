package cn.nihility.unify.distribute;

import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

/**
 * 分布式锁接口
 * redisson 实现，统一线程加锁仅能由该线程解锁
 */
public interface DistributedLock {

    /**
     * 加锁
     * @param key 锁 Key
     * @param waitTime 尝试加锁，等待时间 (ms)
     * @param leaseTime 上锁后的失效时间 (ms)
     * @param success 锁成功执行的逻辑
     * @param fail 锁失败执行的逻辑
     * @return
     */
    <T> T lock(String key, int waitTime, int leaseTime, Supplier<T> success, Supplier<T> fail);

    /**
     * 加锁, 加锁失败立即返回
     * @param key 锁 Key
     * @param leaseTime 上锁后的失效时间 (ms)
     * @param success 锁成功执行的逻辑
     * @param fail 锁失败执行的逻辑
     * @return
     */
    <T> T lock(String key, int leaseTime, Supplier<T> success, Supplier<T> fail);

    /**
     * 加锁, 加锁失败立即返回
     * @param key 锁 Key
     * @param leaseTime 上锁后的失效时间
     * @param timeUnit 时间单位
     * @param success 锁成功执行的逻辑
     * @param fail 锁失败执行的逻辑
     * @return
     */
    <T> T lock(String key, int leaseTime, TimeUnit timeUnit, Supplier<T> success, Supplier<T> fail);

    /**
     * 加锁
     * @param key 锁 Key
     * @param waitTime 尝试加锁，等待时间 (ms)
     * @param leaseTime 上锁后的失效时间 (ms)
     * @param success 锁成功执行的逻辑
     * @param fail 锁失败执行的逻辑
     * @return
     */
    void lock(String key, int waitTime, int leaseTime, Runnable success, Runnable fail);

    /**
     * 加锁, 加锁失败立即返回
     * @param key 锁 Key
     * @param leaseTime 上锁后的失效时间 (ms)
     * @param success 锁成功执行的逻辑
     * @param fail 锁失败执行的逻辑
     * @return
     */
    void lock(String key, int leaseTime, Runnable success, Runnable fail);

    /**
     * 加锁, 加锁失败立即返回
     * @param key 锁 Key
     * @param leaseTime 上锁后的失效时间
     * @param timeUnit 时间单位
     * @param success 锁成功执行的逻辑
     * @param fail 锁失败执行的逻辑
     * @return
     */
    void lock(String key, int leaseTime, TimeUnit timeUnit, Runnable success, Runnable fail);

    /**
     * 加锁，失败后立即返回，简单实现
     * @param key 要加锁的 key
     */
    void lock(String key);

    /**
     * 释放锁，失败后立即返回，简单实现
     * @param key 要释放锁的 key
     */
    void releaseLock(String key);

}


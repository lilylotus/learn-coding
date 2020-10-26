package cn.nihility.unify.distribute;

import java.util.concurrent.TimeUnit;

/**
 * 简单的分布式锁接口
 */
public interface SimpleDistributedLock {

    void lock(String lockKey);

    void unlock(String lockKey);

    void lock(String lockKey, int timeout);

    void lock(String lockKey, int timeout, TimeUnit timeUnit);

}

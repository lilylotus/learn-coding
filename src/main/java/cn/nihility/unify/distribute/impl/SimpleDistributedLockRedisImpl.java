package cn.nihility.unify.distribute.impl;

import cn.nihility.unify.distribute.SimpleDistributedLock;

import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

/**
 * 分布式锁 redisson 的简单实现
 */
public class SimpleDistributedLockRedisImpl implements SimpleDistributedLock {

    private static final Logger log = LoggerFactory.getLogger(SimpleDistributedLockRedisImpl.class);
    private final RedissonClient redissonClient;

    public SimpleDistributedLockRedisImpl(RedissonClient redissonClient) {
        this.redissonClient = redissonClient;
    }

    @Override
    public void lock(String lockKey) {
        log.info("lock key [{}]", lockKey);
        RLock rLock = redissonClient.getLock(lockKey);
        rLock.lock();
    }

    @Override
    public void unlock(String lockKey) {
        log.info("unlock key [{}]", lockKey);
        RLock rLock = redissonClient.getLock(lockKey);
        rLock.unlock();
    }

    @Override
    public void lock(String lockKey, int timeout) {
        RLock lock = redissonClient.getLock(lockKey);
        lock.lock(timeout, TimeUnit.SECONDS);
    }

    @Override
    public void lock(String lockKey, int timeout, TimeUnit timeUnit) {
        RLock lock = redissonClient.getLock(lockKey);
        lock.lock(timeout, timeUnit);
    }

}

package cn.nihility.unify.distribute.impl;

import cn.nihility.unify.distribute.DistributedLock;
import cn.nihility.unify.exception.UnifyException;
import cn.nihility.unify.pojo.UnifyResultCode;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

/**
 * 分布式锁 Redis 的实现方式
 */
public class DistributedLockRedisImpl implements DistributedLock {

    private static final Logger log = LoggerFactory.getLogger(DistributedLockRedisImpl.class);

    private final RedissonClient redissonClient;
    private final DistributedLockMysqlImpl distributedLockMysql;

    public DistributedLockRedisImpl(RedissonClient redissonClient, DistributedLockMysqlImpl distributedLockMysql) {
        this.redissonClient = redissonClient;
        this.distributedLockMysql = distributedLockMysql;
    }

    @Override
    public <T> T lock(String key, int waitTime, int leaseTime, Supplier<T> success, Supplier<T> fail) {
        return doLock(key, waitTime, leaseTime, TimeUnit.MILLISECONDS, success, fail);
    }

    @Override
    public <T> T lock(String key, int leaseTime, Supplier<T> success, Supplier<T> fail) {
        return doLock(key, 0, leaseTime, TimeUnit.MILLISECONDS, success, fail);
    }

    @Override
    public <T> T lock(String key, int leaseTime, TimeUnit timeUnit, Supplier<T> success, Supplier<T> fail) {
        return doLock(key, 0, leaseTime, timeUnit, success, fail);
    }

    @Override
    public void lock(String key, int waitTime, int leaseTime, Runnable success, Runnable fail) {
        doLock(key, waitTime, leaseTime, TimeUnit.SECONDS, success, fail);
    }

    @Override
    public void lock(String key, int leaseTime, Runnable success, Runnable fail) {
        doLock(key, 0, leaseTime, TimeUnit.MILLISECONDS, success, fail);
    }

    @Override
    public void lock(String key, int leaseTime, TimeUnit timeUnit, Runnable success, Runnable fail) {
        doLock(key, 0, leaseTime, timeUnit, success, fail);
    }

    @Override
    public void lock(String key) {
        if (log.isDebugEnabled()) {
            log.debug("do lock key [{}]", key);
        }
        RLock rLock = redissonClient.getLock(key);
        rLock.lock();
    }

    @Override
    public void releaseLock(String key) {
        if (log.isDebugEnabled()) {
            log.debug("do release lock key [{}]", key);
        }
        RLock rLock = redissonClient.getLock(key);
        rLock.unlock();
    }

    private <T> T doLock(String key, int waitTime, int leaseTime, TimeUnit timeUnit,
                         Supplier<T> success, Supplier<T> fail) {
        try {
            if (log.isDebugEnabled()) {
                log.debug("do distribute lock key [{}]", key);
            }
            RLock rLock;
            try {
                rLock = redissonClient.getLock(key);
            } catch (Exception ex) {
                log.error("Get redis distribute lock error, key [{}]", key, ex);
                /* 降级为 Mysql 数据库锁 */
                if (distributedLockMysql != null) {
                    return distributedLockMysql.lock(key, waitTime, leaseTime, success, fail);
                }
                /* 错误返回 */
                return (null != fail ? fail.get() : null);
            }

            if (null == rLock) {
                throw new IllegalArgumentException("Acquire redis lock cannot be null, key [" + key + "]");
            }

            boolean tryLock = false;
            try {
                tryLock = rLock.tryLock(waitTime, leaseTime, timeUnit);
            } catch (Exception e) {
                log.error("Try write lock error, key [{}]", key, e);
                /* 写入 lock 失败，降级为 mysql */
                if (null != distributedLockMysql) {
                    return distributedLockMysql.lock(key, waitTime, leaseTime, success, fail);
                }
            }

            if (!tryLock) {
                log.error("Try write lock fail, key [{}]", key);
                return (null != fail ? fail.get() : null);
            }

            try {
                return success.get();
            } catch (Exception ex) {
                log.error("Exec success callback function error, key [{}]", key, ex);
                throw ex;
            } finally {
                if (rLock.getHoldCount() != 0) {
                    rLock.unlock();
                }
            }
        } catch (Exception ex) {
            throw new UnifyException("do lock error", ex, UnifyResultCode.INTERNAL_SERVER_ERROR);
        }
    }

    private void doLock(String key, int waitTime, int leaseTime, TimeUnit timeUnit, Runnable success, Runnable fail) {
        try {
            if (log.isDebugEnabled()) {
                log.debug("do distribute lock key [{}]", key);
            }
            RLock rLock;
            try {
                rLock = redissonClient.getLock(key);
            } catch (Exception ex) {
                log.error("Get redis distribute lock error, key [{}]", key, ex);
                /* 降级为 Mysql 数据库锁 */
                if (distributedLockMysql != null) {
                    distributedLockMysql.lock(key, waitTime, leaseTime, success, fail);
                }
                /* 错误返回 */
                return;
            }

            boolean tryLock;
            try {
                tryLock = rLock.tryLock(waitTime, leaseTime, timeUnit);
            } catch (InterruptedException e) {
                log.error("Try write lock error key [{}]", key, e);
                if (distributedLockMysql != null) {
                    distributedLockMysql.lock(key, leaseTime, success, fail);
                }
                return;
            }

            if (!tryLock) {
                log.error("Try write lock fail, key [{}]", key);
                if (null != fail) {
                    fail.run();
                }
                return;
            }

            try {
                success.run();
            } catch (Exception ex) {
                log.error("Exec success callback task error, key [{}]", key, ex);
                throw ex;
            } finally {
                if (rLock.getHoldCount() != 0) {
                    rLock.unlock();
                }
            }
        } catch (Exception ex) {
            throw new UnifyException("do lock error", ex, UnifyResultCode.INTERNAL_SERVER_ERROR);
        }
    }

}

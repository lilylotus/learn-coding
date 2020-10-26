package cn.nihility.unify.distribute.impl;

import cn.nihility.unify.distribute.DistributedLock;

import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

/**
 * 基于 Mysql 实现的分布式锁方案， 暂未实现
 */
public class DistributedLockMysqlImpl implements DistributedLock {

    @Override
    public <T> T lock(String key, int waitTime, int leaseTime, Supplier<T> success, Supplier<T> fail) {
        return null;
    }

    @Override
    public <T> T lock(String key, int leaseTime, Supplier<T> success, Supplier<T> fail) {
        return null;
    }

    @Override
    public <T> T lock(String key, int leaseTime, TimeUnit timeUnit, Supplier<T> success, Supplier<T> fail) {
        return null;
    }

    @Override
    public void lock(String key, int waitTime, int leaseTime, Runnable success, Runnable fail) {

    }

    @Override
    public void lock(String key, int leaseTime, Runnable success, Runnable fail) {

    }

    @Override
    public void lock(String key, int leaseTime, TimeUnit timeUnit, Runnable success, Runnable fail) {

    }

    @Override
    public void lock(String key) {

    }

    @Override
    public void releaseLock(String key) {

    }
}

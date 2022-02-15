package cn.nihility.plugin.redis.service.impl;

import cn.nihility.plugin.redis.service.RedissonOperateService;
import org.redisson.api.RBucket;
import org.redisson.api.RList;
import org.redisson.api.RMap;
import org.redisson.api.RedissonClient;

/**
 * @author nihility
 * @date 2022/02/14 18:06
 */
public class RedissonOperateServiceImpl implements RedissonOperateService {

    private RedissonClient redissonClient;

    public RedissonOperateServiceImpl(RedissonClient redissonClient) {
        this.redissonClient = redissonClient;
    }

    @Override
    public <T> RBucket<T> getBucket(String key) {
        return redissonClient.getBucket(key);
    }

    @Override
    public <V> RList<V> getList(String name) {
        return redissonClient.getList(name);
    }

    @Override
    public <K, V> RMap<K, V> getMap(String name) {
        return redissonClient.getMap(name);
    }

}

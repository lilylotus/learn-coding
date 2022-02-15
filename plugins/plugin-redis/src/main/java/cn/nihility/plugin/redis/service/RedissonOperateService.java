package cn.nihility.plugin.redis.service;

import org.redisson.api.RBucket;
import org.redisson.api.RList;
import org.redisson.api.RMap;

public interface RedissonOperateService {

    <T> RBucket<T> getBucket(String key);

    <V> RList<V> getList(String name);

    <K, V> RMap<K, V> getMap(String name);

}

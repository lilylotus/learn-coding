package cn.nihility.plugin.redis.service;

import java.util.concurrent.TimeUnit;

/**
 * redis 管理操作接口
 */
public interface RedisOperateService {

    boolean set(String key, String val, long duration, TimeUnit timeUnit);

    String get(String key);

    boolean deleteKey(String key);

    boolean hasKey(String key);

}

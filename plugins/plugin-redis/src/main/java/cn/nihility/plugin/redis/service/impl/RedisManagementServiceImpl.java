package cn.nihility.plugin.redis.service.impl;

import cn.nihility.plugin.redis.service.RedisManagementService;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.StringUtils;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * redis 管理操作接口具体实现
 */
public class RedisManagementServiceImpl implements RedisManagementService {

    private RedisTemplate<String, Object> redisTemplate;
    private static final String NULL_STRING = null;

    public RedisManagementServiceImpl(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public boolean set(String key, String value, long duration, TimeUnit timeUnit) {
        redisTemplate.boundValueOps(key).set(value, duration, timeUnit);
        return true;
    }

    @Override
    public String get(String key) {
        return Objects.toString(redisTemplate.boundValueOps(key).get(), NULL_STRING);
    }

    @Override
    public boolean deleteKey(String key) {
        return StringUtils.hasText(key) && Boolean.TRUE.equals(redisTemplate.delete(key));
    }

    @Override
    public boolean hasKey(String key) {
        return StringUtils.hasText(key) && Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }

}

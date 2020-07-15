package cn.nihility.learn.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.concurrent.TimeUnit;

/**
 * RedisUtil
 *
 * @author dandelion
 * @date 2020-05-07 15:26
 */
@Component
public class RedisUtil {

    @Autowired
    private RedisTemplate redisTemplate;


    /**
     * 写入 redis token 缓存
     * @param key key
     * @param value value
     * @return
     */
    public boolean setCache(final String key, Object value) {
        boolean result = false;

        try {
            ValueOperations<String, Object> operations = redisTemplate.opsForValue();
            operations.set(key, value);
            result = true;
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return result;
    }

    /**
     * 写入缓存加上过期时间
     * @param key redis 的缓存 key
     * @param value 缓存 token
     * @param expireTime 过期时间
     * @return
     */
    public boolean setCacheEx(final String key, Object value, Long expireTime) {
        boolean result = false;
        try {
            ValueOperations operations = redisTemplate.opsForValue();
            operations.set(key, value);
            redisTemplate.expire(key, expireTime, TimeUnit.SECONDS);
            result = true;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return result;
    }

    /**
     * 判断 key 是否存在
     * @param key 缓存的 token key
     * @return
     */
    public boolean exists(final String key) {
        return redisTemplate.hasKey(key);
    }


    /**
     * 获取缓存
     * @param key 获取缓存的 token key
     * @return
     */
    public Object getCache(final String key) {
        ValueOperations<Serializable, Object> valueOperations = redisTemplate.opsForValue();
        return valueOperations.get(key);
    }


    public boolean remove(final String key) {
        return exists(key) ? redisTemplate.delete(key) : false;
    }

}

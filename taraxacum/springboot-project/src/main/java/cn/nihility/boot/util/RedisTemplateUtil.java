package cn.nihility.boot.util;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * @author dandelion
 * @date 2020:06:27 19:51
 */
@Component
public class RedisTemplateUtil {

    private StringRedisTemplate redisTemplate;

    public RedisTemplateUtil(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    /**
     * 写入 redis token 缓存
     * @param key key
     * @param value value
     */
    public boolean setCache(final String key, String value) {
        boolean result = false;
        try {
            redisTemplate.opsForValue().set(key, value);
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
    public boolean setCacheEx(final String key, String value, Long expireTime) {
        boolean result = false;
        try {
            redisTemplate.opsForValue().set(key, value, expireTime, TimeUnit.SECONDS);
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
        return redisTemplate.hasKey(key) == Boolean.TRUE;
    }


    /**
     * 获取缓存
     * @param key 获取缓存的 token key
     * @return
     */
    public Object getCache(final String key) {
        return redisTemplate.opsForValue().get(key);
    }


    public boolean remove(final String key) {
        return exists(key) && redisTemplate.delete(key) == Boolean.TRUE;
    }
}

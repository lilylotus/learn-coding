package cn.nihility.unify.cache;

import cn.nihility.unify.vo.CacheData;
import org.apache.commons.lang3.RandomUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 讨论缓存问题：
 * 1. 缓存击穿
 * 2. 缓存穿透
 * 3. 缓存雪崩
 */
@Service
public class CacheServiceImpl {

    private static final Logger log = LoggerFactory.getLogger(CacheServiceImpl.class);
    private static final String CACHE_DATA_KEY = "cache:data:";
    private static final String CACHE_DATA_INIT_KEY = "cache:init:";
    private static final String CACHE_LOCK_KEY = "cache:lock:";

    private static final ReentrantLock LOCK = new ReentrantLock();

    /* 模拟数据库 */
    private static final HashMap<Long, CacheData> DATA_MAP = new HashMap<>(64);

    private final StringRedisTemplate stringRedisTemplate;
    private final RedisTemplate<Object, Object> redisTemplate;

    public CacheServiceImpl(StringRedisTemplate stringRedisTemplate, RedisTemplate<Object, Object> redisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
        this.redisTemplate = redisTemplate;
    }

    @PostConstruct
    public void init() {
        for (long i = 0; i < 100; i++) {
            DATA_MAP.put(i, new CacheData("desc:" + i, i));
        }
    }

    /**
     * 缓存穿透、缓存击穿、缓存雪崩问题
     *
     * 缓存穿透: 查找不存在的 key，跳过缓存
     * 缓存击穿：大量请求因为某个 key 缓存过期直接压倒数据库
     *
     * 缓存雪崩：大量的 key 同一时间过期，导致大量请求压到数据库层
     *  -> 是缓存击穿问题的严重化
     */
    public CacheData queryDataById(Long id) {
        // 1. 先从缓存中读取数据
        CacheData data = queryDataFromCache(id);
        if (null == data) {
            // 2. 缓存中数据不存在，读取 DB
            data = queryDataFromDb(id);
            if (null != data) {
                // 3. 在把查到的数据缓存起来
                cacheData(id, data);
            }
        }
        return data;
    }

    /**
     * 优化后完善的缓存优化
     */
    public CacheData queryDataByIdOptimized(Long id) {
        // 1. 先从缓存中读取数据
        CacheData data = queryDataFromCache(id);
        if (null == data) {
            // 2. 先拿到该查询 KEY 的锁，后面可以用分布式锁框架 redisson，这里简单处理
            // 缓存不存在，从数据库查询数据的过程加上锁，避免缓存击穿导致数据库压力过大
            Boolean lock = stringRedisTemplate.boundValueOps(CACHE_LOCK_KEY + id).setIfAbsent("value:" + id, 10, TimeUnit.SECONDS);
            try {
                if (Boolean.TRUE.equals(lock)) {
                    log.info("Get Query DB Lock, id [{}]", id);
                    // 在次读取缓存数据，双重防范
                    data = queryDataFromCache(id);
                    if (null == data) {
                        // 两次读取缓存中数据不存在，读取 DB
                        data = queryDataFromDb(id);
                        if (null != data) {
                            // 在把查到的数据缓存起来
                            cacheData(id, data);
                        }
                    }
                }
            } finally {
                stringRedisTemplate.delete(CACHE_LOCK_KEY + id);
            }
        }
        return data;
    }

    /**
     * 把数据放到缓存
     */
    private void cacheData(Long id, CacheData data) {
        log.info("Put data to [cache] id [{}], data [{}]", id, data);
        if (data != null) {
            redisTemplate.boundValueOps(CACHE_DATA_KEY + id).set(data, 1800 + RandomUtils.nextInt(10, 30), TimeUnit.SECONDS);
        } else {
            // 数据不存在，缓存一个空值
            redisTemplate.boundValueOps(CACHE_DATA_KEY + id).set(new CacheData(null, id), 1800 + RandomUtils.nextInt(10, 30), TimeUnit.SECONDS);
        }
    }

    /**
     * 从数据库中获取数据
     */
    private CacheData queryDataFromDb(Long id) {
        try {
            // 睡眠 100~800 毫秒，模拟数据库 IO 慢操作
            TimeUnit.MILLISECONDS.sleep(RandomUtils.nextLong(100, 800));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        CacheData data = DATA_MAP.get(id);
        log.info("Query data from [database] key [{}], data [{}]", id, data);
        return data;
    }

    /**
     * 从 redis 缓存中查找数据
     */
    private CacheData queryDataFromCache(Long id) {
         CacheData data = (CacheData) redisTemplate.boundValueOps(CACHE_DATA_KEY + id).get();
         log.info("Query data from [cache] key [{}], data [{}]", id, data);
         return data;
    }


}

package cn.nihility.boot.redis;

import cn.nihility.boot.redis.impl.KeyValueCacheServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.BoundValueOperations;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author dandelion
 * @date 2020:06:26 19:38
 */
@SpringBootTest
public class RedisTemplateTest {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private KeyValueCacheServiceImpl keyValueCacheService;

    @Autowired
    private HashCacheService hashCacheService;
    @Autowired
    private ListCacheService listCacheService;
    @Autowired
    private SetCacheService setCacheService;

    @Test
    public void testRedisTemplate() {
        assertNotNull(stringRedisTemplate);

        assertTrue(Optional.ofNullable(stringRedisTemplate.hasKey("redis")).orElse(false));

        BoundValueOperations<String, String> boundValueOps = stringRedisTemplate.boundValueOps("redis");

        String target = "redis key value";
        boundValueOps.set(target);

        assertEquals(target, boundValueOps.get());
    }

    @Test
    public void testKeyValueServiceImpl() {
        assertNotNull(keyValueCacheService);

        String key = "redis4";
        assertFalse(keyValueCacheService.hasKey(key));

        BoundValueOperations<String, String> boundValueOps = stringRedisTemplate.boundValueOps(key);

        String target = "redis key value 看是否有引号";
        boundValueOps.set(target);

        assertEquals(target, boundValueOps.get());
    }

    @Test
    public void testHash() {
        assertNotNull(hashCacheService);

        Map<String, Object> value = new HashMap<>();
        value.put("age", "20");
        value.put("name", "小米");

        String key = "h3";

        hashCacheService.hset(key, "item01", "item01 value");
        hashCacheService.putAll(key, value);

        // stringRedisTemplate.boundHashOps("h1").putAll(value);


        Map<Object, Object> map = hashCacheService.hmget(key);
        if (map != null) {
            map.forEach((k, v) -> System.out.println(k + ":" + v));
        }
    }


    @Test
    public void testList() {
        assertNotNull(listCacheService);

        List<Object> list = new ArrayList<>();
        list.add("werwerwe");
        list.add("信吗");

        listCacheService.lpushAll("list", list);
        listCacheService.lpush("list", "other add value");
    }

    @Test
    public void testSet() {
        assertNotNull(setCacheService);
        List<String> list = new ArrayList<>();
        list.add("信吗");
        list.add("信吗");
        list.add("信吗2");

        setCacheService.add("set", "set value", "v2", "v3");
        setCacheService.add("set", list.toArray(new Object[0]));

    }
}

package cn.nihility.demo.redis;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

@SpringBootTest
class RedisTest {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Test
    void testGetValue() {
        String key = "template:key";
        String value = "value";
        redisTemplate.boundValueOps(key).set(value);
        Object v = redisTemplate.boundValueOps(key).get();

        Assertions.assertEquals("value", v);

        redisTemplate.delete(key);
    }

}

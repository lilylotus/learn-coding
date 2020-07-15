package cn.nihility.boot.redis;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.BoundValueOperations;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.Optional;

/**
 * @author dandelion
 * @date 2020:06:26 19:38
 */
@SpringBootTest
public class RedisTemplateTest {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Test
    public void testRedisTemplate() {
        assertNotNull(stringRedisTemplate);

        assertTrue(Optional.ofNullable(stringRedisTemplate.hasKey("redis")).orElse(false));

        String target = "new redis values new";
        BoundValueOperations<String, String> redis = stringRedisTemplate.boundValueOps("redis");
        assertEquals(target, redis.get());

        String redisVal = stringRedisTemplate.opsForValue().get("redis");
        assertEquals(target, redisVal);
    }

}

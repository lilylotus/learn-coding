package cn.nihility.unify.redis;

import cn.nihility.unify.vo.TestEntity;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Date;

@SpringBootTest
public class JedisClusterTest {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private RedisTemplate<Object, Object> redisTemplate;

    @Test
    public void testRedisSetKey() {
        String key = "key1";
        stringRedisTemplate.boundValueOps(key).set("value1");

        String value = stringRedisTemplate.boundValueOps(key).get();

        Assertions.assertEquals("value1", value);
    }

    @Test
    public void  testRedisTemplate() {
        TestEntity entity = new TestEntity();

        entity.setLocalDateTime(LocalDateTime.now());
        entity.setLocalDate(LocalDate.now());
        entity.setLocalTime(LocalTime.now());
        entity.setInstant(Instant.now());
        entity.setDate(new Date());

        redisTemplate.boundHashOps("entity").put("entity", entity);

        TestEntity o = (TestEntity) redisTemplate.boundHashOps("entity").get("entity");

        System.out.println(o);
    }

    @Test
    public void  testRedisTemplateOpValue() {
        TestEntity entity = new TestEntity();

        entity.setLocalDateTime(LocalDateTime.now());
        entity.setLocalDate(LocalDate.now());
        entity.setLocalTime(LocalTime.now());
        entity.setInstant(Instant.now());
        entity.setDate(new Date());

        redisTemplate.boundValueOps("ev").set(entity);

        TestEntity o = (TestEntity) redisTemplate.boundValueOps("ev").get();

        System.out.println(o);
    }

}

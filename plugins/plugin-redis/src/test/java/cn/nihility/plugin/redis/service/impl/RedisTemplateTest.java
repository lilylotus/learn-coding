package cn.nihility.plugin.redis.service.impl;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStringCommands;
import org.springframework.data.redis.core.BoundValueOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.types.Expiration;

import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class RedisTemplateTest {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Test
    void testRedisTemplateSet() {
        Assertions.assertNotNull(redisTemplate);
        redisTemplate.boundValueOps("redis:serialize:string").set("Redis Serialize Value");
        List<String> list = new ArrayList<>();
        list.add("one");
        list.add("two");
        redisTemplate.boundValueOps("redis:serialize:list").set(list, 60, TimeUnit.SECONDS);
    }

    @Test
    void testRedisTemplateSerialize() {
        Assertions.assertNotNull(redisTemplate);
        List<String> list = new ArrayList<>();
        list.add("one");
        list.add("two");

        BoundValueOperations<String, Object> ops = redisTemplate.boundValueOps("redis:serialize:string");
        BoundValueOperations<String, Object> listOps = redisTemplate.boundValueOps("redis:serialize:list");

        ops.set("Redis Serialize Value");
        listOps.set(list);

        Object stringValue = ops.get();
        Object listValue = listOps.get();

        Assertions.assertNotNull(stringValue);
        Assertions.assertNotNull(listValue);

        Assertions.assertEquals(String.class.getName(), stringValue.getClass().getName());
        Assertions.assertEquals(ArrayList.class.getName(), listValue.getClass().getName());
    }

    @Test
    void testNX() {
        assertNotNull(stringRedisTemplate);
        RedisConnectionFactory factory = stringRedisTemplate.getConnectionFactory();
        assertNotNull(factory);
        try (RedisConnection conn = factory.getConnection()) {
            Boolean ok = conn.set("buy:nx_lock_key".getBytes(StandardCharsets.UTF_8),
                "nx_lock_value".getBytes(StandardCharsets.UTF_8),
                Expiration.seconds(100L), RedisStringCommands.SetOption.SET_IF_ABSENT);
            assertEquals(Boolean.TRUE, ok);
        }
    }

    @Test
    void testGetValue() {
        assertNotNull(redisTemplate);
        assertNotNull(stringRedisTemplate);

        String key = "USER_TOKEN:Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJraWFtVXNlck1haW5JZDIiLCJleHAiOjE2Mzc3NDU1NzAsImlhdCI6MTYzNzczODM3MCwiaXNzIjoiS09BTCIsIkFETUlOX05BTUUiOiIlRTclQjMlQkIlRTclQkIlOUYlRTclQUUlQTElRTclOTAlODYlRTUlOTElOTgiLCJBRE1JTl9ST0xFIjoiIiwiSUQiOiJraWFtVXNlck1haW5JZDIifQ.BggHPRn7mjdaK_q3RxvwOU-NSLSSKOzIp23yTszst4jPezyd2qyz0BXj550W31_mybGAFAI-APayv8Y8iHbJ_A";

        Object v1 = redisTemplate.boundValueOps(key).get();
        String v2 = stringRedisTemplate.boundValueOps(key).get();

        assertEquals("kiamUserMainId2", v1);
        assertEquals("kiamUserMainId2", v2);

    }

    @Test
    void testSetValue() {
        assertNotNull(redisTemplate);
        assertNotNull(stringRedisTemplate);

        String key = "template:key";
        String val = "你是who?";

        redisTemplate.boundValueOps(key).set(val, 10, TimeUnit.MINUTES);
        Object v = redisTemplate.boundValueOps(key).get();


        assertEquals(val, v);

        List<String> list = new ArrayList<>();
        list.add("one");
        redisTemplate.boundHashOps("template:hash").put("k2", list);
        redisTemplate.boundListOps("template:list").leftPush(list);
        redisTemplate.boundSetOps("template:set").add(list);

    }

    @Test
    void testGetValue2() {
        Object k2 = redisTemplate.boundHashOps("template:hash").get("k2");
        Object l2 = redisTemplate.boundListOps("template:list").leftPop();
        Object s2 = redisTemplate.boundSetOps("template:set").pop();

        Assertions.assertNotNull(k2);

        System.out.println(k2);
        System.out.println(l2);
        System.out.println(s2);

    }

    @Test
    void testRedisEntity() {
        RedisEntity entity = new RedisEntity();
        entity.setLocalDateTime(LocalDateTime.now());
        entity.setLongId(System.currentTimeMillis());
        entity.setStringId(UUID.randomUUID().toString().replace("-", ""));
        entity.setInstant(Instant.now());
        entity.setDate(new Date());
        entity.setIntegerId(20);
        entity.setLocalDate(LocalDate.now());

        redisTemplate.opsForValue().set("redis:entity", entity);
        Object et = redisTemplate.opsForValue().get("redis:entity");

        Assertions.assertEquals(entity, et);

    }

    static class RedisEntity implements Serializable {

        private static final long serialVersionUID = -2078156566219783553L;

        private String stringId;
        private Long longId;
        private LocalDateTime localDateTime;
        private Instant instant;
        private Date date;
        private LocalDate localDate;
        private Integer integerId;

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            RedisEntity that = (RedisEntity) o;
            return Objects.equals(stringId, that.stringId) &&
                Objects.equals(longId, that.longId) &&
                Objects.equals(localDateTime, that.localDateTime) &&
                Objects.equals(instant, that.instant) &&
                Objects.equals(date, that.date) &&
                Objects.equals(localDate, that.localDate) &&
                Objects.equals(integerId, that.integerId);
        }

        @Override
        public int hashCode() {
            return Objects.hash(stringId, longId, localDateTime, instant, date, localDate, integerId);
        }

        public String getStringId() {
            return stringId;
        }

        public void setStringId(String stringId) {
            this.stringId = stringId;
        }

        public Long getLongId() {
            return longId;
        }

        public void setLongId(Long longId) {
            this.longId = longId;
        }

        public LocalDateTime getLocalDateTime() {
            return localDateTime;
        }

        public void setLocalDateTime(LocalDateTime localDateTime) {
            this.localDateTime = localDateTime;
        }

        public Instant getInstant() {
            return instant;
        }

        public void setInstant(Instant instant) {
            this.instant = instant;
        }

        public Date getDate() {
            return date;
        }

        public void setDate(Date date) {
            this.date = date;
        }

        public LocalDate getLocalDate() {
            return localDate;
        }

        public void setLocalDate(LocalDate localDate) {
            this.localDate = localDate;
        }

        public Integer getIntegerId() {
            return integerId;
        }

        public void setIntegerId(Integer integerId) {
            this.integerId = integerId;
        }
    }

}

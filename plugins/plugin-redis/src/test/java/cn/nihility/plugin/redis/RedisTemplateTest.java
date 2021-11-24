package cn.nihility.plugin.redis;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class RedisTemplateTest {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

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

}

package cn.nihility.plugin.redis.service.impl;

import cn.nihility.plugin.redis.service.RedissonOperateService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.redisson.api.RBucket;
import org.redisson.api.RList;
import org.redisson.api.RMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class RedissonOperateServiceImplTest {

    @Autowired
    private RedissonOperateService redissonOperateService;

    @Test
    void testBucket() {
        String value = "redission:key:value";
        String key = "redisson:key";
        Assertions.assertNotNull(redissonOperateService);

        RBucket<String> bucket = redissonOperateService.getBucket(key);
        bucket.trySet(value, 60, TimeUnit.SECONDS);
        String result = bucket.get();
        Assertions.assertEquals(value, result);

    }

    @Test
    void testList() {
        String firstValue = "one";

        RList<String> rList = redissonOperateService.getList("redisson:list");
        rList.add(firstValue);
        rList.add("two");
        List<String> resultList = rList.readAll();
        Assertions.assertNotNull(resultList);
        Assertions.assertEquals(firstValue, resultList.get(0));

    }

    @Test
    void testMap() {

        RMap<String, String> rMap = redissonOperateService.getMap("redisson:map");
        rMap.put("one", "oneValue");
        rMap.put("second", "secondValue");

        String result = rMap.get("one");
        Assertions.assertEquals("oneValue", result);

    }

}

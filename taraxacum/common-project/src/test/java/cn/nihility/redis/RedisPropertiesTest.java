package cn.nihility.redis;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLOutput;
import java.util.Properties;

/**
 * RedisPropertiesTest
 *
 * @author dandelion
 * @date 2020-04-03 21:45
 */
public class RedisPropertiesTest {

    @Test
    public void testGetRedisProperties() throws IOException {

        String resource = "redis/redis.properties";
        String resourcePath = Thread.currentThread().getContextClassLoader().getResource(resource).getFile();
        System.out.println(resourcePath);

        Properties properties = new Properties();
        properties.load(new FileInputStream(resourcePath));

        final String host = properties.getProperty("host");
        final String port = properties.getProperty("port");
        final String auth = properties.getProperty("auth");

        Assertions.assertEquals("tencent.nihility.cn", host);
        Assertions.assertEquals("50001", port);
        Assertions.assertEquals("redis", auth);

        properties.clear();
    }

}

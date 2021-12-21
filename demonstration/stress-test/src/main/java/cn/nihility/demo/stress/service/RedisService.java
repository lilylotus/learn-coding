package cn.nihility.demo.stress.service;

import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStringCommands;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.types.Expiration;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.Collections;

@Service
public class RedisService {

    private StringRedisTemplate stringRedisTemplate;

    public RedisService(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }

    public boolean setNX(String key, String value, int expireSecond) {
        RedisConnectionFactory factory = stringRedisTemplate.getConnectionFactory();

        if (null != factory) {
            try (RedisConnection conn = factory.getConnection()) {
                Boolean result = conn.set(key.getBytes(StandardCharsets.UTF_8),
                    value.getBytes(StandardCharsets.UTF_8),
                    Expiration.seconds(expireSecond),
                    RedisStringCommands.SetOption.SET_IF_ABSENT);
                return Boolean.TRUE.equals(result);
            }
        }

        return false;
    }

    private String delNXLUAScript() {
        return "if redis.call(\"get\", KEYS[1]) == ARGV[1] " +
            "then " +
            "    return redis.call(\"del\",KEYS[1]) " +
            "else " +
            "    return 0 " +
            "end ";
    }

    public boolean delNX(String key, String value) {

        DefaultRedisScript<Long> script = new DefaultRedisScript<>(delNXLUAScript(), Long.class);
        Long result = stringRedisTemplate.execute(script, Collections.singletonList(key), value);

        return result != null && result > 0;

    }

}

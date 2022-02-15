package cn.nihility.plugin.redis.config;


import cn.nihility.plugin.redis.service.RedisOperateService;
import cn.nihility.plugin.redis.service.RedissonOperateService;
import cn.nihility.plugin.redis.service.impl.RedisOperateServiceImpl;
import cn.nihility.plugin.redis.service.impl.RedissonOperateServiceImpl;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;

/**
 * @author nihility
 */
public class RedisConfiguration {

    private static final String REDIS_PROTOCOL_PREFIX = "redis://";

    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(connectionFactory);

        final ObjectMapper om = new ObjectMapper();
        // 指定要序列化的域，field,get 和 set,以及修饰符范围，ANY 是都有包括 private 和 public
        om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        // 指定序列化输入的类型，类必须是非 final 修饰的，final 修饰的类，比如 String,Integer 等会抛出异常
        // 这一句非常的重要，作用是序列化时将对象全类名一起保存下来
        om.activateDefaultTyping(LaissezFaireSubTypeValidator.instance, ObjectMapper.DefaultTyping.NON_FINAL, JsonTypeInfo.As.PROPERTY);

        Jackson2JsonRedisSerializer<Object> jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer<>(Object.class);
        jackson2JsonRedisSerializer.setObjectMapper(om);

        // 仅对值进行序列化处理
        redisTemplate.setKeySerializer(StringRedisSerializer.UTF_8);
        redisTemplate.setValueSerializer(jackson2JsonRedisSerializer);
        redisTemplate.setHashKeySerializer(StringRedisSerializer.UTF_8);
        redisTemplate.setHashValueSerializer(jackson2JsonRedisSerializer);

        redisTemplate.afterPropertiesSet();

        return redisTemplate;
    }

    @Bean
    public RedisOperateService redisOperateService(RedisTemplate<String, Object> redisTemplate) {
        return new RedisOperateServiceImpl(redisTemplate);
    }

    @Bean(destroyMethod = "shutdown")
    @ConditionalOnMissingBean(RedissonClient.class)
    public RedissonClient redissonClient(RedisProperties redisProperties) {
        Config config = new Config();

        Duration timeoutDuration = redisProperties.getTimeout();
        int timeout = 10000;
        if (timeoutDuration != null) {
            timeout = (int) timeoutDuration.toMillis();
        }

        config.useSingleServer()
            .setAddress(REDIS_PROTOCOL_PREFIX + redisProperties.getHost() + ":" + redisProperties.getPort())
            .setConnectTimeout(timeout)
            .setDatabase(redisProperties.getDatabase())
            .setPassword(redisProperties.getPassword());

        return Redisson.create(config);
    }

    @Bean
    public RedissonOperateService redissonOperateService(RedissonClient redissonClient) {
        return new RedissonOperateServiceImpl(redissonClient);
    }

}

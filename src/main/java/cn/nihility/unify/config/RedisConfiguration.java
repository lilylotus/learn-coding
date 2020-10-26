package cn.nihility.unify.config;

import cn.nihility.unify.distribute.DistributedLock;
import cn.nihility.unify.distribute.SimpleDistributedLock;
import cn.nihility.unify.distribute.impl.DistributedLockRedisImpl;
import cn.nihility.unify.distribute.impl.SimpleDistributedLockRedisImpl;
import cn.nihility.unify.util.RedissonUtil;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfiguration {

    private static final Logger log = LoggerFactory.getLogger(RedisConfiguration.class);

    /**
     * 设置数据存入 redis 的序列化方式
     * redisTemplate 序列化默认使用的 jdk Serializable
     * 存储二进制字节码，导致 key 会出现乱码，所以自定义序列化类
     */
    @Bean
    @ConditionalOnMissingBean(name = "redisTemplate")
    /* LettuceConnectionFactory */
    public RedisTemplate<Object, Object> redisTemplate(RedisConnectionFactory lettuceConnectionFactory) {
        RedisTemplate<Object, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(lettuceConnectionFactory);

        ObjectMapper objectMapper = JacksonConfiguration.parseJdk8DateTimeFormatObjectMapper();
        objectMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        objectMapper.activateDefaultTyping(objectMapper.getPolymorphicTypeValidator(), ObjectMapper.DefaultTyping.NON_FINAL);

        Jackson2JsonRedisSerializer<Object> jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer<>(Object.class);
        jackson2JsonRedisSerializer.setObjectMapper(objectMapper);

        redisTemplate.setKeySerializer(StringRedisSerializer.UTF_8);
        redisTemplate.setValueSerializer(jackson2JsonRedisSerializer);
        redisTemplate.setHashKeySerializer(StringRedisSerializer.UTF_8);
        redisTemplate.setHashValueSerializer(jackson2JsonRedisSerializer);
        redisTemplate.afterPropertiesSet();

        log.info("Initialize Redis RedisTemplate<Object, Object>");

        return redisTemplate;
    }

    @Bean
    @ConditionalOnMissingBean(StringRedisTemplate.class)
    /* LettuceConnectionFactory */
    public StringRedisTemplate stringredisTemplate(RedisConnectionFactory lettuceConnectionFactory) {
        StringRedisTemplate stringredisTemplate = new StringRedisTemplate();
        stringredisTemplate.setConnectionFactory(lettuceConnectionFactory);
        log.info("Initialize Redis StringRedisTemplate");
        return stringredisTemplate;
    }

    /**
     * 单机模式自动装配
     * org.redisson.spring.starter.RedissonAutoConfiguration#redisson() 有实现
     */
    /*@Bean(destroyMethod = "shutdown")
    public RedissonClient redissonClientSingle() {
        Config config = new Config();
        SingleServerConfig serverConfig = config.useSingleServer()
                .setAddress(redissonProperties.getAddress())
                .setTimeout(redissonProperties.getTimeout())
                .setDatabase(redissonProperties.getDatabase())

                .setConnectionPoolSize(redissonProperties.getConnectionPoolSize())
                .setConnectionMinimumIdleSize(redissonProperties.getConnectionMinimumIdleSize());
        if(StringUtils.isNotBlank(redissonProperties.getPassword())) {
            serverConfig.setPassword(redissonProperties.getPassword());
        }
        return Redisson.create(config);
    }*/

    @Bean
    public SimpleDistributedLock distributedLocker(RedissonClient redissonClient) {
        log.info("Init SimpleDistributedLock Redis Impl");
        return new SimpleDistributedLockRedisImpl(redissonClient);
    }

    @Bean
    public DistributedLock distributedLock(RedissonClient redissonClient) {
        DistributedLockRedisImpl lockRedis = new DistributedLockRedisImpl(redissonClient, null);
        RedissonUtil.setDistributedLock(lockRedis);
        log.info("Init DistributedLock Redis Impl And Add RedissonUtil Locker");
        return lockRedis;
    }

}

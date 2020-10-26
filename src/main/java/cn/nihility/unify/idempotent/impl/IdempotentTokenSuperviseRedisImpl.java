package cn.nihility.unify.idempotent.impl;

import cn.nihility.unify.exception.IdempotentException;
import cn.nihility.unify.idempotent.IdempotentTokenSupervise;
import cn.nihility.unify.pojo.UnifyResultCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * 幂等性业务 key 操作 redis 实现
 */
@Service
public class IdempotentTokenSuperviseRedisImpl implements IdempotentTokenSupervise {
    private static final Logger log = LoggerFactory.getLogger(IdempotentTokenSuperviseRedisImpl.class);

    private final StringRedisTemplate redisTemplate;

    public IdempotentTokenSuperviseRedisImpl(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public boolean cacheToken(String key, String token) {
        verifyParams(key, "Cache Idempotent Token Key Can't Be Empty");
        verifyParams(token, "Cache Idempotent Token Value Can't Be Empty");
        if (log.isDebugEnabled()) {
            log.debug("Cache idempotent token key [{}] value [{}]", key, token);
        }
        redisTemplate.boundValueOps(key).set(token, 1, TimeUnit.MINUTES);
        return true;
    }

    @Override
    public boolean deleteToken(String key) {
        verifyParams(key, "Delete idempotent token key can't be empty");
        return Boolean.TRUE.equals(redisTemplate.delete(key));
    }

    @Override
    public boolean exists(String key) {
        verifyParams(key, "Check idempotent token key exists can't be empty");
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }

    private void verifyParams(String param, String errorTipMessage) {
        if (null == param || "".equals(param.trim())) {
            throw new IdempotentException(errorTipMessage, UnifyResultCode.PARAM_IS_BLANK);
        }
    }

}

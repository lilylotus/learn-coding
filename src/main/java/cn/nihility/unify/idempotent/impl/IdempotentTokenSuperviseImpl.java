package cn.nihility.unify.idempotent.impl;

import cn.nihility.unify.exception.IdempotentException;
import cn.nihility.unify.idempotent.IdempotentTokenSupervise;
import cn.nihility.unify.pojo.UnifyResultCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;

/**
 * 幂等性 token 数据管理实现类
 */
@Component
public class IdempotentTokenSuperviseImpl implements IdempotentTokenSupervise {
    private static final Logger log = LoggerFactory.getLogger(IdempotentTokenSuperviseImpl.class);

    /* 简单的缓存实现 */
    private static final ConcurrentHashMap<String, String> IDEMPOTENT_CACHE = new ConcurrentHashMap<>(16);

    @Override
    public boolean cacheToken(String key, String token) {
        if (null == key || "".equals(key.trim())) {
            throw new IdempotentException("Cache Idempotent Token Key Can't Be Empty",
                    UnifyResultCode.INTERNAL_SERVER_ERROR);
        }
        if (null == token || "".equals(token.trim())) {
            throw new IdempotentException("Cache Idempotent Token Value Can't Be Empty",
                    UnifyResultCode.INTERNAL_SERVER_ERROR);
        }
        if (log.isDebugEnabled()) {
            log.debug("Cache idempotent token key [{}] value [{}]", key, token);
        }
        IDEMPOTENT_CACHE.put(key, token);
        return true;
    }

    @Override
    public boolean deleteToken(String key) {
        if (null == key || "".equals(key.trim())) {
            throw new IdempotentException("Delete idempotent token key can't be empty",
                    UnifyResultCode.INTERNAL_SERVER_ERROR);
        }
        IDEMPOTENT_CACHE.remove(key);
        return false;
    }

    @Override
    public boolean exists(String key) {
        if (null == key || "".equals(key.trim())) {
            throw new IdempotentException("Check idempotent token key exists can't be empty",
                    UnifyResultCode.INTERNAL_SERVER_ERROR);
        }
        return IDEMPOTENT_CACHE.contains(key);
    }

}
